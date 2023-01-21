package com.bergerkiller.bukkit.common.chunk;

import java.util.concurrent.CompletableFuture;

import org.bukkit.World;

/**
 * Base class for a manager for forced chunks that should
 * make sure chunks stay loaded.
 */
public abstract class ForcedChunkManager {
    private ForcedChunkCreator creator = new ForcedChunkCreatorUntracked(ForcedChunkCleaner.create());

    public final ForcedChunk newNone() {
        return creator.create(null);
    }

    public final ForcedChunk newForcedChunk(World world, int chunkX, int chunkZ) {
        return creator.create(this.add(world, chunkX, chunkZ));
    }

    public final ForcedChunk newForcedChunk(World world, int chunkX, int chunkZ, int radius) {
        return creator.create(this.add(world, chunkX, chunkZ, radius));
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
        return creator.isTrackingCreationStack();
    }

    /**
     * Sets whether the stack trace of the creation of forced chunks must be tracked.
     *
     * @param tracking
     * @see #isTrackingCreationStack()
     */
    public void setTrackingCreationStack(boolean tracking) {
        creator = creator.updateTrackingCreationStack(tracking);
    }

    /**
     * Should be called after the plugin shuts down to clean up any background processes
     */
    protected void shutdown() {
        creator.shutdown();
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

    // Generates and stores stack traces.
    private static class ForcedChunkCreatorTracked implements ForcedChunkCreator {
        private final ForcedChunkCleaner cleaner;

        private ForcedChunkCreatorTracked(ForcedChunkCleaner cleaner) {
            this.cleaner = cleaner;
        }

        @Override
        public boolean isTrackingCreationStack() {
            return true;
        }

        @Override
        public ForcedChunkCreator updateTrackingCreationStack(boolean tracking) {
            return tracking ? this : new ForcedChunkCreatorUntracked(cleaner);
        }

        @Override
        public ForcedChunk create(ForcedChunkEntry entry) {
            return cleaner.createAndTrackStack(entry, new Throwable());
        }

        @Override
        public void shutdown() {
            cleaner.shutdown();
        }
    }

    // Does not generate or store stack traces.
    private static class ForcedChunkCreatorUntracked implements ForcedChunkCreator {
        private final ForcedChunkCleaner cleaner;

        private ForcedChunkCreatorUntracked(ForcedChunkCleaner cleaner) {
            this.cleaner = cleaner;
        }

        @Override
        public boolean isTrackingCreationStack() {
            return false;
        }

        @Override
        public ForcedChunkCreator updateTrackingCreationStack(boolean tracking) {
            return tracking ? new ForcedChunkCreatorTracked(cleaner) : this;
        }

        @Override
        public ForcedChunk create(ForcedChunkEntry entry) {
            return cleaner.createDefault(entry);
        }

        @Override
        public void shutdown() {
            cleaner.shutdown();
        }
    }

    private static interface ForcedChunkCreator {
        void shutdown();
        boolean isTrackingCreationStack();
        ForcedChunkCreator updateTrackingCreationStack(boolean tracking);
        ForcedChunk create(ForcedChunkEntry entry);
    }
}
