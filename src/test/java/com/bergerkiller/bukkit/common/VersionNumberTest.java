package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import org.junit.Test;

import com.bergerkiller.bukkit.common.config.yaml.YamlNode;
import com.bergerkiller.bukkit.common.internal.CommonMethods;

/**
 * Checks Common.VERSION is correct.
 */
public class VersionNumberTest {

    @Test
    public void testCheckVersionNumber() {
        // Read plugin version
        YamlNode plugin_yaml = new YamlNode();
        plugin_yaml.loadFromStream(VersionNumberTest.class.getResourceAsStream("/plugin.yml"));
        String version_str = plugin_yaml.get("version", String.class);
        assertNotNull("Version missing in plugin.yml", version_str);
        int expected = CommonMethods.parseVersionNumber(version_str);
        assertEquals("Common.VERSION must be updated to contain '" + expected + "'", expected, Common.VERSION);
    }
}
