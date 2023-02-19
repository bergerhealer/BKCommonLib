package com.bergerkiller.bukkit.common.protocol;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.mountiplex.logic.TextValueSequence;
import org.bukkit.entity.Player;

/**
 * Represents the information about the game used by a Player, in
 * particular the game version. Can be used to check whether the
 * version of the game falls within a range of versions, or to check
 * capabilities. This might be important when sending the (right) type
 * of packets to the player. Supports plugins like ViaVersion.<br>
 * <br>
 * Obtained using {@link #of(Player)}
 */
public interface PlayerGameInfo {
    /**
     * The default game information expected by the server. This is for vanilla
     * clients that the server natively supports.
     */
    static PlayerGameInfo SERVER = new PlayerGameInfo() {
        private final TextValueSequence version = TextValueSequence.parse(
                CommonBootstrap.initCommonServer().getMinecraftVersion());

        @Override
        public String version() {
            return version.toString();
        }

        @Override
        public boolean evaluateVersion(String operand, TextValueSequence rightSide) {
            return TextValueSequence.evaluate(version, operand, rightSide);
        }
    };

    /**
     * Gets the (latest possible) game version the player is running on
     *
     * @return Minecraft game version string of the player
     */
    String version();

    /**
     * Evaluates a logical expression against the versions supported by a player.
     * Player game version is on the left side of the operand.
     *
     * @param operand to evaluate (>, >=, ==, etc.)
     * @param rightSide value on the right side of the operand
     * @return True if the evaluation succeeds, False if not
     */
    default boolean evaluateVersion(String operand, String rightSide) {
        return evaluateVersion(operand, PlayerGameInfoCache.parseVersion(rightSide));
    }

    /**
     * Evaluates a logical expression against the versions supported by a player.
     * Player game version is on the left side of the operand.
     *
     * @param operand to evaluate (>, >=, ==, etc.)
     * @param rightSide value on the right side of the operand
     * @return True if the evaluation succeeds, False if not
     */
    boolean evaluateVersion(String operand, TextValueSequence rightSide);

    /**
     * Gets the game version information used by a particular Player
     *
     * @param player
     * @return Player game version
     */
    static PlayerGameInfo of(Player player) {
        if (CommonPlugin.hasInstance()) {
            return CommonPlugin.getInstance().getGameInfo(player);
        } else {
            return SERVER;
        }
    }
}
