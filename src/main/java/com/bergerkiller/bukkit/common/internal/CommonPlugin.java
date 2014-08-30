package com.bergerkiller.bukkit.common.internal;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.MessageBuilder;
import com.bergerkiller.bukkit.common.ModuleLogger;
import com.bergerkiller.bukkit.common.PluginBase;
import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.TypedValue;
import com.bergerkiller.bukkit.common.collections.EntityMap;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.events.CommonEventFactory;
import com.bergerkiller.bukkit.common.events.EntityRemoveFromServerEvent;
import com.bergerkiller.bukkit.common.internal.network.CommonPacketHandler;
import com.bergerkiller.bukkit.common.internal.network.ProtocolLibPacketHandler;
import com.bergerkiller.bukkit.common.metrics.MyDependingPluginsGraph;
import com.bergerkiller.bukkit.common.metrics.SoftDependenciesGraph;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;

@SuppressWarnings({"rawtypes", "unchecked"})
public class CommonPlugin extends PluginBase {
	/*
	 * Loggers for internal BKCommonLib processes
	 */
	public static final ModuleLogger LOGGER = new ModuleLogger("BKCommonLib");
	public static final ModuleLogger LOGGER_CONVERSION = LOGGER.getModule("Conversion");
	public static final ModuleLogger LOGGER_REFLECTION = LOGGER.getModule("Reflection");
	public static final ModuleLogger LOGGER_NETWORK = LOGGER.getModule("Network");
	public static final ModuleLogger LOGGER_TIMINGS = LOGGER.getModule("Timings");
	public static final ModuleLogger LOGGER_PERMISSIONS = LOGGER.getModule("Permissions");
	/*
	 * Timings class
	 */
	public static final TimingsRootListener TIMINGS = new TimingsRootListener();
	/*
	 * Remaining internal variables
	 */
	private static CommonPlugin instance;
	public final List<PluginBase> plugins = new ArrayList<PluginBase>();
	private EntityMap<Player, CommonPlayerMeta> playerVisibleChunks;
	protected final Map<World, CommonWorldListener> worldListeners = new HashMap<World, CommonWorldListener>();
	private CommonListener listener;
	private final ArrayList<SoftReference<EntityMap>> maps = new ArrayList<SoftReference<EntityMap>>();
	private final List<Runnable> nextTickTasks = new ArrayList<Runnable>();
	private final List<Runnable> nextTickSync = new ArrayList<Runnable>();
	private final List<TimingsListener> timingsListeners = new ArrayList<TimingsListener>(1);
	private final List<Task> startedTasks = new ArrayList<Task>();
	private final HashSet<org.bukkit.entity.Entity> entitiesToRemove = new HashSet<org.bukkit.entity.Entity>();
	private final HashMap<String, TypedValue> debugVariables = new HashMap<String, TypedValue>();
	private CommonEventFactory eventFactory;
	private boolean isServerStarted = false;
	private PacketHandler packetHandler = null;
	private PermissionHandler permissionHandler = null;
	private CommonTabController tabController = null;
	private CommonEntityBlacklist entityBlacklist = null;

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

