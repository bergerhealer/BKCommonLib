package com.bergerkiller.generated.org.bukkit.craftbukkit.entity;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import org.bukkit.Server;
import com.bergerkiller.generated.net.minecraft.server.EntityHandle;
import org.bukkit.entity.Entity;

public class CraftEntityHandle extends Template.Handle {
    public static final CraftEntityClass T = new CraftEntityClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(CraftEntityHandle.class, "org.bukkit.craftbukkit.entity.CraftEntity");


    /* ============================================================================== */

    public static CraftEntityHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        CraftEntityHandle handle = new CraftEntityHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static Entity createCraftEntity(Server server, EntityHandle entity) {
        return T.createCraftEntity.invokeVA(server, entity);
    }

    public void setHandle(EntityHandle entity) {
        T.setHandle.invoke(instance, entity);
    }

    public Object getHandle() {
        return T.getHandle.invoke(instance);
    }

    public EntityHandle getEntityHandle() {
        return T.entityHandle.get(instance);
    }

    public void setEntityHandle(EntityHandle value) {
        T.entityHandle.set(instance, value);
    }

    public static final class CraftEntityClass extends Template.Class<CraftEntityHandle> {
        public final Template.Field.Converted<EntityHandle> entityHandle = new Template.Field.Converted<EntityHandle>();

        public final Template.StaticMethod.Converted<Entity> createCraftEntity = new Template.StaticMethod.Converted<Entity>();

        public final Template.Method.Converted<Void> setHandle = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Object> getHandle = new Template.Method.Converted<Object>();

    }
}
