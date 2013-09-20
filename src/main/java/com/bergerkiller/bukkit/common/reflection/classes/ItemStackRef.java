package com.bergerkiller.bukkit.common.reflection.classes;

import com.bergerkiller.bukkit.common.conversion.ConversionPairs;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;
import com.bergerkiller.bukkit.common.reflection.SafeConstructor;
import com.bergerkiller.bukkit.common.utils.CommonUtil;

public class ItemStackRef {
	public static final ClassTemplate<?> TEMPLATE = NMSClassTemplate.create("ItemStack");
	public static final FieldAccessor<Integer> data = TEMPLATE.getField("damage");
	public static final FieldAccessor<Integer> typeId = TEMPLATE.getField("id");
	public static final FieldAccessor<Integer> amount = TEMPLATE.getField("count");
	public static final FieldAccessor<CommonTagCompound> tag = CommonUtil.unsafeCast(TEMPLATE.getField("tag").translate(ConversionPairs.commonTag));
	private static final SafeConstructor<?> constructor1 = TEMPLATE.getConstructor(int.class, int.class, int.class);

	public static Object newInstance(int typeId, int data, int amount) {
		// Why is Bukkit unable to create proper constructors? Really? -,-
		Object instance = constructor1.newInstance(1, 1, 1);
		ItemStackRef.typeId.set(instance, typeId);
		ItemStackRef.data.set(instance, data);
		ItemStackRef.amount.set(instance, amount);
		return instance;
	}
}
