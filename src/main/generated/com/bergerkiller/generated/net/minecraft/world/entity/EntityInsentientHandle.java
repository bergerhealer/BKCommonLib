package com.bergerkiller.generated.net.minecraft.world.entity;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher.Key;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.entity.EntityInsentient</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.entity.EntityInsentient")
public abstract class EntityInsentientHandle extends EntityLivingHandle {
    /** @see EntityInsentientClass */
    public static final EntityInsentientClass T = Template.Class.create(EntityInsentientClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static EntityInsentientHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract EntityHandle getLeashHolder();
    public abstract Object getNavigation();
    public abstract boolean isSleeping();
    public static final Key<Byte> DATA_INSENTIENT_FLAGS = Key.Type.BYTE.createKey(T.DATA_INSENTIENT_FLAGS, 11);
    public static final int DATA_INSENTIENT_FLAG_NOAI = (1 << 0);
    public static final int DATA_INSENTIENT_FLAG_LEFT_HANDED = (1 << 1);
    /**
     * Stores class members for <b>net.minecraft.world.entity.EntityInsentient</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityInsentientClass extends Template.Class<EntityInsentientHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<Key<Byte>> DATA_INSENTIENT_FLAGS = new Template.StaticField.Converted<Key<Byte>>();

        public final Template.Method.Converted<EntityHandle> getLeashHolder = new Template.Method.Converted<EntityHandle>();
        public final Template.Method<Object> getNavigation = new Template.Method<Object>();
        public final Template.Method<Boolean> isSleeping = new Template.Method<Boolean>();

    }

}

