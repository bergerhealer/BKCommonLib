package com.bergerkiller.bukkit.common.entity;

import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.server.level.EntityPlayerHandle;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * The phase (lifetime) of a {@link Player} instance object.
 * This tracks the following things about an instance:
 * <ul>
 *   <li>The player is currently joining (logging in) the server, but has no Entity yet</li>
 *   <li>The player has joined and has an Entity that is Alive on a World</li>
 *   <li>The player has joined but has died, and is on the respawn screen, and has no Entity</li>
 *   <li>The player has logged off the server, has no Entity, and the Player object is invalid</li>
 * </ul>
 * An important thing is that this method specifically checks for the Player <b>Instance</b>.
 * This means if the player leaves the server and rejoins, the old player instance will
 * return {@link #OFFLINE}.
 */
public enum PlayerInstancePhase {
    /** The Player is no longer connected to the server, and this Player instance should be cleaned up */
    OFFLINE(false, false, false),
    /** The Player is joining the server, but has not yet joined it and does not yet have an Entity on a World */
    JOINING(true, false, false),
    /** The Player has joined the server and has an Entity on a World, and is Alive */
    ALIVE(true, true, true),
    /** The player has joined the server but is dead and on the respawn screen, and has no Entity on a World */
    RESPAWNING(true, true, false);

    private final boolean isConnected;
    private final boolean hasJoined;
    private final boolean isAliveOnWorld;

    PlayerInstancePhase(boolean isConnected, boolean hasJoined, boolean isAliveOnWorld) {
        this.isConnected = isConnected;
        this.hasJoined = hasJoined;
        this.isAliveOnWorld = isAliveOnWorld;
    }

    /**
     * Gets whether this Player instance is still connected to the server. The Player might not be alive
     * or have an Entity on a World.
     *
     * @return True if this Player instance is currently connected to the server
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Gets whether this Player instance is connected to the server and has joined it. The Player might not
     * be alive (respawning), but it has gone through the login process.
     *
     * @return True if this Player instance is currently connected, finished logging in, and is now alive
     *         as an Entity on a World, or is on the respawn screen.
     */
    public boolean hasJoined() {
        return hasJoined;
    }

    /**
     * Gets whether this Player instance is connected to the server and is alive. If this returns True,
     * then actions with the Player's body can be done.
     *
     * @return True if this Player instance is connected and alive on a World
     */
    public boolean isAliveOnWorld() {
        return isAliveOnWorld;
    }

    /**
     * Gets the Player Instance Phase of a Player instance.
     *
     * @param player Player instance
     * @return Phase of this Player instance object
     */
    public static PlayerInstancePhase of(Player player) {
        if (player.isValid()) {
            return ALIVE;
        } else if (Bukkit.getPlayer(player.getUniqueId()) != player) {
            // This also happens while a Player is in the LOGIN phase, so check the
            // player is truly disconnected as well to differentiate the two.
            if (EntityPlayerHandle.fromBukkit(player).hasDisconnected()) {
                return OFFLINE;
            } else {
                return JOINING;
            }
        } else if (player.isDead()) {
            return RESPAWNING;
        } else {
            return JOINING;
        }
    }

    /**
     * Gets a Collection of players on the server that have joined the server fully, having
     * completed the login procedure. These players might be dead.
     *
     * @return Collection of Players that joined the server
     * @see #hasJoined()
     */
    public static Collection<? extends Player> getJoinedPlayers() {
        return CommonUtil.getOnlinePlayers(p -> of(p).hasJoined());
    }

    /**
     * Gets a Collection of players on the server that are alive and have an Entity
     * on a world
     *
     * @return Collection of Players that are alive and have an Entity on a World
     * @see #isAliveOnWorld()
     */
    public static Collection<? extends Player> getAlivePlayers() {
        return CommonUtil.getOnlinePlayers(p -> of(p).isAliveOnWorld());
    }
}
