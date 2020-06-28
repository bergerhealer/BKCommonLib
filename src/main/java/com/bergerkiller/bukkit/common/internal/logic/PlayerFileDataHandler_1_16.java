package com.bergerkiller.bukkit.common.internal.logic;

import java.io.File;

import org.bukkit.World;

import com.bergerkiller.bukkit.common.controller.PlayerDataController;

public class PlayerFileDataHandler_1_16 extends PlayerFileDataHandler {

    @Override
    public PlayerDataController get() {
        return null;
    }

    @Override
    public Hook hook(PlayerDataController controller) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Hook mock(PlayerDataController controller) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void unhook(Hook hook, PlayerDataController controller) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public File getPlayerDataFolder(World world) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
