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
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.server.LightEngineThreadedHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Lighting handler for Minecraft 1.14 and later. A new asynchronous lighting engine was introduced,
 * which means changes to light requires scheduling tasks on a worker thread.
 */
public class LightingHandler_1_14 extends LightingHandler {
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
            throw new IllegalStateException("LightEngineStorage light_storage_live field is not of type LightEngineStorageArray");
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
            return readLayerData(engine, layer, cx, cy, cz);
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
            return readLayerData(engine, layer, cx, cy, cz);
        } catch (Throwable ex) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to read sky light of [" + cx + "/" + cy + "/" + cz + "]", ex);
            return null;
        }
    }

    private byte[] readLayerData(LightEngineThreadedHandle engine, Object layer, int cx, int cy, int cz) throws Throwable {
        if (layer == null) {
            return null;
        } else {
            return this.handle.getData(layer, cx, cy, cz);
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
            this.block = new LayerUpdateTaskList(layer_block);
            this.sky = new LayerUpdateTaskList(layer_sky);
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
            block.run();
            sky.run();

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
        public final Object layer;
        public final List<UpdateTask> tasks;

        public LayerUpdateTaskList(Object layer) {
            this.layer = layer;
            this.tasks  = new ArrayList<UpdateTask>();
        }

        public void run() {
            try {
                if (this.layer == null) {
                    throw new IllegalStateException("Light layer field could not be retrieved");
                }

                // Apply all the changes we have queued up in one go
                Object storage = light_storage.get(this.layer);
                Object storage_array_live = light_storage_array_live.get(storage);
                for (UpdateTask task : this.tasks) {
                    try {
                        handle.setData(storage_array_live, task.cx, task.cy, task.cz, task.data);
                    } catch (Throwable t) {
                        task.error = t;
                    }
                }

                // There's a small cache of the last two 16x16x16 slices queried
                // We clear this cache to make sure data is updated
                handle.clearCache(storage_array_live);

                // This refreshes the thread-safe 'visible' layer
                // All data updated earlier is copied and the immutable copy is
                // assigned to a separate 'visible' representation.
                handle.assignUpdatingToVisible(storage);
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
         * <GET_LAYER_DATA>
         * public static byte[] getLayerData(net.minecraft.server.LightEngineLayer layer, int cx, int cy, int cz) {
         *    NibbleArray array = layer.a(SectionPosition.a(cx, cy, cz));
         *    if (array == null) {
         *        return null;
         *    }
         *    return array.asBytes();
         * }
         */
        @Template.Generated("%GET_LAYER_DATA%")
        public abstract byte[] getData(Object lightEngineLayer, int cx, int cy, int cz);

        /*
         * <SET_LIVE_DATA>
         * public static void setLiveData(net.minecraft.server.LightEngineStorageArray lightEngineStorageArray, int cx, int cy, int cz, byte[] data_bytes) {
         *     long key = SectionPosition.b(cx, cy, cz);
         * 
         * #if exists net.minecraft.server.LightEngineStorageArray protected final QueuedChangesMapLong2Object<NibbleArray> data;
         *     // Retrieve previously stored data for releasing, if needed
         *     #require net.minecraft.server.LightEngineStorageArray protected final QueuedChangesMapLong2Object<NibbleArray> data;
         *     QueuedChangesMapLong2Object data = lightEngineStorageArray#data;
         *     final NibbleArray updating = (NibbleArray) data.getUpdating(key);
         *     if (updating != null) {
         *         System.arraycopy((Object) data_bytes, 0, (Object) updating.asBytesPoolSafe(), 0, 2048);
         *         lightEngineStorageArray.a(key, updating);
         *     } else {
         *         lightEngineStorageArray.a(key, new NibbleArray(data_bytes).markPoolSafe());
         *     }
         * #else
         *     lightEngineStorageArray.a(key, new NibbleArray(data_bytes));
         * #endif
         * }
         */
        @Template.Generated("%SET_LIVE_DATA%")
        public abstract void setData(Object lightEngineStorageArray, int cx, int cy, int cz, byte[] data);

        /*
         * <CLEAR_CACHE>
         * public static void setLiveData(net.minecraft.server.LightEngineStorageArray lightEngineStorageArray) {
         *     lightEngineStorageArray.c();
         * }
         */
        @Template.Generated("%CLEAR_CACHE%")
        public abstract void clearCache(Object lightEngineStorageArray);

        /*
         * <ASSIGN_UPDATING_TO_VISIBLE>
         * public static void assignUpdatingToVisible(net.minecraft.server.LightEngineStorage lightEngineStorage) {
         * #if exists net.minecraft.server.LightEngineStorage protected final Object visibleUpdateLock;
         *     #require net.minecraft.server.LightEngineStorage protected final Object visibleUpdateLock;
         *     Object lock = lightEngineStorage#visibleUpdateLock;
         *     synchronized (lock)
         * #endif
         *     {
         *         #require net.minecraft.server.LightEngineStorage protected final net.minecraft.server.LightEngineStorageArray liveArray:f;
         * #if exists net.minecraft.server.LightEngineStorage protected volatile LightEngineStorageArray e_visible;
         *         #require net.minecraft.server.LightEngineStorage protected volatile LightEngineStorageArray visible:e_visible;
         * #else
         *         #require net.minecraft.server.LightEngineStorage protected volatile LightEngineStorageArray visible:e;
         * #endif
         * 
         *         net.minecraft.server.LightEngineStorageArray m0 = lightEngineStorage#liveArray;
         *         m0 = m0.b();
         *         m0.d();
         *         lightEngineStorage#visible = m0;
         *     }
         * }
         */
        @Template.Generated("%ASSIGN_UPDATING_TO_VISIBLE%")
        public abstract void assignUpdatingToVisible(Object lightEngineStorage);
    }
}
