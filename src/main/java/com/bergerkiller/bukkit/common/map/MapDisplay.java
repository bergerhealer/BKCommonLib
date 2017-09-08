package com.bergerkiller.bukkit.common.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.common.events.map.MapClickEvent;
import com.bergerkiller.bukkit.common.events.map.MapKeyEvent;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.internal.CommonMapController.MapDisplayInfo;
import com.bergerkiller.bukkit.common.internal.CommonMapUUIDStore;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.ItemUtil;

/**
 * A {@link MapDisplay} updates and displays map contents to a group of players set as <u>owners</u>.
 * Owners can also be set to provide input to the map using steering controls. All owners will receiver
 * map updates when they can see the map, and only when contents change. Non-owners will never see these updates.
 * The display is purely <u>virtual</u>, meaning that the map contents are never saved on the server.<br>
 * <br>
 * Owner input can be <u>intercepted</u>, allowing the owner of the map to control it directly using steering controls.
 * The easiest way to enable this, is to set {@link #setReceiveInputWhenHolding(opt)} to true. Alternatively it can be
 * set for individual owners using {@link #setReceiveInput(owner, opt)}. Please note that the player can not move
 * around while input is intercepted.<br>
 * <br>
 * To use this class, it should be further implemented to add custom functionality:
 * <li>{@link #onAttached()} is called after the map display is initialized</li>
 * <li>{@link #onDetached()} is called right before the map display is de-initialized</li>
 * <li>{@link #onTick()} is called every tick, do logic updates and drawing here</li>
 * <li>{@link #onKeyPressed(Event)} is called whenever an owner presses a key (down)</li>
 * <li>{@link #onKeyReleased(Event)} is called whenever an owner releases a key (up)</li>
 * <li>{@link #onKey(event)} is called every tick while the owner is holding down a key</li>
 * <li>{@link #onLeftClick(event)} is called when a player left-clicks on the map</li>
 * <li>{@link #onRightClick(event)} is called when a player right-clicks on the map</li>
 * </ul>
 */
public class MapDisplay {
    private final MapSession session = new MapSession(this);
    private int width, height;
    private final MapClip clip = new MapClip();
    private List<MapDisplayTile> tiles = new ArrayList<MapDisplayTile>();
    private byte[] zbuffer = null;
    private byte[] livebuffer = null;
    private Layer layerStack;
    private boolean _updateWhenNotViewing = false;
    private boolean _receiveInputWhenHolding = false;
    private boolean _global = true;
    private int updateTaskId = -1;
    private ItemStack _item = null;
    protected MapDisplayInfo info = null;
    protected JavaPlugin plugin = null;

    /**
     * Initializes this Map Display, setting the owner plugin and what map it represents.
     * If this display is already initialized for this plugin and map, this method does nothing.
     * 
     * @param plugin owner
     * @param mapItem on which map is displayed
     */
    public void initialize(JavaPlugin plugin, ItemStack mapItem) {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin can not be null");
        }
        MapDisplayInfo mapInfo = CommonPlugin.getInstance().getMapController().getInfo(mapItem);
        if (mapInfo == null) {
            throw new IllegalArgumentException("Map Item is not of a valid map");
        }

        if (this.plugin != null) {
            if (this.plugin != plugin) {
                throw new IllegalArgumentException("This Map Display was already initialized for another plugin");
            }
            if (this.info != mapInfo) {
                throw new IllegalArgumentException("This Map Display was already initialized for a different map");
            }
        } else {
            this.plugin = plugin;
            this.info = mapInfo;
            this._item = mapItem.clone();
        }

        //TODO: Dynamically find the tiles being used
        this.tiles.add(new MapDisplayTile(this, 0, 0));

        // Calculate the dimensions from the tiles and further initialize the buffers
        int minTileX = Integer.MAX_VALUE;
        int minTileY = Integer.MAX_VALUE;
        int maxTileX = 0;
        int maxTileY = 0;
        for (MapDisplayTile tile : this.tiles) {
            if (tile.tileX < minTileX)
                minTileX = tile.tileX;
            if (tile.tileX > maxTileX)
                maxTileX = tile.tileX;
            if (tile.tileY < minTileY)
                minTileY = tile.tileY;
            if (tile.tileY > maxTileY)
                maxTileY = tile.tileY;
        }
        this.width = (maxTileX - minTileX + 1) * 128;
        this.height = (maxTileY - minTileY + 1) * 128;
        this.zbuffer = new byte[this.width * this.height];
        this.livebuffer = new byte[this.width * this.height];
        this.layerStack = new Layer(this);

