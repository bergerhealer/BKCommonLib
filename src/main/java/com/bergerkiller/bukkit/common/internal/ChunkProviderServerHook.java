package com.bergerkiller.bukkit.common.internal;

import java.util.List;
import java.util.Random;

import org.bukkit.Server;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.generator.BlockPopulator;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.reflection.classes.ChunkProviderServerRef;
import com.bergerkiller.bukkit.common.reflection.classes.ChunkRef;
import com.bergerkiller.bukkit.common.reflection.classes.ChunkRegionLoaderRef;
import com.bergerkiller.bukkit.common.reflection.classes.WorldServerRef;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;

import net.minecraft.server.v1_8_R1.BiomeMeta;
import net.minecraft.server.v1_8_R1.BlockPosition;
import net.minecraft.server.v1_8_R1.BlockSand;
import net.minecraft.server.v1_8_R1.Chunk;
import net.minecraft.server.v1_8_R1.ChunkProviderServer;
import net.minecraft.server.v1_8_R1.CrashReport;
import net.minecraft.server.v1_8_R1.CrashReportSystemDetails;
import net.minecraft.server.v1_8_R1.EnumCreatureType;
import net.minecraft.server.v1_8_R1.IChunkLoader;
import net.minecraft.server.v1_8_R1.IChunkProvider;
import net.minecraft.server.v1_8_R1.ReportedException;
import net.minecraft.server.v1_8_R1.WorldServer;

/**
 * A CPS Hook class that provides various new events, timings and other useful
 * utilities. This is here so that other plugins can safely control internal
 * behavior (NoLagg mainly).
 */
public class ChunkProviderServerHook extends ChunkProviderServer {

    public ChunkProviderServerHook(WorldServer worldserver, IChunkLoader ichunkloader, IChunkProvider ichunkprovider) {
        super(worldserver, ichunkloader, ichunkprovider);
    }

    public org.bukkit.World getWorld() {
        return super.world.getWorld();
    }

    @SuppressWarnings("unchecked")
    public List<BiomeMeta> getMobsFor(EnumCreatureType enumcreaturetype, int x, int y, int z) {
        List<BiomeMeta> mobs = super.getMobsFor(enumcreaturetype, new BlockPosition(x, y, z));
        if (CommonPlugin.hasInstance()) {
            org.bukkit.World world = this.world.getWorld();
            return CommonPlugin.getInstance().getEventFactory().handleCreaturePreSpawn(world, x, y, z, mobs);
        } else {
            return mobs;
        }
    }

    @Override
    public Chunk loadChunk(int x, int z) {
        // Perform chunk load from file timings
        if (!CommonPlugin.TIMINGS.isActive()) {
            return super.loadChunk(x, z);
        } else {
            long time = System.nanoTime();
            Chunk nmsChunk = super.loadChunk(x, z);
            if (nmsChunk != null) {
                time = System.nanoTime() - time;
                CommonPlugin.TIMINGS.onChunkLoad(Conversion.toChunk.convert(nmsChunk), time);
            }
            return nmsChunk;
        }
    }

    @Override
    public void getChunkAt(IChunkProvider ichunkprovider, int i, int j) {
        // Perform chunk population timings
        if (!CommonPlugin.TIMINGS.isActive()) {
            super.getChunkAt(ichunkprovider, i, j);
            return;
        }

        Chunk chunk = this.getOrCreateChunk(i, j);

        if (!chunk.done) {
            chunk.done = true;
            this.chunkProvider.getChunkAt(ichunkprovider, i, j);

            // CraftBukkit start
            BlockSand.instaFall = true;
            final Random random = new Random();
            random.setSeed(world.getSeed());
            long xRand = random.nextLong() / 2L * 2L + 1L;
            long zRand = random.nextLong() / 2L * 2L + 1L;
            random.setSeed((long) i * xRand + (long) j * zRand ^ world.getSeed());

            // Call populators
            long time;
            org.bukkit.World bWorld = getWorld();
            org.bukkit.Chunk bChunk = CommonNMS.getChunk(chunk);
            for (BlockPopulator populator : bWorld.getPopulators()) {
                time = System.nanoTime();
                try {
                    populator.populate(bWorld, random, bChunk);
                } finally {
                    time = System.nanoTime() - time;
                    CommonPlugin.TIMINGS.onChunkPopulate(bChunk, populator, time);
                }
            }

            // Done
            BlockSand.instaFall = false;
            super.world.getServer().getPluginManager().callEvent(new ChunkPopulateEvent(chunk.bukkitChunk));
            // CraftBukkit end

            chunk.e();
        }
    }

    @Override
    public Chunk getChunkAt(int x, int z, Runnable runnable) {
        // Perform chunk generation timings
        if (!CommonPlugin.TIMINGS.isActive()) {
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
            long time = System.nanoTime();
            try {
                chunk = CommonNMS.getChunk(this.chunkProvider.getOrCreateChunk(x, z));
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.a(throwable, "Exception generating new chunk");
                CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Chunk to be generated");
                crashreportsystemdetails.a("Location", String.format("%d,%d", x, z));
                crashreportsystemdetails.a("Position hash", Long.valueOf(MathUtil.longHashToLong(x, z)));
                crashreportsystemdetails.a("Generator", this.chunkProvider.getName());
                throw new ReportedException(crashreport);
            }
            if (chunk != null) {
                time = System.nanoTime() - time;
                CommonPlugin.TIMINGS.onChunkGenerate(chunk, time);
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
        if (!CommonPlugin.TIMINGS.isActive()) {
            return super.unloadChunks();
        } else {
            long time = System.nanoTime();
            try {
                return super.unloadChunks();
            } finally {
                time = System.nanoTime() - time;
                CommonPlugin.TIMINGS.onChunkUnloading(getWorld(), time);
            }
        }
    }

    private static <T> T getCPS(org.bukkit.World world, Class<T> type) {
        return CommonUtil.tryCast(WorldServerRef.chunkProviderServer.get(Conversion.toWorldHandle.convert(world)), type);
    }

    private static IChunkLoader getLoader(Object cps) {
        return (IChunkLoader) ChunkProviderServerRef.chunkLoader.get(cps);
    }

    public static void hook(org.bukkit.World world) {
        ChunkProviderServer oldCPS = getCPS(world, ChunkProviderServer.class);
        if (oldCPS instanceof ChunkProviderServerHook) {
            return;
        }
        ChunkProviderServerHook newCPS = new ChunkProviderServerHook(oldCPS.world, getLoader(oldCPS), oldCPS.chunkProvider);
        ChunkProviderServerRef.TEMPLATE.transfer(oldCPS, newCPS);
        WorldServerRef.chunkProviderServer.set(newCPS.world, newCPS);
    }

    public static void unhook(org.bukkit.World world) {
        ChunkProviderServerHook oldCPS = getCPS(world, ChunkProviderServerHook.class);
        if (oldCPS == null) {
            return;
        }
        ChunkProviderServer newCPS = new ChunkProviderServer(oldCPS.world, getLoader(oldCPS), oldCPS.chunkProvider);
        ChunkProviderServerRef.TEMPLATE.transfer(oldCPS, newCPS);
        WorldServerRef.chunkProviderServer.set(newCPS.world, newCPS);
    }
}
