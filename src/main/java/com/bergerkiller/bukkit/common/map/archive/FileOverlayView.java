package com.bergerkiller.bukkit.common.map.archive;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles the listing of files and directories, supporting Minecraft's overlay
 * system to make subdirectories available at the root path. Also converts
 * these overlay paths to the absolute paths where the file can be read.
 */
class FileOverlayView {
    /** Root entry of the actual file system. Does not include overlay details */
    private DirectoryEntry root = new DirectoryEntry("/");
    /** All directory entries mapped to their absolute (overlay-mapped) folder */
    private final Map<String, DirectoryEntry> directories = new HashMap<>();

    /**
     * Lists all of the files and directories inside of a directory
     *
     * @param directoryPath Directory path
     * @param deep True to list all nested files and directories too
     * @return List of files and directories. Directory names end with a / slash.
     */
    public List<String> listFiles(String directoryPath, boolean deep) {
        DirectoryEntry directory = directories.get(directoryPath);
        if (directory != null) {
            return deep ? directory.deepNameList : directory.nameList;
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Gets the absolute file path of where a relative (overlay) file is found.
     * This performs overlay directory translations to the true location,
     * and respects overlay-overriding behavior.
     *
     * @param filePath File path
     * @return Overlay-remapped file path. Or the same path if it does not exist
     *         inside the previously-loaded directory tree.
     */
    public String getAbsoluteFilePath(String filePath) {
        int directoryEnd = filePath.lastIndexOf('/');

        // Directories are not supported (they are merged and such, so they don't have only one location)
        if (directoryEnd == (filePath.length() - 1)) {
            return filePath;
        }

        // Find the directory and file name
        DirectoryEntry directory;
        String name;
        if (directoryEnd == -1) {
            directory = root;
            name = filePath;
        } else {
            directory = directories.get(filePath.substring(0, directoryEnd + 1));
            if (directory == null) {
                return filePath;
            } else {
                name = filePath.substring(directoryEnd + 1);
            }
        }

        // Find the file by this name in the overlay directory, and if found, return its absolute path
        Entry e = directory.entriesWithOverlays.get(name);
        if (e instanceof FileEntry) {
            return ((FileEntry) e).absolutePath;
        } else {
            return filePath;
        }
    }

    /**
     * Clears all previously loaded data, making it empty
     */
    public void clear() {
        root = new DirectoryEntry("/");
        directories.clear();
    }

    /**
     * Loads file tree contents into this overlay view using a loader callback function.
     * The callback function can throw an IOException, in which case loading is aborted
     * and this view is not modified. This exception is then re-thrown.
     *
     * @param loaderFunc Loader Function Callback
     * @throws IOException If the loader function throws one
     */
    public void load(FileLoaderFunc loaderFunc) throws IOException {
        // Load data into the tree builder without affecting this overlay view
        final DirectoryTreeBuilder tree = new DirectoryTreeBuilder(new DirectoryEntry("/"));
        tree.load(loaderFunc);

        // Load into this overlay view, not that loading has succeeded
        // Make the directory entries read-only from this point
        this.root = tree.root;
        tree.entries.values().forEach(DirectoryEntry::makeReadOnly);

        // Initially, no overlays are loaded in yet, so make the overlay entries
        // the same as the absolute path entries.
        tree.entries.values().forEach(DirectoryEntry::reset);

        // If overlay rules were already added, apply them to the tree now
        applyOverlays();

        // Initialize the by-path lookup table and all the (flattened) lists of names
        loadDirectoryTree();
    }

    public void applyOverlays() {
        //TODO: Implement
    }

    private void loadDirectoryTree() {
        // Name lists / deep name lists
        root.initDeepNameLists(true);

        // By-directory-name lookup table
        directories.clear();
        directories.put("/", root);
        root.loadIntoDirectoriesMap(directories, "");
    }

    private static final class DirectoryTreeBuilder {
        public final Map<String, DirectoryEntry> entries = new HashMap<>();
        public final DirectoryEntry root;

        public DirectoryTreeBuilder(DirectoryEntry root) {
            this.entries.put("/", root);
            this.root = root;
        }

        // For during directory lookup
        private int currIdx, lastIdx;

        public DirectoryEntry add(final String directory) {
            // Fast: already existing (or added) or the root / directory (already added)
            {
                DirectoryEntry existing = entries.get(directory);
                if (existing != null) {
                    return existing;
                }
            }

            // Slow: navigate the directory folder by folder and initialize each as an entry
            lastIdx = 0;
            DirectoryEntry entry = root;
            boolean lookupInCache = true;
            while ((currIdx = (directory.indexOf('/', lastIdx) + 1)) != 0) {
                String subDirAbsolutePathKey = directory.substring(0, currIdx);

                final DirectoryEntry parentEntry = entry;
                entry = entries.computeIfAbsent(subDirAbsolutePathKey, subDirAbsolutePath -> {
                    // Does not exist, create a new one with this name
                    String subDirName = directory.substring(lastIdx, currIdx);
                    return parentEntry.addDirectory(subDirName);
                });

                lastIdx = currIdx;
            }

            return entry;
        }

        public void load(FileLoaderFunc loaderFunc) throws IOException {
            loaderFunc.load(path -> {
                int directoryIdx = path.lastIndexOf('/');
                if (directoryIdx == (path.length() - 1)) {
                    add(path);
                } else if (directoryIdx == -1) {
                    root.addFile("/", path);
                } else {
                    String directory = path.substring(0, directoryIdx + 1);
                    add(directory).addFile(directory, path.substring(directoryIdx + 1));
                }
            });
        }
    }

    @FunctionalInterface
    public interface FileLoaderFunc {
        void load(FileLoader loader) throws IOException;
    }

    @FunctionalInterface
    public interface FileLoader {
        /**
         * Adds a single file or directory
         *
         * @param path Full directory or file path. Should end in a / slash
         *             if it is for a directory. Should not have a / slash prefix,
         *             unless it is the root directory.
         */
        void add(String path);
    }

    private static class DirectoryEntry extends Entry {
        /** Read-only list of files and directories inside this directory */
        public List<Entry> entries = new ArrayList<>();

        /** List of files and directories inside this directory, with overlay modifications */
        public Map<String, Entry> entriesWithOverlays = null;
        /** List of file and folder names inside of this directory */
        public List<String> nameList = null;
        /** Flattened list of file and folder names inside of this directory and all sub-directories */
        public List<String> deepNameList = null;

        public DirectoryEntry(String name) {
            super(name);
        }

        public void makeReadOnly() {
            if (this.entries instanceof ArrayList) {
                ((ArrayList<?>) this.entries).trimToSize();
                this.entries = Collections.unmodifiableList(this.entries);
            }
        }

        public void reset() {
            entriesWithOverlays = new HashMap<>();
            entries.forEach(e -> entriesWithOverlays.put(e.name, e));
            nameList = null;
            deepNameList = null;
        }

        public void loadIntoDirectoriesMap(Map<String, DirectoryEntry> directories, String currentPath) {
            for (Entry e : entriesWithOverlays.values()) {
                if (e instanceof DirectoryEntry) {
                    DirectoryEntry de = (DirectoryEntry) e;
                    String dePath = currentPath + de.name;
                    directories.put(dePath, de);
                    de.loadIntoDirectoriesMap(directories, dePath);
                }
            }
        }

        public List<String> initDeepNameLists(boolean isRoot) {
            final ArrayList<String> nameList = new ArrayList<>(this.entriesWithOverlays.size());
            final ArrayList<String> deepNameList = new ArrayList<>(this.entriesWithOverlays.size());

            // Get all entries inside this directory and add them, ordered directories first, then
            // sorted by name alphabetically.
            entriesWithOverlays.values().stream()
                    .sorted()
                    .forEachOrdered(e -> {
                        nameList.add(e.name);
                        deepNameList.add(e.name);

                        // Directory - add name followed by the deep contents (recursive)
                        if (e instanceof DirectoryEntry) {
                            DirectoryEntry subDir = (DirectoryEntry) e;
                            if (isRoot) {
                                deepNameList.addAll(subDir.initDeepNameLists(false));
                            } else {
                                for (String deepName : subDir.initDeepNameLists(false)) {
                                    deepNameList.add(subDir.name + deepName);
                                }
                            }
                        }
                    });

            nameList.trimToSize();
            deepNameList.trimToSize();

            this.nameList = Collections.unmodifiableList(nameList);
            this.deepNameList = Collections.unmodifiableList(deepNameList);

            return this.deepNameList;
        }

        public DirectoryEntry addDirectory(String name) {
            DirectoryEntry e = new DirectoryEntry(name);
            this.entries.add(e);
            return e;
        }

        public void addFile(String directory, String name) {
            this.entries.add(new FileEntry(name, directory + name));
        }

        @Override
        public int compareTo(Entry o) {
            if (o instanceof DirectoryEntry) {
                return super.compareTo(o);
            } else {
                return -1;
            }
        }
    }

    private static class FileEntry extends Entry {
        /** Absolute path where the file or directory can be found on the filesystem */
        public final String absolutePath;

        public FileEntry(String name, String absolutePath) {
            super(name);
            this.absolutePath = absolutePath;
        }

        @Override
        public int compareTo(Entry o) {
            if (o instanceof FileEntry) {
                return super.compareTo(o);
            } else {
                return 1;
            }
        }
    }

    private static class Entry implements Comparable<Entry> {
        /** Name of the file or directory */
        public final String name;

        public Entry(String name) {
            this.name = name;
        }

        @Override
        public int compareTo(Entry o) {
            return name.compareTo(o.name);
        }
    }
}
