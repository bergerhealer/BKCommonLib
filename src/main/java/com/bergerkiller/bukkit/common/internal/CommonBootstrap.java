package com.bergerkiller.bukkit.common.internal;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.log4j.spi.LoggerContextFactory;
import org.bukkit.Bukkit;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.ModuleLogger;
import com.bergerkiller.bukkit.common.server.CommonServer;
import com.bergerkiller.bukkit.common.server.CraftBukkitServer;
import com.bergerkiller.bukkit.common.server.MCPCPlusServer;
import com.bergerkiller.bukkit.common.server.PaperSpigotServer;
import com.bergerkiller.bukkit.common.server.SpigotServer;
import com.bergerkiller.bukkit.common.server.SportBukkitServer;
import com.bergerkiller.bukkit.common.server.TestServerFactory;
import com.bergerkiller.bukkit.common.server.UnknownServer;

/**
 * Initialization of server and internal components in a lazy-loading fashion
 */
public class CommonBootstrap {
    public static boolean WARN_WHEN_INIT_SERVER = false;
    public static boolean WARN_WHEN_INIT_TEMPLATES = false;
    private static boolean _hasInitTestServer = false;
    private static CommonServer _commonServer = null;

    /**
     * Checks if the Minecraft version matches a version condition
     * 
     * @param operand to evaluate with, for example ">=" and "!="
     * @param version the operand is applied to (right side)
     * @return True if the version matches, False if not
     */
    public static boolean evaluateMCVersion(String operand, String version) {
        return getCommonServer().evaluateMCVersion(operand, version);
    }

    /**
     * Detects and returns the common server implementation that is used.
     * The result is cached.
     * 
     * @return common server
     */
    public static CommonServer getCommonServer() {
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
        }
        return _commonServer;
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

            // Sometimes this is unwanted when running tests
            // To debug this issue, set WARN_WHEN_INIT_SERVER = true;
            if (WARN_WHEN_INIT_SERVER) {
                Logging.LOGGER.log(Level.WARNING, "WARN_WHEN_INIT_SERVER", new RuntimeException("Initializing server"));
            }

            TestServerFactory.initTestServer();
            _hasInitTestServer = true;

            // Display this too during the test
            Logging.LOGGER.log(Level.INFO, "Test running on " + Common.SERVER.getServerDetails());
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
