package com.bergerkiller.bukkit.common.server;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.bukkit.Bukkit;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeField;

public class MCPCPlusServer extends SpigotServer {
	private Object classRemapper;
	private MethodAccessor<String> mapType;
	private MethodAccessor<String> mapField;
	private Map<String, String> classesMap;
	private Map<String, String> methodsMap;

	@Override
	public boolean init() {
		if (!super.init() || !Bukkit.getServer().getVersion().contains("MCPC")) {
			return false;
		}
		// Obtain the Class remapper used by MCPC+
		this.classRemapper = SafeField.get(getClass().getClassLoader(), "remapper");
		if (this.classRemapper == null) {
			throw new RuntimeException("Running an MCPC+ server but the remapper is unavailable...please turn it on!");
		}
		// Initialize some fields and methods used by the Jar Remapper
		ClassTemplate<?> template = ClassTemplate.create(this.classRemapper);
		this.mapType = template.getMethod("map", String.class);
		this.mapField = template.getMethod("mapFieldName", String.class, String.class, String.class);
		Object jarMapping = SafeField.get(classRemapper, "jarMapping");
		this.classesMap = SafeField.get(jarMapping, "classes");
		this.methodsMap = SafeField.get(jarMapping, "methods");
		return true;
	}

	@Override
	public String getServerName() {
		return "MCPC+";
	}

	@Override
	public String getClassName(String path) {
		return mapType.invoke(classRemapper, super.getClassName(path).replace('.', '/')).replace('/', '.');
	}

	private String getOriginalOwner(Class<?> type) {
		String typeName = type.getName().replace('.', '/');
		// Find the original type of the current version (PACKAGE_VERSION)
		String result = typeName;
		for (Map.Entry<String, String> entry : classesMap.entrySet()) {
			if (entry.getValue().equals(typeName)) {
				result = entry.getKey();
				// If the perfect one is found, just use it
				if (result.contains(PACKAGE_VERSION)) {
					return result;
				}
			}
		}
		// Failure or perhaps we found something similar...we can only hope for success
		return result;
	}

	@Override
	public String getFieldName(Class<?> type, String fieldName) {
		return mapField.invoke(classRemapper, getOriginalOwner(type), fieldName, "");
	}

	@Override
	public String getMethodName(Class<?> type, String methodName, Class<?>... params) {
		final String methodPath = getOriginalOwner(type) + "/" + methodName + " ";
		for (Map.Entry<String, String> entry : methodsMap.entrySet()) {
			// Try to find the (obfuscated) method, if it exists with the parameters, we found our method
			// We can not use the JarRemapper to do this, because the methods map includes a return type
			// We do not know the return type here, which makes that impossible to use
			if (entry.getKey().startsWith(methodPath)) {
				try {
					type.getDeclaredMethod(entry.getValue(), params);
					// Found our method, return the official method name
					return entry.getValue();
				} catch (Throwable t) {
					// Method not found, go on...
				}
			}
		}
		Class<?> superClass = type.getSuperclass();
		// Try to find the method in the super class
		if (superClass != null) {
			return getMethodName(superClass, methodName, params);
		}
		// Failure to replace anything, perhaps it is correct?
		return methodName;
	}

	@Override
	public File getWorldFolder(String worldName) {
		File container = Bukkit.getWorldContainer();
		if (container.getName().equalsIgnoreCase(worldName)) {
			return container;
		} else {
			return new File(container, worldName);
		}
	}

	@Override
	public Collection<String> getLoadableWorlds() {
		File[] files = Bukkit.getWorldContainer().listFiles();
		File container = Bukkit.getWorldContainer();
		Collection<String> rval = new ArrayList<String>(files.length);
		// Add the main world
		rval.add(container.getName());
		// Add all sub-worlds found in there
		for (String worldName : container.list()) {
			if (isLoadableWorld(worldName)) {
				rval.add(worldName);
			}
		}
		return rval;
	}

	@Override
	public File getWorldRegionFolder(String worldName) {
		File container = Bukkit.getWorldContainer();
		File region;
		if (container.getName().equalsIgnoreCase(worldName)) {
			region = new File(container, "region");
		} else {
			region = new File(container, worldName + File.separator + "region");
		}
		return region.exists() ? region : null;
	}
}
