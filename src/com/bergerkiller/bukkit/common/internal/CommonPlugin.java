package com.bergerkiller.bukkit.common.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import me.snowleo.bleedingmobs.BleedingMobs;
import net.milkbowl.vault.permission.Permission;
import net.minecraft.server.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftItem;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.PluginBase;
import com.bergerkiller.bukkit.common.events.EntityMoveEvent;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.kellerkindt.scs.ShowCaseStandalone;
import com.narrowtux.showcase.Showcase;

public class CommonPlugin extends PluginBase {
	private static CommonPlugin instance;
	public static final List<PluginBase> plugins = new ArrayList<PluginBase>();
	protected static final Map<World, CommonWorldListener> worldListeners = new HashMap<World, CommonWorldListener>();
	public static final List<Runnable> nextTickTasks = new ArrayList<Runnable>();
	private static final List<Runnable> nextTickSync = new ArrayList<Runnable>();
	private int nextTickHandlerId = -1;
	private int entityMoveHandlerId = -1;
	private boolean vaultEnabled = false;
	private Permission vaultPermission = null;
	private boolean isShowcaseEnabled = false;
	private boolean isSCSEnabled = false;
	private Plugin bleedingMobsInstance = null;

	public static CommonPlugin getInstance() {
		return instance;
	}

	/**
	 * Handles the message and/or stack trace logging when something related to reflection is missing
	 * 
	 * @param type of thing that is missing
	 * @param name of the thing that is missing
	 * @param source class in which it is missing
	 */
	public void handleReflectionMissing(String type, String name, Class<?> source) {
		String msg = type + " '" + name + "' does not exist in class file " + source.getSimpleName();
		Exception ex = new Exception(msg);
		for (StackTraceElement elem : ex.getStackTrace()) {
			if (elem.getClassName().startsWith("com.bergerkiller.bukkit.common.reflection.classes")) {
				log(Level.SEVERE, msg + " (Update BKCommonLib?)");
				return;
			}
		}
		ex.printStackTrace();
	}

	@SuppressWarnings("deprecation")
	public boolean isEntityIgnored(Entity entity) {
		if (entity instanceof Item) {
			Item item = (Item) entity;
			if (this.isShowcaseEnabled) {
				try {
					if (Showcase.instance.getItemByDrop(item) != null)
						return true;
				} catch (Throwable t) {
					Bukkit.getLogger().log(Level.SEVERE, "Showcase item verification failed (update needed?), contact the authors!");
					t.printStackTrace();
					this.isShowcaseEnabled = false;
				}
			}
			if (this.isSCSEnabled) {
				try {
					if (ShowCaseStandalone.get().isShowCaseItem(item))
						return true;
				} catch (Throwable t) {
					Bukkit.getLogger().log(Level.SEVERE, "ShowcaseStandalone item verification failed (update needed?), contact the authors!");
					t.printStackTrace();
					this.isSCSEnabled = false;
				}
			}
			if (this.bleedingMobsInstance != null) {
				try {
					BleedingMobs bm = (BleedingMobs) this.bleedingMobsInstance;
					if (bm.isSpawning())
						return true;
					if (bm.isWorldEnabled(item.getWorld())) {
						if (bm.isParticleItem(((CraftItem) item).getUniqueId())) {
							return true;
						}
					}
				} catch (Throwable t) {
					Bukkit.getLogger().log(Level.SEVERE, "Bleeding Mobs item verification failed (update needed?), contact the authors!");
					t.printStackTrace();
					this.bleedingMobsInstance = null;
				}
			}
		}
		return false;
	}

	public boolean hasPermission(CommandSender sender, String permissionNode) {
		if (this.vaultEnabled) {
			return this.vaultPermission.has(sender, permissionNode);
		} else {
			return sender.hasPermission(permissionNode);
		}
	}

	@Override
	public void permissions() {
	}

	@Override
	public void updateDependency(Plugin plugin, String pluginName, boolean enabled) {
		if (pluginName.equals("Showcase")) {
			this.isShowcaseEnabled = enabled;
			if (enabled) {
				log(Level.INFO, "Showcase detected: Showcased items will be ignored");
			}
		} else if (pluginName.equals("ShowCaseStandalone")) {
			this.isSCSEnabled = enabled;
			if (enabled) {
				log(Level.INFO, "Showcase Standalone detected: Showcased items will be ignored");
			}
		} else if (pluginName.equals("BleedingMobs")) {
			this.bleedingMobsInstance = enabled ? plugin : null;
			if (enabled) {
				log(Level.INFO, "Bleeding Mobs detected: Particle items will be ignored");
			}
		} else if (pluginName.equals("Vault")) {
			if (enabled) {
		        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(Permission.class);
		        if (permissionProvider != null) {
		            this.vaultPermission = permissionProvider.getProvider();
		            this.vaultEnabled = this.vaultPermission != null;
		        }
			} else {
				this.vaultPermission = null;
				this.vaultEnabled = false;
			}
		}
	}

	@Override
	public int getMinimumLibVersion() {
		return 0;
	}

	@Override
	public void setDisableMessage(String message) {
	};

	@Override
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

	@Override
	public void enable() {
		instance = this;
		// Register events and tasks, initialize
		this.register(new CommonListener());
		for (WorldServer world : WorldUtil.getWorlds()) {
			CommonWorldListener listener = new CommonWorldListener(world);
			listener.enable();
			worldListeners.put(world.getWorld(), listener);
		}
		nextTickHandlerId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new NextTickHandler(), 1, 1);
		entityMoveHandlerId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new MoveEventHandler(), 1, 1);

		// Parse BKCommonLib version to int
		String ver = this.getVersion();
		int dot1 = ver.indexOf('.');
		if (dot1 != -1) {
			int dot2 = ver.indexOf('.', dot1 + 1);
			if (dot2 != -1) {
				ver = ver.substring(0, dot2);
			}
		}
		int version = this.getVersionNumber();
		if (version != Common.VERSION) {
			log(Level.SEVERE, "Common.VERSION needs to be updated to contain '" + version + "'!");
		}
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
					for (net.minecraft.server.Entity entity : WorldUtil.getEntities(world)) {
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
		return false;
	}
}