package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.conversion.CommonConverters;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.bukkit.common.server.*;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.resolver.ClassPathResolver;
import com.bergerkiller.mountiplex.reflection.resolver.FieldNameResolver;
import com.bergerkiller.mountiplex.reflection.resolver.MethodNameResolver;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.templates.TemplateResolver;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class Common {

    /**
     * BKCommonLib version number, use this to set your dependency version for
     * BKCommonLib-using plugins<br>
     * <b>Use getVersion() instead if you want the actual, current version!
     * Constants get inlined when compiling!</b>
     */
    public static final int VERSION = 11200;
    /**
     * Defines the Minecraft version that runs on the server.
     */
    public static final String MC_VERSION;
    /**
     * Defines the net.minecraft.server constant (which is not inlined or
     * relocated). Implementer note: do NOT change this to a constant or maven
     * shading will rename it.
     */
    public static final String NMS_ROOT = StringUtil.join(".", "net", "minecraft", "server");
    /**
     * Defines the org.bukkit.craftbukkit constant (which is not inlined or
     * relocated). Implementer note: do NOT change this to a constant or maven
     * shading will rename it.
     */
    public static final String CB_ROOT = StringUtil.join(".", "org", "bukkit", "craftbukkit");
    /**
     * Defines the com.bergerkiller.bukkit.common root path of this library
     */
    public static final String COMMON_ROOT = "com.bergerkiller.bukkit.common";
    /**
     * Defines the type of server BKCommonLib is currently running on and
     * provides server-specific implementations.
     */
    public static final CommonServer SERVER;
    /**
     * Description text of the server, logged during startup
     */
    public static final String SERVER_DESCRIPTION;
    /**
     * Resolves template Class Declarations at runtime
     */
    public static final TemplateResolver TEMPLATE_RESOLVER = new TemplateResolver();
    /**
     * Gets whether the current server software used is the Spigot
     * implementation
     */
    public static final boolean IS_SPIGOT_SERVER;
    /**
     * Gets whether the current server software used is the PaperSpigot
     * implementation
     */
    public static final boolean IS_PAPERSPIGOT_SERVER;
    /**
     * Whether BKCommonLib is compatible with the server it is currently running
     * on
     */
    public static final boolean IS_COMPATIBLE;
    /**
     * Server or test level logger that is in use
     */
    public static final ModuleLogger LOGGER;
    /**
     * Whether BKCommonLib is run under test, indicating not all server functionality is available
     */
    public static final boolean IS_TEST_MODE;

    /**
     * When under test internal classes have to be loaded in a very specific order.
     * This function makes sure the server registers are initialized.
     */
    public static void bootstrap() {
        // Nothing. The static block will be executed, where the magic happens.
    }

    static {
        // We MUST have an instantiated Bukkit server to perform tests
        // This here initializes a test server as required
        IS_TEST_MODE = (Bukkit.getServer() == null);
        if (IS_TEST_MODE) {
            TestServerFactory.initTestServer();
        }

        // Depends on whether Bukkit server is initialized
        LOGGER = Logging.LOGGER;

        // Find out what server software we are running on
        CommonServer runningServer = new UnknownServer();
        boolean compatible = false;
        String mc_version = "UNKNOWN";

        // Get all available server types
        List<CommonServer> servers = new ArrayList<>();
        servers.add(new MCPCPlusServer());
        servers.add(new PaperSpigotServer());
        servers.add(new SpigotServer());
        servers.add(new SportBukkitServer());
        servers.add(new CraftBukkitServer());
        servers.add(new UnknownServer());

        // Use the first one that initializes correctly
        for (CommonServer server : servers) {
            try {
                if (server.init()) {
                    server.postInit();
                    compatible = server.isCompatible();
                    mc_version = server.getMinecraftVersion();
                    runningServer = server;
                    break;
                }
            } catch (Throwable t) {
                Logging.LOGGER.log(Level.SEVERE, "An error occurred during server detection:", t);
            }
        }

        // Set up the constants
        SERVER = runningServer;
        IS_COMPATIBLE = compatible;
        MC_VERSION = mc_version;
        IS_SPIGOT_SERVER = SERVER instanceof SpigotServer;
        IS_PAPERSPIGOT_SERVER = SERVER instanceof PaperSpigotServer;

        // Create server description token
        final StringBuilder serverDesc = new StringBuilder(300);
        serverDesc.append(SERVER.getServerName()).append(" (");
        serverDesc.append(SERVER.getServerDescription());
        serverDesc.append(") : ").append(SERVER.getServerVersion());
        SERVER_DESCRIPTION = serverDesc.toString();

        // Under test, log server information
        if (Common.IS_TEST_MODE) {
            Logging.LOGGER.log(Level.INFO, "Test running on " + SERVER_DESCRIPTION);
        }

        // Register server to handle field, method and class resolving
        //TODO! Implement these functions in SERVER directly
        Resolver.registerClassResolver(new ClassPathResolver() {
            @Override
            public String resolveClassPath(String classPath) {
                return SERVER.getClassName(classPath);
            }
        });
        Resolver.registerFieldResolver(new FieldNameResolver() {
            @Override
            public String resolveFieldName(Class<?> declaredClass, String fieldName) {
                return SERVER.getFieldName(declaredClass, fieldName);
            }
        });
        Resolver.registerMethodResolver(new MethodNameResolver() {
            @Override
            public String resolveMethodName(Class<?> declaredClass, String methodName, Class<?>[] parameterTypes) {
                return SERVER.getMethodName(declaredClass, methodName, parameterTypes);
            }
        });

        // Botched deobfuscation of class names on 1.8.8 / proxy missing classes to simplify API
        if (Common.MC_VERSION.equals("1.8.8")) {
            Resolver.registerClassResolver(new ClassPathResolver() {
                @Override
                public String resolveClassPath(String classPath) {
                    if (classPath.equals("net.minecraft.server.v1_8_R3.MobSpawnerData")) {
                        return "net.minecraft.server.v1_8_R3.MobSpawnerAbstract$a";
                    }
                    if (classPath.equals("net.minecraft.server.v1_8_R3.SoundEffectType")) {
                        return "net.minecraft.server.v1_8_R3.Block.StepSound"; // workaround
                    }
                    if (classPath.equals("net.minecraft.server.v1_8_R3.DataWatcher$Item")) {
                        return "net.minecraft.server.v1_8_R3.DataWatcher$WatchableObject";
                    }

                    // We proxy a bunch of classes, because they don't exist in 1.8.8
                    // Writing custom wrappers with switches would be too tiresome
                    // This allows continued use of the same API without trouble
                    // Converters take care to convert between the Class and Id used internally
                    if (classPath.equals("net.minecraft.server.v1_8_R3.EnumItemSlot")) {
                        return "com.bergerkiller.bukkit.common.internal.proxy.EnumItemSlot";
                    }
                    if (classPath.equals("net.minecraft.server.v1_8_R3.DataPaletteBlock")) {
                        return "com.bergerkiller.bukkit.common.internal.proxy.DataPaletteBlock";
                    }
                    if (classPath.equals("net.minecraft.server.v1_8_R3.DataWatcherObject")) {
                        return "com.bergerkiller.bukkit.common.internal.proxy.DataWatcherObject";
                    }
                    if (classPath.equals("net.minecraft.server.v1_8_R3.EnumGamemode")) {
                        return "com.bergerkiller.bukkit.common.internal.proxy.EnumGamemode";
                    }
                    if (classPath.equals("net.minecraft.server.v1_8_R3.MobEffectList")) {
                        return "com.bergerkiller.bukkit.common.internal.proxy.MobEffectList";
                    }
                    return classPath;
                }
            });
        }

        // This must be initialized AFTER we have registered the Class path resolvers!
        TEMPLATE_RESOLVER.load();
        Resolver.registerClassDeclarationResolver(TEMPLATE_RESOLVER);

        // Conversion types registration
        try {
            CommonUtil.loadClass(CommonConverters.class);
            CommonUtil.loadClass(Conversion.class);
            CommonUtil.loadClass(DuplexConversion.class);
        } catch (Throwable t) {
            Logging.LOGGER_CONVERSION.log(Level.SEVERE, "Failed to initialize default converters", t);
        }
    }

    /**
     * Gets the BKCommonLib version number, use this function to compare your
     * own version with the currently installed version
     *
     * @return BKCommonLib version number
     */
    public static int getVersion() {
        return VERSION;
    }

    /**
     * Loads one or more classes<br>
     * Use this method to pre-load certain classes before enabling your plugin
     *
     * @param classNames to load
     */
    public static void loadClasses(String... classNames) {
        for (String className : classNames) {
            try {
                loadInner(Class.forName(className));
            } catch (ExceptionInInitializerError error) {
                throw new RuntimeException("An error occurred trying to initialize class '" + className + "':", error);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException("Could not load class '" + className + "' - Update needed?");
            }
        }
    }

    private static void loadInner(Class<?> clazz) {
        for (Class<?> subclass : clazz.getDeclaredClasses()) {
            loadInner(subclass);
        }
    }

    /**
     * Handles a reflection field or method missing<br>
     * Has a special handler for fields and methods defined inside this library
     *
     * @param type of object: field or method
     * @param name of the field or method
     * @param source class for the field or method
     */
    protected static void handleReflectionMissing(String type, String name, Class<?> source) {
        String msg = type + " '" + name + "' does not exist in class file " + source.getSimpleName();
        Exception ex = new Exception(msg);
        for (StackTraceElement elem : ex.getStackTrace()) {
            if (elem.getClassName().startsWith(COMMON_ROOT + ".reflection")) {
                Bukkit.getServer().getLogger().log(Level.SEVERE, "[BKCommonLib] " + msg + " (Update BKCommonLib?)");
                return;
            }
        }
        ex.printStackTrace();
    }

    /**
     * Checks if the Minecraft version matches a version condition
     * 
     * @param operand to evaluate with, for example ">=" and "!="
     * @param version the operand is applied to (right side)
     * @return True if the version matches, False if not
     */
    public static boolean evaluateMCVersion(String operand, String version) {
        return MountiplexUtil.evaluateText(MC_VERSION, operand, version);
    }
}
