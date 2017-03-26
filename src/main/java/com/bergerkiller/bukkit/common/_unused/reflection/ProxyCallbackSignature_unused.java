package com.bergerkiller.bukkit.common._unused.reflection;

import java.lang.reflect.Method;
import java.util.Map;


public class ProxyCallbackSignature_unused implements CallbackSignature_unused {

    private final Method callbackMethod;

    public ProxyCallbackSignature_unused(Method callbackMethod) {
        this.callbackMethod = callbackMethod;
    }

    @Override
    public CallbackMethod_unused createCallback(Object instance, Map<Class<?>, Object> callbackInstances) {
        final Class<?> declaringClass = callbackMethod.getDeclaringClass();
        final Object callbackInstance = callbackInstances.get(declaringClass);
        if (instance == null) {
            throw new RuntimeException("No callback instance found for Class '" + declaringClass.getName() + "'!");
        }
        return new ProxyCallbackMethod_unused(callbackMethod, callbackInstance);
    }
}
