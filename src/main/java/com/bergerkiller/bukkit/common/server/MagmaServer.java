package com.bergerkiller.bukkit.common.server;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.resolver.FieldNameResolver;
import com.bergerkiller.mountiplex.reflection.resolver.MethodNameResolver;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;

/**
 * Magma is a Spigot + Forge implementation
 */
public class MagmaServer extends SpigotServer implements FieldNameResolver, MethodNameResolver {
    private RemappingUtilsClass remappingUtils = null;
    private Class<?> customEntityBaseClass = null;

    @Override
    public boolean init() {
        if (!super.init()) {
            return false;
        }

        // Check this is actually a Magma server, we expect this Class to exist
        try {
            Class.forName("org.magmafoundation.magma.Magma");
        } catch (Throwable t) {
            return false;
        }

        // Make sure RemappingUtils exists, this initializes the RemappingUtilsHandle using the above initialized declaration
        remappingUtils = Template.Class.create(RemappingUtilsClass.class);
        if (!remappingUtils.isAvailable()) {
            return false;
        }

        // Force initialization to avoid late catastrophic failing
        remappingUtils.forceInitialization();

        // Custom entity class is used for forge entities and such
        try {
            customEntityBaseClass = Class.forName("org.magmafoundation.magma.entity.CraftCustomEntity");
        } catch (Throwable t) {}

        return true;
    }

    @Override
    public String getServerName() {
        return "Magma";
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

        // Ask Magma what the actual class name is on Forge
        path = remappingUtils.mapClassName(path);

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

        // Forge re-writes these classes at runtime, so we can't rely on loading
        // the .class files from there.
        if (classPath.startsWith("net.minecraft.")) {
            return false;
        }

        return true;
    }

    @Override
    public String resolveMethodName(Class<?> type, String methodName, Class<?>[] params) {
        return remappingUtils.mapMethodName(type, methodName, params);
    }

    @Override
    public String resolveFieldName(Class<?> type, String fieldName) {
        String result = remappingUtils.mapFieldName(type, fieldName);

        // Also check superclasses for server types, Object class excluded
        if (result == fieldName && MPLType.getName(type).startsWith("net.minecraft.")) {
            while ((type = type.getSuperclass()) != null && type != Object.class) {
                result = remappingUtils.mapFieldName(type, fieldName);
                if (result != fieldName) {
                    break;
                }
            }
        }

        return result;
    }

    @Override
    public boolean isCustomEntityType(org.bukkit.entity.EntityType entityType) {
        Class<?> entityClass = entityType.getEntityClass();
        return customEntityBaseClass != null && entityClass != null && customEntityBaseClass.isAssignableFrom(entityClass);
    }

    @Override
    public void addVariables(Map<String, String> variables) {
        super.addVariables(variables);
        variables.put("forge", "magma");
        variables.put("forge_nms_obfuscated", "true");
    }

    @Template.Optional
    @Template.InstanceType("org.magmafoundation.magma.remapper.utils.RemappingUtils")
    public static abstract class RemappingUtilsClass extends Template.Class<Template.Handle> {
        /*
         * <MAP_CLASS_NAME>
         * public static String mapClassName(String className) {
         *     if (className.startsWith("net.minecraft.server.")) {
         *         org.magmafoundation.magma.remapper.mappingsModel.ClassMappings mapping;
         *         mapping = (org.magmafoundation.magma.remapper.mappingsModel.ClassMappings) RemappingUtils.jarMapping.byNMSName.get(className);
         *         if (mapping != null) {
         *             return mapping.getMcpName();
         *         } else {
         *             // Magma BUGFIX!!!
         *             // If we do not do this, it will suffer a NPE in the PluginClassLoader
         *             return "missing.type." + className;
         *         }
         *     }
         *     return className;
         * }
         */
        @Template.Generated("%MAP_CLASS_NAME%")
        public abstract String mapClassName(String className);

        @Template.Generated("public static transient String mapMethodName(Class<?> type, String name, Class<?>[] parameterTypes)")
        public abstract String mapMethodName(Class<?> type, String name, Class<?>[] parameterTypes);

        @Template.Generated("public static String mapFieldName(Class<?> type, String fieldName)")
        public abstract String mapFieldName(Class<?> type, String fieldName);
    }

}
