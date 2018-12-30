package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

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


    public boolean hasSkyLight() {
        return !isDarkWorld();
    }

    public boolean isDarkWorld() {
        return isDarkWorldField();
    }
    public abstract boolean isDarkWorldField();
    public abstract void setIsDarkWorldField(boolean value);
    /**
     * Stores class members for <b>net.minecraft.server.WorldProvider</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class WorldProviderClass extends Template.Class<WorldProviderHandle> {
        public final Template.Field.Boolean isDarkWorldField = new Template.Field.Boolean();

    }

}

