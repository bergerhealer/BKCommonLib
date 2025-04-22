package com.bergerkiller.bukkit.common;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Helper class for creating a multi-line string with indents. Useful for document trees
 * in debug toString() methods.
 */
public class IndentedStringBuilder {
    /** Gets direct access to the underlying StringBuilder */
    public final StringBuilder builder;

    private final int indent;
    private final int indentStep;

    /**
     * Creates a new IndentedStringBuilder with a default indent step of 2 spaces
     *
     * @return IndentedStringBuilder
     */
    public static IndentedStringBuilder create() {
        return create(2);
    }

    /**
     * Creates a new IndentedStringBuilder
     *
     * @param indentStep How many spaces are added for each indent
     * @return IndentedStringBuilder
     */
    public static IndentedStringBuilder create(int indentStep) {
        return new IndentedStringBuilder(indentStep);
    }

    /**
     * Helper method that calls {@link AppendableToString#toString(IndentedStringBuilder)} and returns
     * the result as a String.
     *
     * @param appendable Object to stringify
     * @return String
     */
    public static String toString(AppendableToString appendable) {
        IndentedStringBuilder str = create();
        str.append(appendable);
        return str.toString();
    }

    private IndentedStringBuilder(int indentStep) {
        this(new StringBuilder(), indentStep, 0);
    }

    private IndentedStringBuilder(StringBuilder builder, int indentStep, int indent) {
        this.builder = builder;
        this.indent = indent;
        this.indentStep = indentStep;
    }

    /**
     * Returns a new IndentedStringBuilder that writes to the same underlying
     * StringBuilder buffer, but with a +1 indentation level.
     *
     * @return IndentedStringBuilder
     */
    public IndentedStringBuilder indent() {
        return indent(1);
    }

    /**
     * Returns a new IndentedStringBuilder that writes to the same underlying
     * StringBuilder buffer, but with a new indentation level.
     *
     * @param indentIncrease Amount of indent-step spaces to add to the indent
     * @return IndentedStringBuilder
     */
    public IndentedStringBuilder indent(int indentIncrease) {
        return new IndentedStringBuilder(builder, indentStep, indent + indentStep * indentIncrease);
    }

    /**
     * Calls the callback to append additional contents to this builder at a +1 indent level.
     * Returns this same builder so appending can continue on the current indentation level.
     *
     * @param callback Callback that performs indenting on the next indent level
     * @return this
     */
    public IndentedStringBuilder appendWithIndent(Consumer<IndentedStringBuilder> callback) {
        callback.accept(indent());
        return this;
    }

    /**
     * Calls the callback to append additional contents to this builder at a new indent level.
     * Returns this same builder so appending can continue on the current indentation level.
     *
     * @param callback Callback that performs indenting on the next indent level
     * @param indentIncrease Amount of indent-step spaces to add to the indent
     * @return this
     */
    public IndentedStringBuilder appendWithIndent(Consumer<IndentedStringBuilder> callback, int indentIncrease) {
        callback.accept(indent(indentIncrease));
        return this;
    }

    /**
     * Iterates all the appendable items specified and adds it on a new line. A newline is inserted before every
     * single item. Items that support {@link AppendableToString} use that method instead of the default
     * toString().
     *
     * @param lineItems Item values
     * @return this
     */
    public IndentedStringBuilder appendLines(Iterable<?> lineItems) {
        for (Object item : lineItems) {
            if (item instanceof AppendableToString) {
                ((AppendableToString) item).toString(append("\n"));
            } else {
                append("\n").append(item);
            }
        }
        return this;
    }

    /**
     * Iterates all the items specified and adds it on a new line. A newline is inserted before every
     * single item.
     *
     * @param lineItems Item values
     * @param toString Function to write the value to this IndentedStringBuilder
     * @return this
     * @param <T> Value type of the items
     */
    public <T> IndentedStringBuilder appendLines(Iterable<T> lineItems, BiConsumer<IndentedStringBuilder, T> toString) {
        for (T item : lineItems) {
            toString.accept(append("\n"), item);
        }
        return this;
    }

    /**
     * Calls {@link AppendableToString#toString(IndentedStringBuilder)} with this
     * builder. Helpful for chaining calls.
     *
     * @param appendable Object to append to this builder
     * @return this
     */
    public IndentedStringBuilder append(AppendableToString appendable) {
        if (appendable != null) {
            appendable.toString(this);
        } else {
            builder.append("<null>");
        }
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
            return append(obj == null ? "<null>" : obj.toString());
        }
    }

    public IndentedStringBuilder append(String str) {
        if (str == null) {
            builder.append("<null>");
        } else if (indent > 0) {
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
