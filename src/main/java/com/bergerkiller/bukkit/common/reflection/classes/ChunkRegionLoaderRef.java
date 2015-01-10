package com.bergerkiller.bukkit.common.reflection.classes;

import org.bukkit.World;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import com.bergerkiller.bukkit.common.utils.CommonUtil;

public class ChunkRegionLoaderRef {
	public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("ChunkRegionLoader");
	private static final ClassTemplate<?> ChunkIOExecutor = ClassTemplate.create(CommonUtil.getCBClass("chunkio.ChunkIOExecutor"));
	private static final MethodAccessor<Boolean> chunkExists = TEMPLATE.getMethod("chunkExists", WorldRef.TEMPLATE.getType(), int.class, int.class);
	private static final MethodAccessor<Void> queueChunkLoad = ChunkIOExecutor.getMethod("queueChunkLoad", WorldRef.TEMPLATE.getType(), 
			ChunkRegionLoaderRef.TEMPLATE.getType(), ChunkProviderServerRef.TEMPLATE.getType(), int.class, int.class, Runnable.class);

	public static boolean chunkExists(Object chunkRegionLoader, World world, int x, int z) {
		return chunkExists.invoke(chunkRegionLoader, Conversion.toWorldHandle.convert(world), x, z);
	}

	public static void queueChunkLoad(Object chunkRegionLoader, World world, Object chunkProviderServer, int x, int z, Runnable taskWhenFinished) {
		queueChunkLoad.invoke(null, Conversion.toWorldHandle.convert(world), chunkRegionLoader, chunkProviderServer, x, z, taskWhenFinished);
	}
}
