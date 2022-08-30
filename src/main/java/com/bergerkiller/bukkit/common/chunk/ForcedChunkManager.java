package com.bergerkiller.bukkit.common.chunk;

import java.util.concurrent.CompletableFuture;

import org.bukkit.World;

/**
 * Base class for a manager for forced chunks that should
 * make sure chunks stay loaded.
 */
public abstract class ForcedChunkManager {

    public final ForcedChunk newForcedChunk(World world, int chunkX, int chunkZ) {
        return new ForcedChunk(this.add(world, chunkX, chunkZ));
    }

    public final ForcedChunk newForcedChunk(World world, int chunkX, int chunkZ, int radius) {
        return new ForcedChunk(this.add(world, chunkX, chunkZ, radius));
    }

    /**
     * Retrieve the entry for a chunk and adds one reference to it.
     * Assumes a radius of 2 chunks, which ticks entities.
     * 
     * @param world World of the chunk to add
     * @param chunkX X-coordinate of the chunk to add
     * @param chunkZ Z-coordinate of the chunk to add
     * @return forced chunk entry for the chunk
     */
    public final ForcedChunkEntry add(World world, int chunkX, int chunkZ) {
        return add(world, chunkX, chunkZ, 2);
    }

    /**
     * Retrieve the entry for a chunk and adds one reference to it.
     * 
     * @param world World of the chunk to add
     * @param chunkX X-coordinate of the chunk to add
     * @param chunkZ Z-coordinate of the chunk to add
     * @param radius Chunk radius around the chunk to load, 0 to only load the chunk.
     *               A chunk radius of 2 or more is needed to tick entities.
     * @return forced chunk entry for the chunk
     */
    public abstract ForcedChunkEntry add(World world, int chunkX, int chunkZ, int radius);

    public static interface ForcedChunkEntry {
        void add();
        void remove();

        World getWorld();
        int getRadius();
        int getX();
        int getZ();
        org.bukkit.Chunk getChunk();
        CompletableFuture<org.bukkit.Chunk> getChunkAsync();
    }
}
