package com.bergerkiller.bukkit.common.internal.hooks;

import java.util.List;
import java.util.logging.Level;
import java.util.stream.Stream;

import org.bukkit.entity.HumanEntity;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.controller.EntityController;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;
import com.bergerkiller.bukkit.common.wrappers.InteractionResult;
import com.bergerkiller.bukkit.common.wrappers.MoveType;
import com.bergerkiller.generated.net.minecraft.locale.LocaleLanguageHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityTypesHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.item.EntityItemHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.player.EntityHumanHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.Vec3DHandle;
import com.bergerkiller.mountiplex.reflection.ClassHook;

@ClassHook.HookPackage("net.minecraft.server")
@ClassHook.HookImport("net.minecraft.world.entity.player.EntityHuman")
@ClassHook.HookImport("net.minecraft.world.EnumHand")
@ClassHook.HookImport("net.minecraft.world.EnumInteractionResult")
@ClassHook.HookImport("net.minecraft.world.item.ItemStack")
@ClassHook.HookImport("net.minecraft.nbt.NBTTagCompound")
@ClassHook.HookLoadVariables("com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER")
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
    @HookMethodCondition("version <= 1.8.8")
    @HookMethod(value="public boolean onInteractBy_1_8_8:???(EntityHuman entityhuman)")
    public boolean onInteractBy_1_8_8(Object entityHuman) {
        return onInteractBy((HumanEntity) WrapperConversion.toEntity(entityHuman), HumanHand.RIGHT).isTruthy();
    }

    @Deprecated
    @HookMethodCondition("version >= 1.9 && version <= 1.11.1")
    @HookMethod(value="public boolean onInteractBy_1_9:???(EntityHuman entityhuman, ItemStack itemstack, EnumHand enumhand)")
    public boolean onInteractBy_1_10_2(Object entityHuman, Object itemstack, Object enumHand) {
        return onInteractBy_1_11_2(entityHuman, enumHand);
    }

    @Deprecated
    @HookMethodCondition("version >= 1.11.2 && version <= 1.15.2")
    @HookMethod(value="public boolean onInteractBy_1_11_2:???(EntityHuman entityhuman, EnumHand enumhand)")
    public boolean onInteractBy_1_11_2(Object entityHuman, Object enumHand) {
        HumanEntity humanEntity = (HumanEntity) WrapperConversion.toEntity(entityHuman);
        return onInteractBy(humanEntity, HumanHand.fromNMSEnumHand(humanEntity, enumHand)).isTruthy();
    }

    @Deprecated
    @HookMethodCondition("version >= 1.16")
    @HookMethod(value="public EnumInteractionResult onInteractBy_1_16:???(EntityHuman entityhuman, EnumHand enumhand)")
    public Object onInteractBy_1_16(Object entityHuman, Object enumHand) {
        HumanEntity humanEntity = (HumanEntity) WrapperConversion.toEntity(entityHuman);
        return onInteractBy(humanEntity, HumanHand.fromNMSEnumHand(humanEntity, enumHand)).getRawHandle();
    }

    @HookMethodCondition("version >= 1.17")
    @HookMethod("public boolean isAlwaysTicked:???();")
    public boolean isAlwaysTicked() {
        // Must always return true, otherwise problems happen trying to register a custom network
        // controller. This way, no such problems will occur. This can be removed once we figure
        // out a better way to do network synch stuff.
        return true;
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

    @HookMethod("public boolean damageEntity:???(net.minecraft.world.damagesource.DamageSource damagesource, float f)")
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

    @HookMethodCondition("version >= 1.14")
    @HookMethod(value="public void move(net.minecraft.world.entity.EnumMoveType enummovetype, net.minecraft.world.phys.Vec3D vec3d)")
    public void onMove_v3(Object enumMoveType, Object vec3d) {
        double dx = Vec3DHandle.T.x.getDouble(vec3d);
        double dy = Vec3DHandle.T.y.getDouble(vec3d);
        double dz = Vec3DHandle.T.z.getDouble(vec3d);
        this.onMove_v2(enumMoveType, dx, dy, dz);
    }

    @HookMethodCondition("version >= 1.11 && version <= 1.13.2")
    @HookMethod(value="public void move(net.minecraft.world.entity.EnumMoveType enummovetype, double d0, double d1, double d2)")
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

    @HookMethodCondition("version <= 1.10.2")
    @HookMethod(value="public void move(double d0, double d1, double d2)")
    public void onMove_v1(double dx, double dy, double dz) {
        this.onMove_v2(MoveType.SELF.getHandle(), dx, dy, dz);
    }

    private static final Object ENTTIY_REMOVE_REASON_KILLED;
    private static final Object ENTTIY_REMOVE_REASON_DISCARDED;
    static {
        Object killed = null, discarded = null;
        if (CommonCapabilities.ENTITY_REMOVE_WITH_REASON) {
            Class<?> reasonClass = CommonUtil.getClass("net.minecraft.world.entity.Entity.RemovalReason");
            if (reasonClass == null) {
                Logging.LOGGER_REFLECTION.severe("Failed to find Entity.RemovalReason class");
            } else {
                Enum<?>[] values = (Enum<?>[]) reasonClass.getEnumConstants();
                killed = Stream.of(values)
                        .filter(n -> n.name().equals("KILLED"))
                        .findFirst().orElse(null);
                discarded = Stream.of(values)
                        .filter(n -> n.name().equals("DISCARDED"))
                        .findFirst().orElse(null);
            }
        }
        ENTTIY_REMOVE_REASON_KILLED = killed;
        ENTTIY_REMOVE_REASON_DISCARDED = discarded;
    }

    public void onBaseDeath(boolean killed) {
        if (CommonCapabilities.ENTITY_REMOVE_WITH_REASON) {
            if (killed) {
                base.onEntityRemoved(ENTTIY_REMOVE_REASON_KILLED);
            } else {
                base.onEntityRemoved(ENTTIY_REMOVE_REASON_DISCARDED);
            }
        } else {
            base.die();
        }
    }

    @HookMethodCondition("version <= 1.16.5")
    @HookMethod("public void die()")
    public void die() {
        try {
            if (checkController()) {
                controller.onDie(true);
            } else {
                base.die();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @HookMethodCondition("version >= 1.17 && version <= 1.17.1")
    @HookMethod("public void remove:a(net.minecraft.world.entity.Entity.RemovalReason removalReason)")
    public void onEntityRemoved_1_17(Object removalReason) {
        try {
            if (checkController()) {
                if (removalReason == ENTTIY_REMOVE_REASON_KILLED) {
                    controller.onDie(true);
                } else if (removalReason == ENTTIY_REMOVE_REASON_DISCARDED) {
                    controller.onDie(false);
                } else {
                    base.onEntityRemoved_1_17(removalReason);
                }
            } else {
                base.onEntityRemoved_1_17(removalReason);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @HookMethodCondition("version >= 1.18")
    @HookMethod("public void remove(net.minecraft.world.entity.Entity.RemovalReason removalReason)")
    public void onEntityRemoved(Object removalReason) {
        try {
            if (checkController()) {
                if (removalReason == ENTTIY_REMOVE_REASON_KILLED) {
                    controller.onDie(true);
                } else if (removalReason == ENTTIY_REMOVE_REASON_DISCARDED) {
                    controller.onDie(false);
                } else {
                    base.onEntityRemoved(removalReason);
                }
            } else {
                base.onEntityRemoved(removalReason);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @HookMethod("public String getStringUUID:???()")
    public String getStringUUID() {
        try {
            if (checkController()) {
                return controller.getLocalizedName();
            } else {
                return getStringUUID_base(this.instance());
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
    public String getStringUUID_base(Object instance) {
        // >= 1.13 it is a simple lookup of the name of the EntityTypes field value
        // This is safe, so no special logic is warranted here
        if (Common.evaluateMCVersion(">=", "1.13")) {
            return base.getStringUUID();
        }

        // <= 1.10.2 we already take care of this issue with the class translating map of entity names
        if (EntityTypesHandle.T.opt_typeNameMap_1_10_2.isAvailable()) {
            return base.getStringUUID();
        }

        // Special handling for some entity types and when a custom name is set
        if (EntityHumanHandle.T.isAssignableFrom(instance) || EntityItemHandle.T.isAssignableFrom(instance)) {
            return base.getStringUUID();
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

    @HookMethod("public boolean onEntitySave:???(NBTTagCompound nbttagcompound)")
    public boolean onEntitySave(Object tag) {
        Object handle = this.instance();
        if (!EntityHandle.T.isSavingAllowed.invoke(handle)) {
            return false;
        }

        CommonTagCompound commonTag = CommonTagCompound.create(tag);
        commonTag.putValue("id", getSavedName(this.instance()));
        EntityHandle.T.saveToNBT.invoke(handle, commonTag);
        return true;
    }

    // This only has to be hooked on Minecraft 1.12.2 and before, where the implementation
    // calls getSaveID() - which will cause incorrect data to be saved. On Minecraft 1.13
    // and later, this is no longer needed, because it defers to onEntitySave() instead.
    @HookMethodCondition("version <= 1.12.2")
    @HookMethod("public boolean saveEntityIfNotPassenger:d(NBTTagCompound nbttagcompound)")
    public boolean saveEntity(Object tag) {
        try {
            Object handle = this.instance();
            if (EntityHandle.T.isPassenger.invoke(handle)) {
                return false;
            } else {
                return this.onEntitySave(tag);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
    }

    /* This key is used for later de-serializing the entity */
    private static final String getSavedName(Object instance) {
        return EntityTypesHandle.getEntityInternalName(EntityHook.findInstanceBaseType(instance));
    }

    @HookMethod("public void collide:???(net.minecraft.world.entity.Entity entity)")
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
