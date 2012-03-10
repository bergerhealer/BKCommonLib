package com.bergerkiller.bukkit.common;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

class PluginListener implements Listener {
	private final PluginBase plugin;
	public PluginListener(final PluginBase plugin) {
		this.plugin = plugin;
	}
	@SuppressWarnings("unused")
	@EventHandler(priority = EventPriority.MONITOR)
	private void onPluginEnable(final PluginEnableEvent event) {
		this.plugin.updateDependency(event.getPlugin(), true);
	}
	@SuppressWarnings("unused")
	@EventHandler(priority = EventPriority.MONITOR)
	private void onPluginDisable(PluginDisableEvent event) {
		this.plugin.updateDependency(event.getPlugin(), false);
	}
}