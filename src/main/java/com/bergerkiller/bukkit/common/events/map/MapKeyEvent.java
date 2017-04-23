package com.bergerkiller.bukkit.common.events.map;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.map.MapPlayerInput;

/**
 * Player changed the state of a key while controlling a {@link MapDisplay}
 */
public class MapKeyEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player _player;
    private final MapDisplay _map;
    private final MapPlayerInput.Key _key;

    public MapKeyEvent(MapDisplay map, Player player, MapPlayerInput.Key key) {
        this._player = player;
        this._key = key;
        this._map = map;
    }

    /**
     * Gets the player that caused this key event
     * 
     * @return player
     */
    public Player getPlayer() {
        return this._player;
    }

    /**
     * Gets the Map Display for which this key event is for
     * 
     * @return map display
     */
    public MapDisplay getMapDisplay() {
        return this._map;
    }

    /**
     * Gets the key that changed
     * 
     * @return
     */
    public MapPlayerInput.Key getKey() {
        return this._key;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
