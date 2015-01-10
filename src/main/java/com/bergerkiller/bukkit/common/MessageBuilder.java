package com.bergerkiller.bukkit.common;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.bergerkiller.bukkit.common.utils.StringUtil;

public class MessageBuilder {
	private final List<StringBuilder> lines = new ArrayList<StringBuilder>();
	private StringBuilder builder;
	private int currentWidth;
	private String separator = null;
	private int sepwidth = 0;
	private boolean isFirstSeparatorCall = true;
	private int indent = 0;

	public static final int CHAT_WINDOW_WIDTH = 240;

	public MessageBuilder() {
		this(new StringBuilder());
	}

	public MessageBuilder(String firstLine) {
		this(new StringBuilder(firstLine));
	}

	public MessageBuilder(int capacity) {
		this(new StringBuilder(capacity));
	}

	public MessageBuilder(StringBuilder builder) {
		this.lines.add(this.builder = builder);
		this.currentWidth = 0;
	}

	public MessageBuilder setSeparator(ChatColor color, String separator) {
		return this.setSeparator(color + separator);
	}

	public MessageBuilder setSeparator(String separator) {
		if (separator == null) {
			return this.clearSeparator();
		} else {
			this.separator = separator;
			this.sepwidth = StringUtil.getWidth(separator);
			this.isFirstSeparatorCall = true;
			return this;
		}
	}

	public MessageBuilder clearSeparator() {
		this.separator = null;
		this.sepwidth = 0;
		return this;
	}

	public int getIndent() {
		return this.indent;
	}

	public MessageBuilder indent(int indent) {
		this.indent += indent;
		return this;
	}

	public MessageBuilder setIndent(int indent) {
		this.indent = indent;
		return this;
	}

	public MessageBuilder black(Object... text) {
		return this.append(ChatColor.BLACK, text);
	}

	public MessageBuilder dark_blue(Object... text) {
		return this.append(ChatColor.DARK_BLUE, text);
	}

	public MessageBuilder dark_green(Object... text) {
		return this.append(ChatColor.DARK_GREEN, text);
	}

	public MessageBuilder dark_aqua(Object... text) {
		return this.append(ChatColor.DARK_AQUA, text);
	}

	public MessageBuilder dark_red(Object... text) {
		return this.append(ChatColor.DARK_RED, text);
	}

	public MessageBuilder dark_purple(Object... text) {
		return this.append(ChatColor.DARK_PURPLE, text);
	}

	public MessageBuilder gold(Object... text) {
		return this.append(ChatColor.GOLD, text);
	}

	public MessageBuilder gray(Object... text) {
		return this.append(ChatColor.GRAY, text);
	}

	public MessageBuilder dark_gray(Object... text) {
		return this.append(ChatColor.DARK_GRAY, text);
	}

	public MessageBuilder blue(Object... text) {
		return this.append(ChatColor.BLUE, text);
	}

	public MessageBuilder green(Object... text) {
		return this.append(ChatColor.GREEN, text);
	}

	public MessageBuilder aqua(Object... text) {
		return this.append(ChatColor.AQUA, text);
	}

	public MessageBuilder red(Object... text) {
		return this.append(ChatColor.RED, text);
	}

	public MessageBuilder light_purple(Object... text) {
		return this.append(ChatColor.LIGHT_PURPLE, text);
	}

	public MessageBuilder yellow(Object... text) {
		return this.append(ChatColor.YELLOW, text);
	}

	public MessageBuilder white(Object... text) {
		return this.append(ChatColor.WHITE, text);
	}

	public MessageBuilder magic(Object... text) {
		return this.append(ChatColor.MAGIC, text);
	}

	/**
	 * Appends new text after a color
	 * 
	 * @param color for the text
	 * @param text to append
	 * @return This Message Builder
	 */
	public MessageBuilder append(ChatColor color, Object... text) {
		String[] newtext = new String[text.length];
		for (int i = 0; i < text.length; i++) {
			newtext[i] = text[i].toString();
		}
		return this.append(color, newtext);
	}

	/**
	 * Appends new text
	 * 
	 * @param text to append
	 * @return This Message Builder
	 */
	public MessageBuilder append(Object... text) {
		String[] newtext = new String[text.length];
		for (int i = 0; i < text.length; i++) {
			newtext[i] = text[i].toString();
		}
		return this.append(newtext);
	}

