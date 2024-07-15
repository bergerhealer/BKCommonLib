package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.config.BasicConfiguration;
import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.config.FileConfiguration;
import com.bergerkiller.bukkit.common.config.yaml.YamlNodeAbstract;
import com.bergerkiller.bukkit.common.internal.CommonClassManipulation;
import com.bergerkiller.bukkit.common.internal.CommonDependencyStartupLogHandler;
import com.bergerkiller.bukkit.common.internal.CommonMethods;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.internal.logic.PluginLoaderHandler;
import com.bergerkiller.bukkit.common.io.ClassRewriter;
import com.bergerkiller.bukkit.common.localization.ILocalizationDefault;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.metrics.Metrics;
import com.bergerkiller.bukkit.common.permissions.IPermissionDefault;
import com.bergerkiller.bukkit.common.permissions.NoPermissionException;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketMonitor;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.*;
import com.bergerkiller.reflection.org.bukkit.BPluginDescriptionFile;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * The extended javaPlugin base used to communicate with BKCommonLib<br><br>
 * <p>
 * Handles dependencies, command registration, event listener registration,
 * permissions and permission defaults, logging, error handling and
 * localization.
 */
public abstract class PluginBase extends JavaPlugin {
    private String disableMessage, enableMessage;
    private FileConfiguration permissionconfig, localizationconfig;
    final PluginLoaderHandler pluginLoaderHandler;
    private BasicConfiguration pluginYamlBKCL = null; // Cached and re-used, instantiated on first use
    private final CommonDependencyStartupLogHandler.PluginBaseHandler startupLogHandler;
    private boolean enabled = false;
    private boolean wasDisableRequested = false;
    private Metrics metrics;

    public PluginBase() {
        // Captures everything that is logged by this plugin instance since creation
        this.startupLogHandler = CommonDependencyStartupLogHandler.bindSelf(this);

        // Initialize the plugin loader handler, which does all the extra logic of
        // parsing the right plugin.yml/paper-piugin.yml and more
        this.pluginLoaderHandler = PluginLoaderHandler.createFor(this);

        // Set hastebin server to use when uploading error reports
        this.startupLogHandler.setHastebinServer(this.pluginLoaderHandler.getPluginConfig()
                .getString("preloader.hastebinServer", "https://hastebin.com"));

        // Bootstrap!
        this.pluginLoaderHandler.bootstrap();
    }

    /**
     * Gets the logger for a specific module in this Plugin
     *
     * @param modulePath for the module
     * @return a new Module Logger
     */
    public ModuleLogger getModuleLogger(String... modulePath) {
        return new ModuleLogger(this, modulePath);
    }

    /**
     * Logs a message to the server console
     *
     * @param level of the message
     * @param message to log
     */
    public void log(Level level, String message) {
        this.getLogger().log(level, message);
    }

    /**
     * Logs the action of a certain player
     *
     * @param by whome the action was performed (only logged if it is a player)
     * @param action the player performed
     */
    public void logAction(CommandSender by, String action) {
        if (by instanceof Player) {
            log(Level.INFO, ((Player) by).getName() + " " + action);
        }
    }

    /**
     * Gets the version of this Plugin
     *
     * @return Plugin version
     */
    public final String getVersion() {
        return this.getDescription().getVersion();
    }

    /**
     * Gets the version of this Plugin parsed into a major-minor number. This
     * expects the version of the plugin to be formatted like
     * <b>MAJOR.MINOR.REVISION.BUILD</b>
     * with separate parts not exceeding 100.<br>
     * <b>REVISION and BUILD will not be contained in this version
     * number!</b><br><br>
     * <p>
     * Examples:<br>
     * - v1.0 = 10000<br>
     * - v8.6 = 86000<br>
     * - v8.06 = 80600<br>
     * - v1.0.0 = 10000<br>
     * - v1.81.65 = 18165
     *
     * @return version parsed to an Integer
     */
    public int getVersionNumber() {
        return CommonMethods.parseVersionNumber(this.getVersion());
    }

    /**
     * Gets the file of the path relative to this plugin's data folder
     *
     * @param path of the file
     * @return relative data File
     */
    public File getDataFile(String... path) {
        if (path == null || path.length == 0) {
            return this.getDataFolder();
        }
        return new File(this.getDataFolder(), StringUtil.join(File.separator, path));
    }

    /**
     * Loads a MapTexture from an image file stored as resource in this Plugin.
     * Throws an exception if loading fails.
     * 
     * @param filename
     * @return loaded texture
     */
    public MapTexture loadTexture(String filename) {
        return MapTexture.loadPluginResource(this, filename);
    }

    /**
     * Gets a Permission, creates one if it doesn't exist
     *
     * @param path of the Permission to obtain
     * @return Permission
     */
    public static Permission getPermission(String path) {
        return CommonPlugin.getInstance().getPermissionHandler().getOrCreatePermission(path);
    }

    /**
     * Gets a permission configuration node
     *
     * @param path of the node to get
     * @return Permission configuration node
     */
    public final ConfigurationNode getPermissionNode(String path) {
        return this.permissionconfig.getNode(path);
    }

