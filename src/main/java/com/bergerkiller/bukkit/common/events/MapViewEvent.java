package com.bergerkiller.bukkit.common.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.common.map.InteractiveMapDisplay;

/**
 * Event fired when a player opens a map in his inventory. This occurs when:
 * <ul>
 * <li>A player activates an unopened map item</li>
 * <li>A player switches selected item slots to hold a map</li>
 * <li>A player joins the server while holding a map item</li>
 * </ul>
 * This event can be used to to attach an
 * {@link com.bergerkiller.bukkit.common.map.InteractiveMapDisplay InteractiveMapDisplay}
 * to the player.
 */
public class MapViewEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    private final ItemStack item;
    private final MainHand hand;

    public MapViewEvent(Player player, MainHand hand, ItemStack item) {
        super(player);
        this.hand = hand;
        this.item = item;
    }

    /**
     * Gets the ItemStack associated with the map being shown
     * 
     * @return map item
     */
    public ItemStack getMapItem() {
        return this.item;
    }

    /**
     * Gets the hand with which the player is holding this map
     * 
     * @return map-holding hand
     */
    public MainHand getHand() {
        return this.hand;
    }

    /**
     * Obtains the interactive map display that is currently attached to this Map.
     * If no display is attached, null is returned.
     * 
     * @return Interactive map display, null if none is attached
     */
    public InteractiveMapDisplay getDisplay() {
        return InteractiveMapDisplay.findMap(this.player, this.item);
    }

    /**
     * Checks whether an interactive map display is currently attached to this Map.
     * 
     * @return True if a display is attached, False if not
     */
    public boolean hasDisplay() {
        return getDisplay() != null;
    }

    /**
     * Attaches an interactive map display to the Map this event represents.
     * The plugin owner of the display must be specified. A single interactive map
     * display can only be attached once and will last for as long the player is online.
     * 
     * @param plugin owner of the interactive map display
     * @param display to attach
     */
    public void setDisplay(JavaPlugin plugin, InteractiveMapDisplay display) {
        display.start(plugin, this.player, this.item);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
