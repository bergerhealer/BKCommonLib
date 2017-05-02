package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.bukkit.common.conversion2.DuplexConversion;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;
import com.bergerkiller.mountiplex.reflection.SafeDirectField;

import org.bukkit.entity.Entity;

import java.util.List;

public class NMSMobSpawnerAbstract {
    public static final ClassTemplate<?> T = ClassTemplate.createNMS("MobSpawnerAbstract");

    public static final FieldAccessor<Integer> spawnDelay = T.nextField("public int spawnDelay");
    public static final FieldAccessor<List<Object>> mobs = T.nextField("private final List<MobSpawnerData> mobs");
    public static final FieldAccessor<Object> spawnData = T.nextField("private MobSpawnerData spawnData");

    public static final FieldAccessor<Integer> minSpawnDelay = T.nextField("private int minSpawnDelay");
    public static final FieldAccessor<Integer> maxSpawnDelay = T.nextField("private int maxSpawnDelay");
    public static final FieldAccessor<Integer> spawnCount = T.nextField("private int spawnCount");

    public static final FieldAccessor<Entity> entity = T.nextFieldSignature("private Entity i").translate(DuplexConversion.entity);

    public static final FieldAccessor<Integer> maxNearbyEntities = T.selectField("private int maxNearbyEntities");
    public static final FieldAccessor<Integer> requiredPlayerRange = T.selectField("private int requiredPlayerRange");
    public static final FieldAccessor<Integer> spawnRange = T.selectField("private int spawnRange");

    public static final MethodAccessor<Void> onTick = T.selectMethod("public void c()");

    public static final FieldAccessor<Object> mobMinecraftKey = new SafeDirectField<Object>() {
        private final MethodAccessor<Object> getMobMinecraftKey = T.selectMethod("public MinecraftKey getMobName()");
        private final MethodAccessor<Void> setMobMinecraftKey = T.selectMethod("public void setMobName(MinecraftKey minecraftkey)");

		@Override
		public Object get(Object instance) {
			return getMobMinecraftKey.invoke(instance);
		}

		@Override
		public boolean set(Object instance, Object value) {
			if (setMobMinecraftKey.isValid()) {
				setMobMinecraftKey.invoke(instance, value);
				return true;
			}
			return false;
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
