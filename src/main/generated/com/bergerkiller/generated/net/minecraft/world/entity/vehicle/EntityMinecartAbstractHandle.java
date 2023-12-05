package com.bergerkiller.generated.net.minecraft.world.entity.vehicle;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher.Key;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.entity.vehicle.EntityMinecartAbstract</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.entity.vehicle.EntityMinecartAbstract")
public abstract class EntityMinecartAbstractHandle extends EntityHandle {
    /** @see EntityMinecartAbstractClass */
    public static final EntityMinecartAbstractClass T = Template.Class.create(EntityMinecartAbstractClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static EntityMinecartAbstractHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract float getDamage();
    public abstract void setDamage(float damage);
    public abstract int getHurtTime();
    public abstract void activate(int x, int y, int z, boolean active);

    public static final Key<Integer> DATA_SHAKING_FACTOR;
    public static final Key<Integer> DATA_SHAKING_DIRECTION;
    public static final Key<Float> DATA_SHAKING_DAMAGE;

    static {
        if (VehicleEntityHandle.T.isAvailable()) {
            DATA_SHAKING_FACTOR = Key.Type.INTEGER.createKey(VehicleEntityHandle.T.DATA_SHAKING_FACTOR, -1);
            DATA_SHAKING_DIRECTION = Key.Type.INTEGER.createKey(VehicleEntityHandle.T.DATA_SHAKING_DIRECTION, -1);
            DATA_SHAKING_DAMAGE = Key.Type.FLOAT.createKey(VehicleEntityHandle.T.DATA_SHAKING_DAMAGE, -1);
        } else {
            DATA_SHAKING_FACTOR = Key.Type.INTEGER.createKey(T.DATA_SHAKING_FACTOR, 17);
            DATA_SHAKING_DIRECTION = Key.Type.INTEGER.createKey(T.DATA_SHAKING_DIRECTION, 18);
            DATA_SHAKING_DAMAGE = Key.Type.FLOAT.createKey(T.DATA_SHAKING_DAMAGE, 19);
        }
    }

    public static final Key<Integer> DATA_BLOCK_TYPE = Key.Type.INTEGER.createKey(T.DATA_BLOCK_TYPE, 20);
    public static final Key<Integer> DATA_BLOCK_OFFSET = Key.Type.INTEGER.createKey(T.DATA_BLOCK_OFFSET, 21);
    public static final Key<Boolean> DATA_BLOCK_VISIBLE = Key.Type.BOOLEAN.createKey(T.DATA_BLOCK_VISIBLE, 22);
    /**
     * Stores class members for <b>net.minecraft.world.entity.vehicle.EntityMinecartAbstract</b>.
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
        public final Template.Method<Integer> getHurtTime = new Template.Method<Integer>();
        public final Template.Method<Void> activate = new Template.Method<Void>();

    }

}

