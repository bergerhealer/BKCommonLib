package com.bergerkiller.bukkit.common.map.archive;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.map.util.VersionManifest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Retrieves assets from the stock Minecraft client jar.
 * This archive will automatically download the correct client jar from Mojang's servers before use.
 */
public class MapResourcePackClientArchive implements MapResourcePackArchive {
    private final Logger log;
    private final File clientJarFile;
    private final File clientJarTempFile;
    private JarFile archive = null;

    public MapResourcePackClientArchive() {
        super();
        this.log = Logging.LOGGER;

        File file;
        if (Common.IS_TEST_MODE) {
            file = new File(System.getProperty("user.dir"));
        } else {
            file = CommonPlugin.getInstance().getDataFolder();
        }
        file = new File(file, "minecraft");
        file.mkdirs();

        this.clientJarFile = new File(file, Common.MC_VERSION + ".jar");
        this.clientJarTempFile = new File(file, Common.MC_VERSION + ".jar.tmp");
        this.loadArchive();
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
                log.severe("The client for Minecraft " + Common.MC_VERSION + " is presently not installed");
                log.severe("Since the plugin did not call load(), it will be downloaded now. This may cause lag.");
                log.severe("To fix this, please tell the plugin author to call load() on startup.");
            }
            try {
                // Informative log to the administrators so they know what is going on
                log.warning("To display Minecraft assets (models, textures) on maps, the Minecraft client jar is required");
                log.warning("BKCommonLib will now download Minecraft " + Common.MC_VERSION + " client jar from Mojang's servers");
                log.warning("The file will be installed in: " + this.clientJarFile.toString());
                log.warning("By installing this Minecraft client you further indicate agreement to Mojang's EULA.");
                log.warning("The EULA can be read here: https://account.mojang.com/documents/minecraft_eula");

                // Download the version manifest, to find all available versions
                VersionManifest versionManifest = downloadJson(VersionManifest.class, "https://launchermeta.mojang.com/mc/game/version_manifest.json");
                if (versionManifest.versions.isEmpty()) {
                    throw new DownloadFailure("Version manifest does not contain any downloadable versions");
                }

                // Find version suitable for this server
                VersionManifest.Version currentVersion = null;
                for (VersionManifest.Version version : versionManifest.versions) {
                    if (version.id != null && version.id.equals(Common.MC_VERSION)) {
                        currentVersion = version;
                        break;
                    }
                }
                if (currentVersion == null) {
                    throw new DownloadFailure("This Minecraft version is not available");
                }

                // Find this version's asset information
                VersionManifest.VersionAssets versionAssets = downloadJson(VersionManifest.VersionAssets.class, currentVersion.url);
                VersionManifest.VersionAssets.Download download = versionAssets.downloads.get("client");
                if (download == null) {
                    throw new DownloadFailure("This Minecraft version has no downloadable client jar");
                }

                // Download the file
                log.warning("> Fetching Minecraft Client " + Common.MC_VERSION + " from Mojang servers: " + download.url);
                FileOutputStream outStream = new FileOutputStream(this.clientJarTempFile);
                try {
                    URL dlUrl = new URL(download.url);
                    InputStream dlInputStream = dlUrl.openStream();
                    try {
                        int n;
                        int old_progress = 0;
                        long downloaded = 0L;
                        byte[] buffer = new byte[4096];
                        while ((n = dlInputStream.read(buffer)) != -1) {
                            outStream.write(buffer, 0, n);
                            downloaded += n;

                            // percentage indication in 10 steps
                            int progress = (int) ((100 * downloaded) / download.size);
                            if (progress >= (old_progress + 10)) {
                                old_progress = progress;
                                log.warning("> Downloading Minecraft Client " + Common.MC_VERSION + ".jar: " + progress + "%");
                            }
                        }
                    } finally {
                        dlInputStream.close();
                    }
                } finally {
                    outStream.close();
                }

                // Done, move temporary file to final destination
                this.clientJarFile.delete();
                this.clientJarTempFile.renameTo(this.clientJarFile);

                // Load the archive that we downloaded now
                this.loadArchive();
                if (this.archive == null) {
                    throw new IOException("Jar file is corrupt");
                }
            } catch (DownloadFailure f) {
                log.severe("Failed to download the Minecraft " + Common.MC_VERSION + " client: " + f.getMessage());
                this.clientJarFile.delete();
                this.clientJarTempFile.delete();
                logAlternative();
            } catch (Throwable t) {
                log.log(Level.SEVERE, "Failed to download the Minecraft " + Common.MC_VERSION + " client:", t);
                this.clientJarFile.delete();
                this.clientJarTempFile.delete();
                logAlternative();
            }
        }
    }

    private static <T> T downloadJson(Class<T> type, String urlString) throws IOException {
        Logging.LOGGER.warning("> Fetching JSON from Mojang servers: " + urlString);
        URL url = new URL(urlString);
        InputStream inputStream = url.openStream();
        try {
            Reader reader = new InputStreamReader(inputStream, "UTF-8");
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            return gson.fromJson(reader, type);
        } finally {
            inputStream.close();
        }
    }

    private void logAlternative() {
        log.severe("If automatically downloading the client is impossible at this time, you can manually install it.");
        log.severe("Install the correct Minecraft client jar file for " + Common.MC_VERSION + " in the following location:");
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

    private static class DownloadFailure extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public DownloadFailure(String message) {
            super(message);
        }
    }

}
