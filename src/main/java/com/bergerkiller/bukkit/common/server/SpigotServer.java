package com.bergerkiller.bukkit.common.server;

public class SpigotServer extends CraftBukkitServer {
    private boolean _paper;

    @Override
    public boolean init() {
        if (!super.init()) {
            return false;
        }

        // Detect presence of Paper APIs
        // Attempt to load Paper's 'PaperConfig' class, which only exists if the api is also implemented
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
     * Gets whether the PaperMC server of Bukkit is used. If true, the Paper
     * API is available.
     * 
     * @return True if PaperMC is used
     */
    public boolean isPaperServer() {
        return _paper;
    }

    @Override
    public String getServerName() {
        return _paper ? "Paper" : "Spigot";
    }
}
