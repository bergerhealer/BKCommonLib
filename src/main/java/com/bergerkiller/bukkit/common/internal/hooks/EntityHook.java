package com.bergerkiller.bukkit.common.internal.hooks;

import java.util.List;
import java.util.logging.Level;
import java.util.stream.Stream;

import com.bergerkiller.bukkit.common.controller.EntityPositionApplier;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.generated.net.minecraft.world.level.storage.ValueOutputHandle;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.controller.EntityController;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
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
import org.bukkit.util.Vector;

@ClassHook.HookPackage("net.minecraft.server")
@ClassHook.HookImport("net.minecraft.world.entity.player.EntityHuman")
@ClassHook.HookImport("net.minecraft.world.entity.Entity")
@ClassHook.HookImport("net.minecraft.world.entity.Entity.MoveFunction")
@ClassHook.HookImport("net.minecraft.world.EnumHand")
@ClassHook.HookImport("net.minecraft.world.EnumInteractionResult")
@ClassHook.HookImport("net.minecraft.world.item.ItemStack")
@ClassHook.HookImport("net.minecraft.nbt.NBTTagCompound")
@ClassHook.HookLoadVariables("com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER")
public class EntityHook extends ClassHook<EntityHook> {
    private EntityController<?> controller = null;
    private boolean controllerPositionsPassengers = false;
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

        // Optimization: don't do complex hook stuff if not implemented in controller
        this.controllerPositionsPassengers = (controller != null) && CommonUtil.isMethodOverrided(EntityController.class, controller,
                "onPositionPassenger", Entity.class, EntityPositionApplier.class);
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

    // Used for handling passenger position updates on 1.20+
    private static final FastMethod<Void> moveFunctionMethod = new FastMethod<>();
    static {
        Class<?> moveFunctionType = CommonUtil.getClass("net.minecraft.world.entity.Entity$MoveFunction");
        if (moveFunctionType != null) {
            try {
                moveFunctionMethod.init(Resolver.resolveAndGetDeclaredMethod(moveFunctionType,
                        "accept", EntityHandle.T.getType(), double.class, double.class, double.class));
                moveFunctionMethod.forceInitialization();
            } catch (Throwable t) {
                Logging.LOGGER.log(Level.WARNING, "Failed to find the Entity MoveFunction accept method", t);
            }
        } else if (CommonBootstrap.evaluateMCVersion(">=", "1.20")) {
            Logging.LOGGER.warning("Failed to find the Entity MoveFunction class");
        }
    }

