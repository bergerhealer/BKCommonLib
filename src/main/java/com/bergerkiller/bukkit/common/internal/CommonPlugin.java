package com.bergerkiller.bukkit.common.internal;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.MessageBuilder;
import com.bergerkiller.bukkit.common.PluginBase;
import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.TypedValue;
import com.bergerkiller.bukkit.common.bases.CheckedRunnable;
import com.bergerkiller.bukkit.common.collections.EntityMap;
import com.bergerkiller.bukkit.common.collections.ImplicitlySharedSet;
import com.bergerkiller.bukkit.common.collections.ObjectCache;
import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.component.LibraryComponentList;
import com.bergerkiller.bukkit.common.config.FileConfiguration;
import com.bergerkiller.bukkit.common.controller.EntityController;
import com.bergerkiller.bukkit.common.conversion.type.DimensionResourceKeyConversion;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.MC1_18_2_Conversion;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.events.CommonEventFactory;
import com.bergerkiller.bukkit.common.events.CreaturePreSpawnEvent;
import com.bergerkiller.bukkit.common.events.EntityAddEvent;
import com.bergerkiller.bukkit.common.events.EntityRemoveEvent;
import com.bergerkiller.bukkit.common.events.EntityRemoveFromServerEvent;
import com.bergerkiller.bukkit.common.events.PlayerAdvancementProgressEvent;
import com.bergerkiller.bukkit.common.internal.hooks.AdvancementDataPlayerHook;
import com.bergerkiller.bukkit.common.internal.hooks.EntityHook;
import com.bergerkiller.bukkit.common.internal.hooks.LookupEntityClassMap;
import com.bergerkiller.bukkit.common.internal.logic.BlockDataWrapperHook;
import com.bergerkiller.bukkit.common.internal.logic.BlockPhysicsEventDataAccessor;
import com.bergerkiller.bukkit.common.internal.logic.ChunkHandleTracker;
import com.bergerkiller.bukkit.common.internal.logic.CreaturePreSpawnHandler;
import com.bergerkiller.bukkit.common.internal.logic.EntityAddRemoveHandler;
import com.bergerkiller.bukkit.common.internal.logic.PlayerGameInfoSupplier_ViaVersion;
import com.bergerkiller.bukkit.common.internal.logic.PortalHandler;
import com.bergerkiller.bukkit.common.internal.map.CommonMapController;
import com.bergerkiller.bukkit.common.internal.network.CommonPacketHandler;
import com.bergerkiller.bukkit.common.internal.network.ProtocolLibPacketHandler;
import com.bergerkiller.bukkit.common.internal.permissions.PermissionHandler;
import com.bergerkiller.bukkit.common.internal.permissions.PermissionHandlerSelector;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.util.RGBColorToIntConversion;
import com.bergerkiller.bukkit.common.offline.OfflineWorld;
import com.bergerkiller.bukkit.common.protocol.PlayerGameInfo;
import com.bergerkiller.bukkit.common.regionflagtracker.RegionFlagRegistryBaseImpl;
import com.bergerkiller.bukkit.common.softdependency.SoftDependency;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.generated.net.minecraft.nbt.NBTBaseHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityPlayerHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.CraftServerHandle;
import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.SafeField;
import com.bergerkiller.mountiplex.reflection.util.asm.ASMUtil;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
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
import org.incendo.cloud.minecraft.extras.AudienceProvider;

