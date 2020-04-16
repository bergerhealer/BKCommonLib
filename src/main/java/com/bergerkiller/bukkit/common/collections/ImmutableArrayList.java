package com.bergerkiller.bukkit.common.collections;

import java.util.AbstractList;
import java.util.Arrays;

/**
 * Immutable list backed by an Array
 * 
 * @param <E>
 */
public class ImmutableArrayList<E> extends AbstractList<E> {
    private final E[] array;

    public ImmutableArrayList(E[] array) {
        this.array = array;
    }

    @Override
    public E get(int index) {
        return this.array[index];
    }

    @Override
    public int size() {
        return this.array.length;
    }

    @Override
    public Object[] toArray() {
        return this.array.clone();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        int size = size();
        if (a.length < size)
            return Arrays.copyOf(this.array, size,
                                 (Class<? extends T[]>) a.getClass());
        System.arraycopy(this.array, 0, a, 0, size);
        if (a.length > size)
            a[size] = null;
        return a;
    }

    @Override
    public int indexOf(Object o) {
        E[] a = this.array;
        if (o == null) {
            for (int i = 0; i < a.length; i++)
                if (a[i] == null)
                    return i;
        } else {
            for (int i = 0; i < a.length; i++)
                if (o.equals(a[i]))
                    return i;
        }
        return -1;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }
}
