package com.bergerkiller.bukkit.common.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.bukkit.plugin.java.JavaPluginLoader;

import com.bergerkiller.mountiplex.reflection.SafeField;
import com.bergerkiller.mountiplex.reflection.SafeMethod;
import com.google.common.io.ByteStreams;

/**
 * Helper class for performing plugin class modification
 */
public class CommonClassManipulation {
    private static SafeMethod<Package> cl_getPackage_method = new SafeMethod<Package>(ClassLoader.class, "getPackage", String.class);
    private static SafeMethod<Void> cl_definePackage_method1 = new SafeMethod<Void>(URLClassLoader.class, "definePackage",
            String.class, Manifest.class, URL.class);
    private static SafeMethod<Void> cl_definePackage_method2 = new SafeMethod<Void>(URLClassLoader.class, "definePackage",
            String.class, String.class, String.class, String.class, String.class, String.class, String.class, URL.class);
    private static SafeMethod<Class<?>> cl_defineClass_method1 = new SafeMethod<Class<?>>(URLClassLoader.class, "defineClass",
            String.class, byte[].class, int.class, int.class, CodeSource.class);
    private static SafeMethod<Class<?>> cl_defineClass_method2 = new SafeMethod<Class<?>>(ClassLoader.class, "defineClass",
            String.class, byte[].class, int.class, int.class);

    public static void writeClassData(ClassLoader classLoader, String name, byte[] classData) {
        Class<?> loadedClass;
        if (SafeField.contains(classLoader.getClass(), "jar", JarFile.class)) {
            // PluginClassLoader signature A (New versions of CraftBukkit)

            JarFile jar = SafeField.get(classLoader, "jar", JarFile.class);
            URL url = SafeField.get(classLoader, "url", URL.class);
            Manifest manifest = SafeField.get(classLoader, "manifest", Manifest.class);
            JarEntry entry = jar.getJarEntry(findClassPath(name));

            // Define package if needed
            int dot = name.lastIndexOf('.');
            if (dot != -1) {
                String pkgName = name.substring(0, dot);
                if (cl_getPackage_method.invoke(classLoader, pkgName) == null) {
                    try {
                        if (manifest != null) {
                            cl_definePackage_method1.invoke(classLoader, pkgName, manifest, url);
                        } else {
                            cl_definePackage_method2.invoke(classLoader, pkgName, null, null, null, null, null, null, null);
                        }
                    } catch (IllegalArgumentException ex) {
                        if (cl_getPackage_method.invoke(classLoader, pkgName) == null) {
                            throw new IllegalStateException("Cannot find package " + pkgName);
                        }
                    }
                }
            }

            CodeSigner[] signers = entry.getCodeSigners();
            CodeSource source = new CodeSource(url, signers);
            loadedClass = cl_defineClass_method1.invoke(classLoader, name, classData, 0, classData.length, source);
        } else {
            // PluginClassLoader signature B (Spigot)
            loadedClass = cl_defineClass_method2.invoke(classLoader, name, classData, 0, classData.length);
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
