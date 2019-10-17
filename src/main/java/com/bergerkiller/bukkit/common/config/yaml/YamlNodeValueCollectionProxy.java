package com.bergerkiller.bukkit.common.config.yaml;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.bergerkiller.bukkit.common.collections.CollectionBasics;

/**
 * Accesses a YamlNode's values using a Collection interface.
 * Used by {@link YamlNodeMapProxy#values()}.
 */
public class YamlNodeValueCollectionProxy implements Collection<Object> {
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
        return new ValueIterator(_node);
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

    public static class ValueIterator implements Iterator<Object> {
        private final YamlNodeAbstract<?> _node;
        private int _index = 0;
        private boolean _nextCalled = false;

        public ValueIterator(YamlNodeAbstract<?> node) {
            _node = node;
        }

        @Override
        public boolean hasNext() {
            return _index < _node._children.size();
        }

        @Override
        public Object next() {
            if (hasNext()) {
                _nextCalled = true;
                return _node._children.get(_index++).getValue();
            } else {
                throw new NoSuchElementException("No next element available");
            }
        }

        @Override
        public void remove() {
            if (_nextCalled) {
                _nextCalled = false;
                _node.removeChildEntryAt(--_index);
            } else {
                throw new NoSuchElementException("Next must be called before remove()");
            }
        }

        /**
         * Changes the value of the entry last returned the value of using {@link #next()}
         * 
         * @param value
         * @return previous value
         */
        public Object set(Object value) {
            if (_nextCalled) {
                _nextCalled = false;
                return _node._children.get(_index - 1).setValue(value);
            } else {
                throw new NoSuchElementException("Next must be called before set(value)");
            }
        }
    }
}
