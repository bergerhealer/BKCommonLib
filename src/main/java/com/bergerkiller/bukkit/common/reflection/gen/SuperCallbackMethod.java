package com.bergerkiller.bukkit.common.reflection.gen;

import net.sf.cglib.proxy.MethodProxy;

public class SuperCallbackMethod implements CallbackMethod {

    private final MethodProxy superMethodProxy;

    public SuperCallbackMethod(MethodProxy superMethodProxy) {
        this.superMethodProxy = superMethodProxy;
    }

    @Override
    public Object invoke(Object instance, Object[] args) throws Throwable {
        return superMethodProxy.invokeSuper(instance, args);
    }
}
