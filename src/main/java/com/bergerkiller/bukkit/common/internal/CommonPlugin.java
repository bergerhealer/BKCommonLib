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
import net.minecraft.server.v1_5_R2.Entity;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.MessageBuilder;
import com.bergerkiller.bukkit.common.ModuleLogger;
import com.bergerkiller.bukkit.common.PluginBase;
import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.TypedValue;
import com.bergerkiller.bukkit.common.collections.EntityMap;
import com.bergerkiller.bukkit.common.events.EntityMoveEvent;
import com.bergerkiller.bukkit.common.events.EntityRemoveFromServerEvent;
import com.bergerkiller.bukkit.common.metrics.MyDependingPluginsGraph;
import com.bergerkiller.bukkit.common.metrics.SoftDependenciesGraph;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.LongHashSet;
import com.kellerkindt.scs.ShowCaseStandalone;
import com.narrowtux.showcase.Showcase;

@SuppressWarnings({"rawtypes", "unchecked"})
public class CommonPlugin extends PluginBase {
	/**
	 * BKCommonLib Minecraft versioning
	 */
	public static final String DEPENDENT_MC_VERSION = "v1_5_R2";
	public static final boolean IS_COMPATIBLE = Common.isMCVersionCompatible(DEPENDENT_MC_VERSION);
	/**
	 * Known plugins that require ProtocolLib to be installed
	 */
	private static final String[] protLibPlugins = {"Spout"};
	/*
	 * Loggers for internal BKCommonLib processes
	 */
	public static final ModuleLogger LOGGER = new ModuleLogger("BKCommonLib");
	public static final ModuleLogger LOGGER_REFLECTION = LOGGER.getModule("Reflection");
	/*
	 * Remaining internal variables
	 */
	private static CommonPlugin instance;
	public final List<PluginBase> plugins = new ArrayList<PluginBase>();
	private EntityMap<Player, LongHashSet> playerVisibleChunks;
	protected final Map<World, CommonWorldListener> worldListeners = new HashMap<World, CommonWorldListener>();
	protected final ArrayList<SoftReference<EntityMap>> maps = new ArrayList<SoftReference<EntityMap>>();
	private final List<Runnable> nextTickTasks = new ArrayList<Runnable>();
	private final List<Runnable> nextTickSync = new ArrayList<Runnable>();
	private final List<NextTickListener> nextTickListeners = new ArrayList<NextTickListener>(1);
	private final List<Task> startedTasks = new ArrayList<Task>();
	private final HashSet<org.bukkit.entity.Entity> entitiesToRemove = new HashSet<org.bukkit.entity.Entity>();
	private final HashMap<String, TypedValue> debugVariables = new HashMap<String, TypedValue>();
	private boolean vaultEnabled = false;
	private Permission vaultPermission = null;
	private boolean isShowcaseEnabled = false;
	private boolean isSCSEnabled = false;
	private boolean isProtocolLibEnabled = false;
	private Plugin bleedingMobsInstance = null;
	private PacketHandler packetHandler = new CommonPacketHandler();
	public List<Entity> entities = new ArrayList<Entity>();

	public static boolean hasInstance() {
		return instance != null;
	}

