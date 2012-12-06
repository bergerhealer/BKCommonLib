package com.bergerkiller.bukkit.common.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.craftbukkit.util.LongHash;

import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.natives.NativeChunkWrapper;
import com.bergerkiller.bukkit.common.reflection.classes.ChunkProviderServerRef;

/**
 * Contains utilities to get and set chunks of a world
 */
public class ChunkUtil {
	private static boolean canUseLongObjectHashMap = CommonUtil.getClass("org.bukkit.craftbukkit.util.LongObjectHashMap") != null;
	private static boolean canUseLongHashSet = CommonUtil.getClass("org.bukkit.craftbukkit.util.LongHashSet") != null;

	/**
	 * Gets all the chunks loaded on a given world
	 * 
	 * @param chunkprovider to get the loaded chunks from
	 * @return Loaded chunks
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static Collection<Chunk> getChunks(World world) {
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
	public static Chunk getChunk(World world, final int x, final int z) {
		final long key = LongHash.toLong(x, z);
		Object chunks = ChunkProviderServerRef.chunks.get(NativeUtil.getNative(world).chunkProviderServer);
		if (chunks != null) {
			if (canUseLongObjectHashMap) {
				try {
					if (canUseLongObjectHashMap) {
						if (chunks instanceof org.bukkit.craftbukkit.util.LongObjectHashMap) {
							return ((net.minecraft.server.Chunk) ((org.bukkit.craftbukkit.util.LongObjectHashMap) chunks).get(key)).bukkitChunk;
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
	public static void setChunk(World world, final int x, final int z, final Chunk chunk) {
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
