package com.bergerkiller.bukkit.common.map.archive;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.map.MapResourcePack;
import com.bergerkiller.bukkit.common.map.gson.MapResourcePackDeserializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;

/**
 * Abstract interface for accessing the file contents of a resource pack.
 */
public interface MapResourcePackArchive {

    /**
     * Gets the name of this archive. Is used in error reporting.
     *
     * @return Archive name
     */
    String name();

    /**
     * Attempts to load the pack.mcmeta of this resource pack archive. If loading fails,
     * a default fallback is returned instead.
     *
     * @param deserializer Deserializer to use for loading the json file
     * @return Loaded metadata
     */
    default MapResourcePack.Metadata tryLoadMetadata(MapResourcePackDeserializer deserializer) {
        // Load pack.mcmeta contents
        // If this fails, the fallback will take over
        try (InputStream mcMetaInputStream = this.openFileStream("pack.mcmeta")) {
            if (mcMetaInputStream == null) {
                Logging.LOGGER_MAPDISPLAY.warning("Resource pack " + name() + " has no pack.mcmeta");
                return MapResourcePack.Metadata.fallback("missing pack.mcmeta");
            }

            MapResourcePack.Metadata.PackWrapper wrap = deserializer.readGsonObject(
                    MapResourcePack.Metadata.PackWrapper.class, mcMetaInputStream, "pack.mcmeta");
            if (wrap == null || wrap.pack == null) {
                Logging.LOGGER_MAPDISPLAY.warning("Resource pack " + name() + " pack.mcmeta could not be loaded (format error)");
                return MapResourcePack.Metadata.fallback("corrupt pack.mcmeta");
            }

            wrap.postLoad();
            return wrap.pack;
        } catch (Throwable t) {
            Logging.LOGGER_MAPDISPLAY.log(Level.WARNING, "Resource pack " + name() + " pack.mcmeta could not be loaded", t);
            return MapResourcePack.Metadata.fallback("error reading pack.mcmeta");
        }
    }

    /**
     * Loads the resource pack archive, making it available for use
     * 
     * @param lazy whether this is a lazy-load call. Allows logging of warnings if it can take a while.
     */
    void load(boolean lazy);

    /**
     * After this resource pack archive is {@link #load(boolean) loaded} and the resource pack
     * metadata is {@link #tryLoadMetadata(MapResourcePackDeserializer) retrieved}, this method
     * is called to configure the archive's behavior. This should configure overlay layers
     * configured in the pack metadata file.
     *
     * @param metadata Resource pack metadata (pack.mcmeta)
     */
    default void configure(MapResourcePack.Metadata metadata) {
    }

    /**
     * Attempts to find and open an input stream for a file inside the archive
     * 
     * @param path of the file (relative)
     * @return input stream of the file, null if not found
     * @throws IOException when an error occurs trying to access the file
     */
    InputStream openFileStream(String path) throws IOException;

    /**
     * Lists all the files that exist inside a folder of this resource pack archive.
     * Does not list files in subdirectories.
     *
     * @param folder Folder relative to the root of the archive to list
     * @return List of files found in the folder
     * @throws IOException when an error occurs trying to access the archive
     */
    default List<String> listFiles(String folder) throws IOException {
        return listFiles(folder, false);
    }

    /**
     * Lists all the files that exist inside a folder of this resource pack archive
     *
     * @param folder Folder relative to the root of the archive to list
     * @param deep If true, also lists files that are inside subdirectories of this folder
     * @return List of files found in the folder
     * @throws IOException when an error occurs trying to access the archive
     */
    List<String> listFiles(String folder, boolean deep) throws IOException;
}
