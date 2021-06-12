package com.bergerkiller.bukkit.common.internal.proxy;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.bergerkiller.generated.net.minecraft.util.EntitySliceHandle;

/**
 * This is a special proxy class for CraftBukkit so that a single EntitySlice
 * can be accessed as if it is a List (like on Spigot). It may summon dark spirits
 * while attempting this dangerous conversion.<br>
 * <br>
 * Only works pre-1.8
 * 
 * TODO!!!!!
 *
 * @param <E> type of element in the slice/list
 */
public class EntitySliceProxy_1_8<E> extends AbstractList<E> {
    private final EntitySliceHandle handle;
    private final List<E> listValues;

    public EntitySliceProxy_1_8(EntitySliceHandle handle) {
        this.handle = handle;
        this.listValues = new ArrayList<E>(handle.size());

        Iterator<E> iter = handleIter();
        while (iter.hasNext()) {
            this.listValues.add(iter.next());
        }
    }

    public EntitySliceHandle getHandle() {
        return this.handle;
    }

    @Override
    public boolean add(E e) {
        if (!this.handle.add(e)) return false;
        this.listValues.add(e);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (!this.handle.remove(o)) return false;
        this.listValues.remove(o);
        return true;
    }

    @Override
    public void add(int index, E element) {
        // Add the element then move its position in the list to the requested index
        if (!this.handle.add(element)) {
            throw new RuntimeException("Failed to insert new element to Entity Slice");
        }
        this.listValues.add(index, element);
    }

    /* ==== Standard implementations ==== */

    @Override
    public E set(int index, E element) {
        E old = this.remove(index);
        this.add(index, element);
        return old;
    }

    @Override
    public E remove(int index) {
        E result = this.get(index);
        this.remove(result);
        return result;
    }

    @Override
    public void clear() {
        while (!this.isEmpty()) {
            this.remove(this.get(0));
        }
    }

    /* ==== Simple read-only properties ==== */

    @Override
    public int size() {
        return this.handle.size();
    }

    @Override
    public boolean isEmpty() {
        return this.handle.size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        Iterator<E> iter = handleIter();
        while (iter.hasNext()) {
            if (iter.next().equals(o)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object[] toArray() {
        return this.listValues.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return this.listValues.toArray(a);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!this.contains(o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public E get(int index) {
        return this.listValues.get(index);
    }

    @Override
    public int indexOf(Object o) {
        return this.listValues.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.listValues.lastIndexOf(o);
    }

    @SuppressWarnings("unchecked")
    private Iterator<E> handleIter() {
        return this.handle.iterator();
    }
}
