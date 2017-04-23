package com.bergerkiller.bukkit.common.events.map;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * A generic event for representing a Map Display and a Player
 */
public abstract class PlayerMapEvent extends Event {
    private final Player player;

    public PlayerMapEvent(Player player) {
        this.player = player;
    }

    /**
     * Gets the Player that is holding the map
     * 
     * @return player holder
     */
    public Player getPlayer() {
        return this.player;
    }

    
}
