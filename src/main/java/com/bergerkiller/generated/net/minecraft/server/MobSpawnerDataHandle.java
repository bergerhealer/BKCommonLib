package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.MobSpawnerData</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class MobSpawnerDataHandle extends Template.Handle {
    /** @See {@link MobSpawnerDataClass} */
    public static final MobSpawnerDataClass T = new MobSpawnerDataClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(MobSpawnerDataHandle.class, "net.minecraft.server.MobSpawnerData", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static MobSpawnerDataHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>net.minecraft.server.MobSpawnerData</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class MobSpawnerDataClass extends Template.Class<MobSpawnerDataHandle> {
    }

}

