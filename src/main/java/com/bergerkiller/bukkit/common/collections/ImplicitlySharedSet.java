package com.bergerkiller.bukkit.common.collections;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * A set which can be copied without copying the data, performing an actual copy
 * only when the internal data is modified. Copies can be iterated without risking
 * concurrent modification exceptions when the original is modified.<br>
 * <br>
 * When you are done working with a copy of the data, it is recommended to call
 * {@link #close()} on the copy in order for the reference to the data to be removed.
 * This reduces unneeded copying in derivative instances that reference the same data.<br>
 * <br>
 * To iterate all the elements of the set without risking concurrent modification exceptions,
 * the {@link #cloneAsIterable()} can be used. All elements should be iterated for best performance.
 * If the iteration result in a <i>break</i>, then try-with-resources is a better alternative.
 */
public class ImplicitlySharedSet<E> extends ImplicitlySharedHolder<Set<E>> implements Set<E> {

    /**
     * Creates a new implicitly shared set, backed by a HashSet
     */
    public ImplicitlySharedSet() {
        super(new HashSet<E>());
    }

    /**
     * Creates a new implicitly shared set, specifying what underlying set implementation is used.
     * 
     * @param set to use internally
     */
    public ImplicitlySharedSet(Set<E> set) {
        super(set);
    }

    /**
     * Creates a new implicitly shared set by sharing contents from another set.
     * The moment this shared set is about to be modified, a detached copy is created.
     * 
     * @param sharedSet to access for reading contents
     */
    public ImplicitlySharedSet(ImplicitlySharedSet<E> sharedSet) {
        super(sharedSet.ref);
    }

    @Override
    public int size() {
        try (Reference<Set<E>> ref = read()) {
            return ref.val.size();
        }
    }

    @Override
    public boolean isEmpty() {
        try (Reference<Set<E>> ref = read()) {
            return ref.val.isEmpty();
        }
    }

    @Override
    public boolean contains(Object o) {
        try (Reference<Set<E>> ref = read()) {
            return ref.val.contains(o);
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new ReferencedSetIterator();
    }

    @Override
    public Object[] toArray() {
        try (Reference<Set<E>> ref = read()) {
            return ref.val.toArray();
        }
    }

    @Override
    public <T> T[] toArray(T[] a) {
        try (Reference<Set<E>> ref = read()) {
            return ref.val.toArray(a);
        }
    }

    @Override
    public boolean add(E e) {
        try (Reference<Set<E>> ref = write()) {
            return ref.val.add(e);
        }
    }

    @Override
    public boolean remove(Object o) {
        try (Reference<Set<E>> ref = write()) {
            return ref.val.remove(o);
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        try (Reference<Set<E>> ref = read()) {
            return ref.val.containsAll(c);
        }
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        try (Reference<Set<E>> ref = write()) {
            return ref.val.addAll(c);
        }
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if (this.isEmpty()) {
            return false;
        }
        try (Reference<Set<E>> ref = write()) {
            return ref.val.retainAll(c);
        }
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (c.isEmpty()) {
            return false;
        } else if (c instanceof ImplicitlySharedHolder && this.refEquals((ImplicitlySharedHolder<?>) c)) {
            this.clear();
            return true;
        } else {
            try (Reference<Set<E>> ref = write()) {
                return ref.val.removeAll(c);
            }
        }
    }

    @Override
    public void clear() {
        // TODO: This may create a clone of the backing data to then just clear it
        // More efficient would be to create a new empty container value instead
        try (Reference<Set<E>> ref = write()) {
            ref.val.clear();
        }
    }

    /**
     * Creates an iterable view of this shared set. When iterating the elements,
     * a copy of the shared set is iterated instead of the shared set itself.
     * Once the last element is iterated over, the copy is automatically closed.<br>
     * <br>
     * This should be used when iterating over <b>all</b> elements of this set, without
     * risking concurrent modification exceptions when elements are added or removed from
     * the set. Breaking the iteration is not recommended, as it will leave an unclosed
     * copy of the shared set behind, which will result in unneeded copying of the original
     * set when it is modified.
     * 
     * @return iterable clone view of this set's data
     */
    public Iterable<E> cloneAsIterable() {
        return () -> new ReferencedSetCopyIterator<E>(ImplicitlySharedSet.this);
    }

    @Override
    public ImplicitlySharedSet<E> clone() {
        return new ImplicitlySharedSet<E>(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected final Set<E> cloneValue(Set<E> input) {
        // Java clone() is poor. Handle the generic cases and hope for the best.
        if (input instanceof TreeSet) {
            return (Set<E>) ((TreeSet<E>) input).clone();
        } else if (input instanceof LinkedHashSet) {
            return new LinkedHashSet<E>(input);
        } else if (input instanceof HashSet) {
            return (Set<E>) ((HashSet<E>) input).clone();
        } else {
            return new HashSet<E>(input);
        }
    }

    private static final class ReferencedSetCopyIterator<E> implements Iterator<E> {
        private ImplicitlySharedSet<E> copy;
        private Iterator<E> copyIter;

        public ReferencedSetCopyIterator(ImplicitlySharedSet<E> set) {
            this.copy = set.clone();
            this.copyIter = this.copy.iterator();
        }

        @Override
        public boolean hasNext() {
            if (this.copyIter.hasNext()) {
                return true;
            } else {
                this.copy.close();
                return false;
            }
        }

        @Override
        public E next() {
            return this.copyIter.next();
        }

        @Override
        public void remove() {
            // Does nothing. It's a copy.
        }
    }

    private final class ReferencedSetIterator implements Iterator<E> {
        private Reference<Set<E>> ref;
        private Iterator<E> baseIter;
        private boolean hasLastElement;
        private E lastElement;

        public ReferencedSetIterator() {
            this.ref = ImplicitlySharedSet.this.ref;
            this.baseIter = this.ref.val.iterator();
            this.hasLastElement = false;
            this.lastElement = null;
        }

        @Override
        public boolean hasNext() {
            return this.baseIter.hasNext();
        }

        @Override
        public E next() {
            E result = this.baseIter.next();
            this.lastElement = result;
            this.hasLastElement = true;
            return result;
        }

        @Override
        public void remove() {
            if (this.hasLastElement) {
                this.hasLastElement = false;
            } else {
                throw new IllegalStateException("next() must be called before remove() is valid");
            }

            try (Reference<Set<E>> ref_writable = write()) {
                if (ref_writable == this.ref) {
                    // Can write to the same thing we are iterating over
                    this.baseIter.remove();
                } else {
                    // Remove from the writable set, we can not modify what we are iterating over
                    ref_writable.val.remove(this.lastElement);
                }
            }
        }
    }

}
