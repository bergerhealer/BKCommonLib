package com.bergerkiller.bukkit.common.internal.logic;

import java.util.concurrent.CompletableFuture;

import org.bukkit.World;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.SourceDeclaration;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;

/**
 * Lighting handler for Minecraft 1.8 to 1.13.2. This is before the introduction
 * of a new asynchronous light engine, which means everything happens on the main thread.
 */
public class LightingHandler_1_8_to_1_13_2 extends LightingHandler {
    private final FastMethod<byte[]> getSectionBlockLightMethod = new FastMethod<byte[]>();
    private final FastMethod<byte[]> getSectionSkyLightMethod = new FastMethod<byte[]>();
    private final FastMethod<Void> setSectionBlockLightMethod = new FastMethod<Void>();
    private final FastMethod<Void> setSectionSkyLightMethod = new FastMethod<Void>();

    public LightingHandler_1_8_to_1_13_2() {
        ClassResolver resolver = new ClassResolver();
        resolver.setDeclaredClassName("net.minecraft.server.Chunk");
        resolver.setAllVariables(Common.TEMPLATE_RESOLVER);

        getSectionBlockLightMethod.init(new MethodDeclaration(resolver, SourceDeclaration.preprocess(
                "public byte[] getSectionBlockLight(int sectionIndex) {\n" +
                "    ChunkSection[] sections = instance.getSections();\n" +
                "    if (sectionIndex >= 0 && sectionIndex < sections.length) {\n" +
                "        ChunkSection section = sections[sectionIndex];\n" +
                "        if (section != null) {\n" +
                "            NibbleArray array = section.getEmittedLightArray();\n" +
                "            if (array != null) {\n" +
                "#if version >= 1.9\n" +
                "                return array.asBytes();\n" +
                "#else\n" +
                "                return array.a();\n" +
                "#endif\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "    return null;\n" +
                "}", resolver)));
        getSectionBlockLightMethod.forceInitialization();

        getSectionSkyLightMethod.init(new MethodDeclaration(resolver, SourceDeclaration.preprocess(
                "public byte[] getSectionSkyLight(int sectionIndex) {\n" +
                "    ChunkSection[] sections = instance.getSections();\n" +
                "    if (sectionIndex >= 0 && sectionIndex < sections.length) {\n" +
                "        ChunkSection section = sections[sectionIndex];\n" +
                "        if (section != null) {\n" +
                "            NibbleArray array = section.getSkyLightArray();\n" +
                "            if (array != null) {\n" +
                "#if version >= 1.9\n" +
                "                return array.asBytes();\n" +
                "#else\n" +
                "                return array.a();\n" +
                "#endif\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "    return null;\n" +
                "}", resolver)));
        getSectionSkyLightMethod.forceInitialization();

        setSectionBlockLightMethod.init(new MethodDeclaration(resolver,
                "public void setSectionBlockLight(int sectionIndex, byte[] data) {\n" +
                "    ChunkSection[] sections = instance.getSections();\n" +
                "    if (sectionIndex >= 0 && sectionIndex < sections.length) {\n" +
                "        ChunkSection section = sections[sectionIndex];\n" +
                "        if (section != null) {\n" +
                "            section.a(new NibbleArray(data));\n" +
                "        }\n" +
                "    }\n" +
                "}"));
        setSectionBlockLightMethod.forceInitialization();

        setSectionSkyLightMethod.init(new MethodDeclaration(resolver,
                "public void setSectionSkyLight(int sectionIndex, byte[] data) {\n" +
                "    ChunkSection[] sections = instance.getSections();\n" +
                "    if (sectionIndex >= 0 && sectionIndex < sections.length) {\n" +
                "        ChunkSection section = sections[sectionIndex];\n" +
                "        if (section != null) {\n" +
                "            section.b(new NibbleArray(data));\n" +
                "        }\n" +
                "    }\n" +
                "}"));
        setSectionSkyLightMethod.forceInitialization();
    }

    @Override
    public byte[] getSectionBlockLight(World world, int cx, int cy, int cz) {
        Object nms_chunk = HandleConversion.toChunkHandle(world.getChunkAt(cx, cz));
        return getSectionBlockLightMethod.invoke(nms_chunk, cy);
    }

    @Override
    public byte[] getSectionSkyLight(World world, int cx, int cy, int cz) {
        Object nms_chunk = HandleConversion.toChunkHandle(world.getChunkAt(cx, cz));
        return getSectionSkyLightMethod.invoke(nms_chunk, cy);
    }

    @Override
    public CompletableFuture<Void> setSectionBlockLightAsync(World world, int cx, int cy, int cz, byte[] data) {
        return CommonUtil.runAsyncMainThread(() -> {
            Object nms_chunk = HandleConversion.toChunkHandle(world.getChunkAt(cx, cz));
            setSectionBlockLightMethod.invoke(nms_chunk, cy, data);
        });
    }

    @Override
    public CompletableFuture<Void> setSectionSkyLightAsync(World world, int cx, int cy, int cz, byte[] data) {
        return CommonUtil.runAsyncMainThread(() -> {
            Object nms_chunk = HandleConversion.toChunkHandle(world.getChunkAt(cx, cz));
            setSectionSkyLightMethod.invoke(nms_chunk, cy, data);
        });
    }
}
