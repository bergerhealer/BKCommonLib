package com.bergerkiller.bukkit.common.server;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

import com.bergerkiller.generated.net.minecraft.resources.MinecraftKeyHandle;
import org.bukkit.Bukkit;
import org.bukkit.Server;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.utils.StreamUtil;
import com.bergerkiller.mountiplex.MountiplexUtil;
import org.bukkit.World;

/**
 * Special helper methods specifically for Forge servers
 */
public class ForgeSupport {
    private final Server server;
    private final File worldContainer;

    private ForgeSupport(Server server, File worldContainer) {
        this.server = server;
        this.worldContainer = worldContainer;
    }

    public static ForgeSupport bukkit() {
        return new ForgeSupport(Bukkit.getServer(), Bukkit.getWorldContainer());
    }

    public static ForgeSupport of(File worldContainer) {
        return new ForgeSupport(null, worldContainer);
    }

    public String getMainWorldName() {
        return worldContainer.getName();
    }

    public Collection<String> getLoadableWorlds() {
        Collection<String> rval = new HashSet<String>();
        for (LevelDimension dim : listDimensions(worldContainer)) {
            rval.add(dim.toBukkitName());
        }
        for (File worldFolder : worldContainer.listFiles()) {
            for (LevelDimension dim : listDimensions(worldFolder)) {
                rval.add(dim.toBukkitName());
            }
        }
        for (World world : Bukkit.getWorlds()) {
            rval.add(world.getName());
        }
        return rval;
    }

    private Set<LevelDimension> listDimensions(File worldFolder) {
        File levelDatFile = new File(worldFolder, "level.dat");
        if (!levelDatFile.exists()) {
            return Collections.emptySet();
        }

        // Add folder name itself as a world name
        String rootWorldName = worldFolder.getName();

        // Parse level.dat and add all dimensions defined inside, if defined
        try {
            CommonTagCompound levelDat = CommonTagCompound.readFromFile(levelDatFile, true);
            CommonTagCompound data = levelDat.get("Data", CommonTagCompound.class);
            if (data != null) {
                CommonTagCompound worldGenSettings = data.get("WorldGenSettings", CommonTagCompound.class);
                if (worldGenSettings != null) {
                    CommonTagCompound dimensions = worldGenSettings.get("dimensions", CommonTagCompound.class);
                    if (dimensions != null) {
                        Set<LevelDimension> result = new HashSet<>(dimensions.size());
                        for (String modnameAndDimension : dimensions.keySet()) {
                            if (dimensions.get(modnameAndDimension, CommonTagCompound.class) != null) {
                                int modNameEnd = modnameAndDimension.indexOf(':');
                                String namespace, dimension;
                                if (modNameEnd == -1) {
                                    namespace = "minecraft";
                                    dimension = modnameAndDimension;
                                } else {
                                    namespace = modnameAndDimension.substring(0, modNameEnd);
                                    dimension = modnameAndDimension.substring(modNameEnd + 1);
                                }
                                result.add(new LevelDimension(rootWorldName, namespace, dimension));
                            }
                        }

                        // All dimensions accounted for - fallback not needed
                        return result;
                    }
                }
            }
        } catch (Throwable t) {
            Logging.LOGGER.log(Level.SEVERE, "Failed to read level.dat of world " + worldFolder.getName(), t);
        }

        // Fallback solution
        Set<LevelDimension> result = new HashSet<>(3);
        result.add(new LevelDimension(rootWorldName, "minecraft", "overworld"));
        if ((new File(worldFolder, "DIM-1")).exists()) {
            result.add(new LevelDimension(rootWorldName, "minecraft", "the_nether"));
        }
        if ((new File(worldFolder, "DIM1")).exists()) {
            result.add(new LevelDimension(rootWorldName, "minecraft", "the_end"));
        }
        return result;
    }

    public File getWorldLevelFile(String worldName) {
        LevelDimension dimension = fromBukkitName(worldName);
        return new File(getWorldRootFolder(dimension.world), "level.dat");
    }

    public File getWorldRegionFolder(String worldName) {
        // Is always the region subdirectory on Forge
        File regionFolder = new File(getWorldFolder(worldName), "region");
        return regionFolder.exists() ? regionFolder : null;
    }

