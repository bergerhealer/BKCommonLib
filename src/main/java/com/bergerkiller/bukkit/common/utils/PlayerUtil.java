package com.bergerkiller.bukkit.common.utils;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;
import com.bergerkiller.generated.com.mojang.authlib.GameProfileHandle;
import com.bergerkiller.generated.net.minecraft.server.ContainerHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityHumanHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityPlayerHandle;
import com.bergerkiller.generated.net.minecraft.server.ItemStackHandle;
import com.bergerkiller.generated.net.minecraft.server.NetworkManagerHandle;
import com.bergerkiller.generated.net.minecraft.server.PlayerInventoryHandle;
import com.bergerkiller.generated.net.minecraft.server.SlotHandle;
import com.bergerkiller.mountiplex.conversion.util.ConvertingList;
import com.bergerkiller.reflection.net.minecraft.server.NMSPlayerConnection;
import com.bergerkiller.reflection.org.bukkit.craftbukkit.CBCraftPlayer;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
        final Object connection = EntityPlayerHandle.T.playerConnection.get(handle);
        if (connection == null) {
            return true;
        }
        final Object network = NMSPlayerConnection.networkManager.get(connection);
        return network == null || !NetworkManagerHandle.T.isConnected.invoke(network);
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
        EntityPlayerHandle handle = CommonNMS.getHandle(player);
        List<?> nearbyPlayerHandles = handle.getWorld().getRawEntitiesOfType(
                EntityPlayerHandle.T.getType(),
                handle.getBoundingBox().grow(radius, radius, radius));
        return new ConvertingList<Player>(nearbyPlayerHandles, DuplexConversion.player);
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
        return CommonNMS.getHandle(player).getPing();
    }

    /**
     * Change the pinf form a player
     *
     * @param player to change ping for
     * @param ping to replace with (in ms)
     */
    public static void setPing(Player player, int ping) {
        CommonNMS.getHandle(player).setPing(ping);
    }

    /**
     * Gets the players game profile
     *
     * @param player to get game profile from
     * @return The player's GameProfile
     */
    public static GameProfileHandle getGameProfile(Player player) {
        return EntityHumanHandle.T.gameProfile.get(HandleConversion.toEntityHandle(player));
    }

    /**
     * Checks whether a given chunk is visible to the client of a player. This
     * actually checks whether the player subscribed to the chunk data, it doesn't do a
     * distance check.
     *
     * @param player to check
     * @param chunk to check
     * @return True if the chunk is visible to the player, False if not
     */
    public static boolean isChunkVisible(Player player, Chunk chunk) {
        return player.getWorld() == chunk.getWorld() && isChunkVisible(player, chunk.getX(), chunk.getZ());
    }

    /**
     * Checks whether a given chunk is visible to the client of a player. This
     * actually checks whether the player subscribed to the chunk data, it doesn't do a
     * distance check.
     *
     * @param player to check
     * @param chunkX of the chunk to check
     * @param chunkZ of the chunk to check
     * @return True if the chunk is visible to the player, False if not
     */
    public static boolean isChunkVisible(Player player, int chunkX, int chunkZ) {
        final EntityPlayerHandle ep = CommonNMS.getHandle(player);
        return ep.getWorldServer().getPlayerChunkMap().isChunkEntered(ep, chunkX, chunkZ);
    }

    /**
     * <b>Deprecated: </b>use {@link #isChunkVisible(Player, chunkX, chunkZ)} instead
     */
    @Deprecated
    public static boolean isChunkEntered(Player player, int chunkX, int chunkZ) {
        return isChunkVisible(player, chunkX, chunkZ);
    }

    /**
     * <b>Deprecated: </b>use {@link #isChunkVisible(Player, chunk)} instead
     */
    @Deprecated
    public static boolean isChunkEntered(Player player, Chunk chunk) {
        return isChunkVisible(player, chunk);
    }

    /**
     * Gets a modifiable List of Entity IDs that are queuing for Player Chunk
     * Packets to be sent
     *
     * @param player to get it for
     * @return Entity Remove Queue
     */
    public static List<Integer> getEntityRemoveQueue(Player player) {
        return CommonPlugin.getInstance().getPlayerMeta(player).getRemoveQueue();
    }

    /**
     * Gets the item that a player is currently holding in the hand specified
     * 
     * @param player to get the hand item
     * @param hand which hand
     * @return item the player is holding in the hand
     */
    public static ItemStack getItemInHand(Player player, HumanHand hand) {
        return HumanHand.getHeldItem(player, hand);
    }

    /**
     * Gets the slot index of an item in a player inventory. These indices differ!
     * NB: Taken over from CraftPlayerInventory source. There is no exposed function for it.
     * 
     * @param playerInventoryIndex to convert
     * @return index of the item slot in the player inventory window
     */
    public static int getInventorySlotIndex(int playerInventoryIndex) {
        int index = playerInventoryIndex;
        if (index < PlayerInventoryHandle.getHotbarSize()) {
            index += 36;
        } else if (index > 39 && CommonCapabilities.PLAYER_OFF_HAND) {
            index += 5; // Off hand (1.9.2 and onwards only)
        } else if (index > 35) {
            index = 8 - (index - 36);
        }
        return index;
    }

    /**
     * Marks a certain item in a Player's inventory as unchanged, preventing the item
     * from being synchronized to the Player.
     * 
     * @param player to affect
     * @param index of the item in the player's inventory
     */
    @SuppressWarnings("unchecked")
    public static void markItemUnchanged(Player player, int index) {
        Object rawContainer = CommonNMS.getHandle(player).getActiveContainer().getRaw();
        List<Object> rawOldItems = (List<Object>) ContainerHandle.T.oldItems.raw.get(rawContainer);
        List<Object> rawSlots = (List<Object>) ContainerHandle.T.slots.raw.get(rawContainer);

        index = getInventorySlotIndex(index); // conversion is needed

        if (index >= 0 && index < rawOldItems.size() && index < rawSlots.size()) {
            Object oldItem = SlotHandle.T.getItem.raw.invoke(rawSlots.get(index));
            if (CommonNMS.isItemEmpty(oldItem)) {
                if (CommonCapabilities.ITEMSTACK_EMPTY_STATE) {
                    oldItem = ItemStackHandle.EMPTY_ITEM.getRaw(); // >= MC 1.11
                } else {
                    oldItem = null; // <= MC 1.10.2
                }
            } else {
                oldItem = ItemStackHandle.T.cloneItemStack.raw.invoke(oldItem);
            }
            rawOldItems.set(index, oldItem);
        }
    }
}
