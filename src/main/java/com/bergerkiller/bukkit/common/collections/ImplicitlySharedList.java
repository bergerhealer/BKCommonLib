package com.bergerkiller.bukkit.common.collections;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/**
 * A list which can be copied without copying the data, performing an actual copy
 * only when the internal data is modified. Copies can be iterated without risking
 * concurrent modification exceptions when the original is modified.<br>
 * <br>
 * When you are done working with a copy of the data, it is recommended to call
 * {@link #close()} on the copy in order for the reference to the data to be removed.
 * This reduces unneeded copying in derivative instances that reference the same data.<br>
 * <br>
 * To iterate all the elements of the list without risking concurrent modification exceptions,
 * the {@link #cloneAsIterable()} can be used. All elements should be iterated for best performance.
 * If the iteration result in a <i>break</i>, then try-with-resources is a better alternative.
 */
public class ImplicitlySharedList<E> extends AbstractList<E> implements List<E>, AutoCloseable {
    private ReferencedList<E> ref;

    /**
     * Creates a new implicitly shared list, backed by an ArrayList
     */
    public ImplicitlySharedList() {
        this(new ArrayList<E>());
    }

    /**
     * Creates a new implicitly shared list, specifying what underlying list implementation is used.
     * 
     * @param list to use internally
     */
    public ImplicitlySharedList(List<E> list) {
        this(new ReferencedList<E>(list));
    }

    /**
     * Creates a new implicitly shared list by sharing contents from another list.
     * The moment this shared list is about to be modified, a detached copy is created.
     * 
     * @param sharedList to access for reading contents
     */
    public ImplicitlySharedList(ImplicitlySharedList<E> sharedList) {
        this(sharedList.ref);
    }

    private ImplicitlySharedList(ReferencedList<E> referencedList) {
        this.ref = referencedList;
        this.ref.ctr++;
    }

    /**
     * Assigns the contents of an implicitly shared list to this shared list.
     * Future read calls will now read from the list instead of the contents that existed before.
     * The moment this shared list is about to be modified, a detached copy is created.
     * 
     * @param sharedList to assign
     */
    public void assign(ImplicitlySharedList<E> sharedList) {
        this.close();
        this.ref = sharedList.ref;
        this.ref.ctr++;
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

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return write().addAll(index, c);
    }

    @Override
    public E get(int index) {
        return read().get(index);
    }

    @Override
    public E set(int index, E element) {
        return write().set(index, element);
    }

    @Override
    public void add(int index, E element) {
        write().add(index, element);
    }

    @Override
    public E remove(int index) {
        return write().remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return read().indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return read().lastIndexOf(o);
    }

    /**
     * Creates an iterable view of this shared list. When iterating the elements,
     * a copy of the shared list is iterated instead of the shared list itself.
     * Once the last element is iterated over, the copy is automatically closed.<br>
     * <br>
     * This should be used when iterating over <b>all</b> elements of this list, without
     * risking concurrent modification exceptions when elements are added or removed from
     * the list. Breaking the iteration is not recommended, as it will leave an unclosed
     * copy of the shared list behind, which will result in unneeded copying of the original
     * list when it is modified.
     * 
     * @return iterable clone view of this list's data
     */
    public Iterable<E> cloneAsIterable() {
        return new Iterable<E>() {
            @Override
            public Iterator<E> iterator() {
                return new ReferencedListCopyIterator<E>(ImplicitlySharedList.this);
            }
        };
    }

    @Override
    public ImplicitlySharedList<E> clone() {
        return new ImplicitlySharedList<E>(this);
    }

    private final List<E> write() {
        if (this.ref.ctr > 1) {
            this.ref.ctr--;
            this.ref = this.ref.clone();
            this.ref.ctr++;
        }
        return this.ref.list;
    }

    private final List<E> read() {
        return this.ref.list;
    }

    /**
     * Closes this shared list, so that it no longer holds access to the shared list contents.
     * If this shared list was created mirroring another shared list, this call enables
     * that list to modify the contents without copying.
     */
    @Override
    public void close() {
        if (this.ref != null) {
            this.ref.ctr--;
            this.ref = null;
        }
    }

    private static final class ReferencedListCopyIterator<E> implements Iterator<E> {
        private ImplicitlySharedList<E> copy;
        private Iterator<E> copyIter;

        public ReferencedListCopyIterator(ImplicitlySharedList<E> list) {
            this.copy = list.clone();
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

    private static class ReferencedList<T> {
        public final List<T> list;
        public int ctr;

        public ReferencedList(List<T> list) {
            this.list = list;
            this.ctr = 0;
        }

        @Override
        public ReferencedList<T> clone() {
            // Java clone() is poor. Handle the generic cases and hope for the best.
            if (this.list instanceof ArrayList) {
                return create(((ArrayList<T>) this.list).clone());
            } else if (this.list instanceof LinkedList) {
                return create(new LinkedList<T>(this.list));
            } else if (this.list instanceof Vector) {
                return create(((Vector<T>) this.list).clone());
            } else {
                return create(new ArrayList<T>(this.list));
            }
        }

        @SuppressWarnings("unchecked")
        private static final <T> ReferencedList<T> create(Object list) {
            return new ReferencedList<T>((List<T>) list);
        }
    }

}
