package com.bergerkiller.bukkit.common.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.bergerkiller.bukkit.common.filtering.Filter;
import com.bergerkiller.bukkit.common.filtering.FilterBundle;
import com.bergerkiller.bukkit.common.filtering.FilterNull;
import com.bergerkiller.bukkit.common.filtering.FilterType;
import com.bergerkiller.bukkit.common.utils.LogicUtil;

/**
 * A basic implementation of a Collection that filters some elements from a base Collection.
 * To define the elements to a filter, a {@link Filter} is used.
 * 
 * @param <E> - Collection element type
 */
public class FilteredCollection<E> implements Collection<E> {
	private final Collection<E> base;
	protected Filter<E> filter;

	/**
	 * Constructs a new Filtered Collection using the base Collection and
	 * Filter specified
	 * 
	 * @param base Collection
	 * @param filter to use to filter elements from the base Collection
	 */
	public FilteredCollection(Collection<E> base, Filter<E> filter) {
		this.base = base;
		this.filter = filter;
	}

	@Override
	public Iterator<E> iterator() {
		return new FilteredIterator<E>(this);
	}

	/**
	 * Gets the Filter that is being used by this Filtered Collection
	 * 
	 * @return Filtered Collection Filter
	 */
	public Filter<E> getFilter() {
		return this.filter;
	}

	/**
	 * Sets the Filter that is being used by this Filtered Collection
	 * 
	 * @param filter to set to
	 */
	public void setFilter(Filter<E> filter) {
		this.filter = filter;
	}

	private static class FilteredIterator<T> implements Iterator<T> {
		private final Iterator<T> base;
		private final FilteredCollection<T> collection;
		private boolean hasNext;
		private T next;

		public FilteredIterator(FilteredCollection<T> collection) {
			this.base = collection.base.iterator();
			this.collection = collection;
			this.genNext();
		}

		private void genNext() {
			while (this.base.hasNext()) {
				this.next = this.base.next();
				if (!this.collection.filter.isFiltered(this.next)) {
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
		for (E value : base) {
			if (!filter.isFiltered(value)) {
				size++;
			}
		}
		return size;
	}

	@Override
	public boolean isEmpty() {
		if (!base.isEmpty()) {
			for (E value : base) {
				if (!filter.isFiltered(value)) {
					return false;
				}
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
	public <T> T[] toArray(T[] a) {
		return CollectionBasics.toArray(this, a);
	}

	@Override
	public boolean add(E e) {
		if (filter.isFiltered(e)) {
			return false;
		} else {
			base.add(e);
			return true;
		}
	}

	@Override
	public boolean remove(Object o) {
		// Remove the first element that equals o and is not filtered
		Iterator<E> iter = base.iterator();
		while (iter.hasNext()) {
			final E value = iter.next();
			if (LogicUtil.bothNullOrEqual(value, o) && !filter.isFiltered(value)) {
				iter.remove();
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
	public boolean addAll(Collection<? extends E> c) {
		return CollectionBasics.addAll(this, c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		// Size check - do the most efficient thing
		if (c.size() > this.size()) {
			// Use remove on each element in the Collection
			return CollectionBasics.removeAll(this, c);
		} else {
			// Go by all elements in this Collection
			// If contained in c, remove them
			Iterator<E> iter = base.iterator();
			boolean removed = false;
			while (iter.hasNext()) {
				E value = iter.next();
				if (c.contains(value) && !filter.isFiltered(value)) {
					iter.remove();
					removed = true;
				}
			}
			return removed;
		}
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return CollectionBasics.retainAll(this, c);
	}

	@Override
	public void clear() {
		Iterator<E> iter = this.base.iterator();
		if (!filter.isFiltered(iter.next())) {
			iter.remove();
		}
	}

	/**
	 * Constructs a new Filtered Collection using the base Collection and
	 * Filter specified
	 * 
	 * @param base Collection
	 * @param filter to use to filter elements from the base Collection
	 * @return new FilteredCollection instance
	 */
	public static <T> FilteredCollection<T> create(Collection<T> base, Filter<T> filter) {
		return new FilteredCollection<T>(base, filter);
	}

	/**
	 * Constructs a new Filtered Collection using the base Collection and
	 * Filters specified. If any of the Filters filter an element, that element
	 * is not exposed.<br><br>
	 * 
	 * The Filter element types can not be checked, so be sure to only use Filters
	 * that can accept the element type of the base Collection.
	 * 
	 * @param base Collection
	 * @param filters to use to filter elements from the base Collection
	 * @return new FilteredCollection instance
	 */
	public static <T> FilteredCollection<T> createBundleFilter(Collection<T> base,  Filter<?>... filters) {
		return create(base, new FilterBundle<T>(filters));
	}

	/**
	 * Constructs a new Filtered Collection using the base Collection and
	 * Filters specified. If any of the Filters filter an element, that element
	 * is not exposed.<br><br>
	 * 
	 * The Filter element types can not be checked, so be sure to only use Filters
	 * that can accept the element type of the base Collection.
	 * 
	 * @param base Collection
	 * @param filters to use to filter elements from the base Collection
	 * @return new FilteredCollection instance
	 */
	public static <T> FilteredCollection<T> createBundleFilter(Collection<T> base, Collection<Filter<?>> filters) {
		return create(base, new FilterBundle<T>(filters));
	}

	/**
	 * Constructs a new Filtered Collection using the base Collection and
	 * a NULL-Element filter.
	 * Only non-null elements are exposed.
	 * 
	 * @param base Collection
	 * @return new FilteredCollection instance
	 */
	@SuppressWarnings("unchecked")
	public static <T> FilteredCollection<T> createNullFilter(Collection<T> base) {
		return create(base, FilterNull.INSTANCE);
	}

	/**
	 * Constructs a new Filtered Collection using the base Collection and
	 * a NULL-Element filter
	 * 
	 * @param base Collection
	 * @param type of elements to PASS (only elements of this type are exposed)
	 * @return new FilteredCollection instance
	 */
	public static <T> FilteredCollection<T> createTypeFilter(Collection<T> base, Class<?> type) {
		return create(base, new FilterType<T>(type));
	}
}
