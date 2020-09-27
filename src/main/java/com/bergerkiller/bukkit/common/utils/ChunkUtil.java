package com.bergerkiller.bukkit.common.utils;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.chunk.ForcedChunk;
import com.bergerkiller.bukkit.common.collections.FilteredCollection;
import com.bergerkiller.bukkit.common.collections.List2D;
import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.internal.logic.RegionHandler;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.ChunkSection;
import com.bergerkiller.bukkit.common.wrappers.HeightMap;
import com.bergerkiller.generated.net.minecraft.server.ChunkHandle;
import com.bergerkiller.generated.net.minecraft.server.ChunkSectionHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityHandle;
import com.bergerkiller.generated.net.minecraft.server.EnumSkyBlockHandle;
import com.bergerkiller.generated.net.minecraft.server.WorldServerHandle;
import com.bergerkiller.mountiplex.conversion.util.ConvertingList;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Contains utilities to get and set chunks of a world
 */
public class ChunkUtil {

    /**
     * Forces a chunk to stay loaded. Call {@link ForcedChunk#close()} to release
     * the chunk again to allow it to unload. The chunk is loaded asynchronously
     * if it is not already loaded.
     * 
     * @param world
     * @param chunkX
     * @param chunkZ
     * @return forced chunk
     */
    public static ForcedChunk forceChunkLoaded(World world, int chunkX, int chunkZ) {
        return CommonPlugin.getInstance().getForcedChunkManager().newForcedChunk(world, chunkX, chunkZ);
    }

    /**
     * Forces a chunk to stay loaded. Call {@link ForcedChunk#close()} to release
     * the chunk again to allow it to unload. If the provided chunk is currently not
     * actually loaded, it is loaded asynchronously.
     * 
     * @param chunk
     * @return forced chunk
     */
    public static ForcedChunk forceChunkLoaded(org.bukkit.Chunk chunk) {
        return forceChunkLoaded(chunk.getWorld(), chunk.getX(), chunk.getZ());
    }

    /**
     * Gets an array of vertical Chunk Sections that make up the data of a chunk.
     * Some array elements may be null, if that slice stores all-air.<br>
     * <br>
     * <b>Deprecated: Does not take into account infinite-y size worlds when a modded
     * server is used that adds that functionality. Only returns the first 16 slices.</b>
     * 
     * @param chunk to get the sections of
     * @return the first 16 chunk sections of the chunk
     */
    @Deprecated
    public static ChunkSection[] getSections(org.bukkit.Chunk chunk) {
        return ChunkHandle.T.getSections.invoke(HandleConversion.toChunkHandle(chunk));
    }

    /**
     * Gets a vertical 16-high cube slice of the chunk
     * 
     * @param chunk The chunk to get the section of
     * @param cy The 16x16x16 block section coordinate (same coordinate space as chunk x/z)
     * @return chunk section at this coordinate, or null if at this coordinate all blocks are air
     *         and no data is stored here.
     */
    public static ChunkSection getSection(org.bukkit.Chunk chunk, int cy) {
        return ChunkHandle.T.getSection.invoke(HandleConversion.toChunkHandle(chunk), cy);
    }

    /**
     * Gets the light-level height map of a chunk.
     * This stores the height above which all (sky) light levels are 15.
     * The heightmap is not (re-) initialized, instead storing the values as
     * reported by the server.
     * 
     * @param chunk to get the light heightmap for
     * @return light heightmap
     */
    public static HeightMap getLightHeightMap(org.bukkit.Chunk chunk) {
        return getLightHeightMap(chunk, false);
    }

    /**
     * Gets the light-level height map of a chunk.
     * This stores the height above which all (sky) light levels are 15.
     * 
     * @param chunk to get the light heightmap for
     * @param initialize whether to force a complete recalculation of the light heightmap
     * @return light heightmap
     */
    public static HeightMap getLightHeightMap(org.bukkit.Chunk chunk, boolean initialize) {
        return ChunkHandle.fromBukkit(chunk).getLightHeightMap(initialize);
    }

    /**
     * Gets the block light level
     *
     * @param chunk the block is in
     * @param x - coordinate of the block
     * @param y - coordinate of the block
     * @param z - coordinate of the block
     * @return Block light level
     */
    public static int getBlockLight(org.bukkit.Chunk chunk, int x, int y, int z) {
        if (y < 0) {
            return 0;
        } else if (y >= chunk.getWorld().getMaxHeight()) {
            return EnumSkyBlockHandle.BLOCK.getBrightness();
        } else {
            return ChunkHandle.fromBukkit(chunk).getBrightness(EnumSkyBlockHandle.BLOCK,
                    new IntVector3(x & 0xf, y, z & 0xf));
        }
    }

