package com.bergerkiller.bukkit.common.wrappers;

import org.bukkit.entity.Entity;

import com.bergerkiller.bukkit.common.reflection.classes.MobSpawnerAbstractRef;

public class AbstractMobSpawner extends BasicWrapper {
	
	public AbstractMobSpawner(Object handle) {
		setHandle(handle);
	}
	
	public String getMobName() {
		return MobSpawnerAbstractRef.getMobName.invoke(getHandle());
	}
	
	public void onTick() {
		MobSpawnerAbstractRef.onTick.invoke(getHandle());
	}
	
	public int getSpawnDelay() {
		return MobSpawnerAbstractRef.spawnDelay.get(getHandle());
	}
	
	public void setSpawnDelay(int value) {
		MobSpawnerAbstractRef.spawnDelay.set(getHandle(), value);
	}
	
	public int getMinSpawnDelay() {
		return MobSpawnerAbstractRef.minSpawnDelay.get(getHandle());
	}
	
	public void setMinSpawnDelay(int value) {
		MobSpawnerAbstractRef.minSpawnDelay.set(getHandle(), value);
	}
	
	public int getMaxSpawnDelay() {
		return MobSpawnerAbstractRef.maxSpawnDelay.get(getHandle());
	}
	
	public void setMaxSpawnDelay(int value) {
		MobSpawnerAbstractRef.maxSpawnDelay.set(getHandle(), value);
	}
	
	public int getSpawnCount() {
		return MobSpawnerAbstractRef.spawnCount.get(getHandle());
	}
	
	public void setSpawnCount(int value) {
		MobSpawnerAbstractRef.spawnCount.set(getHandle(), value);
	}
	
	public Entity getEntity() {
		return MobSpawnerAbstractRef.entity.get(getHandle());
	}
	
	public void setEntity(Entity entity) {
		MobSpawnerAbstractRef.entity.set(getHandle(), entity);
	}
}