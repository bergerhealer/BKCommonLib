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
interface YamlNodeMappedIterator<T> extends Iterator<T> {

    /**
     * Creates a shallow iterator. Only iterates the direct children of a node.
     *
     * @param node Node whose child entries to iterate
     * @param mapper Mapper from entry to the result of next()
     * @return YamlNodeMappedIterator
     * @param <T> Next() result type
     */
    static <T> YamlNodeMappedIterator<T> shallow(YamlNodeAbstract<?> node, Function<YamlEntry, T> mapper) {
        return new ShallowMappedIterator<>(node, mapper);
    }

    /**
     * Creates a deep iterator. Iterates all children and sub-children of a node.
     *
     * @param node Node whose child entries to iterate
     * @param mapper Mapper from entry to the result of next()
     * @return YamlNodeMappedIterator
     * @param <T> Next() result type
     */
    static <T> YamlNodeMappedIterator<T> deep(YamlNodeAbstract<?> node, Function<YamlEntry, T> mapper) {
        return new DeepMappedIterator<>(node, mapper);
    }

    @Override
    boolean hasNext();

    @Override
    T next();

    @Override
    void remove();

    /**
     * Changes the value of the entry last returned the value of using {@link #next()}
     *
     * @param value
     * @return previous value
     */
    Object setValue(Object value);

    class DeepMappedIterator<T> implements YamlNodeMappedIterator<T> {
        private ChainedShallowEntryIterator _iter;
        private ChainedShallowEntryIterator _next;
        private final Function<YamlEntry, T> _mapper;

        public DeepMappedIterator(YamlNodeAbstract<?> node, Function<YamlEntry, T> mapper) {
            _next = _iter = new ChainedShallowEntryIterator(node, null);
            _mapper = mapper;
        }

        @Override
        public boolean hasNext() {
            return _next.chainedHasNext();
        }

        @Override
        public T next() {
            _iter = _next;
            if (_iter.hasNext()) {
                return handleNext();
            }

            // Consume iterators until we have a next value
            // This resumes iteration with the parent of the child leaf
            while (_iter._after != null) {
                _next = _iter = _iter._after;
                if (_iter.hasNext()) {
                    return handleNext();
                }
            }

            throw new NoSuchElementException("No next element available");
        }

        /**
         * Returns the next value. If this value is of a node, then resumes iteration
         * with this node's children for upcoming iterations. This is done by setting
         * the _next field, which is picked up next iteration with next()
         *
         * @return Value
         */
        private T handleNext() {
            YamlEntry e = _iter.unsafeNext();
            if (e.isAbstractNode()) {
                _next = new ChainedShallowEntryIterator(e.getAbstractNode(), _iter);
            }
            return _mapper.apply(e);
        }

        @Override
        public void remove() {
            _iter.remove();
        }

        @Override
        public Object setValue(Object value) {
            return _iter.setValue(value);
        }
    }

    /**
     * Shallow iterator, with also a reference to the iterator to continue with
     * once reaching the end.
     */
    class ChainedShallowEntryIterator extends ShallowEntryIterator {
        private final ChainedShallowEntryIterator _after;

        public ChainedShallowEntryIterator(YamlNodeAbstract<?> node, ChainedShallowEntryIterator after) {
            super(node);
            _after = after;
        }

        public boolean chainedHasNext() {
            return hasNext() || (_after != null && _after.chainedHasNext());
        }
    }

    /**
     * For shallow iteration, with mapped results
     *
     * @param <T> Mapped type
     */
    class ShallowMappedIterator<T> implements YamlNodeMappedIterator<T> {
        private final ShallowEntryIterator _iter;
        private final Function<YamlEntry, T> _mapper;

        public ShallowMappedIterator(YamlNodeAbstract<?> node, Function<YamlEntry, T> mapper) {
            _iter = new ShallowEntryIterator(node);
            _mapper = mapper;
        }

        @Override
        public boolean hasNext() {
            return _iter.hasNext();
        }

        @Override
        public T next() {
            return _mapper.apply(_iter.next());
        }

        @Override
        public void remove() {
            _iter.remove();
        }

        @Override
        public Object setValue(Object value) {
            return _iter.setValue(value);
        }
    }

    /**
     * For shallow iteration of entries
     */
    class ShallowEntryIterator implements YamlNodeMappedIterator<YamlEntry> {
        private final YamlNodeAbstract<?> _node;
        private int _index = 0;
        private boolean _nextCalled = false;

        public ShallowEntryIterator(YamlNodeAbstract<?> node) {
            _node = node;
        }

        @Override
        public boolean hasNext() {
            return _index < _node._children.size();
        }

        protected YamlEntry unsafeNext() {
            _nextCalled = true;
            return _node._children.get(_index++);
        }

        @Override
        public YamlEntry next() {
            if (hasNext()) {
                return unsafeNext();
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

        @Override
        public Object setValue(Object value) {
            if (_nextCalled) {
                return _node._children.get(_index - 1).setValue(value);
            } else {
                throw new NoSuchElementException("Next must be called before set(value)");
            }
        }
    }
}
