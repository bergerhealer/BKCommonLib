package com.bergerkiller.bukkit.common.bases;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_11_R1.CraftSound;
import org.bukkit.craftbukkit.v1_11_R1.util.CraftMagicNumbers;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.bases.mutable.LocationAbstract;
import com.bergerkiller.bukkit.common.bases.mutable.VectorAbstract;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion2.DuplexConversion;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.reflection.net.minecraft.server.NMSEntity;
import com.bergerkiller.reflection.net.minecraft.server.NMSEntityTrackerEntry;

import net.minecraft.server.v1_11_R1.Entity;
import net.minecraft.server.v1_11_R1.EntityInsentient;
import net.minecraft.server.v1_11_R1.EntityPlayer;

/**
 * Extends the methods provided by the Entity Bukkit class.
 *
 * @param <T> - type of Entity
 */
public class ExtendedEntity<T extends org.bukkit.entity.Entity> {

    public final LocationAbstract loc = new LocationAbstract() {
        public World getWorld() {
            return ExtendedEntity.this.getWorld();
        }

        public LocationAbstract setWorld(World world) {
            ExtendedEntity.this.setWorld(world);
            return this;
        }

        public double getX() {
            return ExtendedEntity.this.h().locX;
        }

        public double getY() {
            return ExtendedEntity.this.h().locY;
        }

        public double getZ() {
            return ExtendedEntity.this.h().locZ;
        }

        public LocationAbstract setX(double x) {
            ExtendedEntity.this.h().locX = x;
            return this;
        }

        public LocationAbstract setY(double y) {
            ExtendedEntity.this.h().locY = y;
            return this;
        }

        public LocationAbstract setZ(double z) {
            ExtendedEntity.this.h().locZ = z;
            return this;
        }

        public float getYaw() {
            return ExtendedEntity.this.h().yaw;
        }

        public float getPitch() {
            return ExtendedEntity.this.h().pitch;
        }

        public LocationAbstract setYaw(float yaw) {
            ExtendedEntity.this.h().yaw = yaw;
            return this;
        }

        public LocationAbstract setPitch(float pitch) {
            ExtendedEntity.this.h().pitch = pitch;
            return this;
        }
    };
    public final LocationAbstract last = new LocationAbstract() {
        public World getWorld() {
            return ExtendedEntity.this.getWorld();
        }

        public LocationAbstract setWorld(World world) {
            return this;
        }

        public double getX() {
            return ExtendedEntity.this.h().lastX;
        }

        public double getY() {
            return ExtendedEntity.this.h().lastY;
        }

        public double getZ() {
            return ExtendedEntity.this.h().lastZ;
        }

        public LocationAbstract setX(double x) {
            ExtendedEntity.this.h().lastX = x;
            return this;
        }

        public LocationAbstract setY(double y) {
            ExtendedEntity.this.h().lastY = y;
            return this;
        }

        public LocationAbstract setZ(double z) {
            ExtendedEntity.this.h().lastZ = z;
            return this;
        }

        public float getYaw() {
            return ExtendedEntity.this.h().lastYaw;
        }

        public float getPitch() {
            return ExtendedEntity.this.h().lastPitch;
        }

        public LocationAbstract setYaw(float yaw) {
            ExtendedEntity.this.h().lastYaw = yaw;
            return this;
        }

        public LocationAbstract setPitch(float pitch) {
            ExtendedEntity.this.h().lastPitch = pitch;
            return this;
        }
    };
    public final VectorAbstract vel = new VectorAbstract() {
        public double getX() {
            return ExtendedEntity.this.h().motX;
        }

        public double getY() {
            return ExtendedEntity.this.h().motY;
        }

        public double getZ() {
            return ExtendedEntity.this.h().motZ;
        }

        public VectorAbstract setX(double x) {
            ExtendedEntity.this.h().motX = x;
            return this;
        }

        public VectorAbstract setY(double y) {
            ExtendedEntity.this.h().motY = y;
            return this;
        }

        public VectorAbstract setZ(double z) {
            ExtendedEntity.this.h().motZ = z;
            return this;
        }
    };