    public File getWorldFolder(String worldName) {
        LevelDimension dimension = fromBukkitName(worldName);
        return dimension.toWorldFolder(getWorldRootFolder(dimension.world));
    }

    // Gets the folder
    private File getWorldRootFolder(String worldName) {
        // If main world, then it is the container itself
        String mainWorldName = getMainWorldName();
        if (worldName.equalsIgnoreCase(mainWorldName)) {
            return worldContainer;
        } else {
            // Try to find it in the container
            for (File file : worldContainer.listFiles()) {
                if (file.getName().equalsIgnoreCase(worldName)) {
                    return file;
                }
            }

            // Fallback
            return new File(worldContainer, worldName);
        }
    }

    public boolean isLoadableWorld(String worldName) {
        if (server != null && server.getWorld(worldName) != null) {
            return true;
        }

        LevelDimension dimension = fromBukkitName(worldName);
        File rootFolder = getWorldRootFolder(dimension.world);
        for (LevelDimension loadableDim : listDimensions(rootFolder)) {
            if (dimension.equals(loadableDim)) {
                return true;
            }
        }

        // Nope! Not in there.
        return false;
    }

    private static class LevelDimension {
        public final String world;
        public final String namespace;
        public final String dimension;

        public LevelDimension(String world, String namespace, String dimension) {
            this.world = world;
            this.namespace = namespace;
            this.dimension = dimension;
        }

        public String toBukkitName() {
            if (namespace.equals(MinecraftKeyHandle.DEFAULT_NAMESPACE)) {
                // Vanilla dimensions
                if (dimension.equals("overworld")) {
                    // World name itself
                    return world;
                } else if (dimension.equals("the_nether")) {
                    return world + "/DIM-1";
                } else if (dimension.equals("the_end")) {
                    return world + "/DIM1";
                } else {
                    return world + "/" + dimension; // Odd
                }
            } else {
                // Mod-added dimensions
                return String.format("%s/%s/%s", world, namespace, dimension);
            }
        }

        public File toWorldFolder(File rootWorldFolder) {
            if (namespace.equals("minecraft")) {
                if (dimension.equals("the_nether")) {
                    return StreamUtil.getFileIgnoreCase(rootWorldFolder, "DIM-1");
                } else if (dimension.equals("the_end")) {
                    return StreamUtil.getFileIgnoreCase(rootWorldFolder, "DIM1");
                } else {
                    return rootWorldFolder;
                }
            }
            return MountiplexUtil.toStream(rootWorldFolder)
                    .map(f -> StreamUtil.getFileIgnoreCase(f, "dimensions"))
                    .map(f -> StreamUtil.getFileIgnoreCase(f, namespace))
                    .map(f -> StreamUtil.getFileIgnoreCase(f, dimension))
                    .findFirst().get();
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof LevelDimension) {
                LevelDimension other = (LevelDimension) o;
                return this.world.equals(other.world) &&
                       this.namespace.equals(other.namespace) &&
                       this.dimension.equals(other.dimension);
            } else {
                return false;
            }
        }
    }

    private LevelDimension fromBukkitName(String bukkitWorldName) {
        String[] parts = bukkitWorldName.split("\\/");
        if (parts.length == 1) {
            // Bukkit naming convention
            return new LevelDimension(bukkitWorldName, "minecraft", "overworld");
        } else if (parts.length == 2) {
            // Worldname/DIM
            if (parts[1].equals("DIM-1")) {
                return new LevelDimension(parts[0], "minecraft", "the_nether");
            } else if (parts[1].equals("DIM1")) {
                return new LevelDimension(parts[0], "minecraft", "the_end");
            } else {
                // Can be nether/end as well, but world name will still work out here
                return new LevelDimension(parts[0], "minecraft", parts[1]);
            }
        } else {
            String partWorldName = parts[0];
            String partModName = parts[1];
            String partDimension = Arrays.stream(parts, 2, parts.length).collect(Collectors.joining("/"));
            return new LevelDimension(partWorldName, partModName, partDimension);
        }
    }
}
