package com.bergerkiller.bukkit.common.internal;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

/**
 * Timing information is reported back to instances of this interface type when registered.
 */
public interface TimingsListener {
	/**
	 * Called after a runnable has just finished it's next-tick operation.
	 * 
	 * @param runnable that was ticked
	 * @param executionTime of the runnable (nanoseconds)
	 */
	void onNextTicked(Runnable runnable, long executionTime);

	/**
	 * Called after a chunk is loaded from file for a world
	 * 
	 * @param chunk that was loaded
	 * @param executionTime of the loading operation (nanoseconds)
	 */
	void onChunkLoad(Chunk chunk, long executionTime);

	/**
	 * Called after a chunk is generated (excludes populator execution time)
	 * 
	 * @param chunk that was generated
	 * @param executionTime of the generation operation (nanoseconds)
	 */
	void onChunkGenerate(Chunk chunk, long executionTime);

	/**
	 * Called after a world finished a single chunk unloading batch operation
	 * 
	 * @param world that unloaded some chunks
	 * @param executionTime of the unloading operation (nanoseconds)
	 */
	void onChunkUnloading(World world, long executionTime);

	/**
	 * Called after a block populator finished operating on a chunk
	 * 
	 * @param chunk that was populated
	 * @param populator that populated
	 * @param executionTime of the population operation (nanoseconds)
	 */
	void onChunkPopulate(Chunk chunk, BlockPopulator populator, long executionTime);
}
