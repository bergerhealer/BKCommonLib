package com.bergerkiller.bukkit.common.config;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public abstract class DataReader {
	
	private final File file;
	public DataReader(Plugin plugin, String filename) {
		this(plugin.getDataFolder(), filename);
	}
	public DataReader(File folder, String filename) {
		this(new File(folder, filename));
	}
	public DataReader(String filepath) {
		this(new File(filepath));
	}
	public DataReader(final File file) {
		this.file = file;
	}
	
	public abstract void read(DataInputStream stream) throws IOException;
	
	public DataInputStream getStream(InputStream stream) {
		return new DataInputStream(stream);
	}
	
	public File getFile() {
		return this.file;
	}
	public boolean exists() {
		return this.file.exists();
	}
	
	public final void read() {
		try {
			DataInputStream stream = getStream(new FileInputStream(this.file));
			try {
				this.read(stream);
			} catch (IOException ex) {
				Bukkit.getLogger().log(Level.SEVERE, "[Configuration] An IO Exception occured while loading file '" + this.file + "':");
				ex.printStackTrace();
			} catch (Throwable t) {
				Bukkit.getLogger().log(Level.SEVERE, "[Configuration] An error occured while loading file '" + this.file + "':");
				t.printStackTrace();
			} finally {
				stream.close();
			}
		} catch (FileNotFoundException ex) {
			//nothing, we allow non-existence of this file
		} catch (Throwable t) {
			Bukkit.getLogger().log(Level.SEVERE, "[Configuration] An error occured while loading file '" + this.file + "':");
			t.printStackTrace();
		}
	}
	
}
