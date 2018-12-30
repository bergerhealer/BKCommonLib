package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.EntityMinecartHopper</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class EntityMinecartHopperHandle extends EntityMinecartAbstractHandle {
    /** @See {@link EntityMinecartHopperClass} */
    public static final EntityMinecartHopperClass T = new EntityMinecartHopperClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EntityMinecartHopperHandle.class, "net.minecraft.server.EntityMinecartHopper", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static EntityMinecartHopperHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract boolean suckItems();
    public abstract boolean isSuckingEnabled();
    public abstract void setSuckingEnabled(boolean enabled);
    public abstract int getSuckingCooldown();
    public abstract void setSuckingCooldown(int value);
    /**
     * Stores class members for <b>net.minecraft.server.EntityMinecartHopper</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityMinecartHopperClass extends Template.Class<EntityMinecartHopperHandle> {
        public final Template.Field.Integer suckingCooldown = new Template.Field.Integer();

        public final Template.Method<Boolean> suckItems = new Template.Method<Boolean>();
        public final Template.Method<Boolean> isSuckingEnabled = new Template.Method<Boolean>();
        public final Template.Method<Void> setSuckingEnabled = new Template.Method<Void>();

    }

}

