package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutKeepAlive</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class PacketPlayOutKeepAliveHandle extends PacketHandle {
    /** @See {@link PacketPlayOutKeepAliveClass} */
    public static final PacketPlayOutKeepAliveClass T = new PacketPlayOutKeepAliveClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayOutKeepAliveHandle.class, "net.minecraft.server.PacketPlayOutKeepAlive", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static PacketPlayOutKeepAliveHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract Long getKey();
    public abstract void setKey(Long value);
    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayOutKeepAlive</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutKeepAliveClass extends Template.Class<PacketPlayOutKeepAliveHandle> {
        public final Template.Field.Converted<Long> key = new Template.Field.Converted<Long>();

    }

}

