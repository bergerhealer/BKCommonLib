package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutEntityDestroy</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class PacketPlayOutEntityDestroyHandle extends PacketHandle {
    /** @See {@link PacketPlayOutEntityDestroyClass} */
    public static final PacketPlayOutEntityDestroyClass T = new PacketPlayOutEntityDestroyClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayOutEntityDestroyHandle.class, "net.minecraft.server.PacketPlayOutEntityDestroy");

    /* ============================================================================== */

    public static PacketPlayOutEntityDestroyHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        PacketPlayOutEntityDestroyHandle handle = new PacketPlayOutEntityDestroyHandle();
        handle.instance = handleInstance;
        return handle;
    }

    public static final PacketPlayOutEntityDestroyHandle createNew(int[] entityIds) {
        return T.constr_entityIds.newInstance(entityIds);
    }

    /* ============================================================================== */

    public int[] getEntityIds() {
        return T.entityIds.get(instance);
    }

    public void setEntityIds(int[] value) {
        T.entityIds.set(instance, value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayOutEntityDestroy</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutEntityDestroyClass extends Template.Class<PacketPlayOutEntityDestroyHandle> {
        public final Template.Constructor.Converted<PacketPlayOutEntityDestroyHandle> constr_entityIds = new Template.Constructor.Converted<PacketPlayOutEntityDestroyHandle>();

        public final Template.Field<int[]> entityIds = new Template.Field<int[]>();

    }

}

