package com.bergerkiller.bukkit.common.reflection.classes;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeField;
import com.bergerkiller.bukkit.common.utils.CommonUtil;

public class RegionFileCacheRef {
	public static final FieldAccessor<Map<File, Object>> filesField = new SafeField<Map<File, Object>>(CommonUtil.getNMSClass("RegionFileCache"), "a");
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
