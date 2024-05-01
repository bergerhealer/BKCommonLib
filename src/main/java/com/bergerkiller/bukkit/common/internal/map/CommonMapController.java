package com.bergerkiller.bukkit.common.internal.map;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.logging.Level;

import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutMapHandle;
import com.bergerkiller.generated.net.minecraft.world.item.ItemStackHandle;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.collections.FastTrackedUpdateSet;
import com.bergerkiller.bukkit.common.collections.ImplicitlySharedSet;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.events.ChunkLoadEntitiesEvent;
import com.bergerkiller.bukkit.common.events.EntityAddEvent;
import com.bergerkiller.bukkit.common.events.EntityRemoveEvent;
import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.events.map.MapAction;
import com.bergerkiller.bukkit.common.events.map.MapShowEvent;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.CommonMapReloadFile;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.map.MapDisplayProperties;
import com.bergerkiller.bukkit.common.map.MapSession;
import com.bergerkiller.bukkit.common.map.binding.ItemFrameInfo;
import com.bergerkiller.bukkit.common.map.binding.MapDisplayInfo;
import com.bergerkiller.bukkit.common.map.util.MapLookPosition;
import com.bergerkiller.bukkit.common.map.util.MapUUID;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.offline.OfflineWorld;
import com.bergerkiller.bukkit.common.map.MapPlayerInput;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.ChunkUtil;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.ItemUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.utils.PlayerUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayInSteerVehicleHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutSetSlotHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityPlayerHandle;
import com.bergerkiller.generated.net.minecraft.server.level.WorldServerHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.decoration.EntityItemFrameHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.inventory.CraftItemStackHandle;
import com.bergerkiller.mountiplex.reflection.SafeField;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;
import com.bergerkiller.mountiplex.reflection.util.OutputTypeMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

public final class CommonMapController implements PacketListener, Listener {
    // Whether this controller has been enabled
    private boolean isEnabled = false;
    // Whether map displays shown on item frames is enabled
    private boolean isFrameDisplaysEnabled = true;
    // Whether tiling is supported. Disables findNeighbours() if false.
    private boolean isFrameTilingSupported = true;
    // Stores cached thread-safe lists of item frames by cluster key
    protected final Map<ItemFrameClusterKey, Set<EntityItemFrameHandle> > itemFrameEntities = new HashMap<>();
    // Bi-directional mapping between map UUID and Map (durability) Id
    private final IntHashMap<MapUUID> mapUUIDById = new IntHashMap<MapUUID>();
    private final HashMap<MapUUID, Integer> mapIdByUUID = new HashMap<MapUUID, Integer>();
    // Additional detectors for map id's which are allocated by other plugins, and shouldn't be used
    private final List<StaticMapIdFilter> mapIdFilters = new ArrayList<>();
    // Stores Map Displays, mapped by Map UUID
    protected final HashMap<UUID, MapDisplayInfo> maps = new HashMap<UUID, MapDisplayInfo>();
    protected final ImplicitlySharedSet<MapDisplayInfo> mapsValues = new ImplicitlySharedSet<MapDisplayInfo>();
    // Stores map items for a short time while a player is moving it around in creative mode
    protected final HashMap<UUID, CreativeDraggedMapItem> creativeDraggedMapItems = new HashMap<UUID, CreativeDraggedMapItem>();
    // Stores Map Displays by their Type information
    private final OutputTypeMap<MapDisplay> displays = new OutputTypeMap<MapDisplay>();
    // Stores player map input (through Vehicle Steer packets)
    protected final HashMap<Player, MapPlayerInput> playerInputs = new HashMap<Player, MapPlayerInput>();
    // Tracks all item frames loaded on the server
    // Note: we are not using an IntHashMap because we need to iterate over the values, which is too slow with IntHashMap
    protected final Map<Integer, ItemFrameInfo> itemFrames = new HashMap<>();
    // Tracks what item frames require a refresh of the item inside (resending of metadata)
    public final FastTrackedUpdateSet<ItemFrameInfo> itemFramesThatNeedItemRefresh = new FastTrackedUpdateSet<ItemFrameInfo>();
    // Tracks what item frames require a refresh of the item inside (resending of metadata)
    public final FastTrackedUpdateSet<MapDisplayInfo> mapsWithItemFrameViewerChanges = new FastTrackedUpdateSet<MapDisplayInfo>();
    public final FastTrackedUpdateSet<MapDisplayInfo> mapsWithItemFrameResolutionChanges = new FastTrackedUpdateSet<MapDisplayInfo>();
    // Tracks what item frames need to have it's item change detection / viewers updated
    protected final ItemFrameUpdateList itemFrameUpdateList = new ItemFrameUpdateList();
    // Tracks entity id's for which item metadata was sent before itemFrameInfo was available
    private final Set<Integer> itemFrameMetaMisses = new HashSet<>();
    // Tracks chunks neighbouring item frame clusters that need to be loaded before clusters load in
    private final HashMap<World, Map<IntVector2, Set<IntVector2>>> itemFrameClusterDependencies = new HashMap<>();
    // Tracks all maps that need to have their Map Ids re-synchronized (item slot / itemframe metadata updates)
    private SetMultimap<UUID, MapUUID> dirtyMapUUIDSet = HashMultimap.create(5, 100);
    private SetMultimap<UUID, MapUUID> dirtyMapUUIDSetTmp = HashMultimap.create(5, 100);
    // Caches used while executing findNeighbours()
    private FindNeighboursCache findNeighboursCache = null;
    // Neighbours of item frames to check for either x-aligned or z-aligned
    private static final BlockFace[] NEIGHBOUR_AXIS_ALONG_X = {BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH};
    private static final BlockFace[] NEIGHBOUR_AXIS_ALONG_Y = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
    private static final BlockFace[] NEIGHBOUR_AXIS_ALONG_Z = {BlockFace.UP, BlockFace.DOWN, BlockFace.WEST, BlockFace.EAST};
    // Item frame clusters previously computed, is short-lived
    protected final Map<World, Map<IntVector3, ItemFrameCluster>> itemFrameClustersByWorld = new IdentityHashMap<>();
    // Whether the short-lived cache is used (only used during the update cycle)
    protected boolean itemFrameClustersByWorldEnabled = false;
    protected int idGenerationCounter = 0;

    /**
     * These packet types are listened to handle the virtualized Map Display API
     */
    public static final PacketType[] PACKET_TYPES = {
            PacketType.OUT_MAP, PacketType.IN_STEER_VEHICLE, 
            PacketType.OUT_WINDOW_ITEMS, PacketType.OUT_WINDOW_SET_SLOT,
            PacketType.OUT_ENTITY_METADATA, PacketType.IN_SET_CREATIVE_SLOT
    };

    /**
     * Accesses the initialize() function of MapDisplay
     */
    public static final MapDisplayInitializeFunction MAP_DISPLAY_INIT_FUNC = SafeField.get(
            MapDisplay.class, "INIT_FUNCTION", MapDisplayInitializeFunction.class);

    /**
     * Gets all registered Map Displays of a particular type
     * 
     * @param type
     * @return collection of map displays
     */
    @SuppressWarnings("unchecked")
    public <T extends MapDisplay> Collection<T> getDisplays(Class<T> type) {
        return (Collection<T>) displays.getAll(TypeDeclaration.fromClass(type));
    }

    /**
     * Gets a map of all displays
     * 
     * @return displays
     */
    public OutputTypeMap<MapDisplay> getDisplays() {
        return displays;
    }

    /**
     * Gets all maps available on the server that may store map displays
     * 
     * @return collection of map display info
     */
    public Collection<MapDisplayInfo> getMaps() {
        return this.maps.values();
    }

    /**
     * Gets all item frames that are tracked
     * 
     * @return item frames
     */
    public Collection<ItemFrameInfo> getItemFrames() {
        return itemFrames.values();
    }

    /**
     * Gets the item frame with the given entity id
     *
     * @param entityId
     * @return item frame
     */
    public ItemFrameInfo getItemFrame(int entityId) {
        return itemFrames.get(Integer.valueOf(entityId));
    }

    /**
     * Prioritizes an ItemFrame for updating the item inside. This can
     * be called when the item inside is changed.
     *
     * @param entityId Item Frame Entity Id
     */
    public synchronized void updateItemFrame(int entityId) {
        ItemFrameInfo info = getItemFrame(entityId);
        if (info != null) {
            this.itemFrameUpdateList.prioritize(info.updateEntry);
        }
    }

    /**
     * Gets the Player Input controller for a certain player
     * 
     * @param player
     * @return player input
     */
    public synchronized MapPlayerInput getPlayerInput(Player player) {
        MapPlayerInput input;
        input = playerInputs.get(player);
        if (input == null) {
            input = new MapPlayerInput(player);
            playerInputs.put(player, input);
        }
        return input;
    }

