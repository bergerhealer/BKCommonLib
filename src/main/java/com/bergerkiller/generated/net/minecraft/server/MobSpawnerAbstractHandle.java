package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.List;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import org.bukkit.entity.Entity;

public class MobSpawnerAbstractHandle extends Template.Handle {
    public static final MobSpawnerAbstractClass T = new MobSpawnerAbstractClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(MobSpawnerAbstractHandle.class, "net.minecraft.server.MobSpawnerAbstract");

    /* ============================================================================== */

    public static MobSpawnerAbstractHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        MobSpawnerAbstractHandle handle = new MobSpawnerAbstractHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public void onTick() {
        T.onTick.invoke(instance);
    }

    public MinecraftKeyHandle getMobName() {
        return T.getMobName.invoke(instance);
    }

    public void setMobName(MinecraftKeyHandle nameKey) {
        T.setMobName.invoke(instance, nameKey);
    }

    public int getSpawnDelay() {
        return T.spawnDelay.getInteger(instance);
    }

    public void setSpawnDelay(int value) {
        T.spawnDelay.setInteger(instance, value);
    }

    public List<MobSpawnerDataHandle> getMobs() {
        return T.mobs.get(instance);
    }

    public void setMobs(List<MobSpawnerDataHandle> value) {
        T.mobs.set(instance, value);
    }

    public MobSpawnerDataHandle getSpawnData() {
        return T.spawnData.get(instance);
    }

    public void setSpawnData(MobSpawnerDataHandle value) {
        T.spawnData.set(instance, value);
    }

    public int getMinSpawnDelay() {
        return T.minSpawnDelay.getInteger(instance);
    }

    public void setMinSpawnDelay(int value) {
        T.minSpawnDelay.setInteger(instance, value);
    }

    public int getMaxSpawnDelay() {
        return T.maxSpawnDelay.getInteger(instance);
    }

    public void setMaxSpawnDelay(int value) {
        T.maxSpawnDelay.setInteger(instance, value);
    }

    public int getSpawnCount() {
        return T.spawnCount.getInteger(instance);
    }

    public void setSpawnCount(int value) {
        T.spawnCount.setInteger(instance, value);
    }

    public Entity getEntity() {
        return T.entity.get(instance);
    }

    public void setEntity(Entity value) {
        T.entity.set(instance, value);
    }

    public int getMaxNearbyEntities() {
        return T.maxNearbyEntities.getInteger(instance);
    }

    public void setMaxNearbyEntities(int value) {
        T.maxNearbyEntities.setInteger(instance, value);
    }

    public int getRequiredPlayerRange() {
        return T.requiredPlayerRange.getInteger(instance);
    }

    public void setRequiredPlayerRange(int value) {
        T.requiredPlayerRange.setInteger(instance, value);
    }

    public int getSpawnRange() {
        return T.spawnRange.getInteger(instance);
    }

    public void setSpawnRange(int value) {
        T.spawnRange.setInteger(instance, value);
    }

    public static final class MobSpawnerAbstractClass extends Template.Class<MobSpawnerAbstractHandle> {
        public final Template.Field.Integer spawnDelay = new Template.Field.Integer();
        public final Template.Field.Converted<List<MobSpawnerDataHandle>> mobs = new Template.Field.Converted<List<MobSpawnerDataHandle>>();
        public final Template.Field.Converted<MobSpawnerDataHandle> spawnData = new Template.Field.Converted<MobSpawnerDataHandle>();
        public final Template.Field.Integer minSpawnDelay = new Template.Field.Integer();
        public final Template.Field.Integer maxSpawnDelay = new Template.Field.Integer();
        public final Template.Field.Integer spawnCount = new Template.Field.Integer();
        public final Template.Field.Converted<Entity> entity = new Template.Field.Converted<Entity>();
        public final Template.Field.Integer maxNearbyEntities = new Template.Field.Integer();
        public final Template.Field.Integer requiredPlayerRange = new Template.Field.Integer();
        public final Template.Field.Integer spawnRange = new Template.Field.Integer();

        public final Template.Method<Void> onTick = new Template.Method<Void>();
        public final Template.Method.Converted<MinecraftKeyHandle> getMobName = new Template.Method.Converted<MinecraftKeyHandle>();
        public final Template.Method.Converted<Void> setMobName = new Template.Method.Converted<Void>();

    }

}

