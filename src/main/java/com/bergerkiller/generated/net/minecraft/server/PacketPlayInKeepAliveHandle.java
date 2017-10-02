package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayInKeepAlive</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class PacketPlayInKeepAliveHandle extends PacketHandle {
    /** @See {@link PacketPlayInKeepAliveClass} */
    public static final PacketPlayInKeepAliveClass T = new PacketPlayInKeepAliveClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayInKeepAliveHandle.class, "net.minecraft.server.PacketPlayInKeepAlive");

    /* ============================================================================== */

    public static PacketPlayInKeepAliveHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract Long getKey();
    public abstract void setKey(Long value);
    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayInKeepAlive</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayInKeepAliveClass extends Template.Class<PacketPlayInKeepAliveHandle> {
        public final Template.Field.Converted<Long> key = new Template.Field.Converted<Long>();

    }

}

