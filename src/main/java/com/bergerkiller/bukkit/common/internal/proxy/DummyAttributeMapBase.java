package com.bergerkiller.bukkit.common.internal.proxy;

import java.lang.reflect.Method;

import com.bergerkiller.generated.net.minecraft.world.entity.ai.attributes.AttributeMapBaseHandle;
import com.bergerkiller.mountiplex.reflection.ClassInterceptor;
import com.bergerkiller.mountiplex.reflection.util.fast.Invoker;
import com.bergerkiller.mountiplex.reflection.util.fast.NullInvoker;

import javassist.Modifier;

/**
 * Helper class stores an instance of AttributeMapBase which is completely unusable.
 */
public class DummyAttributeMapBase {
    public static final Object INSTANCE;

    static {
        // Create an extended class of AttributeMapBase (because the class is abstract)
        // Make sure to implement all abstract methods and return some default null value
        ClassInterceptor interceptor = new ClassInterceptor() {
            @Override
            @SuppressWarnings("unchecked")
            protected Invoker<?> getCallback(Method method) {
                if (Modifier.isAbstract(method.getModifiers())) {
                    return new NullInvoker<Object>((Class<Object>) method.getReturnType());
                } else {
                    return null;
                }
            }
        };
        INSTANCE = interceptor.createInstance(AttributeMapBaseHandle.T.getType());
    }
}
