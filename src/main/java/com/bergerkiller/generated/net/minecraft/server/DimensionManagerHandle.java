package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.DimensionManager</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
public abstract class DimensionManagerHandle extends Template.Handle {
    /** @See {@link DimensionManagerClass} */
    public static final DimensionManagerClass T = new DimensionManagerClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(DimensionManagerHandle.class, "net.minecraft.server.DimensionManager", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static DimensionManagerHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static Object fromId(int i) {
        return T.fromId.invoke(i);
    }

    public abstract int getId();
    /**
     * Stores class members for <b>net.minecraft.server.DimensionManager</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class DimensionManagerClass extends Template.Class<DimensionManagerHandle> {
        public final Template.StaticMethod<Object> fromId = new Template.StaticMethod<Object>();

        public final Template.Method<Integer> getId = new Template.Method<Integer>();

    }

}

