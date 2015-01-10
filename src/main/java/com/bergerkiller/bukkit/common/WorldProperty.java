package com.bergerkiller.bukkit.common;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.World;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;

public class WorldProperty<T> {

	private Map<String, T> worldmap = new HashMap<String, T>();
	private T def;

	public WorldProperty(T defaultValue) {
		this.def = defaultValue;
	}

	public T getDefault() {
		return this.def;
	}

	public T get(String worldname) {
		T value = this.worldmap.get(worldname.toLowerCase());
		return value == null ? this.def : value;
	}

	public T get(World world) {
		return this.get(world.getName());
	}

	public void set(T value) {
		this.def = value;
		this.worldmap.clear();
	}

	public void setDefault(T value) {
		this.def = value;
	}

	public void set(World world, T value) {
		this.set(world.getName(), value);
	}

	public void set(String worldname, T value) {
		this.worldmap.put(worldname.toLowerCase(), value);
	}

	@SuppressWarnings("unchecked")
	public void load(ConfigurationNode node) {
		this.def = node.get("default", this.def);
		for (String world : node.getKeys()) {
			if (!world.equals("default")) {
				T value = (T) node.get(world, this.def.getClass());
				if (value != null) {
					this.worldmap.put(world, value);
				} else {
					node.remove(world);
				}
			}
		}
	}
}
