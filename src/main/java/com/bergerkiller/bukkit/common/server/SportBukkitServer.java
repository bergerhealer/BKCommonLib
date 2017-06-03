package com.bergerkiller.bukkit.common.server;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.mountiplex.reflection.SafeField;
import com.bergerkiller.reflection.net.minecraft.server.NMSEntityPlayer;

import org.bukkit.Bukkit;

import java.util.logging.Level;

public class SportBukkitServer extends CraftBukkitServer {

    @Override
    public boolean init() {
        if (!super.init()) {
            return false;
        }
        return Bukkit.getServer().getVersion().contains("SportBukkit");
    }

    @Override
    public void postInit() {
        super.postInit();
        // Checkup that the Entity Remove queue for players is indeed missing
        if (SafeField.contains(NMSEntityPlayer.T.getType(), "removeQueue", null)) {
        	Logging.LOGGER.log(Level.WARNING, "Entity Removal queue of SportBukkit was added again! (update needed?)");
        }
    }

    @Override
    public String getServerName() {
        return "SportBukkit";
    }

}
