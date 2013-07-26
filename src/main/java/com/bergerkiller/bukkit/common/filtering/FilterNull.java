package com.bergerkiller.bukkit.common.filtering;

/**
 * A filter that performs a simple NULL-Check on each element.
 * If the element is null, True is returned indicating that it should be filtered.
 * 
 * @param <E> - Element type
 */
public class FilterNull<E> implements Filter<E> {
	/**
	 * An instance of a NULL-Filter that can be used directly.
	 * The implementation of a NULL-Filter does not depend on the element type.
	 */
	@SuppressWarnings("rawtypes")
	public static final FilterNull INSTANCE = new FilterNull();

	@Override
	public boolean isFiltered(E element) {
		return element == null;
	}

	/**
	 * Obtains an instance of a NULL-Filter, providing automatic casting.
	 * 
	 * @return {@link #INSTANCE}
	 */
	@SuppressWarnings("unchecked")
	public static <T> FilterNull<T> getInstance() {
		return INSTANCE;
	}
}
