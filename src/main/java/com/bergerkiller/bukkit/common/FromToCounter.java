package com.bergerkiller.bukkit.common;

import java.util.NoSuchElementException;

/**
 * A simple Integer iterator that counts from one value to the other.
 * Allows both positive and negative iteration.
 */
public class FromToCounter {
	private int start, end;
	private int i, incr;

	/**
	 * Constructs a new empty FromToCounter, this one never returns any value
	 * unless reset is called specifying a valid start and end value.
	 */
	public FromToCounter() {
		this.disable();
	}

	/**
	 * Constructs a new FromToCounter counting from the start to the end specified
	 * 
	 * @param start value, inclusive
	 * @param end value, inclusive
	 */
	public FromToCounter(int start, int end) {
		reset(start, end);
	}

	/**
	 * Checks whether a next element is available
	 * 
	 * @return True if a next element is available, False if not
	 */
	public boolean hasNext() {
		return i != (end + incr);
	}

	/**
	 * Gets the next int value
	 * 
	 * @return next int value
	 * @throws NoSuchElementException if no next element is available
	 */
	public int next() {
		if (!hasNext()) {
			throw new NoSuchElementException("No next elements is available");
		}
		final int value = i;
		i += incr;
		return value;
	}

	/**
	 * Gets the last element returned by {@link #next()}
	 * 
	 * @return last element
	 * @throws NoSuchElementException if next was not called prior
	 */
	public int get() {
		if (i == start) {
			throw new NoSuchElementException("A call to next() is required before this can be used");
		}
		return i - incr;
	}

	/**
	 * Disables this counter, causing {@link #hasNext()} to always return false.
	 * Any subsequent calls to {@link #next()} or {@link #get()} will fail.
	 */
	public void disable() {
		this.incr = this.i = this.start = this.end = 0;
	}

	/**
	 * Resets this counter to start counting from the new start to end
	 * 
	 * @param start value, inclusive
	 * @param end value, inclusive
	 */
	public void reset(int start, int end) {
		this.start = start;
		this.end = end;
		this.incr = (end >= start) ? 1 : -1;
		reset();
	}

	/**
	 * Resets this counter to start counting again from the original start and end values
	 */
	public void reset() {
		this.i = this.start;
	}
}
