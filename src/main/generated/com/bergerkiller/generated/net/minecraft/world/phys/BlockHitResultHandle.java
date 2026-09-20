package com.bergerkiller.generated.net.minecraft.world.phys;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.phys.BlockHitResult</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.phys.BlockHitResult")
public abstract class BlockHitResultHandle extends HitResultHandle {
    /** @see BlockHitResultClass */
    public static final BlockHitResultClass T = Template.Class.create(BlockHitResultClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static BlockHitResultHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static BlockHitResultHandle createNewMiss(Vector location, BlockFace direction, IntVector3 pos) {
        return T.createNewMiss.invoke(location, direction, pos);
    }

    public static BlockHitResultHandle createNew(Vector location, BlockFace direction, IntVector3 pos, boolean inside) {
        return T.createNew.invoke(location, direction, pos, inside);
    }

    public abstract IntVector3 getBlockPos();
    public abstract Vector getRelativePosition();
    public abstract BlockFace getDirection();
    public abstract boolean isInside();
    public abstract boolean isWorldBorderHit();
    public abstract boolean isMiss();
    public BlockHitResultHandle withLocation(org.bukkit.util.Vector location) {
        return createNew(location, getDirection(), getBlockPos(), isInside());
    }

    public BlockHitResultHandle withRelativePosition(org.bukkit.util.Vector relativeLocation) {
        IntVector3 blockPos = getBlockPos();
        return withLocation(new org.bukkit.util.Vector(
            relativeLocation.getX() + blockPos.x,
            relativeLocation.getY() + blockPos.y,
            relativeLocation.getZ() + blockPos.z
        ));
    }
    /**
     * Stores class members for <b>net.minecraft.world.phys.BlockHitResult</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class BlockHitResultClass extends Template.Class<BlockHitResultHandle> {
        public final Template.StaticMethod.Converted<BlockHitResultHandle> createNewMiss = new Template.StaticMethod.Converted<BlockHitResultHandle>();
        public final Template.StaticMethod.Converted<BlockHitResultHandle> createNew = new Template.StaticMethod.Converted<BlockHitResultHandle>();

        public final Template.Method.Converted<IntVector3> getBlockPos = new Template.Method.Converted<IntVector3>();
        public final Template.Method<Vector> getRelativePosition = new Template.Method<Vector>();
        public final Template.Method.Converted<BlockFace> getDirection = new Template.Method.Converted<BlockFace>();
        public final Template.Method<Boolean> isInside = new Template.Method<Boolean>();
        public final Template.Method<Boolean> isWorldBorderHit = new Template.Method<Boolean>();
        public final Template.Method<Boolean> isMiss = new Template.Method<Boolean>();

    }

}

