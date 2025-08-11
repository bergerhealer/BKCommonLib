package com.bergerkiller.bukkit.common.config.yaml;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import com.bergerkiller.bukkit.common.collections.CollectionBasics;
import com.bergerkiller.bukkit.common.utils.LogicUtil;

import java.util.Set;

/**
 * Accesses a YamlNode's key and value pairs using a Set interface.
 * Used by {@link YamlNodeMapProxy#entrySet()}.
 */
public class YamlNodeEntrySetProxy implements Set<Map.Entry<String, Object>> {
    private final YamlNodeAbstract<?> _node;

    public YamlNodeEntrySetProxy(YamlNodeAbstract<?> node) {
        this._node = node;
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

    private int indexOfEntry(Object o) {
        if (o instanceof Map.Entry) {
            Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
            int index = _node.indexOfKey(e.getKey());
            if (index != -1 && LogicUtil.bothNullOrEqual(_node._children.get(index).getValue(), e.getValue())) {
                return index;
            }
        }
        return -1;
    }

    @Override
    public boolean contains(Object o) {
        return indexOfEntry(o) != -1;
    }

    @Override
    public Iterator<Map.Entry<String, Object>> iterator() {
        return new Iterator<Map.Entry<String, Object>>() {
            private int _index = 0;
            private boolean _canRemove = false;

            @Override
            public boolean hasNext() {
                return _index < _node._children.size();
            }

            @Override
            public Map.Entry<String, Object> next() {
                if (hasNext()) {
                    _canRemove = true;
                    return _node._children.get(_index++);
                } else {
                    throw new NoSuchElementException("No next element available");
                }
            }

            @Override
            public void remove() {
                if (_canRemove) {
                    _canRemove = false;
                    _node.removeChildEntryAtAndGetValue(--_index);
                } else {
                    throw new NoSuchElementException("Next must be called before remove()");
                }
            }
        };
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
        int index = indexOfEntry(o);
        if (index == -1) {
            return false;
        } else {
            _node.removeChildEntryAtAndGetValue(index);
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
    public boolean add(Map.Entry<String, Object> e) {
        throw new UnsupportedOperationException("Node entries cannot be added");
    }

    @Override
    public boolean addAll(Collection<? extends Map.Entry<String, Object>> c) {
        throw new UnsupportedOperationException("Node entries cannot be added");
    }
}