    /**
     * The minimum x/y/z velocity distance, above which the entity is considered
     * to be moving
     */
    public static final double MIN_MOVE_SPEED = 0.001;
    /**
     * The internally-stored Bukkit Entity instance
     */
    protected T entity;

    /**
     * Constructs a new Extended Entity with the initial entity specified
     *
     * @param entity to use
     */
    public ExtendedEntity(T entity) {
        setEntity(entity);
    }

    /**
     * Sets the backing Bukkit Entity
     *
     * @param entity to set to
     */
    protected void setEntity(T entity) {
        this.entity = entity;
    }

    /**
     * Gets the backing Bukkit Entity
     *
     * @return entity
     */
    public T getEntity() {
        return entity;
    }

    /**
     * Private method for access the NMS Entity handle
     *
     * @return Entity handle
     */
    private Entity h() {
        return getHandle(Entity.class);
    }

    /**
     * Gets the Entity handle
     *
     * @return the Entity handle
     */
    public Object getHandle() {
        return Conversion.toEntityHandle.convert(entity);
    }

    /**
     * Gets the Entity handle, and automatically casts it to a given type
     *
     * @param type to cast to
     * @return the NMS entity handle, cast to the given type
     */
    public <H> H getHandle(Class<H> type) {
        return CommonUtil.tryCast(getHandle(), type);
    }

    /**
     * Obtains the DataWatcher to update and keep track of Entity metadata
     *
     * @return Entity meta data watcher
     */
    public DataWatcher getMetaData() {
        return new DataWatcher(getHandle(Entity.class).getDataWatcher());
    }

    public int getChunkX() {
        return NMSEntity.chunkX.get(getHandle());
    }

    public void setChunkX(int value) {
        NMSEntity.chunkX.set(getHandle(), value);
    }

    public int getChunkY() {
        return NMSEntity.chunkY.get(getHandle());
    }

    public void setChunkY(int value) {
        NMSEntity.chunkY.set(getHandle(), value);
    }

    public int getChunkZ() {
        return NMSEntity.chunkZ.get(getHandle());
    }

    public void setChunkZ(int value) {
        NMSEntity.chunkZ.set(getHandle(), value);
    }

    /**
     * Obtains the Entity head rotation angle, or 0.0 if this Entity has no
     * head.
     *
     * @return Head rotation, if available
     */
    public float getHeadRotation() {
        return getHandle(Entity.class).getHeadRotation();
    }

    public double getMovedX() {
        return loc.getX() - last.getX();
    }

    public double getMovedY() {
        return loc.getY() - last.getY();
    }

    public double getMovedZ() {
        return loc.getZ() - last.getZ();
    }

    public boolean hasMovedHorizontally() {
        return Math.abs(this.getMovedX()) > MIN_MOVE_SPEED || Math.abs(this.getMovedZ()) > MIN_MOVE_SPEED;
    }

    public boolean hasMovedVertically() {
        return Math.abs(this.getMovedY()) > MIN_MOVE_SPEED;
    }

    public boolean hasMoved() {
        return hasMovedHorizontally() || hasMovedVertically();
    }

    public double getMovedXZDistance() {
        return MathUtil.length(getMovedX(), getMovedZ());
    }

    public double getMovedXZDistanceSquared() {
        return MathUtil.lengthSquared(getMovedX(), getMovedZ());
    }

    public double getMovedDistance() {
        return MathUtil.length(getMovedX(), getMovedY(), getMovedZ());
    }

    public double getMovedDistanceSquared() {
        return MathUtil.lengthSquared(getMovedX(), getMovedY(), getMovedZ());
    }

