package com.bergerkiller.bukkit.common.config;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public abstract class DataWriter {

	private final File file;
	public DataWriter(Plugin plugin, String filename) {
		this(plugin.getDataFolder(), filename);
	}
	public DataWriter(File folder, String filename) {
		this(new File(folder, filename));
	}
	public DataWriter(String filepath) {
		this(new File(filepath));
	}
	public DataWriter(final File file) {
		this.file = file;
	}
	
	public abstract void write(DataOutputStream stream) throws IOException;
	
	public DataOutputStream getStream(OutputStream stream) {
		return new DataOutputStream(stream);
	}
	
	public final void write() {
		try {
			DataOutputStream stream = this.getStream(new FileOutputStream(this.file));
			try {
				this.write(stream);
			} catch (IOException ex) {
				Bukkit.getLogger().log(Level.SEVERE, "[Configuration] An IO Exception occured while saving file '" + this.file + "':");
				ex.printStackTrace();
			} catch (Throwable t) {
				Bukkit.getLogger().log(Level.SEVERE, "[Configuration] An error occured while savingg file '" + this.file + "':");
				t.printStackTrace();
			} finally {
				stream.close();
			}
		} catch (FileNotFoundException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "[Configuration] Failed to access file '" + this.file + "' for saving:");
			ex.printStackTrace();
		} catch (Throwable t) {
			Bukkit.getLogger().log(Level.SEVERE, "[Configuration] Failed to save to file '" + this.file + "':");
			t.printStackTrace();
		}
	}
	
}
