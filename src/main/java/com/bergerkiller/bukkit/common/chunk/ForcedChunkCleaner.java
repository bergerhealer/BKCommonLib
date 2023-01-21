package com.bergerkiller.bukkit.common.chunk;

import java.lang.ref.Cleaner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.chunk.ForcedChunkManager.ForcedChunkEntry;

/**
 * Contains the logic of what to do when a ForcedChunk close() isn't called by somebody.
 * On JDK8 overrides finalize() to handle it, on JDK9+ uses the Cleaner API.
 * When this happens the stack trace is tracked (if it wasn't already) and logged.
 * Then the chunk is closed, anyway.
 * This mechanism should not be relied upon!
 */
abstract class ForcedChunkCleaner {

    /**
     * Creates a new ForcedChunkCleaner instance approppriate for the current JVM
     *
     * @return cleaner
     */
    public static ForcedChunkCleaner create() {
        // If JDK9 Cleaner API is available, use that. Otherwise use JDK8 fallback.
        try {
            Class.forName("java.lang.ref.Cleaner");
            return createCleanerJDK9();
        } catch (Throwable t) {
            return createCleanerJDK8();
        }
    }

    /**
     * Called when the plugin shuts down to stop any background threads/etc.
     */
    public abstract void shutdown();

    /**
     * Creates a new ForcedChunk whose garbage-collection is tracked with a cleaner handler
     *
     * @param entry
     * @return forced chunk
     */
    public abstract ForcedChunk createDefault(ForcedChunkEntry entry);

    /**
     * Creates a new ForcedChunk whose garbage-collection is tracked with a cleaner handler.
     * The stack specified is logged if the object is not garbage collected.
     *
     * @param entry
     * @param stack
     * @return forced chunk
     */
    public abstract ForcedChunk createAndTrackStack(ForcedChunkEntry entry, Throwable stack);

    private static ForcedChunkCleaner createCleanerJDK8() {
        return new CleanerJDK8();
    }

    private static ForcedChunkCleaner createCleanerJDK9() {
        return new CleanerJDK9();
    }

    /**
     * Cleaner instance for use on JDK8
     */
    private static class CleanerJDK8 extends ForcedChunkCleaner {
        private final AtomicReference<PendingItem> pending = new AtomicReference<>();
        private final Object shutdownWaitLock = new Object();
        private boolean shutdown = false;
        private final Thread processThread;

        public CleanerJDK8() {
            processThread = new Thread(this::processLoop, "ForcedChunkCleanerThread");
            processThread.setDaemon(true);
            processThread.start();
        }

        @Override
        public void shutdown() {
            synchronized (shutdownWaitLock) {
                shutdown = true;
                shutdownWaitLock.notifyAll();
            }
            try {
                processThread.join(10000);
            } catch (Throwable t) { /* whatever */ }
        }

        @Override
        public ForcedChunk createDefault(ForcedChunkEntry entry) {
            return new ForcedChunkHandlingFinalize(this, entry, null);
        }

        @Override
        public ForcedChunk createAndTrackStack(ForcedChunkEntry entry, Throwable stack) {
            return new ForcedChunkHandlingFinalize(this, entry, stack);
        }

        private void processLoop() {
            boolean shutdown;
            do {
                // Wait 2 seconds for pending stuff to be added
                // We cannot use synchronized for items because gc could kick in at any moment
                // But we can use it to stop waiting sooner when shutdown() is called
                synchronized (shutdownWaitLock) {
                    try {
                        shutdownWaitLock.wait(2000);
                    } catch (InterruptedException e) {}
                    shutdown = this.shutdown;
                }

                // Process pending stuff
                PendingItem item;
                while ((item = pending.getAndSet(null)) != null) {
                    // Fill list of actual items. These are in reversed order.
                    List<PendingItem> allItems = new ArrayList<>();
                    for (PendingItem p = item; p != null; p = p.prev) {
                        if (!p.processed.getAndSet(true)) {
                            allItems.add(p);
                        }
                    }
                    Collections.reverse(allItems);
                    allItems.forEach(PendingItem::clean);
                }
            } while (!shutdown);
        }

        // Note: only ever called from the garbage collector. Synchronized is to avoid pending
        // entries disappearing. Probably only one gc thread ever calls this anyway.
        public synchronized void processLater(ForcedChunkEntry entry, Throwable stack) {
            pending.set(new PendingItem(pending.get(), entry, stack));
        }

