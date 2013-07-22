package com.bergerkiller.bukkit.common.config;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.bergerkiller.bukkit.common.utils.StreamUtil;

/**
 * An abstract version of a Data writing class used to write to a destination File
 */
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

	/**
	 * Is called to write the payload to a prepared data stream
	 * 
	 * @param stream to write to
	 * @throws IOException
	 */
	public abstract void write(DataOutputStream stream) throws IOException;

	/**
	 * Gets the Preferred Data stream from an Output stream<br>
	 * Can add additional stream logic
	 * 
	 * @param stream to get the data stream for
	 * @return Data stream
	 */
	public DataOutputStream getStream(OutputStream stream) {
		return new DataOutputStream(stream);
	}

	/**
	 * Performs the actual writing to the file
	 * 
	 * @return True if writing was successful, False if not
	 */
	public boolean write() {
		try {
			DataOutputStream stream = this.getStream(StreamUtil.createOutputStream(this.file));
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
			return true;
		} catch (FileNotFoundException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "[Configuration] Failed to access file '" + this.file + "' for saving:");
			ex.printStackTrace();
		} catch (Throwable t) {
			Bukkit.getLogger().log(Level.SEVERE, "[Configuration] Failed to save to file '" + this.file + "':");
			t.printStackTrace();
		}
		return false;
	}
}
