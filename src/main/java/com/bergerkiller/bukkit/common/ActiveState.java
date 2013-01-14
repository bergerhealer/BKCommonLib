package com.bergerkiller.bukkit.common;

import java.util.LinkedList;

/**
 * Contains a value which can be used to temporarily change states<br>
 * For example, setting it to a value, and after you are done, setting it back to the old value
 */
public class ActiveState<T> {
	private T state;
	private LinkedList<T> oldStates = new LinkedList<T>();

	/**
	 * Initialized a new Active State
	 * 
	 * @param state value
	 */
	public ActiveState(T state) {
		this.state = state;
	}

	/**
	 * Sets the new state, pushing the old value back
	 * 
	 * @param newState to set to
	 */
	public void next(T newState) {
		oldStates.addLast(this.state);
		this.state = newState;
	}

	/**
	 * Restores the previous state
	 */
	public void previous() {
		if (!oldStates.isEmpty()) {
			this.state = oldStates.pollLast();
		}
	}

	/**
	 * Clears all previous states and sets a new state
	 * 
	 * @param newState to set to
	 */
	public void reset(T newState) {
		this.state = newState;
		this.oldStates.clear();
	}

	/**
	 * Gets the current state
	 * 
	 * @return current state (never null)
	 */
	public T get() {
		return this.state;
	}
}
