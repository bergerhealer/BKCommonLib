package com.bergerkiller.bukkit.common.entity.nms;

import com.bergerkiller.bukkit.common.controller.EntityInventoryController;

import net.minecraft.server.v1_5_R2.ItemStack;

public interface NMSEntityInventoryHook extends NMSEntityHook {
	public EntityInventoryController<?> getInventoryController();

	public void setInventoryController(EntityInventoryController<?> controller);

	public void super_setItem(int index, ItemStack item);
}
