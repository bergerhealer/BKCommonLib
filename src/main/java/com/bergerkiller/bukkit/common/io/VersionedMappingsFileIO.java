package com.bergerkiller.bukkit.common.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * Handles the encoding and decoding of mappings (key=value) with multiple
 * different versions, sorted in order from old to new. When writing out the
 * mappings file, it writes out a delta of the changes compared to the previous
 * 'layer' keeping the file small.<br>
 * <br>
 * Includes some mechanisms to transform the raw string-string map into a custom
 * format.
 */
public class VersionedMappingsFileIO<T> {
    /**
     * All the mappings by version, is sorted in ascending (old - new) order
     * based on the key comparator passed in the constructor.
     */
    public final NavigableMap<String, MappedVersion<T>> byVersion;

    private final Function<MappedVersion<T>, T> mappingLoader;

    /**
     * Creates a new VersionedMappingsFileIO
     *
     * @param versionComparator Comparator used for the versions. This should sort
     *                          from old to new.
     * @param mappingLoader Loader that parses the raw string-string mappings into something
     *                      more useful. Loaded data is cached.
     */
    public VersionedMappingsFileIO(
            final Comparator<String> versionComparator,
            final Function<MappedVersion<T>, T> mappingLoader
    ) {
        this.byVersion = new TreeMap<>(versionComparator);
        this.mappingLoader = mappingLoader;
    }

    /**
     * Gets a sorted set of all version stored, from old to new
     *
     * @return Versions set
     */
    public Set<String> getVersions() {
        return byVersion.keySet();
    }

    /**
     * Gets the decoded mappings data of the exact version specified
     *
     * @param version Version identifier
     * @return Mappings of this version, or empty() if none apply
     */
    public Optional<T> get(String version) {
        MappedVersion<T> mapped = byVersion.get(version);
        return mapped == null ? Optional.empty() : Optional.of(mapped.data());
    }

    /**
     * Gets the decoded mappings data of the version specified. If the exact
     * version is not stored, returns the mappings of the version that came before that.
     * (Older version mappings).<br>
     * <br>
     * For example, if this contains mapping for 1.20 and 1.21, querying
     * 1.20.5 will return the mappings of 1.20.
     *
     * @param version Version identifier
     * @return Mappings of this version or the newest version older than the one specified,
     *         or empty() if no versions apply (too old)
     */
    public Optional<T> getOrOlder(String version) {
        Iterator<MappedVersion<T>> iter = byVersion.headMap(version, true)
                .descendingMap()
                .values()
                .iterator();
        return iter.hasNext() ? Optional.of(iter.next().data()) : Optional.empty();
    }

    /**
     * Gets the decoded mappings data of the version specified. If the exact
     * version is not stored, returns the mappings of the version that came after that.
     * (Newer version mappings).<br>
     * <br>
     * For example, if this contains mapping for 1.20 and 1.21, querying
     * 1.20.5 will return the mappings of 1.21.
     *
     * @param version Version identifier
     * @return Mappings of this version or the oldest version newer than the one specified,
     *         or empty() if no versions apply (too new)
     */
    public Optional<T> getOrNewer(String version) {
        Iterator<MappedVersion<T>> iter = byVersion.tailMap(version, true)
                .values()
                .iterator();
        return iter.hasNext() ? Optional.of(iter.next().data()) : Optional.empty();
    }

    /**
     * Stores new mappings for a particular version. Overwrites previous mappings.
     *
     * @param version Version identifier
     * @param mappings Key-value mappings
     */
    public void store(String version, Map<String, String> mappings) {
        byVersion.put(version, new MappedVersion<>(version, mappings, mappingLoader));
    }

    /**
     * Reads all the mappings from an input stream in binary format.
     * Note: closes the input stream.
     *
     * @param inputStream Input stream
     * @throws IOException If a file read error occurs
     */
    public void read(InputStream inputStream) throws IOException {
        byVersion.clear();

        HashMap<String, String> mappings = new HashMap<>();
        try (DataInputStream stream = new DataInputStream(new InflaterInputStream(inputStream))) {
            int numVersions = stream.readInt();
            if (numVersions == 0) {
                return;
            }

            String version = stream.readUTF();
            while (numVersions-- > 0) {
                String nextVersion = version;
                for (String entry; !(entry = stream.readUTF()).isEmpty();) {
                    int sep = entry.indexOf('=');
                    if (sep == -1) {
                        // Version string
                        nextVersion = entry;
                        break;
                    } else if (sep == (entry.length()-1)) {
                        mappings.remove(entry.substring(0, sep));
                    } else {
                        mappings.put(entry.substring(0, sep), entry.substring(sep + 1));
                    }
                }

                this.store(version, new HashMap<>(mappings));
                version = nextVersion;
            }
        }
    }

    /**
     * Writes all the mappings to an output stream in binary format.
     * Note: closes the output stream.
     *
     * @param outputStream Output stream
     * @throws IOException If a file write error occurs
     */
    public void write(OutputStream outputStream) throws IOException {
        try (DataOutputStream stream = new DataOutputStream(new DeflaterOutputStream(outputStream))) {
            stream.writeInt(byVersion.size());
            Map<String, String> previous = Collections.emptyMap();
            for (Map.Entry<String, MappedVersion<T>> e : byVersion.entrySet()) {
                stream.writeUTF(e.getKey());
                Map<String, String> mappings = e.getValue().mappings;

                // For all entries in the previous map that are now missing, write a 'delete'
                for (Map.Entry<String, String> c : previous.entrySet()) {
                    if (!mappings.containsKey(c.getKey())) {
                        stream.writeUTF(c.getKey() + "=");
                    }
                }
                for (Map.Entry<String, String> c : mappings.entrySet()) {
                    if (!previous.getOrDefault(c.getKey(), "").equals(c.getValue())) {
                        stream.writeUTF(c.getKey() + "=" + c.getValue());
                    }
                }

                previous = mappings; // Delta tracking
            }

            // Closes it out
            stream.writeUTF("");
        }
    }

    /**
     * A single set of mappings stored in the versioned mappings file.
     *
     * @param <T> Decoded data type
     */
    public static class MappedVersion<T> {
        /** Version identifier of these mappings */
        public final String version;
        /** Key-Value entries active for this version */
        public final Map<String, String> mappings;

        private final Function<MappedVersion<T>, T> loader;
        private T cachedValue;

        private MappedVersion(String version, Map<String, String> mappings, Function<MappedVersion<T>, T> loader) {
            this.version = version;
            this.mappings = mappings;
            this.loader = loader;
        }

        /**
         * Decodes the {@link #mappings} using the loader registered for this mapping file.
         * Caches the result so decoding only has to occur once.
         *
         * @return Decoded data
         */
        public T data() {
            T value;
            if ((value = cachedValue) == null) {
                cachedValue = value = loader.apply(this);
            }
            return value;
        }
    }
}
