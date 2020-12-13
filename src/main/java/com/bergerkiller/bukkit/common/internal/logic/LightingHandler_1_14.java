package com.bergerkiller.bukkit.common.internal.logic;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.logging.Level;

import org.bukkit.World;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.lighting.LightingHandler;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.server.LightEngineThreadedHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Lighting handler for Minecraft 1.14 and later. A new asynchronous lighting engine was introduced,
 * which means changes to light requires scheduling tasks on a worker thread.
 */
public class LightingHandler_1_14 implements LightingHandler {
    private final LightEngineHandle handle;
    private final Field light_layer_block;
    private final Field light_layer_sky;
    private final Field light_storage;
    private final Field light_storage_array_live;
    private final Map<World, EngineUpdateTaskLists> task_lists = new HashMap<>();

    public LightingHandler_1_14() throws Throwable {
        this.handle = Template.Class.create(LightEngineHandle.class, Common.TEMPLATE_RESOLVER);
        this.handle.forceInitialization();

        Class<?> lightEngineType = CommonUtil.getNMSClass("LightEngine");
        if (lightEngineType == null) {
            throw new IllegalStateException("LightEngine class not found");
        }

        Class<?> lightEngineLayerType = CommonUtil.getNMSClass("LightEngineLayer");
        if (lightEngineLayerType == null) {
            throw new IllegalStateException("LightEngineLayer class not found");
        }

        Class<?> lightEngineStorageType = CommonUtil.getNMSClass("LightEngineStorage");
        if (lightEngineStorageType == null) {
            throw new IllegalStateException("LightEngineStorage class not found");
        }

        Class<?> lightEngineStorageArrayType = CommonUtil.getNMSClass("LightEngineStorageArray");
        if (lightEngineStorageArrayType == null) {
            throw new IllegalStateException("LightEngineStorageArray class not found");
        }

        this.light_layer_block = lightEngineType.getDeclaredField("a");
        if (!lightEngineLayerType.isAssignableFrom(this.light_layer_block.getType())) {
            throw new IllegalStateException("LightEngine light_layer_block is not of type LightEngineLayer");
        }

        this.light_layer_sky = lightEngineType.getDeclaredField("b");
        if (!lightEngineLayerType.isAssignableFrom(this.light_layer_sky.getType())) {
            throw new IllegalStateException("LightEngine light_layer_sky is not of type LightEngineLayer");
        }

        this.light_storage = lightEngineLayerType.getDeclaredField("c");
        if (!lightEngineStorageType.isAssignableFrom(this.light_storage.getType())) {
            throw new IllegalStateException("LightEngineLayer light_storage field is not of type LightEngineStorage");
        }

        this.light_storage_array_live = lightEngineStorageType.getDeclaredField("f");
        if (!lightEngineStorageArrayType.isAssignableFrom(this.light_storage_array_live.getType())) {
            throw new IllegalStateException("LightEngineStorage light_storage_array_live field is not of type LightEngineStorageArray");
        }

        // Make all accessible
        this.light_layer_block.setAccessible(true);
        this.light_layer_sky.setAccessible(true);
        this.light_storage.setAccessible(true);
        this.light_storage_array_live.setAccessible(true);
    }

    @Override
    public boolean isSupported(World world) {
        return true;
    }

