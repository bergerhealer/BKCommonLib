package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.EntityItem</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class EntityItemHandle extends EntityHandle {
    /** @See {@link EntityItemClass} */
    public static final EntityItemClass T = new EntityItemClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EntityItemHandle.class, "net.minecraft.server.EntityItem");

    /* ============================================================================== */

    public static EntityItemHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final EntityItemHandle createNew(WorldHandle world, double x, double y, double z, ItemStackHandle itemstack) {
        return T.constr_world_x_y_z_itemstack.newInstance(world, x, y, z, itemstack);
    }

    /* ============================================================================== */

    public abstract ItemStackHandle getItemStack();
    public abstract void setItemStack(ItemStackHandle itemstack);
    public abstract int getAge();
    public abstract void setAge(int value);
    public abstract int getPickupDelay();
    public abstract void setPickupDelay(int value);
    /**
     * Stores class members for <b>net.minecraft.server.EntityItem</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityItemClass extends Template.Class<EntityItemHandle> {
        public final Template.Constructor.Converted<EntityItemHandle> constr_world_x_y_z_itemstack = new Template.Constructor.Converted<EntityItemHandle>();

        public final Template.Field.Integer age = new Template.Field.Integer();
        public final Template.Field.Integer pickupDelay = new Template.Field.Integer();

        public final Template.Method.Converted<ItemStackHandle> getItemStack = new Template.Method.Converted<ItemStackHandle>();
        public final Template.Method.Converted<Void> setItemStack = new Template.Method.Converted<Void>();

    }

}

