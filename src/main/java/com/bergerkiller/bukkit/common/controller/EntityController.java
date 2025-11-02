package com.bergerkiller.bukkit.common.controller;

import org.bukkit.Chunk;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.entity.CommonEntityController;
import com.bergerkiller.bukkit.common.internal.hooks.EntityHook;
import com.bergerkiller.bukkit.common.internal.logic.EntityMoveHandler;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;
import com.bergerkiller.bukkit.common.wrappers.InteractionResult;
import com.bergerkiller.bukkit.common.wrappers.MoveType;
import com.bergerkiller.generated.net.minecraft.world.level.chunk.ChunkHandle;

public abstract class EntityController<T extends CommonEntity<?>> extends CommonEntityController<T> {
    private EntityHook hook = null;
    private final EntityMoveHandler moveHandler = EntityMoveHandler.create(this);

    /**
     * Binds this Entity Controller to an Entity. This is called from elsewhere,
     * and should be ignored entirely.
     *
     * @param entity to bind with
     * @param handleAttachment whether to fire {@link #onAttached()}
     */
    @SuppressWarnings("unchecked")
    public final void bind(CommonEntity<?> entity, boolean handleAttachment) {
        if (entity == null && this.hook == null) {
            throw new RuntimeException("WTF");
        }
        if (entity != null && entity.getWorld() == null) {
            throw new RuntimeException("Can not bind to an Entity that has no world set");
        }
        if (this.entity != null) {
            this.onDetached();
            this.markEntityChunkDirty();
        }
        this.entity = (T) entity;
        if (this.entity != null) {
            this.hook = EntityHook.get(this.entity.getHandle(), EntityHook.class);
            if (this.hook == null) {
                this.hook = new EntityHook();
                this.hook.mock(this.entity.getHandle());
            }
            this.hook.setController(this);
            if (handleAttachment) {
                this.onAttached();
            }
            if (!(this instanceof DefaultEntityController)) {
                this.markEntityChunkDirty();
            }
        }
    }

    private void markEntityChunkDirty() {
        Chunk chunk = this.entity.getChunk();
        if (chunk != null) {
            ChunkHandle.fromBukkit(chunk).markEntitiesDirty();
        }
    }

    /**
     * Called when this Entity dies (could be called more than one time).
     * This is not called when the entity is despawned because chunks unload,
     * or when the entity is teleported to another world. Overriding the method
     * and not calling <pre>super.onDie(killed)</pre> allows the death to be cancelled.
     * 
     * @param killed Whether the entity was killed (true), or was destroyed
     *        for another reason (item collect, re-spawned). Not used on
     *        Minecraft 1.16.5 and before, where this will always be true.
     */
    public void onDie(boolean killed) {
        this.hook.onBaseDeath(killed);
    }

    /**
     * Called every tick to update the entity
     */
    public void onTick() {
        this.hook.base.onTick();
    }

    /**
     * Called when the entity is interacted by something
     *
     * @param interacter that interacted
     * @param hand that is used
     * @return True if interaction occurred, False if not
     */
    public InteractionResult onInteractBy(HumanEntity interacter, HumanHand hand) {
        return this.hook.base_onInteractBy(interacter, hand);
    }

    /**
     * Called when the entity is damaged by something
     *
     * @param damageSource of the damage
     * @param damage amount
     */
    public boolean onDamage(com.bergerkiller.bukkit.common.wrappers.DamageSource damageSource, double damage) {
        return this.hook.baseDamageEntity(damageSource.getRawHandle(), (float) damage);
    }

    /**
     * Handles the collision of this minecart with another Entity
     *
     * @param e entity with which is collided
     * @return True if collision is allowed, False if it is ignored
     */
    public boolean onEntityCollision(org.bukkit.entity.Entity e) {
        return true;
    }

    /**
     * Handles the collision of another Entity with this minecart.
     * Prior to calling this callback the {@link #onEntityCollision(Entity)} is called
     * as well. The default implementation executes the 'bump' code of this entity,
     * which handles the velocity changes when other entities bump into this entity.
     * This can be overrided for custom velocity adjustments.<br>
     * <br>
     * Passengers of this Entity can never bump with this Entity, and if this Entity
     * is inside a vehicle, it also cannot be bumped with.
     * 
     * @param e The entity that is bumping into this Entity
     */
    public void onEntityBump(org.bukkit.entity.Entity e) {
        hook.base.collide(HandleConversion.toEntityHandle(e));
    }

