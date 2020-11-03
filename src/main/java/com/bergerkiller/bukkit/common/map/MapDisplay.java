package com.bergerkiller.bukkit.common.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.common.events.map.MapClickEvent;
import com.bergerkiller.bukkit.common.events.map.MapKeyEvent;
import com.bergerkiller.bukkit.common.events.map.MapStatusEvent;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.map.markers.MapDisplayMarkers;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetRoot;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.CommonMapController.MapDisplayInfo;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.utils.ItemUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.PlayerUtil;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;

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
public class MapDisplay implements MapDisplayEvents {
    private final MapSession session = new MapSession(this);
    private int width, height;
    private final MapClip clip = new MapClip();
    private byte[] zbuffer = null;
    private byte[] livebuffer = null;
    private Layer layerStack;
    private boolean _updateWhenNotViewing = true;
    private boolean _receiveInputWhenHolding = false;
    private boolean _global = true;
    private boolean _playSoundToAllViewers = false;
    private float _masterVolume = 1.0f;
    private int updateTaskId = -1;
    private ItemStack _oldItem = null;
    private ItemStack _item = null;
    protected MapDisplayInfo info = null;
    protected JavaPlugin plugin = null;
    private final MapWidgetRoot widgets = new MapWidgetRoot(this);
    private final MapDisplayMarkers markers = new MapDisplayMarkers();

