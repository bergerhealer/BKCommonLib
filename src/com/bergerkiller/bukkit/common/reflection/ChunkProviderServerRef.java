package com.bergerkiller.bukkit.common.reflection;

import com.bergerkiller.bukkit.common.ClassTemplate;
import com.bergerkiller.bukkit.common.SafeField;

import net.minecraft.server.ChunkProviderServer;
import net.minecraft.server.IChunkLoader;

public class ChunkProviderServerRef {
	public static final ClassTemplate<ChunkProviderServer> TEMPLATE = ClassTemplate.create(ChunkProviderServer.class);
	public static final SafeField<IChunkLoader> chunkLoader = TEMPLATE.getField("e");
}
