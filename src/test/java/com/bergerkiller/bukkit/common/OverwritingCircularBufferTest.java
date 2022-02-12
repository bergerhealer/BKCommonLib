package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.bergerkiller.bukkit.common.collections.OverwritingCircularBuffer;

/**
 * Tests the OverwritingCircularBuffer class for its correct (multi-threaded) functioning
 */
public class OverwritingCircularBufferTest {

    @Test
    public void testSimple() {
        OverwritingCircularBuffer<String> buffer = OverwritingCircularBuffer.create(16);
        buffer.add("1");
        buffer.add("2");
        buffer.add("3");
        assertEquals(Arrays.asList("1", "2", "3"), buffer.values());
    }

    @Test
    public void testEmpty() {
        OverwritingCircularBuffer<String> buffer = OverwritingCircularBuffer.create(16);
        assertEquals(Arrays.asList(), buffer.values());
    }

    @Test
    public void testOne() {
        OverwritingCircularBuffer<String> buffer = OverwritingCircularBuffer.create(16);
        buffer.add("1");
        assertEquals(Arrays.asList("1"), buffer.values());
    }

    @Test
    public void testOverflow() {
        OverwritingCircularBuffer<String> buffer = OverwritingCircularBuffer.create(3);
        buffer.add("1");
        buffer.add("2");
        buffer.add("3");
        buffer.add("4");
        buffer.add("5");
        assertEquals(Arrays.asList("3", "4", "5"), buffer.values());
    }

    // Tests primarily that adding values from different threads doesn't result in messages getting lost
    @Test
    public void testMultiThreaded() {
        final AtomicInteger counter = new AtomicInteger(0);
        final int num_threads = 4;
        final int num_runs_per_thread = 10000;

        /*
         * Fill the buffer from many different threads with unique integer values
         */
        final OverwritingCircularBuffer<Integer> buffer = OverwritingCircularBuffer.create(num_runs_per_thread * num_threads);
        {
            Thread[] threads = new Thread[num_threads];
            for (int i = 0; i < threads.length; i++) {
                threads[i] = new Thread() {
                    @Override
                    public void run() {
                        for (int k = 0; k < num_runs_per_thread; k++) {
                            buffer.add(counter.incrementAndGet());
                        }
                    }
                };
            }
            for (Thread thread : threads) {
                thread.setDaemon(true);
                thread.start();
            }
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    /* ignore */
                }
            }
        }

        // By the end we expect that all numbers were generated
        assertEquals(num_runs_per_thread * num_threads, counter.get());

        // All values should now be contained in the buffer
        List<Integer> bufferContents = buffer.values();
        assertEquals(num_runs_per_thread * num_threads, bufferContents.size());
        int[] allValues = bufferContents.stream()
            .mapToInt(Integer::intValue)
            .sorted()
            .toArray();
        for (int n = 0; n < allValues.length; n++) {
            assertEquals(n+1, allValues[n]);
        }
    }
}
