package com.bergerkiller.bukkit.common.entity;

import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;

import com.bergerkiller.bukkit.common.controller.EntityInventoryController;

/**
 * An entity that has an Inventory
 */
public interface CommonEntityInventory<T extends Entity> {
	public Inventory getInventory();

	/**
	 * Updates the Inventory (can mean: Update items to viewers)
	 */
	public void update();

	public void setInventoryController(EntityInventoryController<CommonEntity<T>> controller);

	public EntityInventoryController<CommonEntity<T>> getInventoryController();
}
