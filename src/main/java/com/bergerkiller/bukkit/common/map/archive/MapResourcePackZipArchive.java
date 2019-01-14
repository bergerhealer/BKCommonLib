package com.bergerkiller.bukkit.common.map.archive;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
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
    private final File zipFile;

    public MapResourcePackZipArchive(File zipFile) {
        this.zipFile = zipFile;
    }

    @Override
    public void load(boolean lazy) {
        boolean preferZip = this.zipFile.getName().toLowerCase(Locale.ENGLISH).endsWith(".zip");

        // First try preferred open mode, if this fails the error matters
        IOException error = null;
        try {
            this.open(preferZip);
            return;
        } catch (IOException ex) {
            error = ex;
            this.archive = null;
        }

        // Try non-preferred mode just in case
        try {
            this.open(!preferZip);
            return;
        } catch (IOException ex) {
        }

        // Log preferred mode error
        Logging.LOGGER.log(Level.SEVERE, "Failed to load resource pack " + this.zipFile.getAbsolutePath(), error);
    }

    private void open(boolean useZip) throws IOException {
        this.archive = useZip ? (new ZipFile(this.zipFile)) : (new JarFile(this.zipFile));
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