    /**
     * Gets a localization configuration node
     *
     * @param path of the node to get
     * @return Localization configuration node
     */
    public final ConfigurationNode getLocalizationNode(String path) {
        return this.localizationconfig.getNode(path);
    }

    /**
     * Registers this main class for one or more commands
     *
     * @param commands to register this Plugin class for
     */
    public final void register(String... commands) {
        this.register(this, commands);
    }

    /**
     * Registers a command executor for one or more commands
     *
     * @param executor to register
     * @param commands to register it for
     */
    public final void register(CommandExecutor executor, String... commands) {
        for (String command : commands) {
            PluginCommand cmd = this.getCommand(command);
            if (cmd != null) {
                cmd.setExecutor(executor);
            }
        }
    }

    /**
     * Registers a listener instance
     *
     * @param listener to register
     */
    public final void register(Listener listener) {
        if (listener == null) {
            throw new RuntimeException("Can not load a listener: The listener instance is null");
        }
        if (listener != this) {
            Bukkit.getPluginManager().registerEvents(listener, this);
        }
    }

    /**
     * Registers a listener class
     *
     * @param listener class to register
     */
    public final void register(Class<? extends Listener> listener) {
        if (listener == null) {
            throw new RuntimeException("Can not load a listener: The listener class is null");
        }
        try {
            this.register(listener.newInstance());
        } catch (Throwable t) {
            Logging.LOGGER_REGISTRY.log(Level.SEVERE, "Failed to register listener " + listener, t);
        }
    }

    /**
     * Registers a packet monitor for the packet types specified. Monitors can
     * only monitor packets, they can not alter them.
     *
     * @param packetMonitor to register
     * @param packetTypes to register the listener for
     */
    public final void register(PacketMonitor packetMonitor, PacketType... packetTypes) {
        PacketUtil.addPacketMonitor(this, packetMonitor, packetTypes);
    }

    /**
     * Registers a packet listener for the packet types specified. Listeners are
     * able to modify packets.
     *
     * @param packetListener to register
     * @param packetTypes to register the listener for
     */
    public final void register(PacketListener packetListener, PacketType... packetTypes) {
        PacketUtil.addPacketListener(this, packetListener, packetTypes);
    }

    /**
     * Unregisters a packet listener
     *
     * @param packetListener to unregister
     */
    public final void unregister(PacketListener packetListener) {
        PacketUtil.removePacketListener(packetListener);
    }

    /**
     * Loads all the permissions from a Permissions container class<br>
     * If the class is not an enumeration, the static constants in the class are
     * used instead
     *
     * @param permissionDefaults class
     */
    public final void loadPermissions(Class<? extends IPermissionDefault> permissionDefaults) {
        for (IPermissionDefault def : CommonUtil.getClassConstants(permissionDefaults, IPermissionDefault.class)) {
            this.loadPermission(def);
        }
    }

    /**
     * Loads a single permission using a permission default
     *
     * @param permissionDefault to use
     * @return Permission that was loaded
     */
    public final Permission loadPermission(IPermissionDefault permissionDefault) {
        return this.loadPermission(permissionDefault.getName(), permissionDefault.getDefault(), permissionDefault.getDescription());
    }

    /**
     * Loads a single permission using a permission path
     *
     * @param path of the Permission
     * @return Permission that was loaded
     */
    public final Permission loadPermission(String path) {
        return this.loadPermission(getPermission(path));
    }

    /**
     * Loads a single permission using a Permission
     *
     * @param permission to load
     * @return Permission that was loaded
     */
    public final Permission loadPermission(Permission permission) {
        return this.loadPermission(permission.getName(), permission.getDefault(), permission.getDescription());
    }

    /**
     * Loads a single permission using the path, default and description
     *
     * @param path of the Permission
     * @param def value of the Permission
     * @param description value of the Permission
     * @return Permission that was loaded
     */
    public final Permission loadPermission(String path, PermissionDefault def, String description) {
        return this.loadPermission(getPermissionNode(path), def, description);
    }

    /**
     * Loads a single permission using the configuration node, default and
     * description
     *
     * @param node to use for the permission path, default and description
     * @param def value to use if the node is unusable
     * @param description to use if the node is unusable
     * @return Permission that was loaded
     */
    public final Permission loadPermission(ConfigurationNode node, PermissionDefault def, String description) {
        Permission permission = getPermission(node.getPath());
        permission.setDefault(node.get("default", def));
        permission.setDescription(node.get("description", description));
        return permission;
    }

    /**
     * Loads all the localization defaults from a Localization container
     * class<br>
     * If the class is not an enumeration, the static constants in the class are
     * used instead
     *
     * @param localizationDefaults class
     */
    public void loadLocales(Class<? extends ILocalizationDefault> localizationDefaults) {
        for (ILocalizationDefault def : CommonUtil.getClassConstants(localizationDefaults)) {
            this.loadLocale(def);
        }
    }

