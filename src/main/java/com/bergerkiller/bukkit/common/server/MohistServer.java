package com.bergerkiller.bukkit.common.server;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.bukkit.Bukkit;

import com.bergerkiller.bukkit.common.utils.StreamUtil;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.resolver.FieldNameResolver;
import com.bergerkiller.mountiplex.reflection.resolver.MethodNameResolver;

/**
 * Mohist is a PaperSpigot + Forge implementation
 */
public class MohistServer extends PaperSpigotServer implements FieldNameResolver, MethodNameResolver {
    private RemapUtilsClass remapUtils = null;

    @Override
    public boolean init() {
        if (!super.init()) {
            return false;
        }

        // Check this is actually a Mohist server, we expect this Class to exist
        try {
            Class.forName("red.mohist.Mohist");
        } catch (Throwable t) {
            return false;
        }

        // Make sure RemapUtils exists, this initializes the RemapUtilsHandle using the above initialized declaration
        remapUtils = Template.Class.create(RemapUtilsClass.class);
        if (!remapUtils.isAvailable()) {
            return false;
        }

        // Force initialization to avoid late catastrophic failing
        remapUtils.forceInitialization();

        return true;
    }

    @Override
    public String getServerName() {
        return "Mohist";
    }

    public String getMainWorldName() {
        return "world";
    }

    @Override
    public Collection<String> getLoadableWorlds() {
        String[] subDirs = Bukkit.getWorldContainer().list();
        Collection<String> rval = new ArrayList<String>(subDirs.length + 1);
        rval.add(getMainWorldName());
        for (String worldName : subDirs) {
            if (isLoadableWorld(worldName)) {
                rval.add(worldName);
            }
        }
        return rval;
    }

    @Override
    public File getWorldRegionFolder(String worldName) {
        // Is always the region subdirectory on Forge
        File regionFolder = new File(getWorldFolder(worldName), "region");
        return regionFolder.exists() ? regionFolder : null;
    }

    @Override
    public File getWorldFolder(String worldName) {
        // If main world, then it is the container itself
        if (worldName.equals(getMainWorldName())) {
            return Bukkit.getWorldContainer();
        } else {
            return StreamUtil.getFileIgnoreCase(Bukkit.getWorldContainer(), worldName);
        }
    }

    @Override
    public String resolveClassPath(String path) {
        // Replaces path with proper net.minecraft.server.v1_1_1 path
        path = super.resolveClassPath(path);

        // Ask Mohist what the actual class name is on Forge
        path = remapUtils.mapClassName(path);

        return path;
    }

    @Override
    public boolean canLoadClassPath(String classPath) {
        // The .class data at this path contains obfuscated type information
        // These obfuscated names are deobufscated at runtime
        // This difference causes compiler errors at runtime, so instead of
        // loading the .class files, inspect the signatures using reflection.
        if (classPath.startsWith("org.bukkit.craftbukkit")) {
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
    public void addVariables(Map<String, String> variables) {
        super.addVariables(variables);
        variables.put("forge", "mohist");
    }

    @Template.Optional
    @Template.InstanceType("red.mohist.bukkit.nms.utils.RemapUtils")
    public static abstract class RemapUtilsClass extends Template.Class<Template.Handle> {
        @Template.Generated("public static String mapClassName(String className) {\r\n" + 
                            "    if (className.startsWith(\"net.minecraft.server.\")) {\r\n" + 
                            "        red.mohist.bukkit.nms.model.ClassMapping mapping;\r\n" + 
                            "        mapping = (red.mohist.bukkit.nms.model.ClassMapping) RemapUtils.jarMapping.byNMSName.get(className);\r\n" + 
                            "        if (mapping != null) {\r\n" + 
                            "            return mapping.getMcpName();\r\n" + 
                            "        } else {\r\n" + 
                            "            // Mohist BUGFIX!!!\r\n" + 
                            "            // If we do not do this, it will suffer a NPE in the PluginClassLoader\r\n" + 
                            "            return \"missing.type.\" + className;\r\n" + 
                            "        }\r\n" + 
                            "    }\r\n" + 
                            "    return className;\r\n" + 
                            "}")
        public abstract String mapClassName(String className);

        @Template.Generated("public static transient String mapMethodName(Class<?> type, String name, Class<?>[] parameterTypes)")
        public abstract String mapMethodName(Class<?> type, String name, Class<?>[] parameterTypes);

        @Template.Generated("public static String mapFieldName(Class<?> type, String fieldName)")
        public abstract String mapFieldName(Class<?> type, String fieldName);
    }

}
