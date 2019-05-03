package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap;

public class IntHashMapTest {

    @Test
    public void testHashMap() {
        IntHashMap<String> test = new IntHashMap<String>();
        test.put(154, "test_string");
        assertEquals("test_string", test.get(154));
        assertTrue(test.contains(154));
        assertFalse(test.contains(155));

        IntHashMap.Entry<String> entry = test.getEntry(154);
        assertNotNull(entry);
        assertEquals(154, entry.getKey());
        assertEquals("test_string", entry.getValue());

        assertNull(test.getEntry(155));

        List<String> values = test.values();
        assertEquals(1, values.size());
        assertEquals("test_string", values.get(0));

        List<IntHashMap.Entry<String>> entries = test.entries();
        assertEquals(1, entries.size());
        assertEquals(154, entries.get(0).getKey());
        assertEquals("test_string", entries.get(0).getValue());

        Object handle = DuplexConversion.intHashMap.convertReverse(test);
        assertNotNull(handle);

        IntHashMap<String> original = CommonUtil.unsafeCast(Conversion.toIntHashMap.convert(handle));
        assertNotNull(original);
        assertEquals("test_string", original.get(154));

        entry.setValue("new_string");
        assertEquals("new_string", entry.getValue());
        assertEquals("new_string", test.get(154));
    }
}