    /**
     * Loads a localization using a localization default
     *
     * @param localizationDefault to load from
     */
    public void loadLocale(ILocalizationDefault localizationDefault) {
        localizationDefault.initDefaults(this.localizationconfig);
    }

    /**
     * Loads a single Localization value<br>
     * Adds this node to the localization configuration if it wsn't added
     *
     * @param path to the value (case insensitive, can not be null)
     * @param defaultValue for the value
     */
    public void loadLocale(String path, String defaultValue) {
        path = path.toLowerCase(Locale.ENGLISH);
        if (!this.localizationconfig.contains(path)) {
            this.localizationconfig.set(path, defaultValue);
        }
    }

    /**
     * Tries to find the command configuration for a command
     *
     * @param command to find the configuration node for
     * @return The configuration node, or null if not found
     */
    private ConfigurationNode getCommandNode(String command) {
        command = command.toLowerCase(Locale.ENGLISH);
        String fullPath = "commands." + command;
        if (this.localizationconfig.isNode(fullPath)) {
            return this.localizationconfig.getNode(fullPath);
        } else {
            fullPath = "commands." + command.replace('.', ' ');
            if (this.localizationconfig.isNode(fullPath)) {
                return this.localizationconfig.getNode(fullPath);
            } else {
                return null;
            }
        }
    }

    /**
     * Gets the localized usage for a command
     *
     * @param command name (case insensitive)
     * @return command usage
     */
    public String getCommandUsage(String command) {
        ConfigurationNode node = getCommandNode(command);
        final String defValue = "/" + command;
        if (node == null) {
            return defValue;
        } else {
            return node.get("usage", defValue);
        }
    }

    /**
     * Gets the localized description for a command
     *
     * @param command name (case insensitive)
     * @return command description
     */
    public String getCommandDescription(String command) {
        ConfigurationNode node = getCommandNode(command);
        final String defValue = "No description specified";
        if (node == null) {
            return defValue;
        } else {
            return node.get("description", defValue);
        }
    }

    /**
     * Gets a localization value
     *
     * @param path to the localization value (case insensitive, can not be null)
     * @param arguments to use for the value
     * @return Localization value
     */
    public String getLocale(String path, String... arguments) {
        path = path.toLowerCase(Locale.ENGLISH);
        // First check if the path leads to a node
        if (this.localizationconfig.isNode(path)) {
            // Redirect to the proper sub-node
            // Check recursively if the arguments are contained
            String newPath = path + ".default";
            if (arguments.length > 0) {
                StringBuilder tmpPathBuilder = new StringBuilder(path);
                String tmpPath = path;
                for (String argument : arguments) {
                    tmpPathBuilder.append('.');
                    if (argument == null) {
                        tmpPathBuilder.append("null");
                    } else {
                        tmpPathBuilder.append(argument.toLowerCase(Locale.ENGLISH));
                    }
                    tmpPath = tmpPathBuilder.toString();
                    // New argument appended path exists, update the path
                    if (this.localizationconfig.contains(tmpPath)) {
                        newPath = tmpPath;
                    } else {
                        break;
                    }
                }
            }
            // Update path to lead to the new path
            path = newPath;
        }
        // Regular loading going on
        if (arguments.length > 0) {
            StringBuilder locale = new StringBuilder(this.localizationconfig.get(path, ""));
            for (int i = 0; i < arguments.length; i++) {
                StringUtil.replaceAll(locale, "%" + i + "%", LogicUtil.fixNull(arguments[i], "null"));
            }
            return locale.toString();
        } else {
            return this.localizationconfig.get(path, String.class, "");
        }
    }

    /**
     * Fired when the Permission nodes have to be created
     */
    public void permissions() {
    }

    /**
     * Fired when the Localization nodes have to be created
     */
    public void localization() {
    }

    /**
     * Gets the disable message shown when this Plugin disables
     *
     * @return disable message
     */
    public final String getDisableMessage() {
        return this.disableMessage;
    }

    /**
     * Sets the disable message shown when this Plugin disables
     *
     * @param msg to set to, null to disable the message
     */
    public void setDisableMessage(String msg) {
        this.disableMessage = msg;
    }

    /**
     * Gets the enable message shown after this Plugin enabled successfully
     *
     * @return enable message
     */
    public final String getEnableMessage() {
        return this.enableMessage;
    }

    /**
     * Sets the enable message shown after this Plugin enabled successfully
     *
     * @param msg to set to, null to disable the message
     */
    public void setEnableMessage(String msg) {
        this.enableMessage = msg;
    }

    /**
     * Gets the minimum BKCommonLib version required for this Plugin to
     * function<br>
     * Override this and return Common.VERSION as result (compiler will
     * automatically inline this)
     *
     * @return Minimum BKCommonLib version number
     */
    public abstract int getMinimumLibVersion();

