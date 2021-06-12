package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.generated.net.minecraft.resources.MinecraftKeyHandle;
import org.bukkit.entity.Entity;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.MobSpawnerAbstract</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.server.MobSpawnerAbstract")
public abstract class MobSpawnerAbstractHandle extends Template.Handle {
    /** @See {@link MobSpawnerAbstractClass} */
    public static final MobSpawnerAbstractClass T = Template.Class.create(MobSpawnerAbstractClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static MobSpawnerAbstractHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract void onTick();
    public abstract MinecraftKeyHandle getMobName();
    public abstract void setMobName(MinecraftKeyHandle nameKey);
    public abstract int getSpawnDelay();
    public abstract void setSpawnDelay(int value);
    public abstract List<MobSpawnerDataHandle> getMobs();
    public abstract void setMobs(List<MobSpawnerDataHandle> value);
    public abstract MobSpawnerDataHandle getSpawnData();
    public abstract void setSpawnData(MobSpawnerDataHandle value);
    public abstract int getMinSpawnDelay();
    public abstract void setMinSpawnDelay(int value);
    public abstract int getMaxSpawnDelay();
    public abstract void setMaxSpawnDelay(int value);
    public abstract int getSpawnCount();
    public abstract void setSpawnCount(int value);
    public abstract Entity getEntity();
    public abstract void setEntity(Entity value);
    public abstract int getMaxNearbyEntities();
    public abstract void setMaxNearbyEntities(int value);
    public abstract int getRequiredPlayerRange();
    public abstract void setRequiredPlayerRange(int value);
    public abstract int getSpawnRange();
    public abstract void setSpawnRange(int value);
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

