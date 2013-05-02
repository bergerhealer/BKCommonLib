package com.bergerkiller.bukkit.common.reflection.classes;

import org.bukkit.craftbukkit.v1_5_R3.util.FlatMap;

import com.bergerkiller.bukkit.common.reflection.ClassTemplate;

class SpigotDummyFlatMap extends FlatMap<Object> {
	private static final ClassTemplate<SpigotDummyFlatMap> TEMPLATE = ClassTemplate.create(SpigotDummyFlatMap.class);

	private SpigotDummyFlatMap() {
	}

	@Override
	public Object get(long msw, long lsw) {
		return null;
	}

	@Override
	public Object get(long key) {
		return null;
	}

	@Override
	public void put(long msw, long lsw, Object value) {
	}

	@Override
	public void put(long key, Object value) {
	}

	public static FlatMap<Object> newInstance() {
		return TEMPLATE.newInstanceNull();
	}
}
