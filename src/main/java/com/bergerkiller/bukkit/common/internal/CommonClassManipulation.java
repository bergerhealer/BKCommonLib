package com.bergerkiller.bukkit.common.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.jar.JarFile;

import org.bukkit.plugin.java.JavaPluginLoader;

import com.bergerkiller.mountiplex.reflection.SafeField;
import com.bergerkiller.mountiplex.reflection.util.GeneratorClassLoader;
import com.google.common.io.ByteStreams;

/**
 * Helper class for performing plugin class modification
 */
public class CommonClassManipulation {

    @SuppressWarnings("deprecation")
    public static void writeClassData(ClassLoader classLoader, String name, byte[] classData) {
        // Use the generator class loader to generate new classes
        GeneratorClassLoader loader = GeneratorClassLoader.get(classLoader);

        Class<?> loadedClass;
        if (SafeField.contains(classLoader.getClass(), "jar", JarFile.class)) {
            // PluginClassLoader signature A (New versions of CraftBukkit)

            // All these fields are inside PluginClassLoader, not ClassLoader
            //JarFile jar = SafeField.get(classLoader, "jar", JarFile.class);
            //URL url = SafeField.get(classLoader, "url", URL.class);
            //Manifest manifest = SafeField.get(classLoader, "manifest", Manifest.class);
            //JarEntry entry = jar.getJarEntry(findClassPath(name));

            // Define package if needed
            int dot = name.lastIndexOf('.');
            if (dot != -1) {
                String pkgName = name.substring(0, dot);
                if (loader.getPackage(pkgName) == null) {
                    try {
                        loader.definePackage(pkgName, null, null, null, null, null, null, null);
                    } catch (IllegalArgumentException ex) {}
                }
            }

            //CodeSigner[] signers = entry.getCodeSigners();
            //CodeSource source = new CodeSource(url, signers);
            loadedClass = loader.createClassFromBytecode(name, classData, null, false);
        } else {
            // PluginClassLoader signature B (Spigot)
            loadedClass = loader.createClassFromBytecode(name, classData, null, false);
        }

        writeClass(classLoader, name, loadedClass);
    }

    @SuppressWarnings("unchecked")
    public static void writeClass(ClassLoader classLoader, String name, Class<?> clazz) {
        Map<String, Class<?>> classes = SafeField.get(classLoader, "classes", Map.class);
        JavaPluginLoader loader = SafeField.get(classLoader, "loader", JavaPluginLoader.class);
        Map<String, Class<?>> loaded_classes = SafeField.get(loader, "classes", Map.class);
        classes.put(name, clazz);
        loaded_classes.put(name, clazz);
    }

    public static byte[] readClassData(ClassLoader classLoader, String name) {
        try (InputStream is = classLoader.getResourceAsStream(findClassPath(name))) {
            return ByteStreams.toByteArray(is);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static String findClassPath(String name) {
        return name.replace('.', '/').concat(".class");
    }
}
