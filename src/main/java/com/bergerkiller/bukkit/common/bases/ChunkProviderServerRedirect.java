package com.bergerkiller.bukkit.common.bases;

import org.bukkit.craftbukkit.v1_5_R1.chunkio.ChunkIOExecutor;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.proxies.ChunkProviderServerProxy;
import com.bergerkiller.bukkit.common.reflection.classes.ChunkProviderServerRef;
import com.bergerkiller.bukkit.common.reflection.classes.WorldServerRef;

import net.minecraft.server.v1_5_R1.Chunk;
import net.minecraft.server.v1_5_R1.ChunkProviderServer;
import net.minecraft.server.v1_5_R1.ChunkRegionLoader;
import net.minecraft.server.v1_5_R1.IChunkLoader;

public class ChunkProviderServerRedirect extends ChunkProviderServerProxy {

	public ChunkProviderServerRedirect(Object worldHandle) {
		super(worldHandle, null, null, null);
	}

	@Override
	public void setProxyBase(Object chunkProviderServer) {
		super.setProxyBase(chunkProviderServer);
		ChunkProviderServerRef.TEMPLATE.transfer(chunkProviderServer, this);
	}

	public void setBase(org.bukkit.World world) {
		setProxyBase(WorldServerRef.chunkProviderServer.get(Conversion.toWorldHandle.convert(world)));
	}

	@Override
	public Chunk getChunkAt(int x, int z, Runnable task) {
		if (!this.isSyncLoadSuppressed()) {
			return super.getChunkAt(x, z, task);
		}
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

	/**
	 * Whether attempts at loading the chunk in the call itself are suppressed, 
	 * returning null instead
	 * 
	 * @return True if sync loading is suppressed, False if not
	 */
	public boolean isSyncLoadSuppressed() {
		return false;
	}
}
