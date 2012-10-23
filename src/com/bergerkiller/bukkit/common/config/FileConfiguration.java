package com.bergerkiller.bukkit.common.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.common.utils.StreamUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;

public class FileConfiguration extends ConfigurationNode {

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
	 * Sets the indentation of sub-nodes
	 * 
	 * @param indent size
	 */
	public void setIndent(int indent) {
		this.getSource().options().indent(indent);
	}

	/**
	 * Gets the indentation of sub-nodes
	 * 
	 * @return indent size
	 */
	public int getIndent() {
		return this.getSource().options().indent();
	}

	/**
	 * Loads this File Configuration from file
	 */
	public void load() {
		try {
			FileInputStream stream = new FileInputStream(this.file);

			InputStreamReader reader = new InputStreamReader(stream);
			StringBuilder builder = new StringBuilder();
			BufferedReader input = new BufferedReader(reader);

			try {
				String line, trimmedLine;
				HeaderBuilder header = new HeaderBuilder();
				NodeBuilder node = new NodeBuilder(this.getIndent());
				int indent;
				while ((line = input.readLine()) != null) {
					indent = StringUtil.getSuccessiveCharCount(line, ' ');
					trimmedLine = line.substring(indent);
					// Handle a header line
					if (header.handle(trimmedLine)) {
						continue;
					}
					// Handle a node line
					node.handle(trimmedLine, indent);
					// Apply the header to a node if available
					if (header.hasHeader()) {
						this.setHeader(node.getPath(), header.getHeader());
						header.clear();
					}
					builder.append(line).append('\n');
				}
			} finally {
				input.close();
			}
			this.getSource().loadFromString(builder.toString());
		} catch (FileNotFoundException ex) {
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
			BufferedWriter writer = new BufferedWriter(new FileWriter(this.file));
			try {
				String trimmedLine;
				int indent;
				NodeBuilder node = new NodeBuilder(this.getIndent());
				for (String line : this.getSource().saveToString().split("\n", -1)) {
					indent = StringUtil.getSuccessiveCharCount(line, ' ');
					trimmedLine = line.substring(indent);
					// Handle a node
					if (node.handle(trimmedLine, indent)) {
						String header = this.getHeader(node.getPath());
						if (header != null) {
							for (String headerLine : header.split("\n", -1)) {
								StreamUtil.writeIndent(writer, indent);
								if (headerLine.trim().length() > 0) {
									writer.write("# ");
									writer.write(headerLine);
								}
								writer.newLine();
							}
						}
					}
					writer.write(line);
					writer.newLine();
				}
			} finally {
				writer.close();
			}
			if (regen)
				Bukkit.getLogger().log(Level.INFO, "[Configuration] File '" + this.file + "' has been generated");
		} catch (Exception ex) {
			Bukkit.getLogger().log(Level.SEVERE, "[Configuration] An error occured while saving to file '" + this.file + "':");
			ex.printStackTrace();
		}
	}

}
