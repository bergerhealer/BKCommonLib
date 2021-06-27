package com.bergerkiller.bukkit.common.internal.cdn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * API around the published mappings for minecraft server.
 */
public class MojangMappings {
    public final List<ClassMappings> classes = new ArrayList<>();

    /**
     * The mappings of a single class
     */
    public static class ClassMappings {
        public final String name;
        public final String name_obfuscated;
        public final BiMap<String, String> obfuscated_to_name;
        public final BiMap<String, String> name_to_obfuscated;

        public ClassMappings(String name, String name_obfuscated) {
            this.name = name;
            this.name_obfuscated = name_obfuscated;
            this.obfuscated_to_name = HashBiMap.create();
            this.name_to_obfuscated = this.obfuscated_to_name.inverse();
        }

        public void addField(String obfuscatedName, String mojangName) {
            this.obfuscated_to_name.put(obfuscatedName, mojangName);
        }
    }

    /**
     * Attempts to load previously downloaded mappings from disk
     *
     * @param minecraftVersion
     * @return
     */
    public static MojangMappings fromCacheOrDownload(String minecraftVersion) {
        File cacheFolder = MojangIO.getCacheFolder();
        File mappingsFile = new File(cacheFolder, getMappingFile(minecraftVersion));
        if (mappingsFile.exists()) {
            try {
                return readMappings(mappingsFile);
            } catch (Throwable t) {
                Logging.LOGGER.log(Level.SEVERE, "Failed to parse cached server mappings, redownloading", t);
            }
        }
        return download(minecraftVersion);
    }

    /**
     * Attempts to download the mappings from mojangs servers for the current
     * minecraft version. The contents are saved in a location that can be read
     * using {@link #fromCache(String)} in the future.
     *
     * @return mojang mappings
     */
    public static MojangMappings download(String minecraftVersion) throws DownloadException {
        File cacheFolder = MojangIO.getCacheFolder();
        File tempFile = new File(cacheFolder, getMappingFile(minecraftVersion) + ".tmp");
        File mappingsFile = new File(cacheFolder, getMappingFile(minecraftVersion));

        // Just some legal garbage...
        Logging.LOGGER.warning("Since Minecraft 1.17 the server is obfuscated and requires the server mappings to interpret it correctly");
        Logging.LOGGER.warning("BKCommonLib will now download Minecraft " + minecraftVersion + " server mappings from Mojang's servers");
        Logging.LOGGER.warning("The file will be installed in: " + mappingsFile.toString());
        Logging.LOGGER.warning("By downloading these files you further agree with Mojang's EULA.");
        Logging.LOGGER.warning("The EULA can be read here: https://account.mojang.com/documents/minecraft_eula");

        // Try to download, if it fails, give instructions for manual installation
        try {
            return downloadMain(minecraftVersion, tempFile, mappingsFile);
        } catch (DownloadException ex) {
            Logging.LOGGER.severe("Failed to download the server mappings. You can try manually downloading the file instead.");
            Logging.LOGGER.severe("Install the server mappings in the following location:");
            Logging.LOGGER.severe("> " + mappingsFile.getAbsolutePath());
            throw ex;
        }
    }

    private static MojangMappings downloadMain(String minecraftVersion, File tempFile, File mappingsFile) throws DownloadException {
        // Download the version manifest, to find all available versions
        MojangIO.VersionManifest versionManifest;
        try {
            versionManifest = MojangIO.downloadJson(MojangIO.VersionManifest.class, MojangIO.VersionManifest.URL);
        } catch (IOException ex) {
            throw new DownloadException("Failed to download the game version manifest from Mojangs servers", ex);
        }

        // Find version suitable for this server
        MojangIO.VersionManifest.Version currentVersion = versionManifest.findVersion(minecraftVersion);
        if (currentVersion == null) {
            throw new DownloadException("This Minecraft version is not available");
        }

        // Find this version's asset information
        MojangIO.VersionManifest.VersionAssets versionAssets;
        try {
            versionAssets = MojangIO.downloadJson(MojangIO.VersionManifest.VersionAssets.class, currentVersion.url);
        } catch (IOException ex) {
            throw new DownloadException("Failed to download game version asset information", ex);
        }
        MojangIO.VersionManifest.VersionAssets.Download download = versionAssets.downloads.get("server_mappings");
        if (download == null) {
            throw new DownloadException("This Minecraft version has no downloadable server mappings");
        }

        // Download the file, first write it to a temporary file location
        try {
            MojangIO.downloadFile("Server Mappings", download, tempFile);
        } catch (IOException ex) {
            throw new DownloadException("Failed to download server mappings", ex);
        }

        // Success! Move the temporary file to the final destination
        mappingsFile.delete();
        if (!tempFile.renameTo(mappingsFile)) {
            throw new DownloadException("Failed to move " + tempFile + " to " + mappingsFile);
        }

        // Read 'em!
        try {
            return readMappings(mappingsFile);
        } catch (Throwable t) {
            throw new DownloadException("Failed to parse server mappings", t);
        }
    }

    /**
     * Parses the mappings file, making it accessible through a MojangMappings instance.
     *
     * @param mappingsFile The file to read
     * @return Mojang mappings instance
     * @throws IOException - If reading the file failed
     */
    private static MojangMappings readMappings(File mappingsFile) throws IOException {
        MojangMappings result = new MojangMappings();
        
        // Field mappings
        // Regex: \s+[\[\]<>\w\._\-$]+\s([\w_\-$]+)\s->\s([\w_\-$]+)
        Pattern fieldNamePattern = Pattern.compile("\\s+[\\[\\]<>\\w\\._\\-$]+\\s([\\w_\\-$]+)\\s->\\s([\\w_\\-$]+)");
        try (BufferedReader br = new BufferedReader(new FileReader(mappingsFile))) {
            ClassMappings classMappings = null;
            for (String line; (line = br.readLine()) != null; ) {
                // Skip comments
                if (line.startsWith("#")) {
                    continue;
                }

                // Start of new classes
                if (line.endsWith(":") && !line.startsWith(" ")) {
                    int nameEnd = line.indexOf(" -> ");
                    if (nameEnd <= 0 || nameEnd >= (line.length()-4)) {
                        classMappings = null;
                    } else {
                        classMappings = new ClassMappings(line.substring(0, nameEnd),
                                                          line.substring(nameEnd+4, line.length()-1));
                        result.classes.add(classMappings);
                    }
                }
                if (classMappings == null || !line.startsWith("    ")) {
                    continue; // Weird? Oh well.
                }

                // Parse members of class
                {
                    Matcher m = fieldNamePattern.matcher(line);
                    if (m.matches()) {
                        String obfuscatedName = m.group(2);
                        String mojangName = m.group(1);

                        // These types of obfuscation were not done in the server, and the
                        // original variable name is kept.
                        if (mojangName.startsWith("this$")) {
                            continue;
                        }

                        classMappings.addField(obfuscatedName, mojangName);
                        continue;
                    }
                }

                //TODO: Method names?
            }
        }

        return result;
    }

    /**
     * Exception thrown when the mojang mappings could not be downloaded
     */
    public static class DownloadException extends RuntimeException {
        private static final long serialVersionUID = 6481322561950832096L;

        public DownloadException(String reason) {
            super(reason);
        }

        public DownloadException(String reason, Throwable cause) {
            super(reason, cause);
        }
    }

    private static String getMappingFile(String minecraftVersion) {
        return minecraftVersion + "_server_mappings.txt";
    }
}
