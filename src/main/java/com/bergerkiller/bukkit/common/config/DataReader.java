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

/**
 * An abstract version of a Data reading class used to read from a source File
 */
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

	/**
	 * Is called to read the payload from a prepared data stream
	 * 
	 * @param stream to read from
	 * @throws IOException
	 */
	public abstract void read(DataInputStream stream) throws IOException;

	/**
	 * Gets the Preferred Data stream from an Input stream<br>
	 * Can add additional stream logic
	 * 
	 * @param stream to get the data stream for
	 * @return Data stream
	 */
	public DataInputStream getStream(InputStream stream) {
		return new DataInputStream(stream);
	}

	/**
	 * Gets the File from which is read
	 * 
	 * @return source File
	 */
	public File getFile() {
		return this.file;
	}

	/**
	 * Checks if the file to read from exists
	 * 
	 * @return True if it exists, False if it doesn't
	 */
	public boolean exists() {
		return this.file.exists();
	}

	/**
	 * Performs the actual reading from the file
	 * 
	 * @return True if reading was successful, False if not (file not found, or other error)
	 */
	public boolean read() {
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
			return true;
		} catch (FileNotFoundException ex) {
			// nothing, we allow non-existence of this file
		} catch (Throwable t) {
			Bukkit.getLogger().log(Level.SEVERE, "[Configuration] An error occured while loading file '" + this.file + "':");
			t.printStackTrace();
		}
		return false;
	}
}
