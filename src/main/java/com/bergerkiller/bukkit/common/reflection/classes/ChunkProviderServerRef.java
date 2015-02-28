package com.bergerkiller.bukkit.common.reflection.classes;

import org.bukkit.World;

import com.bergerkiller.bukkit.common.conversion.ConversionPairs;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import com.bergerkiller.bukkit.common.reflection.TranslatorFieldAccessor;

public class ChunkProviderServerRef {

    public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("ChunkProviderServer");
    public static final FieldAccessor<Object> chunkLoader = TEMPLATE.getField("chunkLoader");
    public static final FieldAccessor<Object> chunks = TEMPLATE.getField("chunks");
    public static final FieldAccessor<Object> unloadQueue = TEMPLATE.getField("unloadQueue");
    public static final TranslatorFieldAccessor<World> world = TEMPLATE.getField("world").translate(ConversionPairs.world);
    public static final MethodAccessor<Boolean> isChunkLoaded = TEMPLATE.getMethod("isChunkLoaded", int.class, int.class);
}
