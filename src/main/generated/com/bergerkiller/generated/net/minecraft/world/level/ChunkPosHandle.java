package com.bergerkiller.generated.net.minecraft.world.level;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector2;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.ChunkPos</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.level.ChunkPos")
public abstract class ChunkPosHandle extends Template.Handle {
    /** @see ChunkPosClass */
    public static final ChunkPosClass T = Template.Class.create(ChunkPosClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ChunkPosHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final ChunkPosHandle createNew(int x, int z) {
        return T.constr_x_z.newInstance(x, z);
    }

    /* ============================================================================== */

    public static Object fromIntVector2Raw(IntVector2 vector) {
        return T.fromIntVector2Raw.invoker.invoke(null,vector);
    }

    public abstract int x();
    public abstract int z();
    public abstract IntVector2 toIntVector2();
    public static ChunkPosHandle fromIntVector2(com.bergerkiller.bukkit.common.bases.IntVector2 vector) {
        return createHandle(fromIntVector2Raw(vector));
    }
    /**
     * Stores class members for <b>net.minecraft.world.level.ChunkPos</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ChunkPosClass extends Template.Class<ChunkPosHandle> {
        public final Template.Constructor.Converted<ChunkPosHandle> constr_x_z = new Template.Constructor.Converted<ChunkPosHandle>();

        public final Template.StaticMethod<Object> fromIntVector2Raw = new Template.StaticMethod<Object>();

        public final Template.Method<Integer> x = new Template.Method<Integer>();
        public final Template.Method<Integer> z = new Template.Method<Integer>();
        public final Template.Method<IntVector2> toIntVector2 = new Template.Method<IntVector2>();

    }

}

