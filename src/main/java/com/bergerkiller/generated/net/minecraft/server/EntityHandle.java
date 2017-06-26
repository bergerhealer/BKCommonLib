package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher.Key;
import com.bergerkiller.bukkit.common.wrappers.ResourceKey;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.Entity</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class EntityHandle extends Template.Handle {
    /** @See {@link EntityClass} */
    public static final EntityClass T = new EntityClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EntityHandle.class, "net.minecraft.server.Entity");

    /* ============================================================================== */

    public static EntityHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        EntityHandle handle = new EntityHandle();
        handle.instance = handleInstance;
        return handle;
    }

    public static final EntityHandle createNew(WorldHandle world) {
        return T.constr_world.newInstance(world);
    }

    /* ============================================================================== */

    public static int entityCount() {
        return T.entityCount.getInteger();
    }

    public static void entityCount_set(int value) {
        T.entityCount.setInteger(value);
    }

    public void updateBlockCollision() {
        T.updateBlockCollision.invoke(instance);
    }

    public void playStepSound(IntVector3 position, BlockData blockData) {
        T.playStepSound.invoke(instance, position, blockData);
    }

    public void setRotation(float yaw, float pitch) {
        T.setRotation.invoke(instance, yaw, pitch);
    }

    public void burn(float dmg) {
        T.burn.invoke(instance, dmg);
    }

    public Item dropItem(Material material, int amount, float force) {
        return T.dropItem.invoke(instance, material, amount, force);
    }

    public Item dropItemStack(ItemStack itemstack, float force) {
        return T.dropItemStack.invoke(instance, itemstack, force);
    }

    public ResourceKey getSwimSound() {
        return T.getSwimSound.invoke(instance);
    }

    public void makeSound(ResourceKey soundeffect, float volume, float pitch) {
        T.makeSound.invoke(instance, soundeffect, volume, pitch);
    }

    public boolean isInWaterUpdate() {
        return T.isInWaterUpdate.invoke(instance);
    }

    public boolean isInWater() {
        return T.isInWater.invoke(instance);
    }

    public boolean hasMovementSound() {
        return T.hasMovementSound.invoke(instance);
    }

    public void updateFalling(double d0, boolean flag, BlockData blockData, IntVector3 position) {
        T.updateFalling.invoke(instance, d0, flag, blockData, position);
    }

    public void doStepSoundUpdate(IntVector3 blockposition, BlockData blockData) {
        T.doStepSoundUpdate.invoke(instance, blockposition, blockData);
    }

    public void checkBlockCollisions() {
        T.checkBlockCollisions.invoke(instance);
    }

    public double calculateDistance(double x, double y, double z) {
        return T.calculateDistance.invoke(instance, x, y, z);
    }

    public boolean damageEntity(DamageSourceHandle damagesource, float damage) {
        return T.damageEntity.invoke(instance, damagesource, damage);
    }

    public void setPosition(double x, double y, double z) {
        T.setPosition.invoke(instance, x, y, z);
    }

    public void setPositionRotation(double x, double y, double z, float yaw, float pitch) {
        T.setPositionRotation.invoke(instance, x, y, z, yaw, pitch);
    }

    public void setLocation(double x, double y, double z, float yaw, float pitch) {
        T.setLocation.invoke(instance, x, y, z, yaw, pitch);
    }

    public float getHeadRotation() {
        return T.getHeadRotation.invoke(instance);
    }

    public AxisAlignedBBHandle getBoundingBox() {
        return T.getBoundingBox.invoke(instance);
    }

    public void setBoundingBox(AxisAlignedBBHandle axisalignedbb) {
        T.setBoundingBox.invoke(instance, axisalignedbb);
    }

    public AxisAlignedBBHandle getOtherBoundingBox() {
        return T.getOtherBoundingBox.invoke(instance);
    }

    public AxisAlignedBBHandle getEntityBoundingBox(EntityHandle entity) {
        return T.getEntityBoundingBox.invoke(instance, entity);
    }

    public void recalcPosition() {
        T.recalcPosition.invoke(instance);
    }

    public boolean isBurning() {
        return T.isBurning.invoke(instance);
    }

    public void setOnFire(int numSeconds) {
        T.setOnFire.invoke(instance, numSeconds);
    }

    public boolean isWet() {
        return T.isWet.invoke(instance);
    }

    public void saveToNBT(CommonTagCompound compound) {
        T.saveToNBT.invoke(instance, compound);
    }

    public void onTick() {
        T.onTick.invoke(instance);
    }

    public void loadFromNBT(CommonTagCompound compound) {
        T.loadFromNBT.invoke(instance, compound);
    }

    public boolean savePassenger(CommonTagCompound compound) {
        return T.savePassenger.invoke(instance, compound);
    }

    public boolean saveEntity(CommonTagCompound compound) {
        return T.saveEntity.invoke(instance, compound);
    }

    public boolean isSneaking() {
        return T.isSneaking.invoke(instance);
    }

    public void appendEntityCrashDetails(CrashReportSystemDetailsHandle crashreportsystemdetails) {
        T.appendEntityCrashDetails.invoke(instance, crashreportsystemdetails);
    }

    public int getId() {
        return T.getId.invoke(instance);
    }

    public UUID getUniqueID() {
        return T.getUniqueID.invoke(instance);
    }

    public DataWatcher getDataWatcher() {
        return T.getDataWatcher.invoke(instance);
    }

    public void onPush(double d0, double d1, double d2) {
        T.onPush.invoke(instance, d0, d1, d2);
    }

    public void collide(EntityHandle entity) {
        T.collide.invoke(instance, entity);
    }

    public Entity getBukkitEntity() {
        return T.getBukkitEntity.invoke(instance);
    }


    public List<EntityHandle> getPassengers() {
        if (T.opt_passengers.isAvailable()) {
            List<EntityHandle> passengers = T.opt_passengers.get(instance);
            if (passengers == null) {
                return java.util.Collections.emptyList();
            } else {
                return passengers;
            }
        } else {
            EntityHandle passenger = T.opt_passenger.get(instance);
            if (passenger == null) {
                return java.util.Collections.emptyList();
            } else {
                return java.util.Arrays.asList(passenger);
            }
        }
    }

    public boolean hasPassengers() {
        if (T.opt_passengers.isAvailable()) {
            List<EntityHandle> passengers = T.opt_passengers.get(instance);
            return passengers != null && passengers.size() > 0;
        } else {
            return T.opt_passenger.get(instance) != null;
        }
    }

    public void setPassengers(List<EntityHandle> passengers) {
        if (T.opt_passengers.isAvailable()) {
            List<EntityHandle> entity_passengers = T.opt_passengers.get(instance);
            if (entity_passengers == null) {
                T.opt_passengers.set(instance, passengers);
            } else {
                entity_passengers.clear();
                entity_passengers.addAll(passengers);
            }
        } else if (passengers.size() == 0) {
            T.opt_passenger.set(instance, null);
        } else {
            T.opt_passenger.set(instance, passengers.get(0));
        }
    }


    public static DataWatcher.Key<Byte> DATA_FLAGS = DataWatcher.Key.fromTemplate(T.DATA_FLAGS, 0);
    public static DataWatcher.Key<Integer> DATA_AIR_TICKS = DataWatcher.Key.fromTemplate(T.DATA_AIR_TICKS, 1);
    public static DataWatcher.Key<String> DATA_CUSTOM_NAME = DataWatcher.Key.fromTemplate(T.DATA_CUSTOM_NAME, 2);
    public static DataWatcher.Key<Boolean> DATA_CUSTOM_NAME_VISIBLE = DataWatcher.Key.fromTemplate(T.DATA_CUSTOM_NAME_VISIBLE, 3);
    public static DataWatcher.Key<Boolean> DATA_SILENT = DataWatcher.Key.fromTemplate(T.DATA_SILENT, 4);
    public static DataWatcher.Key<Boolean> DATA_NO_GRAVITY = DataWatcher.Key.fromTemplate(T.DATA_NO_GRAVITY, -1);

    public static final int DATA_FLAG_ON_FIRE = (1 << 0);
    public static final int DATA_FLAG_SNEAKING = (1 << 1);
    public static final int DATA_FLAG_UNKNOWN1 = (1 << 2);
    public static final int DATA_FLAG_SPRINTING = (1 << 3);
    public static final int DATA_FLAG_UNKNOWN2 = (1 << 4);
    public static final int DATA_FLAG_INVISIBLE = (1 << 5);
    public static final int DATA_FLAG_GLOWING = (1 << 6);
    public static final int DATA_FLAG_FLYING = (1 << 7);


    public boolean isPassenger() {
        if (T.isPassenger.isAvailable()) {
            return T.isPassenger.invoke(instance);
        } else {
            return T.vehicle.raw.get(instance) != null;
        }
    }

    public boolean isVehicle() {
        if (T.isVehicle.isAvailable()) {
            return T.isVehicle.invoke(instance);
        } else {
            return T.opt_passenger.get(instance) != null;
        }
    }


    public int getMaxFireTicks() {
        if (T.prop_getMaxFireTicks.isAvailable()) {
            return T.prop_getMaxFireTicks.invoke(instance);
        } else if (T.field_maxFireTicks.isAvailable()) {
            return T.field_maxFireTicks.getInteger(instance);
        } else {
            throw new UnsupportedOperationException("Max Fire Ticks can not be read");
        }
    }


    public EntityHandle getDriverEntity() {
        if (T.getDriverEntity.isAvailable()) {
            return T.getDriverEntity.invoke(instance);
        } else {
            return null; // driver feature not a thing on this server
        }
    }


    public boolean isInSameVehicle(EntityHandle entity) {
        if (T.isInSameVehicle.isAvailable()) {
            return T.isInSameVehicle.invoke(instance, entity);
        } else {
            Object rawPassenger = T.opt_passenger.raw.get(this.instance);
            Object rawVehicle = T.vehicle.raw.get(this.instance);
            Object rawEntity = entity.getRaw();
            return rawEntity == rawPassenger || rawEntity == rawVehicle;
        }
    }


    public static final boolean IS_NEW_MOVE_FUNCTION = com.bergerkiller.bukkit.common.Common.evaluateMCVersion(">=", "1.11.2");


    public WorldServerHandle getWorldServer() {
        return WorldServerHandle.createHandle(T.world.raw.get(instance));
    }

    public org.bukkit.entity.Entity toBukkit() {
        return com.bergerkiller.bukkit.common.conversion.Conversion.toEntity.convert(instance);
    }

    public static EntityHandle fromBukkit(org.bukkit.entity.Entity entity) {
        return createHandle(com.bergerkiller.bukkit.common.conversion.Conversion.toEntityHandle.convert(entity));
    }
    public Entity getBukkitEntityField() {
        return T.bukkitEntityField.get(instance);
    }

    public void setBukkitEntityField(Entity value) {
        T.bukkitEntityField.set(instance, value);
    }

    public int getIdField() {
        return T.idField.getInteger(instance);
    }

    public void setIdField(int value) {
        T.idField.setInteger(instance, value);
    }

    public EntityHandle getVehicle() {
        return T.vehicle.get(instance);
    }

    public void setVehicle(EntityHandle value) {
        T.vehicle.set(instance, value);
    }

    public boolean isIgnoreChunkCheck() {
        return T.ignoreChunkCheck.getBoolean(instance);
    }

    public void setIgnoreChunkCheck(boolean value) {
        T.ignoreChunkCheck.setBoolean(instance, value);
    }

    public WorldHandle getWorld() {
        return T.world.get(instance);
    }

    public void setWorld(WorldHandle value) {
        T.world.set(instance, value);
    }

    public double getLastX() {
        return T.lastX.getDouble(instance);
    }

    public void setLastX(double value) {
        T.lastX.setDouble(instance, value);
    }

    public double getLastY() {
        return T.lastY.getDouble(instance);
    }

    public void setLastY(double value) {
        T.lastY.setDouble(instance, value);
    }

    public double getLastZ() {
        return T.lastZ.getDouble(instance);
    }

    public void setLastZ(double value) {
        T.lastZ.setDouble(instance, value);
    }

    public double getLocX() {
        return T.locX.getDouble(instance);
    }

    public void setLocX(double value) {
        T.locX.setDouble(instance, value);
    }

    public double getLocY() {
        return T.locY.getDouble(instance);
    }

    public void setLocY(double value) {
        T.locY.setDouble(instance, value);
    }

    public double getLocZ() {
        return T.locZ.getDouble(instance);
    }

    public void setLocZ(double value) {
        T.locZ.setDouble(instance, value);
    }

    public double getMotX() {
        return T.motX.getDouble(instance);
    }

    public void setMotX(double value) {
        T.motX.setDouble(instance, value);
    }

    public double getMotY() {
        return T.motY.getDouble(instance);
    }

    public void setMotY(double value) {
        T.motY.setDouble(instance, value);
    }

    public double getMotZ() {
        return T.motZ.getDouble(instance);
    }

    public void setMotZ(double value) {
        T.motZ.setDouble(instance, value);
    }

    public float getYaw() {
        return T.yaw.getFloat(instance);
    }

    public void setYaw(float value) {
        T.yaw.setFloat(instance, value);
    }

    public float getPitch() {
        return T.pitch.getFloat(instance);
    }

    public void setPitch(float value) {
        T.pitch.setFloat(instance, value);
    }

    public float getLastYaw() {
        return T.lastYaw.getFloat(instance);
    }

    public void setLastYaw(float value) {
        T.lastYaw.setFloat(instance, value);
    }

    public float getLastPitch() {
        return T.lastPitch.getFloat(instance);
    }

    public void setLastPitch(float value) {
        T.lastPitch.setFloat(instance, value);
    }

    public AxisAlignedBBHandle getBoundingBoxField() {
        return T.boundingBoxField.get(instance);
    }

    public void setBoundingBoxField(AxisAlignedBBHandle value) {
        T.boundingBoxField.set(instance, value);
    }

    public boolean isOnGround() {
        return T.onGround.getBoolean(instance);
    }

    public void setOnGround(boolean value) {
        T.onGround.setBoolean(instance, value);
    }

    public boolean isHorizontalMovementImpaired() {
        return T.horizontalMovementImpaired.getBoolean(instance);
    }

    public void setHorizontalMovementImpaired(boolean value) {
        T.horizontalMovementImpaired.setBoolean(instance, value);
    }

    public boolean isVerticalMovementImpaired() {
        return T.verticalMovementImpaired.getBoolean(instance);
    }

    public void setVerticalMovementImpaired(boolean value) {
        T.verticalMovementImpaired.setBoolean(instance, value);
    }

    public boolean isMovementImpaired() {
        return T.movementImpaired.getBoolean(instance);
    }

    public void setMovementImpaired(boolean value) {
        T.movementImpaired.setBoolean(instance, value);
    }

    public boolean isVelocityChanged() {
        return T.velocityChanged.getBoolean(instance);
    }

    public void setVelocityChanged(boolean value) {
        T.velocityChanged.setBoolean(instance, value);
    }

    public boolean isJustLanded() {
        return T.justLanded.getBoolean(instance);
    }

    public void setJustLanded(boolean value) {
        T.justLanded.setBoolean(instance, value);
    }

    public boolean isDead() {
        return T.dead.getBoolean(instance);
    }

    public void setDead(boolean value) {
        T.dead.setBoolean(instance, value);
    }

    public float getWidth() {
        return T.width.getFloat(instance);
    }

    public void setWidth(float value) {
        T.width.setFloat(instance, value);
    }

    public float getLength() {
        return T.length.getFloat(instance);
    }

    public void setLength(float value) {
        T.length.setFloat(instance, value);
    }

    public float getWalkedDistanceXZ() {
        return T.walkedDistanceXZ.getFloat(instance);
    }

    public void setWalkedDistanceXZ(float value) {
        T.walkedDistanceXZ.setFloat(instance, value);
    }

    public float getWalkedDistanceXYZ() {
        return T.walkedDistanceXYZ.getFloat(instance);
    }

    public void setWalkedDistanceXYZ(float value) {
        T.walkedDistanceXYZ.setFloat(instance, value);
    }

    public float getFallDistance() {
        return T.fallDistance.getFloat(instance);
    }

    public void setFallDistance(float value) {
        T.fallDistance.setFloat(instance, value);
    }

    public int getStepCounter() {
        return T.stepCounter.getInteger(instance);
    }

    public void setStepCounter(int value) {
        T.stepCounter.setInteger(instance, value);
    }

    public float getHeightOffset() {
        return T.heightOffset.getFloat(instance);
    }

    public void setHeightOffset(float value) {
        T.heightOffset.setFloat(instance, value);
    }

    public boolean isNoclip() {
        return T.noclip.getBoolean(instance);
    }

    public void setNoclip(boolean value) {
        T.noclip.setBoolean(instance, value);
    }

    public Random getRandom() {
        return T.random.get(instance);
    }

    public void setRandom(Random value) {
        T.random.set(instance, value);
    }

    public int getTicksLived() {
        return T.ticksLived.getInteger(instance);
    }

    public void setTicksLived(int value) {
        T.ticksLived.setInteger(instance, value);
    }

    public int getFireTicks() {
        return T.fireTicks.getInteger(instance);
    }

    public void setFireTicks(int value) {
        T.fireTicks.setInteger(instance, value);
    }

    public DataWatcher getDatawatcherField() {
        return T.datawatcherField.get(instance);
    }

    public void setDatawatcherField(DataWatcher value) {
        T.datawatcherField.set(instance, value);
    }

    public boolean isLoaded() {
        return T.isLoaded.getBoolean(instance);
    }

    public void setIsLoaded(boolean value) {
        T.isLoaded.setBoolean(instance, value);
    }

    public int getChunkX() {
        return T.chunkX.getInteger(instance);
    }

    public void setChunkX(int value) {
        T.chunkX.setInteger(instance, value);
    }

    public int getChunkY() {
        return T.chunkY.getInteger(instance);
    }

    public void setChunkY(int value) {
        T.chunkY.setInteger(instance, value);
    }

    public int getChunkZ() {
        return T.chunkZ.getInteger(instance);
    }

    public void setChunkZ(int value) {
        T.chunkZ.setInteger(instance, value);
    }

    public boolean isPositionChanged() {
        return T.positionChanged.getBoolean(instance);
    }

    public void setPositionChanged(boolean value) {
        T.positionChanged.setBoolean(instance, value);
    }

    public int getPortalCooldown() {
        return T.portalCooldown.getInteger(instance);
    }

    public void setPortalCooldown(int value) {
        T.portalCooldown.setInteger(instance, value);
    }

    public boolean isAllowTeleportation() {
        return T.allowTeleportation.getBoolean(instance);
    }

    public void setAllowTeleportation(boolean value) {
        T.allowTeleportation.setBoolean(instance, value);
    }

    public int getDimension() {
        return T.dimension.getInteger(instance);
    }

    public void setDimension(int value) {
        T.dimension.setInteger(instance, value);
    }

    public boolean isValid() {
        return T.valid.getBoolean(instance);
    }

    public void setValid(boolean value) {
        T.valid.setBoolean(instance, value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.Entity</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityClass extends Template.Class<EntityHandle> {
        public final Template.Constructor.Converted<EntityHandle> constr_world = new Template.Constructor.Converted<EntityHandle>();

        public final Template.StaticField.Integer entityCount = new Template.StaticField.Integer();
        @Template.Optional
        public final Template.StaticField.Converted<Key<Byte>> DATA_FLAGS = new Template.StaticField.Converted<Key<Byte>>();
        @Template.Optional
        public final Template.StaticField.Converted<Key<Integer>> DATA_AIR_TICKS = new Template.StaticField.Converted<Key<Integer>>();
        @Template.Optional
        public final Template.StaticField.Converted<Key<String>> DATA_CUSTOM_NAME = new Template.StaticField.Converted<Key<String>>();
        @Template.Optional
        public final Template.StaticField.Converted<Key<Boolean>> DATA_CUSTOM_NAME_VISIBLE = new Template.StaticField.Converted<Key<Boolean>>();
        @Template.Optional
        public final Template.StaticField.Converted<Key<Boolean>> DATA_SILENT = new Template.StaticField.Converted<Key<Boolean>>();
        @Template.Optional
        public final Template.StaticField.Converted<Key<Boolean>> DATA_NO_GRAVITY = new Template.StaticField.Converted<Key<Boolean>>();

        public final Template.Field.Converted<Entity> bukkitEntityField = new Template.Field.Converted<Entity>();
        public final Template.Field.Integer idField = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Converted<List<EntityHandle>> opt_passengers = new Template.Field.Converted<List<EntityHandle>>();
        @Template.Optional
        public final Template.Field.Converted<EntityHandle> opt_passenger = new Template.Field.Converted<EntityHandle>();
        public final Template.Field.Converted<EntityHandle> vehicle = new Template.Field.Converted<EntityHandle>();
        public final Template.Field.Boolean ignoreChunkCheck = new Template.Field.Boolean();
        public final Template.Field.Converted<WorldHandle> world = new Template.Field.Converted<WorldHandle>();
        public final Template.Field.Double lastX = new Template.Field.Double();
        public final Template.Field.Double lastY = new Template.Field.Double();
        public final Template.Field.Double lastZ = new Template.Field.Double();
        public final Template.Field.Double locX = new Template.Field.Double();
        public final Template.Field.Double locY = new Template.Field.Double();
        public final Template.Field.Double locZ = new Template.Field.Double();
        public final Template.Field.Double motX = new Template.Field.Double();
        public final Template.Field.Double motY = new Template.Field.Double();
        public final Template.Field.Double motZ = new Template.Field.Double();
        public final Template.Field.Float yaw = new Template.Field.Float();
        public final Template.Field.Float pitch = new Template.Field.Float();
        public final Template.Field.Float lastYaw = new Template.Field.Float();
        public final Template.Field.Float lastPitch = new Template.Field.Float();
        public final Template.Field.Converted<AxisAlignedBBHandle> boundingBoxField = new Template.Field.Converted<AxisAlignedBBHandle>();
        public final Template.Field.Boolean onGround = new Template.Field.Boolean();
        public final Template.Field.Boolean horizontalMovementImpaired = new Template.Field.Boolean();
        public final Template.Field.Boolean verticalMovementImpaired = new Template.Field.Boolean();
        public final Template.Field.Boolean movementImpaired = new Template.Field.Boolean();
        public final Template.Field.Boolean velocityChanged = new Template.Field.Boolean();
        public final Template.Field.Boolean justLanded = new Template.Field.Boolean();
        public final Template.Field.Boolean dead = new Template.Field.Boolean();
        public final Template.Field.Float width = new Template.Field.Float();
        public final Template.Field.Float length = new Template.Field.Float();
        public final Template.Field.Float walkedDistanceXZ = new Template.Field.Float();
        public final Template.Field.Float walkedDistanceXYZ = new Template.Field.Float();
        public final Template.Field.Float fallDistance = new Template.Field.Float();
        public final Template.Field.Integer stepCounter = new Template.Field.Integer();
        public final Template.Field.Float heightOffset = new Template.Field.Float();
        public final Template.Field.Boolean noclip = new Template.Field.Boolean();
        public final Template.Field<Random> random = new Template.Field<Random>();
        public final Template.Field.Integer ticksLived = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Integer field_maxFireTicks = new Template.Field.Integer();
        public final Template.Field.Integer fireTicks = new Template.Field.Integer();
        public final Template.Field.Converted<DataWatcher> datawatcherField = new Template.Field.Converted<DataWatcher>();
        public final Template.Field.Boolean isLoaded = new Template.Field.Boolean();
        public final Template.Field.Integer chunkX = new Template.Field.Integer();
        public final Template.Field.Integer chunkY = new Template.Field.Integer();
        public final Template.Field.Integer chunkZ = new Template.Field.Integer();
        public final Template.Field.Boolean positionChanged = new Template.Field.Boolean();
        public final Template.Field.Integer portalCooldown = new Template.Field.Integer();
        public final Template.Field.Boolean allowTeleportation = new Template.Field.Boolean();
        public final Template.Field.Integer dimension = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field<double[]> move_SomeArray = new Template.Field<double[]>();
        @Template.Optional
        public final Template.Field.Long move_SomeState = new Template.Field.Long();
        public final Template.Field.Boolean valid = new Template.Field.Boolean();

        public final Template.Method<Void> updateBlockCollision = new Template.Method<Void>();
        public final Template.Method.Converted<Void> playStepSound = new Template.Method.Converted<Void>();
        public final Template.Method<Void> setRotation = new Template.Method<Void>();
        public final Template.Method<Void> burn = new Template.Method<Void>();
        public final Template.Method.Converted<Item> dropItem = new Template.Method.Converted<Item>();
        public final Template.Method.Converted<Item> dropItemStack = new Template.Method.Converted<Item>();
        public final Template.Method.Converted<ResourceKey> getSwimSound = new Template.Method.Converted<ResourceKey>();
        public final Template.Method.Converted<Void> makeSound = new Template.Method.Converted<Void>();
        public final Template.Method<Boolean> isInWaterUpdate = new Template.Method<Boolean>();
        public final Template.Method<Boolean> isInWater = new Template.Method<Boolean>();
        public final Template.Method<Boolean> hasMovementSound = new Template.Method<Boolean>();
        public final Template.Method.Converted<Void> updateFalling = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Void> doStepSoundUpdate = new Template.Method.Converted<Void>();
        public final Template.Method<Void> checkBlockCollisions = new Template.Method<Void>();
        public final Template.Method<Double> calculateDistance = new Template.Method<Double>();
        public final Template.Method.Converted<Boolean> damageEntity = new Template.Method.Converted<Boolean>();
        public final Template.Method<Void> setPosition = new Template.Method<Void>();
        public final Template.Method<Void> setPositionRotation = new Template.Method<Void>();
        public final Template.Method<Void> setLocation = new Template.Method<Void>();
        public final Template.Method<Float> getHeadRotation = new Template.Method<Float>();
        public final Template.Method.Converted<AxisAlignedBBHandle> getBoundingBox = new Template.Method.Converted<AxisAlignedBBHandle>();
        public final Template.Method.Converted<Void> setBoundingBox = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<AxisAlignedBBHandle> getOtherBoundingBox = new Template.Method.Converted<AxisAlignedBBHandle>();
        public final Template.Method.Converted<AxisAlignedBBHandle> getEntityBoundingBox = new Template.Method.Converted<AxisAlignedBBHandle>();
        @Template.Optional
        public final Template.Method<Boolean> isVehicle = new Template.Method<Boolean>();
        @Template.Optional
        public final Template.Method<Boolean> isPassenger = new Template.Method<Boolean>();
        public final Template.Method<Void> recalcPosition = new Template.Method<Void>();
        public final Template.Method<Boolean> isBurning = new Template.Method<Boolean>();
        public final Template.Method<Void> setOnFire = new Template.Method<Void>();
        @Template.Optional
        public final Template.Method<Integer> prop_getMaxFireTicks = new Template.Method<Integer>();
        public final Template.Method<Boolean> isWet = new Template.Method<Boolean>();
        @Template.Optional
        public final Template.Method.Converted<EntityHandle> getDriverEntity = new Template.Method.Converted<EntityHandle>();
        public final Template.Method.Converted<Void> saveToNBT = new Template.Method.Converted<Void>();
        public final Template.Method<Void> onTick = new Template.Method<Void>();
        public final Template.Method.Converted<Void> loadFromNBT = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Boolean> savePassenger = new Template.Method.Converted<Boolean>();
        public final Template.Method.Converted<Boolean> saveEntity = new Template.Method.Converted<Boolean>();
        public final Template.Method<Boolean> isSneaking = new Template.Method<Boolean>();
        @Template.Optional
        public final Template.Method.Converted<Boolean> isInSameVehicle = new Template.Method.Converted<Boolean>();
        public final Template.Method.Converted<Void> appendEntityCrashDetails = new Template.Method.Converted<Void>();
        public final Template.Method<Integer> getId = new Template.Method<Integer>();
        public final Template.Method<UUID> getUniqueID = new Template.Method<UUID>();
        public final Template.Method.Converted<DataWatcher> getDataWatcher = new Template.Method.Converted<DataWatcher>();
        public final Template.Method<Void> onPush = new Template.Method<Void>();
        @Template.Optional
        public final Template.Method.Converted<Boolean> onInteractBy_1_8_8 = new Template.Method.Converted<Boolean>();
        @Template.Optional
        public final Template.Method.Converted<Boolean> onInteractBy_1_10_2 = new Template.Method.Converted<Boolean>();
        @Template.Optional
        public final Template.Method.Converted<Boolean> onInteractBy_1_11_2 = new Template.Method.Converted<Boolean>();
        public final Template.Method.Converted<Void> collide = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Entity> getBukkitEntity = new Template.Method.Converted<Entity>();

    }

}

