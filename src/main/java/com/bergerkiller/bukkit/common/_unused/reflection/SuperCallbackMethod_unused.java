package com.bergerkiller.bukkit.common._unused.reflection;


import net.sf.cglib.proxy.MethodProxy;

public class SuperCallbackMethod_unused implements CallbackMethod_unused {

    private final MethodProxy superMethodProxy;

    public SuperCallbackMethod_unused(MethodProxy superMethodProxy) {
        this.superMethodProxy = superMethodProxy;
    }

    @Override
    public Object invoke(Object instance, Object[] args) throws Throwable {
        return superMethodProxy.invokeSuper(instance, args);
    }
}
