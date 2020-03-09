package com.bergerkiller.generated.org.bukkit.craftbukkit.entity;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.server.EntityHandle;
import org.bukkit.Server;
import org.bukkit.entity.Entity;

/**
 * Instance wrapper handle for type <b>org.bukkit.craftbukkit.entity.CraftEntity</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class CraftEntityHandle extends Template.Handle {
    /** @See {@link CraftEntityClass} */
    public static final CraftEntityClass T = new CraftEntityClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(CraftEntityHandle.class, "org.bukkit.craftbukkit.entity.CraftEntity", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static CraftEntityHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static Entity createCraftEntity(Server server, EntityHandle entity) {
        return T.createCraftEntity.invoke(server, entity);
    }

    public abstract void setHandle(EntityHandle entity);
    public abstract Object getHandle();
    public abstract EntityHandle getEntityHandle();
    public abstract void setEntityHandle(EntityHandle value);
    /**
     * Stores class members for <b>org.bukkit.craftbukkit.entity.CraftEntity</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class CraftEntityClass extends Template.Class<CraftEntityHandle> {
        public final Template.Field.Converted<EntityHandle> entityHandle = new Template.Field.Converted<EntityHandle>();

        public final Template.StaticMethod.Converted<Entity> createCraftEntity = new Template.StaticMethod.Converted<Entity>();

        public final Template.Method.Converted<Void> setHandle = new Template.Method.Converted<Void>();
        public final Template.Method<Object> getHandle = new Template.Method<Object>();

    }

}

