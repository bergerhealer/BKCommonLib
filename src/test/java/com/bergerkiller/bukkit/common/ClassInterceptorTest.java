package com.bergerkiller.bukkit.common;

import java.lang.reflect.Method;

import org.junit.Test;

import com.bergerkiller.mountiplex.reflection.ClassInterceptor;
import com.bergerkiller.mountiplex.reflection.Invokable;

import static org.junit.Assert.*;

public class ClassInterceptorTest {

    @Test
    public void testClassInterception() {
        ClassInterceptor enhancer = new CatInterceptor("woof");

        Cat old_cat = new Cat("meow");
        assertEquals("meow", old_cat.meow());

        Cat dna_engineered_cat = enhancer.hook(old_cat);
        assertEquals("woof", dna_engineered_cat.meow());

        ClassInterceptor enhancer2 = new CatInterceptor("ribbit");
        Cat old_cat2 = new Cat("meow");
        assertEquals("meow", old_cat2.meow());

        Cat dna_engineered_cat2 = enhancer2.hook(old_cat2);
        assertEquals("ribbit", dna_engineered_cat2.meow());
        assertEquals("woof", dna_engineered_cat.meow());

        Cat unhooked_cat = ClassInterceptor.unhook(dna_engineered_cat);
        assertEquals("meow", unhooked_cat.meow());
    }

    /*
     * This test is needed to verify that many different threads calling a method
     * does not cause a method to be called on an entirely different object.
     */
    @Test
    public void testClassThreading() {
        int num_threads = 50;

        ClassInterceptor interceptor = new ClassInterceptor() {
            @Override
            protected Invokable getCallback(final Method method) {
                if (method.getName() == "meow") {
                    return new Invokable() {
                        @Override
                        public Object invoke(Object instance, Object... args) {
                            return invokeSuperMethod(method, instance, args);
                        }
                    };
                }
                return null;
            }
        };

        CrossThreadTest[] tests = new CrossThreadTest[num_threads];
        for (int i = 0; i < tests.length; i++) {
            tests[i] = new CrossThreadTest(interceptor, "Meow#" + i);
        }

        for (int i = 0; i < tests.length; i++) {
            tests[i].start();
        }
        for (int i = 0; i < tests.length; i++) {
            try {
                tests[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static class CrossThreadTest extends Thread {
        private Cat cat;
        private String expectedCall;

        public CrossThreadTest(ClassInterceptor interceptor, String expectedCall) {
            this.cat = interceptor.hook(new Cat(expectedCall));
            this.cat = new Cat(expectedCall);
            this.expectedCall = expectedCall;
        }
 
        @Override
        public void run() {
            for (long i = 0; i < 20000; i++) {
                assertEquals(expectedCall, this.cat.meow());
                Thread.yield();
            }
        }
    }

    public static class Cat {
        private String call;

        public Cat(String call) {
            this.call = call;
        }

        public String meow() {
            return call;
        }
    }

    private static class CatInterceptor extends ClassInterceptor {
        private final String newCall;

        public CatInterceptor(String newCall) {
            this.newCall = newCall;
        }

        @Override
        protected Invokable getCallback(Method method) {
            if (method.getName() == "meow") {
                return new Invokable() {
                    @Override
                    public Object invoke(Object instance, Object... args) {
                        return newCall;
                    }
                };
            }
            return null;
        }
    }
}
