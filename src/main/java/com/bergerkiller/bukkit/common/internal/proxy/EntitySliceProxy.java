package com.bergerkiller.bukkit.common.internal.proxy;

import java.util.AbstractList;
import java.util.Collection;
import java.util.List;

import com.bergerkiller.generated.net.minecraft.server.EntitySliceHandle;

/**
 * This is a special proxy class for CraftBukkit so that a single EntitySlice
 * can be accessed as if it is a List (like on Spigot). It may summon dark spirits
 * while attempting this dangerous conversion.
 *
 * @param <E> type of element in the slice/list
 */
public class EntitySliceProxy<E> extends AbstractList<E> {
    private final EntitySliceHandle handle;
    private final List<E> listValues;

    @SuppressWarnings("unchecked")
    public EntitySliceProxy(EntitySliceHandle handle) {
        this.handle = handle;
        this.listValues = (List<E>) handle.getListValues();
    }

    public EntitySliceHandle getHandle() {
        return this.handle;
    }

    @Override
    public boolean add(E e) {
        return this.handle.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return this.handle.remove(o);
    }

    @Override
    public void add(int index, E element) {
        // Add the element then move its position in the list to the requested index
        if (!this.handle.add(element)) {
            throw new RuntimeException("Failed to insert new element to Entity Slice");
        }
        int oldIndex = this.listValues.lastIndexOf(element);
        if (oldIndex == -1) {
            throw new RuntimeException("Attempted to insert element to Entity Slice but it is now gone");
        }
        if (oldIndex != index) {
            this.listValues.remove(oldIndex);
            this.listValues.add(index, element);
        }
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
        return this.listValues.size();
    }

    @Override
    public boolean isEmpty() {
        return this.listValues.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.listValues.contains(o);
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
        return this.listValues.containsAll(c);
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
}
