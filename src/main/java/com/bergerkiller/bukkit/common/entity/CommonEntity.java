package com.bergerkiller.bukkit.common.entity;

import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.UUID;

import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_5_R1.CraftSound;
import org.bukkit.craftbukkit.v1_5_R1.entity.CraftEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import net.minecraft.server.v1_5_R1.Chunk;
import net.minecraft.server.v1_5_R1.Entity;
import net.minecraft.server.v1_5_R1.EntityPlayer;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.controller.EntityController;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.entity.nms.NMSEntity;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.classes.EntityRef;
import com.bergerkiller.bukkit.common.reflection.classes.EntityTrackerEntryRef;
import com.bergerkiller.bukkit.common.reflection.classes.WorldServerRef;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap;

/**
 * Wrapper class for additional methods Bukkit can't or doesn't provide.
 * 
 * @param <T> - type of Entity
 */
public abstract class CommonEntity<T extends org.bukkit.entity.Entity> {
	private Entity handle;
	protected T entity;

	public CommonEntity(T base) {
		setEntity(base);
	}

	/**
	 * Sets the backing Bukkit Entity
	 * 
	 * @param entity to set to
	 */
	protected void setEntity(T entity) {
		this.entity = entity;
		this.handle = CommonNMS.getNative(entity);
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
		return handle;
	}

	/**
	 * Gets the Entity handle, and automatically casts it to a given type
	 * 
	 * @param type to cast to
	 * @return the NMS entity handle, cast to the given type
	 */
	public <H> H getHandle(Class<H> type) {
		return type.cast(handle);
	}

	public int getChunkX() {
		return EntityRef.chunkX.get(handle);
	}

	public void setChunkX(int value) {
		EntityRef.chunkX.set(handle, value);
	}

	public int getChunkY() {
		return EntityRef.chunkY.get(handle);
	}

	public void setChunkY(int value) {
		EntityRef.chunkY.set(handle, value);
	}

	public int getChunkZ() {
		return EntityRef.chunkZ.get(handle);
	}

	public void setChunkZ(int value) {
		EntityRef.chunkZ.set(handle, value);
	}

	public float getYaw() {
		return handle.yaw;
	}

	public void setYaw(float value) {
		handle.yaw = value;
	}

	public float getPitch() {
		return handle.pitch;
	}

	public void setPitch(float value) {
		handle.pitch = value;
	}

	/**
	 * Gets the X-coordinate of the entity location
	 * 
	 * @return entity location: X-coordinate
	 */
	public double getLocX() {
		return handle.locX;
	}

	public void setLocX(double value) {
		handle.locX = value;
	}

	/**
	 * Gets the Y-coordinate of the entity location
	 * 
	 * @return entity location: Y-coordinate
	 */
	public double getLocY() {
		return handle.locY;
	}

	public void setLocY(double value) {
		handle.locY = value;
	}

	/**
	 * Gets the Z-coordinate of the entity location
	 * 
	 * @return entity location: Z-coordinate
	 */
	public double getLocZ() {
		return handle.locZ;
	}

	public void setLocZ(double value) {
		handle.locZ = value;
	}

	/**
	 * Gets the X-coordinate of the entity location at the start of the current tick
	 * 
	 * @return entity last location: X-coordinate
	 */
	public double getLastX() {
		return handle.lastX;
	}

	public void setLastX(double value) {
		handle.lastX = value;
	}

	/**
	 * Gets the Y-coordinate of the entity location at the start of the current tick
	 * 
	 * @return entity last location: Y-coordinate
	 */
	public double getLastY() {
		return handle.lastY;
	}

	public void setLastY(double value) {
		handle.lastY = value;
	}

	/**
	 * Gets the Z-coordinate of the entity location at the start of the current tick
	 * 
	 * @return entity last location: Z-coordinate
	 */
	public double getLastZ() {
		return handle.lastZ;
	}

	public void setLastZ(double value) {
		handle.lastZ = value;
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
		return handle.lastYaw;
	}

	public void setLastYaw(float value) {
		handle.lastYaw = value;
	}

	public float getLastPitch() {
		return handle.lastPitch;
	}

	public void setLastPitch(float value) {
		handle.lastPitch = value;
	}
	
