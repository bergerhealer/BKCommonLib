package com.bergerkiller.bukkit.common.filtering;

/**
 * Filters all elements that are not an instance of a certain type.
 * The {@link #getFilteredType()} can be overridden to allow a dynamic type.
 * 
 * @param <E> - Element type
 */
public class FilterType<E> implements Filter<E> {
	private final Class<?> type;

	/**
	 * Constructs a new Type Filter
	 * 
	 * @param type that elements have to be an instance of to not get filtered
	 */
	public FilterType(Class<?> type) {
		this.type = type;
	}

	/**
	 * Gets the Class type that is being filtered.
	 * Elements not of this type are filtered.
	 * 
	 * @return filtered type
	 */
	public Class<?> getFilteredType() {
		return type;
	}

	@Override
	public boolean isFiltered(E element) {
		return !getFilteredType().isInstance(element);
	}
}
