package com.bergerkiller.bukkit.common.collections;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Base class for a class that allows multiple instances to hold the same value,
 * but creates a copy of the data when writing. A copy is only created when
 * more than one instance is holding ownership over the data.
 */
public abstract class ImplicitlySharedHolder<T> implements AutoCloseable {
    protected Reference<T> ref;

    public ImplicitlySharedHolder(T value) {
        this(new Reference<T>(value));
    }

    protected ImplicitlySharedHolder(Reference<T> reference) {
        this.ref = reference;
        this.ref.increment();
    }

    /**
     * Assigns the contents of an implicitly shared holder to this shared holder.
     * Future read calls will now read from the value of the holder instead of the contents that existed before.
     * The moment this shared holder is about to be modified, a detached copy is created.
     * 
     * @param sharedHolder to assign
     */
    public final void assign(ImplicitlySharedHolder<T> sharedHolder) {
        this.close();
        this.ref = sharedHolder.ref;
        this.ref.increment();
    }

    /**
     * Gets whether this shared holder references the exact same backing value
     * as another shared holder.
     * 
     * @param sharedHolder to check
     * @return True if referencing the same value
     */
    public final boolean refEquals(ImplicitlySharedHolder<?> sharedHolder) {
        return sharedHolder != null && sharedHolder.ref == this.ref;
    }

    /**
     * Closes this implicitly shared holder, so that it no longer holds access to the shared contents.
     * If this shared holder was created mirroring another shared holder, this call enables
     * that holder to modify the contents without copying.
     */
    @Override
    public final void close() {
        if (this.ref != null) {
            this.ref.decrement();
            this.ref = null;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        // Note: this should not be relied upon
        // It is here more 'just because we can'
        try {
            this.close();
        } finally {
            super.finalize();
        }
    }

    /**
     * Opens exclusive write access, creating a copy of underlying data if required.
     * The returned reference should be closed, preferably using a try-with-resources.
     * 
     * @return reference to the data that should be modified next
     */
    protected final Reference<T> write() {
        Reference<T> old_ref = this.read();

        // If old_ref has more than one instance using it,
        // we must create a copy of it and assign it to this instance.
        if (old_ref.ctr > 1) {
            old_ref.ctr--;

            // Create a clone (while having old_ref locked!)
            // Lock this clone, so it can not be modified the moment it is assigned
            Reference<T> new_ref = new Reference<T>(this.cloneValue(old_ref.val));
            new_ref.ctr++;
            new_ref.open();

            // Assign the clone and unlock the old ref we had locked.
            this.ref = new_ref;
            old_ref.close();

            return new_ref;
        }
        return old_ref;
    }

    /**
     * Opens exclusive read access, no copy of the underlying data is created.
     * The returned reference should be closed, preferably using a try-with-resources.
     * 
     * @return reference to the data that should be read from.
     */
    protected final Reference<T> read() {
        // Acquire exclusive lock of the 'ref'
        // After a lock succeeds, the 'ref' parameter may have changed
        // In that case re-lock using the appropriate new ref
        // until the lock succeeds.
        Reference<T> old_ref;
        for (;;) {
            old_ref = this.ref;
            old_ref.open();
            if (old_ref == this.ref) {
                break;
            } else {
                old_ref.close();
            }
        }
        return old_ref;
    }

    /**
     * Implement the logic for cloning the held value here
     * 
     * @param input
     * @return cloned input
     */
    protected abstract T cloneValue(T input);

    /**
     * Base class for implicit sharing logic
     */
    protected static final class Reference<T> implements AutoCloseable {
        private final Lock lock;
        private int ctr;
        public final T val;

        public Reference(T value) {
            this.lock = new ReentrantLock();
            this.ctr = 0;
            this.val = value;
        }

        /**
         * Decrements the reference counter by one
         */
        public final void decrement() {
            this.lock.lock();
            this.ctr--;
            this.lock.unlock();
        }

        /**
         * Increments the reference counter by one
         */
        public final void increment() {
            this.lock.lock();
            this.ctr++;
            this.lock.unlock();
        }

        /**
         * Opens exclusive access to this reference
         */
        public final void open() {
            this.lock.lock();
        }

        /**
         * Closes exclusive access to this reference
         */
        @Override
        public final void close() {
            this.lock.unlock();
        }
    }

}
