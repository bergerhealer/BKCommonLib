package com.bergerkiller.bukkit.common.map.archive;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.map.MapResourcePack;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Retrieves assets from a resource pack stored as an uncompressed directory
 */
public class MapResourcePackDirectoryArchive implements MapResourcePackArchive {
    private final File directory;
    private final FileOverlayView overlayView = new FileOverlayView();

    public MapResourcePackDirectoryArchive(File directory) {
        this.directory = directory;
    }

    @Override
    public String name() {
        return "Directory: " + directory.getName();
    }

    @Override
    public void load(boolean lazy) {
        overlayView.clear();
        if (directory.exists()) {
            try {
                overlayView.load(loader -> {
                    final Path rootPath = directory.toPath();
                    Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                            loader.add(rootPath.relativize(file).toString());

                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                            if (!dir.equals(rootPath)) { // Exclude the root directory itself
                                loader.add(rootPath.relativize(dir) + "/");
                            }

                            return FileVisitResult.CONTINUE;
                        }
                    });
                });
            } catch (SecurityException | IOException ex) {
                Logging.LOGGER.log(Level.SEVERE, "Failed to index resource pack folder " + directory, ex);
            }
        }
    }

    @Override
    public InputStream openFileStream(String path) throws IOException {
        String absolutePath = overlayView.getAbsoluteFilePath(path);
        File sub = new File(directory, absolutePath);
        if (sub.isFile()) {
            return new FileInputStream(sub);
        }
        return null;
    }

    @Override
    public void configure(MapResourcePack.Metadata metadata) {
        overlayView.addOverlays(metadata.getUsedOverlays().stream()
                .map(o -> o.directory)
                .collect(Collectors.toList()));
    }

    @Override
    public List<String> listFiles(String folder, boolean deep) throws IOException {
        return overlayView.listFiles(folder, deep);
    }
}
