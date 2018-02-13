package com.bergerkiller.bukkit.common.collections;

import java.util.Collection;
import java.util.ConcurrentModificationException;
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
public class ImplicitlySharedSet<E> implements Set<E>, AutoCloseable {
    private ReferencedSet<E> ref;

    /**
     * Creates a new implicitly shared set, backed by a HashSet
     */
    public ImplicitlySharedSet() {
        this(new HashSet<E>());
    }

    /**
     * Creates a new implicitly shared set, specifying what underlying set implementation is used.
     * 
     * @param set to use internally
     */
    public ImplicitlySharedSet(Set<E> set) {
        this(new ReferencedSet<E>(set));
    }

    /**
     * Creates a new implicitly shared set by sharing contents from another set.
     * The moment this shared set is about to be modified, a detached copy is created.
     * 
     * @param sharedSet to access for reading contents
     */
    public ImplicitlySharedSet(ImplicitlySharedSet<E> sharedSet) {
        this(sharedSet.ref);
    }

    private ImplicitlySharedSet(ReferencedSet<E> referencedSet) {
        this.ref = referencedSet;
        this.ref.ctr++;
    }

    /**
     * Assigns the contents of an implicitly shared set to this shared set.
     * Future read calls will now read from the set instead of the contents that existed before.
     * The moment this shared set is about to be modified, a detached copy is created.
     * 
     * @param sharedSet to assign
     */
    public void assign(ImplicitlySharedSet<E> sharedSet) {
        this.close();
        this.ref = sharedSet.ref;
        this.ref.ctr++;
    }

    /**
     * Gets whether this shared set references the exact same backing set
     * as another shared set.
     * 
     * @param sharedSet
     * @return True if referencing the same set
     */
    public boolean refEquals(ImplicitlySharedSet<E> sharedSet) {
        return sharedSet != null && sharedSet.ref == this.ref;
    }

    @Override
    public int size() {
        return read().size();
    }

    @Override
    public boolean isEmpty() {
        return read().isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return read().contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return new ReferencedSetIterator();
    }

    @Override
    public Object[] toArray() {
        return read().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return read().toArray(a);
    }

    @Override
    public boolean add(E e) {
        return write().add(e);
    }

    @Override
    public boolean remove(Object o) {
        return write().remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return read().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return write().addAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return !this.isEmpty() && write().retainAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return !c.isEmpty() && write().removeAll(c);
    }

    @Override
    public void clear() {
        write().clear();
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
        return new Iterable<E>() {
            @Override
            public Iterator<E> iterator() {
                return new ReferencedSetCopyIterator<E>(ImplicitlySharedSet.this);
            }
        };
    }

    @Override
    public ImplicitlySharedSet<E> clone() {
        return new ImplicitlySharedSet<E>(this);
    }

    private final Set<E> write() {
        if (this.ref.ctr > 1) {
            this.ref.ctr--;
            this.ref = this.ref.clone();
            this.ref.ctr++;
        }
        return this.ref.set;
    }

    private final Set<E> read() {
        return this.ref.set;
    }

    /**
     * Closes this shared set, so that it no longer holds access to the shared set contents.
     * If this shared set was created mirroring another shared set, this call enables
     * that set to modify the contents without copying.
     */
    @Override
    public void close() {
        if (this.ref != null) {
            this.ref.ctr--;
            this.ref = null;
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

        @Override
        protected void finalize() throws Throwable {
            try {
                this.copy.close();
            } finally {
                super.finalize();
            }
        }
    }

    private final class ReferencedSetIterator implements Iterator<E> {
        private ReferencedSet<E> ref;
        private Iterator<E> baseIter;
        private int numNextCalls;

        public ReferencedSetIterator() {
            this.ref = ImplicitlySharedSet.this.ref;
            this.baseIter = read().iterator();
            this.numNextCalls = 0;
        }

        @Override
        public boolean hasNext() {
            return baseIter.hasNext();
        }

        @Override
        public E next() {
            this.checkConcurrent();
            E result = baseIter.next();
            this.numNextCalls++;
            return result;
        }

        @Override
        public void remove() {
            this.checkConcurrent();
            if (ref.ctr > 1) {
                // We need to make a copy of the set to modify it
                // However, we are iterating over one as well!
                // So we must skip the elements we have iterated and continue.
                this.baseIter = write().iterator();
                for (int i = 0; i < this.numNextCalls; i++) {
                    this.baseIter.next();
                }
            }
            this.baseIter.remove();
        }

        // Check if we modified the set while iterating
        private final void checkConcurrent() {
            if (ImplicitlySharedSet.this.ref != this.ref) {
                throw new ConcurrentModificationException("Set was modified while iterating");
            }
        }
    }

    private static class ReferencedSet<T> {
        public final Set<T> set;
        public int ctr;

        public ReferencedSet(Set<T> set) {
            this.set = set;
            this.ctr = 0;
        }

        @Override
        public ReferencedSet<T> clone() {
            // Java clone() is poor. Handle the generic cases and hope for the best.
            if (this.set instanceof TreeSet) {
                return create(((TreeSet<T>) this.set).clone());
            } else if (this.set instanceof LinkedHashSet) {
                return create(new LinkedHashSet<T>(this.set));
            } else if (this.set instanceof HashSet) {
                return create(((HashSet<T>) this.set).clone());
            } else {
                return create(new HashSet<T>(this.set));
            }
        }

        @SuppressWarnings("unchecked")
        private static final <T> ReferencedSet<T> create(Object set) {
            return new ReferencedSet<T>((Set<T>) set);
        }
    }

}
