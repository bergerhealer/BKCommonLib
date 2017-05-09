package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.SafeConstructor;
import com.bergerkiller.mountiplex.reflection.TranslatorFieldAccessor;

import org.bukkit.Material;

public class NMSItemStack {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("ItemStack");
    public static final FieldAccessor<Integer> data   = T.selectField("private int damage");
    private static final TranslatorFieldAccessor<Material> type  = T.selectField("private Item item").translate(DuplexConversion.item);
    public static final FieldAccessor<Integer> amount = T.selectField("private int count");
    public static final FieldAccessor<CommonTagCompound> tag = T.selectField("private NBTTagCompound tag").translate(DuplexConversion.commonTagCompound);

    private static final SafeConstructor<?> constructor1 = T.getConstructor(NMSBlock.T.getType(), int.class, int.class);

    @Deprecated
    public static Object newInstance(int typeId, int data, int amount) {
        return newInstance(Material.getMaterial(typeId), data, amount);
    }

    public static Object newInstance(Material type, int data, int amount) {
        // Why is Bukkit unable to create proper constructors? Really? -,-
        Object instance = constructor1.newInstance(Conversion.toBlockHandle.convert(Material.STONE), 1, 1);
        NMSItemStack.type.set(instance, type);
        NMSItemStack.data.set(instance, data);
        NMSItemStack.amount.set(instance, amount);
        return instance;
    }
}
