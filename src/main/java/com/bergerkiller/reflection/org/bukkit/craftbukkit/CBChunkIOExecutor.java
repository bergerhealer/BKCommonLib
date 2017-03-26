package com.bergerkiller.reflection.org.bukkit.craftbukkit;

import org.bukkit.World;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.reflection.ClassTemplate;
import com.bergerkiller.reflection.FieldAccessor;
import com.bergerkiller.reflection.MethodAccessor;

public class CBChunkIOExecutor {
	public static final ClassTemplate<?> T = ClassTemplate.createCB("chunkio.ChunkIOExecutor");
	
    public static final FieldAccessor<Object> asynchronousExecutor = T.selectField("private static final org.bukkit.craftbukkit.util.AsynchronousExecutor<QueuedChunk, Chunk, Runnable, RuntimeException> instance");
	
	private static final MethodAccessor<Void> queueChunkLoad = T.selectMethod(
			"public static void queueChunkLoad(net.minecraft.server.World world," +
			                                  "net.minecraft.server.ChunkRegionLoader loader," +
			                                  "net.minecraft.server.ChunkProviderServer provider," +
			                                  "int x, int z, Runnable runnable)"
	);

    public static void queueChunkLoad(Object chunkRegionLoader, World world, Object chunkProviderServer, int x, int z, Runnable taskWhenFinished) {
        CBChunkIOExecutor.queueChunkLoad.invoke(null, Conversion.toWorldHandle.convert(world), chunkRegionLoader, chunkProviderServer, x, z, taskWhenFinished);
    }
}
