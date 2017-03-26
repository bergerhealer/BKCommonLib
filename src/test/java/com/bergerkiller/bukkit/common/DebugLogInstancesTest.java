package com.bergerkiller.bukkit.common;

import java.util.HashMap;

import org.junit.Test;

import com.bergerkiller.bukkit.common.utils.DebugUtil;

// Tests the correct workings of DebugUtil.logInstances to make sure it works
public class DebugLogInstancesTest {

    @Test
    public void testLogInstances() {
        // Build a little tree of objects
        TestClass1 t1 = new TestClass1();
        TestClass2 t2 = new TestClass2();
        TestClass3 t3 = new TestClass3();
        t1.testValue2 = t2;
        t2.testValue1 = t1;
        t3.testValue1 = t1;
        TestClass2.testValue3 = t3;
        TestClass1.values.put("TestKey3", t3);

        DebugUtil.logInstances(TestClass2.class, t1);
        DebugUtil.logInstances(TestClass2.class, t2);
        DebugUtil.logInstances(TestClass2.class, t3);
    }
    
    
    public static class TestClass1 {
        public TestClass2 testValue2;
        public static HashMap<String, TestClass3> values = new HashMap<String, TestClass3>();
    }

    public static class TestClass2 {
        public static TestClass3 testValue3;
        public TestClass1 testValue1;
    }

    public static class TestClass3 {
        public TestClass1 testValue1;
    }
}
