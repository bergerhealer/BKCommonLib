package com.bergerkiller.bukkit.common.reflection;

import java.io.RandomAccessFile;

import net.minecraft.server.RegionFile;

import com.bergerkiller.bukkit.common.SafeField;

public class RegionFileRef {
	public static final SafeField<RandomAccessFile> stream = new SafeField<RandomAccessFile>(RegionFile.class, "c");
}
