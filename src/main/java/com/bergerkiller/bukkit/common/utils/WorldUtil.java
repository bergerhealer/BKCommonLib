package com.bergerkiller.bukkit.common.utils;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_4_R1.CraftTravelAgent;
import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.reflection.classes.CraftServerRef;
import com.bergerkiller.bukkit.common.reflection.classes.EntityTrackerRef;
import com.bergerkiller.bukkit.common.reflection.classes.WorldServerRef;

import net.minecraft.server.v1_4_R1.Entity;
import net.minecraft.server.v1_4_R1.EntityTracker;
import net.minecraft.server.v1_4_R1.EntityTrackerEntry;
import net.minecraft.server.v1_4_R1.IDataManager;
import net.minecraft.server.v1_4_R1.World;
import net.minecraft.server.v1_4_R1.WorldNBTStorage;
import net.minecraft.server.v1_4_R1.WorldServer;

public class WorldUtil extends ChunkUtil {

	/**
	 * Gets the shared Random of a world
	 * 
	 * @param world to get the Random of
	 * @return Random generator of a world
	 */
	public static Random getRandom(org.bukkit.World world) {
		return NativeUtil.getNative(world).random;
	}
	
	/**
	 * Sets if the spawn chunk should be kept in memory
	 * 
	 * @param world World to apply value on
	 * @param value Keep in memory or not?
	 */
	public static void setKeepSpawnInMemory(org.bukkit.World world, boolean value) {
		NativeUtil.getNative(world).keepSpawnInMemory = value;
	}

	/**
	 * Removes a single entity from the world
	 * 
	 * @param entity to remove
	 */
	public static void removeEntity(org.bukkit.entity.Entity entity) {
		Entity e = NativeUtil.getNative(entity);
		e.world.removeEntity(e);
		((EntityTracker) getTracker(entity.getWorld())).untrackEntity(e);
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
	 * Gets the folder where world data of a certain world is saved in
	 * 
	 * @param world (can not be null)
	 * @return world folder
	 */
	public static File getWorldFolder(org.bukkit.World world) {
		return getWorldFolder(world.getName());
	}

	/**
	 * Gets the folder where world data for a certain world name is saved in
	 * 
	 * @param worldName (can not be null)
	 * @return world folder
	 */
	public static File getWorldFolder(String worldName) {
		return new File(Bukkit.getWorldContainer(), worldName);
	}

	/**
	 * Attempts to find a suitable spawn location, searching from the startLocation specified
	 * 
	 * @param startLocation to find a spawn from
	 * @return spawn location, or null if this failed
	 */
	public static Location findSpawnLocation(Location startLocation) {
		WorldServer ws = (WorldServer) Conversion.toWorldHandle.convert(startLocation.getWorld());
		return new CraftTravelAgent(ws).findOrCreate(startLocation);
	}

	/**
	 * Gets the folder where player data of a certain world is saved in
	 * 
	 * @param world (can not be null)
	 * @return players folder
	 */
	public static File getPlayersFolder(org.bukkit.World world) {
		IDataManager man = NativeUtil.getNative(world).getDataManager();
		if (man instanceof WorldNBTStorage) {
			return ((WorldNBTStorage) man).getPlayerDir();
		}
		return new File(getWorldFolder(world), "players");
	}

	/**
	 * Gets the dimension Id of a world
	 * 
	 * @param world to get from
	 * @return world dimension Id
	 */
	public static int getDimension(org.bukkit.World world) {
		return ((World) Conversion.toWorldHandle.convert(world)).worldProvider.dimension;
	}

	/**
	 * Gets the server a world object is running on
	 * 
	 * @param world to get the server of
	 * @return server
	 */
	public static Server getServer(org.bukkit.World world) {
		return WorldServerRef.getServer(Conversion.toWorldHandle.convert(world));
	}

	/**
	 * Gets the Entity Tracker for the world specified
	 * 
	 * @param world to get the tracker for
	 * @return world Entity Tracker
	 */
	public static Object getTracker(org.bukkit.World world) {
		return getTracker(NativeUtil.getNative(world));
	}

	/**
	 * Gets the Entity Tracker for the world specified
	 * 
	 * @param world to get the tracker for
	 * @return world Entity Tracker
	 */
	public static Object getTracker(World world) {
		return ((WorldServer) world).tracker;
	}

	/**
	 * Gets the tracker entry of the entity specified
	 * 
	 * @param entity to get it for
	 * @return entity tracker entry, or null if none is set
	 */
	public static Object getTrackerEntry(Entity entity) {
		return ((EntityTracker) WorldUtil.getTracker(entity.world)).trackedEntities.get(entity.id);
	}

	/**
	 * Sets a new entity tracker entry for the entity specified
	 * 
	 * @param entity to set it for
	 * @param entityTrackerEntrytracker to set to (can be null to remove only)
	 * @return the previous tracker entry for the entity, or null if there was none
	 */
	public static Object setTrackerEntry(Entity entity, Object entityTrackerEntry) {
		EntityTracker t = (EntityTracker) getTracker(entity.world);
		Set<Object> trackers = EntityTrackerRef.trackerSet.get(t);
		synchronized (t) {
			Object old = (EntityTrackerEntry) t.trackedEntities.d(entity.id);
			if (old != null) {
				trackers.remove(old);
			}
			if (entityTrackerEntry != null) {
				trackers.add(entityTrackerEntry);
				t.trackedEntities.a(entity.id, entityTrackerEntry);
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
