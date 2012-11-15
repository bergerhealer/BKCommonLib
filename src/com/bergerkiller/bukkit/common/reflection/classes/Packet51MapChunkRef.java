package com.bergerkiller.bukkit.common.reflection.classes;

import net.minecraft.server.Packet51MapChunk;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.SafeField;

public class Packet51MapChunkRef {
	public static final ClassTemplate<Packet51MapChunk> TEMPLATE = ClassTemplate.create(Packet51MapChunk.class);
	public static final SafeField<Integer> size = TEMPLATE.getField("size");
	public static final SafeField<Boolean> hasBiomeData = TEMPLATE.getField("e");
	public static final SafeField<byte[]> buffer = TEMPLATE.getField("buffer");
	public static final SafeField<byte[]> inflatedBuffer = TEMPLATE.getField("inflatedBuffer");
	public static final SafeField<Integer> x = TEMPLATE.getField("a");
	public static final SafeField<Integer> z = TEMPLATE.getField("b");
	public static final SafeField<Integer> chunkDataBitMap = TEMPLATE.getField("c");
	public static final SafeField<Integer> chunkBiomeBitMap = TEMPLATE.getField("d");
}
