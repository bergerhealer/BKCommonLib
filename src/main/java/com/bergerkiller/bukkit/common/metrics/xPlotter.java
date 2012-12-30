package com.bergerkiller.bukkit.common.metrics;

public class xPlotter extends Metrics.Plotter {
	
	public xPlotter(String name) {
		super(name);
	}
	
    @Override
    public int getValue() {
        return 1;
    }
}
