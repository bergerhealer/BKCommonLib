package com.bergerkiller.bukkit.common.entity.nms;

import com.bergerkiller.bukkit.common.controller.EntityInventoryController;

import net.minecraft.server.v1_8_R1.ItemStack;

public interface NMSEntityInventoryHook {

    public EntityInventoryController<?> getInventoryController();

    public void setInventoryController(EntityInventoryController<?> controller);

    /**
     * onItemSet
     */
    public void setItem(int index, ItemStack item);

    /**
     * onItemSet super
     */
    public void super_setItem(int index, ItemStack item);
}
