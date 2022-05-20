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
class LightingHandler_1_8_to_1_13_2 implements LightingHandler {
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
        return true;
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
    @Template.Import("net.minecraft.world.level.chunk.ChunkSection")
    @Template.Import("net.minecraft.world.level.chunk.NibbleArray")
    @Template.InstanceType("net.minecraft.world.level.chunk.Chunk")
    /*
     * <PREPARE_CHUNK_SECTION>
     * public ChunkSection prepareChunkSection(int sectionIndex) {
     *     ChunkSection[] sections = instance.getSections();
     *     if (sectionIndex < 0 || sectionIndex >= sections.length) {
     *         return null;
     *     }
     *     ChunkSection section = sections[sectionIndex];
     *     if (section == null) {
     * #select version >=
     * #case 1.13:   boolean hasSkyLight = instance.world.worldProvider.g();
     * #case 1.11:   boolean hasSkyLight = instance.world.worldProvider.m();
     * #case 1.9:    boolean hasSkyLight = !instance.world.worldProvider.m();
     * #case else:   boolean hasSkyLight = !instance.world.worldProvider.o();
     * #endselect
     *         sections[sectionIndex] = section = new ChunkSection(sectionIndex << 4, hasSkyLight);
     *     }
     *     return section;
     * }
     */
    @Template.Require(declaring="net.minecraft.world.level.chunk.Chunk", value="%PREPARE_CHUNK_SECTION%")
    public static abstract class LightingLogicHandle extends Template.Class<Template.Handle> {
        /*
         * <GET_SECTION_BLOCK_LIGHT>
         * public static byte[] getSectionBlockLight(Chunk chunk, int sectionIndex) {
         *     ChunkSection[] sections = chunk.getSections();
         *     if (sectionIndex >= 0 && sectionIndex < sections.length) {
         *         ChunkSection section = sections[sectionIndex];
         *         if (section != null) {
         *             NibbleArray array = section.getEmittedLightArray();
         *             if (array != null) {
         * #if version >= 1.9
         *                 return (byte[]) array.asBytes().clone();
         * #else
         *                 return (byte[]) array.a().clone();
         * #endif
         *             }
         *         }
         *     }
         *     return null;
         * }
         */
        @Template.Generated("%GET_SECTION_BLOCK_LIGHT%")
        public abstract byte[] getSectionBlockLight(Object chunkHandle, int sectionIndex);

        /*
         * <GET_SECTION_SKY_LIGHT>
         * public static byte[] getSectionSkyLight(Chunk chunk, int sectionIndex) {
         *     ChunkSection[] sections = chunk.getSections();
         *     if (sectionIndex >= 0 && sectionIndex < sections.length) {
         *         ChunkSection section = sections[sectionIndex];
         *         if (section != null) {
         *             NibbleArray array = section.getSkyLightArray();
         *             if (array != null) {
         * #if version >= 1.9
         *                 return (byte[]) array.asBytes().clone();
         * #else
         *                 return (byte[]) array.a().clone();
         * #endif
         *             }
         *         }
         *     }
         *     return null;
         * }
         */
        @Template.Generated("%GET_SECTION_SKY_LIGHT%")
        public abstract byte[] getSectionSkyLight(Object chunkHandle, int sectionIndex);

        /*
         * <SET_SECTION_BLOCK_LIGHT>
         * public static void setSectionBlockLight(Chunk chunk, int sectionIndex, byte[] data) {
         *     ChunkSection section = chunk#prepareChunkSection(sectionIndex);
         *     if (section != null) {
         *         section.a(new NibbleArray(data));
         *     }
         * }
         */
        @Template.Generated("%SET_SECTION_BLOCK_LIGHT%")
        public abstract void setSectionBlockLight(Object chunkHandle, int sectionIndex, byte[] data);

        /*
         * <SET_SECTION_SKY_LIGHT>
         * public static void setSectionSkyLight(Chunk chunk, int sectionIndex, byte[] data) {
         *     ChunkSection section = chunk#prepareChunkSection(sectionIndex);
         *     if (section != null) {
         *         section.b(new NibbleArray(data));
         *     }
         * }
         */
        @Template.Generated("%SET_SECTION_SKY_LIGHT%")
        public abstract void setSectionSkyLight(Object chunkHandle, int sectionIndex, byte[] data);
    }
}
