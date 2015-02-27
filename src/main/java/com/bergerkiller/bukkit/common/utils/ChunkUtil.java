package com.bergerkiller.bukkit.common.utils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

import net.minecraft.server.v1_8_R1.Block;
import net.minecraft.server.v1_8_R1.Chunk;
import net.minecraft.server.v1_8_R1.ChunkCoordIntPair;
import net.minecraft.server.v1_8_R1.ChunkSection;
import net.minecraft.server.v1_8_R1.WorldServer;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R1.util.LongHash;
import org.bukkit.craftbukkit.v1_8_R1.util.LongHashSet;
import org.bukkit.craftbukkit.v1_8_R1.util.LongObjectHashMap;
import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.collections.FilteredCollection;
import com.bergerkiller.bukkit.common.collections.List2D;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.ConversionPairs;
import com.bergerkiller.bukkit.common.conversion.util.ConvertingList;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeField;
import com.bergerkiller.bukkit.common.reflection.classes.ChunkProviderServerRef;
import com.bergerkiller.bukkit.common.reflection.classes.ChunkRef;
import com.bergerkiller.bukkit.common.reflection.classes.ChunkRegionLoaderRef;
import com.bergerkiller.bukkit.common.reflection.classes.ChunkSectionRef;
import com.bergerkiller.bukkit.common.reflection.classes.EntityPlayerRef;
import com.bergerkiller.bukkit.common.reflection.classes.WorldServerRef;

import net.minecraft.server.v1_8_R1.BlockPosition;
import net.minecraft.server.v1_8_R1.EntitySlice;

/**
 * Contains utilities to get and set chunks of a world
 */
public class ChunkUtil {

    private static boolean canUseLongObjectHashMap = CommonUtil.getCBClass("util.LongObjectHashMap") != null;
    private static boolean canUseLongHashSet = CommonUtil.getCBClass("util.LongHashSet") != null;
    public static final FieldAccessor<List<Object>> chunkListField;

    static {
        Field f;
        try {
            f = ChunkProviderServerRef.TEMPLATE.getType().getField("chunkList");
        } catch (Throwable t) {
            f = null;
        }
        chunkListField = f == null ? null : new SafeField<List<Object>>(f);
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
        return ChunkRef.getHeight(CommonNMS.getNative(chunk), x, z);
    }

    /**
     * Gets the block light level
     *
     * @param chunk the block is in
     * @param blockposition - coordinates of the block
     * @return Block light level
     */
    public static int getBlockLight(org.bukkit.Chunk chunk, int x, int y, int z) {
		return ChunkRef.getBlockLight(CommonNMS.getNative(chunk), x, y, z);
    }

    /**
     * Gets the sky light level
     *
     * @param chunk the block is in
     * @param blockposition - coordinates of the block
     * @return Sky light level
     */
    public static int getSkyLight(org.bukkit.Chunk chunk, int x, int y, int z) {
		return ChunkRef.getSkyLight(CommonNMS.getNative(chunk), x, y, z);
    }

    /**
     * Gets the block data
     *
     * @param chunk the block is in
     * @param blockposition - coordinates of the block
     * @return block data
     */
    public static int getBlockData(org.bukkit.Chunk chunk, int x, int y, int z) {
		return ChunkRef.getData(CommonNMS.getNative(chunk), x, y, z);
    }

    /**
     * Gets the block type Id
     *
     * @param chunk the block is in
     * @param blockposition - coordinates of the block
     * @return block type Id
     */
    @Deprecated
    public static int getBlockTypeId(org.bukkit.Chunk chunk, int x, int y, int z) {
		return ChunkRef.getTypeId(CommonNMS.getNative(chunk), x, y, z);
    }

    /**
     * Gets the block type Id
     *
     * @param chunk the block is in
     * @param blockposition - coordinates of the block
     * @return block type
     */
    @SuppressWarnings("deprecation")
    public static Material getBlockType(org.bukkit.Chunk chunk, int x, int y, int z) {
		return MaterialUtil.getType(ChunkRef.getTypeId(CommonNMS.getNative(chunk), x, y, z));
    }
    
    /**
     * Gets the block type Id
     *
     * @param world the block is in
     * @param blockposition - coordinates of the block
     * @return block type
     */
    @SuppressWarnings("deprecation")
    public static Material getBlockType(org.bukkit.World world, int x, int y, int z) {
        return MaterialUtil.getType(ChunkRef.getTypeId(CommonNMS.getNative(world), x, y, z));
    }

