package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.EntityMinecartTNT</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class EntityMinecartTNTHandle extends EntityMinecartAbstractHandle {
    /** @See {@link EntityMinecartTNTClass} */
    public static final EntityMinecartTNTClass T = new EntityMinecartTNTClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EntityMinecartTNTHandle.class, "net.minecraft.server.EntityMinecartTNT");

    /* ============================================================================== */

    public static EntityMinecartTNTHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public void explode(double damage) {
        T.explode.invoke(getRaw(), damage);
    }

    public void prime() {
        T.prime.invoke(getRaw());
    }

    public int getFuse() {
        return T.fuse.getInteger(getRaw());
    }

    public void setFuse(int value) {
        T.fuse.setInteger(getRaw(), value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.EntityMinecartTNT</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityMinecartTNTClass extends Template.Class<EntityMinecartTNTHandle> {
        public final Template.Field.Integer fuse = new Template.Field.Integer();

        public final Template.Method<Void> explode = new Template.Method<Void>();
        public final Template.Method<Void> prime = new Template.Method<Void>();

    }

}

