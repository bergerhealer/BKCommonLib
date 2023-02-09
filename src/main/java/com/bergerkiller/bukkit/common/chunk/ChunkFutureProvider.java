package com.bergerkiller.bukkit.common.chunk;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;

/**
 * Translates event listeners into completable futures for various stages that chunks
 * can be in. This class can be used to get notified when a given chunk loads or unloads
 * in the future, or when entities of a chunk are loaded or unloaded.<br>
 * <br>
 * The futures can be cancelled by the caller, upon which they are automatically cleaned up
 * inside this provider. This mechanism also allows conditional futures where, for example,
 * the unloading of one chunk can cancel the future for a neighbour.
 */
public interface ChunkFutureProvider {

    /**
     * Gets or initializes the ChunkFutureProvider to be registered and used by the specified
     * plugin. The returned handler cannot be used asynchronously or unspecified behavior
     * will occur, for that, use {@link #ofThreadSafe(Plugin)}. Only use this on the main thread.<br>
     * <br>
     * All futures will always be completed on the main thread.
     *
     * @param plugin
     * @return chunk future provider suitable for the main thread
     */
    public static ChunkFutureProvider of(Plugin plugin) {
        return ChunkFutureProviderImpl.MainThreadHandler.handlers.computeIfAbsent(plugin, p -> {
            ChunkFutureProviderImpl.MainThreadHandler provider = new ChunkFutureProviderImpl.MainThreadHandler(p);
            Bukkit.getPluginManager().registerEvents(provider, p);
            return provider;
        });
    }

    /**
     * Gets or initializes a multi-thread-safe ChunkFutureProvider to be registered and used
     * by the specified plugin. The returned handler can be used to asynchronously be notified
     * of chunk state changes. This may have a very slight performance overhead.<br>
     * <br>
     * All futures will always be completed on the main thread. However, creating futures
     * can be done on other threads safely.
     *
     * @param plugin
     * @return chunk future provider suitable for cross-thread use
     */
    public static ChunkFutureProvider ofThreadSafe(Plugin plugin) {
        synchronized (ChunkFutureProviderImpl.ThreadSafeHandler.class) {
            return ChunkFutureProviderImpl.ThreadSafeHandler.handlers.computeIfAbsent(plugin, p -> {
                ChunkFutureProviderImpl.ThreadSafeHandler provider = new ChunkFutureProviderImpl.ThreadSafeHandler(p);
                Bukkit.getPluginManager().registerEvents(provider, p);
                return provider;
            });
        }
    }

    /**
     * Gets a special type of future provider that does not actually wait for the future to come,
     * but sync-loads the chunks when needed. Only {@link #whenLoaded(Chunk)} is functional.
     * 
     * @return chunk future provider that sync-loads chunks
     */
    public static ChunkFutureProvider ofSyncLoad() {
        return ChunkFutureProviderImpl.SyncLoadHandler.INSTANCE;
    }

    /**
     * Returns a completable future completed once a chunk is loaded. If the chunk is already
     * loaded, an already-completed future is returned.
     *
     * @param world World of the chunk
     * @param chunkX X-coordinate of the chunk
     * @param chunkZ Z-coordinate of the chunk
     * @return Future completed when the chunk loads
     */
    CompletableFuture<Chunk> whenLoaded(World world, int chunkX, int chunkZ);

    /**
     * Returns a completable future completed once a chunk is loaded. If the chunk is already
     * loaded, an already-completed future is returned.
     *
     * @param chunk The chunk
     * @return Future completed when the chunk loads
     */
    default CompletableFuture<Chunk> whenLoaded(Chunk chunk) {
        return whenLoaded(chunk.getWorld(), chunk.getX(), chunk.getZ());
    }

    /**
     * Returns a completable future completed once a chunk is unloaded. If the chunk is already
     * unloaded, an already-completed future is returned.
     *
     * @param world World of the chunk
     * @param chunkX X-coordinate of the chunk
     * @param chunkZ Z-coordinate of the chunk
     * @return Future completed (with null) when the chunk unloads
     */
    CompletableFuture<Void> whenUnloaded(World world, int chunkX, int chunkZ);

