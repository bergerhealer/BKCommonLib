package com.bergerkiller.bukkit.common.reflection.classes;

import org.bukkit.craftbukkit.v1_5_R1.inventory.CraftItemStack;

import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.SafeConstructor;
import com.bergerkiller.bukkit.common.reflection.SafeField;
import com.bergerkiller.bukkit.common.utils.CommonUtil;

@SuppressWarnings({"unchecked", "rawtypes"})
public class CraftItemStackRef {
	public static final FieldAccessor<Object> handle = new SafeField<Object>(CraftItemStack.class, "handle");
	private static final SafeConstructor handleConstr = new SafeConstructor(CommonUtil.getNMSClass("ItemStack"), int.class, int.class, int.class);

	public static Object newHandleInstance(int typeId, int data, int amount) {
		return handleConstr.newInstance(typeId, amount, data);
	}
}
