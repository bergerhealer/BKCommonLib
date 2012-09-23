package com.bergerkiller.bukkit.common.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import net.minecraft.server.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.PluginBase;
import com.bergerkiller.bukkit.common.utils.WorldUtil;

public class CommonPlugin extends PluginBase {
	public static CommonPlugin instance;
	public static final List<PluginBase> plugins = new ArrayList<PluginBase>();
	protected static final Map<World, CommonWorldListener> worldListeners = new HashMap<World, CommonWorldListener>();
	public static final List<Runnable> nextTickTasks = new ArrayList<Runnable>();
	private static final List<Runnable> nextTickSync = new ArrayList<Runnable>();
	private int nextTickHandlerId = -1;

	@Override
	public void permissions() {
	}

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
			if (enabled) {
				log(Level.INFO, "Bleeding Mobs detected: Particle items will be ignored");
			}
		}
	}

	public void setDisableMessage(String message) {
	};

	public void disable() {
		instance = null;
		for (CommonWorldListener listener : worldListeners.values()) {
			listener.disable();
		}
		worldListeners.clear();
		if (nextTickHandlerId != -1) {
			Bukkit.getScheduler().cancelTask(nextTickHandlerId);
		}
	}

	public void enable() {
		instance = this;
		this.register(new CommonListener());
		for (WorldServer world : WorldUtil.getWorlds()) {
			CommonWorldListener listener = new CommonWorldListener(world);
			listener.enable();
			worldListeners.put(world.getWorld(), listener);
		}
		nextTickHandlerId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				synchronized (nextTickTasks) {
					if (nextTickTasks.isEmpty()) {
						return;
					}
					nextTickSync.addAll(nextTickTasks);
					nextTickTasks.clear();
				}
				for (Runnable task : nextTickSync) {
					task.run();
				}
				nextTickSync.clear();
			}
		}, 1, 1);
	}

	@Override
	public boolean command(CommandSender sender, String command, String[] args) {
		// TODO Auto-generated method stub
		return false;
	}
}