    /**
     * Returns a completable future completed once a chunk is unloaded. If the chunk is already
     * unloaded, an already-completed future is returned.
     *
     * @param chunk The chunk
     * @return Future completed (with null) when the chunk unloads
     */
    default CompletableFuture<Void> whenUnloaded(Chunk chunk) {
        return whenUnloaded(chunk.getWorld(), chunk.getX(), chunk.getZ());
    }

    /**
     * Tracks the loaded or unloaded state of a chunk. Once the listener is registered and
     * the initial state of the chunk is known,
     * {@link ChunkStateListener#onRegistered(ChunkStateTracker) onRegistered} is called.<br>
     * <br>
     * When later on the chunk loads or unloads, then onLoaded and onUnloaded are called.
     * The method returns a tracker object that is also sent to the callbacks, with a
     * {@link ChunkStateTracker#cancel() cancel()} method to stop tracking the chunk at
     * any time.<br>
     * <br>
     * When tracking is stopped using cancel(), then onCancelled() is called on the listener
     * after which no more callbacks are called.<br>
     * <br>
     * All callbacks are always called on the main thread, guaranteed. Even if this method
     * to register a tracker is called from another thread (and threaded provider is used).
     *
     * @param world World the chunk is in
     * @param chunkX X-coordinate of the Chunk
     * @param chunkZ Z-coordinate of the Chunk
     * @param listener Listener that will receive the load/unload updates
     * @return State tracker object with a cancel() function to stop tracking again
     */
    ChunkStateTracker trackLoaded(World world, int chunkX, int chunkZ, ChunkStateListener listener);

    /**
     * Tracks the loaded or unloaded state of a chunk. Once the listener is registered and
     * the initial state of the chunk is known,
     * {@link ChunkStateListener#onRegistered(ChunkStateTracker) onRegistered} is called.<br>
     * <br>
     * When later on the chunk loads or unloads, then onLoaded and onUnloaded are called.
     * This method returns a tracker object that is also sent to the callbacks, with a
     * {@link ChunkStateTracker#cancel() cancel()} method to stop tracking the chunk at
     * any time.<br>
     * <br>
     * When tracking is stopped using cancel(), then onCancelled() is called on the listener
     * after which no more callbacks are called.<br>
     * <br>
     * All callbacks are always called on the main thread, guaranteed. Even if this method
     * to register a tracker is called from another thread (and threaded provider is used).
     *
     * @param chunk The chunk to track
     * @param listener Listener that will receive the load/unload updates
     * @return State tracker object with a cancel() function to stop tracking again
     */
    default ChunkStateTracker trackLoaded(Chunk chunk, ChunkStateListener listener) {
        return trackLoaded(chunk.getWorld(), chunk.getX(), chunk.getZ(), listener);
    }

    /**
     * Tracks the loaded state of a group of neighbouring chunks nearby or relating a main
     * chunk. So long the main chunk stays loaded, the listener is notified whether all the
     * neighbouring chunks are loaded or not.<br>
     * <br>
     * Once the listener is registered and the initial state of the chunk's neighbours is known,
     * {@link ChunkStateListener#onRegistered(ChunkStateTracker) onRegistered} is called.
     * When later on neighbouring chunks load or unload, then onLoaded and onUnloaded are called
     * as the state of the cluster changes. When the main chunk by which this tracker was
     * registered unloads, then the tracker is cancelled and onCancelled() is called.
     * If at the time the neighbours were loaded, then onUnloaded() is called first.<br>
     * <br>
     * This method returns a tracker object that is also sent to the callbacks, with a
     * {@link ChunkStateTracker#cancel() cancel()} method to stop tracking the chunk at
     * any time.
     * When tracking is stopped using cancel(), then onCancelled() is called on the listener
     * after which no more callbacks are called.<br>
     * <br>
     * All callbacks are always called on the main thread, guaranteed. Even if this method
     * to register a tracker is called from another thread (and threaded provider is used).
     *
     * @param mainChunk Main chunk
     * @param neighbours Neighbour list with all the neighbouring chunks that must be loaded
     * @param listener Listener that will receive the load/unload updates
     * @return State tracker object with a cancel() function to stop tracking again
     */
    ChunkStateTracker trackNeighboursLoaded(Chunk mainChunk, ChunkNeighbourList neighbours, ChunkStateListener listener);

