package com.bergerkiller.bukkit.common;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

@SuppressWarnings("unused")
class PluginListener implements Listener {
	@EventHandler(priority = EventPriority.MONITOR)
	private void onPluginEnable(final PluginEnableEvent event) {
		for (PluginBase pb : PluginBase.plugins) {
			pb.updateDependency(event.getPlugin(), true);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	private void onPluginDisable(PluginDisableEvent event) {
		for (PluginBase pb : PluginBase.plugins) {
			pb.updateDependency(event.getPlugin(), false);
		}
	}
}
