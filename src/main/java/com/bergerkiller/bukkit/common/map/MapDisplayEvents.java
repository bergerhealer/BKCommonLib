package com.bergerkiller.bukkit.common.map;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

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

    /**
     * Callback function called when a player holding this display in either hand drops an item from
     * the inventory onto the display.<br>
     * <br>
     * Return true from this callback to cancel the item drop, indicating the drop
     * was handled by the display. By default the callback should return false, allowing the drop
     * to occur like normal.
     * 
     * @param player The player that dropped the item
     * @param item ItemStack of the item dropped by the player
     * @return True if the drop was handled and the item drop should be cancelled
     */
    public boolean onItemDrop(Player player, ItemStack item);

    /**
     * Callback function called when a player holding this display in either hand clicks on a block
     * in the world. This can be useful when selecting block coordinates in the current
     * menu.<br>
     * <br>
     * Callers can cancel the event by using {@link PlayerInteractEvent#setUseInteractedBlock(Event.Result)},
     * indicating the interaction was handled by the display. By default the callback should not touch
     * the event<br>
     * <br>
     * Important note: this will be called also if the original event was cancelled. If this is
     * important, use {@link PlayerInteractEvent#useInteractedBlock()} to check whether interaction
     * was cancelled by another plugin.
     *
     * @param event Player interaction event associated with this click
     */
    public void onBlockInteract(PlayerInteractEvent event);
}
