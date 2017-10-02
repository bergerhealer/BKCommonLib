package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import org.bukkit.Material;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.ItemStack</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class ItemStackHandle extends Template.Handle {
    /** @See {@link ItemStackClass} */
    public static final ItemStackClass T = new ItemStackClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(ItemStackHandle.class, "net.minecraft.server.ItemStack");

    /* ============================================================================== */

    public static ItemStackHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    public static final ItemStackHandle createNew(Material type, int amount, int durability) {
        return T.constr_type_amount_durability.newInstance(type, amount, durability);
    }

    /* ============================================================================== */

    public abstract Object getItem();
    public abstract String getName();
    public abstract ItemStackHandle setName(String s);
    public abstract int getRepairCost();
    public abstract void setRepairCost(int cost);
    public abstract boolean hasName();
    public abstract boolean hasTag();
    public abstract CommonTagCompound getTag();
    public abstract CommonTagCompound saveToNBT(CommonTagCompound compound);
    public abstract ItemStackHandle cloneAndSubtract(int n);
    public abstract ItemStackHandle cloneItemStack();

    public static final ItemStackHandle EMPTY_ITEM;
    static {
        if (T.OPT_EMPTY_ITEM.isAvailable()) {
            EMPTY_ITEM = T.OPT_EMPTY_ITEM.get();
        } else {
            EMPTY_ITEM = T.createHandle(null, true);
        }
    }


    public static ItemStackHandle fromBukkit(org.bukkit.inventory.ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        } else {
            return createHandle(com.bergerkiller.bukkit.common.conversion.type.HandleConversion.toItemStackHandle(itemStack));
        }
    }
    public abstract int getAmountField();
    public abstract void setAmountField(int value);
    public abstract Material getTypeField();
    public abstract void setTypeField(Material value);
    public abstract CommonTagCompound getTagField();
    public abstract void setTagField(CommonTagCompound value);
    public abstract int getDurabilityField();
    public abstract void setDurabilityField(int value);
    /**
     * Stores class members for <b>net.minecraft.server.ItemStack</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ItemStackClass extends Template.Class<ItemStackHandle> {
        public final Template.Constructor.Converted<ItemStackHandle> constr_type_amount_durability = new Template.Constructor.Converted<ItemStackHandle>();
        @Template.Optional
        public final Template.Constructor.Converted<ItemStackHandle> constr_type_amount_durability_convert = new Template.Constructor.Converted<ItemStackHandle>();

        @Template.Optional
        public final Template.StaticField.Converted<ItemStackHandle> OPT_EMPTY_ITEM = new Template.StaticField.Converted<ItemStackHandle>();

        public final Template.Field.Integer amountField = new Template.Field.Integer();
        public final Template.Field.Converted<Material> typeField = new Template.Field.Converted<Material>();
        public final Template.Field.Converted<CommonTagCompound> tagField = new Template.Field.Converted<CommonTagCompound>();
        public final Template.Field.Integer durabilityField = new Template.Field.Integer();

        @Template.Optional
        public final Template.Method<Boolean> isEmpty = new Template.Method<Boolean>();
        public final Template.Method.Converted<Object> getItem = new Template.Method.Converted<Object>();
        public final Template.Method<String> getName = new Template.Method<String>();
        public final Template.Method.Converted<ItemStackHandle> setName = new Template.Method.Converted<ItemStackHandle>();
        public final Template.Method<Integer> getRepairCost = new Template.Method<Integer>();
        public final Template.Method<Void> setRepairCost = new Template.Method<Void>();
        public final Template.Method<Boolean> hasName = new Template.Method<Boolean>();
        public final Template.Method<Boolean> hasTag = new Template.Method<Boolean>();
        public final Template.Method.Converted<CommonTagCompound> getTag = new Template.Method.Converted<CommonTagCompound>();
        public final Template.Method.Converted<CommonTagCompound> saveToNBT = new Template.Method.Converted<CommonTagCompound>();
        public final Template.Method.Converted<ItemStackHandle> cloneAndSubtract = new Template.Method.Converted<ItemStackHandle>();
        public final Template.Method.Converted<ItemStackHandle> cloneItemStack = new Template.Method.Converted<ItemStackHandle>();

    }

}