    public boolean isMovingHorizontally() {
        return vel.x.abs() > MIN_MOVE_SPEED || vel.z.abs() > MIN_MOVE_SPEED;
    }

    public boolean isMovingVertically() {
        return vel.y.abs() > MIN_MOVE_SPEED;
    }

    public boolean isMoving() {
        return isMovingHorizontally() || isMovingVertically();
    }

    public void setWorld(World world) {
        final Entity handle = getHandle(Entity.class);
        handle.world = CommonNMS.getNative(world);
        handle.dimension = WorldUtil.getDimension(world);
    }

    public void setDead(boolean dead) {
        getHandle(Entity.class).dead = dead;
    }

    public float getHeight() {
        return getHandle(Entity.class).length;
    }

    public void setHeight(float height) {
        getHandle(Entity.class).length = height;
    }

    public float getLength() {
        return getHandle(Entity.class).length;
    }

    public void setLength(float length) {
        getHandle(Entity.class).length = length;
    }

    public boolean isOnGround() {
        return getHandle(Entity.class).onGround;
    }

    public void setOnGround(boolean onGround) {
        getHandle(Entity.class).onGround = onGround;
    }

    public boolean isPositionChanged() {
        return NMSEntity.positionChanged.get(getHandle());
    }

    public void setPositionChanged(boolean changed) {
        NMSEntity.positionChanged.set(getHandle(), changed);
    }

    public boolean isVelocityChanged() {
        return NMSEntity.velocityChanged.get(getHandle());
    }

    public void setVelocityChanged(boolean changed) {
        NMSEntity.velocityChanged.set(getHandle(), changed);
    }

    /**
     * Gets whether this Entity is stored in a loaded chunk
     *
     * @return True if loaded, False if not
     */
    public boolean isInLoadedChunk() {
        return NMSEntity.isLoaded.get(getHandle());
    }

    /**
     * Gets whether the entity is hitting something, like an Entity or Block. If
     * this returns True, then the Entity is unable to move freely.
     *
     * @return True if movement is impaired, False if not
     */
    public boolean isMovementImpaired() {
        // Note: this variable is simply wrongly deobfuscated!
        return getHandle(Entity.class).positionChanged;
    }

    /**
     * Sets whether the entity is hitting something, and as a result can not
     * move freely
     *
     * @param impaired state to set to
     */
    public void setMovementImpaired(boolean impaired) {
        // Note: this variable is simply wrongly deobfuscated!
        getHandle(Entity.class).positionChanged = impaired;
    }

    public Random getRandom() {
        return NMSEntity.random.get(getHandle());
    }

    /**
     * Plays a sound for the Entity, but with a slightly random pitch
     *
     * @param sound to play
     * @param volume to play at
     * @param pitch (average) to play at
     */
    public void makeRandomSound(Sound sound, float volume, float pitch) {
        makeRandomSound(CraftSound.getSound(sound), volume, pitch);
    }

    /**
     * Plays a sound for the Entity, but with a slightly random pitch
     *
     * @param soundName to play
     * @param volume to play at
     * @param pitch (average) to play at
     */
    public void makeRandomSound(String soundName, float volume, float pitch) {
        final Random rand = getRandom();
        makeSound(soundName, volume, MathUtil.clamp(pitch + 0.4f * (rand.nextFloat() - rand.nextFloat()), 0.0f, 1.0f));
    }

    public void makeSound(Sound sound, float volume, float pitch) {
        makeSound(CraftSound.getSound(sound), volume, pitch);
    }

    @SuppressWarnings("unused")
	public void makeSound(String soundName, float volume, float pitch) {
        final Entity handle = getHandle(Entity.class);
        //TODO Find method again
//        handle.world.makeSound(handle, soundName, volume, pitch);
    }

    public void makeStepSound(org.bukkit.block.Block block) {
        makeStepSound(block.getX(), block.getY(), block.getZ(), block.getType());
    }

