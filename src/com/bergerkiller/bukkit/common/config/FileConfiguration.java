package com.bergerkiller.bukkit.common.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
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

	private void writeHeader(int indent, String header, BufferedWriter writer) throws IOException {
		if (header == null) return;
		for (String line : header.split("\n", -1)) {
			StreamUtil.writeIndent(writer, indent * this.getIndent());
		    if (line.trim().length() > 0) {
		    	writer.write("# ");
		    	writer.write(line);
		    }
		    writer.newLine();
		}
	}
	
	public void setIndent(int indent) {
		this.getSource().options().indent(indent);
	}
	public int getIndent() {
		return this.getSource().options().indent();
	}
	
	public void load() {
		try {
			FileInputStream stream = new FileInputStream(this.file);

			InputStreamReader reader = new InputStreamReader(stream);
			StringBuilder builder = new StringBuilder();
			BufferedReader input = new BufferedReader(reader);

			try {
				String line;
				List<String> nodes = new ArrayList<String>();
				StringBuilder header = null;
				boolean readfirstheader = false;
				while ((line = input.readLine()) != null) {
					if (!readfirstheader) {
						String h = line.trim();
						if (h.length() == 0) {
							if (header == null) {
								header = new StringBuilder();
							} else {
								header.append('\n');
							}
							continue;
						} else if (h.startsWith("#")) {
							h = h.substring(1).trim();
							if (header == null) {
								header = new StringBuilder(h);
							} else {
								header.append('\n').append(h);
							}
							continue;
						} else {
							if (header != null) {
								this.setHeader(header.toString());
								header = null;
							}
							readfirstheader = true;
						}
					}
					if (line.trim().length() == 0) {
						if (header == null) {
							header = new StringBuilder();
						} else {
							header.append("\n ");
						}
						continue;
					}
					//get indent
					int indent = StringUtil.getSuccessiveCharCount(line, ' ');
					if (indent % 2 == 0 && line.length() > indent && line.charAt(indent) != '-') {
						if (line.charAt(indent) == '#') {
							String h = line.substring(indent + 1).trim();
							if (header != null) {
								header.append('\n').append(h);
							} else {
								header = new StringBuilder(h);
							}
							continue;
						} else if (header != null) {
							int startindex = indent;
							indent >>= 1;
							int endindex = line.indexOf(':', startindex);
							if (endindex >= 0) {
								String varname = line.substring(startindex, endindex);
								//get current path
								if (indent >= nodes.size()) {
									indent = nodes.size();
									nodes.add(varname);
								} else {
									nodes.set(indent, varname);
									for (int i = indent + 1; i < nodes.size(); i++) {
										nodes.remove(indent + 1);
									}
								}
								this.setHeader(StringUtil.combine(".", nodes), header.toString());
								header = null;
							}
						}
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
	public void save() {
		try {
			boolean regen = !this.exists();
			this.file.getAbsoluteFile().getParentFile().mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(this.file));
			try {
				//write first header
				writeHeader(0, this.getHeader(), writer);
				
				List<String> nodes = new ArrayList<String>();
				for (String element : this.getSource().saveToString().split("\n", -1)) {
					//get indent
					int indent = StringUtil.getSuccessiveCharCount(element, ' ');
					if (indent % this.getIndent() == 0 && element.length() > indent + 1 && element.charAt(indent) != '-') {
						int startindex = indent;
						indent /= this.getIndent();
						int endindex = element.indexOf(':', startindex);
						if (endindex >= 0) {
							String varname = element.substring(startindex, endindex);
							//get current path
							if (indent >= nodes.size()) {
								indent = nodes.size();
							} else {
								while (nodes.size() > indent) {
									nodes.remove(nodes.size() - 1);
								}
							}
							nodes.add(varname);
							String header = this.getHeader(StringUtil.combine(".", nodes));
							writeHeader(indent, header, writer);
						}
					}
					writer.write(element);
					writer.newLine();
				}
			} finally {
				writer.close();
			}
			if (regen) Bukkit.getLogger().log(Level.INFO, "[Configuration] File '" + this.file + "' has been generated");
		} catch (Exception ex) {
			Bukkit.getLogger().log(Level.SEVERE, "[Configuration] An error occured while saving to file '" + this.file + "':");
			ex.printStackTrace();
		}
	}

}