    /**
     * Handles the collision of this minecart with a Block
     *
     * @param block with which this minecart collided
     * @param hitFace of the block that the minecart hit
     * @return True if collision is allowed, False if it is ignored
     */
    public boolean onBlockCollision(org.bukkit.block.Block block, BlockFace hitFace) {
        return true;
    }

    /**
     * Gets the localized name of this Entity. Override this method to change
     * the name.
     *
     * @return Localized name
     */
    public String getLocalizedName() {
        return hook.getStringUUID_base(this.getEntity().getHandle());
    }

    public void onPush(double dx, double dy, double dz) {
        hook.baseOnPush(dx, dy, dz);
    }

    /**
     * Performs Entity movement logic, to move the Entity and handle collisions
     *
     * @param moveType mode of moving
     * @param dx offset to move
     * @param dy offset to move
     * @param dz offset to move
     */
    public void onMove(MoveType moveType, double dx, double dy, double dz) {
        moveHandler.move(this.entity.getWrappedHandle(), moveType, dx, dy ,dz);
        //hook.base.onMove(com.bergerkiller.generated.net.minecraft.world.entity.EnumMoveTypeHandle.SELF.getRaw(), dx, dy, dz);
    }

    /**
     * Called when an item in the inventory changes, if the entity has an inventory
     * 
     * @param index of the item
     * @param item it was set to
     */
    public void onItemSet(int index, ItemStack item) {
        hook.base.setInventoryItem(index, HandleConversion.toItemStackHandle(item));
    }

    /**
     * Called when the position of a passenger of this Entity must be calculated. By default
     * positions the entity in the vanilla way. The applier should be used to set the position
     * of the entity. Do not use teleport!
     *
     * @param passenger Passenger entity
     * @param applier Position applier
     */
    public void onPositionPassenger(Entity passenger, EntityPositionApplier applier) {
        hook.basePositionRider(passenger, applier);
    }

    /**
     * Gets whether the Player "take vehicle with" is enabled or not for this Entity. If enabled,
     * when players that are a passenger log off they will be saved along with this vehicle
     * (if they are the sole passenger). If not, then they will be ejected first.<br>
     * <br>
     * When disabled (false), this will also make the Vanilla RootVehicle tag work when multiple
     * players are inside this vehicle.
     *
     * @return True if this vehicle can be taken by players when they log off
     */
    public boolean isPlayerTakeable() {
        return true;
    }

    /**
     * Sets whether block collisions are handled during movement of the entity.
     * Disabling block collisions causes entities to fall through the floor, but may
     * result in a significant boost in performance.
     * 
     * @param enabled
     */
    public void setBlockCollisionEnabled(boolean enabled) {
        this.moveHandler.setBlockCollisionEnabled(enabled);
    }

    /**
     * Sets whether blocks like pressure plates and detector rails are activated during the movement
     * of the entity. Disabling block activation will cause these blocks to stop working, but may
     * result in a significant boost in performance.
     * 
     * @param enabled
     */
    public void setBlockActivationEnabled(boolean enabled) {
        this.moveHandler.setBlockActivationEnabled(enabled);
    }

    /**
     * Sets whether entity collisions are handled during movement of the entity.
     * Disabling entity collisions causes entities to go right through other entities
     * without being affected, and may result in a significant boost in performance.
     * 
     * @param enabled
     */
    public void setEntityCollisionEnabled(boolean enabled) {
        this.moveHandler.setEntityCollisionEnabled(enabled);
    }

    /**
     * Sets custom axis-aligned bounding box x/y/z size dimensions when handling block
     * collisions. If using a larger bounding box, changing the block collision bounding
     * box helps fixing significant stress on the server handling a lot more block collision
     * events than usual.
     * 
     * @param bounds size vector to set to, <i>null</i> for defaults
     */
    public void setBlockCollisionBounds(Vector bounds) {
        this.moveHandler.setCustomBlockCollisionBounds(bounds);
    }
}
