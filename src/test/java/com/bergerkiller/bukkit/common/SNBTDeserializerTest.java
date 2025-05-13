package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.config.SNBTDeserializer;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Tests {@link SNBTDeserializer}
 */
public class SNBTDeserializerTest {

    @Test
    public void testCustomModelData() {
        assertEquals(MapBuilder.create()
                        .put("colors", Arrays.asList(1, 2, 3))
                        .put("flags", Arrays.asList((byte) 1, (byte) 0, (byte) 1))
                        .put("floats", Arrays.asList(1.0f, 2.0f, 4.0f))
                        .put("strings", Arrays.asList("a", "b", "c"))
                        .build(),
                SNBTDeserializer.parse("{colors:[1,2,3],flags:[1b,0b,1b],floats:[1.0f,2.0f,4.0f],strings:[\"a\",\"b\",\"c\"]}"));
    }

    @Test
    public void testMap() {
        assertEquals(MapBuilder.create()
                .put("a", 1)
                .put("b", 2)
                .build(),
                SNBTDeserializer.parse("{a:1,b:2}"));
        assertEquals(MapBuilder.create()
                        .put("a", 1)
                        .put("b", 2)
                        .build(),
                SNBTDeserializer.parse("{  a  :  1  ,  b  :  2  }"));
        assertEquals(MapBuilder.create()
                        .put(":a:", 1)
                        .put(",b,", 2)
                        .build(),
                SNBTDeserializer.parse("{':a:':1,',b,':2}"));
        assertEquals(MapBuilder.create()
                        .put("a", 1)
                        .put("b", 2)
                        .build(),
                SNBTDeserializer.parse("{,a:1,b:2,}"));
    }

    @Test
    public void testList() {
        assertEquals(Arrays.asList(1, 2, 3), SNBTDeserializer.parse("[1,2,3]"));
        assertEquals(Arrays.asList(1, 2, 3), SNBTDeserializer.parse("[ 1 , 2 , 3 ]"));
        assertEquals(Arrays.asList(1, 2, 3), SNBTDeserializer.parse("[,1,2,3,]"));
        assertEquals(Arrays.asList((short) 1, (short) 2, (short) 3), SNBTDeserializer.parse("[1s,2s,3s]"));
        assertEquals(Arrays.asList("a", "b", "c"), SNBTDeserializer.parse("['a','b','c']"));
        assertEquals(Arrays.asList("a", "b", "c"), SNBTDeserializer.parse("[  'a'  ,  'b'  ,  'c'  ]"));
    }

    @Test
    public void testArray() {
        assertArrayEquals(new byte[] { 1, 2, 3 }, (byte[]) SNBTDeserializer.parse("[B;1,2,3]"));
        assertArrayEquals(new byte[] { 1, 2, 3 }, (byte[]) SNBTDeserializer.parse("[B;1b,2s,3l]"));
        assertArrayEquals(new int[] { 1, 2, 3 }, (int[]) SNBTDeserializer.parse("[I;1,2,3]"));
        assertArrayEquals(new int[] { 1, 2, 3 }, (int[]) SNBTDeserializer.parse("[I;1b,2s,3l]"));
        assertArrayEquals(new long[] { 1, 2, 3 }, (long[]) SNBTDeserializer.parse("[L;1,2,3]"));
        assertArrayEquals(new long[] { 1, 2, 3 }, (long[]) SNBTDeserializer.parse("[L;1b,2,3s]"));
    }

    @Test
    public void testString() {
        assertEquals("hello", SNBTDeserializer.parse("'hello'"));
        assertEquals("hello", SNBTDeserializer.parse("\"hello\""));
        assertEquals("'hello'", SNBTDeserializer.parse("'\\'hello\\''"));
        assertEquals("\"hello\"", SNBTDeserializer.parse("\"\\\"hello\\\"\""));
    }

    @Test
    public void testBoolean() {
        assertEquals((byte) 1, SNBTDeserializer.parse("1b"));
        assertEquals((byte) 0, SNBTDeserializer.parse("0b"));
        assertEquals((byte) 1, SNBTDeserializer.parse("true"));
        assertEquals((byte) 0, SNBTDeserializer.parse("false"));
    }

    @Test
    public void testPositiveNumbers() {
        assertEquals(12, SNBTDeserializer.parse("12"));
        assertEquals(12.0f, SNBTDeserializer.parse("12f"));
        assertEquals(12.0f, SNBTDeserializer.parse("12F"));
        assertEquals(12.0, SNBTDeserializer.parse("12.0"));
        assertEquals(12.0, SNBTDeserializer.parse("12.0d"));
        assertEquals(12.0, SNBTDeserializer.parse("12.0D"));
        assertEquals((short) 12, SNBTDeserializer.parse("12s"));
        assertEquals((short) 12, SNBTDeserializer.parse("12S"));
        assertEquals((byte) 12, SNBTDeserializer.parse("12b"));
        assertEquals((byte) 12, SNBTDeserializer.parse("12B"));
    }

    @Test
    public void testNegativeNumbers() {
        assertEquals(-12, SNBTDeserializer.parse("-12"));
        assertEquals(-12.0f, SNBTDeserializer.parse("-12f"));
        assertEquals(-12.0f, SNBTDeserializer.parse("-12F"));
        assertEquals(-12.0, SNBTDeserializer.parse("-12.0"));
        assertEquals(-12.0, SNBTDeserializer.parse("-12.0d"));
        assertEquals(-12.0, SNBTDeserializer.parse("-12.0D"));
        assertEquals((short) -12, SNBTDeserializer.parse("-12s"));
        assertEquals((short) -12, SNBTDeserializer.parse("-12S"));
        assertEquals((byte) -12, SNBTDeserializer.parse("-12b"));
        assertEquals((byte) -12, SNBTDeserializer.parse("-12B"));
    }

    @Test
    public void testBrokenFormat() {
        assertEquals("teststr", SNBTDeserializer.parse("\"teststr"));
        assertEquals("teststr", SNBTDeserializer.parse("'teststr"));
        assertEquals(MapBuilder.create()
                        .put("a", 1)
                        .build(),
                SNBTDeserializer.parse("{a:1,"));
        assertEquals(MapBuilder.create()
                        .put("a", 1)
                        .put("b", null)
                        .build(),
                SNBTDeserializer.parse("{a:1,b:"));
        assertEquals(MapBuilder.create()
                        .put("a", (byte) 1)
                        .put("extra", null)
                        .build(),
                SNBTDeserializer.parse("{a:1bextra}"));
    }

    private static class MapBuilder {
        public static MapBuilder create() {
            return new MapBuilder();
        }

        private Map<String, Object> result = new LinkedHashMap<>();

        public MapBuilder put(String key, Object value) {
            result.put(key, value);
            return this;
        }

        public Map<String, Object> build() {
            return result;
        }
    }
}
