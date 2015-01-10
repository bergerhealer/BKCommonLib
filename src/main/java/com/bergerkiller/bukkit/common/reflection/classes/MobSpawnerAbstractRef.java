package com.bergerkiller.bukkit.common.reflection.classes;

import java.util.List;

import org.bukkit.entity.Entity;

import com.bergerkiller.bukkit.common.conversion.ConversionPairs;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.FieldAccessor;
import com.bergerkiller.bukkit.common.reflection.MethodAccessor;
import com.bergerkiller.bukkit.common.reflection.NMSClassTemplate;

public class MobSpawnerAbstractRef {

    public static final ClassTemplate<Object> TEMPLATE = new NMSClassTemplate("MobSpawnerAbstract");
    public static final FieldAccessor<Integer> spawnDelay = TEMPLATE.getField("spawnDelay");
    public static final FieldAccessor<List<Object>> mobs = TEMPLATE.getField("mobs");
    public static final FieldAccessor<Object> spawnData = TEMPLATE.getField("spawnData");
    public static final FieldAccessor<Integer> minSpawnDelay = TEMPLATE.getField("minSpawnDelay");
    public static final FieldAccessor<Integer> maxSpawnDelay = TEMPLATE.getField("maxSpawnDelay");
    public static final FieldAccessor<Integer> spawnCount = TEMPLATE.getField("spawnCount");
    public static final FieldAccessor<Entity> entity = TEMPLATE.getField("j").translate(ConversionPairs.entity);
    public static final FieldAccessor<Integer> maxNearbyEntities = TEMPLATE.getField("maxNearbyEntities");
    public static final FieldAccessor<Integer> requiredPlayerRange = TEMPLATE.getField("requiredPlayerRange");
    public static final FieldAccessor<Integer> spawnRange = TEMPLATE.getField("spawnRange");

    public static final MethodAccessor<String> getMobName = TEMPLATE.getMethod("getMobName");
    public static final MethodAccessor<Void> setMobName = TEMPLATE.getMethod("a", String.class);
    public static final MethodAccessor<Void> onTick = TEMPLATE.getMethod("g");
}
