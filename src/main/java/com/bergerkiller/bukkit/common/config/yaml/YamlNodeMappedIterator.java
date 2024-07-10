package com.bergerkiller.bukkit.common.config.yaml;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

/**
 * Iterates the entries below a node, and remaps each node to some other value.
 * This is used for both the keys as the values iterator.
 *
 * @param <T> Value type
 */
class YamlNodeMappedIterator<T> implements Iterator<T> {
    private final YamlNodeAbstract<?> _node;
    private final Function<YamlEntry, T> _mapper;
    private int _index = 0;
    private boolean _nextCalled = false;

    /**
     * Creates a shallow iterator. Only iterates the direct children of a node.
     *
     * @param node Node whose child entries to iterate
     * @param mapper Mapper from entry to the result of next()
     * @return YamlNodeMappedIterator
     * @param <T> Next() result type
     */
    public static <T> YamlNodeMappedIterator<T> shallow(YamlNodeAbstract<?> node, Function<YamlEntry, T> mapper) {
        return new YamlNodeMappedIterator<>(node, mapper);
    }

    private YamlNodeMappedIterator(YamlNodeAbstract<?> node, Function<YamlEntry, T> mapper) {
        _node = node;
        _mapper = mapper;
    }

    @Override
    public boolean hasNext() {
        return _index < _node._children.size();
    }

    @Override
    public T next() {
        if (hasNext()) {
            _nextCalled = true;
            return _mapper.apply(_node._children.get(_index++));
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
    public Object setValue(Object value) {
        if (_nextCalled) {
            return _node._children.get(_index - 1).setValue(value);
        } else {
            throw new NoSuchElementException("Next must be called before set(value)");
        }
    }
}
