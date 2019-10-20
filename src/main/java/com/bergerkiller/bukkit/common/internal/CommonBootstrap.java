package com.bergerkiller.bukkit.common.internal;

import java.io.PrintStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.log4j.spi.LoggerContextFactory;
import org.bukkit.Bukkit;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.ModuleLogger;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.MC1_8_8_Conversion;
import com.bergerkiller.bukkit.common.conversion.type.NBTConversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.server.CommonServer;
import com.bergerkiller.bukkit.common.server.CraftBukkitServer;
import com.bergerkiller.bukkit.common.server.MCPCPlusServer;
import com.bergerkiller.bukkit.common.server.PaperSpigotServer;
import com.bergerkiller.bukkit.common.server.SpigotServer;
import com.bergerkiller.bukkit.common.server.SportBukkitServer;
import com.bergerkiller.bukkit.common.server.TestServerFactory;
import com.bergerkiller.bukkit.common.server.UnknownServer;
import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.conversion.Conversion;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.templates.TemplateResolver;

/**
 * Initialization of server and internal components in a lazy-loading fashion
 */
public class CommonBootstrap {
    public static boolean WARN_WHEN_INIT_SERVER = false;
    public static boolean WARN_WHEN_INIT_TEMPLATES = false;
    private static boolean _hasInitTemplates = false;
    private static boolean _hasInitTestServer = false;
    private static boolean _isSpigotServer = false;
    private static CommonServer _commonServer = null;
    private static TemplateResolver _templateResolver = new TemplateResolver();

    /**
     * Checks if the Minecraft version matches a version condition
     * 
     * @param operand to evaluate with, for example ">=" and "!="
     * @param version the operand is applied to (right side)
     * @return True if the version matches, False if not
     */
    public static boolean evaluateMCVersion(String operand, String version) {
        return initCommonServer().evaluateMCVersion(operand, version);
    }

    /**
     * Gets whether the server we are currently running on is a Spigot server
     * 
     * @return True if the current server is a Spigot server
     */
    public static boolean isSpigotServer() {
        initCommonServer();
        return _isSpigotServer;
    }

