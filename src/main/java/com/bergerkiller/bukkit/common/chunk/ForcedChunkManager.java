package com.bergerkiller.bukkit.common.chunk;

import java.util.concurrent.CompletableFuture;

import org.bukkit.World;

/**
 * Base class for a manager for forced chunks that should
 * make sure chunks stay loaded.
 */
public abstract class ForcedChunkManager {
    private boolean trackingCreationStack = false;

    public final ForcedChunk newNone() {
        if (this.isTrackingCreationStack()) {
            return new ForcedChunk.CreationTrackedForcedChunk(null);
        } else {
            return new ForcedChunk(null);
        }
    }

    public final ForcedChunk newForcedChunk(World world, int chunkX, int chunkZ) {
        if (this.isTrackingCreationStack()) {
            return new ForcedChunk.CreationTrackedForcedChunk(this.add(world, chunkX, chunkZ));
        } else {
            return new ForcedChunk(this.add(world, chunkX, chunkZ));
        }
    }

    public final ForcedChunk newForcedChunk(World world, int chunkX, int chunkZ, int radius) {
        if (this.isTrackingCreationStack()) {
            return new ForcedChunk.CreationTrackedForcedChunk(this.add(world, chunkX, chunkZ, radius));
        } else {
            return new ForcedChunk(this.add(world, chunkX, chunkZ, radius));
        }
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

    /**
     * Gets whether the stack trace of the creation of forced chunks must be tracked.
     * This is used to diagnose forced chunks that aren't closed before being garbage
     * collected.
     *
     * @return True to track the creation stack
     */
    public boolean isTrackingCreationStack() {
        return trackingCreationStack;
    }

    /**
     * Sets whether the stack trace of the creation of forced chunks must be tracked.
     *
     * @param tracking
     * @see #isTrackingCreationStack()
     */
    public void setTrackingCreationStack(boolean tracking) {
        trackingCreationStack = tracking;
    }

    public static interface ForcedChunkEntry {
        ForcedChunkManager getManager();

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
