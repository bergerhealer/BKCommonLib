package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.DataWatcherObject</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class DataWatcherObjectHandle extends Template.Handle {
    /** @See {@link DataWatcherObjectClass} */
    public static final DataWatcherObjectClass T = new DataWatcherObjectClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(DataWatcherObjectHandle.class, "net.minecraft.server.DataWatcherObject", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static DataWatcherObjectHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getId();
    public abstract Object getSerializer();
    /**
     * Stores class members for <b>net.minecraft.server.DataWatcherObject</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class DataWatcherObjectClass extends Template.Class<DataWatcherObjectHandle> {
        @Template.Optional
        public final Template.Constructor.Converted<DataWatcherObjectHandle> constr_index = new Template.Constructor.Converted<DataWatcherObjectHandle>();

        public final Template.Method<Integer> getId = new Template.Method<Integer>();
        public final Template.Method.Converted<Object> getSerializer = new Template.Method.Converted<Object>();

    }

}

