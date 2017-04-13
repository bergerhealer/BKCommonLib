package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.reflection.ClassTemplate;
import com.bergerkiller.reflection.FieldAccessor;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class NMSRegionFileCache {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("RegionFileCache")
            .addImport("java.io.File");

    public static final FieldAccessor<Map<File, Object>> filesField = T.selectField("public static final Map<File, RegionFile> a");
    public static final Map<File, Object> FILES;

    static {
        FILES = filesField.isValid() ? filesField.get(null) : new HashMap<File, Object>();
    }

    /**
     * Gets a region file from file without creating a new instance
     *
     * @param file to get the RegionFile of
     * @return the Region File, or null if not loaded
     */
    public static Object getFile(File file) {
        return FILES.get(file);
    }
}
