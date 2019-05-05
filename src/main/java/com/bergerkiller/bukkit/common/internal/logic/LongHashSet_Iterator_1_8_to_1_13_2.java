package com.bergerkiller.bukkit.common.internal.logic;

import java.lang.reflect.Field;
import java.util.NoSuchElementException;

import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.LongHashSet;
import com.bergerkiller.mountiplex.MountiplexUtil;

/**
 * Class pretty much cloned from CraftBukkit's util/LongHashSet class. All
 * credits go to them (or whoever wrote it) Changes:<br>
 * - removed modified check (would slow down too much)<br>
 * - changed Long to long
 */
public class LongHashSet_Iterator_1_8_to_1_13_2 extends LongHashSet.LongIterator {
    // Copied from craftbukkit LongHashSet
    private final static long FREE = 0;
    private final static long REMOVED = Long.MIN_VALUE;
    private static final Field valuesField;
    private static final Field elementCountField;

    static {
        Class<?> longHashSetType = CommonUtil.getCBClass("util.LongHashSet");
        Field theValuesField = null;
        Field theElementCountField = null;
        if (longHashSetType != null) {
            try {
                theValuesField = longHashSetType.getDeclaredField("values");
                theElementCountField = longHashSetType.getDeclaredField("elements");
                theValuesField.setAccessible(true);
                theElementCountField.setAccessible(true);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        valuesField = theValuesField;
        elementCountField = theElementCountField;
    }

    private int index;
    private int lastReturned = -1;
    private final Object handle;
    private final long[] values;

    public LongHashSet_Iterator_1_8_to_1_13_2(Object handle) {
        this.handle = handle;
        try {
            this.values = (long[]) valuesField.get(handle);
        } catch (Throwable t) {
            throw MountiplexUtil.uncheckedRethrow(t);
        }
        for (index = 0; index < values.length && (values[index] == FREE || values[index] == REMOVED); index++) {
            // This is just to drive the index forward to the first valid entry
        }
    }

    @Override
    public boolean hasNext() {
        return index != values.length;
    }

    @Override
    public long next() {
        int length = values.length;
        if (index >= length) {
            lastReturned = -2;
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
        if (lastReturned == -1 || lastReturned == -2) {
            throw new IllegalStateException();
        }

        if (values[lastReturned] != FREE && values[lastReturned] != REMOVED) {
            values[lastReturned] = REMOVED;
            try {
                elementCountField.setInt(handle, elementCountField.getInt(handle) - 1);
            } catch (Throwable t) {
                throw MountiplexUtil.uncheckedRethrow(t);
            }
        }
    }
}
