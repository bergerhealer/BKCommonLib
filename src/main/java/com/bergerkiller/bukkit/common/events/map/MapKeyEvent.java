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
    private final MapDisplay _map;
    private final MapPlayerInput.Key _key;
    private final MapPlayerInput _input;

    public MapKeyEvent(MapDisplay map, MapPlayerInput input,  MapPlayerInput.Key key) {
        this._input = input;
        this._key = key;
        this._map = map;
    }

    /**
     * Gets the player that caused this key event
     * 
     * @return player
     */
    public Player getPlayer() {
        return this._input.player;
    }

    /**
     * Gets the map player input manager responsible for this event
     * 
     * @return input
     */
    public MapPlayerInput getInput() {
        return this._input;
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

    /**
     * Gets the amount of ticks the key has been held down
     * 
     * @return repeat counter
     */
    public int getRepeat() {
        return this._input.getRepeat();
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