    /**
     * Sets a block type id and data without causing physics or lighting updates
     *
     * @param chunk the block is in
     * @param blockposition - coordinates of the block
     * @param typeId to set to
     * @param data to set to
     */
    public static void setBlockFast(org.bukkit.Chunk chunk, BlockPosition blockposition, int typeId, int data) {
        int y = blockposition.getY();
        if (y < 0 || y >= chunk.getWorld().getMaxHeight()) {
            return;
        }
        Object[] sections = ChunkRef.getSections(CommonNMS.getNative(chunk));
        final int secIndex = y >> 4;
        Object section = sections[secIndex];
        if (section == null) {
            section = sections[secIndex] = new ChunkSection(y >> 4 << 4, !CommonNMS.getNative(chunk.getWorld()).worldProvider.o());
        }
        ChunkSectionRef.setTypeId(section, blockposition.getX(), blockposition.getY(), blockposition.getZ(), typeId);
        ChunkSectionRef.setData(section, blockposition.getX(), blockposition.getY(), blockposition.getZ(), data);
    }

    /**
     * Sets a block type id and data, causing physics and lighting updates
     *
     * @param chunk the block is in
     * @param blockposition - coordinates of the block
     * @param typeId to set to
     * @param data to set to
     * @return True if a block got changed, False if not
     */
    @Deprecated
	public static boolean setBlock(org.bukkit.Chunk chunk, int x, int y, int z, int typeId, int data) {
		return setBlock(chunk, x, y, z, MaterialUtil.getType(typeId), data);
    }

    /**
     * Sets a block type id and data, causing physics and lighting updates
     *
     * @param chunk the block is in
     * @param blockposition - coordinates of the block
     * @param type to set to
     * @param data to set to
     * @return True if a block got changed, False if not
     */
    @SuppressWarnings("deprecation")
	public static boolean setBlock(org.bukkit.Chunk chunk, int x, int y, int z, Material type, int data) {
		boolean result = y >= 0 && y <= chunk.getWorld().getMaxHeight();
		WorldServer world = CommonNMS.getNative(chunk.getWorld());
		Block typeBlock = CommonNMS.getBlock(type);
		if (result) {
			result = ChunkRef.setBlock(Conversion.toChunkHandle.convert(chunk), x, y, z, Block.getId(typeBlock), data);
            world.methodProfiler.a("checkLight");
            world.A(new BlockPosition(x, y, z));
            //z
            world.methodProfiler.b();
		}
		if (result) {
			world.applyPhysics(new BlockPosition(x, y, z), typeBlock);
		}
		return result;
    }

    /**
     * Gets a live collection of all the entities in a chunk<br>
     * Changes to this collection are reflected back in the chunk
     *
     * @param chunk for which to get the entities
     * @return Live collection of entities in the chunk
     */
    public static List<org.bukkit.entity.Entity> getEntities(org.bukkit.Chunk chunk) {
        List<Object>[] entitySlices = ChunkRef.entitySlices.get(Conversion.toChunkHandle.convert(chunk));
        return new ConvertingList<>(new List2D<>(entitySlices), ConversionPairs.entity);
    }

