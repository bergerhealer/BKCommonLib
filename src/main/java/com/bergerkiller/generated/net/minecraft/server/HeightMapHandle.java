package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.HeightMap</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class HeightMapHandle extends Template.Handle {
    /** @See {@link HeightMapClass} */
    public static final HeightMapClass T = new HeightMapClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(HeightMapHandle.class, "net.minecraft.server.HeightMap", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static HeightMapHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract ChunkHandle getChunk();
    public abstract int getHeight(int x, int z);
    public abstract void setHeight(int x, int z, int height);
    public abstract void initialize();
    /**
     * Stores class members for <b>net.minecraft.server.HeightMap</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class HeightMapClass extends Template.Class<HeightMapHandle> {
        public final Template.Method.Converted<ChunkHandle> getChunk = new Template.Method.Converted<ChunkHandle>();
        public final Template.Method<Integer> getHeight = new Template.Method<Integer>();
        public final Template.Method<Void> setHeight = new Template.Method<Void>();
        public final Template.Method<Void> initialize = new Template.Method<Void>();

    }

}

