package com.bergerkiller.bukkit.common.internal.hooks;

import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;

import com.bergerkiller.bukkit.common.controller.EntityController;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.wrappers.MoveType;
import com.bergerkiller.reflection.ClassHook;

public class EntityHook extends ClassHook<EntityHook> {
    private EntityController<?> controller = null;

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
