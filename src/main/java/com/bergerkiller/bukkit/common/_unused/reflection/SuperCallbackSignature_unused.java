package com.bergerkiller.bukkit.common._unused.reflection;

import net.sf.cglib.core.Signature;
import net.sf.cglib.proxy.MethodProxy;

import java.util.Map;


public class SuperCallbackSignature_unused implements CallbackSignature_unused {

    private final Signature superSignature;
    private CallbackMethod_unused callbackInstance;

    public SuperCallbackSignature_unused(Signature superSignature) {
        this.superSignature = superSignature;
    }

    @Override
    public CallbackMethod_unused createCallback(Object instance, Map<Class<?>, Object> callbackInstances) {
        if (callbackInstance == null) {
            callbackInstance = new SuperCallbackMethod_unused(MethodProxy.find(instance.getClass(), superSignature));
        }
        return callbackInstance;
    }
}
