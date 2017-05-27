package com.bergerkiller.bukkit.common.server;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.StreamUtil;
import com.bergerkiller.mountiplex.reflection.util.ASMUtil;

import org.bukkit.Bukkit;
import org.bukkit.Server;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

public abstract class CommonServerBase implements CommonServer {
    public static final Class<?> SERVER_CLASS = findServerClass();

    private static final Class<?> findServerClass() {
        // On a live-running server, this method is all that will be needed
        if (Bukkit.getServer() != null) {
            return Bukkit.getServer().getClass();
        }

        // Attempt to figure out the Bukkit class type by inspecting the Main class (CraftBukkit)
        Class<?> cbMailClass = CommonUtil.getClass("org.bukkit.craftbukkit.Main");
        if (cbMailClass != null) {
            for (Class<?> type : ASMUtil.findUsedTypes(cbMailClass)) {
                if (Server.class.isAssignableFrom(type)) {
                    return type;
                }
            }
        }

        // Not found. Unsupported server.
        return null;
    }

    @Override
    public Collection<String> getLoadableWorlds() {
        File[] files = Bukkit.getWorldContainer().listFiles();
        Collection<String> rval = new ArrayList<String>(files.length);
        for (String worldName : Bukkit.getWorldContainer().list()) {
            if (isLoadableWorld(worldName)) {
                rval.add(worldName);
            }
        }
        return rval;
    }

    @Override
    public File getWorldFolder(String worldName) {
        return StreamUtil.getFileIgnoreCase(Bukkit.getWorldContainer(), worldName);
    }

    @Override
    public boolean isLoadableWorld(String worldName) {
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

    @Override
    public File getWorldRegionFolder(String worldName) {
        File mainFolder = getWorldFolder(worldName);
        // Overworld
        File tmp = new File(mainFolder, "region");
        if (tmp.exists()) {
            return tmp;
        }
        // Nether
        tmp = new File(mainFolder, "DIM-1" + File.pathSeparator + "region");
        if (tmp.exists()) {
            return tmp;
        }
        // The End
        tmp = new File(mainFolder, "DIM1" + File.pathSeparator + "region");
        if (tmp.exists()) {
            return tmp;
        }
        // Unknown???
        return null;
    }

    @Override
    public boolean isCompatible() {
        return Common.TEMPLATE_RESOLVER.isSupported(this.getMinecraftVersion());
    }

    @Override
    public void enable(CommonPlugin plugin) {
    }

    @Override
    public void disable(CommonPlugin plugin) {
    }
}
