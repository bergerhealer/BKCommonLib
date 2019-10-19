package com.bergerkiller.bukkit.common.utils;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.internal.logic.EntityAddRemoveHandler;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.server.EntityHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityHangingHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityLivingHandle;
import com.bergerkiller.generated.net.minecraft.server.GenericAttributesHandle;
import com.bergerkiller.generated.net.minecraft.server.WorldServerHandle;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.LivingEntity;

import java.util.UUID;

public class EntityUtil extends EntityPropertyUtil {

    /**
     * Finds an Entity that has the given entity UUID and is of the type
     * specified from a world
     *
     * @param world to find the Entity
     * @param uid of the Entity to find
     * @param type Class of the Entity to find
     * @return the found Entity, or null if not found
     */
    public static <T extends org.bukkit.entity.Entity> T getEntity(org.bukkit.World world, UUID uid, Class<T> type) {
        return CommonUtil.tryCast(getEntity(world, uid), type);
    }

    /**
     * Finds an Entity that has the given entity UUID from a world
     *
     * @param world to find the Entity
     * @param uid of the Entity to find
     * @return the found Entity, or null if not found
     */
    public static org.bukkit.entity.Entity getEntity(org.bukkit.World world, UUID uid) {
        for (org.bukkit.entity.Entity entity : WorldUtil.getEntities(world)) {
            if (entity.getUniqueId().equals(uid)) {
                return entity;
            }
        }
        return null;
    }

    /**
     * Adds a single entity to the server
     *
     * @param entity to add
     */
    public static void addEntity(org.bukkit.entity.Entity entity) {
        EntityHandle nmsentity = CommonNMS.getHandle(entity);
        WorldServerHandle nmsworld = nmsentity.getWorldServer();
        entity.getWorld().getChunkAt(MathUtil.toChunk(nmsentity.getLocX()), MathUtil.toChunk(nmsentity.getLocZ()));
        nmsentity.setDead(false);
        // Remove an entity tracker for this entity if it was present
        nmsworld.getEntityTracker().stopTracking(entity);
        // Add the entity to the world
        nmsworld.addEntity(nmsentity);
        // Process add-related events right now
        EntityAddRemoveHandler.INSTANCE.processEvents();
    }

    /**
     * Changes the speed of a living entity
     *
     * @param entity Entity
     * @param speed New entity speed
     */
    public static void setSpeed(LivingEntity entity, double speed) {
        EntityLivingHandle nmsEntity = CommonNMS.getHandle(entity);
        nmsEntity.getAttributeInstance(GenericAttributesHandle.MOVEMENT_SPEED).setValue(speed);
    }

    /**
     * Gets the speed of a living entity
     *
     * @param entity to check speed
     * @return entity speed
     */
    public static double getSpeed(LivingEntity entity) {
        EntityLivingHandle nmsEntity = CommonNMS.getHandle(entity);
        return nmsEntity.getAttributeInstance(GenericAttributesHandle.MOVEMENT_SPEED).getValue();
    }

    /**
     * <b>Deprecated: there are no plans to support this</b>
     */
    @Deprecated
    public static boolean isIgnored(org.bukkit.entity.Entity entity) {
        return false;
    }

    /*
     * Is near something?
     */
    public static boolean isNearChunk(org.bukkit.entity.Entity entity, final int cx, final int cz, final int chunkview) {
        final int x = MathUtil.toChunk(getLocX(entity)) - cx;
        final int z = MathUtil.toChunk(getLocZ(entity)) - cz;
        return Math.abs(x) <= chunkview && Math.abs(z) <= chunkview;
    }

    public static boolean isNearBlock(org.bukkit.entity.Entity entity, final int bx, final int bz, final int blockview) {
        final int x = MathUtil.floor(getLocX(entity) - bx);
        final int z = MathUtil.floor(getLocZ(entity) - bz);
        return Math.abs(x) <= blockview && Math.abs(z) <= blockview;
    }

    /**
     * Performs entity on entity collision logic for an entity. This will
     * perform the push logic caused by collision.
     *
     * @param entity to work on
     * @param with the entity to collide
     */
    public static void doCollision(org.bukkit.entity.Entity entity, org.bukkit.entity.Entity with) {
        EntityHandle.fromBukkit(entity).collide(EntityHandle.fromBukkit(with));
    }

    /**
     * Teleports an entity in the next tick
     *
     * @param entity to teleport
     * @param to location to teleport to
     */
    public static void teleportNextTick(final org.bukkit.entity.Entity entity, final Location to) {
        CommonUtil.nextTick(() -> teleport(entity, to));
    }

    /**
     * Teleports an entity
     *
     * @param entity to teleport
     * @param to location to teleport to
     */
    public static boolean teleport(final org.bukkit.entity.Entity entity, final Location to) {
        return CommonEntity.get(entity).teleport(to);
    }

    /**
     * Gets a new Entity Id that will be unique during the current server session run
     * 
     * @return entity Id
     */
    public static int getUniqueEntityId() {
        if (EntityHandle.T.opt_atomic_entityCount.isAvailable()) {
            return EntityHandle.T.opt_atomic_entityCount.get().incrementAndGet();
        } else {
            int id = EntityHandle.T.opt_int_entityCount.getInteger();
            EntityHandle.T.opt_int_entityCount.setInteger(id + 1);
            return id;
        }
    }

    /**
     * Gets the Block a hanging entity is attached to
     * 
     * @param entityHanging
     * @return block
     */
    public static Block getHangingBlock(Hanging entityHanging) {
        IntVector3 pos = EntityHangingHandle.T.blockPosition.get(Conversion.toEntityHandle.convert(entityHanging));
        return pos.toBlock(entityHanging.getWorld());
    }

    /**
     * Gets the Data Watcher of an Entity
     * 
     * @return entity data watcher
     */
    public static DataWatcher getDataWatcher(org.bukkit.entity.Entity entity) {
        return EntityHandle.T.getDataWatcher.invoke(HandleConversion.toEntityHandle(entity));
    }
}
