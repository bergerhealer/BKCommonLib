package com.bergerkiller.bukkit.common.reflection.classes;

import java.io.File;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import com.bergerkiller.bukkit.common.utils.CommonUtil;

public class RegionFileRef {
	public static final ClassTemplate<Object> TEMPLATE = new NMSClassTemplate("RegionFile");
	public static final FieldAccessor<File> file = TEMPLATE.getField("b");
	public static final FieldAccessor<RandomAccessFile> stream = TEMPLATE.getField("c");
	public static final MethodAccessor<Void> close = TEMPLATE.getMethod("c");
	public static final MethodAccessor<Boolean> exists = TEMPLATE.getMethod("c", int.class, int.class);
	
	public static Object create() {
		try {
			return CommonUtil.getNMSClass("RegionFile").getConstructor().newInstance();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
