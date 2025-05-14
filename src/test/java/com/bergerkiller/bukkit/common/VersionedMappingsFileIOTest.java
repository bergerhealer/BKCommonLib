package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.io.VersionedMappingsFileIO;
import com.bergerkiller.mountiplex.logic.TextValueSequence;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests {@link VersionedMappingsFileIO}
 */
public class VersionedMappingsFileIOTest {

    @Test
    public void testGetOrNewer() {
        VersionedMappingsFileIO<Map<String, String>> mappingsFile = new VersionedMappingsFileIO<>(
                TextValueSequence.STRING_COMPARATOR,
                m -> m.mappings
        );

        mappingsFile.store("1.2", Collections.singletonMap("a", "2"));
        mappingsFile.store("1.3", Collections.singletonMap("a", "3"));
        mappingsFile.store("1.5", Collections.singletonMap("a", "5"));
        mappingsFile.store("0.6", Collections.singletonMap("b", "20")); // Should add to front

        assertEquals(Arrays.asList("0.6", "1.2", "1.3", "1.5"),
                new ArrayList<>(mappingsFile.getVersions()));

        // Match exactly
        assertEquals(Collections.singletonMap("b", "20"),
                mappingsFile.getOrNewer("0.6").orElse(null));
        assertEquals(Collections.singletonMap("a", "2"),
                mappingsFile.getOrNewer("1.2").orElse(null));
        assertEquals(Collections.singletonMap("a", "3"),
                mappingsFile.getOrNewer("1.3").orElse(null));
        assertEquals(Collections.singletonMap("a", "5"),
                mappingsFile.getOrNewer("1.5").orElse(null));

        // Should match the newer version that we do have (1.4 -> 1.5)
        assertEquals(Collections.singletonMap("a", "5"),
                mappingsFile.getOrNewer("1.4").orElse(null));

        // Version is older than we got stored, should return the oldest (latest) version
        assertEquals(Collections.singletonMap("b", "20"),
                mappingsFile.getOrNewer("0.1").orElse(null));

        // Version is newer than we got stored, should return empty
        assertNull(mappingsFile.getOrNewer("1.9").orElse(null));
    }

    @Test
    public void testGetOrOlder() {
        VersionedMappingsFileIO<Map<String, String>> mappingsFile = new VersionedMappingsFileIO<>(
                TextValueSequence.STRING_COMPARATOR,
                m -> m.mappings
        );

        mappingsFile.store("1.2", Collections.singletonMap("a", "2"));
        mappingsFile.store("1.3", Collections.singletonMap("a", "3"));
        mappingsFile.store("1.5", Collections.singletonMap("a", "5"));
        mappingsFile.store("0.6", Collections.singletonMap("b", "20")); // Should add to front

        assertEquals(Arrays.asList("0.6", "1.2", "1.3", "1.5"),
                new ArrayList<>(mappingsFile.getVersions()));

        // Match exactly
        assertEquals(Collections.singletonMap("b", "20"),
                mappingsFile.getOrOlder("0.6").orElse(null));
        assertEquals(Collections.singletonMap("a", "2"),
                mappingsFile.getOrOlder("1.2").orElse(null));
        assertEquals(Collections.singletonMap("a", "3"),
                mappingsFile.getOrOlder("1.3").orElse(null));
        assertEquals(Collections.singletonMap("a", "5"),
                mappingsFile.getOrOlder("1.5").orElse(null));

        // Should match the older version that we do have (1.4 -> 1.3)
        assertEquals(Collections.singletonMap("a", "3"),
                mappingsFile.getOrOlder("1.4").orElse(null));

        // Version is older than we got stored, should return empty
        assertNull(mappingsFile.getOrOlder("0.1").orElse(null));

        // Version is newer than we got stored, should return the newest (latest) version
        assertEquals(Collections.singletonMap("a", "5"),
                mappingsFile.getOrOlder("1.9").orElse(null));
    }
}