    /**
     * Properties of this Map Display. Can be used to store information about
     * this map display persistently in the map item itself.
     */
    protected final MapDisplayProperties properties = new MapDisplayProperties() {
        @Override
        public ItemStack getMapItem() {
            return MapDisplay.this.getMapItem();
        }

        @Override
        public String getPluginName() {
            Plugin plugin = MapDisplay.this.getPlugin();
            return (plugin != null) ? plugin.getName() : super.getPluginName();
        }

        @Override
        public Plugin getPlugin() {
            Plugin plugin = MapDisplay.this.getPlugin();
            return (plugin != null) ? plugin : super.getPlugin();
        }

        @Override
        public String getMapDisplayClassName() {
            return MapDisplay.this.getClass().getName();
        }

        @Override
        public Class<? extends MapDisplay> getMapDisplayClass() {
            return MapDisplay.this.getClass();
        }

        @Override
        public UUID getUniqueId() {
            MapDisplayInfo info = MapDisplay.this.getMapInfo();
            return (info != null) ? info.uuid : super.getUniqueId();
        }
    };

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
            this._oldItem = ItemUtil.cloneItem(this._item);
        }
        this.setRunning(true);
    }

    // called when setRunning(true) is called, right before onAttached
    private void preRunInitialize() {
        this.info.loadTiles(this.session, true);
        this.markers.clear();
        this.width = this.info.getDesiredWidth();
        this.height = this.info.getDesiredHeight();
        this.zbuffer = new byte[this.width * this.height];
        this.livebuffer = new byte[this.width * this.height];
        this.layerStack = new Layer(this, this.width, this.height);
        this.widgets.setBounds(0, 0, this.width, this.height);
        this.clip.markEverythingDirty();
    }

    /**
     * Gets a list of all display tiles in use by this display
     * 
     * @return tiles
     */
    public List<MapDisplayTile> getDisplayTiles() {
        return this.session.tiles;
    }

    /**
     * Checks whether a particular map display tile exists displaying contents
     * 
     * @param tileX of the tile
     * @param tileY of the tile
     * @return True if the tile exists
     */
    public boolean containsTile(int tileX, int tileY) {
        for (MapDisplayTile tile : this.session.tiles) {
            if (tile.tileX == tileX && tile.tileY == tileY) {
                return true;
            }
        }
        return false;
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

        // Reset the display when resolution changes
        // Note: must be done after sesion.update() as it changes the hasViewers property!
        if (this.session.refreshResolutionRequested && session.hasViewers) {
            this.session.refreshResolutionRequested = false;
            if (this.getWidth() == this.info.getDesiredWidth() &&
                this.getHeight() == this.info.getDesiredHeight())
            {
                // Resolution did not change, but the visible tiles may have. Refresh those.
                this.info.loadTiles(this.session, false);
            } else {
                // Re-initialize the display
                this.handleStopRunning();
                this.handleStartRunning();

                // Continue updating as normal (running = true)
            }
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
            this.widgets.performTickUpdates();
            //StopWatch.instance.stop().log("VirtualMap onTick()");
        }

        // Refresh item
        this.refreshMapItem();

        // Synchronize the map information to the clients
        if (this.clip.dirty) {
            // For all viewers watching, send map texture updates
            // For players that are in-sync, we can re-use the same packet
            List<MapDisplayTile.Update> syncUpdates = null;
            for (MapSession.Owner owner : this.session.onlineOwners) {
                if (!owner.viewing) {
                    // Update dirty clip only
                    owner.clip.markDirty(this.clip);
                    continue;
                }

                // When viewers have individual dirty areas, they need their own update packets
                if (owner.clip.dirty) {
                    owner.clip.markDirty(this.clip);
                    owner.updateMap(this.getUpdates(owner.clip, owner.player));
                    continue;
                }

                // Viewers viewing for a while only need to have our own clip updated
                if (syncUpdates == null) {
                    syncUpdates = this.getUpdates(this.clip, owner.player);
                } else {
                    // We can re-use the same update packets for all viewers
                    // We do need to make sure to clear map marker tiles represented
                    for (int i = 0; i < syncUpdates.size(); i++) {
                        syncUpdates.set(i, syncUpdates.get(i).clone());
                    }
                }
                owner.updateMap(syncUpdates);
            }

            // Done updating, reset the dirty state
            this.clip.clearDirty();

        } else {
            // Send full changes to new dirty viewers
            if (session.hasNewViewers) {
                for (MapSession.Owner owner : session.onlineOwners) {
                    if (owner.isNewViewer()) {
                        owner.updateMap(getUpdates(owner.clip, owner.player));
                    }
                }
            }

            // Check all viewers to see if any of them have dirty areas that need to be refreshed
            // When players rejoin or change worlds, they may not know the map
            for (MapSession.Owner owner : this.session.onlineOwners) {
                if (owner.viewing && owner.clip.dirty) {
                    owner.updateMap(this.getUpdates(owner.clip, owner.player));
                }
            }
        }

        // Check all tiles with markers on them and synchronize the changes to the players
        // Previous map content changes may have already synchronized them, in which case
        // the player was marked as synchronized for the tile affected.
        // After this, all the 'is marker changed' logic is reset.
        markers.synchronize(this.session);
    }

    private final void refreshMapItem() {
        if (!LogicUtil.bothNullOrEqual(this._oldItem, this._item)) {
            CommonPlugin.getInstance().getMapController().updateMapItem(this._oldItem, this._item);
        }
    }

    private final List<MapDisplayTile.Update> getUpdates(MapClip clip, Player viewer) {
        List<MapDisplayTile.Update> updates = new ArrayList<MapDisplayTile.Update>(this.session.tiles.size());
        for (MapDisplayTile tile : this.session.tiles) {
            tile.addTileUpdate(this, viewer, updates, clip);
        }
        return updates;
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
        this._oldItem = ItemUtil.cloneItem(item);
        this.onMapItemChanged();
        onMapChangedWidgets(this.widgets);
    }

    private static void onMapChangedWidgets(MapWidget widget) {
        widget.onMapItemChanged();
        for (MapWidget child : widget.getWidgets()) {
            onMapChangedWidgets(child);
        }
    }

    /**
     * Updates the item associated with the map. All players and item frame holding this map item 
     * that display this Map Display will have their items swapped. If the new item is not a map item,
     * then this display session is terminated.
     * 
     * @param item to set to
     */
    public void setMapItem(ItemStack item) {
        this._item = item;
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
     * are currently viewing this map. By default this is set to true.
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
                    owner.interceptInput = false;
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
     * Gets all widgets added to this display using {@link #addWidget(MapWidget)}.
     * 
     * @return an immutable list of all added widgets
     */
    public List<MapWidget> getWidgets() {
        return this.widgets.getWidgets();
    }

    /**
     * Removes all widgets added to this display using {@link #addWidget(MapWidget)}.
     * Previously drawn widget contents will be cleared.
     */
    public void clearWidgets() {
        this.widgets.clearWidgets();
    }

    /**
     * Adds a widget to this display. This widget will receive updates and can automatically
     * re-draw itself. It is a smart display element on the map. Widgets can have other widgets
     * added as well, allowing for widgets composed of other widgets.
     * 
     * @param widget to add
     */
    public void addWidget(MapWidget widget) {
        this.widgets.addWidget(widget);
    }

    /**
     * Removes a widget previously added using {@link #addWidget(MapWidget)}.
     * Previously drawn widget contents will be cleared.
     * 
     * @param widget to remove
     * @return True if the widget was removed
     */
    public boolean removeWidget(MapWidget widget) {
        return this.widgets.removeWidget(widget);
    }

    /**
     * Gets the default root widget to which all other widgets are added
     * 
     * @return root widget
     */
    public MapWidget getRootWidget() {
        return this.widgets;
    }

    /**
     * Gets the widget that is currently activated for this display.
     * This is the widget that is receiving all user input.
     * 
     * @return activated widget
     */
    public MapWidget getActivatedWidget() {
        return this.widgets.getActivatedWidget();
    }

    /**
     * Gets the widget that is currently focused.
     * This is the widget that will be activated next, if ENTER is pressed.
     * 
     * @return focused widget
     */
    public MapWidget getFocusedWidget() {
        return this.widgets.getFocusedWidget();
    }

    /**
     * Internal use only
     * 
     * @return markers manager
     */
    MapDisplayMarkers getMarkerManager() {
        return markers;
    }

    /**
     * Gets all the map markers added to this display
     * 
     * @return unmodifiable collection of map markers
     */
    public Collection<MapMarker> getMarkers() {
        return markers.values();
    }

    /**
     * Creates a new marker and adds it to to the display.
     * It is assigned a randomly generated unique id.
     * 
     * @return marker
     */
    public MapMarker createMarker() {
        while (true) {
            String name = MapDisplayMarkers.RANDOM_NAME_SOURCE.nextHex();
            if (markers.get(name) == null) {
                return markers.add(new MapMarker(markers, name));
            }
        }
    }

    /**
     * Creates a new marker and adds it to the display.
     * The id specified will be assigned to the marker,
     * allowing it to be later retrieved again by the same id
     * using {@link #getMarker(id)}.<br>
     * <br>
     * If a marker with this id already exists, then that marker
     * is replaced with a new one that has the initial settings
     * of a newly created marker.
     * 
     * @param id The unique ID to assign to the marker
     * @return marker
     */
    public MapMarker createMarker(String id) {
        return markers.add(new MapMarker(markers, id));
    }

    /**
     * Retrieves the marker previously created by id. If
     * the marker by this id could not be found, null is returned.
     * 
     * @param id The unique ID of the marker to find
     * @return marker, null if no marker with this id exists
     */
    public MapMarker getMarker(String id) {
        return markers.get(id);
    }

    /**
     * Removes a marker from this display, returning the marker
     * that was removed if found.
     * 
     * @param id The unique ID of the marker to remove
     * @return marker that was removed, or null if no marker with
     *         this ID was found.
     */
    public MapMarker removeMarker(String id) {
        return markers.remove(id);
    }

    /**
     * Removes all map markers previously added to this display
     */
    public void clearMarkers() {
        markers.clear();
    }

    /**
     * Sets the master volume. All sounds played using {@link #playSound()} will be pre-multiplied with this factor.
     * 
     * @param masterVolume to set to, 1.0 for defaults
     */
    public void setMasterVolume(float masterVolume) {
        this._masterVolume = masterVolume;
    }

    /**
     * Gets the master volume currently applied using {@link #setMasterVolume(float)}.
     * 
     * @return master volume
     */
    public float getMasterVolume() {
        return this._masterVolume;
    }

    /**
     * Sets whether sounds played by this display are played to everyone viewing the map (true), or
     * only to players holding the map in their hands (false)
     * 
     * @param everyone
     */
    public void setPlaySoundToEveryone(boolean everyone) {
        this._playSoundToAllViewers = everyone;
    }

    /**
     * Convenience function for playing sounds to the viewers of this map display
     * 
     * @param soundKey of the sound to play
     */
    public void playSound(ResourceKey<SoundEffect> soundKey) {
        playSound(soundKey, 1.0f, 1.0f);
    }

    /**
     * Convenience function for playing sounds to the viewers of this map display
     * 
     * @param soundKey of the sound to play
     * @param volume of the sound
     * @param pitch of the sound
     */
    public void playSound(ResourceKey<SoundEffect> soundKey, float volume, float pitch) {
        for (Player viewer : this.getViewers()) {
            if (this._playSoundToAllViewers || this.isHolding(viewer)) {
                PlayerUtil.playSound(viewer, soundKey, this._masterVolume * volume, pitch);
            }
        }
    }

    /**
     * Sends a status change event without argument to this map display and all widgets attached to it.
     * First {@link #onStatusChanged(MapStatusEvent)} is called for all widgets, then for this display.
     * 
     * @param name of the status change
     */
    public final void sendStatusChange(String name) {
        this.sendStatusChange(name, null);
    }

    /**
     * Sends a status change event to this map display and all widgets attached to it.
     * First {@link #onStatusChanged(MapStatusEvent)} is called for all widgets, then for this display.
     * 
     * @param name of the status change
     * @param argument for the status change
     */
    public final void sendStatusChange(String name, Object argument) {
        MapStatusEvent event = new MapStatusEvent(name, argument);
        sendStatusChanges(this.widgets, event);
        this.onStatusChanged(event);
    }

    private static void sendStatusChanges(MapWidget from, MapStatusEvent event) {
        from.onStatusChanged(event);
        for (MapWidget child : from.getWidgets()) {
            sendStatusChanges(child, event);
        }
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
     * Gets the top-most layer, overlapping all other layers of the display.
     * 
     * @return top layer
     */
    public final Layer getTopLayer() {
        Layer top = this.layerStack;
        while (top.next != null) {
            top = top.next;
        }
        return top;
    }

    /**
     * Starts or stops this map display. When started, it will be initialized and {@link #onAttached()}
     * will be called. When stopped, the display session is terminated and {@link #onDetached()} is called.
     * 
     * @param running whether the map display is running
     */
    public final void setRunning(boolean running) {
        if (running) {
            if (this.plugin != null && this.updateTaskId == -1) {
                this.updateTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, this::update, 1, 1);
                CommonPlugin.getInstance().getMapController().getDisplays().add(getClass(), this);
                this.handleStartRunning();
            }
        } else {
            if (this.updateTaskId != -1) {
                this.handleStopRunning();

                // Clean up
                Bukkit.getScheduler().cancelTask(this.updateTaskId);
                this.updateTaskId = -1;
                CommonPlugin.getInstance().getMapController().getDisplays().remove(getClass(), this);
            }
        }
    }

    // Initializes the display state and calls onAttached() when done
    // Called from setRunning(true), and when restarting a display (resolution change)
    private void handleStartRunning() {
        if (this.info != null) {
            this.preRunInitialize();
            this.info.sessions.add(this.session);
        }

        this.session.initOwners();
        this.onAttached();
    }

    // Tears down the display state and calls onDetached() before
    // Called from setRunning(false), and when restarting a display (resolution change)
    private void handleStopRunning() {
        // Disable input interception for owners still lingering
        for (MapSession.Owner owner : this.session.onlineOwners) {
            owner.input.handleDisplayUpdate(this, false);
        }

        // Handle onDetached
        this.onDetached();
        this.widgets.clearWidgets();
        this.widgets.handleDetach();
        this.refreshMapItem();

        // Remove session
        if (this.info != null) {
            this.info.sessions.remove(this.session);
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
        private final int width, height;
        private final byte[] buffer;
        private final MapClip clip = new MapClip();

        private Layer(MapDisplay map, int width, int height) {
            this.width = width;
            this.height = height;
            this.buffer = new byte[this.width * this.height];
            this.map = map;
            this.z_index = 0;
        }

        @Override
        public final int getWidth() {
            return this.width;
        }

        @Override
        public final int getHeight() {
            return this.height;
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
                this.next = new Layer(this.map, this.width, this.height);
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
                this.previous = new Layer(this.map, this.width, this.height);
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
        public MapCanvas writePixels(int x, int y, int w, int h, byte[] colorData) {
            // TODO: This needs to be optimized!
            // We are dealing with a transparent depth buffer making this difficult
            // Look into this at some point to speed up drawing of images!
            return super.writePixels(x, y, w, h, colorData);
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
                if (w <= 0 || h <= 0) {
                    return this; // Out of bounds top/left
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
                    // If we are filling an area larger or equal to the current clip
                    // dirty area, we can reset the entire clip (it is all transparent)
                    // It might happen that this is done portion-by-portion, but there
                    // is no clean way to detect that without performance loss.
                    // This could be done by reading the buffer and checking if all pixels
                    // are set to 0, for example.
                    if (this.clip.isFullyEnclosedBy(x, y, w, h)) {
                        this.clip.clearDirty();
                    }
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
                    for (int idx = 0; idx < this.map.zbuffer.length; idx++) {
                        byte curr_z = this.map.zbuffer[idx];
                        if (curr_z == this.z_index) {
                            this.map.livebuffer[idx] = color;
                        } else if (curr_z < this.z_index) {
                            this.map.zbuffer[idx] = this.z_index;
                            this.map.livebuffer[idx] = color;
                        }
                    }
                } else {
                    for (int dy = 0; dy < h; dy++) {
                        int idx = (x + ((y + dy) * this.getWidth()));
                        int idx_end = (idx + w);
                        for (; idx < idx_end; idx++) {
                            byte curr_z = this.map.zbuffer[idx];
                            if (curr_z == this.z_index) {
                                this.map.livebuffer[idx] = color;
                            } else if (curr_z < this.z_index) {
                                this.map.zbuffer[idx] = this.z_index;
                                this.map.livebuffer[idx] = color;
                            }
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
                if (index >= 0 && index < buffer.length) {
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
                if (index >= 0 && index < buffer.length) {
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

            // If blend mode is none/overlay, don't read pixels from underlying layers
            if (this.getBlendMode() == MapBlendMode.NONE || this.getBlendMode() == MapBlendMode.OVERLAY) {
                return dst_buffer;
            }

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

        @Override
        public String toString() {
            return "{layer z=" + this.z_index + ",w=" + this.width + ",h=" + this.height + "} of " + this.map.toString();
        }
    }

    @Override
    public void onAttached() {}

    @Override
    public void onDetached() {}

    @Override
    public void onTick() {}

    @Override
    public void onKey(MapKeyEvent event) {}

    @Override
    public void onKeyPressed(MapKeyEvent event) {}

    @Override
    public void onKeyReleased(MapKeyEvent event) {}

    @Override
    public void onLeftClick(MapClickEvent event) {}

    @Override
    public void onRightClick(MapClickEvent event) {}

    @Override
    public void onMapItemChanged() {}

    @Override
    public void onStatusChanged(MapStatusEvent event) {};

    @Override
    public boolean onItemDrop(Player player, ItemStack item) { return false; }

    /**
     * Creates a new Map Display item that will automatically initialize a particular Map Display class
     * when viewed<br>
     * <br>
     * To store additional properties for the display, use
     * {@link MapDisplayProperties#createNew(Class)} instead.
     * 
     * @param mapDisplayClass from a Java Plugin (jar)
     * @return map item
     * @throws IllegalArgumentException If the map display class is not from a plugin, or lacks a no-args constructor
     * @throws UnsupportedOperationException If map displays are disabled in BKCommonLib's configuration
     */
    public static ItemStack createMapItem(Class<? extends MapDisplay> mapDisplayClass) {
        return MapDisplayProperties.createNew(mapDisplayClass).getMapItem();
    }

    /**
     * Creates a new Map Display item that will automatically initialize a particular Map Display class
     * when viewed.<br>
     * <br>
     * To store additional properties for the display, use
     * {@link MapDisplayProperties#createNew(Plugin, Class)} instead.
     * 
     * @param plugin owner of the display
     * @param mapDisplayClass
     * @return map item
     * @throws IllegalArgumentException If the map display class lacks a no-args constructor
     * @throws UnsupportedOperationException If map displays are disabled in BKCommonLib's configuration
     */
    public static ItemStack createMapItem(Plugin plugin, Class<? extends MapDisplay> mapDisplayClass) {
        return MapDisplayProperties.createNew(plugin, mapDisplayClass).getMapItem();
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

    /**
     * Gets the Map Display bound to a map item, viewed by the player. If the item is not
     * a map item, or the map item has no displays bound to it for the player, null is returned.
     * 
     * @param viewer of the map
     * @param item with map information
     * @return map display for the item viewed by the player, null if none is available
     */
    public static MapDisplay getViewedDisplay(Player viewer, ItemStack item) {
        MapDisplayInfo info = CommonPlugin.getInstance().getMapController().getInfo(item);
        return (info != null) ? info.getViewing(viewer) : null;
    }

    /**
     * Restarts (detaches and re-attaches) all map displays bound to a particular item.
     * This effectively resets the display, forcing a complete re-render and re-initialization.
     * 
     * @param mapItem
     */
    public static void restartDisplays(ItemStack mapItem) {
        MapDisplayInfo mapInfo = CommonPlugin.getInstance().getMapController().getInfo(mapItem);
        if (mapInfo != null) {
            for (MapSession session : new ArrayList<MapSession>(mapInfo.sessions)) {
                MapDisplay display = session.display;
                if (display.isRunning()) {
                    display.handleStopRunning();
                    display.handleStartRunning();
                }
            }
        }
    }

    /**
     * Terminates (detaches) all map displays bound to a particular item.
     * 
     * @param mapItem
     */
    public static void stopDisplays(ItemStack mapItem) {
        MapDisplayInfo mapInfo = CommonPlugin.getInstance().getMapController().getInfo(mapItem);
        if (mapInfo != null) {
            for (MapSession session : new ArrayList<MapSession>(mapInfo.sessions)) {
                session.display.setRunning(false);
            }
        }
    }

    /**
     * Terminates (detaches) all map displays registered by a certain plugin.
     * 
     * @param plugin owner of the displays
     */
    public static void stopDisplaysForPlugin(Plugin plugin) {
        // End all map display sessions for this plugin
        for (MapDisplayInfo map : new ArrayList<MapDisplayInfo>(CommonPlugin.getInstance().getMapController().getMaps())) {
            for (MapSession session : new ArrayList<MapSession>(map.sessions)) {
                if (session.display.getPlugin() == plugin) {
                    session.display.setRunning(false);
                }
            }
        }
    }

    /**
     * Gets all display instances of a certain type used on the server
     * 
     * @param displayClass type
     * @return collection of Map Displays of this type (or an extended type of-)
     */
    public static <T extends MapDisplay> Collection<T> getAllDisplays(Class<T> displayClass) {
        return CommonPlugin.getInstance().getMapController().getDisplays(displayClass);
    }

    /**
     * Gets all display instances currently displayed on the map inside an item frame.
     * This can be more than one if different displays are displayed for different players.
     * A guarantee is made that no duplicate instances shall be returned.
     * 
     * @param itemFrame
     * @return collection of Map Displays displayed
     */
    public static Collection<MapDisplay> getAllDisplays(ItemFrame itemFrame) {
        return getAllDisplaysFromInfo(CommonPlugin.getInstance().getMapController().getInfo(itemFrame));
    }

    /**
     * Gets all display instances currently displayed for an item in a player's hand or
     * seated in an item frame. This can be more than one if different displays
     * are displayed for different players. A guarantee is made that no duplicate
     * instances shall be returned.
     * 
     * @param item
     * @return collection of Map Displays displayed
     */
    public static Collection<MapDisplay> getAllDisplays(ItemStack item) {
        return getAllDisplaysFromInfo(CommonPlugin.getInstance().getMapController().getInfo(item));
    }

    private static Collection<MapDisplay> getAllDisplaysFromInfo(MapDisplayInfo info) {
        if (info != null) {
            HashSet<MapDisplay> uniqueDisplays = new HashSet<MapDisplay>(info.sessions.size());
            for (MapSession session : info.sessions) {
                if (session.display != null) {
                    uniqueDisplays.add(session.display);
                }
            }
            return uniqueDisplays;
        }
        return Collections.emptySet();
    }

    /**
     * Gets the map display a player is viewing in a map held in his hand. When viewing a map display
     * in both hands, the main hand display is returned. When the player is not viewing any display,
     * null is returned instead.
     * 
     * @param viewer
     * @return held map display, null if not holding any map with a display
     */
    public static MapDisplay getHeldDisplay(Player viewer) {
        MapDisplay mainDisplay = getViewedDisplay(viewer, HumanHand.getItemInMainHand(viewer));
        if (mainDisplay != null) {
            return mainDisplay;
        }
        if (CommonCapabilities.PLAYER_OFF_HAND) {
            MapDisplay offDisplay = getViewedDisplay(viewer, HumanHand.getItemInOffHand(viewer));
            if (offDisplay != null) {
                return offDisplay;
            }
        }
        return null;
    }

    /**
     * Gets the map display a player is viewing in a map held in his hand. When viewing a map display
     * in both hands, the main hand display is returned. When the player is not viewing any display,
     * null is returned instead. This method allows specifying a restriction for what map displays
     * to return. When viewing two maps in both hands, and the off hand is the display class type, this method
     * allows returning that display.
     * 
     * @param viewer
     * @param displayClass
     * @return held map display, null if not holding any map with a display
     */
    @SuppressWarnings("unchecked")
    public static <T extends MapDisplay> T getHeldDisplay(Player viewer, Class<T> displayClass) {
        MapDisplay mainDisplay = getViewedDisplay(viewer, HumanHand.getItemInMainHand(viewer));
        if (mainDisplay != null && displayClass.isAssignableFrom(mainDisplay.getClass())) {
            return (T) mainDisplay;
        }
        if (CommonCapabilities.PLAYER_OFF_HAND) {
            MapDisplay offDisplay = getViewedDisplay(viewer, HumanHand.getItemInOffHand(viewer));
            if (offDisplay != null && displayClass.isAssignableFrom(offDisplay.getClass())) {
                return (T) offDisplay;
            }
        }
        return null;
    }
}
