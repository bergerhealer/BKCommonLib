package com.bergerkiller.bukkit.common;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.common.utils.StringUtil;

@SuppressWarnings("unused")
public abstract class PluginBase extends JavaPlugin implements Listener { 
	private final int minbuild;
	private final int maxbuild;
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