	public double getMotX() {
		return handle.motX;
	}

	public void setMotX(double value) {
		handle.motX = value;
	}

	public double getMotY() {
		return handle.motX;
	}

	public void setMotY(double value) {
		handle.motY = value;
	}

	public double getMotZ() {
		return handle.motZ;
	}

	public void setMotZ(double value) {
		handle.motZ = value;
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
		return MathUtil.floor(getLocX());
	}

	public int getLocBlockZ() {
		return MathUtil.floor(getLocZ());
	}

	public IntVector3 getLocBlockPos() {
		return new IntVector3(getLocBlockX(), getLocBlockY(), getLocBlockZ());
	}

	public void setWorld(World world) {
		handle.world = CommonNMS.getNative(world);
		handle.dimension = WorldUtil.getDimension(world);
	}

	public void setDead(boolean dead) {
		handle.dead = dead;
	}

	public float getHeight() {
		return handle.height;
	}

	public void setHeight(float height) {
		handle.height = height;
	}

	public float getLength() {
		return handle.length;
	}

	public void setLength(float length) {
		handle.length = length;
	}

	public boolean isOnGround() {
		return handle.onGround;
	}

	public void setOnGround(boolean onGround) {
		handle.onGround = onGround;
	}

	public boolean isPositionChanged() {
		return EntityRef.positionChanged.get(handle);
	}

	public void setPositionChanged(boolean changed) {
		EntityRef.positionChanged.set(handle, changed);
	}

	public boolean isVelocityChanged() {
		return EntityRef.velocityChanged.get(handle);
	}

	public void setVelocityChanged(boolean changed) {
		EntityRef.velocityChanged.set(handle, changed);
	}

	/**
	 * Gets whether the entity is hitting something, like an Entity or Block.
	 * If this returns True, then the Entity is unable to move freely.
	 * 
	 * @return True if movement is impaired, False if not
	 */
	public boolean isMovementImpaired() {
		// Note: this variable is simply wrongly deobfuscated!
		return handle.positionChanged;
	}

	/**
	 * Sets whether the entity is hitting something, and as a result can not move freely
	 * 
	 * @param impaired state to set to
	 */
	public void setMovementImpaired(boolean impaired) {
		// Note: this variable is simply wrongly deobfuscated!
		handle.positionChanged = impaired;
	}

	public Random getRandom() {
		return EntityRef.random.get(handle);
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
		handle.world.makeSound(handle, soundName, volume, pitch);
	}

	public void makeStepSound(org.bukkit.block.Block block) {
		makeStepSound(block.getX(), block.getY(), block.getZ(), block.getTypeId());
	}

