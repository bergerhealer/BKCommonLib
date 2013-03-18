package com.bergerkiller.bukkit.common.proxies;

import java.lang.reflect.Method;
import java.util.logging.Level;

import org.bukkit.Bukkit;

/**
 * A base class for classes redirecting method calls to another class instance.
 * For example, you can make a Proxy of a List so it will alter elements in another List instance than itself.
 * 
 * @param <T> - type of Proxy base
 */
public class ProxyBase<T> implements Proxy<T> {
	protected T base;

	public ProxyBase(T base) {
		setProxyBase(base);
	}

	@Override
	public void setProxyBase(T base) {
		this.base = base;
	}

	@Override
	public T getProxyBase() {
		return this.base;
	}

	@Override
	public String toString() {
		return base.toString();
	}

	@Override
	public int hashCode() {
		return base.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		return base.equals(unwrap(object));
	}

	/**
	 * If the object is a Proxy class, it's base is unwrapped and returned instead
	 * 
	 * @param object to check
	 * @return unwrapped Proxy data, or the input object
	 */
	public static Object unwrap(Object object) {
		if (object instanceof Proxy) {
			return ((Proxy<?>) object).getProxyBase();
		} else {
			return object;
		}
	}

	/**
	 * Validates that all methods are properly overrided.
	 * Logs a warning if this is not the case.
	 * 
	 * @param proxy to check
	 * @return True if validation was successful, False if not
	 */
	public static boolean validate(Class<? extends Proxy<?>> proxy) {
		try {
			boolean succ = true;
			boolean loggedHeader = false;
			for (Method method : proxy.getDeclaredMethods()) {
				if (method.getDeclaringClass() != proxy) {
					succ = false;
					if (!loggedHeader) {
						loggedHeader = true;
						Bukkit.getLogger().log(Level.WARNING, "[Proxy] Some method(s) are not overrided in '" + proxy.getName() + "':");
					}
					Bukkit.getLogger().log(Level.WARNING, "    - '" + method.toGenericString());
				}
			}
			return succ;
		} catch (Throwable t) {
			t.printStackTrace();
			return false;
		}
	}
}
