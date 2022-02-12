package com.bergerkiller.bukkit.common.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Stores a fixed-size array of values. When new values are added when the capacity
 * is reached, previous (oldest) values are removed silently. The entire data
 * structure is multi-thread safe.<br>
 * <br>
 * This class is characterized by a very fast {@link #add(Object)} method,
 * and a slower {@link #get()} method to read a snapshot of the contents. As such,
 * this class should only be used when reading the contents happens sparingly.
 *
 * @param <E> Element type
 */
public final class OverwritingCircularBuffer<E> implements Iterable<E> {
    private static final Object EOF_SENTINEL = new Object();
    private final Object[] values;
    private final AtomicInteger index = new AtomicInteger();
    private final AtomicInteger written = new AtomicInteger();

    /**
     * Creates a new circular buffer with the given capacity
     *
     * @param <E>
     * @param capacity
     * @return new empty circular buffer
     */
    public static <E> OverwritingCircularBuffer<E> create(int capacity) {
        return new OverwritingCircularBuffer<E>(capacity);
    }

    private OverwritingCircularBuffer(int capacity) {
        this.values = new Object[capacity];
        Arrays.fill(this.values, EOF_SENTINEL);
    }

    /**
     * Gets all the values currently in the circular buffer, sorted from earliest
     * insertion to last insertion. This returns a snapshot of the contents at the
     * moment. The List is immutable and will not change when new values are inserted
     * into this buffer.
     *
     * @return List of values currently inside this circular buffer
     */
    @SuppressWarnings("unchecked")
    public List<E> values() {
        final int capacity = this.values.length;
        int endIndex;
        E[] copy;

        // Try to obtain a snapshot
        // The index comparisons are safe, no index wrapping can occur
        // because this block is synchronized.
        // Only once index == written can we be certain no push() is ongoing
        synchronized (this) {
            do {
                endIndex = Math.floorMod(this.index.get(), capacity);
                copy = (E[]) this.values.clone();
            } while (endIndex != Math.floorMod(this.index.get(), capacity) ||
                     endIndex != Math.floorMod(this.written.get(), capacity));
        }

        // If array is EOF at current position, buffer is empty
        if (copy[endIndex] == EOF_SENTINEL) {
            return Collections.emptyList();
        }

        // Find start index of the buffer by walking in reverse
        int startIndex = endIndex;
        do {
            startIndex = Math.floorMod(startIndex + 1, capacity);
        } while (copy[startIndex] == EOF_SENTINEL);

        // Single value in buffer
        if (startIndex == endIndex) {
            return Collections.singletonList(copy[endIndex]);
        }

        // Fill a new array list with values
        ArrayList<E> result = new ArrayList<E>(Math.floorMod(endIndex - startIndex + 1, capacity));
        int curr = startIndex - 1;
        do {
            curr = Math.floorMod(curr + 1, capacity);
            result.add(copy[curr]);
        } while (curr != endIndex);

        // Return all the contents
        return Collections.unmodifiableList(result);
    }

    /**
     * Gets the maximum amount of elements that could possibly be stored inside this
     * circular buffer.
     *
     * @return Maximum capacity
     */
    public int capacity() {
        return this.values.length;
    }

    /**
     * Clears this buffer so all previously pushed values are removed
     */
    public void clear() {
        Arrays.fill(this.values, EOF_SENTINEL);
    }

    /**
     * Adds a new value to this circular buffer. If capacity is reached, overwrites
     * the oldest value.
     *
     * @param value
     */
    public void add(E value) {
        Object[] values = this.values;
        int currIndex = index.incrementAndGet();
        boolean normalize = (currIndex >= values.length);

        if (normalize) {
            currIndex = Math.floorMod(currIndex, values.length);
        }

        values[currIndex] = value;
        written.incrementAndGet();

        if (normalize) {
            normalizeIndex();
        }
    }

    @Override
    public Iterator<E> iterator() {
        return values().iterator();
    }

    @Override
    public String toString() {
        return values().toString();
    }

    /**
     * Prevents the current index growing ever larger, and reduces the overhead of %.
     * This is also the only place we synchronize, because an index-wrap could spell
     * disaster for {@link #get()}
     */
    private synchronized void normalizeIndex() {
        int len = this.values.length;
        int currIndex;
        while ((currIndex = index.get()) >= len &&
               !index.compareAndSet(currIndex, Math.floorMod(currIndex, len)));
        while ((currIndex = written.get()) >= len &&
               !written.compareAndSet(currIndex, Math.floorMod(currIndex, len)));
    }
}
