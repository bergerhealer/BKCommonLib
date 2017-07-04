package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.DataWatcherRegistry</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
public class DataWatcherRegistryHandle extends Template.Handle {
    /** @See {@link DataWatcherRegistryClass} */
    public static final DataWatcherRegistryClass T = new DataWatcherRegistryClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(DataWatcherRegistryHandle.class, "net.minecraft.server.DataWatcherRegistry");

    /* ============================================================================== */

    public static DataWatcherRegistryHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        DataWatcherRegistryHandle handle = new DataWatcherRegistryHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static int getSerializerId(Object paramDataWatcherSerializer) {
        return T.getSerializerId.invoke(paramDataWatcherSerializer);
    }

    /**
     * Stores class members for <b>net.minecraft.server.DataWatcherRegistry</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class DataWatcherRegistryClass extends Template.Class<DataWatcherRegistryHandle> {
        public final Template.StaticMethod.Converted<Integer> getSerializerId = new Template.StaticMethod.Converted<Integer>();

    }

}

