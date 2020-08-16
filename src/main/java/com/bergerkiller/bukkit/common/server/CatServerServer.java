package com.bergerkiller.bukkit.common.server;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.resolver.FieldNameResolver;
import com.bergerkiller.mountiplex.reflection.resolver.MethodNameResolver;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;

/**
 * CatServer is a Spigot + Forge implementation
 */
public class CatServerServer extends SpigotServer implements FieldNameResolver, MethodNameResolver {
    private RemapUtils remapUtils = null;

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

        System.out.println("CatServer Initialized!");

        return true;
    }

    @Override
    public String getServerName() {
        return "CatServer";
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
    public String resolveMethodName(Class<?> type, String methodName, Class<?>[] params) {
        //TODO: The actual bytecode uses MCP names, but reflection gives the CraftBukkit names
        //      This currently causes a NoSuchMethodError at runtime when generated code is executed
        //return remapUtils.mapMethod(type, methodName, params);
        return methodName;
    }

    @Override
    public String resolveFieldName(Class<?> type, String fieldName) {
        //TODO: The actual bytecode uses MCP names, but reflection gives the CraftBukkit names
        //      This currently causes a NoSuchFieldError at runtime when generated code is executed
        //return remapUtils.mapFieldName(type, fieldName);
        return fieldName;
    }

    @Override
    public void addVariables(Map<String, String> variables) {
        super.addVariables(variables);
        variables.put("forge", "catserver");
    }

    @Template.Optional
    @Template.InstanceType("catserver.server.remapper.RemapUtils")
    public static abstract class RemapUtils  extends Template.Class<Template.Handle> {
        @Template.Generated("public static String mapClassName(String className) {\n" +
                            "    if (className.startsWith(\"net.minecraft.server.\")) {\n" +
                            "        String internalClassName = className.replace('.', '/');\n" +
                            "        String mapping_name = (String) ReflectionTransformer.jarMapping.classes.get(internalClassName);\n" +
                            "        if (mapping_name != null) {\n" +
                            "            return mapping_name.replace('/', '.');\n" +
                            "        } else {\n" +
                            "            // Magma BUGFIX!!!\n" +
                            "            // If we do not do this, it will suffer a NPE in the PluginClassLoader\n" +
                            "            return \"missing.type.\" + className;\n" +
                            "        }\n" +
                            "    }\n" +
                            "    return className;\n" +
                            "}")
        public abstract String mapClassName(String className);

        @Template.Generated("public static String mapMethod(Class<?> inst, String name, Class<?>[] parameterTypes)")
        public abstract String mapMethod(Class<?> type, String name, Class<?>[] parameterTypes);

        @Template.Generated("public static String mapFieldName(Class<?> inst, String name)")
        public abstract String mapFieldName(Class<?> type, String fieldName);
    }

}
