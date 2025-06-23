package com.bergerkiller.bukkit.common.internal.logic;

import java.io.File;

import org.bukkit.World;
import org.bukkit.entity.HumanEntity;

import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.component.LibraryComponentSelector;
import com.bergerkiller.bukkit.common.controller.PlayerDataController;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.mountiplex.reflection.util.LazyInitializedObject;

/**
 * Handles the registration and de-registration of player file data handlers
 */
public abstract class PlayerFileDataHandler implements LazyInitializedObject, LibraryComponent {
    public static final PlayerFileDataHandler INSTANCE = LibraryComponentSelector.forModule(PlayerFileDataHandler.class)
            .addVersionOption(null, "1.15.2", PlayerFileDataHandler_1_8_to_1_15_2::new)
            .addVersionOption("1.16", null, PlayerFileDataHandler_1_16::new)
            .update();

    public abstract PlayerDataController get();
    public abstract Hook hook(PlayerDataController controller);
    public abstract Hook mock(PlayerDataController controller);
    public abstract void unhook(Hook hook, PlayerDataController controller);

    public abstract File getPlayerDataFolder(World world);

    public abstract CommonTagCompound migratePlayerData(CommonTagCompound playerProfileData);

    public static interface Hook {
        CommonTagCompound base_load(HumanEntity human);
        CommonTagCompound base_load_offline(String playerName, String playerUUID);
        void base_save(HumanEntity human);
    }
}
