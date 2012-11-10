package com.bergerkiller.bukkit.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftChunk;
import org.bukkit.craftbukkit.CraftWorld;

import com.bergerkiller.bukkit.common.reflection.classes.CraftServerRef;
import com.bergerkiller.bukkit.common.reflection.classes.EntityTrackerRef;

import net.minecraft.server.Chunk;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityTracker;
import net.minecraft.server.EntityTrackerEntry;
import net.minecraft.server.World;
import net.minecraft.server.WorldServer;

public class WorldUtil extends ChunkUtil {
	/**
	 * Obtains the internal mapping which maps worlds to world names
	 * 
	 * @return A map of world names as keys and Bukkit worlds as values
	 */
	public static Map<String, org.bukkit.World> getWorldsMap() {
		return CraftServerRef.worlds.get(CommonUtil.getCraftServer());
	}

	/**
	 * Obtains the internal list of native Minecraft server worlds
	 * 
	 * @return A list of WorldServer instances
	 */
	public static List<WorldServer> getWorlds() {
		try {
			List<WorldServer> worlds = CommonUtil.getMCServer().worlds;
			if (worlds != null)
				return worlds;
		} catch (NullPointerException ex) {
		}
		return new ArrayList<WorldServer>();
	}

	@SuppressWarnings("unchecked")
	public static List<Entity> getEntities(World world) {
		return world.entityList;
	}

	public static WorldServer getNative(org.bukkit.World world) {
		return ((CraftWorld) world).getHandle();
	}

	public static Chunk getNative(org.bukkit.Chunk chunk) {
		return ((CraftChunk) chunk).getHandle();
	}

	/**
	 * Gets the Entity Tracker for the world specified
	 * 
	 * @param world to get the tracker for
	 * @return world Entity Tracker
	 */
	public static EntityTracker getTracker(org.bukkit.World world) {
		return getTracker(getNative(world));
	}

	/**
	 * Gets the Entity Tracker for the world specified
	 * 
	 * @param world to get the tracker for
	 * @return world Entity Tracker
	 */
	public static EntityTracker getTracker(World world) {
		return ((WorldServer) world).tracker;
	}

	/**
	 * Gets the tracker entry of the entity specified
	 * 
	 * @param entity to get it for
	 * @return entity tracker entry, or null if none is set
	 */
	public static EntityTrackerEntry getTrackerEntry(Entity entity) {
		return (EntityTrackerEntry) WorldUtil.getTracker(entity.world).trackedEntities.get(entity.id);
	}

	/**
	 * Sets a new entity tracker entry for the entity specified
	 * 
	 * @param entity to set it for
	 * @param tracker to set to (can be null to remove only)
	 * @return the previous tracker entry for the entity, or null if there was none
	 */
	public static EntityTrackerEntry setTrackerEntry(Entity entity, EntityTrackerEntry tracker) {
		EntityTracker t = getTracker(entity.world);
		Set<EntityTrackerEntry> trackers = EntityTrackerRef.trackerSet.get(t);
		synchronized (t) {
			EntityTrackerEntry old = (EntityTrackerEntry) t.trackedEntities.d(entity.id);
			if (old != null) {
				trackers.remove(old);
			}
			if (tracker != null) {
				trackers.add(tracker);
				t.trackedEntities.a(entity.id, tracker);
			}
			return old;
		}
	}

	public static void loadChunks(Location location, final int radius) {
		loadChunks(location.getWorld(), location.getX(), location.getZ(), radius);
	}

	public static void loadChunks(org.bukkit.World world, double xmid, double zmid, final int radius) {
		loadChunks(world, MathUtil.locToChunk(xmid), MathUtil.locToChunk(zmid), radius);
	}

	public static void loadChunks(org.bukkit.World world, final int xmid, final int zmid, final int radius) {
		for (int cx = xmid - radius; cx <= xmid + radius; cx++) {
			for (int cz = zmid - radius; cz <= zmid + radius; cz++) {
				world.getChunkAt(cx, cz);
			}
		}
	}

	public static boolean isLoaded(Location location) {
		return isLoaded(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

	public static boolean isLoaded(Block block) {
		return isLoaded(block.getWorld(), block.getX(), block.getY(), block.getZ());
	}

	public static boolean isLoaded(final org.bukkit.World world, double x, double y, double z) {
		return isLoaded(world, MathUtil.locToChunk(x), MathUtil.locToChunk(z));
	}

	public static boolean isLoaded(final org.bukkit.World world, int x, int y, int z) {
		return isLoaded(world, x >> 4, z >> 4);
	}

	public static boolean isLoaded(final org.bukkit.World world, final int chunkX, final int chunkZ) {
		if (world == null) {
			return false;
		}
		return world.isChunkLoaded(chunkX, chunkZ);
	}
}
