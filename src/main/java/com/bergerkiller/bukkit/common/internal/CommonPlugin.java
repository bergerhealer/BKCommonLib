package com.bergerkiller.bukkit.common.internal;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import me.snowleo.bleedingmobs.BleedingMobs;
import net.milkbowl.vault.permission.Permission;
import net.minecraft.server.v1_4_R1.Entity;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.PluginBase;
import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.collections.EntityMap;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.events.EntityMoveEvent;
import com.bergerkiller.bukkit.common.events.EntityRemoveFromServerEvent;
import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.metrics.MyDependingPluginsGraph;
import com.bergerkiller.bukkit.common.metrics.SoftDependenciesGraph;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketFields;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.reflection.classes.EntityPlayerRef;
import com.bergerkiller.bukkit.common.reflection.classes.PlayerConnectionRef;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.PlayerUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.kellerkindt.scs.ShowCaseStandalone;
import com.narrowtux.showcase.Showcase;

@SuppressWarnings({"rawtypes", "unchecked"})
public class CommonPlugin extends PluginBase {
	/*
	 * BKCommonLib Minecraft versioning
	 */
	public static final String DEPENDENT_MC_VERSION = "v1_4_R1";
	public static final boolean IS_COMPATIBLE = Common.isMCVersionCompatible(DEPENDENT_MC_VERSION);
	/*
	 * Known plugins that require ProtocolLib to be installed
	 */
	private static final String[] protLibPlugins = {"Spout"};
	/*
	 * Remaining internal variables
	 */
	private static CommonPlugin instance;
	public final List<PluginBase> plugins = new ArrayList<PluginBase>();
	protected final Map<World, CommonWorldListener> worldListeners = new HashMap<World, CommonWorldListener>();
	protected final ArrayList<SoftReference<EntityMap>> maps = new ArrayList<SoftReference<EntityMap>>();
	protected final List<PacketListener>[] listeners = new ArrayList[256];
	protected final Map<Plugin, List<PacketListener>> listenerPlugins = new HashMap<Plugin, List<PacketListener>>();
	private final List<Runnable> nextTickTasks = new ArrayList<Runnable>();
	private final List<Runnable> nextTickSync = new ArrayList<Runnable>();
	private final List<NextTickListener> nextTickListeners = new ArrayList<NextTickListener>(1);
	private final List<Task> startedTasks = new ArrayList<Task>();
	private final HashSet<org.bukkit.entity.Entity> entitiesToRemove = new HashSet<org.bukkit.entity.Entity>();
	private boolean vaultEnabled = false;
	private Permission vaultPermission = null;
	private boolean isShowcaseEnabled = false;
	private boolean isSCSEnabled = false;
	private boolean isProtocolLibEnabled = false;
	private Plugin bleedingMobsInstance = null;
	public List<Entity> entities = new ArrayList<Entity>();

	public static CommonPlugin getInstance() {
		return instance;
	}

	public boolean isUsingFallBackPacketListener() {
		return !isProtocolLibEnabled;
	}

	/**
	 * Handles the message and/or stack trace logging when something related to reflection is missing
	 * 
	 * @param type of thing that is missing
	 * @param name of the thing that is missing
	 * @param source class in which it is missing
	 */
	public void handleReflectionMissing(String type, String name, Class<?> source) {
		String msg = type + " '" + name + "' does not exist in class file " + source.getName();
		Exception ex = new Exception(msg);
		for (StackTraceElement elem : ex.getStackTrace()) {
			if (elem.getClassName().startsWith("com.bergerkiller.bukkit.common.reflection.classes")) {
				log(Level.SEVERE, "[Reflection] " + msg + " (Update BKCommonLib?)");
				return;
			}
		}
		ex.printStackTrace();
	}

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

	public void addNextTickListener(NextTickListener listener) {
		nextTickListeners.add(listener);
	}

