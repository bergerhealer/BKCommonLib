package com.bergerkiller.bukkit.common.utils;

import net.minecraft.server.v1_4_R1.ChunkCoordIntPair;
import net.minecraft.server.v1_4_R1.EntityHuman;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeMethod;

public class EntityPropertyUtil extends EntityGroupingUtil {
	private static final MethodAccessor<Void> setFirstPlayed = new SafeMethod<Void>(CommonUtil.getCBClass("entity.CraftPlayer"), "setFirstPlayed", long.class);

	public static double getLocX(Entity entity) {
		return NativeUtil.getNative(entity).locX;
	}

	public static void setLocX(Entity entity, double value) {
		NativeUtil.getNative(entity).locX = value;
	}

	public static double getLocY(Entity entity) {
		return NativeUtil.getNative(entity).locY;
	}

	public static void setLocY(Entity entity, double value) {
		NativeUtil.getNative(entity).locY = value;
	}

	public static double getLocZ(Entity entity) {
		return NativeUtil.getNative(entity).locZ;
	}

	public static void setLocZ(Entity entity, double value) {
		NativeUtil.getNative(entity).locZ = value;
	}

	public static double getLastX(Entity entity) {
		return NativeUtil.getNative(entity).lastX;
	}

	public static void setLastX(Entity entity, double value) {
		NativeUtil.getNative(entity).lastX = value;
	}

	public static double getLastY(Entity entity) {
		return NativeUtil.getNative(entity).lastY;
	}

	public static void setLastY(Entity entity, double value) {
		NativeUtil.getNative(entity).lastY = value;
	}

	public static double getLastZ(Entity entity) {
		return NativeUtil.getNative(entity).lastZ;
	}

	public static void setLastZ(Entity entity, double value) {
		NativeUtil.getNative(entity).lastZ = value;
	}

	public static void queueChunkSend(Player player, Chunk chunk) {
		queueChunkSend(player, chunk.getX(), chunk.getZ());
	}

	@SuppressWarnings("unchecked")
	public static void queueChunkSend(Player player, int chunkX, int chunkZ) {
		NativeUtil.getNative(player).chunkCoordIntPairQueue.add(new ChunkCoordIntPair(chunkX, chunkZ));
	}

	public static void cancelChunkSend(Player player, Chunk chunk) {
		cancelChunkSend(player, chunk.getX(), chunk.getZ());
	}

	public static void cancelChunkSend(Player player, int chunkX, int chunkZ) {
		NativeUtil.getNative(player).chunkCoordIntPairQueue.remove(new ChunkCoordIntPair(chunkX, chunkZ));
	}

	/**
	 * Sets the invulerability state of an Entity
	 * 
	 * @param entity to set it for
	 * @param state to set to
	 */
	public static void setInvulnerable(Entity entity, boolean state) {
		if (entity instanceof HumanEntity) {
			NativeUtil.getNative(entity, EntityHuman.class).abilities.isInvulnerable = state;
		}
	}

	/**
	 * Gets the invulerability state of an Entity
	 * 
	 * @param entity to get it for
	 * @return invulnerability state
	 */
	public static boolean isInvulnerable(org.bukkit.entity.Entity entity) {
		if (entity instanceof HumanEntity) {
			return NativeUtil.getNative(entity, EntityHuman.class).abilities.isInvulnerable;
		}
		return false;
	}

	/**
	 * Sets the first time a player played on a server or world
	 * 
	 * @param player to set it for
	 * @param firstPlayed time
	 */
	public static void setFirstPlayed(org.bukkit.entity.Player player, long firstPlayed) {
		setFirstPlayed.invoke(player, firstPlayed);
	}
}
