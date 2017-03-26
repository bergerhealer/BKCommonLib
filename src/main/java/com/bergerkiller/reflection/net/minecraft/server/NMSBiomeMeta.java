package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.reflection.ClassTemplate;
import com.bergerkiller.reflection.FieldAccessor;

public class NMSBiomeMeta extends NMSWeightedRandomChoice {
	public static final ClassTemplate<?> T = ClassTemplate.createNMS("BiomeBase.BiomeMeta");
	public static final FieldAccessor<Class<?>> entity = T.nextField("public Class<? extends EntityInsentient> b");
	public static final FieldAccessor<Integer> minSpawnCount = T.nextFieldSignature("public int c");
	public static final FieldAccessor<Integer> maxSpawnCount = T.nextFieldSignature("public int d");
}
