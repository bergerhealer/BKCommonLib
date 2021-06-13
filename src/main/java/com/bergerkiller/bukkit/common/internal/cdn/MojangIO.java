package com.bergerkiller.bukkit.common.internal.cdn;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Some helper utility classes specifically for Mojang file content
 */
public class MojangIO {

    /**
     * Retrieves the minecraft cache folder, where previously downloaded
     * mojang content is stored. The folder is created if it does not
     * already exist.
     *
     * @return Mojang minecraft cache folder
     */
    public static File getCacheFolder() {
        File file;
        if (Common.IS_TEST_MODE) {
            file = new File(System.getProperty("user.dir"));
        } else {
            file = CommonPlugin.getInstance().getDataFolder();
        }
        file = new File(file, "minecraft");
        file.mkdirs();
        return file;
    }

    /**
     * Downloads a JSON resource from the internet
     *
     * @param <T> Type of GSON document
     * @param type GSON document type
     * @param urlString The URL to download
     * @return Downloaded and decoded JSON (GSON document)
     * @throws IOException - If something goes wrong
     */
    public static <T> T downloadJson(Class<T> type, String urlString) throws IOException {
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

    /**
     * Downloads the file contents of a downloadable asset
     *
     * @param title Title to display during logging
     * @param download The version asset to download
     * @param destinationFile Where to save the file after downloading
     * @throws IOException - If the file could not be downloaded, or corruption was
     *                       detected when verifying the downloaded file.
     */
    public static void downloadFile(String title, VersionManifest.VersionAssets.Download download, File destinationFile) throws IOException {
        // Used to not log 10x all the time, but less often sometimes.
        long logTimeStart = System.currentTimeMillis();
        boolean needsLogging = false;
        boolean hasLoggedProgress = false;

        // Perform the downloading to file
        Logging.LOGGER.warning("> Fetching " + title + " from Mojang servers: " + download.url);
        try (FileOutputStream outStream = new FileOutputStream(destinationFile)) {
            URL dlUrl = new URL(download.url);
            try (InputStream dlInputStream = dlUrl.openStream()) {
                int n;
                int old_progress = 0;
                long downloaded = 0L;
                byte[] buffer = new byte[4096];
                while ((n = dlInputStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, n);
                    downloaded += n;

                    if (download.size == 0) {
                        continue;
                    }

                    // percentage indication in 10 steps
                    int progress = (int) ((100 * downloaded) / download.size);
                    if (progress >= (old_progress + 10)) {
                        old_progress = progress;
                        needsLogging = true;
                    }

                    // if we want to log (step) and it's been long enough ago since last log, well, log!
                    if (needsLogging && ((System.currentTimeMillis() - logTimeStart) > 1000)) {
                        logTimeStart = System.currentTimeMillis();
                        needsLogging = false;
                        hasLoggedProgress = true;
                        Logging.LOGGER.warning("> Downloading " + title + ": " + progress + "%");
                    }
                }
            }
        }

        // Final time, in case the 100% log was skipped
        // Sometimes the download is so quick, we don't even need to see this.
        if (needsLogging && hasLoggedProgress) {
            needsLogging = false;
            Logging.LOGGER.warning("> Downloading " + title + ": 100%");
        }

        // Verify and log completion
        if (verifyFile(title, download, destinationFile)) {
            Logging.LOGGER.warning("> " + title + " verified, download completed!");
        } else {
            Logging.LOGGER.warning("> " + title + " download completed!");
        }
    }

    /**
     * Verifies the downloaded file. If the file was verified, true is returned.
     * If no verification was done, returns false.
     *
     * @param title
     * @param download
     * @param file
     * @return True if verification was performed
     * @throws IOException - If the checksum does not match or file could not be accessed
     */
    private static boolean verifyFile(String title, VersionManifest.VersionAssets.Download download, File file) throws IOException {
        // Decode the sha1 in the version manifest as a BigInteger
        if (download.sha1 == null) {
            Logging.LOGGER.warning("> Download metadata has no checksum, no file verification will be performed");
            return false;
        }
        BigInteger checksum;
        try {
            checksum = new BigInteger(download.sha1, 16);
        } catch (NumberFormatException ex) {
            Logging.LOGGER.warning("> Download metadata has corrupted checksum, no file verification will be performed");
            return false;
        }

        // Compute sha256 of the file on disk to verify no corruption has occurred
        MessageDigest shaDigest;
        try {
            shaDigest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            Logging.LOGGER.warning("> SHA-1 not supported by the JVM, no file verification will be performed");
            return false;
        }

        // Compute message digest one chunk at a time
        Logging.LOGGER.warning("> Verifying " + title + "...");
        try (FileInputStream fis = new FileInputStream(file)) {
            int bytesCount = 0; 
            byte[] buffer = new byte[4096];
            while ((bytesCount = fis.read(buffer)) != -1) {
                shaDigest.update(buffer, 0, bytesCount);
            };
        }

        BigInteger computed = new BigInteger(1, shaDigest.digest());
        if (computed.equals(checksum)) {
            return true;
        }

        throw new IOException("Checksum of downloaded file does not match");
    }

    /**
     * Used to parse the JSON when downloading the right Minecraft Client jar
     */
    public static class VersionManifest {
        public static final String URL = "https://launchermeta.mojang.com/mc/game/version_manifest.json";
        public List<Version> versions = Collections.emptyList();

        public Version findVersion(String minecraftVersion) {
            for (Version version : versions) {
                if (version.id != null && version.id.equals(minecraftVersion)) {
                    return version;
                }
            }
            return null;
        }

        public static class Version {
            public String id;
            public String url;
        }

        public static class VersionAssets {
            public Map<String, Download> downloads = Collections.emptyMap();

            public static class Download {
                public String sha1;
                public long size;
                public String url;
            }
        }
    }

}
