package com.bergerkiller.bukkit.common.map.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Used to parse the JSON when downloading the right Minecraft Client jar
 */
public class VersionManifest {
    public List<Version> versions = Collections.emptyList();

    public static class Version {
        public String id;
        public String url;
    }

    public static class VersionAssets {
        public Map<String, Download> downloads = Collections.emptyMap();

        public static class Download {
            public String sha;
            public long size;
            public String url;
        }
    }
}
