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

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.server.LightEngineThreadedHandle;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;

/**
 * Lighting handler for Minecraft 1.14 and later. A new asynchronous lighting engine was introduced,
 * which means changes to light requires scheduling tasks on a worker thread.
 */
public class LightingHandler_1_14 extends LightingHandler {
    private final Field light_layer_block;
    private final Field light_layer_sky;
    private final Field light_storage;
    private final Field light_storage_live;
    private final Field light_storage_volatile;
    private final Field light_storage_paper_lock;
    private final FastMethod<Void> setStorageDataMethod = new FastMethod<Void>();
    private final FastMethod<Object> createCopyMethod = new FastMethod<Object>();
    private final FastMethod<byte[]> getLayerDataMethod = new FastMethod<byte[]>();
    private final Map<World, EngineUpdateTaskLists> task_lists = new HashMap<>();

    public LightingHandler_1_14() throws Throwable {
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

        this.light_storage_live = lightEngineStorageType.getDeclaredField("f");
        if (!lightEngineStorageArrayType.isAssignableFrom(this.light_storage_live.getType())) {
            throw new IllegalStateException("LightEngineStorage light_storage_live field is not of type LightEngineStorageArray");
        }

        // Renamed (de-obfuscated) by Paper devs
        {
            Field f;
            try {
                f = lightEngineStorageType.getDeclaredField("e_visible");
            } catch (NoSuchFieldException ex) {
                f = lightEngineStorageType.getDeclaredField("e");
            }
            this.light_storage_volatile = f;
        }

        // On paperspigot there is a lock object when the e_visible is updated
        {
            Field f;
            try {
                f = lightEngineStorageType.getDeclaredField("visibleUpdateLock");
            } catch (NoSuchFieldException ex) {
                f = null;
            }
            this.light_storage_paper_lock = f;
        }

        if (!lightEngineStorageArrayType.isAssignableFrom(this.light_storage_volatile.getType())) {
            throw new IllegalStateException("LightEngineStorage light_storage_volatile field is not of type LightEngineStorageArray");
        }

        // Make all accessible
        this.light_layer_block.setAccessible(true);
        this.light_layer_sky.setAccessible(true);
        this.light_storage.setAccessible(true);
        this.light_storage_live.setAccessible(true);
        this.light_storage_volatile.setAccessible(true);
        if (this.light_storage_paper_lock != null) {
            this.light_storage_paper_lock.setAccessible(true);
        }

        ClassResolver storage_resolver = new ClassResolver();
        storage_resolver.setDeclaredClassName("net.minecraft.server.LightEngineStorageArray");

        // This generated method updates the contents in a live layer
        this.setStorageDataMethod.init(new MethodDeclaration(storage_resolver,
                "public void setData(int cx, int cy, int cz, byte[] data) {\n" +
                "    instance.a(SectionPosition.b(cx, cy, cz), new NibbleArray(data));\n" +
                "}"));
        this.setStorageDataMethod.forceInitialization();

        // This generated method creates a copy of the live layer
        this.createCopyMethod.init(new MethodDeclaration(storage_resolver,
                "public LightEngineStorageArray createCopy() {\n" +
                "    LightEngineStorageArray copy = instance.b();\n" +
                "    copy.d();\n" +
                "    return copy;\n" +
                "}"));
        this.createCopyMethod.forceInitialization();

        // This generated method simply reads the nibblearray data and returns a byte[] copy
        ClassResolver layer_resolver = new ClassResolver();
        layer_resolver.setDeclaredClassName("net.minecraft.server.LightEngineLayer");
        this.getLayerDataMethod.init(new MethodDeclaration(layer_resolver,
                "public byte[] getData(int cx, int cy, int cz) {\n" +
                "    NibbleArray array = instance.a(SectionPosition.a(cx, cy, cz));\n" +
                "    if (array == null) {\n" +
                "        return null;\n" +
                "    }\n" +
                "    return array.asBytes();\n" +
                "}"));
        this.getLayerDataMethod.forceInitialization();
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
        } else if (light_storage_paper_lock != null) {
            Object storage = this.light_storage.get(layer);
            synchronized (light_storage_paper_lock.get(storage)) {
                return getLayerDataMethod.invoke(layer, cx, cy, cz);
            }
        } else {
            return getLayerDataMethod.invoke(layer, cx, cy, cz);
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

                // Paperspigot also has a lock we must use while accessing the data
                Object storage = light_storage.get(this.layer);
                if (light_storage_paper_lock != null) {
                    synchronized (light_storage_paper_lock.get(storage)) {
                        run_impl(storage);
                    }
                } else {
                    run_impl(storage);
                }
            } catch (Throwable t) {
                for (UpdateTask task : this.tasks) {
                    task.error = t;
                }
            }
        }

        // Changes the storage data of the live layer for all pending tasks
        // Then creates a copy of the entire live layer, making it available
        private void run_impl(Object storage) throws Throwable {
            Object storage_live = light_storage_live.get(storage);
            for (UpdateTask task : this.tasks) {
                try {
                    setStorageDataMethod.invoke(storage_live, task.cx, task.cy, task.cz, task.data);
                } catch (Throwable t) {
                    task.error = t;
                }
            }
            Object layer_data = createCopyMethod.invoke(storage_live);
            light_storage_volatile.set(storage, layer_data);
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
}
