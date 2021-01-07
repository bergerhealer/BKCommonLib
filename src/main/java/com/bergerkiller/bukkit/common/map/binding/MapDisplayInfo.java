package com.bergerkiller.bukkit.common.map.binding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.internal.CommonMapUUIDStore;
import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.map.MapDisplayTile;
import com.bergerkiller.bukkit.common.map.MapSession;
import com.bergerkiller.bukkit.common.map.util.MapUUID;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.wrappers.LongHashSet;

/**
 * Metadata information about map displays with a certain
 * unique ID. Information about the item frames, viewers,
 * map display sessions and resolution is available here.
 */
public class MapDisplayInfo {
    private final UUID uuid; /* map UUID */

    // Maintains information about the item frames that show this map, and what players
    // can see this map on the item frames
    final ArrayList<ItemFrameInfo> itemFrames = new ArrayList<ItemFrameInfo>();
    private final LinkedHashSet<Player> frameViewers = new LinkedHashSet<Player>();
    boolean hasFrameViewerChanges = true;
    boolean refreshItemFramesRequest = false;
    private int desiredWidth, desiredHeight;

    // A list of all active running displays bound to this map
    private final ArrayList<MapSession> sessions = new ArrayList<MapSession>();

    // Maps the display view stack by player
    private final HashMap<UUID, ViewStack> views = new HashMap<UUID, ViewStack>();

    public MapDisplayInfo(UUID uuid) {
        this.uuid = uuid;
        this.desiredWidth = 128;
        this.desiredHeight = 128;
    }

    /**
     * Gets the unique id of this map display instance
     *
     * @return unique id
     */
    public UUID getUniqueId() {
        return this.uuid;
    }

    /**
     * Gets the viewers that are currently viewing this display
     *
     * @return viewers
     */
    public Collection<Player> getViewers() {
        return this.frameViewers;
    }

    /**
     * Gets a list of the metadata of the item frames displaying this
     * display.
     *
     * @return item frames
     */
    public List<ItemFrameInfo> getItemFrames() {
        return this.itemFrames;
    }

    /**
     * Gets a list of all running map display sessions for maps
     * with this UUID.
     *
     * @return list of map display sessions
     */
    public List<MapSession> getSessions() {
        return this.sessions;
    }

    /**
     * Gets the desired width of the map displays for this map
     * 
     * @return desired width
     */
    public int getDesiredWidth() {
        return this.desiredWidth;
    }

    /**
     * Gets the desired height of the map displays for this map
     * 
     * @return desired height
     */
    public int getDesiredHeight() {
        return this.desiredHeight;
    }

    /**
     * Removes a tile from already running displays, so that players no longer
     * receive update packets for them, if that tile is no longer represented on
     * an item frame.
     * Does nothing if the display session is going to be reset, or no display sessions exist.
     * Tile 0,0 will never be removed as it can be held.
     * 
     * @param tileX
     * @param tileY
     */
    public void removeTileIfMissing(int tileX, int tileY) {
        if (this.sessions.isEmpty() || (tileX == 0 && tileY == 0)) {
            return;
        }

        // Check not contained on some item frame
        for (ItemFrameInfo frame : this.itemFrames) {
            if (frame.lastMapUUID != null && frame.lastMapUUID.getTileX() == tileX && frame.lastMapUUID.getTileY() == tileY) {
                return;
            }
        }

        // Remove from all sessions
        for (MapSession session : this.sessions) {
            if (session.refreshResolutionRequested) {
                continue;
            }
            Iterator<MapDisplayTile> iter = session.tiles.iterator();
            while (iter.hasNext()) {
                MapDisplayTile tile = iter.next();
                if (tile.tileX == tileX && tile.tileY == tileY) {
                    iter.remove();
                    break;
                }
            }
        }
    }

    /**
     * Adds a new display tile to already running displays.
     * Does nothing if the display is going to be reset, or no display sessions exist.
     * Since tile 0,0 is always added by the display, that tile is ignored.
     * 
     * @param tileX
     * @param tileY
     */
    public void addTileIfMissing(int tileX, int tileY) {
        if (this.sessions.isEmpty() || (tileX == 0 && tileY == 0)) {
            return;
        }
        if ((tileX << 7) >= this.desiredWidth) {
            return;
        }
        if ((tileY << 7) >= this.desiredHeight) {
            return;
        }

        for (MapSession session : this.sessions) {
            if (session.refreshResolutionRequested) {
                continue;
            }
            if (session.display.containsTile(tileX, tileY)) {
                continue;
            }

            MapDisplayTile newTile = new MapDisplayTile(this.uuid, tileX, tileY);
            session.tiles.add(newTile);

            for (MapSession.Owner owner : session.onlineOwners) {
                owner.sendDirtyTile(newTile);
            }
        }
    }

    /**
     * Updates the viewers viewing this map display, if there were
     * pending viewer changes. Then, if resolution changed,
     * refreshes the resolution of the display.
     */
    public void updateViewersAndResolution() {
        if (hasFrameViewerChanges) {
            hasFrameViewerChanges = false;

            // Recalculate the list of players that can see an item frame
            frameViewers.clear();
            for (ItemFrameInfo itemFrame : itemFrames) {
                frameViewers.addAll(itemFrame.viewers);
            }

            // Synchronize this list with the 'global' viewer
            //if (map.globalFramedDisplay != null) {
                //map.globalFramedDisplay.setViewers(map.frameViewers);
            //}
        }

        // Refresh all item frames' items showing this map
        // It is possible their UUID changed as a result of the new tiling
        if (refreshItemFramesRequest) {
            for (int i = itemFrames.size()-1; i >= 0; i--) {
                itemFrames.get(i).recalculateUUID();
            }
            refreshResolution();
            refreshItemFramesRequest = false;
        }
    }

