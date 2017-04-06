package com.bergerkiller.bukkit.common.internal.hooks;

import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.controller.EntityController;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.wrappers.MoveType;
import com.bergerkiller.reflection.ClassHook;
import com.bergerkiller.reflection.net.minecraft.server.NMSEntity;
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

    @HookMethod("public boolean b(EntityHuman entityhuman, EnumHand enumhand)")
    public boolean onInteractBy(Object entityHuman, Object enumHand) {
        return controller.onInteractBy((HumanEntity) Conversion.toEntity.convert(entityHuman), Conversion.toMainHand.convert(enumHand));
    }

    @HookMethod("public boolean damageEntity(DamageSource damagesource, float f)")
    public boolean onDamageEntity(Object damageSource, float damage) {
        return controller.onDamage(com.bergerkiller.bukkit.common.wrappers.DamageSource.getForHandle(damageSource), damage);
    }

    @HookMethod("public void A_()")
    public void onTick() {
        if (controller == null) {
            Logging.LOGGER.once(Level.SEVERE , "Incorrect state: no controller assigned! Creator:", stack);
        }
        controller.onTick();
    }

    @HookMethod("protected void burn(float i)")
    public void onBurn(float damage) {
        controller.onBurnDamage((double) damage);
    }

    @HookMethod("public void f(double d0, double d1, double d2)")
    public void onPush(double dx, double dy, double dz) {
        controller.onPush(dx, dy, dz);
    }

    @HookMethod("public void move(EnumMoveType enummovetype, double d0, double d1, double d2)")
    public void onMove(Object enumMoveType, double dx, double dy, double dz) {
        controller.onMove(MoveType.getFromHandle(enumMoveType), dx, dy, dz);
    }

    @HookMethod("public void die()")
    public void die() {
        controller.onDie();
    }

    @HookMethod("public String getName()")
    public String getName() {
        return controller.getLocalizedName();
    }

    @HookMethod("public net.minecraft.server.Entity teleportTo(org.bukkit.Location exit, boolean portal)")
    public Object teleportTo(Location exit, boolean portal) {
        return base.teleportTo(exit, portal);
    }

    @HookMethod("public NBTTagCompound e(NBTTagCompound nbttagcompound)")
    public Object saveEntity(Object nbtTag) {
        return base.saveEntity(nbtTag);
    }

    @HookMethod("public boolean c(NBTTagCompound nbttagcompound)")
    public boolean c(Object tag) {
        Object handle = this.instance();
        if (NMSEntity.dead.get(handle)) {
            return false;
        }

        CommonTagCompound.create(tag).putValue("id", getSavedName());
        saveEntity(tag);
        return true;
    }

    @HookMethod("public boolean d(NBTTagCompound nbttagcompound)")
    public boolean d(Object tag) {
        Object handle = this.instance();
        if (NMSEntity.dead.get(handle)) {
            return false;
        }
        if (NMSEntity.vehicleField.get(handle) != null) {
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
        if (controller.onEntityCollision(Conversion.toEntity.convert(entity))) {
            base.collide(entity);
        }
    }

    @HookMethod("public void setItem(int i, ItemStack itemstack)")
    public void setInventoryItem(int i, Object itemstack) {
        controller.onItemSet(i, Conversion.toItemStack.convert(itemstack));
    }
}