	public void removeNextTickListener(NextTickListener listener) {
		nextTickListeners.remove(listener);
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
					if (bm.isSpawning() || (bm.isWorldEnabled(item.getWorld()) && bm.isParticleItem(item.getUniqueId()))) {
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

	public boolean onPacketSend(Player player, Object packet) {
		if(player == null || packet == null) {
			return true;
		}
		return onPacketSend(player, packet, PacketFields.DEFAULT.packetID.get(packet));
	}

	public void addPacketListener(Plugin plugin, PacketListener listener, int[] ids) {
		if (listener == null) {
			throw new IllegalArgumentException("Listener is not allowed to be null");
		} else if (plugin == null) {
			throw new IllegalArgumentException("Plugin is not allowed to be null");
		}
		// Registration in BKCommonLib
		for (int id : ids) {
			if (id < 0 || id >= listeners.length) {
				throw new IllegalArgumentException("Unknown packet type Id: " + id);
			}
			// Map to listener array
			if (listeners[id] == null) {
				listeners[id] = new ArrayList<PacketListener>();
			}
			listeners[id].add(listener);
			// Map to plugin list
			List<PacketListener> list = listenerPlugins.get(plugin);
			if (list == null) {
				list = new ArrayList<PacketListener>(2);
				listenerPlugins.put(plugin, list);
			}
			list.add(listener);
		}
		// If ProtocolLib is enabled, register a new listener
		if (this.isProtocolLibEnabled) {
			CommonProtocolLibHandler.register(ids);
		}
	}

	public void removePacketListeners(Plugin plugin) {
		List<PacketListener> listeners = listenerPlugins.get(plugin);
		if (listeners != null) {
			for (PacketListener listener : listeners) {
				removePacketListener(listener, false);
			}
		}
	}

	public void removePacketListener(PacketListener listener, boolean fromPlugins) {
		if(listener == null) {
			return;
		}
		for(int i = 0; i < listeners.length; i++) {
			if (!LogicUtil.nullOrEmpty(listeners[i])) {
				listeners[i].remove(listener);
			}
		}
		if (fromPlugins) {
			// Remove from plugin list
			for (Plugin plugin : listenerPlugins.keySet().toArray(new Plugin[0])) {
				List<PacketListener> list = listenerPlugins.get(plugin);
				// If not null, remove the listener, if empty afterwards remove the entire entry
				if (list != null && list.remove(listener) && list.isEmpty()) {
					listenerPlugins.remove(plugin);
				}
			}
		}
	}

	public boolean onPacketSend(Player player, Object packet, int id) {
		if(player == null || packet == null) {
			return true;
		}

		if (!LogicUtil.nullOrEmpty(listeners[id])) {
			CommonPacket cp = new CommonPacket(packet, id);
			PacketSendEvent ev = new PacketSendEvent(player, cp);
			for(PacketListener listener : listeners[id]) {
				listener.onPacketSend(ev);
			}
			return !ev.isCancelled();
		} else {
			return true;
		}
	}

	public boolean onPacketReceive(Player player, Object packet) {
		if(player == null || packet == null) {
			return true;
		}
		return onPacketReceive(player, packet, PacketFields.DEFAULT.packetID.get(packet));
	}

	public boolean onPacketReceive(Player player, Object packet, int id) {
		if(player == null || packet == null) {
			return true;
		}

		if (!LogicUtil.nullOrEmpty(listeners[id])) {
			CommonPacket cp = new CommonPacket(packet, id);
			PacketReceiveEvent ev = new PacketReceiveEvent(player, cp);
			for(PacketListener listener : listeners[id]) {
				listener.onPacketReceive(ev);
			}
			return !ev.isCancelled();
		} else {
			return true;
		}
	}

	public void sendPacket(Player player, Object packet, boolean throughListeners) {
		if (!PacketFields.DEFAULT.isInstance(packet) || PlayerUtil.isDisconnected(player)) {
			return;
		}
		if (this.isProtocolLibEnabled) {
			CommonProtocolLibHandler.sendPacket(player, packet, throughListeners);
		} else {
			final Object connection = EntityPlayerRef.playerConnection.get(Conversion.toEntityHandle.convert(player));
			PlayerConnectionRef.sendPacket(connection, packet);
		}
	}

	@Override
	public void permissions() {
	}

	@Override
	public void updateDependency(Plugin plugin, String pluginName, boolean enabled) {
		if (!enabled) {
			removePacketListeners(plugin);
		}
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
		} else if (pluginName.equals("ProtocolLib")) {
			if (this.isProtocolLibEnabled = enabled) {
				log(Level.INFO, "Now using ProtocolLib to handle packet listeners");
				//Unregister previous PlayerConnection hooks
				CommonPacketListener.unbindAll();
				//Register all packets in ProtocolLib
				Set<Integer> packets = new HashSet<Integer>(10);
				for (int i = 0; i < listeners.length; i++) {
					if (!LogicUtil.nullOrEmpty(listeners[i])) {
						packets.add(i);
					}
				}
				CommonProtocolLibHandler.register(packets);
			} else {
				//Now uses the onPlayerJoin method (see CommonListener) to deal with this
				CommonPacketListener.bindAll();
			}
		} else if (enabled && LogicUtil.contains(pluginName, protLibPlugins)) {
			failPacketListener(plugin.getClass());
		}
	}

	public void failPacketListener(Class<?> playerConnectionType) {
		if (CommonUtil.getPlugin("ProtocolLib") != null) {
			return;
		}
		Plugin plugin = CommonUtil.getPluginByClass(playerConnectionType);
		log(Level.SEVERE, "Failed to hook up a PlayerConnection to listen for received and sent packets");
		if (plugin == null) {
			log(Level.SEVERE, "This was caused by an unknown source, class: " + playerConnectionType.getName());
		} else {
			log(Level.SEVERE, "This was caused by a plugin conflict, namely " + plugin.getName());
		}
		log(Level.SEVERE, "Install ProtocolLib to restore protocol compatibility between plugins");
		log(Level.SEVERE, "Dev-bukkit: http://dev.bukkit.org/server-mods/protocollib/");
		log(Level.SEVERE, "BKCommonLib and all depending plugins will now disable...");
		Bukkit.getPluginManager().disablePlugin(this);
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
		instance = this;
		// Load the classes contained in this library
		CommonClasses.init();
	}

	@Override
	public void disable() {
		instance = null;
		for (CommonWorldListener listener : worldListeners.values()) {
			listener.disable();
		}
		worldListeners.clear();
		// Clear running tasks
		for (Task task : startedTasks) {
			task.stop();
		}
		startedTasks.clear();
		// Transfer PlayerConnection from players back to default
		CommonPacketListener.unbindAll();
	}

	@Override
	public void enable() {
		// Validate version
		if (IS_COMPATIBLE) {
			Plugin plugin;
			for (String protLibPlugin : protLibPlugins) {
				if ((plugin = CommonUtil.getPlugin(protLibPlugin)) != null) {
					failPacketListener(plugin.getClass());
					return;
				}
			}

			log(Level.INFO, "BKCommonLib is running on Minecraft " + DEPENDENT_MC_VERSION);
			final List<String> welcomeMessages = Arrays.asList(
					"This library is written with stability in mind.", 
					"No Bukkit moderators were harmed while compiling this piece of art.", 
					"Have a problem Bukkit can't fix? Write a library!",
					"Bringing home the bacon since 2011!",
					"Completely virus-free and scanned by various Bukkit-dev-staff watching eyes.",
					"Hosts all the features that are impossible to include in a single Class",
					"CraftBukkit: redone, reworked, translated and interfaced.",
					"Having an error? *gasp* Don't forget to file a ticket on dev.bukkit.org!",
					"Package versioning is what brought BKCommonLib and CraftBukkit closer together!",
					"For all the haters out there: BKCommonLib at least tries!",
					"Want fries with that? We have hidden fries in the FoodUtil class.",
					"Not enough wrappers. Needs more wrappers. Moooreee...",
					"Reflection can open the way to everyone's heart, including CraftBukkit.",
					"Our love is not permitted by the overlords. We must flee...",
					"Now a plugin, a new server implementation tomorrow???");
			log(Level.INFO, welcomeMessages.get((int) (Math.random() * welcomeMessages.size())));
		} else {
			log(Level.SEVERE, "BKCommonLib can only run on a CraftBukkit build compatible with Minecraft " + DEPENDENT_MC_VERSION);
			log(Level.SEVERE, "Please look for an available BKCommonLib update:");
			log(Level.SEVERE, "http://dev.bukkit.org/server-mods/bkcommonlib/");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		// Register packet listener if ProtocolLib is not detected
		if (CommonUtil.getPlugin("ProtocolLib") == null) {
			CommonPacketListener.bindAll();
		}

		// Register events and tasks, initialize
		register(new CommonListener());
		startedTasks.add(new NextTickHandler(this).start(1, 1));
		startedTasks.add(new MoveEventHandler(this).start(1, 1));
		startedTasks.add(new EntityRemovalHandler(this).start(1, 1));

		// Register world listeners
		for (World world : WorldUtil.getWorlds()) {
			notifyWorldAdded(world);
		}

		// BKCommonLib Metrics
		if (hasMetrics()) {
			// Soft dependencies
			getMetrics().addGraph(new SoftDependenciesGraph());

			// Depending
			getMetrics().addGraph(new MyDependingPluginsGraph());
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

	private static class EntityRemovalHandler extends Task {
		public EntityRemovalHandler(JavaPlugin plugin) {
			super(plugin);
		}

		@Override
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

	private static class NextTickHandler extends Task {
		public NextTickHandler(JavaPlugin plugin) {
			super(plugin);
		}

		@Override
		public void run() {
			List<Runnable> nextTick = getInstance().nextTickTasks;
			List<Runnable> nextSync = getInstance().nextTickSync;
			List<NextTickListener> nextTickListeners = getInstance().nextTickListeners;
			synchronized (nextTick) {
				if (nextTick.isEmpty()) {
					return;
				}
				nextSync.addAll(nextTick);
				nextTick.clear();
			}
			if (nextTickListeners.isEmpty()) {
				// No time measurement needed
				for (Runnable task : nextSync) {
					try {
						task.run();
					} catch (Throwable t) {
						instance.log(Level.SEVERE, "An error occurred in next-tick task '" + task.getClass().getName() + "':");
						CommonUtil.filterStackTrace(t).printStackTrace();
					}
				}
			} else {
				// Perform time measurement and call next-tick listeners
				int i;
				final int listenerCount = nextTickListeners.size();
				long startTime, delta, endTime = System.nanoTime();
				for (Runnable task : nextSync) {
					startTime = endTime;
					try {
						task.run();
					} catch (Throwable t) {
						instance.log(Level.SEVERE, "An error occurred in next-tick task '" + task.getClass().getName() + "':");
						CommonUtil.filterStackTrace(t).printStackTrace();
					}
					endTime = System.nanoTime();
					delta = endTime - startTime;
					for (i = 0; i < listenerCount; i++) {
						nextTickListeners.get(i).onNextTicked(task, delta);
					}
				}
			}
			nextSync.clear();
		}
	}

	private static class MoveEventHandler extends Task {
		public MoveEventHandler(JavaPlugin plugin) {
			super(plugin);
		}

		@Override
		public void run() {
			CommonPlugin cp = CommonPlugin.getInstance();
			if (CommonUtil.hasHandlers(EntityMoveEvent.getHandlerList())) {
				EntityMoveEvent event = new EntityMoveEvent();
				for (World world : WorldUtil.getWorlds()) {
					cp.entities.addAll(CommonNMS.getNative(world).entityList);
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