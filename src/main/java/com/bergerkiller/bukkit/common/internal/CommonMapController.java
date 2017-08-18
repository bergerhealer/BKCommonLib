package com.bergerkiller.bukkit.common.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
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
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.events.EntityAddEvent;
import com.bergerkiller.bukkit.common.events.EntityRemoveEvent;
import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.events.map.MapAction;
import com.bergerkiller.bukkit.common.events.map.MapClickEvent;
import com.bergerkiller.bukkit.common.events.map.MapShowEvent;
import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.map.MapSession;
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
import com.bergerkiller.bukkit.common.wrappers.HumanHand;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap;
import com.bergerkiller.generated.net.minecraft.server.EntityTrackerEntryHandle;

public class CommonMapController implements PacketListener, Listener {
    private final IntHashMap<UUID> mapUUIDById = new IntHashMap<UUID>();
    private final HashMap<UUID, Short> mapIdByUUID = new HashMap<UUID, Short>();
    private final HashMap<UUID, MapDisplayInfo> maps = new HashMap<UUID, MapDisplayInfo>();
    private final HashMap<Player, MapPlayerInput> playerInputs = new HashMap<Player, MapPlayerInput>();
    private final HashMap<ItemFrame, ItemFrameInfo> itemFrames = new HashMap<ItemFrame, ItemFrameInfo>();

    /**
     * These packet types are listened to handle the virtualized Map Display API
     */
    public static final PacketType[] PACKET_TYPES = {
            PacketType.OUT_MAP, PacketType.IN_STEER_VEHICLE, 
            PacketType.OUT_WINDOW_ITEMS, PacketType.OUT_WINDOW_SET_SLOT
    };

    /**
     * Gets all maps available on the server that may store map displays
     * 
     * @return collection of map display info
     */
    public Collection<MapDisplayInfo> getMaps() {
        return this.maps.values();
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
        ItemFrameInfo frameInfo = itemFrames.get(itemFrame);
        if (frameInfo != null) {
            if (frameInfo.lastMapUUID == null) {
                return null;
            }
            MapDisplayInfo info = maps.get(frameInfo.lastMapUUID);
            if (info == null) {
                info = new MapDisplayInfo(frameInfo.lastMapUUID);
                maps.put(frameInfo.lastMapUUID, info);
            }
            return info;
        }
        return getInfo(itemFrame.getItem());
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
        MapDisplayInfo info = maps.get(mapUUID);
        if (info == null) {
            info = new MapDisplayInfo(mapUUID);
            maps.put(mapUUID, info);
        }
        return info;
    }

    /**
     * Starts all continuous background update tasks for maps
     * 
     * @param plugin
     * @param startedTasks
     */
    public void startTasks(JavaPlugin plugin, List<Task> startedTasks) {
        startedTasks.add(new HeldMapUpdater(plugin).start(1, 1));
        startedTasks.add(new FramedMapUpdater(plugin).start(1, 1));
    }

