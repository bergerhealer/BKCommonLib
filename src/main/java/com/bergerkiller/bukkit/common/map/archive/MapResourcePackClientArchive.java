package com.bergerkiller.bukkit.common.map.archive;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.cdn.MojangIO;
import com.bergerkiller.bukkit.common.map.MapResourcePack;
import com.bergerkiller.bukkit.common.map.gson.MapResourcePackDeserializer;

/**
 * Retrieves assets from the stock Minecraft client jar.
 * This archive will automatically download the correct client jar from Mojang's servers before use.
 */
public class MapResourcePackClientArchive implements MapResourcePackArchive {
    private final String minecraftVersion;
    private final Logger log;
    private final File clientJarFile;
    private final File clientJarTempFile;
    private Map<String, List<String>> directories = null;
    private Map<String, List<String>> deepDirectories = null;
    private JarFile archive = null;

    public MapResourcePackClientArchive() {
        this(Common.MC_VERSION);
    }

    public MapResourcePackClientArchive(String minecraftVersion) {
        this.minecraftVersion = minecraftVersion;
        this.log = Logging.LOGGER;

        File cacheFolder = MojangIO.getCacheFolder();
        this.clientJarFile = new File(cacheFolder, minecraftVersion + ".jar");
        this.clientJarTempFile = new File(cacheFolder, minecraftVersion + ".jar.tmp");
        this.loadArchive();
    }

    @Override
    public String name() {
        return "Vanilla Minecraft " + minecraftVersion;
    }

    @Override
    public MapResourcePack.Metadata tryLoadMetadata(MapResourcePackDeserializer deserializer) {
        return MapResourcePack.Metadata.vanilla(minecraftVersion);
    }

    /**
     * If not already installed, calling this method will automatically download the
     * appropriate Vanilla Minecraft Client jar from the official servers and install them
     * so textures and models can be loaded from them.<br>
     * <br>
     * The Minecraft client resources are owned by Mojang. These assets are installed separately from BKCommonLib.
     * BKCommonLib, nor its developers, distribute (illegal) copies of the Minecraft client.
     * We assume you have already accepted the Minecraft EULA to run the server. If you have not,
     * please read <a href="https://account.mojang.com/documents/minecraft_eula">https://account.mojang.com/documents/minecraft_eula.</a>
     */
    @Override
    public void load(boolean lazy) {
        if (this.archive == null && !this.clientJarFile.exists()) {
            if (lazy) {
                log.severe("The client for Minecraft " + minecraftVersion + " is presently not installed");
                log.severe("Since the plugin did not call load(), it will be downloaded now. This may cause lag.");
                log.severe("To fix this, please tell the plugin author to call load() on startup.");
            }
            try {
                // Informative log to the administrators so they know what is going on
                log.warning("To display Minecraft assets (models, textures) on maps, the Minecraft client jar is required");
                log.warning("BKCommonLib will now download Minecraft " + minecraftVersion + " client jar from Mojang's servers");
                log.warning("The file will be installed in: " + this.clientJarFile.toString());
                log.warning("By installing this Minecraft client you further agree with Mojang's EULA.");
                log.warning("The EULA can be read here: https://account.mojang.com/documents/minecraft_eula");

                // Download the version manifest, to find all available versions
                MojangIO.VersionManifest versionManifest = MojangIO.downloadJson(MojangIO.VersionManifest.class, MojangIO.VersionManifest.URL);
                if (versionManifest.versions.isEmpty()) {
                    throw new DownloadFailure("Failed to download the game version manifest from Mojangs servers");
                }

                // Find version suitable for this server
                MojangIO.VersionManifest.Version currentVersion = versionManifest.findVersion(minecraftVersion);
                if (currentVersion == null) {
                    throw new DownloadFailure("This Minecraft version is not available");
                }

                // Find this version's asset information
                MojangIO.VersionManifest.VersionAssets versionAssets = MojangIO.downloadJson(MojangIO.VersionManifest.VersionAssets.class, currentVersion.url);
                MojangIO.VersionManifest.VersionAssets.Download download = versionAssets.downloads.get("client");
                if (download == null) {
                    throw new DownloadFailure("This Minecraft version has no downloadable client jar");
                }

                // Download the file
                MojangIO.downloadFile("Minecraft Client " + minecraftVersion + ".jar", download, this.clientJarTempFile);

                // Done, move temporary file to final destination
                this.clientJarFile.delete();
                this.clientJarTempFile.renameTo(this.clientJarFile);

                // Load the archive that we downloaded now
                this.loadArchive();
                if (this.archive == null) {
                    throw new IOException("Jar file is corrupt");
                }
            } catch (DownloadFailure f) {
                log.severe("Failed to download the Minecraft " + minecraftVersion + " client: " + f.getMessage());
                this.clientJarFile.delete();
                this.clientJarTempFile.delete();
                logAlternative();
            } catch (Throwable t) {
                log.log(Level.SEVERE, "Failed to download the Minecraft " + minecraftVersion + " client:", t);
                this.clientJarFile.delete();
                this.clientJarTempFile.delete();
                logAlternative();
            }
        }
    }

    private void logAlternative() {
        log.severe("If automatically downloading the client is impossible at this time, you can manually install it.");
        log.severe("Install the correct Minecraft client jar file for " + minecraftVersion + " in the following location:");
        log.severe("> " + clientJarFile.getAbsolutePath());
    }

    private void loadArchive() {
        if (clientJarFile.exists()) {
            try {
                this.archive = new JarFile(this.clientJarFile);
            } catch (IOException ex) {
                this.archive = null;
                log.severe("Failed to load the Minecraft client jar for accessing resources!");
                log.severe("In case the file is corrupt, try deleting it so it is re-downloaded:");
                log.severe("> " + this.clientJarFile.getAbsolutePath());
                log.log(Level.SEVERE, "Loading " + this.clientJarFile.getName() + " failed!", ex);
            }
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

    @Override
    public List<String> listFiles(String folder, boolean deep) throws IOException {
        if (this.directories == null) {
            if (this.archive == null) {
                return Collections.emptyList();
            } else {
                this.directories = MapResourcePackZipArchive.readDirectories(this.archive.stream()
                        .filter(e -> e.getName().startsWith("assets/minecraft") ||
                                     e.getName().startsWith("data/minecraft")));
            }
        }

        if (deep) {
            if (this.deepDirectories == null) {
                this.deepDirectories = MapResourcePackZipArchive.computeDeepDirectories(this.directories);
            }
            return this.deepDirectories.getOrDefault(folder, Collections.emptyList());
        } else {
            return this.directories.getOrDefault(folder, Collections.emptyList());
        }
    }

    private static class DownloadFailure extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public DownloadFailure(String message) {
            super(message);
        }
    }

}
