package com.bergerkiller.bukkit.common.controller;

import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.entity.CommonEntityController;
import com.bergerkiller.bukkit.common.entity.nms.NMSEntityInventoryHook;
import com.bergerkiller.bukkit.common.internal.CommonNMS;

public class EntityInventoryController<T extends CommonEntity<?>> extends CommonEntityController<T> {
	/**
	 * Binds this Entity Inventory Controller to an Entity.
	 * This is called from elsewhere, and should be ignored entirely.
	 * 
	 * @param entity to bind with
	 */
	@SuppressWarnings("unchecked")
	public final void bind(CommonEntity<?> entity) {
		if (this.entity != null) {
			this.onDetached();
		}
		this.entity = (T) entity;
		if (this.entity != null) {
			final Object handle = this.entity.getHandle();
			if (handle instanceof NMSEntityInventoryHook) {
				((NMSEntityInventoryHook) handle).setInventoryController(this);
			}
			if (this.entity.isSpawned()) {
				this.onAttached();
			}
		}
	}

	public void onItemSet(int index, ItemStack item) {
		entity.getHandle(NMSEntityInventoryHook.class).super_setItem(index, CommonNMS.getNative(item)); 
	}
}
