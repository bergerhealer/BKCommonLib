package com.bergerkiller.bukkit.common.internal.hooks;

import java.util.logging.Level;

import org.bukkit.entity.HumanEntity;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.controller.EntityController;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;
import com.bergerkiller.bukkit.common.wrappers.MoveType;
import com.bergerkiller.generated.net.minecraft.server.EntityHandle;
import com.bergerkiller.mountiplex.reflection.ClassHook;
import com.bergerkiller.reflection.net.minecraft.server.NMSEntityTypes;

public class EntityHook extends ClassHook<EntityHook> {
    private EntityController<?> controller = null;
    private Throwable stack = null;

    public void setStack(Throwable t) {
        this.stack = t;
    }

    private Throwable getStack() {
        if (this.stack == null) {
            this.stack = new Throwable();
        }
        return this.stack;
    }

    public boolean hasController() {
        return this.controller != null;
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

    public boolean base_onInteractBy(HumanEntity humanEntity, HumanHand humanHand) {
        Object entityHumanHandle = HandleConversion.toEntityHandle(humanEntity);
        if (EntityHandle.T.onInteractBy_1_11_2.isAvailable()) {
            return base.onInteractBy_1_11_2(entityHumanHandle, humanHand.toNMSEnumHand(humanEntity));
        } else if (EntityHandle.T.onInteractBy_1_9.isAvailable()) {
            Object item = HandleConversion.toItemStackHandle(HumanHand.getHeldItem(humanEntity, humanHand));
            return base.onInteractBy_1_10_2(entityHumanHandle, item, humanHand.toNMSEnumHand(humanEntity));
        } else if (EntityHandle.T.onInteractBy_1_8_8.isAvailable()) {
            return base.onInteractBy_1_8_8(entityHumanHandle);
        } else {
            throw new UnsupportedOperationException("Don't know what interact method is used!");
        }
    }

    @Deprecated
    @HookMethod(value="public boolean onInteractBy_1_8_8:???(EntityHuman entityhuman)", optional=true)
    public boolean onInteractBy_1_8_8(Object entityHuman) {
        return onInteractBy((HumanEntity) WrapperConversion.toEntity(entityHuman), HumanHand.RIGHT);
    }

    @Deprecated
    @HookMethod(value="public boolean onInteractBy_1_9:???(EntityHuman entityhuman, ItemStack itemstack, EnumHand enumhand)", optional=true)
    public boolean onInteractBy_1_10_2(Object entityHuman, Object itemstack, Object enumHand) {
        return onInteractBy_1_11_2(entityHuman, enumHand);
    }

    @Deprecated
    @HookMethod(value="public boolean onInteractBy_1_11_2:???(EntityHuman entityhuman, EnumHand enumhand)", optional=true)
    public boolean onInteractBy_1_11_2(Object entityHuman, Object enumHand) {
        HumanEntity humanEntity = (HumanEntity) WrapperConversion.toEntity(entityHuman);
        return onInteractBy(humanEntity, HumanHand.fromNMSEnumHand(humanEntity, enumHand));
    }

    public boolean onInteractBy(HumanEntity humanEntity, HumanHand humanHand) {
        try {
            if (checkController()) {
                return controller.onInteractBy(humanEntity, humanHand);
            } else {
                return base_onInteractBy(humanEntity, humanHand);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
    }

    @HookMethod("public boolean damageEntity(DamageSource damagesource, float f)")
    public boolean onDamageEntity(Object damageSource, float damage) {
        try {
            if (checkController()) {
                return controller.onDamage(com.bergerkiller.bukkit.common.wrappers.DamageSource.getForHandle(damageSource), damage);
            } else {
                return base.onDamageEntity(damageSource, damage);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
    }

    @HookMethod("public void onTick:???()")
    public void onTick() {
        try {
            if (checkController()) {
                controller.onTick();
            } else {
                base.onTick();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @HookMethod("protected void burn(float i)")
    public void onBurn(float damage) {
        try {
            if (checkController()) {
                controller.onBurnDamage((double) damage);
            } else {
                base.onBurn(damage);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @HookMethod("public void onPush:???(double d0, double d1, double d2)")
    public void onPush(double dx, double dy, double dz) {
        try {
            if (checkController()) {
                controller.onPush(dx, dy, dz);
            } else {
                base.onPush(dx, dy, dz);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @HookMethod(value="public void move(EnumMoveType enummovetype, double d0, double d1, double d2)", optional=true)
    public void onMove(Object enumMoveType, double dx, double dy, double dz) {
        try {
            if (checkController()) {
                if (CommonCapabilities.ENTITY_MOVE_VER2) {
                    controller.onMove(MoveType.getFromHandle(enumMoveType), dx, dy, dz);
                } else {
                    controller.onMove(MoveType.SELF, dx, dy, dz);
                }
            } else {
                if (CommonCapabilities.ENTITY_MOVE_VER2) {
                    base.onMove(enumMoveType, dx, dy, dz);
                } else {
                    base.onMove_old(dx, dy, dz);
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Deprecated
    @HookMethod(value="public void move(double d0, double d1, double d2)", optional=true)
    public void onMove_old(double dx, double dy, double dz) {
        this.onMove(MoveType.SELF.getHandle(), dx, dy, dz);
    }

    @HookMethod("public void die()")
    public void die() {
        try {
            if (checkController()) {
                controller.onDie();
            } else {
                base.die();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @HookMethod("public String getName()")
    public String getName() {
        try {
            if (checkController()) {
                return controller.getLocalizedName();
            } else {
                return base.getName();
            }
        } catch (Throwable t) {
            t.printStackTrace();
            return "ERROR";
        }
    }

    @HookMethod("public boolean savePassenger:???(NBTTagCompound nbttagcompound)")
    public boolean c(Object tag) {
        Object handle = this.instance();
        if (EntityHandle.T.dead.getBoolean(handle)) {
            return false;
        }

        CommonTagCompound commonTag = CommonTagCompound.create(tag);
        commonTag.putValue("id", getSavedName());
        EntityHandle.T.saveToNBT.invoke(handle, commonTag);
        return true;
    }

    @HookMethod("public boolean saveEntity:???(NBTTagCompound nbttagcompound)")
    public boolean d(Object tag) {
        try {
            Object handle = this.instance();
            if (EntityHandle.T.dead.getBoolean(handle)) {
                return false;
            }
            if (EntityHandle.T.vehicle.raw.get(handle) != null) {
                return false;
            }

            CommonTagCompound commonTag = CommonTagCompound.create(tag);
            commonTag.putValue("id", getSavedName());
            EntityHandle.T.saveToNBT.invoke(handle, commonTag);
            return true;
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
    }

    /* This key is used for later de-serializing the entity */
    private final String getSavedName() {
        return NMSEntityTypes.getName(this.instanceBaseType());
    }

    @HookMethod("public void collide(Entity entity)")
    public void collide(Object entity) {
        try {
            if (checkController()) {
                if (controller.onEntityCollision(Conversion.toEntity.convert(entity))) {
                    base.collide(entity);
                }
            } else {
                base.collide(entity);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @HookMethod(value="public void setItem(int i, ItemStack itemstack)", optional=true)
    public void setInventoryItem(int i, Object itemstack) {
        try {
            if (checkController()) {
                controller.onItemSet(i, Conversion.toItemStack.convert(itemstack));
            } else {
                base.setInventoryItem(i, itemstack);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private boolean checkController() {
        if (controller == null) {
            Logging.LOGGER.once(Level.SEVERE , "Incorrect state: no controller assigned! Creator:", getStack());
            return false;
        } else if (controller.getEntity() == null) {
            Logging.LOGGER.once(Level.SEVERE, "Incorrect state: controller " + controller.getClass().getName() + " has no entity bound to it! Creator:", getStack());
            return false;
        } else {
            return true;
        }
    }
}
