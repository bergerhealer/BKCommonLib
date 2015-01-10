package com.bergerkiller.bukkit.common.entity.nms;

import net.minecraft.server.v1_8_R1.ItemStack;

import com.bergerkiller.bukkit.common.controller.DefaultEntityInventoryController;
import com.bergerkiller.bukkit.common.controller.EntityInventoryController;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.internal.CommonNMS;

public class NMSEntityInventoryHookImpl implements NMSEntityInventoryHook {

    private EntityInventoryController<?> controller;

    public NMSEntityInventoryHookImpl(CommonEntity<?> entity) {
        this.controller = new DefaultEntityInventoryController();
        this.controller.bind(entity);
    }

    @Override
    public EntityInventoryController<?> getInventoryController() {
        return controller;
    }

    @Override
    public void setInventoryController(EntityInventoryController<?> controller) {
        this.controller = controller;
    }

    @Override
    public void setItem(int index, ItemStack item) {
        this.controller.onItemSet(index, CommonNMS.getItemStack(item));
    }

    @Override
    public void super_setItem(int index, ItemStack item) {
        this.controller.getEntity().getHandle(NMSEntityInventoryHook.class).super_setItem(index, item);
    }
}
