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

    /**
     * Retrieve the entry for a chunk and adds one reference to it.
     * 
     * @param chunk to add
     * @return forced chunk entry for the chunk
     */
    public abstract ForcedChunkEntry add(World world, int chunkX, int chunkZ);

    public static interface ForcedChunkEntry {
        void add();
        void remove();

        World getWorld();
        int getX();
        int getZ();
        org.bukkit.Chunk getChunk();
        CompletableFuture<org.bukkit.Chunk> getChunkAsync();
    }
}
