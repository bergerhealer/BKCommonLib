package com.bergerkiller.bukkit.common.internal.logic;

import java.util.concurrent.CompletableFuture;

import org.bukkit.World;

import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;

/**
 * Lighting handler for Minecraft 1.8 to 1.13.2. This is before the introduction
 * of a new asynchronous light engine, which means everything happens on the main thread.
 */
public class LightingHandler_1_8_to_1_13_2 extends LightingHandler {
    private final FastMethod<Void> setSectionBlockLightMethod = new FastMethod<Void>();
    private final FastMethod<Void> setSectionSkyLightMethod = new FastMethod<Void>();

    public LightingHandler_1_8_to_1_13_2() {
        ClassResolver resolver = new ClassResolver();
        resolver.setDeclaredClass(CommonUtil.getNMSClass("World"));

        setSectionBlockLightMethod.init(new MethodDeclaration(resolver,
                "public static void setSectionBlockLight(Chunk chunk, int sectionIndex, byte[] data) {\n" +
                "    ChunkSection[] sections = chunk.getSections();\n" +
                "    if (sectionIndex >= 0 && sectionIndex < sections.length) {\n" +
                "        ChunkSection section = sections[sectionIndex];\n" +
                "        if (section != null) {\n" +
                "            section.a(new NibbleArray(data));\n" +
                "        }\n" +
                "    }\n" +
                "}"));
        setSectionBlockLightMethod.forceInitialization();

        setSectionSkyLightMethod.init(new MethodDeclaration(resolver,
                "public static void setSectionSkyLight(Chunk chunk, int sectionIndex, byte[] data) {\n" +
                "    ChunkSection[] sections = chunk.getSections();\n" +
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
    public CompletableFuture<Void> setSectionBlockLightAsync(World world, int cx, int cy, int cz, byte[] data) {
        return CommonUtil.runAsyncMainThread(() -> {
            Object nms_chunk = HandleConversion.toChunkHandle(world.getChunkAt(cx, cz));
            setSectionBlockLightMethod.invoke(null, nms_chunk, cy, data);
        });
    }

    @Override
    public CompletableFuture<Void> setSectionSkyLightAsync(World world, int cx, int cy, int cz, byte[] data) {
        return CommonUtil.runAsyncMainThread(() -> {
            Object nms_chunk = HandleConversion.toChunkHandle(world.getChunkAt(cx, cz));
            setSectionSkyLightMethod.invoke(null, nms_chunk, cy, data);
        });
    }
}
