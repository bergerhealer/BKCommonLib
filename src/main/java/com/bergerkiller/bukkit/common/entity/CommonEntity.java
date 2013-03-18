package com.bergerkiller.bukkit.common.entity;

import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_5_R1.CraftSound;
import org.bukkit.craftbukkit.v1_5_R1.entity.CraftEntity;

import net.minecraft.server.v1_5_R1.Chunk;
import net.minecraft.server.v1_5_R1.Entity;

import com.bergerkiller.bukkit.common.controller.EntityController;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.entity.nms.NMSEntity;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.reflection.ClassTemplate;
import com.bergerkiller.bukkit.common.reflection.classes.EntityRef;
import com.bergerkiller.bukkit.common.reflection.classes.EntityTrackerEntryRef;
import com.bergerkiller.bukkit.common.reflection.classes.WorldServerRef;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap;

/**
 * Wrapper class for additional methods Bukkit can't or doesn't provide.
 * 
 * @param <T> - type of Entity
 */
public abstract class CommonEntity<T extends org.bukkit.entity.Entity> extends CommonEntityStore<T> {
	private Entity entity;

	public CommonEntity(T base) {
		super(base);
	}

	@Override
	public void setProxyBase(T base) {
		super.setProxyBase(base);
		this.entity = CommonNMS.getNative(base);
	}

	/**
	 * Gets the Entity handle, and automatically casts it to a given type
	 * 
	 * @param type to cast to
	 * @return the NMS entity handle, cast to the given type
	 */
	public <H> H getHandle(Class<H> type) {
		return type.cast(entity);
	}

	public int getChunkX() {
		return EntityRef.chunkX.get(entity);
	}

	public void setChunkX(int value) {
		EntityRef.chunkX.set(entity, value);
	}

	public int getChunkY() {
		return EntityRef.chunkY.get(entity);
	}

	public void setChunkY(int value) {
		EntityRef.chunkY.set(entity, value);
	}

	public int getChunkZ() {
		return EntityRef.chunkZ.get(entity);
	}

	public void setChunkZ(int value) {
		EntityRef.chunkZ.set(entity, value);
	}

	public float getYaw() {
		return entity.yaw;
	}

	public void setYaw(float value) {
		entity.yaw = value;
	}

	public float getPitch() {
		return entity.pitch;
	}

	public void setPitch(float value) {
		entity.pitch = value;
	}

	/**
	 * Gets the X-coordinate of the entity location
	 * 
	 * @return entity location: X-coordinate
	 */
	public double getLocX() {
		return entity.locX;
	}

	public void setLocX(double value) {
		entity.locX = value;
	}

	/**
	 * Gets the Y-coordinate of the entity location
	 * 
	 * @return entity location: Y-coordinate
	 */
	public double getLocY() {
		return entity.locY;
	}

	public void setLocY(double value) {
		entity.locY = value;
	}

	/**
	 * Gets the Z-coordinate of the entity location
	 * 
	 * @return entity location: Z-coordinate
	 */
	public double getLocZ() {
		return entity.locZ;
	}

	public void setLocZ(double value) {
		entity.locZ = value;
	}

	/**
	 * Gets the X-coordinate of the entity location at the start of the current tick
	 * 
	 * @return entity last location: X-coordinate
	 */
	public double getLastX() {
		return entity.lastX;
	}

	public void setLastX(double value) {
		entity.lastX = value;
	}

	/**
	 * Gets the Y-coordinate of the entity location at the start of the current tick
	 * 
	 * @return entity last location: Y-coordinate
	 */
	public double getLastY() {
		return entity.lastY;
	}

	public void setLastY(double value) {
		entity.lastY = value;
	}

	/**
	 * Gets the Z-coordinate of the entity location at the start of the current tick
	 * 
	 * @return entity last location: Z-coordinate
	 */
	public double getLastZ() {
		return entity.lastZ;
	}

	public void setLastZ(double value) {
		entity.lastZ = value;
	}

	public float getLastYaw() {
		return entity.lastYaw;
	}

	public void setLastYaw(float value) {
		entity.lastYaw = value;
	}

	public float getLastPitch() {
		return entity.lastPitch;
	}

	public void setLastPitch(float value) {
		entity.lastPitch = value;
	}
	
	public double getMotX() {
		return entity.motX;
	}

	public void setMotX(double value) {
		entity.motX = value;
	}

	public double getMotY() {
		return entity.motX;
	}

	public void setMotY(double value) {
		entity.motY = value;
	}

	public double getMotZ() {
		return entity.motZ;
	}

	public void setMotZ(double value) {
		entity.motZ = value;
	}

	public void setWorld(World world) {
		entity.world = CommonNMS.getNative(world);
		entity.dimension = WorldUtil.getDimension(world);
	}

	public void setDead(boolean dead) {
		entity.dead = dead;
	}

	public float getHeight() {
		return entity.height;
	}

	public void setHeight(float height) {
		entity.height = height;
	}

	public float getLength() {
		return entity.length;
	}

	public void setLength(float length) {
		entity.length = length;
	}

	public boolean isOnGround() {
		return entity.onGround;
	}

	public void setOnGround(boolean onGround) {
		entity.onGround = onGround;
	}

	public boolean isPositionChanged() {
		return EntityRef.positionChanged.get(entity);
	}

	public void setPositionChanged(boolean changed) {
		EntityRef.positionChanged.set(entity, changed);
	}

	public boolean isVelocityChanged() {
		return EntityRef.velocityChanged.get(entity);
	}

	public void setVelocityChanged(boolean changed) {
		EntityRef.velocityChanged.set(entity, changed);
	}

	/**
	 * Gets whether the entity is hitting something, like an Entity or Block.
	 * If this returns True, then the Entity is unable to move freely.
	 * 
	 * @return True if movement is impaired, False if not
	 */
	public boolean isMovementImpaired() {
		// Note: this variable is simply wrongly deobfuscated!
		return entity.positionChanged;
	}

	/**
	 * Sets whether the entity is hitting something, and as a result can not move freely
	 * 
	 * @param impaired state to set to
	 */
	public void setMovementImpaired(boolean impaired) {
		// Note: this variable is simply wrongly deobfuscated!
		entity.positionChanged = impaired;
	}

	public Random getRandom() {
		return EntityRef.random.get(entity);
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
		entity.world.makeSound(entity, soundName, volume, pitch);
	}

	public void makeStepSound(org.bukkit.block.Block block) {
		makeStepSound(block.getX(), block.getY(), block.getZ(), block.getTypeId());
	}

	public void makeStepSound(int blockX, int blockY, int blockZ, int typeId) {
		if (CommonNMS.isValidBlockId(typeId)) {
			EntityRef.playStepSound(entity, blockX, blockY, blockZ, typeId);
		}
	}

	private boolean isNMS() {
		return entity instanceof NMSEntity;
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
			return (EntityController<CommonEntity<T>>) ((NMSEntity) entity).getController();
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
				throw new UnsupportedOperationException("Entity of type '" + base.getClass().getName() + "' has no Controller support!");
			}
			try {
				ClassTemplate<?> TEMPLATE = ClassTemplate.create(type.getSuperclass());
				// Store the previous entity information for later use
				final Entity oldInstance = entity;
				final org.bukkit.entity.Entity oldBukkitEntity = Conversion.toEntity.convert(entity);

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
				Object entry = WorldUtil.getTracker(getWorld()).getEntry(base);
				if (entry != null) {
					EntityTrackerEntryRef.tracker.set(entry, base);
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
