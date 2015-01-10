package com.bergerkiller.bukkit.common.reflection;

/**
 * A method implementation that allows direct invoking
 */
public abstract class SafeDirectMethod<T> implements MethodAccessor<T> {

	@Override
	public boolean isValid() {
		return true;
	}
}
