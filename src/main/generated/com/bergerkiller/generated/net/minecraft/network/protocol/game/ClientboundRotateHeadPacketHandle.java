package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import org.bukkit.entity.Entity;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ClientboundRotateHeadPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ClientboundRotateHeadPacket")
public abstract class ClientboundRotateHeadPacketHandle extends PacketHandle {
    /** @see ClientboundRotateHeadPacketClass */
    public static final ClientboundRotateHeadPacketClass T = Template.Class.create(ClientboundRotateHeadPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ClientboundRotateHeadPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static ClientboundRotateHeadPacketHandle createNew(Entity entity, float headYaw) {
        return T.createNew.invoke(entity, headYaw);
    }

    public abstract float getHeadYaw();
    public abstract void setHeadYaw(float headYaw);
    public static ClientboundRotateHeadPacketHandle createNew() {
        return T.createNewEmpty.invoke();
    }

    public static ClientboundRotateHeadPacketHandle createNew(int entityId, float headYaw) {
        ClientboundRotateHeadPacketHandle packet = createNew();
        packet.setEntityId(entityId);
        packet.setHeadYaw(headYaw);
        return packet;
    }
    public abstract int getEntityId();
    public abstract void setEntityId(int value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundRotateHeadPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ClientboundRotateHeadPacketClass extends Template.Class<ClientboundRotateHeadPacketHandle> {
        public final Template.Field.Integer entityId = new Template.Field.Integer();

        @Template.Optional
        public final Template.StaticMethod.Converted<ClientboundRotateHeadPacketHandle> createNewEmpty = new Template.StaticMethod.Converted<ClientboundRotateHeadPacketHandle>();
        public final Template.StaticMethod.Converted<ClientboundRotateHeadPacketHandle> createNew = new Template.StaticMethod.Converted<ClientboundRotateHeadPacketHandle>();

        public final Template.Method<Float> getHeadYaw = new Template.Method<Float>();
        public final Template.Method<Void> setHeadYaw = new Template.Method<Void>();

    }

}

