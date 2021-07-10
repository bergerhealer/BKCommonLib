package com.bergerkiller.bukkit.common.controller;

import com.bergerkiller.bukkit.common.bases.mutable.FloatAbstract;
import com.bergerkiller.bukkit.common.bases.mutable.IntegerAbstract;
import com.bergerkiller.bukkit.common.bases.mutable.LocationAbstract;
import com.bergerkiller.bukkit.common.bases.mutable.VectorAbstract;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.entity.CommonEntityController;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.internal.hooks.EntityTrackerEntryHook;
import com.bergerkiller.bukkit.common.internal.logic.EntityTypingHandler;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.*;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryStateHandle;
import com.bergerkiller.generated.net.minecraft.world.effect.MobEffectHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityLivingHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.ai.attributes.AttributeModifiableHandle;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A controller that deals with the server to client network synchronization.
 *
 * @param <T> - type of Common Entity this controller is for
 */
public abstract class EntityNetworkController<T extends CommonEntity<?>> extends CommonEntityController<T> {

    /**
     * The maximum allowed distance per relative movement update
     */
    public static final double MAX_RELATIVE_DISTANCE = 32768.0 / 4096.0;
    /**
     * The minimum value position change that is able to trigger an update
     */
    public static final double MIN_RELATIVE_POS_CHANGE = 128.0 / 4096.0;
    /**
     * The minimum value rotation change that is able to trigger an update
     */
    public static final float MIN_RELATIVE_ROT_CHANGE = 4.0f / 360.0f;
    /**
     * The minimum velocity change that is able to trigger an update
     */
    public static final double MIN_RELATIVE_VELOCITY = 0.02;
    /**
     * The tick interval at which the entity is updated absolutely
     */
    public static final int ABSOLUTE_UPDATE_INTERVAL = 400;

    private EntityTrackerEntryHandle entry;
    private EntityTrackerEntryStateHandle state;
    private org.bukkit.entity.Entity last_passenger_1_8_8 = null;

    /**
     * Obtains the velocity as the clients know it, allowing it to be read from
     * or written to
     */
    public VectorAbstract velSynched = new VectorAbstract() {
        public double getX() {
            return state.getXVel();
        }

        public double getY() {
            return state.getYVel();
        }

        public double getZ() {
            return state.getZVel();
        }

        public VectorAbstract set(double x, double y, double z) {
            state.setVelocity(x, y, z);
            return this;
        }

        public VectorAbstract setX(double x) {
            state.setXVel(x);
            return this;
        }

        public VectorAbstract setY(double y) {
            state.setYVel(y);
            return this;
        }

        public VectorAbstract setZ(double z) {
            state.setZVel(z);
            return this;
        }
    };
    /**
     * Obtains the live protocol velocity, allowing it to be read from or
     * written to
     */
    public VectorAbstract velLive = new VectorAbstract() {
        public double getX() {
            return entity.vel.getX();
        }

        public double getY() {
            return entity.vel.getY();
        }

        public double getZ() {
            return entity.vel.getZ();
        }

        public VectorAbstract setX(double x) {
            entity.vel.setX(x);
            return this;
        }

        public VectorAbstract setY(double y) {
            entity.vel.setY(y);
            return this;
        }

        public VectorAbstract setZ(double z) {
            entity.vel.setZ(z);
            return this;
        }
    };
    /**
     * Obtains the protocol location as the clients know it, allowing it to be
     * read from or written to
     */
    public LocationAbstract locSynched = new LocationAbstract() {
        public World getWorld() {
            return entity.getWorld();
        }

        public LocationAbstract setWorld(World world) {
            entity.setWorld(world);
            return this;
        }

        public double getX() {
            return state.getLocX();
        }

        public double getY() {
            return state.getLocY();
        }

        public double getZ() {
            return state.getLocZ();
        }

        public LocationAbstract setX(double x) {
            state.setLocX(x);
            return this;
        }

        public LocationAbstract setY(double y) {
            state.setLocY(y);
            return this;
        }

        public LocationAbstract setZ(double z) {
            state.setLocZ(z);
            return this;
        }

        public float getYaw() {
            return state.getYaw();
        }

        public float getPitch() {
            return state.getPitch();
        }

        public LocationAbstract setYaw(float yaw) {
            state.setYaw(yaw);
            return this;
        }

        public LocationAbstract setPitch(float pitch) {
            state.setPitch(pitch);
            return this;
        }
    };
    /**
     * Obtains the protocol location as it is live, on the server. Read is
     * mainly supported, writing to it is not recommended. Although it has valid
     * setters, the loss of accuracy of the protocol values make it rather
     * pointless to use.
     */
    public LocationAbstract locLive = new LocationAbstract() {

        @Override
        public World getWorld() {
            return entity.getWorld();
        }

        @Override
        public LocationAbstract setWorld(World world) {
            entity.setWorld(world);
            return this;
        }

        public double getX() {
            return entity.loc.getX();
        }

        public double getY() {
            return entity.loc.getY();
        }

        public double getZ() {
            return entity.loc.getZ();
        }

        public LocationAbstract setX(double x) {
            entity.loc.setX(x);
            return this;
        }

        public LocationAbstract setY(double y) {
            entity.loc.setY(y);
            return this;
        }

        public LocationAbstract setZ(double z) {
            entity.loc.setZ(z);
            return this;
        }

        public float getYaw() {
            return entity.loc.getYaw();
        }

        public float getPitch() {
            return entity.loc.getPitch();
        }

        public LocationAbstract setYaw(float yaw) {
            entity.loc.setYaw(yaw);
            return this;
        }

        public LocationAbstract setPitch(float pitch) {
            entity.loc.setPitch(pitch);
            return this;
        }

    };
    /**
     * Obtains the protocol head rotation as the clients know it, allowing it to
     * be read from or written to
     */
    public FloatAbstract headRotSynched = new FloatAbstract() {
        public float get() {
        	return state.getHeadYaw();
        }

        public FloatAbstract set(float value) {
        	state.setHeadYaw(value);
            return this;
        }
    };
    /**
     * Obtains the protocol head rotation as it is live, on the server. Only
     * reading is supported.
     */
    public FloatAbstract headRotLive = new FloatAbstract() {
        public float get() {
            return entity.getHeadRotation();
        }

        public FloatAbstract set(float value) {
            throw new UnsupportedOperationException();
        }
    };
    /**
     * Obtains the tick time, this is for how long this network component/entry
     * has been running on the server. The tick time can be used to perform
     * operations on an interval. The tick time is automatically updated behind
     * the hood.
     */
    public IntegerAbstract ticks = new IntegerAbstract() {
        public int get() {
            return state.getTickCounter();
        }

        public IntegerAbstract set(int value) {
            state.setTickCounter(value);
            return this;
        }
    };

