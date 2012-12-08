package com.bergerkiller.bukkit.common.reflection.classes;

import net.minecraft.server.Packet51MapChunk;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeDirectField;

public class Packet51MapChunkRef {
	public static final ClassTemplate<Packet51MapChunk> TEMPLATE = ClassTemplate.create(Packet51MapChunk.class);
	public static final FieldAccessor<Integer> size = TEMPLATE.getField("size");
	public static final FieldAccessor<byte[]> buffer = TEMPLATE.getField("buffer");
	public static final FieldAccessor<byte[]> inflatedBuffer = TEMPLATE.getField("inflatedBuffer");
	public static final FieldAccessor<Boolean> hasBiomeData = new SafeDirectField<Boolean>() {
		public Boolean get(Object instance) { return ((Packet51MapChunk) instance).e; }
		public boolean set(Object instance, Boolean value) { ((Packet51MapChunk) instance).e = value; return true; }
	};
	public static final FieldAccessor<Integer> x = new SafeDirectField<Integer>() {
		public Integer get(Object instance) { return ((Packet51MapChunk) instance).a; }
		public boolean set(Object instance, Integer value) { ((Packet51MapChunk) instance).a = value; return true; }
	};
	public static final FieldAccessor<Integer> z = new SafeDirectField<Integer>() {
		public Integer get(Object instance) { return ((Packet51MapChunk) instance).b; }
		public boolean set(Object instance, Integer value) { ((Packet51MapChunk) instance).b = value; return true; }
	};
	public static final FieldAccessor<Integer> chunkDataBitMap = new SafeDirectField<Integer>() {
		public Integer get(Object instance) { return ((Packet51MapChunk) instance).c; }
		public boolean set(Object instance, Integer value) { ((Packet51MapChunk) instance).c = value; return true; }
	};
	public static final FieldAccessor<Integer> chunkBiomeBitMap = new SafeDirectField<Integer>() {
		public Integer get(Object instance) { return ((Packet51MapChunk) instance).d; }
		public boolean set(Object instance, Integer value) { ((Packet51MapChunk) instance).d = value; return true; }
	};
}