import java.io.File;
import java.lang.ref.SoftReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
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
    private final LibraryComponentList<CommonPlugin> components = LibraryComponentList.forPlugin(this);
    private EntityMap<Player, CommonPlayerMeta> playerMetadata;
    private CommonListener listener;
    private final ArrayList<SoftReference<EntityMap>> maps = new ArrayList<>();
    private final List<TimingsListener> timingsListeners = new ArrayList<>(1);
    private final List<Task> startedTasks = new ArrayList<>();
    private final ImplicitlySharedSet<org.bukkit.entity.Entity> entitiesRemovedFromServer = new ImplicitlySharedSet<>();
    private final HashMap<String, TypedValue> debugVariables = new HashMap<>();
    private final ThreadPoolExecutor fileIOWorker;
    private CommonEventFactory eventFactory;
    private boolean isServerStarted = false;
    private PacketHandler packetHandler = null;
    private boolean warnedAboutBrokenBundlePacket = false;
    private final PermissionHandlerSelector permissionHandlerSelector = new PermissionHandlerSelector(this);
    private CommonServerLogRecorder serverLogRecorder = new CommonServerLogRecorder(this);
    private CommonMapController mapController = null;
    private CommonForcedChunkManager forcedChunkManager = null;
    private CommonVehicleMountManager vehicleMountManager = null;
    private Function<Player, PlayerGameInfo> gameInfoSupplier = p -> PlayerGameInfo.SERVER;
    private boolean isFrameTilingSupported = true;
    private boolean isFrameDisplaysEnabled = true;
    private boolean isMapDisplaysEnabled = true;
    private boolean teleportPlayersToSeat = true;
    private boolean forceSynchronousSaving = false;
    private boolean isDebugCommandRegistered = false;
    private boolean cloudDisableBrigadier = false;
    private boolean enableProtocolLibPacketHandler = true;
    private Material fallbackItemModelType = null;

    public CommonPlugin() {
        // Before proceeding, make sure the jar file isn't lacking required stuff
        // If it lacks stuff, throw an error here. This prevents anything more of BKCommonLib from being loaded.
        if (!CommonBootstrap.verifyShadedAssets(getLogger())) {
            throw new IllegalStateException("BKCommonLib jar is corrupt! Please redownload.");
        }

        // Initialize thread pool, might be used pretty early on...
        // The IO worker is shut down if no IO occurs for 60 seconds
        // Up to 3 concurrent IO workers can be active at once.
        fileIOWorker = new ThreadPoolExecutor(3, 3, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(1024), r -> {
            Thread t = new Thread(r, "BKCommonLib-IOWorker");
            t.setDaemon(true);
            return t;
        });
        fileIOWorker.allowCoreThreadTimeOut(true);

        // When mountiplex logs, log as BKCommonLib
        MountiplexUtil.LOGGER = this.getLogger();

        // Now it's open for business
        instance = this;
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

    public static CompletableFuture<Void> runIOTaskAsync(CheckedRunnable runnable) {
        CommonPlugin instance = CommonPlugin.instance;
        if (instance != null) {
            return CommonUtil.runCheckedAsync(runnable, instance.getFileIOExecutor());
        } else {
            return CommonUtil.runCheckedAsync(runnable); // Unit test or late write - use java's own threadpool
        }
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

    public boolean isFrameDisplaysEnabled() {
        return isFrameDisplaysEnabled;
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

    public boolean isCloudBrigadierDisabled() {
        return cloudDisableBrigadier;
    }

    public Material getFallbackItemModelType() {
        Material m = fallbackItemModelType;
        if (m == null) {
            fallbackItemModelType = m = getDefaultFallbackItemModelType();
        }
        return m;
    }

    /**
     * Gets the default fallback item model type that this library chooses, if not overridden
     * in the configuration.
     *
     * @return Default item model type material
     */
    public static Material getDefaultFallbackItemModelType() {
        return MaterialUtil.getFirst("IRON_NUGGET", "LEGACY_IRON_NUGGET", "LEGACY_GOLD_NUGGET");
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
        return permissionHandlerSelector.current();
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
     * Obtains the Packet Handler used for packet listeners/monitors and packet
     * sending
     *
     * @return packet handler instance
     */
    public PacketHandler getPacketHandler() {
        return packetHandler;
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
     * Gets the game information of a Player
     *
     * @return game info
     */
    public PlayerGameInfo getGameInfo(Player player) {
        return this.gameInfoSupplier.apply(player);
    }

    /**
     * Gets the server log recorder, which stores the last logged messages of the
     * server / system loggers.
     *
     * @return Server log recorder
     */
    public CommonServerLogRecorder getServerLogRecorder() {
        return this.serverLogRecorder;
    }

    /**
     * Gets the asynchronous executor responsible for file reading/writing I/O
     *
     * @return IO worker executor
     */
    public Executor getFileIOExecutor() {
        return this.fileIOWorker;
    }

    private boolean updatePacketHandler() {
        try {
            Class<? extends PacketHandler> handlerClass = CommonPacketHandler.class;
            if (this.enableProtocolLibPacketHandler && CommonUtil.isPluginEnabled("ProtocolLib")) {
                if (ProtocolLibPacketHandler.isBundlePacketWorking()) {
                    handlerClass = ProtocolLibPacketHandler.class;
                } else if (!warnedAboutBrokenBundlePacket) {
                    warnedAboutBrokenBundlePacket = true;
                    Logging.LOGGER_NETWORK.log(Level.WARNING, "ProtocolLib cannot be used because it does not support the Bundle packet yet");
                    Logging.LOGGER_NETWORK.log(Level.WARNING, "Please update ProtocolLib to a 1.19.4+ supporting version (build #620 or newer)");
                }
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
            Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to register a valid Packet Handler", t);
            return false;
        }
    }

    @Override
    protected void onCriticalStartupFailure(String reason) {
        try {
            if (CommonBootstrap.isCommonServerInitialized()) {
                log(Level.INFO, "Failed to initialize for server " + CommonBootstrap.initCommonServer().getServerDetails());
            }
        } catch (Throwable t) { /* ignore */ }
        log(Level.SEVERE, "BKCommonLib and all depending plugins will now disable...");
        super.onCriticalStartupFailure(reason);

        // We don't need server logs beyond this - just to make sure it's stopped
        serverLogRecorder.disable();
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
        if (!this.updatePacketHandler()) {
            this.onCriticalStartupFailure("Critical failure updating the packet handler");
            return;
        }

        // ViaVersion detection
        if (pluginName.equals("ViaVersion")) {
            if (enabled) {
                this.gameInfoSupplier = new PlayerGameInfoSupplier_ViaVersion();
                log(Level.INFO, "ViaVersion detected, will use it to detect player game versions");
            } else {
                this.gameInfoSupplier = p -> PlayerGameInfo.SERVER;
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

            // Must be loaded early
            gameInfoSupplier = p -> PlayerGameInfo.SERVER;

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

        config.setHeader("enableMapDisplays", "\nWhether the Map Display engine is enabled, running in the background to refresh and render maps");
        config.addHeader("enableMapDisplays", "When enabled, the map item tracking may impose a slight overhead");
        config.addHeader("enableMapDisplays", "If no plugin is using map displays, then this can be safely disabled to improve performance");
        this.isMapDisplaysEnabled = config.get("enableMapDisplays", true);

        // If java.awt is not available, the map display API won't work. Just disable it, and log an error.
        if (this.isMapDisplaysEnabled && CommonBootstrap.isHeadlessJDK()) {
            this.isMapDisplaysEnabled = false;
            Logging.LOGGER_MAPDISPLAY.log(Level.SEVERE, "The Map Displays feature has been turned off because the server is incompatible");
            Logging.LOGGER_MAPDISPLAY.log(Level.SEVERE, "Reason: The Java AWT runtime library is not available");
            Logging.LOGGER_MAPDISPLAY.log(Level.SEVERE, "This is usually because a headless JVM is used for the server");
            Logging.LOGGER_MAPDISPLAY.log(Level.SEVERE, "Please install and configure a non-headless JVM to have Map Displays work");
        }

        config.setHeader("enableItemFrameDisplays", "\nWhether all item frames on the server are tracked to see if they display a map display.");
        config.addHeader("enableItemFrameDisplays", "This allows for map displays to be displayed on item frames and interacted with.");
        config.addHeader("enableItemFrameDisplays", "If 'enableItemFrameTiling' is also true, then this allows for multi-item frame displays.");
        config.addHeader("enableItemFrameDisplays", "Tracking the existence of all item frames on the server can pose an overhead, as");
        config.addHeader("enableItemFrameDisplays", "shown under the 'MapDisplayFramedMapUpdater' task. Turning this off can help performance.");
        config.addHeader("enableItemFrameDisplays", "If 'enableMapDisplays' is true then player-held maps will continue working fine.");
        this.isFrameDisplaysEnabled = config.get("enableItemFrameDisplays", true);
        config.setHeader("enableItemFrameTiling", "\nWhether multiple item frames next to each other can merge to show one large display");
        config.addHeader("enableItemFrameTiling", "This allows Map Displays to be displayed on multiple item frames at a larger resolution");
        config.addHeader("enableItemFrameTiling", "The tiling detection logic poses some overhead on the server, and if unused, can be disabled");
        this.isFrameTilingSupported = config.get("enableItemFrameTiling", true);
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
        config.setHeader("cloudDisableBrigadier", "\nWhether to disable using brigadier for all plugins that use BKCL's cloud command framework");
        config.addHeader("cloudDisableBrigadier", "This might fix problems that occur because of bugs in brigadier, or cloud's handler of it");
        this.cloudDisableBrigadier = config.get("cloudDisableBrigadier", false);
        config.setHeader("preloadTemplateClasses", "\nWhether to load and initialize ALL template classes when BKCommonLib first loads up.");
        config.addHeader("preloadTemplateClasses", "This reveals any at-runtime server incompatibility errors early on and eliminates any");
        config.addHeader("preloadTemplateClasses", "at-runtime lazy initialization lag. It does cause a lot of classes to be loaded into the");
        config.addHeader("preloadTemplateClasses", "JVM that may never get used, which wastes memory. Only enable this for debugging reasons!");
        config.addHeader("preloadTemplateClasses", "As loading is done on all CPU cores, this might improve boot performance on multi-core systems");
        final boolean preloadTemplateClasses = config.get("preloadTemplateClasses", false);
        config.setHeader("trackForcedChunkCreationStack", "\nWhether to track the stack trace of where forced chunks are created");
        config.addHeader("trackForcedChunkCreationStack", "This is useful to detect ForcedChunk instances that are not closed by the developer.");
        config.addHeader("trackForcedChunkCreationStack", "Once a missed close is detected, tracking is automatically started anyway.");
        config.addHeader("trackForcedChunkCreationStack", "As such, this option is primarily useful to diagnose this problem at server startup");
        boolean trackForcedChunkCreationStack = config.get("trackForcedChunkCreationStack", false);
        config.setHeader("fallbackItemModelType", "\nMaterial type to use for custom-namespace item models in resource pack listing");
        config.addHeader("fallbackItemModelType", "Is used on Minecraft 1.21.2 and later with the item_model data component");
        this.fallbackItemModelType = config.get("fallbackItemModelType", getDefaultFallbackItemModelType());
        config.setHeader("enableProtocolLibPacketHandler", "\nWhether to use ProtocolLib for handling packets, if that plugin is installed");
        config.addHeader("enableProtocolLibPacketHandler", "Disabling this could cause bugs when multiple plugins mess with packets");
        this.enableProtocolLibPacketHandler = config.get("enableProtocolLibPacketHandler", true);
        config.save();

        if (preloadTemplateClasses) {
            CommonBootstrap.preloadTemplateClasses(null);
            BlockPhysicsEventDataAccessor.init();
        }

        // Set the packet handler to use before enabling further - it could fail!
        if (!this.updatePacketHandler()) {
            this.onCriticalStartupFailure("Critical failure updating the packet handler");
            return;
        }

        new Task(this) {
            @Override
            public void run() {
                try (BukkitAudiences aud = BukkitAudiences.create(this.getPlugin())) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage("OLD STYLE HELLO");
                        aud.player(player).sendMessage(Component.text("Hello!"));
                    }
                }
            }
        }.start(20, 20);

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
                "Less complicated than the American election.",
                "Bless you SpottedLeaf <3",
                "Includes records and ways to change them!",
                "Working hard to not do too much",
                "Psst! I am backwards-compatible! Don't forget to update me regularly... :(");

        setEnableMessage(welcomeMessages.get(new Random().nextInt(welcomeMessages.size())));
        setDisableMessage(null);

        // Enable all components in order
        this.components.enable(this.serverLogRecorder);
        this.components.enable(new CommonRegionChangeTracker(this));
        this.components.enable("Region Flag Change Tracker", new LibraryComponent() {
            @Override
            public void enable() {
                RegionFlagRegistryBaseImpl.instance().enable(CommonPlugin.this);
            }

            @Override
            public void disable() {
                RegionFlagRegistryBaseImpl.instance().disable();
            }
        });
        this.components.enableForVersions("Dimension to Holder conversion", "1.18.2", null,
                MC1_18_2_Conversion::initComponent);
        this.components.enableCreate(OfflineWorld::initializeComponent);
        this.components.enableForVersions("Dimension resource key tracker", "1.16", "1.16.1",
                DimensionResourceKeyConversion.Tracker::new);
        this.components.enableForVersions("Dimension resource key tracker", "1.19", null,
                DimensionResourceKeyConversion.Tracker::new);

        // Start tracking chunk - handle conversion
        ChunkHandleTracker.INSTANCE.startTracking(this);

        // Enable BlockData hook stuff, we want the initialization error early
        BlockDataWrapperHook.init();

        // Setup next tick executor
        CommonNextTickExecutor.INSTANCE.setExecutorTask(new CommonNextTickExecutor.ExecutorTask(this));

        // Initialize LookupEntityClassMap and hook it into the server
        try {
            LookupEntityClassMap.hook();
        } catch (Throwable t) {
            getLogger().log(Level.SEVERE, "Failed to hook LookupEntityClassMap", t);
        }

        // Initialize MapColorPalette (static initializer)
        // Do not do this if map displays are disabled.
        if (this.isMapDisplaysEnabled) {
            MapColorPalette.getColor(0, 0, 0);

            // Log a message if java's experimental incubator vector api is used for loading images into map colors
            if (RGBColorToIntConversion.ABGR.isUsingSIMD()) {
                getLogger().log(Level.INFO, "JDK17+ incubator vector maths are enabled. Will use it for loading MapDisplay textures.");
            }
        }

        // Initialize NBT early
        NBTBaseHandle.T.forceInitialization();

        // Initialize entity add/remove tracking handler
        // Note: onWorldEnabled() is called for all the worlds later on
        EntityAddRemoveHandler.INSTANCE.onEnabled(this);

        // Initialize vehicle mount manager
        vehicleMountManager = new CommonVehicleMountManager(this);
        vehicleMountManager.enable();

        // Initialize forced chunk manager
        forcedChunkManager = new CommonForcedChunkManager(this, trackForcedChunkCreationStack);
        forcedChunkManager.enable();

        // Initialize permissions based on current state
        SoftDependency.detectAll(permissionHandlerSelector);
        permissionHandlerSelector.detectPermOption();

        // Initialize event factory
        eventFactory = new CommonEventFactory();

        // Initialize portal handling logic
        PortalHandler.INSTANCE.enable(this);

        // Initialize entity map (needs to be here because of CommonPlugin instance needed)
        playerMetadata = new EntityMap<Player, CommonPlayerMeta>();

        // Register events and tasks, initialize
        register(listener = new CommonListener());

        // Only used 1.20.2+
        if (CommonCapabilities.HAS_SIGN_OPEN_EVENT_PAPER) {
            try {
                CommonSignOpenListenerPaper.register(this);
            } catch (Throwable t) {
                getLogger().log(Level.SEVERE, "Failed to listen for paper's open sign event", t);
            }
        } else if (CommonCapabilities.HAS_SIGN_OPEN_EVENT) {
            register(new CommonSignOpenListenerBukkit());
        }

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

        // Ensure advancements are hooked, if event is used
        if (CommonCapabilities.HAS_ADVANCEMENTS) {
            startedTasks.add(new AdvancementProgressEventHandlerDetectorTask(this).start(0, 1));
            if (CommonUtil.hasHandlers(PlayerAdvancementProgressEvent.getHandlerList())) {
                initAdvancementProgressHandler();
            }
        }

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
            //getMetrics().addGraph(new SoftDependenciesGraph());

            // Depending
            //getMetrics().addGraph(new MyDependingPluginsGraph());
        }

        // Server-specific enabling occurs
        Common.SERVER.enable(this);

        // If requested, load some static utility classes up-front
        if (preloadTemplateClasses) {
            CommonClasses.initializeLogicClasses(getLogger());
        }

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

        // Shut down all components
        this.components.disable();

        // Stop tracking chunsk when they unload
        ChunkHandleTracker.INSTANCE.stopTracking();

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
            getLogger().log(Level.SEVERE, "Failed to properly disable the Packet Handler", t);
        }
        packetHandler = null;

        // Disable LookupEntityClassMap hook
        try {
            LookupEntityClassMap.unhook();
        } catch (Throwable t) {
            getLogger().log(Level.SEVERE, "Failed to unhook LookupEntityClassMap", t);
        }

        // Disable CommonForcedChunkManager
        this.forcedChunkManager.disable(this);
        this.forcedChunkManager = null;

        // Wait for pending FileConfiguration save() to complete
        flushSaveOperations(null);

        // Shutdown the IO worker now
        this.fileIOWorker.shutdown();

        // Server-specific disabling occurs
        Common.SERVER.disable(this);

        // Run any pending tasks in the next tick executor right now, so they are not forgotten
        // Disable the executor so that future attempts to queue tasks aren't handled by BKCommonLib
        CommonNextTickExecutor.INSTANCE.setExecutorTask(null);

        // Get rid of the BlockData hooks registered into the server
        try {
            BlockData.values().forEach(b -> BlockDataWrapperHook.INSTANCE.unhook(b.getData()));
            BlockDataWrapperHook.disableHook(); // Prevents new ones being made
        } catch (Throwable t) {
            getLogger().log(Level.SEVERE, "Failed to disable the BlockData hook, some stuff might remain");
        }

        // Get rid of Player advancement handler hooks, if registered
        for (Player player : Bukkit.getOnlinePlayers()) {
            AdvancementDataPlayerHook.unhook(player);
        }

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
            if (!FileConfiguration.flushSaveOperation(file, 500)) {
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
            EntityAddRemoveHandler.INSTANCE.processEventsForAllWorlds();

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

    private static void initAdvancementProgressHandler() {
        AdvancementDataPlayerHook.getAdvancementsInitFailure().ifPresent(err -> {
            getInstance().getLogger().log(Level.SEVERE, "Failed to hook advancement progress. Advancement disabling won't work! Error: " + err);
        });
        for (Player player : Bukkit.getOnlinePlayers()) {
            AdvancementDataPlayerHook.hook(player);
        }
    }

    private static class AdvancementProgressEventHandlerDetectorTask extends Task {
        private boolean _advancementProgressEventHasHandlers;

        public AdvancementProgressEventHandlerDetectorTask(JavaPlugin plugin) {
            super(plugin);
            this._advancementProgressEventHasHandlers = CommonUtil.hasHandlers(PlayerAdvancementProgressEvent.getHandlerList());
        }

        @Override
        public void run() {
            boolean hasHandlers = CommonUtil.hasHandlers(PlayerAdvancementProgressEvent.getHandlerList());
            if (hasHandlers != this._advancementProgressEventHasHandlers) {
                this._advancementProgressEventHasHandlers = hasHandlers;
                if (hasHandlers) {
                    initAdvancementProgressHandler();
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
