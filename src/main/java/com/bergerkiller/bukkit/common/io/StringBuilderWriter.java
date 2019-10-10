package com.bergerkiller.bukkit.common.io;

import java.io.Writer;

/**
 * Writer class that writes to a StringBuilder
 */
public class StringBuilderWriter extends Writer {
    private final StringBuilder builder;

    public StringBuilderWriter() {
        this(new StringBuilder());
    }

    public StringBuilderWriter(StringBuilder builder) {
        this.builder = builder;
    }

    /**
     * Returns the StringBuilder written to
     *
     * @return The StringBuilder
     */
    public StringBuilder getBuilder() {
        return builder;
    }

    @Override
    public Writer append(final char value) {
        this.builder.append(value);
        return this;
    }

    @Override
    public Writer append(final CharSequence value) {
        this.builder.append(value);
        return this;
    }

    @Override
    public Writer append(final CharSequence value, final int start, final int end) {
        this.builder.append(value, start, end);
        return this;
    }

    @Override
    public void write(final String value) {
        this.builder.append(value);
    }

    @Override
    public void write(final char[] value, final int offset, final int length) {
        this.builder.append(value, offset, length);
    }

    @Override
    public void close() {
    }

    @Override
    public void flush() {
    }

    @Override
    public String toString() {
        return this.builder.toString();
    }
}