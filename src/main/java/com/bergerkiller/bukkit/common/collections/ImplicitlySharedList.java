package com.bergerkiller.bukkit.common.collections;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
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
public class ImplicitlySharedList<E> extends ImplicitlySharedHolder<List<E>> implements List<E>, AutoCloseable {

    /**
     * Creates a new implicitly shared list, backed by an ArrayList
     */
    public ImplicitlySharedList() {
        super(new ArrayList<E>());
    }

    /**
     * Creates a new implicitly shared list, specifying what underlying list implementation is used.
     * 
     * @param list to use internally
     */
    public ImplicitlySharedList(List<E> list) {
        super(list);
    }

    /**
     * Creates a new implicitly shared list by sharing contents from another list.
     * The moment this shared list is about to be modified, a detached copy is created.
     * 
     * @param sharedList to access for reading contents
     */
    public ImplicitlySharedList(ImplicitlySharedList<E> sharedList) {
        super(sharedList.ref);
    }

    @Override
    public int size() {
        try (Reference<List<E>> ref = read()) {
            return ref.val.size();
        }
    }

    @Override
    public boolean isEmpty() {
        try (Reference<List<E>> ref = read()) {
            return ref.val.isEmpty();
        }
    }

    @Override
    public boolean contains(Object o) {
        try (Reference<List<E>> ref = read()) {
            return ref.val.contains(o);
        }
    }

    @Override
    public Object[] toArray() {
        try (Reference<List<E>> ref = read()) {
            return ref.val.toArray();
        }
    }

    @Override
    public <T> T[] toArray(T[] a) {
        try (Reference<List<E>> ref = read()) {
            return ref.val.toArray(a);
        }
    }

    @Override
    public boolean add(E e) {
        try (Reference<List<E>> ref = write()) {
            return ref.val.add(e);
        }
    }

    @Override
    public boolean remove(Object o) {
        try (Reference<List<E>> ref = write()) {
            return ref.val.remove(o);
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        try (Reference<List<E>> ref = read()) {
            return ref.val.containsAll(c);
        }
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        try (Reference<List<E>> ref = write()) {
            return ref.val.addAll(c);
        }
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if (this.isEmpty()) {
            return false;
        }
        try (Reference<List<E>> ref = write()) {
            return ref.val.retainAll(c);
        }
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (c.isEmpty()) {
            return false;
        }
        try (Reference<List<E>> ref = write()) {
            return ref.val.removeAll(c);
        }
    }

    @Override
    public void clear() {
        try (Reference<List<E>> ref = write()) {
            ref.val.clear();
        }
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        try (Reference<List<E>> ref = write()) {
            return ref.val.addAll(c);
        }
    }

    @Override
    public E get(int index) {
        try (Reference<List<E>> ref = read()) {
            return ref.val.get(index);
        }
    }

    @Override
    public E set(int index, E element) {
        try (Reference<List<E>> ref = write()) {
            return ref.val.set(index, element);
        }
    }

    @Override
    public void add(int index, E element) {
        try (Reference<List<E>> ref = write()) {
            ref.val.add(index, element);
        }
    }

    @Override
    public E remove(int index) {
        try (Reference<List<E>> ref = write()) {
            return ref.val.remove(index);
        }
    }

    @Override
    public int indexOf(Object o) {
        try (Reference<List<E>> ref = read()) {
            return ref.val.indexOf(o);
        }
    }

    @Override
    public int lastIndexOf(Object o) {
        try (Reference<List<E>> ref = read()) {
            return ref.val.lastIndexOf(o);
        }
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
        return () -> new ReferencedListCopyIterator<E>(ImplicitlySharedList.this);
    }

    @Override
    public ImplicitlySharedList<E> clone() {
        return new ImplicitlySharedList<E>(this);
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
    }

    @Override
    public Iterator<E> iterator() {
        return new ReferencedListIterator();
    }

    private final AbstractList<E> createAbstractList() {
        return new AbstractList<E>() {
            @Override
            public int size() {
                return ImplicitlySharedList.this.size();
            }

            @Override
            public boolean isEmpty() {
                return ImplicitlySharedList.this.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                return ImplicitlySharedList.this.contains(o);
            }

            @Override
            public Object[] toArray() {
                return ImplicitlySharedList.this.toArray();
            }

            @Override
            public <T> T[] toArray(T[] a) {
                return ImplicitlySharedList.this.toArray(a);
            }

            @Override
            public boolean add(E e) {
                return ImplicitlySharedList.this.add(e);
            }

            @Override
            public boolean remove(Object o) {
                return ImplicitlySharedList.this.remove(o);
            }

            @Override
            public boolean containsAll(Collection<?> c) {
                return ImplicitlySharedList.this.containsAll(c);
            }

            @Override
            public boolean addAll(Collection<? extends E> c) {
                return ImplicitlySharedList.this.addAll(c);
            }

            @Override
            public boolean retainAll(Collection<?> c) {
                return ImplicitlySharedList.this.retainAll(c);
            }

            @Override
            public boolean removeAll(Collection<?> c) {
                return ImplicitlySharedList.this.removeAll(c);
            }

            @Override
            public void clear() {
                ImplicitlySharedList.this.clear();
            }

            @Override
            public boolean addAll(int index, Collection<? extends E> c) {
                return ImplicitlySharedList.this.addAll(index, c);
            }

            @Override
            public E get(int index) {
                return ImplicitlySharedList.this.get(index);
            }

            @Override
            public E set(int index, E element) {
                return ImplicitlySharedList.this.set(index, element);
            }

            @Override
            public void add(int index, E element) {
                ImplicitlySharedList.this.add(index, element);
            }

            @Override
            public E remove(int index) {
                return ImplicitlySharedList.this.remove(index);
            }

            @Override
            public int indexOf(Object o) {
                return ImplicitlySharedList.this.indexOf(o);
            }

            @Override
            public int lastIndexOf(Object o) {
                return ImplicitlySharedList.this.lastIndexOf(o);
            }

            @Override
            public Iterator<E> iterator() {
                return ImplicitlySharedList.this.iterator();
            }
        };
    }

    @Override
    public ListIterator<E> listIterator() {
        return createAbstractList().listIterator();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return createAbstractList().listIterator(index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return createAbstractList().subList(fromIndex, toIndex);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected final List<E> cloneValue(List<E> input) {
        // Java clone() is poor. Handle the generic cases and hope for the best.
        if (input instanceof ArrayList) {
            return (List<E>) ((ArrayList<E>) input).clone();
        } else if (input instanceof LinkedList) {
            return new LinkedList<E>(input);
        } else if (input instanceof Vector) {
            return (List<E>) ((Vector<E>) input).clone();
        } else {
            return new ArrayList<E>(input);
        }
    }

    private final class ReferencedListIterator implements Iterator<E> {
        private Reference<List<E>> ref;
        private Iterator<E> baseIter;
        private int index;
        private boolean hasLastElement;

        public ReferencedListIterator() {
            this.ref = ImplicitlySharedList.this.ref;
            this.baseIter = this.ref.val.iterator();
            this.index = -1;
            this.hasLastElement = false;
        }

        @Override
        public boolean hasNext() {
            return this.baseIter.hasNext();
        }

        @Override
        public E next() {
            E result = this.baseIter.next();
            this.index++;
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

            try (Reference<List<E>> ref_writable = write()) {
                if (ref_writable == this.ref) {
                    // Can write to the same thing we are iterating over
                    this.baseIter.remove();
                } else {
                    // Remove from the writable set, we can not modify what we are iterating over
                    ImplicitlySharedList.this.remove(this.index);
                }
                this.index--;
            }
        }
    }

}
