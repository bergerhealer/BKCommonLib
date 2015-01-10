package com.bergerkiller.bukkit.common.proxies;

/**
 * Represents a Class that redirects method calls to another Object
 */
public interface Proxy<T> {
	/**
	 * Sets the base class of this Proxy
	 * This is the Object instance where method calls are redirected to.
	 * 
	 * @param base class to set to
	 */
	public void setProxyBase(T base);

	/**
	 * Gets the base class of this Proxy.
	 * This is the Object instance where method calls are redirected to.
	 * 
	 * @return proxy base
	 */
	public T getProxyBase();
}
