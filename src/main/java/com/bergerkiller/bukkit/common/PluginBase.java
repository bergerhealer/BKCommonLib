package com.bergerkiller.bukkit.common;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

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

import com.bergerkiller.bukkit.common.config.BasicConfiguration;
import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.config.FileConfiguration;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.localization.ILocalizationDefault;
import com.bergerkiller.bukkit.common.metrics.Metrics;
import com.bergerkiller.bukkit.common.permissions.IPermissionDefault;
import com.bergerkiller.bukkit.common.permissions.NoPermissionException;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketMonitor;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.reflection.classes.PluginDescriptionFileRef;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;

/**
 * The extended javaPlugin base used to communicate with BKCommonLib<br><br>
 * 
 * Handles dependencies, command registration, event listener registration, 
 * permissions and permission defaults, logging, error handling and localization.
 */
public abstract class PluginBase extends JavaPlugin {
	private String disableMessage, enableMessage;
	private FileConfiguration permissionconfig, localizationconfig;
	private final BasicConfiguration pluginYaml = new BasicConfiguration();
	private boolean enabled = false;
	private boolean wasDisableRequested = false;
	private Metrics metrics;

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
	 * Gets the version of this Plugin parsed into a major-minor number.
	 * This expects the version of the plugin to be formatted like <b>MAJOR.MINOR.REVISION.BUILD</b>
	 * with separate parts not exceeding 100.<br>
	 * <b>REVISION and BUILD will not be contained in this version number!</b><br><br>
	 * 
	 * Examples:<br>
	 * - v1.0 = 100<br>
	 * - v8.6 = 860<br>
	 * - v8.06 = 806<br>
	 * - v1.0.0 = 100<br>
	 * - v1.81.65 = 181
	 * 
	 * @return version parsed to an Integer
	 */
	public int getVersionNumber() {
		String ver = this.getVersion();
		// Get first dot index
		int dotIndex = ver.indexOf('.');
		if (dotIndex != -1) {
			// Get second dot index from first dot index
			dotIndex = ver.indexOf('.', dotIndex + 1);
			if (dotIndex != -1) {
				// Trim this trailing part
				ver = ver.substring(0, dotIndex);
			}
		}
		return (int) (100.0 * ParseUtil.parseDouble(ver, 1.0));
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
	 * Gets a Permission, creates one if it doesn't exist
	 * 
	 * @param path of the Permission to obtain
	 * @return Permission
	 */
	public static Permission getPermission(String path) {
		return CommonPlugin.getInstance().getPermissionHandler().getPermission(path);
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Registers a packet monitor for the packet types specified.
	 * Monitors can only monitor packets, they can not alter them.
	 * 
	 * @param packetMonitor to register
	 * @param packetTypes to register the listener for
	 */
	public final void register(PacketMonitor packetMonitor, PacketType... packetTypes) {
		PacketUtil.addPacketMonitor(this, packetMonitor, packetTypes);
	}

	/**
	 * Registers a packet listener for the packet types specified.
	 * Listeners are able to modify packets.
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
	 * If the class is not an enumeration, the static constants in the class are used instead
	 * 
	 * @param permissionDefaults class
	 */
	public final void loadPermissions(Class<? extends IPermissionDefault> permissionDefaults) {
		for (IPermissionDefault def : CommonUtil.getClassConstants(permissionDefaults)) {
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
	 * Loads a single permission using the configuration node, default and description
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
	 * Loads all the localization defaults from a Localization container class<br>
	 * If the class is not an enumeration, the static constants in the class are used instead
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
		this.loadLocale(localizationDefault.getName(), localizationDefault.getDefault());
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
				for (int i = 0; i < arguments.length; i++) {
					tmpPathBuilder.append('.');
					if (arguments[i] == null) {
						tmpPathBuilder.append("null");
					} else {
						tmpPathBuilder.append(arguments[i].toLowerCase(Locale.ENGLISH));
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
	 * Gets the minimum BKCommonLib version required for this Plugin to function<br>
	 * Override this and return Common.VERSION as result (compiler will automatically inline this)
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
			StackTraceFilter.SERVER.print(reason);
		} else if (reason instanceof OutOfMemoryError) {
			log(Level.SEVERE, "The server is running out of memory! Do something!");
		} else {
			String pluginCause = getName();
			if (CommonUtil.isInstance(reason, NoClassDefFoundError.class, NoSuchMethodError.class, NoSuchFieldError.class, IllegalAccessError.class)) {
				String fixedReason = StringUtil.trimStart(reason.getMessage(), "tried to access ");
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
							log(Level.SEVERE, type + " a dependency of this plugin");
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
		PermissionDefault def = node.get("default", PermissionDefault.class);
		String desc = node.get("description", String.class);
		if (def != null || desc != null) {
			Permission permission = getPermission(node.getPath().toLowerCase());
			if (def != null) {
				permission.setDefault(def);
			}
			if (desc != null) {
				permission.setDescription(desc);
			}
		}
	}

	/**
	 * Gets the Metrics instance for this Plugin, which is used to send statistics to
	 * <a href="http://mcstats.org/">http://mcstats.org/</a><br>
	 * To make use of this functionality, first add the following line to the <b>plugin.yml</b>:<br>
	 * <pre>metrics: true</pre>
	 * 
	 * To avoid issues, call {@link #hasMetrics()} before using this method to check whether Metrics is available.
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
	 * Checks whether Metrics is available for this Plugin. Always call this method
	 * before using {@link #getMetrics()} - initialization of Metrics could have failed!
	 * 
	 * @return True if Metrics is available, False if not
	 */
	public boolean hasMetrics() {
		return metrics != null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public final void onEnable() {
		// Shortcut to avoid unneeded initialization: calling enable will result in BKCommonLib disabling
		if (!Common.IS_COMPATIBLE && this instanceof CommonPlugin) {
			this.enable();
			return;
		}
		// First of all, check that all dependencies are properly enabled
		for (String dep : LogicUtil.fixNull(getDescription().getDepend(), (List<String>) Collections.EMPTY_LIST)) {
			if (!Bukkit.getPluginManager().isPluginEnabled(dep)) {
				log(Level.SEVERE, "Could not enable '" + getName() + " v" + getVersion() + "' because dependency '" + dep + "' failed to enable!");
				log(Level.SEVERE, "Perhaps the dependency has to be updated? Please check the log for any errors related to " + dep);
				Bukkit.getPluginManager().disablePlugin(this);
				return;
			}
		}

		long startTime = System.currentTimeMillis();
		if (this.getMinimumLibVersion() > Common.VERSION) {
			log(Level.SEVERE, "Requires a newer BKCommonLib version, please update BKCommonLib to the latest version!");
			log(Level.SEVERE, "Verify that there is only one BKCommonLib.jar in the plugins folder before retrying");
			Bukkit.getPluginManager().disablePlugin(this);
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
		this.permissionconfig.addHeader("http://dev.bukkit.org/server-mods/bkcommonlib/pages/general/permission-defaults/");

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
		this.localizationconfig.addHeader("http://dev.bukkit.org/server-mods/bkcommonlib/pages/general/localization/");

		// Load plugin.yml configuration
		try {
			this.pluginYaml.loadFromStream(getResource("plugin.yml"));
		} catch (Exception ex) {
			Bukkit.getLogger().log(Level.SEVERE, "[Configuration] An error occured while loading plugin.yml resource for plugin " + getName() + ":");
		}

		// Load all the commands for this Plugin
		Map<String, Map<String, Object>> commands = this.getDescription().getCommands();
		if (commands != null && PluginDescriptionFileRef.commands.isValid()) {
			// Prepare commands localization node
			ConfigurationNode commandsNode = getLocalizationNode("commands");

			// Create a new modifiable commands map to replace with
			commands = new HashMap<String, Map<String, Object>>(commands);
			for (Entry<String, Map<String, Object>> commandEntry : commands.entrySet()) {
				ConfigurationNode node = commandsNode.getNode(commandEntry.getKey());

				// Transfer description and usage
				Map<String, Object> data = new HashMap<String, Object>(commandEntry.getValue());
				node.shareWith(data, "description", "No description specified");
				node.shareWith(data, "usage", "/" + commandEntry.getKey());
				commandEntry.setValue(Collections.unmodifiableMap(data));
			}

			// Set the new commands map using reflection
			PluginDescriptionFileRef.commands.set(this.getDescription(), Collections.unmodifiableMap(commands));
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

		// ==== Enabling ====
		try {
			// Metrics
			if (this.pluginYaml.get("metrics", false)) {
				// Send anonymous statistics to mcstats.org
				try {
					this.metrics = new Metrics(this);
				} catch (IOException ex) {
					log(Level.SEVERE, "Failed to initialize metrics for " + getName());
					CommonUtil.printFilteredStackTrace(ex);
				}
			}

			this.wasDisableRequested = false;
			this.enable();
			if (this.wasDisableRequested) {
				// Plugin was disabled again while enabling
				return;
			}

			// Start Metrics if enabled
			if (metrics != null) {
				metrics.start();
			}

			// Done, this plugin is enabled
			this.enabled = true;
		} catch (Throwable t) {
			log(Level.SEVERE, "An error occurred while enabling, the plugin will be disabled:");
			handle(t);
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		// update dependencies
		CommonPlugin.getInstance().plugins.add(this);
		for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
			if (plugin.isEnabled()) {
				this.updateDependency(plugin, plugin.getName(), true);
			}
		}

		// Enable messages
		if (this.enableMessage != null) {
			log(Level.INFO, this.enableMessage);
		}
		Bukkit.getLogger().log(Level.INFO, this.getName() + " version " + this.getVersion() + " enabled! (" + MathUtil.round(0.001 * (System.currentTimeMillis() - startTime), 3) + "s)");
	}

	@Override
	public final void onDisable() {
		// are there any plugins that depend on me?
		for (Plugin plugin : Bukkit.getServer().getPluginManager().getPlugins()) {
			if (plugin.isEnabled() && CommonUtil.isDepending(plugin, this)) {
				Bukkit.getServer().getPluginManager().disablePlugin(plugin);
			}
		}
		this.wasDisableRequested = true;
		boolean doDisableMessage = this.disableMessage != null;
		if (this.enabled) {
			// Try to disable the plugin
			try {
				this.disable();
			} catch (Throwable t) {
				log(Level.SEVERE, "An error occurred while disabling:");
				StackTraceFilter.SERVER.print(t);
				doDisableMessage = false;
			}
			// Remove references to the plugin - it is disabled now
			this.enabled = false;
			if (CommonPlugin.hasInstance()) {
				CommonPlugin.getInstance().plugins.remove(this);
			}
		}
		// Disable Metrics if enabled
		if (metrics != null) {
			metrics.stop();
			metrics = null;
		}

		// If specified to do so, a disable message is shown
		if (doDisableMessage) {
			Bukkit.getLogger().log(Level.INFO, this.disableMessage);
		}
	}

	@Override
	public final boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String command, String[] args) {
		try {
			String[] fixedArgs = StringUtil.convertArgs(args);
			// Default commands for all plugins
			if (fixedArgs.length >= 1 && LogicUtil.contains(fixedArgs[0].toLowerCase(Locale.ENGLISH), "version", "ver")) {
				sender.sendMessage(ChatColor.GREEN + this.getName() + " v" + this.getVersion() + " using BKCommonLib v" + CommonPlugin.getInstance().getVersion());
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
			Bukkit.getLogger().log(Level.SEVERE, msg.toString());
			t.printStackTrace();
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

	public final void loadLocalization() {
		this.localizationconfig.load();
	}

	/**
	 * Obtains a configuration instance managed by this PluginBase containing the contents of the plugin.yml
	 * 
	 * @return plugin.yml configuration
	 */
	public final BasicConfiguration getPluginYaml() {
		return this.pluginYaml;
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
	};
}
