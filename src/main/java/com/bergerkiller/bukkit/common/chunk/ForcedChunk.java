package com.bergerkiller.bukkit.common.chunk;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import org.bukkit.World;

import com.bergerkiller.bukkit.common.internal.CommonPlugin;

/**
 * A single forced chunk that is kept loaded for as long as this object exists,
 * until {@link #close()} is called.
 */
public class ForcedChunk implements AutoCloseable, Cloneable {
    private final AtomicReference<ForcedChunkManager.ForcedChunkEntry> entry;

    protected ForcedChunk(ForcedChunkManager.ForcedChunkEntry entry) {
        this.entry = new AtomicReference<ForcedChunkManager.ForcedChunkEntry>(entry);
    }

    /**
     * Gets whether this Forced Chunk is none, that is, has no chunk assigned
     * 
     * @return True if this Forced Chunk refers to no chunk
     */
    public boolean isNone() {
        return this.entry.get() == null;
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
     * Forces a chunk to stay loaded. Call {@link ForcedChunk#close()} to release
     * the chunk again to allow it to unload. The chunk is loaded asynchronously
     * if it is not already loaded.
     * Loads with a radius of 2, so that entities inside are ticked.
     * 
     * @param world
     * @param chunkX
     * @param chunkZ
     * @return forced chunk
     */
    public static ForcedChunk load(World world, int chunkX, int chunkZ) {
        return CommonPlugin.getInstance().getForcedChunkManager().newForcedChunk(world, chunkX, chunkZ);
    }

    /**
     * Forces a chunk to stay loaded. Call {@link ForcedChunk#close()} to release
     * the chunk again to allow it to unload. If the provided chunk is currently not
     * actually loaded, it is loaded asynchronously.
     * Loads with a radius of 2, so that entities inside are ticked.
     * 
     * @param chunk
     * @return forced chunk
     */
    public static ForcedChunk load(org.bukkit.Chunk chunk) {
        return load(chunk.getWorld(), chunk.getX(), chunk.getZ());
    }

    /**
     * Forces a chunk to stay loaded. Call {@link ForcedChunk#close()} to release
     * the chunk again to allow it to unload. The chunk is loaded asynchronously
     * if it is not already loaded.
     * 
     * @param world
     * @param chunkX
     * @param chunkZ
     * @param radius Number of chunks around the chunk to keep loaded.
     *               Radius 2 or higher will make entities inside the chunk get ticked.
     * @return forced chunk
     */
    public static ForcedChunk load(World world, int chunkX, int chunkZ, int radius) {
        return CommonPlugin.getInstance().getForcedChunkManager().newForcedChunk(world, chunkX, chunkZ, radius);
    }

    /**
     * Forces a chunk to stay loaded. Call {@link ForcedChunk#close()} to release
     * the chunk again to allow it to unload. If the provided chunk is currently not
     * actually loaded, it is loaded asynchronously.
     * 
     * @param chunk
     * @param radius Number of chunks around the chunk to keep loaded.
     *               Radius 2 or higher will make entities inside the chunk get ticked.
     * @return forced chunk
     */
    public static ForcedChunk load(org.bukkit.Chunk chunk, int radius) {
        return load(chunk.getWorld(), chunk.getX(), chunk.getZ(), radius);
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
     * Gets the number of chunks around the chunk kept loaded.
     * If 2 or higher, then entities inside the chunk will be ticked.
     * Throws an IllegalStateException if this forced chunk was closed.
     *
     * @return chunk load radius
     */
    public int getRadius() {
        return this.access().getRadius();
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
        return this.access().getChunk();
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

    /**
     * Clones this ForcedChunk. This causes the returned forced chunk instance to also
     * keep the chunk loaded, and if this forced chunk is closed, the chunk will not unload.
     * The returned forced chunk should be closed when no longer used.
     */
    @Override
    public ForcedChunk clone() {
        ForcedChunkManager.ForcedChunkEntry entry = this.entry.get();
        if (entry != null) {
            entry.add();
        }
        return new ForcedChunk(entry);
    }

    // Just a safeguard in case the plugin never calls close() itself to prevent memory leaks
    // Even then it may never be called if the server has sufficient memory and GC never runs
    // This should not be relied upon.
    @Override
    @SuppressWarnings("deprecation")
    public void finalize() throws Throwable {
        this.close();
        super.finalize();
    }
}
