package com.bergerkiller.bukkit.common.chunk;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import org.bukkit.World;

/**
 * A single forced chunk that is kept loaded for as long as this object exists,
 * until {@link #close()} is called.
 */
public class ForcedChunk implements AutoCloseable {
    private final AtomicReference<ForcedChunkManager.ForcedChunkEntry> entry;

    protected ForcedChunk(ForcedChunkManager.ForcedChunkEntry entry) {
        this.entry = new AtomicReference<ForcedChunkManager.ForcedChunkEntry>(entry);
    }

    /**
     * Forced Chunk referring to no chunk at all. Can be used with {@link #move(ForcedChunk)}
     * to start loading at a later time
     * 
     * @return forced chunk
     */
    public static ForcedChunk none() {
        return new ForcedChunk(null);
    }

    /**
     * Stores the ForcedChunk specified in this one. The original forced
     * chunk stored is closed. The input forced chunk will have the closed state.
     * 
     * @param other to move into this Forced Chunk
     */
    public void move(ForcedChunk other) {
        ForcedChunkManager.ForcedChunkEntry old_entry = entry.getAndSet(other.entry.getAndSet(null));
        if (old_entry != null) {
            old_entry.remove();
        }
    }

    /**
     * Gets the World of this forced chunk.
     * Throws an IllegalStateException if this forced chunk was closed.
     * 
     * @return world
     */
    public World getWorld() {
        return this.access().getWorld();
    }

    /**
     * Gets the X-coordinate of this forced chunk.
     * Throws an IllegalStateException if this forced chunk was closed.
     * 
     * @return chunk X-coordinate
     */
    public int getX() {
        return this.access().getX();
    }

    /**
     * Gets the Z-coordinate of this forced chunk.
     * Throws an IllegalStateException if this forced chunk was closed.
     * 
     * @return chunk Z-coordinate
     */
    public int getZ() {
        return this.access().getZ();
    }

    /**
     * Gets the chunk kept loaded by this Forced Chunk.
     * Throws an IllegalStateException if this forced chunk was closed.
     * 
     * @return chunk
     */
    public org.bukkit.Chunk getChunk() {
        ForcedChunkManager.ForcedChunkEntry entry = this.access();
        return entry.getWorld().getChunkAt(entry.getX(), entry.getZ());
    }

    /**
     * Gets a future resolved once this forced chunk is loaded asynchronously.
     * The moment this Forced Chunk was created, a load was scheduled.
     * 
     * @return future for the chunk
     */
    public CompletableFuture<org.bukkit.Chunk> getChunkAsync() {
        return this.access().getChunkAsync();
    }

    private ForcedChunkManager.ForcedChunkEntry access() {
        ForcedChunkManager.ForcedChunkEntry entry = this.entry.get();
        if (entry == null) {
            throw new IllegalStateException("ForcedChunk was closed");
        } else {
            return entry;
        }
    }

    /**
     * Closes this forced chunk, making it no longer keep the chunk loaded,
     * unless another Forced Chunk exists that still does. This method can
     * safely be called multiple times.
     */
    @Override
    public void close() {
        ForcedChunkManager.ForcedChunkEntry entry = this.entry.getAndSet(null);
        if (entry != null) {
            entry.remove();
        }
    }

    // Just a safeguard in case the plugin never calls close() itself to prevent memory leaks
    // Even then it may never be called if the server has sufficient memory and GC never runs
    // This should not be relied upon.
    @Override
    public void finalize() throws Throwable {
        this.close();
        super.finalize();
    }
}
