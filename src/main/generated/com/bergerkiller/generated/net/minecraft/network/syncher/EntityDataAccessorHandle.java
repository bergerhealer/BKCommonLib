package com.bergerkiller.generated.net.minecraft.network.syncher;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.syncher.EntityDataAccessor</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.syncher.EntityDataAccessor")
public abstract class EntityDataAccessorHandle extends Template.Handle {
    /** @see EntityDataAccessorClass */
    public static final EntityDataAccessorClass T = Template.Class.create(EntityDataAccessorClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static EntityDataAccessorHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract int getId();
    public abstract Object getSerializer();
    /**
     * Stores class members for <b>net.minecraft.network.syncher.EntityDataAccessor</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityDataAccessorClass extends Template.Class<EntityDataAccessorHandle> {
        public final Template.Method<Integer> getId = new Template.Method<Integer>();
        public final Template.Method<Object> getSerializer = new Template.Method<Object>();

    }

}

