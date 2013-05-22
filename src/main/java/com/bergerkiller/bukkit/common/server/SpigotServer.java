package com.bergerkiller.bukkit.common.server;

import org.bukkit.Bukkit;

import com.bergerkiller.bukkit.common.Common;

public class SpigotServer extends CraftBukkitServer {

	@Override
	public boolean init() {
		if (!super.init()) {
			return false;
		}
		// Check that the Spigot install is available
		try {
			Class.forName(getClassName(Common.CB_ROOT + ".Spigot"));
			return true;
		} catch (ClassNotFoundException ex) {
			return false;
		}
	}

	@Override
	public String getServerName() {
		return "Spigot (" + Bukkit.getServer().getVersion() + ")";
	}
}
