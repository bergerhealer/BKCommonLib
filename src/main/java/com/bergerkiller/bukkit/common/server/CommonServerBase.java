package com.bergerkiller.bukkit.common.server;

import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.StreamUtil;
import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.util.asm.ASMUtil;

import org.bukkit.Bukkit;
import org.bukkit.Server;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

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
        String[] subDirs = Bukkit.getWorldContainer().list();
        Collection<String> rval = new ArrayList<String>(subDirs.length);
        for (String worldName : subDirs) {
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
        tmp = new File(mainFolder, "DIM-1" + File.separator + "region");
        if (tmp.exists()) {
            return tmp;
        }
        // The End
        tmp = new File(mainFolder, "DIM1" + File.separator + "region");
        if (tmp.exists()) {
            return tmp;
        }
        // Unknown???
        return null;
    }

    @Override
    public boolean isCompatible() {
        return CommonBootstrap.getTemplates().isSupported(this.getMinecraftVersion());
    }

    @Override
    public String getMinecraftVersionMajor() {
        String clean_version = this.getMinecraftVersion();
        int pre_idx = clean_version.indexOf("-pre");
        if (pre_idx != -1) {
            clean_version = clean_version.substring(0, pre_idx);
        }
        return clean_version;
    }

    @Override
    public String getMinecraftVersionPre() {
        String mc_version = this.getMinecraftVersion();
        int pre_idx = mc_version.indexOf("-pre");
        if (pre_idx != -1) {
            return mc_version.substring(pre_idx + 4);
        }
        return null;
    }

    @Override
    public boolean evaluateMCVersion(String operand, String version) {
        return MountiplexUtil.evaluateText(this.getMinecraftVersion(), operand, version);
    }

    @Override
    public void addVariables(Map<String, String> variables) {
    }

    @Override
    public String getServerDetails() {
        // Create server description token
        final StringBuilder serverDesc = new StringBuilder(300);
        serverDesc.append(getServerName()).append(" (");
        serverDesc.append(getServerDescription());
        serverDesc.append(") : ").append(getServerVersion());
        return serverDesc.toString();
    }

    @Override
    public void enable(CommonPlugin plugin) {
    }

    @Override
    public void disable(CommonPlugin plugin) {
    }
}
