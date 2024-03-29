package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.generated.net.minecraft.world.item.ItemStackHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.SafeDirectField;

import org.bukkit.Material;

@Deprecated
public class NMSItemStack {
    public static final ClassTemplate<?> T = ClassTemplate.create(ItemStackHandle.T.getType());
    public static final FieldAccessor<Integer> data   = new SafeDirectField<Integer>() {
        @Override
        public Integer get(Object instance) {
            return ItemStackHandle.createHandle(instance).getDurability();
        }

        @Override
        public boolean set(Object instance, Integer value) {
            ItemStackHandle.createHandle(instance).setDurability(value.intValue());
            return true;
        }
    };
    public static final FieldAccessor<Integer> amount = ItemStackHandle.T.amountField.toFieldAccessor();
    public static final FieldAccessor<CommonTagCompound> tag = ItemStackHandle.T.tagField.toFieldAccessor();

    public static Object newInstance(Material type, int data, int amount) {
        ItemStackHandle instance = ItemStackHandle.newInstance(type);
        instance.setDurability(data);
        instance.setAmountField(amount);
        return instance.getRaw();
    }
}
