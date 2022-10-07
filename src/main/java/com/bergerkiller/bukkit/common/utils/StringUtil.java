package com.bergerkiller.bukkit.common.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.map.MapFont.CharacterSprite;
import org.bukkit.map.MinecraftFont;

import java.util.*;

public class StringUtil {

    public static final char CHAT_STYLE_CHAR = '\u00A7';
    public static final int SPACE_WIDTH = getWidth(' ');
    public static final String[] EMPTY_ARRAY = new String[0];
    private static final char[] CHAT_CODES;

    static {
        ChatColor[] styles = ChatColor.values();
        LinkedHashSet<Character> chars = new LinkedHashSet<Character>(styles.length * 2);
        for (ChatColor style : styles) {
            chars.add(Character.toLowerCase(style.getChar()));
            chars.add(Character.toUpperCase(style.getChar()));
        }
        CHAT_CODES = new char[chars.size()];
        int i = 0;
        for (Character c : chars) {
            CHAT_CODES[i] = c.charValue();
            i++;
        }
    }

    /**
     * Inserts a player name in JSON-formatted chat text
     * 
     * @param player to set
     * @param text to set the player in
     * @return updated text
     */
    public static String setPlayerNameInChatJson(Player player, String text) {
        return text.replaceAll("(?i)\\{PLAYER\\}", player.getName());
    }

    /**
     * Converts a Block to a
     * {@link com.bergerkiller.bukkit.common.BlockLocation} formatted text
     *
     * @param block to convert
     * @return A string representing the Block Location
     */
    public static String blockToString(Block block) {
        return block.getWorld().getName() + "_" + block.getX() + "_" + block.getY() + "_" + block.getZ();
    }

    /**
     * Converts {@link com.bergerkiller.bukkit.common.BlockLocation} formatted
     * text to a Block. If the World or Block is inaccessible, null is returned.
     *
     * @param str The String to convert
     * @return Block denoted by the String
     */
    public static Block stringToBlock(String str) {
        try {
            String s[] = str.split("_");
            // Saved data needs at least 4 elements
            if (s.length < 4) {
                return null;
            }
            // Parse xyz from last three parts
            int x = Integer.parseInt(s[s.length - 3]);
            int y = Integer.parseInt(s[s.length - 2]);
            int z = Integer.parseInt(s[s.length - 1]);
            // Parse the world name from first parts
            StringBuilder worldName = new StringBuilder(12);
            for (int i = 0; i < s.length - 3; i++) {
                if (i != 0) {
                    worldName.append('_');
                }
                worldName.append(s[i]);
            }
            // World exists? If not, can't get a block there
            World world = Bukkit.getServer().getWorld(worldName.toString());
            if (world == null) {
                return null;
            }
            return world.getBlockAt(x, y, z);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Gets the full width of one or more Strings appended
     *
     * @param text to get the total width of (can be one or more parts)
     * @return The width of all the text combined
     */
    public static int getTotalWidth(String... text) {
        int width = 0;
        for (String part : text) {
            width += getWidth(part);
        }
        return width;
    }

    /**
     * Calculates the width in pixels of a String if displayed on a Minecraft Sign.
     * 
     * @param text input
     * @return text display width
     */
    public static int getSignWidth(String text) {
        // For some reason spaces are counted one pixel too small
        int width = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == ' ') {
                width++;
            }
        }
        width += MinecraftFont.Font.getWidth(text);
        return width;
    }

