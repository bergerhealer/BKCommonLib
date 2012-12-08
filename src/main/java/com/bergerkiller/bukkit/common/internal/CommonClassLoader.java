package com.bergerkiller.bukkit.common.internal;

import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.plugin.java.PluginClassLoader;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.reflection.SafeField;

/**
 * Redirects package paths to the correct version to fix Class Not Found exceptions
 */
public class CommonClassLoader extends URLClassLoader {
    private final JavaPluginLoader loader;
    private final Map<String, Class<?>> classes = new HashMap<String, Class<?>>();

    public CommonClassLoader(PluginClassLoader base) {
        super(base.getURLs(), base.getParent());
        this.loader = SafeField.get(base, "loader");
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return findClass(name, true);
    }

	@Override
	protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		if (name.startsWith(Common.NMS)) {
			name = Common.NMS + Common.getMCVersion() + name.substring(Common.NMS.length() - 1);
		} else if (name.startsWith(Common.CB)) {
			name = Common.CB + Common.getMCVersion() + name.substring(Common.CB.length() - 1);
		}
		System.out.println("LOADING: " + name);

		Class<?> c = super.loadClass(name, resolve);
		if (c != null) {
			System.out.println(c.getName());
		}
		
		return c;
	}
//
//	@Override
//	protected Class<?> findClass(String name) throws ClassNotFoundException {
//		// =============== Package name fixing here ==================
//		if (name.startsWith(Common.NMS)) {
//			name = Common.NMS + Common.getMCVersion() + name.substring(Common.NMS.length() - 1);
//		} else if (name.startsWith(Common.CB)) {
//			name = Common.CB + Common.getMCVersion() + name.substring(Common.CB.length() - 1);
//		} else {
//			return null; // This class loader does not deal with this
//		}
//		System.out.println("LOADING: " + name);
//		try {
//			return getParent().loadClass(name);
//		} catch (Throwable t) {
//			t.printStackTrace();
//		}
//		//throw new ClassNotFoundException(name);
//		System.out.println("WUT");
//		return null;
//
////		// ===========================================================
////		try {
////			return this.getParent().loadClass(name);
//////			return ClassLoaderRef.loadClass.invoke(this.base, name, resolve);
//////			//return this.base.loadClass(name);
////		} catch (Throwable t) {
////			System.out.println("NOT SUCCESSFUL!");
////			t.printStackTrace();
////			throw new NoClassDefFoundError(name);
////		}
//	}
}