    /**
     * Returns a completable future completed once the entities of a chunk are loaded.
     * If the chunk's entities are already loaded, an already-completed future is returned.
     *
     * @param world World of the chunk
     * @param chunkX X-coordinate of the chunk
     * @param chunkZ Z-coordinate of the chunk
     * @return Future completed when the entities of a chunk are loaded in
     */
    CompletableFuture<Chunk> whenEntitiesLoaded(World world, int chunkX, int chunkZ);

    /**
     * Returns a completable future completed once the entities of a chunk are loaded.
     * If the chunk's entities are already loaded, an already-completed future is returned.
     *
     * @param chunk The chunk
     * @return Future completed when the entities of a chunk are loaded in
     */
    default CompletableFuture<Chunk> whenEntitiesLoaded(Chunk chunk) {
        return whenEntitiesLoaded(chunk.getWorld(), chunk.getX(), chunk.getZ());
    }

    /**
     * Returns a completable future completed once the entities of a chunk unload.
     * If the chunk isn't loaded, or the entities of the chunk aren't currently loaded,
     * then an already-completed future is returned.
     *
     * @param world World of the chunk
     * @param chunkX X-coordinate of the chunk
     * @param chunkZ Z-coordinate of the chunk
     * @return Future completed (with null) when the entities of a chunk unload
     */
    CompletableFuture<Void> whenEntitiesUnloaded(World world, int chunkX, int chunkZ);

    /**
     * Returns a completable future completed once the entities of a chunk unload.
     * If the chunk isn't loaded, or the entities of the chunk aren't currently loaded,
     * then an already-completed future is returned.
     *
     * @param chunk The chunk
     * @return Future completed (with null) when the entities of a chunk unload
     */
    default CompletableFuture<Void> whenEntitiesUnloaded(Chunk chunk) {
        return whenEntitiesUnloaded(chunk.getWorld(), chunk.getX(), chunk.getZ());
    }

    /**
     * Returns a completable future completed when a chunk on the world loads while
     * another chunk is also loaded. This is useful when needing to access neighbouring chunk
     * data while handling the load logic of a chunk. This assumes the mainChunk
     * is already loaded.
     *
     * @param mainChunk Main chunk making the request, must stay loaded for the future to stay valid
     * @param neighbourChunkX X-coordinate of the chunk that must be loaded
     * @param neighbourChunkZ Z-coordinate of the chunk that must be loaded
     * @return Future completed when the neighbouring chunk is loaded, or cancelled when the
     *         mainChunk unloads.
     */
    default CompletableFuture<Chunk> whenNeighbourLoaded(Chunk mainChunk, int neighbourChunkX, int neighbourChunkZ) {
        if (neighbourChunkX == mainChunk.getX() && neighbourChunkZ == mainChunk.getZ()) {
            return CompletableFuture.completedFuture(mainChunk);
        }

        CompletableFuture<Chunk> neighbourFuture = whenLoaded(mainChunk.getWorld(), neighbourChunkX, neighbourChunkZ);
        if (neighbourFuture.isDone()) {
            // Skip all the extra asynchronous boilerplate
            return neighbourFuture;
        } else {
            // Cancel this operation when the chunk making the request unloads
            // If the neighbour future is cancelled (by caller), also cancel the
            // future on the main chunk.
            CompletableFuture<Void> mainFuture = whenUnloaded(mainChunk);
            if (mainFuture.isDone()) {
                neighbourFuture.cancel(false);
            } else {
                mainFuture.thenAccept(u -> neighbourFuture.cancel(false));
                neighbourFuture.exceptionally(t -> {
                    if (t instanceof CompletionException) {
                        mainFuture.cancel(false);
                    }
                    return null;
                });
            }
            return neighbourFuture;
        }
    }

