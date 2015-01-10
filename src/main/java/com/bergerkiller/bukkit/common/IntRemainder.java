package com.bergerkiller.bukkit.common;

import java.util.Arrays;

import com.bergerkiller.bukkit.common.utils.MathUtil;

/**
 * The Int Remainder uses a set amount of different Integer values to represent a single Double value<br>
 * Integer values can be asked from this Int Remainder, the average of all the returned values is the contained Double value<br>
 * This allows to, for example, work with Double rates when only Integer rates were possible
 */
public class IntRemainder {
	private double contained = 0;
	private final CircularInteger counter;
	private final int[] values;

	/**
	 * Initializes a new Int remainder  with an initial value and the amount of decimals specified
	 * 
	 * @param initialvalue to use
	 * @param decimals count for the accuracy of the remainder
	 */
	public IntRemainder(double initialvalue, int decimals) {
		if (decimals < 1) {
			throw new IllegalArgumentException("Decimal count needs to be higher than 0");
		}
		this.values = new int[10 * decimals];
		this.counter = new CircularInteger(this.values.length);
		this.set(initialvalue);
	}

	/**
	 * Sets the value represented by this Int remainder
	 * 
	 * @param value to set to
	 */
	public void set(double value) {
		this.contained = value;
		// set floor
		int floor = MathUtil.floor(value);
		Arrays.fill(this.values, floor);
		// get remainder (1.2 -> .2)
		floor = (int) ((value - floor) * this.values.length);
		for (int i = 0; i < floor; i++) {
			this.values[i]++;
		}
	}

	/**
	 * Gets the next Integer value of this remainder
	 * 
	 * @return Next Integer value
	 */
	public int next() {
		return this.values[this.counter.next()];
	}

	/**
	 * Gets the Double value represented by this Int remainder
	 * 
	 * @return contained value
	 */
	public double get() {
		return this.contained;
	}

	/**
	 * Gets all the Integer values used to make the contained Double value
	 * 
	 * @return Integer values
	 */
	public int[] getValues() {
		return this.values;
	}
}
