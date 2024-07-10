package com.bergerkiller.bukkit.common.config.yaml;

import com.bergerkiller.bukkit.common.collections.CollectionBasics;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Accesses a YamlNode's key names deeply (recursively) using a Set interface.
 * Used by {@link YamlNodeAbstract<?>#getDeepYamlKeys()}.
 */
class YamlNodeDeepKeySetProxy implements Set<YamlPath> {
    private final YamlNodeAbstract<?> _node;

    public static YamlNodeDeepKeySetProxy yamlPathKeysOf(YamlNodeAbstract<?> node) {
        return new YamlNodeDeepKeySetProxy(node);
    }

    private YamlNodeDeepKeySetProxy(YamlNodeAbstract<?> node) {
        _node = node;
    }

    @Override
    public void clear() {
        _node.clear();
    }

    @Override
    public int size() {
        Iterator<YamlPath> iter = this.iterator();
        int size = 0;
        while (iter.hasNext()) {
            iter.next();
            size++;
        }
        return size;
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
    public Iterator<YamlPath> iterator() {
        return YamlNodeMappedIterator.deep(_node, e -> e.getYamlPath().makeRelative(_node.getYamlPath()));
    }

    @Override
    public boolean remove(Object o) {
        if (o instanceof YamlPath) {
            YamlEntry entry = _node.getEntryIfExists((YamlPath) o);
            if (entry != null && entry.getParentNode() != null) {
                entry.getParentNode().removeChildEntry(entry);
                return true;
            }
        }

        return false;
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
    public <E> E[] toArray(E[] a) {
        return CollectionBasics.toArray(this, a);
    }

    @Override
    public boolean add(YamlPath e) {
        throw new UnsupportedOperationException("Node keys cannot be added");
    }

    @Override
    public boolean addAll(Collection<? extends YamlPath> c) {
        throw new UnsupportedOperationException("Node keys cannot be added");
    }
}