    public int getViewDistance() {
        return entry.getTrackingDistance();
    }

    public void setViewDistance(int blockDistance) {
        entry.setTrackingDistance(blockDistance);
    }

    public int getUpdateInterval() {
        return state.getUpdateInterval();
    }

    public void setUpdateInterval(int tickInterval) {
        state.setUpdateInterval(tickInterval);
    }

    public boolean isMobile() {
        return state.isMobile();
    }

    public void setMobile(boolean mobile) {
        state.setIsMobile(mobile);
    }

    /**
     * Gets the amount of ticks that have passed since the last Location
     * synchronization. A location synchronization means that an absolute
     * position update is performed.
     *
     * @return ticks since last location synchronization
     */
    public int getTicksSinceLocationSync() {
        return state.getTimeSinceLocationSync();
    }

    /**
     * Checks whether the current update interval is reached
     *
     * @return True if the update interval was reached, False if not
     */
    public boolean isUpdateTick() {
        return ticks.isMod(getUpdateInterval());
    }

    /**
     * Binds this Entity Network Controller to an Entity. This is called from
     * elsewhere, and should be ignored entirely.
     *
     * @param entity to bind with
     * @param entityTrackerEntry to bind with
     */
    public final void bind(T entity, Object entityTrackerEntry) {
        if (this.entity != null) {
            this.onDetached();

            // Bind a default entity network controller to the old entry, so that it does not continue
            // calling callbacks in this controller.
            if (this.entry != null) {
                EntityTrackerEntryHook oldHook = EntityTypingHandler.INSTANCE.getEntityTrackerEntryHook(this.entry.getRaw());
                if (oldHook != null && oldHook.getController() == this) {
                    EntityNetworkController<CommonEntity<?>> defaultController = CommonUtil.unsafeCast(new DefaultEntityNetworkController());
                    defaultController.bind(this.entity, this.entry.getRaw());
                }
            }

            this.entity = null;
            this.entry = null;
            this.state = null;
        }
        if (entity == null) {
            return;
        }
        this.entity = entity;
        this.entry = EntityTrackerEntryHandle.createHandle(entityTrackerEntry);
        this.state = this.entry.getState();

        if (!CommonCapabilities.MULTIPLE_PASSENGERS) {
            this.last_passenger_1_8_8 = entity.getPassenger();
        }

        EntityTrackerEntryHook hook = EntityTypingHandler.INSTANCE.getEntityTrackerEntryHook(this.entry.getRaw());
        if (hook != null) {
            hook.setController(this);
        }
        if (this.entity.isSpawned()) {
            this.onAttached();
        }
    }

    /**
     * Obtains the Entity Tracker Entry handle of this Network Controller
     *
     * @return entry handle
     */
    public Object getHandle() {
        return entry.getRaw();
    }

