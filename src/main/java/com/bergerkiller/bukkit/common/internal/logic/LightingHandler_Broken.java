package com.bergerkiller.bukkit.common.internal.logic;

import java.util.concurrent.CompletableFuture;

import org.bukkit.World;

public class LightingHandler_Broken extends LightingHandler {

    @Override
    public CompletableFuture<Void> setSectionSkyLightAsync(World world, int cx, int cy, int cz, byte[] data) {
        throw new UnsupportedOperationException("Server not supported, initialization failed");
    }

    @Override
    public CompletableFuture<Void> setSectionBlockLightAsync(World world, int cx, int cy, int cz, byte[] data) {
        throw new UnsupportedOperationException("Server not supported, initialization failed");
    }
}
