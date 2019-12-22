package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.RegionFile</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class RegionFileHandle extends Template.Handle {
    /** @See {@link RegionFileClass} */
    public static final RegionFileClass T = new RegionFileClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(RegionFileHandle.class, "net.minecraft.server.RegionFile", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static RegionFileHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract void close();
    public abstract boolean chunkExists(int cx, int cz);
    /**
     * Stores class members for <b>net.minecraft.server.RegionFile</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class RegionFileClass extends Template.Class<RegionFileHandle> {
        public final Template.Method<Void> close = new Template.Method<Void>();
        public final Template.Method<Boolean> chunkExists = new Template.Method<Boolean>();

    }

}

