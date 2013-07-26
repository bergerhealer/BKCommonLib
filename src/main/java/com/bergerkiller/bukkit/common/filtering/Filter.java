package com.bergerkiller.bukkit.common.filtering;

/**
 * Represents something that filters elements
 * 
 * @param <E> - Element type
 */
public interface Filter<E> {

	/**
	 * Gets whether an element is filtered by this Filter or not
	 * 
	 * @param element to check
	 * @return True if the element is filtered, False if not
	 */
	public boolean isFiltered(E element);
}
