package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class EntityMinecartTNTHandle extends EntityMinecartAbstractHandle {
    public static final EntityMinecartTNTClass T = new EntityMinecartTNTClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EntityMinecartTNTHandle.class, "net.minecraft.server.EntityMinecartTNT");


    /* ============================================================================== */

    public static EntityMinecartTNTHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        EntityMinecartTNTHandle handle = new EntityMinecartTNTHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public void explode(double damage) {
        T.explode.invoke(instance, damage);
    }

    public void prime() {
        T.prime.invoke(instance);
    }

    public int getFuse() {
        return T.fuse.getInteger(instance);
    }

    public void setFuse(int value) {
        T.fuse.setInteger(instance, value);
    }

    public static final class EntityMinecartTNTClass extends Template.Class<EntityMinecartTNTHandle> {
        public final Template.Field.Integer fuse = new Template.Field.Integer();

        public final Template.Method<Void> explode = new Template.Method<Void>();
        public final Template.Method<Void> prime = new Template.Method<Void>();

    }
}
