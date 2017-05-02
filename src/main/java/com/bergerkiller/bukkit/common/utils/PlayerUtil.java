package com.bergerkiller.bukkit.common.utils;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.ConversionPairs;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.mountiplex.conversion2.util.ConvertingList;
import com.bergerkiller.reflection.net.minecraft.server.NMSEntityHuman;
import com.bergerkiller.reflection.net.minecraft.server.NMSEntityPlayer;
import com.bergerkiller.reflection.net.minecraft.server.NMSNetworkManager;
import com.bergerkiller.reflection.net.minecraft.server.NMSPlayerConnection;
import com.bergerkiller.reflection.org.bukkit.craftbukkit.CBCraftPlayer;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_11_R1.EntityPlayer;
import net.minecraft.server.v1_11_R1.WorldServer;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;

import java.util.List;

/**
 * Player - specific operations and tools
 */
public class PlayerUtil extends EntityUtil {

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
        final Object connection = NMSEntityPlayer.playerConnection.get(handle);
        if (connection == null) {
            return true;
        }
        final Object network = NMSPlayerConnection.networkManager.get(connection);
        return network == null || !NMSNetworkManager.getIsOpen.invoke(network);
    }

    /**
     * Adds the chunk coordinates of the chunk specified to the player chunk
     * sending queue
     *
     * @param player
     * @param chunk
     */
    public static void queueChunkSend(Player player, Chunk chunk) {
        queueChunkSend(player, chunk.getX(), chunk.getZ());
    }

    /**
     * Gets a (referenced) list of all players nearby another Player
     *
     * @param player to get the nearby players of
     * @param radius to look around the player for other playrs
     * @return list of nearby players
     */
    public static List<Player> getNearbyPlayers(Player player, double radius) {
        EntityPlayer handle = CommonNMS.getNative(player);
        List<?> nearbyPlayerHandles = handle.world.a(EntityPlayer.class, handle.getBoundingBox().grow(radius, radius, radius));
        return new ConvertingList<Player>(nearbyPlayerHandles, ConversionPairs.player.toNew());
    }

    /**
     * Adds the chunk coordinates to the player chunk sending queue.<br><br>
     * 
     * BROKEN
     *
     * @param player
     * @param coordinates
     */
    @Deprecated
    public static void queueChunkSend(Player player, IntVector2 coordinates) {
        queueChunkSend(player, coordinates.x, coordinates.z);
    }

    /**
     * Adds the chunk coordinates to the player chunk sending queue<br><br>
     * 
     * BROKEN
     *
     * @param player
     * @param chunkX - coordinate
     * @param chunkZ - coordinate
     */
    @Deprecated
    public static void queueChunkSend(Player player, int chunkX, int chunkZ) {
        throw new RuntimeException("Queueing a chunk send for individual players is broken. Use WorldUtil.queueChunkSend instead.");
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
     * @param coordinates
     */
    public static void cancelChunkSend(Player player, IntVector2 coordinates) {
        cancelChunkSend(player, coordinates.x, coordinates.z);
    }

    /**
     * Removes the chunk coordinates from the player chunk sending queue
     *
     * @param player
     * @param chunkX - coordinate
     * @param chunkZ - coordinate
     */
    @Deprecated
    public static void cancelChunkSend(Player player, int chunkX, int chunkZ) {
//        CommonNMS.getNative(player).chunkCoordIntPairQueue.remove(VectorRef.newPair(chunkX, chunkZ));
    }

    /**
     * Sets the first time a player played on a server or world
     *
     * @param player to set it for
     * @param firstPlayed time
     */
    public static void setFirstPlayed(org.bukkit.entity.Player player, long firstPlayed) {
        CBCraftPlayer.setFirstPlayed.invoke(player, firstPlayed);
    }

    /**
     * Sets whether the player has played before on this server
     *
     * @param player to set it for
     * @param playedBefore state
     */
    public static void setHasPlayedBefore(Player player, boolean playedBefore) {
        CBCraftPlayer.hasPlayedBefore.set(player, playedBefore);
    }

    /**
     * Get the ping fomr a player
     *
     * @param player to get ping from
     * @return Ping (in ms)
     */
    public static int getPing(Player player) {
        return CommonNMS.getNative(player).ping;
    }

    /**
     * Change the pinf form a player
     *
     * @param player to change ping for
     * @param ping to replace with (in ms)
     */
    public static void setPing(Player player, int ping) {
        CommonNMS.getNative(player).ping = ping;
    }

    /**
     * Gets the players game profile
     *
     * @param player to get game profile from
     * @return The player's GameProfile
     */
    public static GameProfile getGameProfile(Player player) {
        return NMSEntityHuman.gameProfile.get(Conversion.toEntityHandle.convert(player));
    }

    /**
     * Checks whether a given chunk is visible to the client of a player. This
     * actually checks whether the chunk data had been sent, it doesn't do a
     * distance check.
     *
     * @param player to check
     * @param chunk to check
     * @return True if the chunk is visible to the player, False if not
     */
    public static boolean isChunkVisible(Player player, Chunk chunk) {
        return isChunkVisible(player, chunk.getX(), chunk.getZ());
    }

    /**
     * Checks whether a given chunk is visible to the client of a player. This
     * actually checks whether the chunk data had been sent, it doesn't do a
     * distance check.
     *
     * @param player to check
     * @param chunkX of the chunk to check
     * @param chunkZ of the chunk to check
     * @return True if the chunk is visible to the player, False if not
     */
    public static boolean isChunkVisible(Player player, int chunkX, int chunkZ) {
        return CommonPlugin.getInstance().getPlayerMeta(player).isChunkVisible(chunkX, chunkZ);
    }

    /**
     * Checks whether a given chunk has been 'entered' by a player. An entered
     * chunk is liable for updates to the client. Note that this does not check
     * whether the chunk is actually sent.
     *
     * @param player to check
     * @param chunkX of the chunk to check
     * @param chunkZ of the chunk to check
     * @return True if the player entered the chunk, False if not
     */
    public static boolean isChunkEntered(Player player, int chunkX, int chunkZ) {
        final EntityPlayer ep = CommonNMS.getNative(player);
        return ((WorldServer) ep.world).getPlayerChunkMap().a(ep, chunkX, chunkZ);
    }

    /**
     * Checks whether a given chunk has been 'entered' by a player. An entered
     * chunk is liable for updates to the client. Note that this does not check
     * whether the chunk is actually sent.
     *
     * @param player to check
     * @param chunk to check
     * @return True if the player entered the chunk, False if not
     */
    public static boolean isChunkEntered(Player player, Chunk chunk) {
        return isChunkEntered(player, chunk.getX(), chunk.getZ());
    }

    /**
     * Gets a modifiable List of Entity IDs that are queuing for Player Chunk
     * Packets to be sent
     *
     * @param player to get it for
     * @return Entity Remove Queue
     */
    public static List<Integer> getEntityRemoveQueue(Player player) {
        return Common.SERVER.getEntityRemoveQueue(player);
    }

    /**
     * Gets the item that a player is currently holding in the hand specified
     * 
     * @param player to get the hand item
     * @param hand which hand
     * @return item the player is holding in the hand
     */
    public static ItemStack getItemInHand(Player player, MainHand hand) {
        if (hand == player.getMainHand()) {
            return player.getInventory().getItemInMainHand();
        } else {
            return player.getInventory().getItemInOffHand();
        }
    }
}
