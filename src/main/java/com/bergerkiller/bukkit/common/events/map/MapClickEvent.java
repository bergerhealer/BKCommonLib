package com.bergerkiller.bukkit.common.events.map;

import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.utils.MathUtil;

/**
 * Player left-clicked or right-clicked somewhere on a map in an {@link ItemFrame} managed by a {@link MapDisplay}.
 * Cancelling this event will cancel any underlying handling of the click, such as removing the item,
 * rotating the item, or placing a new item.
 */
public class MapClickEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final ItemFrame itemFrame;
    private final MapDisplay display;
    private final MapAction action;
    private final double px;
    private final double py;
    private boolean cancelled;

    public MapClickEvent(Player player, ItemFrame itemFrame, MapDisplay display, MapAction action, int px, int py) {
        this(player, itemFrame, display, action, (double) px, (double) py);
    }

    public MapClickEvent(Player player, ItemFrame itemFrame, MapDisplay display, MapAction action, double px, double py) {
        this.player = player;
        this.itemFrame = itemFrame;
        this.display = display;
        this.action = action;
        this.px = px;
        this.py = py;
        this.cancelled = false;
    }

    /**
     * Gets the player that clicked the map
     * 
     * @return player
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Gets the {@link MapDisplay} that was clicked
     * 
     * @return map display
     */
    public MapDisplay getDisplay() {
        return this.display;
    }

    /**
     * Gets the ItemFrame that was clicked
     * 
     * @return item frame
     */
    public ItemFrame getItemFrame() {
        return this.itemFrame;
    }

    /**
     * Gets the type of click action
     * 
     * @return map click action
     */
    public MapAction getAction() {
        return this.action;
    }

    /**
     * Gets the x-coordinate of the pixel that the player clicked
     * 
     * @return clicked x-coordinate
     */
    public int getX() {
        return MathUtil.floor(this.px);
    }

    /**
     * Gets the y-coordinate of the pixel that the player clicked
     * 
     * @return clicked y-coordinate
     */
    public int getY() {
        return MathUtil.floor(this.py);
    }

    /**
     * Gets the x-coordinate of the pixel that the player clicked
     * with floating point (sub-pixel) precision.
     * 
     * @return clicked x-coordinate
     */
    public double getDoubleX() {
        return this.px;
    }

    /**
     * Gets the y-coordinate of the pixel that the player clicked
     * with floating point (sub-pixel) precision.
     * 
     * @return clicked y-coordinate
     */
    public double getDoubleY() {
        return this.py;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
