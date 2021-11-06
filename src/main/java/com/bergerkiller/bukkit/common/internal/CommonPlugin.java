package com.bergerkiller.bukkit.common.internal;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.MessageBuilder;
import com.bergerkiller.bukkit.common.PluginBase;
import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.Timings;
import com.bergerkiller.bukkit.common.TypedValue;
import com.bergerkiller.bukkit.common.collections.EntityMap;
import com.bergerkiller.bukkit.common.collections.ImplicitlySharedSet;
import com.bergerkiller.bukkit.common.collections.ObjectCache;
import com.bergerkiller.bukkit.common.config.FileConfiguration;
import com.bergerkiller.bukkit.common.controller.EntityController;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.events.CommonEventFactory;
import com.bergerkiller.bukkit.common.events.CreaturePreSpawnEvent;
import com.bergerkiller.bukkit.common.events.EntityAddEvent;
import com.bergerkiller.bukkit.common.events.EntityRemoveEvent;
import com.bergerkiller.bukkit.common.events.EntityRemoveFromServerEvent;
import com.bergerkiller.bukkit.common.internal.hooks.EntityHook;
import com.bergerkiller.bukkit.common.internal.hooks.LookupEntityClassMap;
import com.bergerkiller.bukkit.common.internal.logic.CreaturePreSpawnHandler;
import com.bergerkiller.bukkit.common.internal.logic.EntityAddRemoveHandler;
import com.bergerkiller.bukkit.common.internal.logic.PlayerGameVersionSupplier;
import com.bergerkiller.bukkit.common.internal.logic.PlayerGameVersionSupplier_Vanilla;
import com.bergerkiller.bukkit.common.internal.logic.PlayerGameVersionSupplier_ViaVersion;
import com.bergerkiller.bukkit.common.internal.logic.PortalHandler;
import com.bergerkiller.bukkit.common.internal.map.CommonMapController;
import com.bergerkiller.bukkit.common.internal.network.CommonPacketHandler;
import com.bergerkiller.bukkit.common.internal.network.ProtocolLibPacketHandler;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.metrics.MyDependingPluginsGraph;
import com.bergerkiller.bukkit.common.metrics.SoftDependenciesGraph;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.generated.net.minecraft.nbt.NBTBaseHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityPlayerHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.CraftServerHandle;
import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.SafeField;
import com.bergerkiller.mountiplex.reflection.util.asm.ASMUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.Color;
import java.io.File;
import java.lang.ref.SoftReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings({"rawtypes", "unchecked"})
public class CommonPlugin extends PluginBase {
    /*
     * Timings class
     */
    public static final TimingsRootListener TIMINGS = new TimingsRootListener();
    /*
     * Remaining internal variables
     */
    private static CommonPlugin instance;
    public final List<PluginBase> plugins = new ArrayList<>();
    private EntityMap<Player, CommonPlayerMeta> playerMetadata;
    private CommonListener listener;
    private final ArrayList<SoftReference<EntityMap>> maps = new ArrayList<>();
    private final List<TimingsListener> timingsListeners = new ArrayList<>(1);
    private final List<Task> startedTasks = new ArrayList<>();
    private final ImplicitlySharedSet<org.bukkit.entity.Entity> entitiesRemovedFromServer = new ImplicitlySharedSet<>();
    private final HashMap<String, TypedValue> debugVariables = new HashMap<>();
    private CommonEventFactory eventFactory;
    private boolean isServerStarted = false;
    private PacketHandler packetHandler = null;
    private PermissionHandler permissionHandler = null;
    private CommonMapController mapController = null;
    private CommonImmutablePlayerSetManager immutablePlayerSetManager = null;
    private CommonChunkLoaderPool chunkLoaderPool = null;
    private CommonForcedChunkManager forcedChunkManager = null;
    private CommonVehicleMountManager vehicleMountManager = null;
    private PlayerGameVersionSupplier gameVersionSupplier = null;
    private boolean isFrameTilingSupported = true;
    private boolean isMapDisplaysEnabled = true;
    private boolean teleportPlayersToSeat = true;
    private boolean forceSynchronousSaving = false;
    private boolean isDebugCommandRegistered = false;

    public CommonPlugin() {
        instance = this;
        gameVersionSupplier = new PlayerGameVersionSupplier_Vanilla();
        MountiplexUtil.LOGGER = this.getLogger();
    }

