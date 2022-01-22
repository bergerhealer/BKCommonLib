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

/**
 * CatServer is a Spigot + Forge implementation
 */
public class CatServerServer extends SpigotServer implements FieldNameResolver, MethodNameResolver
{
    private RemapUtils remapUtils = null;
    private List<Class<?>> customEntityBaseClasses = new ArrayList<Class<?>>();

    @Override
    public boolean init() {
        if (!super.init()) {
            return false;
        }

        // Check this is actually a CatServer server, we expect this Class to exist
        try {
            Class.forName("catserver.server.CatServer");
        } catch (Throwable t) {
            return false;
        }

        // Make sure RemapUtils exists, this initializes the RemapUtilsHandle using the above initialized declaration
        remapUtils = Template.Class.create(RemapUtils.class);
        if (!remapUtils.isAvailable()) {
            return false;
        }

        // Force initialization to avoid late catastrophic failing
        remapUtils.forceInitialization();

        // Custom entity base classes on CatServer
        Stream.of("CraftCustomEntity", "CraftCustomChestHorse", "CraftCustomHorse").forEach(n -> {
            try {
                Class<?> type = Class.forName("catserver.server.entity." + n);
                customEntityBaseClasses.add(type);
            } catch (Throwable t) {}
        });

        return true;
    }

    @Override
    public String getServerName() {
        return "CatServer";
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

        // Ask CatServer what the actual class name is on Forge
        path = remapUtils.mapClassName(path);

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

        return true;
    }

    @Override
    public String resolveFieldName(Class<?> declaringClass, String fieldName) {
        return remapUtils.mapFieldName(declaringClass, fieldName);
    }

    @Override
    public String resolveMethodName(Class<?> declaringClass, String methodName, Class<?>[] parameterTypes) {
        return remapUtils.mapMethod(declaringClass, methodName, parameterTypes);
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
        variables.put("forge", "catserver");
        variables.put("forge_nms_obfuscated", "true");
    }

    @Template.Optional
    @Template.InstanceType("catserver.server.remapper.RemapUtils")
    public static abstract class RemapUtils  extends Template.Class<Template.Handle> {
        /*
         * <MAP_CLASS_NAME>
         * public static String mapClassName(String className) {
         *     if (className.startsWith("net.minecraft.server.")) {
         *         String internalClassName = className.replace('.', '/');
         *         String mapping_name = (String) ReflectionTransformer.jarMapping.classes.get(internalClassName);
         *         if (mapping_name != null) {
         *             return mapping_name.replace('/', '.');
         *         } else {
         *             // CatServer BUGFIX!!!
         *             // If we do not do this, it will suffer a NPE in the PluginClassLoader
         *             return "missing.type." + className;
         *         }
         *     }
         *     return className;
         * }
         */
        @Template.Generated("%MAP_CLASS_NAME%")
        public abstract String mapClassName(String className);

        @Template.Generated("public static String mapMethod(Class<?> inst, String name, Class<?>[] parameterTypes)")
        public abstract String mapMethod(Class<?> type, String name, Class<?>[] parameterTypes);

        @Template.Generated("public static String mapFieldName(Class<?> inst, String name)")
        public abstract String mapFieldName(Class<?> type, String fieldName);
    }
}