    /**
     * Gets a collection of all Players viewing this Entity
     *
     * @return viewing players
     */
    public final Collection<Player> getViewers() {
        return Collections.unmodifiableCollection(entry.getViewers());
    }

    /**
     * Adds a new viewer to this Network Controller. Calling this method also
     * results in spawn messages being sent to the viewer. When overriding, make
     * sure to always check the super-result before continuing!
     *
     * @param viewer to add
     * @return True if the viewer was added, False if the viewer was already
     * added
     */
    public boolean addViewer(Player viewer) {
        if (!entry.addViewerToSet(viewer)) {
            return false;
        }
        this.makeVisible(viewer);
        return true;
    }

    /**
     * Removes a viewer from this Network Controller. Calling this method also
     * results in destroy messages being sent to the viewer. When overriding,
     * make sure to always check the super-result before continuing!
     *
     * @param viewer to remove
     * @return True if the viewer was removed, False if the viewer wasn't
     * contained
     */
    public boolean removeViewer(Player viewer) {
        if (!entry.removeViewerFromSet(viewer)) {
            return false;
        }
        this.makeHidden(viewer);
        return true;
    }

    /**
     * Checks whether a particular viewer can see this entity
     * 
     * @param viewer
     * @return True if visible
     */
    public final boolean isViewable(Player viewer) {
        // If viewer has blindness due to respawning, do not make it visible just yet
        // When blindness runs out, perform an updateViewer again to make this entity visible quickly
        if (!CommonPlugin.getInstance().getPlayerMeta(viewer).respawnBlindnessCheck(this)) {
            return false;
        }

        return isViewable_self_or_passenger(viewer);
    }

    private boolean isViewable_self_or_passenger(Player viewer) {
        if (isViewable_self(viewer)) {
            return true;
        }
        for (Entity passenger : entity.getPassengers()) {
            EntityNetworkController<?> network = CommonEntity.get(passenger).getNetworkController();
            if (network != null && network.isViewable_self_or_passenger(viewer)) {
                return true;
            }
        }
        return false;
    }

    private boolean isViewable_self(Player viewer) {
        // Viewer is a passenger of this Entity
        for (Entity passenger : entity.getPassengers()) {
            if (viewer.equals(passenger)) {
                return true;
            }
        }
        // View range check
        final int dx = MathUtil.floor(Math.abs(EntityUtil.getLocX(viewer) - this.locSynched.getX()));
        final int dz = MathUtil.floor(Math.abs(EntityUtil.getLocZ(viewer) - this.locSynched.getZ()));
        final int view = this.getViewDistance();
        if (dx > view || dz > view) {
            return false;
        }
        // The entity is in a chunk not seen by the viewer
        if (!EntityHandle.T.isIgnoreChunkCheck.invoke(entity.getHandle())
                && !PlayerUtil.isChunkVisible(viewer, entity.getChunkX(), entity.getChunkZ())) {
            return false;
        }
        // Entity is a Player hidden from sight for the viewer?
        if (entity.getEntity() instanceof Player && !viewer.canSee((Player) entity.getEntity())) {
            return false;
        }
        // It can be seen
        return true;
    }

    /**
     * Sets whether this Entity is marked for removal during the next tick for
     * the player
     *
     * @param player to set it for
     * @param remove - True to remove, False NOT to remove
     */
    public void setRemoveNextTick(Player player, boolean remove) {
        LogicUtil.addOrRemove(PlayerUtil.getEntityRemoveQueue(player), entity.getEntityId(), remove);
    }

    /**
     * Ensures that the Entity is no longer displayed to the viewer
     *
     * @param viewer to hide this Entity for
     * @param instant option: True to instantly hide, False to queue it for the
     * next tick
     */
    public void makeHidden(Player viewer, boolean instant) {
        // If instant, do not send other destroy messages, if not, send one
        this.setRemoveNextTick(viewer, !instant);
        if (instant) {
            PacketUtil.sendPacket(viewer, PacketType.OUT_ENTITY_DESTROY.newInstanceSingle(entity.getEntityId()));
        }
    }

    /**
     * Ensures that the Entity is no longer displayed to the viewer. The entity
     * is not instantly hidden; it is queued for the next tick.
     *
     * @param viewer to hide this Entity for
     */
    public void makeHidden(Player viewer) {
        makeHidden(viewer, false);
    }

    /**
     * Ensures that the Entity is no longer displayed to any viewers. All
     * viewers will see the Entity disappear.
     *
     * @param instant option: True to instantly hide, False to queue it for the
     * next tick
     */
    public void makeHiddenForAll(boolean instant) {
        for (Player viewer : getViewers()) {
            makeHidden(viewer, instant);
        }
    }

    /**
     * Ensures that the Entity is no longer displayed to any viewers. All
     * viewers will see the Entity disappear. This method queues for the next
     * tick.
     */
    public void makeHiddenForAll() {
        for (Player viewer : getViewers()) {
            makeHidden(viewer);
        }
    }

