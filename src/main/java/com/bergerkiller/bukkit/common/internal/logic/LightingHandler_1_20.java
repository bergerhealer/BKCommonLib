package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.lighting.LightingHandler;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.server.level.LightEngineThreadedHandle;
import com.bergerkiller.generated.net.minecraft.server.level.PlayerChunkMapHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import org.bukkit.World;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntSupplier;
import java.util.logging.Level;

/**
 * Lighting handler for Minecraft 1.14 and later. A new asynchronous lighting engine was introduced,
 * which means changes to light requires scheduling tasks on a worker thread.
 */
class LightingHandler_1_20 implements LightingHandler {
    private final LightEngineHandle handle;
    private final Field light_layer_block;
    private final Field light_layer_sky;
    private final Field light_storage;
    private final Field light_storage_array_live;
    private final Object light_engine_pre_update;
    private final Object light_engine_post_update;
    private final Method light_engine_schedule;
    private final Map<World, EngineUpdateTaskLists> task_lists = new HashMap<>();
    private final IntSupplier golden_ticket;

    public LightingHandler_1_20() throws Throwable {
        this.handle = Template.Class.create(LightEngineHandle.class, Common.TEMPLATE_RESOLVER);
        this.handle.forceInitialization();

        Class<?> lightEngineType = CommonUtil.getClass("net.minecraft.world.level.lighting.LightEngine");
        if (lightEngineType == null) {
            throw new IllegalStateException("LightEngine class not found");
        }

        Class<?> levelLightEngineType = CommonUtil.getClass("net.minecraft.world.level.lighting.LevelLightEngine");
        if (levelLightEngineType == null) {
            throw new IllegalStateException("LevelLightEngine class not found");
        }

        Class<?> lightEngineStorageType = CommonUtil.getClass("net.minecraft.world.level.lighting.LightEngineStorage");
        if (lightEngineStorageType == null) {
            throw new IllegalStateException("LightEngineStorage class not found");
        }

        Class<?> lightEngineStorageArrayType = CommonUtil.getClass("net.minecraft.world.level.lighting.LightEngineStorageArray");
        if (lightEngineStorageArrayType == null) {
            throw new IllegalStateException("LightEngineStorageArray class not found");
        }

        String light_layer_block_name, light_layer_sky_name, light_storage_name, light_storage_array_live_name;
        String golden_ticket_name;
        light_layer_block_name = "blockEngine";
        light_layer_sky_name = "skyEngine";
        light_storage_name = "storage";
        light_storage_array_live_name = "updatingSectionData";

        this.light_layer_block = Resolver.resolveAndGetDeclaredField(levelLightEngineType, light_layer_block_name);
        if (!lightEngineType.isAssignableFrom(this.light_layer_block.getType())) {
            throw new IllegalStateException("LightEngine light_layer_block is not of type LightEngine");
        }

        this.light_layer_sky = Resolver.resolveAndGetDeclaredField(levelLightEngineType, light_layer_sky_name);
        if (!lightEngineType.isAssignableFrom(this.light_layer_sky.getType())) {
            throw new IllegalStateException("LightEngine light_layer_sky is not of type LightEngine");
        }

        this.light_storage = MPLType.getDeclaredField(lightEngineType,
                Resolver.resolveFieldName(lightEngineType, light_storage_name));
        if (!lightEngineStorageType.isAssignableFrom(this.light_storage.getType())) {
            throw new IllegalStateException("LightEngine light_storage field is not of type LightEngineStorage");
        }

        this.light_storage_array_live = MPLType.getDeclaredField(lightEngineStorageType,
                Resolver.resolveFieldName(lightEngineStorageType, light_storage_array_live_name));
        if (!lightEngineStorageArrayType.isAssignableFrom(this.light_storage_array_live.getType())) {
            throw new IllegalStateException("LightEngineStorage light_storage_array_live field is not of type LightEngineStorageArray");
        }

        // PlayerChunkMap.GOLDEN_TICKET
        {
            Field f = Resolver.resolveAndGetDeclaredField(Class.forName("net.minecraft.server.level.ChunkLevel"),
                    "MAX_LEVEL");
            final int golden_ticket_value = f.getInt(null);
            this.golden_ticket = () -> golden_ticket_value;
        }

        // Get PRE/POST_UPDATE constants
        Class<?> updateType = CommonUtil.getClass("net.minecraft.server.level.LightEngineThreaded$Update");
        {
            Object preUpdate = null;
            Object postUpdate = null;
            for (Object constant : updateType.getEnumConstants()) {
                String name = ((Enum<?>) constant).name();
                if (name.equals("PRE_UPDATE")) {
                    preUpdate = constant;
                } else if (name.equals("POST_UPDATE")) {
                    postUpdate = constant;
                }
            }
            if (preUpdate == null) {
                throw new IllegalStateException("LightEngineThreaded.Update has no PRE_UPDATE constant");
            }
            if (postUpdate == null) {
                throw new IllegalStateException("LightEngineThreaded.Update has no POST_UPDATE constant");
            }
            light_engine_pre_update = preUpdate;
            light_engine_post_update = postUpdate;
        }

        // Get the private schedule method of the engine
        this.light_engine_schedule = CommonUtil.getClass("net.minecraft.server.level.LightEngineThreaded").getDeclaredMethod("a",
                int.class, int.class, IntSupplier.class, updateType, Runnable.class);

        // Make all accessible
        this.light_layer_block.setAccessible(true);
        this.light_layer_sky.setAccessible(true);
        this.light_storage.setAccessible(true);
        this.light_storage_array_live.setAccessible(true);
        this.light_engine_schedule.setAccessible(true);
    }

