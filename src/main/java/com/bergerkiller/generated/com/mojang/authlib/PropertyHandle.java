package com.bergerkiller.generated.com.mojang.authlib;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>properties.Property</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class PropertyHandle extends Template.Handle {
    /** @See {@link PropertyClass} */
    public static final PropertyClass T = new PropertyClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PropertyHandle.class, "properties.Property");

    /* ============================================================================== */

    public static PropertyHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        PropertyHandle handle = new PropertyHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>properties.Property</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PropertyClass extends Template.Class<PropertyHandle> {
    }

}

