package com.bergerkiller.bukkit.common.map;

/**
 * What conditions must be met to exit a Map Display Session
 */
public enum MapSessionMode {
    /**
     * The map session is kept around forever, even after all owners go offline
     */
    FOREVER,
    /**
     * The map session is kept around for as long as the player is still online.
     * Once the player logs off, he is removed. If he was the last owner of
     * the map display, the map display session is ended.
     */
    ONLINE,
    /**
     * The map session is kept around for as long as the player is viewing it.
     * Once the player stops viewing, he is removed. If he was the last owner of
     * the map display, the map display session is ended.
     */
    VIEWING,
    /**
     * The map session is kept around for as long as the player is holding the map.
     * Once the player stops holding the map, he is removed. If he was the last owner of
     * the map display, the map display session is ended.
     */
    HOLDING
}
