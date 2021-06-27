package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutCamera</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutCamera")
public abstract class PacketPlayOutCameraHandle extends Template.Handle {
    /** @See {@link PacketPlayOutCameraClass} */
    public static final PacketPlayOutCameraClass T = Template.Class.create(PacketPlayOutCameraClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutCameraHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getEntityId();
    public abstract void setEntityId(int value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutCamera</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutCameraClass extends Template.Class<PacketPlayOutCameraHandle> {
        public final Template.Field.Integer entityId = new Template.Field.Integer();

    }

}

