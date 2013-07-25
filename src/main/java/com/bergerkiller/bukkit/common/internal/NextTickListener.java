package com.bergerkiller.bukkit.common.internal;

/**
 * @deprecated Use {@link com.bergerkiller.bukkit.common.internal.TimingsListener} instead
 * 
 * A listener to keep track of next-tick task execution.
 * It is mainly intended for performance measuring.<br><br>
 */
@Deprecated
public interface NextTickListener {

	/**
	 * Called when a runnable has just finished it's next-tick operation.
	 * 
	 * @param runnable that was ticked
	 * @param executionTime of the runnable
	 */
	void onNextTicked(Runnable runnable, long executionTime);
}
