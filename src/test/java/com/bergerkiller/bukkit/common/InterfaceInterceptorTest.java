package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import org.junit.Test;

import com.bergerkiller.reflection.ClassInterceptor;
import com.bergerkiller.reflection.Invokable;

public class InterfaceInterceptorTest {

    @Test
    public void testInterfaceHook() {
        ClassInterceptor interceptor = new ClassInterceptor() {
            @Override
            protected Invokable getCallback(Method method) {
                if (!method.getName().equals("doTest")) {
                    return null;
                }
                return new Invokable() {
                    @Override
                    public Object invoke(Object instance, Object... args) {
                        return "Hello, World!";
                    }
                };
            }
        };

        Object test = interceptor.createInstance(UnknownInterface.class);
        assertTrue("Created interceptable object does not implement the interface", UnknownInterface.class.isInstance(test));
        assertEquals("Hello, World!", ((UnknownInterface) test).doTest());
    }

    // Some random interface we wish to implement with callbacks
    public static interface UnknownInterface {
        
        public String doTest();

    }
}
