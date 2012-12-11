package com.bergerkiller.bukkit.common.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.reflection.classes.CraftServerRef;
import com.bergerkiller.bukkit.common.reflection.classes.EntityTrackerRef;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityTracker;
import net.minecraft.server.EntityTrackerEntry;
import net.minecraft.server.World;
import net.minecraft.server.WorldServer;

public class WorldUtil extends ChunkUtil {

	/**
	 * Removes a single entity from the world
	 * 
	 * @param entity to remove
	 */
	public static void removeEntity(org.bukkit.entity.Entity entity) {
		Entity e = NativeUtil.getNative(entity);
		e.world.removeEntity(e);
		getTracker(entity.getWorld()).untrackEntity(e);
	}

	/**
	 * Removes a world from all global locations where worlds are mapped
	 * 
	 * @param world to remove
	 */
	public static void removeWorld(org.bukkit.World world) {
		// Remove the world from the Bukkit worlds mapping
		Iterator<org.bukkit.World> iter = CraftServerRef.worlds.values().iterator();
		while (iter.hasNext()) {
			if (iter.next() == world) {
				iter.remove();
			}
		}
		// Remove the world from the MinecraftServer worlds mapping
		NativeUtil.getWorlds().remove(NativeUtil.getNative(world));
	}

	/**
	 * Obtains the internally stored collection of worlds<br>
	 * Gets the values from the CraftServer.worlds map
	 * 
	 * @return A collection of World instances
	 */
	public static Collection<org.bukkit.World> getWorlds() {
		return CraftServerRef.worlds.values();
	}

	/**
	 * Gets a live collection (allows modification in the world) of entities on a given world
	 * 
	 * @param world the entities are on
	 * @return collection of entities on the world
	 */
	public static Collection<org.bukkit.entity.Entity> getEntities(org.bukkit.World world) {
		return NativeUtil.getEntities(NativeUtil.getNative(world).entityList);
	}

	/**
	 * Gets a live collection (allows modification in the world) of players on a given world
	 * 
	 * @param world the players are on
	 * @return collection of players on the world
	 */
	public static Collection<Player> getPlayers(org.bukkit.World world) {
		return NativeUtil.getPlayers(NativeUtil.getNative(world).players);
	}

	/**
	 * Gets the Entity Tracker for the world specified
	 * 
	 * @param world to get the tracker for
	 * @return world Entity Tracker
	 */
	public static EntityTracker getTracker(org.bukkit.World world) {
		return getTracker(NativeUtil.getNative(world));
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
		loadChunks(world, MathUtil.toChunk(xmid), MathUtil.toChunk(zmid), radius);
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

	public static boolean isLoaded(org.bukkit.World world, double x, double y, double z) {
		return isLoaded(world, MathUtil.toChunk(x), MathUtil.toChunk(z));
	}

	public static boolean isLoaded(org.bukkit.World world, int x, int y, int z) {
		return isLoaded(world, x >> 4, z >> 4);
	}

	public static boolean isLoaded(org.bukkit.World world, int chunkX, int chunkZ) {
		if (world == null) {
			return false;
		}
		return world.isChunkLoaded(chunkX, chunkZ);
	}

	public static boolean areChunksLoaded(org.bukkit.World world, int chunkCenterX, int chunkCenterZ, int chunkDistance) {
		return areBlocksLoaded(world, chunkCenterX << 4, chunkCenterZ << 4, chunkDistance << 4);
	}

	public static boolean areBlocksLoaded(org.bukkit.World world, int blockCenterX, int blockCenterZ, int distance) {
		return NativeUtil.getNative(world).areChunksLoaded(blockCenterX, 0, blockCenterZ, distance);
	}
}
