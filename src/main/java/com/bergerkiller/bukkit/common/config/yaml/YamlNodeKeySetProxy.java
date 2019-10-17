package com.bergerkiller.bukkit.common.config.yaml;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import com.bergerkiller.bukkit.common.collections.CollectionBasics;

/**
 * Accesses a YamlNode's key names using a Set interface.
 * Used by {@link YamlNodeAbstract<?>#getKeys()}.
 */
public class YamlNodeKeySetProxy implements Set<String> {
    private final YamlNodeAbstract<?> _node;

    public YamlNodeKeySetProxy(YamlNodeAbstract<?> node) {
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
        return _node.indexOfKey(o) != -1;
    }

    @Override
    public Iterator<String> iterator() {
        return new Iterator<String>() {
            private int _index = 0;
            private boolean _canRemove = false;

            @Override
            public boolean hasNext() {
                return _index < _node._children.size();
            }

            @Override
            public String next() {
                if (hasNext()) {
                    _canRemove = true;
                    return _node._children.get(_index++).getKey();
                } else {
                    throw new NoSuchElementException("No next element available");
                }
            }

            @Override
            public void remove() {
                if (_canRemove) {
                    _canRemove = false;
                    _node.removeChildEntryAt(--_index);
                } else {
                    throw new NoSuchElementException("Next must be called before remove()");
                }
            }
        };
    }

    @Override
    public boolean remove(Object o) {
        int index = _node.indexOfKey(o);
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
    public boolean retainAll(Collection<?> c) {
        return CollectionBasics.retainAll(this, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return CollectionBasics.removeAll(this, c);
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
    public boolean add(String e) {
        throw new UnsupportedOperationException("Node keys cannot be added");
    }
    
    @Override
    public boolean addAll(Collection<? extends String> c) {
        throw new UnsupportedOperationException("Node keys cannot be added");
    }
}
