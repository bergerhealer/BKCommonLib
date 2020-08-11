package com.bergerkiller.bukkit.common.server;

import java.lang.reflect.Method;
import java.util.Map;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.resolver.FieldNameResolver;
import com.bergerkiller.mountiplex.reflection.resolver.MethodNameResolver;

/**
 * Mohist is a PaperSpigot + Forge implementation
 */
public class MagmaServer extends SpigotServer implements FieldNameResolver, MethodNameResolver {
    private RemappingUtilsClass remappingUtils = null;

    @Override
    public boolean init() {
        if (!super.init()) {
            return false;
        }

        // Check this is actually a Mohist server, we expect this Class to exist
        try {
            Class.forName("org.magmafoundation.magma.Magma");
        } catch (Throwable t) {
            return false;
        }

        // Make sure RemapUtils exists, this initializes the RemapUtilsHandle using the above initialized declaration
        remappingUtils = Template.Class.create(RemappingUtilsClass.class);
        if (!remappingUtils.isAvailable()) {
            return false;
        }

        // Force initialization to avoid late catastrophic failing
        remappingUtils.forceInitialization();

        return true;
    }

    @Override
    public void postInit() {
        super.postInit();

        try {
            System.out.println(resolveClassPath("net.minecraft.server.EnumDifficulty"));
            Class<?> c = Class.forName(resolveClassPath("net.minecraft.server.EnumDifficulty"));
            System.out.println("mapMethodName: " + remappingUtils.mapMethodName(c, "c", new Class[] {}));
            System.out.println("inverseMapMethodName: " + remappingUtils.mapMethodName(c, "c", new Class[] {}));
            System.out.println("Methods of " + c.getName());
            for (Method m : c.getDeclaredMethods()) {
                System.out.println("  - " + m.toGenericString());
            }
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public String getServerName() {
        return "Magma";
    }

    @Override
    public String resolveClassPath(String path) {
        // Replaces path with proper net.minecraft.server.v1_1_1 path
        path = super.resolveClassPath(path);

        // Ask Mohist what the actual class name is on Forge
        path = remappingUtils.mapClassName(path);

        return path;
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
        @Template.Generated("public static String mapClassName(String className) {\r\n" + 
                            "    if (className.startsWith(\"net.minecraft.server.\")) {\r\n" + 
                            "        org.magmafoundation.magma.remapper.mappingsModel.ClassMappings mapping;\r\n" + 
                            "        mapping = (org.magmafoundation.magma.remapper.mappingsModel.ClassMappings) RemappingUtils.jarMapping.byNMSName.get(className);\r\n" + 
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
