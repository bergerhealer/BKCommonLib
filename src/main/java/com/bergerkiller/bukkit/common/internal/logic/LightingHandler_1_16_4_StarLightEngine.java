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
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.generated.net.minecraft.server.LightEngineThreadedHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Interfaces with the Tuinity server 1.16.4 starlight engine
 */
public class LightingHandler_1_16_4_StarLightEngine extends LightingHandler {
    private StarLightEngineHandle handle;
    private final Map<World, List<Runnable>> lightUpdateQueue = new IdentityHashMap<>();

    public LightingHandler_1_16_4_StarLightEngine() {
        handle = Template.Class.create(StarLightEngineHandle.class, Common.TEMPLATE_RESOLVER);
        handle.forceInitialization();
    }

    @Override
    public boolean isSupported(World world) {
        LightEngineThreadedHandle engine = LightEngineThreadedHandle.forWorld(world);
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
            // Note: StarLight uses offset for below-bedrock light buffers, hence + 1
            return handle.getBlockLightData(HandleConversion.toChunkHandle(chunk), cy + 1);
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
            // Note: StarLight uses offset for below-bedrock light buffers, hence + 1
            return handle.getSkyLightData(HandleConversion.toChunkHandle(chunk), cy + 1);
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
            LightEngineThreadedHandle.forWorld(world).schedule(() -> {
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
    @Template.Import("net.minecraft.server.LightEngineThreaded")
    @Template.InstanceType("com.tuinity.tuinity.chunk.light.ThreadedStarLightEngine")
    public static abstract class StarLightEngineHandle extends Template.Class<Template.Handle> {

        /*
         * <GET_LAYER_DATA>
         * public static byte[] getLayerData(net.minecraft.server.LightEngineLayer layer, int cx, int cy, int cz) {
         *    net.minecraft.server.NibbleArray array = layer.a(net.minecraft.server.SectionPosition.a(cx, cy, cz));
         *    if (array == null) {
         *        return null;
         *    }
         *    return array.asBytes();
         * }
         */
        @Template.Generated("%GET_LAYER_DATA%")
        public abstract byte[] getData(Object lightEngineLayer, int cx, int cy, int cz);

        /*
         * <IS_SUPPORTED>
         * public static boolean isSupported(net.minecraft.server.LightEngineThreaded lightEngineThreaded) {
         *     #require net.minecraft.server.LightEngineThreaded protected final com.tuinity.tuinity.chunk.light.ThreadedStarLightEngine theLightEngine;
         *     return lightEngineThreaded#theLightEngine != null;
         * }
         */
        @Template.Generated("%IS_SUPPORTED%")
        public abstract boolean isSupported(Object lightEngineThreadedHandle);

        /*
         * <GET_SKYLIGHT_DATA>
         * public static byte[] getSkyLightData(net.minecraft.server.Chunk chunk, int cy) {
         *     SWMRNibbleArray[] nibbles = chunk.getSkyNibbles();
         *     if (cy < 0 || cy >= nibbles.length) {
         *         return null;
         *     }
         *     byte[] newData = new byte[2048];
         *     nibbles[cy].copyInto(newData, 0);
         *     return newData;
         * }
         */
        @Template.Generated("%GET_SKYLIGHT_DATA%")
        public abstract byte[] getSkyLightData(Object chunk, int cy);

        /*
         * <GET_BLOCKLIGHT_DATA>
         * public static byte[] getBlockLightData(net.minecraft.server.Chunk chunk, int cy) {
         *     SWMRNibbleArray[] nibbles = chunk.getBlockNibbles();
         *     if (cy < 0 || cy >= nibbles.length) {
         *         return null;
         *     }
         *     byte[] newData = new byte[2048];
         *     nibbles[cy].copyInto(newData, 0);
         *     return newData;
         * }
         */
        @Template.Generated("%GET_BLOCKLIGHT_DATA%")
        public abstract byte[] getBlockLightData(Object chunk, int cy);

        /*
         * <SET_SKYLIGHT_DATA>
         * public static void setSkyLightData(net.minecraft.server.Chunk chunk, int cx, int cy, int cz, byte[] data) {
         *     SWMRNibbleArray nibble = chunk.getSkyNibbles()[cy+1]; // Note: below-bedrock +1 offset!
         *     nibble.copyFrom(data, 0);
         *     if (nibble.updateVisible()) {
         *         net.minecraft.server.ILightAccess lightAccess = chunk.getWorld().getChunkProvider();
         *         lightAccess.markLightSectionDirty(net.minecraft.server.EnumSkyBlock.SKY, new net.minecraft.server.SectionPosition(cx, cy, cz));
         *     }
         * }
         */
        @Template.Generated("%SET_SKYLIGHT_DATA%")
        public abstract void setSkyLightData(Object chunk, int cx, int cy, int cz, byte[] data);

        /*
         * <SET_BLOCKLIGHT_DATA>
         * public static void setSkyLightData(net.minecraft.server.Chunk chunk, int cx, int cy, int cz, byte[] data) {
         *     SWMRNibbleArray nibble = chunk.getBlockNibbles()[cy+1]; // Note: below-bedrock +1 offset!
         *     nibble.copyFrom(data, 0);
         *     if (nibble.updateVisible()) {
         *         net.minecraft.server.ILightAccess lightAccess = chunk.getWorld().getChunkProvider();
         *         lightAccess.markLightSectionDirty(net.minecraft.server.EnumSkyBlock.BLOCK, new net.minecraft.server.SectionPosition(cx, cy, cz));
         *     }
         * }
         */
        @Template.Generated("%SET_BLOCKLIGHT_DATA%")
        public abstract void setBlockLightData(Object chunk, int cx, int cy, int cz, byte[] data);
    }
}