	/**
	 * Appends new text after a color
	 * 
	 * @param color for the text
	 * @param text to append
	 * @return This Message Builder
	 */
	public MessageBuilder append(ChatColor color, String... text) {
		if (text != null && text.length > 0) {
			prepareAppend(StringUtil.getWidth(text));
			this.builder.append(color.toString());
			for (String part : text) {
				this.builder.append(part);
			}
		}
		return this;
	}

	/**
	 * Appends a new character
	 * 
	 * @param character to append
	 * @return This Message Builder
	 */
	public MessageBuilder append(char character) {
		if (character == '\n') {
			return this.newLine();
		}
		// word wrap needed?
		prepareAppend(StringUtil.getWidth(character));
		this.builder.append(character);
		return this;
	}

	/**
	 * Appends new text
	 * 
	 * @param text to append
	 * @return This Message Builder
	 */
	public MessageBuilder append(String... text) {
		if (text != null && text.length > 0) {
			prepareAppend(StringUtil.getWidth(text));
			for (String part : text) {
				this.builder.append(part);
			}
		}
		return this;
	}

	private void prepareAppend(int widthToAppend) {
		if ((this.currentWidth + widthToAppend + this.sepwidth) > CHAT_WINDOW_WIDTH) {
			this.newLine();
		} else if (this.separator != null) {
			if (!this.isFirstSeparatorCall) {
				this.currentWidth += this.sepwidth;
				this.builder.append(this.separator);
			}
			this.isFirstSeparatorCall = false;
		}
		this.currentWidth += widthToAppend;
	}

	/**
	 * Starts a new line
	 * 
	 * @return this Message Builder
	 */
	public MessageBuilder newLine() {
		this.builder = new StringBuilder(30);
		for (int i = 0; i < this.indent; i++) {
			this.builder.append(' ');
		}
		this.currentWidth = this.indent * StringUtil.SPACE_WIDTH;
		this.lines.add(this.builder);
		return this;
	}

	/**
	 * Gets the total length of this Message
	 * 
	 * @return total length
	 */
	public int length() {
		int length = this.lines.size() - 1;
		for (StringBuilder line : this.lines) {
			length += line.length();
		}
		return length;
	}

	/**
	 * Gets whether messages are contained
	 * 
	 * @return True if empty, False if not
	 */
	public boolean isEmpty() {
		return this.lines.size() == 1 && this.builder.length() == 0;
	}

	/**
	 * Gets the last line of this Message Builder
	 * 
	 * @return last line
	 */
	public String lastLine() {
		return this.builder.toString();
	}

	@Override
	public String toString() {
		StringBuilder total = new StringBuilder(this.length());
		for (int i = 0; i < this.lines.size(); i++) {
			if (i != 0) {
				total.append('\n');
			}
			total.append(this.lines.get(i));
		}
		return total.toString();
	}

	/**
	 * Obtains all the lines of this Message
	 * 
	 * @return array of lines
	 */
	public String[] lines() {
		if (this.isEmpty()) {
			return new String[0];
		}
		String[] lines = new String[this.lines.size()];
		int i = 0;
		for (StringBuilder line : this.lines) {
			lines[i++] = line.toString();
		}
		return lines;
	}

	/**
	 * Clears all the Messages stored by this Message Builder
	 * 
	 * @return This Message Builder
	 */
	public MessageBuilder clear() {
		this.lines.clear();
		this.lines.add(this.builder = new StringBuilder());
		this.currentWidth = 0;
		return this;
	}

	/**
	 * Sends all of the messages to a receiver and clears them<br>
	 * After calling flush this Message Builder is cleared
	 * 
	 * @param sender to send to
	 * @return This Message Builder
	 */
	public MessageBuilder flush(CommandSender sender) {
		return this.send(sender).clear();
	}

	/**
	 * Sends all of the messages to a receiver without clearing them
	 * 
	 * @param sender to send to
	 * @return This Message Builder
	 */
	public MessageBuilder send(CommandSender sender) {
		for (StringBuilder line : this.lines) {
			sender.sendMessage(line.toString());
		}
		return this;
	}

	/**
	 * Logs all of the messages to the server
	 * 
	 * @param level for the messages
	 * @return This Message Builder
	 */
	public MessageBuilder log(Level level) {
		for (StringBuilder line : this.lines) {
			Bukkit.getLogger().log(level, line.toString());
		}
		return this;
	}
}
