package com.bergerkiller.bukkit.common.internal.logic;

import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.mountiplex.logic.TextValueSequence;

/**
 * Vanilla supplier that assumes the player's game version equals the
 * server version
 */
public class PlayerGameVersionSupplier_Vanilla extends PlayerGameVersionSupplier {
    private final TextValueSequence version;

    public PlayerGameVersionSupplier_Vanilla() {
        this.version = TextValueSequence.parse(CommonBootstrap.initCommonServer().getMinecraftVersion());
    }

    @Override
    public String getVersion(Player player) {
        return Common.MC_VERSION;
    }

    @Override
    public boolean evaluateVersion(Player player, String operand, TextValueSequence rightSide) {
        return TextValueSequence.evaluate(this.version, operand, rightSide);
    }
}
