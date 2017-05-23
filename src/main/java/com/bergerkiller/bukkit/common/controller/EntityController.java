package com.bergerkiller.bukkit.common.controller;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.entity.CommonEntityController;
import com.bergerkiller.bukkit.common.internal.hooks.EntityHook;
import com.bergerkiller.bukkit.common.internal.logic.EntityMoveHandler;
import com.bergerkiller.bukkit.common.utils.ItemUtil;
import com.bergerkiller.bukkit.common.wrappers.MoveType;

public abstract class EntityController<T extends CommonEntity<?>> extends CommonEntityController<T> {
    private EntityHook hook = null;
    private final EntityMoveHandler moveHandler = new EntityMoveHandler(this);

    /**
     * Binds this Entity Controller to an Entity. This is called from elsewhere,
     * and should be ignored entirely.
     *
     * @param entity to bind with
     */
    @SuppressWarnings("unchecked")
    public final void bind(CommonEntity<?> entity) {
        if (entity == null && this.hook == null) {
            throw new RuntimeException("WTF");
        }
        if (entity != null && entity.getWorld() == null) {
            throw new RuntimeException("Can not bind to an Entity that has no world set");
        }
        if (this.entity != null) {
            this.onDetached();
        }
        this.entity = (T) entity;
        if (this.entity != null) {
            this.hook = EntityHook.get(this.entity.getHandle(), EntityHook.class);
            if (this.hook == null) {
                this.hook = new EntityHook();
                this.hook.mock(this.entity.getHandle());
            }
            this.hook.setController(this);
            this.onAttached();
        }
    }

    /**
     * Called when this Entity dies (could be called more than one time)
     */
    public void onDie() {
        this.hook.base.die();
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
    public boolean onInteractBy(HumanEntity interacter, MainHand hand) {
        return this.hook.base.onInteractBy(Conversion.toEntityHandle.convert(interacter), Conversion.toMainHandHandle.convert(hand));
    }

    /**
     * Called when the entity is damaged by something
     *
     * @param damageSource of the damage
     * @param damage amount
     */
    public boolean onDamage(com.bergerkiller.bukkit.common.wrappers.DamageSource damageSource, double damage) {
        return this.hook.base.onDamageEntity(damageSource.getRawHandle(), (float) damage);
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
     * Fired when the entity is getting burned by something
     *
     * @param damage dealt
     */
    public void onBurnDamage(double damage) {
        hook.base.onBurn((float) damage);
    }

    /**
     * Gets whether this Entity Controller allows players to take this Entity
     * with them. With this enabled, players take the vehicle with them. To
     * disable this default behavior, override this method.
     *
     * @return True if players can take the entity with them, False if not
     */
    public boolean isPlayerTakable() {
        return true;
    }

    /**
     * Gets the localized name of this Entity. Override this method to change
     * the name.
     *
     * @return Localized name
     */
    public String getLocalizedName() {
        return hook.base.getName();
    }

    public void onPush(double dx, double dy, double dz) {
        hook.base.onPush(dx, dy, dz);
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
        moveHandler.move(moveType, dx, dy ,dz);
    	//hook.base.onMove(EnumMoveType.SELF, dx, dy, dz);
    }

    /**
     * Called when an item in the inventory changes, if the entity has an inventory
     * 
     * @param index of the item
     * @param item it was set to
     */
    public void onItemSet(int index, ItemStack item) {
        Object handle = Conversion.toItemStackHandle.convert(item);
        if (handle == null) {
            handle = Conversion.toItemStackHandle.convert(ItemUtil.emptyItem());
        }
        hook.base.setInventoryItem(index, handle);
    }
}
