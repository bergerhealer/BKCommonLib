package com.bergerkiller.bukkit.common.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * Input Stream that can also read individual bits
 */
public class BitInputStream extends InputStream {
    private int bitbuff = 0;
    private int bitbuff_len = 0;
    private boolean eos = false;
    private boolean closed = false;
    private final InputStream input;
    private final boolean closeInput;

    /**
     * Initializes a new Bit Input Stream, reading from the Input Stream specified
     * 
     * @param inputStream to read from
     */
    public BitInputStream(InputStream inputStream) {
        this(inputStream, true);
    }

    /**
     * Initializes a new Bit Input Stream, reading from the Input Stream specified
     * 
     * @param inputStream to read from
     * @param closeInputStream whether to close the underlying input stream when closing this stream
     */
    public BitInputStream(InputStream inputStream, boolean closeInputStream) {
        this.input = inputStream;
        this.closeInput = closeInputStream;
    }

    /**
     * Checks whether the end of the stream has been reached.
     * This returns True when a past {@link #read(int)} returned -1.
     * 
     * @return True if end of stream
     */
    public boolean isEndOfStream() {
        return this.eos;
    }

    @Override
    public int available() throws IOException {
        if (this.closed) {
            throw new IOException("Stream is closed");
        }
        return this.input.available();
    }

    @Override
    public int read() throws IOException {
        return readBits(8);
    }

    /**
     * Reads bits from the stream
     * 
     * @param nBits to read
     * @return read value, -1 when end of stream is reached
     * @throws IOException
     */
    public int readBits(int nBits) throws IOException {
        if (this.closed) {
            throw new IOException("Stream is closed");
        }
        while (this.bitbuff_len < nBits) {
            int readByte = -1;
            try {
                readByte = this.input.read();
            } catch (IOException ex) {}
            if (readByte == -1) {
                this.eos = true;
                return -1;
            }
            this.bitbuff |= (readByte << this.bitbuff_len);
            this.bitbuff_len += 8;
        }
        int result = bitbuff & ((1 << nBits) - 1);
        this.bitbuff >>= nBits;
        this.bitbuff_len -= nBits;
        return result;
    }

    /**
     * Reads bits from this stream, returning a bit packet
     * 
     * @param nBits to read
     * @return bit packet with the read data
     * @throws IOException
     */
    public BitPacket readPacket(int nBits) throws IOException {
        return new BitPacket(this.readBits(nBits), nBits);
    }

    @Override
    public void close() throws IOException {
        if (!this.closed) {
            this.closed = true;
            if (this.closeInput) {
                this.input.close();
            }
        }
    }
}