    /**
     * Refreshes the desired width and height of the map displays based on the
     * item frames that are currently loaded.
     */
    public void refreshResolution() {
        int min_x = 0;
        int min_y = 0;
        int max_x = 0;
        int max_y = 0;
        boolean first = true;
        for (ItemFrameInfo itemFrame : itemFrames) {
            if (itemFrame.lastMapUUID != null) {
                int tx = itemFrame.lastMapUUID.getTileX() << 7;
                int ty = itemFrame.lastMapUUID.getTileY() << 7;
                if (first) {
                    first = false;
                    min_x = max_x = tx;
                    min_y = max_y = ty;
                    continue;
                }
                if (tx < min_x) min_x = tx;
                if (tx > max_x) max_x = tx;
                if (ty < min_y) min_y = ty;
                if (ty > max_y) max_y = ty;
            }
        }
        int new_width = max_x - min_x + 128;
        int new_height = max_y - min_y + 128;
        if (new_width != this.desiredWidth || new_height != this.desiredHeight) {
            this.desiredWidth = new_width;
            this.desiredHeight = new_height;
            this.refreshItemFramesRequest = true;
            for (MapSession session : this.sessions) {
                if (session.refreshResolutionRequested) {
                    continue;
                }
                if (session.display.getWidth() != this.desiredWidth ||
                    session.display.getHeight() != this.desiredHeight)
                {
                    session.refreshResolutionRequested = true;
                }
            }
        }
    }

    /**
     * Loads the tiles in a Map Display. This also removes tiles in the display
     * that don't actually exist.
     * 
     * @param session The session of the map display
     * @param initialize Whether the tiles are initialized, and contents are not yet drawn
     */
    public void loadTiles(MapSession session, boolean initialize) {
        // Collect all tile x/y coordinates into a long hashset
        LongHashSet tile_coords = new LongHashSet();
        for (ItemFrameInfo itemFrame : this.itemFrames) {
            MapUUID uuid = itemFrame.lastMapUUID;
            if (uuid != null) {
                tile_coords.add(uuid.getTileX(), uuid.getTileY());
            }
        }
        tile_coords.add(0, 0);

        if (initialize) {
            // Wipe previous tiles when initializing
            session.tiles.clear();
        } else {
            // Remove tiles from the display that are no longer present
            // Remove existing tiles from the set at the same time
            // We are left with a set containing tiles that must be added
            Iterator<MapDisplayTile> iter = session.tiles.iterator();
            while (iter.hasNext()) {
                MapDisplayTile tile = iter.next();
                if (!tile_coords.remove(tile.tileX, tile.tileY)) {
                    iter.remove();
                }
            }
        }

        // Add all remaining tiles to the display
        LongHashSet.LongIterator iter = tile_coords.longIterator();
        while (iter.hasNext()) {
            long coord = iter.next();
            MapDisplayTile newTile = new MapDisplayTile(this.uuid,
                    MathUtil.longHashMsw(coord), MathUtil.longHashLsw(coord));
            session.tiles.add(newTile);

            // Send map packets for the added tile
            if (!initialize) {
                for (MapSession.Owner owner : session.onlineOwners) {
                    owner.sendDirtyTile(newTile);
                }
            }
        }
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
     * Gets the stack of Map Displays used to display information to a certain player.
     * Creates a new instance if none exists yet.
     * 
     * @param player
     * @return view stack
     */
    public ViewStack getOrCreateViewStack(Player player) {
        UUID playerUUID = player.getUniqueId();
        ViewStack stack = views.get(playerUUID);
        if (stack == null) {
            stack = new ViewStack();
            views.put(playerUUID, stack);
        }
        return stack;
    }

    /**
     * Gets the stack of Map Displays used to display information to a certain player,
     * by player UUID, if one exists. If not, returns null.
     * 
     * @param playerUUID Unique ID of the Player
     * @return view stack if found, otherwise null
     */
    public ViewStack getViewStackByPlayerUUID(UUID playerUUID) {
        return views.get(playerUUID);
    }

    /**
     * Sets whether a certain player is viewing a certain Map Display.
     * 
     * @param player
     * @param display
     * @param viewing
     */
    public void setViewing(Player player, MapDisplay display, boolean viewing) {
        UUID playerUUID = player.getUniqueId();
        ViewStack stack = views.get(playerUUID);
        if (viewing) {
            if (stack == null) {
                stack = new ViewStack();
                stack.stack.add(display);
                views.put(playerUUID, stack);
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
        ViewStack stack = views.get(player.getUniqueId());
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

    /**
     * Adds a new map session, internal use only
     *
     * @param session
     */
    public void addSession(MapSession session) {
        this.sessions.add(session);
    }

    /**
     * Removes a previously added map session, internal use only
     *
     * @param session
     */
    public void removeSession(MapSession session) {
        this.sessions.remove(session);
    }

    /**
     * Maintains a list of past Map Displays that will be shown in order as sessions end.
     * For example, if a map display for showing on an Item Frame was set, and a new one was set
     * for when the player holds the map, it will 'take over' from the item frame version. Once the
     * player stops viewing the map again, and that session ends, it will automatically fall back to
     * showing the item frame version.
     */
    public static class ViewStack {
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
}
