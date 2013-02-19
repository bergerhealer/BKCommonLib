package com.bergerkiller.bukkit.common.wrappers;

/**
 * A basic implementation for a wrapper class
 */
public class BasicWrapper {
	protected Object handle;

	/**
	 * Sets the internal handle for this wrapper<br>
	 * This handle can not be null
	 * 
	 * @param handle to set to
	 */
	protected void setHandle(Object handle) {
		if (handle == null) {
			throw new IllegalArgumentException("The handle can not be null");
		}
		this.handle = handle;
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
			o = ((BasicWrapper) o).getHandle();
		}
		return handle != null && handle.equals(o);
	}
}
