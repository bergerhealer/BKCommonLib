package com.bergerkiller.bukkit.common.io;

/**
 * Simple container for multiple bits of data.
 */
public class BitPacket implements Cloneable {
    public int data, bits;

    public BitPacket() {
        this.data = 0;
        this.bits = 0;
    }

    public BitPacket(int data, int bits) {
        this.data = data;
        this.bits = bits;
    }

    /**
     * Reads some bits from this packet, shifting the bits out of the buffer
     * 
     * @param nBits to read
     * @return bit data
     */
    public int read(int nBits) {
        int result = data & ((1 << nBits) - 1);
        this.data >>= nBits;
        this.bits -= nBits;
        return result;
    }

    /**
     * Writes some bits to this packet, increasing the number of bits stored
     * 
     * @param data to write
     * @param nBits of data
     */
    public void write(int data, int nBits) {
        this.data |= (data << this.bits);
        this.bits += nBits;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof BitPacket) {
            BitPacket other = (BitPacket) o;
            if (other.bits == bits) {
                int mask = ((1 << bits) - 1);
                return (data & mask) == (other.data & mask);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public BitPacket clone() {
        return new BitPacket(this.data, this.bits);
    }

    @Override
    public String toString() {
        String str = Integer.toBinaryString(data & ((1 << bits) - 1));
        while (str.length() < this.bits) {
            str = "0" + str;
        }
        return str;
    }
}
