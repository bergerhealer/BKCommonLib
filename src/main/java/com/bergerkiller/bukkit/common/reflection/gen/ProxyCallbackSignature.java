package com.bergerkiller.bukkit.common.reflection.gen;

import java.lang.reflect.Method;
import java.util.Map;

public class ProxyCallbackSignature implements CallbackSignature {
	private final Method callbackMethod;

	public ProxyCallbackSignature(Method callbackMethod) {
		this.callbackMethod = callbackMethod;
	}

	@Override
	public CallbackMethod createCallback(Object instance, Map<Class<?>, Object> callbackInstances) {
		final Class<?> declaringClass = callbackMethod.getDeclaringClass();
		final Object callbackInstance = callbackInstances.get(declaringClass);
		if (instance == null) {
			throw new RuntimeException("No callback instance found for Class '" + declaringClass.getName() + "'!");
		}
		return new ProxyCallbackMethod(callbackMethod, callbackInstance);
	}
}
