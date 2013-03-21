package com.bergerkiller.bukkit.common.controller;

import net.minecraft.server.v1_5_R2.Entity;
import net.minecraft.server.v1_5_R2.IInventory;

import org.bukkit.craftbukkit.v1_5_R2.entity.CraftEntity;
import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.entity.nms.NMSEntityHook;
import com.bergerkiller.bukkit.common.entity.nms.NMSEntityInventoryHook;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.utils.CommonUtil;

public final class DefaultEntityInventoryController<T extends CommonEntity<?>> extends EntityInventoryController<T> {

	public DefaultEntityInventoryController() {
	}

	@SuppressWarnings("unchecked")
	public DefaultEntityInventoryController(NMSEntityHook entity) {
		bind((T) CommonEntity.get(CraftEntity.getEntity(CommonUtil.getCraftServer(), (Entity) entity)));
	}

	@Override
	public void onItemSet(int index, ItemStack item) {
		final Object handle = entity.getHandle();
		if (handle instanceof NMSEntityInventoryHook) {
			super.onItemSet(index, item);
		} else {
			((IInventory) handle).setItem(index, CommonNMS.getNative(item));
		}
	}
}
