package com.bergerkiller.bukkit.common.reflection.classes;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import net.minecraft.server.v1_4_6.ChunkProviderServer;
import net.minecraft.server.v1_4_6.IChunkLoader;

public class ChunkProviderServerRef {
	public static final ClassTemplate<ChunkProviderServer> TEMPLATE = ClassTemplate.create(ChunkProviderServer.class);
	public static final FieldAccessor<IChunkLoader> chunkLoader = TEMPLATE.getField("e");
	public static final FieldAccessor<Object> chunks = TEMPLATE.getField("chunks");
	public static final FieldAccessor<Object> unloadQueue = TEMPLATE.getField("unloadQueue");
}
