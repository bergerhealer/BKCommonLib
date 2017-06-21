package com.bergerkiller.bukkit.common.internal.hooks;

import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.controller.EntityController;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
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

    public boolean base_onInteractBy(Object entityHuman, Object enumHand) {
        if (EntityHandle.T.onInteractBy_old.isAvailable()) {
            MainHand hand = Conversion.toMainHand.convert(enumHand);
            HumanEntity human = (HumanEntity) WrapperConversion.toEntity(entityHuman);
            ItemStack item = null;
            if (hand == MainHand.LEFT) {
                item = human.getInventory().getItemInOffHand();
            } else {
                item = human.getInventory().getItemInMainHand();
            }
            return base.onInteractBy_old(entityHuman, HandleConversion.toItemStackHandle(item), enumHand);
        } else {
            return base.onInteractBy(entityHuman, enumHand);
        }
    }

    @HookMethod(value="public boolean onInteractBy:???(EntityHuman entityhuman, EnumHand enumhand)", optional=true)
    public boolean onInteractBy(Object entityHuman, Object enumHand) {
        try {
            if (checkController()) {
                return controller.onInteractBy((HumanEntity) Conversion.toEntity.convert(entityHuman), Conversion.toMainHand.convert(enumHand));
            } else {
                return base_onInteractBy(entityHuman, enumHand);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
    }

    @Deprecated
    @HookMethod(value="public boolean onInteractBy_old:???(EntityHuman entityhuman, ItemStack itemstack, EnumHand enumhand)", optional=true)
    public boolean onInteractBy_old(Object entityHuman, Object itemstack, Object enumHand) {
        return onInteractBy(entityHuman, enumHand);
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
                if (EntityHandle.IS_NEW_MOVE_FUNCTION) {
                    controller.onMove(MoveType.getFromHandle(enumMoveType), dx, dy, dz);
                } else {
                    controller.onMove(MoveType.SELF, dx, dy, dz);
                }
            } else {
                if (EntityHandle.IS_NEW_MOVE_FUNCTION) {
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
        try {
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
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
    }

    /* This key is used for later de-serializing the entity */
    private final String getSavedName() {
        Object key = NMSEntityTypes.getName(this.instanceBaseType());
        return key == null ? null : key.toString();
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
