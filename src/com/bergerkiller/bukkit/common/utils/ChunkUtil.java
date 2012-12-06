package com.bergerkiller.bukkit.common.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;

import net.minecraft.server.Chunk;
import net.minecraft.server.ChunkSection;
import net.minecraft.server.WorldServer;

import org.bukkit.World;
import org.bukkit.craftbukkit.util.LongHash;

import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.natives.NativeChunkEntitiesWrapper;
import com.bergerkiller.bukkit.common.natives.NativeChunkWrapper;
import com.bergerkiller.bukkit.common.reflection.classes.ChunkProviderServerRef;

/**
 * Contains utilities to get and set chunks of a world
 */
public class ChunkUtil {
	private static boolean canUseLongObjectHashMap = CommonUtil.getClass("org.bukkit.craftbukkit.util.LongObjectHashMap") != null;
	private static boolean canUseLongHashSet = CommonUtil.getClass("org.bukkit.craftbukkit.util.LongHashSet") != null;

	/**
	 * Gets the block data
	 * 
	 * @param chunk the block is in
	 * @param x-coordinate of the block
	 * @param y-coordinate of the block
	 * @param z-coordinate of the block
	 * @return block data
	 */
	public static int getBlockData(org.bukkit.Chunk chunk, int x, int y, int z) {
		return NativeUtil.getNative(chunk).getData(x & 15, y & 255, z & 15);
	}

	/**
	 * Gets the block type Id
	 * 
	 * @param chunk the block is in
	 * @param x-coordinate of the block
	 * @param y-coordinate of the block
	 * @param z-coordinate of the block
	 * @return block type Id
	 */
	public static int getBlockTypeId(org.bukkit.Chunk chunk, int x, int y, int z) {
		return NativeUtil.getNative(chunk).getTypeId(x & 15, y & 255, z & 15);
	}

	/**
	 * Sets a block type id and data without causing physics or lighting updates
	 * 
	 * @param chunk the block is in
	 * @param x-coordinate of the block
	 * @param y-coordinate of the block
	 * @param z-coordinate of the block
	 * @param typeId to set to
	 * @param data to set to
	 */
	public static void setBlockFast(org.bukkit.Chunk chunk, int x, int y, int z, int typeId, int data) {
		x &= 15;
		y &= 255;
		z &= 15;
		ChunkSection[] sections = NativeUtil.getNative(chunk).i();
		final int secIndex = y >> 4;
		ChunkSection section = sections[secIndex];
		if (section == null) {
			sections[secIndex] = section = new ChunkSection(y >> 4 << 4);
		}
		section.a(x, y & 15, z, typeId);
		section.b(x, y & 15, z, data);
	}

