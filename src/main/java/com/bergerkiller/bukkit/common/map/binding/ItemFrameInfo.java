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
import com.bergerkiller.bukkit.common.collections.FastTrackedUpdateSet;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.internal.map.CommonMapController;
import com.bergerkiller.bukkit.common.internal.map.CommonMapUUIDStore;
import com.bergerkiller.bukkit.common.internal.map.ItemFrameCluster;
import com.bergerkiller.bukkit.common.map.MapDisplayTile;
import com.bergerkiller.bukkit.common.map.util.MapLookPosition;
import com.bergerkiller.bukkit.common.map.util.MapUUID;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryStateHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.decoration.EntityItemFrameHandle;

/**
 * Maintains metadata information for a single item frame
 */
public class ItemFrameInfo {
    private final CommonMapController controller;
    public final ItemFrame itemFrame;
    public final EntityItemFrameHandle itemFrameHandle;
    public final DataWatcher.Item<?> itemFrame_dw_item;
    public final IntVector3 coordinates;
    public final HashSet<Player> viewers;
    public MapUUID lastMapUUID; // last known Map UUID (UUID + tile information) of the map shown in this item frame
    public MapUUID preReloadMapUUID; // Map UUID known from before a reload, and if encountered again, will avoid resending the item (popping)
    public boolean removed; // item frame no longer exists on the server (chunk unloaded, or block removed)
    public boolean sentMapInfoToPlayers; // players have received map item information for this item frame
    public boolean requiresFurtherLoading; // whether neighbouring chunks need loading before a map display can be initialized
    public MapDisplayInfo displayInfo;

    // These fields are used in updateItem() to speed up performance, due to how often it is called
    private Object lastFrameRawItem = null; // performance optimization to avoid conversion
    private ItemStack lastFrameItem = null; // performance optimization to simplify item change detection
    private ItemStack lastFrameItemUpdate = null; // to detect a change in item in updateItem()
    public boolean lastFrameItemUpdateNeeded = true; // tells the underlying system to refresh the item

    // These fields are used in the item frame update task to speedup lookup and avoid unneeded garbage
    private EntityTrackerEntryStateHandle entityTrackerEntryState = null; // Entity tracker entry state for resetting tick timer
    private Collection<Player> entityTrackerViewers = null; // Network synchronization entity tracker entry viewer set

    // Tracks whether an item refresh needs to be done
    public final FastTrackedUpdateSet.Tracker<ItemFrameInfo> needsItemRefresh;
    // Tracker object for updating this item frame
    public final UpdateEntry updateEntry = new UpdateEntry(this);

