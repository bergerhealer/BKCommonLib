package com.bergerkiller.bukkit.common.bases;

import java.util.Random;

import org.bukkit.World;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.generator.BlockPopulator;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.reflection.classes.ChunkProviderServerRef;
import com.bergerkiller.bukkit.common.reflection.classes.WorldServerRef;
import com.bergerkiller.bukkit.common.utils.MathUtil;

import net.minecraft.server.v1_8_R1.BlockSand;
import net.minecraft.server.v1_8_R1.Chunk;
import net.minecraft.server.v1_8_R1.ChunkProviderServer;
import net.minecraft.server.v1_8_R1.CrashReport;
import net.minecraft.server.v1_8_R1.CrashReportSystemDetails;
import net.minecraft.server.v1_8_R1.IChunkLoader;
import net.minecraft.server.v1_8_R1.IChunkProvider;
import net.minecraft.server.v1_8_R1.ReportedException;
import net.minecraft.server.v1_8_R1.WorldServer;

public class ChunkProviderServerBase extends ChunkProviderServer {

    public final org.bukkit.World world;

    public ChunkProviderServerBase(World world) {
        this(WorldServerRef.chunkProviderServer.get(Conversion.toWorldHandle.convert(world)));
    }

    public ChunkProviderServerBase(Object chunkProviderServer) {
        super(getWorld(chunkProviderServer), getLoader(chunkProviderServer), getGenerator(chunkProviderServer));
        ChunkProviderServerRef.TEMPLATE.transfer(chunkProviderServer, this);
        this.world = super.world.getWorld();
    }

    private static WorldServer getWorld(Object chunkProviderServer) {
        return ((ChunkProviderServer) chunkProviderServer).world;
    }

    private static IChunkLoader getLoader(Object chunkProviderServer) {
        return (IChunkLoader) ChunkProviderServerRef.chunkLoader.get(chunkProviderServer);
    }

    private static IChunkProvider getGenerator(Object chunkProviderServer) {
        return ((ChunkProviderServer) chunkProviderServer).chunkProvider;
    }

    private void checkGenerator() {
        if (this.chunkProvider == null) {
            throw new RuntimeException("Chunk provider has no generator set: " + (world == null ? "null" : world.getName()));
        }
    }

    /**
     * Reverts the previous creation of this Chunk Provider, creating the
     * default ChunkProviderServer instance
     *
     * @return ChunkProviderServer instance this base was made from
     */
    public Object revert() {
        ChunkProviderServer chunkProvider = new ChunkProviderServer(super.world, null, null);
        ChunkProviderServerRef.TEMPLATE.transfer(this, chunkProvider);
        return chunkProvider;
    }

    /**
     * @deprecated Use {@link #loadBukkitChunk(int, int) loadBukkitChunk(x, z)}
     * instead
     */
    @Override
    @Deprecated
    public Chunk loadChunk(int x, int z) {
        return (Chunk) Conversion.toChunkHandle.convert(loadBukkitChunk(x, z));
    }

    /**
     * Called when a chunk is loaded from file
     *
     * @param x - coordinate of the chunk
     * @param z - coordinate of the chunk
     * @return the newly loaded Chunk
     */
    public org.bukkit.Chunk loadBukkitChunk(int x, int z) {
        return Conversion.toChunk.convert(super.loadChunk(x, z));
    }

    /**
     * @deprecated Use
     * {@link #getBukkitChunkAt(int, int, Runnable) getBukkitChunkAt(x, z, taskWhenFinished)}
     * instead
     */
    @Override
    @Deprecated
    public Chunk getChunkAt(int i, int j, Runnable runnable) {
        return (Chunk) Conversion.toChunkHandle.convert(getBukkitChunkAt(i, j, runnable));
    }

    /**
     * Called when a chunk is requested from this provider
     *
     * @param x - coordinate of the chunk
     * @param z - coordinate of the chunk
     * @param taskWhenFinished to execute when the chunk is successfully
     * obtained
     * @return the loaded or obtained Bukkit chunk
     */
    public org.bukkit.Chunk getBukkitChunkAt(int x, int z, Runnable taskWhenFinished) {
        checkGenerator();
        return Conversion.toChunk.convert(super.getChunkAt(x, z, taskWhenFinished));
    }

    /**
     * Called when a Block Populator is populating a chunk during generation
     *
     * @param chunk to be populated
     * @param populator used
     * @param random used by the populator
     */
    public void onPopulate(org.bukkit.Chunk chunk, BlockPopulator populator, Random random) {
        populator.populate(world, random, chunk);
    }

    /**
     * Orders the underlying chunk provider to generate a new chunk
     *
     * @param x - coordinate of the chunk to generate
     * @param z - coordinate of the chunk to generate
     * @return generated chunk
     */
    public org.bukkit.Chunk generateChunk(int x, int z) {
        checkGenerator();
        return Conversion.toChunk.convert(this.chunkProvider.getOrCreateChunk(x, z));
    }

    @Override
    public boolean unloadChunks() {
        return super.unloadChunks();
    }

    /**
     * @deprecated Use {@link #onPopulate(Chunk, BlockPopulator, Random)} to
     * handle populators instead
     */
    @Override
    @Deprecated
    public void getChunkAt(IChunkProvider ichunkprovider, int i, int j) {
        checkGenerator();
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

            if (world != null) {
                for (BlockPopulator populator : world.getPopulators()) {
                    onPopulate(chunk.bukkitChunk, populator, random);
                }
            }
            BlockSand.instaFall = false;
            super.world.getServer().getPluginManager().callEvent(new ChunkPopulateEvent(chunk.bukkitChunk));
            // CraftBukkit end

            chunk.e();
        }
    }

    /**
     * Handles an error that occurred during chunk generation
     *
     * @param throwable that was thrown
     * @param chunkX coordinate of the chunk
     * @param chunkZ coordinate of the chunk
     */
    public void handleGeneratorError(Throwable throwable, int chunkX, int chunkZ) {
        CrashReport crashreport = CrashReport.a(throwable, "Exception generating new chunk");
        CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Chunk to be generated");
        crashreportsystemdetails.a("Location", String.format("%d,%d", chunkX, chunkZ));
        crashreportsystemdetails.a("Position hash", Long.valueOf(MathUtil.longHashToLong(chunkX, chunkZ)));
        crashreportsystemdetails.a("Generator", this.chunkProvider.getName());
        throw new ReportedException(crashreport);
    }
}
