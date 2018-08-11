package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.generated.net.minecraft.server.BiomeBaseHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;

@Deprecated
public class NMSBiomeMeta extends NMSWeightedRandomChoice {
	public static final ClassTemplate<?> T = ClassTemplate.createNMS("BiomeBase.BiomeMeta");
	public static final FieldAccessor<Class<?>> entity = BiomeBaseHandle.BiomeMetaHandle.T.entityClass.toFieldAccessor();
	public static final FieldAccessor<Integer> minSpawnCount = BiomeBaseHandle.BiomeMetaHandle.T.minSpawnCount.toFieldAccessor();
	public static final FieldAccessor<Integer> maxSpawnCount = BiomeBaseHandle.BiomeMetaHandle.T.maxSpawnCount.toFieldAccessor();
}
