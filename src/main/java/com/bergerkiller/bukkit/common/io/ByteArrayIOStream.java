package com.bergerkiller.bukkit.common.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Based on {@link java.io.ByteArrayOutputStream}. Adds a method
 * to turn the internally buffered data into an input stream so it can be read from.
 * As well the normally hidden methods to increase capacity have been exposed.
 */
public class ByteArrayIOStream extends ByteArrayOutputStream {
    protected byte buf[];
    protected int count;

    /**
     * Creates a new byte buffer output stream, with an initial capacity of
     * 32 bytes.
     */
    public ByteArrayIOStream() {
        this(32);
    }

    /**
     * Creates a new byte buffer output stream, with an initial capacity as
     * specified.
     * 
     * @param initialCapacity  The initial capacity of the buffer
     */
    public ByteArrayIOStream(int initialCapacity) {
        buf = new byte[initialCapacity];
    }

    /**
     * Turns the buffered contents into an input stream so it can be read from.
     * Writing additional bytes to this stream will not be read by the input stream,
     * as in, this is not a pipe.
     * 
     * @return input stream
     */
    public InputStream toInputStream() {
        return new ByteArrayInputStream(this.buf, 0, this.count);
    }

    /**
     * Reads the input stream specified into this buffer to the very end
     * 
     * @param input The input stream to read from
     * @throws IOException If thrown by the input stream while reading
     */
    public synchronized void readFrom(InputStream input) throws IOException {
        while (true) {
            // Ensure space is available
            this.ensureCapacity(this.count + Math.max(4096, input.available()));

            // Read as many bytes as we can fit into the buffer
            int numRead = input.read(this.buf, this.count, this.buf.length - this.count);
            if (numRead == -1) {
                break;
            }
            this.count += numRead;            
        }
    }

    /**
     * Ensures the buffer can hold at least the given capacity
     * 
     * @param minCapacity
     */
    public synchronized void ensureCapacity(int minCapacity) {
        // overflow-conscious code
        if (minCapacity - buf.length > 0) {
            int oldCapacity = buf.length;
            int newCapacity = oldCapacity << 1;

            if (newCapacity - minCapacity < 0)
                newCapacity = minCapacity;

            if (newCapacity < 0) {
                if (minCapacity < 0) // overflow
                    throw new OutOfMemoryError();
                newCapacity = Integer.MAX_VALUE;
            }
            buf = Arrays.copyOf(buf, newCapacity);
        }
    }

}
