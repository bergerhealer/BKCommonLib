package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutKeepAlive</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class PacketPlayOutKeepAliveHandle extends PacketHandle {
    /** @See {@link PacketPlayOutKeepAliveClass} */
    public static final PacketPlayOutKeepAliveClass T = new PacketPlayOutKeepAliveClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayOutKeepAliveHandle.class, "net.minecraft.server.PacketPlayOutKeepAlive");

    /* ============================================================================== */

    public static PacketPlayOutKeepAliveHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        PacketPlayOutKeepAliveHandle handle = new PacketPlayOutKeepAliveHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public Long getKey() {
        return T.key.get(instance);
    }

    public void setKey(Long value) {
        T.key.set(instance, value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayOutKeepAlive</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutKeepAliveClass extends Template.Class<PacketPlayOutKeepAliveHandle> {
        public final Template.Field.Converted<Long> key = new Template.Field.Converted<Long>();

    }

}

