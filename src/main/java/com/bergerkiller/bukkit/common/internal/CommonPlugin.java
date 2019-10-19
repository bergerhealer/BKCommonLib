package com.bergerkiller.bukkit.common.internal;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.MessageBuilder;
import com.bergerkiller.bukkit.common.PluginBase;
import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.TypedValue;
import com.bergerkiller.bukkit.common.collections.EntityMap;
import com.bergerkiller.bukkit.common.collections.ImplicitlySharedSet;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.events.CommonEventFactory;
import com.bergerkiller.bukkit.common.events.CreaturePreSpawnEvent;
import com.bergerkiller.bukkit.common.events.EntityAddEvent;
import com.bergerkiller.bukkit.common.events.EntityRemoveEvent;
import com.bergerkiller.bukkit.common.events.EntityRemoveFromServerEvent;
import com.bergerkiller.bukkit.common.internal.hooks.ChunkGeneratorHook;
import com.bergerkiller.bukkit.common.internal.hooks.EntityHook;
import com.bergerkiller.bukkit.common.internal.hooks.LookupEntityClassMap;
import com.bergerkiller.bukkit.common.internal.logic.EntityAddRemoveHandler;
import com.bergerkiller.bukkit.common.internal.network.CommonPacketHandler;
import com.bergerkiller.bukkit.common.internal.network.ProtocolLibPacketHandler;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.metrics.MyDependingPluginsGraph;
import com.bergerkiller.bukkit.common.metrics.SoftDependenciesGraph;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.generated.net.minecraft.server.EntityPlayerHandle;
import com.bergerkiller.generated.net.minecraft.server.NBTBaseHandle;
import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.util.ASMUtil;

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

