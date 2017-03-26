package com.bergerkiller.reflection;

import java.lang.reflect.Method;

/**
 * Defines the methods to access a certain method
 */
public interface MethodAccessor<T> extends Invokable {

    /**
     * Checks if this MethodAccessor performs the functions of a certain reflection Method.
     * Methods without reflection backed by them will always return False here.
     * 
     * @param method to compare
     * @return True if the method matches this one, False if not
     */
    boolean isMethod(Method method);

    /**
     * Checks whether this Method accessor is in a valid state<br>
     * Only if this return true can this safe accessor be used without problems
     *
     * @return True if this accessor is valid, False if not
     */
    boolean isValid();

    /**
     * Executes the method
     *
     * @param instance of the class the method is in, use null if it is a static
     * method
     * @param args to use for the method
     * @return A possible returned value from the method, is always null if the
     * method is a void
     */
    T invoke(Object instance, Object... args);
}