	public void makeStepSound(int blockX, int blockY, int blockZ, int typeId) {
		if (CommonNMS.isValidBlockId(typeId)) {
			EntityRef.playStepSound(handle, blockX, blockY, blockZ, typeId);
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

	public Location getLocation(Location arg0) {
		return entity.getLocation(arg0);
	}

	public int getMaxFireTicks() {
		return entity.getMaxFireTicks();
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
		return handle.passenger != null;
	}

	public boolean hasPlayerPassenger() {
		return handle.passenger instanceof EntityPlayer;
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
		return CommonNMS.getItem(handle.a(material.getId(), amount, force));
	}

	/**
	 * Spawns an item as if dropped by this Entity
	 * 
	 * @param item to drop
	 * @param force to drop at
	 * @return the dropped Item
	 */
	public Item spawnItemDrop(org.bukkit.inventory.ItemStack item, float force) {
		return CommonNMS.getItem(handle.a(CommonNMS.getNative(item), force));
	}

	private boolean isNMS() {
		return handle instanceof NMSEntity;
	}

	/**
	 * Called when the Entity Handle is not a BKC 'NMS' type.
	 * The purpose of this Method is to return a valid NMS handle type for this CommonEntity.<br><br>
	 * 
	 * To indicate a lack of support, let this Method return Null.
	 * 
	 * @return NMS type
	 */
	protected abstract Class<? extends NMSEntity> getNMSType();

	/**
	 * Called after the internal handle Entity has been replaced with a BKC 'NMS' type.
	 * Any special replacement logic should be performed here
	 */
	protected void onNMSReplaced() {
	}

	/**
	 * Gets the Entity Controller currently assigned to this Entity.
	 * If none is set, this method returns Null.
	 * 
	 * @return Entity controller, or null if not set
	 */
	@SuppressWarnings("unchecked")
	public EntityController<CommonEntity<T>> getController() {
		if (isNMS()) {
			return (EntityController<CommonEntity<T>>) ((NMSEntity) handle).getController();
		} else {
			return null;
		}
	}

	/**
	 * Sets an Entity Controller for this Entity.
	 * This method throws an Exception if this kind of Entity is not supported.
	 * 
	 * @param controller to set to
	 */
	@SuppressWarnings("unchecked")
	public void setController(EntityController<CommonEntity<T>> controller) {
		if (!isNMS()) {
			// No controller is requested - no need to do anything
			if (controller == null) {
				return;
			}
			// Respawn the entity and attach the controller
			Class<? extends NMSEntity> type = this.getNMSType();
			if (type == null) {
				throw new UnsupportedOperationException("Entity of type '" + entity.getClass().getName() + "' has no Controller support!");
			}
			try {
				ClassTemplate<?> TEMPLATE = ClassTemplate.create(type.getSuperclass());
				// Store the previous entity information for later use
				final Entity oldInstance = handle;
				final org.bukkit.entity.Entity oldBukkitEntity = Conversion.toEntity.convert(handle);

				// Create a new entity instance and perform data/property transfer
				final Entity newInstance = (Entity) type.newInstance();
				TEMPLATE.transfer(oldInstance, newInstance);
				oldInstance.dead = true;
				newInstance.dead = false;

				// Now proceed to replace this NMS Entity in all places imaginable.
				// First load the chunk so we can at least work on something
				Chunk chunk = CommonNMS.getNative(getWorld().getChunkAt(getChunkX(), getChunkZ()));

				// *** Bukkit Entity ***
				((CraftEntity) oldBukkitEntity).setHandle(newInstance);

				// *** Passenger/Vehicle ***
				if (oldInstance.vehicle != null) {
					oldInstance.vehicle.passenger = newInstance;
				}
				if (oldInstance.passenger != null) {
					oldInstance.passenger.vehicle = newInstance;
				}

				// *** Entities By ID Map ***
				final IntHashMap<Object> entitiesById = WorldServerRef.entitiesById.get(oldInstance.world);
				if (entitiesById.remove(oldInstance.id) == null) {
					CommonUtil.nextTick(new Runnable() {
						public void run() {
							entitiesById.put(newInstance.id, newInstance);
						}
					});
				} else {
					entitiesById.put(newInstance.id, newInstance);
				}

				// *** EntityTrackerEntry ***
				Object entry = WorldUtil.getTracker(getWorld()).getEntry(entity);
				if (entry != null) {
					EntityTrackerEntryRef.tracker.set(entry, entity);
				}

				// *** World ***
				ListIterator<Entity> iter = oldInstance.world.entityList.listIterator();
				while (iter.hasNext()) {
					if (iter.next().id == oldInstance.id) {
						iter.set(newInstance);
						break;
					}
				}

				// *** Chunk ***
				final int chunkY = getChunkY();
				if (!replaceInChunk(chunk, chunkY, oldInstance, newInstance)) {
					for (int y = 0; y < chunk.entitySlices.length; y++) {
						if (y != chunkY && replaceInChunk(chunk, y, oldInstance, newInstance)) {
							break;
						}
					}
				}

				// End with an Event
				this.onNMSReplaced();
			} catch (Throwable t) {
				throw new RuntimeException("Failed to set controller:", t);
			}
		}
		// Set the controller
		if (controller == null) {
			// Detach
			getController().onDetached();
		} else {
			// Attach
			controller.onAttached(this);
		}
	}

	@SuppressWarnings({"unchecked"})
	private static boolean replaceInChunk(Chunk chunk, int chunkY, Entity toreplace, Entity with) {
		List<Entity> list = chunk.entitySlices[chunkY];
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).id == toreplace.id) {
				list.set(i, with);
				//set invalid
				chunk.m = true;
				return true;
			}
		}
		return false;
	}
}
