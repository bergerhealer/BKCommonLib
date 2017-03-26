package com.bergerkiller.bukkit.common._unused.reflection;

import java.util.Map;

/**
 * Represents the signature of a particular {@link CallbackMethod_unused}. This
 * signature can be used to construct a (new) Callback for a particular
 * instance.
 */
public interface CallbackSignature_unused {

    /**
     * Creates a (new) Callback suitable for the instance specified
     *
     * @param instance to get a Callback for
     * @param callbackInstances that are active
     * @return (new) Callback instance
     */
    public CallbackMethod_unused createCallback(Object instance, Map<Class<?>, Object> callbackInstances);
}
