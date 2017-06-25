package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher.Key;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.EntityMinecartAbstract</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class EntityMinecartAbstractHandle extends EntityHandle {
    /** @See {@link EntityMinecartAbstractClass} */
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

    public void activate(int x, int y, int z, boolean active) {
        T.activate.invoke(instance, x, y, z, active);
    }


    public static final Key<Integer> DATA_SHAKING_FACTOR = Key.fromTemplate(T.DATA_SHAKING_FACTOR, 17, int.class);
    public static final Key<Integer> DATA_SHAKING_DIRECTION = Key.fromTemplate(T.DATA_SHAKING_DIRECTION, 18, int.class);
    public static final Key<Float> DATA_SHAKING_DAMAGE = Key.fromTemplate(T.DATA_SHAKING_DAMAGE, 19, float.class);
    public static final Key<Integer> DATA_BLOCK_TYPE = Key.fromTemplate(T.DATA_BLOCK_TYPE, 20, int.class);
    public static final Key<Integer> DATA_BLOCK_OFFSET = Key.fromTemplate(T.DATA_BLOCK_OFFSET, 21, int.class);
    public static final Key<Boolean> DATA_BLOCK_VISIBLE = Key.fromTemplate(T.DATA_BLOCK_VISIBLE, 22, byte.class);
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

