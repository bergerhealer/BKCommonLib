package com.bergerkiller.bukkit.common.events;

import org.bukkit.Chunk;
import org.bukkit.event.HandlerList;
import org.bukkit.event.world.ChunkEvent;

/**
 * Event fired by BKCommonLib right after the entities of a chunk have
 * been unloaded. The chunk itself may remain loaded.
 */
public class ChunkUnloadEntitiesEvent extends ChunkEvent {
    private static final HandlerList handlers = new HandlerList();

    public ChunkUnloadEntitiesEvent(Chunk chunk) {
        super(chunk);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
