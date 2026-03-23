package com.bergerkiller.bukkit.common.server;

import java.util.Map;

public class UniverseServer extends SpigotServer {

    @Override
    public boolean init() {
        // Must be a Paper server, when it is a Purpur server
        if (!super.init()) {
            return false;
        }

        // Attempt to load the Universe's 'UniverseConfig' class, which only exists on universe servers
        try {
            Class.forName("com.universeprojects.config.UniverseConfig");
            return true;
        } catch (Throwable t) {}

        return false;
    }

    @Override
    public boolean canLoadClassPath(String classPath) {
        // Universe rewrites server sources at runtime, so we cannot rely on the .class
        // files at all.
        if (classPath.startsWith("org.bukkit.craftbukkit.")) {
            return false;
        }
        if (classPath.startsWith("net.minecraft.")) {
            return false;
        }

        return true;
    }

    @Override
    public String getServerName() {
        return "Universe (Paper) (Spigot)";
    }

    @Override
    public void addVariables(Map<String, String> variables) {
        super.addVariables(variables);
        variables.put("universe", "true");
    }
}
