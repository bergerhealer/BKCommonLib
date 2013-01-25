package com.bergerkiller.bukkit.common.metrics;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.plugin.Plugin;

public class AddonHandler {
	private Plugin plugin;
	public AddonHandler(Plugin i) { plugin = i; }
	private Metrics metrics = null;
	public List<GraphData> graphs = new ArrayList<GraphData>();
	
	public Metrics.Graph addGraph(String name, xPlotter data) {
		if(metrics == null)
			this.startMetrics();
		Metrics.Graph graph = metrics.createGraph(name);
		graph.addPlotter(data);
		graphs.add(new GraphData(name, data));
		return graph;
	}
	
	public Metrics.Graph addGraphs(String name, List<xPlotter> plotters) {
		if(metrics == null)
			this.startMetrics();
		Metrics.Graph graph = metrics.createGraph(name);
		for(xPlotter data : plotters) {
			graph.addPlotter(data);
		}
		return graph;
	}
	
	public List<GraphData> getGraphs() {
		return this.graphs;
	}
	
	public void startMetrics() {
		try {
			metrics = new Metrics(plugin);
			metrics.start();
		} catch (Exception e) {
			String name = plugin.getDescription().getName();
			plugin.getLogger().info("["+name+"] Could not load Metrics");
		}
	}
}
