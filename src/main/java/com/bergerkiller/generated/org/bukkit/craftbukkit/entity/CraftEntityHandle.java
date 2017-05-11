package com.bergerkiller.generated.org.bukkit.craftbukkit.entity;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.Server;
import org.bukkit.entity.Entity;

public class CraftEntityHandle extends Template.Handle {
    public static final CraftEntityClass T = new CraftEntityClass();


    public static Entity createCraftEntity(Server server, Object entity) {
        return T.createCraftEntity.invoke(server, entity);
    }

    public Object getEntityHandle() {
        return T.entityHandle.get(instance);
    }

    public void setEntityHandle(Object value) {
        T.entityHandle.set(instance, value);
    }

    public static class CraftEntityClass extends Template.Class {

        protected CraftEntityClass() {
            init(CraftEntityClass.class, "org.bukkit.craftbukkit.entity.CraftEntity");
        }

        public final Template.Field.Converted<Object> entityHandle = new Template.Field.Converted<Object>();

        public final Template.StaticMethod.Converted<Entity> createCraftEntity = new Template.StaticMethod.Converted<Entity>();

    }
}
