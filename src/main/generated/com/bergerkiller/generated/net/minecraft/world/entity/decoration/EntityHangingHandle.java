package com.bergerkiller.generated.net.minecraft.world.entity.decoration;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import org.bukkit.block.BlockFace;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.entity.decoration.EntityHanging</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.entity.decoration.EntityHanging")
public abstract class EntityHangingHandle extends EntityHandle {
    /** @see EntityHangingClass */
    public static final EntityHangingClass T = Template.Class.create(EntityHangingClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static EntityHangingHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract IntVector3 getBlockPosition();
    public abstract BlockFace getFacing();
    public abstract IntVector3 getBlockPositionField();
    public abstract void setBlockPositionField(IntVector3 value);
    /**
     * Stores class members for <b>net.minecraft.world.entity.decoration.EntityHanging</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityHangingClass extends Template.Class<EntityHangingHandle> {
        public final Template.Field.Converted<IntVector3> blockPositionField = new Template.Field.Converted<IntVector3>();

        public final Template.Method.Converted<IntVector3> getBlockPosition = new Template.Method.Converted<IntVector3>();
        public final Template.Method.Converted<BlockFace> getFacing = new Template.Method.Converted<BlockFace>();

    }

}

