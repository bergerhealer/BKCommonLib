package com.bergerkiller.bukkit.common._unused.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class ProxyCallbackMethod_unused implements CallbackMethod_unused {

    public final Method callbackMethod;
    public final Object callbackInstance;

    public ProxyCallbackMethod_unused(Method callbackMethod, Object callbackInstance) {
        this.callbackMethod = callbackMethod;
        this.callbackInstance = callbackInstance;
    }

    @Override
    public Object invoke(Object instance, Object[] args) throws Throwable {
        try {
            return callbackMethod.invoke(callbackInstance, args);
        } catch (InvocationTargetException ex) {
            throw ex.getCause();
        }
    }
}
