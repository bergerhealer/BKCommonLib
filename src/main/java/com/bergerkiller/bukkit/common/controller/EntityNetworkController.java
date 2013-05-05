package com.bergerkiller.bukkit.common.controller;

import java.util.Collection;
import java.util.Collections;

import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import net.minecraft.server.v1_5_R3.Entity;
import net.minecraft.server.v1_5_R3.EntityTrackerEntry;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.bases.mutable.IntLocationAbstract;
import com.bergerkiller.bukkit.common.bases.mutable.VectorAbstract;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.entity.CommonEntityController;
import com.bergerkiller.bukkit.common.entity.nms.NMSEntityTrackerEntry;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketFields;
import com.bergerkiller.bukkit.common.reflection.classes.EntityRef;
import com.bergerkiller.bukkit.common.reflection.classes.EntityTrackerEntryRef;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.utils.PlayerUtil;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;

/**
 * A controller that deals with the server to client network synchronization.
 * 
 * @param <T> - type of Common Entity this controller is for
 */
public abstract class EntityNetworkController<T extends CommonEntity<?>> extends CommonEntityController<T> {
	/**
	 * The maximum allowed distance per relative movement update
	 */
	public static final int MAX_RELATIVE_DISTANCE = 127;
	/**
	 * The minimum value change that is able to trigger an update
	 */
	public static final int MIN_RELATIVE_CHANGE = 4;
	/**
	 * The minimum velocity change that is able to trigger an update
	 */
	public static final double MIN_RELATIVE_VELOCITY = 0.02;
	/**
	 * The minimum velocity change that is able to trigger an update (squared)
	 */
	public static final double MIN_RELATIVE_VELOCITY_SQUARED = MIN_RELATIVE_VELOCITY * MIN_RELATIVE_VELOCITY;
	/**
	 * The tick interval at which the entity is updated absolutely
	 */
	public static final int ABSOLUTE_UPDATE_INTERVAL = 400;

	private Object handle;

	/**
	 * Obtains the velocity as the clients know it, allowing it to be read from or written to
	 */
	public final VectorAbstract velSynched = new VectorAbstract() {
		public double getX() {return ((EntityTrackerEntry) handle).j;}
		public double getY() {return ((EntityTrackerEntry) handle).k;}
		public double getZ() {return ((EntityTrackerEntry) handle).l;}
		public VectorAbstract setX(double x) {((EntityTrackerEntry) handle).j = x; return this;}
		public VectorAbstract setY(double y) {((EntityTrackerEntry) handle).k = y; return this;}
		public VectorAbstract setZ(double z) {((EntityTrackerEntry) handle).l = z; return this;}
	};
	/**
	 * Obtains the live protocol velocity, allowing it to be read from or written to
	 */
	public final VectorAbstract velLive = new VectorAbstract() {
		public double getX() {return entity.vel.getX();}
		public double getY() {return entity.vel.getY();}
		public double getZ() {return entity.vel.getZ();}
		public VectorAbstract setX(double x) {entity.vel.setX(x); return this;}
		public VectorAbstract setY(double y) {entity.vel.setY(y); return this;}
		public VectorAbstract setZ(double z) {entity.vel.setZ(z); return this;}
	};
	/**
	 * Obtains the protocol location as the clients know it, allowing it to be read from or written to
	 */
	public final IntLocationAbstract locSynched = new IntLocationAbstract() {
		public World getWorld() {return entity.getWorld();}
		public IntLocationAbstract setWorld(World world) {entity.setWorld(world); return this;}
		public int getX() {return ((EntityTrackerEntry) handle).xLoc;}
		public int getY() {return ((EntityTrackerEntry) handle).yLoc;}
		public int getZ() {return ((EntityTrackerEntry) handle).zLoc;}
		public IntLocationAbstract setX(int x) {((EntityTrackerEntry) handle).xLoc = x; return this;}
		public IntLocationAbstract setY(int y) {((EntityTrackerEntry) handle).yLoc = y; return this;}
		public IntLocationAbstract setZ(int z) {((EntityTrackerEntry) handle).zLoc = z; return this;}
		public int getYaw() {return ((EntityTrackerEntry) handle).yRot;}
		public int getPitch() {return ((EntityTrackerEntry) handle).xRot;}
		public IntLocationAbstract setYaw(int yaw) {((EntityTrackerEntry) handle).yRot = yaw; return this;}
		public IntLocationAbstract setPitch(int pitch) {((EntityTrackerEntry) handle).xRot = pitch; return this;}
	};
	/**
	 * Obtains the protocol location as it is live, on the server. Read is mainly supported, writing to it is not recommended.
	 * Although it has valid setters, the loss of accuracy of the protocol values make it rather pointless to use.
	 */
	public final IntLocationAbstract locLive = new IntLocationAbstract() {
		public World getWorld() {return entity.getWorld();}
		public IntLocationAbstract setWorld(World world) {entity.setWorld(world); return this;}
		public int getX() {return protLoc(entity.loc.getX());}
		public int getY() {return MathUtil.floor(entity.loc.getY() * 32.0);}
		public int getZ() {return protLoc(entity.loc.getZ());}
		public IntLocationAbstract setX(int x) {entity.loc.setX(x >> 5); return this;}
		public IntLocationAbstract setY(int y) {entity.loc.setY(y >> 5); return this;}
		public IntLocationAbstract setZ(int z) {entity.loc.setZ(z >> 5); return this;}
		public int getYaw() {return protRot(entity.loc.getYaw());}
		public int getPitch() {return protRot(entity.loc.getPitch());}
		public IntLocationAbstract setYaw(int yaw) {entity.loc.setYaw((float) yaw / 256.0f * 360.0f); return this;}
		public IntLocationAbstract setPitch(int pitch) {entity.loc.setPitch((float) pitch / 256.0f * 360.0f); return this;}
	};

