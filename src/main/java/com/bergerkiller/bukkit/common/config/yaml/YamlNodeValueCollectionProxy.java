package com.bergerkiller.bukkit.common.config.yaml;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.bergerkiller.bukkit.common.collections.CollectionBasics;

/**
 * Accesses a YamlNode's values using a Collection interface.
 * Used by {@link YamlNodeMapProxy#values()}.
 */
public class YamlNodeValueCollectionProxy implements Collection<Object>, YamlNodeLinkedValue {
    private final YamlNodeAbstract<?> _node;

    public YamlNodeValueCollectionProxy(YamlNodeAbstract<?> node) {
        _node = node;
    }

    @Override
    public void clear() {
        _node.clear();
    }

    @Override
    public int size() {
        return _node._children.size();
    }

    @Override
    public boolean isEmpty() {
        return _node._children.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return _node.indexOfValue(o) != -1;
    }

    @Override
    public Iterator<Object> iterator() {
        return YamlNodeMappedIterator.shallow(_node, YamlEntry::getValue);
    }

    @Override
    public Object[] toArray() {
        return CollectionBasics.toArray(this);
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return CollectionBasics.toArray(this, a);
    }

    @Override
    public boolean remove(Object o) {
        int index = _node.indexOfValue(o);
        if (index == -1) {
            return false;
        } else {
            _node.removeChildEntryAt(index);
            return true;
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return CollectionBasics.containsAll(this, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return CollectionBasics.removeAll(this, c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return CollectionBasics.retainAll(this, c);
    }

    @Override
    public boolean addAll(Collection<? extends Object> c) {
        throw new UnsupportedOperationException("Node values can not be added to");
    }

    @Override
    public boolean add(Object e) {
        throw new UnsupportedOperationException("Node values can not be added to");
    }

    @Override
    public void assignTo(YamlEntry entry) {
        entry.setValue(_node);
    }
}
