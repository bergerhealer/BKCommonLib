package com.bergerkiller.bukkit.common.server;

import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.offline.OfflineWorld;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.StreamUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.world.LoadableWorld;
import com.bergerkiller.mountiplex.logic.TextValueSequence;
import com.bergerkiller.mountiplex.reflection.util.asm.ASMUtil;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class CommonServerBase implements CommonServer {
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static final Class<? extends Bukkit> SERVER_CLASS = (Class) findServerClass();

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
    public LoadableWorld getLoadableWorld(World world) {
        if (world == null) {
            throw new IllegalArgumentException("World cannot be null");
        }

        File worldFolder = StreamUtil.getFileIgnoreCase(Bukkit.getWorldContainer(), world.getName());
        if (WorldUtil.isLoaded(world)) {
            return new SpigotLoadableWorld(world.getName(), worldFolder, OfflineWorld.of(world));
        } else {
            return new SpigotLoadableWorld(world.getName(), worldFolder, null);
        }
    }

    @Override
    public LoadableWorld findLoadableWorld(String worldName) {
        World world = Bukkit.getWorld(worldName);
        if (world != null) {
            return getLoadableWorld(world);
        }

        return getLoadableWorlds().stream()
                .filter(l -> l.getNames().contains(worldName))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean isLoadableWorld(String worldName) {
        return findLoadableWorld(worldName) != null;
    }

    @Override
    public Collection<LoadableWorld> getLoadableWorlds() {
        File[] subFolders = Bukkit.getWorldContainer().listFiles();
        if (subFolders == null) {
            return Collections.emptyList();
        }

        List<LoadableWorld> loadableWorlds = new ArrayList<LoadableWorld>(subFolders.length);
        Set<String> loadedWorldNames = new HashSet<String>();
        for (World world : Bukkit.getWorlds()) {
            loadedWorldNames.add(world.getName());
            loadableWorlds.add(getLoadableWorld(world));
        }
        for (File folder : subFolders) {
            if (loadedWorldNames.contains(folder.getName())) {
                continue;
            }
            if (!(new File(folder, "level.dat").exists())) {
                continue;
            }
            loadableWorlds.add(new SpigotLoadableWorld(folder.getName(), folder, null));
        }
        return loadableWorlds;
    }

    @Override
    public Collection<String> getLoadableWorldsLegacy() {
        return getLoadableWorlds().stream()
                .map(LoadableWorld::getDisplayName)
                .collect(Collectors.toList());
    }

    @Override
    public File getWorldFolder(String worldName) {
        LoadableWorld world = findLoadableWorld(worldName);
        if (world == null) {
            throw new IllegalArgumentException("Invalid world name: " + worldName);
        } else {
            return world.getDimensionFolder();
        }
    }

    @Override
    public File getWorldLevelFile(String worldName) {
        LoadableWorld world = findLoadableWorld(worldName);
        if (world == null) {
            throw new IllegalArgumentException("Invalid world name: " + worldName);
        } else {
            return world.getLevelFile();
        }
    }

    @Override
    public File getWorldRegionFolder(String worldName) {
        LoadableWorld world = findLoadableWorld(worldName);
        return world == null ? null : world.getRegionFolder();
    }

    @Override
    public String getMinecraftVersionMajor() {
        return CommonServer.cleanVersion(getMinecraftVersion());
    }

    @Override
    public String getMinecraftVersionPre() {
        return CommonServer.preVersion(getMinecraftVersion());
    }

    @Override
    public boolean evaluateMCVersion(String operand, String version) {
        return TextValueSequence.evaluateText(this.getMinecraftVersion(), operand, version);
    }

    @Override
    public void addVariables(Map<String, String> variables) {
        variables.put("version", getMinecraftVersionMajor());

        String pre_version = getMinecraftVersionPre();
        if (pre_version != null) {
            variables.put("pre", pre_version);
        }
    }

    @Override
    public String getServerDetails() {
        // Create server description token
        final StringBuilder serverDesc = new StringBuilder(300);
        serverDesc.append(getServerName()).append(" (");
        serverDesc.append(getServerDescription());
        if (isMojangMappings()) {
            serverDesc.append(" | mojmap");
        }
        serverDesc.append(") : ").append(getServerVersion());
        return serverDesc.toString();
    }

    @Override
    public boolean isCustomEntityType(org.bukkit.entity.EntityType entityType) {
        return false;
    }

    @Override
    public void enable(CommonPlugin plugin) {
    }

    @Override
    public void disable(CommonPlugin plugin) {
    }

    /**
     * Spigot format, where every world sits in the "world container" root folder, and the folder
     * name is what the world name is.
     */
    protected static class SpigotLoadableWorld implements LoadableWorld {
        private final String worldName;
        private final File worldFolder;
        private OfflineWorld world;

        public SpigotLoadableWorld(String worldName, File worldFolder, OfflineWorld world) {
            this.worldName = worldName;
            this.worldFolder = worldFolder;
            this.world = world;
        }

        @Override
        public String getDisplayName() {
            return worldName;
        }

        @Override
        public Collection<String> getNames() {
            return Collections.singletonList(worldName);
        }

        @Override
        public Format getFormat() {
            return Format.SPIGOT;
        }

        @Override
        public World getWorld() {
            // Look up by name for the first time. Next time we can do it by UUID efficiently using the
            // OfflineWorld API.
            if (world == null) {
                World loadedWorld = Bukkit.getWorld(worldName);
                if (loadedWorld != null) {
                    world = OfflineWorld.of(loadedWorld);
                    return loadedWorld;
                } else {
                    return null;
                }
            }

            return world.getLoadedWorld();
        }

        @Override
        public File getRootFolder() {
            return worldFolder;
        }

        @Override
        public File getDimensionFolder() {
            return getRootFolder();
        }

        @Override
        public File getLevelFile() {
            return new File(getRootFolder(), "level.dat");
        }

        @Override
        public File getRegionFolder() {
            File mainFolder = getDimensionFolder();
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
        public WorldCreator getWorldCreator() {
            return new WorldCreator(worldName);
        }

        @Override
        public String toString() {
            return "SpigotLoadableWorld{" +
                    "format=" + getFormat() +
                    ", displayName='" + getDisplayName() + '\'' +
                    ", name='" + worldName + '\'' +
                    ", world=" + getWorld() +
                    '}';
        }
    }
}
