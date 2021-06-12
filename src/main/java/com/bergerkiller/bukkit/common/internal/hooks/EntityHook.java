package com.bergerkiller.bukkit.common.internal.hooks;

import java.util.List;
import java.util.logging.Level;

import org.bukkit.entity.HumanEntity;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.controller.EntityController;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;
import com.bergerkiller.bukkit.common.wrappers.InteractionResult;
import com.bergerkiller.bukkit.common.wrappers.MoveType;
import com.bergerkiller.generated.net.minecraft.locale.LocaleLanguageHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityItemHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityTypesHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.player.EntityHumanHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.Vec3DHandle;
import com.bergerkiller.mountiplex.reflection.ClassHook;

@ClassHook.HookPackage("net.minecraft.server")
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

    public InteractionResult base_onInteractBy(HumanEntity humanEntity, HumanHand humanHand) {
        Object entityHumanHandle = HandleConversion.toEntityHandle(humanEntity);
        if (EntityHandle.T.onInteractBy_1_16.isAvailable()) {
            return InteractionResult.fromHandle(base.onInteractBy_1_16(entityHumanHandle, humanHand.toNMSEnumHand(humanEntity)));
        } else if (EntityHandle.T.onInteractBy_1_11_2.isAvailable()) {
            return InteractionResult.fromTruthy(base.onInteractBy_1_11_2(entityHumanHandle, humanHand.toNMSEnumHand(humanEntity)));
        } else if (EntityHandle.T.onInteractBy_1_9.isAvailable()) {
            Object item = HandleConversion.toItemStackHandle(HumanHand.getHeldItem(humanEntity, humanHand));
            return InteractionResult.fromTruthy(base.onInteractBy_1_10_2(entityHumanHandle, item, humanHand.toNMSEnumHand(humanEntity)));
        } else if (EntityHandle.T.onInteractBy_1_8_8.isAvailable()) {
            return InteractionResult.fromTruthy(base.onInteractBy_1_8_8(entityHumanHandle));
        } else {
            throw new UnsupportedOperationException("Don't know what interact method is used!");
        }
    }

    @Deprecated
    @HookMethod(value="public boolean onInteractBy_1_8_8:???(EntityHuman entityhuman)", optional=true)
    public boolean onInteractBy_1_8_8(Object entityHuman) {
        return onInteractBy((HumanEntity) WrapperConversion.toEntity(entityHuman), HumanHand.RIGHT).isTruthy();
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
        return onInteractBy(humanEntity, HumanHand.fromNMSEnumHand(humanEntity, enumHand)).isTruthy();
    }

    @Deprecated
    @HookMethod(value="public EnumInteractionResult onInteractBy_1_16:???(EntityHuman entityhuman, EnumHand enumhand)", optional=true)
    public Object onInteractBy_1_16(Object entityHuman, Object enumHand) {
        HumanEntity humanEntity = (HumanEntity) WrapperConversion.toEntity(entityHuman);
        return onInteractBy(humanEntity, HumanHand.fromNMSEnumHand(humanEntity, enumHand)).getRawHandle();
    }

    public InteractionResult onInteractBy(HumanEntity humanEntity, HumanHand humanHand) {
        try {
            if (checkController()) {
                return controller.onInteractBy(humanEntity, humanHand);
            } else {
                return base_onInteractBy(humanEntity, humanHand);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            return InteractionResult.FAIL;
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

    // Minecraft 1.14 and later
    @HookMethod(value="public void move(net.minecraft.world.entity.EnumMoveType enummovetype, Vec3D vec3d)", optional=true)
    public void onMove_v3(Object enumMoveType, Object vec3d) {
        double dx = Vec3DHandle.T.x.getDouble(vec3d);
        double dy = Vec3DHandle.T.y.getDouble(vec3d);
        double dz = Vec3DHandle.T.z.getDouble(vec3d);
        this.onMove_v2(enumMoveType, dx, dy, dz);
    }

    // Minecraft 1.11.2 and later
    @HookMethod(value="public void move(net.minecraft.world.entity.EnumMoveType enummovetype, double d0, double d1, double d2)", optional=true)
    public void onMove_v2(Object enumMoveType, double dx, double dy, double dz) {
        try {
            if (checkController()) {
                if (CommonCapabilities.ENTITY_MOVE_VER2) {
                    controller.onMove(MoveType.getFromHandle(enumMoveType), dx, dy, dz);
                } else {
                    controller.onMove(MoveType.SELF, dx, dy, dz);
                }
            } else {
                if (CommonCapabilities.ENTITY_MOVE_VER3) {
                    base.onMove_v3(enumMoveType, Vec3DHandle.T.constr_x_y_z.raw.newInstance(dx, dy, dz));
                } else if (CommonCapabilities.ENTITY_MOVE_VER2) {
                    base.onMove_v2(enumMoveType, dx, dy, dz);
                } else {
                    base.onMove_v1(dx, dy, dz);
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    // Beginning of time
    @HookMethod(value="public void move(double d0, double d1, double d2)", optional=true)
    public void onMove_v1(double dx, double dy, double dz) {
        this.onMove_v2(MoveType.SELF.getHandle(), dx, dy, dz);
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
                return getName_base(this.instance());
            }
        } catch (Throwable t) {
            t.printStackTrace();
            return "ERROR";
        }
    }

    /**
     * Because the entity type is not registered, there are some issues with the base getName().
     * These issues are fixed here.
     * 
     * @param instance Object instance
     * @return base name
     */
    public String getName_base(Object instance) {
        // >= 1.13 it is a simple lookup of the name of the EntityTypes field value
        // This is safe, so no special logic is warranted here
        if (Common.evaluateMCVersion(">=", "1.13")) {
            return base.getName();
        }

        // <= 1.10.2 we already take care of this issue with the class translating map of entity names
        if (EntityTypesHandle.T.opt_typeNameMap_1_10_2.isAvailable()) {
            return base.getName();
        }

        // Special handling for some entity types and when a custom name is set
        if (EntityHumanHandle.T.isAssignableFrom(instance) || EntityItemHandle.T.isAssignableFrom(instance)) {
            return base.getName();
        }
        if (EntityHandle.T.hasCustomName.invoke(instance)) {
            return EntityHandle.T.getCustomName.invoke(instance).getMessage();
        }

        // Only used MC 1.11 to 1.12.2 inclusively
        // Retrieve MinecraftKey of this entity class, and the String internal name from that
        int typeId = EntityTypesHandle.getEntityTypeId(EntityHook.findInstanceBaseType(instance));
        String name = EntityTypesHandle.T.opt_typeIdToName_1_11.get().get(typeId);
        if (name == null) {
            name = "generic";
        }
        return LocaleLanguageHandle.INSTANCE().get("entity." + name + ".name");
    }

    @HookMethod("public boolean savePassenger:???(NBTTagCompound nbttagcompound)")
    public boolean savePassenger(Object tag) {
        Object handle = this.instance();
        if (EntityHandle.T.dead.getBoolean(handle)) {
            return false;
        }

        CommonTagCompound commonTag = CommonTagCompound.create(tag);
        commonTag.putValue("id", getSavedName(this.instance()));
        EntityHandle.T.saveToNBT.invoke(handle, commonTag);
        return true;
    }

    @HookMethod("public boolean saveEntity:???(NBTTagCompound nbttagcompound)")
    public boolean saveEntity(Object tag) {
        try {
            Object handle = this.instance();
            if (EntityHandle.T.dead.getBoolean(handle)) {
                return false;
            }
            if (EntityHandle.T.vehicle.raw.get(handle) != null) {
                return false;
            }

            CommonTagCompound commonTag = CommonTagCompound.create(tag);
            commonTag.putValue("id", getSavedName(this.instance()));
            EntityHandle.T.saveToNBT.invoke(handle, commonTag);
            return true;
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
    }

    /* This key is used for later de-serializing the entity */
    private static final String getSavedName(Object instance) {
        return EntityTypesHandle.getEntityInternalName(EntityHook.findInstanceBaseType(instance));
    }

    @HookMethod("public void collide(Entity entity)")
    public void collide(Object entity) {
        try {
            if (checkController()) {
                org.bukkit.entity.Entity bukkitEntity = WrapperConversion.toEntity(entity);

                // If entity is a passenger of a vehicle, nobody can collide with it
                if (bukkitEntity.isInsideVehicle()) {
                    return;
                }

                // If entity is a passenger of this entity, ignore the collision
                if (isPassengerOfRecursive(controller.getEntity().getEntity(), bukkitEntity)) {
                    return;
                }

                // If collision is allowed with this Entity, then perform the bump
                if (controller.onEntityCollision(bukkitEntity)) {
                    controller.onEntityBump(bukkitEntity);
                }
            } else {
                base.collide(entity);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private boolean isPassengerOfRecursive(org.bukkit.entity.Entity vehicle, org.bukkit.entity.Entity passenger) {
        List<org.bukkit.entity.Entity> vehiclePassengers;
        vehiclePassengers = com.bergerkiller.generated.org.bukkit.entity.EntityHandle.T.getPassengers.invoker.invoke(vehicle);
        if (!vehiclePassengers.isEmpty()) {
            for (org.bukkit.entity.Entity vehiclePassenger : vehiclePassengers) {
                if (vehiclePassenger == passenger) {
                    return true;
                }
                return isPassengerOfRecursive(vehiclePassenger, passenger);
            }
        }
        return false;
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
