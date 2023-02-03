package com.bergerkiller.bukkit.common.internal;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.bukkit.common.conversion.type.DimensionResourceKeyConversion;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.ItemSlotConversion;
import com.bergerkiller.bukkit.common.conversion.type.MC1_17_Conversion;
import com.bergerkiller.bukkit.common.conversion.type.MC1_18_2_Conversion;
import com.bergerkiller.bukkit.common.conversion.type.MC1_8_8_Conversion;
import com.bergerkiller.bukkit.common.conversion.type.NBTConversion;
import com.bergerkiller.bukkit.common.conversion.type.PropertyConverter;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.internal.logging.CommonLog4jTestLogging;
import com.bergerkiller.bukkit.common.internal.logic.NullPacketDataSerializerInit;
import com.bergerkiller.bukkit.common.server.*;
import com.bergerkiller.bukkit.common.server.CommonServer.PostInitEvent;
import com.bergerkiller.bukkit.common.server.test.TestServerFactory;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.nbt.NBTBaseHandle;
import com.bergerkiller.generated.net.minecraft.nbt.NBTTagCompoundHandle;
import com.bergerkiller.generated.net.minecraft.nbt.NBTTagListHandle;
import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.conversion.Conversion;
import com.bergerkiller.mountiplex.reflection.resolver.ClassPathResolver;
import com.bergerkiller.mountiplex.reflection.resolver.CompiledFieldNameResolver;
import com.bergerkiller.mountiplex.reflection.resolver.CompiledMethodNameResolver;
import com.bergerkiller.mountiplex.reflection.resolver.FieldAliasResolver;
import com.bergerkiller.mountiplex.reflection.resolver.FieldNameResolver;
import com.bergerkiller.mountiplex.reflection.resolver.MethodAliasResolver;
import com.bergerkiller.mountiplex.reflection.resolver.MethodNameResolver;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
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
    private static boolean _isPaperServer = false;
    private static boolean _isPurpurServer = false;
    private static CommonServer _commonServer = null;
    private static boolean _isInitializingCommonServer = false;
    private static TemplateResolver _templateResolver;
    private static boolean _isCompatible = false;
    private static String _incompatibleReason = null;

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
     * Gets whether the server we are currently running on is a PaperMC server
     *
     * @return True if the current server is a PaperMC server
     */
    public static boolean isPaperServer() {
        initCommonServer();
        return _isPaperServer;
    }

    /**
     * Gets whether the server we are currently running on is a Purpur-based server
     *
     * @return True if the current server is a Purpur-based server
     */
    public static boolean isPurpurServer() {
        initCommonServer();
        return _isPurpurServer;
    }

    /**
     * Verifies that everything BKCommonLib needs is available in the jar right now.
     * If running an unshaded jar or the jar file was partially downloaded, this will
     * fail early and clearly to indicate so.
     *
     * @Param logger Logger to log any errors to
     * @return True if shaded assets are valid, false if not
     */
    public static boolean verifyShadedAssets(Logger logger) {
        // If MountiplexUtil isn't available, we're running an unshaded jar, and there's no hope of enabling
        try {
            Class.forName("com.bergerkiller.mountiplex.MountiplexUtil");
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "BKCommonLib jar lacks required shaded dependencies. Please redownload the correct jar!");
            logger.log(Level.SEVERE, "If your BKCommonLib.jar is less than 5 MB then you probably downloaded the wrong file.");
            logger.log(Level.SEVERE, "If using a FTP client, make sure the file is fully transferred to the server.");
            return false;
        }

        return true;
    }

    /**
     * Gets whether bootstrapping has completed and CommonServer/templates/resolvers/converters are initialized.
     *
     * @return True if common server is initialized
     */
    public static boolean isCommonServerInitialized() {
        return _commonServer != null && _isCompatible;
    }

    /**
     * Calls {@link #initCommonServer()} to detect the common server implementation
     * that is used, and returns whether that server is compatible or not. if an
     * exception is preferred, use {@link #initCommonServerAssertCompatibility()}
     * instead.
     *
     * @return True if compatible, False if not
     */
    public static boolean initCommonServerCheckCompatibility() {
        initCommonServer();
        return _isCompatible;
    }

    /**
     * Calls {@link #initCommonServer()} to detect the common server implementation
     * that is used, and then checks whether this version is compatible with this
     * library. Throws an exception if it is not.
     * 
     * @throw UnsupportedOperationException If the server is not supported
     */
    public static void initCommonServerAssertCompatibility() {
        if (!initCommonServerCheckCompatibility()) {
            throw new UnsupportedOperationException(_incompatibleReason);
        }
    }

    /**
     * Detects and returns the common server implementation that is used.
     * The result is cached.
     * 
     * @return common server
     */
    public static CommonServer initCommonServer() {
        if (_commonServer == null) {
            if (_isInitializingCommonServer) {
                throw new UnsupportedOperationException("CommonServer is already being initialized. Fix your code!");
            }

            // Now we know Mountiplex exists, start loading template stuff
            _templateResolver = new TemplateResolver();

            // Some common packages we KNOW for a fact are packages, and never ever should not be
            // If some plugin decides to include such a package name as a Class, we reject it.
            Resolver.getPackageNameCache().addDefaultPackage("net.minecraft.server")
                                          .addDefaultPackage("net.minecraft.core")
                                          .addDefaultPackage("org.bukkit.craftbukkit")
                                          .addDefaultPackage("com.mojang.authlib")
                                          .addDefaultPackage("org.spigotmc");

            // Get all available server types
            _isInitializingCommonServer = true;
            try {
                CommonServer server = null;
                if (isTestMode()) {
                    // Use our own logger to speed up initialization under test
                    CommonLog4jTestLogging.initLog4j();

                    // Always Spigot server
                    server = new SpigotServer();
                    if (!server.init()) {
                        server = null;
                    }
                } else {
                    // Autodetect most likely server type
                    List<CommonServer> servers = new ArrayList<>();
                    servers.add(new MohistServer());
                    servers.add(new MagmaServer());
                    servers.add(new MagmaServerLegacy());
                    servers.add(new ArclightServer());
                    servers.add(new ArclightServerLegacy());
                    servers.add(new CatServerServer());
                    servers.add(new Bukkit4FabricServer());
                    servers.add(new NachoSpigotServer());
                    servers.add(new PurpurServer());
                    servers.add(new SpigotServer());
                    servers.add(new SportBukkitServer());
                    servers.add(new CraftBukkitServer());

                    // Use the first one that initializes correctly
                    for (CommonServer potentialServer : servers) {
                        try {
                            if (potentialServer.init()) {
                                server = potentialServer;
                                break;
                            }
                        } catch (Throwable t) {
                            Logging.LOGGER.log(Level.SEVERE, "An error occurred during server type detection:", t);
                        }
                    }
                }

                // Fallback if none are detected
                if (server == null) {
                    server = new UnknownServer();
                    server.init();
                }

                // Fully initialize the server instance. This will go beyond detection,
                // and will identify internal server classes and check for compatibility
                PostInitEvent event = new PostInitEvent(_templateResolver);
                try {
                    initServerResolvers(server);
                    server.postInit(event);
                } catch (Throwable t) {
                    Logging.LOGGER.log(Level.SEVERE, "An error occurred during server bootstrapping:", t);
                    if (event.isCompatible()) {
                        event.signalIncompatible("Server bootstrapping failed: " + t.getMessage());
                    }

                    // Make sure 'something' is sort-of initialized at least
                    // If for whatever reason it is here that it goes wrong, silently
                    // suppress those errors. We don't care, we're already in a failure state!
                    server = new UnknownServer();
                    try {
                        server.init();
                        server.postInit(new PostInitEvent(_templateResolver));
                    } catch (Throwable suppressed) {}
                }

                // Assign updated state
                _commonServer = server;
                _isCompatible = event.isCompatible();
                _incompatibleReason = event.getIncompatibleReason();
                _isSpigotServer = (_commonServer instanceof SpigotServer);
                _isPaperServer = (_commonServer instanceof SpigotServer && ((SpigotServer) _commonServer).isPaperSpigot());
                _isPurpurServer = (_commonServer instanceof PurpurServer);
            } finally {
                _isInitializingCommonServer = false;
            }

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
        if (!initCommonServerCheckCompatibility()) {
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
     * the case.<br>
     * <br>
     * Also checks the server is compatible before proceeding
     */
    public static void initServer() {
        // Detects what server is being run on, the minecraft server version,
        // and initializes the type converters for that platform. No templates
        // are loaded, those are loaded when something actually requires it.
        initCommonServerAssertCompatibility();

        // If no server instance exists, this creates a dummy server instance
        // and installs it into the server. This is purely so that testing
        // against server classes can be done.
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
     * Registers any resolvers used by the current server handler
     * 
     * @param server
     */
    private static void initServerResolvers(CommonServer server) {
        // Register server to handle field, method and class resolving
        if (server instanceof ClassPathResolver) {
            Resolver.registerClassResolver((ClassPathResolver) server);
        }
        if (server instanceof FieldNameResolver) {
            Resolver.registerFieldResolver((FieldNameResolver) server);
        }
        if (server instanceof MethodNameResolver) {
            Resolver.registerMethodResolver((MethodNameResolver) server);
        }
        if (server instanceof CompiledFieldNameResolver) {
            Resolver.registerCompiledFieldResolver((CompiledFieldNameResolver) server);
        }
        if (server instanceof CompiledMethodNameResolver) {
            Resolver.registerCompiledMethodResolver((CompiledMethodNameResolver) server);
        }
        if (server instanceof FieldAliasResolver) {
            Resolver.registerFieldAliasResolver((FieldAliasResolver) server);
        }
        if (server instanceof MethodAliasResolver) {
            Resolver.registerMethodAliasResolver((MethodAliasResolver) server);
        }
    }

    /**
     * Initializes the type, field and method resolvers for a server, so that such information
     * can be obtained at runtime.
     * 
     * @param server
     */
    private static void initResolvers(CommonServer server) {
        final Map<String, String> remappings = new HashMap<String, String>();

        // We renamed EntityTrackerEntry to EntityTrackerEntryState to account for the wrapping EntityTracker on 1.14 and later
        remappings.put("net.minecraft.server.level.EntityTrackerEntryState", "net.minecraft.server.level.EntityTrackerEntry");

        // Instead of CraftBukkit LongHashSet, we use a custom implementation with bugfixes on 1.13.2 and earlier
        // This is now possible since we no longer interface with CraftBukkit LongHashSet anywhere
        remappings.put("com.bergerkiller.bukkit.common.internal.LongHashSet", "com.bergerkiller.bukkit.common.internal.proxy.LongHashSet_pre_1_13_2");
        remappings.put("com.bergerkiller.bukkit.common.internal.LongHashSet$LongIterator", "com.bergerkiller.bukkit.common.internal.proxy.LongHashSet_pre_1_13_2$LongIterator");

        // Since Minecraft 1.16.2 it has an entirely new Class, this makes the API look clean
        remappings.put("net.minecraft.world.level.biome.BiomeSettingsMobs$SpawnRate", "net.minecraft.world.level.biome.BiomeBase$BiomeMeta");
        remappings.put("net.minecraft.world.level.biome.BiomeSettingsMobs", "net.minecraft.world.level.biome.BiomeBase");

        // Obfuscated class name
        remappings.put("net.minecraft.server.level.ChunkProviderServer$MainThreadExecutor", "net.minecraft.server.level.ChunkProviderServer$a");

        // Before Minecraft 1.10 EnumGameMode sat inside WorldSettings
        if (evaluateMCVersion("<", "1.10.2")) {
            remappings.put("net.minecraft.world.level.EnumGamemode", "net.minecraft.world.level.WorldSettings$EnumGamemode");
        }

        // EnumChatVisibility moved from EntityHuman to package level during MC 1.14
        if (evaluateMCVersion("<", "1.14")) {
            remappings.put("net.minecraft.world.entity.player.EnumChatVisibility", "net.minecraft.world.entity.player.EntityHuman$EnumChatVisibility");
        }

        // Botched deobfuscation of class names on 1.8.8 / proxy missing classes to simplify API
        if (evaluateMCVersion("<", "1.9")) {
            remappings.put("net.minecraft.world.level.MobSpawnerData", "net.minecraft.world.level.MobSpawnerAbstract$a");
            remappings.put("net.minecraft.world.level.block.SoundEffectType", "net.minecraft.world.level.block.Block$StepSound"); // workaround
            remappings.put("net.minecraft.network.syncher.DataWatcher$Item", "net.minecraft.network.syncher.DataWatcher$WatchableObject");
            remappings.put("net.minecraft.network.syncher.DataWatcher$PackedItem", "net.minecraft.network.syncher.DataWatcher$WatchableObject");
            remappings.put("net.minecraft.server.level.PlayerChunk", "net.minecraft.server.level.PlayerChunkMap$PlayerChunk"); // nested on 1.8.8

            // PacketPlayInUseItem and PacketPlayInBlockPlace were merged as one packet on these versions
            remappings.put("net.minecraft.network.protocol.game.PacketPlayInUseItem", "net.minecraft.network.protocol.game.PacketPlayInBlockPlace");

            // We proxy a bunch of classes, because they don't exist in 1.8.8
            // Writing custom wrappers with switches would be too tiresome
            // This allows continued use of the same API without trouble
            // Converters take care to convert between the Class and Id used internally
            remappings.put("net.minecraft.world.entity.EnumItemSlot", "com.bergerkiller.bukkit.common.internal.proxy.EnumItemSlot");
            remappings.put("net.minecraft.world.level.chunk.DataPaletteBlock", "com.bergerkiller.bukkit.common.internal.proxy.DataPaletteBlock");
            remappings.put("net.minecraft.network.syncher.DataWatcherObject", "com.bergerkiller.bukkit.common.internal.proxy.DataWatcherObject");
            remappings.put("net.minecraft.sounds.SoundEffect", "com.bergerkiller.bukkit.common.internal.proxy.SoundEffect_1_8_8");
            remappings.put("net.minecraft.world.level.dimension.DimensionManager", "com.bergerkiller.bukkit.common.internal.proxy.DimensionManager_1_8_8");

            // This one MIGHT exist on some server types (weird!)
            try {
                Class.forName(server.getNMSRoot() + ".MobEffectList");
            } catch (ClassNotFoundException e) {
                remappings.put("net.minecraft.world.effect.MobEffectList", "com.bergerkiller.bukkit.common.internal.proxy.MobEffectList");
            }
        }

        // On version 1.8, for some reason all child classes were on package level
        if (evaluateMCVersion("<=", "1.8")) {
            remappings.put("net.minecraft.world.level.block.state.BlockStateList$BlockData", "net.minecraft.world.level.block.state.BlockData");
            remappings.put("net.minecraft.world.level.EnumGamemode", "net.minecraft.world.level.EnumGamemode");
            remappings.put("net.minecraft.world.level.block.SoundEffectType", "net.minecraft.world.level.block.StepSound");
            remappings.put("net.minecraft.world.level.block.Block$StepSound", "net.minecraft.world.level.block.StepSound");
            remappings.put("net.minecraft.core.EnumDirection$EnumAxis", "net.minecraft.core.EnumAxis");
            remappings.put("net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket$EnumPlayerInfoAction", "net.minecraft.network.protocol.game.EnumPlayerInfoAction");
            remappings.put("net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket$PlayerInfoData", "net.minecraft.network.protocol.game.PlayerInfoData");
            remappings.put("net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket$EnumPlayerInfoAction", "net.minecraft.network.protocol.game.EnumPlayerInfoAction");
            remappings.put("net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket$PlayerInfoData", "net.minecraft.network.protocol.game.PlayerInfoData");
            remappings.put("net.minecraft.network.protocol.game.PacketPlayInUseEntity$EnumEntityUseAction", "net.minecraft.network.protocol.game.EnumEntityUseAction");
            remappings.put("net.minecraft.world.level.MobSpawnerData", "net.minecraft.world.level.block.entity.TileEntityMobSpawnerData");
            remappings.put("net.minecraft.network.syncher.DataWatcher$Item", "net.minecraft.network.syncher.WatchableObject");
            remappings.put("net.minecraft.network.syncher.DataWatcher$PackedItem", "net.minecraft.network.syncher.WatchableObject");
            remappings.put("net.minecraft.network.syncher.DataWatcher$WatchableObject", "net.minecraft.network.syncher.WatchableObject");
            remappings.put("net.minecraft.network.protocol.game.PacketPlayOutScoreboardScore$EnumScoreboardAction", "net.minecraft.network.protocol.game.EnumScoreboardAction");
            remappings.put("net.minecraft.network.protocol.game.PacketPlayOutMapChunk$ChunkMap", "net.minecraft.network.protocol.game.ChunkMap");
            remappings.put("net.minecraft.network.protocol.game.PacketPlayOutPosition$EnumPlayerTeleportFlags", "net.minecraft.network.protocol.game.EnumPlayerTeleportFlags");
            remappings.put("net.minecraft.network.protocol.game.PacketPlayOutTitle$EnumTitleAction", "net.minecraft.network.protocol.game.EnumTitleAction");
            remappings.put("net.minecraft.network.protocol.game.PacketPlayOutCombatEvent$EnumCombatEventType", "net.minecraft.network.protocol.game.EnumCombatEventType");
            remappings.put("net.minecraft.network.protocol.game.PacketPlayOutWorldBorder$EnumWorldBorderAction", "net.minecraft.network.protocol.game.EnumWorldBorderAction");
            remappings.put("net.minecraft.network.protocol.game.PacketPlayInResourcePackStatus$EnumResourcePackStatus", "net.minecraft.network.protocol.game.EnumResourcePackStatus");
            remappings.put("net.minecraft.network.protocol.game.PacketPlayInBlockDig$EnumPlayerDigType", "net.minecraft.network.protocol.game.EnumPlayerDigType");
            remappings.put("net.minecraft.world.entity.player.EnumChatVisibility", "net.minecraft.world.entity.player.EnumChatVisibility");
            remappings.put("net.minecraft.server.level.PlayerChunk", "net.minecraft.server.level.PlayerChunk");
            remappings.put("net.minecraft.util.WeightedRandom$WeightedRandomChoice", "net.minecraft.util.WeightedRandomChoice");
            remappings.put("net.minecraft.world.level.biome.BiomeSettingsMobs$SpawnRate", "net.minecraft.world.level.biome.BiomeMeta");
            remappings.put("net.minecraft.util.IntHashMap$IntHashMapEntry", "net.minecraft.util.IntHashMapEntry");
            remappings.put("net.minecraft.network.protocol.game.PacketPlayOutEntity$PacketPlayOutEntityLook", "net.minecraft.network.protocol.game.PacketPlayOutEntityLook");
            remappings.put("net.minecraft.network.protocol.game.PacketPlayOutEntity$PacketPlayOutRelEntityMove", "net.minecraft.network.protocol.game.PacketPlayOutRelEntityMove");
            remappings.put("net.minecraft.network.protocol.game.PacketPlayOutEntity$PacketPlayOutRelEntityMoveLook", "net.minecraft.network.protocol.game.PacketPlayOutRelEntityMoveLook");
            remappings.put("net.minecraft.network.protocol.game.PacketPlayInFlying$PacketPlayInLook", "net.minecraft.network.protocol.game.PacketPlayInLook");
            remappings.put("net.minecraft.network.protocol.game.PacketPlayInFlying$PacketPlayInPosition", "net.minecraft.network.protocol.game.PacketPlayInPosition");
            remappings.put("net.minecraft.network.protocol.game.PacketPlayInFlying$PacketPlayInPositionLook", "net.minecraft.network.protocol.game.PacketPlayInPositionLook");
            remappings.put("net.minecraft.network.chat.IChatBaseComponent$ChatSerializer", "net.minecraft.network.chat.ChatSerializer");
            remappings.put("net.minecraft.network.NetworkManager$QueuedPacket", "net.minecraft.network.QueuedPacket");
            remappings.put("net.minecraft.network.protocol.game.PacketPlayOutPosition$EnumPlayerTeleportFlags", "net.minecraft.network.protocol.game.EnumPlayerTeleportFlags");
            remappings.put("net.minecraft.network.chat.ChatClickable$EnumClickAction", "net.minecraft.network.chat.EnumClickAction");
            remappings.put("net.minecraft.network.chat.ChatHoverable$EnumHoverAction", "net.minecraft.network.chat.EnumHoverAction");
            remappings.put("net.minecraft.network.protocol.game.PacketPlayInClientCommand$EnumClientCommand", "net.minecraft.network.protocol.game.EnumClientCommand");
            remappings.put("net.minecraft.network.protocol.game.PacketPlayInEntityAction$EnumPlayerAction", "net.minecraft.network.protocol.game.EnumPlayerAction");
            remappings.put("net.minecraft.world.scores.criteria.IScoreboardCriteria$EnumScoreboardHealthDisplay", "net.minecraft.world.scores.criteria.EnumScoreboardHealthDisplay");
        }

        // Proxy classes that were added in 1.13 so that 1.12.2 and before works with the same API
        if (evaluateMCVersion("<", "1.13")) {
            remappings.put("net.minecraft.world.level.levelgen.HeightMap", "com.bergerkiller.bukkit.common.internal.proxy.HeightMapProxy_1_12_2");
            remappings.put("net.minecraft.world.level.levelgen.HeightMap$Type", "com.bergerkiller.bukkit.common.internal.proxy.HeightMapProxy_1_12_2$Type");
            remappings.put("com.bergerkiller.bukkit.common.internal.proxy.HeightMap.Type", "com.bergerkiller.bukkit.common.internal.proxy.HeightMapProxy_1_12_2$Type");
            remappings.put("net.minecraft.world.phys.shapes.VoxelShape", "com.bergerkiller.bukkit.common.internal.proxy.VoxelShapeProxy");
            remappings.put("net.minecraft.world.level.block.entity.TileEntityTypes", "com.bergerkiller.bukkit.common.internal.proxy.TileEntityTypesProxy_1_8_to_1_12_2");
        }

        // EnumArt has seen many places...
        if (evaluateMCVersion("<=", "1.8")) {
            remappings.put("net.minecraft.world.entity.decoration.Paintings", "net.minecraft.world.entity.decoration.EnumArt");
        } else if (evaluateMCVersion("<", "1.13")) {
            remappings.put("net.minecraft.world.entity.decoration.Paintings", "net.minecraft.world.entity.decoration.EntityPainting$EnumArt");
        } else {
            // Located at net.minecraft.world.entity.decoration.Paintings like normal.
        }

        // Still obfuscated on these versions of MC
        if (evaluateMCVersion(">=", "1.14") && evaluateMCVersion("<=", "1.14.1")) {
            remappings.put("net.minecraft.world.level.block.state.pattern.ShapeDetector$Shape", "net.minecraft.world.level.block.state.pattern.ShapeDetector$c");
        }

        // EnumScoreboardAction was moved to a ScoreboardServer class during 1.13
        // There is now a ParticleType class. For 1.12.2 and before, we refer to EnumParticle instead to simplify API
        if (evaluateMCVersion(">=", "1.13")) {
            remappings.put("net.minecraft.network.protocol.game.PacketPlayOutScoreboardScore$EnumScoreboardAction", "net.minecraft.server.ScoreboardServer$Action");
        } else {
            remappings.put("net.minecraft.core.particles.Particle", "net.minecraft.core.particles.EnumParticle");
            remappings.put("net.minecraft.core.particles.Particles", "net.minecraft.core.particles.EnumParticle");
            remappings.put("net.minecraft.core.particles.ParticleType", "net.minecraft.core.particles.EnumParticle");
        }

        // Many classes disappeared, merged or moved with MC 1.14
        if (evaluateMCVersion(">=", "1.14")) {
            String unimi_fastutil_path = "org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.";
            try {
                MPLType.getClassByName(unimi_fastutil_path + "longs.LongSet");

                // Fixes hardcoded fastutil paths used in templates
                for (String fastutilClass : new String[] {
                        "ints.Int2ObjectMap",
                        "ints.Int2ObjectOpenHashMap",
                        "ints.IntList",
                        "ints.IntLists",
                        "ints.IntArrayList",
                        "longs.Long2ObjectMap",
                        "longs.Long2ObjectOpenHashMap",
                        "longs.Long2ObjectLinkedOpenHashMap",
                        "longs.Long2IntOpenHashMap",
                        "longs.LongIterator",
                        "longs.LongLinkedOpenHashSet",
                        "longs.LongOpenHashSet",
                        "longs.LongSet",
                        "longs.LongSortedSet",
                        "longs.LongBidirectionalIterator",
                        "objects.Object2IntMap",
                        "objects.ObjectCollection"
                }) {
                    remappings.put("it.unimi.dsi.fastutil." + fastutilClass, unimi_fastutil_path + fastutilClass);
                }
            } catch (ClassNotFoundException ex) {
                unimi_fastutil_path = "it.unimi.dsi.fastutil.";
            }

            remappings.put("com.bergerkiller.bukkit.common.internal.LongHashSet", unimi_fastutil_path + "longs.LongSet");
            remappings.put("org.bukkit.craftbukkit.util.LongObjectHashMap", unimi_fastutil_path + "longs.Long2ObjectMap");
            remappings.put("net.minecraft.util.IntHashMap", unimi_fastutil_path + "ints.Int2ObjectMap");
            remappings.put("net.minecraft.util.IntHashMap$IntHashMapEntry", unimi_fastutil_path + "ints.Int2ObjectMap$Entry");
            remappings.put(unimi_fastutil_path + "ints.IntHashMap$IntHashMapEntry", unimi_fastutil_path + "ints.Int2ObjectMap$Entry");
            remappings.put("net.minecraft.server.level.EntityTracker", "net.minecraft.server.level.PlayerChunkMap");
            remappings.put("net.minecraft.server.level.EntityTrackerEntry", "net.minecraft.server.level.PlayerChunkMap$EntityTracker");
        }

        // Remaps CraftLegacy from legacy to util (moved since 1.15.2)
        {
            boolean craftLegacyIsInUtil;
            if (evaluateMCVersion("<", "1.15.2")) {
                craftLegacyIsInUtil = true;
            } else if (evaluateMCVersion("==", "1.15.2")) {
                try {
                    Class.forName(server.getCBRoot() + ".legacy.CraftLegacy");
                    craftLegacyIsInUtil = false;
                } catch (Throwable t) {
                    craftLegacyIsInUtil = true;
                }
            } else {
                craftLegacyIsInUtil = false;
            }
            if (craftLegacyIsInUtil) {
                remappings.put("org.bukkit.craftbukkit.legacy.CraftLegacy", "org.bukkit.craftbukkit.util.CraftLegacy");
            }
        }

        // Maps nms ResourceKey to the internal proxy class replacement pre-1.16
        if (evaluateMCVersion("<", "1.16")) {
            remappings.put("net.minecraft.resources.ResourceKey", "com.bergerkiller.bukkit.common.internal.proxy.ResourceKey_1_15_2");
        }

        // WorldData was changed at 1.16 to WorldDataServer, with WorldData now being an interface with bare properties both server and client contain
        if (evaluateMCVersion("<", "1.16")) {
            remappings.put("net.minecraft.world.level.storage.WorldDataServer", "net.minecraft.world.level.storage.WorldData");
        }

        // BiomeBase.BiomeMeta was removed and replaced with BiomeSettingsMobs.c
        // Assume a more human-readable name and remap the name prior to the right place
        if (evaluateMCVersion(">=", "1.16.2")) {
            remappings.put("net.minecraft.world.level.biome.BiomeSettingsMobs$SpawnRate", "net.minecraft.world.level.biome.BiomeSettingsMobs$c");
            remappings.put("net.minecraft.world.level.biome.BiomeSettingsMobs", "net.minecraft.world.level.biome.BiomeSettingsMobs");
        }

        // 1.17 mappings
        if (evaluateMCVersion(">=", "1.17")) {
            remappings.put("net.minecraft.network.protocol.game.PacketPlayInUseEntity$EnumEntityUseAction", "net.minecraft.network.protocol.game.PacketPlayInUseEntity$b");
            remappings.put("net.minecraft.network.protocol.game.PacketPlayInUseEntity$UseType", "net.minecraft.network.protocol.game.PacketPlayInUseEntity$EnumEntityUseAction");
            remappings.put("net.minecraft.world.level.saveddata.maps.WorldMap$PatchData", "net.minecraft.world.level.saveddata.maps.WorldMap$b");
        }

        // 1.18 mappings
        if (evaluateMCVersion(">=", "1.18")) {
            // Some class names still obfuscated
            remappings.put("net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData$BlockEntityData", "net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData$a");

            // PacketPlayOutMapChunk was replaced by a different packet which stores light and block data
            // To simplify the BKCL API we use the same packet class, since in both cases the buffer, heightmap data
            // and changed block state information is available. We handle the adaptering in template code.
            remappings.put("net.minecraft.network.protocol.game.PacketPlayOutMapChunk", "net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket");

            // Obfuscated class name changed
            remappings.put("net.minecraft.server.level.ChunkProviderServer$MainThreadExecutor", "net.minecraft.server.level.ChunkProviderServer$b");
        } else {
            // TickListServer was moved, migrate past versions
            remappings.put("net.minecraft.world.ticks.TickListServer", "net.minecraft.world.level.TickListServer");
            remappings.put("net.minecraft.world.ticks.TickList", "net.minecraft.world.level.TickList");
        }

        // 1.19 mappings
        if (evaluateMCVersion(">=", "1.19")) {
            // Painting / living entity spawn packets were merged into one
            remappings.put("net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLiving", "net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity");
            remappings.put("net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityPainting", "net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity");
        } else {
            // Uses a normal java.util.Random on this version
            remappings.put("net.minecraft.util.RandomSource", "java.util.Random");
        }

        // 1.19.3 mappings - some classes were split out into their own places
        if (evaluateMCVersion(">=", "1.19.3")) {
            // PackedItem class lacks naming because spigot is poop
            remappings.put("net.minecraft.network.syncher.DataWatcher$PackedItem", "net.minecraft.network.syncher.DataWatcher$b");
            // Spigot decided to revert back to mojangs name here for some reason
            remappings.put("net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket", "net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket");
            remappings.put("net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket$EnumPlayerInfoAction", "net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket$a");
            remappings.put("net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket$PlayerInfoData", "net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket$b");
        } else {
            // BuiltInRegistries class does not exist, but all relevant fields are found in IRegistry instead
            remappings.put("net.minecraft.core.registries.BuiltInRegistries", "net.minecraft.core.IRegistry");
            // PackedItem class same as Item class on older versions. Watch out for double-remapping!
            remappings.putIfAbsent("net.minecraft.network.syncher.DataWatcher$PackedItem", "net.minecraft.network.syncher.DataWatcher$Item");
            // Remap to spigots names. Removing and updating player information was handled by the same packet here.
            remappings.put("net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket", "net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo");
            remappings.putIfAbsent("net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket$EnumPlayerInfoAction", "net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo$EnumPlayerInfoAction");
            remappings.putIfAbsent("net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket$PlayerInfoData", "net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo$PlayerInfoData");
            remappings.put("net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket", "net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo");
            remappings.putIfAbsent("net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket$EnumPlayerInfoAction", "net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo$EnumPlayerInfoAction");
            remappings.putIfAbsent("net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket$PlayerInfoData", "net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo$PlayerInfoData");
        }

        // There have been various locations where starlight was installed
        // This was also part of tuinity at some point, but was then ported into paper
        {
            String defaultNamespace = "ca.spottedleaf.starlight.common.light."; // Also used in templates
            String[] starlightNamespaces = new String[] {
                    defaultNamespace,
                    "ca.spottedleaf.starlight.light.",
                    "com.tuinity.tuinity.chunk.light."
            };
            for (String namespace : starlightNamespaces) {
                boolean exists = false;
                try {
                    MPLType.getClassByName(namespace + "StarLightEngine");
                    exists = true;
                } catch (ClassNotFoundException ex) {}
                if (exists) {
                    if (!namespace.equals(defaultNamespace)) {
                        // Remap
                        for (String name : new String[] {
                                "SWMRNibbleArray", "StarLightInterface", "StarLightEngine",
                                "SkyStarLightEngine", "BlockStarLightEngine"
                        }) {
                            remappings.put(defaultNamespace + name, namespace + name);
                        }
                    }
                    break;
                }
            }
        }

        // Various namespaces for purpur
        {
            String defaultNamespace = "org.purpurmc.purpur.";
            String[] purpurNamespaces = new String[] {
                    defaultNamespace,
                    "net.pl3x.purpur."
            };
            for (String namespace : purpurNamespaces) {
                boolean exists = false;
                try {
                    MPLType.getClassByName(namespace + "PurpurConfig");
                    exists = true;
                } catch (ClassNotFoundException ex) {}
                if (exists) {
                    if (!namespace.equals(defaultNamespace)) {
                        // Remap
                        for (String name : new String[] {
                                "PurpurConfig", "PurpurWorldConfig"
                        }) {
                            remappings.put(defaultNamespace + name, namespace + name);
                        }
                    }
                    break;
                }
            }
        }

        // If remappings exist, add a resolver for them
        if (!remappings.isEmpty()) {
            if (server instanceof CraftBukkitServer) {
                // Perform early remappings so that servers such as Mohist don't get super confused
                ((CraftBukkitServer) server).setEarlyRemappings(remappings);
            } else {
                // Add an extra class resolver to do it in
                Resolver.registerClassResolver(classPath -> {
                    String remapped = remappings.get(classPath);
                    return (remapped != null) ? remapped : classPath;
                });
            }
        }

        // Initialize this one right away, as it's used in generated code
        NullPacketDataSerializerInit.initialize();

        // Register converters
        Conversion.registerConverters(WrapperConversion.class);
        Conversion.registerConverters(HandleConversion.class);
        Conversion.registerConverters(NBTConversion.class);

        // EquipmentSlot <> EnumItemSlot, only for later version of 1.8 builds
        {
            boolean hasEquipmentSlotClass = false;
            try {
                Class.forName("org.bukkit.inventory.EquipmentSlot");
                hasEquipmentSlotClass = true;
            } catch (ClassNotFoundException ex) { /* not supported */ }

            if (hasEquipmentSlotClass) {
                Conversion.registerConverters(ItemSlotConversion.class);
            }
        }
        if (evaluateMCVersion("<", "1.9")) {
            Conversion.registerConverters(MC1_8_8_Conversion.class);
        }
        if (evaluateMCVersion(">=", "1.17")) {
            MC1_17_Conversion.init();
            Conversion.registerConverters(MC1_17_Conversion.class);
        }
        if ((evaluateMCVersion(">=", "1.16") && evaluateMCVersion("<=", "1.16.1")) || evaluateMCVersion(">=", "1.19")) {
            try {
                DimensionResourceKeyConversion.init();
                Conversion.registerConverters(DimensionResourceKeyConversion.class);
            } catch (Throwable t) {
                Logging.LOGGER_REFLECTION.log(Level.SEVERE,
                        "Failed to register ResourceKey<>DimensionManager converter", t);
            }
        }
        if (evaluateMCVersion(">=", "1.18.2")) {
            MC1_18_2_Conversion.init();
            Conversion.registerConverters(MC1_18_2_Conversion.class);
        }

        // Initialize the 'Conversion' classes right after to catch errors happening here
        CommonUtil.getClass(Conversion.class.getName(), true);
        CommonUtil.getClass(PropertyConverter.class.getName(), true);
        CommonUtil.getClass(DuplexConversion.class.getName(), true);
    }

    /**
     * Some components of this library may end up being used while the library
     * is (partially) disabled. To avoid errors trying to load classes while
     * the jar file is closed, load these components.
     */
    public static void preloadCriticalComponents() {
        NBTBaseHandle.NBTTagByteArrayHandle.T.forceInitialization();
        NBTBaseHandle.NBTTagByteHandle.T.forceInitialization();
        NBTBaseHandle.NBTTagDoubleHandle.T.forceInitialization();
        NBTBaseHandle.NBTTagFloatHandle.T.forceInitialization();
        NBTBaseHandle.NBTTagIntArrayHandle.T.forceInitialization();
        NBTBaseHandle.NBTTagIntHandle.T.forceInitialization();
        if (evaluateMCVersion(">=", "1.12")) {
            NBTBaseHandle.NBTTagLongArrayHandle.T.forceInitialization();
        }
        NBTBaseHandle.NBTTagLongHandle.T.forceInitialization();
        NBTBaseHandle.NBTTagShortHandle.T.forceInitialization();
        NBTBaseHandle.NBTTagStringHandle.T.forceInitialization();
        NBTTagCompoundHandle.T.forceInitialization();
        NBTTagListHandle.T.forceInitialization();
    }

    /**
     * Gets whether this is a headless JDK that doesn't contain the Java AWT library
     *
     * @return True if java.awt is not available
     */
    public static boolean isHeadlessJDK() {
        try {
            Class.forName("java.awt.Color");
            return false;
        } catch (ClassNotFoundException ex) {
            return true;
        }
    }
}
