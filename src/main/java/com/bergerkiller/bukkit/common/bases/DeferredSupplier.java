package com.bergerkiller.bukkit.common.bases;

import java.util.function.Supplier;

/**
 * Caches the result of a supplier, so it only has to be called once. This is
 * useful if initializing the value takes some time, or the getter imposes
 * an overhead. The returned supplier is thread-safe. Multiple gets that
 * occur from different threads will guaranteed only initialize the value once.<br>
 * <br>
 * Includes a method to tell whether this deferred supplier was called at all,
 * and a value is initialized already.
 */
public final class DeferredSupplier<T> implements Supplier<T> {
    private final Supplier<T> supplier;
    private volatile boolean initialized;
    private T value;

    private DeferredSupplier(Supplier<T> supplier) {
        this.supplier = supplier;
        this.initialized = false;
        this.value = null;
    }

    /**
     * Gets whether a value has been initialized by this
     * supplier. If this returns true, then the value can
     * be returned without any expensive work, guaranteed.
     *
     * @return True if the value was initialized and {@link #get()}
     *         is cheap.
     */
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public T get() {
        if (initialized) {
            return value;
        } else {
            synchronized (this) {
                if (!initialized) {
                    value = supplier.get();
                    initialized = true;
                }
                return value;
            }
        }
    }

    /**
     * Creates a new deferred supplier for the specified supplier.
     *
     * @param <T> Type of value
     * @param supplier Supplier to retrieve the value
     * @return Deferred supplier
     */
    public static <T> DeferredSupplier<T> of(Supplier<T> supplier) {
        return new DeferredSupplier<T>(supplier);
    }
}
