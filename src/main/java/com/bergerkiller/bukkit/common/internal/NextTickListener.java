package com.bergerkiller.bukkit.common.internal;

/**
 * A listener to keep track of next-tick task execution.
 * It is mainly intended for performance measuring.
 */
public interface NextTickListener {

	/**
	 * Called when a runnable has just finished it's next-tick operation.
	 * 
	 * @param runnable that was ticked
	 * @param executionTime of the runnable
	 */
	void onNextTicked(Runnable runnable, long executionTime);
}
