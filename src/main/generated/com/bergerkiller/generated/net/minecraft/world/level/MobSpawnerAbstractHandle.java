package com.bergerkiller.generated.net.minecraft.world.level;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.generated.net.minecraft.resources.MinecraftKeyHandle;
import org.bukkit.World;
import org.bukkit.entity.Entity;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.MobSpawnerAbstract</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.level.MobSpawnerAbstract")
public abstract class MobSpawnerAbstractHandle extends Template.Handle {
    /** @See {@link MobSpawnerAbstractClass} */
    public static final MobSpawnerAbstractClass T = Template.Class.create(MobSpawnerAbstractClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static MobSpawnerAbstractHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract void onTick(World world, IntVector3 blockPosition);
    public abstract MinecraftKeyHandle getMobName();
    public abstract void setMobName(MinecraftKeyHandle nameKey);
    public abstract int getSpawnDelay();
    public abstract void setSpawnDelay(int value);
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
     * Stores class members for <b>net.minecraft.world.level.MobSpawnerAbstract</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class MobSpawnerAbstractClass extends Template.Class<MobSpawnerAbstractHandle> {
        public final Template.Field.Integer spawnDelay = new Template.Field.Integer();
        public final Template.Field.Integer minSpawnDelay = new Template.Field.Integer();
        public final Template.Field.Integer maxSpawnDelay = new Template.Field.Integer();
        public final Template.Field.Integer spawnCount = new Template.Field.Integer();
        public final Template.Field.Converted<Entity> entity = new Template.Field.Converted<Entity>();
        public final Template.Field.Integer maxNearbyEntities = new Template.Field.Integer();
        public final Template.Field.Integer requiredPlayerRange = new Template.Field.Integer();
        public final Template.Field.Integer spawnRange = new Template.Field.Integer();

        public final Template.Method.Converted<Void> onTick = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<MinecraftKeyHandle> getMobName = new Template.Method.Converted<MinecraftKeyHandle>();
        public final Template.Method.Converted<Void> setMobName = new Template.Method.Converted<Void>();

    }

}