    /**
     * Invalidates all map display data that is visible for a player,
     * causing it to be sent again to the player as soon as possible.
     * 
     * @param player
     */
    public synchronized void resendMapData(final Player player) {
        final UUID playerUUID = player.getUniqueId();
        mapsValues.cloneAndForEach(display -> {
            if (display.getViewStackByPlayerUUID(playerUUID) != null) {
                for (MapSession session : display.getSessions()) {
                    for (MapSession.Owner owner : session.onlineOwners) {
                        if (owner.player == player) {
                            owner.clip.markEverythingDirty();
                        }
                    }
                }
            }
        });
    }

    /**
     * Gets the Map display information for a map item displayed in an item frame.
     * All frames showing the same map will return the same {@link MapDisplayInfo}.
     * If the item frame does not show a map, null is returned.
     * 
     * @param itemFrame to get the map information for
     * @return map display info
     */
    public synchronized MapDisplayInfo getInfo(ItemFrame itemFrame) {
        ItemFrameInfo frameInfo = itemFrames.get(itemFrame.getEntityId());
        if (frameInfo != null) {
            if (frameInfo.lastMapUUID == null) {
                return null;
            }
            MapDisplayInfo info = maps.get(frameInfo.lastMapUUID.getUUID());
            if (info == null) {
                info = new MapDisplayInfo(this, frameInfo.lastMapUUID.getUUID());
                maps.put(frameInfo.lastMapUUID.getUUID(), info);
                mapsValues.add(info);
            }
            return info;
        }
        return getInfo(getItemFrameItem(itemFrame));
    }

    /**
     * Gets the Map display information for a certain map item.
     * All items showing the same map will return the same {@link MapDisplayInfo}.
     * If the item does not represent a map, null is returned.
     * 
     * @param mapItem to get the map information for
     * @return map display info
     */
    public synchronized MapDisplayInfo getInfo(ItemStack mapItem) {
        UUID uuid = CommonMapUUIDStore.getMapUUID(mapItem);
        return (uuid == null) ? null : getInfo(uuid);
    }

    /**
     * Gets the Map display information for a certain map item UUID.
     * Creates a new instance if none exists yet. Returns null if
     * the input UUID is null.
     * 
     * @param mapUUID The Unique ID of the map
     * @return display info for this UUID, or null if mapUUID is null
     */
    public synchronized MapDisplayInfo getInfo(UUID mapUUID) {
        if (mapUUID == null) {
            return null;
        } else {
            return maps.computeIfAbsent(mapUUID, uuid -> {
                MapDisplayInfo info = new MapDisplayInfo(this, uuid);
                mapsValues.add(info);
                return info;
            });
        }
    }

    /**
     * Gets the Map display information for a certain map item UUID.
     * Returns null if none exists by this UUID.
     *
     * @param mapUUID The Unique ID of the map
     * @return display info for this UUID, or null if none exists
     */
    public synchronized MapDisplayInfo getInfoIfExists(UUID mapUUID) {
        return maps.get(mapUUID);
    }

    /**
     * Updates the information of a map item, refreshing all item frames
     * and player inventories storing the item. Map displays are also
     * updated.
     * 
     * @param oldItem that was changed
     * @param newItem the old item was changed into
     */
    public synchronized void updateMapItem(CommonItemStack oldItem, CommonItemStack newItem) {
        if (oldItem.isEmpty()) {
            throw new IllegalArgumentException("oldItem is empty");
        }

        // Ensure both are CraftItemStacks
        oldItem.getHandle();
        newItem.getHandle();

        boolean unchanged = isItemUnchanged(oldItem, newItem);
        UUID oldMapUUID = oldItem.getHandle().map(ItemStackHandle::getMapDisplayUUID).orElse(null);
        if (oldMapUUID != null) {
            // Change in the inventories of all player owners
            for (Player player : Bukkit.getOnlinePlayers()) {
                PlayerInventory inv = player.getInventory();
                for (int i = 0; i < inv.getSize(); i++) {
                    UUID mapUUID = CommonMapUUIDStore.getMapUUID(inv.getItem(i));
                    if (oldMapUUID.equals(mapUUID)) {
                        if (unchanged) {
                            PlayerUtil.setItemSilently(player, i, newItem.toBukkit());
                        } else {
                            inv.setItem(i, newItem.toBukkit());
                        }
                    }
                }
            }

            // All item frames that show this same map
            for (ItemFrameInfo itemFrameInfo : CommonPlugin.getInstance().getMapController().getItemFrames()) {
                if (itemFrameInfo.lastMapUUID != null && oldMapUUID.equals(itemFrameInfo.lastMapUUID.getUUID())) {
                    if (unchanged) {
                        // When unchanged set the item in the metadata without causing a refresh
                        DataWatcher data = EntityHandle.fromBukkit(itemFrameInfo.itemFrame).getDataWatcher();
                        DataWatcher.Item<ItemStack> dataItem = data.getItem(EntityItemFrameHandle.DATA_ITEM);
                        dataItem.setValue(newItem.toBukkit(), dataItem.isChanged());
                    } else {
                        // When changed, set it normally so the item is refreshed
                        itemFrameInfo.itemFrameHandle.setItem(newItem.toBukkit());
                        this.itemFrameUpdateList.prioritize(itemFrameInfo.updateEntry);
                    }
                }
            }

            // All map displays showing this item
            MapDisplayInfo info = maps.get(oldMapUUID);
            if (info != null) {
                for (MapSession session : info.getSessions()) {
                    session.display.setMapItemSilently(newItem.toBukkit());
                }
            }
        }

    }

    private boolean isItemUnchanged(CommonItemStack item1, CommonItemStack item2) {
        return trimExtraData(item1).equals(trimExtraData(item2));
    }

    /**
     * Starts all continuous background update tasks for maps
     * 
     * @param plugin
     * @param startedTasks
     */
    public void onEnable(CommonPlugin plugin, List<Task> startedTasks) {
        this.isFrameTilingSupported = plugin.isFrameTilingSupported();
        this.isFrameDisplaysEnabled = plugin.isFrameDisplaysEnabled();

        plugin.register((Listener) this);
        plugin.register((PacketListener) this, PACKET_TYPES);
        plugin.register(new MapDisplayItemChangeListener(this));

        startedTasks.add(new MapDisplayHeldMapUpdater(plugin, this).start(1, 1));
        startedTasks.add(new MapDisplayItemMapIdUpdater(plugin, this).start(1, 1));
        startedTasks.add(new MapDisplayInputUpdater(plugin, this).start(1, 1));
        startedTasks.add(new MapDisplayCreativeDraggedMapItemCleaner(plugin, this)
                .start(100, CreativeDraggedMapItem.CACHED_ITEM_CLEAN_INTERVAL));

        // These tasks only run when map displays on item frames are enabled
        if (this.isFrameDisplaysEnabled) {
            startedTasks.add(new MapDisplayFramedMapUpdater(plugin, this).start(1, 1));
            startedTasks.add(new ByWorldItemFrameSetRefresher(plugin).start(2400, 2400)); // every 2 minutes
        }

        // Whether this sort of stuff is relevant at all, in case it's set from somewhere
        // Avoids a memory leak
        this.mapsWithItemFrameResolutionChanges.setEnabled(this.isFrameDisplaysEnabled && this.isFrameTilingSupported);
        this.mapsWithItemFrameViewerChanges.setEnabled(this.isFrameDisplaysEnabled);
        this.itemFramesThatNeedItemRefresh.setEnabled(this.isFrameDisplaysEnabled);

        // Discover all item frames that exist at plugin load, in already loaded worlds and chunks
        // This is only relevant during /reload, since at server start no world is loaded yet
        // No actual initialization is done yet, this happens next tick cycle!
        if (this.isFrameDisplaysEnabled) {
            for (World world : Bukkit.getWorlds()) {
                for (EntityItemFrameHandle itemFrame : initItemFrameSetOfWorld(world)) {
                    onAddItemFrame(itemFrame);
                }
            }
        }

        // For all item frames with maps we know right now, assume players have seen them already (if reloading)
        // This ensures updated map details are refreshed during the item update discovery
        if (CommonUtil.getServerTicks() > 0) {
            this.getItemFrames().forEach(info -> {
                if (CommonMapUUIDStore.isMap(getItemFrameItem(info.itemFrame))) {
                    info.sentMapInfoToPlayers = true;
                }
            });
        }

        // If this is a reload, that means players have already been watching maps potentially
        // To minimize glitches and problems, restore the map id data from last run
        CommonMapReloadFile.load(plugin, reloadFile -> {

            // Static reserved ids (other plugins have been using it)
            for (Integer staticId : reloadFile.staticReservedIds) {
                storeStaticMapId(staticId.intValue());
            }

            // Dynamic ids we have generated and assigned before
            // To avoid 'popping', make sure to pre-cache the same ones
            for (CommonMapReloadFile.DynamicMappedId dynamicMapId : reloadFile.dynamicMappedIds) {
                if (mapUUIDById.contains(dynamicMapId.id)) {
                    continue; // Already assigned, skip
                }
                if (mapIdByUUID.containsKey(dynamicMapId.uuid)) {
                    continue; // Already assigned, skip
                }

                // Store
                mapIdByUUID.put(dynamicMapId.uuid, dynamicMapId.id);
                mapUUIDById.put(dynamicMapId.id, dynamicMapId.uuid);
            }

            // Give a hint about Map UUID to avoid 'popping' when the item is refreshed
            for (CommonMapReloadFile.ItemFrameDisplayUUID displayUUID : reloadFile.itemFrameDisplayUUIDs) {
                ItemFrameInfo itemFrame = itemFrames.get(displayUUID.entityId);
                if (itemFrame != null) {
                    itemFrame.preReloadMapUUID = displayUUID.uuid;
                }
            }
        });

        // Done!
        this.isEnabled = true;
    }

