package com.bergerkiller.bukkit.common.map.binding;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.internal.CommonMapUUIDStore;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.internal.CommonMapController;
import com.bergerkiller.bukkit.common.map.MapDisplayTile;
import com.bergerkiller.bukkit.common.map.util.MapLookPosition;
import com.bergerkiller.bukkit.common.map.util.MapUUID;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryStateHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.decoration.EntityItemFrameHandle;

/**
 * Maintains metadata information for a single item frame
 */
public class ItemFrameInfo {
    private final CommonMapController controller;
    public final ItemFrame itemFrame;
    public final EntityItemFrameHandle itemFrameHandle;
    public final DataWatcher.Item<?> itemFrame_dw_item;
    public final HashSet<Player> viewers;
    public MapUUID lastMapUUID; // last known Map UUID (UUID + tile information) of the map shown in this item frame
    public MapUUID preReloadMapUUID; // Map UUID known from before a reload, and if encountered again, will avoid resending the item (popping)
    public boolean removed; // item frame no longer exists on the server (chunk unloaded, or block removed)
    public boolean needsItemRefresh; // UUID was changed and item in the item frame needs refreshing
    public boolean sentToPlayers; // players have received item information for this item frame
    public MapDisplayInfo displayInfo;

    // These fields are used in updateItem() to speed up performance, due to how often it is called
    private Object lastFrameRawItem = null; // performance optimization to avoid conversion
    private ItemStack lastFrameItem = null; // performance optimization to simplify item change detection
    private ItemStack lastFrameItemUpdate = null; // to detect a change in item in updateItem()
    public boolean lastFrameItemUpdateNeeded = true; // tells the underlying system to refresh the item

    // These fields are used in the item frame update task to speedup lookup and avoid unneeded garbage
    private EntityTrackerEntryStateHandle entityTrackerEntryState = null; // Entity tracker entry state for resetting tick timer
    private Collection<Player> entityTrackerViewers = null; // Network synchronization entity tracker entry viewer set

    public ItemFrameInfo(CommonMapController controller, EntityItemFrameHandle itemFrame) {
        this.controller = controller;
        this.itemFrame = (ItemFrame) itemFrame.getBukkitEntity();
        this.itemFrameHandle = itemFrame;
        this.itemFrame_dw_item = this.itemFrameHandle.getDataWatcher().getItem(EntityItemFrameHandle.DATA_ITEM);
        this.viewers = new HashSet<Player>();
        this.removed = false;
        this.lastMapUUID = null;
        this.preReloadMapUUID = null;
        this.displayInfo = null;
        this.needsItemRefresh = false;
        this.sentToPlayers = false;
    }

    /**
     * Gets the World the item frame is in
     *
     * @return item frame world
     */
    public World getWorld() {
        return this.itemFrame.getWorld();
    }

    /**
     * Follows an eye ray to see if it lands on this item frame. If it does,
     * returns the exact coordinates on the display shown on this item frame.
     * If there is no map display being displayed, or the eye isn't looking at
     * this item frame, null is returned.<br>
     * <br>
     * Makes the method return null when not within bounds, and the
     * <i>withinBounds</i> parameter is true.
     *
     * @param startPosition Start position of the eye ray
     * @param lookDirection Normalized direction vector of the eye ray
     * @param withinBounds Whether the position looked at must be within bounds of the item frame
     * @return Map Look Position, or null if the eye ray doesn't land on this
     *         item frame, or this item frame isn't displaying a map display,
     *         or withinBounds is true and it is not exactly within item frame bounds.
     */
    public MapLookPosition findLookPosition(Vector startPosition, Vector lookDirection, boolean withinBounds) {
        MapLookPosition result = findLookPosition(startPosition, lookDirection);
        return result != null && (!withinBounds || result.isWithinBounds()) ? result : null;
    }

