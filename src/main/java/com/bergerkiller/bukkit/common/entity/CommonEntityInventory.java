package com.bergerkiller.bukkit.common.entity;

import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;

import com.bergerkiller.bukkit.common.controller.EntityInventoryController;

/**
 * An entity that has an Inventory
 */
public interface CommonEntityInventory<T extends Entity> {
	public Inventory getInventory();

	public void setInventoryController(EntityInventoryController<CommonEntity<T>> controller);

	public EntityInventoryController<CommonEntity<T>> getInventoryController();
}
