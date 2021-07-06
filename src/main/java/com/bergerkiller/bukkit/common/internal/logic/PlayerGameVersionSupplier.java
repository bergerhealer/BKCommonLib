package com.bergerkiller.bukkit.common.internal.logic;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

import com.bergerkiller.mountiplex.logic.TextValueSequence;

/**
 * Checks the actual client game version of a player and performs logical operations on it
 */
public abstract class PlayerGameVersionSupplier {
    private final Map<String, TextValueSequence> valueCache = new ConcurrentHashMap<>(); // Performance

    /**
     * Gets the (latest possible) game version a player is running on
     *
     * @param player Player
     * @return Minecraft game version string of the player
     */
    public abstract String getVersion(Player player);

    /**
     * Evaluates a logical expression against the versions supported by a player
     * 
     * @param player The player to check the game version of, left side of the operand
     * @param operand to evaluate (>, >=, ==, etc.)
     * @param rightSide value on the right side of the operand
     * @return True if the evaluation succeeds, False if not
     */
    public final boolean evaluateVersion(Player player, String operand, String rightSide) {
        return evaluateVersion(player, operand, valueCache.computeIfAbsent(rightSide, TextValueSequence::parse));
    }

    /**
     * Evaluates a logical expression against the versions supported by a player
     * 
     * @param player The player to check the game version of, left side of the operand
     * @param operand to evaluate (>, >=, ==, etc.)
     * @param rightSide value on the right side of the operand
     * @return True if the evaluation succeeds, False if not
     */
    public abstract boolean evaluateVersion(Player player, String operand, TextValueSequence rightSide);
}
