package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.IBlockState</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class IBlockStateHandle extends Template.Handle {
    /** @See {@link IBlockStateClass} */
    public static final IBlockStateClass T = new IBlockStateClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(IBlockStateHandle.class, "net.minecraft.server.IBlockState", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static IBlockStateHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract String getKeyToken();
    public abstract String getValueToken(Comparable value);
    /**
     * Stores class members for <b>net.minecraft.server.IBlockState</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class IBlockStateClass extends Template.Class<IBlockStateHandle> {
        public final Template.Method<String> getKeyToken = new Template.Method<String>();
        public final Template.Method.Converted<String> getValueToken = new Template.Method.Converted<String>();

    }

}

