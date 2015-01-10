package com.bergerkiller.bukkit.common.bases.mutable;

import com.bergerkiller.bukkit.common.utils.MathUtil;

public abstract class DoubleAbstract {
	/**
	 * Gets the value
	 * 
	 * @return value
	 */
	public abstract double get();

	/**
	 * Sets the value
	 * 
	 * @param value to set to
	 * @return this instance
	 */
	public abstract DoubleAbstract set(double value);

	public double squared() {
		final double value = get();
		return value * value;
	}

	public double abs() {
		return Math.abs(get());
	}

	/**
	 * Gets the block coordinate of this value (floor)
	 * 
	 * @return Block coordinate
	 */
	public int block() {
		return getFloor();
	}

	/**
	 * Sets the value to 0.0
	 * 
	 * @return this instance
	 */
	public DoubleAbstract setZero() {
		return set(0.0);
	}

	public int getFloor() {
		return MathUtil.floor(get());
	}

	public int chunk() {
		return MathUtil.toChunk(get());
	}

	public DoubleAbstract clamp(double limit) {
		return set(getClamped(limit));
	}

	public DoubleAbstract clamp(double min, double max) {
		return set(getClamped(min, max));
	}

	public double getClamped(double limit) {
		return MathUtil.clamp(get(), limit);
	}

	public double getClamped(double min, double max) {
		return MathUtil.clamp(get(), min, max);
	}

	public DoubleAbstract add(double value) {
		return set(get() + value);
	}

	public DoubleAbstract subtract(double value) {
		return set(get() - value);
	}

	public DoubleAbstract multiply(double value) {
		return set(get() * value);
	}

	public DoubleAbstract divide(double value) {
		return set(get() / value);
	}

	public DoubleAbstract fixNaN() {
		return set(MathUtil.fixNaN(get()));
	}

	public DoubleAbstract fixNaN(double def) {
		return set(MathUtil.fixNaN(get(), def));
	}

	public boolean equals(double value) {
		return get() == value;
	}

	@Override
	public boolean equals(Object value) {
		if (value instanceof Number) {
			return equals(((Number) value).doubleValue());
		} else if (value instanceof DoubleAbstract) {
			return equals(((DoubleAbstract) value).get());
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return Double.toString(get());
	}
}