    @Override
    public void enable() {
    }

    @Override
    public void disable() {
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
        return scheduleUpdate(world, new UpdateTask(cx, cy, cz, data) {
            @Override
            public void run(LightEngineData data) {
                if (data.block_storage_array == null) {
                    throw new UnsupportedOperationException("World does not store block light data");
                }

                handle.storeBlockLightData(data.lightEngine, this.cx, this.cy, this.cz, this.data);
            }
        });
    }

    @Override
    public CompletableFuture<Void> setSectionSkyLightAsync(World world, int cx, int cy, int cz, byte[] data) {
        return scheduleUpdate(world, new UpdateTask(cx, cy, cz, data) {
            @Override
            public void run(LightEngineData data) {
                if (data.sky_storage_array == null) {
                    throw new UnsupportedOperationException("World does not store sky light data");
                }

                handle.storeSkyLightData(data.lightEngine, this.cx, this.cy, this.cz, this.data);
            }
        });
    }

    // Queues up and schedules the update for a 16x16x16 area of sky/light data
    private CompletableFuture<Void> scheduleUpdate(World world, UpdateTask task)
    {
        synchronized (task_lists) {
            EngineUpdateTaskLists lists = task_lists.computeIfAbsent(world, EngineUpdateTaskLists::new);
            lists.tasks.add(task);
        }
        return task.future;
    }

    // All the updates to perform for a single (world) light engine
    private final class EngineUpdateTaskLists {
        public final World world;
        public final LightEngineThreadedHandle engine;
        public final List<UpdateTask> tasks;
        private final AtomicInteger stage;

        public EngineUpdateTaskLists(World world) {
            this.world = world;
            this.engine = LightEngineThreadedHandle.forWorld(world);
            this.tasks = new ArrayList<UpdateTask>();
            this.stage = new AtomicInteger(0);
            schedule();
        }

        public void preRun() {
            // Remove from task lists so no more tasks are scheduled
            synchronized (task_lists) {
                task_lists.remove(this.world);
            }

            // Process all updates, make sure it runs only once
            if (stage.compareAndSet(0, 1)) {
                LightEngineData data = new LightEngineData(engine.getRaw());
                for (UpdateTask task : this.tasks) {
                    try {
                        task.run(data);
                    } catch (Throwable t) {
                        task.error = t;
                    }
                }
            }
        }

        public void postRun() {
            // Fire callbacks, make sure this happens only once
            if (stage.compareAndSet(1, 2)) {
                CommonUtil.nextTick(() -> {
                    for (UpdateTask task : tasks) {
                        if (task.error != null) {
                            task.future.completeExceptionally(task.error);
                        } else {
                            task.future.complete(null);
                        }
                    }
                });
            } else if (stage.get() == 0) {
                // Well this is bad! We got to run before the preRun() did.
                // We will need to re-schedule this post update again, the pre-update is likely
                // sitting in a queue waiting to be executed.
                try {
                    schedule();
                } catch (IllegalStateException ex) {
                    // If this for some reason fails, fail all the tasks with the exception
                    if (stage.compareAndSet(1, 2)) {
                        CommonUtil.nextTick(() -> {
                            for (UpdateTask task : tasks) {
                                task.future.completeExceptionally(ex);
                            }
                        });
                    }
                }
            }
        }