	public static CommonPlugin getInstance() {
		if (instance == null) {
			throw new RuntimeException("BKCommonLib is not enabled - Plugin Instance can not be obtained! (disjointed Class state?)");
		}
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
				LOGGER_REFLECTION.log(Level.SEVERE, msg + " (Update BKCommonLib?)");
				for (StackTraceElement ste : ex.getStackTrace()) {
					log(Level.SEVERE, "    at " + ste.toString());
				}
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

	public <T> TypedValue<T> getDebugVariable(String name, Class<T> type, T value) {
		TypedValue typed = debugVariables.get(name);
		if (typed == null || typed.type != type) {
			typed = new TypedValue(type, value);
			debugVariables.put(name, typed);
		}
		return typed;
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
					if (ShowCaseStandalone.get().getShopHandler().isShopItem(item)) {
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

	public boolean isChunkVisible(Player player, int chunkX, int chunkZ) {
		synchronized (playerVisibleChunks) {
			LongHashSet chunks = playerVisibleChunks.get(player);
			return chunks == null ? null : chunks.contains(chunkX, chunkZ);
		}
	}

	public void setChunksAsVisible(Player player, int[] chunkX, int[] chunkZ) {
		if (chunkX.length != chunkZ.length) {
			throw new IllegalArgumentException("Chunk X and Z coordinate count is not the same");
		}
		synchronized (playerVisibleChunks) {
			LongHashSet chunks = playerVisibleChunks.get(player);
			if (chunks == null) {
				chunks = new LongHashSet();
				playerVisibleChunks.put(player, chunks);
			}
			for (int i = 0; i < chunkX.length; i++) {
				chunks.add(chunkX[i], chunkZ[i]);
			}
		}
	}

	public void setChunkVisible(Player player, int chunkX, int chunkZ, boolean visible) {
		synchronized (playerVisibleChunks) {
			LongHashSet chunks = playerVisibleChunks.get(player);
			if (chunks == null) {
				if (!visible) {
					return;
				}
				chunks = new LongHashSet();
				playerVisibleChunks.put(player, chunks);
			}
			if (visible) {
				chunks.add(chunkX, chunkZ);
			} else {
				chunks.remove(chunkX, chunkZ);
			}
		}
	}

	/**
	 * Obtains the Packet Handler used for packet listeners/monitors and packet sending
	 * 
	 * @return packet handler instance
	 */
	public PacketHandler getPacketHandler() {
		return packetHandler;
	}

	private void setPacketHandler(PacketHandler handler) {
		this.packetHandler.transfer(handler);
		this.packetHandler = handler;
	}

	@Override
	public void permissions() {
	}

	@Override
	public void updateDependency(Plugin plugin, String pluginName, boolean enabled) {
		if (!enabled) {
			packetHandler.removePacketListeners(plugin);
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
				if(Common.IS_SPIGOT_SERVER) {
					//Disable spigot listener
					SpigotPacketListener.ENABLED = false;
				} else {
					// Unregister previous PlayerConnection hooks
					CommonPlayerConnection.unbindAll();
				}
				// Obtain all current packet listeners
				// Register all packets in ProtocolLib
				setPacketHandler(new ProtocolLibPacketHandler());
			} else {
				if(Common.IS_SPIGOT_SERVER) {
					//Enable spigot listener again
					if(SpigotPacketListener.ENABLED) {
						new SpigotPacketListener();
					} else {
						SpigotPacketListener.ENABLED = true;
					}
				} else {
					//Now uses the onPlayerJoin method (see CommonListener) to deal with this
					CommonPlayerConnection.bindAll();
				}
				//Fall back to default system
				setPacketHandler(new CommonPacketHandler());				
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
		logProtocolLib();
		Bukkit.getPluginManager().disablePlugin(this);
	}

	private void logProtocolLib() {
		log(Level.SEVERE, "Install ProtocolLib to restore protocol compatibility");
		log(Level.SEVERE, "Dev-bukkit: http://dev.bukkit.org/server-mods/protocollib/");
		log(Level.SEVERE, "BKCommonLib and all depending plugins will now disable...");
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
		CommonPlayerConnection.unbindAll();
	}

	@Override
	public void enable() {
		// Validate version
		if (IS_COMPATIBLE) {
			if (CommonUtil.getPlugin("ProtocolLib") == null) {
				if (Common.IS_SPIGOT_SERVER) {
					log(Level.SEVERE, "The BKCommonLib Packet listener injector is not supported on the Spigot server implementation");
					logProtocolLib();
					Bukkit.getPluginManager().disablePlugin(this);
					return;
				} else {
					Plugin plugin;
					for (String protLibPlugin : protLibPlugins) {
						if ((plugin = CommonUtil.getPlugin(protLibPlugin)) != null) {
							failPacketListener(plugin.getClass());
							return;
						}
					}
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

		// Initialize entity map (needs to be here because of CommonPlugin instance needed)
		playerVisibleChunks = new EntityMap<Player, LongHashSet>();

		// Register packet listener if ProtocolLib is not detected
		if (CommonUtil.getPlugin("ProtocolLib") == null) {
			if(Common.IS_SPIGOT_SERVER) {
				new SpigotPacketListener();
			} else {
				CommonPlayerConnection.bindAll();
			}
		} else {
			// Instantly set the packet handler - is more efficient
			packetHandler = new ProtocolLibPacketHandler();
		}

		// Register events and tasks, initialize
		register(new CommonListener());
		register(new CommonPacketMonitor(), CommonPacketMonitor.TYPES);
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
		if (debugVariables.isEmpty()) {
			return false;
		}
		if (command.equals("commondebug") || command.equals("debug")) {
			MessageBuilder message = new MessageBuilder();
			if (args.length == 0) {
				message.green("This command allows you to tweak debug settings in plugins").newLine();
				message.green("All debug variables should be cleared in official builds").newLine();
				message.green("Available debug variables:").newLine();
				message.setSeparator(ChatColor.YELLOW, " \\ ").setIndent(4);
				for (String variable : debugVariables.keySet()) {
					message.green(variable);
				}
			} else {
				final String varname = args[0];
				final TypedValue value = debugVariables.get(varname);
				if (value == null) {
					message.red("No debug variable of name '").yellow(varname).red("'!");
				} else {
					message.green("Value of variable '").yellow(varname).green("' ");
					if (args.length == 1) {
						message.green("= ");
					} else {
						message.green("set to ");
						value.parseSet(StringUtil.combine(" ", StringUtil.remove(args, 0)));
					}
					message.white(value.toString());
				}
			}
			message.send(sender);
			return true;
		}
		return false;
	}
}