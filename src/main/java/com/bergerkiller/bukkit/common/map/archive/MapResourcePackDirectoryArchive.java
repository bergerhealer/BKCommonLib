package com.bergerkiller.bukkit.common.map.archive;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    @Override
    public List<String> listFiles(String folder) throws IOException {
        File sub = new File(directory, folder);
        if (sub.exists() && sub.isDirectory()) {
            return Arrays.asList(sub.list());
        } else {
            return Collections.emptyList();
        }
    }
}
