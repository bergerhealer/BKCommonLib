package com.bergerkiller.bukkit.common.bases;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.reflection.classes.ChunkProviderServerRef;
import com.bergerkiller.bukkit.common.reflection.classes.WorldServerRef;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import net.minecraft.server.v1_9_R1.*;
import org.bukkit.World;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

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

    private static ChunkGenerator getGenerator(Object chunkProviderServer) {
        return ((ChunkProviderServer) chunkProviderServer).chunkGenerator;
    }

    private void checkGenerator() {
        if (this.chunkGenerator == null) {
            throw new RuntimeException("Chunk generator has no generator set: " + (world == null ? "null" : world.getName()));
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
        return Conversion.toChunk.convert(this.chunkGenerator.getOrCreateChunk(x, z));
    }

    @Override
    public boolean unloadChunks() {
        return super.unloadChunks();
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
        crashreportsystemdetails.a("Generator", this.chunkGenerator.getClass().getName());
        throw new ReportedException(crashreport);
    }
}
