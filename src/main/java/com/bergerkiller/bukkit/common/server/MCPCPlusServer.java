package com.bergerkiller.bukkit.common.server;

import org.bukkit.Bukkit;

public class MCPCPlusServer extends SpigotServer {

	@Override
	public boolean init() {
		if (!super.init()) {
			return false;
		}
		//TODO: Detection not done yet
		return false;
	}

	@Override
	public String getServerName() {
		return "MCPC+ (" + Bukkit.getServer().getVersion() + ")";
	}
}
