package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher.Key;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.EntityMinecartAbstract</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class EntityMinecartAbstractHandle extends EntityHandle {
    /** @See {@link EntityMinecartAbstractClass} */
    public static final EntityMinecartAbstractClass T = new EntityMinecartAbstractClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EntityMinecartAbstractHandle.class, "net.minecraft.server.EntityMinecartAbstract", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static EntityMinecartAbstractHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract float getDamage();
    public abstract void setDamage(float damage);
    public abstract int getType();
    public abstract void activate(int x, int y, int z, boolean active);

    public static final Key<Integer> DATA_SHAKING_FACTOR = Key.Type.INTEGER.createKey(T.DATA_SHAKING_FACTOR, 17);
    public static final Key<Integer> DATA_SHAKING_DIRECTION = Key.Type.INTEGER.createKey(T.DATA_SHAKING_DIRECTION, 18);
    public static final Key<Float> DATA_SHAKING_DAMAGE = Key.Type.FLOAT.createKey(T.DATA_SHAKING_DAMAGE, 19);
    public static final Key<Integer> DATA_BLOCK_TYPE = Key.Type.INTEGER.createKey(T.DATA_BLOCK_TYPE, 20);
    public static final Key<Integer> DATA_BLOCK_OFFSET = Key.Type.INTEGER.createKey(T.DATA_BLOCK_OFFSET, 21);
    public static final Key<Boolean> DATA_BLOCK_VISIBLE = Key.Type.BOOLEAN.createKey(T.DATA_BLOCK_VISIBLE, 22);
    /**
     * Stores class members for <b>net.minecraft.server.EntityMinecartAbstract</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityMinecartAbstractClass extends Template.Class<EntityMinecartAbstractHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<Key<Integer>> DATA_SHAKING_FACTOR = new Template.StaticField.Converted<Key<Integer>>();
        @Template.Optional
        public final Template.StaticField.Converted<Key<Integer>> DATA_SHAKING_DIRECTION = new Template.StaticField.Converted<Key<Integer>>();
        @Template.Optional
        public final Template.StaticField.Converted<Key<Float>> DATA_SHAKING_DAMAGE = new Template.StaticField.Converted<Key<Float>>();
        @Template.Optional
        public final Template.StaticField.Converted<Key<Integer>> DATA_BLOCK_TYPE = new Template.StaticField.Converted<Key<Integer>>();
        @Template.Optional
        public final Template.StaticField.Converted<Key<Integer>> DATA_BLOCK_OFFSET = new Template.StaticField.Converted<Key<Integer>>();
        @Template.Optional
        public final Template.StaticField.Converted<Key<Boolean>> DATA_BLOCK_VISIBLE = new Template.StaticField.Converted<Key<Boolean>>();

        public final Template.Method<Float> getDamage = new Template.Method<Float>();
        public final Template.Method<Void> setDamage = new Template.Method<Void>();
        public final Template.Method<Integer> getType = new Template.Method<Integer>();
        public final Template.Method<Void> activate = new Template.Method<Void>();

    }

}

