package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.conversion.CommonConverters;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
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

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class Common {

    /**
     * BKCommonLib version number, use this to set your dependency version for
     * BKCommonLib-using plugins<br>
     * <b>Use getVersion() instead if you want the actual, current version!
     * Constants get inlined when compiling!</b>
     */
    public static final int VERSION = 11302;
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

        if (!Common.IS_COMPATIBLE) {
            throw new UnsupportedOperationException("Minecraft " + Common.MC_VERSION + " is not supported!");
        }
    }

    static {
        // Used to detect whether running in a live environment, or under test
        IS_TEST_MODE = CommonBootstrap.isTestMode();

        // Depends on whether Bukkit server is initialized
        LOGGER = Logging.LOGGER;

        // Set up the constants
        SERVER = CommonBootstrap.getCommonServer();
        IS_COMPATIBLE = SERVER.isCompatible();
        MC_VERSION = SERVER.getMinecraftVersion();
        IS_SPIGOT_SERVER = SERVER instanceof SpigotServer;
        IS_PAPERSPIGOT_SERVER = SERVER instanceof PaperSpigotServer;

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

        // Enum Gamemode not available in package space on <= MC 1.9; we must proxy it
        if (CommonUtil.getNMSClass("EnumGamemode") == null) {
            final String eg_path = Resolver.resolveClassPath("net.minecraft.server.EnumGamemode");
            final String eg_path_proxy = Resolver.resolveClassPath("net.minecraft.server.WorldSettings$EnumGamemode");
            Resolver.registerClassResolver(new ClassPathResolver() {
                @Override
                public String resolveClassPath(String classPath) {
                    if (classPath.equals(eg_path)) {
                        return eg_path_proxy;
                    }
                    return classPath;
                }
            });
        }

        // Register additional version-specific class remappings
        if (IS_COMPATIBLE) {
            final String nms_root = SERVER.getClassName("net.minecraft.server.Entity").replace(".Entity", "");
            final Map<String, String> remappings = new HashMap<String, String>();

            // Botched deobfuscation of class names on 1.8.8 / proxy missing classes to simplify API
            if (Common.evaluateMCVersion("<=", "1.8.8")) {
                remappings.put(nms_root + ".MobSpawnerData", nms_root + ".MobSpawnerAbstract$a");
                remappings.put(nms_root + ".SoundEffectType", nms_root + ".Block$StepSound"); // workaround
                remappings.put(nms_root + ".DataWatcher$Item", nms_root + ".DataWatcher$WatchableObject");
                remappings.put(nms_root + ".PlayerChunk", nms_root + ".PlayerChunkMap$PlayerChunk"); // nested on 1.8.8

                // PacketPlayInUseItem and PacketPlayInBlockPlace were merged as one packet on these versions
                remappings.put(nms_root + ".PacketPlayInUseItem", nms_root + ".PacketPlayInBlockPlace");

                // We proxy a bunch of classes, because they don't exist in 1.8.8
                // Writing custom wrappers with switches would be too tiresome
                // This allows continued use of the same API without trouble
                // Converters take care to convert between the Class and Id used internally
                remappings.put(nms_root + ".EnumItemSlot", "com.bergerkiller.bukkit.common.internal.proxy.EnumItemSlot");
                remappings.put(nms_root + ".DataPaletteBlock", "com.bergerkiller.bukkit.common.internal.proxy.DataPaletteBlock");
                remappings.put(nms_root + ".DataWatcherObject", "com.bergerkiller.bukkit.common.internal.proxy.DataWatcherObject");
                remappings.put(nms_root + ".MobEffectList", "com.bergerkiller.bukkit.common.internal.proxy.MobEffectList");
                remappings.put(nms_root + ".SoundEffect", "com.bergerkiller.bukkit.common.internal.proxy.SoundEffect");
            }

            // Some classes were moved before around 1.8
            if (Common.evaluateMCVersion("<=", "1.8")) {
                remappings.put(nms_root + ".SoundEffectType", nms_root + ".StepSound");
                remappings.put(nms_root + ".Block$StepSound", nms_root + ".StepSound");
                remappings.put(nms_root + ".EnumDirection$EnumAxis", nms_root + ".EnumAxis");
                remappings.put(nms_root + ".PacketPlayOutPlayerInfo$EnumPlayerInfoAction", nms_root + ".EnumPlayerInfoAction");
                remappings.put(nms_root + ".PacketPlayInUseEntity$EnumEntityUseAction", nms_root + ".EnumEntityUseAction");
                remappings.put(nms_root + ".MobSpawnerData", nms_root + ".TileEntityMobSpawnerData");
                remappings.put(nms_root + ".DataWatcher$Item", nms_root + ".WatchableObject");
                remappings.put(nms_root + ".DataWatcher$WatchableObject", nms_root + ".WatchableObject");
                remappings.put(nms_root + ".PacketPlayOutScoreboardScore$EnumScoreboardAction", nms_root + ".EnumScoreboardAction");
                remappings.put(nms_root + ".PacketPlayOutMapChunk$ChunkMap", nms_root + ".ChunkMap");
                remappings.put(nms_root + ".PacketPlayOutPosition$EnumPlayerTeleportFlags", nms_root + ".EnumPlayerTeleportFlags");
                remappings.put(nms_root + ".PacketPlayOutTitle$EnumTitleAction", nms_root + ".EnumTitleAction");
                remappings.put(nms_root + ".PacketPlayOutCombatEvent$EnumCombatEventType", nms_root + ".EnumCombatEventType");
                remappings.put(nms_root + ".PacketPlayOutWorldBorder$EnumWorldBorderAction", nms_root + ".EnumWorldBorderAction");
                remappings.put(nms_root + ".PacketPlayOutPlayerInfo$PlayerInfoData", nms_root + ".PlayerInfoData");
                remappings.put(nms_root + ".PacketPlayInResourcePackStatus$EnumResourcePackStatus", nms_root + ".EnumResourcePackStatus");
                remappings.put(nms_root + ".PacketPlayInBlockDig$EnumPlayerDigType", nms_root + ".EnumPlayerDigType");
                remappings.put(nms_root + ".EntityHuman$EnumChatVisibility", nms_root + ".EnumChatVisibility");
                remappings.put(nms_root + ".PlayerChunk", nms_root + ".PlayerChunk");
                remappings.put(nms_root + ".WeightedRandom$WeightedRandomChoice", nms_root + ".WeightedRandomChoice");
                remappings.put(nms_root + ".BiomeBase$BiomeMeta", nms_root + ".BiomeMeta");
                remappings.put(nms_root + ".IScoreboardCriteria$EnumScoreboardHealthDisplay", nms_root + ".EnumScoreboardHealthDisplay");
                remappings.put(nms_root + ".IntHashMap$IntHashMapEntry", nms_root + ".IntHashMapEntry");
                remappings.put(nms_root + ".PacketPlayOutEntity$PacketPlayOutEntityLook", nms_root + ".PacketPlayOutEntityLook");
                remappings.put(nms_root + ".PacketPlayOutEntity$PacketPlayOutRelEntityMove", nms_root + ".PacketPlayOutRelEntityMove");
                remappings.put(nms_root + ".PacketPlayOutEntity$PacketPlayOutRelEntityMoveLook", nms_root + ".PacketPlayOutRelEntityMoveLook");
                remappings.put(nms_root + ".IChatBaseComponent$ChatSerializer", nms_root + ".ChatSerializer");
            }

            // Proxy classes that were added in 1.13 so that 1.12.2 and before works with the same API
            if (Common.evaluateMCVersion("<", "1.13")) {
                remappings.put(nms_root + ".HeightMap", "com.bergerkiller.bukkit.common.internal.proxy.HeightMapProxy_1_12_2");
                remappings.put(nms_root + ".HeightMap$Type", "com.bergerkiller.bukkit.common.internal.proxy.HeightMapProxy_1_12_2$Type");
                remappings.put("com.bergerkiller.bukkit.common.internal.proxy.HeightMap.Type", "com.bergerkiller.bukkit.common.internal.proxy.HeightMapProxy_1_12_2$Type");
                remappings.put(nms_root + ".VoxelShape", "com.bergerkiller.bukkit.common.internal.proxy.VoxelShapeProxy");
            }

            // EnumArt has seen many places...
            if (Common.evaluateMCVersion("<=", "1.8")) {
                remappings.put(nms_root + ".Paintings", nms_root + ".EnumArt");
            } else if (Common.evaluateMCVersion("<", "1.13")) {
                remappings.put(nms_root + ".Paintings", nms_root + ".EntityPainting$EnumArt");
            } else {
                // Located at net.minecraft.server.Paintings like normal.
            }

            // Some classes were moved after 1.13
            if (Common.evaluateMCVersion(">=", "1.13")) {
                remappings.put(nms_root + ".PacketPlayOutScoreboardScore$EnumScoreboardAction", nms_root + ".ScoreboardServer$Action");
            }

            // If remappings exist, add a resolver for them
            if (!remappings.isEmpty()) {
                Resolver.registerClassResolver(new ClassPathResolver() {
                    @Override
                    public String resolveClassPath(String classPath) {
                        String remapped = remappings.get(classPath);
                        return (remapped != null) ? remapped : classPath;
                    }
                });
            }
        }

        // Only do these things when we are compatible
        if (Common.IS_COMPATIBLE) {
            // Debug
            if (CommonBootstrap.WARN_WHEN_INIT_TEMPLATES) {
                Logging.LOGGER.log(Level.WARNING, "WARN_WHEN_INIT_TEMPLATES", new RuntimeException("Initializing templates"));
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

        // This unloader takes care of de-referencing everything container in here
        MountiplexUtil.registerUnloader(new Runnable() {
            @Override
            public void run() {
                TEMPLATE_RESOLVER.unload();
            }
        });
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
        return CommonBootstrap.evaluateMCVersion(operand, version);
    }
}
