package com.bergerkiller.bukkit.common.internal.hooks;

import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.controller.EntityController;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.wrappers.MoveType;
import com.bergerkiller.generated.net.minecraft.server.EntityHandle;
import com.bergerkiller.mountiplex.reflection.ClassHook;
import com.bergerkiller.reflection.net.minecraft.server.NMSEntityTypes;

public class EntityHook extends ClassHook<EntityHook> {
    private EntityController<?> controller = null;
    public Throwable stack;

    public EntityHook() {
        this.stack = new Throwable();
    }

    public EntityController<?> getController() {
        if (this.controller == null) {
            throw new RuntimeException("Controller is never allowed to be null!");
        }
        return this.controller;
    }

    public void setController(EntityController<?> controller) {
        this.controller = controller;
    }

    @HookMethod("public boolean onInteractBy:???(EntityHuman entityhuman, EnumHand enumhand)")
    public boolean onInteractBy(Object entityHuman, Object enumHand) {
        if (checkController()) {
            return controller.onInteractBy((HumanEntity) Conversion.toEntity.convert(entityHuman), Conversion.toMainHand.convert(enumHand));
        } else {
            return base.onInteractBy(entityHuman, enumHand);
        }
    }

    @HookMethod("public boolean damageEntity(DamageSource damagesource, float f)")
    public boolean onDamageEntity(Object damageSource, float damage) {
        if (checkController()) {
            return controller.onDamage(com.bergerkiller.bukkit.common.wrappers.DamageSource.getForHandle(damageSource), damage);
        } else {
            return base.onDamageEntity(damageSource, damage);
        }
    }

    @HookMethod("public void onTick:???()")
    public void onTick() {
        if (checkController()) {
            controller.onTick();
        } else {
            base.onTick();
        }
    }

    @HookMethod("protected void burn(float i)")
    public void onBurn(float damage) {
        if (checkController()) {
            controller.onBurnDamage((double) damage);
        } else {
            base.onBurn(damage);
        }
    }

    @HookMethod("public void onPush:???(double d0, double d1, double d2)")
    public void onPush(double dx, double dy, double dz) {
        if (checkController()) {
            controller.onPush(dx, dy, dz);
        } else {
            base.onPush(dx, dy, dz);
        }
    }

    @HookMethod("public void move(EnumMoveType enummovetype, double d0, double d1, double d2)")
    public void onMove(Object enumMoveType, double dx, double dy, double dz) {
        if (checkController()) {
            controller.onMove(MoveType.getFromHandle(enumMoveType), dx, dy, dz);
        } else {
            base.onMove(enumMoveType, dx, dy, dz);
        }
    }

    @HookMethod("public void die()")
    public void die() {
        if (checkController()) {
            controller.onDie();
        } else {
            base.die();
        }
    }

    @HookMethod("public String getName()")
    public String getName() {
        if (checkController()) {
            return controller.getLocalizedName();
        } else {
            return base.getName();
        }
    }

    @HookMethod("public Entity teleportTo(org.bukkit.Location exit, boolean portal)")
    public Object teleportTo(Location exit, boolean portal) {
        return base.teleportTo(exit, portal);
    }

    @HookMethod("public NBTTagCompound saveToNBT:???(NBTTagCompound nbttagcompound)")
    public Object saveEntity(Object nbtTag) {
        return base.saveEntity(nbtTag);
    }

    @HookMethod("public boolean savePassenger:???(NBTTagCompound nbttagcompound)")
    public boolean c(Object tag) {
        Object handle = this.instance();
        if (EntityHandle.T.dead.getBoolean(handle)) {
            return false;
        }

        CommonTagCompound.create(tag).putValue("id", getSavedName());
        saveEntity(tag);
        return true;
    }

    @HookMethod("public boolean saveEntity:???(NBTTagCompound nbttagcompound)")
    public boolean d(Object tag) {
        Object handle = this.instance();
        if (EntityHandle.T.dead.getBoolean(handle)) {
            return false;
        }
        if (EntityHandle.T.vehicle.raw.get(handle) != null) {
            return false;
        }

        CommonTagCompound.create(tag).putValue("id", getSavedName());
        saveEntity(tag);
        return true;
    }

    /* This key is used for later de-serializing the entity */
    private final String getSavedName() {
        Object key = NMSEntityTypes.getName(this.instanceBaseType());
        return key == null ? null : key.toString();
    }

    @HookMethod("public void collide(Entity entity)")
    public void collide(Object entity) {
        if (checkController()) {
            if (controller.onEntityCollision(Conversion.toEntity.convert(entity))) {
                base.collide(entity);
            }
        } else {
            base.collide(entity);
        }
    }

    @HookMethod(value="public void setItem(int i, ItemStack itemstack)", optional=true)
    public void setInventoryItem(int i, Object itemstack) {
        if (checkController()) {
            controller.onItemSet(i, Conversion.toItemStack.convert(itemstack));
        } else {
            base.setInventoryItem(i, itemstack);
        }
    }

    private boolean checkController() {
        if (controller == null) {
            Logging.LOGGER.once(Level.SEVERE , "Incorrect state: no controller assigned! Creator:", stack);
            return false;
        } else if (controller.getEntity() == null) {
            Logging.LOGGER.once(Level.SEVERE, "Incorrect state: controller " + controller.getClass().getName() + " has no entity bound to it! Creator:", stack);
            return false;
        } else {
            return true;
        }
    }
}
