package com.bergerkiller.bukkit.common.config;

/**
 * Can handle header formatted lines and store them in an internal buffer
 */
public class HeaderBuilder {
	private StringBuilder buffer = new StringBuilder();

	private StringBuilder add() {
		return this.buffer.append('\n');
	}

	/**
	 * Handles the reading input of a new line
	 * 
	 * @param line to handle
	 * @return True if a header was handled, False if not
	 */
	public boolean handle(String line) {
		if (line.isEmpty()) {
			add().append(' ');
		} else if (line.startsWith("# ")) {
			add().append(line.substring(2));
		} else if (line.startsWith("#")) {
			add().append(line.substring(1));
		} else {
			return false;
		}
		return true;
	}

	/**
	 * Clears the header contained
	 */
	public void clear() {
		this.buffer.setLength(0);
	}

	/**
	 * Checks if a header can be obtained
	 * 
	 * @return True if it has a header, False if not
	 */
	public boolean hasHeader() {
		return this.buffer.length() > 0;
	}

	/**
	 * Obtains the header contained, null if there is none
	 * 
	 * @return header
	 */
	public String getHeader() {
		return hasHeader() ? this.buffer.substring(1) : null;
	}
}