    public void makeStepSound(int blockX, int blockY, int blockZ, Material type) {
        NMSEntity.playStepSound(getHandle(), blockX, blockY, blockZ, CommonNMS.getBlock(type));
    }

    @Deprecated
    public void makeStepSound(int blockX, int blockY, int blockZ, int typeId) {
        NMSEntity.playStepSound(getHandle(), blockX, blockY, blockZ, typeId);
    }

    public List<MetadataValue> getMetadata(String arg0) {
        return entity.getMetadata(arg0);
    }

    public boolean hasMetadata(String arg0) {
        return entity.hasMetadata(arg0);
    }

    public void removeMetadata(String arg0, Plugin arg1) {
        entity.removeMetadata(arg0, arg1);
    }

    public void setMetadata(String arg0, MetadataValue arg1) {
        entity.setMetadata(arg0, arg1);
    }

    public boolean eject() {
        return entity.eject();
    }

    public int getEntityId() {
        return entity.getEntityId();
    }

    public float getFallDistance() {
        return entity.getFallDistance();
    }

    public int getFireTicks() {
        return entity.getFireTicks();
    }

    public EntityDamageEvent getLastDamageCause() {
        return entity.getLastDamageCause();
    }

    public Location getLocation() {
        return entity.getLocation();
    }

    public Location getLastLocation() {
        return last.toLocation();
    }

    public Location getLocation(Location arg0) {
        return entity.getLocation(arg0);
    }

    public int getMaxFireTicks() {
        return entity.getMaxFireTicks();
    }

    public void setFootLocation(double x, double y, double z, float yaw, float pitch) {
        getHandle(Entity.class).setPositionRotation(x, y, z, yaw, pitch);
    }

    public void setLocation(double x, double y, double z, float yaw, float pitch) {
        getHandle(Entity.class).setLocation(x, y, z, yaw, pitch);
    }

    /**
     * Sets the yaw and pitch rotation, while ensuring that there are no
     * 360-turns
     *
     * @param yaw to set to
     * @param pitch to set to
     */
    public void setRotation(float yaw, float pitch) {
        NMSEntity.setRotation(getHandle(), yaw, pitch);
    }

    public void setPosition(double x, double y, double z) {
        getHandle(Entity.class).setPosition(x, y, z);
    }

    public List<org.bukkit.entity.Entity> getNearbyEntities(double radius) {
        return this.getNearbyEntities(radius, radius, radius);
    }

    public List<org.bukkit.entity.Entity> getNearbyEntities(double radX, double radY, double radZ) {
        return WorldUtil.getNearbyEntities(this.getEntity(), radX, radY, radZ);
    }

    /**
     * Use getpassengers instead!
     */
    @Deprecated()
	public org.bukkit.entity.Entity getPassenger() {
        return entity.getPassenger();
    }

    /**
     * Use getPlayerPassengers() instead!
     */
    @Deprecated
    public Player getPlayerPassenger() {
        return CommonUtil.tryCast(getPassenger(), Player.class);
    }

    private static boolean has_passengers_field = true;

    /**
     * Retrieves a list of passenger entities
     * 
     * @return passenger entity list
     */
    public List<org.bukkit.entity.Entity> getPassengers() {
        if (has_passengers_field) {
            try {
                return entity.getPassengers();
            } catch (Throwable t) {
                has_passengers_field = false;
            }
        }
        return Arrays.asList(entity.getPassenger());
    }

    /**
     * Retrieves a list of passengers that are players
     * 
     * @return list of player passengers
     */
    public List<Player> getPlayerPassengers() {
        List<org.bukkit.entity.Entity> passengers = this.getPassengers();
        List<Player> playerPassengers = new ArrayList<Player>(passengers.size());
        for (org.bukkit.entity.Entity entity : passengers) {
            if (entity instanceof Player) {
                playerPassengers.add((Player) entity);
            }
        }
        return playerPassengers;
    }

