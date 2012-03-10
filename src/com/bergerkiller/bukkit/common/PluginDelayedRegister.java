package com.bergerkiller.bukkit.common;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

class PluginDelayedRegister extends Task {

	private Plugin plugin;
	private Listener listener;
	public PluginDelayedRegister(Plugin plugin, Listener listener) {
		this.plugin = plugin;
		this.listener = listener;
	}
	
	@Override
	public void run() {
		Bukkit.getPluginManager().registerEvents(listener, plugin);
	}

}
