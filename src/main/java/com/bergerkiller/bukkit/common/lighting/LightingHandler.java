package com.bergerkiller.bukkit.common.lighting;

import java.util.concurrent.CompletableFuture;

import org.bukkit.World;

import com.bergerkiller.bukkit.common.internal.logic.LightingHandlerSelector;

/**
 * Interfaces with server internals to retrieve and apply block and sky lighting
 * information to 16x16x16 sections of chunks. Reading can be done blocking/synchronously,
 * while writing is done asynchronously.
 */
public interface LightingHandler {

    /**
     * Gets whether this lighting handler can support operations on the world specified.
     * 
     * @param world
     * @return True if the world is supported
     */
    boolean isSupported(World world);

    /**
     * Gets the sky light values for a single 16x16x16 section of blocks
     * 
     * @param world World
     * @param cx Chunk Section X-coordinate
     * @param cy Chunk Section Y-coordinate
     * @param cz Chunk Section Z-coordinate
     * @return 4096-size array of sky lighting data
     */
    byte[] getSectionSkyLight(World world, int cx, int cy, int cz);

    /**
     * Gets the block light values for a single 16x16x16 section of blocks
     * 
     * @param world World
     * @param cx Chunk Section X-coordinate
     * @param cy Chunk Section Y-coordinate
     * @param cz Chunk Section Z-coordinate
     * @return 4096-size array of block lighting data
     */
    byte[] getSectionBlockLight(World world, int cx, int cy, int cz);

    /**
     * Sets the sky light values for a single 16x16x16 section of blocks asynchronously
     * 
     * @param world World
     * @param cx Chunk Section X-coordinate
     * @param cy Chunk Section Y-coordinate
     * @param cz Chunk Section Z-coordinate
     * @param data
     * @return completable future completed when the light has been updated
     */
    CompletableFuture<Void> setSectionSkyLightAsync(World world, int cx, int cy, int cz, byte[] data);

    /**
     * Sets the block light values for a single 16x16x16 section of blocks asynchronously
     * 
     * @param world World
     * @param cx Chunk Section X-coordinate
     * @param cy Chunk Section Y-coordinate
     * @param cz Chunk Section Z-coordinate
     * @param data
     * @return completable future completed when the light has been updated
     */
    CompletableFuture<Void> setSectionBlockLightAsync(World world, int cx, int cy, int cz, byte[] data);

    /**
     * Gets the BKCommonLib LightingHandler API interface
     * 
     * @return lighting handler interface
     */
    public static LightingHandler instance() {
        return LightingHandlerSelector.INSTANCE;
    }
}
