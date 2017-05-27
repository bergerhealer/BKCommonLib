package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.generated.net.minecraft.server.ItemStackHandle;
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

    @Deprecated
    public static Object newInstance(int typeId, int data, int amount) {
        return newInstance(Material.getMaterial(typeId), data, amount);
    }

    public static Object newInstance(Material type, int data, int amount) {
        // Why is Bukkit unable to create proper constructors? Really? -,-
        ItemStackHandle instance = ItemStackHandle.createNew(Material.STONE, 1, 0, false);
        instance.setTypeField(type);
        instance.setDurabilityField(data);
        instance.setAmountField(amount);
        return instance.getRaw();
    }
}
