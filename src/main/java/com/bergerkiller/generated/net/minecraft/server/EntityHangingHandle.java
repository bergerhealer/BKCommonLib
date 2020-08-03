package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.EntityHanging</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.server.EntityHanging")
public abstract class EntityHangingHandle extends EntityHandle {
    /** @See {@link EntityHangingClass} */
    public static final EntityHangingClass T = Template.Class.create(EntityHangingClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static EntityHangingHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract IntVector3 getBlockPosition();
    public abstract void setBlockPosition(IntVector3 value);
    /**
     * Stores class members for <b>net.minecraft.server.EntityHanging</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityHangingClass extends Template.Class<EntityHangingHandle> {
        public final Template.Field.Converted<IntVector3> blockPosition = new Template.Field.Converted<IntVector3>();

    }

}

