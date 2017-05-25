package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class EntityMinecartAbstractHandle extends EntityHandle {
    public static final EntityMinecartAbstractClass T = new EntityMinecartAbstractClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EntityMinecartAbstractHandle.class, "net.minecraft.server.EntityMinecartAbstract");


    /* ============================================================================== */

    public static EntityMinecartAbstractHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        EntityMinecartAbstractHandle handle = new EntityMinecartAbstractHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public float getDamage() {
        return T.getDamage.invoke(instance);
    }

    public void setDamage(float damage) {
        T.setDamage.invoke(instance, damage);
    }

    public int getType() {
        return T.getType.invoke(instance);
    }

    public static final class EntityMinecartAbstractClass extends Template.Class<EntityMinecartAbstractHandle> {
        public final Template.Method<Float> getDamage = new Template.Method<Float>();
        public final Template.Method<Void> setDamage = new Template.Method<Void>();
        public final Template.Method<Integer> getType = new Template.Method<Integer>();

    }
}
