package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import org.junit.Test;

import com.bergerkiller.bukkit.common.collections.CharArrayBuffer;

public class CharArrayBufferTest {

    @Test
    public void testToString() {
        assertEquals("", new CharArrayBuffer().toString());
        assertEquals("hello", new CharArrayBuffer("hello").toString());
        assertEquals("hello", new CharArrayBuffer(new char[] {'1', '2', 'h', 'e', 'l', 'l', 'o'}, 2, 5).toString());
    }

    @Test
    public void testLength() {
        CharArrayBuffer buffer = new CharArrayBuffer();
        assertEquals(0, buffer.length());

        buffer = new CharArrayBuffer("hello");
        assertEquals(5, buffer.length());

        buffer.update("helloworld");
        assertEquals(10, buffer.length());

        buffer.update("helloworl");
        assertEquals(9, buffer.length());
    }

    @Test
    public void testUpdateWithString() {
        CharArrayBuffer buffer = new CharArrayBuffer();

        assertEquals(5, buffer.update("hello"));
        assertEquals("hello", buffer.toString());
        assertEquals('l', buffer.charAt(2));

        assertEquals(-2, buffer.update("hey"));
        assertEquals("hey", buffer.toString());
        assertEquals('y', buffer.charAt(2));

        assertEquals(2, buffer.update("hewwo"));
        assertEquals("hewwo", buffer.toString());
        assertEquals('w', buffer.charAt(2));
    }

    @Test
    public void testUpdateWithCharSequence() {
        CharArrayBuffer buffer = new CharArrayBuffer();

        assertEquals(5, buffer.update((CharSequence) "hello"));
        assertEquals("hello", buffer.toString());
        assertEquals('l', buffer.charAt(2));

        assertEquals(-2, buffer.update((CharSequence) "hey"));
        assertEquals("hey", buffer.toString());
        assertEquals('y', buffer.charAt(2));

        assertEquals(2, buffer.update((CharSequence) "hewwo"));
        assertEquals("hewwo", buffer.toString());
        assertEquals('w', buffer.charAt(2));
    }

    @Test
    public void testUpdateWithCharArrayBuffer() {
        CharArrayBuffer buffer = new CharArrayBuffer();

        assertEquals(5, buffer.update(new CharArrayBuffer("hello")));
        assertEquals("hello", buffer.toString());
        assertEquals('l', buffer.charAt(2));

        assertEquals(-2, buffer.update(new CharArrayBuffer("hey")));
        assertEquals("hey", buffer.toString());
        assertEquals('y', buffer.charAt(2));

        assertEquals(2, buffer.update(new CharArrayBuffer("hewwo")));
        assertEquals("hewwo", buffer.toString());
        assertEquals('w', buffer.charAt(2));
    }

    @Test
    public void testMoveToBuffer() {
        char[] output_buffer = new char[] {'X','X','X','X','X','X','X','X'};
        CharArrayBuffer buffer = new CharArrayBuffer("hello");

        // Move the buffer to the array at the position
        // We expect the value to be copied and the buffer reference to be updated
        assertEquals(7, buffer.moveToBuffer(output_buffer, 2));
        assertEquals("XXhelloX", new String(output_buffer));
        assertEquals("hello", buffer.toString());

        // Changes to the buffer should be reflected by the CharArrayBuffer
        output_buffer[2] = 'j';
        assertEquals("jello", buffer.toString());
    }

    @Test
    public void testSwapBuffer() {
        char[] input_buffer = new char[] {'X','X','h','e','l','l','o','X'};
        CharArrayBuffer buffer = new CharArrayBuffer("XXXXX");

        // Swap the buffers. CharArrayBuffer should now refer to 'hello' (same length)
        buffer.swapBuffer(input_buffer, 2);
        assertEquals("XXhelloX", new String(input_buffer));
        assertEquals("hello", buffer.toString());

        // Changes to the buffer should be reflected by the CharArrayBuffer
        input_buffer[2] = 'j';
        assertEquals("jello", buffer.toString());
    }

    @Test
    public void testCopyToString() {
        char[] input_buffer = new char[] {'X','X','h','e','l','l','o','e','x','t','r','a','X'};
        CharArrayBuffer buffer = new CharArrayBuffer(input_buffer, 2, 5);

        assertEquals("hello", buffer.copyToString(5));
        assertEquals("helloextra", buffer.copyToString(10));
    }

    @Test
    public void testCopyTo() {
        char[] input_buffer = new char[] {'X','X','h','e','l','l','o','e','x','t','r','a','X'};
        char[] output_buffer = new char[] {'X','X','X','X','X','X','X','X','X','X','X','X','X'};
        CharArrayBuffer buffer = new CharArrayBuffer(input_buffer, 2, 5);

        buffer.copyTo(output_buffer, 2, 5);
        assertEquals("XXhelloXXXXXX", new String(output_buffer));

        buffer.copyTo(output_buffer, 2, 10);
        assertEquals("XXhelloextraX", new String(output_buffer));
    }

    @Test
    public void testSubSequence() {
        CharArrayBuffer buffer = new CharArrayBuffer("helloworld");
        assertTrue(buffer.contentEquals("helloworld"));
        assertTrue("hello".contentEquals(buffer.subSequence(0, 5)));
        assertTrue(buffer.subSequence(0, 5).contentEquals("hello"));
        assertTrue("world".contentEquals(buffer.subSequence(5, 10)));
        assertTrue(buffer.subSequence(5, 10).contentEquals("world"));
        assertTrue("helloworld".contentEquals(buffer.subSequence(0, 10)));
        assertTrue(buffer.subSequence(0, 10).contentEquals("helloworld"));
    }
}