    public static boolean hasInstance() {
        return instance != null;
    }

    public static CommonPlugin getInstance() {
        if (instance == null) {
            throw new RuntimeException("BKCommonLib is not enabled - Plugin Instance can not be obtained! (disjointed Class state?)");
        }
        return instance;
    }

    public void registerMap(EntityMap map) {
        this.maps.add(new SoftReference(map));
    }

    public boolean isServerStarted() {
        return isServerStarted;
    }

    public boolean isFrameTilingSupported() {
        return isFrameTilingSupported;
    }

    public boolean isMapDisplaysEnabled() {
        return isMapDisplaysEnabled;
    }

    public boolean teleportPlayersToSeat() {
        return teleportPlayersToSeat;
    }

    public boolean forceSynchronousSaving() {
        return forceSynchronousSaving;
    }

    public <T> TypedValue<T> getDebugVariable(String name, Class<T> type, T value) {
        registerDebugCommand(); // On first use!

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
        TIMINGS.setActive(!this.timingsListeners.isEmpty());
    }

    public void addTimingsListener(TimingsListener listener) {
        this.timingsListeners.add(listener);
        TIMINGS.setActive(true);
    }

    public void removeTimingsListener(TimingsListener listener) {
        this.timingsListeners.remove(listener);
        TIMINGS.setActive(!this.timingsListeners.isEmpty());
    }

    // called early, too soon to fire an EntityAddEvent, but soon enough to do bookkeeping
    public void notifyAddedEarly(org.bukkit.World world, org.bukkit.entity.Entity e) {
        // Remove from mapping
        this.entitiesRemovedFromServer.remove(e);
    }

    public void notifyAdded(org.bukkit.World world, org.bukkit.entity.Entity e) {
        // Event
        CommonUtil.callEvent(new EntityAddEvent(world, e));
    }

    public void notifyRemoved(org.bukkit.World world, org.bukkit.entity.Entity e) {
        this.entitiesRemovedFromServer.add(e);
        // Event
        CommonUtil.callEvent(new EntityRemoveEvent(world, e));
    }

    public void notifyRemovedFromServer(org.bukkit.World world, org.bukkit.entity.Entity e, boolean removeFromChangeSet) {
        // Also remove from the set tracking these changes
        if (removeFromChangeSet) {
            this.entitiesRemovedFromServer.remove(e);
        }

        // Remove from maps
        Iterator<SoftReference<EntityMap>> iter = this.maps.iterator();
        while (iter.hasNext()) {
            EntityMap map = iter.next().get();
            if (map == null) {
                iter.remove();
            } else {
                map.remove(e);
            }
        }

        // Fire events
        if (CommonUtil.hasHandlers(EntityRemoveFromServerEvent.getHandlerList())) {
            CommonUtil.callEvent(new EntityRemoveFromServerEvent(e));
        }

        // Remove any entity controllers set for the entities that were removed
        EntityHook hook = EntityHook.get(HandleConversion.toEntityHandle(e), EntityHook.class);
        if (hook != null && hook.hasController()) {
            EntityController<?> controller = hook.getController();
            if (controller.getEntity() != null) {
                controller.getEntity().setController(null);
            }
        }
    }

    public void notifyWorldAdded(org.bukkit.World world) {
        CreaturePreSpawnHandler.INSTANCE.onWorldEnabled(world);
        EntityAddRemoveHandler.INSTANCE.onWorldEnabled(world);
    }

    /**
     * Gets the Player Meta Data associated to a Player
     *
     * @param player to get the meta data of
     * @return Player meta data
     */
    public CommonPlayerMeta getPlayerMeta(Player player) {
        synchronized (playerMetadata) {
            CommonPlayerMeta meta = playerMetadata.get(player);
            if (meta == null) {
                playerMetadata.put(player, meta = new CommonPlayerMeta(player));
            }
            return meta;
        }
    }

    /**
     * Obtains the Map Controller that is responsible for maintaining the
     * maps and their associated Map Displays.
     * 
     * @return map controller
     */
    public CommonMapController getMapController() {
        return mapController;
    }

    /**
     * Obtains the Permission Handler used for handling player and console
     * permissions
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
     * Gets the immutable player set manager (as used by {@link ImmutablePlayerSet})
     * 
     * @return immutable player set manager
     */
    public CommonImmutablePlayerSetManager getImmutablePlayerSetManager() {
        return this.immutablePlayerSetManager;
    }

