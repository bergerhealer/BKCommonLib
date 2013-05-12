package com.bergerkiller.bukkit.common.internal;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Server;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.entity.CommonEntityType;
import com.bergerkiller.bukkit.common.reflection.classes.ChunkProviderServerRef;
import com.bergerkiller.bukkit.common.reflection.classes.ChunkRef;
import com.bergerkiller.bukkit.common.reflection.classes.ChunkRegionLoaderRef;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;

import net.minecraft.server.BiomeMeta;
import net.minecraft.server.Chunk;
import net.minecraft.server.ChunkProviderServer;
import net.minecraft.server.CrashReport;
import net.minecraft.server.CrashReportSystemDetails;
import net.minecraft.server.EnumCreatureType;
import net.minecraft.server.IChunkLoader;
import net.minecraft.server.IChunkProvider;
import net.minecraft.server.ReportedException;
import net.minecraft.server.WorldServer;

/**
 * A CPS Hook class that provides various new events, timings and other useful utilities.
 * This is here so that other plugins can safely control internal behavior (NoLagg mainly).
 */
public class ChunkProviderServerHook extends ChunkProviderServer {
	private final List<BiomeMeta> mobsBuffer = new ArrayList<BiomeMeta>();

	public ChunkProviderServerHook(WorldServer worldserver, IChunkLoader ichunkloader, IChunkProvider ichunkprovider) {
		super(worldserver, ichunkloader, ichunkprovider);
	}

	public org.bukkit.World getWorld() {
		return super.world.getWorld();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<BiomeMeta> getMobsFor(EnumCreatureType enumcreaturetype, int x, int y, int z) {
		// Use a buffer for the entities, recreating a new List for every spawn operation kills the server
		List<BiomeMeta> mobs = super.getMobsFor(enumcreaturetype, x, y, z);
		if (LogicUtil.nullOrEmpty(mobs)) {
			return mobs;
		}

		// Fire events for all mobs spawned
		this.mobsBuffer.clear();
		final CommonPlugin instance = CommonPlugin.getInstance();
		for (BiomeMeta mob : mobs) {
			if (instance.canSpawnMob(this.world.getWorld(), x, y, z, CommonEntityType.byNMSEntityClass(mob.b).entityType)) {
				this.mobsBuffer.add(mob);
			}
		}

		// Return the buffer (note that this does not allow 'permanent' storage in any way)
		return this.mobsBuffer;
	}

	@Override
	public Chunk loadChunk(int x, int z) {
		// Perform chunk load from file timings
		List<TimingsListener> listeners = CommonPlugin.getTimings();
		if (listeners.isEmpty()) {
			return super.loadChunk(x, z);
		} else {
			long time = System.nanoTime();
			Chunk nmsChunk = super.loadChunk(x, z);
			if (nmsChunk != null) {
				time = System.nanoTime() - time;
				org.bukkit.Chunk chunk = Conversion.toChunk.convert(nmsChunk);
				for (TimingsListener listener : listeners) {
					listener.onChunkLoad(chunk, time);
				}
			}
			return nmsChunk;
		}
	}

	@Override
	public Chunk getChunkAt(int x, int z, Runnable runnable) {
		// Perform chunk generation timings
		List<TimingsListener> listeners = CommonPlugin.getTimings();
		if (listeners.isEmpty()) {
			return super.getChunkAt(x, z, runnable);
		}

		WorldUtil.setChunkUnloading(getWorld(), x, z, false);
		org.bukkit.Chunk chunk = WorldUtil.getChunk(getWorld(), x, z);

		// Deal with delayed (async) loading accordingly
		if (chunk != null) {
			if (runnable != null) {
				runnable.run();
			}
			return CommonNMS.getNative(chunk);
		} else if (runnable != null) {
			// Queue chunk for loading Async
			final Object chunkRegionLoader = CommonUtil.tryCast(ChunkProviderServerRef.chunkLoader.get(this), ChunkRegionLoaderRef.TEMPLATE.getType());
			if (chunkRegionLoader != null && ChunkRegionLoaderRef.chunkExists(chunkRegionLoader, getWorld(), x, z)) {
				// Schedule for loading Async - return null to indicate that no chunk is loaded yet
				ChunkRegionLoaderRef.queueChunkLoad(chunkRegionLoader, getWorld(), this, x, z, runnable);
				return null;
			}
		}

		// Try to load the chunk from file
		chunk = CommonNMS.getChunk(this.loadChunk(x, z));

		// Try to generate the chunk instead
		boolean newChunk;
		if (newChunk = (chunk == null)) {
			if (this.chunkProvider == null) {
				// Nothing to do here
				// Don't even fire a chunk load event - empty chunk is not a valid Chunk
				// Registering it is also a bad idea...
				return this.emptyChunk;
			}

			// Generate the chunk
			try {
				long time = System.nanoTime();
				chunk = CommonNMS.getChunk(this.chunkProvider.getOrCreateChunk(x, z));
				if (chunk != null) {
					time = System.nanoTime() - time;
					for (TimingsListener listener : listeners) {
						listener.onChunkGenerate(chunk, time);
					}
				}
			} catch (Throwable throwable) {
				CrashReport crashreport = CrashReport.a(throwable, "Exception generating new chunk");
				CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Chunk to be generated");
				crashreportsystemdetails.a("Location", String.format("%d,%d", x, z));
				crashreportsystemdetails.a("Position hash", Long.valueOf(MathUtil.longHashToLong(x, z)));
				crashreportsystemdetails.a("Generator", this.chunkProvider.getName());
				throw new ReportedException(crashreport);
			}
		}

		// Sadly, loading failed...how did this happen?!
		if (chunk == null) {
			return null;
		}

		// Initial registration of the chunk on the server
		WorldUtil.setChunk(getWorld(), x, z, chunk);
		Chunk chunkHandle = CommonNMS.getNative(chunk);
		ChunkRef.addEntities(chunkHandle);

		// CraftBukkit start
		Server server = WorldUtil.getServer(getWorld());
		if (server != null) {
			/*
			 * If it's a new world, the first few chunks are generated
			 * inside the World constructor. We can't reliably alter
			 * that, so we have no way of creating a
			 * CraftWorld/CraftServer at that point.
			 */
			server.getPluginManager().callEvent(new org.bukkit.event.world.ChunkLoadEvent(chunk, newChunk));
		}
		// CraftBukkit end

		// Perhaps load some neighboring chunks? (population related)
		ChunkRef.loadNeighbours(chunkHandle, this, this, x, z);

		// Successful load!
		return chunkHandle;
	}

	@Override
	public boolean unloadChunks() {
		// Perform chunk unload timings
		List<TimingsListener> listeners = CommonPlugin.getTimings();
		if (listeners.isEmpty()) {
			return super.unloadChunks();
		} else {
			long time = System.nanoTime();
			try {
				return super.unloadChunks();
			} finally {
				time = System.nanoTime() - time;
				org.bukkit.World world = getWorld();
				for (TimingsListener listener : listeners) {
					listener.onChunkUnloading(world, time);
				}
			}
		}
	}
}
