package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayInTeleportAccept</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
public class PacketPlayInTeleportAcceptHandle extends PacketHandle {
    /** @See {@link PacketPlayInTeleportAcceptClass} */
    public static final PacketPlayInTeleportAcceptClass T = new PacketPlayInTeleportAcceptClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayInTeleportAcceptHandle.class, "net.minecraft.server.PacketPlayInTeleportAccept");

    /* ============================================================================== */

    public static PacketPlayInTeleportAcceptHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        PacketPlayInTeleportAcceptHandle handle = new PacketPlayInTeleportAcceptHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public int getTeleportId() {
        return T.teleportId.getInteger(instance);
    }

    public void setTeleportId(int value) {
        T.teleportId.setInteger(instance, value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayInTeleportAccept</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayInTeleportAcceptClass extends Template.Class<PacketPlayInTeleportAcceptHandle> {
        public final Template.Field.Integer teleportId = new Template.Field.Integer();

    }

}

