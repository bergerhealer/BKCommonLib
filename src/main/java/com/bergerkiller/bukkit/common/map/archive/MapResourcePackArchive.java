package com.bergerkiller.bukkit.common.map.archive;

import java.io.IOException;
import java.io.InputStream;

/**
 * Abstract interface for accessing the file contents of a resource pack.
 */
public interface MapResourcePackArchive {

    /**
     * Loads the resource pack archive, making it available for use
     * 
     * @param lazy whether this is a lazy-load call. Allows logging of warnings if it can take a while.
     */
    void load(boolean lazy);

    /**
     * Attempts to find and open an input stream for a file inside the archive
     * 
     * @param path of the file (relative)
     * @return input stream of the file, null if not found
     * @throws IOException when an error occurs trying to access the file
     */
    InputStream openFileStream(String path) throws IOException;
}
