package com.bergerkiller.bukkit.common.map.archive;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Retrieves assets from a resource pack stored as an uncompressed directory
 */
public class MapResourcePackDirectoryArchive implements MapResourcePackArchive {
    private final File directory;

    public MapResourcePackDirectoryArchive(File directory) {
        this.directory = directory;
    }

    @Override
    public void load(boolean lazy) {
    }

    @Override
    public InputStream openFileStream(String path) throws IOException {
        File sub = new File(directory, path);
        if (sub.isFile()) {
            return new FileInputStream(sub);
        }
        return null;
    }
}
