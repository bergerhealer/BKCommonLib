package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

import com.bergerkiller.bukkit.common.collections.ImplicitlySharedList;
import com.bergerkiller.bukkit.common.collections.ImplicitlySharedSet;

/**
 * Tests container classes that use implicit sharing to avoid creating unneeded copies
 */
public class ImplicitSharingTest {

    @Test
    public void testImplicitSharedSet() {
        try (ImplicitlySharedSet<String> input = new ImplicitlySharedSet<String>()) {
            input.add("Hello");
            input.add("World");
            assertTrue(input.contains("Hello"));
            assertTrue(input.contains("World"));
            try (ImplicitlySharedSet<String> copy = input.clone()) {
                assertTrue(copy.contains("Hello"));
                assertTrue(copy.contains("World"));
                assertTrue(input.refEquals(copy));
                copy.add("Wide");
                assertFalse(input.refEquals(copy));
                assertTrue(copy.contains("Hello"));
                assertTrue(copy.contains("World"));
                assertTrue(copy.contains("Wide"));
                assertFalse(input.contains("Wide"));
                input.add("Web");
                assertTrue(input.contains("Web"));
                assertFalse(copy.contains("Web"));
            }

            // Safe copy iteration
            int n = 0;
            List<String> iterated = new ArrayList<String>();
            for (String element : input.cloneAsIterable()) {
                input.add("Hello" + (n++));
                iterated.add(element);
            }
            assertTrue(iterated.contains("Hello"));
            assertTrue(iterated.contains("World"));
            assertTrue(iterated.contains("Web"));
            assertFalse(iterated.contains("Hello0"));
            assertFalse(iterated.contains("Hello1"));
            assertFalse(iterated.contains("Hello2"));

            // Verify actually added
            assertTrue(input.contains("Hello0"));
            assertTrue(input.contains("Hello1"));
            assertTrue(input.contains("Hello2"));

            // Removing elements while iterating, should not remove from the copy
            try (ImplicitlySharedSet<String> input_copy = input.clone()) {
                Iterator<String> iter = input.iterator();
                while (iter.hasNext()) {
                    String s = iter.next();
                    if (s.equals("World")) {
                        iter.remove();
                    }
                }
                assertTrue(input.contains("Web"));
                assertFalse(input.contains("World"));
                assertTrue(input_copy.contains("World"));
            }
        }
    }

    @Test
    public void testImplicitlySharedList() {
        try (ImplicitlySharedList<String> input = new ImplicitlySharedList<String>()) {
            input.add("Hello");
            input.add("World");
            assertTrue(input.contains("Hello"));
            assertTrue(input.contains("World"));
            try (ImplicitlySharedList<String> copy = input.clone()) {
                assertTrue(copy.contains("Hello"));
                assertTrue(copy.contains("World"));
                assertTrue(input.refEquals(copy));
                copy.add("Wide");
                assertFalse(input.refEquals(copy));
                assertTrue(copy.contains("Hello"));
                assertTrue(copy.contains("World"));
                assertTrue(copy.contains("Wide"));
                assertFalse(input.contains("Wide"));
                input.add("Web");
                assertTrue(input.contains("Web"));
                assertFalse(copy.contains("Web"));
            }

            // Safe copy iteration
            int n = 0;
            List<String> iterated = new ArrayList<String>();
            for (String element : input.cloneAsIterable()) {
                input.add("Hello" + (n++));
                iterated.add(element);
            }
            assertTrue(iterated.contains("Hello"));
            assertTrue(iterated.contains("World"));
            assertTrue(iterated.contains("Web"));
            assertFalse(iterated.contains("Hello0"));
            assertFalse(iterated.contains("Hello1"));
            assertFalse(iterated.contains("Hello2"));

            // Verify actually added
            assertTrue(input.contains("Hello0"));
            assertTrue(input.contains("Hello1"));
            assertTrue(input.contains("Hello2"));

            // Removing elements while iterating, should not remove from the copy
            try (ImplicitlySharedList<String> input_copy = input.clone()) {
                Iterator<String> iter = input.iterator();
                while (iter.hasNext()) {
                    String s = iter.next();
                    if (s.equals("World")) {
                        iter.remove();
                    }
                }
                assertTrue(input.contains("Web"));
                assertFalse(input.contains("World"));
                assertTrue(input_copy.contains("World"));
            }
        }
    }

    @Test
    public void testImplicitSharedSetAsync() {
        // Verifies that iterating a clone on another thread has no impact on using the set

        // Set to work with
        try (final ImplicitlySharedSet<String> set = new ImplicitlySharedSet<String>()) {
            // These items are always present
            set.add("test1");
            set.add("test2");
            set.add("test3");

            // Start some threads
            final AtomicBoolean threadLoopFailed = new AtomicBoolean(false);
            final AtomicBoolean stop = new AtomicBoolean(false);
            List<Thread> threads = new ArrayList<Thread>();
            for (int i = 0; i < 10; i++) {
                threads.add(new Thread() {
                    @Override
                    @SuppressWarnings("unused")
                    public void run() {
                        try {
                            while (!stop.get()) {
                                int cnt = 0;
                                for (String s : set.cloneAsIterable()) {
                                    cnt++;
                                }
                                if (cnt != 3 && cnt != 4) {
                                    throw new IllegalStateException("Count is not 3 or 4: " + cnt);
                                }
                                Thread.sleep(0, 1000);
                            }
                        } catch (Throwable t) {
                            t.printStackTrace();
                            threadLoopFailed.set(true);
                        }
                    }
                });
            }

            // Start threads
            for (Thread t : threads) {
                t.start();
            }

            // Run for a little while, modifying the set continuously
            for (long n = 0; n < 1000000; n++) {
                set.add("test4");
                set.remove("test4");
                set.add("test5");
                set.remove("test5");
            }

            // Stop threads
            stop.set(true);
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Data loss while looping
            if (threadLoopFailed.get()) {
                fail("Failed to loop contents of set on one or more of the threads");
            }
        }
    }
}
