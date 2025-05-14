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
    private Map<String, List<String>> deepDirectories = null;
    private final File zipFile;

    public MapResourcePackZipArchive(File zipFile) {
        if (zipFile == null) {
            throw new IllegalArgumentException("Zip file cannot be null");
        }
        this.zipFile = zipFile;
    }

    @Override
    public String name() {
        return "ZIP: " + zipFile;
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
    public List<String> listFiles(String folder, boolean deep) throws IOException {
        if (this.directories == null) {
            if (this.archive == null) {
                return Collections.emptyList();
            } else {
                this.directories = MapResourcePackZipArchive.readDirectories(this.archive.stream());
            }
        }

        if (deep) {
            if (this.deepDirectories == null) {
                this.deepDirectories = computeDeepDirectories(this.directories);
            }
            return this.deepDirectories.getOrDefault(folder, Collections.emptyList());
        } else {
            return this.directories.getOrDefault(folder, Collections.emptyList());
        }
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

            dirs.values().forEach(Collections::sort);
            return dirs;
        } catch (Throwable t) {
            Logging.LOGGER.log(Level.SEVERE, "Failed to index resource pack archive", t);
            return Collections.emptyMap();
        }
    }

    static Map<String, List<String>> computeDeepDirectories(Map<String, List<String>> directories) {
        Map<String, List<String>> deep = new HashMap<>(directories.size());

        // Go by all keys and look for parent directories, and populate accordingly
        for (Map.Entry<String, List<String>> e : directories.entrySet()) {
            String key = e.getKey();
            List<String> files = e.getValue();

            deep.computeIfAbsent(key, k -> new ArrayList<>()).addAll(files);
            while (!key.equals("/")) {
                String prefix;

                {
                    int nextIndex = key.lastIndexOf('/', key.length() - 2);
                    if (nextIndex != -1) {
                        // Parent folder
                        prefix = key.substring(nextIndex + 1);
                        key = key.substring(0, nextIndex + 1);
                    } else {
                        // Root
                        prefix = key;
                        key = "/";
                    }
                }

                // Clone the list with the key prefix at this level added in front
                files = new ArrayList<>(files);
                for (int i = 0; i < files.size(); i++) {
                    files.set(i, prefix + files.get(i));
                }

                // Add to deep map
                deep.computeIfAbsent(key, k -> new ArrayList<>()).addAll(files);
            }
        }

        deep.values().forEach(Collections::sort);
        return deep;
    }
}