    /**
     * Gets the full width of a string character sequence
     *
     * @param text character sequence to get the total width of
     * @return The width of all the text combined
     */
    public static int getWidth(CharSequence text) {
        int width = 0;
        char character;
        CharacterSprite charsprite;
        for (int i = 0; i < text.length(); i++) {
            character = text.charAt(i);
            if (character == '\n') {
                continue;
            }
            if (character == StringUtil.CHAT_STYLE_CHAR) {
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
        return width;
    }

    /**
     * Gets the Width of a certain character in Minecraft Font
     *
     * @param character to get the width of
     * @return Character width in pixels
     */
    public static int getWidth(char character) {
        CharacterSprite s = MinecraftFont.Font.getChar(character);
        return (s == null) ? 1 : s.getWidth();
    }

    public static int firstIndexOf(String text, char... values) {
        for (int i = 0; i < text.length(); i++) {
            if (LogicUtil.containsChar(text.charAt(i), values)) {
                return i;
            }
        }
        return -1;
    }

    public static int firstIndexOf(String text, String... values) {
        return firstIndexOf(text, 0, values);
    }

    public static int firstIndexOf(String text, int startindex, String... values) {
        int i = -1;
        int index;
        for (String value : values) {
            if ((index = text.indexOf(value, startindex)) != -1 && (i == -1 || index < i)) {
                i = index;
            }
        }
        return i;
    }

    /**
     * Gets a String containing a given text appended n times
     *
     * @param text to fill inside the String
     * @param n times to put in the String
     * @return new String with a length of [n * text.length()]
     */
    public static String getFilledString(String text, int n) {
        StringBuffer outputBuffer = new StringBuffer(text.length() * n);
        for (int i = 0; i < n; i++) {
            outputBuffer.append(text);
        }
        return outputBuffer.toString();
    }

    /**
     * Gets the text before the first occurrence of a given separator in a text.
     *
     * @param text to use
     * @param delimiter to find
     * @return the text before the first occurrence of the delimiter, or an
     * empty String if not found
     */
    public static String getBefore(String text, String delimiter) {
        final int index = text.indexOf(delimiter);
        return index >= 0 ? text.substring(0, index) : "";
    }

    /**
     * Gets the text after the first occurrence of a given separator in a text
     *
     * @param text to use
     * @param delimiter to find
     * @return the text after the first occurrence of the delimiter, or an empty
     * String if not found
     */
    public static String getAfter(String text, String delimiter) {
        final int index = text.indexOf(delimiter);
        return index >= 0 ? text.substring(index + delimiter.length()) : "";
    }

    /**
     * Gets the text before the last occurrence of a given separator in a text
     *
     * @param text to use
     * @param delimiter to find
     * @return the text before the delimiter, or an empty String if not found
     */
    public static String getLastBefore(String text, String delimiter) {
        final int index = text.lastIndexOf(delimiter);
        return index >= 0 ? text.substring(0, index) : "";
    }

    /**
     * Gets the text after the last occurrence of a given separator in a text
     *
     * @param text to use
     * @param delimiter to find
     * @return the text after the last occurrence of the delimiter, or an empty
     * String if not found
     */
    public static String getLastAfter(String text, String delimiter) {
        final int index = text.lastIndexOf(delimiter);
        return index >= 0 ? text.substring(index + delimiter.length()) : "";
    }

    /**
     * Replaces a part of the text with the replacement
     *
     * @param text to replace a part in
     * @param startIndex of the part
     * @param endIndex of the part
     * @param replacement for this part
     */
    public static String replace(String text, int startIndex, int endIndex, String replacement) {
        StringBuilder builder = new StringBuilder(text);
        builder.replace(startIndex, endIndex, replacement);
        return builder.toString();
    }

    /**
     * Trims away a piece of text from the end of the input text
     *
     * @param text to trim the end of
     * @param textToTrim from the ending
     * @return text trimmed at the end
     */
    public static String trimEnd(String text, String... textToTrim) {
        for (String trim : textToTrim) {
            if (text.endsWith(trim)) {
                return text.substring(0, text.length() - trim.length());
            }
        }
        return text;
    }

    /**
     * Trims away a piece of text from the beginning of the input text
     *
     * @param text to trim the start of
     * @param textToTrim from the beginning
     * @return text trimmed at the start
     */
    public static String trimStart(String text, String... textToTrim) {
        for (String trim : textToTrim) {
            if (text.startsWith(trim)) {
                return text.substring(trim.length());
            }
        }
        return text;
    }

    /**
     * Equivalent of {@link String#trim()}, but only trims away the whitespace
     * at the beginning.
     *
     * @param text to trim the start of
     * @return text trimmed at the start
     */
    public static String trimStart(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) != ' ') {
                return text.substring(i);
            }
        }
        return "";
    }

    /**
     * Equivalent of {@link String#trim()}, but only trims away the whitespace
     * at the end.
     *
     * @param text to trim the end of
     * @return text trimmed at the end
     */
    public static String trimEnd(String text) {
        for (int i = text.length() - 1; i >= 0; i--) {
            if (text.charAt(i) != ' ') {
                return text.substring(0, i + 1);
            }
        }
        return "";
    }

    /**
     * Removes a single element from a String array.<br>
     * See also: {@link LogicUtil#removeArrayElement(Object[], int)}
     *
     * @param input array
     * @param index in the array to remove
     * @return modified array
     */
    public static String[] remove(String[] input, int index) {
        return LogicUtil.removeArrayElement(input, index);
    }

    /**
     * Combines all the items in a set using the 'and' and ',' separators
     *
     * @param items to combine
     * @return Combined items text
     */
    @SuppressWarnings("rawtypes")
    public static String combineNames(Set items) {
        return combineNames((Collection) items);
    }

    /**
     * Combines all the items in a collection using the 'and' and ',' separators
     *
     * @param items to combine
     * @return Combined items text
     */
    @SuppressWarnings("rawtypes")
    public static String combineNames(Collection items) {
        // If null or empty collection, return empty String
        if (items == null || items.isEmpty()) {
            return "";
        }
        // If only one item, return just this one item
        if (items.size() == 1) {
            Object item = items.iterator().next();
            return item == null ? "" : item.toString();
        }
        // Build the items into one return value
        StringBuilder rval = new StringBuilder();
        int i = 0;
        for (Object item : items) {
            if (i == items.size() - 1) {
                // Last item: Combine using 'and'
                rval.append(" and ");
            } else if (i > 0) {
                // Middle item: Combine using a comma
                rval.append(", ");
            }
            if (item != null) {
                rval.append(item);
            }
            i++;
        }
        return rval.toString();
    }

    /**
     * Combines all the items in an array using the 'and' and ',' separators
     *
     * @param items to combine
     * @return Combined items text
     */
    public static String combineNames(String... items) {
        return combineNames(Arrays.asList(items));
    }

    /**
     * Use {@link #join(String, String...)} instead (is more properly named).
     * Method is not likely to be removed, however.
     */
    @Deprecated
    public static String combine(String separator, String... parts) {
        return join(separator, parts);
    }

    /**
     * Use {@link #join(String, Collection)} instead (is more properly named)
     * Method is not likely to be removed, however.
     */
    @Deprecated
    public static String combine(String separator, Collection<String> parts) {
        return join(separator, parts);
    }

    /**
     * Combines all the separate parts together with a separator in between
     *
     * @param separator to put in between the parts
     * @param parts to combine
     * @return combined parts separated using the separator
     */
    public static String join(String separator, String... parts) {
        return join(separator, Arrays.asList(parts));
    }

    /**
     * Combines all the separate parts together with a separator in between
     *
     * @param separator to put in between the parts
     * @param parts to combine
     * @return combined parts separated using the separator
     */
    public static String join(String separator, Collection<String> parts) {
        StringBuilder builder = new StringBuilder(parts.size() * 16);
        boolean first = true;
        for (String line : parts) {
            if (!first) {
                builder.append(separator);
            }
            if (line != null) {
                builder.append(line);
            }
            first = false;
        }
        return builder.toString();
    }

    /**
     * Converts the argument list to turn "-surrounded arguments into a single argument.
     * To use "-characters, it can be escaped using \"
     * 
     * @param args Input argument array
     * @return new array with the converted arguments
     */
    public static String[] convertArgs(String[] args) {
        return convertArgsList(Arrays.asList(args)).toArray(new String[0]);
    }

    /**
     * Converts the argument list to turn "-surrounded arguments into a single argument.
     * The "-characters not surrounded by spaces are not interpreted.
     * To specify a single "-character as argument around spaces, it can be supplied as <code>"""</code>.<br>
     * <br>
     * For example:
     * <pre>
     * [0] = these
     * [1] = are
     * [2] = "command
     * [3] = """arguments""
     * </pre>
     * Becomes:
     * <pre>
     * [0] = these
     * [1] = are
     * [2] = command "arguments"
     * </pre>
     * 
     * @param args Input argument list or iterable
     * @return New LinkedList with the converted arguments
     */
    public static LinkedList<String> convertArgsList(Iterable<String> args) {
        StringArgTokenizer tokenizer = new StringArgTokenizer();
        for (String arg : args) {
            tokenizer.next(arg);
        }
        return tokenizer.complete();
    }

    /**
     * Checks if a given Character is a valid chat formatting code
     *
     * @param character to check
     * @return True if it is a formatting code, False if not
     */
    public static boolean isChatCode(char character) {
        return LogicUtil.containsChar(character, CHAT_CODES);
    }

    public static int getSuccessiveCharCount(String value, char character) {
        return getSuccessiveCharCount(value, character, 0, value.length() - 1);
    }

    public static int getSuccessiveCharCount(String value, char character, int startindex) {
        return getSuccessiveCharCount(value, character, startindex, value.length() - startindex - 1);
    }

    public static int getSuccessiveCharCount(String value, char character, int startindex, int endindex) {
        int count = 0;
        for (int i = startindex; i <= endindex; i++) {
            if (value.charAt(i) == character) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    public static void replaceAll(StringBuilder builder, String from, String to) {
        int index = builder.indexOf(from);
        while (index != -1) {
            builder.replace(index, index + from.length(), to);
            // Move to the end of the replacement
            index += to.length();
            index = builder.indexOf(from, index);
        }
    }

    /**
     * Obtains a chat color constant from a given color code<br>
     * If the code is not part of a constant, the default value is returned
     *
     * @param code of the chat color
     * @param def to return if not found
     * @return Chat Color of the code
     */
    public static ChatColor getColor(char code, ChatColor def) {
        for (ChatColor color : ChatColor.values()) {
            if (code == color.toString().charAt(1)) {
                return color;
            }
        }
        return def;
    }

    /**
     * Converts color codes such as &5 to the Color code representation
     *
     * @param line to work on
     * @return converted line
     */
    public static String ampToColor(String line) {
        return swapColorCodes(line, '&', CHAT_STYLE_CHAR);
    }

    /**
     * Converts color codes to the ampersand representation, such as &5
     *
     * @param line to work on
     * @return converted line
     */
    public static String colorToAmp(String line) {
        return swapColorCodes(line, CHAT_STYLE_CHAR, '&');
    }

    /**
     * Swaps the color coded character
     *
     * @param line to operate on
     * @param fromCode to replace
     * @param toCode to replace fromCode with
     * @return converted String
     */
    public static String swapColorCodes(String line, char fromCode, char toCode) {
        StringBuilder builder = new StringBuilder(line);
        for (int i = 0; i < builder.length() - 1; i++) {
            // Next char is a valid color code?
            if (builder.charAt(i) == fromCode && isChatCode(builder.charAt(i + 1))) {
                builder.setCharAt(i, toCode);
                i++;
            }
        }
        return builder.toString();
    }

    /**
     * Removes  a substring portion of an input text
     *
     * @param text Input text
     * @param beginIndex First index of the character to remove, inclusive
     * @param endIndex Last index of the characters to remove, exclusive
     * @return Text with substring removed
     */
    public static String removeSubstring(String text, int beginIndex, int endIndex) {
        return replaceSubstring(text, beginIndex, endIndex, "");
    }

    /**
     * Replaces a substring portion of an input text with a replacement text
     *
     * @param text Input text
     * @param beginIndex First index of the character to replace, inclusive
     * @param endIndex Last index of the characters to replace, exclusive
     * @param replacement Replacement text. Empty string removed the substring only.
     * @return Text with substring replaced
     */
    public static String replaceSubstring(String text, int beginIndex, int endIndex, String replacement) {
        try {
            StringBuilder str = new StringBuilder(text.length() - (endIndex - beginIndex) + replacement.length());
            str.append(text, 0, beginIndex);
            str.append(replacement);
            str.append(text, endIndex, text.length());
            return str.toString();
        } catch (RuntimeException ex) {
            // Just to avoid vagueness, create a proper readable exception
            if (text == null) throw new IllegalArgumentException("Input text is null");
            if (replacement == null) throw new IllegalArgumentException("Input replacement string is null");
            if (beginIndex < 0) throw new IllegalArgumentException("Begin index is negative");
            if (endIndex > text.length()) throw new IllegalArgumentException("End index is beyond the length of the text");
            if (endIndex < beginIndex) throw new IllegalArgumentException("End index of " + endIndex + " is before begin index " + beginIndex);
            throw ex;
        }
    }

    /**
     * More performant version of {@link ChatColor#stripColor(String)}
     *
     * @param text Text to strip chat styling characters of
     * @return Text without any ChatColor style characters
     */
    public static String stripChatStyle(String text) {
        int index = text.indexOf(CHAT_STYLE_CHAR);
        if (index == -1) {
            return text; // Shortcut, avoiding StringBuilder
        }

        int lastIndex = 0;
        int textLength = text.length();
        StringBuilder newStr = new StringBuilder(textLength);
        do {
            newStr.append(text, lastIndex, index);
            lastIndex = index + 2;
            if (lastIndex >= textLength) {
                // Done! Avoid out of range errors.
                return newStr.toString();
            }
        } while ((index = text.indexOf(CHAT_STYLE_CHAR, lastIndex)) != -1);

        // Last bit
        newStr.append(text, lastIndex, text.length());
        return newStr.toString();
    }

    // Used by convertArgsList
    private static final class StringArgTokenizer {
        private final LinkedList<String> result = new LinkedList<String>();
        private final StringBuilder buffer = new StringBuilder();
        private boolean isEscaped = false;

        public void next(String arg) {
            // If currently escaped, append space character to buffer first
            if (isEscaped) {
                buffer.append(' ');
            }

            int numEscapedAtStart = 0;
            for (;numEscapedAtStart < arg.length() && arg.charAt(numEscapedAtStart)=='\"'; numEscapedAtStart++);
            int remEscapedAtStart = (numEscapedAtStart % 3);

            // Only "-characters are specified. This requires special handling.
            // Replace """ with "-characters, and decide based on remainder what to do.
            if (numEscapedAtStart == arg.length()) {
                if (isEscaped) {
                    if (remEscapedAtStart == 0) {
                        // Doesn't change escape parity
                        appendEscapedQuotes(numEscapedAtStart / 3);
                    } else {
                        // ["some | "] -> [some ]
                        // ["some | ""] -> [some "]
                        // ["some | """"] -> [some "]
                        // ["some | """""] -> [some ""]
                        appendEscapedQuotes((numEscapedAtStart / 3) + remEscapedAtStart - 1);
                        commitBuffer();
                    }
                } else {
                    appendEscapedQuotes(numEscapedAtStart / 3);
                    if (remEscapedAtStart == 1) {
                        isEscaped = true; // Start escape section
                    } else {
                        // When remainder isn't a single quote, commit the buffer
                        commitBuffer();
                    }
                }
                return;
            }

            // Handle start of an escaped section
            if (!isEscaped && remEscapedAtStart > 0) {
                isEscaped = true;
                remEscapedAtStart--;
            }

            // Add all remaining quotes at the start
            appendEscapedQuotes(numEscapedAtStart / 3 + remEscapedAtStart);

            // Process remaining characters of argument in sequence
            int numTrailingQuotes = 0;
            for (int i = numEscapedAtStart; i < arg.length(); i++) {
                char c = arg.charAt(i);
                if (c != '\"') {
                    appendEscapedQuotes(numTrailingQuotes);
                    numTrailingQuotes = 0;
                    buffer.append(c);
                } else if (++numTrailingQuotes == 3) {
                    numTrailingQuotes = 0;
                    buffer.append('\"');
                }
            }

            // Close escaped section
            if (!isEscaped) {
                appendEscapedQuotes(numTrailingQuotes);
                commitBuffer();
            } else if (numTrailingQuotes > 0) {
                appendEscapedQuotes(numTrailingQuotes-1);
                commitBuffer();
            }
        }

        public LinkedList<String> complete() {
            if (isEscaped) {
                commitBuffer();
            }
            return result;
        }

        private void appendEscapedQuotes(int num_quotes) {
            for (int n = 0; n < num_quotes; n++) {
                buffer.append('\"');
            }
        }

        private void commitBuffer() {
            result.add(buffer.toString());
            buffer.setLength(0);
            isEscaped = false;
        }
    }
}
