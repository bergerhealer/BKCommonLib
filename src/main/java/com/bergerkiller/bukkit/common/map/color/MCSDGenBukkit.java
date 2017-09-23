package com.bergerkiller.bukkit.common.map.color;

import org.bukkit.map.MapPalette;

import com.bergerkiller.bukkit.common.Logging;

public class MCSDGenBukkit extends MapColorSpaceData {

    /**
     * Generates the color map information by using Bukkit's algorithms.
     */
    @SuppressWarnings("deprecation")
    public void generate() {
        this.clear();
        for (int i = 0; i < 256; i++) {
            try {
                setColor((byte) i, MapPalette.getColor((byte) i));
            } catch (Throwable t) {}
        }
        for (int r = 0; r < 256; r++) {
            Logging.LOGGER_MAPDISPLAY.info("Generating Bukkit color map " + (r + 1) + "/256");
            for (int g = 0; g < 256; g++) {
                for (int b = 0; b < 256; b++) {
                    set(r, g, b, MapPalette.matchColor(r, g, b));
                }
            }
        }
    }
}
