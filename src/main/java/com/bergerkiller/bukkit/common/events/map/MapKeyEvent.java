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
    private final Player _player;
    private final int _repeat;

    public MapKeyEvent(MapDisplay map, MapPlayerInput input,  MapPlayerInput.Key key) {
        if (input == null) {
            throw new IllegalArgumentException("Input can not be null");
        }
        this._map = map;
        this._key = key;
        this._input = input;
        this._player = input.player;
        this._repeat = input.getRepeat();
    }

    /**
     * Creates a key event without requiring a {@link MapPlayerInput} instance.
     * <p>
     * Note: this constructor is intended for tests. In normal runtime usage,
     * key events are created with a non-null {@link MapPlayerInput}.
     *
     * @param map Map display
     * @param player Player responsible for the key event
     * @param key Key that changed
     * @param repeat Repeat counter, where 0 is first press
     */
    private MapKeyEvent(MapDisplay map, Player player, MapPlayerInput.Key key, int repeat) {
        this._map = map;
        this._key = key;
        this._input = null;
        this._player = player;
        this._repeat = repeat;
    }

    /**
     * Creates a test key event without requiring a {@link MapPlayerInput} instance.
     *
     * @param map Map display
     * @param player Player responsible for the key event
     * @param key Key that changed
     * @param repeat Repeat counter, where 0 is first press
     * @return test key event
     */
    public static MapKeyEvent createTestEvent(MapDisplay map, Player player, MapPlayerInput.Key key, int repeat) {
        return new MapKeyEvent(map, player, key, repeat);
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
        return this._repeat;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
