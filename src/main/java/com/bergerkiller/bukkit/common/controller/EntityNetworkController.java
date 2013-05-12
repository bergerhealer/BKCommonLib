package com.bergerkiller.bukkit.common.controller;

import java.util.Collection;
import java.util.Collections;

import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;

import net.minecraft.server.EntityTrackerEntry;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.bases.mutable.IntLocationAbstract;
import com.bergerkiller.bukkit.common.bases.mutable.IntegerAbstract;
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
import com.bergerkiller.bukkit.common.utils.CommonUtil;
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
	 * Obtains the protocol head rotation as the clients know it, allowing it to be read from or written to
	 */
	public final IntegerAbstract headRotSynched = new IntegerAbstract() {
		public int get() {return ((EntityTrackerEntry) handle).i;}
		public IntegerAbstract set(int value) {((EntityTrackerEntry) handle).i = value; return this;}
	};
	/**
	 * Obtains the protocol head rotation as it is live, on the server. Only reading is supported.
	 */
	public final IntegerAbstract headRotLive = new IntegerAbstract() {
		public int get() {return protRot(entity.getHeadRotation());}
		public IntegerAbstract set(int value) {throw new UnsupportedOperationException();}
	};
	/**
	 * Obtains the tick time, this is for how long this network component/entry has been running on the server.
	 * The tick time can be used to perform operations on an interval.
	 * The tick time is automatically updated behind the hood.
	 */
	public final IntegerAbstract ticks = new IntegerAbstract() {
		public int get() {return ((EntityTrackerEntry) handle).m;}
		public IntegerAbstract set(int value) {((EntityTrackerEntry) handle).m = value; return this;}
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
		//TODO: Item frame support? Meh. Not for now.
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
				this.broadcast(PacketFields.ENTITY_VELOCITY.newInstance(entity.getEntityId(), velocity));
			}
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
		// Position check
		final boolean moved = Math.abs(locLive.getX() - locSynched.getX()) >= MIN_RELATIVE_CHANGE
				|| Math.abs(locLive.getY() - locSynched.getY()) >= MIN_RELATIVE_CHANGE
				|| Math.abs(locLive.getZ() - locSynched.getZ()) >= MIN_RELATIVE_CHANGE;

		// Rotation check
		final boolean rotated = Math.abs(locLive.getYaw() - locSynched.getYaw()) >= MIN_RELATIVE_CHANGE
				|| Math.abs(locLive.getPitch() - locSynched.getPitch()) >= MIN_RELATIVE_CHANGE;

		// Synchronize
		syncLocation(moved, rotated);
	}

	/**
	 * Synchronizes the entity head yaw rotation to all Clients.
	 */
	public void syncHeadRotation() {
		final int oldYaw = headRotSynched.get();
		final int newYaw = headRotLive.get();
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
		if ((velLive.lengthSquared() == 0.0 && velSynched.lengthSquared() > 0.0) || velLive.distanceSquared(velSynched) > MIN_RELATIVE_VELOCITY_SQUARED) {
			this.syncVelocity(velLive.getX(), velLive.getY(), velLive.getZ());
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
			packet.write(PacketFields.VEHICLE_SPAWN.x, locSynched.getX());
			packet.write(PacketFields.VEHICLE_SPAWN.y, locSynched.getY());
			packet.write(PacketFields.VEHICLE_SPAWN.z, locSynched.getZ());
			// Rotation
			packet.write(PacketFields.VEHICLE_SPAWN.yaw, (byte) locSynched.getYaw());
			packet.write(PacketFields.VEHICLE_SPAWN.pitch, (byte) locSynched.getPitch());
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
		velSynched.set(velLive);
		locSynched.set(locLive);
		headRotSynched.set(headRotLive.get());

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
		syncLocationAbsolute(locLive.getX(), locLive.getY(), locLive.getZ(), locLive.getYaw(), locLive.getPitch());
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
		syncLocationAbsolute(position.x, position.y, position.z, rotation.x, rotation.z);
	}

	/**
	 * Synchronizes the entity position / rotation absolutely (Teleport packet)
	 * 
	 * @param posX - protocol position X
	 * @param posY - protocol position Y
	 * @param posZ - protocol position Z
	 * @param yaw - protocol rotation yaw
	 * @param pitch - protocol rotation pitch
	 */
	public void syncLocationAbsolute(int posX, int posY, int posZ, int yaw, int pitch) {
		// Update protocol values
		locSynched.set(posX, posY, posZ, yaw, pitch);

		// Update last synchronization time
		EntityTrackerEntryRef.timeSinceLocationSync.set(handle, 0);

		// Send synchronization messages
		broadcast(PacketFields.ENTITY_TELEPORT.newInstance(entity.getEntityId(), posX, posY, posZ, (byte) yaw, (byte) pitch));
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
	 * Synchronizes the entity position / rotation relatively.
	 * If the relative change is too big, an absolute update is performed instead.
	 * Pass in null values to ignore updating it.
	 * 
	 * @param position (new)
	 * @param rotation (new, x = yaw, z = pitch)
	 */
	public void syncLocation(IntVector3 position, IntVector2 rotation) {
		if (position == null && rotation == null) {
			return;
		}
		if (position == null) {
			syncLocation(false, true, 0, 0, 0, rotation.x, rotation.z);
		} else if (rotation == null) {
			syncLocation(true, false, position.x, position.y, position.z, 0, 0);
		} else {
			syncLocation(true, true, position.x, position.y, position.z, rotation.x, rotation.z);
		}
	}

	/**
	 * Synchronizes the entity position / rotation relatively.
	 * If the relative change is too big, an absolute update is performed instead.
	 * 
	 * @param position - whether to update position (read pos on/off)
	 * @param rotation - whether to update rotation (read yawpitch on/off)
	 * @param posX - protocol position X
	 * @param posY - protocol position Y
	 * @param posZ - protocol position Z
	 * @param yaw - protocol rotation yaw
	 * @param pitch - protocol rotation pitch
	 */
	public void syncLocation(boolean position, boolean rotation, int posX, int posY, int posZ, int yaw, int pitch) {
		// No position updates allowed for passengers (this is FORCED). Rotation is allowed.
		if (position && !entity.isInsideVehicle()) {
			final int deltaX = posX - locSynched.getX();
			final int deltaY = posY - locSynched.getY();
			final int deltaZ = posZ - locSynched.getZ();

			// There is no use sending relative updates with zero change...
			if (deltaX == 0 && deltaY == 0 && deltaZ == 0) {
				return;
			}

			// Absolute updates for too long distances
			if (Math.abs(deltaX) > MAX_RELATIVE_DISTANCE || Math.abs(deltaY) > MAX_RELATIVE_DISTANCE || Math.abs(deltaZ) > MAX_RELATIVE_DISTANCE) {
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
				broadcast(PacketFields.REL_ENTITY_MOVE_LOOK.newInstance(entity.getEntityId(), 
						(byte) deltaX, (byte) deltaY, (byte) deltaZ, (byte) yaw, (byte) pitch));
			} else {
				// Only update position relatively
				locSynched.set(posX, posY, posZ);
				broadcast(PacketFields.REL_ENTITY_MOVE.newInstance(entity.getEntityId(), 
						(byte) deltaX, (byte) deltaY, (byte) deltaZ));
			}
		} else if (rotation) {
			// Only update rotation
			locSynched.setRotation(yaw, pitch);
			broadcast(PacketFields.ENTITY_LOOK.newInstance(entity.getEntityId(), (byte) yaw, (byte) pitch));
		}
	}

	/**
	 * Synchronizes the entity head yaw rotation to all Clients
	 * 
	 * @param headRotation to set to
	 */
	public void syncHeadRotation(int headRotation) {
		headRotSynched.set(headRotation);
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
		this.broadcast(PacketFields.ENTITY_VELOCITY.newInstance(entity.getEntityId(), velX, velY, velZ));
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
		velSynched.set(velocity);
	}

	/**
	 * Obtains the current velocity of the entity according to the viewers of this entity
	 * 
	 * @return Client-synchronized entity velocity
	 */
	public Vector getProtocolVelocitySynched() {
		return velSynched.vector();
	}

	/**
	 * Obtains the current position of the entity according to the viewers of this entity
	 * 
	 * @return Client-synchronized entity position
	 */
	public IntVector3 getProtocolPositionSynched() {
		return locSynched.vector();
	}

	/**
	 * Obtains the current rotation of the entity according to the viewers of this entity
	 * 
	 * @return Client-synchronized entity rotation (x = yaw, z = pitch)
	 */
	public IntVector2 getProtocolRotationSynched() {
		return new IntVector2(locSynched.getYaw(), locSynched.getPitch());
	}

	/**
	 * Obtains the current velocity of the entity, converted to protocol format
	 * 
	 * @return Entity velocity in protocol format
	 */
	public Vector getProtocolVelocity() {
		return velLive.vector();
	}

	/**
	 * Obtains the current position of the entity, converted to protocol format
	 * 
	 * @return Entity position in protocol format
	 */
	public IntVector3 getProtocolPosition() {
		return locLive.vector();
	}

	/**
	 * Obtains the current rotation (yaw/pitch) of the entity, converted to protocol format
	 * 
	 * @return Entity rotation in protocol format (x = yaw, z = pitch)
	 */
	public IntVector2 getProtocolRotation() {
		return new IntVector2(locLive.getYaw(), locLive.getPitch());
	}

	/**
	 * Obtains the current head yaw rotation of this entity, according to the viewers
	 * 
	 * @return Client-synched head-yaw rotation
	 */
	public int getProtocolHeadRotationSynched() {
		return headRotSynched.get();
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
		return ticks.isMod(((EntityTrackerEntry) handle).c);
	}

	/**
	 * Checks whether a certain interval is reached
	 * 
	 * @param interval in ticks
	 * @return True if the interval was reached, False if not
	 */
	public boolean isTick(int interval) {
		return ticks.isMod(interval);
	}

	/**
	 * Obtains the current 'tick' value, which can be used for intervals
	 * 
	 * @return Tick time
	 */
	public int getTick() {
		return ticks.get();
	}

	/**
	 * Obtains the current head rotation of the entity, converted to protocol format
	 * 
	 * @return Entity head rotation in protocol format
	 */
	public int getProtocolHeadRotation() {
		return headRotLive.get();
	}

	private int protRot(float rot) {
		return MathUtil.floor(rot * 256.0f / 360.0f);
	}

	private int protLoc(double loc) {
		return ((EntityTrackerEntry) handle).tracker.at.a(loc);
	}
}
