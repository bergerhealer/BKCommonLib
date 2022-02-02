package com.bergerkiller.bukkit.common.bases;

/**
 * Caches the result of a checked supplier, so it only has to be called once. This is
 * useful if initializing the value takes some time, or the getter imposes
 * an overhead. The returned supplier is thread-safe. Multiple gets that
 * occur from different threads will guaranteed only initialize the value once.
 * If an error is thrown, then this error will be re-thrown in future calls to
 * get() without calling the base supplier a second time.<br>
 * <br>
 * Includes a method to tell whether this deferred supplier was called at all,
 * and a value is initialized already. And to check error status.
 */
public final class CheckedDeferredSupplier<T> implements CheckedSupplier<T> {
    private ErrorHandlingCheckedSupplier<T> supplier;

    private CheckedDeferredSupplier(CheckedSupplier<T> supplier) {
        this.supplier = new InitializingSupplier(supplier);
    }

    private CheckedDeferredSupplier(ErrorHandlingCheckedSupplier<T> supplier) {
        this.supplier = supplier;
    }

    /**
     * Gets whether a value has been initialized by this
     * supplier. If this returns true, then the value can
     * be returned without any expensive work, guaranteed.<br>
     * <br>
     * This also returns true if an error occurred in the base-supplier,
     * which should be checked with {@link #hasError()}.
     *
     * @return True if the value was initialized and {@link #get()}
     *         is cheap.
     */
    public boolean isInitialized() {
        return supplier.isInitialized();
    }

    /**
     * Gets whether an error occurred during initialization or not.
     * Will call the base supplier if it hasn't been called before.
     *
     * @return True if an error had occurred
     */
    public boolean hasError() {
        return supplier.hasError();
    }

    /**
     * Gets an error that occurred if {@link #hasError()} is true, otherwise
     * returns null
     *
     * @return Error, or null if no error occurred
     */
    public Throwable getError() {
        return supplier.getError();
    }

    @Override
    public T get() throws Throwable {
        return supplier.get();
    }

    /**
     * Creates a new deferred checked supplier for the specified supplier.
     *
     * @param <T> Type of value
     * @param supplier CheckedSupplier to retrieve the value
     * @return Deferred checked supplier
     */
    public static <T> CheckedDeferredSupplier<T> of(CheckedSupplier<T> supplier) {
        return new CheckedDeferredSupplier<T>(supplier);
    }

    /**
     * Same as {@link #of(CheckedSupplier)} but calls the supplier right away,
     * forcing the result to be initialized. If an error occurs, this error is
     * not thrown by this method but will be thrown next time {@link #get()}
     * is called.
     *
     * @param <T> Type of value
     * @param supplier CheckedSupplier to retrieve the value
     * @return Initialized deferred checked supplier
     */
    public static <T> CheckedDeferredSupplier<T> call(CheckedSupplier<T> supplier) {
        try {
            T result = supplier.get();
            return new CheckedDeferredSupplier<T>(new ResultSupplier<T>(result));
        } catch (Throwable t) {
            return new CheckedDeferredSupplier<T>(new ErrorSupplier<T>(t));
        }
    }

    private final class InitializingSupplier implements ErrorHandlingCheckedSupplier<T> {
        private final CheckedSupplier<T> baseSupplier;

        public InitializingSupplier(CheckedSupplier<T> baseSupplier) {
            this.baseSupplier = baseSupplier;
        }

        @Override
        public T get() throws Throwable {
            synchronized (CheckedDeferredSupplier.this) {
                if (supplier == this) {
                    try {
                        T result = baseSupplier.get();
                        supplier = new ResultSupplier<T>(result);
                        return result;
                    } catch (Throwable t) {
                        supplier = new ErrorSupplier<T>(t);
                        throw t;
                    }
                }
            }

            return supplier.get();
        }

        @Override
        public boolean isInitialized() {
            return false;
        }

        @Override
        public boolean hasError() {
            try {
                get();
                return false;
            } catch (Throwable t) {
                return true;
            }
        }

        @Override
        public Throwable getError() {
            try {
                get();
                return null;
            } catch (Throwable t) {
                return t;
            }
        }
    }

    private static final class ResultSupplier<T> implements ErrorHandlingCheckedSupplier<T> {
        public final T value;

        public ResultSupplier(T value) {
            this.value = value;
        }

        @Override
        public T get() throws Throwable {
            return this.value;
        }

        @Override
        public boolean isInitialized() {
            return true;
        }

        @Override
        public boolean hasError() {
            return false;
        }

        @Override
        public Throwable getError() {
            return null;
        }
    }

    private static final class ErrorSupplier<T> implements ErrorHandlingCheckedSupplier<T> {
        public final Throwable error;

        public ErrorSupplier(Throwable error) {
            this.error = error;
        }

        @Override
        public T get() throws Throwable {
            throw this.error;
        }

        @Override
        public boolean isInitialized() {
            return true;
        }

        @Override
        public boolean hasError() {
            return true;
        }

        @Override
        public Throwable getError() {
            return this.error;
        }
    }

    private static interface ErrorHandlingCheckedSupplier<T> extends CheckedSupplier<T> {
        boolean isInitialized();
        boolean hasError();
        Throwable getError();
    }
}
