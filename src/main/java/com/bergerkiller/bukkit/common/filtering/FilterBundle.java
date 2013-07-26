package com.bergerkiller.bukkit.common.filtering;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import com.bergerkiller.bukkit.common.utils.CommonUtil;

/**
 * A bundle of Filters. If any of the Filters contained in a bundle
 * filter an element, the {@link #isFiltered(Object)} method will return False.
 * This can be used for multiple conditional filtering requirements.
 *
 * @param <E> - Element type
 */
public class FilterBundle<E> implements Filter<E> {
	private final Collection<Filter<E>> filters;

	/**
	 * Constructs a new Filter Bundle using the filters specified.
	 * The Filter element types can not be checked, so be sure to
	 * only construct a Filter Bundle using Filters that can accept
	 * the element type of this filter.
	 * 
	 * @param filters to create a bundle for
	 */
	public FilterBundle(Filter<?>... filters) {
		this(Arrays.asList(filters));
	}

	/**
	 * Constructs a new Filter Bundle using the filters specified.
	 * The Filter element types can not be checked, so be sure to
	 * only construct a Filter Bundle using Filters that can accept
	 * the element type of this filter.
	 * 
	 * @param filters to create a bundle for
	 */
	public FilterBundle(Collection<Filter<?>> filters) {
		this.filters = CommonUtil.unsafeCast(Collections.unmodifiableCollection(filters));
	}

	/**
	 * Gets a Collection of all the Filters used by this Filter Bundle.
	 * The returned Collection is unmodifiable.
	 * 
	 * @return Collection of filters
	 */
	public Collection<Filter<E>> getFilters() {
		return this.filters;
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
