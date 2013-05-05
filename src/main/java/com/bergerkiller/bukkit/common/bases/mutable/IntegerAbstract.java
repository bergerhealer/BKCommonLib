package com.bergerkiller.bukkit.common.bases.mutable;

import com.bergerkiller.bukkit.common.utils.MathUtil;

public abstract class IntegerAbstract {
	/**
	 * Gets the value
	 * 
	 * @return value
	 */
	public abstract int get();

	/**
	 * Sets the value
	 * 
	 * @param value to set to
	 * @return this instance
	 */
	public abstract IntegerAbstract set(int value);

	public int squared() {
		final int value = get();
		return value * value;
	}

	public int abs() {
		return Math.abs(get());
	}

	/**
	 * Sets the value to 0.0
	 * 
	 * @return this instance
	 */
	public IntegerAbstract setZero() {
		return set(0);
	}

	public int chunk() {
		return MathUtil.toChunk(get());
	}

	public IntegerAbstract clamp(int limit) {
		return set(getClamped(limit));
	}

	public IntegerAbstract clamp(int min, int max) {
		return set(getClamped(min, max));
	}

	public int getClamped(int limit) {
		return MathUtil.clamp(get(), limit);
	}

	public int getClamped(int min, int max) {
		return MathUtil.clamp(get(), min, max);
	}

	public IntegerAbstract add(int value) {
		return set(get() + value);
	}

	public IntegerAbstract subtract(int value) {
		return set(get() - value);
	}

	public IntegerAbstract multiply(int value) {
		return set(get() * value);
	}

	public IntegerAbstract divide(int value) {
		return set(get() / value);
	}

	public boolean equals(int value) {
		return get() == value;
	}

	@Override
	public boolean equals(Object value) {
		if (value instanceof Number) {
			return equals(((Number) value).intValue());
		} else if (value instanceof DoubleAbstract) {
			return equals(((IntegerAbstract) value).get());
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return Integer.toString(get());
	}
}
