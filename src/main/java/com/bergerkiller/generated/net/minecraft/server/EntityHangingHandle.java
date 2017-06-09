package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.EntityHanging</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class EntityHangingHandle extends EntityHandle {
    /** @See {@link EntityHangingClass} */
    public static final EntityHangingClass T = new EntityHangingClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EntityHangingHandle.class, "net.minecraft.server.EntityHanging");

    /* ============================================================================== */

    public static EntityHangingHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        EntityHangingHandle handle = new EntityHangingHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public IntVector3 getBlockPosition() {
        return T.blockPosition.get(instance);
    }

    public void setBlockPosition(IntVector3 value) {
        T.blockPosition.set(instance, value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.EntityHanging</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityHangingClass extends Template.Class<EntityHangingHandle> {
        public final Template.Field.Converted<IntVector3> blockPosition = new Template.Field.Converted<IntVector3>();

    }

}

