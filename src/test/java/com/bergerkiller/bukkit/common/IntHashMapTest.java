package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import org.junit.Test;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap;

public class IntHashMapTest {

    static {
        CommonUtil.bootstrap();
    }
    
    @Test
    public void testHashMap() {
        IntHashMap<String> test = new IntHashMap<String>();
        test.put(154, "test_string");
        assertEquals("test_string", test.get(154));

        Object handle = DuplexConversion.intHashMap.convertReverse(test);
        assertNotNull(handle);

        IntHashMap<String> original = (IntHashMap) Conversion.toIntHashMap.convert(handle);
        assertNotNull(original);
        assertEquals("test_string", original.get(154));
    }
}