    /**
     * Synchronizes new passenger information to a player viewer.
     * This function is only called on MC >= 1.10.2.
     * The oldPassengers list will be empty when the player sees this Entity
     * for the first time ({@link #makeVisible(Player)}).
     * 
     * @param viewer
     * @param oldPassengers known to the viewer
     * @param newPassengers known to the viewer
     */
    protected void onSyncPassengers(Player viewer, List<org.bukkit.entity.Entity> oldPassengers, List<org.bukkit.entity.Entity> newPassengers) {
        // MC >= 1.9 only
        if (PacketType.OUT_MOUNT.isValid()) {
            PacketUtil.sendPacket(viewer, PacketType.OUT_MOUNT.newInstance(entity.getEntity(), newPassengers));
        }
    }

    /**
     * Ensures that the Entity is displayed to the viewer.
     * Can be overridden to completely alter how the entity is spawned.
     *
     * @param viewer to display this Entity for
     */
    public void makeVisible(Player viewer) {
        // We just made it visible - do not try to remove it
        setRemoveNextTick(viewer, false);

        // Spawn packet
        PacketUtil.sendPacket(viewer, getSpawnPacket());

        // Meta Data
        initMetaData(viewer);

        // Velocity
        if (this.isMobile()) {
            PacketUtil.sendPacket(viewer, PacketType.OUT_ENTITY_VELOCITY.newInstance(entity.getEntityId(), this.velSynched.vector()));
        }

        // On >= MC 1.10.2 we must update the passengers of this Entity
        List<org.bukkit.entity.Entity> passengers = getSynchedPassengers();
        if (!passengers.isEmpty()) {
            onSyncPassengers(viewer, new ArrayList<org.bukkit.entity.Entity>(0), passengers);
        }

        if (EntityTrackerEntryStateHandle.T.opt_vehicle.isAvailable()) {
            // On <= MC 1.8.8 we must update the vehicle of this Entity
            org.bukkit.entity.Entity vehicle = EntityTrackerEntryStateHandle.T.opt_vehicle.get(state.getRaw());
            if (vehicle != null) {
                PacketUtil.sendPacket(viewer, PacketType.OUT_ENTITY_ATTACH.newInstanceMount(entity.getEntity(), vehicle));
            }
        }

        // Potential leash
        Entity leashHolder = entity.getLeashHolder();
        if (leashHolder != null) {
            PacketUtil.sendPacket(viewer, PacketType.OUT_ENTITY_ATTACH.newInstanceLeash(leashHolder, entity.getEntity()));
        }

        // Human entity sleeping action
        if (entity.getEntity() instanceof HumanEntity && ((HumanEntity) entity.getEntity()).isSleeping()) {
            PacketUtil.sendPacket(viewer, PacketType.OUT_BED.newInstance((HumanEntity) entity.getEntity(),
                   entity.loc.block()));
        }

        // Initial entity head rotation
        float headRot = headRotLive.get();
        if (headRot != 0.0f) {
            PacketUtil.sendPacket(viewer, getHeadRotationPacket(headRot));
        }
    }

