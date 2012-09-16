package com.bergerkiller.bukkit.common.reflection;

import java.io.File;
import java.lang.ref.Reference;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.server.RegionFile;

import com.bergerkiller.bukkit.common.SafeField;

public class RegionFileCacheRef {
	public static final SafeField<Map<File, Reference<RegionFile>>> filesField = new SafeField<Map<File, Reference<RegionFile>>>(RegionFileCacheRef.class, "a");
	public static final Map<File, Reference<RegionFile>> FILES;
	static {
		FILES = filesField.isValid() ? filesField.get(null) : new HashMap<File, Reference<RegionFile>>();
	}
}
