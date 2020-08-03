package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayInTeleportAccept</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
@Template.InstanceType("net.minecraft.server.PacketPlayInTeleportAccept")
public abstract class PacketPlayInTeleportAcceptHandle extends PacketHandle {
    /** @See {@link PacketPlayInTeleportAcceptClass} */
    public static final PacketPlayInTeleportAcceptClass T = Template.Class.create(PacketPlayInTeleportAcceptClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayInTeleportAcceptHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getTeleportId();
    public abstract void setTeleportId(int value);
    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayInTeleportAccept</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayInTeleportAcceptClass extends Template.Class<PacketPlayInTeleportAcceptHandle> {
        public final Template.Field.Integer teleportId = new Template.Field.Integer();

    }

}

