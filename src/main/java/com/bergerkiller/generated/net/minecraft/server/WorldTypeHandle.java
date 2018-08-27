package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.Dimension;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.WorldType</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class WorldTypeHandle extends Template.Handle {
    /** @See {@link WorldTypeClass} */
    public static final WorldTypeClass T = new WorldTypeClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(WorldTypeHandle.class, "net.minecraft.server.WorldType");

    /* ============================================================================== */

    public static WorldTypeHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static WorldTypeHandle getType(String name) {
        return T.getType.invoke(name);
    }

    public abstract Dimension getDimension();
    public abstract String getName();
    public abstract void setName(String value);
    /**
     * Stores class members for <b>net.minecraft.server.WorldType</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class WorldTypeClass extends Template.Class<WorldTypeHandle> {
        public final Template.Field<String> name = new Template.Field<String>();

        public final Template.StaticMethod.Converted<WorldTypeHandle> getType = new Template.StaticMethod.Converted<WorldTypeHandle>();

        public final Template.Method.Converted<Dimension> getDimension = new Template.Method.Converted<Dimension>();

    }

}

