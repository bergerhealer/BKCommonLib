package com.bergerkiller.bukkit.common.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.bergerkiller.bukkit.common.conversion.type.EntityPoseConversion;
import com.bergerkiller.bukkit.common.conversion.type.JOMLConversion;
import com.bergerkiller.bukkit.common.conversion.type.MapConversion;
import com.bergerkiller.bukkit.common.conversion.type.ScoreboardDisplaySlotConversion;
import com.bergerkiller.bukkit.common.conversion.type.SerializedEnumConversion;
import com.bergerkiller.bukkit.common.entity.CommonEntityType;
import com.bergerkiller.bukkit.common.internal.logic.EmptyBlockGetterInit;
import com.bergerkiller.bukkit.common.internal.logic.ScopedProblemReporterInit;
import com.bergerkiller.bukkit.common.internal.logic.UnsetDataWatcherItemInit;
import com.bergerkiller.bukkit.common.wrappers.Brightness;
import com.bergerkiller.bukkit.common.wrappers.ItemDisplayMode;
import com.bergerkiller.bukkit.common.wrappers.RelativeFlags;
import com.bergerkiller.generated.net.minecraft.nbt.CompoundTagHandle;
import com.bergerkiller.generated.net.minecraft.nbt.ListTagHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
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
import com.bergerkiller.generated.net.minecraft.nbt.TagHandle;
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
     * @param logger Logger to log any errors to
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
     * @throws UnsupportedOperationException If the server is not supported
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
                    servers.add(new UniverseServer());
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
                _isPaperServer = (_commonServer instanceof SpigotServer && ((SpigotServer) _commonServer).isPaperServer());
                _isPurpurServer = (_commonServer instanceof PurpurServer);
            } finally {
                _isInitializingCommonServer = false;
            }

            // Make type and method information available for this server type
            // Templates should not be initialized during this time, bad things happen
            if (_isCompatible) {
                boolean oldWarnTemplates = WARN_WHEN_INIT_TEMPLATES;
                WARN_WHEN_INIT_TEMPLATES = true;
                initResolvers(_commonServer);
                WARN_WHEN_INIT_TEMPLATES = oldWarnTemplates;
            }
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
            // Also include the Java version so it's clear what runtime the tests are executed on
            Logging.LOGGER.log(Level.INFO, "Test running on " + Common.SERVER.getServerDetails()
                    + " (Java " + System.getProperty("java.version") + ")");
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

        // Custom static remappings that are BKCommonLib-specific used on the latest version of Minecraft

        // We renamed ServerEntity to EntityTrackerEntryState to account for the wrapping EntityTracker on 1.14 and later
        remappings.put("net.minecraft.server.level.EntityTracker", "net.minecraft.server.level.ChunkMap");
        remappings.put("net.minecraft.server.level.EntityTrackerEntry", "net.minecraft.server.level.ChunkMap$TrackedEntity");
        remappings.put("net.minecraft.server.level.EntityTrackerEntryState", "net.minecraft.server.level.ServerEntity");

        // Instead of CraftBukkit LongHashSet, we use a custom implementation with bugfixes on 1.13.2 and earlier
        // This is now possible since we no longer interface with CraftBukkit LongHashSet anywhere
        remappings.put("com.bergerkiller.bukkit.common.internal.LongHashSet", "com.bergerkiller.bukkit.common.internal.proxy.LongHashSet_pre_1_13_2");
        remappings.put("com.bergerkiller.bukkit.common.internal.LongHashSet$LongIterator", "com.bergerkiller.bukkit.common.internal.proxy.LongHashSet_pre_1_13_2$LongIterator");

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
                        "objects.ObjectCollection",
                        "objects.ObjectIterator"
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
        }

        // Remaps CraftLegacy from legacy to util (moved since 1.15.2)
        {
            boolean craftLegacyIsInUtil;
            if (evaluateMCVersion("<", "1.15.2")) {
                craftLegacyIsInUtil = true;
            } else if (evaluateMCVersion("==", "1.15.2")) {
                try {
                    Class.forName(server.getCBRoot() + ".legacy.CraftLegacy", false, CommonBootstrap.class.getClassLoader());
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

        // There have been various locations where starlight was installed
        // This was also part of tuinity at some point, but was then ported into paper
        {
            String defaultNamespace = "ca.spottedleaf.moonrise.patches.starlight.light."; // Also used in templates
            String[] starlightNamespaces = new String[] {
                    defaultNamespace,
                    "ca.spottedleaf.starlight.common.light.",
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

        /*
        ****************************************************************************************************************
        * Below are changes to the Mojang-mapped class names in reverse order
        * Each acts as a migration to use the newest mojang-mapped class name on older Minecraft versions
        * In cases where we do not know the mojang-mapped name at all (pre-1.17), this is typically instead handled by
        * the mojang <> spigot class mappings (class_mappings.dat) since Mojang Mapping wasn't in practical use.
        ****************************************************************************************************************
        */

        /* ======== Mojang remapping changes for 1.21.11 ======== */
        if (evaluateMCVersion("<", "1.21.11")) {
            // A lot of classes were moved to new packages. These remap it back to the old path for older versions.

            // Identifier moved, though spigot still maps it to the same location. Definitely moved on 26.1+
            remappings.put("net.minecraft.resources.Identifier", "net.minecraft.resources.ResourceLocation");
            remappings.put("net.minecraft.IdentifierException", "net.minecraft.ResourceLocationException");
            remappings.put("net.minecraft.commands.arguments.IdentifierArgument", "net.minecraft.commands.arguments.ResourceLocationArgument");

            // Util (SystemUtils) was moved to a util sub-package
            remappings.put("net.minecraft.util.Util", "net.minecraft.Util");

            // Paintings were moved to a painting sub-package
            remappings.put("net.minecraft.world.entity.decoration.painting.PaintingVariants", "net.minecraft.world.entity.decoration.PaintingVariants");
            remappings.put("net.minecraft.world.entity.decoration.painting.PaintingVariant", "net.minecraft.world.entity.decoration.PaintingVariant");
            remappings.put("net.minecraft.world.entity.decoration.painting.Painting", "net.minecraft.world.entity.decoration.Painting");

            // Arrows were moved to an arrow sub-package
            remappings.put("net.minecraft.world.entity.projectile.arrow.ThrownTrident", "net.minecraft.world.entity.projectile.ThrownTrident");
            remappings.put("net.minecraft.world.entity.projectile.arrow.SpectralArrow", "net.minecraft.world.entity.projectile.SpectralArrow");
            remappings.put("net.minecraft.world.entity.projectile.arrow.Arrow", "net.minecraft.world.entity.projectile.Arrow");
            remappings.put("net.minecraft.world.entity.projectile.arrow.AbstractArrow", "net.minecraft.world.entity.projectile.AbstractArrow");

            // Hurting projectiles were moved to a hurtingprojectile sub-package
            remappings.put("net.minecraft.world.entity.projectile.hurtingprojectile.WitherSkull", "net.minecraft.world.entity.projectile.WitherSkull");
            remappings.put("net.minecraft.world.entity.projectile.hurtingprojectile.SmallFireball", "net.minecraft.world.entity.projectile.SmallFireball");
            remappings.put("net.minecraft.world.entity.projectile.hurtingprojectile.LargeFireball", "net.minecraft.world.entity.projectile.LargeFireball");
            remappings.put("net.minecraft.world.entity.projectile.hurtingprojectile.Fireball", "net.minecraft.world.entity.projectile.Fireball");
            remappings.put("net.minecraft.world.entity.projectile.hurtingprojectile.DragonFireball", "net.minecraft.world.entity.projectile.DragonFireball");
            remappings.put("net.minecraft.world.entity.projectile.hurtingprojectile.AbstractHurtingProjectile", "net.minecraft.world.entity.projectile.AbstractHurtingProjectile");

            // Boat classes were moved to a boat sub-package
            remappings.put("net.minecraft.world.entity.vehicle.boat.Boat", "net.minecraft.world.entity.vehicle.Boat");
            remappings.put("net.minecraft.world.entity.vehicle.boat.ChestBoat", "net.minecraft.world.entity.vehicle.ChestBoat");
            remappings.put("net.minecraft.world.entity.vehicle.boat.AbstractChestBoat", "net.minecraft.world.entity.vehicle.AbstractChestBoat");
            remappings.put("net.minecraft.world.entity.vehicle.boat.AbstractBoat", "net.minecraft.world.entity.vehicle.AbstractBoat");

            // Minecart classes were moved to a minecart sub-package
            remappings.put("net.minecraft.world.entity.vehicle.minecart.MinecartBehavior", "net.minecraft.world.entity.vehicle.MinecartBehavior");
            remappings.put("net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior", "net.minecraft.world.entity.vehicle.NewMinecartBehavior");
            remappings.put("net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior$MinecartStep", "net.minecraft.world.entity.vehicle.NewMinecartBehavior$MinecartStep");
            remappings.put("net.minecraft.world.entity.vehicle.minecart.OldMinecartBehavior", "net.minecraft.world.entity.vehicle.OldMinecartBehavior");
            remappings.put("net.minecraft.world.entity.vehicle.minecart.AbstractMinecart", "net.minecraft.world.entity.vehicle.AbstractMinecart");
            remappings.put("net.minecraft.world.entity.vehicle.minecart.AbstractMinecartContainer", "net.minecraft.world.entity.vehicle.AbstractMinecartContainer");
            remappings.put("net.minecraft.world.entity.vehicle.minecart.Minecart", "net.minecraft.world.entity.vehicle.Minecart");
            remappings.put("net.minecraft.world.entity.vehicle.minecart.MinecartTNT", "net.minecraft.world.entity.vehicle.MinecartTNT");
            remappings.put("net.minecraft.world.entity.vehicle.minecart.MinecartCommandBlock", "net.minecraft.world.entity.vehicle.MinecartCommandBlock");
            remappings.put("net.minecraft.world.entity.vehicle.minecart.MinecartSpawner", "net.minecraft.world.entity.vehicle.MinecartSpawner");
            remappings.put("net.minecraft.world.entity.vehicle.minecart.MinecartHopper", "net.minecraft.world.entity.vehicle.MinecartHopper");
            remappings.put("net.minecraft.world.entity.vehicle.minecart.MinecartFurnace", "net.minecraft.world.entity.vehicle.MinecartFurnace");
            remappings.put("net.minecraft.world.entity.vehicle.minecart.MinecartChest", "net.minecraft.world.entity.vehicle.MinecartChest");
        }

        /* ======== Mojang remapping changes for 1.21.6 ======== */
        if (evaluateMCVersion("<", "1.21.6")) {
            // On versions before ValueOutput/Input existed, pretend they use CompoundTag (NBTTagCompound) instead to keep things simple
            remappings.put("net.minecraft.world.level.storage.ValueOutput", "net.minecraft.nbt.CompoundTag");
            remappings.put("net.minecraft.world.level.storage.ValueInput", "net.minecraft.nbt.CompoundTag");
            remappings.put("net.minecraft.world.level.storage.TagValueOutput", "net.minecraft.nbt.CompoundTag");
            remappings.put("net.minecraft.world.level.storage.TagValueInput", "net.minecraft.nbt.CompoundTag");
        }

        /* ======== Mojang remapping changes for 1.21.5 ======== */
        if (evaluateMCVersion("<", "1.21.5")) {
            // RespawnConfig record class was added in 1.21.5, use a proxy on versions prior to hold this data
            remappings.put("net.minecraft.server.level.ServerPlayer$RespawnConfig", "com.bergerkiller.bukkit.common.internal.proxy.PlayerRespawnConfig_pre_1_21_5");
        }

        /* ======== Mojang remapping changes for 1.21.2 ======== */
        if (evaluateMCVersion("<", "1.21.2")) {
            remappings.put("net.minecraft.world.entity.Relative", "net.minecraft.world.entity.RelativeMovement");

            // Before this version the ClientboundPlayerRotationPacket doesn't exist
            // It is emulated with the ClientboundPlayerPositionPacket (PacketPlayOutPosition) instead (with x/y/z 0.0 relative)
            remappings.put("net.minecraft.network.protocol.game.ClientboundPlayerRotationPacket", "net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket");

            // Before this version the ClientboundEntityPositionSyncPacket doesn't exist
            // It is emulated with the ClientboundTeleportEntityPacket (PacketPlayOutEntityTeleport) instead which behaves identically
            remappings.put("net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacket", "net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket");

            // Packet was renamed to ClientboundSetHeldSlotPacket
            remappings.put("net.minecraft.network.protocol.game.ClientboundSetHeldSlotPacket", "net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket");
        }

        /* ======== Mojang remapping changes for 1.20.5 ======== */
        if (evaluateMCVersion("<", "1.20.5")) {
            // ChunkStatus is now in a status package. Remap to it on older versions.
            remappings.put("net.minecraft.world.level.chunk.status.ChunkStatus", "net.minecraft.world.level.chunk.ChunkStatus");
            // SerializableMeta is now a standalone class, before it was a subclass of CraftMetaItem. Remap to it.
            remappings.put("org.bukkit.craftbukkit.inventory.SerializableMeta", "org.bukkit.craftbukkit.inventory.CraftMetaItem$SerializableMeta");
            // CustomModelData was introduced. Map to our proxy class on older versions.
            remappings.put("net.minecraft.world.item.component.CustomModelData", "com.bergerkiller.bukkit.common.internal.proxy.CustomModelData_pre_1_20_5");
            // MapDecorationType(s) was an enum subclass of MapDecoration instead of a registry
            remappings.put("net.minecraft.world.level.saveddata.maps.MapDecorationType", "net.minecraft.world.level.saveddata.maps.MapDecoration$Type");
            remappings.put("net.minecraft.world.level.saveddata.maps.MapDecorationTypes", "net.minecraft.world.level.saveddata.maps.MapDecoration$Type");
        }

        /* ======== Mojang remapping changes for 1.20.3 ======== */
        if (evaluateMCVersion("<", "1.20.3")) {
            // Before 1.20.3 the score reset was handled by the ClientboundSetScorePacket with an 'action' field
            remappings.put("net.minecraft.network.protocol.game.ClientboundResetScorePacket", "net.minecraft.network.protocol.game.ClientboundSetScorePacket");

            // PlainTextContents was referred to as LiteralContents in MojMap, just like the spigot name
            // But the spigot name did not change when mojang renamed it. So we handle a remap to the old name here.
            remappings.put("net.minecraft.network.chat.contents.PlainTextContents", "net.minecraft.network.chat.contents.LiteralContents");
        }

        /* ======== Mojang remapping changes for 1.20.2 ======== */
        if (evaluateMCVersion(">=", "1.20.2")) {
            // The ClientboundAddPlayerPacket (PacketPlayOutNamedEntitySpawn) was removed. Instead to spawn players,
            // the generic ClientboundAddEntityPacket (PacketPlayOutSpawnEntity) is used instead.
            remappings.put("net.minecraft.network.protocol.game.ClientboundAddPlayerPacket", "net.minecraft.network.protocol.game.ClientboundAddEntityPacket");

            // De-obfuscate a ClientboundCustomPayloadPacket implementation used when sending messages using Bukkit API
            // Spigot devs made this an anonymous Class which is highly annoying if you want to send it yourself
            // No longer exists as of 1.20.5, where they abuse the DiscardedPayload class instead
            if (evaluateMCVersion("<", "1.20.5")) {
                Class<?> customPayloadType = null;
                try {
                    customPayloadType = MPLType.getClassByName("net.minecraft.network.protocol.common.custom.CustomPacketPayload");
                } catch (Throwable t) {
                    Logging.LOGGER_REFLECTION.log(Level.WARNING, "Unable to identify the CustomPacketPayload type", t);
                }
                String anonTypeName = null;
                if (customPayloadType != null) {
                    for (int n = 1; n < 1000; n++) {
                        String name = server.getCBRoot() + ".entity.CraftPlayer$" + n;
                        try {
                            Class<?> type = MPLType.getClassByName(name);
                            if (customPayloadType.isAssignableFrom(type)) {
                                anonTypeName = name;
                                break;
                            }
                        } catch (ClassNotFoundException e) {
                            break;
                        }
                    }
                }
                if (anonTypeName != null) {
                    remappings.put("net.minecraft.network.protocol.common.custom.BukkitCustomPayload", anonTypeName);
                } else {
                    Logging.LOGGER_REFLECTION.log(Level.WARNING, "Unable to identify the Bukkit custom payload type");
                }
            }
        }
        if (evaluateMCVersion("==", "1.20.2")) {
            // On Minecraft 1.20.2 ClientboundResourcePackPacket was shortly moved to the 'common' package
            // The BKCL API for ClientboundResourcePackPushPacket covers both, so we got to map to it
            remappings.put("net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket", "net.minecraft.network.protocol.common.ClientboundResourcePackPacket");
        }
        if (evaluateMCVersion("<", "1.20.2")) {
            // Before Minecraft 1.20.2 ClientboundResourcePackPushPacket does not exist
            // It is handled by ClientboundResourcePackPacket (PacketPlayOutResourcePackSend) instead
            remappings.put("net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket", "net.minecraft.network.protocol.game.ClientboundResourcePackPacket");

            // Before Minecraft 1.20.2 all of these packets existed in the game package, after they are in common. Remap them so common works.
            remappings.put("net.minecraft.network.protocol.common.ServerboundKeepAlivePacket", "net.minecraft.network.protocol.game.ServerboundKeepAlivePacket");
            remappings.put("net.minecraft.network.protocol.common.ClientboundKeepAlivePacket", "net.minecraft.network.protocol.game.ClientboundKeepAlivePacket");
            remappings.put("net.minecraft.network.protocol.common.ServerboundResourcePackPacket", "net.minecraft.network.protocol.game.ServerboundResourcePackPacket");
            remappings.put("net.minecraft.network.protocol.common.ServerboundResourcePackPacket$Action", "net.minecraft.network.protocol.game.ServerboundResourcePackPacket$Action");
            remappings.put("net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket", "net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket");
            remappings.put("net.minecraft.network.protocol.common.ClientboundDisconnectPacket", "net.minecraft.network.protocol.game.ClientboundDisconnectPacket");
            remappings.put("net.minecraft.network.protocol.common.ServerboundClientInformationPacket", "net.minecraft.network.protocol.game.ServerboundClientInformationPacket");
        }

        /* ======== Mojang remapping changes for 1.19.4 ======== */
        if (evaluateMCVersion("<", "1.19.4")) {
            remappings.put("net.minecraft.world.entity.Relative", "net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket$RelativeArgument");
        }

        /* ======== Mojang remapping changes for 1.19.3 ======== */
        if (evaluateMCVersion(">=", "1.19.3")) {
            // CustomSoundEffect was removed as of 1.19.3, now fully handled by the unified ClientboundSoundPacket (PacketPlayOutNamedSoundEffect)
            remappings.put("net.minecraft.network.protocol.game.ClientboundCustomSoundPacket", "net.minecraft.network.protocol.game.ClientboundSoundPacket");
        } else {
            // BuiltInRegistries class does not exist before 1.19.3, all relevant registry fields are found in Registry (IRegistry) instead
            remappings.put("net.minecraft.core.registries.BuiltInRegistries", "net.minecraft.core.Registry");
        }

        /* ======== Mojang remapping changes for 1.19 ======== */
        if (evaluateMCVersion(">=", "1.19")) {
            // Painting / living entity spawn packets were merged into one
            remappings.put("net.minecraft.network.protocol.game.ClientboundAddMobPacket", "net.minecraft.network.protocol.game.ClientboundAddEntityPacket");
            remappings.put("net.minecraft.network.protocol.game.ClientboundAddPaintingPacket", "net.minecraft.network.protocol.game.ClientboundAddEntityPacket");
        } else {
            // Uses a normal java.util.Random on older versions
            remappings.put("net.minecraft.util.RandomSource", "java.util.Random");
            // PaintingVariant(s) class came to exist on 1.19, before it pointed to Motive (spigot: EntityPainting.EnumArt / decoration.Paintings)
            remappings.put("net.minecraft.world.entity.decoration.painting.PaintingVariant", "net.minecraft.world.entity.decoration.Motive");
            remappings.put("net.minecraft.world.entity.decoration.painting.PaintingVariants", "net.minecraft.world.entity.decoration.Motive");
        }

        /* ======== Mojang remapping changes for 1.18.2 ======== */
        if (evaluateMCVersion("<", "1.18.2")) {
            // Different MojMap name for Frozen on 1.18.1 and earlier
            remappings.put("net.minecraft.core.RegistryAccess$Frozen", "net.minecraft.core.RegistryAccess$RegistryHolder");
        }

        /* ======== Mojang remapping changes for 1.18 ======== */
        if (evaluateMCVersion("<", "1.18")) {
            // ClientboundLevelChunkPacket was replaced with a different packet which stores light and block data at 1.18
            // To simplify the BKCL API we use ClientboundLevelChunkWithLightPacket, since in both cases the buffer, heightmap data
            // and changed block state information is available. We handle the adaptering in template code.
            // On Spigot this is further mapped to PacketPlayOutMapChunk
            remappings.put("net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket", "net.minecraft.network.protocol.game.ClientboundLevelChunkPacket");
        }

        /* ======== Mojang remapping changes for 1.17.1 ======== */
        if (evaluateMCVersion("<", "1.17.1")) {
            // Before 1.17.1 the destroy packet only destroyed a single entity
            remappings.put("net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket", "net.minecraft.network.protocol.game.ClientboundRemoveEntityPacket");
        }

        /* ======== Mojang remapping changes for 1.16 ======== */
        if (evaluateMCVersion("<", "1.16")) {
            // Maps nms ResourceKey to the internal proxy class replacement pre-1.16
            remappings.put("net.minecraft.resources.ResourceKey", "com.bergerkiller.bukkit.common.internal.proxy.ResourceKey_1_15_2");
        }

        /* ======== Mojang remapping changes for 1.14 ======== */
        if (evaluateMCVersion("<", "1.14")) {
            remappings.put("net.minecraft.server.level.EntityTracker", "net.minecraft.server.level.EntityTracker");
            remappings.put("net.minecraft.server.level.EntityTrackerEntry", "net.minecraft.server.level.ServerEntity");

            // Empty block getter doesn't exist, so we made our own implementation for it
            remappings.put("net.minecraft.world.level.EmptyBlockGetter", EmptyBlockGetterInit.CLASS_NAME);
        }

        /* ======== Mojang remapping changes for 1.13 ======== */
        if (evaluateMCVersion("<", "1.13")) {
            // Proxy classes that were added in 1.13
            remappings.put("net.minecraft.world.level.levelgen.Heightmap", "com.bergerkiller.bukkit.common.internal.proxy.HeightMapProxy_1_12_2");
            remappings.put("net.minecraft.world.level.levelgen.HeightMap$Types", "com.bergerkiller.bukkit.common.internal.proxy.HeightMapProxy_1_12_2$Type");
            remappings.put("net.minecraft.world.phys.shapes.VoxelShape", "com.bergerkiller.bukkit.common.internal.proxy.VoxelShapeProxy");
            remappings.put("net.minecraft.world.level.block.entity.BlockEntityType", "com.bergerkiller.bukkit.common.internal.proxy.TileEntityTypesProxy_1_8_to_1_12_2");
            // Stop sound works using an MC|StopSound custom message
            remappings.put("net.minecraft.network.protocol.game.ClientboundStopSoundPacket", "net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket");
        }

        /* ======== Mojang remapping changes for 1.11 ======== */
        if (evaluateMCVersion("<", "1.11")) {
            // MapDecorationType (MapIcon$Type) did not exist before 1.11, we use a proxy class for 1.8 - 1.10.2
            remappings.put("net.minecraft.world.level.saveddata.maps.MapDecorationType", "com.bergerkiller.bukkit.common.internal.proxy.MapDecorationType_1_8_to_1_10_2");
        }

        /* ======== Mojang remapping changes for 1.9 ======== */
        if (evaluateMCVersion("<", "1.9")) {
            // Before Minecraft 1.9 some servers implement MobEffectList, but others do not
            // Remap to our proxy class if missing
            try {
                Class.forName(server.getNMSRoot() + ".MobEffectList");
            } catch (ClassNotFoundException e) {
                remappings.put("net.minecraft.world.effect.MobEffect", "com.bergerkiller.bukkit.common.internal.proxy.MobEffectList");
            }

            // ServerboundUseItemOnPacket (PacketPlayInUseItem) did not exist on 1.8
            // The packet is handled by ServerboundUseItemPacket (PacketPlayInBlockPlace) instead
            remappings.put("net.minecraft.network.protocol.game.ServerboundUseItemOnPacket", "net.minecraft.network.protocol.game.ServerboundUseItemPacket");

            // ClientboundCustomSoundPacket (PacketPlayOutCustomSoundEffect) did not exist on 1.8
            // The packet is handled by ClientboundSoundPacket (PacketPlayOutNamedSoundEffect) instead
            remappings.put("net.minecraft.network.protocol.game.ClientboundCustomSoundPacket", "net.minecraft.network.protocol.game.ClientboundSoundPacket");

            // We proxy a bunch of classes, because they don't exist in 1.8
            // Writing custom wrappers with switches would be too tiresome
            // This allows continued use of the same API without trouble
            // Converters take care to convert between the Class and Id used internally
            remappings.put("net.minecraft.world.entity.EquipmentSlot", "com.bergerkiller.bukkit.common.internal.proxy.EnumItemSlot");
            remappings.put("net.minecraft.world.level.chunk.PalettedContainer", "com.bergerkiller.bukkit.common.internal.proxy.DataPaletteBlock");
            remappings.put("net.minecraft.sounds.SoundEvent", "com.bergerkiller.bukkit.common.internal.proxy.SoundEffect_1_8_8");
            remappings.put("net.minecraft.world.level.dimension.DimensionType", "com.bergerkiller.bukkit.common.internal.proxy.DimensionManager_1_8_8");
        }

        // Register the remappings with the server if it supports it, otherwise register a class resolver to do it
        if (server instanceof CraftBukkitServer) {
            // Perform early remappings so that servers such as Mohist don't get super confused
            // This also ensures that MojMap -> Spigot translation occurs after these remappings
            ((CraftBukkitServer) server).setEarlyRemappings(remappings);
        } else {
            // Add an extra class resolver to do it in
            Resolver.registerClassResolver(classPath -> {
                String remapped = remappings.get(classPath);
                return (remapped != null) ? remapped : classPath;
            });
        }

        // Initialize this one right away, as it's used in generated code
        NullPacketDataSerializerInit.initialize();
        EmptyBlockGetterInit.initialize();
        ScopedProblemReporterInit.initialize();
        UnsetDataWatcherItemInit.initialize();

        // Register converters
        Conversion.registerConverters(WrapperConversion.class);
        Conversion.registerConverters(HandleConversion.class);
        Conversion.registerConverters(NBTConversion.class);
        Conversion.registerConverters(ItemDisplayMode.class);
        Conversion.registerConverters(Brightness.class);
        Conversion.registerConverters(CommonEntityType.class);
        Conversion.registerConverters(MapConversion.class);
        Conversion.registerConverters(RelativeFlags.class);

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
        if (evaluateMCVersion(">=", "1.14")) {
            Conversion.registerConverters(EntityPoseConversion.class);
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

        // Scoreboard DisplaySlot conversions
        try {
            ScoreboardDisplaySlotConversion.init();
            Conversion.registerConverters(ScoreboardDisplaySlotConversion.class);
        } catch (Throwable t) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE,
                    "Failed to register Scoreboard DisplaySlot converters", t);
        }

        // JOML was introduced later
        try {
            if (JOMLConversion.available()) {
                JOMLConversion.init();
                Conversion.registerConverters(JOMLConversion.class);
            }
        } catch (Throwable t) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE,
                    "Failed to register JOML converters", t);
        }

        // Enum <> String type of conversions based on Mojangs Codec system
        SerializedEnumConversion.registerMinecraftEnumConversion();

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
        TagHandle.ByteArrayTagHandle.T.forceInitialization();
        TagHandle.ByteTagHandle.T.forceInitialization();
        TagHandle.DoubleTagHandle.T.forceInitialization();
        TagHandle.FloatTagHandle.T.forceInitialization();
        TagHandle.IntArrayTagHandle.T.forceInitialization();
        TagHandle.IntTagHandle.T.forceInitialization();
        if (evaluateMCVersion(">=", "1.12")) {
            TagHandle.LongArrayTagHandle.T.forceInitialization();
        }
        TagHandle.LongTagHandle.T.forceInitialization();
        TagHandle.ShortTagHandle.T.forceInitialization();
        TagHandle.StringTagHandle.T.forceInitialization();
        CompoundTagHandle.T.forceInitialization();
        ListTagHandle.T.forceInitialization();
    }

    /**
     * Gets a List of all classes generated in BKCommonLib's com.bergerkiller.generated
     * package. This method will work both when running from a jar file and when running from the classes build folder under test.
     * The returned class names start with "com.bergerkiller.generated.".
     *
     * @return List of class names in the com.bergerkiller.generated package.
     *         The returned list is mutable (new ArrayList) and sorted alphabetically.
     * @throws IOException If an unexpected I/O error occurs while reading the jar file or classes directory
     */
    public static List<String> getGeneratedClassNames() throws IOException {
        final boolean isRunFromBuildDirectory;
        try {
            if (Common.IS_TEST_MODE) {
                URL codeSource = CommonBootstrap.class.getProtectionDomain().getCodeSource().getLocation();
                if (codeSource == null) {
                    throw new IOException("Unable to determine code source location for BKCommonLib");
                }

                Path locationPath = java.nio.file.Paths.get(codeSource.toURI());
                isRunFromBuildDirectory = Files.isDirectory(locationPath);
            } else {
                isRunFromBuildDirectory = false;
            }
        } catch (java.net.URISyntaxException e) {
            throw new IOException("Failed to resolve code source location", e);
        }

        List<String> tmpClassNames = new ArrayList<>();
        if (isRunFromBuildDirectory) {
            // If run from a build directory (BKCommonLib unit tests), then list the .class files in there
            // This should never ever run on production, hence the extra IS_TEST_MODE guard.

            // List the .class files in the "classes" build folder of BKCommonLib
            String bkclClassesDir = System.getProperty("main.classes.dir");
            if (bkclClassesDir == null || bkclClassesDir.isEmpty()) {
                throw new IOException("Run under test without main.classes.dir set (gradle error?)");
            }

            final Path bkclClassesPath = (new File(bkclClassesDir)).toPath();
            try (Stream<Path> bkclClassFiles = Files.walk(bkclClassesPath)) {
                tmpClassNames = bkclClassFiles
                        .filter(Files::isRegularFile)
                        .map(bkclClassesPath::relativize)
                        .map(Path::toString)
                        .collect(Collectors.toList());
            }
        } else {
            // List the .class files in the jar zip file of BKCommonLib
            URLClassLoader loader = (URLClassLoader) CommonBootstrap.class.getClassLoader();
            File jarFile = null;
            for (URL url : loader.getURLs()) {
                jarFile = new File(url.getFile());
                if (!jarFile.exists()) {
                    jarFile = null;
                }
            }
            if (jarFile == null) {
                throw new IOException("Unable to determine the jar file of BKCommonLib");
            }

            // Note: can't use stream/iterator api because getNextEntry() throws
            try (ZipInputStream zip = new ZipInputStream(new FileInputStream(jarFile))) {
                for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
                    if (!entry.isDirectory()) {
                        tmpClassNames.add(entry.getName());
                    }
                }
            }
        }

        return tmpClassNames.stream()
                // Omit / prefix for more reliable matching
                .map(className -> {
                    if (className.startsWith("/")) {
                        className = className.substring(1);
                    }
                    return className;
                })
                // Only match Template files in the generated package, that are class files
                .filter(name -> name.startsWith("com/bergerkiller/generated") && name.endsWith(".class"))
                // Turn into a loadable class name
                .map(name -> name.substring(0, name.length()-6).replace('/', '.'))
                // Alphabetical for reliable logic
                .sorted()
                // Turn into a mutable list
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Forces all template classes to initialize. Inspects the BKCommonLib jar file to find all template classes
     * to pull this off.
     *
     * @param classLoaderOrderRandom If non-null, randomizes the order of classes using this Random.
     *                               This can be used to identify load orders that cause deadlocks or
     *                               other issues by brute force.
     */
    public static void preloadTemplateClasses(Random classLoaderOrderRandom) {
        List<String> classNames;
        try {
            classNames = getGeneratedClassNames();
        } catch (IOException ex) {
            Logging.LOGGER.log(Level.WARNING, "Failed to pre-load template classes: listing failed", ex);
            return;
        }

        // For debugging class loading deadlocks, randomize the order of the classes
        // Some random shuffle seeds can more reliably reproduce such deadlocks
        if (classLoaderOrderRandom != null) {
            Collections.shuffle(classNames, classLoaderOrderRandom);
        }

        classNames.parallelStream()
                // Load all these classes in parallel
                .map(className -> {
                    try {
                        // The below code loads the class and calls the static initializer on it
                        Class<?> templateClass = Class.forName(className, true, CommonClasses.class.getClassLoader());
                        if (Template.Handle.class.isAssignableFrom(templateClass)) {
                            Field templateClassInstanceField = templateClass.getDeclaredField("T");
                            Template.Class<?> cls = (Template.Class<?>) templateClassInstanceField.get(null);
                            return cls;
                        }
                    } catch (Throwable t) {
                        Logging.LOGGER.log(Level.SEVERE, "Failed to load class " + className, t);
                        return null;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .forEach(cls -> {
                    try {
                        cls.forceInitialization();
                    } catch (Throwable t) {
                        Logging.LOGGER.log(Level.SEVERE, "Failed to initialize " + cls.getHandleType(), t);
                    }
                });
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
