package com.bergerkiller.bukkit.common.internal.logic;

import java.util.concurrent.CompletableFuture;

import org.bukkit.World;

import com.bergerkiller.bukkit.common.Common;

public abstract class LightingHandler {
    public static final LightingHandler INSTANCE = createInstance();

    private static LightingHandler createInstance() {
        try {
            if (Common.evaluateMCVersion(">=", "1.14")) {
                return new LightingHandler_1_14();
            } else {
                return new LightingHandler_1_8_to_1_13_2();
            }
        } catch (Throwable t) {
            t.printStackTrace();
            return new LightingHandler_Broken();
        }
    }

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
     */
    public abstract CompletableFuture<Void> setSectionBlockLightAsync(World world, int cx, int cy, int cz, byte[] data);
}
