package com.bergerkiller.bukkit.common.internal.logic;

import java.util.concurrent.CompletableFuture;

import org.bukkit.World;

import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;

/**
 * Selects the most appropriate lighting handler for a world, then
 * forwards calls to that handler.
 */
public final class LightingHandlerSelector extends LightingHandler {
    private final LightingHandler fallback;
    private final LightingHandler cubicchunks;
    private final LightingHandler starlight;

    public LightingHandlerSelector() {
        // Vanilla fallback
        if (CommonBootstrap.evaluateMCVersion(">=", "1.14")) {
            fallback = LogicUtil.tryCreate(LightingHandler_1_14::new, LightingHandlerDisabled::new);
        } else {
            fallback = LogicUtil.tryCreate(LightingHandler_1_8_to_1_13_2::new, LightingHandlerDisabled::new);
        }

        // Cubic chunks existence check, then initialize
        if (CommonUtil.getClass("io.github.opencubicchunks.cubicchunks.api.world.ICube") != null) {
            cubicchunks = LogicUtil.tryCreate(LightingHandler_CubicChunks_1_12_2::new, LightingHandlerDisabled::new);
        } else {
            cubicchunks = new LightingHandlerDisabled(null);
        }

        // Tuinity StarLight engine check, then initialize
        if (CommonUtil.getClass("com.tuinity.tuinity.chunk.light.ThreadedStarLightEngine") != null) {
            starlight = new LightingHandler_1_16_4_StarLightEngine();
        } else {
            starlight = new LightingHandlerDisabled(null);
        }
    }

    @Override
    public boolean isSupported(World world) {
        return getHandler(world).isSupported(world);
    }

    private LightingHandler getHandler(World world) {
        if (cubicchunks.isSupported(world)) {
            return cubicchunks;
        } else if (starlight.isSupported(world)) {
            return starlight;
        } else {
            return fallback;
        }
    }

    @Override
    public byte[] getSectionSkyLight(World world, int cx, int cy, int cz) {
        return getHandler(world).getSectionSkyLight(world, cx, cy, cz);
    }

    @Override
    public byte[] getSectionBlockLight(World world, int cx, int cy, int cz) {
        return getHandler(world).getSectionBlockLight(world, cx, cy, cz);
    }

    @Override
    public CompletableFuture<Void> setSectionSkyLightAsync(World world, int cx, int cy, int cz, byte[] data) {
        return getHandler(world).setSectionSkyLightAsync(world, cx, cy, cz, data);
    }

    @Override
    public CompletableFuture<Void> setSectionBlockLightAsync(World world, int cx, int cy, int cz, byte[] data) {
        return getHandler(world).setSectionBlockLightAsync(world, cx, cy, cz, data);
    }
}
