package com.bergerkiller.bukkit.common.server;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.resolver.FieldNameResolver;
import com.bergerkiller.mountiplex.reflection.resolver.MethodNameResolver;

/**
 * Fabric server support, that uses the Bukkit4Fabric mod for paper/spigot/bukkit support.<br>
 * <br>
 * https://github.com/BukkitFabric/bukkit4fabric/
 */
public class Bukkit4FabricServer extends SpigotServer implements FieldNameResolver, MethodNameResolver {
    private ReflectionRemapperClass reflectionRemapper = null;

    @Override
    public boolean init() {
        if (!super.init()) {
            return false;
        }

        // Check Bukkit4fabric mod exists, we expect this Class to exist
        try {
            Class.forName("com.javazilla.bukkitfabric.BukkitFabricMod");
        } catch (Throwable t) {
            return false;
        }

        // Make sure RemappingUtils exists, this initializes the RemappingUtilsHandle using the above initialized declaration
        reflectionRemapper = Template.Class.create(ReflectionRemapperClass.class);
        if (!reflectionRemapper.isAvailable()) {
            return false;
        }

        // Force initialization to avoid late catastrophic failing
        reflectionRemapper.forceInitialization();

        // A basic test.
        /*
        try {
            System.out.println(this.resolveClassPath("net.minecraft.resources.MinecraftKey"));
            System.out.println(this.resolveClassPath("net.minecraft.resources.ResourceKey"));
            Class<?> mckey = Class.forName(this.resolveClassPath("net.minecraft.resources.MinecraftKey"));
            Class<?> reskey = Class.forName(this.resolveClassPath("net.minecraft.resources.ResourceKey"));
            System.out.println(this.resolveMethodName(mckey, "a", new Class<?>[0]));
            for (Method m : reskey.getDeclaredMethods()) {
                System.out.println("- " + m);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        */

        return true;
    }

    @Override
    public String getServerName() {
        return "Fabric (Bukkit4fabric)";
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
        path = reflectionRemapper.mapClassName(path);

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
        try {
            Method method = reflectionRemapper.getDeclaredMethodByName(type, methodName, params);
            if (method != null) {
                return method.getName();
            }
        } catch (Throwable t) {
            t.printStackTrace(); // They really need to fix this.
        }

        return methodName;
    }

    @Override
    public String resolveFieldName(Class<?> type, String fieldName) {
        try {
            Field field = reflectionRemapper.getDeclaredFieldByName(type, fieldName);
            if (field != null) {
                return field.getName();
            }
        } catch (Throwable t) {
            t.printStackTrace(); // They really need to fix this.
        }

        return fieldName;
    }

    @Override
    public void addVariables(Map<String, String> variables) {
        super.addVariables(variables);
        variables.put("fabric", "bukkit4fabric");
        variables.put("forge_nms_obfuscated", "true");
    }

    @Template.Optional
    @Template.InstanceType("com.javazilla.bukkitfabric.nms.ReflectionRemapper")
    public static abstract class ReflectionRemapperClass extends Template.Class<Template.Handle> {

        @Template.Generated("public static String mapClassName(String className)")
        public abstract String mapClassName(String className);

        @Template.Generated("public static java.lang.reflect.Method getDeclaredMethodByName(Class<?> calling, String f, Class<?>[] parms)")
        public abstract Method getDeclaredMethodByName(Class<?> type, String name, Class<?>[] params);

        @Template.Generated("public static java.lang.reflect.Field getDeclaredFieldByName(Class<?> calling, String f)")
        public abstract Field getDeclaredFieldByName(Class<?> type, String fieldName);
    }

}
