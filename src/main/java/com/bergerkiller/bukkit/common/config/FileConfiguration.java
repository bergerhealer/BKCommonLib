package com.bergerkiller.bukkit.common.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class FileConfiguration extends BasicConfiguration {
	private final File file;

	public FileConfiguration(JavaPlugin plugin) {
		this(plugin, "config.yml");
	}

	public FileConfiguration(JavaPlugin plugin, String filepath) {
		this(plugin.getDataFolder() + File.separator + filepath);
	}

	public FileConfiguration(String filepath) {
		this(new File(filepath));
	}

	public FileConfiguration(final File file) {
		this.file = file;
	}

	public boolean exists() {
		return this.file.exists();
	}

	/**
	 * Loads this File Configuration from file
	 */
	public void load() {
		/** We can't load a file that is null or does not exist */
		if(this.file == null)
			throw new IllegalArgumentException("File is null!");
		
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				Bukkit.getLogger().log(Level.SEVERE, "[Configuration] An error occured while creating file '" + this.file + "':");
				e.printStackTrace();
			}
		}
		
		try {
			this.loadFromStream(new FileInputStream(this.file));
		} catch (Exception ex) {
			Bukkit.getLogger().log(Level.SEVERE, "[Configuration] An error occured while loading file '" + this.file + "':");
			ex.printStackTrace();
		}
	}

	/**
	 * Saves this File Configuration to file
	 */
	public void save() {
		try {
			boolean regen = !this.exists();
			this.file.getAbsoluteFile().getParentFile().mkdirs();
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
