package com.bergerkiller.bukkit.common.internal.cdn;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import com.bergerkiller.bukkit.common.internal.cdn.MojangIO.VersionManifest;
import com.bergerkiller.mountiplex.MountiplexUtil;

/**
 * Communicates with hub.spigotmc.org to generate spigot <> mojang class
 * name mappings. These can also be efficiently saved/read from a file resource.
 */
public class SpigotMappings {
    /**
     * All the mappings by minecraft version, is sorted in ascending (old - new) order
     */
    public final SortedMap<String, Map<String, String>> byVersion = new TreeMap<>((a, b) -> {
        if (a.equals(b)) {
            return 0;
        } else if (MountiplexUtil.evaluateText(a, ">", b)) {
            return 1;
        } else {
            return -1;
        }
    });

    /**
     * Reads all the mappings from an input stream in binary format.
     * Note: closes the input stream.
     *
     * @param inputStream Input stream
     * @throws IOException
     */
    public void read(InputStream inputStream) throws IOException {
        try (DataInputStream stream = new DataInputStream(new InflaterInputStream(inputStream))) {
            int numVersions = stream.readInt();
            if (numVersions == 0) {
                return;
            }

            String version = stream.readUTF();
            Map<String, String> mapping = new HashMap<>();
            while (numVersions-- > 0) {
                this.byVersion.put(version, mapping);
                for (String entry; !(entry = stream.readUTF()).isEmpty();) {
                    int sep = entry.indexOf('=');
                    if (sep == -1) {
                        // Version string
                        version = entry;
                        break;
                    } else if (sep == (entry.length()-1)) {
                        mapping.remove(entry.substring(0, sep));
                    } else {
                        mapping.put(entry.substring(0, sep), entry.substring(sep + 1));
                    }
                }
                mapping = new HashMap<String, String>(mapping);
            }
        }
    }

    /**
     * Writes all the mappings to an output stream in binary format.
     * Note: closes the output stream.
     *
     * @param outputStream Output stream
     * @throws IOException
     */
    public void write(OutputStream outputStream) throws IOException {
        try (DataOutputStream stream = new DataOutputStream(new DeflaterOutputStream(outputStream))) {
            stream.writeInt(byVersion.size());
            Map<String, String> previous = Collections.emptyMap();
            for (Map.Entry<String, Map<String, String>> e : byVersion.entrySet()) {
                stream.writeUTF(e.getKey());
                Map<String, String> mapping = e.getValue();

                // For all entries in the previous map that are now missing, write a 'delete'
                for (Map.Entry<String, String> c : previous.entrySet()) {
                    if (!mapping.containsKey(c.getKey())) {
                        stream.writeUTF(c.getKey() + "=");
                    }
                }
                for (Map.Entry<String, String> c : e.getValue().entrySet()) {
                    if (!previous.getOrDefault(c.getKey(), "").equals(c.getValue())) {
                        stream.writeUTF(c.getKey() + "=" + c.getValue());
                    }
                }

                previous = mapping; // Delta tracking
            }

            // Closes it out
            stream.writeUTF("");
        }
    }

    /**
     * Downloads all the mappings for versions specified, if they aren't
     * already included inside these mappings
     *
     * @param version
     * @return True if these mappings changed as a result
     */
    public boolean assertMappings(String... versions) throws IOException {
        boolean changed = false;
        for (String version : versions) {
            if (!byVersion.containsKey(version)) {
                downloadMappings(null, version);
                changed = true;
            }
        }
        return changed;
    }

    /**
     * Communicates with hub.spigotmc.org to download the class file mappings
     * for a given minecraft version, and adds the interpreted data to this instance.
     * Generally only used in development to produce the final mappings to ship
     * with the jar file.
     *
     * @param mojangMappings Mojang's mappings, used if required. Pass null to initialize a new one.
     * @param version Minecraft version to download
     */
    public void downloadMappings(MojangMappings mojangMappings, String version) throws IOException {
        // For this, we must also have the Mojang mappings, to de-obfuscate the names
        if (mojangMappings == null) {
            mojangMappings = MojangMappings.fromCacheOrDownload(version);
        }
        Map<String, String> mojang_class_mapping = mojangMappings.classes.stream().collect(
                Collectors.toMap(v -> v.name_obfuscated, v -> v.name));

        // First query https://hub.spigotmc.org/versions/[version].json for full metadata
        SpigotVersionMeta versionMeta = MojangIO.downloadJson(SpigotVersionMeta.class,
                "https://hub.spigotmc.org/versions/" + version + ".json");
        if (versionMeta.refs == null || versionMeta.refs.BuildData == null) {
            throw new IOException("No BuildData ref in response to /versions");
        }

        // Retrieve the class mappings file from the builddata git repo
        File mappingsFile = File.createTempFile("bukkit-" + version + "-cl", ".csrg");
        VersionManifest.VersionAssets.Download download = new VersionManifest.VersionAssets.Download();
        download.sha1 = null;
        download.size = 0;
        download.url = "https://hub.spigotmc.org/stash/projects/SPIGOT/repos/builddata/raw/mappings/bukkit-"
                + version + "-cl.csrg?at=" + versionMeta.refs.BuildData;
        MojangIO.downloadFile("Minecraft - Bukkit " + version + " class mappings", download, mappingsFile);

        // Regex: ([\w\._\-/$]+)\s+([\w\._\-/$]+)
        Pattern classNamePattern = Pattern.compile("([\\w\\._\\-/$]+)\\s+([\\w\\._\\-/$]+)");

        // Parse the bukkit - obfuscated mappings file
        Map<String, String> obfuscatedToBukkit = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(mappingsFile))) {
            for (String line; (line = br.readLine()) != null; ) {
                Matcher m = classNamePattern.matcher(line);
                if (m.matches()) {
                    String obfuscatedName = m.group(1).replace('/', '.');
                    String bukkitFullName = m.group(2).replace('/', '.');
                    obfuscatedToBukkit.put(obfuscatedName, bukkitFullName);
                }
            }
        }

        // Go by all of Mojang's classes and if the class name was remapped on Bukkit,
        // store the Bukkit name for those classes. Sometimes a subclass of a class isn't
        // remapped, in which case we must perform remapping of this name ourselves.
        Map<String, String> mappings = new HashMap<>();
        for (MojangMappings.ClassMappings cl : mojangMappings.classes) {
            String bukkitFullName = obfuscatedToBukkit.get(cl.name_obfuscated);
            if (bukkitFullName == null) {
                // See if this is a subclass of a class we do have mappings for, recursively
                int subClassIndex = cl.name_obfuscated.length();
                while ((subClassIndex = cl.name_obfuscated.lastIndexOf('$', subClassIndex - 1)) != -1) {
                    bukkitFullName = obfuscatedToBukkit.get(cl.name_obfuscated.substring(0, subClassIndex));
                    if (bukkitFullName != null) {
                        bukkitFullName += cl.name_obfuscated.substring(subClassIndex);
                        break;
                    }
                }
            }
            if (bukkitFullName != null) {
                mappings.put(bukkitFullName, cl.name);
            }
        }

        // Store the result
        this.byVersion.put(version, mappings);

        // No longer need it.
        mappingsFile.delete();
    }

    private static class SpigotVersionMeta {
        public Refs refs;

        private static class Refs {
            public String BuildData;
        }
    }
}