    /**
     * Handles a possible throwable thrown somewhere in the Plugin<br>
     * If the throwable is too severe, the plugin is automatically disabled<br>
     * Additional exception types can be handled if needed
     *
     * @param reason to throw
     */
    @SuppressWarnings("unchecked")
    public void handle(Throwable reason) {
        if (reason instanceof Exception) {
            getLogger().log(Level.SEVERE, reason.getMessage(), reason);
        } else if (reason instanceof OutOfMemoryError) {
            log(Level.SEVERE, "The server is running out of memory! Do something!");
        } else {
            String pluginCause = getName();
            if (CommonUtil.isInstance(reason, NoClassDefFoundError.class, NoSuchMethodError.class, NoSuchFieldError.class, IllegalAccessError.class)) {
                String fixedReason = StringUtil.trimStart(LogicUtil.fixNull(reason.getMessage(), ""), "tried to access ");
                String path = StringUtil.trimStart(fixedReason, "method ", "field ", "class ");
                if (path.startsWith(Common.NMS_ROOT)) {
                    log(Level.SEVERE, "This version of the plugin is incompatible with this Minecraft version:");
                } else if (path.startsWith(Common.CB_ROOT)) {
                    log(Level.SEVERE, "This version of the plugin is incompatible with this CraftBukkit implementation:");
                } else if (path.startsWith("org.bukkit")) {
                    log(Level.SEVERE, "This version of the plugin is incompatible with the current Bukkit API:");
                } else {
                    final Plugin plugin = CommonUtil.getPluginByClass(path);
                    if (plugin == this) {
                        if (reason instanceof NoClassDefFoundError) {
                            log(Level.WARNING, "Class is missing (plugin was hot-swapped?): " + reason.getMessage());
                            return;
                        } else {
                            log(Level.SEVERE, "Encountered a compiler error");
                        }
                    } else {
                        // Obtain the type of happening
                        final String type;
                        if (reason instanceof IllegalAccessError) {
                            if (fixedReason.startsWith("class ")) {
                                type = "Class is inaccessible in";
                            } else if (fixedReason.startsWith("method ")) {
                                type = "Method is inaccessible in";
                            } else if (fixedReason.startsWith("field ")) {
                                type = "Field is inaccessible in";
                            } else {
                                type = "Something is inaccessible in";
                            }
                        } else if (reason instanceof NoClassDefFoundError) {
                            type = "Class is missing from";
                        } else if (reason instanceof NoSuchMethodError) {
                            type = "Method is missing from";
                        } else if (reason instanceof NoSuchFieldError) {
                            type = "Field is missing from";
                        } else {
                            type = "Something is missing from";
                        }
                        // Log the message
                        if (plugin == null) {
                            getLogger().log(Level.SEVERE, type + " a dependency of this plugin", reason);
                            // Add all dependencies of this plugin to the cause
                            LinkedHashSet<String> dep = new LinkedHashSet<String>();
                            dep.add(this.getName());
                            dep.addAll(LogicUtil.fixNull(this.getDescription().getDepend(), Collections.EMPTY_LIST));
                            dep.addAll(LogicUtil.fixNull(this.getDescription().getSoftDepend(), Collections.EMPTY_LIST));
                            pluginCause = StringUtil.combineNames(dep);
                        } else {
                            pluginCause = getName() + " and " + plugin.getName();
                            log(Level.SEVERE, type + " dependency '" + plugin.getName() + "'");
                        }
                    }
                }
            } else {
                log(Level.SEVERE, "Encountered a critical error");
            }
            log(Level.SEVERE, "Please, check for an updated version of " + pluginCause + " before reporting this bug!");
            StackTraceFilter.SERVER.print(reason);
        }
    }

    private static void setPermissions(ConfigurationNode node) {
        for (ConfigurationNode subNode : node.getNodes()) {
            setPermissions(subNode);
        }

        PermissionDefault def = ParseUtil.convert(getNodeStringValue(node, "default"), PermissionDefault.class);
        String desc = getNodeStringValue(node, "description");
        if (def != null || desc != null) {
            Permission permission = getPermission(node.getPath().toLowerCase(Locale.ENGLISH));
            if (def != null) {
                permission.setDefault(def);
            }
            if (desc != null) {
                permission.setDescription(desc);
            }
        }
    }

    // Gets the value stored at a key of a node, without trying to convert it
    // Prevents nodes or lists being interpreted as a String
    // Returns null if not something that can be interpreted as a String
    private static String getNodeStringValue(ConfigurationNode node, String key) {
        Object value = node.get(key);
        if (value == null || value instanceof YamlNodeAbstract) {
            return null;
        } else {
            return value.toString();
        }
    }

    /**
     * Gets the Metrics instance for this Plugin, which is used to send
     * statistics to
     * <a href="http://mcstats.org/">http://mcstats.org/</a><br>
     * To make use of this functionality, first add the following line to the
     * <b>plugin.yml</b>:<br>
     * <pre>metrics: true</pre>
     * <p>
     * To avoid issues, call {@link #hasMetrics()} before using this method to
     * check whether Metrics is available.
     *
     * @return the Metrics instance used
     * @throws RuntimeException if no metrics is available
     */
    public Metrics getMetrics() {
        if (metrics == null) {
            throw new RuntimeException("Metrics is not enabled or failed to initialize for this Plugin.");
        }
        return metrics;
    }

