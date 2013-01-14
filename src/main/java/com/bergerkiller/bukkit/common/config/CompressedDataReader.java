package com.bergerkiller.bukkit.common.config;

import java.io.DataInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;

import org.bukkit.plugin.Plugin;

/**
 * A compression based Data reader
 */
public abstract class CompressedDataReader extends DataReader {

	public CompressedDataReader(File folder, String filename) {
		super(folder, filename);
	}

	public CompressedDataReader(File file) {
		super(file);
	}

	public CompressedDataReader(Plugin plugin, String filename) {
		super(plugin, filename);
	}

	public CompressedDataReader(String filepath) {
		super(filepath);
	}

	@Override
	public DataInputStream getStream(InputStream stream) {
		return super.getStream(new InflaterInputStream(stream));
	}
}
