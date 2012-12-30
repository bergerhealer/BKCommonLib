package com.bergerkiller.bukkit.common.metrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.plugin.Plugin;

public class AddonHandler {
	private Plugin plugin;
	public AddonHandler(Plugin i) { plugin = i; }
	private Metrics metrics;
	public List<cPlotter> plotters = new ArrayList<cPlotter>();
	public HashMap<String, xPlotter> graphs = new HashMap<String, xPlotter>();
	
	public void addPlotter(cPlotter plotter) {
		metrics.addCustomData(plotter);
		plotters.add(plotter);
	}
	
	public Metrics.Graph addGraph(String name, xPlotter data) {
		Metrics.Graph graph = metrics.createGraph(name);
		graph.addPlotter(data);
		graphs.put(name, data);
		return graph;
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
