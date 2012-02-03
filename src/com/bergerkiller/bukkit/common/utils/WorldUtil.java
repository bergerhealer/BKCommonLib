package com.bergerkiller.bukkit.common.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftChunk;
import org.bukkit.craftbukkit.CraftWorld;

import net.minecraft.server.Chunk;
import net.minecraft.server.ChunkProviderServer;
import net.minecraft.server.EntityTracker;
import net.minecraft.server.World;
import net.minecraft.server.WorldServer;

public class WorldUtil {
	
	public static List<WorldServer> getWorlds() {
		try {
			List<WorldServer> worlds = CommonUtil.getMCServer().worlds;
			if (worlds != null) return worlds;
		} catch (NullPointerException ex) {}
		return new ArrayList<WorldServer>();
	}
	public static WorldServer getNative(org.bukkit.World world) {
		return ((CraftWorld) world).getHandle();
	}
	public static Chunk getNative(org.bukkit.Chunk chunk) {
		return ((CraftChunk) chunk).getHandle();
	}
	
	public static org.bukkit.Chunk getChunk(org.bukkit.World world, final int x, final int z) {
		Chunk chunk = getChunk(getNative(world), x, z);
		return chunk == null ? null : chunk.bukkitChunk;
	}
	public static Chunk getChunk(World world, final int x, final int z) {
		return getChunk(((WorldServer) world).chunkProviderServer, x, z);
	}
	public static Chunk getChunk(ChunkProviderServer chunkprovider, final int x, final int z) {
		return chunkprovider.chunks.get(x, z);
	}
	
	public static EntityTracker getTracker(org.bukkit.World world) {
		return getTracker(getNative(world));
	}
	public static EntityTracker getTracker(World world) {
		return ((WorldServer) world).tracker;
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
		return isLoaded(location.getWorld(), location.getX(), location.getY(), location.getZ());
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
		if (world == null) return false;
		return world.isChunkLoaded(chunkX, chunkZ);
	}
	
}
