package com.bergerkiller.bukkit.common.map.archive;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.map.MapResourcePack;

/**
 * Retrieves assets from a resource pack stored as a zip file on the file system
 */
public class MapResourcePackZipArchive implements MapResourcePackArchive {
    private ZipFile archive = null;
    private final FileOverlayView overlayView = new FileOverlayView();
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
        overlayView.clear();

        if (openArchive()) {
            try {
                overlayView.load(loader -> {
                    archive.stream().map(ZipEntry::getName).forEach(loader::add);
                });
            } catch (Throwable t) {
                Logging.LOGGER.log(Level.SEVERE, "Failed to index resource pack archive", t);
            }
        }
    }

    @Override
    public void configure(MapResourcePack.Metadata metadata) {
        overlayView.addOverlays(metadata.getUsedOverlays().stream()
                .map(o -> o.directory)
                .collect(Collectors.toList()));
    }

    private boolean openArchive() {
        boolean preferZip = this.zipFile.getName().toLowerCase(Locale.ENGLISH).endsWith(".zip");

        // First try preferred open mode, if this fails the error matters
        IOException error = null;
        try {
            this.openWithMode(preferZip);
            return true;
        } catch (IOException ex) {
            error = ex;
            this.archive = null;
        }

        // Try non-preferred mode just in case
        try {
            this.openWithMode(!preferZip);
            return true;
        } catch (IOException ex) {
        }

        // Log preferred mode error
        Logging.LOGGER.log(Level.SEVERE, "Failed to load resource pack " + this.zipFile.getAbsolutePath(), error);
        return false;
    }

    private void openWithMode(boolean useZip) throws IOException {
        this.archive = useZip ? (new ZipFile(this.zipFile)) : (new JarFile(this.zipFile));
    }

    @Override
    public ArchiveResource openResource(final String path) {
        if (this.archive == null) {
            return null;
        }

        final String absoluteFilePath = overlayView.getAbsoluteFilePath(path);
        if (!overlayView.hasAbsoluteFile(absoluteFilePath)) {
            return null;
        }

        return () -> {
            ZipEntry entry = archive.getEntry(absoluteFilePath);
            if (entry == null) {
                throw new IOException("Unexpected resource not found: " + path);
            } else {
                return archive.getInputStream(entry);
            }
        };
    }

    @Override
    public List<String> listFiles(String folder, boolean deep) throws IOException {
        return overlayView.listFiles(folder, deep);
    }
}
