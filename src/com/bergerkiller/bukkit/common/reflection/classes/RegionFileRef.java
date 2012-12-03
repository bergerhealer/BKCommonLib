package com.bergerkiller.bukkit.common.reflection.classes;

import java.io.File;
import java.io.RandomAccessFile;

import net.minecraft.server.RegionFile;

import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeField;

public class RegionFileRef {
	public static final FieldAccessor<File> file = new SafeField<File>(RegionFile.class, "b");
	public static final FieldAccessor<RandomAccessFile> stream = new SafeField<RandomAccessFile>(RegionFile.class, "c");
}