        private void schedule() throws IllegalStateException {
            try {
                final IntSupplier priority = golden_ticket;
                light_engine_schedule.invoke(engine.getRaw(), 0, 0, priority, light_engine_pre_update, (Runnable) this::preRun);
                light_engine_schedule.invoke(engine.getRaw(), 0, 0, priority, light_engine_post_update, (Runnable) this::postRun);
            } catch (Throwable t) {
                throw new IllegalStateException("Failed to schedule updates", t);
            }
        }
    }

    @SuppressWarnings("unused")
    private final class LightEngineData {
        public final Object lightEngine;

        public final Object block_storage;
        public final Object block_storage_array;

        public final Object sky_storage;
        public final Object sky_storage_array;

        public LightEngineData(Object lightEngine) {
            this.lightEngine = lightEngine;

            // Get block layer
            {
                Object layer = null, storage = null, array = null;
                try {
                    layer = light_layer_block.get(lightEngine);
                    if (layer != null) {
                        storage = light_storage.get(layer);
                        array = light_storage_array_live.get(storage);
                    }
                } catch (Throwable t) {
                    Logging.LOGGER.log(Level.SEVERE, "Failed to access block light layer data", t);
                }

                this.block_storage = storage;
                this.block_storage_array = array;
            }

            // Get sky layer
            {
                Object layer = null, storage = null, array = null;
                try {
                    layer = light_layer_sky.get(lightEngine);
                    if (layer != null) {
                        storage = light_storage.get(layer);
                        array = light_storage_array_live.get(storage);
                    }
                } catch (Throwable t) {
                    Logging.LOGGER.log(Level.SEVERE, "Failed to access sky light layer data", t);
                }

                this.sky_storage = storage;
                this.sky_storage_array = array;
            }
        }
    }

    // A single task to change a single 16x16x16 slice
    private static abstract class UpdateTask {
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

        public abstract void run(LightEngineData data);
    }

    @Template.Optional
    @Template.Import("com.destroystokyo.paper.util.map.QueuedChangesMapLong2Object")
    @Template.Import("net.minecraft.server.MCUtil")
    @Template.Import("net.minecraft.core.SectionPosition")
    @Template.Import("net.minecraft.world.level.chunk.NibbleArray")
    @Template.Import("net.minecraft.world.level.EnumSkyBlock")
    @Template.InstanceType("net.minecraft.world.level.lighting.LightEngine")
    public static abstract class LightEngineHandle extends Template.Class<Template.Handle> {

        /*
         * <GET_LAYER_LIGHT_DATA>
         * public static byte[] getLayerData(LightEngine engine, int cx, int cy, int cz) {
         *    if (engine == null) {
         *        return null;
         *    }
         *    NibbleArray array = engine.getDataLayerData(SectionPosition.of(cx, cy, cz));
         *    if (array == null) {
         *        return null;
         *    }
         *    return array.getData();
         * }
         */
        @Template.Generated("%GET_LAYER_LIGHT_DATA%")
        public abstract byte[] getLightData(Object lightEngine, int cx, int cy, int cz);

        /*
         * <STORE_SKY_LIGHT_DATA>
         * public static void storeSkyLightData(LevelLightEngine engine, int cx, int cy, int cz, byte[] data_bytes) {
         *     final SectionPosition pos = SectionPosition.of(cx, cy, cz);
         *     engine.queueSectionData(EnumSkyBlock.SKY, pos, new NibbleArray(data_bytes));
         * }
         */
        @Template.Generated("%STORE_SKY_LIGHT_DATA%")
        public abstract void storeSkyLightData(Object levelLightEngine, int cx, int cy, int cz, byte[] data);

        /*
         * <STORE_BLOCK_LIGHT_DATA>
         * public static void storeBlockLightData(LevelLightEngine engine, int cx, int cy, int cz, byte[] data_bytes) {
         *     final SectionPosition pos = SectionPosition.of(cx, cy, cz);
         *     engine.queueSectionData(EnumSkyBlock.BLOCK, pos, new NibbleArray(data_bytes));
         * }
         */
        @Template.Generated("%STORE_BLOCK_LIGHT_DATA%")
        public abstract void storeBlockLightData(Object levelLightEngine, int cx, int cy, int cz, byte[] data);
    }
}
