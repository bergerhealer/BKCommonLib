package com.bergerkiller.bukkit.common.reflection.classes;

import java.io.RandomAccessFile;

import net.minecraft.server.RegionFile;

import com.bergerkiller.bukkit.common.reflection.SafeField;

public class RegionFileRef {
	public static final SafeField<RandomAccessFile> stream = new SafeField<RandomAccessFile>(RegionFile.class, "c");
}
