package com.bergerkiller.bukkit.common.server;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.resolver.FieldNameResolver;
import com.bergerkiller.mountiplex.reflection.resolver.MethodNameResolver;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;

/**
 * Mohist is a PaperSpigot + Forge implementation
 */
public class MohistServer extends SpigotServer implements FieldNameResolver, MethodNameResolver {
    private RemapUtilsClass remapUtils = null;
    private List<Class<?>> customEntityBaseClasses = new ArrayList<Class<?>>();

    @Override
    public boolean init() {
        if (!super.init()) {
            return false;
        }

        // Check this is actually a Mohist server, we expect this Class to exist
        final String mohistNamespace;
        final Class<? extends RemapUtilsClass> mohistRemapUtilsType;
        {
            String namespace;
            Class<? extends RemapUtilsClass> remapUtilsType;
            try {
                Class.forName("com.mohistmc.MohistMC");
                namespace = "com.mohistmc";
                remapUtilsType = RemapUtilsClassImpl.class;
            } catch (Throwable t) {
                try {
                    Class.forName("red.mohist.Mohist");
                    namespace = "red.mohist";
                    remapUtilsType = RemapUtilsClassImplLegacy.class;
                } catch (Throwable t2) {
                    return false;
                }
            }

            mohistNamespace = namespace;
            mohistRemapUtilsType = remapUtilsType;
        }

        // Make sure RemapUtils exists, this initializes the RemapUtilsHandle using the above initialized declaration
        remapUtils = Template.Class.create(mohistRemapUtilsType);
        if (!remapUtils.isAvailable()) {
            return false;
        }

        // Force initialization to avoid late catastrophic failing
        remapUtils.forceInitialization();

        // Custom entity class used for forge entities
        Stream.of("CraftCustomEntity", "CraftCustomChestHorse", "CraftCustomAbstractHorse").forEach(n -> {
            try {
                Class<?> type = Class.forName(mohistNamespace + ".entity." + n);
                customEntityBaseClasses.add(type);
            } catch (Throwable t) {}
        });

        return true;
    }

    @Override
    public String getServerName() {
        return "Mohist";
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
    public String resolveClassPath(final String path) {
        // Replaces path with proper net.minecraft.server.v1_1_1 path
        String remappedPath = super.resolveClassPath(path);

        // Perform remapping. If found, return
        // For NMS paths null is returned if loading the path would cause an NPE
        remappedPath = remapUtils.mapClassName(remappedPath);
        if (remappedPath != null) {
            return remappedPath;
        }

        // Missing class in net.minecraft.server
        // If the original path refers to an existing Class, allow it
        // Otherwise, return an on-purpose missing path, or it will suffer a NPE in the PluginClassLoader
        try {
            MPLType.getClassByName(path);
            return path;
        } catch (NullPointerException ex) {
            return "missing.type." + path; // Mohist bug
        } catch (ClassNotFoundException e) {
            return "missing.type." + path;
        }
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

        return true;
    }

    @Override
    public String resolveMethodName(Class<?> type, String methodName, Class<?>[] params) {
        return remapUtils.mapMethodName(type, methodName, params);
    }

    @Override
    public String resolveFieldName(Class<?> type, String fieldName) {
        return remapUtils.mapFieldName(type, fieldName);
    }

    @Override
    public boolean isCustomEntityType(org.bukkit.entity.EntityType entityType) {
        Class<?> entityClass = entityType.getEntityClass();
        if (entityClass != null) {
            for (Class<?> customType : customEntityBaseClasses) {
                if (customType.isAssignableFrom(entityClass)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void addVariables(Map<String, String> variables) {
        super.addVariables(variables);
        variables.put("forge", "mohist");
        if (this.evaluateMCVersion("<=", "1.12.2")) {
            variables.put("forge_nms_obfuscated", "true");
        }
    }

    @Template.Optional
    @Template.InstanceType("com.mohistmc.bukkit.nms.utils.RemapUtils")
    public static abstract class RemapUtilsClassImpl extends RemapUtilsClass {
        /*
         * <MAP_CLASS_NAME_IMPL>
         * public static String mapClassName(String className) {
         *     if (className.startsWith("net.minecraft.server.")) {
         *         com.mohistmc.bukkit.nms.model.ClassMapping mapping;
         *         mapping = (com.mohistmc.bukkit.nms.model.ClassMapping) RemapUtils.jarMapping.byNMSName.get(className);
         *         if (mapping != null) {
         *             return mapping.getMcpName();
         *         } else {
         *             // Mohist BUGFIX!!!
         *             // If we do not do this, it will suffer a NPE in the PluginClassLoader
         *             return null;
         *         }
         *     }
         *     return className;
         * }
         */
        @Template.Generated("%MAP_CLASS_NAME_IMPL%")
        public abstract String mapClassName(String className);

        @Template.Generated("public static transient String mapMethodName(Class<?> type, String name, Class<?>[] parameterTypes)")
        public abstract String mapMethodName(Class<?> type, String name, Class<?>[] parameterTypes);

        @Template.Generated("public static String mapFieldName(Class<?> type, String fieldName)")
        public abstract String mapFieldName(Class<?> type, String fieldName);
    }

    @Template.Optional
    @Template.InstanceType("red.mohist.bukkit.nms.utils.RemapUtils")
    public static abstract class RemapUtilsClassImplLegacy extends RemapUtilsClass {
        /*
         * <MAP_CLASS_NAME_LEGACY>
         * public static String mapClassName(String className) {
         *     if (className.startsWith("net.minecraft.server.")) {
         *         red.mohist.bukkit.nms.model.ClassMapping mapping;
         *         mapping = (red.mohist.bukkit.nms.model.ClassMapping) RemapUtils.jarMapping.byNMSName.get(className);
         *         if (mapping != null) {
         *             return mapping.getMcpName();
         *         } else {
         *             // Mohist BUGFIX!!!
         *             // If we do not do this, it will suffer a NPE in the PluginClassLoader
         *             return null;
         *         }
         *     }
         *     return className;
         * }
         */
        @Template.Generated("%MAP_CLASS_NAME_LEGACY%")
        public abstract String mapClassName(String className);

        @Template.Generated("public static transient String mapMethodName(Class<?> type, String name, Class<?>[] parameterTypes)")
        public abstract String mapMethodName(Class<?> type, String name, Class<?>[] parameterTypes);

        @Template.Generated("public static String mapFieldName(Class<?> type, String fieldName)")
        public abstract String mapFieldName(Class<?> type, String fieldName);
    }

    public static abstract class RemapUtilsClass extends Template.Class<Template.Handle> {
        public abstract String mapClassName(String className);
        public abstract String mapMethodName(Class<?> type, String name, Class<?>[] parameterTypes);
        public abstract String mapFieldName(Class<?> type, String fieldName);
    }
}
