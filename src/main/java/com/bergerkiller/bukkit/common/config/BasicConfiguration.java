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

import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.StreamUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;

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
	public void loadFromStream(InputStream stream) throws Exception {
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
					line = StringUtil.ampToColor(line);
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
			this.getSource().loadFromString(builder.toString());
		} catch (FileNotFoundException ex) {
			// Ignored
		}
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
	public void saveToStream(OutputStream stream) throws Exception {
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
			String trimmedLine;
			int indent;
			NodeBuilder node = new NodeBuilder(this.getIndent());
			for (String line : this.getSource().saveToString().split("\n", -1)) {
				line = StringUtil.colorToAmp(line);
				indent = StringUtil.getSuccessiveCharCount(line, ' ');
				trimmedLine = line.substring(indent);
				// ===== Logic start =====
				// Get rid of the unneeded '-characters around certain common names
				if (trimmedLine.equals("'*':")) {
					trimmedLine = "*:";
					line = StringUtil.getFilledString(" ", indent) + trimmedLine;
				}

				// Handle a node
				if (node.handle(trimmedLine, indent)) {
					writeHeader(false, writer, this.getHeader(node.getPath()), indent);
				}
				writer.write(line);
				writer.newLine();
			}
		} finally {
			writer.close();
		}
	}
}
