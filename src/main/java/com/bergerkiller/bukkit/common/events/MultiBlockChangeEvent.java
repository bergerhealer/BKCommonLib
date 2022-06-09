package com.bergerkiller.bukkit.common.events;

import java.util.Set;

import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.event.world.WorldEvent;

import com.bergerkiller.bukkit.common.bases.IntVector2;

/**
 * Event fired after a large amount of block changes were performed
 * in one or more chunks. This is called when events like WorldEdit make
 * changes to the world.<br>
 * <br>
 * It can be used to keep tracked block information in sync after plugins
 * modify the world out of band.
 */
public class MultiBlockChangeEvent extends WorldEvent {
    private static final HandlerList handlers = new HandlerList();
    private final Set<IntVector2> chunkCoordinates;

    public MultiBlockChangeEvent(World world, Set<IntVector2> chunkCoordinates) {
        super(world);
        this.chunkCoordinates = chunkCoordinates;
    }

    /**
     * Gets the chunk coordinates of the chunks that were affected by the
     * block changes.
     *
     * @return Chunk coordinates containing the blocks that changed
     */
    public Set<IntVector2> getChunkCoordinates() {
        return this.chunkCoordinates;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
