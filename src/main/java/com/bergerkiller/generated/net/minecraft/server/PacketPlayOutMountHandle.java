package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutMount</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.Optional
public class PacketPlayOutMountHandle extends PacketHandle {
    /** @See {@link PacketPlayOutMountClass} */
    public static final PacketPlayOutMountClass T = new PacketPlayOutMountClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayOutMountHandle.class, "net.minecraft.server.PacketPlayOutMount");

    /* ============================================================================== */

    public static PacketPlayOutMountHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        PacketPlayOutMountHandle handle = new PacketPlayOutMountHandle();
        handle.instance = handleInstance;
        return handle;
    }

    public static final PacketPlayOutMountHandle createNew() {
        return T.constr.newInstance();
    }

    /* ============================================================================== */


    public static PacketPlayOutMountHandle createNew(int entityId, int[] mountedEntityIds) {
        PacketPlayOutMountHandle handle = createNew();
        handle.setEntityId(entityId);
        handle.setMountedEntityIds(mountedEntityIds);
        return handle;
    }
    public int getEntityId() {
        return T.entityId.getInteger(instance);
    }

    public void setEntityId(int value) {
        T.entityId.setInteger(instance, value);
    }

    public int[] getMountedEntityIds() {
        return T.mountedEntityIds.get(instance);
    }

    public void setMountedEntityIds(int[] value) {
        T.mountedEntityIds.set(instance, value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayOutMount</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutMountClass extends Template.Class<PacketPlayOutMountHandle> {
        public final Template.Constructor.Converted<PacketPlayOutMountHandle> constr = new Template.Constructor.Converted<PacketPlayOutMountHandle>();

        public final Template.Field.Integer entityId = new Template.Field.Integer();
        public final Template.Field<int[]> mountedEntityIds = new Template.Field<int[]>();

    }

}

