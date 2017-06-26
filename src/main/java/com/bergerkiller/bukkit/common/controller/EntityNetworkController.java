package com.bergerkiller.bukkit.common.controller;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.bases.mutable.FloatAbstract;
import com.bergerkiller.bukkit.common.bases.mutable.IntegerAbstract;
import com.bergerkiller.bukkit.common.bases.mutable.LocationAbstract;
import com.bergerkiller.bukkit.common.bases.mutable.ObjectAbstract;
import com.bergerkiller.bukkit.common.bases.mutable.VectorAbstract;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.entity.CommonEntityController;
import com.bergerkiller.bukkit.common.internal.hooks.EntityTrackerHook;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.*;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.server.AttributeMapServerHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityLivingHandle;
import com.bergerkiller.generated.net.minecraft.server.EntityTrackerEntryHandle;
import com.bergerkiller.generated.net.minecraft.server.MobEffectHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutSpawnEntityHandle;

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

    private EntityTrackerEntryHandle handle;

    /**
     * Obtains the velocity as the clients know it, allowing it to be read from
     * or written to
     */
    public VectorAbstract velSynched = new VectorAbstract() {
        public double getX() {
            return handle.getXVel();
        }

        public double getY() {
            return handle.getYVel();
        }

        public double getZ() {
            return handle.getZVel();
        }

        public VectorAbstract setX(double x) {
            handle.setXVel(x);
            return this;
        }

        public VectorAbstract setY(double y) {
            handle.setYVel(y);
            return this;
        }

        public VectorAbstract setZ(double z) {
            handle.setZVel(z);
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
            return handle.getLocX();
        }

        public double getY() {
            return handle.getLocY();
        }

        public double getZ() {
            return handle.getLocZ();
        }

        public LocationAbstract setX(double x) {
            handle.setLocX(x);
            return this;
        }

        public LocationAbstract setY(double y) {
            handle.setLocY(y);
            return this;
        }

        public LocationAbstract setZ(double z) {
            handle.setLocZ(z);
            return this;
        }

        public float getYaw() {
            return handle.getYaw();
        }

        public float getPitch() {
            return handle.getPitch();
        }

        public LocationAbstract setYaw(float yaw) {
            handle.setYaw(yaw);
            return this;
        }

        public LocationAbstract setPitch(float pitch) {
            handle.setPitch(pitch);
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
        	return handle.getHeadYaw();
        }

        public FloatAbstract set(float value) {
        	handle.setHeadYaw(value);
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
            return handle.getTickCounter();
        }

        public IntegerAbstract set(int value) {
            handle.setTickCounter(value);
            return this;
        }
    };
    /**
     * Obtains a list of (passenger) Entities as the clients know it,
     * allowing it to be read from or written to
     */
    public ObjectAbstract<List<Entity>> passengersSynched = new ObjectAbstract<List<Entity>>() {
        public List<Entity> get() {
            return handle.getPassengers();
        }

        public ObjectAbstract<List<Entity>> set(List<Entity> value) {
            handle.setPassengers(value);
            return this;
        }
    };

    public int getViewDistance() {
        return handle.getViewDistance();
    }

    public void setViewDistance(int blockDistance) {
        handle.setViewDistance(blockDistance);
    }

    public int getUpdateInterval() {
        return handle.getUpdateInterval();
    }

    public void setUpdateInterval(int tickInterval) {
        handle.setUpdateInterval(tickInterval);
    }

    public boolean isMobile() {
        return handle.isMobile();
    }

    public void setMobile(boolean mobile) {
        handle.setIsMobile(mobile);
    }

    /**
     * Gets the amount of ticks that have passed since the last Location
     * synchronization. A location synchronization means that an absolute
     * position update is performed.
     *
     * @return ticks since last location synchronization
     */
    public int getTicksSinceLocationSync() {
        return handle.getTimeSinceLocationSync();
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
        }
        this.entity = entity;
        this.handle = EntityTrackerEntryHandle.createHandle(entityTrackerEntry);

        EntityTrackerHook hook = EntityTrackerHook.get(this.handle.getRaw(), EntityTrackerHook.class);
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
        return handle.getRaw();
    }

    /**
     * Gets a collection of all Players viewing this Entity
     *
     * @return viewing players
     */
    public final Collection<Player> getViewers() {
        return Collections.unmodifiableCollection(handle.getViewers());
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
        if (!handle.addViewerToSet(viewer)) {
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
        if (!handle.removeViewerFromSet(viewer)) {
            return false;
        }
        this.makeHidden(viewer);
        return true;
    }

    /**
     * Adds or removes a viewer based on viewer distance
     *
     * @param viewer to update
     */
    public final void updateViewer(Player viewer) {
        // Check if the viewer can see this entity, or one of this entity's passengers
        boolean viewable = isViewable(viewer);
        if (!viewable) {
            for (Entity passenger : entity.getPassengers()) {
                EntityNetworkController<?> network = CommonEntity.get(passenger).getNetworkController();
                if (network != null && network.getViewers().contains(viewer)) {
                    viewable = true;
                    break;
                }
            }
        }

        // Add or remove the viewer depending on whether this entity is viewable by the viewer
        if (viewable) {
            addViewer(viewer);
        } else {
            removeViewer(viewer);
        }
    }

    private boolean isViewable(Player viewer) {
        // View range check
        final int dx = MathUtil.floor(Math.abs(EntityUtil.getLocX(viewer) - this.locSynched.getX()));
        final int dz = MathUtil.floor(Math.abs(EntityUtil.getLocZ(viewer) - this.locSynched.getZ()));
        final int view = this.getViewDistance();
        if (dx > view || dz > view) {
            return false;
        }
        // The entity is in a chunk not seen by the viewer
        if (!EntityHandle.T.ignoreChunkCheck.getBoolean(entity.getHandle())
                && !PlayerUtil.isChunkEntered(viewer, entity.getChunkX(), entity.getChunkZ())) {
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
            PacketUtil.sendPacket(viewer, PacketType.OUT_ENTITY_DESTROY.newInstance(entity.getEntityId()));
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
     * Ensures that the Entity is displayed to the viewer
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

        // Passenger/Vehicle information
        if (entity.isInsideVehicle()) {
            Logging.LOGGER_DEBUG.warnOnce("is it required to send a separate vehicle packet?");
        }
        List<org.bukkit.entity.Entity> passengers = this.passengersSynched.get();
        if (!passengers.isEmpty()) {
            PacketUtil.sendPacket(viewer, getMountPacket(passengers));
        }

        // Potential leash
        Entity leashHolder = entity.getLeashHolder();
        if (leashHolder != null) {
            PacketUtil.sendPacket(viewer, PacketType.OUT_ENTITY_ATTACH.newInstance(leashHolder, entity.getEntity()));
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
            AttributeMapServerHandle attributeMap = living.getAttributeMap();
            Collection<?> attributes = attributeMap.attributes();
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
                PacketUtil.sendPacket(viewer, PacketType.OUT_ENTITY_EFFECT_ADD.newInstance(entity.getEntityId(), effect.getRaw()));
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

    /**
     * Called at a set interval to synchronize data to clients
     */
    public void onSync() {
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
            AttributeMapServerHandle attributeMap = living.getAttributeMap();
            Collection<?> attributes = attributeMap.attributes();;
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
        syncPassengers(entity.getPassengers());
    }

    /**
     * Checks whether passengers have changed since the last sync
     * 
     * @param passengers that are expected
     * @return True if changed, False if not
     */
    public boolean isPassengersChanged() {
        return isPassengersChanged(entity.getPassengers());
    }

    /**
     * Checks whether passengers have changed since the last sync
     * 
     * @param passengers that are expected
     * @return True if changed, False if not
     */
    public boolean isPassengersChanged(List<org.bukkit.entity.Entity> passengers) {
        List<Entity> syncPassengers = this.passengersSynched.get();
        boolean passengersDifferent = (passengers.size() != syncPassengers.size());
        if (!passengersDifferent) {
            for (int i = 0; i < syncPassengers.size() && !passengersDifferent; i++) {
                passengersDifferent = (syncPassengers.get(i).getEntityId() != passengers.get(i).getEntityId());
            }
        }
        return passengersDifferent;
    }

    /**
     * Synchronizes the entity Vehicle
     *
     * @param vehicle to synchronize, NULL for no Vehicle
     */
    public void syncPassengers(List<org.bukkit.entity.Entity> passengers) {
        if (isPassengersChanged(passengers)) {
            this.passengersSynched.set(new ArrayList<Entity>(passengers));
            broadcast(getMountPacket(passengers));
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
        handle.setTimeSinceLocationSync(0);

        // Send synchronization messages
        broadcast(getLocationPacket(posX, posY, posZ, yaw, pitch));
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
            PacketUtil.sendPacket(viewer, packet);
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
        final CommonPacket packet = handle.getSpawnPacket();
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
     * Gets a new packet with vehicle information for this Entity
     *
     * @param vehicle this Entity is now a passenger of
     * @return packet with vehicle information
     */
    public CommonPacket getMountPacket(List<Entity> passengers) {
        return PacketType.OUT_MOUNT.newInstance(entity.getEntity(), passengers);
    }

    /**
     * Gets a new packet with head rotation information for this Entity
     *
     * @param headRotation value (protocol value)
     * @return packet with head rotation information
     */
    public CommonPacket getHeadRotationPacket(float headRotation) {
        int prot = EntityTrackerEntryHandle.getProtocolRotation(headRotation);
        return PacketType.OUT_ENTITY_HEAD_ROTATION.newInstance(entity.getEntity(), (byte) prot);
    }

}
