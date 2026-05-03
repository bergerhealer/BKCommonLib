package com.bergerkiller.bukkit.common.server;

import com.bergerkiller.bukkit.common.offline.OfflineWorld;
import com.bergerkiller.bukkit.common.resources.ResourceCategory;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.world.LoadableWorld;
import com.bergerkiller.generated.net.minecraft.resources.IdentifierHandle;
import com.bergerkiller.generated.net.minecraft.server.MinecraftServerHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ServerLevelHandle;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SpigotServer extends CraftBukkitServer {
    private boolean _paper;
    private boolean _hasNewPaperWorldFormat;

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

    @Override
    public void postInit(PostInitEvent event) {
        super.postInit(event);
        _hasNewPaperWorldFormat = isPaperServer() && evaluateMCVersion(">=", "26.1");
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
    public LoadableWorld getLoadableWorld(World world) {
        if (!_hasNewPaperWorldFormat) {
            return super.getLoadableWorld(world);
        }

        String levelName = MinecraftServerHandle.instance().getLevelName();
        File mainWorldFolder = new File(Bukkit.getWorldContainer(), levelName);
        return getPaperLoadableWorld(mainWorldFolder, world);
    }

    private PaperLoadableWorld getPaperLoadableWorld(File mainWorldFolder, World world) {
        return new PaperLoadableWorld(mainWorldFolder, ServerLevelHandle.fromBukkit(world).getDimensionKey(), OfflineWorld.of(world));
    }

    @Override
    public Collection<LoadableWorld> getLoadableWorlds() {
        // On older versions of Paper, no migration to the new world format will occur
        if (!_hasNewPaperWorldFormat) {
            return super.getLoadableWorlds();
        }

        // Look up the main server-configured level-name
        // Note: could also use the Paper-only Server#getLevelDirectory() API
        String levelName = MinecraftServerHandle.instance().getLevelName();
        final File mainWorldFolder = new File(Bukkit.getWorldContainer(), levelName);

        // Include all worlds that are already loaded
        List<PaperLoadableWorld> loadedWorlds = Bukkit.getWorlds().stream()
                .map(w -> getPaperLoadableWorld(mainWorldFolder, w))
                .collect(Collectors.toList());

        List<LoadableWorld> loadableWorlds = new ArrayList<>(loadedWorlds);

        // List all dimensions in the main level-name folder
        // Skip those that are already loaded
        File dimensionsFolder = new File(mainWorldFolder, "dimensions");
        File[] namespaceFolders = dimensionsFolder.listFiles();
        if (namespaceFolders != null) {
            for (File namespaceFolder : namespaceFolders) {
                String namespace = namespaceFolder.getName();
                File[] dimensionFolders = namespaceFolder.listFiles();
                if (dimensionFolders == null) {
                    continue;
                }

                for (File dimensionFolder : dimensionFolders) {
                    if (!dimensionFolder.isDirectory()) {
                        continue;
                    }
                    String dimension = dimensionFolder.getName();

                    // Check not already loaded
                    boolean loaded = false;
                    for (PaperLoadableWorld loadedWorld : loadedWorlds) {
                        if (loadedWorld.dimensionNamespace.equals(namespace) && loadedWorld.dimensionName.equals(dimension)) {
                            loaded = true;
                            break;
                        }
                    }
                    if (loaded) {
                        continue;
                    }

                    ResourceKey<World> dimensionKey = ResourceKey.fromPath(ResourceCategory.dimension, namespace, dimension);
                    loadableWorlds.add(new PaperLoadableWorld(mainWorldFolder, dimensionKey, null));
                }
            }
        }

        // List all other folders in the "world container" that contain a level.dat
        // These worlds will be migrated to be a main-world dimension when loaded
        File[] subFolders = Bukkit.getWorldContainer().listFiles();
        if (subFolders != null) {
            for (File subFolder : subFolders) {
                if (mainWorldFolder.equals(subFolder)) {
                    continue;
                }
                if (!new File(subFolder, "level.dat").exists()) {
                    continue;
                }

                loadableWorlds.add(new ConvertedSpigotLoadableWorld(subFolder.getName(), subFolder, null));
            }
        }

        // Count for all names in use, how many times it is used
        Map<String, Integer> byNameCounts = new HashMap<>();
        for (LoadableWorld w : loadableWorlds) {
            for (String n : w.getNames()) {
                byNameCounts.merge(n, 1, Integer::sum);
            }
        }

        // Eliminate all ambiguous cases of world names in the getNames() listing
        // This guarantees a name will never match more than one world
        // If multiple namespaces declare the same world name, then loading the world will require
        // passing that namespace as a prefix.
        for (LoadableWorld loadableWorld : loadableWorlds) {
            // Spigot worlds can only be addressed by the legacy spigot name
            if (!(loadableWorld instanceof PaperLoadableWorld)) {
                continue;
            }

            // Remove names that have a count of more than 1.
            ((PaperLoadableWorld) loadableWorld).names.removeIf(n -> byNameCounts.getOrDefault(n, 0) > 1);
        }

        return loadableWorlds;
    }

    @Override
    public void addVariables(Map<String, String> variables) {
        super.addVariables(variables);
        if (_paper) {
            variables.put("paper", "true");
        }
    }

    @Override
    public String getServerName() {
        return _paper ? "Paper" : "Spigot";
    }

    protected static class PaperLoadableWorld implements LoadableWorld {
        private final File worldFolder;
        private final ResourceKey<World> dimensionKey;
        private final String dimensionNamespace;
        private final String dimensionName;
        private final File dimensionFolder;
        private final List<String> names;
        private OfflineWorld world;

        public PaperLoadableWorld(File worldFolder, ResourceKey<World> dimensionKey, OfflineWorld world) {
            this.worldFolder = worldFolder;

            this.dimensionKey = dimensionKey;
            IdentifierHandle identifier = dimensionKey.getName();
            this.dimensionNamespace = identifier.getNamespace();
            this.dimensionName = identifier.getName();
            this.dimensionFolder = new File(worldFolder,
                    "dimensions" + File.pathSeparator +
                            dimensionNamespace + File.pathSeparator +
                            dimensionName);
            this.names = new ArrayList<>(4);

            // For the 3 main dimensions, also include the legacy spigot world name as a valid matching name
            // This ensures old pre-paper configurations stay functional
            if (this.dimensionNamespace.equals("minecraft")) {
                if (this.dimensionName.equals("overworld")) {
                    this.names.add(worldFolder.getName());
                } else if (this.dimensionName.equals("the_nether")) {
                    this.names.add(worldFolder.getName() + "_nether");
                } else if (this.dimensionName.equals("the_end")) {
                    this.names.add(worldFolder.getName() + "_the_end");
                }
            }

            this.names.add(this.dimensionName);
            this.names.add(this.dimensionNamespace + ":" + dimensionName);
            this.names.add(worldFolder.getName() + "/" + dimensionNamespace + ":" + dimensionName);
            this.world = world;
        }

        @Override
        public String getDisplayName() {
            return names.get(0);
        }

        @Override
        public Collection<String> getNames() {
            return Collections.unmodifiableList(names);
        }

        @Override
        public Format getFormat() {
            return Format.PAPER;
        }

        @Override
        public World getWorld() {
            // Look up by name for the first time. Next time we can do it by UUID efficiently using the
            // OfflineWorld API.
            if (world == null) {
                World loadedWorld = WorldUtil.getWorldByDimensionKey(this.dimensionKey);
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
            return dimensionFolder;
        }

        @Override
        public File getLevelFile() {
            return new File(worldFolder, "level.dat");
        }

        @Override
        public File getRegionFolder() {
            File regionFolder = new File(getDimensionFolder(), "region");
            return regionFolder.isDirectory() ? regionFolder : null;
        }

        @Override
        public WorldCreator getWorldCreator() {
            // Convert dimension key to Bukkit NamespacedKey
            Class<?> namespacedKeyType = CommonUtil.getClass("org.bukkit.NamespacedKey");
            Method ofKeyMethod;
            try {
                ofKeyMethod = WorldCreator.class.getMethod("ofKey", namespacedKeyType);
            } catch (Throwable t) {
                throw new UnsupportedOperationException("Missing ofKey method in WorldCreator", t);
            }

            Object namespacedKey = dimensionKey.getName().toBukkit();
            try {
                return (WorldCreator) ofKeyMethod.invoke(null, namespacedKey);
            } catch (Throwable t) {
                throw new IllegalStateException("Failed to create world creator", t);
            }
        }

        @Override
        public String toString() {
            return "PaperLoadableWorld{" +
                    "format=" + getFormat() +
                    ", displayName='" + getDisplayName() + '\'' +
                    ", worldFolder='" + worldFolder + '\'' +
                    ", dimensionNamespace='" + dimensionNamespace + '\'' +
                    ", dimensionName='" + dimensionName + '\'' +
                    ", world=" + getWorld() +
                    '}';
        }
    }

    protected static class ConvertedSpigotLoadableWorld extends SpigotLoadableWorld {

        public ConvertedSpigotLoadableWorld(String worldName, File worldFolder, OfflineWorld world) {
            super(worldName, worldFolder, world);
        }

        @Override
        public Format getFormat() {
            return Format.SPIGOT_CONVERTED;
        }
    }
}
