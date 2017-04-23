package com.bergerkiller.bukkit.common.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.events.EntityAddEvent;
import com.bergerkiller.bukkit.common.events.EntityRemoveEvent;
import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.events.map.MapShowEvent;
import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.map.MapSession;
import com.bergerkiller.bukkit.common.map.MapPlayerInput;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.PlayerUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap;
import com.bergerkiller.reflection.net.minecraft.server.NMSEntityTrackerEntry;

public class CommonMapController implements PacketListener, Listener {
    private final IntHashMap<MapDisplayInfo> maps = new IntHashMap<MapDisplayInfo>();
    private final HashMap<Player, MapPlayerInput> playerInputs = new HashMap<Player, MapPlayerInput>();
    private final HashMap<ItemFrame, ItemFrameInfo> itemFrames = new HashMap<ItemFrame, ItemFrameInfo>();

    /**
     * Gets a list of all maps available on the server that may store map displays
     * 
     * @return list of map display info
     */
    public List<MapDisplayInfo> getMaps() {
        return this.maps.values();
    }

    /**
     * Gets the Player Input controller for a certain player
     * 
     * @param player
     * @return player input
     */
    public MapPlayerInput getPlayerInput(Player player) {
        MapPlayerInput input = playerInputs.get(player);
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
    public MapDisplayInfo getInfo(ItemFrame itemFrame) {
        ItemFrameInfo frameInfo = itemFrames.get(itemFrame);
        if (frameInfo != null) {
            if (frameInfo.lastMapId == -1) {
                return null;
            }
            MapDisplayInfo info = maps.get(frameInfo.lastMapId);
            if (info == null) {
                info = new MapDisplayInfo(frameInfo.lastMapId);
                maps.put(frameInfo.lastMapId, info);
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
    public MapDisplayInfo getInfo(ItemStack mapItem) {
        int id = getMapId(mapItem);
        if (id == -1) {
            return null;
        } else {
            MapDisplayInfo info = maps.get(id);
            if (info == null) {
                info = new MapDisplayInfo(id);
                maps.put(id, info);
            }
            return info;
        }
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
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

    @Override
    public void onPacketSend(PacketSendEvent event) {
        // Check if any virtual single maps are attached to this map
        if (event.getType() == PacketType.OUT_MAP) {
            int itemid = event.getPacket().read(PacketType.OUT_MAP.itemId);
            MapDisplayInfo info = maps.get(itemid);
            if (info != null && !info.sessions.isEmpty()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    protected void onPlayerJoin(PlayerJoinEvent event) {
        // Let everyone know we got a player over here!
        Player player = event.getPlayer();
        for (MapDisplayInfo map : this.maps.values()) {
            for (MapSession session : map.sessions) {
                session.updatePlayerOnline(player);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    protected void onEntityAdded(EntityAddEvent event) {
        if (event.getEntityType() == EntityType.ITEM_FRAME) {
            ItemFrame frame = (ItemFrame) event.getEntity();
            itemFrames.put(frame, new ItemFrameInfo(frame));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    protected void onEntityRemoved(EntityRemoveEvent event) {
        if (event.getEntityType() == EntityType.ITEM_FRAME) {
            ItemFrame frame = (ItemFrame) event.getEntity();
            ItemFrameInfo info = itemFrames.get(frame);
            if (info != null) {
                info.removed = true;
            }
        }
    }

    /**
     * Maintains the metadata information for a map
     */
    public class MapDisplayInfo {
        public final int id; /* map id */

        // Maintains information about the item frames that show this map, and what players
        // can see this map on the item frames
        public final ArrayList<ItemFrameInfo> itemFrames = new ArrayList<ItemFrameInfo>();
        public final LinkedHashSet<Player> frameViewers = new LinkedHashSet<Player>();
        private boolean hasFrameViewerChanges = true;

        // A list of all active running displays bound to this map
        public final ArrayList<MapSession> sessions = new ArrayList<MapSession>();

        // Maps the display view stack by player
        public final HashMap<Player, ViewStack> views = new HashMap<Player, ViewStack>();

        public MapDisplayInfo(int id) {
            this.id = id;
        }

        /**
         * Gets whether a certain ItemStack contains this map
         * 
         * @param item to check
         * @return True if the item contains this map
         */
        public boolean isMap(ItemStack item) {
            return getMapId(item) == id;
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
        public int lastMapId;
        public boolean removed;
        public MapDisplayInfo displayInfo;

        public ItemFrameInfo(ItemFrame itemFrame) {
            this.itemFrame = itemFrame;
            this.viewers = new ArrayList<Player>();
            this.removed = false;
            this.lastMapId = -1;
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
            this.lastMapId = -1;
        }

        public void add() {
            if (this.displayInfo == null && lastMapId != -1) {
                this.displayInfo = maps.get(lastMapId);
                if (this.displayInfo == null) {
                    this.displayInfo = new MapDisplayInfo(lastMapId);
                    maps.put(lastMapId, this.displayInfo);
                }
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

                Object trackerEntry = WorldUtil.getTracker(info.itemFrame.getWorld()).getEntry(info.itemFrame);
                if (trackerEntry == null) {
                    // Item Frame isn't tracked on the server, so no players can view it
                    info.remove();
                    continue;
                }

                // Handle changes in map item shown in item frames
                int mapId = getMapId(info.itemFrame.getItem());
                if (mapId != info.lastMapId) {
                    info.remove();
                    if (mapId != -1) {
                        info.lastMapId = mapId;
                        info.add();
                    }
                }

                // Update list of players for item frames showing maps
                if (info.lastMapId != -1) {
                    Set<Player> liveViewers = NMSEntityTrackerEntry.viewers.get(trackerEntry);
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
                ItemStack currLeftHand = PlayerUtil.getItemInHand(this.player, MainHand.LEFT);
                ItemStack currRightHand = PlayerUtil.getItemInHand(this.player, MainHand.RIGHT);

                if (isMap(currLeftHand) 
                        && !mapEquals(currLeftHand, lastLeftHand) 
                        && !mapEquals(currLeftHand, lastRightHand)) {
                    // Left hand now has a map! We did not swap hands, either.
                    CommonUtil.callEvent(new MapShowEvent(player, MainHand.LEFT, currLeftHand));
                }
                if (isMap(currRightHand) 
                        && !mapEquals(currRightHand, lastRightHand) 
                        && !mapEquals(currRightHand, lastLeftHand)) {
                    // Right hand now has a map! We did not swap hands, either.
                    CommonUtil.callEvent(new MapShowEvent(player, MainHand.RIGHT, currRightHand));
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

    /**
     * Internal use only! Obtains the unique Id of a map item. Returns -1 when the item is not a valid map.
     * This function may be subject to change and should not be depended on.
     * 
     * @param item to get the Map Id for
     * @return map id
     */
    public static int getMapId(ItemStack item) {
        return (item == null || item.getType() != Material.MAP) ? -1 : item.getDurability(); 
    }
}
