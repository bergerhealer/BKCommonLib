package com.bergerkiller.bukkit.common;

/**
 * Helper class for creating a multi-line string with indents. Useful for document trees
 * in debug toString() methods.
 */
public class IndentedStringBuilder {
    /** Gets direct access to the underlying StringBuilder */
    public final StringBuilder builder;

    private final int indent;

    public static IndentedStringBuilder create() {
        return new IndentedStringBuilder();
    }

    /**
     * Helper method that calls {@link AppendableToString#toString(IndentedStringBuilder)} and returns
     * the result as a String.
     *
     * @param appendable Object to stringify
     * @return String
     */
    public static String toString(AppendableToString appendable) {
        IndentedStringBuilder str = new IndentedStringBuilder();
        str.append(appendable);
        return str.toString();
    }

    private IndentedStringBuilder() {
        this(new StringBuilder(), 0);
    }

    private IndentedStringBuilder(StringBuilder builder, int indent) {
        this.builder = builder;
        this.indent = indent;
    }

    /**
     * Returns a new IndentedStringBuilder that writes to the same underlying
     * StringBuilder buffer, but with a new indentation level.
     *
     * @param indentIncrease Amount of spaces to add to the indent
     * @return IndentedStringBuilder
     */
    public IndentedStringBuilder indent(int indentIncrease) {
        return new IndentedStringBuilder(builder, indent + indentIncrease);
    }

    /**
     * Calls {@link AppendableToString#toString(IndentedStringBuilder)} with this
     * builder. Helpful for chaining calls.
     *
     * @param appendable Object to append to this builder
     * @return this
     */
    public IndentedStringBuilder append(AppendableToString appendable) {
        appendable.toString(this);
        return this;
    }

    public IndentedStringBuilder append(float value) {
        builder.append(value);
        return this;
    }

    public IndentedStringBuilder append(double value) {
        builder.append(value);
        return this;
    }

    public IndentedStringBuilder append(byte value) {
        builder.append(value);
        return this;
    }

    public IndentedStringBuilder append(short value) {
        builder.append(value);
        return this;
    }

    public IndentedStringBuilder append(int value) {
        builder.append(value);
        return this;
    }

    public IndentedStringBuilder append(long value) {
        builder.append(value);
        return this;
    }

    public IndentedStringBuilder append(boolean value) {
        builder.append(value);
        return this;
    }

    public IndentedStringBuilder append(Object obj) {
        if (obj instanceof AppendableToString) {
            ((AppendableToString) obj).toString(this);
            return this;
        } else {
            return append(obj == null ? "null" : obj.toString());
        }
    }

    public IndentedStringBuilder append(String str) {
        if (indent > 0) {
            int startChar = 0;
            int newlineChar = -1;
            while ((newlineChar = str.indexOf('\n', startChar)) != -1) {
                builder.append(str, startChar, newlineChar + 1);
                for (int i = 0; i < indent; i++) {
                    builder.append(' ');
                }
                startChar = newlineChar + 1;
            }
            builder.append(str, startChar, str.length());
        } else {
            // Simplified
            builder.append(str);
        }
        return this;
    }

    public IndentedStringBuilder append(char ch) {
        if (ch == '\n' && indent > 0) {
            builder.append('\n');
            for (int i = 0; i < indent; i++) {
                builder.append(' ');
            }
        } else {
            builder.append(ch);
        }
        return this;
    }

    @Override
    public String toString() {
        return builder.toString();
    }

    public interface AppendableToString {
        /**
         * Appends this object to the IndentedStringBuilder
         *
         * @param str IndentedStringBuilder to append to
         */
        void toString(IndentedStringBuilder str);
    }
}
