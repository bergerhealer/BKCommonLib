package com.bergerkiller.bukkit.common.internal;

import java.io.IOException;
import java.io.InputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.RemappingClassAdapter;

import com.bergerkiller.bukkit.common.Common;

/**
 * Re-maps class references from non-versioned packages to the correct versioned package
 */
class NoVerRemapper extends Remapper {
	public static final String ASM_ROOT = "com.bergerkiller.bukkit.common.libs.org.objectweb.asm.";

	@Override
	public String mapDesc(String desc) {
		return filter(desc);
	}

	@Override
	public String map(String typeName) {
		return filter(typeName);
	}

	private static String filter(String text) {
		int idx;
		idx = text.indexOf(Common.NMS_PATH);
		if (idx != -1) {
			return convert(text, Common.NMS_PATH, idx);
		}
		idx = text.indexOf(Common.CB_PATH);
		if (idx != -1) {
			return convert(text, Common.CB_PATH, idx);
		}
		return text;
	}

	private static String convert(String text, String packagePath, int startIndex) {
		String name = text.substring(startIndex + packagePath.length());
		String header = text.substring(0, startIndex);
		if (name.startsWith("v")) {
			int firstidx = name.indexOf('_');
			if (firstidx != -1) {
				// Check if the major version is a valid number
				String major = name.substring(0, firstidx);
				try {
					Integer.parseInt(major);
					// Major test success
					int end = name.indexOf('/');
					if (end != -1) {
						// Get rid of the version (removes 'v1_4_5.')
						name = name.substring(end + 1);
					}
				} catch (NumberFormatException ex) {
					// Major test fail
				}
			}
		}
		if (Common.MC_VERSION.isEmpty()) {
			return header + packagePath + name;
		} else {
			return header + packagePath + Common.MC_VERSION + '/' + name;
		}
	}

	public static byte[] remap(InputStream stream) throws IOException {
		ClassReader classReader = new ClassReader(stream);
        ClassWriter classWriter = new ClassWriter(classReader, 0);
        Remapper remapper = new NoVerRemapper();
        classReader.accept(new RemappingClassAdapter(classWriter, remapper), ClassReader.EXPAND_FRAMES);
        return classWriter.toByteArray();
	}
}
