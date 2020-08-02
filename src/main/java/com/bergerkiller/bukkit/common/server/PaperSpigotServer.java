package com.bergerkiller.bukkit.common.server;

/**
 * Created by Develop on 27-2-2016.
 */
public class PaperSpigotServer extends SpigotServer {

    @Override
    public boolean init() {
        // Must be a Spigot server, when it is a Paper Spigot server
        if (!super.init()) {
            return false;
        }

        // Attempt to load the Paperspigot's 'PaperConfig' class, which only exists on paperspigot servers
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
        } catch (Throwable t1) {
            // On Mohist it's called "PaperMCConfig" for some reason
            try {
                Class.forName("com.destroystokyo.paper.PaperMCConfig");
            } catch (Throwable t2) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String getServerName() {
        return "Paper(Spigot)";
    }
}
