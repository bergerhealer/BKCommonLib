package com.bergerkiller.bukkit.common.events.map;

import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.map.binding.ItemFrameInfo;
import com.bergerkiller.bukkit.common.map.util.IMapLookPosition;
import com.bergerkiller.bukkit.common.map.util.MapLookPosition;

/**
 * Player left-clicked or right-clicked somewhere on a map in an {@link ItemFrame} managed by a {@link MapDisplay}.
 * Cancelling this event will cancel any underlying handling of the click, such as removing the item,
 * rotating the item, or placing a new item.
 */
public class MapClickEvent extends Event implements Cancellable, IMapLookPosition {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final MapLookPosition position;
    private final MapDisplay display;
    private final MapAction action;
    private boolean cancelled;

    public MapClickEvent(Player player, MapLookPosition position, MapDisplay display, MapAction action) {
        this.player = player;
        this.position = position;
        this.display = display;
        this.action = action;
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

    @Override
    public ItemFrameInfo getItemFrameInfo() {
        return this.position.getItemFrameInfo();
    }

    /**
     * Gets the ItemFrame that was clicked
     * 
     * @return item frame
     */
    @Override
    public ItemFrame getItemFrame() {
        return this.position.getItemFrame();
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
    @Override
    public int getX() {
        return this.position.getX();
    }

    /**
     * Gets the y-coordinate of the pixel that the player clicked
     * 
     * @return clicked y-coordinate
     */
    @Override
    public int getY() {
        return this.position.getY();
    }

    /**
     * Gets the x-coordinate of the pixel that the player clicked
     * with floating point (sub-pixel) precision.
     * 
     * @return clicked x-coordinate
     */
    @Override
    public double getDoubleX() {
        return this.position.getDoubleX();
    }

    /**
     * Gets the y-coordinate of the pixel that the player clicked
     * with floating point (sub-pixel) precision.
     * 
     * @return clicked y-coordinate
     */
    @Override
    public double getDoubleY() {
        return this.position.getDoubleY();
    }

    @Override
    public double getDistance() {
        return this.position.getDistance();
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
