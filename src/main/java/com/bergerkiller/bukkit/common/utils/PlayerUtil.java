package com.bergerkiller.bukkit.common.utils;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.classes.EntityPlayerRef;
import com.bergerkiller.bukkit.common.reflection.classes.PlayerConnectionRef;

/**
 * Player - specific operations and tools
 */
public class PlayerUtil extends EntityUtil {
	private static final ClassTemplate<?> CRAFTPLAYER = ClassTemplate.create(CommonUtil.getCBClass("entity.CraftPlayer"));
	private static final MethodAccessor<Void> setFirstPlayed = CRAFTPLAYER.getMethod("setFirstPlayed", long.class);
	private static final FieldAccessor<Boolean> hasPlayedBefore = CRAFTPLAYER.getField("hasPlayedBefore");

	/**
	 * Gets whether a player is disconnected from the server
	 * 
	 * @param player to check
	 * @return True if the player is disconnected, False if not
	 */
	public static boolean isDisconnected(Player player) {
		final Object handle = Conversion.toEntityHandle.convert(player);
		if (handle == null) {
			return true;
		}
		final Object connection = EntityPlayerRef.playerConnection.get(handle);
		return connection == null || PlayerConnectionRef.disconnected.get(connection);
	}

	/**
	 * Adds the chunk coordinates of the chunk specified to the player chunk sending queue
	 * 
	 * @param player
	 * @param chunk
	 */
	public static void queueChunkSend(Player player, Chunk chunk) {
		queueChunkSend(player, chunk.getX(), chunk.getZ());
	}

	/**
	 * Adds the chunk coordinates to the player chunk sending queue
	 * 
	 * @param player
	 * @param chunkX - coordinate
	 * @param chunkZ - coordinate
	 */
	public static void queueChunkSend(Player player, int chunkX, int chunkZ) {
		queueChunkSend(player, new IntVector2(chunkX, chunkZ));
	}

	/**
	 * Adds the chunk coordinates to the player chunk sending queue
	 * 
	 * @param player
	 * @param coordinates
	 */
	@SuppressWarnings("unchecked")
	public static void queueChunkSend(Player player, IntVector2 coordinates) {
		CommonNMS.getNative(player).chunkCoordIntPairQueue.add(coordinates);
	}

	/**
	 * Removes the chunk coordinates from the player chunk sending queue
	 * 
	 * @param player
	 * @param chunk
	 */
	public static void cancelChunkSend(Player player, Chunk chunk) {
		cancelChunkSend(player, chunk.getX(), chunk.getZ());
	}

	/**
	 * Removes the chunk coordinates from the player chunk sending queue
	 * 
	 * @param player
	 * @param chunkX - coordinate
	 * @param chunkZ - coordinate
	 */
	public static void cancelChunkSend(Player player, int chunkX, int chunkZ) {
		cancelChunkSend(player, new IntVector2(chunkX, chunkZ));
	}

	/**
	 * Removes the chunk coordinates from the player chunk sending queue
	 * 
	 * @param player
	 * @param coordinates
	 */
	public static void cancelChunkSend(Player player, IntVector2 coordinates) {
		CommonNMS.getNative(player).chunkCoordIntPairQueue.remove(coordinates);
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

	/**
	 * Sets whether the player has played before on this server
	 * 
	 * @param player to set it for
	 * @param playedBefore state
	 */
	public static void setHasPlayedBefore(Player player, boolean playedBefore) {
		hasPlayedBefore.set(player, playedBefore);
	}

	/**
	 * Checks whether a given chunk is visible to the client of a player.
	 * This actually checks whether the chunk data had been sent, it doesn't do a distance check.
	 * 
	 * @param player to check
	 * @param chunk to check
	 * @return True if the chunk is visible to the player, False if not
	 */
	public static boolean isChunkVisible(Player player, Chunk chunk) {
		return isChunkVisible(player, chunk.getX(), chunk.getZ());
	}

	/**
	 * Checks whether a given chunk is visible to the client of a player.
	 * This actually checks whether the chunk data had been sent, it doesn't do a distance check.
	 * 
	 * @param player to check
	 * @param chunkX if the chunk to check
	 * @param chunkZ if the chunk to check
	 * @return True if the chunk is visible to the player, False if not
	 */
	public static boolean isChunkVisible(Player player, int chunkX, int chunkZ) {
		return CommonPlugin.getInstance().isChunkVisible(player, chunkX, chunkZ);
	}
}
