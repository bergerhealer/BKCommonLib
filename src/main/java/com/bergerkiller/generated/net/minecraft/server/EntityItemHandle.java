package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class EntityItemHandle extends EntityHandle {
    public static final EntityItemClass T = new EntityItemClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EntityItemHandle.class, "net.minecraft.server.EntityItem");

    /* ============================================================================== */

    public static EntityItemHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        EntityItemHandle handle = new EntityItemHandle();
        handle.instance = handleInstance;
        return handle;
    }

    public static final EntityItemHandle createNew(WorldHandle world, double x, double y, double z, ItemStackHandle itemstack) {
        return T.constr_world_x_y_z_itemstack.newInstance(world, x, y, z, itemstack);
    }

    /* ============================================================================== */

    public ItemStackHandle getItemStack() {
        return T.getItemStack.invoke(instance);
    }

    public void setItemStack(ItemStackHandle itemstack) {
        T.setItemStack.invoke(instance, itemstack);
    }

    public int getAge() {
        return T.age.getInteger(instance);
    }

    public void setAge(int value) {
        T.age.setInteger(instance, value);
    }

    public int getPickupDelay() {
        return T.pickupDelay.getInteger(instance);
    }

    public void setPickupDelay(int value) {
        T.pickupDelay.setInteger(instance, value);
    }

    public static final class EntityItemClass extends Template.Class<EntityItemHandle> {
        public final Template.Constructor.Converted<EntityItemHandle> constr_world_x_y_z_itemstack = new Template.Constructor.Converted<EntityItemHandle>();

        public final Template.Field.Integer age = new Template.Field.Integer();
        public final Template.Field.Integer pickupDelay = new Template.Field.Integer();

        public final Template.Method.Converted<ItemStackHandle> getItemStack = new Template.Method.Converted<ItemStackHandle>();
        public final Template.Method.Converted<Void> setItemStack = new Template.Method.Converted<Void>();

    }

}

