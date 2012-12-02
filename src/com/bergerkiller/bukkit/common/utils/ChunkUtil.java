package com.bergerkiller.bukkit.common.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;

import net.minecraft.server.Chunk;
import net.minecraft.server.ChunkProviderServer;
import net.minecraft.server.World;
import net.minecraft.server.WorldServer;

import org.bukkit.craftbukkit.util.LongHash;

import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.reflection.classes.ChunkProviderServerRef;

/**
 * Contains utilities to get and set chunks of a world
 */
public class ChunkUtil {
	private static boolean canUseLongObjectHashMap = CommonUtil.getClass("org.bukkit.craftbukkit.util.LongObjectHashMap") != null;
	private static boolean canUseLongHashSet = CommonUtil.getClass("org.bukkit.craftbukkit.util.LongHashSet") != null;

	/**
	 * Gets a chunk from a world without loading or generating it
	 * 
	 * @param world to obtain the chunk from
	 * @param x coordinate of the chunk
	 * @param z coordinate of the chunk
	 * @return The chunk, or null if it is not loaded
	 */
	public static org.bukkit.Chunk getChunk(org.bukkit.World world, final int x, final int z) {
		Chunk chunk = getChunk(WorldUtil.getNative(world), x, z);
		return chunk == null ? null : chunk.bukkitChunk;
	}

	/**
	 * Gets all the chunks loaded on a given world
	 * 
	 * @param world to get the loaded chunks from
	 * @return Loaded chunks
	 */
	public static Collection<Chunk> getChunks(World world) {
		return getChunks(((WorldServer) world).chunkProviderServer);
	}

	/**
	 * Gets all the chunks loaded on a given world
	 * 
	 * @param chunkprovider to get the loaded chunks from
	 * @return Loaded chunks
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static Collection<Chunk> getChunks(ChunkProviderServer chunkprovider) {
		if (canUseLongObjectHashMap) {
			Object chunks = ChunkProviderServerRef.chunks.get(chunkprovider);
			if (chunks != null) {
				try {
					if (canUseLongObjectHashMap) {
						if (chunks instanceof org.bukkit.craftbukkit.util.LongObjectHashMap) {
							return ((org.bukkit.craftbukkit.util.LongObjectHashMap) chunks).values();
						}
					}
				} catch (Throwable t) {
					canUseLongObjectHashMap = false;
					CommonPlugin.instance.log(Level.WARNING, "Failed to access chunks using CraftBukkit's long object hashmap, support disabled");
					CommonUtil.filterStackTrace(t).printStackTrace();
				}
			}
		}
		// Bukkit alternative
		org.bukkit.Chunk[] bChunks = chunkprovider.world.getWorld().getLoadedChunks();
		Chunk[] rval = new Chunk[bChunks.length];
		for (int i = 0; i < rval.length; i++) {
			rval[i] = WorldUtil.getNative(bChunks[i]);
		}
		return Arrays.asList(rval);
	}

	/**
	 * Gets a chunk from a world without loading or generating it
	 * 
	 * @param world to obtain the chunk from
	 * @param x coordinate of the chunk
	 * @param z coordinate of the chunk
	 * @return The chunk, or null if it is not loaded
	 */
	public static Chunk getChunk(World world, final int x, final int z) {
		return getChunk(((WorldServer) world).chunkProviderServer, x, z);
	}

	/**
	 * Gets a chunk from a world without loading or generating it
	 * 
	 * @param chunkprovider to obtain the chunk from
	 * @param x coordinate of the chunk
	 * @param z coordinate of the chunk
	 * @return The chunk, or null if it is not loaded
	 */
	@SuppressWarnings("rawtypes")
	public static Chunk getChunk(ChunkProviderServer chunkprovider, final int x, final int z) {
		if (canUseLongObjectHashMap) {
			Object chunks = ChunkProviderServerRef.chunks.get(chunkprovider);
			if (chunks != null) {
				final long key = LongHash.toLong(x, z);
				try {
					if (canUseLongObjectHashMap) {
						if (chunks instanceof org.bukkit.craftbukkit.util.LongObjectHashMap) {
							return (Chunk) ((org.bukkit.craftbukkit.util.LongObjectHashMap) chunks).get(key);
						}
					}
				} catch (Throwable t) {
					canUseLongObjectHashMap = false;
					CommonPlugin.instance.log(Level.WARNING, "Failed to access chunks using CraftBukkit's long object hashmap, support disabled");
					CommonUtil.filterStackTrace(t).printStackTrace();
				}
			}
		}
		// Bukkit alternative
		if (chunkprovider.isChunkLoaded(x, z)) {
			return chunkprovider.getChunkAt(x, z);
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
	public static void setChunk(World world, final int x, final int z, final Chunk chunk) {
		setChunk(((WorldServer) world).chunkProviderServer, x, z, chunk);
	}

	/**
	 * Sets a given chunk coordinate to contain the chunk specified
	 * 
	 * @param chunkprovider to set the chunk in
	 * @param x coordinate of the chunk
	 * @param z coordinate of the chunk
	 * @param chunk to set to
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void setChunk(ChunkProviderServer chunkprovider, final int x, final int z, final Chunk chunk) {
		if (canUseLongObjectHashMap) {
			Object chunks = ChunkProviderServerRef.chunks.get(chunkprovider);
			if (chunks != null) {
				final long key = LongHash.toLong(x, z);
				try {
					if (canUseLongObjectHashMap) {
						if (chunks instanceof org.bukkit.craftbukkit.util.LongObjectHashMap) {
							((org.bukkit.craftbukkit.util.LongObjectHashMap) chunks).put(key, chunk);
							return;
						}
					}
				} catch (Throwable t) {
					canUseLongObjectHashMap = false;
					CommonPlugin.instance.log(Level.WARNING, "Failed to access chunks using CraftBukkit's long object hashmap, support disabled");
					CommonUtil.filterStackTrace(t).printStackTrace();
				}
			}
		}
		throw new RuntimeException("Failed to set chunk using a known method");
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
		setChunkUnloading(((WorldServer) world).chunkProviderServer, x, z, unload);
	}

	/**
	 * Sets whether a given chunk coordinate has to be unloaded
	 * 
	 * @param chunkprovider to set the unload request for
	 * @param x coordinate of the chunk
	 * @param z coordinate of the chunk
	 * @param unload state to set to
	 */
	public static void setChunkUnloading(ChunkProviderServer chunkprovider, final int x, final int z, boolean unload) {
		if (canUseLongHashSet) {
			Object unloadQueue = ChunkProviderServerRef.unloadQueue.get(chunkprovider);
			if (unloadQueue != null) {
				try {
					if (canUseLongHashSet) {
						if (unloadQueue instanceof org.bukkit.craftbukkit.util.LongHashSet) {
							if (unload) {
								((org.bukkit.craftbukkit.util.LongHashSet) unloadQueue).remove(x, z);
							} else {
								((org.bukkit.craftbukkit.util.LongHashSet) unloadQueue).add(x, z);
							}
							return;
						}
					}
				} catch (Throwable t) {
					canUseLongHashSet = false;
					CommonPlugin.instance.log(Level.WARNING, "Failed to access chunks using CraftBukkit's long object hashmap, support disabled");
					CommonUtil.filterStackTrace(t).printStackTrace();
				}
			}
		}
		throw new RuntimeException("Failed to set unload queue using a known method");
	}
}
