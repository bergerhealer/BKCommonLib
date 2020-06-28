package com.bergerkiller.bukkit.common.internal.logic;

import java.io.File;

import org.bukkit.World;
import org.bukkit.entity.HumanEntity;

import com.bergerkiller.bukkit.common.controller.PlayerDataController;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;

/**
 * Handles the registration and de-registration of player file data handlers
 */
public abstract class PlayerFileDataHandler {
    public static final PlayerFileDataHandler INSTANCE;

    static {
        if (CommonBootstrap.evaluateMCVersion(">=", "1.16")) {
            INSTANCE = new PlayerFileDataHandler_1_16();
        } else {
            INSTANCE = new PlayerFileDataHandler_1_8_to_1_15_2();
        }
    }

    public abstract PlayerDataController get();
    public abstract Hook hook(PlayerDataController controller);
    public abstract Hook mock(PlayerDataController controller);
    public abstract void unhook(Hook hook, PlayerDataController controller);

    public abstract File getPlayerDataFolder(World world);

    public static interface Hook {
        String[] base_getSeenPlayers();
        CommonTagCompound base_load(HumanEntity human);
        void base_save(HumanEntity human);
    }
}
