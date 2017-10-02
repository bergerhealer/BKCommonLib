package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutAttachEntity</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class PacketPlayOutAttachEntityHandle extends PacketHandle {
    /** @See {@link PacketPlayOutAttachEntityClass} */
    public static final PacketPlayOutAttachEntityClass T = new PacketPlayOutAttachEntityClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayOutAttachEntityHandle.class, "net.minecraft.server.PacketPlayOutAttachEntity");

    /* ============================================================================== */

    public static PacketPlayOutAttachEntityHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */


    public static PacketPlayOutAttachEntityHandle createNew(org.bukkit.entity.Entity passengerEntity, org.bukkit.entity.Entity vehicleEntity) {
        if (T.constr_passengerEntity_vehicleEntity.isAvailable()) {
            return T.constr_passengerEntity_vehicleEntity.newInstance(passengerEntity, vehicleEntity);
        } else {
            return T.constr_leashId_passengerEntity_vehicleEntity.newInstance(0, passengerEntity, vehicleEntity);
        }
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
        public final Template.Constructor.Converted<PacketPlayOutAttachEntityHandle> constr_leashId_passengerEntity_vehicleEntity = new Template.Constructor.Converted<PacketPlayOutAttachEntityHandle>();
        @Template.Optional
        public final Template.Constructor.Converted<PacketPlayOutAttachEntityHandle> constr_passengerEntity_vehicleEntity = new Template.Constructor.Converted<PacketPlayOutAttachEntityHandle>();

        @Template.Optional
        public final Template.Field.Integer leashId = new Template.Field.Integer();
        public final Template.Field.Integer passengerId = new Template.Field.Integer();
        public final Template.Field.Integer vehicleId = new Template.Field.Integer();

    }

}

