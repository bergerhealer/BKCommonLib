package com.bergerkiller.bukkit.common.bases;

import org.bukkit.craftbukkit.chunkio.ChunkIOExecutor;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.proxies.ChunkProviderServerProxy;
import com.bergerkiller.bukkit.common.reflection.classes.ChunkProviderServerRef;
import com.bergerkiller.bukkit.common.reflection.classes.WorldServerRef;

import net.minecraft.server.Chunk;
import net.minecraft.server.ChunkProviderServer;
import net.minecraft.server.ChunkRegionLoader;
import net.minecraft.server.IChunkLoader;

/**
 * This class is mainly used by NoLagg chunks - for compatibilities' sake, it is ported to here.
 */
public class DummyChunkProviderServer extends ChunkProviderServerProxy {

	DummyChunkProviderServer(Object worldHandle) {
		super(worldHandle, null, null, null);
	}

	public void setBase(org.bukkit.World world) {
		setProxyBase(WorldServerRef.chunkProviderServer.get(Conversion.toWorldHandle.convert(world)));
	}

	@Override
	public Chunk getChunkAt(int x, int z, Runnable task) {
		if (this.isChunkLoaded(x, z)) {
			return null; // Ignore, is already loaded
		}
		IChunkLoader l = (IChunkLoader) ChunkProviderServerRef.chunkLoader.get(getProxyBase());
		if (l instanceof ChunkRegionLoader) {
			ChunkRegionLoader loader = (ChunkRegionLoader) l;
			if (loader.chunkExists(world, x, z)) {
				// Load the chunk async
	            ChunkIOExecutor.queueChunkLoad(world, loader, (ChunkProviderServer) getProxyBase(), x, z, task);
			}
		}
		// Ignore attempt to generate the chunk
		return null;
	}
}
