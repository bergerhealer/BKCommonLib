package com.bergerkiller.bukkit.common.filtering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.bergerkiller.bukkit.common.utils.LogicUtil;

/**
 * A bundle of Filters. If any of the Filters contained in a bundle
 * filter an element, the {@link #isFiltered(Object)} method will return False.
 * This can be used for multiple conditional filtering requirements.
 *
 * @param <E> - Element type
 */
public class FilterBundle<E> implements Filter<E> {
	private final List<Filter<E>> filters = new ArrayList<Filter<E>>();

	/**
	 * Constructs a new empty Filter Bundle.
	 * You can add filters using the {@link #addFilter(Filter)} method.
	 */
	public FilterBundle() {
	}

	/**
	 * Constructs a new Filter Bundle using the filters specified.
	 * The Filter element types can not be checked, so be sure to
	 * only construct a Filter Bundle using Filters that can accept
	 * the element type of this filter.
	 * 
	 * @param filters to create a bundle for
	 */
	@SuppressWarnings("unchecked")
	public FilterBundle(Filter<?>... filters) {
		LogicUtil.addArray(this.filters, (Filter<E>[]) filters);
	}

	/**
	 * Constructs a new Filter Bundle using the filters specified.
	 * The Filter element types can not be checked, so be sure to
	 * only construct a Filter Bundle using Filters that can accept
	 * the element type of this filter.
	 * 
	 * @param filters to create a bundle for
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	public FilterBundle(Collection<Filter<?>> filters) {
		this.filters.addAll((Collection) filters);
	}

	/**
	 * Adds a Filter to this Filter Bundle.
	 * The Filter element types can not be checked, so be sure to
	 * only construct a Filter Bundle using Filters that can accept
	 * the element type of this filter.
	 * 
	 * @param filter to add
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void addFilter(Filter<?> filter) {
		this.filters.add((Filter) filter);
	}

	/**
	 * Removes a Filter from this Filter Bundle.
	 * 
	 * @param filter to remove
	 */
	public void removeFilter(Filter<?> filter) {
		this.filters.remove(filter);
	}

	/**
	 * Gets a Collection of all the Filters used by this Filter Bundle.
	 * The returned Collection is unmodifiable.
	 * 
	 * @return Collection of filters
	 */
	public Collection<Filter<E>> getFilters() {
		return Collections.unmodifiableCollection(this.filters);
	}

	@Override
	public boolean isFiltered(E element) {
		for (Filter<E> filter : this.filters) {
			if (filter.isFiltered(element)) {
				return true;
			}
		}
		return false;
	}
}
