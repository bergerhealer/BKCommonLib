package com.bergerkiller.bukkit.common;

import java.util.Iterator;

/**
 * A circular integer is an ever cycling integer value from 0 to size - 1<br>
 * When the last value (size) is reached, it resets to 0<br>
 * Iterating a Circular Integer will result in an infinite loop, as the sequence never ends
 */
public class CircularInteger implements Iterable<Integer> {
	private int value;
	private final int size;

	/**
	 * Initializes a new Circular Integer of the given size
	 * 
	 * @param size to loop in
	 */
	public CircularInteger(final int size) {
		this.value = 0;
		this.size = size;
	}

	/**
	 * Gets the next value from this Circular Integer sequence
	 * 
	 * @return Next value
	 */
	public int next() {
		if (this.value == this.size) {
			return (this.value = 0);
		} else {
			return this.value++;
		}
	}

	/**
	 * Gets the previous value from this Circular Integer sequence
	 * 
	 * @return Previous value
	 */
	public int previous() {
		if (this.value == -1) {
			return (this.value = this.size - 1);
		} else {
			return this.value--;
		}
	}

	/**
	 * Gets the Iterator of this Circular Integer<br>
	 * Note that iterating over a Circular Integer never ends
	 */
	@Override
	public Iterator<Integer> iterator() {
		final CircularInteger me = this;
		return new Iterator<Integer>() {
			@Override
			public boolean hasNext() {
				return true;
			}

			@Override
			public Integer next() {
				return me.next();
			}

			@Override
			public void remove() {
				me.previous();
			}
		};
	}
}