	/**
	 * Sets a block type id and data, causing physics and lighting updates
	 * 
	 * @param chunk the block is in
	 * @param x-coordinate of the block
	 * @param y-coordinate of the block
	 * @param z-coordinate of the block
	 * @param typeId to set to
	 * @param data to set to
	 * @return True if a block got changed, False if not
	 */
	public static boolean setBlock(org.bukkit.Chunk chunk, int x, int y, int z, int typeId, int data) {
		boolean result = y >= 0 && y <= chunk.getWorld().getMaxHeight();
		WorldServer world = NativeUtil.getNative(chunk.getWorld());
		if (result) {
			result = NativeUtil.getNative(chunk).a(x & 15, y & 255, z & 15, typeId, data);
            world.methodProfiler.a("checkLight");
            world.z(x, y, z);
            world.methodProfiler.b();
		}
		if (result) {
			world.applyPhysics(x, y, z, typeId);
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
	public static Collection<org.bukkit.entity.Entity> getEntities(org.bukkit.Chunk chunk) {
		return new NativeChunkEntitiesWrapper(chunk);
	}

	/**
	 * Gets all the chunks loaded on a given world
	 * 
	 * @param chunkprovider to get the loaded chunks from
	 * @return Loaded chunks
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static Collection<org.bukkit.Chunk> getChunks(World world) {
		if (canUseLongObjectHashMap) {
			Object chunks = ChunkProviderServerRef.chunks.get(NativeUtil.getNative(world).chunkProviderServer);
			if (chunks != null) {
				try {
					if (canUseLongObjectHashMap) {
						if (chunks instanceof org.bukkit.craftbukkit.util.LongObjectHashMap) {
							return new NativeChunkWrapper(((org.bukkit.craftbukkit.util.LongObjectHashMap) chunks).values());
						}
					}
				} catch (Throwable t) {
					canUseLongObjectHashMap = false;
					CommonPlugin.getInstance().log(Level.WARNING, "Failed to access chunks using CraftBukkit's long object hashmap, support disabled");
					CommonUtil.filterStackTrace(t).printStackTrace();
				}
			}
		}
		// Bukkit alternative
		return Arrays.asList(world.getLoadedChunks());
	}

	/**
	 * Gets a chunk from a world without loading or generating it
	 * 
	 * @param world to obtain the chunk from
	 * @param x coordinate of the chunk
	 * @param z coordinate of the chunk
	 * @return The chunk, or null if it is not loaded
	 */
	@SuppressWarnings("rawtypes")
	public static org.bukkit.Chunk getChunk(World world, final int x, final int z) {
		final long key = LongHash.toLong(x, z);
		Object chunks = ChunkProviderServerRef.chunks.get(NativeUtil.getNative(world).chunkProviderServer);
		if (chunks != null) {
			if (canUseLongObjectHashMap) {
				try {
					if (canUseLongObjectHashMap) {
						if (chunks instanceof org.bukkit.craftbukkit.util.LongObjectHashMap) {
							return NativeUtil.getChunk(((Chunk) ((org.bukkit.craftbukkit.util.LongObjectHashMap) chunks).get(key)));
						}
					}
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
	 * Sets a given chunk coordinate to contain the chunk specified
	 * 
	 * @param world to set the chunk in
	 * @param x coordinate of the chunk
	 * @param z coordinate of the chunk
	 * @param chunk to set to
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void setChunk(World world, final int x, final int z, final org.bukkit.Chunk chunk) {
		if (canUseLongObjectHashMap) {
			Object chunks = ChunkProviderServerRef.chunks.get(NativeUtil.getNative(world).chunkProviderServer);
			if (chunks != null) {
				final long key = LongHash.toLong(x, z);
				try {
					if (canUseLongObjectHashMap) {
						if (chunks instanceof org.bukkit.craftbukkit.util.LongObjectHashMap) {
							((org.bukkit.craftbukkit.util.LongObjectHashMap) chunks).put(key, NativeUtil.getNative(chunk));
							return;
						}
					}
				} catch (Throwable t) {
					canUseLongObjectHashMap = false;
					CommonPlugin.getInstance().log(Level.WARNING, "Failed to access chunks using CraftBukkit's long object hashmap, support disabled");
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
		NativeUtil.getNative(chunk.getWorld()).chunkProviderServer.saveChunk(NativeUtil.getNative(chunk));
	}

	/**
	 * Sets whether a given chunk coordinate has to be unloaded
	 * 
	 * @param world to set the unload request for
	 * @param x coordinate of the chunk
	 * @param z coordinate of the chunk
	 * @param unload state to set to
	 */
	public static void setChunkUnloading(World world, final int x, final int z, boolean unload) {
		if (canUseLongHashSet) {
			Object unloadQueue = ChunkProviderServerRef.unloadQueue.get(NativeUtil.getNative(world).chunkProviderServer);
			if (unloadQueue != null) {
				try {
					if (canUseLongHashSet) {
						if (unloadQueue instanceof org.bukkit.craftbukkit.util.LongHashSet) {
							if (unload) {
								((org.bukkit.craftbukkit.util.LongHashSet) unloadQueue).add(x, z);
							} else {
								((org.bukkit.craftbukkit.util.LongHashSet) unloadQueue).remove(x, z);
							}
							return;
						}
					}
				} catch (Throwable t) {
					canUseLongHashSet = false;
					CommonPlugin.getInstance().log(Level.WARNING, "Failed to access chunks using CraftBukkit's long object hashmap, support disabled");
					CommonUtil.filterStackTrace(t).printStackTrace();
				}
			}
		}
		throw new RuntimeException("Failed to set unload queue using a known method");
	}
}
