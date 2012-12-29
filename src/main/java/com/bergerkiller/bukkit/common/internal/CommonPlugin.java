package com.bergerkiller.bukkit.common.internal;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import me.snowleo.bleedingmobs.BleedingMobs;
import net.milkbowl.vault.permission.Permission;
import net.minecraft.server.v1_4_6.Entity;
import net.minecraft.server.v1_4_6.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_4_6.entity.CraftItem;
import org.bukkit.entity.Item;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.EntityMap;
import com.bergerkiller.bukkit.common.PluginBase;
import com.bergerkiller.bukkit.common.events.EntityMoveEvent;
import com.bergerkiller.bukkit.common.events.EntityRemoveFromServerEvent;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.NativeUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.kellerkindt.scs.ShowCaseStandalone;
import com.narrowtux.showcase.Showcase;

@SuppressWarnings("rawtypes")
public class CommonPlugin extends PluginBase {
	/*
	 * BKCommonLib Minecraft versioning
	 */
	public static final String DEPENDENT_MC_VERSION = "v1_4_6";
	public static final boolean IS_COMPATIBLE = Common.isMCVersionCompatible(DEPENDENT_MC_VERSION);
	/*
	 * Remaining internal variables
	 */
	private static CommonPlugin instance;
	public final List<PluginBase> plugins = new ArrayList<PluginBase>();
	protected final Map<World, CommonWorldListener> worldListeners = new HashMap<World, CommonWorldListener>();
	protected final ArrayList<SoftReference<EntityMap>> maps = new ArrayList<SoftReference<EntityMap>>();
	private final List<Runnable> nextTickTasks = new ArrayList<Runnable>();
	private final List<Runnable> nextTickSync = new ArrayList<Runnable>();
	private final HashSet<org.bukkit.entity.Entity> entitiesToRemove = new HashSet<org.bukkit.entity.Entity>();
	private int nextTickHandlerId = -1;
	private int entityMoveHandlerId = -1;
	private int entityRemoveHandlerId = -1;
	private boolean vaultEnabled = false;
	private Permission vaultPermission = null;
	private boolean isShowcaseEnabled = false;
	private boolean isSCSEnabled = false;
	private Plugin bleedingMobsInstance = null;
	public List<Entity> entities = new ArrayList<Entity>();

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

	@SuppressWarnings("unchecked")
	public void registerMap(EntityMap map) {
		this.maps.add(new SoftReference(map));
	}

	public void nextTick(Runnable runnable) {
		if (runnable != null) {
			synchronized (this.nextTickTasks) {
				this.nextTickTasks.add(runnable);
			}
		}
	}

	public void notifyAdded(org.bukkit.entity.Entity e) {
		this.entitiesToRemove.remove(e);
	}

	public void notifyRemoved(org.bukkit.entity.Entity e) {
		this.entitiesToRemove.add(e);
	}
	
	public void notifyWorldAdded(org.bukkit.World world) {
		if (worldListeners.containsKey(world)) {
			return;
		}
		CommonWorldListener listener = new CommonWorldListener(world);
		listener.enable();
		worldListeners.put(world, listener);
	}

