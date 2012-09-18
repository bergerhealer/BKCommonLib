package com.bergerkiller.bukkit.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.config.FileConfiguration;
import com.bergerkiller.bukkit.common.permissions.IPermissionDefault;
import com.bergerkiller.bukkit.common.permissions.NoPermissionException;
import com.bergerkiller.bukkit.common.utils.EnumUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;

@SuppressWarnings("unused")
public abstract class PluginBase extends JavaPlugin {
	private String disableMessage = null;
	private FileConfiguration permissionconfig;
	private ArrayList<Command> commands = new ArrayList<Command>();

	static List<PluginBase> plugins = new ArrayList<PluginBase>();

	public PluginBase() {
		super();
	}

	@Deprecated
	public PluginBase(int minbuild, int maxbuild) {
		super();
	}

	public void log(Level level, String message) {
		Bukkit.getLogger().log(level, "[" + this.getName() + "] " + message);
	}

	public final String getVersion() {
		return this.getDescription().getVersion();
	}

	public final void register(String... commands) {
		this.register(this, commands);
	}

	public final void register(CommandExecutor executor, String... commands) {
		for (String command : commands) {
			PluginCommand cmd = this.getCommand(command);
			if (cmd != null)
				cmd.setExecutor(executor);
		}
	}

	public final void register(Listener listener) {
		if (listener == this)
			return;
		Bukkit.getPluginManager().registerEvents(listener, this);
	}