    /**
     * Checks whether Metrics is available for this Plugin. Always call this
     * method before using {@link #getMetrics()} - initialization of Metrics
     * could have failed!
     *
     * @return True if Metrics is available, False if not
     */
    public boolean hasMetrics() {
        return metrics != null;
    }

    @Override
    public final void onEnable() {
        // Do this early so command perms work properly all the time
        if (Bukkit.getPluginManager().getPermission(CommonDependencyStartupLogHandler.PERMISSION) == null) {
            Permission permission = new Permission(
                    CommonDependencyStartupLogHandler.PERMISSION,
                    "Use the startuplog subcommand to view the startup log of plugins",
                    PermissionDefault.OP);
            Bukkit.getPluginManager().addPermission(permission);
        }

        // Check compatible with server
        boolean compatible = false;
        try {
            compatible = Common.IS_COMPATIBLE;
        } catch (Throwable t) {
            getLogger().log(Level.SEVERE, "An unexpected BKCommonLib initialization error occurred", t);
            if (this instanceof CommonPlugin) {
                onCriticalStartupFailure("Critical initialization error (unsupported server?)");
                return;
            }
        }

        // Shortcut to avoid unneeded initialization: calling enable will result in BKCommonLib disabling
        // The enable() will, after logging details, call onCriticalStartupFailure()
        if (!compatible && this instanceof CommonPlugin) {
            try {
                this.enable();
            } catch (Throwable t) {
                getLogger().log(Level.SEVERE, "An unexpected BKCommonLib initialization error occurred", t);
                if (!startupLogHandler.hasCriticalStartupFailure()) {
                    onCriticalStartupFailure("Critical initialization error (unsupported server?)");
                }
            }
            return;
        }

        // If BKCommonLib is not compatible, don't bother enabling a dependency of it
        if (!compatible) {
            onCriticalStartupFailure("Installed BKCommonLib is not compatible with this server",
                    Bukkit.getPluginManager().getPlugin("BKCommonLib"));
            return;
        }
        if (!(this instanceof CommonPlugin) && !CommonPlugin.hasInstance()) {
            onCriticalStartupFailure("BKCommonLib failed to enable, this plugin is disabled",
                    Bukkit.getPluginManager().getPlugin("BKCommonLib"));
            return;
        }

        // First of all, check that all dependencies are properly enabled
        // Install a logger hook in all dependencies
        {
            // Load a full list of hard dependencies. These MUST be enabled to continue.
            List<String> dependencies = LogicUtil.fixNull(getDescription().getDepend(), Collections.emptyList());

            // Include all dependency's startup logs for the report of this plugin's logs
            dependencies.stream()
                .map(Bukkit.getPluginManager()::getPlugin)
                .filter(Objects::nonNull)
                .forEach(this.startupLogHandler::bindDependency);

            // Fail enabling this plugin if a required dependency is not enabled
            for (String dep : dependencies) {
                if (!PluginLoaderHandler.isPluginFullyEnabled(dep)) {
                    log(Level.SEVERE, "Could not enable '" + getName() + " v" + getVersion() + "' because dependency '" + dep + "' failed to enable!");
                    log(Level.SEVERE, "Perhaps the dependency has to be updated? Please check the log for any errors related to " + dep);
                    onCriticalStartupFailure("Plugin dependency '" + dep + "' failed to enable", Bukkit.getPluginManager().getPlugin(dep));
                    return;
                }
            }
        }

        long startTime = System.currentTimeMillis();
        if (this.getMinimumLibVersion() > Common.VERSION) {
            log(Level.SEVERE, "Requires a newer BKCommonLib version, please update BKCommonLib to the latest version!");
            log(Level.SEVERE, "Verify that there is only one BKCommonLib.jar in the plugins folder before retrying");
            onCriticalStartupFailure("Plugin requires a newer version of BKCommonLib");
            return;
        }

        this.setDisableMessage(this.getName() + " disabled!");

        // Load permission configuration
        this.permissionconfig = new FileConfiguration(this, "PermissionDefaults.yml");

        // load
        if (this.permissionconfig.exists()) {
            this.loadPermissions();
        }

        // header
        this.permissionconfig.setHeader("Below are the default permissions set for plugin '" + this.getName() + "'.");
        this.permissionconfig.addHeader("These permissions are ignored if the permission is set for a group or player.");
        this.permissionconfig.addHeader("Use the defaults as a base to keep the permissions file small");
        this.permissionconfig.addHeader("Need help with this file? Please visit:");
        this.permissionconfig.addHeader("https://wiki.traincarts.net/p/BKCommonLib/PermissionDefaults");

        // Load localization configuration
        this.localizationconfig = new FileConfiguration(this, "Localization.yml");
        // load
        if (this.localizationconfig.exists()) {
            this.loadLocalization();
        }

        // header
        this.localizationconfig.setHeader("Below are the localization nodes set for plugin '" + this.getName() + "'.");
        this.localizationconfig.addHeader("For colors, use the & character followed up by 0 - F");
        this.localizationconfig.addHeader("Need help with this file? Please visit:");
        this.localizationconfig.addHeader("https://wiki.traincarts.net/p/BKCommonLib/Localization");

        // Load all the commands for this Plugin
        Map<String, Map<String, Object>> commands = this.getDescription().getCommands();
        if (commands != null && BPluginDescriptionFile.commands.isValid()) {
            // Prepare commands localization node
            ConfigurationNode commandsNode = getLocalizationNode("commands");

            // Create a new modifiable commands map to replace with
            commands = new HashMap<String, Map<String, Object>>(commands);
            for (Entry<String, Map<String, Object>> commandEntry : commands.entrySet()) {
                ConfigurationNode node = commandsNode.getNode(commandEntry.getKey());

                // Transfer description and usage
                Map<String, Object> data = new HashMap<String, Object>(commandEntry.getValue());
                node.shareWithMap(data, "description", "No description specified");
                node.shareWithMap(data, "usage", "/" + commandEntry.getKey());
                commandEntry.setValue(Collections.unmodifiableMap(data));
            }

            // Set the new commands map using reflection
            BPluginDescriptionFile.commands.set(this.getDescription(), Collections.unmodifiableMap(commands));
        }

        // ==== Permissions ====
        this.permissions();

        // Load all nodes from the permissions config
        setPermissions(this.permissionconfig);
        if (!this.permissionconfig.isEmpty()) {
            this.savePermissions();
        }

        // ==== Localization ====
        this.localization();
        if (!this.localizationconfig.isEmpty()) {
            this.saveLocalization();
        }

        // ==== BStats Metrics ====
        try {
            org.bukkit.configuration.file.YamlConfiguration pluginConfig = this.pluginLoaderHandler.getPluginConfig();
            org.bukkit.configuration.ConfigurationSection bstatsConfig = pluginConfig.getConfigurationSection("bstats");
            if (bstatsConfig != null && bstatsConfig.getBoolean("enabled", false) && bstatsConfig.contains("plugin-id")) {
                int id = bstatsConfig.getInt("plugin-id");
                metrics = new Metrics(this, id);

                // If set, show the build number. Exclude NO-CI as there is no way to know what that means.
                String pluginBuildCfg = pluginConfig.getString("build", "NO-CI");
                if (pluginBuildCfg.equals("") || pluginBuildCfg.equals("NO-CI")) {
                    pluginBuildCfg = "other";
                }
                final String pluginBuild = pluginBuildCfg;

                // Build metric
                metrics.addCustomChart(new Metrics.SimplePie("build", () -> pluginBuild));

                // Plugin + Build combined metric
                final String version = this.getDescription().getVersion();
                metrics.addCustomChart(new Metrics.DrilldownPie("pluginBuildVersion", () ->
                        Collections.singletonMap(version, Collections.singletonMap(pluginBuild, 1))
                ));
            }
        } catch (Throwable t) {
            getLogger().log(Level.SEVERE, "Failed to initialize metrics for " + getName(), t);
        }

        // ==== Enabling ====
        try {
            this.wasDisableRequested = false;
            this.enable();
            if (this.hasCriticalStartupFailure() || this.wasDisableRequested) {
                // Plugin was disabled again while enabling
                return;
            }

            // Disable startup logging next tick. This makes sure that stuff logged by this plugin
            // while other plugins (depending on it) enable is still included in the history.
            startupLogHandler.setNotStartupNextTick();

            // Done, this plugin is enabled
            this.enabled = true;
        } catch (Throwable t) {
            getLogger().log(Level.SEVERE, "An error occurred while enabling, the plugin will be disabled:", t);
            onCriticalStartupFailure("An error occurred while enabling");
            return;
        }

        // update dependencies
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (PluginLoaderHandler.isPluginFullyEnabled(plugin)) {
                pluginLoaderHandler.onPluginLoaded(plugin);
                updateDependency(plugin, plugin.getName(), true);
            }
        }

