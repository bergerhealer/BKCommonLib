package com.bergerkiller.bukkit.common.collections;

import java.util.AbstractList;
import java.util.Collection;
import java.util.List;

/**
 * Simple proxy class to turn any list into an abstract list.
 * Can be used to call methods such as subList and ListIterator
 * to use the default implementation. Only a select few
 * methods have to be implemented.
 */
public class AbstractListProxy<E> extends AbstractList<E> {
    private final List<E> source;

    protected AbstractListProxy(List<E> source) {
        this.source = source;
    }

    @Override
    public int size() {
        return this.source.size();
    }

    @Override
    public boolean isEmpty() {
        return this.source.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.source.contains(o);
    }

    @Override
    public Object[] toArray() {
        return this.source.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return this.source.toArray(a);
    }

    @Override
    public boolean add(E e) {
        return this.source.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return this.source.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.source.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return this.source.addAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return this.source.retainAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return this.source.removeAll(c);
    }

    @Override
    public void clear() {
        this.source.clear();
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return this.source.addAll(index, c);
    }

    @Override
    public E get(int index) {
        return this.source.get(index);
    }

    @Override
    public E set(int index, E element) {
        return this.source.set(index, element);
    }

    @Override
    public void add(int index, E element) {
        this.source.add(index, element);
    }

    @Override
    public E remove(int index) {
        return this.source.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return this.source.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.source.lastIndexOf(o);
    }

    public static <E> AbstractListProxy<E> create(List<E> source) {
        return new AbstractListProxy<E>(source);
    }
}
