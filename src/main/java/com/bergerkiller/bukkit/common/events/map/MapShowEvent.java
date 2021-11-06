package com.bergerkiller.bukkit.common.events.map;

import java.util.UUID;

import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.internal.map.CommonMapController;
import com.bergerkiller.bukkit.common.internal.map.CommonMapUUIDStore;
import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.map.binding.MapDisplayInfo;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;

/**
 * Event fired when a map becomes shown to a player somewhere. This can be in his own
 * hands, or on an item frame in the world. This event is fired multiple times, whenever
 * a player sees a (new) item frame or opens the map in his hands.
 */
public class MapShowEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final ItemStack mapItem;
    private final HumanHand hand;
    private final ItemFrame itemFrame;

    public MapShowEvent(Player player, HumanHand hand, ItemStack mapItem) {
        this.player = player;
        this.mapItem = mapItem;
        this.hand = hand;
        this.itemFrame = null;
    }

    public MapShowEvent(Player player, ItemFrame itemFrame) {
        this.player = player;
        this.mapItem = CommonMapController.getItemFrameItem(itemFrame);
        this.itemFrame = itemFrame;
        this.hand = null;
    }

    /**
     * Gets whether this event was caused by a player opening the map in one of his hands
     * 
     * @return True if the player held a map, False if not
     */
    public boolean isHeldEvent() {
        return this.hand != null;
    }

    /**
     * Gets whether this event was caused by a player moving in range of an Item Frame containing
     * a map.
     * 
     * @return True if the player saw a map on an item frame, False if not
     */
    public boolean isItemFrameEvent() {
        return this.itemFrame != null;
    }

    /**
     * Gets the Player that is holding the map
     * 
     * @return player holder
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Gets the ItemStack associated with the map being shown
     * 
     * @return map item
     */
    public ItemStack getMapItem() {
        return this.mapItem;
    }

    /**
     * Gets the unique Id of the map item
     * 
     * @return map unique id
     */
    public UUID getMapUUID() {
        return CommonMapUUIDStore.getMapUUID(this.mapItem);
    }

    /**
     * Gets the hand with which the player is holding the map. If this event
     * does not represent an {@link #isHeldEvent()}, this function returns null.
     * 
     * @return hand with which the player is holding the map
     */
    public HumanHand getHand() {
        return this.hand;
    }

    /**
     * Gets the item frame that the player started seeing a map in. If this event
     * does not represent an {@link #isItemFrameEvent()}, this function returns null.
     * 
     * @return item frame showing the map
     */
    public ItemFrame getItemFrame() {
        return this.itemFrame;
    }

    /**
     * Gets the Map Display currently visible to the Player for this map
     * 
     * @return map display
     */
    public MapDisplay getDisplay() {
        MapDisplayInfo info = CommonPlugin.getInstance().getMapController().getInfo(this.mapItem);
        return (info == null) ? null : info.getViewing(this.player);
    }

    /**
     * Checks whether a Map Display is set for this map and visible to this Player
     * 
     * @return True if a display is set, False if not
     */
    public boolean hasDisplay() {
        return getDisplay() != null;
    }

    /**
     * Sets a new display that will take over Map Display for this Player
     * 
     * @param plugin owner of the display
     * @param display to set to
     */
    public void setDisplay(JavaPlugin plugin, MapDisplay display) {
        display.addOwner(this.player);
        CommonMapController.MAP_DISPLAY_INIT_FUNC.initialize(display, plugin, this.mapItem);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