	@SuppressWarnings("deprecation")
	public boolean isEntityIgnored(org.bukkit.entity.Entity entity) {
		if (entity instanceof Item) {
			Item item = (Item) entity;
			if (this.isShowcaseEnabled) {
				try {
					if (Showcase.instance.getItemByDrop(item) != null) {
						return true;
					}
				} catch (Throwable t) {
					Bukkit.getLogger().log(Level.SEVERE, "Showcase item verification failed (update needed?), contact the authors!");
					t.printStackTrace();
					this.isShowcaseEnabled = false;
				}
			}
			if (this.isSCSEnabled) {
				try {
					if (ShowCaseStandalone.get().isShowCaseItem(item)) {
						return true;
					}
				} catch (Throwable t) {
					Bukkit.getLogger().log(Level.SEVERE, "ShowcaseStandalone item verification failed (update needed?), contact the authors!");
					t.printStackTrace();
					this.isSCSEnabled = false;
				}
			}
			if (this.bleedingMobsInstance != null) {
				try {
					BleedingMobs bm = (BleedingMobs) this.bleedingMobsInstance;
					if (bm.isSpawning() || (bm.isWorldEnabled(item.getWorld()) && bm.isParticleItem(((CraftItem) item).getUniqueId()))) {
						return true;
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
	public void onLoad() {
		CommonClasses.init(); // Load the classes contained in this library
	}

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
		if (entityRemoveHandlerId != -1) {
			Bukkit.getScheduler().cancelTask(entityRemoveHandlerId);
		}
	}

	@Override
	public void enable() {
		instance = this;

		// Validate version
		if (IS_COMPATIBLE) {
			log(Level.INFO, "BKCommonLib is running on Minecraft " + DEPENDENT_MC_VERSION);
			//send annonymous stats to mcstats.org
			try {
				Metrics metrics =  new Metrics(this);
				metrics.start();
			} catch(Exception e) {
				log(Level.INFO, "Failed sending stats to mcstats.org");
			}
		} else {
			log(Level.SEVERE, "BKCommonLib can only run on a CraftBukkit build compatible with Minecraft " + DEPENDENT_MC_VERSION);
			log(Level.SEVERE, "Please look for an available BKCommonLib update:");
			log(Level.SEVERE, "http://dev.bukkit.org/server-mods/bkcommonlib/");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		// Register events and tasks, initialize
		register(new CommonListener());
		nextTickHandlerId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new NextTickHandler(), 1, 1);
		entityMoveHandlerId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new MoveEventHandler(), 1, 1);
		entityRemoveHandlerId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new EntityRemovalHandler(), 1, 1);
		// Register world listeners
		for (World world : WorldUtil.getWorlds()) {
			notifyWorldAdded(world);
		}
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

	private static class EntityRemovalHandler implements Runnable {
		@SuppressWarnings("unchecked")
		public void run() {
			Set<org.bukkit.entity.Entity> entities = getInstance().entitiesToRemove;
			if (!entities.isEmpty()) {
				// Remove from maps
				Iterator<SoftReference<EntityMap>> iter = CommonPlugin.getInstance().maps.iterator();
				while (iter.hasNext()) {
					EntityMap map = iter.next().get();
					if (map == null) {
						iter.remove();
					} else if (!map.isEmpty()) {
						map.keySet().removeAll(entities);
					}
				}
				// Fire events
				if (CommonUtil.hasHandlers(EntityRemoveFromServerEvent.getHandlerList())) {
					for (org.bukkit.entity.Entity e : entities) {
						CommonUtil.callEvent(new EntityRemoveFromServerEvent(e));
					}
				}
				// Clear for next run
				entities.clear();
			}
		}
	}

	private static class NextTickHandler implements Runnable {
		public void run() {
			List<Runnable> nextTick = getInstance().nextTickTasks;
			List<Runnable> nextSync = getInstance().nextTickSync;
			synchronized (nextTick) {
				if (nextTick.isEmpty()) {
					return;
				}
				nextSync.addAll(nextTick);
				nextTick.clear();
			}
			for (Runnable task : nextSync) {
				try {
					task.run();
				} catch (Throwable t) {
					instance.log(Level.SEVERE, "An error occurred in next-tick task '" + task.getClass().getName() + "':");
					CommonUtil.filterStackTrace(t).printStackTrace();
				}
			}
			nextSync.clear();
		}
	}

	private static class MoveEventHandler implements Runnable {
		@SuppressWarnings("unchecked")
		public void run() {
			CommonPlugin cp = CommonPlugin.getInstance();
			if (CommonUtil.hasHandlers(EntityMoveEvent.getHandlerList())) {
				EntityMoveEvent event = new EntityMoveEvent();
				for (WorldServer world : NativeUtil.getWorlds()) {
					cp.entities.addAll(world.entityList);
					for (Entity entity : cp.entities) {
						if (entity.locX != entity.lastX || entity.locY != entity.lastY || entity.locZ != entity.lastZ 
								|| entity.yaw != entity.lastYaw || entity.pitch != entity.lastPitch) {

							event.setEntity(entity);
							CommonUtil.callEvent(event);
						}
					}
					cp.entities.clear();
				}
			}
		}
	}

	@Override
	public boolean command(CommandSender sender, String command, String[] args) {
		return false;
	}
}