package com.bergerkiller.bukkit.common.controller;

import java.util.Collection;
import java.util.Collections;

import net.minecraft.server.v1_8_R1.AttributeMapServer;
import net.minecraft.server.v1_8_R1.EntityLiving;
import net.minecraft.server.v1_8_R1.EntityTrackerEntry;
import net.minecraft.server.v1_8_R1.MathHelper;
import net.minecraft.server.v1_8_R1.MobEffect;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.mutable.IntLocationAbstract;
import com.bergerkiller.bukkit.common.bases.mutable.IntegerAbstract;
import com.bergerkiller.bukkit.common.bases.mutable.ObjectAbstract;
import com.bergerkiller.bukkit.common.bases.mutable.VectorAbstract;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.entity.CommonEntityController;
import com.bergerkiller.bukkit.common.entity.nms.EnumEntitySize;
import com.bergerkiller.bukkit.common.entity.nms.NMSEntityTrackerEntry;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.reflection.classes.EntityLivingRef;
import com.bergerkiller.bukkit.common.reflection.classes.EntityRef;
import com.bergerkiller.bukkit.common.reflection.classes.EntityTrackerEntryRef;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
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
	
	private EnumEntitySize es;
	
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
	 * The tick interval at which the entity is updated absolutely
	 */
	public static final int ABSOLUTE_UPDATE_INTERVAL = 400;

	private Object handle;

	/**
	 * Obtains the velocity as the clients know it, allowing it to be read from or written to
	 */
	public VectorAbstract velSynched = new VectorAbstract() {
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
	public VectorAbstract velLive = new VectorAbstract() {
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
	public IntLocationAbstract locSynched = new IntLocationAbstract() {
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
	public IntLocationAbstract locLive = new IntLocationAbstract() {
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
	public IntegerAbstract headRotSynched = new IntegerAbstract() {
		public int get() {return ((EntityTrackerEntry) handle).i;}
		public IntegerAbstract set(int value) {((EntityTrackerEntry) handle).i = value; return this;}
	};
	/**
	 * Obtains the protocol head rotation as it is live, on the server. Only reading is supported.
	 */
	public IntegerAbstract headRotLive = new IntegerAbstract() {
		public int get() {return protRot(entity.getHeadRotation());}
		public IntegerAbstract set(int value) {throw new UnsupportedOperationException();}
	};
	/**
	 * Obtains the tick time, this is for how long this network component/entry has been running on the server.
	 * The tick time can be used to perform operations on an interval.
	 * The tick time is automatically updated behind the hood.
	 */
	public IntegerAbstract ticks = new IntegerAbstract() {
		public int get() {return ((EntityTrackerEntry) handle).m;}
		public IntegerAbstract set(int value) {((EntityTrackerEntry) handle).m = value; return this;}
	};
	/**
	 * Obtains the vehicle of this (passenger) Entity as the clients know it, allowing it to be read from or written to
	 */
	public ObjectAbstract<Entity> vehicleSynched = new ObjectAbstract<Entity>() {
		public Entity get() {return EntityTrackerEntryRef.vehicle.get(handle);}
		public ObjectAbstract<Entity> set(Entity value) {EntityTrackerEntryRef.vehicle.set(handle, value); return this;}
	};

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
		return ticks.isMod(getUpdateInterval());
	}

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
		if (isViewable(viewer)) {
			addViewer(viewer);
			return;
		}
		// Check that the passenger of this Entity is not still viewable
		// We can not hide vehicle of passengers that are still viewable...
		if (entity.hasPassenger()) {
			EntityNetworkController<?> network = CommonEntity.get(entity.getPassenger()).getNetworkController();
			if (network != null && network.getViewers().contains(viewer)) {
				addViewer(viewer);
				return;
			}
		}
		// No longer a viewer
		removeViewer(viewer);
	}

	private boolean isViewable(Player viewer) {
		// View range check
		final int dx = MathHelper.floor(Math.abs(EntityUtil.getLocX(viewer) - (double) (this.locSynched.getX() / 32.0)));
		final int dz = MathHelper.floor(Math.abs(EntityUtil.getLocZ(viewer) - (double) (this.locSynched.getZ() / 32.0)));
		final int view = this.getViewDistance();
		if (dx > view || dz > view) {
			return false;
		}
		// The entity is in a chunk not seen by the viewer
		if (!EntityRef.ignoreChunkCheck.get(entity.getHandle()) && 
				!PlayerUtil.isChunkEntered(viewer, entity.getChunkX(), entity.getChunkZ())) {
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
	 * Sets whether this Entity is marked for removal during the next tick for the player
	 * 
	 * @param player to set it for
	 * @param remove - True to remove, False NOT to remove
	 */
	public void setRemoveNextTick(Player player, boolean remove) {
		LogicUtil.addOrRemove(Common.SERVER.getEntityRemoveQueue(player), entity.getEntityId(), remove);
	}

	/**
	 * Ensures that the Entity is no longer displayed to the viewer
	 * 
	 * @param viewer to hide this Entity for
	 * @param instant option: True to instantly hide, False to queue it for the next tick
	 */
	public void makeHidden(Player viewer, boolean instant) {
		// If instant, do not send other destroy messages, if not, send one
		this.setRemoveNextTick(viewer, !instant);
		if (instant) {
			PacketUtil.sendPacket(viewer, PacketType.OUT_ENTITY_DESTROY.newInstance(entity.getEntityId()));
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
	 * Ensures that the Entity is no longer displayed to any viewers.
	 * All viewers will see the Entity disappear. This method queues for the next tick.
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
			PacketUtil.sendPacket(viewer, getVehiclePacket(entity.getVehicle()));
		}
		if (entity.hasPassenger()) {
			PacketUtil.sendPacket(viewer, getPassengerPacket(entity.getPassenger()));
		}

		// Potential leash
		Entity leashHolder = entity.getLeashHolder();
		if (leashHolder != null) {
			//PacketUtil.sendPacket(viewer, PacketType.OUT_ENTITY_ATTACH.newInstance(entity.getEntity(), leashHolder, 1));
		}

		// Human entity sleeping action
		if (entity.getEntity() instanceof HumanEntity && ((HumanEntity) entity.getEntity()).isSleeping()) {
			//PacketUtil.sendPacket(viewer, PacketType.OUT_BED.newInstance((HumanEntity) entity.getEntity(), entity.loc.x.block(), entity.loc.y.block(), entity.loc.z.block()));
		}

		// Initial entity head rotation
		int headRot = headRotLive.get();
		if (headRot != 0) {
			PacketUtil.sendPacket(viewer, getHeadRotationPacket(headRot));
		}
	}

	/**
	 * Synchronizes all Entity Meta Data including Entity Attributes and other specific flags.
	 * Movement and positioning information is not updated.<br><br>
	 * 
	 * This should be called when making this Entity visible to a viewer.
	 * 
	 * @param viewer to send the meta data to
	 */
	@SuppressWarnings("unchecked")
	public void initMetaData(Player viewer) {
		// Meta Data
		DataWatcher metaData = entity.getMetaData();
		if (!metaData.isEmpty()) {
			PacketUtil.sendPacket(viewer, PacketType.OUT_ENTITY_METADATA.newInstance(entity.getEntityId(), metaData, true));
		}
		// Living Entity - only data
		if (handle instanceof EntityLiving) {
			// Entity Attributes
			AttributeMapServer attributeMap = (AttributeMapServer) EntityLivingRef.getAttributesMap.invoke(handle);
			Collection<?> attributes = attributeMap.c();
			if (!attributes.isEmpty()) {
				PacketUtil.sendPacket(viewer, PacketType.OUT_ENTITY_UPDATE_ATTRIBUTES.newInstance(entity.getEntityId(), attributes));
			}

			// Entity Equipment
			EntityLiving living = (EntityLiving) handle;
			for (int i = 0; i < 5; ++i) {
	            org.bukkit.inventory.ItemStack itemstack = Conversion.toItemStack.convert(living.getEquipment(i));
	            if (itemstack != null) {
	            	PacketUtil.sendPacket(viewer, PacketType.OUT_ENTITY_EQUIPMENT.newInstance(entity.getEntityId(), i, itemstack));
	            }
			}

			// Entity Mob Effects
			for (MobEffect effect : (Collection<MobEffect>) living.getEffects()) {
				PacketUtil.sendPacket(viewer, PacketType.OUT_ENTITY_EFFECT_ADD.newInstance(entity.getEntityId(), effect));
			}
		}
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
		this.syncVehicle();

		// Position / Rotation
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
	 * between live and synched exceed the minimum change provided.
	 * In short, it checks whether the position changed.
	 * 
	 * @param minChange to look for
	 * @return True if changed, False if not
	 */
	public boolean isPositionChanged(int minChange) {
		return Math.abs(locLive.getX() - locSynched.getX()) >= minChange
				|| Math.abs(locLive.getY() - locSynched.getY()) >= minChange
				|| Math.abs(locLive.getZ() - locSynched.getZ()) >= minChange;
	}

	/**
	 * Checks whether one of the rotation (protocol) component differences
	 * between live and synched exceed the minimum change provided.
	 * In short, it checks whether the rotation changed.
	 * 
	 * @param minChange to look for
	 * @return True if changed, False if not
	 */
	public boolean isRotationChanged(int minChange) {
		return Math.abs(locLive.getYaw() - locSynched.getYaw()) >= minChange
				|| Math.abs(locLive.getPitch() - locSynched.getPitch()) >= minChange;
	}

	/**
	 * Checks whether the velocity difference
	 * between live and synched exceeds the minimum change provided.
	 * In short, it checks whether the velocity changed.
	 * 
	 * @param minChange to look for
	 * @return True if changed, False if not
	 */
	public boolean isVelocityChanged(double minChange) {
		return velLive.distanceSquared(velSynched) > (minChange * minChange);
	}

	/**
	 * Checks whether the head rotation difference
	 * between live and synched exceeds the minimum change provided.
	 * In short, it checks whether the head rotation changed.
	 * 
	 * @param minChange to look for
	 * @return True if changed, False if not
	 */
	public boolean isHeadRotationChanged(int minChange) {
		return Math.abs(headRotLive.get() - headRotSynched.get()) >= minChange;
	}

	/**
	 * Synchronizes all Entity Meta Data including Entity Attributes and other specific flags.
	 * Movement and positioning information is not updated.
	 * Only the changes are sent, it is a relative update.
	 */
	public void syncMetaData() {
		// Meta Data
		DataWatcher meta = entity.getMetaData();
		if (meta.isChanged()) {
			broadcast(PacketType.OUT_ENTITY_METADATA.newInstance(entity.getEntityId(), meta, false), true);
		}
		// Living Entity - only data
		if (handle instanceof EntityLiving) {
			// Entity Attributes
			AttributeMapServer attributeMap = (AttributeMapServer) EntityLivingRef.getAttributesMap.invoke(handle);
			Collection<?> attributes = attributeMap.c();
			if (!attributes.isEmpty()) {
				this.broadcast(PacketType.OUT_ENTITY_UPDATE_ATTRIBUTES.newInstance(entity.getEntityId(), attributes), true);
			}
			attributes.clear();
		}
	}

	/**
	 * Synchronizes the entity Vehicle to all viewers.
	 * Updates when the vehicle changes.
	 */
	public void syncVehicle() {
		syncVehicle(entity.getVehicle());
	}
	
	/**
	 * Synchronizes the entity Vehicle
	 * 
	 * @param vehicle to synchronize, NULL for no Vehicle
	 */
	public void syncVehicle(org.bukkit.entity.Entity vehicle) {
		if (vehicleSynched.get() != vehicle) {
			vehicleSynched.set(vehicle);
			broadcast(getVehiclePacket(vehicle));
		}
	}

	/**
	 * Synchronizes the entity head yaw rotation to all viewers.
	 */
	public void syncHeadRotation() {
		if (isHeadRotationChanged(MIN_RELATIVE_CHANGE)) {
			syncHeadRotation(headRotLive.get());
		}
	}

	/**
	 * Synchronizes the entity head yaw rotation to all viewers.
	 * 
	 * @param headRotation to set to
	 */
	public void syncHeadRotation(int headRotation) {
		headRotSynched.set(headRotation);
		this.broadcast(getHeadRotationPacket(headRotation));
	}

	/**
	 * Synchronizes the entity velocity to all viewers.
	 * Based on a change in Velocity, velocity will be updated.
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
	 * Synchronizes the entity location to all clients.
	 * Based on the distances, relative or absolute movement is performed.
	 */
	public void syncLocation() {
		syncLocation(isPositionChanged(MIN_RELATIVE_CHANGE), isRotationChanged(MIN_RELATIVE_CHANGE));
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
	public void syncLocationAbsolute(int posX, int posY, int posZ, int yaw, int pitch) {
		// Update protocol values
		locSynched.set(posX, posY, posZ, yaw, pitch);

		// Update last synchronization time
		EntityTrackerEntryRef.timeSinceLocationSync.set(handle, 0);

		// Send synchronization messages
		broadcast(getLocationPacket(posX, posY, posZ, (byte) yaw, (byte) pitch));
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
				broadcast(PacketType.OUT_ENTITY_MOVE_LOOK.newInstance(entity.getEntityId(), 
						(byte) deltaX, (byte) deltaY, (byte) deltaZ, (byte) yaw, (byte) pitch, entity.isOnGround()));
			} else {
				// Only update position relatively
				locSynched.set(posX, posY, posZ);
				broadcast(PacketType.OUT_ENTITY_MOVE.newInstance(entity.getEntityId(), 
						(byte) deltaX, (byte) deltaY, (byte) deltaZ, entity.isOnGround()));
			}
		} else if (rotation) {
			// Only update rotation
			locSynched.setRotation(yaw, pitch);
			broadcast(PacketType.OUT_ENTITY_LOOK.newInstance(entity.getEntityId(), (byte) yaw, (byte) pitch, entity.isOnGround()));
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
	 * Gets a new packet with absolute Entity position information
	 * 
	 * @param posX - position X (protocol)
	 * @param posY - position Y (protocol)
	 * @param posZ - position Z (protocol)
	 * @param yaw - position yaw (protocol)
	 * @param pitch - position pitch (protocol)
	 * @return a packet with absolute position information
	 */
	public CommonPacket getLocationPacket(int posX, int posY, int posZ, int yaw, int pitch) {
		return PacketType.OUT_ENTITY_TELEPORT.newInstance(entity.getEntityId(), posX, posY, posZ, (byte) yaw, (byte) pitch, false);
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
	 * Creates a new spawn packet for spawning this Entity.
	 * To change the spawned entity type, override this method.
	 * By default, the entity is evaluated and the right packet is created automatically.
	 * 
	 * @return spawn packet
	 */
	public CommonPacket getSpawnPacket() {
		final CommonPacket packet = EntityTrackerEntryRef.getSpawnPacket(handle);
		if (PacketType.OUT_ENTITY_SPAWN.isInstance(packet)) {
			// NMS error: They are not using the position, but the live position
			// This has some big issues when new players join...

			// Position
			packet.write(PacketType.OUT_ENTITY_SPAWN.x, locSynched.getX());
			packet.write(PacketType.OUT_ENTITY_SPAWN.y, locSynched.getY());
			packet.write(PacketType.OUT_ENTITY_SPAWN.z, locSynched.getZ());
			// Rotation
			packet.write(PacketType.OUT_ENTITY_SPAWN.yaw, locSynched.getYaw());
			packet.write(PacketType.OUT_ENTITY_SPAWN.pitch, locSynched.getPitch());
		}
		return packet;
	}

	/**
	 * Gets a new packet with information for this Entity
	 * 
	 * @param passenger that is now inside this vehicle Entity
	 * @return packet with passenger information
	 */
	public CommonPacket getPassengerPacket(Entity passenger) {
		return PacketType.OUT_ENTITY_ATTACH.newInstance(passenger, entity.getEntity());
	}

	/**
	 * Gets a new packet with vehicle information for this Entity
	 * 
	 * @param vehicle this Entity is now a passenger of
	 * @return packet with vehicle information
	 */
	public CommonPacket getVehiclePacket(Entity vehicle) {
		return PacketType.OUT_ENTITY_ATTACH.newInstance(entity.getEntity(), vehicle);
	}

	/**
	 * Gets a new packet with head rotation information for this Entity
	 * 
	 * @param headRotation value (protocol value)
	 * @return packet with head rotation information
	 */
	public CommonPacket getHeadRotationPacket(int headRotation) {
		return PacketType.OUT_ENTITY_HEAD_ROTATION.newInstance(entity.getEntity(), (byte) headRotation);
	}

	private int protRot(float rot) {
		return MathUtil.floor(rot * 256.0f / 360.0f);
	}

	private int protLoc(double loc) {
		return this.es.a(loc);
	}
}
