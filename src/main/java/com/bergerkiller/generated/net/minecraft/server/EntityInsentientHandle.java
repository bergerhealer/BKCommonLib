package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.EntityInsentient</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class EntityInsentientHandle extends EntityLivingHandle {
    /** @See {@link EntityInsentientClass} */
    public static final EntityInsentientClass T = new EntityInsentientClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EntityInsentientHandle.class, "net.minecraft.server.EntityInsentient");

    /* ============================================================================== */

    public static EntityInsentientHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        EntityInsentientHandle handle = new EntityInsentientHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public EntityHandle getLeashHolder() {
        return T.getLeashHolder.invoke(instance);
    }

    public Object getNavigation() {
        return T.getNavigation.invoke(instance);
    }

    /**
     * Stores class members for <b>net.minecraft.server.EntityInsentient</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityInsentientClass extends Template.Class<EntityInsentientHandle> {
        public final Template.Method.Converted<EntityHandle> getLeashHolder = new Template.Method.Converted<EntityHandle>();
        public final Template.Method.Converted<Object> getNavigation = new Template.Method.Converted<Object>();

    }

}

