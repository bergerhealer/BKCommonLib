package com.bergerkiller.bukkit.common.internal.logic;

import java.lang.reflect.Field;
import java.util.concurrent.CompletableFuture;
import java.util.function.IntSupplier;

import org.bukkit.World;

import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.server.LightEngineThreadedHandle;
import com.bergerkiller.generated.net.minecraft.server.PlayerChunkMapHandle;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;

/**
 * Lighting handler for Minecraft 1.14 and later. A new asynchronous lighting engine was introduced,
 * which means changes to light requires scheduling tasks on a worker thread.
 */
public class LightingHandler_1_14 extends LightingHandler {
    private final IntSupplier updateTicket;
    private final Object lightUpdateStage;
    private final Field light_layer_block;
    private final Field light_layer_sky;
    private final Field light_storage;
    private final Field light_storage_live;
    private final Field light_storage_volatile;
    private final Field light_storage_paper_lock;
    private final FastMethod<Object> setStorageDataAndCopyMethod = new FastMethod<Object>();

    public LightingHandler_1_14() throws Throwable {
        { // Update ticket intsupplier
            final int golden_ticket = PlayerChunkMapHandle.T.getType().getDeclaredField("GOLDEN_TICKET").getInt(null);
            this.updateTicket = () -> golden_ticket;
        }

        { // PRE_UPDATE constant to pass to schedule()
            Class<?> updateEnumType = CommonUtil.getNMSClass("LightEngineThreaded.Update");
            Field preUpdateConstant = updateEnumType.getDeclaredField("PRE_UPDATE");
            preUpdateConstant.setAccessible(true);
            this.lightUpdateStage = preUpdateConstant.get(null);
            preUpdateConstant.setAccessible(false);
        }

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

        // This generated method updates the contents in a live layer, creates a copy of the data and returns it
        ClassResolver resolver = new ClassResolver();
        resolver.setDeclaredClass(lightEngineStorageArrayType);
        this.setStorageDataAndCopyMethod.init(new MethodDeclaration(resolver,
                "public LightEngineStorageArray setDataAndCopy(int cx, int cy, int cz, byte[] data) {\n" +
                "    instance.a(SectionPosition.b(cx, cy, cz), new NibbleArray(data));\n" +
                "    LightEngineStorageArray copy = instance.b();\n" +
                "    copy.d();\n" +
                "    return copy;\n" +
                "}"));
        this.setStorageDataAndCopyMethod.forceInitialization();
    }

    @Override
    public CompletableFuture<Void> setSectionBlockLightAsync(World world, int cx, int cy, int cz, byte[] data) {
        LightEngineThreadedHandle engine = LightEngineThreadedHandle.forWorld(world);
        try {
            Object layer = this.light_layer_block.get(engine.getRaw());
            return scheduleSetLayerStorageData(engine, layer, cx, cy, cz, data);
        } catch (Throwable ex) {
            return completedExceptionally(ex);
        }
    }

    @Override
    public CompletableFuture<Void> setSectionSkyLightAsync(World world, int cx, int cy, int cz, byte[] data) {
        LightEngineThreadedHandle engine = LightEngineThreadedHandle.forWorld(world);
        try {
            Object layer = this.light_layer_sky.get(engine.getRaw());
            return scheduleSetLayerStorageData(engine, layer, cx, cy, cz, data);
        } catch (Throwable ex) {
            return completedExceptionally(ex);
        }
    }

    private CompletableFuture<Void> scheduleSetLayerStorageData(LightEngineThreadedHandle engine, Object layer, int cx, int cy, int cz, byte[] data) {
        final CompletableFuture<Void> future = new CompletableFuture<Void>();
        engine.schedule(cx, cz, this.updateTicket, this.lightUpdateStage, () -> {
            try {
                Object storage = this.light_storage.get(layer);
                Object storage_live = this.light_storage_live.get(storage);
                Object layer_data = setStorageDataAndCopyMethod.invoke(storage_live, cx, cy, cz, data);
                if (light_storage_paper_lock != null) {
                    synchronized (light_storage_paper_lock.get(storage)) {
                        this.light_storage_volatile.set(storage, layer_data);
                    }
                } else {
                    this.light_storage_volatile.set(storage, layer_data);
                }

                //TODO: Can scheduling it onto the main thread be done differently?
                CommonUtil.nextTick(() -> {
                    future.complete(null);
                });
            } catch (Throwable t) {
                //TODO: Can scheduling it onto the main thread be done differently?
                CommonUtil.nextTick(() -> {
                    future.completeExceptionally(t);
                });
            }
        });
        return future;
    }

    private static CompletableFuture<Void> completedExceptionally(Throwable ex) {
        CompletableFuture<Void> future = new CompletableFuture<Void>();
        future.completeExceptionally(ex);
        return future;
    }
}
