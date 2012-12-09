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
	/*
	 * Internal variables used for the remapping of classes
	 * If you use this in your own plugin - don't forget to provide alternatives for SafeField/Method
	 * You can use the reflection.Field and Method classes in a try-catch
	 */
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

	/**
	 * Package path to asm - required to ignore remapping of the remapper, as that causes
	 * class loading circularity errors
	 */
	public static final String ASM_ROOT = "com.bergerkiller.bukkit.common.libs.org.objectweb.asm.";

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
		// If the class was already loaded, get it from the map
		Class<?> result = classes.get(name);
		if (result == null) {
			// Ask other class loaders for this Class
			// Disallow this class loader to return an (unmodified) value
			failNativeGet = true;
			result = loader.getClassByName(name);
			failNativeGet = false;
			if (result == NO_CLASS_FOUND_CONST) {
				result = null;
			}

			if (result == null) {
				// Try to let this class loader create the Class
				if (!name.startsWith(ASM_ROOT)) {
					try {
						// Load the resource to the name
						String path = name.replace('.', '/').concat(".class");
						InputStream stream = getResourceAsStreamMethod.invoke(this.topChild, path);
						if (stream != null) {
							// Remap the classes
							byte[] data = NoVerRemapper.remap(stream);
							// Define (create) the class using the modified byte code
							// The top-child class loader is used for this to prevent access violations
							result = defineClassMethod.invoke(this.topChild, name, data, 0, data.length);
							if (result != null) {
								// Resolve it - sets the class loader of the class
								resolveClassMethod.invoke(this.topChild, result);
							}
						}
					} catch (Throwable t) {
						t.printStackTrace();
					}
				}

				if (result == null) {
					// This should only occur for the remapper classes
					// It may result in access violations if used for other classes
					result = super.findClass(name);
				}

				if (result != null) {
					// Map this class to the JavaPluginLoader so other plugins can access it
					loader.setClass(name, result);
				}
			}
			// Map this class to this class loader to allow quicker access next time
			classes.put(name, result);
		}
		return result;
	}
}
