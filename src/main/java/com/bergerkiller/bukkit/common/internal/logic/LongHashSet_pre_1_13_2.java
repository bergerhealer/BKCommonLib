/*
  Based on CompactHashSet Copyright 2011 Ontopia Project

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/

package com.bergerkiller.bukkit.common.internal.logic;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * LongHashSet class implementation from CraftBukkit, with some fixes and API
 * improvements. Most important fix is that it can also store the value 0 and Long.MIN_VALUE,
 * which is otherwise not possible to be used.
 */
public class LongHashSet_pre_1_13_2 {
    private final static int INITIAL_SIZE = 3;
    private final static double LOAD_FACTOR = 0.75;

    private final static long FREE = 0;
    private final static long REMOVED = Long.MIN_VALUE;

    private int freeEntries;
    private int elements;
    private long[] values;
    private int modCount;
    private boolean has_free_value;
    private boolean has_removed_value;

    public LongHashSet_pre_1_13_2() {
        this(INITIAL_SIZE);
    }

    public LongHashSet_pre_1_13_2(int size) {
        values = new long[(size==0 ? 1 : size)];
        elements = 0;
        freeEntries = values.length;
        modCount = 0;
        has_free_value = false;
        has_removed_value = false;
    }

    public LongIterator iterator() {
        return new LongIterator();
    }

    public int size() {
        return elements + (has_free_value ? 1 : 0) + (has_removed_value ? 1 : 0);
    }

    public boolean isEmpty() {
        return elements == 0 && !has_free_value && !has_removed_value;
    }

    public boolean contains(long value) {
        if (value == FREE) return has_free_value;
        if (value == REMOVED) return has_removed_value;

        int hash = hash(value);
        int index = (hash & 0x7FFFFFFF) % values.length;
        int offset = 1;

        // search for the object (continue while !null and !this object)
        while(values[index] != FREE && !(hash(values[index]) == hash && values[index] == value)) {
            index = ((index + offset) & 0x7FFFFFFF) % values.length;
            offset = offset * 2 + 1;

            if (offset == -1) {
                offset = 2;
            }
        }

        return values[index] != FREE;
    }

    public boolean add(long value) {
        if (value == FREE) {
            if (has_free_value) {
                return false;
            }
            has_free_value = true;
            return true;
        }
        if (value == REMOVED) {
            if (has_removed_value) {
                return false;
            }
            has_removed_value = true;
            return true;
        }

        int hash = hash(value);
        int index = (hash & 0x7FFFFFFF) % values.length;
        int offset = 1;
        int deletedix = -1;

        // search for the object (continue while !null and !this object)
        while(values[index] != FREE && !(hash(values[index]) == hash && values[index] == value)) {
            // if there's a deleted object here we can put this object here,
            // provided it's not in here somewhere else already
            if (values[index] == REMOVED) {
                deletedix = index;
            }

            index = ((index + offset) & 0x7FFFFFFF) % values.length;
            offset = offset * 2 + 1;

            if (offset == -1) {
                offset = 2;
            }
        }

        if (values[index] == FREE) {
            if (deletedix != -1) { // reusing a deleted cell
                index = deletedix;
            } else {
                freeEntries--;
            }

            modCount++;
            elements++;
            values[index] = value;

            if (1 - (freeEntries / (double) values.length) > LOAD_FACTOR) {
                rehash();
            }

            return true;
        } else {
            return false;
        }
    }

    public boolean remove(long value) {
        if (value == FREE) {
            if (!has_free_value) {
                return false;
            }
            has_free_value = false;
            return true;
        }
        if (value == REMOVED) {
            if (!has_removed_value) {
                return false;
            }
            has_removed_value = false;
            return true;
        }

        int hash = hash(value);
        int index = (hash & 0x7FFFFFFF) % values.length;
        int offset = 1;

        // search for the object (continue while !null and !this object)
        while(values[index] != FREE && !(hash(values[index]) == hash && values[index] == value)) {
            index = ((index + offset) & 0x7FFFFFFF) % values.length;
            offset = offset * 2 + 1;

            if (offset == -1) {
                offset = 2;
            }
        }

        if (values[index] != FREE) {
            values[index] = REMOVED;
            modCount++;
            elements--;
            return true;
        } else {
            return false;
        }
    }

    public void clear() {
        elements = 0;
        has_free_value = false;
        has_removed_value = false;
        for (int ix = 0; ix < values.length; ix++) {
            values[ix] = FREE;
        }

        freeEntries = values.length;
        modCount++;
    }