    /**
     * Cleans up all running map displays and de-initializes all map display logic
     */
    public void onDisable(CommonPlugin plugin) {
        if (this.isEnabled) {
            this.isEnabled = false;

            // If reloading, save current map id state to avoid glitches
            CommonMapReloadFile.save(plugin, reloadFile -> {
                // Add static reserved / dynamic map ids
                for (Map.Entry<MapUUID, Integer> entry : mapIdByUUID.entrySet()) {
                    MapUUID mapUUID = entry.getKey();
                    if (mapUUID.isStaticUUID()) {
                        reloadFile.staticReservedIds.add(entry.getValue());
                    } else {
                        reloadFile.addDynamicMapId(mapUUID, entry.getValue());
                    }
                }

                // Add information about all item frames and what display they displayed last
                for (Map.Entry<Integer, ItemFrameInfo> entry : itemFrames.entrySet()) {
                    ItemFrameInfo info = entry.getValue();
                    if (info.lastMapUUID != null) {
                        reloadFile.addItemFrameDisplayUUID(entry.getKey().intValue(), info.lastMapUUID);
                    }
                }
            });

            this.mapsValues.cloneAndForEach(map -> {
                for (MapSession session : new ArrayList<MapSession>(map.getSessions())) {
                    session.display.setRunning(false);
                }
            });
        }
    }

    /**
     * Activates or de-activates all map items for a particular plugin
     * 
     * @param plugin
     * @param pluginName
     * @param enabled
     */
    public void updateDependency(Plugin plugin, String pluginName, boolean enabled) {
        if (enabled) {
            //TODO: Go through all items on the server, and if lacking a display,
            // and set to use this plugin for it, re-create the display
            // Not enabled right now because it is kind of slow.

            // InteractiveBoard map id filter. Only needed for older versions of the plugin.
            if (pluginName.equals("InteractiveBoard") && CommonUtil.getClass("com.interactiveboard.utility.MapChecker", false) != null) {
                try {
                    registerMapFilter(plugin, new InteractiveBoardMapIDFilter(plugin));
                } catch (Throwable t) {
                    Logging.LOGGER_MAPDISPLAY.log(Level.SEVERE, "Failed to add InteractiveBoard support", t);
                }
            }
        } else {
            // End all map display sessions for this plugin
            MapDisplay.stopDisplaysForPlugin(plugin);

            // If a filter was provided by this plugin, remove it
            synchronized (this) {
                for (Iterator<StaticMapIdFilter> iter = mapIdFilters.iterator(); iter.hasNext();) {
                    if (plugin == iter.next().owner) {
                        iter.remove();
                    }
                }
            }
        }
    }

    /**
     * Registers a map id allocation filter, and the plugin that provides this filter.
     * The filter is automatically cleaned up when the plugin disables.
     *
     * @param plugin Plugin registering this filter
     * @param filter Map ID filter
     */
    public synchronized void registerMapFilter(Plugin plugin, IntPredicate filter) {
        mapIdFilters.add(new StaticMapIdFilter(plugin, filter));
    }

