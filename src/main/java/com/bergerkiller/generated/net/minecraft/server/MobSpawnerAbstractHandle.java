package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.entity.Entity;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.MobSpawnerAbstract</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class MobSpawnerAbstractHandle extends Template.Handle {
    /** @See {@link MobSpawnerAbstractClass} */
    public static final MobSpawnerAbstractClass T = new MobSpawnerAbstractClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(MobSpawnerAbstractHandle.class, "net.minecraft.server.MobSpawnerAbstract");

    /* ============================================================================== */

    public static MobSpawnerAbstractHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public void onTick() {
        T.onTick.invoke(getRaw());
    }

    public MinecraftKeyHandle getMobName() {
        return T.getMobName.invoke(getRaw());
    }

    public void setMobName(MinecraftKeyHandle nameKey) {
        T.setMobName.invoke(getRaw(), nameKey);
    }

    public int getSpawnDelay() {
        return T.spawnDelay.getInteger(getRaw());
    }

    public void setSpawnDelay(int value) {
        T.spawnDelay.setInteger(getRaw(), value);
    }

    public List<MobSpawnerDataHandle> getMobs() {
        return T.mobs.get(getRaw());
    }

    public void setMobs(List<MobSpawnerDataHandle> value) {
        T.mobs.set(getRaw(), value);
    }

    public MobSpawnerDataHandle getSpawnData() {
        return T.spawnData.get(getRaw());
    }

    public void setSpawnData(MobSpawnerDataHandle value) {
        T.spawnData.set(getRaw(), value);
    }

    public int getMinSpawnDelay() {
        return T.minSpawnDelay.getInteger(getRaw());
    }

    public void setMinSpawnDelay(int value) {
        T.minSpawnDelay.setInteger(getRaw(), value);
    }

    public int getMaxSpawnDelay() {
        return T.maxSpawnDelay.getInteger(getRaw());
    }

    public void setMaxSpawnDelay(int value) {
        T.maxSpawnDelay.setInteger(getRaw(), value);
    }

    public int getSpawnCount() {
        return T.spawnCount.getInteger(getRaw());
    }

    public void setSpawnCount(int value) {
        T.spawnCount.setInteger(getRaw(), value);
    }

    public Entity getEntity() {
        return T.entity.get(getRaw());
    }

    public void setEntity(Entity value) {
        T.entity.set(getRaw(), value);
    }

    public int getMaxNearbyEntities() {
        return T.maxNearbyEntities.getInteger(getRaw());
    }

    public void setMaxNearbyEntities(int value) {
        T.maxNearbyEntities.setInteger(getRaw(), value);
    }

    public int getRequiredPlayerRange() {
        return T.requiredPlayerRange.getInteger(getRaw());
    }

    public void setRequiredPlayerRange(int value) {
        T.requiredPlayerRange.setInteger(getRaw(), value);
    }

    public int getSpawnRange() {
        return T.spawnRange.getInteger(getRaw());
    }

    public void setSpawnRange(int value) {
        T.spawnRange.setInteger(getRaw(), value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.MobSpawnerAbstract</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
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