        this.setRunning(true);
    }

    /**
     * Gets the total width of this map display
     * 
     * @return total width
     */
    public final int getWidth() {
        return this.width;
    }

    /**
     * Gets the total height of this map display
     * 
     * @return total height
     */
    public final int getHeight() {
        return this.height;
    }

    /**
     * Gets the backing pixel color buffer for all currently displayed colors of the map display.
     * This is what is synchronized to the clients.
     * 
     * @return live display buffer
     */
    public final byte[] getLiveBuffer() {
        return this.livebuffer;
    }

    /**
     * Gets whether this Map Display is initialized or not
     * 
     * @return True if initialized, False if not
     */
    public boolean isInitialized() {
        return this.plugin != null;
    }

    /**
     * Sets the Session Mode used to decide when this Map Display should be automatically removed.
     * By default this is set to ONLINE, meaning the session will be removed once all owners are offline.
     * 
     * @param mode to set to
     */
    public void setSessionMode(MapSessionMode mode) {
        this.session.mode = mode;
    }

    /**
     * Gets the plugin owner of this Map Display. If this display has not yet been initialized,
     * this function returns null.
     * 
     * @return plugin owner
     */
    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    /**
     * Adds an owner to this Map Display that will receive map updates and can perform key input.
     * If this is the first owner being added, and {@link #initialize(plugin, mapItem)} has been called,
     * the display will start updating automatically. If there is an existing Map Display for this map,
     * that also is owned by this player, this player is removed as owner and re-added once this Map Display
     * stops.
     * 
     * @param owner to add
     */
    public void addOwner(Player owner) {
        this.session.addOwner(owner);
        this.setRunning(true);
    }

    /**
     * Removes an owner from this Map Display that will no longer receive map updates.
     * 
     * @param owner to remove
     */
    public void removeOwner(Player owner) {
        this.session.removeOwner(owner);
    }

    /**
     * Gets the internally used map display information
     * 
     * @return map display information
     */
    public MapDisplayInfo getMapInfo() {
        return this.info;
    }

    /**
     * Gets whether this Map Display has started updating
     * 
     * @return True if started, False if not
     */
    public boolean isRunning() {
        return updateTaskId != -1;
    }

    /**
     * Gets whether this Map Display is global. New players that view this map will
     * automatically see the contents of this Map Display if global. By default this is set to true.
     * 
     * @return True if this Map Display is visible to all players
     */
    public boolean isGlobal() {
        return this._global;
    }

    /**
     * Sets whether this Map Display is global. New players that view this this map will
     * automatically see the contents of this Map Display if global. By default this is set to true.<br>
     * <br>
     * If it is important that players can not see the contents maps that other players see,
     * this should be set to false.
     * 
     * @param global option to set to
     */
    public void setGlobal(boolean global) {
        this._global = global;
    }

    /**
     * Invalidates the entire view area of this MapDisplay, causing the display contents
     * to be re-sent to all viewers.
     */
    public void invalidate() {
        this.clip.markEverythingDirty();
    }

    /**
     * Performs all the updates required to display this Map Display to its viewers.
     * This is called automatically after having called start() on one of the implementations.
     * It should only be called when manually managing the map is required.
     */
    public final void update() {
        if (this.info == null) {
            return;
        }

        // Update session. This will update the viewers, and update interception modes.
        if (!this.session.update()) {
            this.setRunning(false);
            return;
        }

        // Intercept player input when set
        if (this._receiveInputWhenHolding) {
            for (MapSession.Owner owner : this.session.onlineOwners) {
                owner.interceptInput = owner.controlling;
            }
        }

        // Perform the actual updates to the map
        if (this._updateWhenNotViewing || this.session.hasViewers) {
            //StopWatch.instance.start();
            this.onTick();
            //StopWatch.instance.stop().log("VirtualMap onTick()");
        }

        // Synchronize the map information to the clients
        if (this.clip.dirty) {
            // For all viewers watching, send map texture updates
            // For players that are in-sync, we can re-use the same packet
            List<CommonPacket> syncPackets = null;
            for (MapSession.Owner owner : this.session.onlineOwners) {
                if (!owner.viewing) {
                    // Update dirty clip only
                    owner.clip.markDirty(this.clip);
                    continue;
                }

                // When viewers have individual dirty areas, they need their own update packets
                if (owner.clip.dirty) {
                    owner.clip.markDirty(this.clip);
                    owner.updateMap(this.getUpdatePackets(owner.clip));
                    continue;
                }

                // Viewers viewing for a while only need to have our own clip updated
                // We can re-use the same update packet for all viewers
                if (syncPackets == null) {
                    syncPackets = this.getUpdatePackets(this.clip);
                } else {
                    for (int i = 0; i < syncPackets.size(); i++) {
                        syncPackets.set(i, syncPackets.get(i).clone());
                    }
                }
                owner.updateMap(syncPackets);
            }

            // Done updating, reset the dirty state
            this.clip.clearDirty();

        } else if (session.hasNewViewers) {

            // Send full changes to the dirty viewers
            for (MapSession.Owner owner : session.onlineOwners) {
                if (owner.isNewViewer()) {
                    owner.updateMap(getUpdatePackets(owner.clip));
                }
            }
        }
    }

    private final List<CommonPacket> getUpdatePackets(MapClip clip) {
        List<CommonPacket> packets = new ArrayList<CommonPacket>(this.tiles.size());
        for (MapDisplayTile tile : this.tiles) {
            tile.addUpdatePackets(packets, clip);
        }
        return packets;
    }

    /**
     * Loads a texture from the plugin resources of the owner of this MapDisplay.
     * The MapDisplay must have been attached by a plugin before it can be used.
     * Throws an exception if the resource could not be loaded.
     * 
     * @param filename of the resource
     * @return texture
     */
    public final MapTexture loadTexture(String filename) {
        return MapTexture.loadPluginResource(this.plugin, filename);
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
     * Sets the map item without refreshing it in any persistent way
     * 
     * @param item to set to
     */
    public void setMapItemSilently(ItemStack item) {
        this._item = item;
        this.onMapItemChanged();
    }

    /**
     * Updates the item associated with the map. All players and item frame holding this map item 
     * that display this Map Display will have their items swapped. If the new item is not a map item,
     * then this display session is terminated.
     * 
     * @param item to set to
     */
    public void setMapItem(ItemStack item) {
        CommonPlugin.getInstance().getMapController().updateMapItem(this._item, item);
    }

    /**
     * Gets the input controller for an owner of this display. If the player is not an owner,
     * null is returned instead.
     * 
     * @param player to get the input from
     * @return input, or null if the player is not an owner
     */
    public MapPlayerInput getInput(Player player) {
        for (MapSession.Owner owner : this.session.onlineOwners) {
            if (owner.player == player) {
                return owner.input;
            }
        }
        return null;
    }

    /**
     * Gets a list of players currently owning this Map Display.
     * These players are still tracked, and make sure the map display is not reset.
     * 
     * @return list of owners
     */
    public List<Player> getOwners() {
        ArrayList<Player> owners = new ArrayList<Player>(this.session.onlineOwners.size());
        for (MapSession.Owner owner : this.session.onlineOwners) {
            owners.add(owner.player);
        }
        return owners;
    }

    /**
     * Gets a list of players currently viewing this Map Display
     * 
     * @return list of viewers
     */
    public List<Player> getViewers() {
        ArrayList<Player> viewers = new ArrayList<Player>(this.session.onlineOwners.size());
        for (MapSession.Owner owner : this.session.onlineOwners) {
            if (owner.viewing) {
                viewers.add(owner.player);
            }
        }
        return viewers;
    }

    /**
     * Checks whether a certain Player is currently viewing this map
     * 
     * @param player to check
     * @return True if viewing, False if not
     */
    public boolean isViewing(Player player) {
        for (MapSession.Owner owner : this.session.onlineOwners) {
            if (owner.player == player) {
                return owner.viewing;
            }
        }
        return false;
    }

    /**
     * Checks whether a certain Player is currently holding this map
     * in either of his hands.
     * 
     * @param player to check
     * @return True if holding, False if not
     */
    public boolean isHolding(Player player) {
        for (MapSession.Owner owner : this.session.onlineOwners) {
            if (owner.player == player) {
                return owner.holding;
            }
        }
        return false;
    }

    /**
     * Checks whether a certain Player is currently holding this map
     * in his main hand, enabling control
     * 
     * @param player to check
     * @return True if controlling, False if not
     */
    public boolean isControlling(Player player) {
        for (MapSession.Owner owner : this.session.onlineOwners) {
            if (owner.player == player) {
                return owner.controlling;
            }
        }
        return false;
    }
    
    /**
     * Gets whether this virtual map has any viewers at all
     * 
     * @return True if there are viewers, False if not
     */
    public boolean hasViewers() {
        return this.session.hasViewers;
    }

    /**
     * Sets whether this Virtual Map's {@link #onTick()} method is called when no players
     * are currently viewing this map. By default this is set to false.
     * 
     * @param updateWithoutViewers option
     */
    public void setUpdateWithoutViewers(boolean updateWhenNotViewing) {
        this._updateWhenNotViewing = updateWhenNotViewing;
    }

    /**
     * Sets whether player input is received when players are holding the map in their main hand.
     * Input is intercepted, which means they can no longer move around while holding the map.
     * They need to switch to a different item slot to be able to walk again.<br>
     * <br>
     * By default this is set to <b>false</b>
     * 
     * @param inputWhenHolding to set to
     */
    public void setReceiveInputWhenHolding(boolean inputWhenHolding) {
        if (this._receiveInputWhenHolding != inputWhenHolding) {
            this._receiveInputWhenHolding = inputWhenHolding;
            if (!inputWhenHolding) {
                // Release everyone we may have set as holding before
                for (MapSession.Owner owner : this.session.onlineOwners) {
                    owner.input.handleDisplayUpdate(this, false);
                }
            }
        }
    }

    /**
     * Gets whether player input is received and used for controlling this map.
     * If received, the player can not move around and map input is available.
     * By default this is set to False.
     * 
     * @param player viewer to get the option for
     * @return True if input is intercepted, False if not.
     */
    public boolean isReceivingInput(Player player) {
        for (MapSession.Owner owner : this.session.onlineOwners) {
            if (owner.player == player) {
                return owner.interceptInput;
            }
        }
        throw new IllegalArgumentException("Player is not an owner of this display");
    }

    /**
     * Sets whether player input is received and used for controlling this map.
     * If received, the player can not move around and map input is available.
     * By default this is set to False.
     *
     * @param player viewer to set the option for
     * @param interceptInput option
     */
    public void setReceiveInput(Player player, boolean interceptInput) {
        for (MapSession.Owner owner : this.session.onlineOwners) {
            if (owner.player == player) {
                owner.interceptInput = interceptInput;
                return;
            }
        }
        throw new IllegalArgumentException("Player is not an owner of this display");
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

    private void setRunning(boolean updating) {
        if (updating) {
            if (this.plugin != null && this.updateTaskId == -1) {
                this.updateTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, new Runnable() {
                    @Override
                    public void run() {
                        update();
                    }
                }, 1, 1);

                if (this.info != null) {
                    this.info.sessions.add(this.session);
                }

                this.session.initOwners();

                this.onAttached();
            }
        } else {
            if (this.updateTaskId != -1) {
                // Disable input interception for owners still lingering
                for (MapSession.Owner owner : this.session.onlineOwners) {
                    owner.input.handleDisplayUpdate(this, false);
                }

                // Handle onDetached
                this.onDetached();

                // Clean up
                Bukkit.getScheduler().cancelTask(this.updateTaskId);
                this.updateTaskId = -1;
                if (this.info != null) {
                    this.info.sessions.remove(this.session);
                }
            }
        }
    }

    @Override
    public String toString() {
        if (this.plugin == null) {
            return "{UNREGISTERED " + this.getClass().getSimpleName() + "}";
        } else {
            return "{" + this.plugin.getName() + " " + this.getClass().getSimpleName() + "}";
        }
    }

    /**
     * A single layer in the Z-buffer of the virtual map
     */
    public static class Layer extends MapCanvas {
        private Layer previous, next;
        private byte z_index;
        private final MapDisplay map;
        private final byte[] buffer;
        private final MapClip clip = new MapClip();

        private Layer(MapDisplay map) {
            this.buffer = new byte[map.getWidth() * map.getHeight()];
            this.map = map;
            this.z_index = 0;
        }

        @Override
        public final int getWidth() {
            return map.getWidth();
        }

        @Override
        public final int getHeight() {
            return map.getHeight();
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
        public MapCanvas writePixelsFill(int x, int y, int w, int h, byte color) {
            // Out of bounds
            if (x >= this.getWidth() || y >= this.getHeight()) {
                return this;
            }

            // First do some bounds checking on the rectangle
            boolean is_entire_canvas = (x == 0 && y == 0 && w == this.getWidth() && h == this.getHeight());
            if (!is_entire_canvas) {
                if (x < 0) {
                    w += x;
                    x = 0;
                }
                if (y < 0) {
                    h += y;
                    y = 0;
                }
                if ((x + w) > this.getWidth()) {
                    w = (this.getWidth() - x);
                }
                if ((y + h) > this.getHeight()) {
                    h = (this.getHeight() - y);
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
                    this.clip.markDirty(0, 0, this.getWidth(), this.getHeight());
                    this.map.clip.markDirty(0, 0, this.getWidth(), this.getHeight());
                }
            } else {
                // Fill the pixel buffer in lines
                for (int dy = 0; dy < h; dy++) {
                    int idx = (x + ((y + dy) * this.getWidth()));
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
                final MapDisplay map = this.map;
                Layer layer = this;
                boolean hasFaultyZ;
                do {
                    hasFaultyZ = false;
                    if (layer.previous == null) {
                        // There is no layer below.
                        // Pixels at this z-index need to be copied to the live buffer
                        for (int dy = 0; dy < h; dy++) {
                            int idx = (x + ((y + dy) * this.getWidth()));
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
                            int index = (x + ((y + dy) * this.getWidth()));
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
                        int idx = (x + ((y + dy) * this.getWidth()));
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
            if (x < 0 || x >= this.getWidth()) {
                return;
            }
            int index = x + (y * this.getWidth());
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
            if (x >= 0 && y < this.getHeight()) {
                int index = x + (y * this.getWidth());
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
            if (x >= 0 && y < this.getHeight()) {
                int index = x + (y * this.getWidth());
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

    /**
     * Called right after this Map Display is bound to a plugin and map
     */
    public void onAttached() {}

    /**
     * Called right before the map display is removed after the session ends
     */
    public void onDetached() {}

    /**
     * Fired every tick to update this Virtual Map.
     * This method can be overridden to dynamically update the map continuously.
     * To optimize performance, only draw things in the map when they change.
     */
    public void onTick() {}

    /**
     * Callback function called every tick while a key is pressed down.
     * These callbacks are called before {@link #onTick()}.
     * 
     * @param event
     */
    public void onKey(MapKeyEvent event) {}

    /**
     * Callback function called when a key changed from not-pressed to pressed down
     * These callbacks are called before {@link #onTick()}.
     * 
     * @param event
     */
    public void onKeyPressed(MapKeyEvent event) {}

    /**
     * Callback function called when a key changed from pressed to not pressed down
     * These callbacks are called before {@link #onTick()}.
     * 
     * @param event
     */
    public void onKeyReleased(MapKeyEvent event) {}

    /**
     * Callback function called when a player left-clicks the map held in an item frame
     * showing this Map Display.
     * 
     * @param event
     */
    public void onLeftClick(MapClickEvent event) {}

    /**
     * Callback function called when a player right-clicks the map held in an item frame
     * showing this Map Display.
     * 
     * @param event
     */
    public void onRightClick(MapClickEvent event) {}

    /**
     * Callback function called when the map item of this Map Display changed
     */
    public void onMapItemChanged() {}

    /**
     * Creates a new Map Display item that will automatically initialize a particular Map Display class
     * when viewed
     * 
     * @param mapDisplayClass from a Java Plugin (jar)
     * @return map item
     */
    public static ItemStack createMapItem(Class<? extends MapDisplay> mapDisplayClass) {
        Plugin plugin = CommonUtil.getPluginByClass(mapDisplayClass);
        if (plugin == null) {
            throw new IllegalArgumentException("The class " + mapDisplayClass.getName() + " does not belong to a Java Plugin");
        }
        return createMapItem(plugin, mapDisplayClass);
    }

    /**
     * Creates a new Map Display item that will automatically initialize a particular Map Display class
     * when viewed
     * 
     * @param plugin owner of the display
     * @param mapDisplayClass
     * @return map item
     */
    public static ItemStack createMapItem(Plugin plugin, Class<? extends MapDisplay> mapDisplayClass) {
        ItemStack mapItem = ItemUtil.createItem(Material.MAP, 0, 1);
        CommonTagCompound tag = ItemUtil.getMetaTag(mapItem, true);
        tag.putValue("mapDisplayPlugin", plugin.getName());
        tag.putValue("mapDisplayClass", mapDisplayClass.getName());
        tag.putUUID("mapDisplay", CommonMapUUIDStore.generateDynamicMapUUID());
        return mapItem;
    }

    /**
     * Globally refreshes the contents of a particular map item
     * 
     * @param oldItem to be refreshed, can not be null
     * @param newItem to set to, null to remove. Can be same as oldItem.
     */
    public static void updateMapItem(ItemStack oldItem, ItemStack newItem) {
        CommonPlugin.getInstance().getMapController().updateMapItem(oldItem, newItem);
    }
}
