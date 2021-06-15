package com.bergerkiller.bukkit.common.server;

import org.bukkit.Bukkit;

public class SportBukkitServer extends CraftBukkitServer {

    @Override
    public boolean init() {
        if (!super.init()) {
            return false;
        }
        return Bukkit.getServer().getVersion().contains("SportBukkit");
    }

    @Override
    public String getServerName() {
        return "SportBukkit";
    }

}
