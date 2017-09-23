package com.bergerkiller.bukkit.common.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Output Stream which can also write individual bits
 */
public class BitOutputStream extends OutputStream {
    private int bitbuff = 0;
    private int bitbuff_len = 0;
    private boolean closed = false;
    private final OutputStream output;
    private final boolean closeOutput;

    /**
     * Initializes a new Bit Output Stream, writing to the Output Stream specified
     * 
     * @param outputStream to write to
     */
    public BitOutputStream(OutputStream outputStream) {
        this(outputStream, true);
    }

    /**
     * Initializes a new Bit Output Stream, writing to the Output Stream specified
     * 
     * @param outputStream to write to
     * @param closeOutputStream whether to close the underlying output stream when closing this stream
     */
    public BitOutputStream(OutputStream outputStream, boolean closeOutputStream) {
        this.output = outputStream;
        this.closeOutput = closeOutputStream;
    }

    @Override
    public void write(int b) throws IOException {
        writeBits(b, 8);
    }

    /**
     * Writes bits to this stream
     * 
     * @param data to write
     * @param nBits of data
     * @throws IOException
     */
    public void writeBits(int data, int nBits) throws IOException {
        if (this.closed) {
            throw new IOException("Stream is closed");
        }
        this.bitbuff |= (data << this.bitbuff_len);
        this.bitbuff_len += nBits;
        while (this.bitbuff_len >= 8) {
            this.output.write(this.bitbuff & 0xFF);
            this.bitbuff_len -= 8;
            this.bitbuff >>= 8;
        }
    }

    /**
     * Writes the bits in a packet to this stream
     * 
     * @param packet to write
     * @throws IOException
     */
    public void writePacket(BitPacket packet) throws IOException {
        writeBits(packet.data, packet.bits);
    }

    @Override
    public void close() throws IOException {
        if (!this.closed) {
            if (this.bitbuff_len > 0) {
                this.output.write(this.bitbuff);
                this.bitbuff = 0;
                this.bitbuff_len = 0;
            }
            this.closed = true;
            if (this.closeOutput) {
                this.output.close();
            }
        }
    }

}
