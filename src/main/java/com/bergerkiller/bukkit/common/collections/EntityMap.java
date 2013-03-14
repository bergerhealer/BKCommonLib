package com.bergerkiller.bukkit.common.collections;

import java.util.Map;
import java.util.WeakHashMap;

import org.bukkit.entity.Entity;

import com.bergerkiller.bukkit.common.internal.CommonPlugin;

/**
 * A HashMap that allows binding data to live entities without causing memory issues<br>
 * Entities are directly stored, when an entity is removed from the server it's binding is lost as well<br>
 * If you wish to map in a persistent way, use an identifier key (id, UUID, name) of an entity instead
 */
public class EntityMap<K extends Entity, T> extends WeakHashMap<K, T> {

	public EntityMap() {
		super();
		register();
	}

	public EntityMap(int initialCapacity) {
		super(initialCapacity);
		register();
	}

	public EntityMap(Map<? extends K, ? extends T> m) {
		super(m);
		register();
	}

	private void register() {
		CommonPlugin plugin = CommonPlugin.getInstance();
		if (plugin == null) {
			throw new IllegalStateException("Can not initialize a new EntityMap when BKCommonLib is not yet enabled!");
		}
		plugin.registerMap(this);
	}
}
