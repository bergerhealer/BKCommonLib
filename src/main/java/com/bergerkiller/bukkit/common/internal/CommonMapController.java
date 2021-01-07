package com.bergerkiller.bukkit.common.internal;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.collections.ImplicitlySharedSet;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.events.EntityAddEvent;
import com.bergerkiller.bukkit.common.events.EntityRemoveEvent;
import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.events.map.MapAction;
import com.bergerkiller.bukkit.common.events.map.MapClickEvent;
import com.bergerkiller.bukkit.common.events.map.MapShowEvent;
import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.map.MapDisplayProperties;
import com.bergerkiller.bukkit.common.map.MapDisplayTile;
import com.bergerkiller.bukkit.common.map.MapSession;
import com.bergerkiller.bukkit.common.map.binding.ItemFrameInfo;
import com.bergerkiller.bukkit.common.map.binding.MapDisplayInfo;
import com.bergerkiller.bukkit.common.map.util.MapUUID;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.map.MapPlayerInput;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.ItemUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.PlayerUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap;
import com.bergerkiller.generated.net.minecraft.server.EntityHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityItemFrameHandle;
import com.bergerkiller.generated.net.minecraft.server.WorldServerHandle;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;
import com.bergerkiller.mountiplex.reflection.util.OutputTypeMap;

public class CommonMapController implements PacketListener, Listener {
    // Stores cached thread-safe lists of item frames by cluster key
    private final Map<ItemFrameClusterKey, Set<EntityItemFrameHandle> > itemFrameEntities = new HashMap<>();
    // Bi-directional mapping between map UUID and Map (durability) Id
    private final IntHashMap<MapUUID> mapUUIDById = new IntHashMap<MapUUID>();
    private final HashMap<MapUUID, Integer> mapIdByUUID = new HashMap<MapUUID, Integer>();
    // Stores Map Displays, mapped by Map UUID
    private final HashMap<UUID, MapDisplayInfo> maps = new HashMap<UUID, MapDisplayInfo>();
    // Stores map items for a short time while a player is moving it around in creative mode
    private final HashMap<UUID, CachedMapItem> cachedMapItems = new HashMap<UUID, CachedMapItem>();
    // How long a cached item is kept around and tracked when in the creative player's control
    private static final int CACHED_ITEM_MAX_LIFE = 20*60*10; // 10 minutes
    private static final int CACHED_ITEM_CLEAN_INTERVAL = 60; //60 ticks
    // Stores Map Displays by their Type information
    private final OutputTypeMap<MapDisplay> displays = new OutputTypeMap<MapDisplay>();
    // Stores player map input (through Vehicle Steer packets)
    private final HashMap<Player, MapPlayerInput> playerInputs = new HashMap<Player, MapPlayerInput>();
    // Tracks all item frames loaded on the server
    // Note: we are not using an IntHashMap because we need to iterate over the values, which is too slow with IntHashMap
    private final Map<Integer, ItemFrameInfo> itemFrames = new HashMap<>();
    // Tracks entity id's for which item metadata was sent before itemFrameInfo was available
    private final Set<Integer> itemFrameMetaMisses = new HashSet<>();
    // Tracks all maps that need to have their Map Ids re-synchronized (item slot / itemframe metadata updates)
    private HashSet<UUID> dirtyMapUUIDSet = new HashSet<UUID>();
    // Whether to automatically load neighbouring chunks when item frames are found on the edges
    private boolean LOAD_BORDER_CHUNKS = false;
    // Stores neighbouring chunks of chunk-bordering item frames that must be loaded in case they are part of a multi-display
    private final ImplicitlySharedSet<PendingChunkLoad> neighbourChunkQueue = new ImplicitlySharedSet<PendingChunkLoad>();
    // Caches used while executing findNeighbours()
    private FindNeighboursCache findNeighboursCache = null;
    // Whether this controller has been enabled
    private boolean isEnabled = false;
    // Whether tiling is supported. Disables findNeighbours() if false.
    private boolean isFrameTilingSupported = true;
    // Neighbours of item frames to check for either x-aligned or z-aligned
    private static final BlockFace[] NEIGHBOUR_AXIS_ALONG_X = {BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH};
    private static final BlockFace[] NEIGHBOUR_AXIS_ALONG_Y = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
    private static final BlockFace[] NEIGHBOUR_AXIS_ALONG_Z = {BlockFace.UP, BlockFace.DOWN, BlockFace.WEST, BlockFace.EAST};
    // Item frame clusters previously computed, is short-lived
    private final Map<World, Map<IntVector3, ItemFrameCluster>> itemFrameClustersByWorld = new IdentityHashMap<>();
    // Whether the short-lived cache is used (only used during the update cycle)
    private boolean itemFrameClustersByWorldEnabled = false;
    // This counter is incremented every time a new map Id is added to the mapping
    // Every 1000 map ids we do a cleanup to free up slots for maps that no longer exist on the server
    // This is required, otherwise we can run out of the 32K map Ids we have available given enough uptime
    private static final int GENERATION_COUNTER_CLEANUP_INTERVAL = 1000;
    private int idGenerationCounter = 0;

    /**
     * These packet types are listened to handle the virtualized Map Display API
     */
    public static final PacketType[] PACKET_TYPES = {
            PacketType.OUT_MAP, PacketType.IN_STEER_VEHICLE, 
            PacketType.OUT_WINDOW_ITEMS, PacketType.OUT_WINDOW_SET_SLOT,
            PacketType.OUT_ENTITY_METADATA, PacketType.IN_SET_CREATIVE_SLOT
    };

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
    public synchronized void resendMapData(Player player) {
        UUID playerUUID = player.getUniqueId();
        for (MapDisplayInfo display : maps.values()) {
            if (display.getViewStackByPlayerUUID(playerUUID) != null) {
                for (MapSession session : display.getSessions()) {
                    for (MapSession.Owner owner : session.onlineOwners) {
                        if (owner.player == player) {
                            owner.clip.markEverythingDirty();
                        }
                    }
                }
            }
        }
    }

