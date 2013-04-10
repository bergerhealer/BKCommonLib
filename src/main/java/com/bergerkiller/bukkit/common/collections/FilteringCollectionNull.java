package com.bergerkiller.bukkit.common.collections;

import java.util.Collection;

/**
 * A Filtering Collection implementation that filters null elements
 * 
 * @param <T> - type of Collection
 */
public class FilteringCollectionNull<T> extends FilteringCollection<T> {

	public FilteringCollectionNull(Collection<T> base) {
		super(base);
	}

	@Override
	public boolean isFiltered(Object value) {
		return value == null;
	}
}
