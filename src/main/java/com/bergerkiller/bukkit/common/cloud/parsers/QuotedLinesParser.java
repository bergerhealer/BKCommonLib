package com.bergerkiller.bukkit.common.cloud.parsers;

import org.incendo.cloud.annotation.specifier.Greedy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Parses a String into separate lines separated by spaces. Lines with
 * spaces can be parsed by surrounding them in quotes. Can be used to
 * parse a {@link Greedy @Greedy} string further into lines while keeping support
 * for quotes.
 */
public class QuotedLinesParser {
    private final List<String> lines = new ArrayList<>();

    /**
     * Creates a new initially-empty QuotedLinesParser
     *
     * @return QuotedLinesParser
     */
    public static QuotedLinesParser create() {
        return new QuotedLinesParser();
    }

    /**
     * Gets whether lines were parsed at all, or that the input was all empty or whitespace
     *
     * @return True if lines were parsed
     */
    public boolean hasLines() {
        return !lines.isEmpty();
    }

    /**
     * Gets the lines of text parsed so far
     *
     * @return Lines
     */
    public List<String> getLines() {
        return lines;
    }

    /**
     * Gets the lines of text parsed so far as an array
     *
     * @return Lines
     */
    public String[] getLinesArray() {
        return lines.toArray(new String[0]);
    }

    /**
     * Adds a line to the results of this parser
     *
     * @param line Line to add
     * @return this
     */
    public QuotedLinesParser addLine(String line) {
        this.lines.add(line);
        return this;
    }

    /**
     * Adds lines to the results of this parser
     *
     * @param lines Lines to add
     * @return this
     */
    public QuotedLinesParser addLines(String[] lines) {
        this.lines.addAll(Arrays.asList(lines));
        return this;
    }

    /**
     * Adds lines to the results of this parser
     *
     * @param lines Lines to add
     * @return this
     */
    public QuotedLinesParser addLines(Collection<String> lines) {
        this.lines.addAll(lines);
        return this;
    }

    /**
     * Parses the lines from an input String
     *
     * @param input Input
     * @return this
     */
    public QuotedLinesParser parse(String input) {
        if (input != null) {
            parse(input, 0, input.length());
        }
        return this;
    }

    /**
     * Parses the lines from a substring of an input String
     *
     * @param input Input
     * @param startIndex Start index offset into the input
     * @param endIndex End index offset
     * @return this
     */
    public QuotedLinesParser parse(String input, int startIndex, int endIndex) {
        if (input != null) {
            int currStartIndex = startIndex;
            while ((currStartIndex = parseNext(input, currStartIndex, endIndex)) != -1);
        }
        return this;
    }

    private int parseNext(String input, int startIndex, int endIndex) {
        // Omit preceding whitespace
        while (startIndex < endIndex && input.charAt(startIndex) == ' ') {
            startIndex++;
        }

        // Start with a quote? Quoted! Find the trailing quote.
        if (startIndex < endIndex && input.charAt(startIndex) == '"') {
            StringBuilder unescaped = null; // When we encounter a \, unescape
            int partStartIndex = startIndex + 1;
            int nextQuote;
            while ((nextQuote = input.indexOf('"', partStartIndex)) != -1 && nextQuote < endIndex) {
                if (input.charAt(nextQuote - 1) == '\\') {
                    // Escaped quote found. Build up a string without the escaped chars.
                    if (unescaped == null) {
                        unescaped = new StringBuilder(input.length() - startIndex);
                    }
                    unescaped.append(input, partStartIndex, nextQuote);
                    unescaped.append('"');
                    partStartIndex = nextQuote + 1;
                } else {
                    // End of string part found!
                    if (unescaped != null) {
                        // Use builder
                        unescaped.append(input, partStartIndex, nextQuote);
                        addLine(unescaped.toString());
                    } else {
                        // Use substring
                        addLine(input.substring(startIndex + 1, nextQuote));
                    }

                    // Continue next part past the quote
                    return nextQuote + 1;
                }
            }

            // Could not find a second quote. Parse as words and ignore it.
        }

        // Parse next word, instead
        int space = input.indexOf(' ', startIndex);
        if (space != -1) {
            addLine(input.substring(startIndex, space));
            return space + 1;
        }

        // End of string / final word. Only include if non-empty
        if (startIndex < endIndex) {
            addLine(input.substring(startIndex, endIndex));
        }
        return -1; // End
    }
}
