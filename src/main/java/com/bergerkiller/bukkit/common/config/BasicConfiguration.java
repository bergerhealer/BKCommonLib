package com.bergerkiller.bukkit.common.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;

import org.bukkit.configuration.InvalidConfigurationException;

import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.common.utils.StreamUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap;

/**
 * A basic YAML configuration implementation
 */
public class BasicConfiguration extends ConfigurationNode {
	public static final String MAIN_HEADER_PREFIX = "#> ";

	@Override
	public String getPath() {
		return "";
	}

	@Override
	public String getPath(String append) {
		return LogicUtil.nullOrEmpty(append) ? "" : append;
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
	 * Loads this configuration from stream<br>
	 * Note: Closes the stream when finished
	 * 
	 * @param stream to read from
	 */
	public void loadFromStream(InputStream stream) throws IOException {
		try {
			InputStreamReader reader = new InputStreamReader(stream);
			StringBuilder builder = new StringBuilder();
			BufferedReader input = new BufferedReader(reader);

			try {
				String line, trimmedLine;
				HeaderBuilder header = new HeaderBuilder();
				NodeBuilder node = new NodeBuilder(this.getIndent());
				StringBuilder mainHeader = new StringBuilder();
				int indent;
				while ((line = input.readLine()) != null) {
					line = fixLine(line);
					indent = StringUtil.getSuccessiveCharCount(line, ' ');
					trimmedLine = line.substring(indent);
					// Prevent new name convention errors
					if (trimmedLine.equals("*:")) {
						trimmedLine = "'*':";
						line = StringUtil.getFilledString(" ", indent) + trimmedLine;
					}
					// Handle a main header line
					if (trimmedLine.startsWith(MAIN_HEADER_PREFIX)) {
						mainHeader.append('\n').append(trimmedLine.substring(MAIN_HEADER_PREFIX.length()));
						continue;
					}
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
				// Set main header
				if (mainHeader.length() > 0) {
					this.setHeader(mainHeader.toString());
				}
			} finally {
				input.close();
			}
			try {
				this.getSource().loadFromString(builder.toString());
			} catch (InvalidConfigurationException e) {
				throw new IOException("YAML file is corrupt", e);
			}
		} catch (FileNotFoundException ex) {
			// Ignored
		}
	}

	/**
	 * Attempts to fix a single line of YAML text of easy to detect issues.
	 * Quotation issues can not be fixed, as they can span multiple lines.
	 * 
	 * @param line to fix
	 * @return fixed line
	 */
	private String fixLine(String line) {
		// Replace chat style characters
		String fixedLine = StringUtil.ampToColor(line);
		// Replace tabs with spaces
		int count = StringUtil.getSuccessiveCharCount(fixedLine, '\t');
		if (count > 0) {
			fixedLine = StringUtil.getFilledString(" ", count * getIndent()) + fixedLine.substring(count);
		}
		return fixedLine;
	}

	private void writeHeader(boolean main, BufferedWriter writer, String header, int indent) throws IOException {
		if (header != null) {
			for (String headerLine : header.split("\n", -1)) {
				StreamUtil.writeIndent(writer, indent);
				if (main) {
					writer.write(MAIN_HEADER_PREFIX);
					writer.write(headerLine);
				} else if (headerLine.trim().length() > 0) {
					writer.write("# ");
					writer.write(headerLine);
				}
				writer.newLine();
			}
		}
	}

	/**
	 * Writes this configuration to stream<br>
	 * Note: Closes the stream when finished
	 * 
	 * @param stream to write to
	 */
	public void saveToStream(OutputStream stream) throws IOException {
		// Get rid of newline characters in text - Bukkit bug prevents proper saving
		for (String key : this.getSource().getKeys(true)) {
			Object value = this.getSource().get(key);
			if (value instanceof String) {
				String text = (String) value;
				if (text.contains("\n")) {
					this.getSource().set(key, Arrays.asList(text.split("\n", -1)));
				}
			}
		}

		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream));
		try {
			// Write the top header
			writeHeader(true, writer, this.getHeader(), 0);

			// Write other headers and the nodes
			IntHashMap<String> anchorData = new IntHashMap<String>();
			int indent;
			int anchStart, anchEnd, anchId = -1, anchDepth = 0, anchIndent = 0;
			boolean wasAnchor;
			int refStart, refEnd, refId;
			StringBuilder refData = new StringBuilder();
			NodeBuilder node = new NodeBuilder(this.getIndent());
			for (String line : this.getSource().saveToString().split("\n", -1)) {
				line = StringUtil.colorToAmp(line);
				indent = StringUtil.getSuccessiveCharCount(line, ' ');
				line = line.substring(indent);
				wasAnchor = false;
				// ===== Logic start =====
				// Get rid of the unneeded '-characters around certain common names
				if (line.equals("'*':")) {
					line = "*:";
				}

				// Handle a node
				if (node.handle(line, indent)) {
					// Store old anchor data
					if (anchId >= 0 && node.getDepth() <= anchDepth) {
						anchorData.put(anchId, refData.toString());
						refData.setLength(0);
						anchId = refId = -1;
					}

					// Saving a new node: Write the node header
					writeHeader(false, writer, this.getHeader(node.getPath()), indent);

					// Check if the value denotes a reference
					refStart = line.indexOf("*id", node.getName().length());
					refEnd = line.indexOf(' ', refStart);
					if (refEnd == -1) {
						refEnd = line.length();
					}
					if (refStart > 0 && refEnd > refStart) {
						// This is a reference pointer: get id
						refId = ParseUtil.parseInt(line.substring(refStart + 3, refEnd), -1);
						if (refId >= 0) {
							// Obtain the reference data
							String data = anchorData.get(refId);
							if (data != null) {
								// Replace the line with the new data
								line = StringUtil.trimEnd(line.substring(0, refStart)) + " " + data;
							}
						}
					}

					// Check if the value denotes a data anchor
					anchStart = line.indexOf("&id", node.getName().length());
					anchEnd = line.indexOf(' ', anchStart);
					if (anchEnd == -1) {
						anchEnd = line.length();
					}
					if (anchStart > 0 && anchEnd > anchStart) {
						// This is a reference node anchor: get id
						anchId = ParseUtil.parseInt(line.substring(anchStart + 3, anchEnd), -1);
						anchDepth = node.getDepth();
						anchIndent = indent;
						if (anchId >= 0) {
							// Fix whitespace after anchor identifier
							anchEnd += StringUtil.getSuccessiveCharCount(line.substring(anchEnd), ' ');

							// Store the data of this anchor
							refData.append(line.substring(anchEnd));

							// Remove the variable reference from saved data
							line = StringUtil.replace(line, anchStart, anchEnd, "");
						}
						wasAnchor = true;
					}
				}
				if (!wasAnchor && anchId >= 0) {
					// Not an anchor: append anchor data
					refData.append('\n').append(StringUtil.getFilledString(" ", indent - anchIndent)).append(line);
				}
				// Write the data
				if (LogicUtil.containsChar('\n', line)) {
					for (String part : line.split("\n", -1)) {
						StreamUtil.writeIndent(writer, indent);
						writer.write(part);
						writer.newLine();
					}
				} else {
					StreamUtil.writeIndent(writer, indent);
					writer.write(line);
					writer.newLine();
				}
			}
		} finally {
			writer.close();
		}
	}
}
