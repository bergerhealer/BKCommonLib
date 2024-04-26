package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

import java.util.Optional;
import java.util.function.Function;

/**
 * Holds a value that is part of an internal registry. On old versions of Minecraft,
 * this simply wraps the actual value being stored.
 *
 * @param <T> Value Type (Handle)
 */
public interface Holder<T> {
    /**
     * Value of this holder
     *
     * @return Value
     */
    T value();

    /**
     * Gets the raw value. Compared to {@link #value()} which can store a wrapper or
     * handle, this returns the true internal value.
     *
     * @return Raw NMS Value
     */
    Object rawValue();

    /**
     * Gets the resource key identifier of this value, if this holder references
     * a value inside a registry mapped to a particular key. Returns empty if
     * it does not reference one, or there is no such registry.
     *
     * @return Resource Key, or Empty if known not in a registry
     */
    Optional<ResourceKey<T>> key();

    /**
     * Retrieves the raw, NMS Holder instance that this wrapper Holder wraps
     *
     * @return Raw Holder instance
     */
    Object toRawHolder();

    /**
     * Wraps a value, without referencing it in any sort of registry
     *
     * @param handleInstance Handle instance wrapping the true NMS value
     * @return Holder storing the value
     * @param <T> Handle Value Type
     */
    static <T extends Template.Handle> Holder<T> direct(T handleInstance) {
        return HolderImpl.direct(handleInstance);
    }

    /**
     * Wraps a value, without referencing it in any sort of registry. The handle wrapper value
     * is created using the constructor method, which is only called once and if needed.
     *
     * @param rawValue Internal raw NMS value represented
     * @param handleCtor Function to creates an API-facing wrapper object of the internal NMS value
     * @param <T> Handle Value Type
     */
    static <T extends Template.Handle> Holder<T> directWrap(Object rawValue, Function<Object, T> handleCtor) {
        return HolderImpl.directWrap(rawValue, handleCtor);
    }

    /**
     * Wraps a NMS Holder value, as well as providing the method to turn the internal NMS value
     * the holder holds into a wrapper type. This is only called, once, if this is actually
     * requested by the user.
     *
     * @param rawHolder NMS Holder value
     * @param handleCtor Function to creates an API-facing wrapper object of the internal NMS value
     * @return Holder representing the NMS raw holder value
     * @param <T> API (Handle) Value Type
     */
    static <T> Holder<T> fromHandle(Object rawHolder, Function<Object, T> handleCtor) {
        return new HolderImpl<T>(rawHolder, handleCtor);
    }
}
