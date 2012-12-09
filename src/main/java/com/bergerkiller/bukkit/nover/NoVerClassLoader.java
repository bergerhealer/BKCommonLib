package com.bergerkiller.bukkit.nover;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.plugin.java.PluginClassLoader;

import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeField;
import com.bergerkiller.bukkit.common.reflection.SafeMethod;

/**
 * Redirects package paths to the correct version to fix Class Not Found exceptions<br><br>
 * 
 * <b>Feel free to use NoVerClassLoader and NoVerRemapper in your own plugins</b>
 */
public class NoVerClassLoader extends PluginClassLoader {
	private final JavaPluginLoader loader;
	private final PluginClassLoader topChild;
	private final Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
	private static final FieldAccessor<JavaPluginLoader> loaderField = new SafeField<JavaPluginLoader>(PluginClassLoader.class, "loader");
	private static final Class<?> NO_CLASS_FOUND_CONST = NoVerClassLoader.class;
	private static boolean failNativeGet = false;
	private static final SafeMethod<Void> resolveClassMethod = new SafeMethod<Void>(PluginClassLoader.class, "resolveClass", Class.class);
	private static final SafeMethod<Class<?>> defineClassMethod = new SafeMethod<Class<?>>(ClassLoader.class, "defineClass", String.class, byte[].class, int.class, int.class);
	private static final SafeMethod<InputStream> getResourceAsStreamMethod = new SafeMethod<InputStream>(PluginClassLoader.class, "getResourceAsStream", String.class);
	/**
	 * Defines the minecraft version this class loader will redirect package-versioned classes to
	 */
	public static String MC_VERSION = "v1_4_5"; // Can be changed externally, is merely here to separate these classes

	static {
		// Pre-load the remapper - otherwise we get class loading circularities
		NoVerRemapper.class.getName();
	}

	/**
	 * Applies the NoVerClassLoader to the plugin class loader chain to get around package version
	 * 
	 * @param pluginClass - main plugin class of your plugin
	 */
	public static void undoPackageVersioning(Class<?> pluginClass) {
		ClassLoader loader = pluginClass.getClassLoader();
		if (loader instanceof NoVerClassLoader) {
			return;
		}
		if (loader instanceof PluginClassLoader) {
			SafeField.set(loader, "parent", new NoVerClassLoader((PluginClassLoader) loader));
		} else {
			throw new RuntimeException("The plugin class specified was not loaded by the Bukkit plugin loader (is it a plugin?)");
		}
	}

	private NoVerClassLoader(PluginClassLoader base) {
		super(loaderField.get(base), base.getURLs(), base.getParent());
		this.topChild = base;
		this.loader = loaderField.get(base);

		// Disable the classes field of the top-level loader to prevent unwanted storage
		SafeField.set(base, "classes", new HashMap<String, Class<?>>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Class<?> put(String key, Class<?> value) {
				return null;
			}

			@Override
			public Class<?> get(Object key) {
				if (failNativeGet) {
					return NO_CLASS_FOUND_CONST;
				} else {
					return null;
				}
			}
		});
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		if (name.startsWith("org.bukkit.") || name.startsWith("net.minecraft.")) {
			throw new ClassNotFoundException(name);
		}
		Class<?> result = classes.get(name);
		if (result == null) {
			failNativeGet = true;
			result = loader.getClassByName(name);
			failNativeGet = false;
			if (result == NO_CLASS_FOUND_CONST) {
				result = null;
			}

			if (result == null) {
				if (!name.startsWith(NoVerRemapper.ASM_ROOT)) {
					try {
						String path = name.replace('.', '/').concat(".class");
						InputStream stream = getResourceAsStreamMethod.invoke(this.topChild, path);
						if (stream != null) {
							byte[] data = NoVerRemapper.remap(stream);
							result = defineClassMethod.invoke(this.topChild, name, data, 0, data.length);
							if (result != null) {
								resolveClassMethod.invoke(this.topChild, result);
							}
						}
					} catch (Throwable t) {
						t.printStackTrace();
					}
				}

				if (result == null) {
					result = super.findClass(name);
				}

				if (result != null) {
					loader.setClass(name, result);
				}
			}

			classes.put(name, result);
		}
		return result;
	}
}