    @Deprecated
    @HookMethodCondition("version < 1.9")
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
            Logging.LOGGER.log(Level.SEVERE, "An unhandled exception occurred during the entity onInteractBy callback", t);
            return InteractionResult.FAIL;
        }
    }

    @HookMethodCondition("version < 1.21.2")
    @HookMethod("public boolean damageEntity:???(net.minecraft.world.damagesource.DamageSource damagesource, float f)")
    public boolean onDamageEntityLegacy(Object damageSource, float damage) {
        try {
            if (checkController()) {
                return controller.onDamage(com.bergerkiller.bukkit.common.wrappers.DamageSource.getForHandle(damageSource), damage);
            } else {
                return base.onDamageEntityLegacy(damageSource, damage);
            }
        } catch (Throwable t) {
            Logging.LOGGER.log(Level.SEVERE, "An unhandled exception occurred during the entity damageEntity callback", t);
            return false;
        }
    }

    @HookMethodCondition("version >= 1.21.2")
    @HookMethod("public boolean damageEntityWithWorld:hurtServer(net.minecraft.server.level.WorldServer world, net.minecraft.world.damagesource.DamageSource damagesource, float f)")
    public boolean onDamageEntity(Object world, Object damageSource, float damage) {
        try {
            if (checkController()) {
                return controller.onDamage(com.bergerkiller.bukkit.common.wrappers.DamageSource.getForHandle(damageSource), damage);
            } else {
                return base.onDamageEntity(world, damageSource, damage);
            }
        } catch (Throwable t) {
            Logging.LOGGER.log(Level.SEVERE, "An unhandled exception occurred during the entity damageEntity callback", t);
            return false;
        }
    }

    private static final boolean DAMAGE_ENTITY_HAS_WORLD_ARG = CommonBootstrap.evaluateMCVersion(">=", "1.21.2");

    public boolean baseDamageEntity(Object damageSource, float damage) {
        if (DAMAGE_ENTITY_HAS_WORLD_ARG) {
            Object worldServer = EntityHandle.T.getWorld.raw.invoker.invoke(instance());
            return base.onDamageEntity(worldServer, damage, damage);
        } else {
            return base.onDamageEntityLegacy(damageSource, damage);
        }
    }

    @HookMethod("public void onTick:???()")
    public void onTick() {
        try {
            if (checkController()) {
                controller.onTick();
                if (EntityHandle.T.opt_tick_pushToHopper.isAvailable()) {
                    EntityHandle.T.opt_tick_pushToHopper.invoke(instance());
                }
            } else {
                base.onTick();
            }
        } catch (Throwable t) {
            Logging.LOGGER.log(Level.SEVERE, "An unhandled exception occurred during the entity tick callback", t);
        }
    }

    private static boolean HAS_ONPUSH_WITH_ENTITY = LogicUtil.tryCreate(() -> {
        Resolver.resolveAndGetDeclaredMethod(EntityHandle.T.getType(),
                "push", double.class, double.class, double.class, EntityHandle.T.getType());
        return true;
    }, err -> false);

    private Object lastPushedEntity = null;

    public void baseOnPush(double dx, double dy, double dz) {
        if (HAS_ONPUSH_WITH_ENTITY) {
            base.onPushWithEntity(dx, dy, dz, lastPushedEntity);
        } else {
            base.onPush(dx, dy, dz);
        }
    }

    @HookMethodCondition("!exists net.minecraft.world.entity.Entity public void push(double x, double y, double z, net.minecraft.world.entity.Entity pushingEntity)")
    @HookMethod("public void onPush:???(double d0, double d1, double d2)")
    public void onPush(double dx, double dy, double dz) {
        try {
            if (checkController()) {
                controller.onPush(dx, dy, dz);
            } else {
                base.onPush(dx, dy, dz);
            }
        } catch (Throwable t) {
            Logging.LOGGER.log(Level.SEVERE, "An unhandled exception occurred during the entity push callback", t);
        }
    }

    @HookMethodCondition("exists net.minecraft.world.entity.Entity public void push(double x, double y, double z, net.minecraft.world.entity.Entity pushingEntity)")
    @HookMethod("public void push(double d0, double d1, double d2, net.minecraft.world.entity.Entity pushingEntity)")
    public void onPushWithEntity(double dx, double dy, double dz, Object pushingEntity) {
        try {
            if (checkController()) {
                Object before = lastPushedEntity;
                try {
                    lastPushedEntity = pushingEntity;
                    controller.onPush(dx, dy, dz);
                } finally {
                    lastPushedEntity = before;
                }
            } else {
                base.onPushWithEntity(dx, dy, dz, pushingEntity);
            }
        } catch (Throwable t) {
            Logging.LOGGER.log(Level.SEVERE, "An unhandled exception occurred during the entity push callback", t);
        }
    }

    private static final boolean POSITIONRIDER_1_20 = CommonBootstrap.evaluateMCVersion(">=", "1.20");
    private static final boolean POSITIONRIDER_1_9_TO_1_20 = CommonBootstrap.evaluateMCVersion(">=", "1.9") &&
                                                             CommonBootstrap.evaluateMCVersion("<", "1.20");
    private static final boolean POSITIONRIDER_1_8 = CommonBootstrap.evaluateMCVersion("<", "1.9");

    public void basePositionRider(Entity passenger, EntityPositionApplier applier) {
        if (POSITIONRIDER_1_20) {
            if (!(applier instanceof EntityPositionApplierWithNMSMoveFunction)) {
                throw new IllegalArgumentException("Position applier is of an invalid type");
            }
            Object nmsMoveFunction = ((EntityPositionApplierWithNMSMoveFunction) applier).nmsMoveFunction;
            base.onPositionRider_1_20(HandleConversion.toEntityHandle(passenger), nmsMoveFunction);
        } else if (POSITIONRIDER_1_9_TO_1_20) {
            base.onPositionRider_1_9_to_1_20(HandleConversion.toEntityHandle(passenger));
        } else if (POSITIONRIDER_1_8) {
            base.onPositionRider_1_8();
        }
    }

    @HookMethodCondition("version >= 1.20")
    @HookMethod("protected void positionRider(Entity passenger, Entity.MoveFunction moveFunction)")
    public void onPositionRider_1_20(Object nmsPassenger, Object nmsMoveFunction) {
        if (!controllerPositionsPassengers) {
            base.onPositionRider_1_20(nmsPassenger, nmsMoveFunction);
            return;
        }

        Entity bPassenger = WrapperConversion.toEntity(nmsPassenger);
        if (bPassenger == null || !controller.getEntity().isPassenger(bPassenger)) {
            base.onPositionRider_1_20(nmsPassenger, nmsMoveFunction);
            return;
        }

        try {
            EntityPositionApplierWithNMSMoveFunction applier = new EntityPositionApplierWithNMSMoveFunction(nmsPassenger, nmsMoveFunction);
            controller.onPositionPassenger(bPassenger, applier);
        } catch (Throwable t) {
            Logging.LOGGER.log(Level.SEVERE, "An unhandled exception occurred during the entity position rider callback", t);
        }
    }

    @HookMethodCondition("version >= 1.9 && version < 1.20")
    @HookMethod("public void positionRider:???(Entity passenger)")
    public void onPositionRider_1_9_to_1_20(Object nmsPassenger) {
        if (!controllerPositionsPassengers) {
            base.onPositionRider_1_9_to_1_20(nmsPassenger);
            return;
        }

        Entity bPassenger = WrapperConversion.toEntity(nmsPassenger);
        if (bPassenger == null || !controller.getEntity().isPassenger(bPassenger)) {
            base.onPositionRider_1_9_to_1_20(nmsPassenger);
            return;
        }

        try {
            controller.onPositionPassenger(bPassenger, new EntityPositionApplierBaseImpl(nmsPassenger));
        } catch (Throwable t) {
            Logging.LOGGER.log(Level.SEVERE, "An unhandled exception occurred during the entity position rider callback", t);
        }
    }

    @HookMethodCondition("version < 1.9")
    @HookMethod("public void positionRider:al()")
    public void onPositionRider_1_8() {
        if (!controllerPositionsPassengers) {
            base.onPositionRider_1_8();
            return;
        }

        try {
            List<Entity> passengers = controller.getEntity().getPassengers();
            for (Entity passenger : passengers) {
                controller.onPositionPassenger(passenger, new EntityPositionApplierBaseImpl(
                        HandleConversion.toEntityHandle(passenger)));
            }
        } catch (Throwable t) {
            Logging.LOGGER.log(Level.SEVERE, "An unhandled exception occurred during the entity position rider callback", t);
        }
    }

    @HookMethodCondition("version >= 1.14")
    @HookMethod("public void move(net.minecraft.world.entity.EnumMoveType enummovetype, net.minecraft.world.phys.Vec3D vec3d)")
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
            Logging.LOGGER.log(Level.SEVERE, "An unhandled exception occurred during the entity move callback", t);
        }
    }

    @HookMethodCondition("version <= 1.10.2")
    @HookMethod(value="public void move(double d0, double d1, double d2)")
    public void onMove_v1(double dx, double dy, double dz) {
        this.onMove_v2(MoveType.SELF.getHandle(), dx, dy, dz);
    }

    private static final Object ENTITY_REMOVE_REASON_KILLED;
    private static final Object ENTITY_REMOVE_REASON_DISCARDED;
    private static BaseEntityRemoveHandler BASE_ENTITY_REMOVED;
    static {
        Object killed = null, discarded = null;
        BaseEntityRemoveHandler baseEntityRemoved = (hook, removalReason, cause) -> {};
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
            baseEntityRemoved = (base, removalReason, cause) -> base.onEntityRemoved(removalReason);

            // Is there an alternative method that accepts a Bukkit cause? If so, call that one instead
            // This avoids a circular loop when the non-cause method calls into the one with cause
            Class<?> bukkitCauseClass = CommonUtil.getClass("org.bukkit.event.entity.EntityRemoveEvent.Cause");
            if (bukkitCauseClass != null) {
                try {
                    Resolver.resolveAndGetDeclaredMethod(EntityHandle.T.getType(), "remove", reasonClass, bukkitCauseClass);
                    baseEntityRemoved = EntityHook::onEntityRemovedWithCause;
                } catch (Throwable t) { /* ignore */ }
            }
        }
        ENTITY_REMOVE_REASON_KILLED = killed;
        ENTITY_REMOVE_REASON_DISCARDED = discarded;
        BASE_ENTITY_REMOVED = baseEntityRemoved;
    }

    @FunctionalInterface
    private interface BaseEntityRemoveHandler {
        void remove(EntityHook hook, Object removalReason, Object cause);
    }

    public void onBaseDeath(boolean killed) {
        if (CommonCapabilities.ENTITY_REMOVE_WITH_REASON) {
            BASE_ENTITY_REMOVED.remove(base, killed ? ENTITY_REMOVE_REASON_KILLED
                                                    : ENTITY_REMOVE_REASON_DISCARDED, null);
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
            Logging.LOGGER.log(Level.SEVERE, "An unhandled exception occurred during the entity remove callback", t);
        }
    }

    @HookMethodCondition("version >= 1.17")
    @HookMethod("public void remove:???(net.minecraft.world.entity.Entity.RemovalReason removalReason)")
    public void onEntityRemoved(Object removalReason) {
        try {
            if (checkController()) {
                if (removalReason == ENTITY_REMOVE_REASON_KILLED) {
                    controller.onDie(true);
                } else if (removalReason == ENTITY_REMOVE_REASON_DISCARDED) {
                    controller.onDie(false);
                } else {
                    BASE_ENTITY_REMOVED.remove(base, removalReason, null);
                }
            } else {
                BASE_ENTITY_REMOVED.remove(base, removalReason, null);
            }
        } catch (Throwable t) {
            Logging.LOGGER.log(Level.SEVERE, "An unhandled exception occurred during the entity remove callback", t);
        }
    }

    @HookMethodCondition("exists net.minecraft.world.entity.Entity public void remove(" +
                         "    net.minecraft.world.entity.Entity.RemovalReason removalReason," +
                         "    org.bukkit.event.entity.EntityRemoveEvent.Cause cause)")
    @HookMethod("public void remove(net.minecraft.world.entity.Entity.RemovalReason removalReason, org.bukkit.event.entity.EntityRemoveEvent.Cause cause);")
    public void onEntityRemovedWithCause(Object removalReason, Object removeEventCause) {
        try {
            if (checkController()) {
                if (removalReason == ENTITY_REMOVE_REASON_KILLED) {
                    controller.onDie(true);
                } else if (removalReason == ENTITY_REMOVE_REASON_DISCARDED) {
                    controller.onDie(false);
                } else {
                    BASE_ENTITY_REMOVED.remove(base, removalReason, removeEventCause);
                }
            } else {
                BASE_ENTITY_REMOVED.remove(base, removalReason, removeEventCause);
            }
        } catch (Throwable t) {
            Logging.LOGGER.log(Level.SEVERE, "An unhandled exception occurred during the entity remove callback", t);
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
            Logging.LOGGER.log(Level.SEVERE, "An unhandled exception occurred serializing saved entity identifier", t);
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

    private boolean handleSaveAsPassenger(Object valueoutput, boolean includeAll, boolean includeNonSaveable, boolean forceSerialization) {
        Object handle = this.instance();
        if (!EntityHandle.T.isSavingAllowed.invoke(handle)) {
            return false;
        }

        ValueOutputHandle.T.putString.invoke(valueoutput, "id", getSavedName(this.instance()));
        EntityHandle.T.saveWithoutId.raw.invoke(handle, valueoutput, includeAll, includeNonSaveable, forceSerialization);
        return true;
    }

    // Base NMS method, with all default options set. Always exists.
    @HookMethod("public boolean saveAsPassenger(net.minecraft.world.level.storage.ValueOutput valueOutput)")
    public boolean onSaveAsPassenger(Object valueoutput) {
        return handleSaveAsPassenger(valueoutput, true, false, false);
    }

    // Paper added a bunch of flags here
    @HookMethodCondition("paper && version >= 1.21.4")
    @HookMethod("public boolean saveAsPassenger(net.minecraft.world.level.storage.ValueOutput valueOutput, boolean includeAll, boolean includeNonSaveable, boolean forceSerialization)")
    public boolean onSaveAsPassengerPaperMultiFlags(Object valueoutput, boolean includeAll, boolean includeNonSaveable, boolean forceSerialization) {
        return handleSaveAsPassenger(valueoutput, includeAll, includeNonSaveable, forceSerialization);
    }

    // includeAll option flag by CraftBukkit since 1.20.3, for Paper specifically
    // Here because we don't have good () logic for #if
    @HookMethodCondition("paper && version >= 1.20.3 && version < 1.21.4")
    @HookMethod("public boolean saveAsPassenger(net.minecraft.world.level.storage.ValueOutput valueOutput, boolean includeAll)")
    public boolean onSaveAsPassengerPaperIncludeAll(Object valueoutput, boolean includeAll) {
        return handleSaveAsPassenger(valueoutput, includeAll, false, false);
    }

    // includeAll option flag by CraftBukkit since 1.20.3
    // Alternative methods are used on Paper, which can also add extra bool args we need to handle
    @HookMethodCondition("!paper && version >= 1.20.3")
    @HookMethod("public boolean saveAsPassenger(net.minecraft.world.level.storage.ValueOutput valueOutput, boolean includeAll)")
    public boolean onSaveAsPassengerSpigotIncludeAll(Object valueoutput, boolean includeAll) {
        return handleSaveAsPassenger(valueoutput, includeAll, false, false);
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
                return this.onSaveAsPassenger(tag);
            }
        } catch (Throwable t) {
            Logging.LOGGER.log(Level.SEVERE, "An unhandled exception occurred during the save callback", t);
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
            Logging.LOGGER.log(Level.SEVERE, "An unhandled exception occurred during the collide callback", t);
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
            Logging.LOGGER.log(Level.SEVERE, "An unhandled exception occurred during the setItem callback", t);
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

    private static class EntityPositionApplierWithNMSMoveFunction extends EntityPositionApplierBaseImpl {
        protected final Object nmsMoveFunction;

        public EntityPositionApplierWithNMSMoveFunction(Object nmsEntity, Object nmsMoveFunction) {
            super(nmsEntity);
            this.nmsMoveFunction = nmsMoveFunction;
        }

        @Override
        public void setPosition(double x, double y, double z) {
            moveFunctionMethod.invoke(nmsMoveFunction, entityHandle.getRaw(), x, y, z);
        }
    }

    /**
     * Used for passenger movement handling
     */
    private static class EntityPositionApplierBaseImpl implements EntityPositionApplier {
        protected final EntityHandle entityHandle;

        public EntityPositionApplierBaseImpl(Object nmsEntity) {
            this.entityHandle = EntityHandle.createHandle(nmsEntity);
        }

        @Override
        public void setPosition(double x, double y, double z) {
            entityHandle.setPosition(x, y, z);
        }

        @Override
        public Vector getPosition() {
            return entityHandle.getLoc();
        }

        @Override
        public void setBodyYaw(float yaw) {
            entityHandle.setYaw(yaw);
        }

        @Override
        public void setHeadYaw(float yaw) {
            entityHandle.setHeadRotation(yaw);
        }

        @Override
        public void setHeadPitch(float pitch) {
            entityHandle.setPitch(pitch);
        }

        @Override
        public float getBodyYaw() {
            return entityHandle.getYaw();
        }

        @Override
        public float getHeadYaw() {
            return entityHandle.getHeadRotation();
        }

        @Override
        public float getHeadPitch() {
            return entityHandle.getPitch();
        }
    }
}