    public ItemFrameInfo(CommonMapController controller, EntityItemFrameHandle itemFrame) {
        this.controller = controller;
        this.itemFrame = (ItemFrame) itemFrame.getBukkitEntity();
        this.itemFrameHandle = itemFrame;
        this.itemFrame_dw_item = this.itemFrameHandle.getDataWatcher().getItem(EntityItemFrameHandle.DATA_ITEM);
        this.coordinates = this.itemFrameHandle.getBlockPosition();
        this.viewers = new HashSet<Player>();
        this.removed = false;
        this.lastMapUUID = null;
        this.preReloadMapUUID = null;
        this.displayInfo = null;
        this.needsItemRefresh = controller.itemFramesThatNeedItemRefresh.track(this);
        this.sentMapInfoToPlayers = false;
        this.requiresFurtherLoading = false;
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

        // Check whether the item frame is invisible. If so, a different offset is used.
        boolean invisible = this.itemFrameHandle.getDataWatcher().getFlag(
                EntityHandle.DATA_FLAGS, EntityHandle.DATA_FLAG_INVISIBLE);

        // Offset from block face to canvas
        double FRAME_OFFSET = invisible ? 0.00625 : 0.0625;

        // Compare facing with the eye ray to calculate the eye distance to the item frame
        final double distance;
        boolean withinBounds = true;
        IntVector3 frameBlock = this.coordinates;
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
        double edgeDistance = Double.MAX_VALUE;
        if (withinBounds) {
            // Get distance from the edge of each coordinate space
            final Vector edge = new Vector(Math.max(0.0, Math.abs(at_x) - 0.5),
                                           Math.max(0.0, Math.abs(at_y) - 0.5),
                                           Math.max(0.0, Math.abs(at_z) - 0.5));
            edgeDistance = edge.length();
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
                distance, edgeDistance);
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
     * Checks if the item in the ItemFrame changed, and handles this change if so.
     */
    public void updateItem() {
        // Recalculate UUID if the map changed/item became a map
        if (checkItemChanged()) {
            recalculateUUID();
        }
    }

    /**
     * Synchronizes the viewers that view this item frame.
     * For every viewer that is added or removed, the synchronizer callbacks are called.
     *
     * @param viewerSynchronizer Synchronizer to call callbacks of
     */
    public void updateViewers(LogicUtil.ItemSynchronizer<Player, Player> viewerSynchronizer) {
        // Update list of players for item frames showing maps
        if (lastMapUUID != null) {
            if (entityTrackerEntryState == null) {
                EntityTrackerEntryHandle entry = WorldUtil.getTracker(itemFrame.getWorld()).getEntry(itemFrame);

                // Item Frame isn't tracked on the server, so no players can view it
                if (entry == null) {
                    this.removed = true;
                    this.needsItemRefresh.set(false);
                    return;
                }

                entityTrackerEntryState = entry.getState();
                entityTrackerViewers = entry.getViewers();
            }

            boolean changes = LogicUtil.synchronizeUnordered(viewers, entityTrackerViewers, viewerSynchronizer);

            if (changes && displayInfo != null) {
                displayInfo.hasFrameViewerChanges.set(true);
            }

            // Reset tick counter to 1 so that the normal WorldMap refreshing never occurs
            // Only do this for non-vanilla maps
            if (!lastMapUUID.isStaticUUID()) {
                entityTrackerEntryState.setTickCounter(1);
            }
        }
    }

    private boolean checkItemChanged() {
        // Avoid expensive conversion and creation of CraftItemStack by detecting changes
        boolean raw_item_changed = false;
        Object raw_item = DataWatcher.Item.getRawValue(this.itemFrame_dw_item);
        raw_item = CommonNMS.unwrapDWROptional(raw_item); // May be needed
        if (this.lastFrameRawItem != raw_item || this.lastFrameItemUpdateNeeded) {
            this.lastFrameRawItem = raw_item;
            this.lastFrameItem = WrapperConversion.toItemStack(this.lastFrameRawItem);
            raw_item_changed = true;
        }

        // Reset flag
        this.lastFrameItemUpdateNeeded = false;

        // If the raw item has not changed, and the item is not a map, don't bother checking
        // The equality check for ItemStack is very slow, because of the deep NBT check that occurs
        // When the item in the item frame is not a map item, there is no use wasting time here
        // Always passes the first time
        if (!raw_item_changed || LogicUtil.bothNullOrEqual(this.lastFrameItemUpdate, this.lastFrameItem)) {
            return false;
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
            this.sentMapInfoToPlayers = false;
            this.requiresFurtherLoading = false;
            if (lastMapUUID != null) {
                remove();
            }
        } else if (lastMapUUID == null || !lastMapUUID.getUUID().equals(mapUUID)) {
            // Map UUID was changed, or neighbours need to be re-calculated
            return true;
        }

        // No map changes
        return false;
    }

    public void onChunkDependencyLoaded() {
        if (this.requiresFurtherLoading) {
            this.recalculateUUID();
        }
    }

    void recalculateUUID() {
        // Find out the tile information of this item frame
        // This is a slow and lengthy procedure; hopefully it does not happen too often
        // What we do is: we add all neighbours, then find the most top-left item frame
        // Subtracting coordinates will give us the tile x/y of this item frame
        IntVector3 itemFramePosition = this.coordinates;
        ItemFrameCluster cluster = this.controller.findCluster(itemFrameHandle, itemFramePosition);

        // If not fully loaded, 'park' this item frame until surrounding chunks are loaded too
        World world = this.getWorld();
        boolean fullyLoaded = true;
        for (ItemFrameCluster.ChunkDependency dependency : cluster.chunk_dependencies) {
            fullyLoaded &= this.controller.checkClusterChunkDependency(world, dependency);
        }
        if (!fullyLoaded) {
            this.requiresFurtherLoading = true;
            return;
        }

        // Calculate UUID of this item frame
        this.recalculateUUIDInCluster(cluster);

        // Cluster is fully loaded, check if there are any other item frames part of
        // this cluster that were waiting to be further loaded. If so, load them in!
        // This (slow) operation is only needed once when a previously partially loaded
        // display is fully initialized. This won't run when displays are fully inside
        // loaded chunks.
        // By doing this here we make sure the display resolution is correct instantly,
        // as otherwise there is a tick delay until these other item frames initialize.
        if (cluster.hasMultipleTiles()) {
            for (ItemFrameInfo itemFrame : this.controller.findClusterItemFrames(cluster)) {
                if (itemFrame == this) {
                    continue;
                }
                if ((itemFrame.lastFrameItemUpdateNeeded && itemFrame.checkItemChanged()) || itemFrame.requiresFurtherLoading) {
                    itemFrame.recalculateUUIDInCluster(cluster);
                }
            }
        }
    }

    private void recalculateUUIDInCluster(ItemFrameCluster cluster) {
        this.requiresFurtherLoading = false;

        IntVector3 itemFramePosition = this.coordinates;
        UUID mapUUID = this.itemFrameHandle.getItemMapDisplayUUID();
        if (mapUUID == null) {
            return;
        }

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
            needsItemRefresh.set(sentMapInfoToPlayers && !newMapUUID.equals(preReloadMapUUID));
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
            needsItemRefresh.set(sentMapInfoToPlayers);
            preReloadMapUUID = null;

            // If the previous coordinates are now no longer used, remove the tile
            this.displayInfo.removeTileIfMissing(oldTileX, oldTileY);
        } else {
            // Tile coordinates changed, but we had no previous display info
            // Strange.
            lastMapUUID = newMapUUID;
            needsItemRefresh.set(sentMapInfoToPlayers);
            preReloadMapUUID = null;
        }
    }