    /**
     * Obtains the Packet Handler used for packet listeners/monitors and packet
     * sending
     *
     * @return packet handler instance
     */
    public PacketHandler getPacketHandler() {
        return packetHandler;
    }

    /**
     * Gets a helper class dealing with the asynchronous chunk loading not supported on later versions
     * of Minecraft natively.
     * 
     * @return chunk loader pool
     */
    public CommonChunkLoaderPool getChunkLoaderPool() {
        return this.chunkLoaderPool;
    }

    /**
     * Gets a helper class that allows to designate chunks as 'force loaded', keeping them loaded.
     * 
     * @return forced chunk manager
     */
    public CommonForcedChunkManager getForcedChunkManager() {
        return this.forcedChunkManager;
    }

    /**
     * Gets the vehicle mount manager, which offers vehicle mount handler facilities
     * 
     * @return vehicle mount manager
     */
    public CommonVehicleMountManager getVehicleMountManager() {
        return this.vehicleMountManager;
    }

    /**
     * Gets the player game version supplier
     *
     * @return game version supplier
     */
    public PlayerGameVersionSupplier getGameVersionSupplier() {
        return this.gameVersionSupplier;
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
                    Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to disable the previous " + this.packetHandler.getName() + " packet handler!");
                    return false;
                }
            }
            this.packetHandler = handler;
            if (!this.packetHandler.onEnable()) {
                Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to enable the " + this.packetHandler.getName() + " packet handler!");
                return false;
            }
            Logging.LOGGER_NETWORK.log(Level.INFO, "Now using " + handler.getName() + " to provide Packet Listener and Monitor support");
            return true;
        } catch (Throwable t) {
        	Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to register a valid Packet Handler:");
            t.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onCriticalStartupFailure(String reason) {
        log(Level.SEVERE, "BKCommonLib and all depending plugins will now disable...");
        super.onCriticalStartupFailure(reason);
    }

    /**
     * Registers the debug variable command. Is done when a debug
     * variable is registered, which is normally not the case.
     */
    public void registerDebugCommand() {
        if (this.isDebugCommandRegistered) {
            return;
        } else {
            this.isDebugCommandRegistered = true;
        }

        try {
            // Create a PluginCommand instance for the /debug command
            // Constructor is protected, so that's fun.
            Command debugCommand;
            {
                Constructor<PluginCommand> constr = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
                constr.setAccessible(true);
                debugCommand = constr.newInstance("debugvar", this);
            }

            // Setup some info
            debugCommand.setDescription("Developer debugging commands for changing values at runtime. Not to be used in production.");
            debugCommand.setUsage("/debugvar [name] [value...]");
            debugCommand.setAliases(Arrays.asList("dvar"));
            debugCommand.setPermission("bkcommonlib.debug.variables");

            // Get command map instance where commands are registered
            CommandMap commandMap = SafeField.get(Bukkit.getPluginManager(), "commandMap", SimpleCommandMap.class);

            // Register the command. Note: does not register with brigadier
            commandMap.register(this.getName(), debugCommand);

            // Rebuild brigadier, otherwise it won't even show up :(
            // Must do this next-tick, as doing this during command execution
            // bricks the command currently being executed. (potentially)
            if (Common.evaluateMCVersion(">=", "1.13")) {
                CommonUtil.nextTick(() -> {
                    try {
                        Method m = Bukkit.getServer().getClass().getMethod("syncCommands");
                        m.invoke(Bukkit.getServer());
                    } catch (Throwable t) {
                        getLogger().log(Level.WARNING, "Failed to update brigadier", t);
                    }
                });
            }
        } catch (Throwable t) {
            getLogger().log(Level.WARNING, "Failed to register debug command", t);
        }
    }

    @Override
    public void permissions() {
    }

    @Override
    public void updateDependency(Plugin plugin, String pluginName, boolean enabled) {
        if (!enabled) {
            packetHandler.removePacketListeners(plugin);
        }
        this.mapController.updateDependency(plugin, pluginName, enabled);
        this.permissionHandler.updateDependency(plugin, pluginName, enabled);
        if (!this.updatePacketHandler()) {
            this.onCriticalStartupFailure("Critical failure updating the packet handler");
            return;
        }

        // ViaVersion detection
        if (pluginName.equals("ViaVersion")) {
            if (enabled) {
                this.gameVersionSupplier = new PlayerGameVersionSupplier_ViaVersion();
                log(Level.INFO, "ViaVersion detected, will use it to detect player game versions");
            } else {
                this.gameVersionSupplier = new PlayerGameVersionSupplier_Vanilla();
                log(Level.INFO, "ViaVersion was disabled, will no longer use it to detect player game versions");
            }
        }
    }

    @Override
    public int getMinimumLibVersion() {
        return 0;
    }

    @Override
    public void onLoad() {
        try {
            if (!Common.IS_COMPATIBLE) {
                return;
            }

            // Modify the BlockStateProxy class on MC <= 1.12.2, because BlockData does not exist there.
            if (Common.evaluateMCVersion("<=", "1.12.2")) {
                this.rewriteClass("com.bergerkiller.bukkit.common.proxies.BlockStateProxy", (plugin, name, classBytes) -> ASMUtil.removeClassMethods(classBytes, new HashSet<String>(Arrays.asList(
                        "getBlockData()Lorg/bukkit/block/data/BlockData;",
                        "setBlockData(Lorg/bukkit/block/data/BlockData;)V"
                ))));
            }

            // Load the classes contained in this library
            CommonClasses.init();
        } catch (Throwable t) {
            getLogger().log(Level.SEVERE, "An error occurred while loading", t);
        }
    }

    @Override
    public void enable() {
        // Validate version
        if (Common.IS_COMPATIBLE) {
            log(Level.INFO, "BKCommonLib is running on " + Common.SERVER.getServerDetails());
        } else {
            String verText = Common.TEMPLATE_RESOLVER.getDebugSupportedVersionsString();
            log(Level.SEVERE, "This version of BKCommonLib is not compatible with: " + Common.SERVER.getServerDetails());
            log(Level.SEVERE, "It could be that BKCommonLib has to be updated, as the current version is built for MC " + verText);
            log(Level.SEVERE, "Please look for a new updated BKCommonLib version that is compatible:");
            log(Level.SEVERE, "https://www.spigotmc.org/resources/bkcommonlib.39590/");
            log(Level.SEVERE, "Unstable development builds for MC " + Common.MC_VERSION + " may be found on our continuous integration server:");
            log(Level.SEVERE, "https://ci.mg-dev.eu/job/BKCommonLib/");
            this.onCriticalStartupFailure("BKCommonLib is not compatible with " + Common.SERVER.getServerDetails());
            return;
        }

        // Set the packet handler to use before enabling further - it could fail!
        if (!this.updatePacketHandler()) {
            this.onCriticalStartupFailure("Critical failure updating the packet handler");
            return;
        }

        // Allow this to fail if there's big problems, as this is an optional requirement
        try {
            CommonBootstrap.preloadCriticalComponents();
        } catch (Throwable t) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to initialize some critical components", t);
        }

        // Load configuration
        FileConfiguration config = new FileConfiguration(this);
        config.load();
        config.setHeader("This is the main configuration file of BKCommonLib");
        config.addHeader("Normally you should not have to make changes to this file");
        config.addHeader("Unused components of the library can be disabled to improve performance");
        config.addHeader("By default all components and features are enabled");
        config.setHeader("enableItemFrameTiling", "\nWhether multiple item frames next to each other can merge to show one large display");
        config.addHeader("enableItemFrameTiling", "This allows Map Displays to be displayed on multiple item frames at a larger resolution");
        config.addHeader("enableItemFrameTiling", "The tiling detection logic poses some overhead on the server, and if unused, can be disabled");
        this.isFrameTilingSupported = config.get("enableItemFrameTiling", true);
        config.setHeader("enableMapDisplays", "\nWhether the Map Display engine is enabled, running in the background to refresh and render maps");
        config.addHeader("enableMapDisplays", "When enabled, the map item tracking may impose a slight overhead");
        config.addHeader("enableMapDisplays", "If no plugin is using map displays, then this can be safely disabled to improve performance");
        this.isMapDisplaysEnabled = config.get("enableMapDisplays", true);
        config.setHeader("teleportPlayersToSeat", "\nWhether to teleport players to their supposed seat while they hold the sneak button");
        config.addHeader("teleportPlayersToSeat", "This is used on Minecraft 1.16 and later to make sure players stay near their seat,");
        config.addHeader("teleportPlayersToSeat", "when exiting the seat was cancelled.");
        this.teleportPlayersToSeat = config.get("teleportPlayersToSeat", true);
        config.setHeader("forceSynchronousSaving", "\nWhether to force saving to be done synchronously, rather than asynchronously");
        config.addHeader("forceSynchronousSaving", "If the Asynchronous File I/O in the JVM has a glitch in it, it might cause very large");
        config.addHeader("forceSynchronousSaving", "corrupt (.yml) files to be generated. On server restart this can cause a loss of data.");
        config.addHeader("forceSynchronousSaving", "Synchronous saving (such as YAML) may hurt server performance for large files,");
        config.addHeader("forceSynchronousSaving", "but will prevent these issues from happening.");
        this.forceSynchronousSaving = config.get("forceSynchronousSaving", false);
        config.setHeader("debugTimings", "\nWhether to instrument additional Timings for some of the core BKCommonLib components");
        config.addHeader("debugTimings", "These timings might be useful to identify performance problems, or their causes");
        config.addHeader("debugTimings", "They may cause a slight performance hit, so leave this option disabled unless you need them");
        final boolean debugTimings = config.get("debugTimings", false);
        config.save();

        // Welcome message
        final List<String> welcomeMessages = Arrays.asList(
                "This library is written with stability in mind.",
                "No Bukkit moderators were harmed while compiling this piece of art.",
                "Have a problem Bukkit can't fix? Write a library!",
                "Bringing home the bacon since 2011!",
                "Completely virus-free and scanned by various Bukkit-dev-staff watching eyes.",
                "Hosts all the features that are impossible to include in a single Class",
                "CraftBukkit: redone, reworked, translated and interfaced.",
                "Having an error? *gasp* Don't forget to file a ticket on github!",
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
                "BKCommonLib isn't a plugin, its a language based on english.",
                "Updating is like reinventing the wheel for BKCommonLib.",
                "Say thanks to our wonderful devs: Friwi, KamikazePlatypus and mg_1999",
                "Welcome to the modern era, welcome to callback hell",
                "Soon generating lambda expressions from thin air!",
                "We have a Discord!",
                "Years of Minecraft history carefully catalogued",
                "50% generated, 50% crafted by an artificial intelligence",
                "Supplier supplying suppliers for your lazy needs!",
                "Please wait while we get our code ready...",
                "60% of the time, it works all the time.",
                "I don't make mistakes. I just find ways not to code this plugin.",
                "Less complicated than the American election.");

        setEnableMessage(welcomeMessages.get(new Random().nextInt(welcomeMessages.size())));
        setDisableMessage(null);

        // Timings, if enabled, otherwise no-op
        if (debugTimings) {
            CommonTimings.QUEUE_PACKET = Timings.create(this, "PacketHandler::queuePacket");
            CommonTimings.SEND_PACKET = Timings.create(this, "PacketHandler::sendPacket");
        }

        // Setup next tick executor
        CommonNextTickExecutor.INSTANCE.setExecutorTask(new CommonNextTickExecutor.ExecutorTask(this));

        // Initialize LookupEntityClassMap and hook it into the server
        try {
            LookupEntityClassMap.hook();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        // Initialize MapColorPalette (static initializer)
        MapColorPalette.getColor(Color.RED);

        // Initialize NBT early
        NBTBaseHandle.T.forceInitialization();

        // Initialize entity add/remove tracking handler
        // Note: onWorldEnabled() is called for all the worlds later on
        EntityAddRemoveHandler.INSTANCE.onEnabled(this);

        // Initialize vehicle mount manager
        vehicleMountManager = new CommonVehicleMountManager(this);
        vehicleMountManager.enable();

        // Initialize chunk loader pool
        chunkLoaderPool = new CommonChunkLoaderPool();

        // Initialize forced chunk manager
        forcedChunkManager = new CommonForcedChunkManager(this);
        forcedChunkManager.enable();

        // Initialize immutable player set manager
        immutablePlayerSetManager = new CommonImmutablePlayerSetManager();

        // Initialize permissions
        permissionHandler = new PermissionHandler();

        // Initialize event factory
        eventFactory = new CommonEventFactory();

        // Initialize portal handling logic
        PortalHandler.INSTANCE.enable(this);

        // Initialize entity map (needs to be here because of CommonPlugin instance needed)
        playerMetadata = new EntityMap<Player, CommonPlayerMeta>();

        // Register events and tasks, initialize
        register(listener = new CommonListener());

        // NO LONGER USED!!!
        // register(new CommonPacketMonitor(), CommonPacketMonitor.TYPES);

        mapController = new CommonMapController();
        if (this.isMapDisplaysEnabled) {
            mapController.onEnable(this, startedTasks);
        }

        startedTasks.add(new MoveEventHandler(this).start(1, 1));
        startedTasks.add(new EntityRemovalHandler(this).start(1, 1));
        startedTasks.add(new CreaturePreSpawnEventHandlerDetectorTask(this).start(0, 20));
        startedTasks.add(new ObjectCacheCleanupTask(this).start(10, 20*60*30));

        // Some servers do not have an Entity Remove Queue.
        // For those servers, we handle them using our own system
        if (!EntityPlayerHandle.T.getRemoveQueue.isAvailable()) {
            startedTasks.add(new EntityRemoveQueueSyncTask(this).start(1, 1));
        }

        // Operations to execute the next tick (when the server has started)
        CommonUtil.nextTick(() -> {
            // Set server started state
            isServerStarted = true;
        });

        // Register listeners and hooks
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

        // Server-specific enabling occurs
        Common.SERVER.enable(this);

        // Parse BKCommonLib version to int
        int version = this.getVersionNumber();
        if (version != Common.VERSION) {
            log(Level.SEVERE, "Common.VERSION needs to be updated to contain '" + version + "'!");
        }
    }

    @Override
    public void disable() {
        // Erase all traces of BKCommonLib from this server
        {
            Collection<Entity> entities = new ArrayList<Entity>();
            for (World world : WorldUtil.getWorlds()) {
                // Unhook potential hooks for this world
                CreaturePreSpawnHandler.INSTANCE.onWorldDisabled(world);
                // Unhook entities
                for (Entity entity : WorldUtil.getEntities(world)) {
                    entities.add(entity);
                }
                for (Entity entity : entities) {
                    CommonEntity.clearControllers(entity);
                }
                entities.clear();
            }
        }

        // Shut down any ongoing tasks for the portal handler
        PortalHandler.INSTANCE.disable(this);

        // Shut down map display controller
        this.mapController.onDisable(this);

        // Disable listeners
        for (World world : Bukkit.getWorlds()) {
            EntityAddRemoveHandler.INSTANCE.onWorldDisabled(world);
        }
        EntityAddRemoveHandler.INSTANCE.onDisabled();
        HandlerList.unregisterAll(listener);
        PacketUtil.removePacketListener(this.mapController);

        // Disable Vehicle mount manager
        this.vehicleMountManager.disable();
        this.vehicleMountManager = null;

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

        // Disable LookupEntityClassMap hook
        try {
            LookupEntityClassMap.unhook();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        // Disable CommonForcedChunkManager
        this.forcedChunkManager.disable(this);
        this.forcedChunkManager = null;

        // Shut down the chunk loader pool, allowing tasks to complete first
        // Set to null to not allow new tasks to be queued
        CommonChunkLoaderPool oldPool = chunkLoaderPool;
        chunkLoaderPool = null;
        oldPool.disable();

        // Wait for pending FileConfiguration save() to complete
        flushSaveOperations(null);

        // Server-specific disabling occurs
        Common.SERVER.disable(this);

        // Run any pending tasks in the next tick executor right now, so they are not forgotten
        // Disable the executor so that future attempts to queue tasks aren't handled by BKCommonLib
        CommonNextTickExecutor.INSTANCE.setExecutorTask(null);

        // Dereference
        MountiplexUtil.unloadMountiplex();
        instance = null;
    }

    @Override
    public boolean command(CommandSender sender, String command, String[] args) {
        if (debugVariables.isEmpty()) {
            return false;
        }
        if (LogicUtil.contains(command, "debugvar", "dvar")) {
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
                List<String> variableNames = new ArrayList<String>(args.length);
                List<TypedValue> variables = new ArrayList<TypedValue>(args.length);
                List<String> values = new ArrayList<String>(args.length);
                for (String arg : args) {
                    TypedValue variable = debugVariables.get(arg);
                    if (variable != null) {
                        variables.add(variable);
                        variableNames.add(arg);
                    } else {
                        values.add(arg);
                    }
                }
                if (variables.size() != values.size()) {
                    if (variables.isEmpty()) {
                        message.red("No debug variable of name '").yellow(values.get(0)).red("'!");
                    } else {
                        for (int i = 0; i < variables.size(); i++) {
                            if (i > 0) message.newLine();
                            message.green("Value of variable '").yellow(variableNames.get(i)).green("' ");
                            message.green("= ");
                            message.white(variables.get(i).toString());
                        }
                    }
                } else {
                    for (int i = 0; i < variables.size(); i++) {
                        TypedValue value = variables.get(i);
                        if (i > 0) message.newLine();
                        message.green("Value of variable '").yellow(variableNames.get(i)).green("' ");
                        message.green("set to ");
                        value.parseSet(values.get(i));
                        message.white(value.toString());
                    }
                }
            }
            message.send(sender);
            return true;
        }
        return false;
    }

    /**
     * Flushes all pending save operations to disk and waits for this to complete
     * 
     * @param plugin The plugin to flush operations for, null to flush all operations
     */
    public static void flushSaveOperations(Plugin plugin) {
        final Logger logger  = (plugin == null) ? Logging.LOGGER_CONFIG : plugin.getLogger();
        final File directory = (plugin == null) ? null : plugin.getDataFolder();
        for (File file : FileConfiguration.findSaveOperationsInDirectory(directory)) {
            // First try and flush the save operation with a timeout of 500 milliseconds
            // if it takes longer than this, log a message and wait for as long as it takes
            if (FileConfiguration.flushSaveOperation(file, 500)) {
                logger.log(Level.INFO, "Saving " + getPluginsRelativePath(plugin, file) + "...");
                FileConfiguration.flushSaveOperation(file);
            }
        }
    }

    // Attempts to turn an absolute path into a path relative to plugins/
    // If this fails FOR ANY REASON, just return the absolute path
    private static String getPluginsRelativePath(Plugin plugin, File file) {
        try {
            File pluginsDirectory;
            if (plugin == null) {
                pluginsDirectory = CraftServerHandle.instance().getPluginsDirectory().getAbsoluteFile();
            } else {
                pluginsDirectory = plugin.getDataFolder().getAbsoluteFile().getParentFile();
            }
            Path pluginsParentPath = pluginsDirectory.getParentFile().toPath();
            Path filePath = file.getAbsoluteFile().toPath();
            return pluginsParentPath.relativize(filePath).toString();
        } catch (Throwable t) {
            return file.getAbsolutePath();
        }
    }

    private static class EntityRemovalHandler extends Task {

        public EntityRemovalHandler(JavaPlugin plugin) {
            super(plugin);
        }

        @Override
        public void run() {
            // Make sure all entity add events have been notified
            // This runs every tick, which guarantees processEvents() is called at least once per tick
            EntityAddRemoveHandler.INSTANCE.processEvents();

            CommonPlugin plugin = getInstance();
            if (plugin.entitiesRemovedFromServer.isEmpty()) {
                return;
            }

            // Iterate all entities pending for removal safely by creating an implicit copy
            // If the set is modified while handling the removal, do a slow set removal one by one
            boolean clearRemovedSet = false;
            try (ImplicitlySharedSet<org.bukkit.entity.Entity> removedCopy = plugin.entitiesRemovedFromServer.clone()) {
                for (org.bukkit.entity.Entity removedEntity : removedCopy) {
                    plugin.notifyRemovedFromServer(removedEntity.getWorld(), removedEntity, false);
                }

                // If the set is not modified, we can simply clear() the set (much faster)
                if (plugin.entitiesRemovedFromServer.refEquals(removedCopy)) {
                    clearRemovedSet = true; // Do outside try() so that the copy is closed
                } else {
                    plugin.entitiesRemovedFromServer.removeAll(removedCopy);
                }
            }
            if (clearRemovedSet) {
                plugin.entitiesRemovedFromServer.clear();
            }
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

    private static class EntityRemoveQueueSyncTask extends Task {

        public EntityRemoveQueueSyncTask(JavaPlugin plugin) {
            super(plugin);
        }

        @Override
        public void run() {
            if (CommonPlugin.hasInstance()) {
                for (CommonPlayerMeta meta : CommonPlugin.getInstance().playerMetadata.values()) {
                    meta.syncRemoveQueue();
                }
            }
        }
    }

    private static class CreaturePreSpawnEventHandlerDetectorTask extends Task {
        private boolean _creaturePreSpawnEventHasHandlers;

        public CreaturePreSpawnEventHandlerDetectorTask(JavaPlugin plugin) {
            super(plugin);
            this._creaturePreSpawnEventHasHandlers = CommonUtil.hasHandlers(CreaturePreSpawnEvent.getHandlerList());
        }

        @Override
        public void run() {
            boolean hasHandlers = CommonUtil.hasHandlers(CreaturePreSpawnEvent.getHandlerList());
            if (hasHandlers != this._creaturePreSpawnEventHasHandlers) {
                this._creaturePreSpawnEventHasHandlers = hasHandlers;
                if (hasHandlers) {
                    CreaturePreSpawnHandler.INSTANCE.onEventHasHandlers();
                }
            }
        }
    }

    // Runs every so often (30 minutes) to wipe all the default ObjectCache instances
    // Makes sure that if somebody uses a lot of them once, they can be freed up
    // Also makes sure that collections that grow internal buffers get cleaned up
    private static class ObjectCacheCleanupTask extends Task {

        public ObjectCacheCleanupTask(JavaPlugin plugin) {
            super(plugin);
        }

        @Override
        public void run() {
            ObjectCache.clearDefaultCaches();
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

        public void onChunkLoad(Chunk chunk, long executionTime) {
        }

        public void onChunkGenerate(Chunk chunk, long executionTime) {
        }

        public void onChunkUnloading(World world, long executionTime) {
        }

        public void onChunkPopulate(Chunk chunk, BlockPopulator populator, long executionTime) {
        }
    }

    public static class TimingsRootListener implements TimingsListener {
        private boolean active = false;

        /**
         * Gets whether timings are active, and should be informed of
         * information
         *
         * @return True if active, False if not
         */
        public final boolean isActive() {
            return active;
        }

        /**
         * Sets whether the timings listener is active.
         * Set automatically when timings listeners are added/removed.
         * 
         * @param active state to set to
         */
        private final void setActive(boolean active) {
            this.active = active;
        }

        @Override
        public void onNextTicked(Runnable runnable, long executionTime) {
            if (this.active) {
                try {
                    for (TimingsListener element : instance.timingsListeners) {
                        element.onNextTicked(runnable, executionTime);
                    }
                } catch (Throwable t) {
                	Logging.LOGGER_TIMINGS.log(Level.SEVERE, "An error occurred while calling timings event", t);
                }
            }
        }

        @Override
        public void onChunkLoad(Chunk chunk, long executionTime) {
            if (this.active) {
                try {
                    for (TimingsListener element : instance.timingsListeners) {
                        element.onChunkLoad(chunk, executionTime);
                    }
                } catch (Throwable t) {
                	Logging.LOGGER_TIMINGS.log(Level.SEVERE, "An error occurred while calling timings event", t);
                }
            }
        }

        @Override
        public void onChunkGenerate(Chunk chunk, long executionTime) {
            if (this.active) {
                try {
                    for (TimingsListener element : instance.timingsListeners) {
                        element.onChunkGenerate(chunk, executionTime);
                    }
                } catch (Throwable t) {
                	Logging.LOGGER_TIMINGS.log(Level.SEVERE, "An error occurred while calling timings event", t);
                }
            }
        }

        @Override
        public void onChunkUnloading(World world, long executionTime) {
            if (this.active) {
                try {
                    for (TimingsListener element : instance.timingsListeners) {
                        element.onChunkUnloading(world, executionTime);
                    }
                } catch (Throwable t) {
                	Logging.LOGGER_TIMINGS.log(Level.SEVERE, "An error occurred while calling timings event", t);
                }
            }
        }

        @Override
        public void onChunkPopulate(Chunk chunk, BlockPopulator populator, long executionTime) {
            if (this.active) {
                try {
                    for (TimingsListener element : instance.timingsListeners) {
                        element.onChunkPopulate(chunk, populator, executionTime);
                    }
                } catch (Throwable t) {
                	Logging.LOGGER_TIMINGS.log(Level.SEVERE, "An error occurred while calling timings event", t);
                }
            }
        }
    }
}