    /**
     * Detects and returns the common server implementation that is used.
     * The result is cached.
     * 
     * @return common server
     */
    public static CommonServer initCommonServer() {
        if (_commonServer == null) {
            _commonServer = new UnknownServer();

            // Get all available server types
            if (isTestMode()) {
                // Use our own logger to speed up initialization under test
                initLog4j();

                // Always Spigot server
                _commonServer = new SpigotServer();
                _commonServer.init();
                _commonServer.postInit();
            } else {
                // Autodetect most likely server type
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
                            _commonServer = server;
                            break;
                        }
                    } catch (Throwable t) {
                        Logging.LOGGER.log(Level.SEVERE, "An error occurred during server detection:", t);
                    }
                }
            }

            // Update
            _isSpigotServer = (_commonServer instanceof SpigotServer);

            // Make type and method information available for this server type
            // Templates should not be initialized during this time, bad things happen
            boolean oldWarnTemplates = WARN_WHEN_INIT_TEMPLATES;
            WARN_WHEN_INIT_TEMPLATES = true;
            initResolvers(_commonServer);
            WARN_WHEN_INIT_TEMPLATES = oldWarnTemplates;
        }
        return _commonServer;
    }

    /**
     * Gets the template engine without initializing it.
     * Can be used to check if a minecraft version is supported.
     * 
     * @return template engine
     */
    public static TemplateResolver getTemplates() {
        return _templateResolver;
    }

    /**
     * Initializes the template engine, so that templates can be loaded into their respective
     * handles. This must be called before that can work properly, since it requires type
     * translation and converters to be registered.
     * 
     * @return template resolver
     */
    public static TemplateResolver initTemplates() {
        if (_hasInitTemplates) {
            return _templateResolver;
        }
        _hasInitTemplates = true;

        // Retrieve the CommonServer instance (which initializes resolvers) and check if compatible
        // Don't initialize the templates if we are not compatible.
        if (!initCommonServer().isCompatible()) {
            return _templateResolver;
        }

        // Debug
        if (WARN_WHEN_INIT_TEMPLATES) {
            Logging.LOGGER.log(Level.WARNING, "WARN_WHEN_INIT_TEMPLATES", new RuntimeException("Initializing templates"));
        }

        // This must be initialized AFTER we have registered the Class path resolvers!
        _templateResolver.load();
        Resolver.registerClassDeclarationResolver(_templateResolver);

        // This unloader takes care of de-referencing everything contained in here
        MountiplexUtil.registerUnloader(_templateResolver::unload);

        return _templateResolver;
    }

    /**
     * Detects whether the server is running under test, and not on an actual live server
     * 
     * @return True if test mode
     */
    public static boolean isTestMode() {
        return _hasInitTestServer || Bukkit.getServer() == null;
    }

    /**
     * Ensures that {@link org.bukkit.Bukkit#getServer()} returns a valid non-null Server instance.
     * During normal execution this is guaranteed to be fine, but while running tests this is not
     * the case.
     */
    public static void initServer() {
        Common.bootstrap();
        if (!_hasInitTestServer && Bukkit.getServer() == null) {
            _hasInitTestServer = true;

            // Sometimes this is unwanted when running tests
            // To debug this issue, set WARN_WHEN_INIT_SERVER = true;
            if (WARN_WHEN_INIT_SERVER) {
                Logging.LOGGER.log(Level.WARNING, "WARN_WHEN_INIT_SERVER", new RuntimeException("Initializing server"));
            }

            // Initialize the server. Restore the output and error streams.
            // The server initializes log4j and causes that to otherwise break.
            PrintStream oldout = System.out;
            PrintStream olderr = System.err;
            try {
                TestServerFactory.initTestServer();
            } finally {
                System.setOut(oldout);
                System.setErr(olderr);
            }

            // Display this too during the test
            Logging.LOGGER.log(Level.INFO, "Test running on " + Common.SERVER.getServerDetails());
        }
    }

    /**
     * Initializes the type, field and method resolvers for a server, so that such information
     * can be obtained at runtime.
     * 
     * @param server
     */
    private static void initResolvers(CommonServer server) {
        // Register server to handle field, method and class resolving
        //TODO! Implement these functions in SERVER directly
        Resolver.registerClassResolver(server::getClassName);
        Resolver.registerFieldResolver(server::getFieldName);
        Resolver.registerMethodResolver(server::getMethodName);

        // Enum Gamemode not available in package space on <= MC 1.9; we must proxy it
        if (Resolver.loadClass("net.minecraft.server.EnumGamemode", false) == null) {
            final String eg_path = Resolver.resolveClassPath("net.minecraft.server.EnumGamemode");
            final String eg_path_proxy = Resolver.resolveClassPath("net.minecraft.server.WorldSettings$EnumGamemode");
            Resolver.registerClassResolver(classPath -> {
                if (classPath.equals(eg_path)) {
                    return eg_path_proxy;
                }
                return classPath;
            });
        }

        final String nms_root = server.getClassName("net.minecraft.server.Entity").replace(".Entity", "");
        final String cb_root = server.getClassName("org.bukkit.craftbukkit.CraftServer").replaceAll(".CraftServer", "");
        final Map<String, String> remappings = new HashMap<String, String>();

        // We renamed EntityTrackerEntry to EntityTrackerEntryState to account for the wrapping EntityTracker on 1.14 and later
        remappings.put(nms_root + ".EntityTrackerEntryState", nms_root + ".EntityTrackerEntry");

        // Instead of CraftBukkit LongHashSet, we use a custom implementation with bugfixes on 1.13.2 and earlier
        // This is now possible since we no longer interface with CraftBukkit LongHashSet anywhere
        remappings.put("com.bergerkiller.bukkit.common.internal.LongHashSet", "com.bergerkiller.bukkit.common.internal.logic.LongHashSet_pre_1_13_2");
        remappings.put("com.bergerkiller.bukkit.common.internal.LongHashSet$LongIterator", "com.bergerkiller.bukkit.common.internal.logic.LongHashSet_pre_1_13_2$LongIterator");

        // Botched deobfuscation of class names on 1.8.8 / proxy missing classes to simplify API
        if (evaluateMCVersion("<=", "1.8.8")) {
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
        if (evaluateMCVersion("<=", "1.8")) {
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
            remappings.put(nms_root + ".PacketPlayInFlying$PacketPlayInLook", nms_root + ".PacketPlayInLook");
            remappings.put(nms_root + ".PacketPlayInFlying$PacketPlayInPosition", nms_root + ".PacketPlayInPosition");
            remappings.put(nms_root + ".PacketPlayInFlying$PacketPlayInPositionLook", nms_root + ".PacketPlayInPositionLook");
            remappings.put(nms_root + ".IChatBaseComponent$ChatSerializer", nms_root + ".ChatSerializer");
        }

        // Proxy classes that were added in 1.13 so that 1.12.2 and before works with the same API
        if (evaluateMCVersion("<", "1.13")) {
            remappings.put(nms_root + ".HeightMap", "com.bergerkiller.bukkit.common.internal.proxy.HeightMapProxy_1_12_2");
            remappings.put(nms_root + ".HeightMap$Type", "com.bergerkiller.bukkit.common.internal.proxy.HeightMapProxy_1_12_2$Type");
            remappings.put("com.bergerkiller.bukkit.common.internal.proxy.HeightMap.Type", "com.bergerkiller.bukkit.common.internal.proxy.HeightMapProxy_1_12_2$Type");
            remappings.put(nms_root + ".VoxelShape", "com.bergerkiller.bukkit.common.internal.proxy.VoxelShapeProxy");
        }

        // EnumArt has seen many places...
        if (evaluateMCVersion("<=", "1.8")) {
            remappings.put(nms_root + ".Paintings", nms_root + ".EnumArt");
        } else if (evaluateMCVersion("<", "1.13")) {
            remappings.put(nms_root + ".Paintings", nms_root + ".EntityPainting$EnumArt");
        } else {
            // Located at net.minecraft.server.Paintings like normal.
        }

        // Still obfuscated on these versions of MC
        if (evaluateMCVersion(">=", "1.14") && evaluateMCVersion("<=", "1.14.1")) {
            remappings.put(nms_root + ".ShapeDetector$Shape", nms_root + ".ShapeDetector$c");
        }

        // Some classes were moved after 1.13
        if (evaluateMCVersion(">=", "1.13")) {
            remappings.put(nms_root + ".PacketPlayOutScoreboardScore$EnumScoreboardAction", nms_root + ".ScoreboardServer$Action");
        }

        // Many classes disappeared, merged or moved with MC 1.14
        if (evaluateMCVersion(">=", "1.14")) {
            String unimi_fastutil_path = "org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.";
            try {
                Class.forName(unimi_fastutil_path + "longs.LongSet");

                // Fixes hardcoded fastutil paths used in templates
                remappings.put("it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap", unimi_fastutil_path + "ints.Int2ObjectOpenHashMap");
                remappings.put("it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap", unimi_fastutil_path + "longs.Long2ObjectOpenHashMap");
                remappings.put("it.unimi.dsi.fastutil.longs.LongIterator", unimi_fastutil_path + "longs.LongIterator");
                remappings.put("it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet", unimi_fastutil_path + "longs.LongLinkedOpenHashSet");
                remappings.put("it.unimi.dsi.fastutil.longs.LongOpenHashSet", unimi_fastutil_path + "longs.LongOpenHashSet");
                remappings.put("it.unimi.dsi.fastutil.longs.LongSet", unimi_fastutil_path + "longs.LongSet");
            } catch (ClassNotFoundException ex) {
                unimi_fastutil_path = "it.unimi.dsi.fastutil.";
            }

            remappings.put(nms_root + ".EntityHuman$EnumChatVisibility", nms_root + ".EnumChatVisibility");
            remappings.put(nms_root + ".EntityTracker", nms_root + ".PlayerChunkMap$EntityTracker");
            remappings.put("com.bergerkiller.bukkit.common.internal.LongHashSet", unimi_fastutil_path + "longs.LongSet");
            remappings.put(cb_root + ".util.LongObjectHashMap", unimi_fastutil_path + "longs.Long2ObjectMap");
            remappings.put(nms_root + ".IntHashMap", unimi_fastutil_path + "ints.Int2ObjectMap");
            remappings.put(nms_root + ".IntHashMap$IntHashMapEntry", unimi_fastutil_path + "ints.Int2ObjectMap$Entry");
            remappings.put(unimi_fastutil_path + "ints.IntHashMap$IntHashMapEntry", unimi_fastutil_path + "ints.Int2ObjectMap$Entry");
            remappings.put(nms_root + ".EntityTracker", nms_root + ".PlayerChunkMap");
            remappings.put(nms_root + ".EntityTrackerEntry", nms_root + ".PlayerChunkMap$EntityTracker");
        }

        // If remappings exist, add a resolver for them
        if (!remappings.isEmpty()) {
            Resolver.registerClassResolver(classPath -> {
                String remapped = remappings.get(classPath);
                return (remapped != null) ? remapped : classPath;
            });
        }

        // Register converters
        Conversion.registerConverters(WrapperConversion.class);
        Conversion.registerConverters(HandleConversion.class);
        Conversion.registerConverters(NBTConversion.class);
        if (evaluateMCVersion("<=", "1.8.8")) {
            Conversion.registerConverters(MC1_8_8_Conversion.class);
        }
    }

    /**
     * Initializes the Logger Context Factory of log4j to log to our own Module Logger.
     * This prevents very slow initialization of the default logger under test.
     */
    private static void initLog4j() {
        org.apache.logging.log4j.LogManager.setFactory(new LoggerContextFactory() {
            @Override
            public LoggerContext getContext(String arg0, ClassLoader arg1, Object arg2, boolean arg3) {
                return new LoggerContext() {
                    @Override
                    public Object getExternalContext() {
                        return null;
                    }

                    @Override
                    public ExtendedLogger getLogger(String name) {
                        return new ModuleLogger(name).toLog4jExtendedLogger();
                    }

                    @Override
                    public ExtendedLogger getLogger(String arg0, MessageFactory arg1) {
                        return getLogger(arg0);
                    }

                    @Override
                    public boolean hasLogger(String arg0) {
                        return true;
                    }

                    @Override
                    public boolean hasLogger(String arg0, MessageFactory arg1) {
                        return true;
                    }

                    @Override
                    public boolean hasLogger(String arg0, Class<? extends MessageFactory> arg1) {
                        return true;
                    }
                };
            }

            @Override
            public LoggerContext getContext(String arg0, ClassLoader arg1, Object arg2, boolean arg3, URI arg4, String arg5) {
                return getContext(arg0, arg1, arg2, arg3);
            }

            @Override
            public void removeContext(LoggerContext arg0) {
            }
        });
    }
}