    private void remove() {
        if (displayInfo != null) {
            displayInfo.itemFrames.remove(this);
            displayInfo.hasFrameViewerChanges.set(true);
            displayInfo.refreshResolution();
            displayInfo.removeTileIfMissing(this.lastMapUUID.getTileX(), this.lastMapUUID.getTileY());
            if (!displayInfo.itemFrames.isEmpty()) {
                displayInfo.hasFrameResolutionChanges.set(true);
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
        this.requiresFurtherLoading = false;
        this.lastMapUUID = null;
    }

    private void add() {
        if (this.displayInfo == null && this.lastMapUUID != null) {
            this.displayInfo = this.controller.getInfo(this.lastMapUUID.getUUID());
            this.displayInfo.itemFrames.add(this);
            displayInfo.hasFrameResolutionChanges.set(true);
            displayInfo.refreshResolution();
            displayInfo.addTileIfMissing(this.lastMapUUID.getTileX(), this.lastMapUUID.getTileY());
        }
    }

    /**
     * Forms a linked list of all item frame entries
     * on the server. Each entry represents a single
     * item frame being updated.
     */
    public static final class UpdateEntry {
        public final ItemFrameInfo info;
        public boolean added;
        public boolean prioritized;
        public UpdateEntry prev;
        public UpdateEntry next;

        private UpdateEntry(ItemFrameInfo info) {
            this.info = info;
            this.added = false;
            this.prioritized = false;
            this.prev = null;
            this.next = null;
        }
    }
}