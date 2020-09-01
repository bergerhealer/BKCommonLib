package com.bergerkiller.bukkit.common.server;

import java.util.Map;

public class SpigotServer extends CraftBukkitServer {
    private boolean _paper;

    @Override
    public boolean init() {
        if (!super.init()) {
            return false;
        }

        // Detect presence of Paperspigot fork
        // Attempt to load the Paperspigot's 'PaperConfig' class, which only exists on paperspigot servers
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            _paper = true;
        } catch (Throwable t1) {
            // On Mohist it's called "PaperMCConfig" for some reason
            try {
                Class.forName("com.destroystokyo.paper.PaperMCConfig");
                _paper = true;
            } catch (Throwable t2) {
                _paper = false;
            }
        }

        // Check that the Spigot install is available
        // Method 1 (older): Spigot class in org.bukkit.craftbukkit
        try {
            Class.forName(CB_ROOT_VERSIONED + ".Spigot");
            return true;
        } catch (ClassNotFoundException ex) {
        }
        // Method 2 (newer): Spigot configuration in the org.spigotmc
        try {
            Class.forName("org.spigotmc.SpigotConfig");
            return true;
        } catch (ClassNotFoundException ex) {
        }
        return false;
    }

    /**
     * Gets whether the Paperspigot fork of Spigot is used
     * 
     * @return True if paperspigot is used
     */
    public boolean isPaperSpigot() {
        return _paper;
    }

    @Override
    public String getServerName() {
        return _paper ? "Paperspigot" : "Spigot";
    }

    @Override
    public void addVariables(Map<String, String> variables) {
        super.addVariables(variables);
        variables.put("spigot", "true");
        if (_paper) {
            variables.put("paperspigot", "true");
        }
    }
}
