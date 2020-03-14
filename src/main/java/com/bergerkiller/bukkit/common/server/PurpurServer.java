package com.bergerkiller.bukkit.common.server;

public class PurpurServer extends PaperSpigotServer {

    @Override
    public boolean init() {
        // Must be a Paper Spigot server, when it is a Purpur server
        if (!super.init()) {
            return false;
        }

        // Attempt to load the Purpurpigot's 'PurpurConfig' class, which only exists on purpurspigot servers
        try {
            Class.forName("net.pl3x.purpur.PurpurConfig");
            return true;
        } catch (Throwable t) {}
        return false;
    }

    @Override
    public String getServerName() {
        return "Purpur (Paper) (Spigot)";
    }
}
