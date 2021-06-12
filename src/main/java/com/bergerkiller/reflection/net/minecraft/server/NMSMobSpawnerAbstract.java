package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.resources.MinecraftKeyHandle;
import com.bergerkiller.generated.net.minecraft.world.level.MobSpawnerAbstractHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;
import com.bergerkiller.mountiplex.reflection.SafeDirectField;

import org.bukkit.entity.Entity;

import java.util.List;

@Deprecated
public class NMSMobSpawnerAbstract {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("MobSpawnerAbstract");

    public static final FieldAccessor<Integer> spawnDelay = MobSpawnerAbstractHandle.T.spawnDelay.toFieldAccessor();
    public static final FieldAccessor<List<Object>> mobs = CommonUtil.unsafeCast(MobSpawnerAbstractHandle.T.mobs.raw.toFieldAccessor());
    public static final FieldAccessor<Object> spawnData = MobSpawnerAbstractHandle.T.spawnData.raw.toFieldAccessor();

    public static final FieldAccessor<Integer> minSpawnDelay = MobSpawnerAbstractHandle.T.minSpawnDelay.toFieldAccessor();
    public static final FieldAccessor<Integer> maxSpawnDelay = MobSpawnerAbstractHandle.T.maxSpawnDelay.toFieldAccessor();
    public static final FieldAccessor<Integer> spawnCount = MobSpawnerAbstractHandle.T.spawnCount.toFieldAccessor();

    public static final FieldAccessor<Entity> entity = MobSpawnerAbstractHandle.T.entity.toFieldAccessor();

    public static final FieldAccessor<Integer> maxNearbyEntities = MobSpawnerAbstractHandle.T.maxNearbyEntities.toFieldAccessor();
    public static final FieldAccessor<Integer> requiredPlayerRange = MobSpawnerAbstractHandle.T.requiredPlayerRange.toFieldAccessor();
    public static final FieldAccessor<Integer> spawnRange = MobSpawnerAbstractHandle.T.spawnRange.toFieldAccessor();

    public static final MethodAccessor<Void> onTick = MobSpawnerAbstractHandle.T.onTick.toMethodAccessor();

    public static final FieldAccessor<Object> mobMinecraftKey = new SafeDirectField<Object>() {
		@Override
		public Object get(Object instance) {
			return MobSpawnerAbstractHandle.T.getMobName.invoke(instance).getRaw();
		}

		@Override
		public boolean set(Object instance, Object value) {
		    MobSpawnerAbstractHandle.T.setMobName.invoke(instance, MinecraftKeyHandle.createHandle(value));
		    return true;
		}
    };

    public static final FieldAccessor<String> mobName = new SafeDirectField<String>() {
		@Override
		public String get(Object instance) {
			return NMSMinecraftKey.getCombinedName(mobMinecraftKey.get(instance));
		}

		@Override
		public boolean set(Object instance, String value) {
			return mobMinecraftKey.set(instance, NMSMinecraftKey.newInstance(value));
		}
    };
}
