package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.IDataManager</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class IDataManagerHandle extends Template.Handle {
    /** @See {@link IDataManagerClass} */
    public static final IDataManagerClass T = new IDataManagerClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(IDataManagerHandle.class, "net.minecraft.server.IDataManager", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static IDataManagerHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.server.IDataManager</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class IDataManagerClass extends Template.Class<IDataManagerHandle> {
    }

}

