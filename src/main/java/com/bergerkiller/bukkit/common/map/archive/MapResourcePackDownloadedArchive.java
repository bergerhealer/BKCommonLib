package com.bergerkiller.bukkit.common.map.archive;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Keeps a locally downloaded archive synchronized by checking the resource pack SHA1 against a hash.
 * In general, makes sure an archive is downloaded and available.
 * When the hash string is kept empty, it is only re-downloaded when the path/url is changed.
 */
public class MapResourcePackDownloadedArchive implements MapResourcePackArchive {
    private final File resourcepacksFolder;
    private final URL resourcePackURL;
    private final Optional<String> resourcePackHash;
    private final Logger log;
    private MapResourcePackArchive archive = null;

    public MapResourcePackDownloadedArchive(URL resourcePackURL, String resourcePackHash) {
        if (resourcePackURL == null) {
            throw new IllegalArgumentException("Resource pack URL cannot be null");
        }
        this.resourcePackURL = resourcePackURL;
        this.resourcePackHash = (resourcePackHash == null || resourcePackHash.isEmpty())
                ? Optional.empty() : Optional.of(resourcePackHash);
        this.log = Logging.LOGGER_MAPDISPLAY;

        File pluginFolder;
        if (CommonBootstrap.isTestMode()) {
            pluginFolder = new File(System.getProperty("user.dir"));
        } else {
            pluginFolder = CommonPlugin.getInstance().getDataFolder();
        }
        this.resourcepacksFolder = new File(pluginFolder, "resourcepacks");
    }

    @Override
    public String name() {
        return "URL: " + resourcePackURL;
    }

    @Override
    public void load(boolean lazy) {
        // Prepare the location of the resource packs folder
        this.resourcepacksFolder.mkdirs();

        // Read the resource packs index json file
        boolean indexChanged = false;
        PackIndex index = new PackIndex();
        Gson gson = new GsonBuilder().create();
        File resourcepacksIndex = new File(resourcepacksFolder, "index.json");
        if (resourcepacksIndex.isFile()) {
            try (Reader reader = new FileReader(resourcepacksIndex)) {
                index = gson.fromJson(reader, PackIndex.class);
            } catch (Throwable t) {
                log.log(Level.SEVERE, "Failed to read resource packs index", t);
            }
        }
        if (index == null) {
            index = new PackIndex();
        }

        // Remove index entries linking to resource pack files that don't exist
        // At the same time, try to find the index entry that matches the URL
        PackIndex.Entry foundEntry = null;
        Iterator<PackIndex.Entry> entryIter = index.entries.iterator();
        while (entryIter.hasNext()) {
            PackIndex.Entry entry = entryIter.next();
            File entryFile = new File(resourcepacksFolder, entry.file);
            if (!entryFile.isFile()) {
                entryIter.remove();
                log.log(Level.WARNING, "Resource pack " + entry.file + " no longer exists in the cache");
                indexChanged = true;
                continue;
            }
            if (entry.url == null) {
                entryIter.remove();
                log.log(Level.WARNING, "Resource pack " + entry.file + " has an invalid URL set");
                indexChanged = true;
                continue;
            }
            if (entry.url.equals(this.resourcePackURL)) {
                foundEntry = entry;
            }
        }

        // If the entry was not found, or a hash was set and the hash differs, re-download the file
        if (foundEntry == null || !resourcePackHash.map(foundEntry::checkHash).orElse(true)) {
            // Download the resource pack and save it as a temporary file
            if (lazy) {
                log.severe("A resource pack is being downloaded while the server is running. This will result in lag.");
            }
            PackDownload download = downloadPack();
            if (download != null) {
                // Create new entry if needed
                if (foundEntry == null) {
                    foundEntry = new PackIndex.Entry();
                    index.entries = new ArrayList<PackIndex.Entry>(index.entries);
                    index.entries.add(foundEntry);
                }

                // If the real file name is the same as the one that already exists, delete the old one
                if (foundEntry.file.equalsIgnoreCase(download.realName)) {
                    File f = new File(this.resourcepacksFolder, foundEntry.file);
                    if (!f.delete()) {
                        log.warning("Failed to delete outdated resource pack: " + foundEntry.file);
                    }
                }

                // Figure out the file to save as. If the file already exists, pick something else.
                File resFile = new File(this.resourcepacksFolder, download.realName);
                for (int i = 1; resFile.isFile(); i++) {
                    String prefix = download.realName;
                    String postfix = "";
                    int extIdx = download.realName.lastIndexOf('.');
                    if (extIdx != -1) {
                        prefix = download.realName.substring(0, extIdx);
                        postfix = download.realName.substring(extIdx);
                    }
                    resFile = new File(this.resourcepacksFolder, prefix + i + postfix);
                }

                // Rename the temp file to the wanted file
                File tmpFile = new File(this.resourcepacksFolder, download.tempName);
                if (!tmpFile.renameTo(resFile)) {
                    log.warning("Failed to rename " + download.tempName + " to " + resFile.getName());
                }

                // Update the entry
                foundEntry.url = this.resourcePackURL;
                foundEntry.file = resFile.getName();
                foundEntry.hash = download.hash;
                indexChanged = true;

                // Log a warning when the calculated hash does not match the expected
                if (!resourcePackHash.map(foundEntry::checkHash).orElse(true)) {
                    log.warning("The downloaded resource pack SHA-1 hash does not match with what was expected");
                    log.warning("Expected " + this.resourcePackHash.get() + ", but was " + download.hash);
                    log.warning("The resource pack will be re-downloaded every time it is loaded unless this is fixed!");
                }
            }
        }

        // If changed, write the resource packs index file
        if (indexChanged) {
            try (Writer writer = new FileWriter(resourcepacksIndex)) {
                gson.toJson(index, writer);
            } catch (Throwable t) {
                log.log(Level.SEVERE, "Failed to write resource packs index", t);
            }
        }

        // Set the archive if an entry was successfully added/found
        if (foundEntry != null) {
            this.archive = new MapResourcePackZipArchive(new File(resourcepacksFolder, foundEntry.file));
            this.archive.load(lazy);
        } else {
            this.archive = null;
        }
    }

