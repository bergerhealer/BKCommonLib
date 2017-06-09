package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.ChunkCoordIntPair</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class ChunkCoordIntPairHandle extends Template.Handle {
    /** @See {@link ChunkCoordIntPairClass} */
    public static final ChunkCoordIntPairClass T = new ChunkCoordIntPairClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(ChunkCoordIntPairHandle.class, "net.minecraft.server.ChunkCoordIntPair");

    /* ============================================================================== */

    public static ChunkCoordIntPairHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        ChunkCoordIntPairHandle handle = new ChunkCoordIntPairHandle();
        handle.instance = handleInstance;
        return handle;
    }

    public static final ChunkCoordIntPairHandle createNew(int x, int z) {
        return T.constr_x_z.newInstance(x, z);
    }

    /* ============================================================================== */

    public int getX() {
        return T.x.getInteger(instance);
    }

    public void setX(int value) {
        T.x.setInteger(instance, value);
    }

    public int getZ() {
        return T.z.getInteger(instance);
    }

    public void setZ(int value) {
        T.z.setInteger(instance, value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.ChunkCoordIntPair</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ChunkCoordIntPairClass extends Template.Class<ChunkCoordIntPairHandle> {
        public final Template.Constructor.Converted<ChunkCoordIntPairHandle> constr_x_z = new Template.Constructor.Converted<ChunkCoordIntPairHandle>();

        public final Template.Field.Integer x = new Template.Field.Integer();
        public final Template.Field.Integer z = new Template.Field.Integer();

    }

}

