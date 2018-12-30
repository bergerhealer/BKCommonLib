package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.IWorldAccess</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class IWorldAccessHandle extends Template.Handle {
    /** @See {@link IWorldAccessClass} */
    public static final IWorldAccessClass T = new IWorldAccessClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(IWorldAccessHandle.class, "net.minecraft.server.IWorldAccess", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static IWorldAccessHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract void onEntityAdded(EntityHandle entity);
    public abstract void onEntityRemoved(EntityHandle entity);
    /**
     * Stores class members for <b>net.minecraft.server.IWorldAccess</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class IWorldAccessClass extends Template.Class<IWorldAccessHandle> {
        public final Template.Method.Converted<Void> onEntityAdded = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Void> onEntityRemoved = new Template.Method.Converted<Void>();

    }

}