        // Save
        CommonPlugin.flushSaveOperations(this);

        // Enable messages
        if (this.enableMessage != null) {
            log(Level.INFO, this.enableMessage);
        }
        log(Level.INFO, this.getName() + " version " + this.getDebugVersion() + " enabled! (" + MathUtil.round(0.001 * (System.currentTimeMillis() - startTime), 3) + "s)");
    }

    @Override
    public final void onDisable() {
        // Are there any plugins that depend on me?
        // normally this will never be the case - we will not get here with those still enabled
        // if they are, likely plugman is involved.
        if (enabled) {
            for (Plugin plugin : Bukkit.getServer().getPluginManager().getPlugins()) {
                if (plugin.isEnabled() && CommonUtil.isDepending(plugin, this)) {
                    Bukkit.getServer().getPluginManager().disablePlugin(plugin);
                }
            }
        }

        // Actual disabling logic.
        this.softDisable(false);
    }

    /**
     * Performs a soft disabling, disabling the plugin but leaving it capable of
     * hosting event handlers / tasks.
     * 
     * @param forceDisableMessage Whether to force a disable message, if configured
     */
    private void softDisable(boolean forceDisableMessage) {
        this.wasDisableRequested = true;
        boolean doDisableMessage = forceDisableMessage;
        if (this.enabled) {
            doDisableMessage = this.disableMessage != null;

            // Try to disable the plugin
            try {
                this.disable();
            } catch (Throwable t) {
                getLogger().log(Level.SEVERE, "An error occurred while disabling:", t);
                doDisableMessage = false;
            }
            // Remove references to the plugin - it is disabled now
            this.enabled = false;
        }

        // Disable Metrics if enabled
        if (metrics != null) {
            metrics.shutdown();
            metrics = null;
        }

        // Finish saving
        CommonPlugin.flushSaveOperations(this);

        // If specified to do so, a disable message is shown
        if (doDisableMessage) {
            this.getLogger().log(Level.INFO, this.disableMessage);
        }
    }

    /**
     * Gets whether this plugin suffered a critical startup failure and had to be
     * disabled.
     *
     * @return True if this plugin suffered a critical startup failure
     */
    public boolean hasCriticalStartupFailure() {
        return startupLogHandler.hasCriticalStartupFailure();
    }

    /**
     * Reports a critical failure because a dependency plugin failed to enable.
     *
     * @param reason Failure reason
     * @param pluginCause Optional cause - if it has a critical failure, will be appended to reason
     */
    private void onCriticalStartupFailure(String reason, Plugin pluginCause) {
        if (pluginCause != null && pluginCause instanceof PluginBase) {
            CommonDependencyStartupLogHandler.PluginBaseHandler handler = ((PluginBase) pluginCause).startupLogHandler;
            if (handler.hasCriticalStartupFailure()) {
                onCriticalStartupFailure(reason + "\n" + pluginCause.getName() + ": " + handler.getCriticalStartupFailure());
                return;
            }
        }

        onCriticalStartupFailure(reason);
    }

    /**
     * Call this method to put the plugin in a disabled state. Commands will be
     * available to access a startup log or for operators to know about this.
     *
     * @param reason Reason for the critical startup failure
     */
    protected void onCriticalStartupFailure(String reason) {
        // Figure out all commands the plugin would normally register
        // Use plugin.yml for this, as well use a preloader commands section if set
        // If truly no command is set, make one up (plugin name)
        List<CommonDependencyStartupLogHandler.CriticalAltCommand> altCommands = Collections.emptyList();
        try {
            altCommands = this.getDescription().getCommands().keySet().stream()
                    .map(Bukkit::getPluginCommand)
                    .filter(Objects::nonNull)
                    .map(CommonDependencyStartupLogHandler.CriticalAltCommand::new)
                    .collect(Collectors.toCollection(ArrayList::new));

            List<String> preloaderCommands = this.pluginLoaderHandler.getPluginConfig().getStringList("preloader.commands");
            if (preloaderCommands != null && !preloaderCommands.isEmpty()) {
                for (String preloaderCommand : preloaderCommands) {
                    boolean found = false;
                    for (CommonDependencyStartupLogHandler.CriticalAltCommand altCommand : altCommands) {
                        if (altCommand.name.equalsIgnoreCase(preloaderCommand)) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        altCommands.add(new CommonDependencyStartupLogHandler.CriticalAltCommand(preloaderCommand));
                    }
                }
            }
        } catch (Throwable t) {
            /* Ignore ... */
        }

        if (altCommands.isEmpty()) {
            altCommands.add(new CommonDependencyStartupLogHandler.CriticalAltCommand(
                    getName().toLowerCase(Locale.ENGLISH)));
        }

        if (this instanceof CommonPlugin) {
            // We are BKCommonLib - perform a soft disabling. This disables BKCommonLib's core logic,
            // but keeps it enabled so that listeners/tasks can be safely registered.

            // This means we execute onDisable ourselves, and have to then clean up
            // all listeners/tasks that are left behind. Normally Bukkit does this for us.
            this.softDisable(true);
            CommonUtil.handlePostDisable(this);

            // Now register commands declared by the plugin, and a command executor that
            // simply explains the current situation.
            startupLogHandler.criticalStartupFailure((CommonPlugin) this, reason, altCommands);
        } else {
            // Disable ourselves
            Bukkit.getPluginManager().disablePlugin(this);

            // Get CommonPlugin instance using the PluginManager, rather than getInstance()
            // Even if BKCommonLib failed to be enabled fully, we can still register stuff to it
            CommonPlugin commonPlugin = (CommonPlugin) Bukkit.getPluginManager().getPlugin("BKCommonLib");
            if (commonPlugin != null && commonPlugin.isEnabled()) {
                startupLogHandler.criticalStartupFailure(commonPlugin, reason, altCommands);
            } else {
                startupLogHandler.stopReadingLogNow();
            }
        }
    }

    /**
     * Called when a command is postfixed with 'ver' or 'version'. By default displays
     * the version information of the plugin and BKCommonLib.
     * If those commands should be handled by {@link #command(CommandSender, String, String[])},
     * override this method and return false.
     * 
     * @param sender to send version information to
     * @return True if the version command was handled
     */
    public boolean onVersionCommand(String command, CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + this.getName() + ": v" + this.getDebugVersion());
        sender.sendMessage(ChatColor.GREEN + "BKCommonLib: v" + CommonPlugin.getInstance().getDebugVersion());
        sender.sendMessage(ChatColor.GREEN + "Server: " + getServer().getName() + " " + getServer().getVersion());
        return true;
    }

    /**
     * Called when a command is postfixed with 'startuplog'. By default uploads the
     * startup log of this plugin to Hastebin and makes it available through a hyperlink.
     * If those commands should be handled by {@link #command(CommandSender, String, String[])},
     * override this method and return false.
     *
     * @param sender
     * @param command
     * @param args
     * @return True if the startup log command was handled
     */
    public boolean onStartupLogCommand(CommandSender sender, String command, String[] args) {
        startupLogHandler.handleStartupLogCommand(sender);
        return true;
    }

    @Override
    public final boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String command, String[] args) {
        try {
            String[] fixedArgs = StringUtil.convertArgs(args);
            // Default commands for all plugins
            if (fixedArgs.length >= 1 && LogicUtil.contains(fixedArgs[0].toLowerCase(Locale.ENGLISH), "version", "ver")) {
                if (onVersionCommand(command, sender)) {
                    return true;
                }
            }
            if (fixedArgs.length >= 1 && fixedArgs[0].equalsIgnoreCase("startuplog")) {
                if (onStartupLogCommand(sender, command, args)) {
                    return true;
                }
            }
            // Handle regularly
            if (command(sender, command, fixedArgs)) {
                return true;
            }
            sender.sendMessage(ChatColor.RED + "Unknown command, for help use /help " + command);
        } catch (NoPermissionException ex) {
            if (sender instanceof Player) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            } else {
                sender.sendMessage("This command is only for players!");
            }
        } catch (Throwable t) {
            StringBuilder msg = new StringBuilder("Unhandled exception executing command '");
            msg.append(command).append("' in plugin ").append(this.getName()).append(" v").append(this.getVersion());
            Common.LOGGER.log(Level.SEVERE, msg.toString(), t);
            sender.sendMessage(ChatColor.RED + "An internal error occured while executing this command");
        }
        return true;
    }

    /**
     * Called when this plugin is being enabled
     */
    public abstract void enable();

    /**
     * Called when this plugin is being disabled
     */
    public abstract void disable();

    /**
     * Handles a command
     *
     * @param sender of the command
     * @param command name
     * @param args of the command
     * @return True if handled, False if not
     */
    public abstract boolean command(CommandSender sender, String command, String[] args);

    /**
     * Rewrites a single Class from this plugin using a Class Rewriter. This will modify the Class
     * at runtime.
     * 
     * @param name of the Class to rewrite.
     * @param rewriter to use
     */
    public void rewriteClass(String name, ClassRewriter rewriter) {
        byte[] classBytes = CommonClassManipulation.readClassData(this.getClassLoader(), name);
        classBytes = rewriter.rewrite(this, name, classBytes);
        CommonClassManipulation.writeClassData(this.getClassLoader(), name, classBytes);
    }

    public final void loadLocalization() {
        this.localizationconfig.load();
    }

    /**
     * Obtains a configuration instance managed by this PluginBase containing
     * the contents of the plugin.yml
     *
     * @return plugin.yml configuration
     */
    public final BasicConfiguration getPluginYaml() {
        if (pluginYamlBKCL == null) {
            pluginYamlBKCL = new BasicConfiguration();
            pluginYamlBKCL.loadFromString(pluginLoaderHandler.getPluginConfigText());
        }
        return this.pluginYamlBKCL;
    }

    /**
     * Collects known plugin information and compiles it into a single version description.
     * This will include at least the version number, and if available, the 'build' property
     * stored in the plugin.yml.
     * 
     * @return debugging version information
     */
    public final String getDebugVersion() {
        return pluginLoaderHandler.getDebugVersion();
    }

    /**
     * Gets everything that was logged by this Plugin during the loading/enabling
     * phase. Things logged after enabling are not included. Logs generated by
     * dependencies of this plugin are also included.
     *
     * @return full startup log
     */
    public final String getDebugFullStartupLog() {
        return this.startupLogHandler.getFullStartupLog();
    }

    public final void saveLocalization() {
        this.localizationconfig.save();
    }

    public final void loadPermissions() {
        this.permissionconfig.load();
    }

    public final void savePermissions() {
        this.permissionconfig.save();
    }

    /**
     * Called when a plugin is enabled or disabled
     *
     * @param plugin that got enabled or disabled
     * @param pluginName of the plugin
     * @param enabled state, True if enabled, False if disabled
     */
    public void updateDependency(Plugin plugin, String pluginName, boolean enabled) {
    }
}
