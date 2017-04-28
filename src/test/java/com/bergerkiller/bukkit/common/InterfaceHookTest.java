package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import org.junit.Test;

import com.bergerkiller.mountiplex.reflection.ClassHook;

public class InterfaceHookTest {

    @Test
    public void testInterfaceHook() {
        InterfaceHook hooker = new InterfaceHook();
        Object created = hooker.createInstance(UnknownHookInterface.class);
        assertTrue("Created object does not implement the interface", created instanceof UnknownHookInterface);
        assertEquals("working!", ((UnknownHookInterface) created).doSomething());
    }

    public static class InterfaceHook extends ClassHook<InterfaceHook> {

        @HookMethod("public String doSomething()")
        public String doSomethingHooked() {
            return "working!";
        }
    }

    // Some random interface we wish to implement with callbacks
    public static interface UnknownHookInterface {

        public String doSomething();

    }
}
