package com.bergerkiller.bukkit.common.entity;

import com.bergerkiller.bukkit.common.controller.DefaultEntityController;
import com.bergerkiller.bukkit.common.controller.EntityController;
import com.bergerkiller.bukkit.common.controller.EntityNetworkController;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.entity.nms.NMSEntityHook;
import com.bergerkiller.bukkit.common.entity.nms.NMSEntityTrackerEntry;
import com.bergerkiller.bukkit.common.proxies.EntityProxy;
import com.bergerkiller.bukkit.common.utils.WorldUtil;

public class CommonEntityStore<T extends org.bukkit.entity.Entity> extends EntityProxy<T> {

	public CommonEntityStore(T base) {
		super(base);
	}

	public static boolean hasController(org.bukkit.entity.Entity entity) {
		return getController(entity) != null;
	}

	/**
	 * Gets the potential Entity Network Controller that is attached to a given Entity
	 * 
	 * @param entity to check
	 * @return the Entity Network Controller, or null if none is attached
	 */
	public static EntityNetworkController<?> getNetworkController(org.bukkit.entity.Entity entity) {
		final Object entry = WorldUtil.getTracker(entity.getWorld()).getEntry(entity);
		if (entry instanceof NMSEntityTrackerEntry) {
			return ((NMSEntityTrackerEntry) entry).getController();
		} else {
			return null;
		}
	}

	/**
	 * Gets the potential Entity Controller that is attached to a given Entity
	 * 
	 * @param entity to check
	 * @return the Entity Controller, or null if none is attached
	 */
	public static EntityController<?> getController(org.bukkit.entity.Entity entity) {
		final Object handle = Conversion.toEntityHandle.convert(entity);
		if (handle instanceof NMSEntityHook) {
			final EntityController<?> controller = ((NMSEntityHook) handle).getController();
			if (!(controller instanceof DefaultEntityController)) {
				return controller;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T extends org.bukkit.entity.Entity> CommonEntity<T> get(T entity) {
		final Object handle = Conversion.toEntityHandle.convert(entity);
		if (handle instanceof NMSEntityHook) {
			return (CommonEntity<T>) ((NMSEntityHook) handle).getController().getEntity();
		}
		return CommonEntityTypeStore.byNMSEntity(handle).createCommonEntity(entity);
	}
}
