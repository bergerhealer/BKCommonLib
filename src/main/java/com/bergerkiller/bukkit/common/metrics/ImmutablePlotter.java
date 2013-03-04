package com.bergerkiller.bukkit.common.metrics;

/**
 * A metrics plotter for plotting a single value that never changes
 */
public class ImmutablePlotter extends Plotter {
	private final int value;

	public ImmutablePlotter(String name, int value) {
		super(name);
		this.value = value;
	}

	@Override
	public int getValue() {
		return value;
	}
}
