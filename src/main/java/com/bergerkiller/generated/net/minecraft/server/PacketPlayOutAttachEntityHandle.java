package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.entity.Entity;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutAttachEntity</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class PacketPlayOutAttachEntityHandle extends PacketHandle {
    /** @See {@link PacketPlayOutAttachEntityClass} */
    public static final PacketPlayOutAttachEntityClass T = new PacketPlayOutAttachEntityClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayOutAttachEntityHandle.class, "net.minecraft.server.PacketPlayOutAttachEntity", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static PacketPlayOutAttachEntityHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static PacketPlayOutAttachEntityHandle createNewMount(Entity passengerEntity, Entity vehicleEntity) {
        return T.createNewMount.invoke(passengerEntity, vehicleEntity);
    }

    public static PacketPlayOutAttachEntityHandle createNewLeash(Entity leashedEntity, Entity holderEntity) {
        return T.createNewLeash.invoke(leashedEntity, holderEntity);
    }

    public abstract boolean isLeash();
    public abstract void setIsLeash(boolean isLeash);

    public static PacketPlayOutAttachEntityHandle createNewLeash(int leashedEntityId, int holderEntityId) {
        PacketPlayOutAttachEntityHandle packet = T.newHandleNull();
        packet.setVehicleId(holderEntityId);
        packet.setPassengerId(leashedEntityId);
        packet.setIsLeash(true);
        return packet;
    }

    public static PacketPlayOutAttachEntityHandle createNewMount(int passengerEntityId, int vehicleEntityId) {
        if (!T.leashId.isAvailable()) {
            throw new UnsupportedOperationException("Not supported >= MC 1.9, use Mount packet instead");
        }
        PacketPlayOutAttachEntityHandle packet = T.newHandleNull();
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
     * Stores class members for <b>net.minecraft.server.PacketPlayOutAttachEntity</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutAttachEntityClass extends Template.Class<PacketPlayOutAttachEntityHandle> {
        @Template.Optional
        public final Template.Field.Integer leashId = new Template.Field.Integer();
        public final Template.Field.Integer passengerId = new Template.Field.Integer();
        public final Template.Field.Integer vehicleId = new Template.Field.Integer();

        public final Template.StaticMethod.Converted<PacketPlayOutAttachEntityHandle> createNewMount = new Template.StaticMethod.Converted<PacketPlayOutAttachEntityHandle>();
        public final Template.StaticMethod.Converted<PacketPlayOutAttachEntityHandle> createNewLeash = new Template.StaticMethod.Converted<PacketPlayOutAttachEntityHandle>();

        public final Template.Method<Boolean> isLeash = new Template.Method<Boolean>();
        public final Template.Method<Void> setIsLeash = new Template.Method<Void>();

    }

}