    /**
     * Adjusts the internal remapping from UUID to Map Id taking into account the new item
     * being synchronized to the player. If the item is that of a virtual map, the map Id
     * of the item is updated.
     * 
     * @param item
     * @return True if the item was changed and needs to be updated in the packet
     */
    public ItemStack handleItemSync(ItemStack item) {
        if (item == null || item.getType() != Material.MAP) {
            return null;
        }

        // When a map UUID is specified, use that to dynamically allocate a map Id to use
        CommonTagCompound tag = ItemUtil.getMetaTag(item, false);
        if (tag != null) {
            UUID mapUUID = tag.getUUID("mapDisplay");
            if (mapUUID != null) {
                short id = getMapId(mapUUID);
                if (item.getDurability() != id) {
                    item = ItemUtil.cloneItem(item);
                    item.setDurability(id);
                    return item; // Id changed
                } else {
                    return null; // no change in Id
                }
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
    public synchronized short getMapId(UUID mapUUID) {
        // Obtain from cache
        Short storedMapId = mapIdByUUID.get(mapUUID);
        if (storedMapId != null) {
            return storedMapId.shortValue();
        }

        // If the UUID is that of a static UUID, we must make sure to store it as such
        // We may have to remap the old Map Id to free up the Id slot we need
        short mapId = CommonMapUUIDStore.getStaticMapId(mapUUID);
        if (mapId != -1) {
            storeStaticMapId(mapId);
            return mapId;
        }

        // Store a new map
        return storeDynamicMapId(mapUUID);
    }

    /**
     * Forces a particular map Id to stay static (unchanging) and stores it
     * as such in the mappings.
     * 
     * @param mapId
     */
    private synchronized void storeStaticMapId(short mapId) {
        if (storeDynamicMapId(mapUUIDById.get(mapId)) != mapId) {
            UUID mapUUID = CommonMapUUIDStore.getStaticMapUUID(mapId);
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
    private synchronized short storeDynamicMapId(UUID mapUUID) {
        // Null safety check
        if (mapUUID == null) {
            return -1;
        }

        // If the UUID is static, do not store anything and return the static Id instead
        short staticMapid = CommonMapUUIDStore.getStaticMapId(mapUUID);
        if (staticMapid != -1) {
            return staticMapid;
        }

        // Figure out a free Map Id we can use
        for (int i = 0; i < Short.MAX_VALUE; i++) {
            if (!mapUUIDById.contains(i)) {
                // Store in mapping
                mapUUIDById.put(i, mapUUID);
                mapIdByUUID.put(mapUUID, Short.valueOf((short) i));

                // Invalidate display if it exists
                MapDisplayInfo mapInfo = maps.get(mapUUID);
                if (mapInfo != null) {
                    for (MapSession session : mapInfo.sessions) {
                        session.display.invalidate();
                    }
                }
                return (short) i;
            }
        }
        return -1;
    }

    @Override
    public synchronized void onPacketSend(PacketSendEvent event) {
        // Check if any virtual single maps are attached to this map
        if (event.getType() == PacketType.OUT_MAP) {    
            int itemid = event.getPacket().read(PacketType.OUT_MAP.itemId);
            UUID mapUUID = mapUUIDById.get(itemid);
            if (mapUUID == null) {
                this.storeStaticMapId((short) itemid);
            } else if (CommonMapUUIDStore.getStaticMapId(mapUUID) == -1) {
                event.setCancelled(true);
            }
        }

        // Correct Map ItemStacks as they are sent to the clients (virtual)
        if (event.getType() == PacketType.OUT_WINDOW_ITEMS) {
            List<ItemStack> items = event.getPacket().read(PacketType.OUT_WINDOW_ITEMS.items);
            ListIterator<ItemStack> iter = items.listIterator();
            while (iter.hasNext()) {
                ItemStack newItem = this.handleItemSync(iter.next());
                if (newItem != null) {
                    iter.set(newItem);
                }
            }
        }
        if (event.getType() == PacketType.OUT_WINDOW_SET_SLOT) {
            ItemStack oldItem = event.getPacket().read(PacketType.OUT_WINDOW_SET_SLOT.item);
            ItemStack newItem = this.handleItemSync(oldItem);
            if (newItem != null) {
                event.getPacket().write(PacketType.OUT_WINDOW_SET_SLOT.item, newItem);
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
            itemFrames.put(frame, new ItemFrameInfo(frame));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    protected synchronized void onEntityRemoved(EntityRemoveEvent event) {
        if (event.getEntityType() == EntityType.ITEM_FRAME) {
            ItemFrame frame = (ItemFrame) event.getEntity();
            ItemFrameInfo info = itemFrames.get(frame);
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
        public UUID lastMapUUID;
        public boolean removed;
        public MapDisplayInfo displayInfo;

        public ItemFrameInfo(ItemFrame itemFrame) {
            this.itemFrame = itemFrame;
            this.viewers = new ArrayList<Player>();
            this.removed = false;
            this.lastMapUUID = null;
            this.displayInfo = null;
        }

        public void remove() {
            if (displayInfo != null) {
                displayInfo.itemFrames.remove(this);
                displayInfo.hasFrameViewerChanges = true;
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
        }

        public void add() {
            if (this.displayInfo == null && this.lastMapUUID != null) {
                this.displayInfo = getInfo(this.lastMapUUID);
                this.displayInfo.itemFrames.add(this);
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
            Iterator<Map.Entry<ItemFrame, ItemFrameInfo>> frame_it = itemFrames.entrySet().iterator();
            while (frame_it.hasNext()) {
                final ItemFrameInfo info = frame_it.next().getValue();
                if (info.removed) {
                    // Remove all players that have been set as viewers
                    info.remove();
                    frame_it.remove();
                    continue;
                }

                EntityTrackerEntryHandle trackerEntry = WorldUtil.getTracker(info.itemFrame.getWorld()).getEntry(info.itemFrame);
                if (trackerEntry == null) {
                    // Item Frame isn't tracked on the server, so no players can view it
                    info.remove();
                    continue;
                }

                // Handle changes in map item shown in item frames
                UUID mapUUID = CommonMapUUIDStore.getMapUUID(info.itemFrame.getItem());
                if (mapUUID != info.lastMapUUID) {
                    info.remove();
                    if (mapUUID != null) {
                        info.lastMapUUID = mapUUID;
                        info.add();
                    }
                }

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
                            CommonUtil.callEvent(new MapShowEvent(player, info.itemFrame));
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
                    CommonUtil.callEvent(new MapShowEvent(player, HumanHand.LEFT, currLeftHand));
                }
                if (isMap(currRightHand) 
                        && !mapEquals(currRightHand, lastRightHand) 
                        && !mapEquals(currRightHand, lastLeftHand)) {
                    // Right hand now has a map! We did not swap hands, either.
                    CommonUtil.callEvent(new MapShowEvent(player, HumanHand.RIGHT, currRightHand));
                }

                lastLeftHand = currLeftHand;
                lastRightHand = currRightHand;
            }

            private final boolean isMap(ItemStack item) {
                return item != null && item.getType() == Material.MAP;
            }

            private final boolean mapEquals(ItemStack item1, ItemStack item2) {
                return item1 != null && item2 != null && item1.getType() == item2.getType() &&
                        item1.getDurability() == item2.getDurability();
            }
        }
    }

}