    /**
     * Checks if a chunk is about be loaded
     *
     * @param player who will receive the chunk
     * @param cx location for the chunk X
     * @param cz location for the chunk Z
     * @return chunk being loaded soon?
     */
    public static boolean isLoadRequested(Player player, int cx, int cz) {
        List<?> chunkQue = EntityPlayerRef.chunkQueue.get(Conversion.toEntityHandle.convert(player));
        for (Object location : chunkQue) {
            ChunkCoordIntPair loc = (ChunkCoordIntPair) location;
            if (loc.x == cx && loc.z == cz) {
                return true;
            }
        }
        return false;
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
        Object cps = WorldServerRef.chunkProviderServer.get(Conversion.toWorldHandle.convert(world));
        if (ChunkProviderServerRef.isChunkLoaded.invoke(cps, x, z)) {
            // Chunk is loaded into memory, True
            return true;
        } else {
            Object chunkLoader = ChunkProviderServerRef.chunkLoader.get(cps);
            if (ChunkRegionLoaderRef.TEMPLATE.isInstance(chunkLoader)) {
                // Chunk can be loaded from file
                return ChunkRegionLoaderRef.chunkExists(chunkLoader, world, x, z);
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
    @SuppressWarnings("rawtypes")
    public static Collection<org.bukkit.Chunk> getChunks(World world) {
        // ChunkList field (if available)
        if (chunkListField != null) {
            Object cps = CommonNMS.getNative(world).chunkProviderServer;
            List<Object> rawChunkList = chunkListField.get(cps);
            Collection<org.bukkit.Chunk> chunkList = CommonNMS.getChunks(rawChunkList);
            return FilteredCollection.createNullFilter(chunkList);
        }
        // LongObjectHashMap mirror
        if (canUseLongObjectHashMap) {
            Object chunks = ChunkProviderServerRef.chunks.get(CommonNMS.getNative(world).chunkProviderServer);
            if (chunks != null) {
                try {
                    if (canUseLongObjectHashMap && chunks instanceof LongObjectHashMap) {
                        return FilteredCollection.createNullFilter(CommonNMS.getChunks(((LongObjectHashMap) chunks).values()));
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
     * Gets a chunk from a world without loading or generating it
     *
     * @param world to obtain the chunk from
     * @param x - coordinate of the chunk
     * @param z - coordinate of the chunk
     * @return The chunk, or null if it is not loaded
     */
    @SuppressWarnings("rawtypes")
    public static org.bukkit.Chunk getChunk(World world, final int x, final int z) {
        final long key = LongHash.toLong(x, z);
        Object chunks = ChunkProviderServerRef.chunks.get(CommonNMS.getNative(world).chunkProviderServer);
        if (chunks != null) {
            if (canUseLongObjectHashMap && chunks instanceof LongObjectHashMap) {
                try {
                    return CommonNMS.getChunk(((Chunk) ((LongObjectHashMap) chunks).get(key)));
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
        CommonNMS.getNative(world).chunkProviderServer.getChunkAt(x, z, runnable);
    }

    /**
     * Sets a given chunk coordinate to contain the chunk specified
     *
     * @param world to set the chunk in
     * @param x - coordinate of the chunk
     * @param z - coordinate of the chunk
     * @param chunk to set to (use null to remove)
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void setChunk(World world, final int x, final int z, final org.bukkit.Chunk chunk) {
        final Chunk handle = CommonNMS.getNative(chunk);
        if (handle != null && handle.bukkitChunk == null) {
            throw new RuntimeException("Can not put a chunk that has no BukkitChunk");
        }
        if (canUseLongObjectHashMap) {
            Object cps = CommonNMS.getNative(world).chunkProviderServer;
            Object chunks = ChunkProviderServerRef.chunks.get(cps);
            if (chunks != null) {
                final long key = LongHash.toLong(x, z);
                try {
                    if (canUseLongObjectHashMap && chunks instanceof LongObjectHashMap) {
                        if (handle == null) {
                            // Remove the chunk
                            Object removed = ((LongObjectHashMap) chunks).remove(key);
                            if (removed != null && chunkListField != null) {
                                chunkListField.get(cps).remove(removed);
                            }
                        } else {
                            // Add the chunk
                            Object oldChunk = ((LongObjectHashMap) chunks).put(key, handle);
                            if (chunkListField != null) {
                                List<Object> chunkList = chunkListField.get(cps);
                                if (oldChunk != null) {
                                    chunkList.remove(oldChunk);
                                }
                                chunkList.add(handle);
                            }
                        }
                        return;
                    }
                } catch (Throwable t) {
                    canUseLongObjectHashMap = false;
                    CommonPlugin.LOGGER.log(Level.WARNING, "Failed to access chunks using CraftBukkit's long object hashmap, support disabled");
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
        CommonNMS.getNative(chunk.getWorld()).chunkProviderServer.saveChunk(CommonNMS.getNative(chunk));
    }

    /**
     * Gets whether a chunk needs to be saved
     *
     * @param chunk to check
     * @return True if it needs to be saved, False if not
     */
    public static boolean needsSaving(org.bukkit.Chunk chunk) {
        return ChunkRef.needsSaving(Conversion.toChunkHandle.convert(chunk));
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
            Object unloadQueue = ChunkProviderServerRef.unloadQueue.get(CommonNMS.getNative(world).chunkProviderServer);
            if (unloadQueue != null) {
                try {
                    if (canUseLongHashSet && unloadQueue instanceof LongHashSet) {
                        if (unload) {
                            ((LongHashSet) unloadQueue).add(x, z);
                        } else {
                            ((LongHashSet) unloadQueue).remove(x, z);
                        }
                        return;
                    }
                } catch (Throwable t) {
                    canUseLongHashSet = false;
                    CommonPlugin.LOGGER.log(Level.WARNING, "Failed to access chunks using CraftBukkit's long object hashmap, support disabled");
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
     * @return collection of Block States (mutual)
     */
    public static Collection<BlockState> getBlockStates(org.bukkit.Chunk chunk) {
        return ConversionPairs.blockState.convertAll(CommonNMS.getNative(chunk).tileEntities.values());
    }

    /**
     * Adds an Entity to a Chunk
     *
     * @param chunk to add an entity to
     * @param entity to add
     */
    public static void addEntity(org.bukkit.Chunk chunk, org.bukkit.entity.Entity entity) {
        CommonNMS.getNative(chunk).a(CommonNMS.getNative(entity));
    }

    /**
     * Removes an Entity from a Chunk
     *
     * @param chunk to remove an entity from
     * @param entity to remove
     * @return True if the entity has been removed, False if not (not found)
     */
    @SuppressWarnings("unchecked")
    public static boolean removeEntity(org.bukkit.Chunk chunk, org.bukkit.entity.Entity entity) {
        final EntitySlice[] slices = CommonNMS.getNative(chunk).entitySlices;
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