import java.awt.Color;
import java.lang.ref.SoftReference;
import java.util.*;
import java.util.logging.Level;

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
    private final List<Runnable> nextTickTasks = new ArrayList<>();
    private final List<Runnable> nextTickSync = new ArrayList<>();
    private final List<TimingsListener> timingsListeners = new ArrayList<>(1);
    private final List<Task> startedTasks = new ArrayList<>();
    private final ImplicitlySharedSet<org.bukkit.entity.Entity> entitiesRemovedFromServer = new ImplicitlySharedSet<>();
    private final HashMap<String, TypedValue> debugVariables = new HashMap<>();
    private CommonEventFactory eventFactory;
    private boolean isServerStarted = false;
    private PacketHandler packetHandler = null;
    private PermissionHandler permissionHandler = null;
    private CommonTabController tabController = null;
    private CommonMapController mapController = null;
    private CommonImmutablePlayerSetManager immutablePlayerSetManager = null;
    private CommonChunkLoaderPool chunkLoaderPool = null;
    private CommonForcedChunkManager forcedChunkManager = null;

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
            hook.getController().getEntity().setController(null);
        }
    }

    public void notifyWorldAdded(org.bukkit.World world) {
        EntityAddRemoveHandler.INSTANCE.hook(world);
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
     * Obtains the Tab Controller that is responsible for the creation and
     * updating of tabs
     *
     * @return tab controller
     */
    public CommonTabController getTabController() {
        return tabController;
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

    /**
     * Should be called when BKCommonLib is unable to continue running as it
     * does
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
        this.mapController.updateDependency(plugin, pluginName, enabled);
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

        // Modify the BlockStateProxy class on MC <= 1.12.2, because BlockData does not exist there.
        if (Common.evaluateMCVersion("<=", "1.12.2")) {
            this.rewriteClass("com.bergerkiller.bukkit.common.proxies.BlockStateProxy", (plugin, name, classBytes) -> ASMUtil.removeClassMethods(classBytes, new HashSet<String>(Arrays.asList(
                    "getBlockData()Lorg/bukkit/block/data/BlockData;",
                    "setBlockData(Lorg/bukkit/block/data/BlockData;)V"
            ))));
        }

        // Load the classes contained in this library
        CommonClasses.init();
    }

    @Override
    public void disable() {
        // Erase all traces of BKCommonLib from this server
        Collection<Entity> entities = new ArrayList<Entity>();
        for (World world : WorldUtil.getWorlds()) {
            // Unhook chunk providers
            ChunkGeneratorHook.unhook(world);
            // Unhook entities
            entities.addAll(WorldUtil.getEntities(world));
            for (Entity entity : entities) {
                CommonEntity.clearControllers(entity);
            }
            entities.clear();
        }

        // Shut down map display controller
        this.mapController.onDisable();

        // Disable listeners
        for (World world : Bukkit.getWorlds()) {
            EntityAddRemoveHandler.INSTANCE.unhook(world);
        }
        HandlerList.unregisterAll(listener);
        PacketUtil.removePacketListener(this.mapController);
        PacketUtil.removePacketListener(this.tabController);

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

        // Server-specific disabling occurs
        Common.SERVER.disable(this);

        // Dereference
        MountiplexUtil.unloadMountiplex();
        instance = null;
    }

    @Override
    public void enable() {
        // Validate version
        if (Common.IS_COMPATIBLE) {
            log(Level.INFO, "BKCommonLib is running on " + Common.SERVER.getServerDetails());
        } else {
            String verText = StringUtil.combineNames(Common.TEMPLATE_RESOLVER.getSupportedVersions());
            log(Level.SEVERE, "This version of BKCommonLib is not compatible with: " + Common.SERVER.getServerDetails());
            log(Level.SEVERE, "It could be that BKCommonLib has to be updated, as the current version is built for MC " + verText);
            log(Level.SEVERE, "Please look for a new updated BKCommonLib version that is compatible:");
            log(Level.SEVERE, "https://www.spigotmc.org/resources/bkcommonlib.39590/");
            log(Level.SEVERE, "Unstable development builds for MC " + Common.MC_VERSION + " may be found on our continuous integration server:");
            log(Level.SEVERE, "https://ci.mg-dev.eu/job/BKCommonLib/");
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
                "BKCommonLib isn't a plugin, its a language based on english.",
                "Updating is like reinventing the wheel for BKCommonLib.",
                "Say thanks to our wonderful devs: Friwi, KamikazePlatypus and mg_1999");
        setEnableMessage(welcomeMessages.get(new Random().nextInt(welcomeMessages.size())));
        setDisableMessage(null);

        // Initialize LookupEntityClassMap and hook it into the server
        try {
            LookupEntityClassMap.hook();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        // Initialize MapColorPalette (static initializer)
        MapColorPalette.getColor(Color.RED);

        // Initialize NBTBase createHandle() - workaround for a reported error
        // The error this potentially fixes is related to a linkageerror when generating the class
        NBTBaseHandle.T.createHandle(null, true);

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

        // Initialize entity map (needs to be here because of CommonPlugin instance needed)
        playerMetadata = new EntityMap<Player, CommonPlayerMeta>();

        // Register events and tasks, initialize
        register(listener = new CommonListener());

        // NO LONGER USED!!!
        // register(new CommonPacketMonitor(), CommonPacketMonitor.TYPES);

        tabController = new CommonTabController();
        register(tabController);
        PacketUtil.addPacketListener(this, tabController, PacketType.OUT_PLAYER_INFO);

        register(mapController = new CommonMapController());
        PacketUtil.addPacketListener(this, mapController, CommonMapController.PACKET_TYPES);
        mapController.onEnable(this, startedTasks);

        startedTasks.add(new NextTickHandler(this).start(1, 1));
        startedTasks.add(new MoveEventHandler(this).start(1, 1));
        startedTasks.add(new EntityRemovalHandler(this).start(1, 1));
        startedTasks.add(new TabUpdater(this).start(1, 1));
        startedTasks.add(new CreaturePreSpawnEventHandlerDetectorTask(this).start(0, 20));

        // Some servers do not have an Entity Remove Queue.
        // For those servers, we handle them using our own system
        if (!EntityPlayerHandle.T.removeQueue.isAvailable()) {
            startedTasks.add(new EntityRemoveQueueSyncTask(this).start(1, 1));
        }

        // Operations to execute the next tick (when the server has started)
        CommonUtil.nextTick(() -> {
            // Set server started state
            isServerStarted = true;
            // Tell the tabs to initialize the initial dimensions
            getTabController().setDefaultSize();
        });

        // Register listeners and hooks
        if (CommonUtil.hasHandlers(CreaturePreSpawnEvent.getHandlerList())) {
            for (World world : WorldUtil.getWorlds()) {
                ChunkGeneratorHook.hook(world);
            }
        }
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
                    for (World world : WorldUtil.getWorlds()) {
                        ChunkGeneratorHook.hook(world);
                    }
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
