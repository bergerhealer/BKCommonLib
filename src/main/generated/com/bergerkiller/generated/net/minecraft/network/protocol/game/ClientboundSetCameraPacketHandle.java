package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ClientboundSetCameraPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ClientboundSetCameraPacket")
public abstract class ClientboundSetCameraPacketHandle extends PacketHandle {
    /** @see ClientboundSetCameraPacketClass */
    public static final ClientboundSetCameraPacketClass T = Template.Class.create(ClientboundSetCameraPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ClientboundSetCameraPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static ClientboundSetCameraPacketHandle createNew() {
        return T.createNew.invoke();
    }

    public static ClientboundSetCameraPacketHandle createNew(int entityId) {
        ClientboundSetCameraPacketHandle packet = createNew();
        packet.setEntityId(entityId);
        return packet;
    }
    public abstract int getEntityId();
    public abstract void setEntityId(int value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundSetCameraPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ClientboundSetCameraPacketClass extends Template.Class<ClientboundSetCameraPacketHandle> {
        public final Template.Field.Integer entityId = new Template.Field.Integer();

        public final Template.StaticMethod.Converted<ClientboundSetCameraPacketHandle> createNew = new Template.StaticMethod.Converted<ClientboundSetCameraPacketHandle>();

    }

}

