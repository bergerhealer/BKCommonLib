package com.bergerkiller.generated.net.minecraft.world.level;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.generated.net.minecraft.resources.IdentifierHandle;
import org.bukkit.World;
import org.bukkit.entity.Entity;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.BaseSpawner</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.level.BaseSpawner")
public abstract class BaseSpawnerHandle extends Template.Handle {
    /** @see BaseSpawnerClass */
    public static final BaseSpawnerClass T = Template.Class.create(BaseSpawnerClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static BaseSpawnerHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract void onTick(World world, IntVector3 blockPosition);
    public abstract IdentifierHandle getMobName();
    public abstract void setMobName(IdentifierHandle nameKey);
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
     * Stores class members for <b>net.minecraft.world.level.BaseSpawner</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class BaseSpawnerClass extends Template.Class<BaseSpawnerHandle> {
        public final Template.Field.Integer spawnDelay = new Template.Field.Integer();
        public final Template.Field.Integer minSpawnDelay = new Template.Field.Integer();
        public final Template.Field.Integer maxSpawnDelay = new Template.Field.Integer();
        public final Template.Field.Integer spawnCount = new Template.Field.Integer();
        public final Template.Field.Converted<Entity> entity = new Template.Field.Converted<Entity>();
        public final Template.Field.Integer maxNearbyEntities = new Template.Field.Integer();
        public final Template.Field.Integer requiredPlayerRange = new Template.Field.Integer();
        public final Template.Field.Integer spawnRange = new Template.Field.Integer();

        public final Template.Method.Converted<Void> onTick = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<IdentifierHandle> getMobName = new Template.Method.Converted<IdentifierHandle>();
        public final Template.Method.Converted<Void> setMobName = new Template.Method.Converted<Void>();

    }

}

