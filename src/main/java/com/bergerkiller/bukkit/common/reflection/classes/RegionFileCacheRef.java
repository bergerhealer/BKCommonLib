package com.bergerkiller.bukkit.common.reflection.classes;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.server.RegionFile;
import net.minecraft.server.RegionFileCache;

import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeField;

public class RegionFileCacheRef {
	public static final FieldAccessor<Map<File, RegionFile>> filesField = new SafeField<Map<File, RegionFile>>(RegionFileCache.class, "a");
	public static final Map<File, RegionFile> FILES;
	static {
		FILES = filesField.isValid() ? filesField.get(null) : new HashMap<File, RegionFile>();
	}

	/**
	 * Gets a region file from file without creating a new instance
	 * 
	 * @param file to get the RegionFile of
	 * @return the Region File, or null if not loaded
	 */
	public static RegionFile getFile(File file) {
		return FILES.get(file);
	}
}
