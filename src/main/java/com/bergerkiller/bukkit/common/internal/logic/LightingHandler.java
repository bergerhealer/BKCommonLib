package com.bergerkiller.bukkit.common.internal.logic;

import java.util.concurrent.CompletableFuture;

import org.bukkit.World;

public abstract class LightingHandler {
    public static final LightingHandler INSTANCE = new LightingHandlerSelector();

    /**
     * Gets whether this lighting handler can support operations on the world specified.
     * 
     * @param world
     * @return True if the world is supported
     */
    public abstract boolean isSupported(World world);

    /**
     * Gets the sky light values for a single 16x16x16 section of blocks
     * 
     * @param world World
     * @param cx Chunk Section X-coordinate
     * @param cy Chunk Section Y-coordinate
     * @param cz Chunk Section Z-coordinate
     * @return 4096-size array of sky lighting data
     */
    public abstract byte[] getSectionSkyLight(World world, int cx, int cy, int cz);

    /**
     * Gets the block light values for a single 16x16x16 section of blocks
     * 
     * @param world World
     * @param cx Chunk Section X-coordinate
     * @param cy Chunk Section Y-coordinate
     * @param cz Chunk Section Z-coordinate
     * @return 4096-size array of block lighting data
     */
    public abstract byte[] getSectionBlockLight(World world, int cx, int cy, int cz);

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
    public abstract CompletableFuture<Void> setSectionSkyLightAsync(World world, int cx, int cy, int cz, byte[] data);

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
    public abstract CompletableFuture<Void> setSectionBlockLightAsync(World world, int cx, int cy, int cz, byte[] data);
}