    /**
     * Gets the sky light level
     *
     * @param chunk the block is in
     * @param x - coordinate of the block
     * @param y - coordinate of the block
     * @param z - coordinate of the block
     * @return Sky light level
     */
    public static int getSkyLight(org.bukkit.Chunk chunk, int x, int y, int z) {
        if (y < 0) {
            return 0;
        } else if (y >= chunk.getWorld().getMaxHeight()) {
            return EnumSkyBlockHandle.SKY.getBrightness();
        } else {
            return ChunkHandle.fromBukkit(chunk).getBrightness(EnumSkyBlockHandle.SKY,
                    new IntVector3(x & 0xf, y, z & 0xf));
        }
    }

    /**
     * Gets the block type Id
     *
     * @param chunk the block is in
     * @param x - coordinate of the block
     * @param y - coordinate of the block
     * @param z - coordinate of the block
     * @return block type
     */
    public static Material getBlockType(org.bukkit.Chunk chunk, int x, int y, int z) {
        return getBlockData(chunk, x, y, z).getType();
    }

    /**
     * Gets the block type and data from a chunk
     * 
     * @param chunk the block is in
     * @param x - coordinate of the block
     * @param y - coordinate of the block
     * @param z - coordinate of the block
     * @return block data information
     */
    public static BlockData getBlockData(org.bukkit.Chunk chunk, int x, int y, int z) {
        return ChunkHandle.T.getBlockDataAtCoord.invoke(HandleConversion.toChunkHandle(chunk), x, y, z);

        /*
        Object chunkHandleRaw = HandleConversion.toChunkHandle(chunk);
        Object blockPos = BlockPositionHandle.T.constr_x_y_z.raw.newInstance(x, y, z);
        Object iBlockData = ChunkHandle.T.getBlockData.raw.invoke(chunkHandleRaw, blockPos);
        return BlockData.fromBlockData(iBlockData);
        */
    }

    /**
     * Sets a block type id and data without causing physics or lighting updates
     *
     * @param chunk the block is in
     * @param block - the block coordinates within the chunk to set
     * @param data to set to
     */
    public static void setBlockFast(org.bukkit.Chunk chunk, Block block, BlockData data) {
        final int secIndex = block.getY() >> 4;
        Object section = (Object[]) ChunkHandle.T.getSection.raw.invoke(HandleConversion.toChunkHandle(chunk), secIndex);
        if (section != null) {
            ChunkSectionHandle.T.setBlockDataAtBlock.invoke(section, block, data);
        } else {
            // Slow method, to initialize the empty chunk
            WorldUtil.setBlockData(block, data);
        }
    }

    /**
     * Sets a block type id and data without causing physics or lighting updates
     *
     * @param chunk the block is in
     * @param x - coordinate of the block
     * @param y - coordinate of the block
     * @param z - coordinate of the block
     * @param data to set to
     */
    public static void setBlockFast(org.bukkit.Chunk chunk, int x, int y, int z, BlockData data) {
        final int secIndex = y >> 4;
        Object section = (Object[]) ChunkHandle.T.getSection.raw.invoke(HandleConversion.toChunkHandle(chunk), secIndex);
        if (section != null) {
            ChunkSectionHandle.T.setBlockData.invoke(section, x & 0xf, y & 0xf, z & 0xf, data);
        } else {
            // Slow method, to initialize the empty chunk
            WorldUtil.setBlockData(chunk.getWorld(),
                    (chunk.getX() << 4) | (x & 0xf),
                    y,
                    (chunk.getZ() << 4) | (z & 0xf),
                    data);
        }
    }

    /**
     * Gets a live collection of all the entities in a chunk<br>
     * Changes to this collection are reflected back in the chunk
     *
     * @param chunk for which to get the entities
     * @return Live collection of entities in the chunk
     */
    public static List<org.bukkit.entity.Entity> getEntities(org.bukkit.Chunk chunk) {
        List<Object>[] entitySlices = ChunkHandle.fromBukkit(chunk).getEntitySlices();
        if (entitySlices == null || entitySlices.length == 0) {
            return Collections.emptyList();
        }
        return new ConvertingList<org.bukkit.entity.Entity>(new List2D<Object>(entitySlices), DuplexConversion.entity);
    }

    /**
     * Checks if a chunk is about be loaded
     *
     * @param player who will receive the chunk
     * @param cx location for the chunk X
     * @param cz location for the chunk Z
     * @return chunk being loaded soon?
     */
    @Deprecated
    public static boolean isLoadRequested(Player player, int cx, int cz) {
    	throw new RuntimeException("BROKEN");
    }

    /**
     * Gets whether a given chunk is readily available. If this method returns
     * False, the chunk is not yet generated.
     *
     * @param world the chunk is in
     * @param x - coordinate of the chunk
     * @param z - coordinate of the chunk
     * @return True if the chunk can be obtained without generating it, False if
     * not
     */
    public static boolean isChunkAvailable(World world, int x, int z) {
        if (WorldUtil.isLoaded(world, x, z)) {
            // Chunk is loaded into memory, True
            return true;
        } else {
            return RegionHandler.INSTANCE.isChunkSaved(world, x, z);
        }
    }

