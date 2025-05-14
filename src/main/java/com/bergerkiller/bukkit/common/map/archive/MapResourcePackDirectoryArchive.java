package com.bergerkiller.bukkit.common.map.archive;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
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
    public String name() {
        return "Directory: " + directory.getName();
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
    public List<String> listFiles(String folder, boolean deep) throws IOException {
        File sub = new File(directory, folder);
        if (!sub.exists() || !sub.isDirectory()) {
            return Collections.emptyList();
        }

        if (deep) {
            // Deep listing
            final Path rootPath = sub.toPath();
            final List<String> result = new ArrayList<>();

            Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    result.add(rootPath.relativize(file).toString());
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    if (!dir.equals(rootPath)) { // Exclude the root directory itself
                        result.add(rootPath.relativize(dir) + "/");
                    }
                    return FileVisitResult.CONTINUE;
                }
            });

            return result;
        } else {
            // Non-recursive file listing
            File[] files = sub.listFiles();
            if (files == null) {
                return Collections.emptyList();
            }

            List<String> result = new ArrayList<>(files.length);
            for (File file : files) {
                if (file.isDirectory()) {
                    result.add(file.getName() + "/");
                } else {
                    result.add(file.getName());
                }
            }
            return result;
        }
    }
}
