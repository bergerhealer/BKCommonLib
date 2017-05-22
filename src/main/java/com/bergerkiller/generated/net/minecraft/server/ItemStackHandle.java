package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class ItemStackHandle extends Template.Handle {
    public static final ItemStackClass T = new ItemStackClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(ItemStackHandle.class, "net.minecraft.server.ItemStack");


    /* ============================================================================== */

    public static ItemStackHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        ItemStackHandle handle = new ItemStackHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public Object getItem() {
        return T.getItem.invoke(instance);
    }

    public String getName() {
        return T.getName.invoke(instance);
    }

    public ItemStackHandle setName(String s) {
        return T.setName.invoke(instance, s);
    }

    public int getRepairCost() {
        return T.getRepairCost.invoke(instance);
    }

    public void setRepairCost(int cost) {
        T.setRepairCost.invoke(instance, cost);
    }

    public boolean hasName() {
        return T.hasName.invoke(instance);
    }

    public boolean hasTag() {
        return T.hasTag.invoke(instance);
    }

    public CommonTagCompound getTag() {
        return T.getTag.invoke(instance);
    }

    public CommonTagCompound saveToNBT(CommonTagCompound compound) {
        return T.saveToNBT.invoke(instance, compound);
    }

    public static ItemStackHandle fromBukkit(org.bukkit.inventory.ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        } else {
            return createHandle(com.bergerkiller.bukkit.common.conversion.type.HandleConversion.toItemStackHandle(itemStack));
        }
    }

    public static final class ItemStackClass extends Template.Class<ItemStackHandle> {
        public final Template.Method.Converted<Object> getItem = new Template.Method.Converted<Object>();
        public final Template.Method<String> getName = new Template.Method<String>();
        public final Template.Method.Converted<ItemStackHandle> setName = new Template.Method.Converted<ItemStackHandle>();
        public final Template.Method<Integer> getRepairCost = new Template.Method<Integer>();
        public final Template.Method<Void> setRepairCost = new Template.Method<Void>();
        public final Template.Method<Boolean> hasName = new Template.Method<Boolean>();
        public final Template.Method<Boolean> hasTag = new Template.Method<Boolean>();
        public final Template.Method.Converted<CommonTagCompound> getTag = new Template.Method.Converted<CommonTagCompound>();
        public final Template.Method.Converted<CommonTagCompound> saveToNBT = new Template.Method.Converted<CommonTagCompound>();

    }
}
