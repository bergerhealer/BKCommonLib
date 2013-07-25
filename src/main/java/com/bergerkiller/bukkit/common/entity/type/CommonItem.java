package com.bergerkiller.bukkit.common.entity.type;

import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.entity.CommonEntity;

/**
 * A Common Entity implementation for Items
 */
public class CommonItem extends CommonEntity<Item> {

	public CommonItem(Item base) {
		super(base);
	}

	public int getPickupDelay() {
		return entity.getPickupDelay();
	}

	public void setPickupDelay(int tickDelay) {
		entity.setPickupDelay(tickDelay);
	}

	public ItemStack getItemStack() {
		return entity.getItemStack();
	}

	public void setItemStack(ItemStack item) {
		entity.setItemStack(item);
	}
}
