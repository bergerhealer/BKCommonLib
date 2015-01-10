package com.bergerkiller.bukkit.common;

import java.util.Iterator;

/**
 * A circular integer is an ever cycling integer value from 0 to size - 1<br>
 * When the last value (size) is reached, it resets to 0<br>
 * Iterating a Circular Integer will result in an infinite loop, as the sequence never ends
 */
public class CircularInteger implements Iterable<Integer> {
	private int value;
	private int size;

	/**
	 * Initializes a new Circular Integer of the given size
	 * 
	 * @param size to loop in
	 */
	public CircularInteger(int size) {
		this.value = 0;
		this.size = size;
	}

	/**
	 * Gets the current size
	 * 
	 * @return current size
	 */
	public int getSize() {
		return this.size;
	}

	/**
	 * Sets the new size
	 * 
	 * @param size to set to
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * Gets the next value from this Circular Integer sequence
	 * 
	 * @return Next value
	 */
	public int next() {
		this.value++;
		if (this.value >= this.size) {
			this.value = 0;
		}
		return this.value;
	}

	/**
	 * Gets the previous value from this Circular Integer sequence
	 * 
	 * @return Previous value
	 */
	public int previous() {
		this.value--;
		if (this.value <= -1) {
			this.value = this.size - 1;
		}
		return this.value;
	}

	/**
	 * Gets the next integer value, and returns true if it is the first value (0)
	 * 
	 * @return True if the next value is 0, False if not
	 */
	public boolean nextBool() {
		return this.next() == 0;
	}

	/**
	 * Gets the Iterator of this Circular Integer<br>
	 * Note that iterating over a Circular Integer never ends
	 */
	@Override
	public Iterator<Integer> iterator() {
		return new Iterator<Integer>() {
			@Override
			public boolean hasNext() {
				return true;
			}

			@Override
			public Integer next() {
				return CircularInteger.this.next();
			}

			@Override
			public void remove() {
				CircularInteger.this.previous();
			}
		};
	}
}
