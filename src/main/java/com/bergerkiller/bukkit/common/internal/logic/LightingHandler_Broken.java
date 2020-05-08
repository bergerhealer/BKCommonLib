package com.bergerkiller.bukkit.common.internal.logic;

import java.util.concurrent.CompletableFuture;

import org.bukkit.World;

public class LightingHandler_Broken extends LightingHandler {

    @Override
    public CompletableFuture<Void> setSectionSkyLightAsync(World world, int cx, int cy, int cz, byte[] data) {
        throw new UnsupportedOperationException("Failed to initialize lighting handler, BKCommonLib does not support this server");
    }

    @Override
    public CompletableFuture<Void> setSectionBlockLightAsync(World world, int cx, int cy, int cz, byte[] data) {
        throw new UnsupportedOperationException("Failed to initialize lighting handler, BKCommonLib does not support this server");
    }
}
