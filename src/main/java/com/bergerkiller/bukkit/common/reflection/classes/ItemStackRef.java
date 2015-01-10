package com.bergerkiller.bukkit.common.reflection.classes;

import net.minecraft.server.v1_8_R1.Blocks;

import org.bukkit.Material;

import com.bergerkiller.bukkit.common.conversion.ConversionPairs;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import com.bergerkiller.bukkit.common.reflection.SafeConstructor;
import com.bergerkiller.bukkit.common.utils.CommonUtil;

public class ItemStackRef {

    public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("ItemStack");
    public static final FieldAccessor<Integer> data = TEMPLATE.getField("damage");
    public static final FieldAccessor<Object> type = TEMPLATE.getField("item");
    public static final FieldAccessor<Integer> amount = TEMPLATE.getField("count");
    public static final FieldAccessor<CommonTagCompound> tag = CommonUtil.unsafeCast(TEMPLATE.getField("tag").translate(ConversionPairs.commonTag));
    private static final SafeConstructor<?> constructor1 = TEMPLATE.getConstructor(BlockRef.TEMPLATE.getType(), int.class, int.class);

    @Deprecated
    public static Object newInstance(int typeId, int data, int amount) {
        return newInstance(Material.getMaterial(typeId), data, amount);
    }

    public static Object newInstance(Material type, int data, int amount) {
        // Why is Bukkit unable to create proper constructors? Really? -,-
        Object instance = constructor1.newInstance(Blocks.STONE, 1, 1);
        ItemStackRef.type.set(instance, CommonNMS.getItem(type));
        ItemStackRef.data.set(instance, data);
        ItemStackRef.amount.set(instance, amount);
        return instance;
    }
}
