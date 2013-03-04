package com.bergerkiller.bukkit.common.metrics;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.bergerkiller.bukkit.common.utils.CommonUtil;

/**
 * Graph implementation for showing all the plugins that depend on 'me' as a plugin.
 * Checks for both soft and regular depending.
 */
public class MyDependingPluginsGraph extends Graph {

	public MyDependingPluginsGraph() {
		this("Depending on me");
	}

	public MyDependingPluginsGraph(final String name) {
		super(name);
	}

	@Override
	protected void onUpdate(Plugin plugin) {
		clearPlotters();
		synchronized (Bukkit.getPluginManager()) {
			for (Plugin otherPlugin : CommonUtil.getPluginsUnsafe()) {
				if (!otherPlugin.isEnabled()) {
					continue;
				}
				if (!CommonUtil.isDepending(otherPlugin, plugin) && !CommonUtil.isSoftDepending(otherPlugin, plugin)) {
					continue;
				}
				togglePlotter(otherPlugin.getName(), true);
			}
		}
	}
}
