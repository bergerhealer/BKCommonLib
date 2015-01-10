package com.bergerkiller.bukkit.common.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.common.ModuleLogger;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.StreamUtil;

public class FileConfiguration extends BasicConfiguration {
	private static final ModuleLogger LOGGER = new ModuleLogger("Configuration");
	private final File file;

	public FileConfiguration(JavaPlugin plugin) {
		this(plugin, "config.yml");
	}

	public FileConfiguration(JavaPlugin plugin, String filepath) {
		this(new File(CommonUtil.getPluginDataFolder(plugin), filepath));
	}

	public FileConfiguration(String filepath) {
		this(new File(filepath));
	}

	public FileConfiguration(final File file) {
		if (file == null) {
			throw new IllegalArgumentException("File is not allowed to be null!");
		}
		this.file = file;
	}

	public boolean exists() {
		return this.file.exists();
	}

	/**
	 * Loads this File Configuration from file
	 */
	public void load() {
		// Ignore loading if file doesn't exist
		if (!file.exists()) {
			return;
		}
		try {
			this.loadFromStream(new FileInputStream(this.file));
		} catch (Throwable t) {
			LOGGER.log(Level.SEVERE, "An error occured while loading file '" + this.file + "'");
			try {
				File backup = new File(this.file.getPath() + ".old");
				StreamUtil.copyFile(this.file, backup);
				LOGGER.log(Level.SEVERE, "A backup of this (corrupted?) file named '" + backup.getName() + "' can be found in case you wish to restore", t);
			} catch (IOException ex) {
				LOGGER.log(Level.SEVERE, "A backup of this (corrupted?) file could not be made and its contents may be lost (overwritten)", t);
			}
		}
	}

	/**
	 * Saves this File Configuration to file
	 */
	public void save() {
		try {
			boolean regen = !this.exists();
			this.saveToStream(StreamUtil.createOutputStream(this.file));
			if (regen) {
				Bukkit.getLogger().log(Level.INFO, "[Configuration] File '" + this.file + "' has been generated");
			}
		} catch (Exception ex) {
			Bukkit.getLogger().log(Level.SEVERE, "[Configuration] An error occured while saving to file '" + this.file + "':");
			ex.printStackTrace();
		}
	}
}
