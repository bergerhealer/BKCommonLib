package com.bergerkiller.bukkit.common.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.map.MapCursor;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.PacketUtil;

/**
 * This is a Virtual Map view that is synchronized to individual players, but is never actually saved on the server.
 * It boasts an efficient synchronization system that attempts to minimize the amount of data sent over during drawing.
 */
public class VirtualMap {
    private static final int RESOLUTION = 128;
    private static final int BUFFER_SIZE = (RESOLUTION * RESOLUTION);
    private final ArrayList<Viewer> viewers = new ArrayList<Viewer>();
    private final byte[] zbuffer = new byte[BUFFER_SIZE];
    private final byte[] livebuffer = new byte[BUFFER_SIZE];
    private Layer layerStack = new Layer(this);
    private final MapClip clip = new MapClip();
    private boolean _stopWithoutOwners = true;
    private boolean _isBeingViewed = false;
    private boolean _updateWhenNotViewing = false;
    private int updateTaskId = -1;
    private ItemStack _item = null;
    protected int itemId = -1;
    protected JavaPlugin plugin = null;

    /**
     * Starts updating this map
     * 
     * @param plugin that owns this map
     * @param mapItem associated with the map to update
     */
    protected void start(JavaPlugin plugin, ItemStack mapItem) {
        if (this.updateTaskId != -1) {
            throw new IllegalStateException("This virtual map was already started");
        }
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin can not be null");
        }
        int itemId = getMapId(mapItem);
        if (itemId == -1) {
            throw new IllegalArgumentException("Item is not of a valid map");
        }
        this.plugin = plugin;
        this._item = mapItem.clone();
        this.itemId = getMapId(mapItem);
        this.updateTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, new Runnable() {
            @Override
            public void run() {
                update();
            }
        }, 1, 1);
    }

    /**
     * Stops updating this virtual map.
     */
    protected void stop() {
        if (this.updateTaskId != -1) {
            Bukkit.getScheduler().cancelTask(this.updateTaskId);
            this.updateTaskId = -1;
            this._item = null;
            this.itemId = -1;
        }
    }

    public final void update() {
        if (itemId == -1) {
            return;
        }

        // Check for all viewers whether they are holding the map
        // If they are, perform continuous updates
        boolean hasDirtyViewers = false;
        this._isBeingViewed = false;
        ListIterator<Viewer> iter = this.viewers.listIterator();
        while (iter.hasNext()) {
            Viewer viewer = iter.next();
            viewer.update();
            if (viewer.viewing) {
                this._isBeingViewed = true;
                hasDirtyViewers |= viewer.clip.dirty;
            } else if (!viewer.owning) {
                iter.remove();
            }
        }

        // If no more owners remain, remove (option)
        if (this.viewers.isEmpty() && this._stopWithoutOwners) {
            this.stop();
            return;
        }

        // Perform the actual updates to the map
        doTick(this._updateWhenNotViewing || this._isBeingViewed);

        // Synchronize the map information to the clients
        if (this.clip.dirty) {
            this.clip.dirty = false;

            // For all viewers watching, send map texture updates
            // For players that are in-sync, we can re-use the same packet
            CommonPacket syncPacket = null;
            for (Viewer viewer : this.viewers) {
                if (!viewer.viewing) {
                    viewer.clip.markDirty(clip);
                } else if (viewer.clip.dirty) {
                    viewer.clip.markDirty(clip);
                    viewer.clip.dirty = false;
                    viewer.send(createPacket(viewer.clip));
                } else {
                    if (syncPacket == null) {
                        syncPacket = createPacket(this.clip);
                    } else {
                        syncPacket = syncPacket.clone();
                    }
                    viewer.send(syncPacket);
                }
            }

        } else if (hasDirtyViewers) {

            // Send full changes to the dirty viewers
            for (Viewer viewer : this.viewers) {
                if (viewer.viewing && viewer.clip.dirty) {
                    viewer.clip.dirty = false;
                    viewer.send(createPacket(viewer.clip));
                }
            }
        }
    }

    /**
     * Executes {@link #onTick()}, with optional other map-specific tasks that occur every tick.
     * This should not be called by anyone, not overridden!
     */
    protected void doTick(boolean mapVisible) {
        // Perform the actual updates to the map
        if (mapVisible) {
            //StopWatch.instance.start();
            this.onTick();
            //StopWatch.instance.stop().log("VirtualMap onTick()");
        }
    }

    /**
     * Gets the item associated with the map
     * 
     * @return map item
     */
    public ItemStack getMapItem() {
        return this._item;
    }

    /**
     * Gets whether this virtual map has any player viewers at all
     * 
     * @return True if there are viewers, False if not
     */
    public boolean hasViewers() {
        return !this.viewers.isEmpty();
    }

    /**
     * Checks whether this map is currently being viewed by any players.
     * This is when they are holding the map in either of their hands.
     * 
     * @return True if this map is being viewed by anyone, False if not
     */
    public boolean isBeingViewed() {
        return this._isBeingViewed;
    }

    /**
     * Sets whether this Virtual Map's {@link #onTick()} method is called when no players
     * are currently viewing this map.
     * 
     * @param updateWithoutViewers option
     */
    public void setUpdateWhenNotViewing(boolean updateWhenNotViewing) {
        this._updateWhenNotViewing = updateWhenNotViewing;
    }

    /**
     * Sets whether {@link #stop()} is called when no more players own this map, or all owners
     * are offline.
     * 
     * @param stopWithoutOwners option
     */
    public void setStopWithoutOwners(boolean stopWithoutOwners) {
        this._stopWithoutOwners = stopWithoutOwners;
    }

    /**
     * Fired every tick to update this Virtual Map.
     * This method can be overridden to dynamically update the map continuously.
     * To optimize performance, only draw things in the map when they change.
     */
    public void onTick() {
    }

    /**
     * Adds a new player that can view the updated VirtualMap.
     * Their map will be instantly updated to show the current virtual view.
     * 
     * @param player to add as viewer
     * @param item of the map that this VirtualMap should be shown on
     */
    public void addViewer(Player player) {
        Viewer viewer = new Viewer(player, this);
        for (Viewer oldViewer : this.viewers) {
            if (oldViewer.equals(viewer)) {
                return;
            }
        }
        this.viewers.add(viewer);
        viewer.send(createPacket(null));
    }

    /**
     * Removes a player from the viewer list, they will no longer see the updated VirtualMap.
     * If they are still online, their map will be updated to the default map.
     * 
     * @param player to remove as viewer
     * @param item associated with the map
     */
    public void removeViewer(Player player) {
        this.viewers.remove(new Viewer(player, this));
    }

    /**
     * Gets whether this Virtual Map is viewed by a player
     * 
     * @param player to check as viewer
     * @param item of the map
     * @return True if viewed, False if not
     */
    public boolean containsViewer(Player player) {
        return this.viewers.contains(new Viewer(player, this));
    }

    private CommonPacket createPacket(MapClip clip) {
        CommonPacket mapUpdate = PacketType.OUT_MAP.newInstance();
        mapUpdate.write(PacketType.OUT_MAP.cursors, new MapCursor[0]);
        mapUpdate.write(PacketType.OUT_MAP.itemId, this.itemId);
        mapUpdate.write(PacketType.OUT_MAP.scale, (byte) 1);
        mapUpdate.write(PacketType.OUT_MAP.track, false);
        if (clip == null) {
            mapUpdate.write(PacketType.OUT_MAP.xmin, 0);
            mapUpdate.write(PacketType.OUT_MAP.ymin, 0);
            mapUpdate.write(PacketType.OUT_MAP.width, RESOLUTION);
            mapUpdate.write(PacketType.OUT_MAP.height, RESOLUTION);
            mapUpdate.write(PacketType.OUT_MAP.pixels, this.livebuffer.clone());
        } else {
            int w = clip.getWidth();
            int h = clip.getHeight();
            byte[] pixels = new byte[w * h];

            int dst_index = 0;
            for (int y = 0; y < h; y++) {
                int src_index = ((y + clip.getY()) * 128) + clip.getX();
                System.arraycopy(this.livebuffer, src_index, pixels, dst_index, w);
                dst_index += w;
            }

            mapUpdate.write(PacketType.OUT_MAP.xmin, clip.getX());
            mapUpdate.write(PacketType.OUT_MAP.ymin, clip.getY());
            mapUpdate.write(PacketType.OUT_MAP.width, w);
            mapUpdate.write(PacketType.OUT_MAP.height, h);
            mapUpdate.write(PacketType.OUT_MAP.pixels, pixels);
        }
        return mapUpdate;
    }

    /**
     * Retrieves the base layer at z-index 0.
     * Note that if layers at negative z-index exist, this is not the background.
     * 
     * @return base layer
     */
    public final Layer getLayer() {
        return this.layerStack;
    }

    /**
     * Retrieves a certain layer in the Z-buffer
     * 
     * @param z buffer index, both positive and negative numbers supported
     * @return layer at this z-buffer index value
     */
    public final Layer getLayer(int z) {
        Layer currentLayer = this.layerStack;
        while (z < 0) {
            currentLayer = currentLayer.previous();
            z++;
        }
        while (z > 0) {
            currentLayer = currentLayer.next();
            z--;
        }
        return currentLayer;
    }

    /**
     * A single layer in the Z-buffer of the virtual map
     */
    public static class Layer extends MapCanvas {
        private Layer previous, next;
        private byte z_index;
        private final VirtualMap map;
        private final byte[] buffer;
        private final MapClip clip = new MapClip();

        private Layer(VirtualMap map) {
            this.buffer = new byte[BUFFER_SIZE];
            this.map = map;
            this.z_index = 0;
        }

        @Override
        public final int getWidth() {
            return RESOLUTION;
        }

        @Override
        public final int getHeight() {
            return RESOLUTION;
        }

        @Override
        public final byte[] getBuffer() {
            return this.buffer;
        }

        /**
         * Gets the layer that follows this one, creating a new layer if required.
         * The next layer is higher in the z-buffer, meaning it overlaps this layer.
         * 
         * @return next layer
         */
        public Layer next() {
            if (this.next == null) {
                this.next = new Layer(this.map);
                this.next.z_index = (byte) (this.z_index + 1);
                this.next.previous = this;
            }
            return this.next;
        }

        /**
         * Gets the layer that precedes this one, creating a new layer if required.
         * The previous layer is lower in the z-buffer, meaning that this layer overlaps that one.
         * 
         * @return previous layer
         */
        public Layer previous() {
            if (this.previous == null) {
                this.previous = new Layer(this.map);
                this.previous.next = this;

                // All z-indices are offset by one
                Layer current = this;
                for (int i = 0; i < this.map.zbuffer.length; i++) {
                    this.map.zbuffer[i]++;
                }
                do {
                    current.z_index++;
                    current = current.next;
                } while (current != null);
            }
            return this.previous;
        }

        @Override
        public MapCanvas fillPixels(int x, int y, int w, int h, byte color) {
            // Out of bounds
            if (x >= RESOLUTION || y >= RESOLUTION) {
                return this;
            }

            // First do some bounds checking on the rectangle
            boolean is_entire_canvas = (x == 0 && y == 0 && w == RESOLUTION && h == RESOLUTION);
            if (!is_entire_canvas) {
                if (x < 0) {
                    w += x;
                    x = 0;
                }
                if (y < 0) {
                    h += y;
                    y = 0;
                }
                if ((x + w) > RESOLUTION) {
                    w = (RESOLUTION - x);
                }
                if ((y + h) > RESOLUTION) {
                    h = (RESOLUTION - y);
                }
            }

            if (is_entire_canvas) {
                // Update the pixel buffer efficiently
                Arrays.fill(this.buffer, color);
                if (color == 0) {
                    if (this.clip.dirty) {
                        this.map.clip.markDirty(this.clip);
                    }

                    // all pixel data in this layer cleared
                    this.clip.dirty = false;
                } else {
                    // entire pixel area dirty
                    this.clip.markDirty(0, 0, RESOLUTION, RESOLUTION);
                    this.map.clip.markDirty(0, 0, RESOLUTION, RESOLUTION);
                }
            } else {
                // Fill the pixel buffer in lines
                for (int dy = 0; dy < h; dy++) {
                    int idx = (x + ((y + dy) * RESOLUTION));
                    Arrays.fill(this.buffer, idx, idx + w, color);
                }
                if (color == 0) {
                    // Marking only a portion of the area cleared
                    // We will have to update the clip area accordingly
                    // This requires a re-calculation (but is not required)
                    //TODO!
                } else {
                    // mark layer area dirty
                    this.clip.markDirty(x, y, w, h);
                }

                // Need to update this entire area to the viewers
                this.map.clip.markDirty(x, y, w, h);
            }

            // Update the z-buffer and live buffer
            if (color == 0) {
                // Go down the layers, fixing up the z-index as needed
                final VirtualMap map = this.map;
                Layer layer = this;
                boolean hasFaultyZ;
                do {
                    hasFaultyZ = false;
                    if (layer.previous == null) {
                        // There is no layer below.
                        // Pixels at this z-index need to be copied to the live buffer
                        for (int dy = 0; dy < h; dy++) {
                            int idx = (x + ((y + dy) * RESOLUTION));
                            int idx_end = (idx + w);
                            while (idx < idx_end) {
                                if (map.zbuffer[idx] == layer.z_index) {
                                    map.livebuffer[idx] = layer.buffer[idx];
                                }
                                ++idx;
                            }
                        }
                    } else {
                        // There is a layer below we can drop down to
                        final byte prev_z = layer.previous.z_index;
                        for (int dy = 0; dy < h; dy++) {
                            int index = (x + ((y + dy) * RESOLUTION));
                            int index_end = (index + w);
                            while (index < index_end) {
                                if (map.zbuffer[index] == layer.z_index) {
                                    byte layer_color = layer.buffer[index];
                                    if (layer_color != 0) {
                                        map.livebuffer[index] = layer_color;
                                    } else {
                                        map.zbuffer[index] = prev_z;
                                        hasFaultyZ = true;
                                    }
                                }
                                index++;
                            }
                        }
                    }
                } while ((layer = layer.previous) != null && hasFaultyZ);

            } else {
                // Update z-index to be greater or equal to this layer's z
                if (is_entire_canvas) {
                    for (int i = 0; i < this.map.zbuffer.length; i++) {
                        if (this.map.zbuffer[i] < this.z_index) {
                            this.map.zbuffer[i] = this.z_index;
                        }
                    }
                } else {
                    for (int dy = 0; dy < h; dy++) {
                        int idx = (x + ((y + dy) * RESOLUTION));
                        int idx_end = (idx + w);
                        while (idx < idx_end) {
                            if (this.map.zbuffer[idx] < this.z_index) {
                                this.map.livebuffer[idx] = this.z_index;
                            }
                            ++idx;
                        }
                    }
                }
            }
            return this;
        }

        @Override
        public final void writePixel(int x, int y, byte color) {
            // Validate and obtain buffer index. The bounds check avoid bounds checks for the buffers
            if (x < 0 || x >= RESOLUTION) {
                return;
            }
            int index = x + (y * RESOLUTION);
            if (index < 0 || index >= buffer.length) {
                return;
            }

            // Update pixel value and check for change of transparency
            byte pixel_z = map.zbuffer[index];
            boolean is_transparency_change = ((buffer[index] == 0) != (color == 0));
            buffer[index] = color;

            // Update the Z-Buffer when transparency changes
            // See if this pixel is visible
            if (is_transparency_change) {
                this.clip.markDirty(x, y);
                if (color == 0) {
                    if (pixel_z == z_index) {
                        // We are no longer visible, drop z-index down to layers below
                        Layer layer = this;
                        while (layer.buffer[index] == 0 && layer.previous != null) {
                            layer = layer.previous;
                        }
                        this.map.zbuffer[index] = pixel_z = layer.z_index;
                        this.map.livebuffer[index] = layer.buffer[index];
                        this.map.clip.markDirty(x, y);
                    }
                } else if (pixel_z <= z_index) {
                    // We became visible
                    this.map.zbuffer[index] = z_index;
                    this.map.livebuffer[index] = color;
                    this.map.clip.markDirty(x, y);
                }
            } else if (pixel_z == z_index) {
                // Update pixel if this pixel is at z-index
                this.map.livebuffer[index] = color;
                this.map.clip.markDirty(x, y);
            }
        }

        @Override
        public final byte readPixel(int x, int y) {
            if (x >= 0 && y < RESOLUTION) {
                int index = x + (y * RESOLUTION);
                if (index >= 0 || index < buffer.length) {
                    Layer layer = this;
                    byte color;
                    do {
                        color = layer.buffer[index];
                        layer = layer.previous;
                    } while (layer != null && color == 0);
                    return color;
                }
            }
            return (byte) 0;
        }

        private final byte readBasePixel(int x, int y) {
            if (x >= 0 && y < RESOLUTION) {
                int index = x + (y * RESOLUTION);
                if (index >= 0 || index < buffer.length) {
                    return buffer[index];
                }
            }
            return (byte) 0;
        }

        private final byte[] readBasePixels(int x, int y, int w, int h, byte[] dst_buffer) {
            return super.readPixels(x, y, w, h, dst_buffer);
        }

        @Override
        public byte[] readPixels(int x, int y, int w, int h, byte[] dst_buffer) {
            // Read layer itself
            readBasePixels(x, y, w, h, dst_buffer);

            // Slow method. Have to figure out the underlying pixel values
            byte[] tmp_buffer = null;
            Layer layer = this;
            int remaining_pixels = dst_buffer.length;
            while (layer.previous != null && remaining_pixels > 0) {
                layer = layer.previous;

                if (!layer.clip.dirty) {
                    continue;
                }

                // For larger amounts of pixels it is worthwhile to read the full buffer each time
                // Too many calls to readPixel() are slow!
                if (remaining_pixels > 100) {
                    if (tmp_buffer == null) {
                        tmp_buffer = new byte[w * h];
                    }
                    layer.readBasePixels(x, y, w, h, tmp_buffer);

                    remaining_pixels = 0;
                    for (int i = 0; i < tmp_buffer.length; i++) {
                        if (dst_buffer[i] == 0) {
                            byte color = tmp_buffer[i];
                            if (color == 0) {
                                remaining_pixels++; // also transparent, move layer down
                            } else {
                                dst_buffer[i] = color;
                            }
                        }
                    }
                } else {
                    int dx_end = x + w;
                    int dx = x;
                    int dy = y;
                    remaining_pixels = 0;
                    for (int i = 0; i < dst_buffer.length; i++) {
                        if (dst_buffer[i] == 0) {
                            byte color = layer.readBasePixel(dx, dy);
                            if (color != 0) {
                                dst_buffer[i] = color;
                            } else {
                                remaining_pixels++;
                            }
                        }
                        if (++dx == dx_end) {
                            dx = x;
                            ++dy;
                        }
                    }
                }
            }
            return dst_buffer;
        }

    }

    private static class Viewer {
        public final Player player;
        public final MapClip clip = new MapClip();
        private final VirtualMap map;
        public boolean viewing;
        public boolean owning;

        public Viewer(Player player, VirtualMap map) {
            this.player = player;
            this.map = map;
            this.viewing = false;
            this.owning = true;
        }

        public void update() {
            this.owning = this.player.isOnline();
            if (this.owning) {
                PlayerInventory inv = this.player.getInventory();
                this.viewing = (getMapId(inv.getItemInMainHand()) == map.itemId) || 
                        (getMapId(inv.getItemInOffHand()) == map.itemId);
            } else {
                this.viewing = false;
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Viewer) {
                return player == ((Viewer) obj).player;
            }
            return false;
        }

        public void send(CommonPacket mapUpdatePacket) {
            PacketUtil.sendPacket(this.player, mapUpdatePacket, false);
        }
    }

    protected static int getMapId(ItemStack item) {
        return (item == null || item.getType() != Material.MAP) ? -1 : item.getDurability(); 
    }
}
