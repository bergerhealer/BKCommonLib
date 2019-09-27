package com.bergerkiller.bukkit.common.collections;

import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Collection implementation where the index to values is not predictable,
 * but can be used to get and set stored values. Removing or adding values will not shift
 * the data in the array or change the index to other values.<br>
 * <br>
 * Removing entries will instead make that slot available for re-use using a linked list
 * of free slots. Adding entries is done by filling such free slots up.<br>
 * <br>
 * The somewhat immutable nature of the collection means that it can be iterated while
 * adding or removing values. It is then not predictable whether or not newly inserted
 * values will be iterated or not.<br>
 * <br>
 * Null values can be stored inside this collection.
 */
public class IndexedCollection<T> extends AbstractCollection<T> {
    private Object[] _values;
    private int _freeIndex;
    private int _size;

    public IndexedCollection() {
        this.clear();
    }

    @Override
    public int size() {
        return this._size;
    }

    @Override
    public void clear() {
        this._values = new Object[] { new FreeValue(-1) };
        this._freeIndex = 0;
        this._size = 0;
    }

    /**
     * Sets the value stored at the index
     * 
     * @param index to set
     * @param value to set
     */
    public void setAt(int index, T value) {
        this._values[index] = value;
    }

    /**
     * Gets the value stored at the index
     * 
     * @param index to get
     * @return value stored at index
     */
    @SuppressWarnings("unchecked")
    public T getAt(int index) {
        return (T) this._values[index];
    }

    /**
     * Removes the value stored at the index, making that index slot
     * available for future new values.
     * 
     * @param index to remove
     */
    public void removeAt(int index) {
        FreeValue free = new FreeValue(this._freeIndex);
        this._freeIndex = index;
        this._values[index] = free;
        this._size--;
    }

    /**
     * Adds a new value to this collection and returns the index to its slot
     * 
     * @param value to add
     * @return index where the value is stored
     */
    public int addAndGetIndex(T value) {
        int valueIndex = this._freeIndex;
        this._freeIndex = ((FreeValue) this._values[this._freeIndex]).next;
        this._values[valueIndex] = value;
        this._size++;

        if (this._freeIndex < 0) {
            // We need more free entries! Resize the array to make space for them.
            int index = this._values.length;
            this._values = Arrays.copyOf(this._values, index << 1);

            // Fill free slots with values
            this._freeIndex = index;
            while (index < (this._values.length-1)) {
                this._values[index] = new FreeValue(index+1);
                index++;
            }
            this._values[index] = new FreeValue(-1);
        }

        return valueIndex;
    }

    /**
     * Gets the (first) index of a value stored inside this collection
     * 
     * @param value to find
     * @return index to the value, -1 if not found
     */
    public int indexOf(Object value) {
        if (value == null) {
            for (int i = 0; i < this._values.length; i++) {
                if (this._values[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < this._values.length; i++) {
                Object stored = this._values[i];
                if (!(stored instanceof FreeValue) && value.equals(stored)) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public boolean contains(Object value) {
        return indexOf(value) != -1;
    }

    @Override
    public boolean remove(Object value) {
        int index = indexOf(value);
        if (index != -1) {
            this.removeAt(index);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean add(T value) {
        this.addAndGetIndex(value);
        return true;
    }

    @Override
    public Iterator<T> iterator() {
        return new IndexedCollectionIterator<T>(this);
    }

    private static final class IndexedCollectionIterator<T> implements Iterator<T> {
        private static final int INDEX_FLAG_UNSET = -1;
        private static final int INDEX_FLAG_FINISHED = -2;
        private final IndexedCollection<T> collection;
        private int index;
        private int nextIndex;

        public IndexedCollectionIterator(IndexedCollection<T> collection) {
            this.collection = collection;
            this.index = INDEX_FLAG_UNSET;
            this.nextIndex = INDEX_FLAG_UNSET;
        }

        @Override
        public boolean hasNext() {
            if (this.nextIndex == INDEX_FLAG_UNSET) {
                this.findNext();
            }
            return this.nextIndex != INDEX_FLAG_FINISHED;
        }

        @Override
        @SuppressWarnings("unchecked")
        public T next() {
            if (this.nextIndex == INDEX_FLAG_UNSET) {
                this.findNext();
            }
            if (this.nextIndex == INDEX_FLAG_FINISHED) {
                throw new NoSuchElementException("No more elements");
            }
            this.index = this.nextIndex;
            this.nextIndex = INDEX_FLAG_UNSET;
            return (T) this.collection._values[this.index];
        }

        @Override
        public void remove() {
            if (this.index == INDEX_FLAG_UNSET) {
                throw new NoSuchElementException("No element was previously iterated using next()");
            }
            this.collection.removeAt(this.index);
            if (this.nextIndex == INDEX_FLAG_UNSET) {
                this.findNext();
            }
            this.index = INDEX_FLAG_UNSET;
        }

        private void findNext() {
            Object[] values = this.collection._values;
            this.nextIndex = this.index;
            while (++this.nextIndex < values.length) {
                if (!(values[this.nextIndex] instanceof FreeValue)) {
                    return;
                }
            }
            this.nextIndex = INDEX_FLAG_FINISHED;
        }
    }

    private static final class FreeValue {
        public final int next;

        public FreeValue(int next) {
            this.next = next;
        }
    }
}