    /**
     * Follows an eye ray to see if it lands on this item frame. If it does,
     * returns the exact coordinates on the display shown on this item frame.
     * If there is no map display being displayed, or the eye isn't looking at
     * this item frame, null is returned.<br>
     * <br>
     * Use {@link MapLookPosition#isWithinBounds()} to check whether the player is
     * looking within bounds of this item frame, or not.
     *
     * @param startPosition Start position of the eye ray
     * @param lookDirection Normalized direction vector of the eye ray
     * @return Map Look Position, or null if the eye ray doesn't land on this
     *         item frame, or this item frame isn't displaying a map display.
     */
    public MapLookPosition findLookPosition(Vector startPosition, Vector lookDirection) {
        // If it shows no display, don't bother checking
        if (this.lastMapUUID == null) {
            return null;
        }

        // Offset from block face to canvas
        final double FRAME_OFFSET = 0.0625;

        // Compare facing with the eye ray to calculate the eye distance to the item frame
        final double distance;
        boolean withinBounds = true;
        IntVector3 frameBlock = this.itemFrameHandle.getBlockPosition();
        BlockFace facing = this.itemFrameHandle.getFacing();
        switch (facing) {
        case NORTH:
            if (lookDirection.getZ() > 1e-10) {
                distance = (frameBlock.z + 1.0 - FRAME_OFFSET - startPosition.getZ()) / lookDirection.getZ();
            } else {
                withinBounds = false;
                distance = MathUtil.distance(frameBlock.x, frameBlock.y, frameBlock.z,
                        startPosition.getX(), startPosition.getY(), startPosition.getZ());
            }
            break;
        case SOUTH:
            if (lookDirection.getZ() < -1e-10) {
                distance = (frameBlock.z + FRAME_OFFSET - startPosition.getZ()) / lookDirection.getZ();
            } else {
                withinBounds = false;
                distance = MathUtil.distance(frameBlock.x, frameBlock.y, frameBlock.z,
                        startPosition.getX(), startPosition.getY(), startPosition.getZ());
            }
            break;
        case WEST:
            if (lookDirection.getX() > 1e-10) {
                distance = (frameBlock.x + 1.0 - FRAME_OFFSET - startPosition.getX()) / lookDirection.getX();
            } else {
                withinBounds = false;
                distance = MathUtil.distance(frameBlock.x, frameBlock.y, frameBlock.z,
                        startPosition.getX(), startPosition.getY(), startPosition.getZ());
            }
            break;
        case EAST:
            if (lookDirection.getX() < -1e-10) {
                distance = (frameBlock.x + FRAME_OFFSET - startPosition.getX()) / lookDirection.getX();
            } else {
                withinBounds = false;
                distance = MathUtil.distance(frameBlock.x, frameBlock.y, frameBlock.z,
                        startPosition.getX(), startPosition.getY(), startPosition.getZ());
            }
            break;
        case DOWN:
            if (lookDirection.getY() > 1e-10) {
                distance = (frameBlock.y + 1.0 - FRAME_OFFSET - startPosition.getY()) / lookDirection.getY();
            } else {
                withinBounds = false;
                distance = MathUtil.distance(frameBlock.x, frameBlock.y, frameBlock.z,
                        startPosition.getX(), startPosition.getY(), startPosition.getZ());
            }
            break;
        case UP:
            if (lookDirection.getY() < -1e-10) {
                distance = (frameBlock.y + FRAME_OFFSET - startPosition.getY()) / lookDirection.getY();
            } else {
                withinBounds = false;
                distance = MathUtil.distance(frameBlock.x, frameBlock.y, frameBlock.z,
                        startPosition.getX(), startPosition.getY(), startPosition.getZ());
            }
            break;
        default:
            throw new IllegalArgumentException("Invalid facing: " + facing);
        }

        // Add distance * lookDirection to startPosition and subtract item frame coordinates
        // to find the coordinates relative to the middle of the block that are looked at
        final double at_x = distance * lookDirection.getX() + startPosition.getX() - frameBlock.x - 0.5;
        final double at_y = distance * lookDirection.getY() + startPosition.getY() - frameBlock.y - 0.5;
        final double at_z = distance * lookDirection.getZ() + startPosition.getZ() - frameBlock.z - 0.5;

        // If outside range [-0.5 .. 0.5] then this item frame was not looked at
        if (withinBounds && (at_x < -0.5 || at_x > 0.5 || at_z < -0.5 || at_z > 0.5 || at_y < -0.5 || at_y > 0.5)) {
            withinBounds = false;
        }

        // Convert x/y/z into x/y using facing information
        double map_x, map_y;
        switch (facing) {
        case NORTH:
            map_x = 0.5 - at_x;
            map_y = 0.5 - at_y;
            break;
        case SOUTH:
            map_x = 0.5 + at_x;
            map_y = 0.5 - at_y;
            break;
        case WEST:
            map_x = 0.5 + at_z;
            map_y = 0.5 - at_y;
            break;
        case EAST:
            map_x = 0.5 - at_z;
            map_y = 0.5 - at_y;
            break;
        case DOWN:
            map_x = 0.5 + at_x;
            map_y = 0.5 - at_z;
            break;
        case UP:
            map_x = 0.5 + at_x;
            map_y = 0.5 + at_z;
            break;
        default:
            throw new IllegalArgumentException("Invalid facing: " + facing);
        }

        // Adjust the coordinates if a non-zero rotation is set
        switch (this.itemFrameHandle.getRotationOrdinal() & 0x3) {
        case 1:
        {
            double tmp = map_x;
            map_x = map_y;
            map_y = 1.0 - tmp;
            break;
        }
        case 2:
        {
            map_x = 1.0 - map_x;
            map_y = 1.0 - map_y;
            break;
        }
        case 3:
        {
            double tmp = map_x;
            map_x = 1.0 - map_y;
            map_y = tmp;
            break;
        }
        default:
            break;
        }

        // Change to pixel coordinates based on resolution and done!
        return new MapLookPosition(this,
                MapDisplayTile.RESOLUTION * (map_x + this.lastMapUUID.getTileX()),
                MapDisplayTile.RESOLUTION * (map_y + this.lastMapUUID.getTileY()),
                distance, withinBounds);
    }

