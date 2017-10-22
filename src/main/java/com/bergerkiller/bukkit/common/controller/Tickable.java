package com.bergerkiller.bukkit.common.controller;

/**
 * Something that can be updated in ticks repeatedly
 */
public interface Tickable {

    /**
     * Updates the object state as part of a single tick (1/20th second)
     */
    void onTick();
}
