package com.bergerkiller.bukkit.common.collections;

import java.util.Collection;

import com.bergerkiller.bukkit.common.filtering.Filter;

/**
 * A self-filtering FilteredCollection implementation.
 * This implementation allows for quick-and-dirty Filtered Collections
 * that don't require the creation of new Filter instances.
 * 
 * @param <E> - Collection element type
 */
public abstract class FilteredCollectionSelf<E> extends FilteredCollection<E> implements Filter<E> {

	/**
	 * Constructs a new Self-Filtered Collection using the base Collection specified
	 * 
	 * @param base Collection
	 */
	protected FilteredCollectionSelf(Collection<E> base) {
		super(base, null);
		filter = this;
	}
}