    /**
     * When the item frame this info is for was removed from the world,
     * this method internally updates that information
     *
     * @return True if removed, False otherwise
     */
    public boolean handleRemoved() {
        if (this.removed) {
            this.remove();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Updates the item if possibly changed, then synchronizes the viewers that view this item frame.
     * For every viewer that is added or removed, the synchronizer callbacks are called.
     *
     * @param viewerSynchronizer Synchronizer to call callbacks of
     */
    public void updateItemAndViewers(LogicUtil.ItemSynchronizer<Player, Player> viewerSynchronizer) {
        // Refreshes cached information about this item frame's item
        if (lastFrameItemUpdateNeeded) {
            updateItem();
        }

        // Update list of players for item frames showing maps
        if (lastMapUUID != null) {
            if (entityTrackerEntryState == null) {
                EntityTrackerEntryHandle entry = WorldUtil.getTracker(itemFrame.getWorld()).getEntry(itemFrame);

                // Item Frame isn't tracked on the server, so no players can view it
                if (entry == null) {
                    this.removed = true;
                    return;
                }

                entityTrackerEntryState = entry.getState();
                entityTrackerViewers = entry.getViewers();
            }

            boolean changes = LogicUtil.synchronizeUnordered(viewers, entityTrackerViewers, viewerSynchronizer);

            if (changes && displayInfo != null) {
                displayInfo.hasFrameViewerChanges = true;
            }

            // Reset tick counter to 1 so that the normal WorldMap refreshing never occurs
            // Only do this for non-vanilla maps
            if (!lastMapUUID.isStaticUUID()) {
                entityTrackerEntryState.setTickCounter(1);
            }
        }
    }

    public void updateItem() {
        // Reset flag
        this.lastFrameItemUpdateNeeded = false;

        // Avoid expensive conversion and creation of CraftItemStack by detecting changes
        boolean raw_item_changed = false;
        Object raw_item = DataWatcher.Item.getRawValue(this.itemFrame_dw_item);
        raw_item = CommonNMS.unwrapDWROptional(raw_item); // May be needed
        if (this.lastFrameRawItem != raw_item) {
            this.lastFrameRawItem = raw_item;
            this.lastFrameItem = WrapperConversion.toItemStack(this.lastFrameRawItem);
            raw_item_changed = true;
        }

        // If the raw item has not changed, and the item is not a map, don't bother checking
        // The equality check for ItemStack is very slow, because of the deep NBT check that occurs
        // When the item in the item frame is not a map item, there is no use wasting time here
        if (!raw_item_changed && lastMapUUID == null && !CommonMapUUIDStore.isMap(this.lastFrameItem)) {
            return;
        }

        // Check item changed
        if (LogicUtil.bothNullOrEqual(this.lastFrameItemUpdate, this.lastFrameItem)) {
            return;
        }

        // Assign & clone so that changes can be detected
        this.lastFrameItemUpdate = this.lastFrameItem;
        if (this.lastFrameItemUpdate != null) {
            this.lastFrameItemUpdate = this.lastFrameItemUpdate.clone();
        }

        // Handle changes in map item shown in item frames
        UUID mapUUID = CommonMapUUIDStore.getMapUUID(this.lastFrameItemUpdate);
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

    void recalculateUUID() {
        UUID mapUUID = this.itemFrameHandle.getItemMapDisplayUUID();

        // Find out the tile information of this item frame
        // This is a slow and lengthy procedure; hopefully it does not happen too often
        // What we do is: we add all neighbours, then find the most top-left item frame
        // Subtracting coordinates will give us the tile x/y of this item frame
        IntVector3 itemFramePosition = itemFrameHandle.getBlockPosition();
        CommonMapController.ItemFrameCluster cluster = this.controller.findCluster(itemFrameHandle, itemFramePosition);
        MapUUID newMapUUID;

        if (cluster.hasMultipleTiles()) {
            int tileX = 0;
            int tileY = 0;
            if (cluster.facing.getModY() > 0) {
                // Vertical pointing up
                // We use rotation of the item frame to decide which side is up
                switch (cluster.rotation) {
                case 90:
                    tileX = (itemFramePosition.z - cluster.min_coord.z);
                    tileY = (cluster.max_coord.x - itemFramePosition.x);
                    break;
                case 180:
                    tileX = (cluster.max_coord.x - itemFramePosition.x);
                    tileY = (cluster.max_coord.z - itemFramePosition.z);
                    break;
                case 270:
                    tileX = (cluster.max_coord.z - itemFramePosition.z);
                    tileY = (itemFramePosition.x - cluster.min_coord.x);
                    break;
                default:
                    tileX = (itemFramePosition.x - cluster.min_coord.x);
                    tileY = (itemFramePosition.z - cluster.min_coord.z);
                    break;
                }
            } else if (cluster.facing.getModY() < 0) {
                // Vertical pointing down
                // We use rotation of the item frame to decide which side is up
                switch (cluster.rotation) {
                case 90:
                    tileX = (cluster.max_coord.z - itemFramePosition.z);
                    tileY = (cluster.max_coord.x - itemFramePosition.x);
                    break;
                case 180:
                    tileX = (cluster.max_coord.x - itemFramePosition.x);
                    tileY = (itemFramePosition.z - cluster.min_coord.z);
                    break;
                case 270:
                    tileX = (itemFramePosition.z - cluster.min_coord.z);
                    tileY = (itemFramePosition.x - cluster.min_coord.x);
                    break;
                default:
                    tileX = (itemFramePosition.x - cluster.min_coord.x);
                    tileY = (cluster.max_coord.z - itemFramePosition.z);
                    break;
                }
            } else {
                // On the wall
                switch (cluster.facing) {
                case NORTH:
                    tileX = (cluster.max_coord.x - itemFramePosition.x);
                    break;
                case EAST:
                    tileX = (cluster.max_coord.z - itemFramePosition.z);
                    break;
                case SOUTH:
                    tileX = (itemFramePosition.x - cluster.min_coord.x);
                    break;
                case WEST:
                    tileX = (itemFramePosition.z - cluster.min_coord.z);
                    break;
                default:
                    tileX = 0;
                    break;
                }
                tileY = cluster.max_coord.y - itemFramePosition.y;
            }

            newMapUUID = new MapUUID(mapUUID, tileX, tileY);
        } else {
            newMapUUID = new MapUUID(mapUUID, 0, 0);
        }

        if (lastMapUUID == null || !lastMapUUID.getUUID().equals(mapUUID)) {
            // Map item UUID changed entirely. Remove the previous and add the new.
            remove();
            lastMapUUID = newMapUUID;
            needsItemRefresh = sentToPlayers && !newMapUUID.equals(preReloadMapUUID);
            preReloadMapUUID = null;
            add();
        } else if (newMapUUID.equals(lastMapUUID)) {
            // No change occurred
        } else if (this.displayInfo != null) {
            // Tile coordinates of this map were changed

            // Ensure the new tile coordinates are added
            this.displayInfo.addTileIfMissing(newMapUUID.getTileX(), newMapUUID.getTileY());

            // Refresh state now so that removeTileIfMissing works correctly
            int oldTileX = lastMapUUID.getTileX();
            int oldTileY = lastMapUUID.getTileY();
            lastMapUUID = newMapUUID;
            needsItemRefresh = sentToPlayers;
            preReloadMapUUID = null;

            // If the previous coordinates are now no longer used, remove the tile
            this.displayInfo.removeTileIfMissing(oldTileX, oldTileY);
        } else {
            // Tile coordinates changed, but we had no previous display info
            // Strange.
            lastMapUUID = newMapUUID;
            needsItemRefresh = sentToPlayers;
            preReloadMapUUID = null;
        }
    }

    private void remove() {
        if (displayInfo != null) {
            displayInfo.itemFrames.remove(this);
            displayInfo.hasFrameViewerChanges = true;
            displayInfo.refreshResolution();
            displayInfo.removeTileIfMissing(this.lastMapUUID.getTileX(), this.lastMapUUID.getTileY());
            if (!displayInfo.itemFrames.isEmpty()) {
                displayInfo.refreshItemFramesRequest = true;
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
    }

    private void add() {
        if (this.displayInfo == null && this.lastMapUUID != null) {
            this.displayInfo = this.controller.getInfo(this.lastMapUUID.getUUID());
            this.displayInfo.itemFrames.add(this);
            displayInfo.refreshItemFramesRequest = true;
            displayInfo.refreshResolution();
            displayInfo.addTileIfMissing(this.lastMapUUID.getTileX(), this.lastMapUUID.getTileY());
        }
    }
}