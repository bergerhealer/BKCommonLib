package com.bergerkiller.generated.org.bukkit;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>org.bukkit.World</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class WorldHandle extends Template.Handle {
    /** @See {@link WorldClass} */
    public static final WorldClass T = new WorldClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(WorldHandle.class, "org.bukkit.World", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static WorldHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    /**
     * Stores class members for <b>org.bukkit.World</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class WorldClass extends Template.Class<WorldHandle> {
        @Template.Optional
        public final Template.Method<Void> playSound = new Template.Method<Void>();

    }

}

