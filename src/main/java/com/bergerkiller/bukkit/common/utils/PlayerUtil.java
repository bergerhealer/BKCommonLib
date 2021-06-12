package com.bergerkiller.bukkit.common.utils;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.controller.VehicleMountController;
import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.internal.logic.PortalHandler;
import com.bergerkiller.bukkit.common.resources.DimensionType;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;
import com.bergerkiller.generated.com.mojang.authlib.GameProfileHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityPlayerHandle;
import com.bergerkiller.generated.net.minecraft.server.PlayerConnectionHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.player.EntityHumanHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.player.PlayerInventoryHandle;
import com.bergerkiller.generated.net.minecraft.world.inventory.ContainerHandle;
import com.bergerkiller.generated.net.minecraft.world.inventory.SlotHandle;
import com.bergerkiller.generated.net.minecraft.world.item.ItemStackHandle;
import com.bergerkiller.mountiplex.conversion.util.ConvertingList;
import com.bergerkiller.reflection.org.bukkit.craftbukkit.CBCraftPlayer;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.List;

/**
 * Player - specific operations and tools
 */
public class PlayerUtil extends EntityUtil {

    /**
     * Displays the game end credits for the player. If the player has already seen
     * these credits, then the screen is automatically closed by the player.
     * In all cases, the player is respawned onto the world.
     * 
     * @param player
     */
    public static void showEndCredits(Player player) {
        PortalHandler.INSTANCE.showEndCredits(player);
    }

    /**
     * Gets the vehicle mount controller for a player. This controller can be used
     * to automatically mount and unmount entities when they spawn/respawn.
     * 
     * @param player
     * @return Vehicle Mount Controller
     */
    public static VehicleMountController getVehicleMountController(Player player) {
        return CommonPlugin.getInstance().getVehicleMountManager().get(player);
    }

    /**
     * Gets whether a player is disconnected from the server
     *
     * @param player to check
     * @return True if the player is disconnected, False if not
     */
    public static boolean isDisconnected(Player player) {
        return PlayerConnectionHandle.forPlayer(player) == null;
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
     * Gets a modifiable List of Entity IDs that will result in entity destroy packets
     * to be sent the next tick.
     *
     * @param player to get it for
     * @return Entity Remove Queue
     */
    public static Collection<Integer> getEntityRemoveQueue(Player player) {
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
     * Sets an item in the player's inventory without causing events or packets to be sent to the player.
     * 
     * @param player
     * @param index of the slot in the inventory
     * @param item to set to
     */
    @SuppressWarnings("unchecked")
    public static void setItemSilently(Player player, int index, ItemStack item) {
        Object rawContainer = CommonNMS.getHandle(player).getActiveContainer().getRaw();
        List<Object> rawOldItems = (List<Object>) ContainerHandle.T.oldItems.raw.get(rawContainer);
        List<Object> rawSlots = (List<Object>) ContainerHandle.T.slots.raw.get(rawContainer);

        int convertedIndex = getInventorySlotIndex(index); // conversion is needed

        if (convertedIndex >= 0 && convertedIndex < rawOldItems.size() && convertedIndex < rawSlots.size()) {
            Object oldItem = SlotHandle.T.getItem.raw.invoke(rawSlots.get(convertedIndex));
            if (oldItem != null && oldItem != ItemStackHandle.EMPTY_ITEM.getRaw()) {
                // Copy all fields from the input item to this old item
                ItemStackHandle.T.copy(HandleConversion.toItemStackHandle(item), oldItem);
            } else {
                // Item is not modifiable, we have to set it (and cause a change)
                player.getInventory().setItem(index, item);
            }
        }

        // Writes the item at the slot to the 'old items' list
        markItemUnchanged(player, index);
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

    /**
     * Spawns the REDSTONE particles of a given color and size spread.
     * 
     * @param player to spawn the particles for
     * @param position to spawn at
     * @param color of the particles
     * @param size of the particle spread
     */
    public static void spawnDustParticles(Player player, Vector position, Color color) {
        if (CommonCapabilities.PARTICLE_OPTIONS) {
            // Official Bukkit API introduced in MC 1.13
            spawnDustParticles_1_13(player, position, color);
        } else {
            // This is a legacy fallback used on Minecraft 1.12.2 and before
            // The color is a close approximation
            double red = MathUtil.clamp((double) color.getRed() / 255.0, 0.0, 1.0);
            double green = MathUtil.clamp((double) color.getGreen() / 255.0, 0.0, 1.0);
            double blue = MathUtil.clamp((double) color.getBlue() / 255.0, 0.0, 1.0);
            if (red > 0.5) {
                red -= 1.0;
                if (red > -0.01) {
                    red = -0.01;
                }
            } else {
                red *= 1.7;
                if (red < 0.00001) {
                    red = 0.00001;
                }
            }
            player.spawnParticle(Particle.REDSTONE, position.getX(), position.getY(), position.getZ(), 0, red, green, blue, 1.0);
        }
    }

    private static void spawnDustParticles_1_13(Player player, Vector position, Color color) {
        try {
            // Official Bukkit API introduced in MC 1.13
            player.spawnParticle(Particle.REDSTONE,
                    position.getX(), position.getY(), position.getZ(),
                    1, new Particle.DustOptions(color, 1.0f));
        } catch (NoClassDefFoundError err) {
        }
    }

    /**
     * Plays a named sound effect at the player's location for a single player
     * 
     * @param player to play for and play at
     * @param soundKey of the sound to play
     * @param volume of the sound
     * @param pitch of the sound
     */
    public static void playSound(Player player, ResourceKey<SoundEffect> soundKey, float volume, float pitch) {
        if (soundKey != null) {
            player.playSound(player.getEyeLocation(), soundKey.getName().getName(), volume, pitch);
        }
    }

    /**
     * Plays a named sound effect at a location for a single player
     * 
     * @param player to play for
     * @param location to play at
     * @param soundKey of the sound to play
     * @param volume of the sound
     * @param pitch of the sound
     */
    public static void playSound(Player player, Location location, ResourceKey<SoundEffect> soundKey, float volume, float pitch) {
        if (soundKey != null) {
            player.playSound(location, soundKey.getName().getName(), volume, pitch);
        }
    }

    /**
     * Gets the dimension of a world a player is on, which is guaranteed to have a valid
     * registration in the server. This dimension will be OVERWORLD for all normal-type worlds,
     * THE_END for end worlds, etc. This method returns null for ProtocolLib's TemporaryPlayer.
     * (player before joining a world)
     *
     * @param player to get a world for
     * @return world dimension
     */
    public static DimensionType getPlayerDimension(Player player) {
        if (CBCraftPlayer.T.isAssignableFrom(player.getClass())) {
            return WorldUtil.getDimensionType(player.getWorld());
        } else {
            return null;
        }
    }

    /**
     * Sends a {@link ChatText} chat message to a player
     * 
     * @param player The player to send a message to
     * @param text The text to send, formatted using ChatText
     */
    public static void sendMessage(Player player, ChatText text) {
        EntityPlayerHandle.fromBukkit(player).sendMessage(text);
    }
}
