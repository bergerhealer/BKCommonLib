package com.bergerkiller.bukkit.common;

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
		CommonPlugin.getInstance().registerMap(this);
	}

	public EntityMap(int initialCapacity) {
		super(initialCapacity);
		CommonPlugin.getInstance().registerMap(this);
	}

	public EntityMap(Map<? extends K, ? extends T> m) {
		super(m);
		CommonPlugin.getInstance().registerMap(this);
	}
}
