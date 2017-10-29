package com.bergerkiller.bukkit.common.map.archive;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.bergerkiller.bukkit.common.Logging;

/**
 * Retrieves assets from a resource pack stored as a zip file on the file system
 */
public class MapResourcePackZipArchive implements MapResourcePackArchive {
    private ZipFile archive = null;
    private final String zipFilePath;

    public MapResourcePackZipArchive(String filePath) {
        this.zipFilePath = filePath;
    }

    @Override
    public void load(boolean lazy) {
        try {
            this.archive = new JarFile(this.zipFilePath);
        } catch (IOException ex) {
            this.archive = null;
            Logging.LOGGER.log(Level.SEVERE, "Failed to load resource pack " + this.zipFilePath, ex);
        }
    }

    @Override
    public InputStream openFileStream(String path) throws IOException {
        if (this.archive != null) {
            ZipEntry entry = this.archive.getEntry(path);
            if (entry != null) {
                return this.archive.getInputStream(entry);
            }
        }
        return null;
    }
}