    /**
     * Similar to {@link #whenNeighbourLoaded(Chunk, int, int)}: will wait for
     * the chunk of another block to be loaded while the chunk of a main block
     * remains loaded.
     *
     * @param mainChunk Chunk that must remain loaded or the future is cancelled
     * @param block Block whose chunk to wait to be loaded, and to then read BlockData of
     * @return Future for the BlockData of the block.
     */
    default CompletableFuture<BlockData> readNeighbourBlockData(Chunk mainChunk, Block block) {
        int neighChunkX = MathUtil.toChunk(block.getX());
        int neighChunkZ = MathUtil.toChunk(block.getZ());
        if (mainChunk.getX() == neighChunkX && mainChunk.getZ() == neighChunkZ) {
            return CompletableFuture.completedFuture(WorldUtil.getBlockData(block));
        } else {
            return whenNeighbourLoaded(mainChunk, neighChunkX, neighChunkZ).thenApply(c -> {
                return WorldUtil.getBlockData(block);
            });
        }
    }

    /**
     * Returns a completable future completed once the main chunk and all chunks at the neighbouring chunk
     * coordinates are loaded. If the main chunk unloads, the future is cancelled.
     * It is assumed that the main chunk is currently loaded.
     *
     * @param mainChunk Chunk that must remain loaded or the future is cancelled
     * @param neighbours Tracker list with all the neighbouring chunks that must be loaded
     * @return Completable Future completed once all neighbouring chunks loaded
     */
    CompletableFuture<Chunk> whenAllNeighboursLoaded(Chunk mainChunk, ChunkNeighbourList neighbours);

    /**
     * Tracker object that holds a listener with callbacks called when the chunk
     * loads or unloads. The tracker has a {@link #cancel()} method to de-register
     * the tracker and stop calling the callbacks.<br>
     * <br>
     * Used for both single-chunk and multi-chunk area load tracking.
     */
    public static interface ChunkStateTracker {

        /**
         * Gets the World this tracker is for
         *
         * @return World
         */
        World getWorld();

        /**
         * Gets the X-coordinate of the Chunk that is being tracked
         *
         * @return Chunk X-coordinate
         */
        int getChunkX();

        /**
         * Gets the Z-coordinate of the Chunk that is being tracked
         *
         * @return Chunk Z-coordinate
         */
        int getChunkZ();

        /**
         * Gets the Chunk, if loaded. Otherwise returns null.
         * Will return a valid chunk during the handling of
         * {@link ChunkStateListener#onUnloaded(ChunkStateTracker) onUnloaded()}, but
         * will become null right after.
         *
         * @return Chunk if loaded, null if unloaded
         */
        Chunk getChunk();

        /**
         * Whether the chunk is currently loaded or not, according to the rules
         * by which this tracker was created.
         *
         * @return True if loaded
         */
        boolean isLoaded();

        /**
         * Cancels this tracker and stops calling the callbacks of the listener.
         * Depending on the chunk future provider used to create the tracker, is
         * multi-thread safe.
         */
        void cancel();
    }

    /**
     * Listener for chunk unload and load events for a particular chunk.
     * Used for both tracking a single chunk, as well as the neighbours
     * related to a chunk.
     */
    public static interface ChunkStateListener {

        /**
         * Called once the listener is registered and will be notified
         * of changes. The tracker's {@link ChunkStateTracker#isLoaded() isLoaded()}
         * can be used to know the initial state of the chunk.
         *
         * @param tracker The chunk tracker for the chunk
         */
        void onRegistered(ChunkStateTracker tracker);

        /**
         * Called when the listener stops listening for
         * changes to the chunk. Called when cancel() is called,
         * either by the user or internally.
         *
         * @param tracker The chunk tracker for the chunk
         */
        void onCancelled(ChunkStateTracker tracker);

