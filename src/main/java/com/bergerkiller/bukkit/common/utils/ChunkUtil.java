package com.bergerkiller.bukkit.common.utils;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.collections.FilteredCollection;
import com.bergerkiller.bukkit.common.collections.List2D;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.internal.CommonMethods;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.ChunkSection;
import com.bergerkiller.generated.net.minecraft.server.ChunkHandle;
import com.bergerkiller.generated.net.minecraft.server.ChunkProviderServerHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.util.LongHashSetHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.util.LongObjectHashMapHandle;
import com.bergerkiller.mountiplex.conversion.util.ConvertingList;
import com.bergerkiller.reflection.net.minecraft.server.NMSChunk;
import com.bergerkiller.reflection.net.minecraft.server.NMSChunkProviderServer;
import com.bergerkiller.reflection.net.minecraft.server.NMSChunkRegionLoader;
import com.bergerkiller.reflection.net.minecraft.server.NMSChunkSection;
import com.bergerkiller.reflection.net.minecraft.server.NMSWorldServer;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

/**
 * Contains utilities to get and set chunks of a world
 */
public class ChunkUtil {

    private static boolean canUseLongObjectHashMap = CommonUtil.getCBClass("util.LongObjectHashMap") != null;
    private static boolean canUseLongHashSet = CommonUtil.getCBClass("util.LongHashSet") != null;

    /**
     * Gets an array of vertical Chunk Sections that make up the data of a chunk
     * 
     * @param chunk to get the sections of
     * @return chunk sections
     */
    public static ChunkSection[] getSections(org.bukkit.Chunk chunk) {
        return NMSChunk.sections.get(Conversion.toChunkHandle.convert(chunk));
    }

    /**
     * Gets the height of a given column in a chunk
     *
     * @param chunk the column is in
     * @param x - coordinate of the block column
     * @param z - coordinate of the block column
     * @return column height
     */
    public static int getHeight(org.bukkit.Chunk chunk, int x, int z) {
        return NMSChunk.getHeight(CommonNMS.getNative(chunk), x, z);
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
        return NMSChunk.getBlockLight(CommonNMS.getNative(chunk), x, y, z);
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
        return NMSChunk.getSkyLight(CommonNMS.getNative(chunk), x, y, z);
    }

