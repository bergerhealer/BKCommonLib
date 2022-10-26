package com.bergerkiller.bukkit.common.server;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.resolver.FieldNameResolver;
import com.bergerkiller.mountiplex.reflection.resolver.MethodNameResolver;

/**
 * ArcLight spigot + forge server.
 * Used for 1.15, and 1.16 build #155 and earlier.<br>
 * <br>
 * https://github.com/IzzelAliz/Arclight
 */
public class ArclightServerLegacy extends SpigotServer implements FieldNameResolver, MethodNameResolver
{
    private ArclightRemapper arclightRemapper = null;
    private Object classLoaderRemapper = null;

    @Override
    public boolean init() {
        if (!super.init()) {
            return false;
        }

        // Check this is actually a Arclight server, we expect this Class to exist
        try {
            Class.forName("io.izzel.arclight.common.ArclightMain");
        } catch (Throwable t) {
            return false;
        }

        // If this class exists, this isn't legacy
        try {
            Class.forName("io.izzel.arclight.common.mod.util.remapper.resource.RemapSourceHandler");
            return false;
        } catch (Throwable t) {}

        // Make sure RemapUtils exists, this initializes the RemapUtilsHandle using the above initialized declaration
        arclightRemapper = Template.Class.create(ArclightRemapper.class);
        if (!arclightRemapper.isAvailable()) {
            return false;
        }

        // Force initialization to avoid late catastrophic failing
        arclightRemapper.forceInitialization();

        // Retrieve remapper from the class loader loading BKCommonLib
        // Class: io.izzel.arclight.common.mod.util.remapper.ClassLoaderRemapper
        try {
            ClassLoader loader = this.getClass().getClassLoader();
            Field field = loader.getClass().getDeclaredField("remapper");
            field.setAccessible(true);
            classLoaderRemapper = field.get(loader);
            field.setAccessible(false);
        } catch (Throwable t) {
            Logging.LOGGER_REFLECTION.log(Level.WARNING, "Failed to initialize Arclight remapper", t);
            return false;
        }

        return true;
    }

    @Override
    public String getServerName() {
        return "Arclight (Legacy)";
    }

    @Override
    public boolean isForgeServer() {
        return true;
    }

    @Override
    public Collection<String> getLoadableWorlds() {
        return ForgeSupport.bukkit().getLoadableWorlds();
    }

    @Override
    public boolean isLoadableWorld(String worldName) {
        return ForgeSupport.bukkit().isLoadableWorld(worldName);
    }

    @Override
    public File getWorldRegionFolder(String worldName) {
        return ForgeSupport.bukkit().getWorldRegionFolder(worldName);
    }

    @Override
    public File getWorldFolder(String worldName) {
        return ForgeSupport.bukkit().getWorldFolder(worldName);
    }

    @Override
    public File getWorldLevelFile(String worldName) {
        return ForgeSupport.bukkit().getWorldLevelFile(worldName);
    }

    @Override
    public String resolveClassPath(String path) {
        // Replaces path with proper net.minecraft.server.v1_1_1 path
        path = super.resolveClassPath(path);

        // Ask Arclight what the actual class name is on Forge
        path = arclightRemapper.mapClassName(classLoaderRemapper, path);

        return path;
    }

    @Override
    public boolean canLoadClassPath(String classPath) {
        // The .class data at this path contains obfuscated type information
        // These obfuscated names are deobufscated at runtime
        // This difference causes compiler errors at runtime, so instead of
        // loading the .class files, inspect the signatures using reflection.
        if (classPath.startsWith("org.bukkit.craftbukkit.")) {
            return false;
        }

        // NMS World class 'entitiesById' has different field modifiers in bytecode than loaded class
        if (classPath.startsWith("net.minecraft.server.")) {
            return false;
        }

        return false;
    }

    /*
    @Override
    public String resolveCompiledFieldName(Class<?> declaringClass, String fieldName) {
        return arclightRemapper.mapFieldName(declaringClass, fieldName);
    }

    @Override
    public String resolveCompiledMethodName(Class<?> declaringClass, String methodName, Class<?>[] parameterTypes) {
        return arclightRemapper.mapMethodName(declaringClass, methodName, parameterTypes);
    }
    */

    @Override
    public String resolveFieldName(Class<?> declaringClass, String fieldName) {
        return arclightRemapper.mapFieldName(classLoaderRemapper, declaringClass, fieldName);
    }

    @Override
    public String resolveMethodName(Class<?> declaringClass, String methodName, Class<?>[] parameterTypes) {
        return arclightRemapper.mapMethodName(classLoaderRemapper, declaringClass, methodName, parameterTypes);
    }

    @Override
    public void addVariables(Map<String, String> variables) {
        super.addVariables(variables);
        variables.put("forge", "arclight");
    }

    @Template.Optional
    @Template.InstanceType("io.izzel.arclight.common.mod.util.remapper.ClassLoaderRemapper")
    public static abstract class ArclightRemapper extends Template.Class<Template.Handle> {
        /*
         * <CREATE_REMAPPER>
         * public static Object createRemapper(ClassLoader classLoader) {
         *     return ArclightRemapper.createClassLoaderRemapper(classLoader);
         * }
         */
        @Template.Generated("%CREATE_REMAPPER%")
        public abstract Object createRemapper(ClassLoader classLoader);

        /*
         * <MAP_CLASS_NAME>
         * public static String mapClassName(Object classLoaderRemapper, String className) {
         *     ClassLoaderRemapper remapper = (ClassLoaderRemapper) classLoaderRemapper;
         *     String internalName = className.replace('.', '/');
         *     String mappedName = remapper.map(internalName);
         *     if (!mappedName.equals(internalName)) {
         *         return mappedName.replace('/', '.');
         *     }
         *     return className;
         * }
         */
        @Template.Generated("%MAP_CLASS_NAME%")
        public abstract String mapClassName(Object classLoaderRemapper, String className);

        /*
         * <MAP_METHOD_NAME>
         * public static String mapMethodName(Object classLoaderRemapper, Class<?> type, String name, Class<?>[] parameterTypes) {
         *     ClassLoaderRemapper remapper = (ClassLoaderRemapper) classLoaderRemapper;
         *     java.lang.reflect.Method method = remapper.tryMapMethodToSrg(type, name, parameterTypes);
         *     if (method != null) {
         *         return com.bergerkiller.mountiplex.reflection.util.asm.MPLType.getName(method);
         *     }
         * 
         *     return name;
         * }
         */
        @Template.Generated("%MAP_METHOD_NAME%")
        public abstract String mapMethodName(Object classLoaderRemapper, Class<?> type, String name, Class<?>[] parameterTypes);

        /*
         * <MAP_FIELD_NAME>
         * public static String mapFieldName(Object classLoaderRemapper, Class<?> type, String fieldName) {
         *     ClassLoaderRemapper remapper = (ClassLoaderRemapper) classLoaderRemapper;
         *     try {
         *         return remapper.tryMapFieldToSrg(type, fieldName);
         *     } catch (Throwable t) {
         *         com.bergerkiller.bukkit.common.Logging.LOGGER_REFLECTION.log(java.util.logging.Level.WARNING, "Failed to remap field " + fieldName, t);
         *         return fieldName;
         *     }
         * }
         */
        @Template.Generated("%MAP_FIELD_NAME%")
        public abstract String mapFieldName(Object classLoaderRemapper, Class<?> type, String fieldName);
    }
}
