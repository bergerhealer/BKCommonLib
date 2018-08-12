package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.generated.net.minecraft.server.ItemStackHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;

import org.bukkit.Material;

@Deprecated
public class NMSItemStack {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("ItemStack");
    public static final FieldAccessor<Integer> data   = ItemStackHandle.T.durabilityField.toFieldAccessor();
    public static final FieldAccessor<Integer> amount = ItemStackHandle.T.amountField.toFieldAccessor();
    public static final FieldAccessor<CommonTagCompound> tag = ItemStackHandle.T.tagField.toFieldAccessor();

    public static Object newInstance(Material type, int data, int amount) {
        ItemStackHandle instance = ItemStackHandle.newInstance();
        instance.setTypeField(type);
        instance.setDurability(data);
        instance.setAmountField(amount);
        return instance.getRaw();
    }
}
