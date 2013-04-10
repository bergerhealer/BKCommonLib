package com.bergerkiller.bukkit.common.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A basic implementation of a Collection that filters some elements from a base Collection
 * 
 * @param <T> - type of Collection
 */
public abstract class FilteringCollection<T> implements Collection<T> {
	private final Collection<T> base;

	public FilteringCollection(Collection<T> base) {
		this.base = base;
	}

	/**
	 * Checks whether a given value is filtered from this Collection
	 * 
	 * @param value to check
	 * @return True if the value is filtered, False if not
	 */
	public abstract boolean isFiltered(Object value);

	@Override
	public Iterator<T> iterator() {
		return new FilteredIterator<T>(this);
	}

	private static class FilteredIterator<T> implements Iterator<T> {
		private final Iterator<T> base;
		private final FilteringCollection<T> collection;
		private boolean hasNext;
		private T next;

		public FilteredIterator(FilteringCollection<T> collection) {
			this.base = collection.base.iterator();
			this.collection = collection;
			this.genNext();
		}

		private void genNext() {
			while (this.base.hasNext()) {
				this.next = this.base.next();
				if (!this.collection.isFiltered(this.next)) {
					this.hasNext = true;
					return;
				}
			}
			this.hasNext = false;
		}

		@Override
		public boolean hasNext() {
			return this.hasNext;
		}

		@Override
		public T next() {
			if (!this.hasNext) {
				throw new NoSuchElementException("No new elements are available");
			}
			final T next = this.next;
			this.genNext();
			return next;
		}

		@Override
		public void remove() {
			this.base.remove();
		}
	}

	@Override
	public int size() {
		int size = 0;
		for (T value : base) {
			if (!isFiltered(value)) {
				size++;
			}
		}
		return size;
	}

	@Override
	public boolean isEmpty() {
		for (T value : base) {
			if (!isFiltered(value)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean contains(Object o) {
		return base.contains(o);
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
		if (isFiltered(e)) {
			return false;
		}
		base.add(e);
		return true;
	}

	@Override
	public boolean remove(Object o) {
		if (isFiltered(o)) {
			return false;
		}
		base.remove(o);
		return true;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return CollectionBasics.containsAll(this, c);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		return CollectionBasics.addAll(this, c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return CollectionBasics.removeAll(this, c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return CollectionBasics.retainAll(this, c);
	}

	@Override
	public void clear() {
		Iterator<T> iter = this.base.iterator();
		if (!isFiltered(iter.next())) {
			iter.remove();
		}
	}
}
