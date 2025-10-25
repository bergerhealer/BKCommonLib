package com.bergerkiller.bukkit.common.utils;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.internal.logic.EntityAddRemoveHandler;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.server.level.WorldServerHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityLivingHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.ai.attributes.GenericAttributesHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.decoration.EntityHangingHandle;
import com.bergerkiller.generated.org.bukkit.inventory.PlayerInventoryHandle;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

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
        return LogicUtil.tryCast(getEntity(world, uid), type);
    }

    /**
     * Finds an Entity that has the given entity UUID from a world
     *
     * @param world to find the Entity
     * @param uuid of the Entity to find
     * @return the found Entity, or null if not found
     */
    public static org.bukkit.entity.Entity getEntity(org.bukkit.World world, UUID uuid) {
        return WorldServerHandle.fromBukkit(world).getEntityByUUID(uuid);
    }

    /**
     * Adds a single entity to the server
     *
     * @param entity to add
     */
    public static void addEntity(org.bukkit.entity.Entity entity) {
        World world = entity.getWorld();
        EntityHandle nmsentity = CommonNMS.getHandle(entity);
        WorldServerHandle nmsworld = nmsentity.getWorldServer();
        world.getChunkAt(MathUtil.toChunk(nmsentity.getLocX()), MathUtil.toChunk(nmsentity.getLocZ()));
        nmsentity.setDestroyed(false);
        // Remove an entity tracker for this entity if it was present
        nmsworld.getEntityTracker().stopTracking(entity);
        // Add the entity to the world
        nmsworld.addEntity(nmsentity);
        // Process add-related events right now
        EntityAddRemoveHandler.INSTANCE.processEvents(world);
    }

    /**
     * Changes the speed of a living entity
     *
     * @param entity Entity
     * @param speed New entity speed
     */
    public static void setSpeed(LivingEntity entity, double speed) {
        EntityLivingHandle nmsEntity = CommonNMS.getHandle(entity);
        nmsEntity.getAttribute(GenericAttributesHandle.MOVEMENT_SPEED).setBaseValue(speed);
    }

    /**
     * Gets the speed of a living entity
     *
     * @param entity to check speed
     * @return entity speed
     */
    public static double getSpeed(LivingEntity entity) {
        EntityLivingHandle nmsEntity = CommonNMS.getHandle(entity);
        return nmsEntity.getAttribute(GenericAttributesHandle.MOVEMENT_SPEED).getBaseValue();
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
        //EntityHandle.T.getLocX.invoke(h(
        EntityHandle handle = EntityHandle.fromBukkit(entity);
        final int x = MathUtil.toChunk(handle.getLocX()) - cx;
        final int z = MathUtil.toChunk(handle.getLocZ()) - cz;
        return Math.abs(x) <= chunkview && Math.abs(z) <= chunkview;
    }

    public static boolean isNearBlock(org.bukkit.entity.Entity entity, final int bx, final int bz, final int blockview) {
        EntityHandle handle = EntityHandle.fromBukkit(entity);
        final int x = MathUtil.floor(handle.getLocX() - bx);
        final int z = MathUtil.floor(handle.getLocZ() - bz);
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
        IntVector3 pos = EntityHangingHandle.T.getBlockPosition.invoke(HandleConversion.toEntityHandle(entityHanging));
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

    /**
     * Detects and applies changes to the equipment of an Entity. This sends equipment
     * packets to nearby viewers (or player itself) and properly applies attribute
     * changes the equipment causes (armor slots, bonus health, etc.).<br>
     * <br>
     * Normally occurs automatically every tick. Call this to apply it earlier, for example,
     * to refresh the max health property so that health updates work properly.
     *
     * @param livingEntity Living Entity to detect equipment changes for
     */
    public static void detectEquipmentChanges(LivingEntity livingEntity) {
        EntityLivingHandle.fromBukkit(livingEntity).detectEquipmentChanges();
    }

    /**
     * Gets the Item inside a given equipment slot of a HumanEntity/Player Inventory
     *
     * @param humanEntity Entity
     * @param slot EquipmentSlot
     * @return Item inside this slot
     */
    public static ItemStack getEquipment(HumanEntity humanEntity, EquipmentSlot slot) {
        return PlayerInventoryHandle.T.getItem.invoke(humanEntity.getInventory(), slot);
    }

    /**
     * Sets the Item inside a given equipment slot of a HumanEntity/Player Inventory
     *
     * @param humanEntity Entity
     * @param slot EquipmentSlot
     * @param item Item to set. Null to clear.
     */
    public static void setEquipment(HumanEntity humanEntity, EquipmentSlot slot, ItemStack item) {
        PlayerInventoryHandle.T.setItem.invoke(humanEntity.getInventory(), slot, item);
    }

    /**
     * Gets whether a particular equipment slot is supported by the entity specified.
     * If not, {@link #getEquipment(HumanEntity, EquipmentSlot)} will always return null,
     * and {@link #setEquipment(HumanEntity, EquipmentSlot, ItemStack)} will be a no-op.
     * Bukkits equipment methods should not be called if this returns false.
     * This should also be checked when interacting with the equipment slots of other
     * (living) entities.
     *
     * @param entity Entity
     * @param slot EquipmentSlot
     * @return True if this particular equipment slot is supported by the entity
     */
    public static boolean isEquipmentSupported(Entity entity, EquipmentSlot slot) {
        return com.bergerkiller.generated.org.bukkit.entity.EntityHandle.T.isEquipmentSlotSupported.invoke(entity, slot);
    }
}
