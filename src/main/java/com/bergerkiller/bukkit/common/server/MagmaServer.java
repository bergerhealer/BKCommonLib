package com.bergerkiller.bukkit.common.server;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.resolver.FieldNameResolver;
import com.bergerkiller.mountiplex.reflection.resolver.MethodNameResolver;

/**
 * Magma is a Spigot + Forge implementation
 */
public class MagmaServer extends SpigotServer implements FieldNameResolver, MethodNameResolver {
    private RemappingUtilsClass remappingUtils = null;

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

        return true;
    }

    @Override
    public String getServerName() {
        return "Magma";
    }

    @Override
    public Collection<String> getLoadableWorlds() {
        return ForgeSupport.getLoadableWorlds();
    }

    @Override
    public boolean isLoadableWorld(String worldName) {
        return ForgeSupport.isLoadableWorld(worldName);
    }

    @Override
    public File getWorldRegionFolder(String worldName) {
        return ForgeSupport.getWorldRegionFolder(worldName);
    }

    @Override
    public File getWorldFolder(String worldName) {
        return ForgeSupport.getWorldFolder(worldName);
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

        return true;
    }

    @Override
    public String resolveMethodName(Class<?> type, String methodName, Class<?>[] params) {
        return remappingUtils.mapMethodName(type, methodName, params);
    }

    @Override
    public String resolveFieldName(Class<?> type, String fieldName) {
        return remappingUtils.mapFieldName(type, fieldName);
    }

    @Override
    public void addVariables(Map<String, String> variables) {
        super.addVariables(variables);
        variables.put("forge", "magma");
    }

    @Template.Optional
    @Template.InstanceType("org.magmafoundation.magma.remapper.utils.RemappingUtils")
    public static abstract class RemappingUtilsClass extends Template.Class<Template.Handle> {
        @Template.Generated("public static String mapClassName(String className) {\n" +
                            "    if (className.startsWith(\"net.minecraft.server.\")) {\n" +
                            "        org.magmafoundation.magma.remapper.mappingsModel.ClassMappings mapping;\n" +
                            "        mapping = (org.magmafoundation.magma.remapper.mappingsModel.ClassMappings) RemappingUtils.jarMapping.byNMSName.get(className);\n" +
                            "        if (mapping != null) {\n" +
                            "            return mapping.getMcpName();\n" +
                            "        } else {\n" +
                            "            // Magma BUGFIX!!!\n" +
                            "            // If we do not do this, it will suffer a NPE in the PluginClassLoader\n" +
                            "            return \"missing.type.\" + className;\n" +
                            "        }\n" +
                            "    }\n" +
                            "    return className;\n" +
                            "}")
        public abstract String mapClassName(String className);

        @Template.Generated("public static transient String mapMethodName(Class<?> type, String name, Class<?>[] parameterTypes)")
        public abstract String mapMethodName(Class<?> type, String name, Class<?>[] parameterTypes);

        @Template.Generated("public static String mapFieldName(Class<?> type, String fieldName)")
        public abstract String mapFieldName(Class<?> type, String fieldName);
    }

}
