package com.bergerkiller.bukkit.common;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.config.FileConfiguration;
import com.bergerkiller.bukkit.common.utils.EnumUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;

@SuppressWarnings("unused")
public abstract class PluginBase extends JavaPlugin implements Listener { 
	private final int minbuild;
	private final int maxbuild;
	private FileConfiguration permissionconfig;
	private static final int cbBuild;
	static {
		int build = 0;
		String ver = Bukkit.getVersion();
		int idx = ver.lastIndexOf('-');
		if (idx != -1 && (ver.length() - idx) > 2) {
			ver = ver.substring(idx + 2);
			StringBuilder builder = new StringBuilder(4);
			for (byte b : ver.getBytes()) {
				if (b < 48 || b > 57) {
					break;
				} else {
					builder.append((char) b);
				}
			}
			ver = builder.toString();
			try {
				build = Integer.parseInt(ver);
			} catch (NumberFormatException ex) {}	    	
		}
		cbBuild = build;
	}
	public static int getCraftBukkitBuild() {
		return cbBuild;
	}

	public PluginBase(final int minbuild, final int maxbuild) {
		super();
		this.minbuild = minbuild;
		this.maxbuild = maxbuild;
	}

	public void log(Level level, String message) {
		Bukkit.getLogger().log(level, "[" + this.getName() + "] " + message);
	}

	public final String getVersion() {
		return this.getDescription().getVersion();
	}
	public final String getName() {
		return this.getDescription().getName();
	}
	public final void register(String... commands) {
		this.register(this, commands);
	}
	public final void register(CommandExecutor executor, String... commands) {
		for (String command : commands) {
			this.getCommand(command).setExecutor(executor);
		}
	}
	public final void register(Listener listener) {
		if (listener == this) return;
		Bukkit.getPluginManager().registerEvents(listener, this);
	}
	public final void register(Class<? extends Listener> listener) {
		try {
			this.register(listener.newInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public final void registerNextTick(Class<? extends Listener> listener) {
		try {
			this.registerNextTick(listener.newInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public final void registerNextTick(final Listener listener) {
		final PluginBase me = this;
		new Task() {
			public void run() {
				me.register(listener);
			}
		}.start();
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
	public final void loadPermission(String permissionnode, PermissionDefault def, String description) {
		this.loadPermission(getPermissionNode(permissionnode), def, description);
	}
	public final void loadPermission(ConfigurationNode permissionnode, PermissionDefault def, String description) {
		description = permissionnode.get("description", description);
		//permission default can be true/false too!
		Object setvalue = permissionnode.get("default");
		if (setvalue != null) {
			if (setvalue instanceof String) {
				def = EnumUtil.parsePermissionDefault((String) setvalue, def);
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
		setPermission(permissionnode.getPath(), def, description);
	}
	public static final void setPermission(String permissionnode, PermissionDefault def, String description) {
		setPermission(getPermission(permissionnode), def, description);
	}
	public static final void setPermission(Permission permission, PermissionDefault def, String description) {
		if (def != null) permission.setDefault(def);
		if (description != null) permission.setDescription(description);
	}

	public abstract void permissions();

	public final void onEnable() {
		if (cbBuild == 0) {
			Bukkit.getLogger().log(Level.WARNING, this.getName() + " version " + this.getVersion() + " may not be compatible: Unknown CraftBukkit version");
		} else if (cbBuild < this.minbuild) {
			Bukkit.getLogger().log(Level.SEVERE, "Plugin has not been enabled!");
			Bukkit.getLogger().log(Level.SEVERE, "CraftBukkit build " + cbBuild + " is too old for plugin '" + this.getName() + "' v" + this.getVersion());
			Bukkit.getLogger().log(Level.SEVERE, "Update CraftBukkit to a newer build or look for an older version of " + this.getName());
			//cb is too old
			return;
		} else if (cbBuild > this.maxbuild) {
			Bukkit.getLogger().log(Level.SEVERE, "Plugin has not been enabled!");
			Bukkit.getLogger().log(Level.SEVERE, "Plugin '" + this.getName() + "' v" + this.getVersion() + " is too old to run on CraftBukkit build " + cbBuild);
			Bukkit.getLogger().log(Level.SEVERE, "Update " + this.getName() + " to a newer version or look for an older build of CraftBukkit");
			//this plugin is too old
			return;
		}
		//update dependencies
		Bukkit.getPluginManager().registerEvents(this, this);
		for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
			if (!plugin.isEnabled()) continue;
			this.updateDependency(plugin, true);
		}
		this.permissionconfig = new FileConfiguration(this, "PermissionDefaults.yml");
		if (this.permissionconfig.exists()) {
			this.loadPermissions();
		}
		//header
		this.permissionconfig.setHeader("Below are the default permissions set for plugin '" + this.getName() + "'.");
		this.permissionconfig.addHeader("These permissions are ignored if the permission is set for a group or player.");
		this.permissionconfig.addHeader("Use the defaults as a base to keep the permissions file small");
		
		this.permissions();
		if (!this.permissionconfig.isEmpty()) {
			this.savePermissions();
		}
		this.enable();
		Bukkit.getLogger().log(Level.INFO, this.getName() + " version " + this.getVersion() + " enabled!");
	}
	public final void onDisable() {
		if (cbBuild == 0 || (this.minbuild >= cbBuild && this.maxbuild <= cbBuild)) {
			this.disable();
			Bukkit.getLogger().log(Level.INFO, this.getName() + " disabled!");
		}
	}
	public final boolean onCommand(CommandSender sender, Command cmd, String command, String[] args) {
		return command(sender, command, StringUtil.convertArgs(args));
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
	public void updateDependency(Plugin plugin, String pluginName, boolean enabled) {};

	@EventHandler(priority = EventPriority.MONITOR)
	private void onPluginEnable(final PluginEnableEvent event) {
		this.updateDependency(event.getPlugin(), true);
	}
	@EventHandler(priority = EventPriority.MONITOR)
	private void onPluginDisable(PluginDisableEvent event) {
		this.updateDependency(event.getPlugin(), false);
	}

}
