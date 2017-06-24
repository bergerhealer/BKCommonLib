package com.bergerkiller.bukkit.common.server;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.bukkit.Bukkit;

import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.ClassHook;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;

/**
 * This facility creates an appropriate Server object for use within Bukkit.
 * It should accurately try to detect and create the server BKCommonLib is linked against.
 */
public class TestServerFactory {

    public static void initTestServer() {
        if (CommonServerBase.SERVER_CLASS == null) {
            throw new IllegalStateException("Unable to detect server type during test");
        }

        //System.out.println("Detected server class under test: " + CommonServerBase.SERVER_CLASS);

        String cb_root = getPackagePath(CommonServerBase.SERVER_CLASS);
        String nms_root = "net.minecraft.server" + cb_root.substring(cb_root.lastIndexOf('.'));
        try {
            Field f = CommonServerBase.SERVER_CLASS.getDeclaredField("console");
            nms_root = getPackagePath(f.getType());
        } catch (Throwable t) {}
        cb_root += ".";
        nms_root += ".";

        //System.out.println("CB ROOT: " + cb_root);
        //System.out.println("NMS ROOT: " + nms_root);

        try {
            // Bootstrap is required
            Class<?> dispenserRegistryClass = Class.forName(nms_root + "DispenserRegistry");
            Method dispenserRegistryBootstrapMethod = dispenserRegistryClass.getMethod("c");
            dispenserRegistryBootstrapMethod.invoke(null);

            // Create some stuff by null-constructing them (not calling initializer)
            // This prevents loads of extra server logic executing during test
            ClassTemplate<?> server_t = ClassTemplate.create(CommonServerBase.SERVER_CLASS);
            Object server = server_t.newInstanceNull();
            Class<?> dedicatedType = Class.forName(nms_root + "DedicatedServer");
            ClassTemplate<?> mc_server_t = ClassTemplate.create(dedicatedType);
            Object mc_server = mc_server_t.newInstanceNull();

            // Create data converter registry manager object - used for serialization/deserialization
            // Only used >= MC 1.10.2
            Class<?> dataConverterRegistryClass = null;
            try {
                dataConverterRegistryClass = Class.forName(nms_root + "DataConverterRegistry");
                Method dataConverterRegistryInitMethod = dataConverterRegistryClass.getMethod("a");
                Object dataConverterManager = dataConverterRegistryInitMethod.invoke(null);
                setField(mc_server, "dataConverterManager", dataConverterManager);
            } catch (ClassNotFoundException ex) {}

            // Initialize some of the fields so they don't result into NPE during test
            setField(server, "console", mc_server);
            setField(server, "logger",  MountiplexUtil.LOGGER);

            // Assign to the Bukkit server silently (don't want a duplicate server info log line with random null's)
            Field bkServerField = Bukkit.class.getDeclaredField("server");
            bkServerField.setAccessible(true);
            bkServerField.set(null, server);
        } catch (Throwable t) {
            System.err.println("Failed to initialize server under test");
            System.out.println("Detected server class under test: " + CommonServerBase.SERVER_CLASS);
            System.out.println("Detected NMS_ROOT: " + nms_root);
            System.out.println("Detected CB_ROOT: " + cb_root);
            t.printStackTrace();
        }
    }

    private static void setField(Object instance, String name, Object value) {
        Field f = null;
        Class<?> t = instance.getClass();
        while (t != null && f == null) {
            try {
                f = t.getDeclaredField(name);
                f.setAccessible(true);
            } catch (Throwable ex) {}
            t = t.getSuperclass();
        }
        if (f == null) {
            throw new RuntimeException("Field " + name + " not found in " + instance.getClass().getName());
        }
        try {
            f.set(instance, value);
        } catch (Throwable ex) {
            throw new RuntimeException("Failed to set field " + name, ex);
        }
    }
    
    private static String getPackagePath(Class<?> type) {
        return type.getPackage().getName();
    }

    public static class ServerHook extends ClassHook<ServerHook> {
        @HookMethod("public String getVersion()")
        public String getVersion() {
            return "DUMMY_SERVER";
        }

        @HookMethod("public String getBukkitVersion()")
        public String getBukkitVersion() {
            return "DUMMY_BUKKIT";
        }
    }
}
