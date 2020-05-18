package com.bergerkiller.bukkit.common.internal.logic;

import java.util.concurrent.CompletableFuture;

import org.bukkit.World;

public class LightingHandler_Broken extends LightingHandler {

    private UnsupportedOperationException fail() {
        return new UnsupportedOperationException("Failed to initialize lighting handler, BKCommonLib does not support this server");
    }

    @Override
    public CompletableFuture<Void> setSectionSkyLightAsync(World world, int cx, int cy, int cz, byte[] data) {
        throw fail();
    }

    @Override
    public CompletableFuture<Void> setSectionBlockLightAsync(World world, int cx, int cy, int cz, byte[] data) {
        throw fail();
    }

    @Override
    public byte[] getSectionSkyLight(World world, int cx, int cy, int cz) {
        throw fail();
    }

    @Override
    public byte[] getSectionBlockLight(World world, int cx, int cy, int cz) {
        throw fail();
    }
}