	public final void register(Class<? extends Listener> listener) {
		try {
			this.register(listener.newInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public final <T extends Command> T register(T command) {
		this.commands.add(command);
		return command;
	}

	public static Permission getPermission(String permissionnode) {
		Permission perm = Bukkit.getServer().getPluginManager().getPermission(permissionnode);
		if (perm == null) {
			perm = new Permission(permissionnode, PermissionDefault.FALSE);
			Bukkit.getServer().getPluginManager().addPermission(perm);
		}
		return perm;
	}

	public final ConfigurationNode getPermissionNode() {
		return this.permissionconfig;
	}

	public final ConfigurationNode getPermissionNode(String path) {
		return this.permissionconfig.getNode(path);
	}

	public final void loadPermissions(Class<? extends IPermissionDefault> permissionDefaults) {
		for (IPermissionDefault perm : permissionDefaults.getEnumConstants()) {
			this.loadPermission(perm);
		}
	}

	public final void loadPermission(IPermissionDefault permissionDefault) {
		this.loadPermission(permissionDefault.getName(), permissionDefault.getDefault(), permissionDefault.getDescription());
	}

	public final Permission loadPermission(String permissionnode) {
		return this.loadPermission(getPermission(permissionnode));
	}

	public final Permission loadPermission(Permission permission) {
		return this.loadPermission(permission.getName(), permission.getDefault(), permission.getDescription());
	}

	public final Permission loadPermission(String permissionnode, PermissionDefault def, String description) {
		return this.loadPermission(getPermissionNode(permissionnode), def, description);
	}

	public final Permission loadPermission(ConfigurationNode permissionnode, PermissionDefault def, String description) {
		description = permissionnode.get("description", description);
		// permission default can be true/false too!
		Object setvalue = permissionnode.get("default");
		if (setvalue != null) {
			if (setvalue instanceof String) {
				def = EnumUtil.parse((String) setvalue, def);
				permissionnode.set("default", def.toString());
			} else if (setvalue instanceof Boolean) {
				boolean val = (Boolean) setvalue;
				if (val) {
					def = PermissionDefault.TRUE;
				} else {
					def = PermissionDefault.FALSE;
				}
			} else {
				setvalue = null;
			}
		}
		if (setvalue == null) {
			if (def == PermissionDefault.TRUE) {
				permissionnode.set("default", true);
			} else if (def == PermissionDefault.FALSE) {
				permissionnode.set("default", false);
			} else {
				permissionnode.set("default", def.toString());
			}
		}
		return setPermission(permissionnode.getPath(), def, description);
	}

	public static Permission setPermission(String permissionnode, PermissionDefault def, String description) {
		Permission perm = getPermission(permissionnode);
		setPermission(perm, def, description);
		return perm;
	}

	public static void setPermission(Permission permission, PermissionDefault def, String description) {
		if (def != null)
			permission.setDefault(def);
		if (description != null)
			permission.setDescription(description);
	}

	public abstract void permissions();

	public final String getDisableMessage() {
		return this.disableMessage;
	}

	public void setDisableMessage(String msg) {
		this.disableMessage = msg;
	}

	/**
	 * Gets the minimum BKCommonLib version required for this Plugin to function
	 * 
	 * @return Minimum BKCommonLib version number
	 */
	public int getMinimumLibVersion() {
		return 0;
	}

	/**
	 * Handles a possible throwable thrown somewhere in the Plugin<br>
	 * If the throwable is too severe, the plugin is automatically disabled<br>
	 * Additional exception types can be handled if needed
	 * 
	 * @param reason to throw
	 */
	public void handle(Throwable reason) {
		if (reason instanceof Exception) {
			reason.printStackTrace();
		} else if (reason instanceof NoClassDefFoundError) {
			log(Level.WARNING, "Class is missing (plugin was hot-swapped?): " + reason.getMessage());
		} else {
			log(Level.SEVERE, "Encountered a critical error and had to be disabled.");
			log(Level.SEVERE, "You may have to update " + this.getName() + " or look for a newer CraftBukkit build.");
			reason.printStackTrace();
			Bukkit.getPluginManager().disablePlugin(this);
		}
	}

	public final void onEnable() {
		if (this.getMinimumLibVersion() > Common.VERSION) {
			log(Level.SEVERE, "Requires a newer BKCommonLib version, please update BKCommonLib to the latest version!");
			log(Level.SEVERE, "Verify that there is only one BKCommonLib.jar in the plugins folder before retrying");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		this.setDisableMessage(this.getName() + " disabled!");
		this.permissionconfig = new FileConfiguration(this, "PermissionDefaults.yml");
		if (this.permissionconfig.exists()) {
			this.loadPermissions();
		}
		// header
		this.permissionconfig.setHeader("Below are the default permissions set for plugin '" + this.getName() + "'.");
		this.permissionconfig.addHeader("These permissions are ignored if the permission is set for a group or player.");
		this.permissionconfig.addHeader("Use the defaults as a base to keep the permissions file small");
		this.permissions();
		if (!this.permissionconfig.isEmpty()) {
			this.savePermissions();
		}

		try {
			this.enable();
		} catch (Throwable t) {
			log(Level.SEVERE, "An error occurred while enabling, the plugin will be disabled");
			log(Level.SEVERE, "You may have to update " + this.getName() + " or look for a newer CraftBukkit build.");
			t.printStackTrace();
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		// update dependencies
		plugins.add(this);
		for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
			if (!plugin.isEnabled())
				continue;
			this.updateDependency(plugin, true);
		}

		Bukkit.getLogger().log(Level.INFO, this.getName() + " version " + this.getVersion() + " enabled!");
	}

	public final void onDisable() {
		// are there any plugins that depend on me?
		List<String> depend;
		for (Plugin plugin : Bukkit.getServer().getPluginManager().getPlugins()) {
			if (!plugin.isEnabled())
				continue;
			depend = (List<String>) plugin.getDescription().getDepend();
			if (depend != null && depend.contains(this.getName())) {
				Bukkit.getServer().getPluginManager().disablePlugin(plugin);
			}
		}

		try {
			this.disable();
		} catch (Throwable t) {
			log(Level.SEVERE, "An error occurred while disabling:");
			t.printStackTrace();
			plugins.remove(this);
			return;
		}

		plugins.remove(this);

		if (this.disableMessage != null) {
			Bukkit.getLogger().log(Level.INFO, this.disableMessage);
		}
	}

	public final boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String command, String[] args) {
		try {
			args = StringUtil.convertArgs(args);
			if (!this.commands.isEmpty()) {
				ArrayList<String> arglist = new ArrayList<String>(args.length + 1);
				arglist.add(command);
				for (String arg : args) {
					arglist.add(arg);
				}
				for (Command ccc : this.commands) {
					if (ccc.execute(sender, arglist)) {
						return true;
					}
				}
			}
			return command(sender, command, args);
		} catch (NoPermissionException ex) {
			if (sender instanceof Player) {
				sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
			} else {
				sender.sendMessage("This command is only for players!");
			}
			return true;
		} catch (Throwable t) {
			StringBuilder msg = new StringBuilder("Unhandled exception executing command '");
			msg.append(command).append("' in plugin ").append(this.getName()).append(" v").append(this.getVersion());
			Bukkit.getLogger().log(Level.SEVERE, msg.toString());
			t.printStackTrace();
			sender.sendMessage(ChatColor.RED + "An internal error occured while executing this command");
			return true;
		}
	}

	public abstract void enable();

	public abstract void disable();

	public abstract boolean command(CommandSender sender, String command, String[] args);

	public final void loadPermissions() {
		this.permissionconfig.load();
	}

	public final void savePermissions() {
		this.permissionconfig.save();
	}

	public final void updateDependency(Plugin plugin, boolean enabled) {
		this.updateDependency(plugin, plugin.getDescription().getName(), enabled);
	}

	public void updateDependency(Plugin plugin, String pluginName, boolean enabled) {
	};

}
