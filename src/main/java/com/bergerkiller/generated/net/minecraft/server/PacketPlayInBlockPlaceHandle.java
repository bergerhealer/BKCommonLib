package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayInBlockPlace</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class PacketPlayInBlockPlaceHandle extends Template.Handle {
    /** @See {@link PacketPlayInBlockPlaceClass} */
    public static final PacketPlayInBlockPlaceClass T = new PacketPlayInBlockPlaceClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayInBlockPlaceHandle.class, "net.minecraft.server.PacketPlayInBlockPlace");

    /* ============================================================================== */

    public static PacketPlayInBlockPlaceHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        PacketPlayInBlockPlaceHandle handle = new PacketPlayInBlockPlaceHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public long getTimestamp() {
        return T.timestamp.getLong(instance);
    }

    public void setTimestamp(long value) {
        T.timestamp.setLong(instance, value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayInBlockPlace</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayInBlockPlaceClass extends Template.Class<PacketPlayInBlockPlaceHandle> {
        @Template.Optional
        public final Template.Field.Converted<Object> enumHand = new Template.Field.Converted<Object>();
        public final Template.Field.Long timestamp = new Template.Field.Long();

    }

}

