package com.bergerkiller.reflection;

public interface Invokable {
    /**
     * Invokes the method
     *
     *
     * @param instance of the class the method is in, use null if it is a static method
     * @param args to use for the method
     * @return A possible returned value from the method, is always null if the
     * method is a void
     */
    Object invoke(Object instance, Object... args);
}
