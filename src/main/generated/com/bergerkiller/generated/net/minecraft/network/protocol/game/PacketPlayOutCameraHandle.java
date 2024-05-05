package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutCamera</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutCamera")
public abstract class PacketPlayOutCameraHandle extends PacketHandle {
    /** @see PacketPlayOutCameraClass */
    public static final PacketPlayOutCameraClass T = Template.Class.create(PacketPlayOutCameraClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutCameraHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static PacketPlayOutCameraHandle createNew() {
        return T.createNew.invoke();
    }

    public static PacketPlayOutCameraHandle createNew(int entityId) {
        PacketPlayOutCameraHandle packet = createNew();
        packet.setEntityId(entityId);
        return packet;
    }
    public abstract int getEntityId();
    public abstract void setEntityId(int value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutCamera</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutCameraClass extends Template.Class<PacketPlayOutCameraHandle> {
        public final Template.Field.Integer entityId = new Template.Field.Integer();

        public final Template.StaticMethod.Converted<PacketPlayOutCameraHandle> createNew = new Template.StaticMethod.Converted<PacketPlayOutCameraHandle>();

    }

}

