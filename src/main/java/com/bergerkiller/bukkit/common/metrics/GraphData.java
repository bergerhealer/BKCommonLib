package com.bergerkiller.bukkit.common.metrics;

public class GraphData {
	private String name;
	private xPlotter plotter;
	
	public GraphData(String name, xPlotter plotter) {
		this.name = name;
		this.plotter = plotter;
	}
	
	public String getName() {
		return this.name;
	}
	
	public xPlotter getPlotter() {
		return this.plotter;
	}
}
