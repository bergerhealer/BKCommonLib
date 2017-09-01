package com.bergerkiller.generated.com.mojang.authlib;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.UUID;

/**
 * Instance wrapper handle for type <b>com.mojang.authlib.GameProfile</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class GameProfileHandle extends Template.Handle {
    /** @See {@link GameProfileClass} */
    public static final GameProfileClass T = new GameProfileClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(GameProfileHandle.class, "com.mojang.authlib.GameProfile");

    /* ============================================================================== */

    public static GameProfileHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        GameProfileHandle handle = new GameProfileHandle();
        handle.instance = handleInstance;
        return handle;
    }

    public static final GameProfileHandle createNew(UUID uuid, String name) {
        return T.constr_uuid_name.newInstance(uuid, name);
    }

    /* ============================================================================== */

    public UUID getId() {
        return T.getId.invoke(instance);
    }

    public String getName() {
        return T.getName.invoke(instance);
    }

    /**
     * Stores class members for <b>com.mojang.authlib.GameProfile</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class GameProfileClass extends Template.Class<GameProfileHandle> {
        public final Template.Constructor.Converted<GameProfileHandle> constr_uuid_name = new Template.Constructor.Converted<GameProfileHandle>();

        public final Template.Method<UUID> getId = new Template.Method<UUID>();
        public final Template.Method<String> getName = new Template.Method<String>();

    }

}

