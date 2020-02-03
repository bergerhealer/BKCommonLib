package com.bergerkiller.generated.org.bukkit.entity;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.entity.Entity;
import java.util.List;

/**
 * Instance wrapper handle for type <b>org.bukkit.entity.Entity</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class EntityHandle extends Template.Handle {
    /** @See {@link EntityClass} */
    public static final EntityClass T = new EntityClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EntityHandle.class, "org.bukkit.entity.Entity", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static EntityHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract List<Entity> getPassengers();
    public abstract boolean addPassenger(Entity passenger);
    public abstract boolean removePassenger(Entity passenger);
    /**
     * Stores class members for <b>org.bukkit.entity.Entity</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityClass extends Template.Class<EntityHandle> {
        public final Template.Method<List<Entity>> getPassengers = new Template.Method<List<Entity>>();
        public final Template.Method<Boolean> addPassenger = new Template.Method<Boolean>();
        public final Template.Method<Boolean> removePassenger = new Template.Method<Boolean>();

    }

}