    public boolean hasPassenger() {
        return getHandle(Entity.class).passengers != null && getHandle(Entity.class).passengers.size()>0;
    }

    public boolean hasPlayerPassenger() {
        for(Entity passenger : getHandle(Entity.class).passengers)if(passenger instanceof EntityPlayer)return true;
        return false;
    }

    /**
     * Gets whether this type of Entity is a type of Vehicle, allowing entities
     * to enter it
     *
     * @return True if this is a Vehicle, False if not
     */
    public boolean isVehicle() {
        return entity instanceof Vehicle;
    }

    public Server getServer() {
        return entity.getServer();
    }

    public int getTicksLived() {
        return entity.getTicksLived();
    }

    public EntityType getType() {
        return entity.getType();
    }

    public UUID getUniqueId() {
        return entity.getUniqueId();
    }

    public org.bukkit.entity.Entity getVehicle() {
        return entity.getVehicle();
    }

    public Vector getVelocity() {
        return entity.getVelocity();
    }

    /**
     * Checks whether this Entity is spawned in the world
     *
     * @return True if the Entity is spawned, False if not
     */
    public boolean isSpawned() {
        final Entity handle = h();
        return handle != null && handle.world != null && handle.world.entityList.contains(handle);
    }

    public World getWorld() {
        return Conversion.toWorld.convert(h().world);
    }

    public boolean isDead() {
        return entity.isDead();
    }

    public boolean isEmpty() {
        return entity.isEmpty();
    }

    public boolean isInsideVehicle() {
        return entity.isInsideVehicle();
    }

    /**
     * Gets the Entity that is holding this Entity by a leash. If this Entity
     * does not support leashing, or the Entity is not on a leash, null is
     * returned instead.
     *
     * @return Leash holder
     */
    public org.bukkit.entity.Entity getLeashHolder() {
        EntityInsentient handle = getHandle(EntityInsentient.class);
        return handle == null ? null : Conversion.toEntity.convert(handle.getLeashHolder());
    }

    public boolean isValid() {
        return entity.isValid();
    }

    /**
     * Gets whether the Entity is submerged in water (subsequently, extinguising
     * any fire). This method does not update the state, it merely reads it.
     *
     * @return True if this Entity is in water, False if not
     */
    public boolean isInWater() {
        return isInWater(false);
    }

    /**
     * Gets whether the Entity is submerged in water (subsequently, extinguising
     * any fire)
     *
     * @param update option: True to update this state by checking for water
     * blocks nearby
     * @return True if this Entity is in water, False if not
     */
    public boolean isInWater(boolean update) {
        return NMSEntity.isInWater(getHandle(), update);
    }

    /**
     * Sets whether an Entity is allowed to teleport upon entering a portal
     * right now. This state is live-updated based on whether the Entity moved
     * into/away from a portal.
     *
     * @param allow whether to allow portal entering
     */
    public void setAllowTeleportation(boolean allow) {
        EntityUtil.setAllowTeleportation(entity, allow);
    }

    /**
     * Gets whether an Entity is allowed to teleport upon entering a portal
     * right now. This state is live-updated based on whether the Entity moved
     * into/away from a portal.
     */
    public boolean getAllowTeleportation() {
        return EntityUtil.getAllowTeleportation(entity);
    }

    /**
     * Gets the entity portal enter cooldown ticks
     *
     * @return entity cooldown ticks
     */
    public int getPortalCooldown() {
        return EntityUtil.getPortalCooldown(entity);
    }

    /**
     * Sets the entity portal enter cooldown ticks
     *
     * @param cooldownTicks to set to
     */
    public void setPortalCooldown(int cooldownTicks) {
        EntityUtil.setPortalCooldown(entity, cooldownTicks);
    }

    /**
     * Gets the maximum portal cooldown ticks. This is the value applied right
     * after entering a portal.
     *
     * @return entity maximum portal cooldown ticks
     */
    public int getPortalCooldownMaximum() {
        return EntityUtil.getPortalCooldownMaximum(entity);
    }

