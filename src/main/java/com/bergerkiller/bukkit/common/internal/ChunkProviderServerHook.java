package com.bergerkiller.bukkit.common.internal;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.reflection.net.minecraft.server.NMSChunk;
import com.bergerkiller.reflection.net.minecraft.server.NMSChunkProviderServer;
import com.bergerkiller.reflection.net.minecraft.server.NMSChunkRegionLoader;
import com.bergerkiller.reflection.net.minecraft.server.NMSWorldServer;
import com.bergerkiller.reflection.org.bukkit.craftbukkit.CBChunkIOExecutor;

import net.minecraft.server.v1_11_R1.BiomeBase.BiomeMeta;
import net.minecraft.server.v1_11_R1.*;
import org.bukkit.Server;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.generator.BlockPopulator;

import java.util.List;
import java.util.Random;

/**
 * A CPS Hook class that provides various new events, timings and other useful
 * utilities. This is here so that other plugins can safely control internal
 * behavior (NoLagg mainly).
 */
public class ChunkProviderServerHook extends ChunkProviderServer {

    public ChunkProviderServerHook(WorldServer worldserver, IChunkLoader ichunkloader, ChunkGenerator ichunkprovider) {
        super(worldserver, ichunkloader, ichunkprovider);
    }

    public org.bukkit.World getWorld() {
        return super.world.getWorld();
    }

    public List<BiomeMeta> getMobsFor(EnumCreatureType enumcreaturetype, BlockPosition pos) {
        List<BiomeMeta> mobs = super.chunkGenerator.getMobsFor(enumcreaturetype, pos);
        if (CommonPlugin.hasInstance()) {
            org.bukkit.World world = this.world.getWorld();
            return CommonPlugin.getInstance().getEventFactory().handleCreaturePreSpawn(world, pos.getX(), pos.getY(), pos.getZ(), mobs);
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

    public Chunk getChunkAt(int i, int j) {
        // Perform chunk population timings
        if (!CommonPlugin.TIMINGS.isActive()) {
            return super.getChunkAt(i, j);
        }

        Chunk chunk = this.getOrLoadChunkAt(i, j);

        if (!chunk.isDone()) {
            //chunk.done = true;
            chunk.d(true);
            chunk = this.chunkGenerator.getOrCreateChunk(i, j);
            chunk.d(true);

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
            org.bukkit.Chunk bChunk = Conversion.toChunk.convert(chunk);
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
        return chunk;
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
            final Object chunkRegionLoader = CommonUtil.tryCast(NMSChunkProviderServer.chunkLoader.get(this), NMSChunkRegionLoader.T.getType());
            if (chunkRegionLoader != null && NMSChunkRegionLoader.chunkExists(chunkRegionLoader, getWorld(), x, z)) {
                // Schedule for loading Async - return null to indicate that no chunk is loaded yet
                CBChunkIOExecutor.queueChunkLoad(chunkRegionLoader, getWorld(), this, x, z, runnable);
                return null;
            }
        }

        // Try to load the chunk from file
        chunk = Conversion.toChunk.convert(this.loadChunk(x, z));

        // Try to generate the chunk instead
        boolean newChunk;
        if (newChunk = (chunk == null)) {
            if (this.chunkGenerator == null) {
                // Nothing to do here
                // Don't even fire a chunk load event - empty chunk is not a valid Chunk
                // Registering it is also a bad idea...
                return null;
            }

            // Generate the chunk
            long time = System.nanoTime();
            try {
                chunk = Conversion.toChunk.convert(this.chunkGenerator.getOrCreateChunk(x, z));
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.a(throwable, "Exception generating new chunk");
                CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Chunk to be generated");
                crashreportsystemdetails.a("Location", String.format("%d,%d", x, z));
                crashreportsystemdetails.a("Position hash", Long.valueOf(MathUtil.longHashToLong(x, z)));
                crashreportsystemdetails.a("Generator", this.chunkGenerator.getClass().getName());
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
        NMSChunk.addEntities(chunkHandle);

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
        NMSChunk.loadNeighbours(chunkHandle, this, this, x, z);

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
        return CommonUtil.tryCast(NMSWorldServer.chunkProviderServer.get(Conversion.toWorldHandle.convert(world)), type);
    }

    private static IChunkLoader getLoader(Object cps) {
        return (IChunkLoader) NMSChunkProviderServer.chunkLoader.get(cps);
    }

    public static void hook(org.bukkit.World world) {
        ChunkProviderServer oldCPS = getCPS(world, ChunkProviderServer.class);
        if (oldCPS instanceof ChunkProviderServerHook) {
            return;
        }
        ChunkProviderServerHook newCPS = new ChunkProviderServerHook(oldCPS.world, getLoader(oldCPS), oldCPS.chunkGenerator);
        NMSChunkProviderServer.T.transfer(oldCPS, newCPS);
        NMSWorldServer.chunkProviderServer.set(newCPS.world, newCPS);
    }

    public static void unhook(org.bukkit.World world) {
        ChunkProviderServerHook oldCPS = getCPS(world, ChunkProviderServerHook.class);
        if (oldCPS == null) {
            return;
        }
        ChunkProviderServer newCPS = new ChunkProviderServer(oldCPS.world, getLoader(oldCPS), oldCPS.chunkGenerator);
        NMSChunkProviderServer.T.transfer(oldCPS, newCPS);
        NMSWorldServer.chunkProviderServer.set(newCPS.world, newCPS);
    }
}
