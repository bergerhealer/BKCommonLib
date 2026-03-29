package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import org.bukkit.entity.Entity;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket")
public abstract class ClientboundSetEntityLinkPacketHandle extends PacketHandle {
    /** @see ClientboundSetEntityLinkPacketClass */
    public static final ClientboundSetEntityLinkPacketClass T = Template.Class.create(ClientboundSetEntityLinkPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ClientboundSetEntityLinkPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static ClientboundSetEntityLinkPacketHandle createNew() {
        return T.createNew.invoke();
    }

    public static ClientboundSetEntityLinkPacketHandle createNewMount(Entity passengerEntity, Entity vehicleEntity) {
        return T.createNewMount.invoke(passengerEntity, vehicleEntity);
    }

    public static ClientboundSetEntityLinkPacketHandle createNewLeash(Entity leashedEntity, Entity holderEntity) {
        return T.createNewLeash.invoke(leashedEntity, holderEntity);
    }

    public abstract boolean isLeash();
    public abstract void setIsLeash(boolean isLeash);
    public static ClientboundSetEntityLinkPacketHandle createNewLeash(int leashedEntityId, int holderEntityId) {
        ClientboundSetEntityLinkPacketHandle packet = createNew();
        packet.setVehicleId(holderEntityId);
        packet.setPassengerId(leashedEntityId);
        packet.setIsLeash(true);
        return packet;
    }

    public static ClientboundSetEntityLinkPacketHandle createNewMount(int passengerEntityId, int vehicleEntityId) {
        if (!T.leashId.isAvailable()) {
            throw new UnsupportedOperationException("Not supported >= MC 1.9, use Mount packet instead");
        }
        ClientboundSetEntityLinkPacketHandle packet = createNew();
        packet.setVehicleId(vehicleEntityId);
        packet.setPassengerId(passengerEntityId);
        packet.setIsLeash(false);
        return packet;
    }
    public abstract int getPassengerId();
    public abstract void setPassengerId(int value);
    public abstract int getVehicleId();
    public abstract void setVehicleId(int value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ClientboundSetEntityLinkPacketClass extends Template.Class<ClientboundSetEntityLinkPacketHandle> {
        @Template.Optional
        public final Template.Field.Integer leashId = new Template.Field.Integer();
        public final Template.Field.Integer passengerId = new Template.Field.Integer();
        public final Template.Field.Integer vehicleId = new Template.Field.Integer();

        public final Template.StaticMethod.Converted<ClientboundSetEntityLinkPacketHandle> createNew = new Template.StaticMethod.Converted<ClientboundSetEntityLinkPacketHandle>();
        public final Template.StaticMethod.Converted<ClientboundSetEntityLinkPacketHandle> createNewMount = new Template.StaticMethod.Converted<ClientboundSetEntityLinkPacketHandle>();
        public final Template.StaticMethod.Converted<ClientboundSetEntityLinkPacketHandle> createNewLeash = new Template.StaticMethod.Converted<ClientboundSetEntityLinkPacketHandle>();

        public final Template.Method<Boolean> isLeash = new Template.Method<Boolean>();
        public final Template.Method<Void> setIsLeash = new Template.Method<Void>();

    }

}

