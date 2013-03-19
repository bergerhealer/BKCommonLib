package com.bergerkiller.bukkit.common.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Item;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.entity.minecart.PoweredMinecart;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.entity.minecart.StorageMinecart;

import net.minecraft.server.v1_5_R1.Entity;

import com.bergerkiller.bukkit.common.controller.DefaultEntityController;
import com.bergerkiller.bukkit.common.controller.EntityController;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.entity.nms.NMSEntity;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.proxies.EntityProxy;
import com.bergerkiller.bukkit.common.reflection.SafeConstructor;

public class CommonEntityStore<T extends org.bukkit.entity.Entity> extends EntityProxy<T> {
	private static final Map<Class<?>, SafeConstructor<?>> commonEntities = new HashMap<Class<?>, SafeConstructor<?>>();

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static <T extends org.bukkit.entity.Entity> void register(Class<T> bukkitType, Class<? extends CommonEntity<T>> commonType) {
		commonEntities.put(bukkitType, new SafeConstructor(commonType, bukkitType));
	}

	static {
		register(ExplosiveMinecart.class, CommonMinecartTNT.class);
		register(RideableMinecart.class, CommonMinecartRideable.class);
		register(HopperMinecart.class, CommonMinecartHopper.class);
		register(PoweredMinecart.class, CommonMinecartFurnace.class);
		register(StorageMinecart.class, CommonMinecartChest.class);
		register(Item.class, CommonItem.class);
	}

	public CommonEntityStore(T base) {
		super(base);
	}

	public static boolean hasController(org.bukkit.entity.Entity entity) {
		return getController(entity) != null;
	}

	/**
	 * Gets the potential Entity Controller that is attached to a given Entity
	 * 
	 * @param entity to check
	 * @return the Entity Controller, or null if none is attached
	 */
	public static EntityController<?> getController(org.bukkit.entity.Entity entity) {
		final Object handle = Conversion.toEntityHandle.convert(entity);
		if (handle instanceof NMSEntity) {
			final EntityController<?> controller = ((NMSEntity) handle).getController();
			if (!(controller instanceof DefaultEntityController)) {
				return controller;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T extends org.bukkit.entity.Entity> CommonEntity<T> get(T entity) {
		final Entity handle = CommonNMS.getNative(entity);
		if (handle instanceof NMSEntity) {
			return (CommonEntity<T>) ((NMSEntity) handle).getController().getEntity();
		}
		SafeConstructor<?> constr = commonEntities.get(entity.getClass());
		if (constr == null) {
			for (Entry<Class<?>, SafeConstructor<?>> entry : commonEntities.entrySet()) {
				if (entry.getKey().isInstance(entity)) {
					constr = entry.getValue();
					break;
				}
			}
		}
		if (constr == null) {
			return new CommonEntityUnknown<T>(entity);
		} else {
			return (CommonEntity<T>) constr.newInstance(entity);
		}
	}
}
