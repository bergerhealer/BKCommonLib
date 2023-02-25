package com.bergerkiller.generated.net.minecraft.network.syncher;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.syncher.DataWatcherObject</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.syncher.DataWatcherObject")
public abstract class DataWatcherObjectHandle extends Template.Handle {
    /** @see DataWatcherObjectClass */
    public static final DataWatcherObjectClass T = Template.Class.create(DataWatcherObjectClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static DataWatcherObjectHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getId();
    public abstract Object getSerializer();
    /**
     * Stores class members for <b>net.minecraft.network.syncher.DataWatcherObject</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class DataWatcherObjectClass extends Template.Class<DataWatcherObjectHandle> {
        public final Template.Method<Integer> getId = new Template.Method<Integer>();
        public final Template.Method<Object> getSerializer = new Template.Method<Object>();

    }

}