	/**
	 * Binds this Entity Network Controller to an Entity.
	 * This is called from elsewhere, and should be ignored entirely.
	 * 
	 * @param entity to bind with
	 * @param entityTrackerEntry to bind with
	 */
	public final void bind(T entity, Object entityTrackerEntry) {
		if (this.entity != null) {
			this.onDetached();
		}
		this.entity = entity;
		this.handle = entityTrackerEntry;
		if (this.handle instanceof NMSEntityTrackerEntry) {
			((NMSEntityTrackerEntry) this.handle).setController(this);
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
		return handle;
	}

	/**
	 * Gets a collection of all Players viewing this Entity
	 * 
	 * @return viewing players
	 */
	public final Collection<Player> getViewers() {
		return Collections.unmodifiableCollection(EntityTrackerEntryRef.viewers.get(handle));
	}

	/**
	 * Adds a new viewer to this Network Controller.
	 * Calling this method also results in spawn messages being sent to the viewer.
	 * When overriding, make sure to always check the super-result before continuing!
	 * 
	 * @param viewer to add
	 * @return True if the viewer was added, False if the viewer was already added
	 */
	@SuppressWarnings("unchecked")
	public boolean addViewer(Player viewer) {
		if (!((EntityTrackerEntry) handle).trackedPlayers.add(Conversion.toEntityHandle.convert(viewer))) {
			return false;
		}
		this.makeVisible(viewer);
		return true;
	}

	/**
	 * Removes a viewer from this Network Controller.
	 * Calling this method also results in destroy messages being sent to the viewer.
	 * When overriding, make sure to always check the super-result before continuing!
	 * 
	 * @param viewer to remove
	 * @return True if the viewer was removed, False if the viewer wasn't contained
	 */
	public boolean removeViewer(Player viewer) {
		if (!((EntityTrackerEntry) handle).trackedPlayers.remove(Conversion.toEntityHandle.convert(viewer))) {
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
		final IntVector3 pos = this.getProtocolPositionSynched();
		final double dx = Math.abs(EntityUtil.getLocX(viewer) - (double) (pos.x / 32.0));
		final double dz = Math.abs(EntityUtil.getLocZ(viewer) - (double) (pos.z / 32.0));
		final double view = this.getViewDistance();
		// Only add the viewer if it is in view, and if the viewer can actually see the entity (PlayerChunk)
		// The ignoreChunkCheck is needed for when a new player spawns (it is not yet added to the PlayerChunk)
		if (dx <= view && dz <= view && (EntityRef.ignoreChunkCheck.get(entity.getHandle()) || 
				PlayerUtil.isChunkEntered(viewer, entity.getChunkX(), entity.getChunkZ()))) {
			addViewer(viewer);
		} else {
			removeViewer(viewer);
		}
	}

	/**
	 * Ensures that the Entity is displayed to the viewer
	 * 
	 * @param viewer to display this Entity for
	 */
	public void makeVisible(Player viewer) {
		CommonNMS.getNative(viewer).removeQueue.remove((Object) entity.getEntityId());

		// Spawn packet
		PacketUtil.sendPacket(viewer, getSpawnPacket());

		// Meta Data
		PacketUtil.sendPacket(viewer, PacketFields.ENTITY_METADATA.newInstance(entity.getEntityId(), entity.getMetaData(), true));

		// Velocity
		PacketUtil.sendPacket(viewer, PacketFields.ENTITY_VELOCITY.newInstance(entity.getEntityId(), this.getProtocolVelocitySynched()));

		// Passenger?
		if (entity.isInsideVehicle()) {
			PacketUtil.sendPacket(viewer, PacketFields.ATTACH_ENTITY.newInstance(entity.getEntity(), entity.getVehicle()));
		}

		// Special living entity messages
		if (entity.getEntity() instanceof LivingEntity) {
			// Equipment: TODO (needs NMS because index is lost...)

			// Mob effects: TODO (can use Potion effects?)
		}

		// Human entity sleeping action
		if (entity.getEntity() instanceof HumanEntity && ((HumanEntity) entity.getEntity()).isSleeping()) {
			PacketUtil.sendPacket(viewer, PacketFields.ENTITY_LOCATION_ACTION.newInstance(entity.getEntity(), 
					0, entity.loc.x.block(), entity.loc.y.block(), entity.loc.z.block()));
		}

		// Initial entity head rotation
		int headRot = getProtocolHeadRotation();
		if (headRot != 0) {
			PacketUtil.sendPacket(viewer, PacketFields.ENTITY_HEAD_ROTATION.newInstance(entity.getEntityId(), (byte) headRot));
		}
	}

	/**
	 * Ensures that the Entity is no longer displayed to any viewers.
	 * All viewers will see the Entity disappear. This method queues for the next tick.
	 */
	public void makeHiddenForAll() {
		for (Player viewer : getViewers()) {
			makeHidden(viewer);
		}
	}

	/**
	 * Ensures that the Entity is no longer displayed to any viewers.
	 * All viewers will see the Entity disappear.
	 * 
	 * @param instant option: True to instantly hide, False to queue it for the next tick
	 */
	public void makeHiddenForAll(boolean instant) {
		for (Player viewer : getViewers()) {
			makeHidden(viewer, instant);
		}
	}

	/**
	 * Ensures that the Entity is no longer displayed to the viewer.
	 * The entity is not instantly hidden; it is queued for the next tick.
	 * 
	 * @param viewer to hide this Entity for
	 */
	public void makeHidden(Player viewer) {
		makeHidden(viewer, false);
	}

	/**
	 * Ensures that the Entity is no longer displayed to the viewer
	 * 
	 * @param viewer to hide this Entity for
	 * @param instant option: True to instantly hide, False to queue it for the next tick
	 */
	@SuppressWarnings("unchecked")
	public void makeHidden(Player viewer, boolean instant) {
		if (instant) {
			PacketUtil.sendPacket(viewer, PacketFields.DESTROY_ENTITY.newInstance(entity.getEntityId()));
		} else {
			CommonNMS.getNative(viewer).removeQueue.add((Object) entity.getEntityId());
		}
	}

	/**
	 * Called at a set interval to synchronize data to clients
	 */
	public void onSync() {
		if (entity.isDead()) {
			return;
		}
		//TODO: Item frame support? Meh. Not for not. Later.
		this.syncVehicle();
		if (this.isUpdateTick() || entity.isPositionChanged()) {
			entity.setPositionChanged(false);
			// Update location
			if (this.getTicksSinceLocationSync() > ABSOLUTE_UPDATE_INTERVAL) {
				this.syncLocationAbsolute();
			} else {
				this.syncLocation();
			}

			// Update velocity when position changes
			entity.setVelocityChanged(false);
			this.syncVelocity();
		} else if (entity.isVelocityChanged()) {
			// Update velocity when velocity changes
			entity.setVelocityChanged(false);
			this.syncVelocity();
		}
		this.syncMeta();
		this.syncHeadRotation();
	}

	/**
	 * Synchronizes the entity Vehicle.
	 * Updates when the vehicle changes, or if in a Vehicle at a set interval.
	 */
	public void syncVehicle() {
		org.bukkit.entity.Entity oldVehicle = this.getVehicleSynched();
		org.bukkit.entity.Entity newVehicle = entity.getVehicle();
		if (oldVehicle != newVehicle) { // || (newVehicle != null && isTick(60))) { << DISABLED UNTIL IT ACTUALLY WORKS
			this.syncVehicle(newVehicle);
		}
	}

	/**
	 * Synchronizes the entity location to all clients.
	 * Based on the distances, relative or absolute movement is performed.
	 */
	public void syncLocation() {
		// Position
		final IntVector3 oldPos = this.getProtocolPositionSynched();
		final IntVector3 newPos = this.getProtocolPosition();
		final boolean moved = newPos.subtract(oldPos).abs().greaterEqualThan(MIN_RELATIVE_CHANGE);
		// Rotation
		final IntVector2 oldRot = this.getProtocolRotationSynched();
		final IntVector2 newRot = this.getProtocolRotation();
		final boolean rotated = newRot.subtract(oldRot).abs().greaterEqualThan(MIN_RELATIVE_CHANGE);
		// Synchronize
		syncLocation(moved ? newPos : null, rotated ? newRot : null);
	}

	/**
	 * Synchronizes the entity head yaw rotation to all Clients.
	 */
	public void syncHeadRotation() {
		final int oldYaw = this.getProtocolHeadRotationSynched();
		final int newYaw = this.getProtocolHeadRotation();
		if (Math.abs(newYaw - oldYaw) >= MIN_RELATIVE_CHANGE) {
			syncHeadRotation(newYaw);
		}
	}

	/**
	 * Synchronizes the entity metadata to all Clients.
	 * Metadata changes are read and used.
	 */
	public void syncMeta() {
		DataWatcher meta = entity.getMetaData();
		if (meta.isChanged()) {
			broadcast(PacketFields.ENTITY_METADATA.newInstance(entity.getEntityId(), meta, false), true);
		}
	}

	/**
	 * Synchronizes the entity velocity to all Clients.
	 * Based on a change in Velocity, velocity will be updated.
	 */
	public void syncVelocity() {
		if (!this.isMobile()) {
			return;
		}
		//TODO: For players, there should be an event here!
		Vector oldVel = this.getProtocolVelocitySynched();
		Vector newVel = this.getProtocolVelocity();
		// Synchronize velocity when the entity stopped moving, or when the velocity change is large enough
		if ((newVel.lengthSquared() == 0.0 && oldVel.lengthSquared() > 0.0) || oldVel.distanceSquared(newVel) > MIN_RELATIVE_VELOCITY_SQUARED) {
			this.syncVelocity(newVel);
		}
	}

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
	 * @param self option: True to send to self (if a player), False to not send to self
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
	 * Creates a new spawn packet for spawning this Entity.
	 * To change the spawned entity type, override this method.
	 * By default, the entity is evaluated and the right packet is created automatically.
	 * 
	 * @return spawn packet
	 */
	public CommonPacket getSpawnPacket() {
		final CommonPacket packet = EntityTrackerEntryRef.getSpawnPacket(handle);
		if (PacketFields.VEHICLE_SPAWN.isInstance(packet)) {
			// NMS error: They are not using the position, but the live position
			// This has some big issues when new players join...

			// Position
			final IntVector3 pos = this.getProtocolPositionSynched();
			packet.write(PacketFields.VEHICLE_SPAWN.x, pos.x);
			packet.write(PacketFields.VEHICLE_SPAWN.y, pos.y);
			packet.write(PacketFields.VEHICLE_SPAWN.z, pos.z);
			// Rotation
			final IntVector2 rot = this.getProtocolRotationSynched();
			packet.write(PacketFields.VEHICLE_SPAWN.yaw, (byte) rot.z);
			packet.write(PacketFields.VEHICLE_SPAWN.pitch, (byte) rot.x);
		}
		return packet;
	}

	public int getViewDistance() {
		return EntityTrackerEntryRef.viewDistance.get(handle);
	}

	public void setViewDistance(int blockDistance) {
		EntityTrackerEntryRef.viewDistance.set(handle, blockDistance);
	}

	public int getUpdateInterval() {
		return EntityTrackerEntryRef.updateInterval.get(handle);
	}

	public void setUpdateInterval(int tickInterval) {
		EntityTrackerEntryRef.updateInterval.set(handle, tickInterval);
	}

	public boolean isMobile() {
		return EntityTrackerEntryRef.isMobile.get(handle);
	}

	public void setMobile(boolean mobile) {
		EntityTrackerEntryRef.isMobile.set(handle, mobile);
	}

	/**
	 * Synchronizes everything by first destroying and then respawning this Entity to all viewers
	 */
	public void syncRespawn() {
		// Hide
		for (Player viewer : getViewers()) {
			this.makeHidden(viewer, true);
		}

		// Update information
		final EntityTrackerEntry handle = (EntityTrackerEntry) this.handle;
		final Vector velocity = this.getProtocolVelocity();
		handle.j = velocity.getX();
		handle.k = velocity.getY();
		handle.l = velocity.getZ();
		final IntVector3 position = this.getProtocolPosition();
		handle.xLoc = position.x;
		handle.yLoc = position.y;
		handle.zLoc = position.z;
		final IntVector2 rotation = this.getProtocolRotation();
		handle.yRot = rotation.x;
		handle.xRot = rotation.z;
		final int headYaw = this.getProtocolHeadRotation();
		handle.i = headYaw;

		// Spawn
		for (Player viewer : getViewers()) {
			this.makeVisible(viewer);
		}
		// Attach messages (because it is not handled by vehicles)
		if (entity.hasPassenger()) {
			broadcast(PacketFields.ATTACH_ENTITY.newInstance(entity.getPassenger(), entity.getEntity()));
		}
	}

	/**
	 * Synchronizes the entity Vehicle
	 * 
	 * @param vehicle to synchronize, NULL for no Vehicle
	 */
	public void syncVehicle(org.bukkit.entity.Entity vehicle) {
		EntityTrackerEntryRef.vehicle.set(handle, vehicle);
		broadcast(PacketFields.ATTACH_ENTITY.newInstance(entity.getEntity(), vehicle));
	}

	/**
	 * Synchronizes the entity position / rotation absolutely (Teleport packet)
	 */
	public void syncLocationAbsolute() {
		syncLocationAbsolute(getProtocolPosition(), getProtocolRotation());
	}

	/**
	 * Synchronizes the entity position / rotation absolutely (Teleport packet)
	 * 
	 * @param position (new)
	 * @param rotation (new, x = yaw, z = pitch)
	 */
	public void syncLocationAbsolute(IntVector3 position, IntVector2 rotation) {
		if (position == null) {
			position = this.getProtocolPositionSynched();
		}
		if (rotation == null) {
			rotation = this.getProtocolRotationSynched();
		}
		// Update protocol values
		final EntityTrackerEntry handle = (EntityTrackerEntry) this.handle;
		handle.xLoc = position.x;
		handle.yLoc = position.y;
		handle.zLoc = position.z;
		handle.yRot = rotation.x;
		handle.xRot = rotation.z;

		// Update last synchronization time
		EntityTrackerEntryRef.timeSinceLocationSync.set(handle, 0);

		// Send synchronization messages
		broadcast(PacketFields.ENTITY_TELEPORT.newInstance(entity.getEntityId(), position.x, position.y, position.z, 
				(byte) rotation.x, (byte) rotation.z));
	}

	/**
	 * Synchronizes the entity position / rotation relatively.
	 * 
	 * @param position - whether to sync position
	 * @param rotation - whether to sync rotation
	 */
	public void syncLocation(boolean position, boolean rotation) {
		syncLocation(position ? getProtocolPosition() : null,
				rotation ? getProtocolRotation() : null);
	}

	/**
	 * Synchronizes the entity position / rotation relatively.
	 * If the relative change is too big, an absolute update is performed instead.
	 * Pass in null values to ignore updating it.
	 * 
	 * @param position (new)
	 * @param rotation (new, x = yaw, z = pitch)
	 */
	public void syncLocation(IntVector3 position, IntVector2 rotation) {
		final boolean moved = position != null;
		final boolean rotated = rotation != null;
		final IntVector3 deltaPos = moved ? position.subtract(this.getProtocolPositionSynched()) : IntVector3.ZERO;
		if (deltaPos.abs().greaterThan(MAX_RELATIVE_DISTANCE)) {
			// Perform teleport instead
			syncLocationAbsolute(position, rotation);
		} else {
			// Update protocol values
			final EntityTrackerEntry handle = (EntityTrackerEntry) this.handle;
			if (moved) {
				handle.xLoc = position.x;
				handle.yLoc = position.y;
				handle.zLoc = position.z;
			}
			if (rotated) {
				handle.yRot = rotation.x;
				handle.xRot = rotation.z;
			}

			// Send synchronization messages
			// If inside vehicle - there is no use to update the location!
			if (entity.isInsideVehicle()) {
				if (rotated) {
					broadcast(PacketFields.ENTITY_LOOK.newInstance(entity.getEntityId(), (byte) rotation.x, (byte) rotation.z));
				}
			} else if (moved && rotated) {
				broadcast(PacketFields.REL_ENTITY_MOVE_LOOK.newInstance(entity.getEntityId(), 
						(byte) deltaPos.x, (byte) deltaPos.y, (byte) deltaPos.z, (byte) rotation.x, (byte) rotation.z));

			} else if (moved) {
				broadcast(PacketFields.REL_ENTITY_MOVE.newInstance(entity.getEntityId(), 
						(byte) deltaPos.x, (byte) deltaPos.y, (byte) deltaPos.z));

			} else if (rotated) {
				broadcast(PacketFields.ENTITY_LOOK.newInstance(entity.getEntityId(), (byte) rotation.x, (byte) rotation.z));
			}
		}
	}

	/**
	 * Synchronizes the entity head yaw rotation to all Clients
	 * 
	 * @param headRotation to set to
	 */
	public void syncHeadRotation(int headRotation) {
		((EntityTrackerEntry) handle).i = headRotation;
		this.broadcast(PacketFields.ENTITY_HEAD_ROTATION.newInstance(entity.getEntityId(), (byte) headRotation));
	}

	/**
	 * Synchronizes the entity velocity
	 * 
	 * @param velocity (new)
	 */
	public void syncVelocity(Vector velocity) {
		setProtocolVelocitySynched(velocity);
		// If inside a vehicle, there is no use in updating
		if (entity.isInsideVehicle()) {
			return;
		}
		this.broadcast(PacketFields.ENTITY_VELOCITY.newInstance(entity.getEntityId(), velocity));
	}

	/**
	 * Obtains the current Vehicle entity according to the viewers of this entity
	 * 
	 * @return Client-synchronized vehicle entity
	 */
	public org.bukkit.entity.Entity getVehicleSynched() {
		return EntityTrackerEntryRef.vehicle.get(handle);
	}

	/**
	 * Sets the current synched velocity of the entity according to the viewers of this entity.
	 * This method can be used instead of syncVelocity to ignore packet sending.
	 * 
	 * @param velocity to set to
	 */
	public void setProtocolVelocitySynched(Vector velocity) {
		final EntityTrackerEntry handle = (EntityTrackerEntry) this.handle;
		handle.j = velocity.getX();
		handle.k = velocity.getY();
		handle.l = velocity.getZ();
	}

	/**
	 * Obtains the current velocity of the entity according to the viewers of this entity
	 * 
	 * @return Client-synchronized entity velocity
	 */
	public Vector getProtocolVelocitySynched() {
		final EntityTrackerEntry handle = (EntityTrackerEntry) this.handle;
		return new Vector(handle.j, handle.k, handle.l);
	}

	/**
	 * Obtains the current position of the entity according to the viewers of this entity
	 * 
	 * @return Client-synchronized entity position
	 */
	public IntVector3 getProtocolPositionSynched() {
		final EntityTrackerEntry handle = (EntityTrackerEntry) this.handle;
		return new IntVector3(handle.xLoc, handle.yLoc, handle.zLoc);
	}

	/**
	 * Obtains the current rotation of the entity according to the viewers of this entity
	 * 
	 * @return Client-synchronized entity rotation (x = yaw, z = pitch)
	 */
	public IntVector2 getProtocolRotationSynched() {
		final EntityTrackerEntry handle = (EntityTrackerEntry) this.handle;
		return new IntVector2(handle.yRot, handle.xRot);
	}

	/**
	 * Obtains the current velocity of the entity, converted to protocol format
	 * 
	 * @return Entity velocity in protocol format
	 */
	public Vector getProtocolVelocity() {
		return this.entity.getVelocity();
	}

	/**
	 * Obtains the current position of the entity, converted to protocol format
	 * 
	 * @return Entity position in protocol format
	 */
	public IntVector3 getProtocolPosition() {
		final Entity entity = this.entity.getHandle(Entity.class);
		return new IntVector3(protLoc(entity.locX), MathUtil.floor(entity.locY * 32.0), protLoc(entity.locZ));
	}

	/**
	 * Obtains the current rotation (yaw/pitch) of the entity, converted to protocol format
	 * 
	 * @return Entity rotation in protocol format (x = yaw, z = pitch)
	 */
	public IntVector2 getProtocolRotation() {
		final Entity entity = this.entity.getHandle(Entity.class);
		return new IntVector2(protRot(entity.yaw), protRot(entity.pitch));
	}

	/**
	 * Obtains the current head yaw rotation of this entity, according to the viewers
	 * 
	 * @return Client-synched head-yaw rotation
	 */
	public int getProtocolHeadRotationSynched() {
		return ((EntityTrackerEntry) handle).i;
	}

	/**
	 * Gets the amount of ticks that have passed since the last Location synchronization.
	 * A location synchronization means that an absolute position update is performed.
	 * 
	 * @return ticks since last location synchronization
	 */
	public int getTicksSinceLocationSync() {
		return EntityTrackerEntryRef.timeSinceLocationSync.get(handle);
	}

	/**
	 * Checks whether the current update interval is reached
	 * 
	 * @return True if the update interval was reached, False if not
	 */
	public boolean isUpdateTick() {
		final EntityTrackerEntry handle = (EntityTrackerEntry) this.handle;
		return (handle.m % handle.c) == 0;
	}

	/**
	 * Checks whether a certain interval is reached
	 * 
	 * @param interval in ticks
	 * @return True if the interval was reached, False if not
	 */
	public boolean isTick(int interval) {
		return (((EntityTrackerEntry) handle).m % interval) == 0;
	}

	/**
	 * Obtains the current 'tick' value, which can be used for intervals
	 * 
	 * @return Tick time
	 */
	public int getTick() {
		return ((EntityTrackerEntry) handle).m;
	}

	/**
	 * Obtains the current head rotation of the entity, converted to protocol format
	 * 
	 * @return Entity head rotation in protocol format
	 */
	public int getProtocolHeadRotation() {
		return protRot(this.entity.getHeadRotation());
	}

	private int protRot(float rot) {
		return MathUtil.floor(rot * 256.0f / 360.0f);
	}

	private int protLoc(double loc) {
		return ((EntityTrackerEntry) handle).tracker.at.a(loc);
	}
}
