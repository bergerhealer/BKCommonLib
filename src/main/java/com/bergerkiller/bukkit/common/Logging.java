package com.bergerkiller.bukkit.common;

public class Logging {

    public static final ModuleLogger LOGGER = new ModuleLogger("BKCommonLib");
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
