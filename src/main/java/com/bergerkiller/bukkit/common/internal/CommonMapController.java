package com.bergerkiller.bukkit.common.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.events.EntityAddEvent;
import com.bergerkiller.bukkit.common.events.EntityRemoveEvent;
import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.events.map.MapAction;
import com.bergerkiller.bukkit.common.events.map.MapClickEvent;
import com.bergerkiller.bukkit.common.events.map.MapShowEvent;
import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.map.MapSession;
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
import com.bergerkiller.generated.net.minecraft.server.EntityTrackerEntryHandle;
import com.bergerkiller.generated.net.minecraft.server.WorldHandle;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;
import com.bergerkiller.mountiplex.reflection.util.OutputTypeMap;

public class CommonMapController implements PacketListener, Listener {
    // Bi-directional mapping between map UUID and Map (durability) Id
    private final IntHashMap<MapUUID> mapUUIDById = new IntHashMap<MapUUID>();
    private final HashMap<MapUUID, Short> mapIdByUUID = new HashMap<MapUUID, Short>();
    // Stores Map Displays, mapped by Map UUID
    private final HashMap<UUID, MapDisplayInfo> maps = new HashMap<UUID, MapDisplayInfo>();
    // Stores Map Displays by their Type information
    private final OutputTypeMap<MapDisplay> displays = new OutputTypeMap<MapDisplay>();
    // Stores player map input (through Vehicle Steer packets)
    private final HashMap<Player, MapPlayerInput> playerInputs = new HashMap<Player, MapPlayerInput>();
    // Tracks all item frames loaded on the server
    private final IntHashMap<ItemFrameInfo> itemFrames = new IntHashMap<ItemFrameInfo>();
    // Tracks all maps that need to have their Map Ids re-synchronized (item slot / itemframe metadata updates)
    private HashSet<UUID> dirtyMapUUIDSet = new HashSet<UUID>();
    // This counter is incremented every time a new map Id is added to the mapping
    // Every 1000 map ids we do a cleanup to free up slots for maps that no longer exist on the server
    // This is required, otherwise we can run out of the 32K map Ids we have available given enough uptime
    private static final int GENERATION_COUNTER_CLEANUP_INTERVAL = 1000;
    private int idGenerationCounter = 0;
    // When items are updated we sometimes wish to do so silently, without refreshing the client
    // This prevents the client hiding and re-showing the map when data is updated
    private static final ThreadLocal<Boolean> disableMapItemChanges = new ThreadLocal<Boolean>() {
        protected Boolean initialValue() { return false; }
    };

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
    public List<ItemFrameInfo> getItemFrames() {
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
                            disableMapItemChanges.set(true);
                            inv.setItem(i, newItem);
                            PlayerUtil.markItemUnchanged(player, i);
                            disableMapItemChanges.set(false);
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
                        disableMapItemChanges.set(true);
                        DataWatcher data = EntityHandle.fromBukkit(itemFrameInfo.itemFrame).getDataWatcher();
                        DataWatcher.Item<ItemStack> dataItem = data.getItem(EntityItemFrameHandle.DATA_ITEM);
                        dataItem.setValue(newItem, dataItem.isChanged());
                        disableMapItemChanges.set(false);
                    } else {
                        // When changed, set it normally so the item is refreshed
                        CommonMapController.setItemFrameItem(itemFrameInfo.itemFrame, newItem);
                    }
                }
            }

            // All map displays showing this item
            MapDisplayInfo info = maps.get(oldMapUUID);
            if (info != null) {
                for (MapSession session : info.sessions) {
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
    public void onEnable(JavaPlugin plugin, List<Task> startedTasks) {
        startedTasks.add(new HeldMapUpdater(plugin).start(1, 1));
        startedTasks.add(new FramedMapUpdater(plugin).start(1, 1));
        startedTasks.add(new ItemMapIdUpdater(plugin).start(1, 1));
        startedTasks.add(new MapInputUpdater(plugin).start(1, 1));
    }

    /**
     * Cleans up all running map displays and de-initializes all map display logic
     */
    public void onDisable() {
        for (MapDisplayInfo map : new ArrayList<MapDisplayInfo>(this.maps.values())) {
            for (MapSession session : new ArrayList<MapSession>(map.sessions)) {
                session.display.setRunning(false);
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
        if (item == null || item.getType() != Material.MAP) {
            return null;
        }

        // When a map UUID is specified, use that to dynamically allocate a map Id to use
        CommonTagCompound tag = ItemUtil.getMetaTag(item, false);
        if (tag != null) {
            UUID mapUUID = tag.getUUID("mapDisplay");
            if (mapUUID != null) {
                item = trimExtraData(item);
                item.setDurability(getMapId(new MapUUID(mapUUID, tileX, tileY)));
                return item;
            }
        }

        // Static map Id MUST be enforced
        storeStaticMapId(item.getDurability());
        return null;
    }

    /**
     * Obtains the Map Id used for displaying a particular map UUID
     * 
     * @param mapUUID to be displayed
     * @return map Id
     */
    public synchronized short getMapId(MapUUID mapUUID) {
        // Obtain from cache
        Short storedMapId = mapIdByUUID.get(mapUUID);
        if (storedMapId != null) {
            return storedMapId.shortValue();
        }

        // If the UUID is that of a static UUID, we must make sure to store it as such
        // We may have to remap the old Map Id to free up the Id slot we need
        short mapId = CommonMapUUIDStore.getStaticMapId(mapUUID.getUUID());
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
    private synchronized void storeStaticMapId(short mapId) {
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
    private synchronized short storeDynamicMapId(MapUUID mapUUID) {
        // Null safety check
        if (mapUUID == null) {
            return -1;
        }

        // If the UUID is static, do not store anything and return the static Id instead
        short staticMapid = CommonMapUUIDStore.getStaticMapId(mapUUID.getUUID());
        if (staticMapid != -1) {
            return staticMapid;
        }

        // Increment this counter. The Map Id updater task will clean up unused maps every 1000 cycles.
        idGenerationCounter++;

        // Figure out a free Map Id we can use
        for (int mapidValue = 0; mapidValue < Short.MAX_VALUE; mapidValue++) {
            if (!mapUUIDById.contains(mapidValue)) {
                // Check if the Map Id was changed compared to before
                boolean idChanged = mapIdByUUID.containsKey(mapUUID);

                // Store in mapping
                mapUUIDById.put(mapidValue, mapUUID);
                mapIdByUUID.put(mapUUID, Short.valueOf((short) mapidValue));

                // Invalidate display if it exists
                MapDisplayInfo mapInfo = maps.get(mapUUID.getUUID());
                if (mapInfo != null) {
                    for (MapSession session : mapInfo.sessions) {
                        session.display.invalidate();
                    }
                }

                if (idChanged) {
                    dirtyMapUUIDSet.add(mapUUID.getUUID());
                }

                return (short) mapidValue;
            }
        }
        return -1;
    }

    @Override
    public synchronized void onPacketSend(PacketSendEvent event) {
        // Check if any virtual single maps are attached to this map
        if (event.getType() == PacketType.OUT_MAP) {    
            int itemid = event.getPacket().read(PacketType.OUT_MAP.itemId);
            MapUUID mapUUID = mapUUIDById.get(itemid);
            if (mapUUID == null) {
                this.storeStaticMapId((short) itemid);
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
                if (disableMapItemChanges.get()) {
                    event.setCancelled(true);
                } else {
                    event.getPacket().write(PacketType.OUT_WINDOW_SET_SLOT.item, newItem);
                }
            }
        }
 
        // Correct the ItemStack displayed in Item Frames
        if (event.getType() == PacketType.OUT_ENTITY_METADATA) {
            int entityId = event.getPacket().read(PacketType.OUT_ENTITY_METADATA.entityId);
            Entity entity = WorldHandle.fromBukkit(event.getPlayer().getWorld()).getEntityById(entityId);
            if (!(entity instanceof ItemFrame)) {
                return;
            }
            ItemFrameInfo frameInfo = this.itemFrames.get(entity.getEntityId());
            if (frameInfo == null) {
                return; // no information available
            }

            frameInfo.updateItem();
            if (frameInfo.lastMapUUID == null) {
                return; // not a map
            }
            frameInfo.sentToPlayers = true;
            if (disableMapItemChanges.get()) {
                event.setCancelled(true);
                return; // map changes are suppressed
            }
            short staticMapId = CommonMapUUIDStore.getStaticMapId(frameInfo.lastMapUUID.getUUID());
            if (staticMapId != -1) {
                this.storeStaticMapId(staticMapId);
                return; // static Id, not dynamic, no re-assignment
            }

            // Map Id is dynamically assigned, adjust metadata items to use this new Id
            // Avoid using any Bukkit or Wrapper types here for performance reasons
            short newMapId = this.getMapId(frameInfo.lastMapUUID);
            List<DataWatcher.Item<Object>> items = event.getPacket().read(PacketType.OUT_ENTITY_METADATA.watchedObjects);
            if (items != null) {
                ListIterator<DataWatcher.Item<Object>> itemsIter = items.listIterator();
                while (itemsIter.hasNext()) {
                    DataWatcher.Item<ItemStack> item = itemsIter.next().translate(EntityItemFrameHandle.DATA_ITEM);
                    if (item == null) {
                        continue;
                    }

                    ItemStack metaItem = item.getValue();
                    if (metaItem == null || metaItem.getDurability() == newMapId) {
                        continue;
                    }

                    ItemStack newMapItem = ItemUtil.cloneItem(metaItem);
                    newMapItem.setDurability(newMapId);
                    item = item.clone();
                    item.setValue(newMapItem, item.isChanged());
                    itemsIter.set(CommonUtil.unsafeCast(item));
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
                ItemStack originalMapItem = null;
                for (ItemStack oldItem : event.getPlayer().getInventory()) {
                    if (mapUUID.equals(CommonMapUUIDStore.getMapUUID(oldItem))) {
                        originalMapItem = oldItem.clone();
                        break;
                    }
                }

                if (originalMapItem != null) {
                    // Original item was found. Restore all properties of that item.
                    event.getPacket().write(PacketType.IN_SET_CREATIVE_SLOT.item, originalMapItem);
                } else {
                    // Dynamic Id. Force a durability value of 0 to prevent creation of new World Map instances
                    item = ItemUtil.cloneItem(item);
                    item.setDurability((short) 0);
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
            for (MapSession session : map.sessions) {
                session.updatePlayerOnline(player);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    protected synchronized void onEntityAdded(EntityAddEvent event) {
        if (event.getEntityType() == EntityType.ITEM_FRAME) {
            ItemFrame frame = (ItemFrame) event.getEntity();
            itemFrames.put(frame.getEntityId(), new ItemFrameInfo(frame));

            // Load the chunk to the left/right of this item frame
            // If the display crosses chunk boundaries, this ensures those are loaded
            // TODO: Is onEntityAdded really the right place for this? Could cause recursive loading.
            BlockFace left_right = FaceUtil.rotate(frame.getFacing(), 2);
            IntVector3 pos = new IntVector3(frame.getLocation());
            IntVector3 pos_left = pos.add(left_right);
            IntVector3 pos_right = pos.subtract(left_right);
            if (pos.getChunkX() != pos_left.getChunkX() || pos.getChunkZ() != pos_left.getChunkZ()) {
                frame.getWorld().getChunkAt(pos_left.getChunkX(), pos_left.getChunkZ());
            }
            if (pos.getChunkX() != pos_right.getChunkX() || pos.getChunkZ() != pos_right.getChunkZ()) {
                frame.getWorld().getChunkAt(pos_right.getChunkX(), pos_right.getChunkZ());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    protected synchronized void onEntityRemoved(EntityRemoveEvent event) {
        if (event.getEntityType() == EntityType.ITEM_FRAME) {
            ItemFrame frame = (ItemFrame) event.getEntity();
            ItemFrameInfo info = itemFrames.get(frame.getEntityId());
            if (info != null) {
                info.removed = true;
            }
        }
    }

    private boolean dispatchClickAction(Player player, ItemFrame itemFrame, double dx, double dy, MapAction action) {
        if (player.isSneaking()) {
            return false; // do not click while sneaking to allow for normal block interaction
        }
        int px = (int) (dx * 127.0);
        int py = (int) (dy * 127.0);
        if (px < 0 || py < 0 || px >= 128 || py >= 128) {
            return false; // not within map canvas
        }

        MapDisplayInfo info = getInfo(itemFrame);
        if (info == null) {
            return false; // no map here
        }

        // Find the Display this player is sees on this map
        ViewStack stack = info.views.get(player);
        if (stack == null || stack.stack.isEmpty()) {
            return false; // no visible display for this player
        }

        // Adjust px/py based on item frame tile information
        ItemFrameInfo frameInfo = this.itemFrames.get(itemFrame.getEntityId());
        if (frameInfo != null && frameInfo.lastMapUUID != null) {
            px += 128 * frameInfo.lastMapUUID.getTileX();
            py += 128 * frameInfo.lastMapUUID.getTileY();
        }

        MapClickEvent event = new MapClickEvent(player, itemFrame, stack.stack.getLast(), action, px, py);
        CommonUtil.callEvent(event);
        if (!event.isCancelled()) {
            if (action == MapAction.LEFT_CLICK) {
                event.getDisplay().onLeftClick(event);
            } else {
                event.getDisplay().onRightClick(event);
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
        } else {
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
                if (displayInfo.sessions.isEmpty()) {
                    maps.remove(toRemove.getUUID());
                } else {
                    continue; // still has an active session; cannot remove
                }
            }

            // Clean up from bi-directional mapping
            Short mapId = mapIdByUUID.remove(toRemove);
            if (mapId != null) {
                mapUUIDById.remove(mapId.intValue());
            }

            // Clean up from 'dirty' set (probably never needed)
            dirtyMapUUIDSet.remove(toRemove);
        }
    }

    private synchronized void handleMapShowEvent(MapShowEvent event) {
        // Check if there are other map displays that should be shown to the player automatically
        // This uses the 'isGlobal()' property of the display
        MapDisplayInfo info = CommonMapController.this.getInfo(event.getMapUUID());
        boolean hasDisplay = false;
        if (info != null) {
            for (MapSession session : info.sessions) {
                if (session.display.isGlobal()) {
                    session.display.addOwner(event.getPlayer());
                    hasDisplay = true;
                    break;
                }
            }
        }

        // When defined in the NBT of the item, construct the Map Display automatically
        CommonTagCompound tag = ItemUtil.getMetaTag(event.getMapItem(), false);
        if (tag != null && !hasDisplay) {
            String pluginName = tag.getValue("mapDisplayPlugin", String.class);
            String displayClassName = tag.getValue("mapDisplayClass", String.class);
            if (pluginName != null && displayClassName != null) {
                Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
                Class<?> displayClass = null;
                if (plugin != null) {
                    try {
                        displayClass = plugin.getClass().getClassLoader().loadClass(displayClassName);
                        if (!MapDisplay.class.isAssignableFrom(displayClass)) {
                            displayClass = null;
                        }
                    } catch (ClassNotFoundException e) {
                        
                    }
                }
                if (displayClass != null && !event.hasDisplay()) {
                    try {
                        MapDisplay display = (MapDisplay) displayClass.newInstance();
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
    private MapUUID getItemFrameMapUUID(ItemFrame itemFrame) {
        if (itemFrame == null) {
            return null;
        } else {
            ItemFrameInfo info = this.itemFrames.get(itemFrame.getEntityId());
            if (info == null) {
                return null;
            } else {
                info.updateItem();
                return info.lastMapUUID;
            }
        }
    }

    /**
     * Maintains the metadata information for a map
     */
    public class MapDisplayInfo {
        public final UUID uuid; /* map UUID */

        // Maintains information about the item frames that show this map, and what players
        // can see this map on the item frames
        public final ArrayList<ItemFrameInfo> itemFrames = new ArrayList<ItemFrameInfo>();
        public final LinkedHashSet<Player> frameViewers = new LinkedHashSet<Player>();
        private boolean hasFrameViewerChanges = true;
        private boolean resetDisplayRequest = false;

        // A list of all active running displays bound to this map
        public final ArrayList<MapSession> sessions = new ArrayList<MapSession>();

        // Maps the display view stack by player
        public final HashMap<Player, ViewStack> views = new HashMap<Player, ViewStack>();

        public MapDisplayInfo(UUID uuid) {
            this.uuid = uuid;
        }

        /**
         * Gets whether a certain ItemStack contains this map
         * 
         * @param item to check
         * @return True if the item contains this map
         */
        public boolean isMap(ItemStack item) {
            return this.uuid.equals(CommonMapUUIDStore.getMapUUID(item));
        }

        /**
         * Gets the stack of Map Displays used to display information to a certain player
         * 
         * @param player
         * @return view stack
         */
        public ViewStack getStack(Player player) {
            ViewStack stack = views.get(player);
            if (stack == null) {
                stack = new ViewStack();
                views.put(player, stack);
            }
            return stack;
        }

        /**
         * Sets whether a certain player is viewing a certain Map Display.
         * 
         * @param player
         * @param display
         * @param viewing
         */
        public void setViewing(Player player, MapDisplay display, boolean viewing) {
            ViewStack stack = views.get(player);
            if (viewing) {
                if (stack == null) {
                    stack = new ViewStack();
                    stack.stack.add(display);
                    views.put(player, stack);
                } else {
                    // Make sure the display is at the very end of the list
                    stack.stack.remove(display);
                    stack.stack.add(display);
                }
            } else if (stack != null) {
                stack.stack.remove(display);
            }
        }

        /**
         * Gets the Map Display that is currently displayed on this map for a player
         * 
         * @param player to get the display for
         * @return map display, null if no display is set for this Player
         */
        public MapDisplay getViewing(Player player) {
            ViewStack stack = views.get(player);
            return (stack == null || stack.stack.isEmpty()) ? null : stack.stack.getLast();
        }

        /**
         * Checks whether a player is viewing a certain map display
         * 
         * @param player
         * @param display
         * @return True if viewing, False if not
         */
        public boolean isViewing(Player player, MapDisplay display) {
            return getViewing(player) == display;
        }
    }

    /**
     * Maintains a list of past Map Displays that will be shown in order as sessions end.
     * For example, if a map display for showing on an Item Frame was set, and a new one was set
     * for when the player holds the map, it will 'take over' from the item frame version. Once the
     * player stops viewing the map again, and that session ends, it will automatically fall back to
     * showing the item frame version.
     */
    public class ViewStack {
        public final LinkedList<MapDisplay> stack = new LinkedList<MapDisplay>();

        @Override
        public String toString() {
            String str = "ViewStack:";
            for (MapDisplay display : this.stack) {
                str += "\n  " + display.toString();
            }
            return str;
        }
    }

    /**
     * Maintains metadata information for a single item frame
     */
    public class ItemFrameInfo {
        public final ItemFrame itemFrame;
        public final ArrayList<Player> viewers;
        public MapUUID lastMapUUID; // last known Map UUID (UUID + tile information) of the map shown in this item frame
        public boolean removed; // item frame no longer exists on the server (chunk unloaded, or block removed)
        public boolean isDisplayTile; // item frame is part of a larger set of tiles making up a map display
        public boolean needsItemRefresh; // UUID was changed and item in the item frame needs refreshing
        public boolean sentToPlayers; // players have received item information for this item frame
        public MapDisplayInfo displayInfo;

        public ItemFrameInfo(ItemFrame itemFrame) {
            this.itemFrame = itemFrame;
            this.viewers = new ArrayList<Player>();
            this.removed = false;
            this.lastMapUUID = null;
            this.displayInfo = null;
            this.isDisplayTile = false;
            this.needsItemRefresh = false;
            this.sentToPlayers = false;
        }

        public void updateItem() {
            // Handle changes in map item shown in item frames
            UUID mapUUID = CommonMapUUIDStore.getMapUUID(getItemFrameItem(itemFrame));
            if (mapUUID == null) {
                // Map was removed
                this.sentToPlayers = false;
                if (lastMapUUID != null) {
                    remove();
                }
            } else if (lastMapUUID == null || !lastMapUUID.getUUID().equals(mapUUID)) {
                // Map UUID was changed, or neighbours need to be re-calculated
                recalculateUUID();
            }
        }

        public void recalculateUUID() {
            UUID mapUUID = CommonMapUUIDStore.getMapUUID(getItemFrameItem(itemFrame));

            // Find out the tile information of this item frame
            // This is a slow and lengthy procedure; hopefully it does not happen too often
            // What we do is: we add all neighbours, then find the most top-left item frame
            // Subtracting coordinates will give us the tile x/y of this item frame
            List<IntVector3> neighbours = findNeighbours(itemFrame);
            MapUUID newMapUUID;
            boolean isTile;
            if (!neighbours.isEmpty()) {
                IntVector3 selfPos = new IntVector3(itemFrame.getLocation());
                BlockFace selfFacing = itemFrame.getFacing();
                int tileX = 0;
                int tileY = 0;
                for (IntVector3 neighbour : neighbours) {
                    int dx = selfFacing.getModX() * (selfPos.z - neighbour.z) -
                             selfFacing.getModZ() * (selfPos.x - neighbour.x);
                    int dy = selfPos.y - neighbour.y;
                    if (dx < tileX) {
                        tileX = dx;
                    }
                    if (dy < tileY) {
                        tileY = dy;
                    }
                }
                tileX = -tileX;
                tileY = -tileY;

                newMapUUID = new MapUUID(mapUUID, tileX, tileY);
                isTile = true;
            } else {
                newMapUUID = new MapUUID(mapUUID, 0, 0);
                isTile = false;
            }

            boolean isTiledDisplay = (isDisplayTile && lastMapUUID != null) || (!neighbours.isEmpty());
            boolean readd = (lastMapUUID == null || !lastMapUUID.getUUID().equals(mapUUID));
            if (readd) {
                this.remove();
            }

            if (!newMapUUID.equals(lastMapUUID)) {
                lastMapUUID = newMapUUID;
                isDisplayTile = isTile;
                needsItemRefresh = this.sentToPlayers;
            }

            if (readd) {
                this.add(isTiledDisplay);
            }
        }

        public void remove() {
            if (displayInfo != null) {
                displayInfo.itemFrames.remove(this);
                displayInfo.hasFrameViewerChanges = true;
                if (isDisplayTile && !displayInfo.sessions.isEmpty()) {
                    displayInfo.resetDisplayRequest = true;
                }
                displayInfo = null;
            }
            if (!this.viewers.isEmpty()) {
                //for (Player viewer : viewers) {
                    //TODO NEEDS HIDE EVENT
                    //CommonUtil.callEvent(new HideFramedMapEvent(viewer, itemFrame));
                //}
                viewers.clear();
            }
            this.lastMapUUID = null;
            this.isDisplayTile = false;
        }

        public void add(boolean isTiledDisplay) {
            if (this.displayInfo == null && this.lastMapUUID != null) {
                this.displayInfo = getInfo(this.lastMapUUID.getUUID());
                this.displayInfo.itemFrames.add(this);
            }
            if (isTiledDisplay && !this.displayInfo.sessions.isEmpty()) {
                this.displayInfo.resetDisplayRequest = true;
            }
        }
    }

    public class ItemMapIdUpdater extends Task {

        public ItemMapIdUpdater(JavaPlugin plugin) {
            super(plugin);
        }

        @Override
        public void run() {
            // Remove non-existing maps from the internal mapping
            if (idGenerationCounter > GENERATION_COUNTER_CLEANUP_INTERVAL) {
                idGenerationCounter = 0;

                // Find all map UUIDs that exist on the server
                HashSet<MapUUID> validUUIDs = new HashSet<MapUUID>();
                for (World world : Bukkit.getWorlds()) {
                    for (Entity entity : WorldUtil.getEntities(world)) {
                        if (entity instanceof ItemFrame) {
                            MapUUID mapUUID = getItemFrameMapUUID((ItemFrame) entity);
                            if (mapUUID != null) {
                                validUUIDs.add(mapUUID);
                            }
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
                for (World world : Bukkit.getWorlds()) {
                    for (Entity entity : WorldUtil.getEntities(world)) {
                        if (entity instanceof ItemFrame) {
                            ItemStack item = getItemFrameItem((ItemFrame) entity);
                            UUID mapUUID = CommonMapUUIDStore.getMapUUID(item);
                            if (dirtyMaps.contains(mapUUID)) {
                                ((ItemFrame) entity).setItem(item);
                            }
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
            for (MapPlayerInput input : playerInputs.values()) {
                input.onTick();
            }
        }
    }

    /**
     * Updates the players viewing item frames and fires events for them
     */
    public class FramedMapUpdater extends Task {

        public FramedMapUpdater(JavaPlugin plugin) {
            super(plugin);
        }

        @Override
        public void run() {
            for (IntHashMap.Entry<ItemFrameInfo> entry : itemFrames.entries()) {
                final ItemFrameInfo info = entry.getValue();
                if (info.removed) {
                    // Remove all players that have been set as viewers
                    info.remove();
                    itemFrames.remove(entry.getKey());
                    continue;
                }

                EntityTrackerEntryHandle trackerEntry = WorldUtil.getTracker(info.itemFrame.getWorld()).getEntry(info.itemFrame);
                if (trackerEntry == null) {
                    // Item Frame isn't tracked on the server, so no players can view it
                    info.remove();
                    continue;
                }

                // Refreshes cached information about this item frame's item
                info.updateItem();

                // Update list of players for item frames showing maps
                if (info.lastMapUUID != null) {
                    Collection<Player> liveViewers = trackerEntry.getViewers();
                    boolean changes = LogicUtil.synchronizeList(info.viewers, liveViewers, new LogicUtil.ItemSynchronizer<Player, Player>() {
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
                    });

                    if (changes && info.displayInfo != null) {
                        info.displayInfo.hasFrameViewerChanges = true;
                    }
                }

                // Resend Item Frame item (metadata) when the UUID changes
                // UUID can change when the relative tile displayed changes
                // This happens when a new item frame is placed left/above a display
                if (info.needsItemRefresh) {
                    info.needsItemRefresh = false;
                    setItemFrameItem(info.itemFrame, getItemFrameItem(info.itemFrame));
                }
            }

            // Update the player viewers of all map displays
            for (MapDisplayInfo map : maps.values()) {
                if (map.hasFrameViewerChanges) {
                    map.hasFrameViewerChanges = false;

                    // Recalculate the list of players that can see an item frame
                    map.frameViewers.clear();
                    for (ItemFrameInfo itemFrame : map.itemFrames) {
                        map.frameViewers.addAll(itemFrame.viewers);
                    }

                    // Synchronize this list with the 'global' viewer
                    //if (map.globalFramedDisplay != null) {
                        //map.globalFramedDisplay.setViewers(map.frameViewers);
                    //}
                }
                if (map.resetDisplayRequest) {
                    map.resetDisplayRequest = false;

                    // Refresh all item frames' items showing this map
                    // It is possible their UUID changed as a result of the new tiling
                    for (ItemFrameInfo itemFrame : map.itemFrames) {
                        itemFrame.recalculateUUID();
                    }

                    // Restart all display sessions; their canvas changed resolution or has holes
                    for (MapSession session : new ArrayList<MapSession>(map.sessions)) {
                        MapDisplay display = session.display;
                        display.setRunning(false);
                        display.setRunning(true);
                    }
                }
            }
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

                if (isMap(currLeftHand) 
                        && !mapEquals(currLeftHand, lastLeftHand) 
                        && !mapEquals(currLeftHand, lastRightHand)) {
                    // Left hand now has a map! We did not swap hands, either.
                    handleMapShowEvent(new MapShowEvent(player, HumanHand.LEFT, currLeftHand));
                }
                if (isMap(currRightHand) 
                        && !mapEquals(currRightHand, lastRightHand) 
                        && !mapEquals(currRightHand, lastLeftHand)) {
                    // Right hand now has a map! We did not swap hands, either.
                    handleMapShowEvent(new MapShowEvent(player, HumanHand.RIGHT, currRightHand));
                }

                lastLeftHand = currLeftHand;
                lastRightHand = currRightHand;
            }

            private final boolean isMap(ItemStack item) {
                return item != null && item.getType() == Material.MAP;
            }

            private final boolean mapEquals(ItemStack item1, ItemStack item2) {
                UUID mapUUID1 = CommonMapUUIDStore.getMapUUID(item1);
                UUID mapUUID2 = CommonMapUUIDStore.getMapUUID(item2);
                return mapUUID1 != null && mapUUID2 != null && mapUUID1.equals(mapUUID2);
            }
        }
    }

    /**
     * Finds all connected neighbours of an item frame
     * 
     * @param itemFrame
     */
    private static List<IntVector3> findNeighbours(ItemFrame itemFrame) {
        HashSet<IntVector3> neighbours = new HashSet<IntVector3>();
        BlockFace facing = itemFrame.getFacing();
        IntVector3 itemFramePos = new IntVector3(itemFrame.getLocation());
        UUID itemFrameMapUUID = CommonMapUUIDStore.getMapUUID(getItemFrameItem(itemFrame));
        if (itemFrameMapUUID == null) {
            return Collections.emptyList(); // no neighbours
        }

        // Find all item frames that:
        // - Are on the same world as this item frame
        // - Facing the same way
        // - Along the same x/z (facing)
        // - Same ItemStack map UUID
        for (Entity entity : WorldUtil.getEntities(itemFrame.getWorld())) {
            if (!(entity instanceof ItemFrame) || entity == itemFrame) {
                continue;
            }
            ItemFrame otherFrame = (ItemFrame) entity;
            if (otherFrame.getFacing() != facing) {
                continue;
            }
            IntVector3 otherFramePos = new IntVector3(otherFrame.getLocation());
            if (FaceUtil.isAlongX(facing)) {
                if (otherFramePos.x != itemFramePos.x) {
                    continue;
                }
            } else {
                if (otherFramePos.z != itemFramePos.z) {
                    continue;
                }
            }
            UUID otherFrameMapUUID = CommonMapUUIDStore.getMapUUID(getItemFrameItem(otherFrame));
            if (!itemFrameMapUUID.equals(otherFrameMapUUID)) {
                continue;
            }
            neighbours.add(otherFramePos);
        }

        // Make sure the neighbours result are a single contiguous blob
        // Islands (can not reach the input item frame) are removed
        BlockFace[] sides = {
                BlockFace.UP, BlockFace.DOWN,
                FaceUtil.rotate(facing, 2),
                FaceUtil.rotate(facing, -2)
        };
        List<IntVector3> pendingList = new ArrayList<IntVector3>(5);
        List<IntVector3> result = new ArrayList<IntVector3>(neighbours.size());
        pendingList.add(itemFramePos);
        do {
            IntVector3 pending = pendingList.remove(pendingList.size() - 1);
            for (BlockFace side : sides) {
                IntVector3 sidePoint = pending.add(side);
                if (neighbours.remove(sidePoint)) {
                    pendingList.add(sidePoint);
                    result.add(sidePoint);
                }
            }
        } while (!pendingList.isEmpty());

        return result;
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
        EntityItemFrameHandle handle = EntityItemFrameHandle.fromBukkit(itemFrame);
        handle.setItem(item);
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
}
