package com.bergerkiller.bukkit.common.map.archive;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import com.bergerkiller.bukkit.common.Logging;

/**
 * An archive that automatically detects the correct archive class to use.
 * This handles URL and Filesystem paths
 */
public class MapResourcePackAutoArchive implements MapResourcePackArchive {
    private final String resourcePackPath;
    private MapResourcePackArchive archive;

    public MapResourcePackAutoArchive(String resourcePackPath, String resourcePackHash) {
        this.resourcePackPath = resourcePackPath;
        File file = new File(resourcePackPath);
        if (file.isFile()) {
            this.archive = new MapResourcePackZipArchive(file);
        } else if (file.isDirectory()) {
            this.archive = new MapResourcePackDirectoryArchive(file);
        } else {
            URL resourcePackURL = null;
            try {
                resourcePackURL = new URL(resourcePackPath);
            } catch (MalformedURLException e) {}

            if (resourcePackURL != null) {
                this.archive = new MapResourcePackDownloadedArchive(resourcePackURL, resourcePackHash);
            } else {
                // Resource pack path could not be located and is not a URL
                Logging.LOGGER_MAPDISPLAY.warning("Resource pack '" + resourcePackPath + "' could not be found or understood");
            }
        }
    }

    @Override
    public String name() {
        return resourcePackPath;
    }

    @Override
    public void load(boolean lazy) {
        if (this.archive != null) {
            this.archive.load(lazy);
        }
    }

    @Override
    public InputStream openFileStream(String path) throws IOException {
        if (this.archive == null) return null;
        return this.archive.openFileStream(path);
    }

    @Override
    public List<String> listFiles(String folder) throws IOException {
        if (this.archive == null) return Collections.emptyList();
        return this.archive.listFiles(folder);
    }
}
