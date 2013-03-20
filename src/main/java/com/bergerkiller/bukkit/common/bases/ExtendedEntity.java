package com.bergerkiller.bukkit.common.bases;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.minecraft.server.v1_5_R1.Entity;
import net.minecraft.server.v1_5_R1.EntityPlayer;

import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_5_R1.CraftSound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.reflection.classes.EntityRef;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;

/**
 * Extends the methods provided by the Entity Bukkit class.
 * 
 * @param <T> - type of Entity
 */
public class ExtendedEntity<T extends org.bukkit.entity.Entity> {
	/**
	 * The minimum x/y/z velocity distance, above which the entity is considered to be moving
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
	 * Gets the Entity handle
	 * 
	 * @return the Entity handle
	 */
	public Object getHandle() {
		return CommonNMS.getNative(entity);
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
		return EntityRef.chunkX.get(getHandle());
	}

	public void setChunkX(int value) {
		EntityRef.chunkX.set(getHandle(), value);
	}

	public int getChunkY() {
		return EntityRef.chunkY.get(getHandle());
	}

	public void setChunkY(int value) {
		EntityRef.chunkY.set(getHandle(), value);
	}

	public int getChunkZ() {
		return EntityRef.chunkZ.get(getHandle());
	}

	public void setChunkZ(int value) {
		EntityRef.chunkZ.set(getHandle(), value);
	}

	public float getYaw() {
		return getHandle(Entity.class).yaw;
	}

	public void setYaw(float value) {
		getHandle(Entity.class).yaw = value;
	}

	public float getPitch() {
		return getHandle(Entity.class).pitch;
	}

	public void setPitch(float value) {
		getHandle(Entity.class).pitch = value;
	}

	public float getYawDifference(float yawcomparer) {
		return MathUtil.getAngleDifference(this.getYaw(), yawcomparer);
	}

	public float getPitchDifference(float pitchcomparer) {
		return MathUtil.getAngleDifference(this.getPitch(), pitchcomparer);
	}

	public float getYawDifference(CommonEntity<?> comparer) {
		return getYawDifference(comparer.getPitch());
	}

	public float getPitchDifference(CommonEntity<?> comparer) {
		return getPitchDifference(comparer.getPitch());
	}

	/**
	 * Obtains the Entity head rotation angle, or 0.0 if this Entity has no head.
	 * 
	 * @return Head rotation, if available
	 */
	public float getHeadRotation() {
		return getHandle(Entity.class).ao();
	}

	/**
	 * Gets the X-coordinate of the entity location
	 * 
	 * @return entity location: X-coordinate
	 */
	public double getLocX() {
		return getHandle(Entity.class).locX;
	}

	public void setLocX(double value) {
		getHandle(Entity.class).locX = value;
	}

	/**
	 * Gets the Y-coordinate of the entity location
	 * 
	 * @return entity location: Y-coordinate
	 */
	public double getLocY() {
		return getHandle(Entity.class).locY;
	}

	public void setLocY(double value) {
		getHandle(Entity.class).locY = value;
	}

	/**
	 * Gets the Z-coordinate of the entity location
	 * 
	 * @return entity location: Z-coordinate
	 */
	public double getLocZ() {
		return getHandle(Entity.class).locZ;
	}

	public void setLocZ(double value) {
		getHandle(Entity.class).locZ = value;
	}

	/**
	 * Gets the X-coordinate of the entity location at the start of the current tick
	 * 
	 * @return entity last location: X-coordinate
	 */
	public double getLastX() {
		return getHandle(Entity.class).lastX;
	}

	public void setLastX(double value) {
		getHandle(Entity.class).lastX = value;
	}

	/**
	 * Gets the Y-coordinate of the entity location at the start of the current tick
	 * 
	 * @return entity last location: Y-coordinate
	 */
	public double getLastY() {
		return getHandle(Entity.class).lastY;
	}

