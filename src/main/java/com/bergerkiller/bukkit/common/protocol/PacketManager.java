package com.bergerkiller.bukkit.common.protocol;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.bergerkiller.bukkit.common.internal.CommonPlugin;

public class PacketManager {
	private CommonPlugin plugin;
	public static PacketManager instance;
	public boolean libaryInstalled = false;
	
	public PacketManager(CommonPlugin plugin) {
		this.plugin = plugin;
		instance = this;
	}
	
	public void enable() {
		PluginManager pm = plugin.getServer().getPluginManager();
		Plugin lib = pm.getPlugin("ProtocolLib");
		
		if(lib != null) {
			libaryInstalled = true;
			ProtocolLib.enable(plugin);
		}
		
		pm.registerEvents(new ProtocolListener(), plugin);
	}
}