        private static class PendingItem {
            public final PendingItem prev;
            public final AtomicBoolean processed;
            public final CleanerFunction cleaner;

            public PendingItem(PendingItem prev, ForcedChunkEntry entry, Throwable stack) {
                this.prev = prev;
                this.processed = new AtomicBoolean(false);
                if (stack == null) {
                    this.cleaner = new CleanerFunction(new AtomicReference<>(entry));
                } else {
                    this.cleaner = new CleanerFunctionStackTracked(new AtomicReference<>(entry), stack);
                }
            }

            public void clean() {
                try {
                    try {
                        cleaner.run();
                    } catch (Throwable t) {
                        Logging.LOGGER.log(Level.SEVERE, "Error occurred handling missed ForcedChunk close()", t);
                    }
                } catch (Throwable t2) {
                    // If this happens then we're kinda screwed
                    t2.printStackTrace();
                }
            }
        }
    }

    /**
     * Cleaner instance for use on JDK9+
     */
    private static class CleanerJDK9 extends ForcedChunkCleaner {
        private final Cleaner cleaner = Cleaner.create();

        @Override
        public void shutdown() {
            // cleaner shuts itself down I guess
        }

        @Override
        public ForcedChunk createDefault(ForcedChunkEntry entry) {
            ForcedChunk chunk = new ForcedChunk(entry);
            cleaner.register(chunk, new CleanerFunction(chunk.entry));
            return chunk;
        }

        @Override
        public ForcedChunk createAndTrackStack(ForcedChunkEntry entry, Throwable stack) {
            ForcedChunk chunk = new ForcedChunk(entry);
            cleaner.register(chunk, new CleanerFunctionStackTracked(chunk.entry, stack));
            return chunk;
        }
    }

    /**
     * Only used on JDK8. Executes the cleaner function when the object itself is garbage
     * collected.
     */
    private static class ForcedChunkHandlingFinalize extends ForcedChunk {
        private final CleanerJDK8 cleaner;
        private final Throwable stack;

        protected ForcedChunkHandlingFinalize(CleanerJDK8 cleaner, ForcedChunkEntry entry, Throwable stack) {
            super(entry);
            this.cleaner = cleaner;
            this.stack = stack;
        }

        @Override
        @SuppressWarnings("deprecation")
        public final void finalize() throws Throwable {
            ForcedChunkManager.ForcedChunkEntry entry = this.entry.getAndSet(null);
            if (entry != null) {
                cleaner.processLater(entry, stack);
            }

            super.finalize();
        }
    }

    /**
     * Tracks the stack trace of when the object was requested
     */
    private static final class CleanerFunctionStackTracked extends CleanerFunction {
        private final Throwable stack;

        public CleanerFunctionStackTracked(AtomicReference<ForcedChunkEntry> entry, Throwable stack) {
            super(entry);
            this.stack = stack;
        }

        @Override
        public void log(ForcedChunkManager.ForcedChunkEntry entry) {
            Logging.LOGGER_DEBUG.log(Level.WARNING, "ForcedChunk.close() was not called for " + entry.toString() +
                    ", it was created at:", stack);
        }
    }

    /**
     * Default function. Stack trace isn't tracked at all.
     */
    private static class CleanerFunction implements Runnable {
        private final AtomicReference<ForcedChunkManager.ForcedChunkEntry> entry;

        public CleanerFunction(AtomicReference<ForcedChunkManager.ForcedChunkEntry> entry) {
            this.entry = entry;
        }

        public void log(ForcedChunkManager.ForcedChunkEntry entry) {
            Logging.LOGGER_DEBUG.log(Level.WARNING, "ForcedChunk.close() was not called for " + entry.toString());
            entry.getManager().setTrackingCreationStack(true);
        }

        public final void clean(ForcedChunkManager.ForcedChunkEntry entry) {
            try {
                log(entry);
            } finally {
                entry.remove();
            }
        }

        // Only used with JDK9 cleaner API
        @Override
        public final void run() {
            ForcedChunkManager.ForcedChunkEntry entry = this.entry.getAndSet(null);
            if (entry != null) {
                clean(entry);
            }
        }
    }
}
