package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

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
                copy.add("Wide");
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
                copy.add("Wide");
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
        }
    }
}
