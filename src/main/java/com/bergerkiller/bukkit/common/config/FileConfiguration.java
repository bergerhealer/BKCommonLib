package com.bergerkiller.bukkit.common.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.common.utils.CommonUtil;

public class FileConfiguration extends BasicConfiguration {
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
			Bukkit.getLogger().log(Level.SEVERE, "[Configuration] An error occured while loading file '" + this.file + "':");
			t.printStackTrace();
		}
	}

	/**
	 * Saves this File Configuration to file
	 */
	public void save() {
		try {
			boolean regen = !this.exists();
			this.file.getAbsoluteFile().getParentFile().mkdirs();
			if (!this.file.exists()) {
				this.file.createNewFile();
			}
			this.saveToStream(new FileOutputStream(this.file));
			if (regen) {
				Bukkit.getLogger().log(Level.INFO, "[Configuration] File '" + this.file + "' has been generated");
			}
		} catch (Exception ex) {
			Bukkit.getLogger().log(Level.SEVERE, "[Configuration] An error occured while saving to file '" + this.file + "':");
			ex.printStackTrace();
		}
	}
}
