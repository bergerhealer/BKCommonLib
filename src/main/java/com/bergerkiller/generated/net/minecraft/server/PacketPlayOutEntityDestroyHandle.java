package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutEntityDestroy</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.server.PacketPlayOutEntityDestroy")
public abstract class PacketPlayOutEntityDestroyHandle extends PacketHandle {
    /** @See {@link PacketPlayOutEntityDestroyClass} */
    public static final PacketPlayOutEntityDestroyClass T = Template.Class.create(PacketPlayOutEntityDestroyClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutEntityDestroyHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final PacketPlayOutEntityDestroyHandle createNew(int[] entityIds) {
        return T.constr_entityIds.newInstance(entityIds);
    }

    /* ============================================================================== */

    public abstract int[] getEntityIds();
    public abstract void setEntityIds(int[] value);
    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayOutEntityDestroy</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutEntityDestroyClass extends Template.Class<PacketPlayOutEntityDestroyHandle> {
        public final Template.Constructor.Converted<PacketPlayOutEntityDestroyHandle> constr_entityIds = new Template.Constructor.Converted<PacketPlayOutEntityDestroyHandle>();

        public final Template.Field<int[]> entityIds = new Template.Field<int[]>();

    }

}

