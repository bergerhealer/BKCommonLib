package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.generated.net.minecraft.server.ItemStackHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;

import org.bukkit.Material;

public class NMSItemStack {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("ItemStack");
    public static final FieldAccessor<Integer> data   = ItemStackHandle.T.durabilityField.toFieldAccessor();
    public static final FieldAccessor<Integer> amount = ItemStackHandle.T.amountField.toFieldAccessor();
    public static final FieldAccessor<CommonTagCompound> tag = ItemStackHandle.T.tagField.toFieldAccessor();

    @Deprecated
    public static Object newInstance(int typeId, int data, int amount) {
        return newInstance(Material.getMaterial(typeId), data, amount);
    }

    public static Object newInstance(Material type, int data, int amount) {
        // Why is Bukkit unable to create proper constructors? Really? -,-
        ItemStackHandle instance;
        if (ItemStackHandle.T.constr_type_amount_durability_convert.isAvailable()) {
            instance = ItemStackHandle.T.constr_type_amount_durability_convert.newInstance(Material.STONE, 1, 0, false);
        } else {
            instance = ItemStackHandle.createNew(Material.STONE, 1, 0);
        }
        instance.setTypeField(type);
        instance.setDurabilityField(data);
        instance.setAmountField(amount);
        return instance.getRaw();
    }
}
