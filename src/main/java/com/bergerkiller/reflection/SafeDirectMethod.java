package com.bergerkiller.reflection;

import java.lang.reflect.Method;

/**
 * A method implementation that allows direct invoking
 */
public abstract class SafeDirectMethod<T> implements MethodAccessor<T> {

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public boolean isMethod(Method method) {
        return false;
    }
}