    /**
     * Gets the block type Id
     *
     * @param chunk the block is in
     * @param x - coordinate of the block
     * @param y - coordinate of the block
     * @param z - coordinate of the block
     * @return block type Id
     */
    @Deprecated
    public static int getBlockTypeId(org.bukkit.Chunk chunk, int x, int y, int z) {
        return NMSChunk.getTypeId(CommonNMS.getNative(chunk), x, y, z);
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
    @SuppressWarnings("deprecation")
    public static Material getBlockType(org.bukkit.Chunk chunk, int x, int y, int z) {
        return MaterialUtil.getType(NMSChunk.getTypeId(CommonNMS.getNative(chunk), x, y, z));
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
        if (y < 0 || y >= chunk.getWorld().getMaxHeight()) {
            return;
        }

        Object[] sections = (Object[]) NMSChunk.sections.getInternal(CommonNMS.getNative(chunk));
        final int secIndex = y >> 4;
        Object section = sections[secIndex];
        if (section == null) {
            section = sections[secIndex] = CommonMethods.ChunkSection_new(chunk.getWorld(), y).getRaw();
        }

        NMSChunkSection.setBlockData(section, x, y, z, data);
    }

    /**
     * Gets a live collection of all the entities in a chunk<br>
     * Changes to this collection are reflected back in the chunk
     *
     * @param chunk for which to get the entities
     * @return Live collection of entities in the chunk
     */
    public static List<org.bukkit.entity.Entity> getEntities(org.bukkit.Chunk chunk) {
        List<Object>[] entitySlices = NMSChunk.entitySlices.get(Conversion.toChunkHandle.convert(chunk));
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
        Object cps = NMSWorldServer.chunkProviderServer.get(Conversion.toWorldHandle.convert(world));
        if (NMSChunkProviderServer.isChunkLoaded.invoke(cps, x, z)) {
            // Chunk is loaded into memory, True
            return true;
        } else {
            Object chunkLoader = NMSChunkProviderServer.chunkLoader.get(cps);
            if (NMSChunkRegionLoader.T.isInstance(chunkLoader)) {
                // Chunk can be loaded from file
                return NMSChunkRegionLoader.chunkExists(chunkLoader, world, x, z);
            } else {
                // Unable to find out...
                return false;
            }
        }
    }

    /**
     * Gets all the chunks loaded on a given world
     *
     * @param world to get the loaded chunks from
     * @return Loaded chunks
     */
    public static Collection<org.bukkit.Chunk> getChunks(World world) {
        // LongObjectHashMap mirror
        if (canUseLongObjectHashMap) {
            Object chunks = CommonNMS.getHandle(world).getChunkProviderServer().getChunks();
            if (chunks != null) {
                try {
                    if (canUseLongObjectHashMap && LongObjectHashMapHandle.T.isAssignableFrom(chunks)) {
                        Object hashmap_values = LongObjectHashMapHandle.T.values.invoke(chunks);
                        Collection<org.bukkit.Chunk> chunk_collection = DuplexConversion.chunkCollection.convert(hashmap_values);
                        return FilteredCollection.createNullFilter(chunk_collection);
                    }
                } catch (Throwable t) {
                    canUseLongObjectHashMap = false;
                    CommonPlugin.getInstance().log(Level.WARNING, "Failed to access chunks using CraftBukkit's long object hashmap, support disabled");
                    CommonUtil.filterStackTrace(t).printStackTrace();
                }
            }
        }
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
        final long key = MathUtil.longHashToLong(x, z);
        Object chunks = CommonNMS.getHandle(world).getChunkProviderServer().getChunks();
        if (chunks != null) {
            if (canUseLongObjectHashMap && LongObjectHashMapHandle.T.isAssignableFrom(chunks)) {
                try {
                    return WrapperConversion.toChunk(LongObjectHashMapHandle.T.get.invoke(chunks, key));
                } catch (Throwable t) {
                    canUseLongObjectHashMap = false;
                    CommonPlugin.getInstance().log(Level.WARNING, "Failed to access chunks using CraftBukkit's long object hashmap, support disabled");
                    CommonUtil.filterStackTrace(t).printStackTrace();
                }
            }
        }

        // Bukkit alternative
        if (world.isChunkLoaded(x, z)) {
            return world.getChunkAt(x, z);
        } else {
            return null;
        }
    }

    /**
     * Gets, loads or generated a chunk without loading or generating it on the
     * main thread. Allows the lazy-loading of chunks without locking the
     * server.
     *
     * @param world to obtain the chunk from
     * @param x - coordinate of the chunk
     * @param z - coordinate of the chunk
     * @param runnable to execute once the chunk is loaded or obtained
     */
    public static void getChunkAsync(World world, final int x, final int z, Runnable runnable) {
        CommonNMS.getHandle(world).getChunkProviderServer().getChunkAt(x, z);
    }

    /**
     * Sets a given chunk coordinate to contain the chunk specified
     *
     * @param world to set the chunk in
     * @param x - coordinate of the chunk
     * @param z - coordinate of the chunk
     * @param chunk to set to (use null to remove)
     */
    public static void setChunk(World world, final int x, final int z, final org.bukkit.Chunk chunk) {
        final ChunkHandle handle = ChunkHandle.fromBukkit(chunk);
        if (handle != null && handle.getBukkitChunk() == null) {
            throw new RuntimeException("Can not put a chunk that has no BukkitChunk");
        }
        if (canUseLongObjectHashMap) {
            ChunkProviderServerHandle cps = CommonNMS.getHandle(world).getChunkProviderServer();
            Object chunks = cps.getChunks();
            if (chunks != null) {
                final long key = MathUtil.longHashToLong(x, z);
                try {
                    if (canUseLongObjectHashMap && LongObjectHashMapHandle.T.isAssignableFrom(chunks)) {
                        LongObjectHashMapHandle map = LongObjectHashMapHandle.createHandle(chunks);
                        if (handle == null) {
                            // Remove the chunk
                            map.remove(key);
                        } else {
                            // Add the chunk
                            map.put(key, handle.getRaw());
                        }
                        return;
                    }
                } catch (Throwable t) {
                    canUseLongObjectHashMap = false;
                    Logging.LOGGER.log(Level.WARNING, "Failed to access chunks using CraftBukkit's long object hashmap, support disabled");
                    CommonUtil.filterStackTrace(t).printStackTrace();
                }
            }
        }
        throw new RuntimeException("Failed to set chunk using a known method");
    }

    /**
     * Saves a single chunk to disk
     *
     * @param chunk to save
     */
    public static void saveChunk(org.bukkit.Chunk chunk) {
        CommonNMS.getHandle(chunk.getWorld()).getChunkProviderServer().saveChunk(CommonNMS.getHandle(chunk));
    }

    /**
     * Gets whether a chunk needs to be saved
     *
     * @param chunk to check
     * @return True if it needs to be saved, False if not
     */
    public static boolean needsSaving(org.bukkit.Chunk chunk) {
        return NMSChunk.needsSaving(Conversion.toChunkHandle.convert(chunk));
    }

    /**
     * Sets whether a given chunk coordinate has to be unloaded
     *
     * @param world to set the unload request for
     * @param x - coordinate of the chunk
     * @param z - coordinate of the chunk
     * @param unload state to set to
     */
    public static void setChunkUnloading(World world, final int x, final int z, boolean unload) {
        if (canUseLongHashSet) {
            Object unloadQueue = CommonNMS.getHandle(world).getChunkProviderServer().getUnloadQueue();
            if (unloadQueue != null) {
                try {
                    if (canUseLongHashSet && LongHashSetHandle.T.isAssignableFrom(unloadQueue)) {
                        LongHashSetHandle set = LongHashSetHandle.createHandle(unloadQueue);
                        if (unload) {
                            set.add(x, z);
                        } else {
                            set.remove(x, z);
                        }
                        return;
                    }
                } catch (Throwable t) {
                    canUseLongHashSet = false;
                    Logging.LOGGER.log(Level.WARNING, "Failed to access chunks using CraftBukkit's long object hashmap, support disabled");
                    CommonUtil.filterStackTrace(t).printStackTrace();
                }
            }
        }
        throw new RuntimeException("Failed to set unload queue using a known method");
    }

    /**
     * Obtains all the Block State tile entities available in a Chunk
     *
     * @param chunk to get the Block States for
     * @return collection of Block States (mutable)
     */
    public static Collection<BlockState> getBlockStates(org.bukkit.Chunk chunk) {
        return DuplexConversion.blockStateCollection.convert(CommonNMS.getNative(chunk).tileEntities.values());
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
        final List<?>[] slices = (List<?>[]) CommonNMS.getHandle(chunk).getEntitySlices();
        final int sliceY = MathUtil.clamp(MathUtil.toChunk(EntityUtil.getLocY(entity)), 0, slices.length - 1);
        final Object handle = Conversion.toEntityHandle.convert(entity);
        if (slices[sliceY].remove(handle)) {
            return true;
        } else {
            for (int y = 0; y < slices.length; y++) {
                if (y != sliceY && slices[y].remove(handle)) {
                    return true;
                }
            }
            return false;
        }
    }
}
