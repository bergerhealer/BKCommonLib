package com.bergerkiller.bukkit.common.map;

import com.bergerkiller.bukkit.common.controller.Tickable;
import com.bergerkiller.bukkit.common.events.map.MapClickEvent;
import com.bergerkiller.bukkit.common.events.map.MapKeyEvent;
import com.bergerkiller.bukkit.common.events.map.MapStatusEvent;

/**
 * An interface of all possible event callback methods provided by Map Displays and Map Display Controls.
 */
public interface MapDisplayEvents extends Tickable {
    /**
     * Called right after this Map Display is bound to a plugin and map
     */
    public void onAttached();

    /**
     * Called right before the map display is removed after the session ends
     */
    public void onDetached();

    /**
     * Fired every tick to update this Virtual Map.
     * This method can be overridden to dynamically update the map continuously.
     * To optimize performance, only draw things in the map when they change.
     */
    public void onTick();

    /**
     * Callback function called every tick while a key is pressed down.
     * These callbacks are called before {@link #onTick()}.
     * 
     * @param event
     */
    public void onKey(MapKeyEvent event);

    /**
     * Callback function called when a key changed from not-pressed to pressed down
     * These callbacks are called before {@link #onTick()}.
     * 
     * @param event
     */
    public void onKeyPressed(MapKeyEvent event);

    /**
     * Callback function called when a key changed from pressed to not pressed down
     * These callbacks are called before {@link #onTick()}.
     * 
     * @param event
     */
    public void onKeyReleased(MapKeyEvent event);

    /**
     * Callback function called when a player left-clicks the map held in an item frame
     * showing this Map Display.
     * 
     * @param event
     */
    public void onLeftClick(MapClickEvent event);

    /**
     * Callback function called when a player right-clicks the map held in an item frame
     * showing this Map Display.
     * 
     * @param event
     */
    public void onRightClick(MapClickEvent event);

    /**
     * Callback function called when the map item of this Map Display changed
     */
    public void onMapItemChanged();

    /**
     * Callback function called when a widget in the map display wishes to broadcast
     * a status change. These status changes can be used to refresh the map or apply
     * updated configurations.
     * 
     * @param event of the status change
     */
    public void onStatusChanged(MapStatusEvent event);
}
