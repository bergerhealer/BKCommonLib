package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.Dimension;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.WorldProvider</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class WorldProviderHandle extends Template.Handle {
    /** @See {@link WorldProviderClass} */
    public static final WorldProviderClass T = new WorldProviderClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(WorldProviderHandle.class, "net.minecraft.server.WorldProvider", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static WorldProviderHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract boolean hasSkyLight();
    public abstract Dimension getDimension();

    public boolean isDarkWorld() {
        return !hasSkyLight();
    }
    /**
     * Stores class members for <b>net.minecraft.server.WorldProvider</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class WorldProviderClass extends Template.Class<WorldProviderHandle> {
        public final Template.Method<Boolean> hasSkyLight = new Template.Method<Boolean>();
        public final Template.Method.Converted<Dimension> getDimension = new Template.Method.Converted<Dimension>();

    }

}

