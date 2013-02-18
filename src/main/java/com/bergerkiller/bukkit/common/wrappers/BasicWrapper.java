package com.bergerkiller.bukkit.common.wrappers;

/**
 * A basic implementation for a wrapper class<br>
 * Note: there is no type safety in the constructor
 * 
 * @param <T> - type of wrapped data
 */
public class BasicWrapper<T> {
	protected T handle;

	/**
	 * Sets the internal handle for this wrapper<br>
	 * This handle can not be null
	 * 
	 * @param handle to set to
	 */
	@SuppressWarnings("unchecked")
	protected void setHandle(Object handle) {
		if (handle == null) {
			throw new IllegalArgumentException("The handle can not be null");
		}
		this.handle = (T) handle;
	}

	/**
	 * Gets the internal handle from this wrapper
	 * 
	 * @return handle
	 */
	public Object getHandle() {
		return handle;
	}

	@Override
	public String toString() {
		return handle.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof BasicWrapper) {
			o = ((BasicWrapper<?>) o).getHandle();
		}
		return handle.equals(o);
	}
}
