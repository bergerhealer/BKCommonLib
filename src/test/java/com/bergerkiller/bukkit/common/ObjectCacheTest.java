package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.bergerkiller.bukkit.common.collections.ObjectCache;

public class ObjectCacheTest {

    @Test
    public void lol() {
        final ObjectCache<TestObject> cache = ObjectCache.create(TestObject::new, TestObject::reset);

        List<Thread> tasks = new ArrayList<Thread>();
        for (int i = 0; i < 10; i++) {
            Thread t = new Thread() {
                @Override
                public void run() {
                    for (int n = 0; n < 100000; n++) {
                        try (ObjectCache.Entry<TestObject> e = cache.create()) {
                            e.get().use();
                        }
                    }
                }
            };
            t.setDaemon(true);
            t.start();
            tasks.add(t);
        }
        for (Thread task : tasks) {
            try {
                task.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        assertTrue(TestObject.numInitialized.get() <= tasks.size());
        assertEquals(0, TestObject.numSyncError.get());
    }

    private static class TestObject {
        public static ArrayList<TestObject> all = new ArrayList<TestObject>();
        public static AtomicInteger numInitialized = new AtomicInteger(0);
        public static AtomicInteger numSyncError = new AtomicInteger(0);
        private final AtomicBoolean inUse = new AtomicBoolean(false);

        public TestObject() {
            numInitialized.incrementAndGet();
            synchronized (all) {
                all.add(this);
            }
        }

        public void use() {
            if (!inUse.compareAndSet(false, true)) {
                numSyncError.incrementAndGet();
            }
        }

        public void reset() {
            if (!inUse.compareAndSet(true, false)) {
                numSyncError.incrementAndGet();
            }
        }
    }
}
