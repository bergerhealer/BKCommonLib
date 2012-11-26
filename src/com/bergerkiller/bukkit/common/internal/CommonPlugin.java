package com.bergerkiller.bukkit.common.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import net.minecraft.server.Entity;
import net.minecraft.server.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.PluginBase;
import com.bergerkiller.bukkit.common.events.EntityMoveEvent;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;

public class CommonPlugin extends PluginBase {
	public static CommonPlugin instance;
	public static final List<PluginBase> plugins = new ArrayList<PluginBase>();
	protected static final Map<World, CommonWorldListener> worldListeners = new HashMap<World, CommonWorldListener>();
	public static final List<Runnable> nextTickTasks = new ArrayList<Runnable>();
	private static final List<Runnable> nextTickSync = new ArrayList<Runnable>();
	private int nextTickHandlerId = -1;
	private int entityMoveHandlerId = -1;

	@Override
	public void permissions() {
	}

	@Override
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

	@Override
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
		if (entityMoveHandlerId != -1) {
			Bukkit.getScheduler().cancelTask(entityMoveHandlerId);
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
		nextTickHandlerId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new NextTickHandler(), 1, 1);
		entityMoveHandlerId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new MoveEventHandler(), 1, 1);
		// Parse version to int
		String ver = this.getVersion();
		int dot1 = ver.indexOf('.');
		if (dot1 != -1) {
			int dot2 = ver.indexOf('.', dot1 + 1);
			if (dot2 != -1) {
				ver = ver.substring(0, dot2);
			}
		}
		Common.VERSION = (int) (100.0 * ParseUtil.parseDouble(this.getVersion(), 0.0));
	}

	private static class NextTickHandler implements Runnable {
		public void run() {
			synchronized (nextTickTasks) {
				if (nextTickTasks.isEmpty()) {
					return;
				}
				nextTickSync.addAll(nextTickTasks);
				nextTickTasks.clear();
			}
			for (Runnable task : nextTickSync) {
				try {
					task.run();
				} catch (Throwable t) {
					instance.log(Level.SEVERE, "An error occurred in next-tick task '" + task.getClass().getName() + "':");
					CommonUtil.filterStackTrace(t).printStackTrace();
				}
			}
			nextTickSync.clear();
		}
	}

	private static class MoveEventHandler implements Runnable {
		public void run() {
			if (EntityMoveEvent.getHandlerList().getRegisteredListeners().length > 0) {
				EntityMoveEvent event = new EntityMoveEvent();
				for (WorldServer world : WorldUtil.getWorlds()) {
					for (Entity entity : WorldUtil.getEntities(world)) {
						if (entity.locX != entity.lastX || entity.locY != entity.lastY || entity.locZ != entity.lastZ 
								|| entity.yaw != entity.lastYaw || entity.pitch != entity.lastPitch) {

							event.setEntity(entity);
							CommonUtil.callEvent(event);
						}
					}
				}
			}
		}
	}

	@Override
	public boolean command(CommandSender sender, String command, String[] args) {
		// TODO Auto-generated method stub
		return false;
	}
}
