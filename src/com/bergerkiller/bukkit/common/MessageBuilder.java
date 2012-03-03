package com.bergerkiller.bukkit.common;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.map.MapFont.CharacterSprite;
import org.bukkit.map.MinecraftFont;

public class MessageBuilder {
		
	private final List<StringBuilder> lines = new ArrayList<StringBuilder>();
	private StringBuilder builder;
	private int currentWidth;
	private String separator = null;
	private int sepwidth = 0;
	private int indent = 0;
	
	public static final int CHAT_WINDOW_WIDTH = 240;
	public static int SPACE_WIDTH = getWidth(' ');
	
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
			this.sepwidth = getWidth(separator);
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
	
	public MessageBuilder wordWrap(String... toAppend) {
		if (this.needsWordWrap(toAppend)) {
			this.newLine();
		}
		return this;
	}
	public boolean needsWordWrap(String... textToAppend) {
		return this.needsWordWrap(getWidth(textToAppend));
	}
	public boolean needsWordWrap(int widthToAppend) {
		return (this.currentWidth + widthToAppend + this.sepwidth + (this.indent * SPACE_WIDTH)) > CHAT_WINDOW_WIDTH;
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
	public MessageBuilder append(ChatColor color, Object... text) {
		String[] newtext = new String[text.length];
		for (int i = 0; i < text.length; i++) {
			newtext[i] = text[i].toString();
		}
		return this.append(color, newtext);
	}	

	public MessageBuilder append(Object... text) {
		String[] newtext = new String[text.length];
		for (int i = 0; i < text.length; i++) {
			newtext[i] = text[i].toString();
		}
		return this.append(newtext);
	}
	
	public MessageBuilder append(ChatColor color, String... text) {
		if (text == null || text.length == 0) return this;
		int width = getWidth(text);
		if (this.needsWordWrap(width)) {
			this.newLine();
		}
		this.currentWidth += width;
		this.prepareNewAppend();
		this.builder.append(color.toString());
		for (String part : text) {
			this.builder.append(part);
		}
		return this;
	}
	public MessageBuilder append(char character) {
		if (character == '\n') {
			this.newLine();
			return this;
		}
		//word wrap needed?
		int width = getWidth(character);
		if (this.needsWordWrap(width)) {
			this.newLine();
		}
		this.currentWidth += width;
		this.prepareNewAppend();
		this.builder.append(character);
		return this;
	}
	public MessageBuilder append(String... text) {
		if (text == null || text.length == 0) return this;
		int width = getWidth(text);
		if (this.needsWordWrap(width)) {
			this.newLine();
		}
		this.currentWidth += width;
		this.prepareNewAppend();
		for (String part : text) {
			this.builder.append(part);
		}
		return this;
	}
	private void prepareNewAppend() {
		if (this.builder.length() == 0) {
			this.currentWidth += SPACE_WIDTH * this.indent;
			for (int i = 0; i < this.indent; i++) {
				this.builder.append(' ');
			}
		} else if (this.separator != null) {
			this.currentWidth += this.sepwidth;
			this.builder.append(this.separator);
		}
	}
	
	public MessageBuilder newLine() {
		this.builder = new StringBuilder();
		this.lines.add(this.builder);
		this.currentWidth = 0;
		return this;
	}
	
	public int length() {
		return this.builder.length();
	}
	public boolean isEmpty() {
		return this.builder.length() == 0;
	}
	
	public static int getWidth(String... text) {
		int width = 0;
		for (String part : text) {
			char character;
			CharacterSprite charsprite;
			for (int i = 0; i < part.length(); i++) {
				character = part.charAt(i);
				if (character == '\n') continue;
				if (character == '\u00A7') {
					i++;
					continue;
				} else if (character == ' ') {
					width += SPACE_WIDTH;
				} else {
					charsprite = MinecraftFont.Font.getChar(character);
					if (charsprite != null) {
						width += charsprite.getWidth();
					}
				}
			}
		}
		return width;
	}
	public static int getWidth(char character) {
		return MinecraftFont.Font.getChar(character).getWidth();
	}
	
	public String lastLine() {
		return this.builder.toString();
	}
	public String toString() {
		StringBuilder total = new StringBuilder();
		for (StringBuilder line : this.lines) {
			if (total.length() > 0) {
				total.append('\n');
			}
			total.append(line);
		}
		return total.toString();
	}
	
	public String[] lines() {
		String[] lines = new String[this.lines.size()];
		int i = 0;
		for (StringBuilder line : this.lines) {
			lines[i++] = line.toString();
		}
		return lines;
	}
	public MessageBuilder clear() {
		this.lines.clear();
		this.lines.add(this.builder = new StringBuilder());
		this.currentWidth = 0;
		return this;
	}
	public MessageBuilder flush(CommandSender sender) {
		if (this.lines.size() > 1) {
			this.lines.remove(this.lines.size() - 1);
			this.send(sender);
			this.lines.clear();
			this.lines.add(this.builder);
		}
		return this;
	}
	public MessageBuilder send(CommandSender sender) {
		for (StringBuilder line : this.lines) {
			sender.sendMessage(line.toString());
		}
		return this;
	}
	public MessageBuilder log(Level level) {
		for (StringBuilder line : this.lines) {
			Bukkit.getLogger().log(level, line.toString());
		}
		return this;
	}
	
}
