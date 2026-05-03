package com.bergerkiller.bukkit.common.world;

import com.bergerkiller.bukkit.common.Common;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.util.Collection;

/**
 * A world that exists on disk that can be loaded. Provides information where the
 * world files are located and a method to load the world. This automatically
 * handles the different world formats since Paper 26.1 and forge hybrid servers.
 */
public interface LoadableWorld {
    /**
     * Gets the human-readable name of this world. Use this in listing calls
     * for what worlds exist. If multiple worlds exist with the same name due to
     * the new dimension format, the display name is made longer to include the
     * parent world folder the dimension is inside of.
     *
     * @return World display name
     */
    String getDisplayName();

    /**
     * Gets all world names that result in this loadable world being matched. This is used
     * in the by-name lookup of loadable worlds. For the new dimension format, this includes the parent
     * world name and dimension name format (e.g. world/overworld)
     *
     * @return All names that resolve to this loadable world
     */
    Collection<String> getNames();

    /**
     * Gets the world format of this loadable world. This is used to determine how to load the world and
     * where the world files are located on disk.
     *
     * @return World format
     */
    Format getFormat();

    /**
     * Gets the world, if it is presently loaded.
     *
     * @return Loaded World
     */
    World getWorld();

    /**
     * Gets the main data folder. This is the folder in which the level.dat file resides, among
     * other files like maps and datapacks. On the new paper format, the main world data is
     * in a dimension folder instead of here.
     *
     * @return Root folder of the world
     * @see #getDimensionFolder()
     */
    File getRootFolder();

    /**
     * Gets the main data folder where the world's data is stored. This stores the region files
     * among other things. The level.dat might not be located here depending on the world format.
     *
     * @return World dimension main data folder
     */
    File getDimensionFolder();

    /**
     * Gets the file location of the level.dat of this world. This level.dat might store
     * information for multiple dimensions depending on the world format.
     *
     * @return Location of level.dat
     */
    File getLevelFile();

    /**
     * Gets the folder in which this world's region files are located
     *
     * @return Location of the 'regions' folder. Returns <i>null</i> if no such folder
     *         exists for this world (no world data)
     */
    File getRegionFolder();

    /**
     * Initializes a new WorldCreator with the configuration required to load this
     * loadable world.
     *
     * @return World Creator
     */
    WorldCreator getWorldCreator();

    /**
     * Gets whether this loadable world is already loaded
     *
     * @return True if loaded
     * @see #getWorld()
     */
    default boolean isLoaded() {
        return getWorld() != null;
    }

    /**
     * Retrieves the Loadable World details of an already-loaded Bukkit World.
     *
     * @param world Bukkit World
     * @return Loadable World
     */
    static LoadableWorld of(World world) {
        return Common.SERVER.getLoadableWorld(world);
    }

    /**
     * Gets the loadable world information for a world by name. This provides information
     * about where the files for this world are located, and a means to load the world
     * if it isn't already loaded. If multiple worlds somehow match the same name (spigot and paper dimension
     * with the same name), then that name will not match that world anymore. Instead, the full
     * name to the world (including namespace) must be provided.
     *
     * @param worldName Unique world name
     * @return LoadableWorld, or <i>null</i> if no world with the given name exists or can be loaded
     * @see #getNames()
     */
    static LoadableWorld find(String worldName) {
        return Common.SERVER.findLoadableWorld(worldName);
    }

    /**
     * Lists all worlds that can be loaded on the server. This walks the directory structure to
     * identify what worlds and world dimensions can be loaded.
     *
     * @return All loadable worlds
     */
    static Collection<LoadableWorld> listAll() {
        return Common.SERVER.getLoadableWorlds();
    }

    /**
     * World format
     */
    enum Format {
        /** Legacy spigot format. Each world has it's own folder in the server root */
        SPIGOT,
        /** Legacy spigot format, but will be converted to PAPER format once loaded in a data migration */
        SPIGOT_CONVERTED,
        /** New paper format. Each world is stored in a subfolder of the main world "dimensions" folder */
        PAPER
    }
}
