package com.bergerkiller.generated.net.minecraft.world.entity;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher.Key;
import com.bergerkiller.bukkit.common.wrappers.InteractionResult;
import com.bergerkiller.generated.net.minecraft.CrashReportSystemDetailsHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryHandle;
import com.bergerkiller.generated.net.minecraft.util.RandomSourceHandle;
import com.bergerkiller.generated.net.minecraft.world.damagesource.DamageSourceHandle;
import com.bergerkiller.generated.net.minecraft.world.level.WorldHandle;
import com.bergerkiller.generated.net.minecraft.world.level.storage.ValueOutputHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.AxisAlignedBBHandle;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.entity.Entity</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.entity.Entity")
public abstract class EntityHandle extends Template.Handle {
    /** @see EntityClass */
    public static final EntityClass T = Template.Class.create(EntityClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static EntityHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract void setTrackerEntry(EntityTrackerEntryHandle tracker);
    public abstract List<EntityHandle> getPassengers();
    public abstract boolean isVehicle();
    public abstract boolean isPassenger();
    public abstract boolean hasPassengers();
    public abstract void setPassengers(List<EntityHandle> passengers);
    public abstract boolean isInSameVehicle(EntityHandle entity);
    public abstract boolean isIgnoreChunkCheck();
    public abstract void setIgnoreChunkCheck(boolean ignore);
    public abstract double getLocX();
    public abstract double getLocY();
    public abstract double getLocZ();
    public abstract void setLoc(double x, double y, double z);
    public abstract void setLocX(double x);
    public abstract void setLocY(double y);
    public abstract void setLocZ(double z);
    public abstract Vector getLoc();
    public abstract void assignEntityReference();
    public abstract boolean isLastAndCurrentPositionDifferent();
    public abstract Vector getMot();
    public abstract void setMotVector(Vector mot);
    public abstract void setMot(double x, double y, double z);
    public abstract void fixMotNaN();
    public abstract void setMotX(double x);
    public abstract void setMotY(double y);
    public abstract void setMotZ(double z);
    public abstract double getMotX();
    public abstract double getMotY();
    public abstract double getMotZ();
    public abstract void setHorizontalMovementBlocked(boolean blocked);
    public abstract void setVerticalMovementBlocked(boolean blocked);
    public abstract boolean isCollidingWithBlock();
    public abstract Vector getBlockCollisionMultiplier();
    public abstract void setNotCollidingWithBlock();
    public abstract void setRemovedPassive();
    public abstract void setDestroyed(boolean dead);
    public abstract boolean isDestroyed();
    public abstract boolean isSavingAllowed();
    public abstract boolean isLoadedInWorld();
    public abstract int getChunkX();
    public abstract int getChunkY();
    public abstract int getChunkZ();
    public abstract Chunk getCurrentChunk();
    public abstract float getWidth();
    public abstract float getHeight();
    public abstract void setStepCounter(float value);
    public abstract float getStepCounter();
    public abstract float getHeightOffset();
    public abstract void playStepSound(IntVector3 position, BlockData blockData);
    public abstract void setRotation(float yaw, float pitch);
    public abstract void burn(float dmg);
    public abstract Item dropItem(Material material, int amount, float force);
    public abstract Item dropItemStack(ItemStack itemstack, float force);
    public abstract ResourceKey<SoundEffect> getSwimSound();
    public abstract void handleMovementEmissionAndPlaySound(Vector movement, IntVector3 blockPosition, BlockData iblockdata);
    public abstract void makeSound(ResourceKey<SoundEffect> soundeffect, float volume, float pitch);
    public abstract boolean isWet();
    public abstract boolean isInWaterUpdate();
    public abstract boolean isInWater();
    public abstract boolean hasMovementSound();
    public abstract void updateFalling(double d0, boolean flag, BlockData blockData, IntVector3 position);
    public abstract boolean isOutsideWorldBorder();
    public abstract void setOutsideWorldBorder(boolean outside);
    public abstract void applyEffectsFromBlocks();
    public abstract double calculateDistanceSquared(double x, double y, double z);
    public abstract boolean damageEntity(DamageSourceHandle damagesource, float damage);
    public abstract String getStringUUID();
    public abstract void setPosition(double x, double y, double z);
    public abstract void setSize(float width, float height);
    public abstract AxisAlignedBBHandle getBoundingBox();
    public abstract void setPositionRotation(double x, double y, double z, float yaw, float pitch);
    public abstract void setLocation(double x, double y, double z, float yaw, float pitch);
    public abstract void setBoundingBox(AxisAlignedBBHandle axisalignedbb);
    public abstract float getHeadRotation();
    public abstract void setHeadRotation(float angle);
    public abstract boolean canCollideWith(EntityHandle otherEntity);
    public abstract AxisAlignedBBHandle getEntityBoundingBox(EntityHandle entity);
    public abstract void setPositionFromBoundingBox();
    public abstract void handleFireBlockTick();
    public abstract boolean isBurning();
    public abstract void setOnFire(float numSeconds);
    public abstract EntityHandle getDriverEntity();
    public abstract void onTick();
    public abstract void loadFromNBT(CommonTagCompound compound);
    public abstract void saveWithoutId(ValueOutputHandle valueoutput, boolean includeAll, boolean includeNonSaveable, boolean forceSerialization);
    public abstract void saveToNBT(CommonTagCompound compound);
    public abstract int getId();
    public abstract UUID getUniqueID();
    public abstract DataWatcher getDataWatcher();
    public abstract boolean isSneaking();
    public abstract void appendEntityCrashDetails(CrashReportSystemDetailsHandle crashreportsystemdetails);
    public abstract void onPush(double d0, double d1, double d2);
    public abstract void positionRider(Entity passenger);
    public abstract int getPortalCooldownMaximum();
    public abstract boolean isInsidePortalThisTick();
    public abstract void suppressPortalThisTick();
    public abstract int getPortalTime();
    public abstract boolean setPortalTime(int portalTimeTicks);
    public abstract int getPortalWaitTime();
    public abstract boolean isAlwaysTicked();
    public abstract boolean hasCustomName();
    public abstract ChatText getCustomName();
    public abstract void collide(EntityHandle entity);
    public abstract World getBukkitWorld();
    public abstract WorldHandle getWorld();
    public abstract void setWorld(WorldHandle world);
    public abstract Entity getBukkitEntity();
    public com.bergerkiller.generated.net.minecraft.server.level.WorldServerHandle getWorldServer() {
        return com.bergerkiller.generated.net.minecraft.server.level.WorldServerHandle.createHandle(T.getWorld.raw.invoke(getRaw()));
    }

    @Deprecated
    public boolean isDead() {
        return isDestroyed();
    }

    public static final DataWatcher.Key<Byte> DATA_FLAGS = DataWatcher.Key.Type.BYTE.createKey(T.DATA_FLAGS, 0);
    public static final DataWatcher.Key<Integer> DATA_AIR_TICKS = DataWatcher.Key.Type.INTEGER.createKey(T.DATA_AIR_TICKS, 1);
    public static final DataWatcher.Key<ChatText> DATA_CUSTOM_NAME;
    static {
        if (com.bergerkiller.bukkit.common.Common.evaluateMCVersion(">=", "1.13")) {
            DATA_CUSTOM_NAME = DataWatcher.Key.Type.CHAT_TEXT.createKey(T.DATA_CUSTOM_NAME, 2);
        } else {
            DATA_CUSTOM_NAME = DataWatcher.Key.Type.STRING.translate(ChatText.class).createKey(T.DATA_CUSTOM_NAME, 2);
        }
    }
    public static final DataWatcher.Key<Boolean> DATA_CUSTOM_NAME_VISIBLE = DataWatcher.Key.Type.BOOLEAN.createKey(T.DATA_CUSTOM_NAME_VISIBLE, 3);
    public static final DataWatcher.Key<Boolean> DATA_SILENT = DataWatcher.Key.Type.BOOLEAN.createKey(T.DATA_SILENT, 4);
    public static final DataWatcher.Key<Boolean> DATA_NO_GRAVITY = DataWatcher.Key.Type.BOOLEAN.createKey(T.DATA_NO_GRAVITY, -1);

    public static final int DATA_FLAG_ON_FIRE = (1 << 0);
    public static final int DATA_FLAG_SNEAKING = (1 << 1);
    public static final int DATA_FLAG_UNKNOWN1 = (1 << 2);
    public static final int DATA_FLAG_SPRINTING = (1 << 3);
    public static final int DATA_FLAG_UNKNOWN2 = (1 << 4);
    public static final int DATA_FLAG_INVISIBLE = (1 << 5);
    public static final int DATA_FLAG_GLOWING = (1 << 6);
    public static final int DATA_FLAG_FLYING = (1 << 7);

    public int getMaxFireTicks() {
        if (T.prop_getMaxFireTicks.isAvailable()) {
            return T.prop_getMaxFireTicks.invoke(getRaw());
        } else if (T.field_maxFireTicks.isAvailable()) {
            return T.field_maxFireTicks.getInteger(getRaw());
        } else {
            throw new UnsupportedOperationException("Max Fire Ticks can not be read");
        }
    }

    public static EntityHandle fromBukkit(org.bukkit.entity.Entity entity) {
        return createHandle(com.bergerkiller.bukkit.common.conversion.type.HandleConversion.toEntityHandle(entity));
    }
    public abstract Entity getBukkitEntityField();
    public abstract void setBukkitEntityField(Entity value);
    public abstract int getIdField();
    public abstract void setIdField(int value);
    public abstract boolean isPreventBlockPlace();
    public abstract void setPreventBlockPlace(boolean value);
    public abstract EntityHandle getVehicle();
    public abstract void setVehicle(EntityHandle value);
    public abstract double getLastX();
    public abstract void setLastX(double value);
    public abstract double getLastY();
    public abstract void setLastY(double value);
    public abstract double getLastZ();
    public abstract void setLastZ(double value);
    public abstract float getYaw();
    public abstract void setYaw(float value);
    public abstract float getPitch();
    public abstract void setPitch(float value);
    public abstract float getLastYaw();
    public abstract void setLastYaw(float value);
    public abstract float getLastPitch();
    public abstract void setLastPitch(float value);
    public abstract AxisAlignedBBHandle getBoundingBoxField();
    public abstract void setBoundingBoxField(AxisAlignedBBHandle value);
    public abstract boolean isOnGround();
    public abstract void setOnGround(boolean value);
    @Template.Readonly
    public abstract boolean isHorizontalMovementBlocked();
    @Template.Readonly
    public abstract boolean isVerticalMovementBlocked();
    public abstract boolean isVelocityChanged();
    public abstract void setVelocityChanged(boolean value);
    public abstract float getFallDistance();
    public abstract void setFallDistance(float value);
    public abstract boolean isNoclip();
    public abstract void setNoclip(boolean value);
    public abstract RandomSourceHandle getRandom();
    public abstract void setRandom(RandomSourceHandle value);
    public abstract int getFireTicks();
    public abstract void setFireTicks(int value);
    public abstract int getTicksLived();
    public abstract void setTicksLived(int value);
    public abstract DataWatcher getDatawatcherField();
    public abstract void setDatawatcherField(DataWatcher value);
    public abstract boolean isPositionChanged();
    public abstract void setPositionChanged(boolean value);
    public abstract int getPortalCooldown();
    public abstract void setPortalCooldown(int value);
    public abstract boolean isValid();
    public abstract void setValid(boolean value);
    /**
     * Stores class members for <b>net.minecraft.world.entity.Entity</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityClass extends Template.Class<EntityHandle> {
        @Template.Optional
        public final Template.StaticField<AtomicInteger> opt_atomic_entityCount = new Template.StaticField<AtomicInteger>();
        @Template.Optional
        public final Template.StaticField.Integer opt_int_entityCount = new Template.StaticField.Integer();
        @Template.Optional
        public final Template.StaticField.Converted<Key<Byte>> DATA_FLAGS = new Template.StaticField.Converted<Key<Byte>>();
        @Template.Optional
        public final Template.StaticField.Converted<Key<Integer>> DATA_AIR_TICKS = new Template.StaticField.Converted<Key<Integer>>();
        @Template.Optional
        public final Template.StaticField.Converted<Key<?>> DATA_CUSTOM_NAME = new Template.StaticField.Converted<Key<?>>();
        @Template.Optional
        public final Template.StaticField.Converted<Key<Boolean>> DATA_CUSTOM_NAME_VISIBLE = new Template.StaticField.Converted<Key<Boolean>>();
        @Template.Optional
        public final Template.StaticField.Converted<Key<Boolean>> DATA_SILENT = new Template.StaticField.Converted<Key<Boolean>>();
        @Template.Optional
        public final Template.StaticField.Converted<Key<Boolean>> DATA_NO_GRAVITY = new Template.StaticField.Converted<Key<Boolean>>();

        public final Template.Field.Converted<Entity> bukkitEntityField = new Template.Field.Converted<Entity>();
        public final Template.Field.Integer idField = new Template.Field.Integer();
        public final Template.Field.Boolean preventBlockPlace = new Template.Field.Boolean();
        public final Template.Field.Converted<EntityHandle> vehicle = new Template.Field.Converted<EntityHandle>();
        public final Template.Field.Double lastX = new Template.Field.Double();
        public final Template.Field.Double lastY = new Template.Field.Double();
        public final Template.Field.Double lastZ = new Template.Field.Double();
        public final Template.Field.Float yaw = new Template.Field.Float();
        public final Template.Field.Float pitch = new Template.Field.Float();
        public final Template.Field.Float lastYaw = new Template.Field.Float();
        public final Template.Field.Float lastPitch = new Template.Field.Float();
        public final Template.Field.Converted<AxisAlignedBBHandle> boundingBoxField = new Template.Field.Converted<AxisAlignedBBHandle>();
        public final Template.Field.Boolean onGround = new Template.Field.Boolean();
        @Template.Readonly
        public final Template.Field.Boolean horizontalMovementBlocked = new Template.Field.Boolean();
        @Template.Readonly
        public final Template.Field.Boolean verticalMovementBlocked = new Template.Field.Boolean();
        public final Template.Field.Boolean velocityChanged = new Template.Field.Boolean();
        public final Template.Field.Float fallDistance = new Template.Field.Float();
        public final Template.Field.Boolean noclip = new Template.Field.Boolean();
        public final Template.Field.Converted<RandomSourceHandle> random = new Template.Field.Converted<RandomSourceHandle>();
        public final Template.Field.Integer fireTicks = new Template.Field.Integer();
        public final Template.Field.Integer ticksLived = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Integer field_maxFireTicks = new Template.Field.Integer();
        public final Template.Field.Converted<DataWatcher> datawatcherField = new Template.Field.Converted<DataWatcher>();
        public final Template.Field.Boolean positionChanged = new Template.Field.Boolean();
        public final Template.Field.Integer portalCooldown = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field<double[]> move_SomeArray = new Template.Field<double[]>();
        @Template.Optional
        public final Template.Field.Long move_SomeState = new Template.Field.Long();
        public final Template.Field.Boolean valid = new Template.Field.Boolean();

        public final Template.Method.Converted<Void> setTrackerEntry = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<List<EntityHandle>> getPassengers = new Template.Method.Converted<List<EntityHandle>>();
        public final Template.Method<Boolean> isVehicle = new Template.Method<Boolean>();
        public final Template.Method<Boolean> isPassenger = new Template.Method<Boolean>();
        public final Template.Method<Boolean> hasPassengers = new Template.Method<Boolean>();
        public final Template.Method.Converted<Void> setPassengers = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Boolean> isInSameVehicle = new Template.Method.Converted<Boolean>();
        public final Template.Method<Boolean> isIgnoreChunkCheck = new Template.Method<Boolean>();
        public final Template.Method<Void> setIgnoreChunkCheck = new Template.Method<Void>();
        public final Template.Method<Double> getLocX = new Template.Method<Double>();
        public final Template.Method<Double> getLocY = new Template.Method<Double>();
        public final Template.Method<Double> getLocZ = new Template.Method<Double>();
        public final Template.Method<Void> setLoc = new Template.Method<Void>();
        public final Template.Method<Void> setLocX = new Template.Method<Void>();
        public final Template.Method<Void> setLocY = new Template.Method<Void>();
        public final Template.Method<Void> setLocZ = new Template.Method<Void>();
        public final Template.Method<Vector> getLoc = new Template.Method<Vector>();
        public final Template.Method<Void> assignEntityReference = new Template.Method<Void>();
        public final Template.Method<Boolean> isLastAndCurrentPositionDifferent = new Template.Method<Boolean>();
        public final Template.Method<Vector> getMot = new Template.Method<Vector>();
        public final Template.Method<Void> setMotVector = new Template.Method<Void>();
        public final Template.Method<Void> setMot = new Template.Method<Void>();
        public final Template.Method<Void> fixMotNaN = new Template.Method<Void>();
        public final Template.Method<Void> setMotX = new Template.Method<Void>();
        public final Template.Method<Void> setMotY = new Template.Method<Void>();
        public final Template.Method<Void> setMotZ = new Template.Method<Void>();
        public final Template.Method<Double> getMotX = new Template.Method<Double>();
        public final Template.Method<Double> getMotY = new Template.Method<Double>();
        public final Template.Method<Double> getMotZ = new Template.Method<Double>();
        public final Template.Method<Void> setHorizontalMovementBlocked = new Template.Method<Void>();
        public final Template.Method<Void> setVerticalMovementBlocked = new Template.Method<Void>();
        public final Template.Method<Boolean> isCollidingWithBlock = new Template.Method<Boolean>();
        public final Template.Method<Vector> getBlockCollisionMultiplier = new Template.Method<Vector>();
        public final Template.Method<Void> setNotCollidingWithBlock = new Template.Method<Void>();
        public final Template.Method<Void> setRemovedPassive = new Template.Method<Void>();
        public final Template.Method<Void> setDestroyed = new Template.Method<Void>();
        public final Template.Method<Boolean> isDestroyed = new Template.Method<Boolean>();
        public final Template.Method<Boolean> isSavingAllowed = new Template.Method<Boolean>();
        public final Template.Method<Boolean> isLoadedInWorld = new Template.Method<Boolean>();
        public final Template.Method<Integer> getChunkX = new Template.Method<Integer>();
        public final Template.Method<Integer> getChunkY = new Template.Method<Integer>();
        public final Template.Method<Integer> getChunkZ = new Template.Method<Integer>();
        @Template.Optional
        public final Template.Method<Void> setLoadedInWorld_pre_1_17 = new Template.Method<Void>();
        public final Template.Method.Converted<Chunk> getCurrentChunk = new Template.Method.Converted<Chunk>();
        public final Template.Method<Float> getWidth = new Template.Method<Float>();
        public final Template.Method<Float> getHeight = new Template.Method<Float>();
        public final Template.Method<Void> setStepCounter = new Template.Method<Void>();
        public final Template.Method<Float> getStepCounter = new Template.Method<Float>();
        public final Template.Method<Float> getHeightOffset = new Template.Method<Float>();
        public final Template.Method.Converted<Void> playStepSound = new Template.Method.Converted<Void>();
        public final Template.Method<Void> setRotation = new Template.Method<Void>();
        public final Template.Method<Void> burn = new Template.Method<Void>();
        public final Template.Method.Converted<Item> dropItem = new Template.Method.Converted<Item>();
        public final Template.Method.Converted<Item> dropItemStack = new Template.Method.Converted<Item>();
        public final Template.Method.Converted<ResourceKey<SoundEffect>> getSwimSound = new Template.Method.Converted<ResourceKey<SoundEffect>>();
        public final Template.Method.Converted<Void> handleMovementEmissionAndPlaySound = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Void> makeSound = new Template.Method.Converted<Void>();
        public final Template.Method<Boolean> isWet = new Template.Method<Boolean>();
        public final Template.Method<Boolean> isInWaterUpdate = new Template.Method<Boolean>();
        public final Template.Method<Boolean> isInWater = new Template.Method<Boolean>();
        public final Template.Method<Boolean> hasMovementSound = new Template.Method<Boolean>();
        public final Template.Method.Converted<Void> updateFalling = new Template.Method.Converted<Void>();
        @Template.Optional
        public final Template.Method<Void> setLegacyTrackingEntity = new Template.Method<Void>();
        public final Template.Method<Boolean> isOutsideWorldBorder = new Template.Method<Boolean>();
        public final Template.Method<Void> setOutsideWorldBorder = new Template.Method<Void>();
        public final Template.Method<Void> applyEffectsFromBlocks = new Template.Method<Void>();
        public final Template.Method<Double> calculateDistanceSquared = new Template.Method<Double>();
        public final Template.Method.Converted<Boolean> damageEntity = new Template.Method.Converted<Boolean>();
        public final Template.Method<String> getStringUUID = new Template.Method<String>();
        public final Template.Method<Void> setPosition = new Template.Method<Void>();
        public final Template.Method<Void> setSize = new Template.Method<Void>();
        public final Template.Method.Converted<AxisAlignedBBHandle> getBoundingBox = new Template.Method.Converted<AxisAlignedBBHandle>();
        public final Template.Method<Void> setPositionRotation = new Template.Method<Void>();
        public final Template.Method<Void> setLocation = new Template.Method<Void>();
        public final Template.Method.Converted<Void> setBoundingBox = new Template.Method.Converted<Void>();
        public final Template.Method<Float> getHeadRotation = new Template.Method<Float>();
        public final Template.Method<Void> setHeadRotation = new Template.Method<Void>();
        public final Template.Method.Converted<Boolean> canCollideWith = new Template.Method.Converted<Boolean>();
        public final Template.Method.Converted<AxisAlignedBBHandle> getEntityBoundingBox = new Template.Method.Converted<AxisAlignedBBHandle>();
        public final Template.Method<Void> setPositionFromBoundingBox = new Template.Method<Void>();
        public final Template.Method<Void> handleFireBlockTick = new Template.Method<Void>();
        public final Template.Method<Boolean> isBurning = new Template.Method<Boolean>();
        public final Template.Method.Converted<Void> setOnFire = new Template.Method.Converted<Void>();
        @Template.Optional
        public final Template.Method<Integer> prop_getMaxFireTicks = new Template.Method<Integer>();
        public final Template.Method.Converted<EntityHandle> getDriverEntity = new Template.Method.Converted<EntityHandle>();
        public final Template.Method<Void> onTick = new Template.Method<Void>();
        public final Template.Method.Converted<Void> loadFromNBT = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Void> saveWithoutId = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Void> saveToNBT = new Template.Method.Converted<Void>();
        public final Template.Method<Integer> getId = new Template.Method<Integer>();
        public final Template.Method<UUID> getUniqueID = new Template.Method<UUID>();
        public final Template.Method.Converted<DataWatcher> getDataWatcher = new Template.Method.Converted<DataWatcher>();
        public final Template.Method<Boolean> isSneaking = new Template.Method<Boolean>();
        public final Template.Method.Converted<Void> appendEntityCrashDetails = new Template.Method.Converted<Void>();
        public final Template.Method<Void> onPush = new Template.Method<Void>();
        public final Template.Method.Converted<Void> positionRider = new Template.Method.Converted<Void>();
        public final Template.Method<Integer> getPortalCooldownMaximum = new Template.Method<Integer>();
        public final Template.Method<Boolean> isInsidePortalThisTick = new Template.Method<Boolean>();
        public final Template.Method<Void> suppressPortalThisTick = new Template.Method<Void>();
        public final Template.Method<Integer> getPortalTime = new Template.Method<Integer>();
        public final Template.Method<Boolean> setPortalTime = new Template.Method<Boolean>();
        public final Template.Method<Integer> getPortalWaitTime = new Template.Method<Integer>();
        @Template.Optional
        public final Template.Method<Void> opt_tick_pushToHopper = new Template.Method<Void>();
        @Template.Optional
        public final Template.Method<Void> remove = new Template.Method<Void>();
        @Template.Optional
        public final Template.Method.Converted<InteractionResult> onInteractBy_1_16 = new Template.Method.Converted<InteractionResult>();
        @Template.Optional
        public final Template.Method.Converted<Boolean> onInteractBy_1_11_2 = new Template.Method.Converted<Boolean>();
        @Template.Optional
        public final Template.Method.Converted<Boolean> onInteractBy_1_9 = new Template.Method.Converted<Boolean>();
        @Template.Optional
        public final Template.Method.Converted<Boolean> onInteractBy_1_8_8 = new Template.Method.Converted<Boolean>();
        public final Template.Method<Boolean> isAlwaysTicked = new Template.Method<Boolean>();
        public final Template.Method<Boolean> hasCustomName = new Template.Method<Boolean>();
        public final Template.Method.Converted<ChatText> getCustomName = new Template.Method.Converted<ChatText>();
        public final Template.Method.Converted<Void> collide = new Template.Method.Converted<Void>();
        public final Template.Method<World> getBukkitWorld = new Template.Method<World>();
        public final Template.Method.Converted<WorldHandle> getWorld = new Template.Method.Converted<WorldHandle>();
        public final Template.Method.Converted<Void> setWorld = new Template.Method.Converted<Void>();
        public final Template.Method<Entity> getBukkitEntity = new Template.Method<Entity>();

    }

}

