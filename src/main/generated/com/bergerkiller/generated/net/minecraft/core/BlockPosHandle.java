package com.bergerkiller.generated.net.minecraft.core;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import org.bukkit.block.Block;

/**
 * Instance wrapper handle for type <b>net.minecraft.core.BlockPos</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.core.BlockPos")
public abstract class BlockPosHandle extends Vec3iHandle {
    /** @see BlockPosClass */
    public static final BlockPosClass T = Template.Class.create(BlockPosClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static BlockPosHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final BlockPosHandle createNew(int x, int y, int z) {
        return T.constr_x_y_z.newInstance(x, y, z);
    }

    /* ============================================================================== */

    public static Object fromIntVector3Raw(IntVector3 vector) {
        return T.fromIntVector3Raw.invoker.invoke(null,vector);
    }

    public static Object fromBukkitBlockRaw(Block block) {
        return T.fromBukkitBlockRaw.invoker.invoke(null,block);
    }

    public static BlockPosHandle fromIntVector3(com.bergerkiller.bukkit.common.bases.IntVector3 vector) {
        return createHandle(fromIntVector3Raw(vector));
    }

    public static BlockPosHandle fromBukkitBlock(org.bukkit.block.Block block) {
        return createHandle(fromBukkitBlock(block));
    }
    /**
     * Stores class members for <b>net.minecraft.core.BlockPos</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class BlockPosClass extends Template.Class<BlockPosHandle> {
        public final Template.Constructor.Converted<BlockPosHandle> constr_x_y_z = new Template.Constructor.Converted<BlockPosHandle>();

        public final Template.StaticMethod<Object> fromIntVector3Raw = new Template.StaticMethod<Object>();
        public final Template.StaticMethod<Object> fromBukkitBlockRaw = new Template.StaticMethod<Object>();

    }

}

