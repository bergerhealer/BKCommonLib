package com.bergerkiller.bukkit.common.server;

public class SpigotServer extends CraftBukkitServer {

	@Override
	public boolean init() {
		if (!super.init()) {
			return false;
		}
		// Check that the Spigot install is available
		try {
			Class.forName(CB_ROOT_VERSIONED + ".Spigot");
			return true;
		} catch (ClassNotFoundException ex) {
			return false;
		}
	}

	@Override
	public String getServerName() {
		return "Spigot";
	}
}
