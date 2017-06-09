package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.MinecraftKey</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class MinecraftKeyHandle extends Template.Handle {
    /** @See {@link MinecraftKeyClass} */
    public static final MinecraftKeyClass T = new MinecraftKeyClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(MinecraftKeyHandle.class, "net.minecraft.server.MinecraftKey");

    /* ============================================================================== */

    public static MinecraftKeyHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        MinecraftKeyHandle handle = new MinecraftKeyHandle();
        handle.instance = handleInstance;
        return handle;
    }

    public static final MinecraftKeyHandle createNew(String keyToken) {
        return T.constr_keyToken.newInstance(keyToken);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.server.MinecraftKey</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class MinecraftKeyClass extends Template.Class<MinecraftKeyHandle> {
        public final Template.Constructor.Converted<MinecraftKeyHandle> constr_keyToken = new Template.Constructor.Converted<MinecraftKeyHandle>();

    }

}

