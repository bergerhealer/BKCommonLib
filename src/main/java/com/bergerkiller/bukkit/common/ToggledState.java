package com.bergerkiller.bukkit.common;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Allows you to toggle this Boolean On or Off
 */
public class ToggledState extends AtomicBoolean {
	private static final long serialVersionUID = 1L;

	public ToggledState(boolean initial) {
		super(initial);
	}

	public ToggledState() {
		super();
	}

	/**
	 * Sets the value to 'True'
	 * 
	 * @return True if the value changed to True, False if left unchanged
	 */
	public boolean set() {
		return this.compareAndSet(false, true);
	}

	/**
	 * Sets the value to 'False'
	 * 
	 * @return True if the value changed to False, False if left unchanged
	 */
	public boolean clear() {
		return this.compareAndSet(true, false);
	}
}
