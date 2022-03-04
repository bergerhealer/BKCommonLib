package com.bergerkiller.bukkit.common.map.archive;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.bergerkiller.bukkit.common.Logging;

/**
 * Retrieves assets from a resource pack stored as a zip file on the file system
 */
public class MapResourcePackZipArchive implements MapResourcePackArchive {
    private ZipFile archive = null;
    private Map<String, List<String>> directories = null;
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

    @Override
    public List<String> listFiles(String folder) throws IOException {
        if (this.directories == null) {
            if (this.archive == null) {
                return Collections.emptyList();
            } else {
                this.directories = MapResourcePackZipArchive.readDirectories(this.archive.stream());
            }
        }

        return this.directories.getOrDefault(folder, Collections.emptyList());
    }

    static Map<String, List<String>> readDirectories(Stream<? extends ZipEntry> entries) {
        try {
            final Map<String, List<String>> dirs = new HashMap<>();
            entries.map(ZipEntry::getName)
                   .forEach(path -> {
                       int startIdx = (path.endsWith("/") ? (path.length()-2) : (path.length()-1));
                       int parentEndIdx = path.lastIndexOf('/', startIdx);
                       String parentPath = (parentEndIdx == -1) ? "/" : path.substring(0, parentEndIdx+1);
                       String name = (parentEndIdx == -1) ? path : path.substring(parentEndIdx+1);
                       dirs.computeIfAbsent(parentPath, un -> new ArrayList<>()).add(name);
                   });
            return dirs;
        } catch (Throwable t) {
            Logging.LOGGER.log(Level.SEVERE, "Failed to index resource pack archive", t);
            return Collections.emptyMap();
        }
    }
}
