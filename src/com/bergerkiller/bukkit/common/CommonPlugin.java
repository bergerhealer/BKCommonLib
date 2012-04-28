package com.bergerkiller.bukkit.common;

import java.util.logging.Level;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class CommonPlugin extends PluginBase {
		
	@Override
	public void permissions() {}
	
	public void updateDependency(Plugin plugin, String pluginName, boolean enabled) {
		if (pluginName.equals("Showcase")) {
			Common.isShowcaseEnabled = enabled;
			if (enabled) {
				log(Level.INFO, "Showcase detected: Showcased items will be ignored");
			}
		} else if (pluginName.equals("ShowCaseStandalone")) {
			Common.isSCSEnabled = enabled;
			if (enabled) {
				log(Level.INFO, "Showcase Standalone detected: Showcased items will be ignored");
			}
		} else if (pluginName.equals("BleedingMobs")) {
			Common.bleedingMobsInstance = enabled ? plugin : null;
			if (enabled ){
				log(Level.INFO, "Bleeding Mobs detected: Particle items will be ignored");
			}
		}
	}
		
	public void setDisableMessage(String message) {};
	
	public void disable() {}
	public void enable() {
		this.register(new PluginListener());
	}

	@Override
	public boolean command(CommandSender sender, String command, String[] args) {
		// TODO Auto-generated method stub
		return false;
	}

}
