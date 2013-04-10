package com.bergerkiller.bukkit.common.collections;

import java.util.Collection;

/**
 * A filtering Collection implementation that filters all elements not of a given type
 * 
 * @param <T> - type of Collection
 */
public class FilteringCollectionType<T> extends FilteringCollection<T> {
	private final Class<?> type;

	public FilteringCollectionType(Collection<T> base, Class<?> type) {
		super(base);
		this.type = type;
	}

	@Override
	public boolean isFiltered(Object value) {
		return !type.isInstance(value);
	}
}
