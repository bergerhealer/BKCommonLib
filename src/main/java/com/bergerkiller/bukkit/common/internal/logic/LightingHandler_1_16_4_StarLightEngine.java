package com.bergerkiller.bukkit.common.internal.logic;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

import org.bukkit.Chunk;
import org.bukkit.World;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.lighting.LightingHandler;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.generated.net.minecraft.server.level.ThreadedLevelLightEngineHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Interfaces with the Tuinity server 1.16.4 starlight engine
 */
class LightingHandler_1_16_4_StarLightEngine implements LightingHandler {
    private StarLightEngineHandle handle = Template.Class.create(StarLightEngineHandle.class, Common.TEMPLATE_RESOLVER);
    private final Map<World, List<Runnable>> lightUpdateQueue = new IdentityHashMap<>();

    @Override
    public void enable() {
        handle.forceInitialization();
    }

    @Override
    public void disable() {
    }

    @Override
    public boolean isSupported(World world) {
        ThreadedLevelLightEngineHandle engine = ThreadedLevelLightEngineHandle.forWorld(world);
        return handle.isSupported(engine.getRaw());
    }

    @Override
    public byte[] getSectionBlockLight(World world, int cx, int cy, int cz) {
        final Chunk chunk = WorldUtil.getChunk(world, cx, cz);
        if (chunk == null) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to read sky light of [" + cx + "/" + cy + "/" + cz + "]: Chunk not loaded");
            return null;
        }
        try {
            return handle.getBlockLightData(HandleConversion.toChunkHandle(chunk), cy);
        } catch (Throwable ex) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to read sky light of [" + cx + "/" + cy + "/" + cz + "]", ex);
            return null;
        }
    }

    @Override
    public byte[] getSectionSkyLight(World world, int cx, int cy, int cz) {
        final Chunk chunk = WorldUtil.getChunk(world, cx, cz);
        if (chunk == null) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to read sky light of [" + cx + "/" + cy + "/" + cz + "]: Chunk not loaded");
            return null;
        }
        try {
            return handle.getSkyLightData(HandleConversion.toChunkHandle(chunk), cy);
        } catch (Throwable ex) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to read sky light of [" + cx + "/" + cy + "/" + cz + "]", ex);
            return null;
        }
    }

    @Override
    public CompletableFuture<Void> setSectionSkyLightAsync(World world, int cx, int cy, int cz, byte[] data) {
        final CompletableFuture<Void> future = new CompletableFuture<Void>();
        final Chunk chunk = WorldUtil.getChunk(world, cx, cz);

        scheduleUpdate(world, () -> {
            try {
                handle.setSkyLightData(HandleConversion.toChunkHandle(chunk), cx, cy, cz, data);
                future.complete(null);
            } catch (Throwable t) {
                future.completeExceptionally(t);
            }
        });

        return future;
    }

    @Override
    public CompletableFuture<Void> setSectionBlockLightAsync(World world, int cx, int cy, int cz, byte[] data) {
        final CompletableFuture<Void> future = new CompletableFuture<Void>();
        final Chunk chunk = WorldUtil.getChunk(world, cx, cz);

        scheduleUpdate(world, () -> {
            try {
                handle.setBlockLightData(HandleConversion.toChunkHandle(chunk), cx, cy, cz, data);
                future.complete(null);
            } catch (Throwable t) {
                future.completeExceptionally(t);
            }
        });

        return future;
    }

    private void scheduleUpdate(final World world, final Runnable runnable) {
        synchronized (lightUpdateQueue) {
            // Try adding to an already scheduled queue
            {
                List<Runnable> queue = lightUpdateQueue.get(world);
                if (queue != null) {
                    queue.add(runnable);
                    return;
                }
            }

            // Cache a new queue, and cache it so later schedules use this list
            {
                List<Runnable> queue = new ArrayList<Runnable>();
                queue.add(runnable);
                lightUpdateQueue.put(world, queue);
            }

            // When the scheduler runs, remove it from the cache again
            // Then run all commands in the list
            ThreadedLevelLightEngineHandle.forWorld(world).schedule(() -> {
                List<Runnable> queue;
                synchronized (lightUpdateQueue) {
                    queue = lightUpdateQueue.remove(world);
                }
                if (queue != null) {
                    for (Runnable queuedTask : queue) {
                        queuedTask.run();
                    }
                }
            });
        }
    }

    @Template.Optional
    @Template.Import("net.minecraft.server.level.ThreadedLevelLightEngine")
    @Template.Import("net.minecraft.core.SectionPos")
    @Template.Import("net.minecraft.world.level.chunk.LevelChunk")
    @Template.Import("net.minecraft.world.level.chunk.DataLayer")
    @Template.Import("net.minecraft.world.level.LightLayer")
    @Template.Import("net.minecraft.world.level.chunk.LightChunkGetter")
    @Template.InstanceType("ca.spottedleaf.moonrise.patches.starlight.light.SWMRNibbleArray")
    /*
     * <GET_HEIGHT_OFFSET>
     * public static int getHeightOffset(LevelChunk chunk) {
     *     // Note: StarLight uses offset for below-bedrock light buffers, hence + 1
     * #if exists ca.spottedleaf.moonrise.common.util.WorldUtil public static int getMinLightSection(net.minecraft.world.level.LevelHeightAccessor world);
     *     return -ca.spottedleaf.moonrise.common.util.WorldUtil.getMinLightSection(chunk.getLevel());
     * #elseif version >= 1.17
     *     return 1 - chunk.getMinSection();
     * #else
     *     return 1;
     * #endif
     * }
     */
    @Template.Require(declaring="net.minecraft.world.level.chunk.LevelChunk", value="%GET_HEIGHT_OFFSET%")
    public static abstract class StarLightEngineHandle extends Template.Class<Template.Handle> {

        /*
         * <IS_SUPPORTED>
         * public static boolean isSupported(net.minecraft.server.level.ThreadedLevelLightEngine lightEngineThreaded) {
         * #if version >= 1.21
         *     return true;
         * #else
         *   #if exists net.minecraft.server.level.ThreadedLevelLightEngine protected final ca.spottedleaf.moonrise.patches.starlight.light.StarLightInterface theLightEngine;
         *     #require net.minecraft.server.level.ThreadedLevelLightEngine protected final ca.spottedleaf.moonrise.patches.starlight.light.StarLightInterface theLightEngine;
         *   #else
         *     #require net.minecraft.server.level.ThreadedLevelLightEngine protected final ca.spottedleaf.moonrise.patches.starlight.light.ThreadedStarLightEngine theLightEngine;
         *   #endif
         *     return lightEngineThreaded#theLightEngine != null;
         * #endif
         * }
         */
        @Template.Generated("%IS_SUPPORTED%")
        public abstract boolean isSupported(Object lightEngineThreadedHandle);

        /*
         * <GET_SKYLIGHT_DATA>
         * public static byte[] getSkyLightData(LevelChunk chunk, int cy) {
         *     cy += #getHeightOffset(chunk);
         *
         * #if exists net.minecraft.world.level.chunk.LevelChunk public ca.spottedleaf.moonrise.patches.starlight.light.SWMRNibbleArray[] starlight$getSkyNibbles();
         *     SWMRNibbleArray[] nibbles = chunk.starlight$getSkyNibbles();
         * #else
         *     SWMRNibbleArray[] nibbles = chunk.getSkyNibbles();
         * #endif
         *     SWMRNibbleArray swmr_nibble;
         *     if (cy < 0 || cy >= nibbles.length || (swmr_nibble = nibbles[cy]) == null) {
         *         return null;
         *     }
         * 
         * #if exists ca.spottedleaf.moonrise.patches.starlight.light.SWMRNibbleArray public net.minecraft.world.level.chunk.DataLayer toVanillaNibble();
         *     DataLayer nibble = swmr_nibble.toVanillaNibble();
         *     if (nibble == null) {
         *         return null;
         *     } else {
         *   #if version >= 1.18
         *         return nibble.getData();
         *   #else
         *         return nibble.asBytes();
         *   #endif
         *     }
         * #else
         *     byte[] newData = new byte[2048];
         *     swmr_nibble.copyInto(newData, 0);
         *     return newData;
         * #endif
         * }
         */
        @Template.Generated("%GET_SKYLIGHT_DATA%")
        public abstract byte[] getSkyLightData(Object chunk, int cy);

        /*
         * <GET_BLOCKLIGHT_DATA>
         * public static byte[] getBlockLightData(LevelChunk chunk, int cy) {
         *     cy += #getHeightOffset(chunk);
         *
         * #if exists net.minecraft.world.level.chunk.LevelChunk public ca.spottedleaf.moonrise.patches.starlight.light.SWMRNibbleArray[] starlight$getBlockNibbles();
         *     SWMRNibbleArray[] nibbles = chunk.starlight$getBlockNibbles();
         * #else
         *     SWMRNibbleArray[] nibbles = chunk.getBlockNibbles();
         * #endif
         *     SWMRNibbleArray swmr_nibble;
         *     if (cy < 0 || cy >= nibbles.length || (swmr_nibble = nibbles[cy]) == null) {
         *         return null;
         *     }
         * 
         * #if exists ca.spottedleaf.moonrise.patches.starlight.light.SWMRNibbleArray public net.minecraft.world.level.chunk.DataLayer toVanillaNibble();
         *     DataLayer nibble = swmr_nibble.toVanillaNibble();
         *     if (nibble == null) {
         *         return null;
         *     } else {
         *   #if version >= 1.18
         *         return nibble.getData();
         *   #else
         *         return nibble.asBytes();
         *   #endif
         *     }
         * #else
         *     byte[] newData = new byte[2048];
         *     swmr_nibble.copyInto(newData, 0);
         *     return newData;
         * #endif
         * }
         */
        @Template.Generated("%GET_BLOCKLIGHT_DATA%")
        public abstract byte[] getBlockLightData(Object chunk, int cy);

        /*
         * <SET_SKYLIGHT_DATA>
         * public static void setSkyLightData(LevelChunk chunk, int cx, int cy, int cz, byte[] data) {
         *     cy += #getHeightOffset(chunk);
         *
         * #if exists net.minecraft.world.level.chunk.LevelChunk public ca.spottedleaf.moonrise.patches.starlight.light.SWMRNibbleArray[] starlight$getSkyNibbles();
         *     SWMRNibbleArray[] nibbles = chunk.starlight$getSkyNibbles();
         * #else
         *     SWMRNibbleArray[] nibbles = chunk.getSkyNibbles();
         * #endif
         *     if (cy < 0 || cy >= nibbles.length) {
         *         return null;
         *     }
         *     SWMRNibbleArray nibble = nibbles[cy];
         *     if (nibble == null) {
         *         nibble = new SWMRNibbleArray();
         *         nibbles[cy] = nibble;
         *     }
         * 
         * #if exists ca.spottedleaf.moonrise.patches.starlight.light.SWMRNibbleArray public void copyFrom(final byte[] src, final int off);
         *     nibble.copyFrom(data, 0);
         * #else
         *     nibble.set(0, 0);
         *     #require ca.spottedleaf.moonrise.patches.starlight.light.SWMRNibbleArray protected byte[] storageUpdating;
         *     byte[] updating = nibble#storageUpdating;
         *     System.arraycopy(data, 0, updating, 0, 2048);
         * #endif
         * 
         *     if (nibble.updateVisible()) {
         * #if version >= 1.18
         *         LightChunkGetter lightAccess = chunk.getLevel().getChunkSource();
         *         SectionPos position = SectionPos.of(cx, cy-1, cz);
         *         lightAccess.onLightUpdate(LightLayer.SKY, position);
         * #else
         *         LightChunkGetter lightAccess = chunk.getWorld().getChunkProvider();
         *         SectionPos position = SectionPos.a(cx, cy-1, cz);
         *   #if exists net.minecraft.world.level.chunk.LightChunkGetter public abstract void markLightSectionDirty(net.minecraft.world.level.LightLayer block, net.minecraft.core.SectionPos pos);
         *         lightAccess.markLightSectionDirty(LightLayer.SKY, position);
         *   #else
         *         lightAccess.a(LightLayer.SKY, position);
         *   #endif
         * #endif
         *     }
         * }
         */
        @Template.Generated("%SET_SKYLIGHT_DATA%")
        public abstract void setSkyLightData(Object chunk, int cx, int cy, int cz, byte[] data);

        /*
         * <SET_BLOCKLIGHT_DATA>
         * public static void setSkyLightData(LevelChunk chunk, int cx, int cy, int cz, byte[] data) {
         *     cy += #getHeightOffset(chunk);
         *
         * #if exists net.minecraft.world.level.chunk.LevelChunk public ca.spottedleaf.moonrise.patches.starlight.light.SWMRNibbleArray[] starlight$getBlockNibbles();
         *     SWMRNibbleArray[] nibbles = chunk.starlight$getBlockNibbles();
         * #else
         *     SWMRNibbleArray[] nibbles = chunk.getBlockNibbles();
         * #endif
         *     if (cy < 0 || cy >= nibbles.length) {
         *         return null;
         *     }
         *     SWMRNibbleArray nibble = nibbles[cy];
         *     if (nibble == null) {
         *         nibble = new SWMRNibbleArray();
         *         nibbles[cy] = nibble;
         *     }
         * 
         * #if exists ca.spottedleaf.moonrise.patches.starlight.light.SWMRNibbleArray public void copyFrom(final byte[] src, final int off);
         *     nibble.copyFrom(data, 0);
         * #else
         *     nibble.set(0, 0);
         *     #require ca.spottedleaf.moonrise.patches.starlight.light.SWMRNibbleArray protected byte[] storageUpdating;
         *     byte[] updating = nibble#storageUpdating;
         *     System.arraycopy(data, 0, updating, 0, 2048);
         * #endif
         * 
         *     if (nibble.updateVisible()) {
         * #if version >= 1.18
         *         LightChunkGetter lightAccess = chunk.getLevel().getChunkSource();
         *         SectionPos position = SectionPos.of(cx, cy-1, cz);
         *         lightAccess.onLightUpdate(LightLayer.BLOCK, position);
         * #else
         *         LightChunkGetter lightAccess = chunk.getWorld().getChunkProvider();
         *         SectionPos position = SectionPos.a(cx, cy-1, cz);
         *   #if exists net.minecraft.world.level.chunk.LightChunkGetter public abstract void markLightSectionDirty(net.minecraft.world.level.LightLayer block, net.minecraft.core.SectionPos pos);
         *         lightAccess.markLightSectionDirty(LightLayer.BLOCK, position);
         *   #else
         *         lightAccess.a(LightLayer.BLOCK, position);
         *   #endif
         * #endif
         *     }
         * }
         */
        @Template.Generated("%SET_BLOCKLIGHT_DATA%")
        public abstract void setBlockLightData(Object chunk, int cx, int cy, int cz, byte[] data);
    }
}