    private PackDownload downloadPack() {
        // Log that we are going to be downloading!
        log.warning("Downloading resource pack " + this.resourcePackURL + "...");

        // Maximum 10 redirects, to prevent a redirect DDOS
        URL currURL = this.resourcePackURL;
        for (int n = 0; n < 10; n++) {
            // Open a connection for downloading the resource pack
            URLConnection con;
            try {
                con = currURL.openConnection();
                con.addRequestProperty("X-Minecraft-Version-ID", Common.MC_VERSION);
                // X-Minecraft-Pack-Format: 22
                con.addRequestProperty("X-Minecraft-Version", Common.MC_VERSION);
                con.addRequestProperty("User-Agent", "Minecraft Java/" + Common.MC_VERSION);
                con.addRequestProperty("X-Minecraft-Username", "plugin_bkcommonlib");
                con.addRequestProperty("X-Minecraft-UUID", new UUID(0L, 0L).toString());

                con.setReadTimeout(60000);

                // Check for redirects
                if (con instanceof HttpURLConnection) {
                    int code = ((HttpURLConnection) con).getResponseCode();
                    if (code ==  HttpURLConnection.HTTP_MOVED_PERM || code ==  HttpURLConnection.HTTP_MOVED_TEMP) {
                        // Redirect to the new location
                        String redirectLocation = con.getHeaderField("Location");
                        redirectLocation = URLDecoder.decode(redirectLocation, "UTF-8");
                        URL redirectURL = new URL(currURL, redirectLocation); // Deal with relative URLs
                        currURL = redirectURL;
                        log.warning("Redirected to " + redirectURL + "...");
                        ((HttpURLConnection) con).disconnect();
                        continue;
                    }

                    //TODO: Should we check for other types of error codes?
                }

                // Try to download the pack
                return downloadPackFileContent(con, currURL);
            } catch (IOException ex) {
                log.log(Level.SEVERE, "Failed to start download for " + currURL, ex);
                return null;
            }
        }

        // Bad server
        log.log(Level.SEVERE, "Reached the maximum of 10 redirects downloading " + this.resourcePackURL);
        return null;
    }

    private PackDownload downloadPackFileContent(URLConnection con, URL url) throws IOException {
        PackDownload download = new PackDownload();

        // Retrieve the original file name from the url using the content disposition header
        String fieldValue = con.getHeaderField("Content-Disposition");
        if (fieldValue != null) {
            int filenameIndex = fieldValue.indexOf("filename=\"");
            if (filenameIndex != -1) {
                filenameIndex += 10;
                int filenameEndIndex = fieldValue.indexOf('\"', filenameIndex);
                if (filenameEndIndex != -1) {
                    download.realName = fieldValue.substring(filenameIndex, filenameEndIndex);
                }
            }
        }

        // If no header was set, deduce the file name from the URL
        if (download.realName == null) {
            download.realName = new File(url.getPath()).getName();
        }

        // Generate a temp file name from the real name
        download.tempName = download.realName + ".tmp";

        // Calculate SHA-1 hash while we download
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException ex) {
            log.warning("Resource pack SHA-1 hashing is not available");
        }

        // Download the resource pack and save it to the temp file
        File tempFile = new File(this.resourcepacksFolder, download.tempName);
        try (
                InputStream connInput = con.getInputStream();
                FileOutputStream tmpOutput = new FileOutputStream(tempFile);
        ) {
            byte[] arr = new byte[4096];
            int len;
            while ((len = connInput.read(arr)) != -1) {
                // Write to file
                tmpOutput.write(arr, 0, len);

                // Update hash
                if (md != null) {
                    md.update(arr, 0, len);
                }

                // Refresh size
                download.size += len;
            }
        }

        download.hash = "";
        if (md != null) {
            Formatter formatter = new Formatter();
            for (byte b : md.digest()) {
                formatter.format("%02x", b);
            }
            download.hash = formatter.toString();
            formatter.close();
        }

        log.warning("Resource pack downloaded (" + download.realName + ", " + download.size + " bytes)");
        return download;
    }

    @Override
    public InputStream openFileStream(String path) throws IOException {
        return (this.archive == null) ? null : this.archive.openFileStream(path);
    }

    @Override
    public List<String> listFiles(String folder, boolean deep) throws IOException {
        return (this.archive == null) ? Collections.emptyList() : this.archive.listFiles(folder, deep);
    }

    private static class PackDownload {
        public String tempName = null;
        public String realName = null;
        public String hash = null;
        public long size = 0;
    }

    private static class PackIndex {
        public List<Entry> entries = Collections.emptyList();

        public static class Entry {
            public URL url = null;
            public String file = "";
            public String hash = "";

            public boolean checkHash(String sha1Hash) {
                if (this.hash == null || this.hash.isEmpty()) {
                    return true;
                }
                return this.hash.equalsIgnoreCase(sha1Hash);
            }
        }
    }

}