	public void setLastY(double value) {
		getHandle(Entity.class).lastY = value;
	}

	/**
	 * Gets the Z-coordinate of the entity location at the start of the current tick
	 * 
	 * @return entity last location: Z-coordinate
	 */
	public double getLastZ() {
		return getHandle(Entity.class).lastZ;
	}

	public void setLastZ(double value) {
		getHandle(Entity.class).lastZ = value;
	}

	public double getMovedX() {
		return getLocX() - getLastX();
	}

	public double getMovedY() {
		return getLocY() - getLastY();
	}

	public double getMovedZ() {
		return getLocZ() - getLastZ();
	}

 	public boolean hasMovedHorizontally() {
 		return Math.abs(this.getMovedX()) > MIN_MOVE_SPEED || Math.abs(this.getMovedZ()) > MIN_MOVE_SPEED;
 	}
 
 	public boolean hasMovedVertically() {
 		return Math.abs(this.getMovedX()) > MIN_MOVE_SPEED;
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

	public double distanceTo(double x, double y, double z) {
		return MathUtil.distance(this.getLocX(), this.getLocY(), this.getLocZ(), x, y, z);
	}

	public double distanceXZTo(double x, double z) {
		return MathUtil.distance(getLocX(), getLocZ(), x, z);
	}

	public double distanceSquaredTo(double x, double y, double z) {
		return MathUtil.distanceSquared(this.getLocX(), this.getLocY(), this.getLocZ(), x, y, z);
	}

	public double distanceXZSquaredTo(double x, double z) {
		return MathUtil.distanceSquared(getLocX(), getLocZ(), x, z);
	}

	public double distanceXZTo(CommonEntity<?> entity) {
		return distanceXZTo(entity.getEntity());
	}

	public double distanceXZTo(org.bukkit.entity.Entity e) {
		return distanceXZTo(EntityUtil.getLocX(e), EntityUtil.getLocZ(e));
	}

	public double distanceXZTo(Location l) {
		return distanceXZTo(l.getX(), l.getZ());
	}

	public double distanceXZTo(Block b) {
		return distanceXZTo(b.getX() + 0.5, b.getZ() + 0.5);
	}

	public double distanceTo(CommonEntity<?> entity) {
		return distanceTo(entity.getEntity());
	}

	public double distanceTo(org.bukkit.entity.Entity e) {
		return distanceTo(EntityUtil.getLocX(e), EntityUtil.getLocY(e), EntityUtil.getLocZ(e));
	}

	public double distanceTo(Location l) {
		return distanceTo(l.getX(), l.getY(), l.getZ());
	}

	public double distanceTo(Block b) {
		return distanceTo(b.getX() + 0.5, b.getY() + 0.5, b.getZ() + 0.5);
	}

	public double distanceXZSquaredTo(CommonEntity<?> entity) {
		return distanceXZSquaredTo(entity.getEntity());
	}

	public double distanceXZSquaredTo(org.bukkit.entity.Entity e) {
		return distanceXZSquaredTo(EntityUtil.getLocX(e), EntityUtil.getLocZ(e));
	}

	public double distanceXZSquaredTo(Location l) {
		return distanceXZSquaredTo(l.getX(), l.getZ());
	}

	public double distanceXZSquaredTo(Block b) {
		return distanceXZSquaredTo(b.getX() + 0.5, b.getZ() + 0.5);
	}

	public double distanceSquaredTo(CommonEntity<?> entity) {
		return distanceSquaredTo(entity.getEntity());
	}

	public double distanceSquaredTo(org.bukkit.entity.Entity e) {
		return distanceSquaredTo(EntityUtil.getLocX(e), EntityUtil.getLocY(e), EntityUtil.getLocZ(e));
	}

	public double distanceSquaredTo(Location l) {
		return distanceSquaredTo(l.getX(), l.getY(), l.getZ());
	}

	public double distanceSquaredTo(Block b) {
		return distanceSquaredTo(b.getX() + 0.5, b.getY() + 0.5, b.getZ() + 0.5);
	}

	public Vector locOffsetTo(double x, double y, double z) {
		return new Vector(x - getLocX(), y - getLocY(), z - getLocZ());
	}

	public Vector locOffsetTo(Location l) {
		return locOffsetTo(l.getX(), l.getY(), l.getZ());
	}

	public Vector locOffsetTo(CommonEntity<?> entity) {
		return locOffsetTo(entity.getEntity());
	}

	public Vector locOffsetTo(org.bukkit.entity.Entity e) {
		return locOffsetTo(EntityUtil.getLocX(e), EntityUtil.getLocY(e), EntityUtil.getLocZ(e));
	}

	public float getLastYaw() {
		return getHandle(Entity.class).lastYaw;
	}

	public void setLastYaw(float value) {
		getHandle(Entity.class).lastYaw = value;
	}

	public float getLastPitch() {
		return getHandle(Entity.class).lastPitch;
	}

	public void setLastPitch(float value) {
		getHandle(Entity.class).lastPitch = value;
	}

	public double getMotX() {
		return getHandle(Entity.class).motX;
	}

	public void setMotX(double value) {
		getHandle(Entity.class).motX = value;
	}

	public void addMotX(double value) {
		getHandle(Entity.class).motX += value;
	}

	public double getMotY() {
		return getHandle(Entity.class).motY;
	}

	public void setMotY(double value) {
		getHandle(Entity.class).motY = value;
	}

	public void addMotY(double value) {
		getHandle(Entity.class).motY += value;
	}

	public double getMotZ() {
		return getHandle(Entity.class).motZ;
	}

	public void setMotZ(double value) {
		getHandle(Entity.class).motZ = value;
	}

	public void addMotZ(double value) {
		getHandle(Entity.class).motZ += value;
	}

	public void multiplyVelocity(Vector factor) {
		multiplyVelocity(factor.getX(), factor.getY(), factor.getZ());
	}

	public void multiplyVelocity(double factor) {
		multiplyVelocity(factor, factor, factor);
	}

	public void multiplyVelocity(double factX, double factY, double factZ) {
		final Entity handle = getHandle(Entity.class);
		handle.motX *= factX;
		handle.motY *= factY;
		handle.motZ *= factZ;
	}

	public boolean isMovingHorizontally() {
		return Math.abs(getMotX()) > MIN_MOVE_SPEED || Math.abs(getMotZ()) > MIN_MOVE_SPEED;
	}

	public boolean isMovingVertically() {
		return Math.abs(getMotY()) > MIN_MOVE_SPEED;
	}

	public boolean isMoving() {
		return isMovingHorizontally() || isMovingVertically();
	}
	
	public int getLocChunkX() {
		return MathUtil.toChunk(getLocX());
	}

	public int getLocChunkY() {
		return MathUtil.toChunk(getLocY());
	}

	public int getLocChunkZ() {
		return MathUtil.toChunk(getLocZ());
	}

	public int getLocBlockX() {
		return MathUtil.floor(getLocX());
	}

	public int getLocBlockY() {
		return MathUtil.floor(getLocY());
	}

	public int getLocBlockZ() {
		return MathUtil.floor(getLocZ());
	}

	public IntVector3 getLocBlockPos() {
		return new IntVector3(getLocBlockX(), getLocBlockY(), getLocBlockZ());
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
		return getHandle(Entity.class).height;
	}

	public void setHeight(float height) {
		getHandle(Entity.class).height = height;
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
		return EntityRef.positionChanged.get(getHandle());
	}

	public void setPositionChanged(boolean changed) {
		EntityRef.positionChanged.set(getHandle(), changed);
	}

	public boolean isVelocityChanged() {
		return EntityRef.velocityChanged.get(getHandle());
	}

	public void setVelocityChanged(boolean changed) {
		EntityRef.velocityChanged.set(getHandle(), changed);
	}

	/**
	 * Gets whether the entity is hitting something, like an Entity or Block.
	 * If this returns True, then the Entity is unable to move freely.
	 * 
	 * @return True if movement is impaired, False if not
	 */
	public boolean isMovementImpaired() {
		// Note: this variable is simply wrongly deobfuscated!
		return getHandle(Entity.class).positionChanged;
	}

	/**
	 * Sets whether the entity is hitting something, and as a result can not move freely
	 * 
	 * @param impaired state to set to
	 */
	public void setMovementImpaired(boolean impaired) {
		// Note: this variable is simply wrongly deobfuscated!
		getHandle(Entity.class).positionChanged = impaired;
	}

	public Random getRandom() {
		return EntityRef.random.get(getHandle());
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

	public void makeSound(String soundName, float volume, float pitch) {
		final Entity handle = getHandle(Entity.class);
		handle.world.makeSound(handle, soundName, volume, pitch);
	}

	public void makeStepSound(org.bukkit.block.Block block) {
		makeStepSound(block.getX(), block.getY(), block.getZ(), block.getTypeId());
	}

	public void makeStepSound(int blockX, int blockY, int blockZ, int typeId) {
		if (CommonNMS.isValidBlockId(typeId)) {
			EntityRef.playStepSound(getHandle(), blockX, blockY, blockZ, typeId);
		}
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
		return new Location(getWorld(), getLastX(), getLastY(), getLastZ(), getLastYaw(), getLastPitch());
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

	public void setRotation(float yaw, float pitch) {
		EntityRef.setRotation(getHandle(), yaw, pitch);
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

	public org.bukkit.entity.Entity getPassenger() {
		return entity.getPassenger();
	}

	public Player getPlayerPassenger() {
		return CommonUtil.tryCast(getPassenger(), Player.class);
	}

	public boolean hasPassenger() {
		return getHandle(Entity.class).passenger != null;
	}

	public boolean hasPlayerPassenger() {
		return getHandle(Entity.class).passenger instanceof EntityPlayer;
	}

	/**
	 * Gets whether this type of Entity is a type of Vehicle, allowing entities to enter it
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

	public double getVelLength() {
		return MathUtil.length(getMotX(), getMotY(), getMotZ());
	}

	public double getVelXZLength() {
		return MathUtil.length(getMotX(), getMotZ());
	}

	public double getVelLengthSquared() {
		return MathUtil.lengthSquared(getMotX(), getMotY(), getMotZ());
	}

	public double getVelXZLengthSquared() {
		return MathUtil.lengthSquared(getMotX(), getMotZ());
	}

	public World getWorld() {
		return entity.getWorld();
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

	public boolean isValid() {
		return entity.isValid();
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

	public boolean setPassenger(org.bukkit.entity.Entity arg0) {
		return entity.setPassenger(arg0);
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

	public boolean teleport(Location arg0) {
		return entity.teleport(arg0);
	}

	public boolean teleport(org.bukkit.entity.Entity arg0) {
		return entity.teleport(arg0);
	}

	public boolean teleport(Location arg0, TeleportCause arg1) {
		return entity.teleport(arg0, arg1);
	}

	public boolean teleport(org.bukkit.entity.Entity arg0, TeleportCause arg1) {
		return entity.teleport(arg0, arg1);
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
		return CommonNMS.getItem(getHandle(Entity.class).a(material.getId(), amount, force));
	}

	/**
	 * Spawns an item as if dropped by this Entity
	 * 
	 * @param item to drop
	 * @param force to drop at
	 * @return the dropped Item
	 */
	public Item spawnItemDrop(org.bukkit.inventory.ItemStack item, float force) {
		return CommonNMS.getItem(getHandle(Entity.class).a(CommonNMS.getNative(item), force));
	}

}