    public long[] toArray() {
        long[] result = new long[this.size()];
        long[] values = Arrays.copyOf(this.values, this.values.length);
        int pos = 0;

        if (has_free_value) result[pos++] = FREE;
        if (has_removed_value) result[pos++] = REMOVED;

        for (long value : values) {
            if (value != FREE && value != REMOVED) {
                result[pos++] = value;
            }
        }

        return result;
    }

    public long popFirst() {
        if (has_free_value) {
            has_free_value = false;
            return FREE;
        }
        if (has_removed_value) {
            has_removed_value = false;
            return REMOVED;
        }

        for (long value : values) {
            if (value != FREE && value != REMOVED) {
                remove(value);
                return value;
            }
        }

        throw new java.util.NoSuchElementException();
    }

    public long[] popAll() {
        long[] ret = toArray();
        clear();
        return ret;
    }

    // This method copied from Murmur3, written by Austin Appleby released under Public Domain
    private int hash(long value) {
        value ^= value >>> 33;
        value *= 0xff51afd7ed558ccdL;
        value ^= value >>> 33;
        value *= 0xc4ceb9fe1a85ec53L;
        value ^= value >>> 33;
        return (int) value;
    }

    private void rehash() {
        int gargagecells = values.length - (elements + freeEntries);
        if (gargagecells / (double) values.length > 0.05) {
            rehash(values.length);
        } else {
            rehash(values.length * 2 + 1);
        }
    }

    private void rehash(int newCapacity) {
        long[] newValues = new long[newCapacity];

        for (long value : values) {
            if (value == FREE || value == REMOVED) {
                continue;
            }

            int hash = hash(value);
            int index = (hash & 0x7FFFFFFF) % newCapacity;
            int offset = 1;

            // search for the object
            while (newValues[index] != FREE) {
                index = ((index + offset) & 0x7FFFFFFF) % newCapacity;
                offset = offset * 2 + 1;

                if (offset == -1) {
                    offset = 2;
                }
            }

            newValues[index] = value;
        }

        values = newValues;
        freeEntries = values.length - elements;
    }

    public final class LongIterator implements Iterator<Long> {
        private static final int LAST_RETURNED_INITIAL = -1;
        private static final int LAST_RETURNED_END = -2;
        private static final int LAST_RETURNED_FREE = -3;
        private static final int LAST_RETURNED_REMOVED = -4;
        private int index;
        private int lastReturned = LAST_RETURNED_INITIAL;
        private int expectedModCount;

        public LongIterator() {
            for (index = 0; index < values.length && (values[index] == FREE || values[index] == REMOVED); index++) {
                // This is just to drive the index forward to the first valid entry
            }
            expectedModCount = modCount;
        }

        @Override
        public boolean hasNext() {
            if (index != values.length) {
                return true;
            }

            // Special case for REMOVED and FREE values
            if (has_free_value && lastReturned >= 0) {
                return true;
            }
            if (has_removed_value && (lastReturned >= 0 || lastReturned == LAST_RETURNED_FREE)) {
                return true;
            }

            return false;
        }

        @Override
        public Long next() {
            return Long.valueOf(nextLong());
        }

        public long nextLong() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }

            int length = values.length;
            if (index >= length) {
                if (has_free_value && lastReturned >= 0) {
                    lastReturned = LAST_RETURNED_FREE;
                    return FREE;
                }
                if (has_removed_value && (lastReturned >= 0 || lastReturned == LAST_RETURNED_FREE)) {
                    lastReturned = LAST_RETURNED_REMOVED;
                    return REMOVED;
                }

                lastReturned = LAST_RETURNED_END;
                throw new NoSuchElementException();
            }

            lastReturned = index;
            for (index += 1; index < length && (values[index] == FREE || values[index] == REMOVED); index++) {
                // This is just to drive the index forward to the next valid entry
            }

            if (values[lastReturned] == FREE) {
                return FREE;
            } else {
                return values[lastReturned];
            }
        }

        @Override
        public void remove() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            } else if (lastReturned == LAST_RETURNED_FREE) {
                has_free_value = false;
            } else if (lastReturned == LAST_RETURNED_REMOVED) {
                has_removed_value = false;
            } else if (lastReturned == LAST_RETURNED_INITIAL || lastReturned == LAST_RETURNED_END) {
                throw new IllegalStateException();
            } else if (values[lastReturned] != FREE && values[lastReturned] != REMOVED) {
                values[lastReturned] = REMOVED;
                elements--;
                modCount++;
                expectedModCount = modCount;
            }
        }
    }
}
