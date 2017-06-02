package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class EntityInsentientHandle extends EntityLivingHandle {
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

    public static final class EntityInsentientClass extends Template.Class<EntityInsentientHandle> {
        public final Template.Method.Converted<EntityHandle> getLeashHolder = new Template.Method.Converted<EntityHandle>();
        public final Template.Method.Converted<Object> getNavigation = new Template.Method.Converted<Object>();

    }

}

