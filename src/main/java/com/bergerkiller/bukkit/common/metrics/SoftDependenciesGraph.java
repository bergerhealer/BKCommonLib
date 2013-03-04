package com.bergerkiller.bukkit.common.metrics;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.bergerkiller.bukkit.common.utils.LogicUtil;

/**
 * Graph implementation for showing the enabled soft dependencies of a plugin.
 * There is no need to include normal dependencies - the plugin wouldn't be running without them.
 */
public class SoftDependenciesGraph extends Graph {

	public SoftDependenciesGraph() {
		this("Soft Dependencies");
	}

	public SoftDependenciesGraph(final String name) {
		super(name);
	}

	@Override
	protected void onUpdate(Plugin plugin) {
		List<String> dependencies = plugin.getDescription().getSoftDepend();
		if (LogicUtil.nullOrEmpty(dependencies)) {
			clearPlotters();
		} else {
			for (String depend : dependencies) {
				togglePlotter(depend, Bukkit.getPluginManager().isPluginEnabled(depend));
			}
		}
	}
}
