package com.bergerkiller.bukkit.common.server;

import java.util.Map;

public class PurpurServer extends SpigotServer {

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

    @Override
    public void addVariables(Map<String, String> variables) {
        super.addVariables(variables);
        variables.put("purpur", "true");
    }
}
