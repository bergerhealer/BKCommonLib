package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutAttachEntity</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class PacketPlayOutAttachEntityHandle extends PacketHandle {
    /** @See {@link PacketPlayOutAttachEntityClass} */
    public static final PacketPlayOutAttachEntityClass T = new PacketPlayOutAttachEntityClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayOutAttachEntityHandle.class, "net.minecraft.server.PacketPlayOutAttachEntity");

    /* ============================================================================== */

    public static PacketPlayOutAttachEntityHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        PacketPlayOutAttachEntityHandle handle = new PacketPlayOutAttachEntityHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */


    public static PacketPlayOutAttachEntityHandle createNew(org.bukkit.entity.Entity passengerEntity, org.bukkit.entity.Entity vehicleEntity) {
        if (T.constr_passengerEntity_vehicleEntity.isAvailable()) {
            return T.constr_passengerEntity_vehicleEntity.newInstance(passengerEntity, vehicleEntity);
        } else {
            return T.constr_leashId_passengerEntity_vehicleEntity.newInstance(0, passengerEntity, vehicleEntity);
        }
    }
    public int getPassengerId() {
        return T.passengerId.getInteger(instance);
    }

    public void setPassengerId(int value) {
        T.passengerId.setInteger(instance, value);
    }

    public int getVehicleId() {
        return T.vehicleId.getInteger(instance);
    }

    public void setVehicleId(int value) {
        T.vehicleId.setInteger(instance, value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayOutAttachEntity</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutAttachEntityClass extends Template.Class<PacketPlayOutAttachEntityHandle> {
        @Template.Optional
        public final Template.Constructor.Converted<PacketPlayOutAttachEntityHandle> constr_leashId_passengerEntity_vehicleEntity = new Template.Constructor.Converted<PacketPlayOutAttachEntityHandle>();
        @Template.Optional
        public final Template.Constructor.Converted<PacketPlayOutAttachEntityHandle> constr_passengerEntity_vehicleEntity = new Template.Constructor.Converted<PacketPlayOutAttachEntityHandle>();

        @Template.Optional
        public final Template.Field.Integer leashId = new Template.Field.Integer();
        public final Template.Field.Integer passengerId = new Template.Field.Integer();
        public final Template.Field.Integer vehicleId = new Template.Field.Integer();

    }

}

