package com.bergerkiller.bukkit.common.internal;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import me.snowleo.bleedingmobs.BleedingMobs;
import net.milkbowl.vault.permission.Permission;
import net.minecraft.server.Entity;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import regalowl.hyperconomy.HyperConomy;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.MessageBuilder;
import com.bergerkiller.bukkit.common.ModuleLogger;
import com.bergerkiller.bukkit.common.PluginBase;
import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.TypedValue;
import com.bergerkiller.bukkit.common.collections.EntityMap;
import com.bergerkiller.bukkit.common.events.EntityMoveEvent;
import com.bergerkiller.bukkit.common.events.EntityRemoveFromServerEvent;
import com.bergerkiller.bukkit.common.internal.network.CommonPacketHandler;
import com.bergerkiller.bukkit.common.internal.network.ProtocolLibPacketHandler;
import com.bergerkiller.bukkit.common.internal.network.SpigotPacketHandler;
import com.bergerkiller.bukkit.common.metrics.MyDependingPluginsGraph;
import com.bergerkiller.bukkit.common.metrics.SoftDependenciesGraph;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
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
	public static final String DEPENDENT_MC_VERSION = "v1_5_R3";
	public static final boolean IS_COMPATIBLE = Common.isMCVersionCompatible(DEPENDENT_MC_VERSION);
	/*
	 * Loggers for internal BKCommonLib processes
	 */
	public static final ModuleLogger LOGGER = new ModuleLogger("BKCommonLib");
	public static final ModuleLogger LOGGER_CONVERSION = LOGGER.getModule("Conversion");
	public static final ModuleLogger LOGGER_REFLECTION = LOGGER.getModule("Reflection");
	public static final ModuleLogger LOGGER_NETWORK = LOGGER.getModule("Network");
	public static final ModuleLogger LOGGER_TIMINGS = LOGGER.getModule("Timings");
	/*
	 * Timings class
	 */
	public static final TimingsRootListener TIMINGS = new TimingsRootListener();
	/*
	 * Remaining internal variables
	 */
	private static CommonPlugin instance;
	public final List<PluginBase> plugins = new ArrayList<PluginBase>();
	private EntityMap<Player, LongHashSet> playerVisibleChunks;
	protected final Map<World, CommonWorldListener> worldListeners = new HashMap<World, CommonWorldListener>();
	private final ArrayList<SoftReference<EntityMap>> maps = new ArrayList<SoftReference<EntityMap>>();
	private final List<Runnable> nextTickTasks = new ArrayList<Runnable>();
	private final List<Runnable> nextTickSync = new ArrayList<Runnable>();
	private final List<TimingsListener> timingsListeners = new ArrayList<TimingsListener>(1);
	private final List<Task> startedTasks = new ArrayList<Task>();
	private final HashSet<org.bukkit.entity.Entity> entitiesToRemove = new HashSet<org.bukkit.entity.Entity>();
	private final HashMap<String, TypedValue> debugVariables = new HashMap<String, TypedValue>();
	private final List<MobPreSpawnListener> mobPreSpawnListeners = new ArrayList<MobPreSpawnListener>();
	private boolean vaultEnabled = false;
	private Permission vaultPermission = null;
	private boolean isShowcaseEnabled = false;
	private boolean isSCSEnabled = false;
	private boolean isHyperConomyEnabled = false;
	private Plugin bleedingMobsInstance = null;
	private PacketHandler packetHandler = null;

	public static boolean hasInstance() {
		return instance != null;
	}

	public static CommonPlugin getInstance() {
		if (instance == null) {
			throw new RuntimeException("BKCommonLib is not enabled - Plugin Instance can not be obtained! (disjointed Class state?)");
		}
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
		String msg = type + " '" + name + "' does not exist in class file " + source.getName();
		Exception ex = new Exception(msg);
		for (StackTraceElement elem : ex.getStackTrace()) {
			if (elem.getClassName().startsWith(Common.COMMON_ROOT + ".reflection.classes")) {
				LOGGER_REFLECTION.log(Level.SEVERE, msg + " (Update BKCommonLib?)");
				for (StackTraceElement ste : ex.getStackTrace()) {
					log(Level.SEVERE, "at " + ste.toString());
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
		synchronized (this.nextTickTasks) {
			this.nextTickTasks.add(runnable);
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

	public void addMobPreSpawnListener(MobPreSpawnListener listener) {
		mobPreSpawnListeners.add(listener);
	}

	public void removeMobPreSpawnListener(MobPreSpawnListener listener) {
		mobPreSpawnListeners.remove(listener);
	}

	public boolean canSpawnMob(World world, int x, int y, int z, EntityType entityType) {
		for (MobPreSpawnListener listener : mobPreSpawnListeners) {
			if (!listener.canSpawn(world, x, y, z, entityType)) {
				return false;
			}
		}
		return true;
	}

	@Deprecated
	public void addNextTickListener(NextTickListener listener) {
		this.addTimingsListener(new NextTickListenerProxy(listener));
	}

	@Deprecated
	public void removeNextTickListener(NextTickListener listener) {
		Iterator<TimingsListener> iter = this.timingsListeners.iterator();
		while (iter.hasNext()) {
			TimingsListener t = iter.next();
			if (t instanceof NextTickListenerProxy && ((NextTickListenerProxy) t).listener == listener) {
				iter.remove();
			}
		}
	}

	public void addTimingsListener(TimingsListener listener) {
		this.timingsListeners.add(listener);
	}

	public void removeTimingsListener(TimingsListener listener) {
		this.timingsListeners.remove(listener);
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
					log(Level.SEVERE, "Showcase item verification failed (update needed?), contact the authors!");
					handle(t);
					this.isShowcaseEnabled = false;
				}
			}
			if (this.isSCSEnabled) {
				try {
					if (ShowCaseStandalone.get().getShopHandler().isShopItem(item)) {
						return true;
					}
				} catch (Throwable t) {
					log(Level.SEVERE, "ShowcaseStandalone item verification failed (update needed?), contact the authors!");
					handle(t);
					this.isSCSEnabled = false;
				}
			}
			if (this.isHyperConomyEnabled) {
				try {
					if (HyperConomy.hyperAPI.isItemDisplay(item)) {
						return true;
					}
				} catch (Throwable t) {
					log(Level.SEVERE, "HyperConomy item verification failed (update needed?), contact the authors!");
					handle(t);
					this.isHyperConomyEnabled = false;
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

	private boolean permCheck(CommandSender sender, String node) {
		// This check avoids the *-permissions granting all OP-players permission for everything
		if (Bukkit.getPluginManager().getPermission(node) == null) {
			Bukkit.getPluginManager().addPermission(new org.bukkit.permissions.Permission(node, PermissionDefault.FALSE));
		}
		if (this.vaultEnabled) {
			return this.vaultPermission.has(sender, node);
		} else {
			return sender.hasPermission(node);
		}
	}

	private boolean permCheck(CommandSender sender, StringBuilder root, String[] args, int argIndex) {
		// End of the sequence?
		if (argIndex >= args.length) {
			return permCheck(sender, root.toString());
		}
		int rootLength = root.length();
		if (rootLength != 0) {
			root.append('.');
			rootLength++;
		}
		final int newArgIndex = argIndex + 1;
		// Check permission with original name
		root.append(args[argIndex].toLowerCase(Locale.ENGLISH));
		if (permCheck(sender, root, args, newArgIndex)) {
			return true;
		}

		// Try with *-signs
		root.setLength(rootLength);
		root.append('*');
		return permCheck(sender, root, args, newArgIndex);
	}

	public boolean hasPermission(CommandSender sender, String[] permissionNode) {
		int expectedLength = permissionNode.length;
		for (String node : permissionNode) {
			expectedLength += node.length();
		}
		return permCheck(sender, new StringBuilder(expectedLength), permissionNode, 0);
	}

	public boolean hasPermission(CommandSender sender, String permissionNode) {
		return permCheck(sender, permissionNode.toLowerCase(Locale.ENGLISH)) || hasPermission(sender, permissionNode.split("\\."));
	}

	public boolean isChunkVisible(Player player, int chunkX, int chunkZ) {
		synchronized (playerVisibleChunks) {
			LongHashSet chunks = playerVisibleChunks.get(player);
			return chunks == null ? false : chunks.contains(chunkX, chunkZ);
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

	private boolean updatePacketHandler() {
		try {
			final Class<? extends PacketHandler> handlerClass;
			if (CommonUtil.isPluginEnabled("ProtocolLib")) {
				handlerClass = ProtocolLibPacketHandler.class;
			} else if (Common.IS_SPIGOT_SERVER) {
				handlerClass = SpigotPacketHandler.class;
			} else {
				handlerClass = CommonPacketHandler.class;
			}
			// Register the packet handler
			if (this.packetHandler != null && this.packetHandler.getClass() == handlerClass) {
				return true;
			}
			final PacketHandler handler = handlerClass.newInstance();
			if (this.packetHandler != null) {
				this.packetHandler.transfer(handler);
				if (!this.packetHandler.onDisable()) {
					return false;
				}
			}
			this.packetHandler = handler;
			if (!this.packetHandler.onEnable()) {
				return false;
			}
			LOGGER_NETWORK.log(Level.INFO, "Now using " + handler.getName() + " to provide Packet Listener and Monitor support");
			return true;
		} catch (Throwable t) {
			LOGGER_NETWORK.log(Level.SEVERE, "Failed to register a valid Packet Handler:");
			t.printStackTrace();
			return false;
		}
	}

	/**
	 * Should be called when BKCommonLib is unable to continue running as it does
	 */
	public void onCriticalFailure() {
		log(Level.SEVERE, "BKCommonLib and all depending plugins will now disable...");
		Bukkit.getPluginManager().disablePlugin(this);
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
			if (this.isShowcaseEnabled = enabled) {
				log(Level.INFO, "Showcase detected: Showcased items will be ignored");
			}
		} else if (pluginName.equals("ShowCaseStandalone")) {
			if (this.isSCSEnabled = enabled) {
				log(Level.INFO, "Showcase Standalone detected: Showcased items will be ignored");
			}
		} else if (pluginName.equals("HyperConomy")) {
			if (this.isHyperConomyEnabled = enabled) {
				log(Level.INFO, "HyperConomy detected: Showcased items will be ignored");
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
		if (!this.updatePacketHandler()) {
			this.onCriticalFailure();
		}
	}

	@Override
	public int getMinimumLibVersion() {
		return 0;
	}

	@Override
	public void onLoad() {
		instance = this;
		if (!IS_COMPATIBLE) {
			return;
		}
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

		// Disable the packet handlers
		try {
			packetHandler.onDisable();
		} catch (Throwable t) {
			log(Level.SEVERE, "Failed to properly disable the Packet Handler:");
			t.printStackTrace();
		}
		packetHandler = null;
	}

	@Override
	public void enable() {
		// Validate version
		if (IS_COMPATIBLE) {
			String version = "Minecraft " + Common.MC_VERSION;
			if (Common.MC_VERSION_PACKAGEPART.isEmpty()) {
				version += " (Non-versioned package)";
			} else {
				version += " (" + DEPENDENT_MC_VERSION + ")";
			}
			log(Level.INFO, "BKCommonLib is running on " + version);
		} else {
			log(Level.SEVERE, "BKCommonLib can only run on a CraftBukkit build compatible with Minecraft " + DEPENDENT_MC_VERSION);
			log(Level.SEVERE, "Please look for an available BKCommonLib update that is compatible with Minecraft " + Common.MC_VERSION + ":");
			log(Level.SEVERE, "http://dev.bukkit.org/server-mods/bkcommonlib/");
			this.onCriticalFailure();
			return;
		}

		// Set the packet handler to use before enabling further - it could fail!
		if (!this.updatePacketHandler()) {
			this.onCriticalFailure();
			return;
		}

		// Welcome message
		setDisableMessage(null);
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

		// Initialize entity map (needs to be here because of CommonPlugin instance needed)
		playerVisibleChunks = new EntityMap<Player, LongHashSet>();

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
		int version = this.getVersionNumber();
		if (version != Common.VERSION) {
			log(Level.SEVERE, "Common.VERSION needs to be updated to contain '" + version + "'!");
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

	private static class EntityRemovalHandler extends Task {
		public EntityRemovalHandler(JavaPlugin plugin) {
			super(plugin);
		}

		@Override
		public void run() {
			Set<org.bukkit.entity.Entity> removed = getInstance().entitiesToRemove;
			if (!removed.isEmpty()) {
				// Remove from maps
				Iterator<SoftReference<EntityMap>> iter = CommonPlugin.getInstance().maps.iterator();
				while (iter.hasNext()) {
					EntityMap map = iter.next().get();
					if (map == null) {
						iter.remove();
					} else if (!map.isEmpty()) {
						map.keySet().removeAll(removed);
					}
				}
				// Fire events
				if (CommonUtil.hasHandlers(EntityRemoveFromServerEvent.getHandlerList())) {
					for (org.bukkit.entity.Entity e : removed) {
						CommonUtil.callEvent(new EntityRemoveFromServerEvent(e));
					}
				}
				// Clear for next run
				removed.clear();
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
			synchronized (nextTick) {
				if (nextTick.isEmpty()) {
					return;
				}
				nextSync.addAll(nextTick);
				nextTick.clear();
			}
			if (!TIMINGS.isActive()) {
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
					TIMINGS.onNextTicked(task, delta);
				}
			}
			nextSync.clear();
		}
	}

	private static class MoveEventHandler extends Task {
		private final List<Entity> entityBuffer = new ArrayList<Entity>();

		public MoveEventHandler(JavaPlugin plugin) {
			super(plugin);
		}

		@Override
		public void run() {
			if (CommonUtil.hasHandlers(EntityMoveEvent.getHandlerList())) {
				EntityMoveEvent event = new EntityMoveEvent();
				for (World world : WorldUtil.getWorlds()) {
					entityBuffer.addAll(CommonNMS.getNative(world).entityList);
					for (Entity entity : entityBuffer) {
						if (entity.locX != entity.lastX || entity.locY != entity.lastY || entity.locZ != entity.lastZ 
								|| entity.yaw != entity.lastYaw || entity.pitch != entity.lastPitch) {

							event.setEntity(entity);
							CommonUtil.callEvent(event);
						}
					}
					entityBuffer.clear();
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	private static class NextTickListenerProxy implements TimingsListener {
		private final NextTickListener listener;

		public NextTickListenerProxy(NextTickListener listener) {
			this.listener = listener;
		}

		@Override
		public void onNextTicked(Runnable runnable, long executionTime) {
			this.listener.onNextTicked(runnable, executionTime);
		}

		public void onChunkLoad(Chunk chunk, long executionTime) {}
		public void onChunkGenerate(Chunk chunk, long executionTime) {}
		public void onChunkUnloading(World world, long executionTime) {}
	}

	public static class TimingsRootListener implements TimingsListener {

		/**
		 * Gets whether timings are active, and should be informed of information
		 * 
		 * @return True if active, False if not
		 */
		public boolean isActive() {
			return instance != null && !instance.timingsListeners.isEmpty();
		}

		@Override
		public void onNextTicked(Runnable runnable, long executionTime) {
			if (isActive()) {
				try {
					for (int i = 0; i < instance.timingsListeners.size(); i++) {
						instance.timingsListeners.get(i).onNextTicked(runnable, executionTime);
					}
				} catch (Throwable t) {
					LOGGER_TIMINGS.log(Level.SEVERE, "An error occurred while calling timings event", t);
				}
			}
		}

		@Override
		public void onChunkLoad(Chunk chunk, long executionTime) {
			if (isActive()) {
				try {
					for (int i = 0; i < instance.timingsListeners.size(); i++) {
						instance.timingsListeners.get(i).onChunkLoad(chunk, executionTime);
					}
				} catch (Throwable t) {
					LOGGER_TIMINGS.log(Level.SEVERE, "An error occurred while calling timings event", t);
				}
			}
		}

		@Override
		public void onChunkGenerate(Chunk chunk, long executionTime) {
			if (isActive()) {
				try {
					for (int i = 0; i < instance.timingsListeners.size(); i++) {
						instance.timingsListeners.get(i).onChunkGenerate(chunk, executionTime);
					}
				} catch (Throwable t) {
					LOGGER_TIMINGS.log(Level.SEVERE, "An error occurred while calling timings event", t);
				}
			}
		}

		@Override
		public void onChunkUnloading(World world, long executionTime) {
			if (isActive()) {
				try {
					for (int i = 0; i < instance.timingsListeners.size(); i++) {
						instance.timingsListeners.get(i).onChunkUnloading(world, executionTime);
					}
				} catch (Throwable t) {
					LOGGER_TIMINGS.log(Level.SEVERE, "An error occurred while calling timings event", t);
				}
			}
		}
	}
}