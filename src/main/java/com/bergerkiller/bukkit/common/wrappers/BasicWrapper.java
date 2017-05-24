package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * A basic implementation for a wrapper class
 */
public class BasicWrapper<T extends Template.Handle> {

    protected T handle;

    /**
     * Sets the internal handle for this wrapper<br>
     * This handle can not be null
     *
     * @param handle to set to
     */
    protected void setHandle(T handle) {
        if (handle == null) {
            throw new IllegalArgumentException("The handle can not be null");
        }
        this.handle = handle;
    }

    /**
     * Gets the raw internal handle from this wrapper
     *
     * @return handle
     */
    public Object getRawHandle() {
        return handle.getRaw();
    }

    /**
     * Gets the internal handle from this wrapper and casts it to the type
     * specified. If no handle is contained or the conversion failed, NULL is
     * returned instead.
     *
     * @param type to cast to
     * @return the handle cast to the type, or NULL if no handle or casting
     * fails
     */
    public <H> H getRawHandle(Class<H> type) {
        return CommonUtil.tryCast(handle.getRaw(), type);
    }

    /**
     * <b>Deprecated: </b>use {@link #getRawHandle()} instead
     */
    @Deprecated
    public Object getHandle() {
        return handle.getRaw();
    }

    /**
     * <b>Deprecated: </b>use {@link #getRawHandle()} instead
     */
    @Deprecated
    public <H> H getHandle(Class<H> type) {
        return CommonUtil.tryCast(handle.getRaw(), type);
    }

    @Override
    public String toString() {
        return handle.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof BasicWrapper) {
            o = ((BasicWrapper<?>) o).getRawHandle();
        }
        return handle != null && handle.getRaw().equals(o);
    }
}