        /**
         * Called when the chunk is loaded. During this time,
         * {@link ChunkStateTracker#getChunk()} is safe to use.
         *
         * @param tracker The chunk tracker for the chunk
         */
        void onLoaded(ChunkStateTracker tracker);

        /**
         * Called when the chunk is unloaded. During this time,
         * {@link ChunkStateTracker#getChunk()} is safe to use.
         *
         * @param tracker The chunk tracker for the chunk
         */
        void onUnloaded(ChunkStateTracker tracker);
    }

    /**
     * List of tracked chunks that are neighbours of another main chunk
     */
    public static interface ChunkNeighbourList {

        /**
         * Adds the neighbouring chunk
         *
         * @param world
         * @param chunkX
         * @param chunkZ
         */
        void add(World world, int chunkX, int chunkZ);

        /**
         * Adds the neighbouring chunk
         *
         * @param world
         * @param coordinates
         */
        default void add(World world, IntVector2 coordinates) {
            add(world, coordinates.x, coordinates.z);
        }

        /**
         * Creates a new empty ChunkNeighbourList
         *
         * @return neighbours list
         */
        public static ChunkNeighbourList create() {
            return new ChunkFutureProviderImpl.ChunkTrackerListImpl(16);
        }

        /**
         * Creates a new ChunkNeighbourList with all the neighbours of a Chunk.
         * <ul>
         * <li>With radius=0, an empty list is returned
         * <li>With radius=1, the 8 neighbours around the chunk are added
         * <li>With radius=2, the 16 outer neighbours and 8 inner neighbours are added (24)
         * <li>Etc.
         * </ul>
         *
         * @param chunk Main chunk
         * @param radius Radius of chunks around it to add as neighbours
         * @return neighbours list
         */
        public static ChunkNeighbourList neighboursOf(Chunk chunk, int radius) {
            final World world = chunk.getWorld();
            final int chunkX = chunk.getX();
            final int chunkZ = chunk.getZ();

            if (radius == 1) {
                // Optimized un-loop-rolled
                ChunkNeighbourList list = new ChunkFutureProviderImpl.ChunkTrackerListImpl(8);
                list.add(world, chunkX - 1, chunkZ - 1);
                list.add(world, chunkX - 1, chunkZ);
                list.add(world, chunkX - 1, chunkZ + 1);
                list.add(world, chunkX, chunkZ + 1);
                list.add(world, chunkX + 1, chunkZ + 1);
                list.add(world, chunkX + 1, chunkZ);
                list.add(world, chunkX + 1, chunkZ - 1);
                list.add(world, chunkX, chunkZ - 1);
                return list;
            }

            // All other radius values need some loops
            int edge = (1 + 2 * radius);
            ChunkNeighbourList list = new ChunkFutureProviderImpl.ChunkTrackerListImpl((edge*edge) - 1);

            int cx0 = chunkX - radius;
            int cz0 = chunkZ - radius;
            int cx1 = chunkX + radius;
            int cz1 = chunkZ + radius;
            for (int cx = cx0; cx <= cx1; cx++) {
                for (int cz = cz0; cz <= cz1; cz++) {
                    if (cx != chunkX && cz != chunkZ) {
                        list.add(chunk.getWorld(), cx, cz);
                    }
                }
            }

            return list;
        }
    }

    /**
     * Completable Futures are completed exceptionally with this exception when the constraints for the
     * future have stopped to be met. It does not contain a stack trace for improved performance
     * over using {@link CompletableFuture#cancel(boolean)}. Instead of using <i>cancel(true)</i>
     * users can use this exception class to cancel the operation.
     */
    public static class FutureCancelledException extends CompletionException {
        /**
         * Static singleton instance of this exception, to avoid overhead of construction
         */
        public static final FutureCancelledException INSTANCE = new FutureCancelledException();
        private static final long serialVersionUID = 7273969366281170690L;

        @Override
        public Throwable fillInStackTrace() {
            return this;
        }
    }
}
