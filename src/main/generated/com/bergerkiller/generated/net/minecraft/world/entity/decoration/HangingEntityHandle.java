package com.bergerkiller.generated.net.minecraft.world.entity.decoration;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import org.bukkit.block.BlockFace;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.entity.decoration.HangingEntity</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.entity.decoration.HangingEntity")
public abstract class HangingEntityHandle extends EntityHandle {
    /** @see HangingEntityClass */
    public static final HangingEntityClass T = Template.Class.create(HangingEntityClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static HangingEntityHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract void setBlockPositionField(IntVector3 blockPosition);
    public abstract IntVector3 getBlockPosition();
    public abstract BlockFace getFacing();
    /**
     * Stores class members for <b>net.minecraft.world.entity.decoration.HangingEntity</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class HangingEntityClass extends Template.Class<HangingEntityHandle> {
        public final Template.Method.Converted<Void> setBlockPositionField = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<IntVector3> getBlockPosition = new Template.Method.Converted<IntVector3>();
        public final Template.Method.Converted<BlockFace> getFacing = new Template.Method.Converted<BlockFace>();

    }

}