    public boolean leaveVehicle() {
        return entity.leaveVehicle();
    }

    public void playEffect(EntityEffect arg0) {
        entity.playEffect(arg0);
    }

    public void remove() {
        entity.remove();
    }

    public void setFallDistance(float arg0) {
        entity.setFallDistance(arg0);
    }

    public void setFireTicks(int arg0) {
        entity.setFireTicks(arg0);
    }

    public void setLastDamageCause(EntityDamageEvent arg0) {
        entity.setLastDamageCause(arg0);
    }

    /**
     * Deprecated! Use add/remove passenger instead!
     */
    @Deprecated
	public boolean setPassenger(org.bukkit.entity.Entity passenger) {
        return passenger == null ? entity.eject() : entity.setPassenger(passenger);
    }

    /**
     * Checks whether a certain entity is a passenger of this entity
     * 
     * @param passenger to check
     * @return true if a passenger, false if not
     */
    public boolean isPassenger(org.bukkit.entity.Entity passenger) {
        for (org.bukkit.entity.Entity currPassenger : this.getPassengers()) {
            if (currPassenger.getUniqueId().equals(passenger.getUniqueId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a passenger to this Vehicle, while throwing possible events. If the
     * entering didn't happen, False is returned.
     *
     * @param passenger to add
     * @return True if the passenger was successfully set, False if not
     */
	public boolean addPassenger(org.bukkit.entity.Entity passenger) {
	    return entity.addPassenger(passenger);
	}

    /**
     * Removes a passenger from this Vehicle, while throwing possible events. If the
     * exiting didn't happen, False is returned.
     *
     * @param passenger to add
     * @return True if the passenger was successfully removed, False if not
     */
    public boolean removePassenger(org.bukkit.entity.Entity passenger) {
        return entity.removePassenger(passenger);
    }

    /**
     * Use setPassengersSilent instead!
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public void setPassengerSilent(org.bukkit.entity.Entity newPassenger) {
        setPassengersSilent((newPassenger == null) ? Collections.EMPTY_LIST : Arrays.asList(newPassenger));
    }

    /**
     * Sets the passengers of this Vehicle without raising any events
     *
     * @param newPassenger to set to
     */
    public void setPassengersSilent(List<org.bukkit.entity.Entity> newPassengers) {
        final Entity handle = getHandle(Entity.class);

        // Generate a difference view between the expected list of passengers, and the current
        List<Entity> removedPassengers = new ArrayList<Entity>(handle.passengers.size());
        List<org.bukkit.entity.Entity> keptPassengers = new ArrayList<org.bukkit.entity.Entity>(newPassengers.size());
        for (Entity oldPassenger : handle.passengers) {
            boolean found = false;
            for (org.bukkit.entity.Entity p : newPassengers) {
                if (oldPassenger == Conversion.toEntityHandle.convert(p)) {
                    found = true;
                    keptPassengers.add(p);
                    break;
                }
            }
            if (!found) {
                removedPassengers.add(oldPassenger);
            }
        }

        // Update network info with the removed entities
        /*
        {
            CommonPacket packet = NMSPacketTypes.OUT_MOUNT.newInstance(entity, keptPassengers);
            PacketUtil.broadcastEntityPacket(entity, packet);
        }
        */

        // Remove vehicle information and passenger information for all removed passengers
        for (Entity passenger : removedPassengers) {
            NMSEntity.vehicleField.setInternal(passenger, null);
            handle.passengers.remove(passenger);
        }

        // Add new passengers as required
        for (org.bukkit.entity.Entity p : newPassengers) {
            Entity passengerHandle = (Entity) Conversion.toEntityHandle.convert(p);
            if (!handle.passengers.contains(passengerHandle)) {
                handle.passengers.add(passengerHandle);
                NMSEntity.vehicleField.setInternal(passengerHandle, handle);

                // Send mount packet
                CommonPacket packet = PacketType.OUT_MOUNT.newInstance(entity, keptPassengers);
                PacketUtil.broadcastEntityPacket(entity, packet);
            }
        }

        CommonPacket packet = PacketType.OUT_MOUNT.newInstance(entity, DuplexConversion.entityList.convert(handle.passengers));
        PacketUtil.broadcastEntityPacket(entity, packet);

        // Synchronize entity tracker of the vehicle to make sure it does not try to synchronize a second time
        Object entry = WorldUtil.getTracker(entity.getWorld()).getEntry(entity);
        if (entry != null) {
            NMSEntityTrackerEntry.passengers.set(entry, newPassengers);
        }
    }

    /**
     * Sends a packet to all nearby players in view of this Entity
     *
     * @param packet to send
     */
    public void sendPacketNearby(CommonPacket packet) {
        WorldUtil.getTracker(getWorld()).sendPacket(entity, packet);
    }

    public void setTicksLived(int arg0) {
        entity.setTicksLived(arg0);
    }

    public void setVelocity(Vector arg0) {
        entity.setVelocity(arg0);
    }

    public void setVelocity(double motX, double motY, double motZ) {
        final Entity handle = getHandle(Entity.class);
        handle.motX = motX;
        handle.motY = motY;
        handle.motZ = motZ;
    }

    public boolean teleport(Location location) {
        return teleport(location, TeleportCause.PLUGIN);
    }

    public boolean teleport(Location location, TeleportCause cause) {
        return entity.teleport(location, cause);
    }

    public boolean teleport(org.bukkit.entity.Entity destination) {
        return teleport(destination.getLocation());
    }

    public boolean teleport(org.bukkit.entity.Entity destination, TeleportCause cause) {
        return teleport(destination.getLocation(), cause);
    }

    /**
     * Spawns an item as if dropped by this Entity
     *
     * @param material of the item to drop
     * @param amount of the material to drop
     * @param force to drop at
     * @return the dropped Item
     */
    public Item spawnItemDrop(Material material, int amount, float force) {
        return Conversion.toItem.convert(getHandle(Entity.class).a(CraftMagicNumbers.getItem(material), amount, force));
    }

    /**
     * Spawns an item as if dropped by this Entity
     *
     * @param item to drop
     * @param force to drop at
     * @return the dropped Item
     */
    public Item spawnItemDrop(org.bukkit.inventory.ItemStack item, float force) {
        return Conversion.toItem.convert(getHandle(Entity.class).a(CommonNMS.getNative(item), force));
    }

    /**
     * Gets the datawatcher used to synchronize certain entity properties with the viewing clients
     * 
     * @return datawatcher
     */
    public DataWatcher getDataWatcher() {
        return new DataWatcher(this.h().getDataWatcher());
    }

    /**
     * Creates a helper DataWatcher Item class for accessing a Metadata field for this ExtendedEntity
     * 
     * @param key for the metadata
     * @return DataWatcher Item
     */
    public <V> DataWatcher.EntityItem<V> getDataItem(DataWatcher.Key<V> key) {
        return new DataWatcher.EntityItem<V>(this, key);
    }

    /**
     * Reads an NMS field value
     * 
     * @param field to read
     * @return field value
     */
    public <V> V read(FieldAccessor<V> field) {
        return field.get(getHandle());
    }

    /**
     * Writes an NMS field value
     * 
     * @param field to write
     * @param value to write
     */
    public <V> void write(FieldAccessor<V> field, V value) {
        field.set(getHandle(), value);
    }

    @Override
    public int hashCode() {
        return entity == null ? super.hashCode() : entity.hashCode();
    }

    @Override
    public String toString() {
        return entity == null ? "null" : entity.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ExtendedEntity) {
            return ((ExtendedEntity<?>) object).entity == this.entity;
        } else {
            return false;
        }
    }
}