	public boolean isServerStarted() {
		return isServerStarted;
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

	/**
	 * Gets the Player Meta Data associated to a Player
	 * 
	 * @param player to get the meta data of
	 * @return Player meta data
	 */
	public CommonPlayerMeta getPlayerMeta(Player player) {
		synchronized (playerVisibleChunks) {
			CommonPlayerMeta meta = playerVisibleChunks.get(player);
			if (meta == null) {
				playerVisibleChunks.put(player, meta = new CommonPlayerMeta(player));
			}
			return meta;
		}
	}

	/**
	 * Obtains the Entity Blacklist that filters out Entities that are part of other plugin's logic.
	 * This includes entities that represent particles or virtual items.
	 * 
	 * @return entity blacklist
	 */
	public CommonEntityBlacklist getEntityBlacklist() {
		return entityBlacklist;
	}

	/**
	 * Obtains the Tab Controller that is responsible for the creation and updating of tabs
	 * 
	 * @return tab controller
	 */
	public CommonTabController getTabController() {
		return tabController;
	}

	/**
	 * Obtains the Permission Handler used for handling player and console permissions
	 * 
	 * @return permission handler
	 */
	public PermissionHandler getPermissionHandler() {
		return permissionHandler;
	}

	/**
	 * Obtains the event factory used to raise events for server happenings
	 * 
	 * @return event factory
	 */
	public CommonEventFactory getEventFactory() {
		return eventFactory;
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
//			} else if (CommonUtil.getClass("org.spigotmc.netty.NettyServerConnection") != null) {
//				handlerClass = SpigotPacketHandler.class;
			} else {
				handlerClass = CommonPacketHandler.class;
//			} else {
//				handlerClass = DisabledPacketHandler.class;
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
		this.entityBlacklist.updateDependency(plugin, pluginName, enabled);
		this.permissionHandler.updateDependency(plugin, pluginName, enabled);
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
		if (!Common.IS_COMPATIBLE) {
			return;
		}
		// Load the classes contained in this library
		CommonClasses.init();
	}

	@Override
	public void disable() {
		// Disable listeners
		for (CommonWorldListener listener : worldListeners.values()) {
			listener.disable();
		}
		worldListeners.clear();
		HandlerList.unregisterAll(listener);

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

		// Erase all traces of BKCommonLib from this server
		Collection<Entity> entities = new ArrayList<Entity>();
		for (World world : WorldUtil.getWorlds()) {
			// Unhook chunk providers
			ChunkProviderServerHook.unhook(world);
			// Unhook entities
			entities.addAll(WorldUtil.getEntities(world));
			for (Entity entity : entities) {
				CommonEntity.clearControllers(entity);
			}
			entities.clear();
		}

		// Server-specific disabling occurs
		Common.SERVER.disable(this);

		// Dereference
		instance = null;
	}

	@Override
	public void enable() {
		// Validate version
		final StringBuilder serverDesc = new StringBuilder(300);
		serverDesc.append(Common.SERVER.getServerName()).append(" (");
		serverDesc.append(Common.SERVER.getServerDescription());
		serverDesc.append(") : ").append(Common.SERVER.getServerVersion());
		if (Common.IS_COMPATIBLE) {
			log(Level.INFO, "BKCommonLib is running on " + serverDesc);
		} else {
			log(Level.SEVERE, "This version of BKCommonLib is not compatible with: " + serverDesc);
			log(Level.SEVERE, "It could be that BKCommonLib has to be updated, as the current version is built for MC " + Common.DEPENDENT_MC_VERSION);
			log(Level.SEVERE, "Please look for an available BKCommonLib version that is compatible:");
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
				"Now a plugin, a new server implementation tomorrow???",
				"Providing support for supporting the unsupportable.",
				"Every feature break in Bukkit makes my feature list longer.",
				"I...I forgot an exclamation mark...*rages internally*", 
				"I am still winning the game. Are you?",
				"We did what our big brother couldn't",
				"If you need syntax help visit javadocs.a.b.v1_2_3.net",
				"v1_1_R1 1+1+1 = 3, Half life 3 confirmed?",
				"BKCommonLib > Minecraft.a.b().q.f * Achievement.OBFUSCATED.value",
				"BKCommonLib isn't a plugin, its a language based on english.");
		setEnableMessage(welcomeMessages.get((int) (Math.random() * welcomeMessages.size())));
		setDisableMessage(null);

		// Initialize permissions
		permissionHandler = new PermissionHandler();

		// Initialize event factory
		eventFactory = new CommonEventFactory();

		// Initialize entity map (needs to be here because of CommonPlugin instance needed)
		playerVisibleChunks = new EntityMap<Player, CommonPlayerMeta>();

		// Initialize Entity Blacklist
		entityBlacklist = new CommonEntityBlacklist();

		// Register events and tasks, initialize
		register(listener = new CommonListener());
		register(new CommonPacketMonitor(), CommonPacketMonitor.TYPES);
		register(tabController = new CommonTabController());
		PacketUtil.addPacketListener(this, tabController, PacketType.OUT_PLAYER_INFO);
		startedTasks.add(new NextTickHandler(this).start(1, 1));
		startedTasks.add(new MoveEventHandler(this).start(1, 1));
		startedTasks.add(new EntityRemovalHandler(this).start(1, 1));
		startedTasks.add(new TabUpdater(this).start(1, 1));

		// Operations to execute the next tick (when the server has started)
		CommonUtil.nextTick(new Runnable() {
			public void run() {
				// Set server started state
				isServerStarted = true;
				// Tell the tabs to initialize the initial dimensions
				getTabController().setDefaultSize();
			}
		});

		// Register listeners and hooks
		for (World world : WorldUtil.getWorlds()) {
			ChunkProviderServerHook.hook(world);
			notifyWorldAdded(world);
		}

		// BKCommonLib Metrics
		if (hasMetrics()) {
			// Soft dependencies
			getMetrics().addGraph(new SoftDependenciesGraph());

			// Depending
			getMetrics().addGraph(new MyDependingPluginsGraph());
		}

		// Server-specific enabling occurs
		Common.SERVER.enable(this);

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
						value.parseSet(StringUtil.join(" ", StringUtil.remove(args, 0)));
					}
					message.white(value.toString());
				}
			}
			message.send(sender);
			return true;
		}
		return false;
	}

	private static class TabUpdater extends Task {
		public TabUpdater(JavaPlugin plugin) {
			super(plugin);
		}

		@Override
		public void run() {
			getInstance().getTabController().refreshAllTabs();
		}
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
		public MoveEventHandler(JavaPlugin plugin) {
			super(plugin);
		}

		@Override
		public void run() {
			CommonPlugin.getInstance().getEventFactory().handleEntityMove();
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
		public void onChunkPopulate(Chunk chunk, BlockPopulator populator, long executionTime) {}
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

		@Override
		public void onChunkPopulate(Chunk chunk, BlockPopulator populator, long executionTime) {
			if (isActive()) {
				try {
					for (int i = 0; i < instance.timingsListeners.size(); i++) {
						instance.timingsListeners.get(i).onChunkPopulate(chunk, populator, executionTime);
					}
				} catch (Throwable t) {
					LOGGER_TIMINGS.log(Level.SEVERE, "An error occurred while calling timings event", t);
				}
			}
		}
	}
}