package com.bergerkiller.bukkit.common;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;

/**
 * A logger that is meant to log a given module of the server, for example that of a Plugin
 */
public class ModuleLogger extends Logger {
	private final String[] modulePath;
	private final String prefix;

	public ModuleLogger(Plugin plugin, String... modulePath) {
		this(LogicUtil.appendArray(new String[] {getPrefix(plugin)}, modulePath));
	}

	public ModuleLogger(String... modulePath) {
		this(Bukkit.getLogger(), modulePath);
	}

	public ModuleLogger(Logger parent, String... modulePath) {
		super(StringUtil.join(".", modulePath), null);
		this.setParent(parent);
		this.setLevel(Level.ALL);
		this.modulePath = modulePath;
		StringBuilder builder = new StringBuilder();
		for (String module : modulePath) {
			builder.append("[").append(module).append("] ");
		}
		this.prefix = builder.toString();
	}

	private static String getPrefix(Plugin plugin) {
		return LogicUtil.fixNull(plugin.getDescription().getPrefix(), plugin.getDescription().getName());
	}

	/**
	 * Obtains a Module Logger for the path specified
	 * 
	 * @param path to get the Module Logger for
	 * @return new Module Logger pointing to the path relative to this Module Logger
	 */
	public ModuleLogger getModule(String... path) {
		return new ModuleLogger(this.getParent(), LogicUtil.appendArray(this.modulePath, path));
	}

    @Override
    public void log(LogRecord logRecord) {
        logRecord.setMessage(this.prefix + logRecord.getMessage());
        super.log(logRecord);
    }
}
