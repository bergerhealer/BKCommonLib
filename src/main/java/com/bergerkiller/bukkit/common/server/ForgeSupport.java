package com.bergerkiller.bukkit.common.server;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import org.bukkit.Bukkit;

import com.bergerkiller.bukkit.common.utils.StreamUtil;

/**
 * Special helper methods specifically for Forge servers
 */
public class ForgeSupport {

    public static String getMainWorldName() {
        return Bukkit.getWorldContainer().getName();
    }

    public static Collection<String> getLoadableWorlds() {
        String[] subDirs = Bukkit.getWorldContainer().list();
        Collection<String> rval = new ArrayList<String>(subDirs.length + 1);
        rval.add(getMainWorldName());
        for (String worldName : subDirs) {
            if (isLoadableWorld(worldName)) {
                rval.add(worldName);
            }
        }
        return rval;
    }

    public static File getWorldRegionFolder(String worldName) {
        // Is always the region subdirectory on Forge
        File regionFolder = new File(getWorldFolder(worldName), "region");
        return regionFolder.exists() ? regionFolder : null;
    }

    public static File getWorldFolder(String worldName) {
        // If main world, then it is the container itself
        if (worldName.equals(getMainWorldName())) {
            return Bukkit.getWorldContainer();
        } else {
            return StreamUtil.getFileIgnoreCase(Bukkit.getWorldContainer(), worldName);
        }
    }

    public static boolean isLoadableWorld(String worldName) {
        if (Bukkit.getWorld(worldName) != null) {
            return true;
        }
        File worldFolder = getWorldFolder(worldName);
        if (!worldFolder.isDirectory()) {
            return false;
        }
        if (new File(worldFolder, "level.dat").exists()) {
            return true;
        }
        // Check whether there are any valid region files in the folder
        File regionFolder = getWorldRegionFolder(worldName);
        if (regionFolder != null) {
            for (String fileName : regionFolder.list()) {
                if (fileName.toLowerCase(Locale.ENGLISH).endsWith(".mca")) {
                    return true;
                }
            }
        }
        return false;
    }
}
