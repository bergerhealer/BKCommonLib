package com.bergerkiller.bukkit.common.collections;

import java.util.Arrays;

/**
 * Stores a String inside a char[] array buffer that is allocated elsewhere.
 * Should be used to refer to a part of a longer String in a mutable fashion.
 */
public class CharArrayBuffer implements CharSequence {
    private char[] _buffer;
    private int _position;
    private int _length;

    public CharArrayBuffer() {
        this(new char[0], 0, 0);
    }

    public CharArrayBuffer(String value) {
        this(value.toCharArray(), 0, value.length());
    }

    public CharArrayBuffer(char[] buffer, int pos, int len) {
        this.assign(buffer, pos, len);
    }

    /**
     * Assigns a new buffer, specifying the portion of the buffer used
     * 
     * @param buffer  The buffer
     * @param pos     Start offset into the buffer
     * @param len     Length of the contents in the buffer
     */
    public void assign(char[] buffer, int pos, int len) {
        this._buffer = buffer;
        this._position = pos;
        this._length = len;
    }

    /**
     * Copies the contents of the underlying buffer to a String.
     * The length is allowed to be longer than the length of this buffer's value
     * in case the underlying buffer stores additional data.
     * 
     * @param length to copy
     * @return String
     */
    public String copyToString(int length) {
        return new String(this._buffer, this._position, length);
    }

    /**
     * Copies the contents of the underlying buffer to another array,
     * starting from the position of this buffer.
     * The length is allowed to be longer than the length of this buffer's value
     * in case the underlying buffer stores additional data.
     * 
     * @param dest_buffer The destination buffer to write to
     * @param dest_pos The position in the destination buffer to write at
     * @param length to copy
     */
    public void copyTo(char[] dest_buffer, int dest_pos, int length) {
        System.arraycopy(this._buffer, this._position, dest_buffer, dest_pos, length);
    }

    /**
     * Updates the buffer being used, assuming that the original String value
     * is already stored at the position in the buffer.
     * 
     * @param buffer
     * @param pos
     * @return end position into the buffer where the value is stored
     */
    public int swapBuffer(char[] buffer, int pos) {
        this._buffer = buffer;
        this._position = pos;
        return this._position + this._length;
    }

    /**
     * Moves the contents of this buffer to a new char[] buffer array, and refers to
     * the newly written buffer portion in the future.
     * 
     * @param buffer to write to
     * @param position into the buffer
     * @return end position into the buffer where the value is stored
     */
    public int moveToBuffer(char[] buffer, int position) {
        System.arraycopy(this._buffer, this._position, buffer, position, this._length);
        this._buffer = buffer;
        this._position = position;
        return this._position + this._length;
    }

    /**
     * Sets a new value to be stored in this buffer using the value of another buffer.
     * If the value is smaller or equal to the buffer size, the value is written to the buffer.
     * If it is larger, then a new buffer is allocated to store the String in.
     * 
     * @param value to store
     * @return the change in length of the String compared to the previous value
     */
    public int update(CharArrayBuffer value) {
        int original_length = this._length;
        this._length = value._length;
        if (this._length <= original_length) {
            System.arraycopy(value._buffer, value._position, this._buffer, this._position, this._length);
        } else {
            this.assign(Arrays.copyOfRange(value._buffer, value._position, value._length), 0, value._length);
        }
        return this._length - original_length;
    }

    /**
     * Sets a new value to be stored in this buffer. If the value is smaller
     * or equal to the buffer size, the value is written to the buffer. If it is
     * larger, then a new buffer is allocated to store the String in.
     * 
     * @param value to store
     * @return the change in length of the String compared to the previous value
     */
    public int update(String value) {
        int original_length = this._length;
        this._length = value.length();
        if (this._length <= original_length) {
            value.getChars(0, this._length, this._buffer, this._position);
        } else {
            this.assign(value.toCharArray(), 0, this._length);
        }
        return this._length - original_length;
    }

    /**
     * Sets a new value to be stored in this buffer. If the value is smaller
     * or equal to the buffer size, the value is written to the buffer. If it is
     * larger, then a new buffer is allocated to store the String in.
     * 
     * @param value to store
     * @return the change in length of the String compared to the previous value
     */
    public int update(CharSequence value) {
        int original_length = this._length;
        this._length = value.length();
        if (this._length > original_length) {
            this.assign(new char[this._length], 0, this._length);
        }
        for (int i = 0; i < this._length; i++) {
            this._buffer[this._position + i] = value.charAt(i);
        }
        return this._length - original_length;
    }

    /**
     * Gets the String stored in this buffer
     * 
     * @return String
     */
    @Override
    public String toString() {
        return new String(this._buffer, this._position, this._length);
    }

    @Override
    public int length() {
        return this._length;
    }

    @Override
    public char charAt(int index) {
        if (index < 0 || index >= this._length) {
            throw new IndexOutOfBoundsException();
        }
        return this._buffer[this._position + index];
    }

    @Override
    public CharArrayBuffer subSequence(int start, int end) {
        if (start < 0 || start >= this._length) {
            throw new IndexOutOfBoundsException("Start index is out of bounds");
        }
        if (end < start || end > this._length) {
            throw new IndexOutOfBoundsException("End index is out of bounds");
        }
        return new CharArrayBuffer(this._buffer, this._position + start, end - start);
    }

    /**
     * Checks whether the contents of this buffer equals the sequence of characters
     * specified.
     * 
     * @param other to compare against
     * @return True if equal
     */
    public boolean contentEquals(CharSequence other) {
        if (other.length() != this._length) {
            return false;
        }
        for (int i = 0; i < this._length; i++) {
            if (this._buffer[this._position + i] != other.charAt(i)) {
                return false;
            }
        }
        return true;
    }
}