    private synchronized boolean isMapIdFiltered(int mapId) {
        for (StaticMapIdFilter filter : mapIdFilters) {
            if (filter.filter.test(mapId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adjusts the internal remapping from UUID to Map Id taking into account the new item
     * being synchronized to the player. If the item is that of a virtual map, the map Id
     * of the item is updated. NBT data that should not be synchronized is dropped.
     * 
     * @param item
     * @param tileX the X-coordinate of the tile in which the item is displayed
     * @param tileY the Y-coordinate of the tile in which the item is displayed
     * @return The new item stack to put in the slot, or <i>null</i> if the item did not
     *         need to change
     */
    public CommonItemStack handleItemSync(CommonItemStack item, int tileX, int tileY) {
        if (!item.isFilledMap()) {
            return null;
        }

        // When a map UUID is specified, use that to dynamically allocate a map Id to use
        UUID mapUUID = item.getCustomData().getUUID("mapDisplay");
        if (mapUUID != null) {
            item = trimExtraData(item);
            int id = getMapId(new MapUUID(mapUUID, tileX, tileY));
            item.setFilledMapId(id);
            return item;
        }

        // Static map Id MUST be enforced
        int mapId = item.getFilledMapId();
        if (mapId != -1) {
            storeStaticMapId(mapId);
        }
        return null;
    }

    /**
     * Obtains the Map Id used for displaying a particular map UUID
     * 
     * @param mapUUID to be displayed
     * @return map Id
     */
    public synchronized int getMapId(MapUUID mapUUID) {
        // Obtain from cache
        Integer storedMapId = mapIdByUUID.get(mapUUID);
        if (storedMapId != null) {
            return storedMapId.intValue();
        }

        // If the UUID is that of a static UUID, we must make sure to store it as such
        // We may have to remap the old Map Id to free up the Id slot we need
        int mapId = CommonMapUUIDStore.getStaticMapId(mapUUID.getUUID());
        if (mapId != -1) {
            storeStaticMapId(mapId);
            return mapId;
        }

        // Store a new map
        return storeDynamicMapId(mapUUID);
    }

    /**
     * Forces a particular map Id to stay static (unchanging) and stores it
     * as such in the mappings. No tiling is possible with static map Ids.
     * 
     * @param mapId
     */
    private synchronized void storeStaticMapId(int mapId) {
        if (storeDynamicMapId(mapUUIDById.get(mapId)) != mapId) {
            MapUUID mapUUID = new MapUUID(CommonMapUUIDStore.getStaticMapUUID(mapId), 0, 0);
            mapUUIDById.put(mapId, mapUUID);
            mapIdByUUID.put(mapUUID, mapId);
        }
    }

    /**
     * Figures out a new Map Id and prepares the display of a map with this new Id.
     * This method is only suitable for dynamically generated map Ids.
     * 
     * @param mapUUID to store
     * @return map Id that was assigned
     */
    private synchronized int storeDynamicMapId(MapUUID mapUUID) {
        // Null safety check
        if (mapUUID == null) {
            return -1;
        }

        // If the UUID is static, do not store anything and return the static Id instead
        int staticMapid = CommonMapUUIDStore.getStaticMapId(mapUUID.getUUID());
        if (staticMapid != -1) {
            return staticMapid;
        }

        // Fixes an issue of maps going empty
        ImageFrameIdZeroApplier.apply();

        // Increment this counter. The Map Id updater task will clean up unused maps every 1000 cycles.
        idGenerationCounter++;

        // Figure out a free Map Id we can use
        final int MAX_IDS = CommonCapabilities.MAP_ID_IN_NBT ? Integer.MAX_VALUE : Short.MAX_VALUE;
        for (int mapidValue = 0; mapidValue < MAX_IDS; mapidValue++) {
            if (!mapUUIDById.contains(mapidValue)) {
                // Check ID isn't in use by another plugin. If it is, put it aside as a static id.
                if (this.isMapIdFiltered(mapidValue)) {
                    this.storeStaticMapId(mapidValue);
                    continue;
                }

                // Check if the Map Id was changed compared to before
                boolean idChanged = mapIdByUUID.containsKey(mapUUID);

                // Store in mapping
                mapUUIDById.put(mapidValue, mapUUID);
                mapIdByUUID.put(mapUUID, Integer.valueOf(mapidValue));

                // If it had changed, update map items showing this map
                // uuid, and also re-send map packets for this id.
                // This is all done periodically on the main thread.
                if (idChanged) {
                    dirtyMapUUIDSet.get(mapUUID.getUUID()).add(mapUUID);
                }

                return mapidValue;
            }
        }
        return -1;
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public synchronized void onPacketSend(PacketSendEvent event) {
        // Check if any virtual single maps are attached to this map
        if (event.getType() == PacketType.OUT_MAP) {
            int itemid = PacketPlayOutMapHandle.createHandle(event.getPacket().getHandle()).getMapId();
            this.storeStaticMapId(itemid);

            // This used to be used to just cancel interfering plugins
            // However, sometimes map data is sent before any item frame
            // set-item or similar packets. So, we'll have to act sooner.
            //
            // Note that we send packets with an ignoreListeners flag, causing
            // such packets to not go through this listener.
            /*
            MapUUID mapUUID = mapUUIDById.get(itemid);
            if (mapUUID == null) {
                this.storeStaticMapId(itemid);
            } else if (CommonMapUUIDStore.getStaticMapId(mapUUID.getUUID()) == -1) {
                event.setCancelled(true);
            }
            */
        }

        // Correct Map ItemStacks as they are sent to the clients (virtual)
        // This is always tile 0,0 (held map)
        if (event.getType() == PacketType.OUT_WINDOW_ITEMS) {
            List<ItemStack> items = event.getPacket().read(PacketType.OUT_WINDOW_ITEMS.items);
            ListIterator<ItemStack> iter = items.listIterator();
            while (iter.hasNext()) {
                CommonItemStack newItem = this.handleItemSync(CommonItemStack.of(iter.next()), 0, 0);
                if (newItem != null) {
                    iter.set(newItem.toBukkit());
                }
            }
        }
        if (event.getType() == PacketType.OUT_WINDOW_SET_SLOT) {
            ItemStack oldItem = event.getPacket().read(PacketType.OUT_WINDOW_SET_SLOT.item);
            CommonItemStack newItem = this.handleItemSync(CommonItemStack.of(oldItem), 0, 0);
            if (newItem != null) {
                event.getPacket().write(PacketType.OUT_WINDOW_SET_SLOT.item, newItem.toBukkit());
            }
        }

        // Correct the ItemStack displayed in Item Frames
        if (this.isFrameDisplaysEnabled && event.getType() == PacketType.OUT_ENTITY_METADATA) {
            int entityId = event.getPacket().read(PacketType.OUT_ENTITY_METADATA.entityId);
            ItemFrameInfo frameInfo = this.itemFrames.get(entityId);
            if (frameInfo == null) {
                // When map metadata is sent before the ItemFrame is loaded, then a metadata packet
                // was sent before the entity add event for it fired.
                // To prevent glitches, track that in the itemFrameMetaMisses set
                if (hasMapItemInMetadata(event.getPacket())) {
                    itemFrameMetaMisses.add(entityId);
                }
                return; // no information available or not an item frame
            }

            // Sometimes the metadata packet is handled before we do routine updates
            // If so, we can't do anything with it yet until the item frame is
            // loaded on the main thread.
            if (frameInfo.lastFrameItemUpdateNeeded ||
                frameInfo.requiresFurtherLoading ||
                frameInfo.lastMapUUID == null
            ) {
                // Map item metadata is sent, once the frame itself loads in, we must resend the item
                if (hasMapItemInMetadata(event.getPacket())) {
                    frameInfo.sentMapInfoToPlayers = true;
                }

                return; // not yet loaded or not a map
            }

            // Presumed to contain a map item, so mark it that players have received it
            int staticMapId = CommonMapUUIDStore.getStaticMapId(frameInfo.lastMapUUID.getUUID());
            if (staticMapId != -1) {
                frameInfo.sentMapInfoToPlayers = true;
                this.storeStaticMapId(staticMapId);
                return; // static Id, not dynamic, no re-assignment
            }

            // Map Id is dynamically assigned, adjust metadata items to use this new Id
            // Avoid using any Bukkit or Wrapper types here for performance reasons
            int newMapId = this.getMapId(frameInfo.lastMapUUID);
            List<DataWatcher.PackedItem<Object>> items = event.getPacket().read(PacketType.OUT_ENTITY_METADATA.watchedObjects);
            if (items != null) {
                ListIterator<DataWatcher.PackedItem<Object>> itemsIter = items.listIterator();
                while (itemsIter.hasNext()) {
                    DataWatcher.PackedItem<Object> itemRaw = itemsIter.next();

                    // Some plugins send garbage that isn't metadata and need to be updated
                    // In that case, a null entry is put (as it cannot be converted)
                    if (itemRaw == null) {
                        continue;
                    }

                    DataWatcher.PackedItem<ItemStack> item = itemRaw.translate(EntityItemFrameHandle.DATA_ITEM);
                    if (item == null) {
                        continue;
                    }

                    // Check item is actually a map item and do some logic
                    ItemStack metaItem = item.value();
                    int oldMapId;
                    if (metaItem == null || (oldMapId = CommonMapUUIDStore.getItemMapId(metaItem)) == -1) {
                        break;
                    }

                    // Map information is sent, if the map id we use differs, update it
                    frameInfo.sentMapInfoToPlayers = true;
                    if (oldMapId != newMapId) {
                        ItemStack newMapItem = ItemUtil.cloneItem(metaItem);
                        CommonMapUUIDStore.setItemMapId(newMapItem, newMapId);

                        item = item.cloneWithValue(newMapItem);
                        itemsIter.set((DataWatcher.PackedItem<Object>) (DataWatcher.PackedItem) item);
                    }

                    break;
                }
            }
        }
    }

    /**
     * Checks that an Entity Metadata packet has the item metadata of an item frame,
     * and that the ItemStack item is of a Map.
     *
     * @param entityMetadataPacket
     * @return True if the metadata packet sends an ItemFrame Map Item
     */
    private static boolean hasMapItemInMetadata(CommonPacket entityMetadataPacket) {
        // Verify the Item Frame DATA_ITEM key is inside the metadata of this packet
        // If this is the case, then this is metadata for an item frame and not a different entity
        List<DataWatcher.PackedItem<Object>> items = entityMetadataPacket.read(PacketType.OUT_ENTITY_METADATA.watchedObjects);
        if (items != null) {
            for (DataWatcher.PackedItem<Object> dw_item : items) {
                // Some plugins send garbage that isn't metadata and need to be updated
                // In that case, a null entry is put (as it cannot be converted)
                if (dw_item != null) {
                    DataWatcher.PackedItem<ItemStack> item = dw_item.translate(EntityItemFrameHandle.DATA_ITEM);
                    if (item != null) {
                        return CommonMapUUIDStore.isMap(item.value());
                    }
                }
            }
        }
        return false;
    }

    @Override
    public synchronized void onPacketReceive(PacketReceiveEvent event) {
        // Handle input coming from the player for the map
        if (event.getType() == PacketType.IN_STEER_VEHICLE) {
            Player p = event.getPlayer();
            MapPlayerInput input = playerInputs.get(p);
            if (input != null) {
                PacketPlayInSteerVehicleHandle packet = PacketPlayInSteerVehicleHandle.createHandle(event.getPacket().getHandle());
                int dx = (int) -Math.signum(packet.getSideways());
                int dy = (int) -Math.signum(packet.getForwards());
                int dz = 0;
                if (packet.isUnmount()) {
                    dz -= 1;
                }
                if (packet.isJump()) {
                    dz += 1;
                }

                // Receive input. If it will be handled, it will cancel further handling of this packet
                event.setCancelled(input.receiveInput(dx, dy, dz));
            }
        }

        // When in creative mode, players may accidentally set the 'virtual' map Id as the actual Id in their inventory
        // We have to prevent that in here
        if (event.getType() == PacketType.IN_SET_CREATIVE_SLOT) {
            CommonItemStack item = CommonItemStack.of(event.getPacket().read(PacketType.IN_SET_CREATIVE_SLOT.item));
            UUID mapUUID = CommonMapUUIDStore.getMapUUID(item);
            if (mapUUID != null && CommonMapUUIDStore.getStaticMapId(mapUUID) == -1) {
                // Dynamic Id map. Since we do not refresh NBT data over the network, this packet contains incorrect data
                // Find the original item the player took (by UUID). If it exists, merge its NBT data with this item.
                // For this we also have the map item cache, which is filled with data the moment a player picks up an item
                // This data is kept around for 10 minutes (unlikely a player will hold onto it for that long...)
                CommonItemStack originalMapItem = null;
                CreativeDraggedMapItem cachedItem = this.creativeDraggedMapItems.get(mapUUID);
                if (cachedItem != null) {
                    cachedItem.life = CreativeDraggedMapItem.CACHED_ITEM_MAX_LIFE;
                    originalMapItem = cachedItem.item;
                } else {
                    for (ItemStack oldBukkitItem : event.getPlayer().getInventory()) {
                        CommonItemStack oldItem = CommonItemStack.of(oldBukkitItem);
                        if (mapUUID.equals(CommonMapUUIDStore.getMapUUID(oldItem))) {
                            originalMapItem = oldItem.clone();
                            break;
                        }
                    }
                }
                if (originalMapItem != null) {
                    // Original item was found. Restore all properties of that item.
                    // Keep metadata the player can control, replace everything else
                    item.setCustomData(originalMapItem.getCustomData());
                    event.getPacket().write(PacketType.IN_SET_CREATIVE_SLOT.item, item.toBukkit());
                } else {
                    // Dynamic Id. Force a map id value of 0 to prevent creation of new World Map instances
                    item = item.clone();
                    item.setFilledMapId(0);
                    event.getPacket().write(PacketType.IN_SET_CREATIVE_SLOT.item, item.toBukkit());
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    protected synchronized void onPlayerJoin(PlayerJoinEvent event) {
        // Let everyone know we got a player over here!
        final Player player = event.getPlayer();
        this.mapsValues.cloneAndForEach(map -> {
            for (MapSession session : map.getSessions()) {
                session.updatePlayerOnline(player);
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent event) {
        // The below resends a slot update packet one tick delayed to make sure a
        // map is updated for the Player

        // Dropping a map item onto the quickbar requires refreshing the item a tick later
        // No need to resent the entire inventory though
        boolean is_place = event.getAction() == InventoryAction.PLACE_ALL ||
                           event.getAction() == InventoryAction.PLACE_SOME ||
                           event.getAction() == InventoryAction.PLACE_ONE;
        if (is_place &&
            event.getSlotType() == SlotType.QUICKBAR &&
            event.getWhoClicked() instanceof Player &&
            CommonItemStack.of(event.getCursor()).isMapDisplay()
        ) {
            final Player player = (Player) event.getWhoClicked();
            final int slot = event.getSlot();
            final int rawSlot = event.getRawSlot();
            CommonUtil.nextTick(() -> {
                if (!player.isOnline()) {
                    return;
                }

                ItemStack item = player.getInventory().getItem(slot);
                if (CommonItemStack.of(item).isMapDisplay()) {
                    // Resend the item!
                    PacketUtil.sendPacket(player, PacketPlayOutSetSlotHandle.createNew(EntityPlayerHandle.fromBukkit(player).getCurrentWindowId(), rawSlot, item));
                }
            });
        }

        // If shift-clicking an item in another inventory, refresh any hotbar items which contain
        // a map display afterwards
        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY &&
            event.getSlotType() != SlotType.QUICKBAR &&
            event.getWhoClicked() instanceof Player &&
            CommonItemStack.of(event.getCurrentItem()).isMapDisplay()
        ) {
            final Player player = (Player) event.getWhoClicked();
            CommonUtil.nextTick(() -> {
                if (!player.isOnline()) {
                    return;
                }

                player.updateInventory();
            });
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    protected synchronized void onPlayerQuit(PlayerQuitEvent event) {
        MapPlayerInput input = this.playerInputs.remove(event.getPlayer());
        if (input != null) {
            input.onDisconnected();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    protected void onPlayerRespawn(PlayerRespawnEvent event) {
        this.resendMapData(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    protected void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        this.resendMapData(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    protected synchronized void onEntityAdded(EntityAddEvent event) {
        if (this.isFrameDisplaysEnabled && event.getEntity() instanceof ItemFrame) {
            EntityItemFrameHandle frameHandle = EntityItemFrameHandle.createHandle(HandleConversion.toEntityHandle(event.getEntity()));
            getItemFrameEntities(new ItemFrameClusterKey(frameHandle)).add(frameHandle);
            onAddItemFrame(frameHandle);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    protected synchronized void onEntityRemoved(EntityRemoveEvent event) {
        if (event.getEntity() instanceof ItemFrame) {
            ItemFrame frame = (ItemFrame) event.getEntity();
            EntityItemFrameHandle frameHandle = EntityItemFrameHandle.fromBukkit(frame);
            getItemFrameEntities(new ItemFrameClusterKey(frameHandle)).remove(frameHandle);
            ItemFrameInfo info = itemFrames.get(frame.getEntityId());
            if (info != null) {
                info.signalEntityRemoved();
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    protected synchronized void onChunkEntitiesLoaded(ChunkLoadEntitiesEvent event) {
        onChunkEntitiesLoaded(event.getChunk());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    protected synchronized void onWorldLoad(WorldLoadEvent event) {
        if (this.isFrameDisplaysEnabled) {
            for (EntityItemFrameHandle frame : initItemFrameSetOfWorld(event.getWorld())) {
                onAddItemFrame(frame);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    protected void onWorldUnload(WorldUnloadEvent event) {
        this.deinitItemFrameSetOfWorld(event.getWorld());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    protected synchronized void onInventoryCreativeSlot(InventoryCreativeEvent event) {
        // When taking items from the inventory in creative mode, store metadata of what is taken
        // We apply this metadata again when receiving the item
        if (event.getResult() != Result.DENY) {
            CommonItemStack item = CommonItemStack.of(event.getCurrentItem());
            UUID mapUUID = CommonMapUUIDStore.getMapUUID(item);
            if (mapUUID != null) {
                this.creativeDraggedMapItems.put(mapUUID, new CreativeDraggedMapItem(item.clone()));
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    protected void onFlightToggled(PlayerToggleFlightEvent event) {
        // While player is controlling the map (not off-hand), disallow flight toggling
        // This prevents the player falling down when spamming SPACEBAR in the menu,
        // which would be highly annoying.
        // TODO: Iterating all map displays feels kind of slow
        for (MapDisplayInfo info : mapsValues) {
            MapDisplay display = info.getViewing(event.getPlayer());
            if (display != null && display.isControlling(event.getPlayer())) {
                event.setCancelled(true);
                break;
            }
        }
    }

    private void onAddItemFrame(EntityItemFrameHandle frame) {
        int entityId = frame.getId();
        {
            ItemFrameInfo frameInfo = itemFrames.get(entityId);
            if (frameInfo != null) {
                frameInfo.removed = false;
                if (frameInfo.sentMapInfoToPlayers) {
                    frameInfo.needsItemRefresh.set(true);
                }
                return;
            }
        }

        // Add Item Frame Info
        ItemFrameInfo frameInfo = new ItemFrameInfo(this, frame);
        itemFrames.put(entityId, frameInfo);
        itemFrameUpdateList.add(frameInfo.updateEntry);
        if (itemFrameMetaMisses.remove(entityId)) {
            frameInfo.needsItemRefresh.set(true);
            frameInfo.sentMapInfoToPlayers = true;
        }
    }

    private void onChunkEntitiesLoaded(Chunk chunk) {
        World world = chunk.getWorld();

        Set<IntVector2> dependingChunks;
        {
            Map<IntVector2, Set<IntVector2>> dependencies = this.itemFrameClusterDependencies.get(world);
            if (dependencies == null || (dependingChunks = dependencies.remove(new IntVector2(chunk))) == null) {
                return;
            }
        }

        boolean wasClustersByWorldCacheEnabled = this.itemFrameClustersByWorldEnabled;
        try {
            this.itemFrameClustersByWorldEnabled = true;
            for (IntVector2 depending : dependingChunks) {
                // Check this depending chunk is still loaded with all entities inside
                // If not, then when it loads the cluster will be revived then
                Chunk dependingChunk = WorldUtil.getChunk(world, depending.x, depending.z);
                if (dependingChunk == null || !WorldUtil.isChunkEntitiesLoaded(dependingChunk)) {
                    continue;
                }

                // Go by all entities in this depending chunk to find the item frames
                // Quicker than iterating all item frames on the world
                for (Entity entity : ChunkUtil.getEntities(dependingChunk)) {
                    if (!(entity instanceof ItemFrame)) {
                        continue;
                    }

                    // Recalculate UUID, this will re-discover the cluster
                    // May also revive other item frames that were part of the same cluster
                    // Note that if this chunk being loaded contained item frames part of the cluster,
                    // the cluster is already revived. Entity add handling occurs prior.
                    ItemFrameInfo frameInfo = this.itemFrames.get(entity.getEntityId());
                    if (frameInfo != null) {
                        frameInfo.onChunkDependencyLoaded();
                    }
                }
            }
        } finally {
            this.itemFrameClustersByWorldEnabled = wasClustersByWorldCacheEnabled;
            if (!wasClustersByWorldCacheEnabled) {
                itemFrameClustersByWorld.clear();
            }
        }
    }

    /**
     * Checks whether an item frame cluster's chunk dependency has all item frames currently loaded.
     * If not, returns false, and tracks this chunk for when it loads in the future.
     *
     * @param world World
     * @param dependency Dependency
     * @return True if the chunk dependency is loaded, False if it is not
     */
    public synchronized boolean checkClusterChunkDependency(World world, ItemFrameCluster.ChunkDependency dependency) {
        if (!this.isFrameTilingSupported) {
            return true; // No need to even check
        } else if (WorldUtil.isChunkEntitiesLoaded(world, dependency.neighbour.x, dependency.neighbour.z)) {
            return true;
        } else {
            Map<IntVector2, Set<IntVector2>> dependencies = this.itemFrameClusterDependencies.computeIfAbsent(world, unused -> new HashMap<>());
            Set<IntVector2> dependingChunks = dependencies.computeIfAbsent(dependency.neighbour, unused -> new HashSet<>());
            dependingChunks.add(dependency.self);
            return false;
        }
    }

    /**
     * Flood-fills all item frames with the same item that are considered to be a single map display
     * cluster. Empty item frames are filled into as well, and are considered to be part of the
     * start item frame. This allows an existing display to be enlarged.
     *
     * @param startItemFrame
     * @param item
     */
    public synchronized void fillItemFrames(ItemFrame startItemFrame, ItemStack item) {
        if (!this.isFrameDisplaysEnabled) {
            throw new UnsupportedOperationException("Item frame map displays are disabled in BKCommonLib's configuration");
        }
        if (!this.isFrameTilingSupported) {
            throw new UnsupportedOperationException("Item frame map display tiling is disabled in BKCommonLib's configuration");
        }
        if (startItemFrame.isDead()) {
            throw new IllegalArgumentException("Input item frame was removed (dead)");
        }
        ItemFrameInfo info = this.getItemFrame(startItemFrame.getEntityId());
        if (info == null) {
            throw new IllegalStateException("Item frame had no metadata information for some reason");
        }

        // Find this item frame cluster
        ItemFrameCluster cluster = this.findCluster(info.itemFrameHandle, info.coordinates, true);
        for (ItemFrameInfo frame : this.findClusterItemFrames(cluster)) {
            // Update the item inside this item frame. Also schedule it for an instant refresh!
            // We could spin up the display and such here too, but let's keep it simple for now.
            frame.itemFrameHandle.setItem(item);
            frame.needsItemRefresh.set(true);
        }
    }

    protected SetMultimap<UUID, MapUUID> swapDirtyMapUUIDs() {
        final SetMultimap<UUID, MapUUID> dirtyMaps;
        dirtyMaps = dirtyMapUUIDSet;
        dirtyMapUUIDSet = dirtyMapUUIDSetTmp;
        dirtyMapUUIDSetTmp = dirtyMaps;
        return dirtyMaps;
    }

    private LookAtSearchResult findLookingAt(Player player, ItemFrame itemFrame) {
        Location eye = player.getEyeLocation();
        return findLookingAt(player, itemFrame, eye.toVector(), eye.getDirection());
    }

    private LookAtSearchResult findLookingAt(Player player, ItemFrame itemFrame, Vector startPosition, Vector lookDirection) {
        MapDisplayInfo info = getInfo(itemFrame);
        if (info == null) {
            return null; // no map here
        }

        // Find the Display this player is sees on this map
        MapDisplayInfo.ViewStack stack = info.getViewStackByPlayerUUID(player.getUniqueId());
        if (stack == null || stack.stack.isEmpty()) {
            return null; // no visible display for this player
        }

        // Find the item frame metadata information
        ItemFrameInfo frameInfo = this.itemFrames.get(itemFrame.getEntityId());
        if (frameInfo == null) {
            return null; // not tracked
        }

        // Ask item frame to compute look-at information
        // If looking further than 16 map pixels away from the edge, fail
        MapLookPosition position = frameInfo.findLookPosition(startPosition, lookDirection);
        final double limit = 16.0;
        if (position == null || position.getEdgeDistance() > (limit / 128.0)) {
            return null;
        }

        // Keep position within bounds of the display
        // If very much out of bounds (>16 pixels) fail the looking-at check
        // This loose-ness allows for smooth clicking between frames without failures
        MapDisplay display = stack.stack.getLast();
        double new_x = position.getDoubleX();
        double new_y = position.getDoubleY();
        if (new_x < -limit || new_y < -limit || new_x > (display.getWidth() + limit) || new_y >= (display.getHeight() + limit)) {
            return null;
        } else if (new_x < 0.0 || new_y < 0.0 || new_x >= display.getWidth() || new_y >= display.getHeight()) {
            new_x = MathUtil.clamp(new_x, 0.0, (double) display.getWidth() - 1e-10);
            new_y = MathUtil.clamp(new_y, 0.0, (double) display.getHeight() - 1e-10);
            position = new MapLookPosition(position.getItemFrameInfo(), new_x, new_y, position.getDistance(), position.getEdgeDistance());
        }

        return new LookAtSearchResult(display, position);
    }

    // Returns true if base click was cancelled
    private boolean dispatchClickAction(Player player, ItemFrame itemFrame, Vector startPosition, Vector lookDirection, MapAction action) {
        LookAtSearchResult lookAt = findLookingAt(player, itemFrame, startPosition, lookDirection);
        return lookAt != null && lookAt.click(player, action).isCancelled();
    }

    // Returns true if base click was cancelled
    private boolean dispatchClickActionApprox(Player player, ItemFrame itemFrame, MapAction action) {
        Location eye = player.getEyeLocation();
        return dispatchClickAction(player, itemFrame, eye.toVector(), eye.getDirection(), action);
    }

    // Returns true if base click was cancelled
    private boolean dispatchClickActionFromBlock(Player player, Block clickedBlock, BlockFace clickedFace, MapAction action) {
        if (!this.isFrameDisplaysEnabled) {
            return false;
        }

        Vector look = player.getEyeLocation().getDirection();
        final double eps = 0.001;

        double x1 = clickedBlock.getX() + 0.5 + (double) clickedFace.getModX() * 0.5;
        double y1 = clickedBlock.getY() + 0.5 + (double) clickedFace.getModY() * 0.5;
        double z1 = clickedBlock.getZ() + 0.5 + (double) clickedFace.getModZ() * 0.5;

        // Based on look direction, expand the search radius to check for corner item frames
        double x2 = x1, y2 = y1, z2 = z1;
        if (look.getX() < 0.0) {
            x2 += 1.0 + eps;
            x1 -= eps;
        } else {
            x2 -= 1.0 + eps;
            x1 += eps;
        }
        if (look.getY() < 0.0) {
            y2 += 1.0 + eps;
            y1 -= eps;
        } else {
            y2 -= 1.0 + eps;
            y1 += eps;
        }
        if (look.getZ() < 0.0) {
            z2 += 1.0 + eps;
            z1 -= eps;
        } else {
            z2 -= 1.0 + eps;
            z1 += eps;
        }

        LookAtSearchResult bestApprox = null;
        for (Entity e : WorldUtil.getEntities(clickedBlock.getWorld(), null, 
                x1, y1, z1, x2, y2, z2))
        {
            if (e instanceof ItemFrame) {
                LookAtSearchResult result = this.findLookingAt(player, (ItemFrame) e);
                if (result != null) {
                    // If within bounds, pick it right away!
                    if (result.lookPosition.isWithinBounds()) {
                        return result.click(player, action).isCancelled();
                    }

                    // Select the lowest distance result
                    if (bestApprox == null || bestApprox.lookPosition.getDistance() > result.lookPosition.getDistance()) {
                        bestApprox = result;
                    }
                }
            }
        }
        return bestApprox != null && bestApprox.click(player, action).isCancelled();
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    protected void onEntityLeftClick(EntityDamageByEntityEvent event) {
        if (!this.isFrameDisplaysEnabled || !(event.getEntity() instanceof ItemFrame) || !(event.getDamager() instanceof Player)) {
            return;
        }
        if (dispatchClickActionApprox(
                (Player) event.getDamager(),
                (ItemFrame) event.getEntity(),
                MapAction.LEFT_CLICK))
        {
            event.setCancelled(true);
        }
    }

    private Vector lastClickOffset = null;

    @EventHandler(priority = EventPriority.MONITOR)
    protected void onEntityRightClickAt(PlayerInteractAtEntityEvent event) {
        if (!this.isFrameDisplaysEnabled || event.getRightClicked() instanceof ItemFrame) {
            lastClickOffset = event.getClickedPosition();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    protected void onEntityRightClick(PlayerInteractEntityEvent event) {
        if (!this.isFrameDisplaysEnabled || !(event.getRightClicked() instanceof ItemFrame)) {
            return;
        }
        ItemFrame itemFrame = (ItemFrame) event.getRightClicked();
        boolean cancelled;
        if (lastClickOffset != null) {
            Location eye = event.getPlayer().getEyeLocation();
            Location pos = itemFrame.getLocation().add(lastClickOffset);
            Vector dir = eye.getDirection();
            lastClickOffset = null;

            // Move the position back a distance so the computed distance later matches up
            // A bit of a hack, but easier than injecting distance after the fact
            double distance = eye.distance(pos);
            pos.subtract(dir.clone().multiply(distance));

            cancelled = dispatchClickAction(event.getPlayer(), itemFrame, pos.toVector(), dir, MapAction.RIGHT_CLICK);
        } else {
            cancelled = dispatchClickActionApprox(event.getPlayer(), itemFrame, MapAction.RIGHT_CLICK);
        }
        if (cancelled) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    protected void onBlockInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }
        MapAction action;
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            action = MapAction.LEFT_CLICK;
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            action = MapAction.RIGHT_CLICK;
        } else {
            return;
        }
        if (dispatchClickActionFromBlock(event.getPlayer(), event.getClickedBlock(), event.getBlockFace(), action)) {
            event.setUseInteractedBlock(Result.DENY);
            event.setCancelled(true);
            event.setUseItemInHand(Result.DENY);
        }
    }

    protected synchronized void cleanupUnusedUUIDs(Set<MapUUID> existingMapUUIDs) {
        HashSet<MapUUID> idsToRemove = new HashSet<MapUUID>(mapIdByUUID.keySet());
        idsToRemove.removeAll(existingMapUUIDs);
        for (MapUUID toRemove : idsToRemove) {
            // Clean up the map display information first
            MapDisplayInfo displayInfo = maps.get(toRemove.getUUID());
            if (displayInfo != null) {
                if (displayInfo.getSessions().isEmpty()) {
                    MapDisplayInfo removed = maps.remove(toRemove.getUUID());
                    if (removed != null) {
                        mapsValues.remove(removed);
                        removed.onRemoved();
                    }
                } else {
                    continue; // still has an active session; cannot remove
                }
            }

            // Clean up from bi-directional mapping
            Integer mapId = mapIdByUUID.remove(toRemove);
            if (mapId != null) {
                mapUUIDById.remove(mapId.intValue());
            }

            // Clean up from 'dirty' set (probably never needed)
            dirtyMapUUIDSet.removeAll(toRemove.getUUID());
        }
    }

    @SuppressWarnings("deprecation")
    protected synchronized void handleMapShowEvent(MapShowEvent event) {
        // Check if there are other map displays that should be shown to the player automatically
        // This uses the 'isGlobal()' property of the display
        MapDisplayInfo info = CommonMapController.this.getInfo(event.getMapUUID());
        boolean hasDisplay = false;
        if (info != null) {
            for (MapSession session : info.getSessions()) {
                if (session.display.isGlobal()) {
                    session.display.addOwner(event.getPlayer());
                    hasDisplay = true;
                    break;
                }
            }
        }

        // When defined in the NBT of the item, construct the Map Display automatically
        // Do not do this when one was already assigned (global, or during event handling)
        // We initialize the display the next tick using the plugin owner's task to avoid
        // BKCommonLib showing up in timings when onAttached() is slow.
        MapDisplayProperties properties = MapDisplayProperties.of(event.getMapItem());
        if (!hasDisplay && !event.hasDisplay() && properties != null) {
            Class<? extends MapDisplay> displayClass = properties.getMapDisplayClass();
            if (displayClass != null) {
                Plugin plugin = properties.getPlugin();
                if (plugin instanceof JavaPlugin) {
                    try {
                        MapDisplay display = displayClass.newInstance();
                        event.setDisplay((JavaPlugin) plugin, display);
                    } catch (InstantiationException | IllegalAccessException e) {
                        plugin.getLogger().log(Level.SEVERE, "Failed to initialize MapDisplay", e);
                    }
                }
            }
        }

        CommonUtil.callEvent(event);
    }

    /**
     * Gets the item frame map UUID, also handling the tile information of the item frame
     * 
     * @param itemFrame to get the map UUID from
     */
    protected MapUUID getItemFrameMapUUID(EntityItemFrameHandle itemFrame) {
        if (itemFrame == null) {
            return null;
        } else {
            ItemFrameInfo info = this.itemFrames.get(itemFrame.getId());
            if (info == null) {
                return null;
            } else {
                info.updateItem();
                return info.lastMapUUID;
            }
        }
    }

    // Runs every now and then to reset and refresh the by-world item frame sets
    // This makes sure bugs or glitches don't cause item frames to stay in there forever
    public class ByWorldItemFrameSetRefresher extends Task {

        public ByWorldItemFrameSetRefresher(JavaPlugin plugin) {
            super(plugin);
        }

        @Override
        public void run() {
            Collection<World> worlds = Bukkit.getWorlds();
            synchronized (CommonMapController.this) {
                deinitItemFrameListForWorldsNotIn(worlds);

                // Verify that all item frame entities currently loaded are validly mapped
                // If some are not, we re-initialize the listing
                List<EntityItemFrameHandle> itemFramesToAdd = new ArrayList<>();
                for (World world : worlds) {
                    for (Object entityHandle : (Iterable<?>) WorldServerHandle.T.getEntities.raw.invoke(HandleConversion.toWorldHandle(world))) {
                        if (!EntityItemFrameHandle.T.isAssignableFrom(entityHandle)) {
                            continue;
                        }
                        Integer id = EntityHandle.T.getId.invoker.invoke(entityHandle);
                        if (itemFrames.containsKey(id)) {
                            continue;
                        }
                        itemFramesToAdd.add(EntityItemFrameHandle.createHandle(entityHandle));
                    }
                }
                for (EntityItemFrameHandle frameHandle : itemFramesToAdd) {
                    getItemFrameEntities(new ItemFrameClusterKey(frameHandle)).add(frameHandle);
                    onAddItemFrame(frameHandle);
                }
            }
        }
    }

    /**
     * Looks up all item frame information loaded of a particular item frame cluster
     *
     * @param cluster Item frame cluster
     * @return List of item frame info
     */
    public List<ItemFrameInfo> findClusterItemFrames(ItemFrameCluster cluster) {
        if (!cluster.world.isLoaded()) {
            return Collections.emptyList();
        }

        List<ItemFrameInfo> result = new ArrayList<>(cluster.coordinates.size());
        for (Entity entity : WorldUtil.getEntities(cluster.world.getLoadedWorld(), null,
                cluster.min_coord.x + 0.01,
                cluster.min_coord.y + 0.01,
                cluster.min_coord.z + 0.01,
                cluster.max_coord.x + 0.99,
                cluster.max_coord.y + 0.99,
                cluster.max_coord.z + 0.99))
        {
            if (!(entity instanceof ItemFrame)) {
                continue;
            }
            ItemFrameInfo itemFrame = this.getItemFrame(entity.getEntityId());
            if (itemFrame == null) {
                continue;
            }
            if (!cluster.coordinates.contains(itemFrame.coordinates)) {
                continue;
            }
            result.add(itemFrame);
        }
        return result;
    }

    /**
     * Finds a cluster of all connected item frames that an item frame is part of
     * 
     * @param itemFrame Start item frame
     * @param itemFramePosition Start block position of itemFrame
     * @return cluster
     */
    public final synchronized ItemFrameCluster findCluster(
            final EntityItemFrameHandle itemFrame,
            final IntVector3 itemFramePosition
    ) {
        return findCluster(itemFrame, itemFramePosition, false);
    }

    /**
     * Finds a cluster of all connected item frames that an item frame is part of
     * 
     * @param itemFrame Start item frame
     * @param itemFramePosition Start block position of itemFrame
     * @param includingEmpty Whether to include frames in the cluster that have no item
     * @return cluster
     */
    public final synchronized ItemFrameCluster findCluster(
            final EntityItemFrameHandle itemFrame,
            final IntVector3 itemFramePosition,
            final boolean includingEmpty
    ) {
        final Predicate<EntityItemFrameHandle> itemFrameFilter;
        if (includingEmpty && ItemUtil.isEmpty(itemFrame.getItem())) {
            // Proceed only filling empty item frames
            itemFrameFilter = e -> ItemUtil.isEmpty(e.getItem());
        } else {
            final UUID itemFrameMapUUID;
            if (this.isFrameTilingSupported && (itemFrameMapUUID = itemFrame.getItemMapDisplayDynamicOnlyUUID()) != null) {
                if (includingEmpty) {
                    itemFrameFilter = e -> {
                        return itemFrameMapUUID.equals(e.getItemMapDisplayDynamicOnlyUUID()) ||
                               ItemUtil.isEmpty(e.getItem());
                    };
                } else {
                    itemFrameFilter = e -> {
                        return itemFrameMapUUID.equals(e.getItemMapDisplayDynamicOnlyUUID());
                    };
                }
            } else {
                // no neighbours or tiling disabled
                return new ItemFrameCluster(OfflineWorld.of(itemFrame.getBukkitWorld()),
                        itemFrame.getFacing(),
                        Collections.singleton(itemFramePosition), 0);
            }
        }

        // Look up in cache first
        World world = itemFrame.getBukkitWorld();
        Map<IntVector3, ItemFrameCluster> cachedClusters;
        if (!includingEmpty && itemFrameClustersByWorldEnabled) {
            cachedClusters = itemFrameClustersByWorld.get(world);
            if (cachedClusters == null) {
                cachedClusters = new HashMap<>();
                itemFrameClustersByWorld.put(world, cachedClusters);
            }
            ItemFrameCluster fromCache = cachedClusters.get(itemFramePosition);
            if (fromCache != null) {
                return fromCache;
            }
        } else {
            cachedClusters = null;
        }

        // Take cache entry
        FindNeighboursCache cache = this.findNeighboursCache;
        if (cache != null) {
            cache.reset();
            this.findNeighboursCache = null;
        } else {
            cache = new FindNeighboursCache();
        }
        try {
            // Find all item frames that:
            // - Are on the same world as this item frame
            // - Facing the same way
            // - Along the same x/y/z (facing)
            // - Same ItemStack map UUID (or, if includingEmpty, are empty)

            ItemFrameClusterKey key = new ItemFrameClusterKey(world, itemFrame.getFacing(), itemFramePosition);
            for (EntityItemFrameHandle otherFrame : getItemFrameEntities(key)) {
                if (otherFrame.getId() != itemFrame.getId() && itemFrameFilter.test(otherFrame)) {
                    cache.put(otherFrame);
                }
            }

            BlockFace[] neighbourAxis;
            if (FaceUtil.isAlongY(key.facing)) {
                neighbourAxis = NEIGHBOUR_AXIS_ALONG_Y;
            } else if (FaceUtil.isAlongX(key.facing)) {
                neighbourAxis = NEIGHBOUR_AXIS_ALONG_X;
            } else {
                neighbourAxis = NEIGHBOUR_AXIS_ALONG_Z;
            }

            // Find the most common item frame rotation in use
            // Only 4 possible rotations can be used for maps, so this is easy
            int[] rotation_counts = new int[4];
            rotation_counts[(new FindNeighboursCache.Frame(itemFrame)).rotation]++;

            // Make sure the neighbours result are a single contiguous blob
            // Islands (can not reach the input item frame) are removed
            Set<IntVector3> result = new HashSet<IntVector3>(cache.cache.size());
            result.add(itemFramePosition);
            cache.pendingList.add(itemFramePosition);
            do {
                IntVector3 pending = cache.pendingList.poll();
                for (BlockFace side : neighbourAxis) {
                    IntVector3 sidePoint = pending.add(side);
                    FindNeighboursCache.Frame frame = cache.cache.remove(sidePoint);
                    if (frame != null) {
                        rotation_counts[frame.rotation]++;
                        cache.pendingList.add(sidePoint);
                        result.add(sidePoint);
                    }
                }
            } while (!cache.pendingList.isEmpty());

            // Find maximum rotation index
            int rotation_idx = 0;
            for (int i = 1; i < rotation_counts.length; i++) {
                if (rotation_counts[i] > rotation_counts[rotation_idx]) {
                    rotation_idx = i;
                }
            }

            // The final combined result
            ItemFrameCluster cluster = new ItemFrameCluster(OfflineWorld.of(key.world), key.facing, result, rotation_idx * 90);
            if (cachedClusters != null) {
                for (IntVector3 position : cluster.coordinates) {
                    cachedClusters.put(position, cluster);
                }
            }

            return cluster;
        } finally {
            // Return to cache
            this.findNeighboursCache = cache;
        }
    }

    private static final class FindNeighboursCache {
        // Stores potential multi-ItemFrame neighbours during findNeighbours() temporarily
        public final HashMap<IntVector3, Frame> cache = new HashMap<IntVector3, Frame>();
        // Stores the coordinates of the item frames whose neighbours still need to be checked during findNeighbours()
        public final Queue<IntVector3> pendingList = new ArrayDeque<IntVector3>();

        // Called before use
        public void reset() {
            cache.clear();
            pendingList.clear();
        }

        // Helper
        public void put(EntityItemFrameHandle itemFrame) {
            cache.put(itemFrame.getBlockPosition(), new Frame(itemFrame));
        }

        // Single entry
        public static final class Frame {
            public final int rotation;

            public Frame(EntityItemFrameHandle itemFrame) {
                this.rotation = itemFrame.getRotationOrdinal() & 0x3;
            }
        }
    }

    private final void deinitItemFrameListForWorldsNotIn(Collection<World> worlds) {
        Iterator<ItemFrameClusterKey> iter = this.itemFrameEntities.keySet().iterator();
        while (iter.hasNext()) {
            if (!worlds.contains(iter.next().world)) {
                iter.remove();
            }
        }
    }

    private final synchronized void deinitItemFrameSetOfWorld(World world) {
        Iterator<ItemFrameClusterKey> iter = this.itemFrameEntities.keySet().iterator();
        while (iter.hasNext()) {
            if (iter.next().world == world) {
                iter.remove();
            }
        }
    }

    private final List<EntityItemFrameHandle> initItemFrameSetOfWorld(World world) {
        List<EntityItemFrameHandle> itemFrames = new ArrayList<EntityItemFrameHandle>();
        for (Object entityHandle : (Iterable<?>) WorldServerHandle.T.getEntities.raw.invoke(HandleConversion.toWorldHandle(world))) {
            if (EntityItemFrameHandle.T.isAssignableFrom(entityHandle)) {
                EntityItemFrameHandle itemFrame = EntityItemFrameHandle.createHandle(entityHandle);
                getItemFrameEntities(new ItemFrameClusterKey(itemFrame)).add(itemFrame);
                itemFrames.add(itemFrame);
            }
        }
        return itemFrames;
    }

    private final Set<EntityItemFrameHandle> getItemFrameEntities(ItemFrameClusterKey key) {
        Set<EntityItemFrameHandle> set = this.itemFrameEntities.get(key);
        if (set == null) {
            set = new HashSet<EntityItemFrameHandle>();
            this.itemFrameEntities.put(key, set);
        }
        return set;
    }

    /**
     * Gets the Item displayed in an ItemFrame. Bukkit discards NBT Metadata, which is pretty annoying.
     * Always use this method instead.
     * 
     * @param itemFrame to get the item from
     * @return item displayed, null if empty
     */
    public static ItemStack getItemFrameItem(ItemFrame itemFrame) {
        return EntityItemFrameHandle.fromBukkit(itemFrame).getItem();
    }

    /**
     * Sets the item displayed in an ItemFrame. Bukkit discards NBT Metadata, which is pretty annoying.
     * Always use this method instead.
     * 
     * @param itemFrame to set the item for
     * @param item to set
     */
    public static void setItemFrameItem(ItemFrame itemFrame, ItemStack item) {
        EntityItemFrameHandle.fromBukkit(itemFrame).setItem(item);
    }

    /**
     * Removes all NBT data for map items that is unimportant for clients to know
     * 
     * @param item
     * @return new item copy with metadata trimmed
     */
    public static CommonItemStack trimExtraData(CommonItemStack item) {
        // If empty, return empty. Simples.
        if (item.isEmpty()) {
            return CommonItemStack.empty();
        }

        // If item has no metadata tag, there is no need to clone it
        final CommonTagCompound oldTag = item.getCustomData();
        if (oldTag.isEmpty()) {
            // Make sure item is a CraftItemStack so we can access NBT properly
            // If metadata tag is null, that's okay.
            if (!item.isCraftItemStack()) {
                throw new IllegalArgumentException("Input item is no CraftItemStack");
            }

            return item;
        }

        // Get rid of all custom metadata from the item
        // Only Minecraft items are interesting (because its the Minecraft client)
        item = item.clone();
        item.setCustomData(null); // Avoids unneeded clone overhead
        item.updateCustomData(newTag -> {
            final String[] nbt_filter = {
                    "ench", "display", "RepairCost",
                    "AttributeModifiers", "CanDestroy",
                    "CanPlaceOn", "Unbreakable",

                    // Also keep Map Display specific tags alive
                    // This is important to prevent potential corruption
                    "mapDisplayUUIDMost", "mapDisplayUUIDLeast",
                    "mapDisplayPlugin", "mapDisplayClass"
            };
            for (String filter : nbt_filter) {
                if (oldTag.containsKey(filter)) {
                    newTag.put(filter, oldTag.get(filter));
                }
            }
        });
        return item;
    }

    /**
     * Initializes a Map Display. This interface provides access to the internals
     * of the display, so that others using MapDisplay don't see the initialize() function.
     */
    @FunctionalInterface
    public static interface MapDisplayInitializeFunction {
        void initialize(MapDisplay display, JavaPlugin plugin, ItemStack mapItem);
    }

    /**
     * Avoids static map ids being used that are used by other plugins.
     * Required for some plugins which can interfere otherwise.
     */
    private static final class StaticMapIdFilter {
        public final IntPredicate filter;
        public final Plugin owner;

        public StaticMapIdFilter(Plugin owner, IntPredicate filter) {
            this.owner = owner;
            this.filter = filter;
        }
    }
}