    /**
     * Synchronizes all Entity Meta Data including Entity Attributes and other
     * specific flags. Movement and positioning information is not
     * updated.<br><br>
     * <p/>
     * This should be called when making this Entity visible to a viewer.
     *
     * @param viewer to send the meta data to
     */
    public void initMetaData(Player viewer) {
        // Meta Data
        DataWatcher metaData = entity.getMetaData();
        if (!metaData.isEmpty()) {
            PacketUtil.sendPacket(viewer, PacketType.OUT_ENTITY_METADATA.newInstance(entity.getEntityId(), metaData, true));
        }
        // Living Entity - only data
        Object entityHandle = this.entity.getHandle();
        if (EntityLivingHandle.T.isAssignableFrom(entityHandle)) {
            EntityLivingHandle living = EntityLivingHandle.createHandle(entityHandle);

            // Entity Attributes
            Collection<AttributeModifiableHandle> attributes = living.getAttributeMap().getSynchronizedAttributes();
            if (!attributes.isEmpty()) {
                PacketUtil.sendPacket(viewer, PacketType.OUT_ENTITY_UPDATE_ATTRIBUTES.newInstance(entity.getEntityId(), attributes));
            }

            // Entity Equipment
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                org.bukkit.inventory.ItemStack itemstack = living.getEquipment(slot);
                if (itemstack != null) {
                    PacketUtil.sendPacket(viewer, PacketType.OUT_ENTITY_EQUIPMENT.newInstance(entity.getEntityId(), slot, itemstack));
                }
            }

            // Entity Mob Effects
            for (MobEffectHandle effect : living.getEffects()) {
                PacketUtil.sendPacket(viewer, PacketType.OUT_ENTITY_EFFECT_ADD.newInstance(entity.getEntityId(), effect));
            }
        }
    }

    /**
     * Synchronizes everything by first destroying and then respawning this
     * Entity to all viewers
     */
    public void syncRespawn() {
        // Hide
        for (Player viewer : getViewers()) {
            this.makeHidden(viewer, true);
        }

        // Update information
        velSynched.set(velLive);
        locSynched.set(locLive);
        headRotSynched.set(headRotLive.get());

        // Spawn
        for (Player viewer : getViewers()) {
            this.makeVisible(viewer);
        }
    }

    @Override
    public void onTick() {
        if (entity.isDead()) {
            return;
        }

        //TODO: Item frame support? Meh. Not for now.
        // Vehicle
        this.syncPassengers();

        // Position / Rotation
        if (this.isUpdateTick() || entity.isPositionChanged() || entity.getDataWatcher().isChanged()) {
            entity.setPositionChanged(false);
            // Update location
            if (this.getTicksSinceLocationSync() > ABSOLUTE_UPDATE_INTERVAL) {
                this.syncLocationAbsolute();
            } else {
                this.syncLocation();
            }

            // Update velocity when position changes
            this.syncVelocity();
        }

        // Refresh/resend velocity when requested (isVelocityChanged sets this)
        if (entity.isVelocityChanged()) {
            entity.setVelocityChanged(false);
            Vector velocity = velLive.vector();
            boolean cancelled = false;
            if (entity.getEntity() instanceof Player) {
                PlayerVelocityEvent event = new PlayerVelocityEvent((Player) entity.getEntity(), velocity);
                if (CommonUtil.callEvent(event).isCancelled()) {
                    cancelled = true;
                } else if (!velocity.equals(event.getVelocity())) {
                    velocity = event.getVelocity();
                    velLive.set(velocity);
                }
            }
            // Send update packet if not cancelled
            if (!cancelled) {
                this.broadcast(getVelocityPacket(velocity.getX(), velocity.getY(), velocity.getZ()));
            }
        }

        // Meta Data
        this.syncMetaData();

        // Head rotation
        this.syncHeadRotation();
    }

    /**
     * Gets a list of passengers that have last been synchronized to the viewers.
     * On MC 1.8.8 this will return the last known passenger entity.
     * 
     * @return list of last known passengers (not modifiable)
     */
    public List<org.bukkit.entity.Entity> getSynchedPassengers() {
        if (!EntityTrackerEntryStateHandle.T.opt_passengers.isAvailable()) {
            if (this.last_passenger_1_8_8 == null) {
                return Collections.emptyList();
            } else {
                return Collections.singletonList(this.last_passenger_1_8_8);
            }
        }
        return Collections.unmodifiableList(EntityTrackerEntryStateHandle.T.opt_passengers.get(state.getRaw()));
    }

    /**
     * Checks whether there are any passenger changes pending.<br>
     * <br>
     * Checks whether passengers have changed since the last sync. On MC 1.8.8, this method
     * checks whether the vehicle of this Entity has changed instead.
     * 
     * @return True if changed, False if not
     */
    public boolean isPassengersChanged() {
        if (EntityTrackerEntryStateHandle.T.opt_passengers.isAvailable()) {
            List<Entity> old_passengers = getSynchedPassengers();
            List<Entity> new_passengers = this.entity.getPassengers();
            if (old_passengers.size() != new_passengers.size()) {
                return true;
            }
            for (int i = 0; i < old_passengers.size(); i++) {
                if (old_passengers.get(i).getEntityId() != new_passengers.get(i).getEntityId()) {
                    return true;
                }
            }
        } else if (EntityTrackerEntryStateHandle.T.opt_vehicle.isAvailable()) {
            Entity old_vehicle = EntityTrackerEntryStateHandle.T.opt_vehicle.get(state.getRaw());
            Entity new_vehicle = this.entity.getVehicle();
            if (old_vehicle != new_vehicle) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether one of the position (protocol) component differences
     * between live and synched exceed the minimum change provided. In short, it
     * checks whether the position changed.
     *
     * @param minChange to look for
     * @return True if changed, False if not
     */
    public boolean isPositionChanged(double minChange) {
        return Math.abs(locLive.getX() - locSynched.getX()) >= minChange
                || Math.abs(locLive.getY() - locSynched.getY()) >= minChange
                || Math.abs(locLive.getZ() - locSynched.getZ()) >= minChange;
    }

    /**
     * Checks whether one of the rotation (protocol) component differences
     * between live and synched exceed the minimum change provided. In short, it
     * checks whether the rotation changed.
     *
     * @param minChange to look for
     * @return True if changed, False if not
     */
    public boolean isRotationChanged(float minChange) {
        return MathUtil.getAngleDifference(locLive.getYaw(), locSynched.getYaw()) >= minChange
                || MathUtil.getAngleDifference(locLive.getPitch(), locSynched.getPitch()) >= minChange;
    }

    /**
     * Checks whether the velocity difference between live and synched exceeds
     * the minimum change provided. In short, it checks whether the velocity
     * changed.
     *
     * @param minChange to look for
     * @return True if changed, False if not
     */
    public boolean isVelocityChanged(double minChange) {
        return velLive.distanceSquared(velSynched) > (minChange * minChange);
    }

    /**
     * Checks whether the head rotation difference between live and synched
     * exceeds the minimum change provided. In short, it checks whether the head
     * rotation changed.
     *
     * @param minChange to look for
     * @return True if changed, False if not
     */
    public boolean isHeadRotationChanged(float minChange) {
        return Math.abs(headRotLive.get() - headRotSynched.get()) >= minChange;
    }

    /**
     * Synchronizes all Entity Meta Data including Entity Attributes and other
     * specific flags. Movement and positioning information is not updated. Only
     * the changes are sent, it is a relative update.
     */
    public void syncMetaData() {
        // Meta Data
        DataWatcher meta = entity.getMetaData();
        if (meta.isChanged()) {
            broadcast(PacketType.OUT_ENTITY_METADATA.newInstance(entity.getEntityId(), meta, false), true);
        }
        // Living Entity - only data
        Object entityHandle = this.entity.getHandle();
        if (EntityLivingHandle.T.isAssignableFrom(entityHandle)) {
            EntityLivingHandle living = EntityLivingHandle.createHandle(entityHandle);

            // Entity Attributes
            Collection<AttributeModifiableHandle> attributes = living.getAttributeMap().getSynchronizedAttributes();
            if (!attributes.isEmpty()) {
                this.broadcast(PacketType.OUT_ENTITY_UPDATE_ATTRIBUTES.newInstance(entity.getEntityId(), attributes), true);
            }
            attributes.clear();
        }
    }

    /**
     * Synchronizes the entity Vehicle to all viewers. Updates when the vehicle
     * changes.
     */
    public void syncPassengers() {
        if (EntityTrackerEntryStateHandle.T.opt_passengers.isAvailable()) {
            // On MC >= 1.9 we must update passengers of this Entity

            List<Entity> old_passengers = getSynchedPassengers();
            List<Entity> new_passengers = entity.getPassengers();
            boolean passengersDifferent = (old_passengers.size() != new_passengers.size());
            if (!passengersDifferent) {
                for (int i = 0; i < old_passengers.size(); i++) {
                    if (old_passengers.get(i).getEntityId() != new_passengers.get(i).getEntityId()) {
                        passengersDifferent = true;
                        break;
                    }
                }
            }
            if (passengersDifferent) {
                // Store old passengers list for later event handling
                ArrayList<org.bukkit.entity.Entity> old_passengers_bu = new ArrayList<org.bukkit.entity.Entity>(old_passengers);  

                // Update the raw List. This prevents converters being used in the final List.
                List<Object> newList = CommonUtil.unsafeCast(EntityTrackerEntryStateHandle.T.opt_passengers.raw.get(state.getRaw()));
                if (newList instanceof ArrayList) {
                    newList.clear();
                } else {
                    newList = new ArrayList<Object>(new_passengers.size());
                    EntityTrackerEntryStateHandle.T.opt_passengers.raw.set(state.getRaw(), newList);
                }
                for (Entity e : new_passengers) {
                    newList.add(HandleConversion.toEntityHandle(e));
                }

                // Send update packet for the new passengers
                for (Player viewer : this.getViewers()) {
                    onSyncPassengers(viewer, old_passengers_bu, new_passengers);
                }
            }
        } else if (EntityTrackerEntryStateHandle.T.opt_vehicle.isAvailable()) {
            // On MC <= 1.8.8 we must update the vehicle of this Entity

            Entity old_vehicle = EntityTrackerEntryStateHandle.T.opt_vehicle.get(state.getRaw());
            Entity new_vehicle = this.entity.getVehicle();
            if (old_vehicle != new_vehicle) {
                EntityTrackerEntryStateHandle.T.opt_vehicle.set(state.getRaw(), new_vehicle);
                broadcast(PacketType.OUT_ENTITY_ATTACH.newInstanceMount(this.entity.getEntity(), new_vehicle));
            }

            // Track passenger ourselves to implement onSyncPassengers functionality
            org.bukkit.entity.Entity new_entity = this.entity.getPassenger();
            if (new_entity != this.last_passenger_1_8_8) {
                ArrayList<org.bukkit.entity.Entity> old_list = new ArrayList<org.bukkit.entity.Entity>(1);
                ArrayList<org.bukkit.entity.Entity> new_list = new ArrayList<org.bukkit.entity.Entity>(1);
                if (this.last_passenger_1_8_8 != null) {
                    old_list.add(this.last_passenger_1_8_8);
                }
                if (new_entity != null) {
                    new_list.add(new_entity);
                }
                this.last_passenger_1_8_8 = new_entity;

                for (Player viewer : this.getViewers()) {
                    onSyncPassengers(viewer, old_list, new_list);
                }
            }
        }
    }

    /**
     * Synchronizes the entity head yaw rotation to all viewers.
     */
    public void syncHeadRotation() {
        if (isHeadRotationChanged(MIN_RELATIVE_ROT_CHANGE)) {
            syncHeadRotation(headRotLive.get());
        }
    }

    /**
     * Synchronizes the entity head yaw rotation to all viewers.
     *
     * @param headRotation to set to
     */
    public void syncHeadRotation(float headRotation) {
        headRotSynched.set(headRotation);
        this.broadcast(getHeadRotationPacket(headRotation));
    }

    /**
     * Synchronizes the entity velocity to all viewers. Based on a change in
     * Velocity, velocity will be updated.
     */
    public void syncVelocity() {
        if (!this.isMobile()) {
            return;
        }
        if ((velLive.lengthSquared() == 0.0 && velSynched.lengthSquared() > 0.0) || isVelocityChanged(MIN_RELATIVE_VELOCITY)) {
            this.syncVelocity(velLive.getX(), velLive.getY(), velLive.getZ());
        }
    }

    /**
     * Synchronizes the entity velocity
     *
     * @param velocity (new)
     */
    public void syncVelocity(Vector velocity) {
        syncVelocity(velocity.getX(), velocity.getY(), velocity.getZ());
    }

    /**
     * Synchronizes the entity velocity
     *
     * @param velX
     * @param velY
     * @param velZ
     */
    public void syncVelocity(double velX, double velY, double velZ) {
        velSynched.set(velX, velY, velZ);
        // If inside a vehicle, there is no use in updating
        if (entity.isInsideVehicle()) {
            return;
        }
        this.broadcast(getVelocityPacket(velX, velY, velZ));
    }

    /**
     * Synchronizes the entity location to all clients. Based on the distances,
     * relative or absolute movement is performed.
     */
    public void syncLocation() {
        syncLocation(isPositionChanged(MIN_RELATIVE_POS_CHANGE), isRotationChanged(MIN_RELATIVE_ROT_CHANGE));
    }

    /**
     * Synchronizes the entity position / rotation absolutely
     */
    public void syncLocationAbsolute() {
        syncLocationAbsolute(locLive.getX(), locLive.getY(), locLive.getZ(), locLive.getYaw(), locLive.getPitch());
    }

    /**
     * Synchronizes the entity position / rotation absolutely
     *
     * @param posX - protocol position X
     * @param posY - protocol position Y
     * @param posZ - protocol position Z
     * @param yaw - protocol rotation yaw
     * @param pitch - protocol rotation pitch
     */
    public void syncLocationAbsolute(double posX, double posY, double posZ, float yaw, float pitch) {
        // Update protocol values
        locSynched.set(posX, posY, posZ, yaw, pitch);

        // Update last synchronization time
        this.markLocationSyncedAbsolute();

        // Send synchronization messages
        broadcast(getLocationPacket(posX, posY, posZ, yaw, pitch));
    }

    /**
     * Resets the absolute update timer, marking the entity's location as synchronized absolutely.
     * After calling this method, {@link #getTicksSinceLocationSync()} will return 0.
     */
    public void markLocationSyncedAbsolute() {
        state.setTimeSinceLocationSync(0);
    }

    /**
     * Synchronizes the entity position / rotation relatively.
     *
     * @param position - whether to sync position
     * @param rotation - whether to sync rotation
     */
    public void syncLocation(boolean position, boolean rotation) {
        if (!position && !rotation) {
            return;
        }
        syncLocation(position, rotation, locLive.getX(), locLive.getY(), locLive.getZ(), locLive.getYaw(), locLive.getPitch());
    }

    /**
     * Synchronizes the entity position / rotation relatively. If the relative
     * change is too big, an absolute update is performed instead.
     *
     * @param position - whether to update position (read pos on/off)
     * @param rotation - whether to update rotation (read yawpitch on/off)
     * @param posX - protocol position X
     * @param posY - protocol position Y
     * @param posZ - protocol position Z
     * @param yaw - protocol rotation yaw
     * @param pitch - protocol rotation pitch
     */
    public void syncLocation(boolean position, boolean rotation, double posX, double posY, double posZ, float yaw, float pitch) {
        // No position updates allowed for passengers (this is FORCED). Rotation is allowed.
        if (position && !entity.isInsideVehicle()) {
            final double deltaX = posX - locSynched.getX();
            final double deltaY = posY - locSynched.getY();
            final double deltaZ = posZ - locSynched.getZ();

            // There is no use sending relative updates with zero change...
            if (deltaX == 0 && deltaY == 0 && deltaZ == 0) {
                return;
            }

            // Absolute updates for too long distances
            if (Math.abs(deltaX) >= MAX_RELATIVE_DISTANCE || Math.abs(deltaY) >= MAX_RELATIVE_DISTANCE || Math.abs(deltaZ) >= MAX_RELATIVE_DISTANCE) {
                // Distance too large, perform absolute update
                // If no rotation is being updated, set the rotation to the synched rotation
                if (!rotation) {
                    yaw = locSynched.getYaw();
                    pitch = locSynched.getPitch();
                }
                syncLocationAbsolute(posX, posY, posZ, yaw, pitch);
            } else if (rotation) {
                // Update rotation and position relatively
                locSynched.set(posX, posY, posZ, yaw, pitch);
                broadcast(PacketType.OUT_ENTITY_MOVE_LOOK.newInstance(entity.getEntityId(),
                        deltaX, deltaY, deltaZ, yaw, pitch, entity.isOnGround()));
            } else {
                // Only update position relatively
                locSynched.set(posX, posY, posZ);
                broadcast(PacketType.OUT_ENTITY_MOVE.newInstance(entity.getEntityId(),
                        deltaX, deltaY, deltaZ, entity.isOnGround()));
            }
        } else if (rotation) {
            // Only update rotation
            locSynched.setRotation(yaw, pitch);
            broadcast(PacketType.OUT_ENTITY_LOOK.newInstance(entity.getEntityId(), yaw, pitch, entity.isOnGround()));
        }
    }

    /* 
     * ================================================
     * =====  Very basic protocol-related methods =====
     * ================================================
     */
    /**
     * Sends a packet to all viewers, excluding the entity itself
     *
     * @param packet to send
     */
    public void broadcast(CommonPacket packet) {
        broadcast(packet, false);
    }

    /**
     * Sends a packet to all viewers, and if set, to itself
     *
     * @param packet to send
     * @param self option: True to send to self (if a player), False to not send
     * to self
     */
    public void broadcast(CommonPacket packet, boolean self) {
        if (self && entity.getEntity() instanceof Player) {
            PacketUtil.sendPacket((Player) entity.getEntity(), packet);
        }
        // Viewers
        for (Player viewer : this.getViewers()) {
            if (viewer != entity.getEntity()) {
                PacketUtil.sendPacket(viewer, packet);
            }
        }
    }

    /**
     * Gets a new packet with absolute Entity position information
     *
     * @param posX - position X (protocol)
     * @param posY - position Y (protocol)
     * @param posZ - position Z (protocol)
     * @param yaw - position yaw (protocol)
     * @param pitch - position pitch (protocol)
     * @return a packet with absolute position information
     */
    public CommonPacket getLocationPacket(double posX, double posY, double posZ, float yaw, float pitch) {
        return PacketType.OUT_ENTITY_TELEPORT.newInstance(entity.getEntityId(), posX, posY, posZ, yaw, pitch, true);
    }

    /**
     * Gets a new packet with velocity information for this Entity
     *
     * @param velX - velocity X
     * @param velY - velocity Y
     * @param velZ - velocity Z
     * @return a packet with velocity information
     */
    public CommonPacket getVelocityPacket(double velX, double velY, double velZ) {
        return PacketType.OUT_ENTITY_VELOCITY.newInstance(entity.getEntityId(), velX, velY, velZ);
    }

    /**
     * Creates a new spawn packet for spawning this Entity. To change the
     * spawned entity type, override this method. By default, the entity is
     * evaluated and the right packet is created automatically.
     *
     * @return spawn packet
     */
    public CommonPacket getSpawnPacket() {
        final CommonPacket packet = state.getSpawnPacket();
        if (packet != null && packet.getType() == PacketType.OUT_ENTITY_SPAWN) {
            // NMS error: They are not using the position, but the live position
            // This has some big issues when new players join...

            PacketPlayOutSpawnEntityHandle handle = PacketPlayOutSpawnEntityHandle.createHandle(packet.getHandle());

            // Motion
            handle.setMotX(velSynched.getX());
            handle.setMotY(velSynched.getY());
            handle.setMotZ(velSynched.getZ());
            // Position
            handle.setPosX(locSynched.getX());
            handle.setPosY(locSynched.getY());
            handle.setPosZ(locSynched.getZ());
            // Rotation
            handle.setYaw(locSynched.getYaw());
            handle.setPitch(locSynched.getPitch());
        }
        return packet;
    }

    /**
     * Gets a new packet with head rotation information for this Entity
     *
     * @param headRotation value (protocol value)
     * @return packet with head rotation information
     */
    public CommonPacket getHeadRotationPacket(float headRotation) {
        int prot = EntityTrackerEntryStateHandle.getProtocolRotation(headRotation);
        return PacketType.OUT_ENTITY_HEAD_ROTATION.newInstance(entity.getEntity(), (byte) prot);
    }

}
