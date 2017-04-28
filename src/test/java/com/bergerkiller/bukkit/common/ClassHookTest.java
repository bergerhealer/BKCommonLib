package com.bergerkiller.bukkit.common;

import org.junit.Test;
import static org.junit.Assert.*;

import com.bergerkiller.mountiplex.reflection.ClassHook;

public class ClassHookTest {

    @Test
    public void testClassHookMain() {
        Dog dog = new Dog();

        assertEquals("Dog::woof()", dog.woof());

        DogHook hook = new DogHook();

        Dog hooked_dog = hook.hook(dog);

        assertEquals("DogHook::theWoofMethod()", hooked_dog.woof());

        assertEquals("DogHook::theWoofMethod()", hook.theWoofMethod());
        assertEquals("Dog::woof()", hook.base.theWoofMethod());

        testTimings("    Original", new Dog());
        testTimings("Hooked proxy", hooked_dog);
    }

    @Test
    public void testClassHookBaseCalls() {
        Dog dog = new Dog();
        assertEquals("Dog::woof()", dog.woof());

        DogSuperHook hook = new DogSuperHook();
        Dog hooked_dog = hook.hook(dog);

        assertEquals("DogSuperHook::theWoofMethod() -> Dog::woof()", hooked_dog.woof());

        assertEquals("DogSuperHook::theWoofMethod() -> Dog::woof()", hook.theWoofMethod());
        assertEquals("Dog::woof()", hook.base.theWoofMethod());

        testTimings("    Original", new Dog());
        testTimings(" Hooked base", hooked_dog);
    }

    /*
     * Tests the correct working of mock() interception
     */
    @Test
    public void testMocking() {
        Dog dog = new Dog();
        DogHook hook = new DogHook();
        hook.mock(dog);

        assertEquals("Dog::woof()", dog.woof());
        assertEquals("Dog::woof()", hook.base.theWoofMethod());
        assertEquals("DogHook::theWoofMethod()", hook.theWoofMethod());
    }

    public static class Dog {

        public String woof() {
            return "Dog::woof()";
        }

    }

    public class DogHook extends ClassHook<DogHook> {
        @HookMethod("public String woof()")
        public String theWoofMethod() {
            return "DogHook::theWoofMethod()";
        }
    }

    public class DogSuperHook extends ClassHook<DogSuperHook> {
        @HookMethod("public String woof()")
        public String theWoofMethod() {
            return "DogSuperHook::theWoofMethod() -> " + base.theWoofMethod();
        }
    }

    public class DogLoopbackHook extends ClassHook<DogLoopbackHook> {
        @HookMethod("public String woof()")
        public String theWoofMethod() {
            return base.theWoofMethod();
        }
    }

    private static void testTimings(String info, Dog dog) {
        long start = System.nanoTime();
        for (long i = 0; i < 1000000; i++) {
            dog.woof();
        }
        long diff = (System.nanoTime() - start) / 1000000;
        System.out.println("[" + info + "] Elapsed nano time: " + diff + " ms");
    }
}
