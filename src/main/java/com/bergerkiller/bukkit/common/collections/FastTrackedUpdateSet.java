package com.bergerkiller.bukkit.common.collections;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

/**
 * Stores a simple List of values to be updated, but uses a small
 * object to store the state of tracking so no set operations
 * have to be performed when adding and removing. In addition, the
 * tracked object itself can be used to add/remove it to the set
 * it was parented to.<br>
 * <br>
 * This makes this class useful for tracking values to be updated
 * with as little overhead as possible.<br>
 * <br>
 * It is safe to add/remove trackers while the set is being iterated.
 *
 * @param <E> Element type being stored
 */
public final class FastTrackedUpdateSet<E> {
    private final List<Tracker<E>> trackers = new ArrayList<Tracker<E>>();
    private final Iterable<E> iterable;
    private boolean enabled;

    public FastTrackedUpdateSet() {
        final TrackerIterator iter = new TrackerIterator();
        this.iterable = () -> iter;
        this.enabled = true;
    }

    /**
     * Gets whether this update set is enabled
     *
     * @return True if enabled
     */
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Sets whether this update set is enabled. If it is not enabled, then
     * trackers are never added when they change state. This avoids a memory
     * leak if the set is never read out.<br>
     * <br>
     * All previously created Tracker objects become invalid when this is called,
     * and should not be used again.
     *
     * @param enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            clear();
        }
    }

    /**
     * Creates a new Tracker for this set. It is not be marked for updating.
     *
     * @param value Value to add to the set when setting the tracker
     * @return new Tracker
     */
    public Tracker<E> track(E value) {
        return new Tracker<E>(this, value, this.enabled);
    }

    /**
     * Returns an Iterable that iterates over all values positively added to this
     * set, and when done iterating, clears it.<br>
     * <br>
     * Note that this always returns the same iterable and the same iterable
     * always returns the same iterator. This is because, by spec, the list
     * elements are removed while iterating over it. This means that, if you
     * create an iterator and don't fully iterate the list, then a future iterator
     * will resume where the previous left off.<br>
     * <br>
     * As such it is safe to break iteration and resume later, without missing
     * updates.
     *
     * @return Iterable for all tracked values set for updating
     */
    public Iterable<E> iterateAndClear() {
        return this.iterable;
    }

    /**
     * Iterates all values positively added tothis set, calling the supplier
     * consumer with them, and when done iterating, clears it.
     *
     * @param action
     * @see {@link #iterateAndClear()}
     */
    public void forEachAndClear(Consumer<? super E> action) {
        this.iterable.forEach(action);
    }

    /**
     * Clears all tracked values so no more updates are set
     */
    public void clear() {
        trackers.forEach(t -> {
            t.addedToList = false;
            t.isSet = false;
        });
        trackers.clear();
    }

    /**
     * Tracks a single value, and offers methods for adding/removing
     * the tracker from the owning set.
     */
    public static final class Tracker<E> {
        private final FastTrackedUpdateSet<E> owner;
        private final E value;
        private boolean tracked;
        private boolean addedToList;
        private boolean isSet;

        private Tracker(FastTrackedUpdateSet<E> owner, E value, boolean tracked) {
            this.owner = owner;
            this.value = value;
            this.addedToList = false;
            this.isSet = false;
            this.tracked = tracked;
        }

        /**
         * Gets the value represented by this Tracker object, and is
         * the value the owner set will know of.
         *
         * @return tracked value
         */
        public E getValue() {
            return value;
        }

        /**
         * Gets whether this tracker is currently set for updating
         *
         * @return True if set
         */
        public boolean isSet() {
            return this.isSet;
        }

        /**
         * Adds or removes this Tracker from being updated
         *
         * @param added Whether to add this tracker object
         */
        public void set(boolean added) {
            if (!this.tracked) {
                return;
            }
            this.isSet = added;
            if (added && !this.addedToList) {
                this.addedToList = true;
                this.owner.trackers.add(this);
            }
        }

        /**
         * Untracks this Tracker and sets it to false. Future set
         * calls will not do anything anymore and leave state on false.
         * This effectively ensures this tracker will never again be added
         * to the update set for iteration.
         */
        public void untrack() {
            this.tracked = false;
            this.isSet = false;
        }
    }

    private final class TrackerIterator implements Iterator<E> {
        private FallbackIterator<Tracker<E>> iter;
        private Tracker<E> current;

        private boolean startIterating() {
            if (iter == null) {
                if (trackers.isEmpty()) {
                    return false;
                }
                iter = new FallbackIterator<Tracker<E>>(trackers);
                return prepNextValue();
            }
            return true;
        }

        private boolean prepNextValue() {
            while (iter.hasNext()) {
                Tracker<E> t = iter.next();
                t.addedToList = false;
                if (t.isSet) {
                    t.isSet = false;
                    current = t;
                    return true;
                }
            }
            iter = null;
            current = null;
            trackers.clear();
            return false;
        }

        @Override
        public boolean hasNext() {
            return startIterating();
        }

        @Override
        public E next() {
            if (!startIterating()) {
                throw new NoSuchElementException();
            }

            try {
                return current.value;
            } finally {
                prepNextValue();
            }
        }
    }

    /**
     * A very simple iterator that wraps the base iterator, and once a concurrent
     * modification exception occurs, switches to a fallback mode of just
     * incrementing an index. This makes this safe for lists that potentially get elements
     * added at the end while iterating.
     *
     * @param <E>
     */
    private static final class FallbackIterator<E> implements Iterator<E> {
        private final List<E> list;
        private Iterator<E> iter;
        private int index;

        public FallbackIterator(List<E> list) {
            this.list = list;
            this.iter = list.iterator();
            this.index = 0;
        }

        @Override
        public boolean hasNext() {
            try {
                return this.iter.hasNext();
            } catch (ConcurrentModificationException ex) {
                this.iter = new ByIndexIterator();
                return this.iter.hasNext();
            }
        }

        @Override
        public E next() {
            try {
                return this.iter.next();
            } catch (ConcurrentModificationException ex) {
                this.iter = new ByIndexIterator();
                return this.iter.next();
            }
        }

        private final class ByIndexIterator implements Iterator<E> {
            @Override
            public boolean hasNext() {
                return index < list.size();
            }

            @Override
            public E next() {
                try {
                    return list.get(index++);
                } catch (IndexOutOfBoundsException ex) {
                    --index;
                    throw new NoSuchElementException();
                }
            }
        }
    }
}
