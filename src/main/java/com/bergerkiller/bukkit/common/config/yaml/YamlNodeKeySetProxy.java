package com.bergerkiller.bukkit.common.config.yaml;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import com.bergerkiller.bukkit.common.collections.CollectionBasics;

/**
 * Accesses a YamlNode's key names using a Set interface.
 * Used by {@link YamlNodeAbstract<?>#getKeys()}.
 *
 * @param <T> - Value Type of the keys
 */
class YamlNodeKeySetProxy<T> implements Set<T> {
    private final YamlNodeAbstract<?> _node;
    private final KeyConverter<T> _keyConv;

    public static YamlNodeKeySetProxy<String> stringKeysOf(YamlNodeAbstract<?> node) {
        return new YamlNodeKeySetProxy<>(node, KeyConverter.STRING_KEYS);
    }

    public static YamlNodeKeySetProxy<YamlPath> yamlPathKeysOf(YamlNodeAbstract<?> node) {
        return new YamlNodeKeySetProxy<>(node, KeyConverter.PATH_KEYS);
    }

    private YamlNodeKeySetProxy(YamlNodeAbstract<?> node, KeyConverter<T> keyConv) {
        _node = node;
        _keyConv = keyConv;
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
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int _index = 0;
            private boolean _canRemove = false;

            @Override
            public boolean hasNext() {
                return _index < _node._children.size();
            }

            @Override
            public T next() {
                if (hasNext()) {
                    _canRemove = true;
                    return _keyConv.toKey(_node, _node._children.get(_index++).getYamlPath());
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
        YamlPath path = _keyConv.toPath(_node, o);
        if (path == null) {
            return false;
        }

        int index = _node.indexOfYamlPath(path);
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
    public <E> E[] toArray(E[] a) {
        return CollectionBasics.toArray(this, a);
    }

    @Override
    public boolean add(T e) {
        throw new UnsupportedOperationException("Node keys cannot be added");
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException("Node keys cannot be added");
    }

    public interface KeyConverter<T> {
        KeyConverter<String> STRING_KEYS = new KeyConverter<String>() {
            @Override
            public YamlPath toPath(YamlNodeAbstract<?> node, Object key) {
                if (key instanceof String) {
                    return node.getYamlPath().childWithName((String) key);
                } else {
                    return null;
                }
            }

            @Override
            public String toKey(YamlNodeAbstract<?> node, YamlPath absolutePath) {
                // Only contains children 1 level deep. So we can just return the name.
                return absolutePath.name();
            }
        };
        KeyConverter<YamlPath> PATH_KEYS = new KeyConverter<YamlPath>() {
            @Override
            public YamlPath toPath(YamlNodeAbstract<?> node, Object key) {
                if (key instanceof YamlPath) {
                    YamlPath path = (YamlPath) key;
                    if (path.parent().equals(node.getYamlPath())) {
                        return path;
                    }
                }
                return null;
            }

            @Override
            public YamlPath toKey(YamlNodeAbstract<?> node, YamlPath absolutePath) {
                // Make the absolute path relative to the node path
                return absolutePath.makeRelative(node.getYamlPath());
            }
        };

        YamlPath toPath(YamlNodeAbstract<?> node, Object key);
        T toKey(YamlNodeAbstract<?> node, YamlPath absolutePath);
    }
}
