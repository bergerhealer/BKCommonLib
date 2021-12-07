package com.bergerkiller.bukkit.common.internal.logic;

import java.util.concurrent.CompletableFuture;

import org.bukkit.World;

import com.bergerkiller.bukkit.common.component.LibraryComponentSelector;
import com.bergerkiller.bukkit.common.lighting.LightingHandler;
import com.bergerkiller.bukkit.common.utils.CommonUtil;

/**
 * Selects the most appropriate lighting handler for a world, then
 * forwards calls to that handler.
 */
public final class LightingHandlerSelector implements LightingHandler {
    public static final LightingHandlerSelector INSTANCE = new LightingHandlerSelector();

    private final LightingHandler fallback;
    private final LightingHandler cubicchunks;
    private final LightingHandler starlight;

    public LightingHandlerSelector() {
        // Vanilla fallback
        this.fallback = LibraryComponentSelector.forModule(LightingHandler.class)
                .setDefaultComponent(LightingHandlerDisabled::new)
                .addVersionOption(null, "1.13.2", LightingHandler_1_8_to_1_13_2::new)
                .addVersionOption("1.14", null, LightingHandler_1_14::new)
                .update();

        // Cubic chunks
        this.cubicchunks = LibraryComponentSelector.forModule(LightingHandler.class)
                .setDefaultComponent(LightingHandlerDisabled::new)
                .addOption(new Conditional<Void, LightingHandler>() {
                    @Override
                    public String getIdentifier() {
                        return "CubicChunks Handler";
                    }

                    @Override
                    public boolean isSupported(Void environment) {
                        return CommonUtil.getClass("io.github.opencubicchunks.cubicchunks.api.world.ICube") != null;
                    }

                    @Override
                    public LightingHandler create(Void environment) throws Throwable {
                        return new LightingHandler_CubicChunks_1_12_2();
                    }
                })
                .update();

        // Tuinity StarLight engine check, then initialize
        this.starlight = LibraryComponentSelector.forModule(LightingHandler.class)
                .setDefaultComponent(LightingHandlerDisabled::new)
                .addOption(new Conditional<Void, LightingHandler>() {
                    @Override
                    public String getIdentifier() {
                        return "StarLight Engine Handler";
                    }

                    @Override
                    public boolean isSupported(Void environment) {
                        return CommonUtil.getClass("ca.spottedleaf.starlight.common.light.StarLightEngine") != null;
                    }

                    @Override
                    public LightingHandler create(Void environment) throws Throwable {
                        return new LightingHandler_1_16_4_StarLightEngine();
                    }
                })
                .update();
    }

    public boolean isFallbackInitialized() {
        return !(this.fallback instanceof LightingHandlerDisabled);
    }

    @Override
    public void enable() {
    }

    @Override
    public void disable() {
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
