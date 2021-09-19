package com.bergerkiller.bukkit.common;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.bergerkiller.bukkit.common.internal.CommonPlugin;

public class Logging {
    public static final ModuleLogger LOGGER;

    static {
        // Note: under test there is no Plugin, so we need a fallback!
        Plugin plugin = null;
        if (CommonPlugin.hasInstance()) {
            plugin = CommonPlugin.getInstance();
        } else if (Bukkit.getServer() != null && Bukkit.getServer().getPluginManager() != null) {
            plugin = Bukkit.getServer().getPluginManager().getPlugin("BKCommonLib");
        }
        LOGGER = (plugin != null) ? new ModuleLogger(plugin) : new ModuleLogger("BKCommonLib");
    }

    public static final ModuleLogger LOGGER_DEBUG = LOGGER.getModule("Debug");
    public static final ModuleLogger LOGGER_CONFIG = LOGGER.getModule("Configuration");
    public static final ModuleLogger LOGGER_CONVERSION = LOGGER.getModule("Conversion");
    public static final ModuleLogger LOGGER_REFLECTION = LOGGER.getModule("Reflection");
    public static final ModuleLogger LOGGER_NETWORK = LOGGER.getModule("Network");
    public static final ModuleLogger LOGGER_TIMINGS = LOGGER.getModule("Timings");
    public static final ModuleLogger LOGGER_PERMISSIONS = LOGGER.getModule("Permissions");
    public static final ModuleLogger LOGGER_REGISTRY = LOGGER.getModule("Registry");
    public static final ModuleLogger LOGGER_MAPDISPLAY = LOGGER.getModule("Maps");
}
