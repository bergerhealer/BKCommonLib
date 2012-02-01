package com.bergerkiller.bukkit.common;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class Common extends PluginBase {
	public static boolean isShowcaseEnabled = false;
	public static boolean isSCSEnabled = false;
	public static Plugin bleedingMobsInstance = null;
	
	public void updateDependency(Plugin plugin, String pluginName, boolean enabled) {
		if (pluginName.equals("Showcase")) {
			isShowcaseEnabled = enabled;
		} else if (pluginName.equals("ShowCaseStandalone")) {
			isSCSEnabled = enabled;
		} else if (pluginName.equals("BleedingMobs")) {
			bleedingMobsInstance = enabled ? plugin : null;
		}
	}
	
	public Common() {
		super(1818, 3000);
	}
	
	public void disable() {}
	public void enable() {}

	@Override
	public boolean command(CommandSender sender, String command, String[] args) {
		return true;
	}

}
