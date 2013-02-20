package com.bergerkiller.bukkit.common.bases;

import java.util.List;

import org.bukkit.craftbukkit.v1_4_R1.chunkio.ChunkIOExecutor;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.reflection.classes.ChunkProviderServerRef;
import com.bergerkiller.bukkit.common.reflection.classes.WorldServerRef;

import net.minecraft.server.v1_4_R1.Chunk;
import net.minecraft.server.v1_4_R1.ChunkPosition;
import net.minecraft.server.v1_4_R1.ChunkProviderServer;
import net.minecraft.server.v1_4_R1.ChunkRegionLoader;
import net.minecraft.server.v1_4_R1.EnumCreatureType;
import net.minecraft.server.v1_4_R1.IChunkLoader;
import net.minecraft.server.v1_4_R1.IChunkProvider;
import net.minecraft.server.v1_4_R1.IProgressUpdate;
import net.minecraft.server.v1_4_R1.World;
import net.minecraft.server.v1_4_R1.WorldServer;

public class ChunkProviderServerRedirect extends ChunkProviderServer {
	private ChunkProviderServer base;

	public ChunkProviderServerRedirect(Object worldHandle) {
		super((WorldServer) worldHandle, null, null);
	}

	public void setBase(org.bukkit.World world) {
		setBase(WorldServerRef.chunkProviderServer.get(Conversion.toWorldHandle.convert(world)));
	}

	public void setBase(Object chunkProviderServer) {
		this.base = (ChunkProviderServer) chunkProviderServer;
	}
	
	@Override
	public void a() {
		this.base.a();
	}

	@Override
	public boolean canSave() {
		return this.base.canSave();
	}

	@Override
	public ChunkPosition findNearestMapFeature(World world, String s, int i, int j, int k) {
		return this.base.findNearestMapFeature(world, s, i, j, k);
	}

	@Override
	public void getChunkAt(IChunkProvider arg0, int arg1, int arg2) {
		this.base.getChunkAt(arg0, arg1, arg2);
	}

	@Override
	public Chunk getChunkAt(int x, int z, Runnable task) {
		if (!this.isSyncLoadSuppressed()) {
			return this.base.getChunkAt(x, z, task);
		}
		if (base.isChunkLoaded(x, z)) {
			return null; // Ignore, is already loaded
		}
		IChunkLoader l = (IChunkLoader) ChunkProviderServerRef.chunkLoader.get(base);
		if (l instanceof ChunkRegionLoader) {
			ChunkRegionLoader loader = (ChunkRegionLoader) l;
			if (loader.chunkExists(base.world, x, z)) {
				// Load the chunk async
	            ChunkIOExecutor.queueChunkLoad(base.world, loader, base, x, z, task);
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

	@Override
	public Chunk getChunkAt(int i, int j) {
		return this.base.getChunkAt(i, j);
	}

	@Override
	public int getLoadedChunks() {
		return this.base.getLoadedChunks();
	}

	@Override
	@SuppressWarnings("rawtypes")
	public List getMobsFor(EnumCreatureType enumcreaturetype, int i, int j, int k) {
		return this.base.getMobsFor(enumcreaturetype, i, j, k);
	}

	@Override
	public String getName() {
		return this.base.getName();
	}

	@Override
	public Chunk getOrCreateChunk(int arg0, int arg1) {
		return this.base.getOrCreateChunk(arg0, arg1);
	}

	@Override
	public boolean isChunkLoaded(int i, int j) {
		return this.base.isChunkLoaded(i, j);
	}

	@Override
	public Chunk loadChunk(int arg0, int arg1) {
		return this.base.loadChunk(arg0, arg1);
	}

	@Override
	public void queueUnload(int arg0, int arg1) {
		this.base.queueUnload(arg0, arg1);
	}

	@Override
	public void recreateStructures(int i, int j) {
		this.base.recreateStructures(i, j);
	}

	@Override
	public void saveChunk(Chunk arg0) {
		this.base.saveChunk(arg0);
	}

	@Override
	public void saveChunkNOP(Chunk arg0) {
		this.base.saveChunkNOP(arg0);
	}

	@Override
	public boolean saveChunks(boolean arg0, IProgressUpdate arg1) {
		return this.base.saveChunks(arg0, arg1);
	}

	@Override
	public boolean unloadChunks() {
		return this.base.unloadChunks();
	}
}
