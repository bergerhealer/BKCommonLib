package com.bergerkiller.bukkit.common.internal.cdn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.cdn.MojangIO.VersionManifest;
import com.bergerkiller.bukkit.common.io.VersionedMappingsFileIO;
import com.bergerkiller.bukkit.common.server.CraftBukkitServer;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.mountiplex.logic.TextValueSequence;

/**
 * Communicates with hub.spigotmc.org to generate spigot &lt;&gt; mojang class
 * name mappings. These can also be efficiently saved/read from a file resource.
 */
public class SpigotMappings extends VersionedMappingsFileIO<SpigotMappings.ClassMappings> {

    /**
     * Creates new empty SpigotMappings
     */
    public SpigotMappings() {
        super(TextValueSequence.STRING_COMPARATOR, ClassMappings::new);
    }

    /**
     * Downloads all the mappings for versions specified, if they aren't
     * already included inside these mappings
     *
     * @param versions Versions that should be available
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
        HashMap<String, String> newMappings = new HashMap<>();
        for (MojangMappings.ClassMappings cl : mojangMappings.classes()) {
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
                newMappings.put(cl.name, bukkitFullName);
            }
        }

        // Store the result
        this.store(version, newMappings);

        // No longer need it.
        mappingsFile.delete();
    }

    /**
     * Loads the spigot&lt;&gt;mojang class name mappings for a certain version of Minecraft, by reading
     * from the jar-included cache file.
     *
     * @param minecraftVersion Minecraft game version
     * @return Class mappings
     */
    public static SpigotMappings.ClassMappings forVersion(String minecraftVersion) {
        // Retrieve Spigot-Mojang class name mappings
        // We need this to properly remap the mojang field and method names later
        SpigotMappings spigotMappings = new SpigotMappings();
        String classMappingsFile = "/com/bergerkiller/bukkit/common/internal/resources/class_mappings.dat";
        try {
            try (InputStream in = CraftBukkitServer.class.getResourceAsStream(classMappingsFile)) {
                spigotMappings.read(in);
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to read class mappings (corrupted BKCommonLib jar?)");
        }

        return spigotMappings.getOrOlder(minecraftVersion)
                .orElseThrow(() -> new IllegalStateException("Version " + minecraftVersion + " has no mappings"));
    }

    /**
     * Visualizes all information related to a particular key word. You can specify just the base name of
     * the class and it'll log all information about it, whether its part of a mojang or spigot class
     * name it does not matter. Use this for debugging and development purposes.
     *
     * @param searchQuery Text contents to match
     */
    public void visualizeMapping(String searchQuery) {
        Set<String> mojangMatches = getAll().stream()
                .flatMap(m -> m.getMojangToSpigot().keySet().stream())
                .filter(e -> e.contains(searchQuery))
                .collect(Collectors.toSet());
        Set<String> spigotMatches = getAll().stream()
                .flatMap(m -> m.getSpigotToMojang().keySet().stream())
                .filter(e -> e.contains(searchQuery))
                .collect(Collectors.toSet());
        System.out.println("All mapping details for search query: " + searchQuery);
        for (String mojangClassName : mojangMatches) {
            visualizeMappingsForMojangClass(mojangClassName);
        }
        for (String spigotClassName : spigotMatches) {
            visualizeMappingsForSpigotClass(spigotClassName);
        }
    }

    /**
     * Visualizes all of the mappings for a particular Mojang class name, showing which Spigot class name it points to
     * across all versions of Minecraft. This is useful for debugging and development.
     *
     * @param mojangClassName Mojang-mapped class name (net.minecraft.server.level.ServerPlayer)
     */
    public void visualizeMappingsForMojangClass(String mojangClassName) {
        boolean first = true;
        String prev = null;
        String startVersion = null;
        String endVersion = null;
        for (String version : getVersions()) {
            String spigotName = get(version)
                    .orElseThrow(() -> new UnsupportedOperationException("Version bug: " + version))
                    .getMojangToSpigot()
                    .get(mojangClassName);
            if (startVersion == null) {
                startVersion = version;
                endVersion = version;
            }
            if (!first && !LogicUtil.bothNullOrEqual(prev, spigotName)) {
                System.out.println("[" + startVersion + " - " + endVersion + "] " + mojangClassName + " -> " + prev);
                startVersion = version;
                endVersion = version;
            }
            endVersion = version;
            prev = spigotName;
            first = false;
        }
        if (startVersion != null) {
            System.out.println("[" + startVersion + " - " + endVersion + "] " + mojangClassName + " -> " + prev);
        }
    }

    /**
     * Visualizes all of the mappings for a particular Spigot class name, showing which Mojang class names point to it
     * across all versions of Minecraft. This is useful for debugging and development.
     *
     * @param spigotClassName Spigot-mapped class name (net.minecraft.server.level.EntityPlayer)
     */
    public void visualizeMappingsForSpigotClass(String spigotClassName) {
        boolean first = true;
        Set<String> prev = null;
        String startVersion = null;
        String endVersion = null;
        for (String version : getVersions()) {
            Set<String> mojangNames = get(version)
                    .orElseThrow(() -> new UnsupportedOperationException("Version bug: " + version))
                    .getSpigotToMojang()
                    .get(spigotClassName);
            if (startVersion == null) {
                startVersion = version;
                endVersion = version;
            }
            if (!first && !LogicUtil.bothNullOrEqual(prev, mojangNames)) {
                if (prev != null) {
                    System.out.println("[" + startVersion + " - " + endVersion + "]:");
                    for (String mojangName : prev) {
                        System.out.println("  - " + mojangName + " -> " + spigotClassName);
                    }
                } else {
                    System.out.println("[" + startVersion + " - " + endVersion + "]: None");
                }
                startVersion = version;
                endVersion = version;
            }
            endVersion = version;
            prev = mojangNames;
            first = false;
        }
        if (startVersion != null) {
            if (prev != null) {
                System.out.println("[" + startVersion + " - " + endVersion + "]:");
                for (String mojangName : prev) {
                    System.out.println("  - " + mojangName + " -> " + spigotClassName);
                }
            } else {
                System.out.println("[" + startVersion + " - " + endVersion + "]: None");
            }
        }
    }

    private static class SpigotVersionMeta {
        public Refs refs;

        private static class Refs {
            public String BuildData;
        }
    }

    public static class ClassMappings {
        private final Map<String, String> mojangToSpigot;
        private Map<String, Set<String>> cachedSpigotToMojang;

        private ClassMappings(VersionedMappingsFileIO.MappedVersion<ClassMappings> mappedVersion) {
            this(mappedVersion.mappings);
        }

        public ClassMappings(Map<String, String> mojangToSpigot) {
            this.mojangToSpigot = mojangToSpigot;
            this.cachedSpigotToMojang = null; // Lazy-gen
        }

        public Map<String, String> getMojangToSpigot() {
            return mojangToSpigot;
        }

        public Map<String, Set<String>> getSpigotToMojang() {
            if (cachedSpigotToMojang == null) {
                cachedSpigotToMojang = new HashMap<>(mojangToSpigot.size());
                for (Map.Entry<String, String> entry : mojangToSpigot.entrySet()) {
                    cachedSpigotToMojang.compute(entry.getValue(), (k, oldValues) -> {
                        if (oldValues == null) {
                            return Collections.singleton(entry.getKey());
                        } else {
                            Set<String> newValues = (oldValues.size() == 1) ? new HashSet<>(oldValues) : oldValues;
                            newValues.add(entry.getKey());
                            return newValues;
                        }
                    });
                }
            }
            return cachedSpigotToMojang;
        }

        public void put(String mojangClassName, String spigotClassName) {
            this.mojangToSpigot.put(mojangClassName, spigotClassName);
            this.cachedSpigotToMojang = null; // Invalidate cache
        }

        /**
         * Stores a new mapping from a sub-class of a mojang class name, remapping to the same
         * sub-class name but with spigot mappings instead.
         *
         * @param mojangClassName Mojang class name
         * @param subClassName Sub-class name for under the spigot/mojang class name
         */
        public void remapMojangSubClass(String mojangClassName, String subClassName) {
            put(mojangClassName + "$" + subClassName, toSpigot(mojangClassName) + "$" + subClassName);
        }

        /**
         * Gets the single Spigot class name that a particular Mojang class name points to.
         * Returns the Mojang class name itself if no mapping is found.
         *
         * @param mojangClassName Mojang class name
         * @return Spigot mapped class name, or the Mojang class name itself if no mapping is found.
         */
        public String toSpigot(String mojangClassName) {
            return mojangToSpigot.getOrDefault(mojangClassName, mojangClassName);
        }

        /**
         * Gets the MojMap known class names that point to a particular Spigot class. Can be more than one
         * if multiple Mojang classes are remapped to the same Spigot class (older versions can do that).
         *
         * @param spigotClassName Spigot class name
         * @return Set of Mojang class names that point to the Spigot class name. If no mappings, returns a set with the Spigot class name itself.
         */
        public Set<String> toMojang(String spigotClassName) {
            return getSpigotToMojang().getOrDefault(spigotClassName, Collections.singleton(spigotClassName));
        }
    }
}
