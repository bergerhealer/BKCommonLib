package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayInBlockPlace</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class PacketPlayInBlockPlaceHandle extends PacketHandle {
    /** @See {@link PacketPlayInBlockPlaceClass} */
    public static final PacketPlayInBlockPlaceClass T = new PacketPlayInBlockPlaceClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayInBlockPlaceHandle.class, "net.minecraft.server.PacketPlayInBlockPlace");

    /* ============================================================================== */

    public static PacketPlayInBlockPlaceHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayInBlockPlace</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayInBlockPlaceClass extends Template.Class<PacketPlayInBlockPlaceHandle> {
        @Template.Optional
        public final Template.Field.Converted<Object> enumHand = new Template.Field.Converted<Object>();
        @Template.Optional
        public final Template.Field.Long timestamp = new Template.Field.Long();

    }

}