    @Override
    public byte[] getSectionBlockLight(World world, int cx, int cy, int cz) {
        LightEngineThreadedHandle engine = LightEngineThreadedHandle.forWorld(world);
        try {
            Object layer = this.light_layer_block.get(engine.getRaw());
            return this.handle.getLightData(layer, cx, cy, cz);
        } catch (Throwable ex) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to read sky light of [" + cx + "/" + cy + "/" + cz + "]", ex);
            return null;
        }
    }

    @Override
    public byte[] getSectionSkyLight(World world, int cx, int cy, int cz) {
        LightEngineThreadedHandle engine = LightEngineThreadedHandle.forWorld(world);
        try {
            Object layer = this.light_layer_sky.get(engine.getRaw());
            return this.handle.getLightData(layer, cx, cy, cz);
        } catch (Throwable ex) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to read sky light of [" + cx + "/" + cy + "/" + cz + "]", ex);
            return null;
        }
    }

    @Override
    public CompletableFuture<Void> setSectionBlockLightAsync(World world, int cx, int cy, int cz, byte[] data) {
        return scheduleUpdate(world, list -> list.block, cx, cy, cz, data);
    }

    @Override
    public CompletableFuture<Void> setSectionSkyLightAsync(World world, int cx, int cy, int cz, byte[] data) {
        return scheduleUpdate(world, list -> list.sky, cx, cy, cz, data);
    }

    // Queues up and schedules the update for a 16x16x16 area of sky/light data
    private CompletableFuture<Void> scheduleUpdate(World world, Function<EngineUpdateTaskLists, LayerUpdateTaskList> function,
            int cx, int cy, int cz, byte[] data)
    {
        UpdateTask task = new UpdateTask(cx, cy, cz, data);
        synchronized (task_lists) {
            EngineUpdateTaskLists lists = task_lists.computeIfAbsent(world, EngineUpdateTaskLists::new);
            function.apply(lists).tasks.add(task);
        }
        return task.future;
    }

    // All the updates to perform for a single (world) light engine
    private final class EngineUpdateTaskLists implements Runnable {
        public final World world;
        public final LayerUpdateTaskList block;
        public final LayerUpdateTaskList sky;
        private boolean hasRun;

        public EngineUpdateTaskLists(World world) {
            LightEngineThreadedHandle engine = LightEngineThreadedHandle.forWorld(world);
            Object layer_block, layer_sky;
            try {
                layer_block = light_layer_block.get(engine.getRaw());
                layer_sky = light_layer_sky.get(engine.getRaw());
            } catch (Throwable t) {
                layer_block = null;
                layer_sky = null;
            }
            this.world = world;
            this.block = new LayerUpdateTaskList(engine, layer_block);
            this.sky = new LayerUpdateTaskList(engine, layer_sky);
            this.hasRun = false;
            engine.schedule(this);
        }

        @Override
        public void run() {
            // Remove from task lists, if different (should never happen!) run separately
            EngineUpdateTaskLists removed;
            synchronized (task_lists) {
                removed = task_lists.remove(this.world);
            }
            if (removed != null && removed != this) {
                removed.run();
            }

            // Protection
            if (this.hasRun) {
                return;
            } else {
                this.hasRun = true;
            }

            // Process all updates
            block.run(false);
            sky.run(true);

            // Complete all callbacks
            CommonUtil.nextTick(() -> {
                fireCallbacks(block);
                fireCallbacks(sky);
            });
        }

        private void fireCallbacks(LayerUpdateTaskList list) {
            for (UpdateTask task : list.tasks) {
                if (task.error != null) {
                    task.future.completeExceptionally(task.error);
                } else {
                    task.future.complete(null);
                }
            }
        }
    }

    // All the tasks for a single layer (block/sky)
    private final class LayerUpdateTaskList {
        public final LightEngineThreadedHandle engine;
        public final Object layer;
        public final List<UpdateTask> tasks;

        public LayerUpdateTaskList(LightEngineThreadedHandle engine, Object layer) {
            this.engine = engine;
            this.layer = layer;
            this.tasks  = new ArrayList<UpdateTask>();
        }

        public void run(boolean isSkyLight) {
            try {
                if (this.layer == null) {
                    throw new IllegalStateException("Light layer field could not be retrieved");
                }

                // Apply all the changes we have queued up in one go
                Object storage = light_storage.get(this.layer);
                Object storage_array_live = light_storage_array_live.get(storage);

                for (UpdateTask task : this.tasks) {
                    try {
                        handle.setLightDataOrStoreNew(engine.getRaw(), storage, storage_array_live, isSkyLight, task.cx, task.cy, task.cz, task.data);
                    } catch (Throwable t) {
                        task.error = t;
                    }
                }

                // This refreshes the thread-safe 'visible' layer
                // All data updated earlier is copied and the immutable copy is
                // assigned to a separate 'visible' representation.
                handle.syncUpdatingToVisible(storage);
            } catch (Throwable t) {
                for (UpdateTask task : this.tasks) {
                    task.error = t;
                }
            }
        }
    }

    // A single task to change a single 16x16x16 slice
    private static final class UpdateTask {
        public final CompletableFuture<Void> future = new CompletableFuture<Void>();
        public final int cx;
        public final int cy;
        public final int cz;
        public final byte[] data;
        public Throwable error = null;

        public UpdateTask(int cx, int cy, int cz, byte[] data) {
            this.cx = cx;
            this.cy = cy;
            this.cz = cz;
            this.data = data;
        }
    }

    @Template.Optional
    @Template.Import("com.destroystokyo.paper.util.map.QueuedChangesMapLong2Object")
    @Template.Import("net.minecraft.server.MCUtil")
    @Template.InstanceType("net.minecraft.server.LightEngineStorage")
    public static abstract class LightEngineHandle extends Template.Class<Template.Handle> {

        /*
         * <GET_LAYER_LIGHT_DATA>
         * public static byte[] getLayerData(net.minecraft.server.LightEngineLayer layer, int cx, int cy, int cz) {
         *    if (layer == null) {
         *        return null;
         *    }
         *    NibbleArray array = layer.a(SectionPosition.a(cx, cy, cz));
         *    if (array == null) {
         *        return null;
         *    }
         *    return array.asBytes();
         * }
         */
        @Template.Generated("%GET_LAYER_LIGHT_DATA%")
        public abstract byte[] getLightData(Object lightEngineLayer, int cx, int cy, int cz);

        /*
         * <SET_LIGHT_DATA_OR_STORE_NEW>
         * public static void setLightDataOrStoreNew(net.minecraft.server.LightEngine engine, net.minecraft.server.LightEngineStorage lightEngineStorage, net.minecraft.server.LightEngineStorageArray lightEngineStorageArray, boolean skyLight, int cx, int cy, int cz, byte[] data_bytes) {
         *     long key = SectionPosition.b(cx, cy, cz);
         * 
         *     NibbleArray dataNibble = lightEngineStorageArray.c(key);
         *     if (dataNibble == null) {
         *         // Missing, use engine to register and store a new section
         *         final SectionPosition pos = SectionPosition.a(cx, cy, cz);
         *         final EnumSkyBlock enumSky = skyLight ? EnumSkyBlock.SKY : EnumSkyBlock.BLOCK;
         *         engine.a(enumSky, pos, new NibbleArray(data_bytes), true);
         *         return;
         *     }
         * 
         *     // Copy new section light data and mark for updating
         * #if exists net.minecraft.server.NibbleArray public byte[] asBytesPoolSafe();
         *     System.arraycopy(data_bytes, 0, dataNibble.asBytesPoolSafe(), 0, 2048);
         * #else
         *     System.arraycopy(data_bytes, 0, dataNibble.asBytes(), 0, 2048);
         * #endif
         *     lightEngineStorageArray.a(key, dataNibble);
         * 
         *     // Track the nibbles we have pushed in this set
         *     #require net.minecraft.server.LightEngineStorage protected final it.unimi.dsi.fastutil.longs.LongSet syncPendingSet:g;
         *     it.unimi.dsi.fastutil.longs.LongSet syncPending = lightEngineStorage#syncPendingSet;
         *     syncPending.add(key);
         * 
         *     // When storing light data, make sure to refresh the top section where light data is stored
         *     // Otherwise it will send all-15 light levels for sky light mistakingly
         *     // notifyCubePresent(long) implementation is in LightEngineStorageSky
         * #if version >= 1.14.1
         *     #require net.minecraft.server.LightEngineStorage protected void notifyCubePresent:k(long i);
         * #else
         *     #require net.minecraft.server.LightEngineStorage protected void notifyCubePresent:j(long i);
         * #endif
         *     lightEngineStorage#notifyCubePresent(key);
         * }
         */
        @Template.Generated("%SET_LIGHT_DATA_OR_STORE_NEW%")
        public abstract void setLightDataOrStoreNew(Object lightEngine, Object lightEngineStorage, Object lightEngineStorageArray, boolean skyLight, int cx, int cy, int cz, byte[] data);

        /*
         * <SYNC_UPDATING_TO_VISIBLE>
         * public static void syncUpdatingToVisible(net.minecraft.server.LightEngineStorage lightEngineStorage) {
         * #if version >= 1.15
         *     #require net.minecraft.server.LightEngineStorage protected void sync:e();
         * #else
         *     #require net.minecraft.server.LightEngineStorage protected void sync:d();
         * #endif
         *     lightEngineStorage#sync();
         * }
         */
        @Template.Generated("%SYNC_UPDATING_TO_VISIBLE%")
        public abstract void syncUpdatingToVisible(Object lightEngineStorage);
    }
}