    /**
     * Gets the Map display information for a map item displayed in an item frame.
     * All frames showing the same map will return the same {@link #MapDisplayInfo}.
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
                info = new MapDisplayInfo(frameInfo.lastMapUUID.getUUID());
                maps.put(frameInfo.lastMapUUID.getUUID(), info);
            }
            return info;
        }
        return getInfo(getItemFrameItem(itemFrame));
    }

    /**
     * Gets the Map display information for a certain map item.
     * All items showing the same map will return the same {@link #MapDisplayInfo}.
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
     * 
     * @param mapUUID of the map
     * @return display info for this UUID
     */
    public synchronized MapDisplayInfo getInfo(UUID mapUUID) {
        if (mapUUID == null) {
            return null;
        }
        MapDisplayInfo info = maps.get(mapUUID);
        if (info == null) {
            info = new MapDisplayInfo(mapUUID);
            maps.put(mapUUID, info);
        }
        return info;
    }

    /**
     * Updates the information of a map item, refreshing all item frames
     * and player inventories storing the item. Map displays are also
     * updated.
     * 
     * @param oldItem that was changed
     * @param newItem the old item was changed into
     */
    public synchronized void updateMapItem(ItemStack oldItem, ItemStack newItem) {
        boolean unchanged = isItemUnchanged(oldItem, newItem);
        UUID oldMapUUID = CommonMapUUIDStore.getMapUUID(oldItem);
        if (oldMapUUID != null) {
            // Change in the inventories of all player owners
            for (Player player : Bukkit.getOnlinePlayers()) {
                PlayerInventory inv = player.getInventory();
                for (int i = 0; i < inv.getSize(); i++) {
                    UUID mapUUID = CommonMapUUIDStore.getMapUUID(inv.getItem(i));
                    if (oldMapUUID.equals(mapUUID)) {
                        if (unchanged) {
                            PlayerUtil.setItemSilently(player, i, newItem);
                        } else {
                            inv.setItem(i, newItem);
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
                        dataItem.setValue(newItem, dataItem.isChanged());
                    } else {
                        // When changed, set it normally so the item is refreshed
                        itemFrameInfo.itemFrameHandle.setItem(newItem);
                    }
                }
            }

            // All map displays showing this item
            MapDisplayInfo info = maps.get(oldMapUUID);
            if (info != null) {
                for (MapSession session : info.getSessions()) {
                    session.display.setMapItemSilently(newItem);
                }
            }
        }

    }

    private boolean isItemUnchanged(ItemStack item1, ItemStack item2) {
        ItemStack trimmed_old_item = CommonMapController.trimExtraData(item1);
        ItemStack trimmed_new_item = CommonMapController.trimExtraData(item2);
        return LogicUtil.bothNullOrEqual(trimmed_old_item, trimmed_new_item);
    }

    /**
     * Starts all continuous background update tasks for maps
     * 
     * @param plugin
     * @param startedTasks
     */
    public void onEnable(CommonPlugin plugin, List<Task> startedTasks) {
        startedTasks.add(new HeldMapUpdater(plugin).start(1, 1));
        startedTasks.add(new FramedMapUpdater(plugin).start(1, 1));
        startedTasks.add(new ItemMapIdUpdater(plugin).start(1, 1));
        startedTasks.add(new MapInputUpdater(plugin).start(1, 1));
        startedTasks.add(new CachedMapItemCleaner(plugin).start(100, CACHED_ITEM_CLEAN_INTERVAL));
        startedTasks.add(new ByWorldItemFrameSetRefresher(plugin).start(1200, 1200)); // every minute

        this.isFrameTilingSupported = plugin.isFrameTilingSupported();

        // Discover all item frames that exist at plugin load, in already loaded worlds and chunks
        // This is only relevant during /reload, since at server start no world is loaded yet
        for (World world : Bukkit.getWorlds()) {
            for (EntityItemFrameHandle itemFrame : initItemFrameSetOfWorld(world)) {
                onAddItemFrame(itemFrame);
            }
        }

        // Done!
        this.isEnabled = true;
    }

    /**
     * Cleans up all running map displays and de-initializes all map display logic
     */
    public void onDisable() {
        if (this.isEnabled) {
            this.isEnabled = false;
            for (MapDisplayInfo map : new ArrayList<MapDisplayInfo>(this.maps.values())) {
                for (MapSession session : new ArrayList<MapSession>(map.getSessions())) {
                    session.display.setRunning(false);
                }
            }
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
        } else {
            // End all map display sessions for this plugin
            MapDisplay.stopDisplaysForPlugin(plugin);
        }
    }

    /**
     * Adjusts the internal remapping from UUID to Map Id taking into account the new item
     * being synchronized to the player. If the item is that of a virtual map, the map Id
     * of the item is updated. NBT data that should not be synchronized is dropped.
     * 
     * @param item
     * @param tileX the X-coordinate of the tile in which the item is displayed
     * @param tileY the Y-coordinate of the tile in which the item is displayed
     * @return True if the item was changed and needs to be updated in the packet
     */
    public ItemStack handleItemSync(ItemStack item, int tileX, int tileY) {
        if (!CommonMapUUIDStore.isMap(item)) {
            return null;
        }

        // When a map UUID is specified, use that to dynamically allocate a map Id to use
        CommonTagCompound tag = ItemUtil.getMetaTag(item, false);
        if (tag != null) {
            UUID mapUUID = tag.getUUID("mapDisplay");
            if (mapUUID != null) {
                item = trimExtraData(item);
                CommonMapUUIDStore.setItemMapId(item, getMapId(new MapUUID(mapUUID, tileX, tileY)));
                return item;
            }
        }

        // Static map Id MUST be enforced
        storeStaticMapId(CommonMapUUIDStore.getItemMapId(item));
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

        // Increment this counter. The Map Id updater task will clean up unused maps every 1000 cycles.
        idGenerationCounter++;

        // Figure out a free Map Id we can use
        final int MAX_IDS = CommonCapabilities.MAP_ID_IN_NBT ? Integer.MAX_VALUE : Short.MAX_VALUE;
        for (int mapidValue = 0; mapidValue < MAX_IDS; mapidValue++) {
            if (!mapUUIDById.contains(mapidValue)) {
                // Check if the Map Id was changed compared to before
                boolean idChanged = mapIdByUUID.containsKey(mapUUID);

                // Store in mapping
                mapUUIDById.put(mapidValue, mapUUID);
                mapIdByUUID.put(mapUUID, Integer.valueOf(mapidValue));

                // Invalidate display if it exists
                MapDisplayInfo mapInfo = maps.get(mapUUID.getUUID());
                if (mapInfo != null) {
                    for (MapSession session : mapInfo.getSessions()) {
                        session.display.invalidate();
                    }
                }

                if (idChanged) {
                    dirtyMapUUIDSet.add(mapUUID.getUUID());
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
            int itemid = event.getPacket().read(PacketType.OUT_MAP.itemId);
            MapUUID mapUUID = mapUUIDById.get(itemid);
            if (mapUUID == null) {
                this.storeStaticMapId(itemid);
            } else if (CommonMapUUIDStore.getStaticMapId(mapUUID.getUUID()) == -1) {
                event.setCancelled(true);
            }
        }

        // Correct Map ItemStacks as they are sent to the clients (virtual)
        // This is always tile 0,0 (held map)
        if (event.getType() == PacketType.OUT_WINDOW_ITEMS) {
            List<ItemStack> items = event.getPacket().read(PacketType.OUT_WINDOW_ITEMS.items);
            ListIterator<ItemStack> iter = items.listIterator();
            while (iter.hasNext()) {
                ItemStack newItem = this.handleItemSync(iter.next(), 0, 0);
                if (newItem != null) {
                    iter.set(newItem);
                }
            }
        }
        if (event.getType() == PacketType.OUT_WINDOW_SET_SLOT) {
            ItemStack oldItem = event.getPacket().read(PacketType.OUT_WINDOW_SET_SLOT.item);
            ItemStack newItem = this.handleItemSync(oldItem, 0, 0);
            if (newItem != null) {
                event.getPacket().write(PacketType.OUT_WINDOW_SET_SLOT.item, newItem);
            }
        }
 
        // Correct the ItemStack displayed in Item Frames
        if (event.getType() == PacketType.OUT_ENTITY_METADATA) {
            int entityId = event.getPacket().read(PacketType.OUT_ENTITY_METADATA.entityId);
            ItemFrameInfo frameInfo = this.itemFrames.get(entityId);
            if (frameInfo == null) {
                // Verify the Item Frame DATA_ITEM key is inside the metadata of this packet
                // If this is the case, then this is metadata for an item frame and not a different entity
                // When that happens then a metadata packet was sent before the entity add event for it fired
                // To prevent glitches, track that in the itemFrameMetaMisses set
                List<DataWatcher.Item<Object>> items = event.getPacket().read(PacketType.OUT_ENTITY_METADATA.watchedObjects);
                if (items != null) {
                    for (DataWatcher.Item<Object> item : items) {
                        if (EntityItemFrameHandle.DATA_ITEM.equals(item.getKey())) {
                            itemFrameMetaMisses.add(entityId);
                            break;
                        }
                    }
                }
                return; // no information available or not an item frame
            }

            frameInfo.updateItem();
            if (frameInfo.lastMapUUID == null) {
                return; // not a map
            }
            frameInfo.sentToPlayers = true;
            int staticMapId = CommonMapUUIDStore.getStaticMapId(frameInfo.lastMapUUID.getUUID());
            if (staticMapId != -1) {
                this.storeStaticMapId(staticMapId);
                return; // static Id, not dynamic, no re-assignment
            }

            // Map Id is dynamically assigned, adjust metadata items to use this new Id
            // Avoid using any Bukkit or Wrapper types here for performance reasons
            int newMapId = this.getMapId(frameInfo.lastMapUUID);
            List<DataWatcher.Item<Object>> items = event.getPacket().read(PacketType.OUT_ENTITY_METADATA.watchedObjects);
            if (items != null) {
                ListIterator<DataWatcher.Item<Object>> itemsIter = items.listIterator();
                while (itemsIter.hasNext()) {
                    DataWatcher.Item<ItemStack> item = itemsIter.next().translate(EntityItemFrameHandle.DATA_ITEM);
                    if (item == null) {
                        continue;
                    }

                    ItemStack metaItem = item.getValue();
                    if (metaItem == null || CommonMapUUIDStore.getItemMapId(metaItem) == newMapId) {
                        continue;
                    }

                    ItemStack newMapItem = ItemUtil.cloneItem(metaItem);
                    CommonMapUUIDStore.setItemMapId(newMapItem, newMapId);
                    item = item.clone();
                    item.setValue(newMapItem, item.isChanged());
                    itemsIter.set((DataWatcher.Item<Object>) (DataWatcher.Item) item);
                }
            }
        }
    }

    @Override
    public synchronized void onPacketReceive(PacketReceiveEvent event) {
        // Handle input coming from the player for the map
        if (event.getType() == PacketType.IN_STEER_VEHICLE) {
            Player p = event.getPlayer();
            MapPlayerInput input = playerInputs.get(p);
            if (input != null) {
                CommonPacket packet = event.getPacket();
                int dx = (int) -Math.signum(packet.read(PacketType.IN_STEER_VEHICLE.sideways));
                int dy = (int) -Math.signum(packet.read(PacketType.IN_STEER_VEHICLE.forwards));
                int dz = 0;
                if (packet.read(PacketType.IN_STEER_VEHICLE.unmount)) {
                    dz -= 1;
                }
                if (packet.read(PacketType.IN_STEER_VEHICLE.jump)) {
                    dz += 1;
                }

                // Receive input. If it will be handled, it will cancel further handling of this packet
                event.setCancelled(input.receiveInput(dx, dy, dz));
            }
        }

        // When in creative mode, players may accidentally set the 'virtual' map Id as the actual Id in their inventory
        // We have to prevent that in here
        if (event.getType() == PacketType.IN_SET_CREATIVE_SLOT) {
            ItemStack item = event.getPacket().read(PacketType.IN_SET_CREATIVE_SLOT.item);
            UUID mapUUID = CommonMapUUIDStore.getMapUUID(item);
            if (mapUUID != null && CommonMapUUIDStore.getStaticMapId(mapUUID) == -1) {
                // Dynamic Id map. Since we do not refresh NBT data over the network, this packet contains incorrect data
                // Find the original item the player took (by UUID). If it exists, merge its NBT data with this item.
                // For this we also have the map item cache, which is filled with data the moment a player picks up an item
                // This data is kept around for 10 minutes (unlikely a player will hold onto it for that long...)
                ItemStack originalMapItem = null;
                CachedMapItem cachedItem = this.cachedMapItems.get(mapUUID);
                if (cachedItem != null) {
                    cachedItem.life = CACHED_ITEM_MAX_LIFE;
                    originalMapItem = cachedItem.item;
                } else {
                    for (ItemStack oldItem : event.getPlayer().getInventory()) {
                        if (mapUUID.equals(CommonMapUUIDStore.getMapUUID(oldItem))) {
                            originalMapItem = oldItem.clone();
                            break;
                        }
                    }
                }
                if (originalMapItem != null) {
                    // Original item was found. Restore all properties of that item.
                    // Keep metadata the player can control, replace everything else
                    ItemUtil.setMetaTag(item, ItemUtil.getMetaTag(originalMapItem));
                    event.getPacket().write(PacketType.IN_SET_CREATIVE_SLOT.item, item);
                } else {
                    // Dynamic Id. Force a map id value of 0 to prevent creation of new World Map instances
                    item = ItemUtil.cloneItem(item);
                    CommonMapUUIDStore.setItemMapId(item, 0);
                    event.getPacket().write(PacketType.IN_SET_CREATIVE_SLOT.item, item);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    protected synchronized void onPlayerJoin(PlayerJoinEvent event) {
        // Let everyone know we got a player over here!
        Player player = event.getPlayer();
        for (MapDisplayInfo map : this.maps.values()) {
            for (MapSession session : map.getSessions()) {
                session.updatePlayerOnline(player);
            }
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
        if (event.getEntityType() == EntityType.ITEM_FRAME) {
            EntityItemFrameHandle frameHandle = EntityItemFrameHandle.createHandle(HandleConversion.toEntityHandle(event.getEntity()));
            getItemFrameEntities(new ItemFrameClusterKey(frameHandle)).add(frameHandle);
            onAddItemFrame(frameHandle);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    protected synchronized void onEntityRemoved(EntityRemoveEvent event) {
        if (event.getEntityType() == EntityType.ITEM_FRAME) {
            ItemFrame frame = (ItemFrame) event.getEntity();
            EntityItemFrameHandle frameHandle = EntityItemFrameHandle.fromBukkit(frame);
            getItemFrameEntities(new ItemFrameClusterKey(frameHandle)).remove(frameHandle);
            ItemFrameInfo info = itemFrames.get(frame.getEntityId());
            if (info != null) {
                info.removed = true;
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    protected synchronized void onWorldLoad(WorldLoadEvent event) {
        for (EntityItemFrameHandle frame : initItemFrameSetOfWorld(event.getWorld())) {
            onAddItemFrame(frame);
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
            UUID mapUUID = CommonMapUUIDStore.getMapUUID(event.getCurrentItem());
            if (mapUUID != null) {
                this.cachedMapItems.put(mapUUID, new CachedMapItem(event.getCurrentItem().clone()));
            }
        }
    }

    private void onAddItemFrame(EntityItemFrameHandle frame) {
        int entityId = frame.getId();
        if (itemFrames.containsKey(entityId)) {
            return;
        }

        // Add Item Frame Info
        ItemFrameInfo frameInfo = new ItemFrameInfo(this, frame);
        itemFrames.put(entityId, frameInfo);
        if (itemFrameMetaMisses.remove(entityId)) {
            frameInfo.needsItemRefresh = true;
            frameInfo.sentToPlayers = true;
        }

        // If frame tiling is disabled, then neighbouring chunks don't have to be loaded
        // If the item frame does not store a map item, we don't have to load the neighbouring chunks
        if (!this.LOAD_BORDER_CHUNKS || !this.isFrameTilingSupported || !frame.getItemIsMap()) {
            return;
        }

        // Queue chunks left/right of this item frame for loading
        // If the display crosses chunk boundaries, this ensures those are loaded
        World world = frame.getBukkitWorld();
        BlockFace facing = frame.getFacing();
        if (FaceUtil.isAlongY(facing)) {
            // Along Y, all chunks surrounding it touching the item frame should be loaded
            IntVector3 pos = frame.getBlockPosition();
            IntVector2 chunk = pos.toChunkCoordinates();
            PendingChunkLoad chunk_neigh0 = new PendingChunkLoad(world, pos.add(1, 0, 0));
            PendingChunkLoad chunk_neigh1 = new PendingChunkLoad(world, pos.add(0, 0, 1));
            PendingChunkLoad chunk_neigh2 = new PendingChunkLoad(world, pos.add(-1, 0, 0));
            PendingChunkLoad chunk_neigh3 = new PendingChunkLoad(world, pos.add(0, 0, -1));
            if (chunk.x != chunk_neigh0.x || chunk.z != chunk_neigh0.z) {
                neighbourChunkQueue.add(chunk_neigh0);
            }
            if (chunk.x != chunk_neigh1.x || chunk.z != chunk_neigh1.z) {
                neighbourChunkQueue.add(chunk_neigh1);
            }
            if (chunk.x != chunk_neigh2.x || chunk.z != chunk_neigh2.z) {
                neighbourChunkQueue.add(chunk_neigh2);
            }
            if (chunk.x != chunk_neigh3.x || chunk.z != chunk_neigh3.z) {
                neighbourChunkQueue.add(chunk_neigh3);
            }
        } else {
            // Along X or Z, check chunks loaded in other two directions
            BlockFace left_right = FaceUtil.rotate(facing, 2);
            IntVector3 pos = frame.getBlockPosition();
            IntVector2 chunk = pos.toChunkCoordinates();
            PendingChunkLoad chunk_left = new PendingChunkLoad(world, pos.add(left_right));
            PendingChunkLoad chunk_right = new PendingChunkLoad(world, pos.subtract(left_right));
            if (chunk.x != chunk_left.x || chunk.z != chunk_left.z) {
                neighbourChunkQueue.add(chunk_left);
            }
            if (chunk.x != chunk_right.x || chunk.z != chunk_right.z) {
                neighbourChunkQueue.add(chunk_right);
            }
        }
    }

    private boolean dispatchClickAction(Player player, ItemFrame itemFrame, double dx, double dy, MapAction action) {
        if (player.isSneaking()) {
            return false; // do not click while sneaking to allow for normal block interaction
        }
        double px = (dx * (double) MapDisplayTile.RESOLUTION);
        double py = (dy * (double) MapDisplayTile.RESOLUTION);
        if (px < 0 || py < 0 || px >= 128 || py >= 128) {
            return false; // not within map canvas
        }

        MapDisplayInfo info = getInfo(itemFrame);
        if (info == null) {
            return false; // no map here
        }

        // Find the Display this player is sees on this map
        MapDisplayInfo.ViewStack stack = info.getViewStackByPlayerUUID(player.getUniqueId());
        if (stack == null || stack.stack.isEmpty()) {
            return false; // no visible display for this player
        }

        // Adjust px/py based on item frame tile information
        ItemFrameInfo frameInfo = this.itemFrames.get(itemFrame.getEntityId());
        if (frameInfo != null) {
            frameInfo.updateItem();
            frameInfo.lastFrameItemUpdateNeeded = true; // post-click refresh
            if (frameInfo.lastMapUUID != null) {
                px += MapDisplayTile.RESOLUTION * frameInfo.lastMapUUID.getTileX();
                py += MapDisplayTile.RESOLUTION * frameInfo.lastMapUUID.getTileY();
            }
        }

        MapClickEvent event = new MapClickEvent(player, itemFrame, stack.stack.getLast(), action, px, py);
        CommonUtil.callEvent(event);
        if (!event.isCancelled()) {
            if (action == MapAction.LEFT_CLICK) {
                event.getDisplay().onLeftClick(event);
                event.getDisplay().getRootWidget().onLeftClick(event);
            } else {
                event.getDisplay().onRightClick(event);
                event.getDisplay().getRootWidget().onRightClick(event);
            }
        }
        return event.isCancelled();
    }

    private boolean dispatchClickActionApprox(Player player, ItemFrame itemFrame, MapAction action) {
        // Calculate the vector position on the map that was clicked
        BlockFace attachedFace = itemFrame.getAttachedFace();
        Location playerPos = player.getEyeLocation();
        Vector dir = playerPos.getDirection();
        Block itemBlock = EntityUtil.getHangingBlock(itemFrame);
        double target_x = (double) itemBlock.getX() + 1.0;
        double target_y = (double) itemBlock.getY() + 1.0;
        double target_z = (double) itemBlock.getZ() + 1.0;

        final double FRAME_OFFSET = 0.0625; // offset from wall
        double dx, dy;

        if (FaceUtil.isAlongZ(attachedFace)) {
            if (attachedFace == BlockFace.NORTH) {
                target_z -= 1.0;
            }
            target_z -= attachedFace.getModZ() * FRAME_OFFSET;
            dir.multiply((target_z - playerPos.getZ()) / dir.getZ());
            dx = target_x - (playerPos.getX() + dir.getX());
            dy = target_y - (playerPos.getY() + dir.getY());
            if (attachedFace == BlockFace.NORTH) {
                dx = 1.0 - dx;
            }
        } else if (FaceUtil.isAlongX(attachedFace)) {
            if (attachedFace == BlockFace.WEST) {
                target_x -= 1.0;
            }
            target_x -= attachedFace.getModX() * FRAME_OFFSET;
            dir.multiply((target_x - playerPos.getX()) / dir.getX());
            dx = target_z - (playerPos.getZ() + dir.getZ());
            dy = target_y - (playerPos.getY() + dir.getY());
            if (attachedFace == BlockFace.EAST) {
                dx = 1.0 - dx;
            }
        } else {
            //TODO: Vertical
            dx = 0.5;
            dy = 0.5;
        }
        return dispatchClickAction(player, itemFrame, dx, dy, action);
    }

    private boolean dispatchClickActionFromBlock(Player player, Block clickedBlock, BlockFace clickedFace, MapAction action) {
        double x = clickedBlock.getX() + 0.5 + (double) clickedFace.getModX() * 0.5;
        double y = clickedBlock.getY() + 0.5 + (double) clickedFace.getModY() * 0.5;
        double z = clickedBlock.getZ() + 0.5 + (double) clickedFace.getModZ() * 0.5;
        for (Entity e : WorldUtil.getEntities(clickedBlock.getWorld(), null, 
                x - 0.01, y - 0.01, z - 0.01,
                x + 0.01, y + 0.01, z + 0.01))
        {
            if (e instanceof ItemFrame) {
                return dispatchClickActionApprox(player, (ItemFrame) e, action);
            }
        }
        return false;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    protected void onEntityLeftClick(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof ItemFrame) || !(event.getDamager() instanceof Player)) {
            return;
        }
        event.setCancelled(dispatchClickActionApprox(
                (Player) event.getDamager(),
                (ItemFrame) event.getEntity(),
                MapAction.LEFT_CLICK));
    }

    private Vector lastClickOffset = null;

    @EventHandler(priority = EventPriority.MONITOR)
    protected void onEntityRightClickAt(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof ItemFrame) {
            lastClickOffset = event.getClickedPosition();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    protected void onEntityRightClick(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof ItemFrame)) {
            return;
        }
        ItemFrame itemFrame = (ItemFrame) event.getRightClicked();
        if (lastClickOffset != null) {
            Vector pos = lastClickOffset;
            lastClickOffset = null;
            BlockFace attachedFace = itemFrame.getAttachedFace();
            double dx, dy;
            if (FaceUtil.isAlongZ(attachedFace)) {
                dx = pos.getX() + 0.5;
                dy = 1.0 - (pos.getY() + 0.5);
                if (attachedFace == BlockFace.SOUTH) {
                    dx = 1.0 - dx;
                }
            } else {
                dx = pos.getZ() + 0.5;
                dy = 1.0 - (pos.getY() + 0.5);
                if (attachedFace == BlockFace.WEST) {
                    dx = 1.0 - dx;
                }
            }
            event.setCancelled(dispatchClickAction(event.getPlayer(), itemFrame, dx, dy, MapAction.RIGHT_CLICK));
        } else {
            event.setCancelled(dispatchClickActionApprox(event.getPlayer(), itemFrame, MapAction.RIGHT_CLICK));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
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

    private synchronized Set<UUID> getDirtyMapUUIDs() {
        if (this.dirtyMapUUIDSet.isEmpty()) {
            return Collections.emptySet();
        } else {
            Set<UUID> result = this.dirtyMapUUIDSet;
            this.dirtyMapUUIDSet = new HashSet<UUID>();
            return result;
        }
    }

    private synchronized void cleanupUnusedUUIDs(Set<MapUUID> existingMapUUIDs) {
        HashSet<MapUUID> idsToRemove = new HashSet<MapUUID>(mapIdByUUID.keySet());
        idsToRemove.removeAll(existingMapUUIDs);
        for (MapUUID toRemove : idsToRemove) {
            // Clean up the map display information first
            MapDisplayInfo displayInfo = maps.get(toRemove.getUUID());
            if (displayInfo != null) {
                if (displayInfo.getSessions().isEmpty()) {
                    maps.remove(toRemove.getUUID());
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
            dirtyMapUUIDSet.remove(toRemove.getUUID());
        }
    }

    private synchronized void handleMapShowEvent(MapShowEvent event) {
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
        MapDisplayProperties properties = MapDisplayProperties.of(event.getMapItem());
        if (!hasDisplay && !event.hasDisplay() && properties != null) {
            Class<? extends MapDisplay> displayClass = properties.getMapDisplayClass();
            if (displayClass != null) {
                Plugin plugin = properties.getPlugin();
                if (plugin != null) {
                    try {
                        MapDisplay display = displayClass.newInstance();
                        event.setDisplay((JavaPlugin) plugin, display);;
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
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
    private MapUUID getItemFrameMapUUID(EntityItemFrameHandle itemFrame) {
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

    public class ItemMapIdUpdater extends Task {

        public ItemMapIdUpdater(JavaPlugin plugin) {
            super(plugin);
        }

        @Override
        public void run() {
            synchronized (CommonMapController.this) {
                updateMapIds();
            }
        }

        public void updateMapIds() {
            // Remove non-existing maps from the internal mapping
            if (idGenerationCounter > GENERATION_COUNTER_CLEANUP_INTERVAL) {
                idGenerationCounter = 0;

                // Find all map UUIDs that exist on the server
                HashSet<MapUUID> validUUIDs = new HashSet<MapUUID>();
                for (Set<EntityItemFrameHandle> itemFrameSet : itemFrameEntities.values()) {
                    for (EntityItemFrameHandle itemFrame : itemFrameSet) {
                        MapUUID mapUUID = getItemFrameMapUUID(itemFrame);
                        if (mapUUID != null) {
                            validUUIDs.add(mapUUID);
                        }
                    }
                }

                for (Player player : Bukkit.getOnlinePlayers()) {
                    PlayerInventory inv = player.getInventory();
                    for (int i = 0; i < inv.getSize(); i++) {
                        ItemStack item = inv.getItem(i);
                        UUID mapUUID = CommonMapUUIDStore.getMapUUID(item);
                        if (mapUUID != null) {
                            validUUIDs.add(new MapUUID(mapUUID));
                        }
                    }
                }

                // Perform the cleanup (synchronized access required!)
                cleanupUnusedUUIDs(validUUIDs);
            }

            // Refresh items known to clients when Map Ids are re-assigned
            Set<UUID> dirtyMaps = getDirtyMapUUIDs();
            if (!dirtyMaps.isEmpty()) {
                // Refresh all item frames that display this map
                // This will result in a new EntityMetadata packets being sent, refreshing the map Id
                for (Set<EntityItemFrameHandle> itemFrameSet : itemFrameEntities.values()) {
                    for (EntityItemFrameHandle itemFrame : itemFrameSet) {
                        UUID mapUUID = itemFrame.getItemMapDisplayUUID();
                        if (dirtyMaps.contains(mapUUID)) {
                            itemFrame.refreshItem();
                        }
                    }
                }

                // Refresh all player inventories that contain this map
                // This will result in new SetItemSlot packets being sent, refreshing the map Id
                for (Player player : Bukkit.getOnlinePlayers()) {
                    PlayerInventory inv = player.getInventory();
                    for (int i = 0; i < inv.getSize(); i++) {
                        ItemStack item = inv.getItem(i);
                        UUID mapUUID = CommonMapUUIDStore.getMapUUID(item);
                        if (dirtyMaps.contains(mapUUID)) {
                            inv.setItem(i, item.clone());
                        }
                    }
                }
            }
        }
    }

    /**
     * Refreshes the input state of maps every tick, when input is intercepted
     */
    public class MapInputUpdater extends Task {

        public MapInputUpdater(JavaPlugin plugin) {
            super(plugin);
        }

        @Override
        public void run() {
            Iterator<Map.Entry<Player, MapPlayerInput>> iter = playerInputs.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<Player, MapPlayerInput> entry = iter.next();
                if (entry.getKey().isOnline()) {
                    entry.getValue().onTick();
                } else {
                    entry.getValue().onDisconnected();
                    iter.remove();
                }
            }
        }
    }

    /**
     * Updates the players viewing item frames and fires events for them
     */
    public class FramedMapUpdater extends Task {

        private ItemFrameInfo info = null;
        private final LogicUtil.ItemSynchronizer<Player, Player> synchronizer = new LogicUtil.ItemSynchronizer<Player, Player>() {
            @Override
            public boolean isItem(Player item, Player value) {
                return item == value;
            }

            @Override
            public Player onAdded(Player player) {
                handleMapShowEvent(new MapShowEvent(player, info.itemFrame));
                return player;
            }

            @Override
            public void onRemoved(Player player) {
                //TODO!!!
                //CommonUtil.callEvent(new HideFramedMapEvent(player, info.itemFrame));
            }
        };

        public FramedMapUpdater(JavaPlugin plugin) {
            super(plugin);
        }

        @Override
        public void run() {
            // Load neighbouring chunks
            if (LOAD_BORDER_CHUNKS) {
                while (!neighbourChunkQueue.isEmpty()) {
                    try (ImplicitlySharedSet<PendingChunkLoad> copy = neighbourChunkQueue.clone()) {
                        // Load all the chunks
                        for (PendingChunkLoad chunk : copy) {
                            chunk.world.getChunkAt(chunk.x, chunk.z);
                        }

                        // Remove the chunks we have loaded
                        neighbourChunkQueue.removeAll(copy);
                    }
                }
            }

            // Enable the item frame cluster cache
            itemFrameClustersByWorldEnabled = true;

            // Iterate all tracked item frames and update them
            Iterator<ItemFrameInfo> itemFrames_iter = itemFrames.values().iterator();
            while (itemFrames_iter.hasNext()) {
                info = itemFrames_iter.next();
                if (info.handleRemoved()) {
                    itemFrames_iter.remove();
                    continue;
                }

                info.updateItemAndViewers(synchronizer);

                // May find out it's removed during the update
                if (info.handleRemoved()) {
                    itemFrames_iter.remove();
                    continue;
                }
            }

            // Update the player viewers of all map displays
            for (MapDisplayInfo map : maps.values()) {
                map.updateViewersAndResolution();
            }

            for (ItemFrameInfo info : itemFrames.values()) {
                // Resend Item Frame item (metadata) when the UUID changes
                // UUID can change when the relative tile displayed changes
                // This happens when a new item frame is placed left/above a display
                if (info.needsItemRefresh) {
                    info.needsItemRefresh = false;
                    info.itemFrameHandle.refreshItem();
                }
            }

            // Disable cache again and wipe
            itemFrameClustersByWorldEnabled = false;
            itemFrameClustersByWorld.clear();
        }
    }

    /**
     * Continuously checks if a map item is being held by a player
     */
    public class HeldMapUpdater extends Task implements LogicUtil.ItemSynchronizer<Player, HeldMapUpdater.MapViewEntry> {
        private final List<MapViewEntry> entries = new LinkedList<MapViewEntry>();

        public HeldMapUpdater(JavaPlugin plugin) {
            super(plugin);
        }

        @Override
        public void run() {
            LogicUtil.synchronizeList(entries, CommonUtil.getOnlinePlayers(), this);
            for (MapViewEntry entry : entries) {
                entry.update();
            }
        }

        @Override
        public boolean isItem(MapViewEntry entry, Player player) {
            return entry.player == player;
        }

        @Override
        public MapViewEntry onAdded(Player player) {
            return new MapViewEntry(player);
        }

        @Override
        public void onRemoved(MapViewEntry entry) {
        }

        private class MapViewEntry {
            public final Player player;
            public ItemStack lastLeftHand = null;
            public ItemStack lastRightHand = null;

            public MapViewEntry(Player player) {
                this.player = player;
            }

            public void update() {
                ItemStack currLeftHand = PlayerUtil.getItemInHand(this.player, HumanHand.LEFT);
                ItemStack currRightHand = PlayerUtil.getItemInHand(this.player, HumanHand.RIGHT);

                if (CommonMapUUIDStore.isMap(currLeftHand) 
                        && !mapEquals(currLeftHand, lastLeftHand) 
                        && !mapEquals(currLeftHand, lastRightHand)) {
                    // Left hand now has a map! We did not swap hands, either.
                    handleMapShowEvent(new MapShowEvent(player, HumanHand.LEFT, currLeftHand));
                }
                if (CommonMapUUIDStore.isMap(currRightHand) 
                        && !mapEquals(currRightHand, lastRightHand) 
                        && !mapEquals(currRightHand, lastLeftHand)) {
                    // Right hand now has a map! We did not swap hands, either.
                    handleMapShowEvent(new MapShowEvent(player, HumanHand.RIGHT, currRightHand));
                }

                lastLeftHand = currLeftHand;
                lastRightHand = currRightHand;
            }

            private final boolean mapEquals(ItemStack item1, ItemStack item2) {
                UUID mapUUID1 = CommonMapUUIDStore.getMapUUID(item1);
                UUID mapUUID2 = CommonMapUUIDStore.getMapUUID(item2);
                return mapUUID1 != null && mapUUID2 != null && mapUUID1.equals(mapUUID2);
            }
        }
    }

    /**
     * Removes map items from the cache when they have been in there for too long
     */
    public class CachedMapItemCleaner extends Task {

        public CachedMapItemCleaner(JavaPlugin plugin) {
            super(plugin);
        }

        @Override
        public void run() {
            synchronized (CommonMapController.this) {
                if (!CommonMapController.this.cachedMapItems.isEmpty()) {
                    Iterator<CachedMapItem> iter = CommonMapController.this.cachedMapItems.values().iterator();
                    while (iter.hasNext()) {
                        if ((iter.next().life -= CACHED_ITEM_CLEAN_INTERVAL) <= 0) {
                            iter.remove();
                        }
                    }
                }
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
                for (World world : worlds) {
                    initItemFrameSetOfWorld(world);
                }
            }
        }
    }

    /**
     * An item that sits around in memory while players in creative mode are moving the item around.
     * 
     */
    private static class CachedMapItem {
        public int life;
        public final ItemStack item;

        public CachedMapItem(ItemStack item) {
            this.item = item;
            this.life = CACHED_ITEM_MAX_LIFE;
        }
    }

    private static class PendingChunkLoad {
        public final World world;
        public final int x;
        public final int z;

        public PendingChunkLoad(World world, IntVector3 pos) {
            this.world = world;
            this.x = pos.getChunkX();
            this.z = pos.getChunkZ();
        }

        @Override
        public int hashCode() {
            int result = 1;
            result = 31 * result + this.world.hashCode();
            result = 31 * result + this.x;
            result = 31 * result + this.z;
            return result;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof PendingChunkLoad) {
                PendingChunkLoad other = (PendingChunkLoad) o;
                return this.world == other.world &&
                       this.x == other.x &&
                       this.z == other.z;
            }
            return false;
        }
    }

    /**
     * Finds a cluster of all connected item frames that an item frame is part of
     * 
     * @param itemFrame
     * @return cluster
     */
    public final synchronized ItemFrameCluster findCluster(EntityItemFrameHandle itemFrame, IntVector3 itemFramePosition) {
        UUID itemFrameMapUUID;
        if (!this.isFrameTilingSupported || (itemFrameMapUUID = itemFrame.getItemMapDisplayUUID()) == null) {
            return new ItemFrameCluster(itemFrame.getFacing(),
                    Collections.singletonList(itemFramePosition), 0); // no neighbours or tiling disabled
        }

        // Look up in cache first
        World world = itemFrame.getBukkitWorld();
        Map<IntVector3, ItemFrameCluster> cachedClusters;
        if (itemFrameClustersByWorldEnabled) {
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
            // - Same ItemStack map UUID

            ItemFrameClusterKey key = new ItemFrameClusterKey(world, itemFrame.getFacing(), itemFramePosition);
            for (EntityItemFrameHandle otherFrame : getItemFrameEntities(key)) {
                if (otherFrame.getId() == itemFrame.getId()) {
                    continue;
                }
                UUID otherFrameMapUUID = otherFrame.getItemMapDisplayUUID();
                if (itemFrameMapUUID.equals(otherFrameMapUUID)) {
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
            List<IntVector3> result = new ArrayList<IntVector3>(cache.cache.size());
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
            ItemFrameCluster cluster = new ItemFrameCluster(key.facing, result, rotation_idx * 90);
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
    public static ItemStack trimExtraData(ItemStack item) {
        // If null, return null. Simples.
        if (item == null) {
            return null;
        }

        // Get rid of all custom metadata from the item
        // Only Minecraft items are interesting (because its the Minecraft client)
        CommonTagCompound oldTag = ItemUtil.getMetaTag(item, false);
        CommonTagCompound newTag = new CommonTagCompound();
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

        item = ItemUtil.cloneItem(item);
        ItemUtil.setMetaTag(item, newTag);
        return item;
    }

    /**
     * Group of item frames that are connected together and face the same way
     */
    public static class ItemFrameCluster {
        // Facing of the display
        public final BlockFace facing;
        // List of coordinates where item frames are stored
        public final List<IntVector3> coordinates;
        // Most common ItemFrame rotation used for the display
        public final int rotation;
        // Minimum/maximum coordinates and size of the item frame coordinates in this cluster
        public final IntVector3 min_coord, max_coord;
        // Resolution in rotation/facing relative space (unused)
        // public final IntVector3 size;
        // public final IntVector2 resolution;

        public ItemFrameCluster(BlockFace facing, List<IntVector3> coordinates, int rotation) {
            this.facing = facing;
            this.coordinates = coordinates;
            this.rotation = rotation;

            if (hasMultipleTiles()) {
                // Compute minimum/maximum x and z coordinates
                Iterator<IntVector3> iter = coordinates.iterator();
                IntVector3 coord = iter.next();
                int min_x, max_x, min_y, max_y, min_z, max_z;
                min_x = max_x = coord.x; min_y = max_y = coord.y; min_z = max_z = coord.z;
                while (iter.hasNext()) {
                    coord = iter.next();
                    if (coord.x < min_x) min_x = coord.x;
                    if (coord.y < min_y) min_y = coord.y;
                    if (coord.z < min_z) min_z = coord.z;
                    if (coord.x > max_x) max_x = coord.x;
                    if (coord.y > max_y) max_y = coord.y;
                    if (coord.z > max_z) max_z = coord.z;
                }
                min_coord = new IntVector3(min_x, min_y, min_z);
                max_coord = new IntVector3(max_x, max_y, max_z);
            } else {
                min_coord = max_coord = coordinates.get(0);
            }

            // Compute resolution (unused)
            /*
            if (hasMultipleTiles()) {
                size = max_coord.subtract(min_coord);
                if (facing.getModY() > 0) {
                    // Vertical pointing up
                    // We use rotation of the item frame to decide which side is up
                    switch (rotation) {
                    case 90:
                    case 270:
                        resolution = new IntVector2(size.z+1, size.x+1);
                        break;
                    case 180:
                    default:
                        resolution = new IntVector2(size.x+1, size.z+1);
                        break;
                    }
                } else if (facing.getModY() < 0) {
                    // Vertical pointing down
                    // We use rotation of the item frame to decide which side is up
                    switch (rotation) {
                    case 90:
                    case 270:
                        resolution = new IntVector2(size.z+1, size.x+1);
                        break;
                    case 180:
                    default:
                        resolution = new IntVector2(size.x+1, size.z+1);
                        break;
                    }
                } else {
                    // On the wall
                    switch (facing) {
                    case NORTH:
                    case SOUTH:
                        resolution = new IntVector2(size.x+1, size.y+1);
                        break;
                    case EAST:
                    case WEST:
                        resolution = new IntVector2(size.z+1, size.y+1);
                        break;
                    default:
                        resolution = new IntVector2(1, 1);
                        break;
                    }
                }
            } else {
                resolution = new IntVector2(1, 1);
                size = IntVector3.ZERO;
            }
            */
        }

        public boolean hasMultipleTiles() {
            return coordinates.size() > 1;
        }
    }

    /**
     * When clustering item frames (finding neighbours), this key is used
     * to store a mapping of what item frames exist on the server
     */
    private static class ItemFrameClusterKey {
        public final World world;
        public final BlockFace facing;
        public final int coordinate;

        public ItemFrameClusterKey(EntityItemFrameHandle itemFrame) {
            this(itemFrame.getBukkitWorld(), itemFrame.getFacing(), itemFrame.getBlockPosition());
        }

        public ItemFrameClusterKey(World world, BlockFace facing, IntVector3 coordinates) {
            this.world = world;
            this.facing = facing;
            this.coordinate = facing.getModX()*coordinates.x +
                              facing.getModY()*coordinates.y +
                              facing.getModZ()*coordinates.z;
        }

        @Override
        public int hashCode() {
            return this.coordinate + (facing.ordinal()<<6);
        }

        @Override
        public boolean equals(Object o) {
            ItemFrameClusterKey other = (ItemFrameClusterKey) o;
            return other.coordinate == this.coordinate && (other.world == this.world || other.world.equals(this.world)) && other.facing == this.facing;
        }
    }
}
