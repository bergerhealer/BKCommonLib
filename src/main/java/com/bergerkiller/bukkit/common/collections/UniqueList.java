package com.bergerkiller.bukkit.common.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * A List implementation that only stores unique values, like a Set does
 * 
 * @param <E> - Element type
 */
public class UniqueList<E> extends ArrayList<E> {
	private static final long serialVersionUID = 1L;
	private final HashSet<E> uniqueElements;

	/**
	 * Constructs a new UniqueList with a capacity of 10
	 */
	public UniqueList() {
		this(10);
	}

	/**
	 * Constructs a new UniqueList containing all the unique contents added in order
	 * 
	 * @param contents to add to the newly constructed UniqueList
	 */
	public UniqueList(Collection<E> contents) {
		this(contents.size());
		addAll(contents);
	}

	/**
	 * Constructs a new UniqueList with the initial capacity
	 * 
	 * @param capacity to use
	 */
	public UniqueList(int capacity) {
		super(capacity);
		uniqueElements = new HashSet<E>(capacity);
	}

	@Override
	public void clear() {
		super.clear();
		uniqueElements.clear();
	}

	@Override
	public boolean contains(Object o) {
		return uniqueElements.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return uniqueElements.containsAll(c);
	}

	@Override
	public boolean remove(Object o) {
		return uniqueElements.remove(o) && super.remove(o);
	}

	@Override
	public boolean add(E e) {
		return uniqueElements.add(e) && super.add(e);
	}

	@Override
	public E remove(int index) {
		final E removed = super.remove(index);
		uniqueElements.remove(removed);
		return removed;
	}

	@Override
	public E set(int index, E e) {
		E oldValue = get(index);
		if (oldValue != e) {
			uniqueElements.remove(oldValue);
			if (uniqueElements.add(e)) {
				// Added
				super.set(index, e);
			} else {
				// Restore - we can't add this!
				uniqueElements.add(oldValue);
			}
		}
		return oldValue;
	}

	@Override
	public boolean addAll(Collection<? extends E> elements) {
		return CollectionBasics.addAll(this, elements);
	}

	@Override
	public boolean removeAll(Collection<?> elements) {
		if (this.size() <= 10) {
			return CollectionBasics.removeAll(this, elements);
		} else {
			// Faster algortihm for larger amounts of elements
			final boolean changed = uniqueElements.removeAll(elements);
			if (changed) {
				Iterator<E> iter = this.iterator();
				while (iter.hasNext()) {
					if (!uniqueElements.contains(iter.next())) {
						iter.remove();
					}
				}
			}
			return changed;
		}
	}

	@Override
	public boolean retainAll(Collection<?> elements) {
		return CollectionBasics.retainAll(this, elements);
	}

	@Override
	protected void removeRange(int fromIndex, int toIndex) {
		for (int i = fromIndex; i < toIndex; i++) {
			uniqueElements.remove(get(i));
		}
		super.removeRange(fromIndex, toIndex);
	}
}
