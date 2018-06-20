package com.bergerkiller.bukkit.common.config;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.StreamUtil;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;

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
        	Logging.LOGGER_CONFIG.log(Level.SEVERE, "An error occured while loading file '" + this.file + "'");
            try {
                File backup = new File(this.file.getPath() + ".old");
                StreamUtil.copyFile(this.file, backup);
                Logging.LOGGER_CONFIG.log(Level.SEVERE, "A backup of this (corrupted?) file named '" + backup.getName() + "' can be found in case you wish to restore", t);
            } catch (IOException ex) {
            	Logging.LOGGER_CONFIG.log(Level.SEVERE, "A backup of this (corrupted?) file could not be made and its contents may be lost (overwritten)", t);
            }
        }
    }

    /**
     * Saves this File Configuration to file
     */
    public void save() {
        try {
            // First save to a .tmp file
            File tmp_file = new File(this.file.getAbsolutePath() + ".tmp");
            this.saveToStream(StreamUtil.createOutputStream(tmp_file));
            if (this.exists()) {
                if (this.file.delete()) {
                    // Rename tmp file to file
                    tmp_file.renameTo(this.file);
                } else {
                    // Attempt stream copying from the tmp file to the new file
                    // Then delete the tmp file
                    StreamUtil.tryCopyFile(tmp_file, this.file);
                    tmp_file.delete();
                }
            } else {
                tmp_file.renameTo(this.file);
                Logging.LOGGER_CONFIG.log(Level.INFO, "File '" + this.file + "' has been generated");
            }
        } catch (Exception ex) {
        	Logging.LOGGER_CONFIG.log(Level.SEVERE, "An error occured while saving to file '" + this.file + "':");
            ex.printStackTrace();
        }
    }
}
