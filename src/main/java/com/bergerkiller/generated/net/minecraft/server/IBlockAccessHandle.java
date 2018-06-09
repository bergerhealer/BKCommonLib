package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.IBlockAccess</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class IBlockAccessHandle extends Template.Handle {
    /** @See {@link IBlockAccessClass} */
    public static final IBlockAccessClass T = new IBlockAccessClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(IBlockAccessHandle.class, "net.minecraft.server.IBlockAccess");

    /* ============================================================================== */

    public static IBlockAccessHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.server.IBlockAccess</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class IBlockAccessClass extends Template.Class<IBlockAccessHandle> {
    }

}

