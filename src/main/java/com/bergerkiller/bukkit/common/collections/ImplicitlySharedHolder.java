package com.bergerkiller.bukkit.common.collections;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

import com.bergerkiller.bukkit.common.utils.LogicUtil;

/**
 * Base class for a class that allows multiple instances to hold the same value,
 * but creates a copy of the data when writing. A copy is only created when
 * more than one instance is holding ownership over the data.
 */
public abstract class ImplicitlySharedHolder<T> implements AutoCloseable {
    protected final AtomicReference<Reference<T>> ref;

    /**
     * Initializes a new ImplicitlySharedHolder for the initial value specified. Looks up
     * a most-suitable cloning method for the value specific. If the value is null or
     * the value type lacks a clone() method, throws an IllegalArgumentException
     *
     * @param value Initial value
     * @throws IllegalArgumentException If the initial value is null or can't be cloned
     */
    public ImplicitlySharedHolder(T value) {
        this(value, LogicUtil.findCloneMethod(value));
    }

    /**
     * Initializes a new ImplicitlySharedHolder for the initial value and cloning function
     * specified.
     *
     * @param <V> Value type stored/cloned
     * @param value Initial value
     * @param cloneFunction Cloning function to make copied of the stored value
     */
    @SuppressWarnings("unchecked")
    public <V extends T> ImplicitlySharedHolder(V value, UnaryOperator<V> cloneFunction) {
        this.ref = new AtomicReference<>((Reference<T>) new Reference<V>(value, cloneFunction, 1));
    }

    protected ImplicitlySharedHolder(Reference<T> reference) {
        reference.openRead();
        this.ref = new AtomicReference<>(reference);
    }

    /**
     * Assigns the contents of an implicitly shared holder to this shared holder.
     * Future read calls will now read from the value of the holder instead of the contents that existed before.
     * The moment this shared holder is about to be modified, a detached copy is created.
     * 
     * @param sharedHolder to assign
     */
    public final void assign(ImplicitlySharedHolder<T> sharedHolder) {
        // Note assign to self will work fine, the openRead() and close() make parity
        Reference<T> new_ref = sharedHolder.ref.get();
        new_ref.openRead();
        Reference<T> old_ref = this.ref.getAndSet(new_ref);
        if (old_ref != null) {
            old_ref.close();
        }
    }

    /**
     * Gets whether this shared holder references the exact same backing value
     * as another shared holder.
     * 
     * @param sharedHolder to check
     * @return True if referencing the same value
     */
    public final boolean refEquals(ImplicitlySharedHolder<?> sharedHolder) {
        return sharedHolder != null && sharedHolder.ref.get() == this.ref.get();
    }

    /**
     * Closes this implicitly shared holder, so that it no longer holds access to the shared contents.
     * If this shared holder was created mirroring another shared holder, this call enables
     * that holder to modify the contents without copying.
     */
    @Override
    public final void close() {
        Reference<T> old_ref = this.ref.getAndSet(null);
        if (old_ref != null) {
            old_ref.close();
        }
    }

    /**
     * Opens exclusive write access, creating a copy of underlying data if required.
     * The returned reference should be closed, preferably using a try-with-resources.<br>
     * <br>
     * <b>This method is not re-entrant, and write() should not be called more than once
     * from one thread!</b>
     * 
     * @return reference to the data that should be modified next
     */
    protected final Reference<T> write() {
        Reference<T> old_ref = this.ref.get();

        try {
            // Fast: if readers is 1, set to WRITER_TOKEN to indicate exclusive write access
            // If this succeeds, then we can start writing right away
            // If this throws an exception, very likely ref is null.
            // In that case, throw a controlled SharedResourceClosedException instead.
            if (old_ref.openWrite()) {
                return old_ref;
            }
        } catch (RuntimeException ex) {
            if (old_ref == null) {
                throw new SharedResourceClosedException();
            } else {
                throw ex;
            }
        }

        // Can't write to this reference, a copy must be created
        // If another writer is busy with the reference, we must wait for it
        old_ref.openRead();
        try {
            // Having gained read access, create a copy of the data and swap the reference in use
            UnaryOperator<T> cloneFunc = old_ref.cloneFunction;
            Reference<T> new_ref = new Reference<T>(cloneFunc.apply(old_ref.val), cloneFunc, Reference.WRITE_TOKEN);
            this.ref.set(new_ref);
            return new_ref;
        } finally {
            // Close read access to the original reference
            old_ref.close();
        }
    }

    /**
     * Opens exclusive read access, no copy of the underlying data is created.
     * The returned reference should be closed, preferably using a try-with-resources.
     * 
     * @return reference to the data that should be read from.
     */
    protected final Reference<T> read() {
        Reference<T> ref = this.ref.get();
        try {
            // Increment counter one more time, making it at least 2.
            // This will wait until read access is available (nobody is writing).
            // If this throws an exception, very likely ref is null.
            // In that case, throw a controlled SharedResourceClosedException instead.
            ref.openRead();
            return ref;
        } catch (RuntimeException ex) {
            if (ref == null) {
                throw new SharedResourceClosedException();
            } else {
                throw ex;
            }
        }
    }

    /**
     * Base class for implicit sharing logic
     */
    protected static final class Reference<T> implements AutoCloseable {
        /// The readers field is set to this value when someone has exclusive write access
        /// This is a very low value, low enough that multiple increment() operations can
        /// not result in the value going positive. The write does close it, which will
        /// cause a single decrement(), which means we need one decrement step.
        private static final int WRITE_TOKEN = Integer.MIN_VALUE+1;
        /// Number of currently active read operations
        /// If this is -1, then somebody has exclusive access to write
        /// If this is 0, nobody is using it, and it can be written to without copying
        private final AtomicInteger readers;
        public final T val;
        public final UnaryOperator<T> cloneFunction;

        private Reference(T value, UnaryOperator<T> cloneFunction, int initialReaders) {
            this.readers = new AtomicInteger(initialReaders);
            this.val = value;
            this.cloneFunction = cloneFunction;
        }

        /**
         * Tries to write to the value stored by this reference.
         * Only if nobody is currently reading will this method return true.
         * 
         * @return True if writing was possible
         */
        public final boolean openWrite() {
            return this.readers.compareAndSet(1, WRITE_TOKEN);
        }

        /**
         * Gains read access to this reference, and waits until anyone
         * writing to this reference has finished. If writing, it assumes
         * writing will take a very short time.
         */
        public final void openRead() {
            int value;
            while ((value = this.readers.incrementAndGet()) < 0) {
                // Prevent the reader count from going up, reset to WRITE_TOKEN
                this.readers.compareAndSet(value, WRITE_TOKEN);
                // Yield (busy wait)
                Thread.yield();
            }
        }

        /**
         * Closes exclusive access to this reference
         */
        @Override
        public final void close() {
            // Decrement readers by one. If the count is negative, then
            // we previously opened as a writer, and the reader count
            // must be reset to 1.
            if (this.readers.decrementAndGet() < 0) {
                this.readers.set(1);
            }
        }
    }

    /**
     * Exception thrown when a shared resource is accessed after close() was called
     * on the resource. This means the resource no longer exists.
     */
    public static final class SharedResourceClosedException extends RuntimeException {
        private static final long serialVersionUID = -5807941780901855505L;

        public SharedResourceClosedException() {
            super("Shared resource was accessed after it was closed");
        }
    }
}