    /**
     * Gets all the chunks loaded on a given world
     *
     * @param world to get the loaded chunks from
     * @return Loaded chunks
     */
    public static Collection<org.bukkit.Chunk> getChunks(World world) {
        // Bukkit alternative
        return FilteredCollection.createNullFilter(Arrays.asList(world.getLoadedChunks()));
    }

    /**
     * Gets a chunk from a world without loading or generating it.
     * This method guarantees no event will be generated at all trying to get this chunk.
     * If the chunk is not loaded, null is returned.
     *
     * @param world to obtain the chunk from
     * @param x - coordinate of the chunk
     * @param z - coordinate of the chunk
     * @return The chunk, or null if it is not loaded
     */
    public static org.bukkit.Chunk getChunk(World world, final int x, final int z) {
        return WorldServerHandle.T.getChunkIfLoaded.invoke(HandleConversion.toWorldHandle(world), x, z);
    }

    /**
     * Gets, loads or generates a chunk without loading or generating it on the
     * main thread. Allows the lazy-loading of chunks without locking the
     * server.
     *
     * @param world to obtain the chunk from
     * @param x - coordinate of the chunk
     * @param z - coordinate of the chunk
     * @return chunk future that is completed when the chunk is ready
     */
    public static CompletableFuture<org.bukkit.Chunk> getChunkAsync(World world, final int x, final int z) {
        final CompletableFuture<org.bukkit.Chunk> result = new CompletableFuture<org.bukkit.Chunk>();
        final ForcedChunk forced = WorldUtil.forceChunkLoaded(world, x, z);
        forced.getChunkAsync().whenComplete((chunk, exception) -> {
            try {
                if (exception != null) {
                    result.completeExceptionally(exception);
                } else {
                    result.complete(chunk);
                }
            } finally {
                forced.close();
            }
        });
        return result;
    }

    /**
     * Gets, loads or generates a chunk without loading or generating it on the
     * main thread. Allows the lazy-loading of chunks without locking the
     * server.
     *
     * @param world to obtain the chunk from
     * @param x - coordinate of the chunk
     * @param z - coordinate of the chunk
     * @param runnable to execute once the chunk is loaded or obtained
     */
    @Deprecated
    public static void getChunkAsync(World world, final int x, final int z, Runnable runnable) {
        getChunkAsync(world, x, z).thenRun(runnable);
    }

    /**
     * Saves a single chunk to disk.
     * This method is only valid for chunks that are loaded.
     * Unloaded chunks should not be saved (again).
     *
     * @param chunk to save
     */
    public static void saveChunk(org.bukkit.Chunk chunk) {
        CommonNMS.getHandle(chunk.getWorld()).getChunkProviderServer().saveLoadedChunk(CommonNMS.getHandle(chunk));
    }

    /**
     * Gets whether a chunk needs to be saved
     *
     * @param chunk to check
     * @return True if it needs to be saved, False if not
     */
    public static boolean needsSaving(org.bukkit.Chunk chunk) {
        return ChunkHandle.T.checkCanSave.invoke(HandleConversion.toChunkHandle(chunk));
    }

    /**
     * Obtains all the Block State tile entities available in a Chunk
     *
     * @param chunk to get the Block States for
     * @return collection of Block States (mutable)
     */
    public static Collection<BlockState> getBlockStates(org.bukkit.Chunk chunk) {
        return ChunkHandle.T.getTileEntities.invoke(HandleConversion.toChunkHandle(chunk));
    }

    /**
     * Adds an Entity to a Chunk
     *
     * @param chunk to add an entity to
     * @param entity to add
     */
    public static void addEntity(org.bukkit.Chunk chunk, org.bukkit.entity.Entity entity) {
        ChunkHandle.fromBukkit(chunk).addEntity(EntityHandle.fromBukkit(entity));
    }

    /**
     * Removes an Entity from a Chunk
     *
     * @param chunk to remove an entity from
     * @param entity to remove
     * @return True if the entity has been removed, False if not (not found)
     */
    public static boolean removeEntity(org.bukkit.Chunk chunk, org.bukkit.entity.Entity entity) {
        final ChunkHandle chunkHandle = CommonNMS.getHandle(chunk);
        final List<Object>[] slices = chunkHandle.getEntitySlices();
        final int sliceY = MathUtil.clamp(MathUtil.toChunk(EntityUtil.getLocY(entity)), 0, slices.length - 1);
        final Object handle = HandleConversion.toEntityHandle(entity);
        if (slices[sliceY].remove(handle)) {
            chunkHandle.markEntitiesDirty();
            return true;
        } else {
            for (int y = 0; y < slices.length; y++) {
                if (y != sliceY && slices[y].remove(handle)) {
                    chunkHandle.markEntitiesDirty();
                    return true;
                }
            }
            return false;
        }
    }
}
