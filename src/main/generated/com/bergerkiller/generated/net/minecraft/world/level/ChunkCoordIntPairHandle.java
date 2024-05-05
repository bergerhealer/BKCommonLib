package com.bergerkiller.generated.net.minecraft.world.level;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector2;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.ChunkCoordIntPair</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.level.ChunkCoordIntPair")
public abstract class ChunkCoordIntPairHandle extends Template.Handle {
    /** @see ChunkCoordIntPairClass */
    public static final ChunkCoordIntPairClass T = Template.Class.create(ChunkCoordIntPairClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ChunkCoordIntPairHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final ChunkCoordIntPairHandle createNew(int x, int z) {
        return T.constr_x_z.newInstance(x, z);
    }

    /* ============================================================================== */

    public static Object fromIntVector2Raw(IntVector2 vector) {
        return T.fromIntVector2Raw.invoker.invoke(null,vector);
    }

    public abstract IntVector2 toIntVector2();
    public static ChunkCoordIntPairHandle fromIntVector2(com.bergerkiller.bukkit.common.bases.IntVector2 vector) {
        return createHandle(fromIntVector2Raw(vector));
    }
    public abstract int getX();
    public abstract void setX(int value);
    public abstract int getZ();
    public abstract void setZ(int value);
    /**
     * Stores class members for <b>net.minecraft.world.level.ChunkCoordIntPair</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ChunkCoordIntPairClass extends Template.Class<ChunkCoordIntPairHandle> {
        public final Template.Constructor.Converted<ChunkCoordIntPairHandle> constr_x_z = new Template.Constructor.Converted<ChunkCoordIntPairHandle>();

        public final Template.Field.Integer x = new Template.Field.Integer();
        public final Template.Field.Integer z = new Template.Field.Integer();

        public final Template.StaticMethod<Object> fromIntVector2Raw = new Template.StaticMethod<Object>();

        public final Template.Method<IntVector2> toIntVector2 = new Template.Method<IntVector2>();

    }

}

