package com.bergerkiller.bukkit.common.internal.logic;

import java.util.concurrent.CompletableFuture;

import org.bukkit.World;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.lighting.LightingHandler;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Lighting handler for Minecraft 1.8 to 1.13.2. This is before the introduction
 * of a new asynchronous light engine, which means everything happens on the main thread.
 */
class LightingHandler_CubicChunks_1_12_2 implements LightingHandler {
    private final LightingLogicHandle handle = Template.Class.create(LightingLogicHandle.class, Common.TEMPLATE_RESOLVER);

    @Override
    public void enable() {
        handle.forceInitialization();
    }

    @Override
    public void disable() {
    }

    @Override
    public boolean isSupported(World world) {
        return handle.isSupported(HandleConversion.toWorldHandle(world));
    }

    @Override
    public byte[] getSectionBlockLight(World world, int cx, int cy, int cz) {
        Object nms_chunk = HandleConversion.toChunkHandle(world.getChunkAt(cx, cz));
        return handle.getSectionBlockLight(nms_chunk, cy);
    }

    @Override
    public byte[] getSectionSkyLight(World world, int cx, int cy, int cz) {
        Object nms_chunk = HandleConversion.toChunkHandle(world.getChunkAt(cx, cz));
        return handle.getSectionSkyLight(nms_chunk, cy);
    }

    @Override
    public CompletableFuture<Void> setSectionBlockLightAsync(World world, int cx, int cy, int cz, byte[] data) {
        return CommonUtil.runAsyncMainThread(() -> {
            Object nms_chunk = HandleConversion.toChunkHandle(world.getChunkAt(cx, cz));
            handle.setSectionBlockLight(nms_chunk, cy, data);
        });
    }

    @Override
    public CompletableFuture<Void> setSectionSkyLightAsync(World world, int cx, int cy, int cz, byte[] data) {
        return CommonUtil.runAsyncMainThread(() -> {
            Object nms_chunk = HandleConversion.toChunkHandle(world.getChunkAt(cx, cz));
            handle.setSectionSkyLight(nms_chunk, cy, data);
        });
    }

    @Template.Optional
    @Template.Import("io.github.opencubicchunks.cubicchunks.api.world.ICubicWorld")
    @Template.Import("io.github.opencubicchunks.cubicchunks.api.world.ICube")
    @Template.Import("io.github.opencubicchunks.cubicchunks.api.world.IColumn")
    @Template.Import("io.github.opencubicchunks.cubicchunks.core.world.cube.Cube")
    @Template.Import("net.minecraft.world.level.chunk.NibbleArray")
    @Template.Import("net.minecraft.world.level.chunk.ChunkSection")
    @Template.InstanceType("net.minecraft.world.level.chunk.Chunk")
    public static abstract class LightingLogicHandle extends Template.Class<Template.Handle> {

        /*
         * <IS_SUPPORTED>
         * public static boolean isSupported(net.minecraft.server.level.WorldServer world) {
         *     return world instanceof ICubicWorld && ((ICubicWorld) world).isCubicWorld();
         * }
         */
        @Template.Generated("%IS_SUPPORTED%")
        public abstract boolean isSupported(Object worldHandle);

        /*
         * <GET_SECTION_BLOCK_LIGHT>
         * public static byte[] getSectionBlockLight(IColumn chunk, int cy) {
         *     ICube cube = chunk.getCube(cy);
         *     ChunkSection section = cube.getStorage();
         *     if (section != null) {
         *         NibbleArray array = section.getEmittedLightArray();
         *         if (array != null) {
         * #if version >= 1.9
         *             return (byte[]) array.asBytes().clone();
         * #else
         *             return (byte[]) array.a().clone();
         * #endif
         *         }
         *     }
         *     return null;
         * }
         */
        @Template.Generated("%GET_SECTION_BLOCK_LIGHT%")
        public abstract byte[] getSectionBlockLight(Object chunkHandle, int cy);

        /*
         * <GET_SECTION_SKY_LIGHT>
         * public static byte[] getSectionSkyLight(IColumn chunk, int cy) {
         *     ICube cube = chunk.getCube(cy);
         *     ChunkSection section = cube.getStorage();
         *     if (section != null) {
         *         NibbleArray array = section.getSkyLightArray();
         *         if (array != null) {
         * #if version >= 1.9
         *             return (byte[]) array.asBytes().clone();
         * #else
         *             return (byte[]) array.a().clone();
         * #endif
         *         }
         *     }
         *     return null;
         * }
         */
        @Template.Generated("%GET_SECTION_SKY_LIGHT%")
        public abstract byte[] getSectionSkyLight(Object chunkHandle, int cy);

        /*
         * <SET_SECTION_BLOCK_LIGHT>
         * public static void setSectionBlockLight(IColumn chunk, int cy, byte[] data) {
         *     ICube cube = chunk.getCube(cy);
         *     ChunkSection section = cube.getStorage();
         *     if (section != null) {
         *         section.a(new NibbleArray(data));
         *         ((Cube) cube).markDirty();
         *     }
         * }
         */
        @Template.Generated("%SET_SECTION_BLOCK_LIGHT%")
        public abstract void setSectionBlockLight(Object chunkHandle, int cy, byte[] data);

        /*
         * <SET_SECTION_SKY_LIGHT>
         * public static void setSectionSkyLight(IColumn chunk, int cy, byte[] data) {
         *     ICube cube = chunk.getCube(cy);
         *     ChunkSection section = cube.getStorage();
         *     if (section != null) {
         *         section.b(new NibbleArray(data));
         *         ((Cube) cube).markDirty();
         *     }
         * }
         */
        @Template.Generated("%SET_SECTION_SKY_LIGHT%")
        public abstract void setSectionSkyLight(Object chunkHandle, int cy, byte[] data);
    }
}
