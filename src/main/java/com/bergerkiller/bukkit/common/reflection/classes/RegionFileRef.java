package com.bergerkiller.bukkit.common.reflection.classes;

import java.io.File;
import java.io.RandomAccessFile;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import com.bergerkiller.bukkit.common.reflection.SafeConstructor;

public class RegionFileRef {
	public static final ClassTemplate<Object> TEMPLATE = new NMSClassTemplate("RegionFile");
	public static final FieldAccessor<File> file = TEMPLATE.getField("b");
	public static final FieldAccessor<RandomAccessFile> stream = TEMPLATE.getField("c");
	public static final MethodAccessor<Void> close = TEMPLATE.getMethod("c");
	public static final MethodAccessor<Boolean> exists = TEMPLATE.getMethod("c", int.class, int.class);
	private static final SafeConstructor<Object> constructor1 = TEMPLATE.getConstructor(File.class);

	public static Object create(File file) {
		return constructor1.newInstance(file);
	}
}
