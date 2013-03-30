package com.bergerkiller.bukkit.common.entity;

import java.util.List;
import java.util.ListIterator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_5_R2.CraftServer;
import org.bukkit.craftbukkit.v1_5_R2.entity.CraftEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import net.minecraft.server.v1_5_R2.Chunk;
import net.minecraft.server.v1_5_R2.Entity;
import net.minecraft.server.v1_5_R2.EntityTrackerEntry;
import net.minecraft.server.v1_5_R2.World;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.ExtendedEntity;
import com.bergerkiller.bukkit.common.controller.DefaultEntityController;
import com.bergerkiller.bukkit.common.controller.DefaultEntityNetworkController;
import com.bergerkiller.bukkit.common.controller.EntityController;
import com.bergerkiller.bukkit.common.controller.EntityNetworkController;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.entity.nms.NMSEntityHook;
import com.bergerkiller.bukkit.common.entity.nms.NMSEntityTrackerEntry;
import com.bergerkiller.bukkit.common.entity.type.CommonPlayer;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.reflection.classes.EntityPlayerRef;
import com.bergerkiller.bukkit.common.reflection.classes.EntityRef;
import com.bergerkiller.bukkit.common.reflection.classes.EntityTrackerEntryRef;
import com.bergerkiller.bukkit.common.reflection.classes.PlayerConnectionRef;
import com.bergerkiller.bukkit.common.reflection.classes.WorldServerRef;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap;

/**
 * Wrapper class for additional methods Bukkit can't or doesn't provide.
 * 
 * @param <T> - type of Entity
 */
public class CommonEntity<T extends org.bukkit.entity.Entity> extends ExtendedEntity<T> {
	public CommonEntity(T entity) {
		super(entity);
	}

	/**
	 * Gets the Entity Network Controller currently assigned to this Entity.
	 * If none is available, this method returns Null.
	 * If no custom network controller is set, this method returns a new
	 * {@link com.bergerkiller.bukkit.common.controller.DefaultEntityNetworkController DefaultEntityNetworkController} instance.
	 * 
	 * @return Entity Network Controller, or null if not available
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public EntityNetworkController<CommonEntity<T>> getNetworkController() {
		final EntityNetworkController result;
		final Object entityTrackerEntry = WorldUtil.getTrackerEntry(entity);
		if (entityTrackerEntry instanceof NMSEntityTrackerEntry) {
			result = ((NMSEntityTrackerEntry) entityTrackerEntry).getController();
		} else if (entityTrackerEntry instanceof EntityTrackerEntry) {
			result = new DefaultEntityNetworkController();
			result.bind(this, entityTrackerEntry);
		} else {
			return null;
		}
		return result;
	}

	/**
	 * Sets an Entity Network Controller for this Entity.
	 * To stop tracking this minecart, pass in Null.
	 * To default back to the net.minecraft.server implementation, pass in a new
	 * {@link com.bergerkiller.bukkit.common.controller.DefaultEntityNetworkController DefaultEntityNetworkController} instance.<br>
	 * <br>
	 * This method only works if the Entity world has previously been set.
	 * 
	 * @param controller to set to
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void setNetworkController(EntityNetworkController controller) {
		if (getWorld() == null) {
			throw new RuntimeException("Can not set the network controller when no world is known! (need to spawn it?)");
		}
		final EntityTracker tracker = WorldUtil.getTracker(getWorld());
		final Object storedEntry = tracker.getEntry(entity);

		// Properly handle a previously set controller
		if (storedEntry instanceof NMSEntityTrackerEntry) {
			final EntityNetworkController oldController = ((NMSEntityTrackerEntry) storedEntry).getController();
			if (oldController == controller) {
				return;
			} else if (oldController != null) {
				oldController.onDetached();
			}
		}

		// Take care of null controllers - stop tracking
		if (controller == null) {
			tracker.stopTracking(entity);
			return;
		}

		final Object newEntry;
		if (controller instanceof DefaultEntityNetworkController) {
			// Assign the default Entity Tracker Entry
			if (EntityTrackerEntryRef.TEMPLATE.isType(storedEntry)) {
				// Nothing to be done here
				newEntry = storedEntry;
			} else {
				// Create a new entry
				final CommonEntityType type = CommonEntityType.byEntity(entity);
				newEntry = new EntityTrackerEntry(getHandle(Entity.class), type.networkViewDistance, type.networkUpdateInterval, type.networkIsMobile);
				// Transfer data if needed
				if (storedEntry != null) {
					EntityTrackerEntryRef.TEMPLATE.transfer(storedEntry, newEntry);
				}
			}
		} else {
			// Assign a new Entity Tracker Entry with controller capabilities
			if (storedEntry instanceof NMSEntityTrackerEntry) {
				// Use the previous entry - hotswap the controller
				newEntry = storedEntry;
				EntityTrackerEntryRef.viewers.get(newEntry).clear();
			} else {
				// Create a new entry from scratch
				newEntry = new NMSEntityTrackerEntry(this.getEntity());
				// Transfer possible information over
				if (storedEntry != null) {
					EntityTrackerEntryRef.TEMPLATE.transfer(storedEntry, newEntry);
				}
			}
		}

		// Attach the entry to the controller
		controller.bind(this, newEntry);

		// Attach (new?) entry to the world
		if (storedEntry != newEntry) {
			tracker.setEntry(entity, newEntry);

			// Make sure to update the viewers
			EntityTrackerEntryRef.scanPlayers(newEntry, getWorld().getPlayers());
		}
	}

	/**
	 * Gets the Entity Controller currently assigned to this Entity.
	 * If no custom controller is set, this method returns a new 
	 * {@link com.bergerkiller.bukkit.common.controller.DefaultEntityController DefaultEntityController} instance.
	 * 
	 * @return Entity Controller
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public EntityController<CommonEntity<T>> getController() {
		if (isHooked()) {
			return (EntityController<CommonEntity<T>>) getHandle(NMSEntityHook.class).getController();
		}
		final EntityController controller = new DefaultEntityController();
		controller.bind(this);
		return controller;
	}

	/**
	 * Checks whether this particular Entity supports the use of Entity Controllers.
	 * If this method returns True, {@link setController(controller)} can be used.<br><br>
	 * 
	 * Note that Entity Network Controllers are always supported.
	 * 
	 * @return True if Entity Controllers are supported, False if not
	 */
	public boolean hasControllerSupport() {
		if (isHooked()) {
			return true;
		} else if (getHandle() == null) {
			return false;
		} else {
			return getHandle().getClass().getName().startsWith(Common.NMS_ROOT);
		}
	}

	/**
	 * Checks whether the Entity is a BKCommonLib hook
	 * 
	 * @return True if hooked, False if not
	 */
	protected boolean isHooked() {
		return getHandle() instanceof NMSEntityHook;
	}

	/**
	 * Replaces the current entity, if needed, with the BKCommonLib Hook entity type
	 */
	@SuppressWarnings("unchecked")
	protected void prepareHook() {
		final Entity oldInstance = getHandle(Entity.class);
		if (oldInstance instanceof NMSEntityHook) {
			// Already hooked
			return;
		}

		// Check whether conversion is allowed
		final String oldInstanceName = oldInstance.getClass().getName();
		if (!oldInstanceName.startsWith(Common.NMS_ROOT)) {
			throw new RuntimeException("Can not assign controllers to a custom Entity Type (" + oldInstanceName + ")");
		}
		final CommonEntityType type = CommonEntityType.byEntity(entity);
		if (!type.hasNMSEntity()) {
			throw new RuntimeException("Entity of type '" + type.entityType + "' has no Controller support!");
		}
		// Respawn the entity and attach the controller
		try {
			// Create a new entity instance and perform data/property transfer
			final Entity newInstance = (Entity) type.createNMSHookEntity(this);
			type.nmsType.transfer(oldInstance, newInstance);
			oldInstance.dead = true;
			newInstance.dead = false;
			oldInstance.valid = false;
			newInstance.valid = true;

			// *** Bukkit Entity ***
			((CraftEntity) entity).setHandle(newInstance);

			// *** Give the old entity a new Bukkit Entity ***
			EntityRef.bukkitEntity.set(oldInstance, CraftEntity.getEntity((CraftServer) Bukkit.getServer(), oldInstance));

			// *** Passenger/Vehicle ***
			if (newInstance.vehicle != null) {
				newInstance.vehicle.passenger = newInstance;
			}
			if (newInstance.passenger != null) {
				newInstance.passenger.vehicle = newInstance;
			}

			// Only do this replacement logic for Entities that are already spawned
			if (this.isSpawned()) {
				// Now proceed to replace this NMS Entity in all places imaginable.
				// First load the chunk so we can at least work on something
				Chunk chunk = CommonNMS.getNative(getWorld().getChunkAt(getChunkX(), getChunkZ()));

				// *** Entities By ID Map ***
				final IntHashMap<Object> entitiesById = WorldServerRef.entitiesById.get(oldInstance.world);
				if (entitiesById.remove(oldInstance.id) == null) {
					CommonUtil.nextTick(new Runnable() {
						public void run() {
							entitiesById.put(newInstance.id, newInstance);
						}
					});
				}
				entitiesById.put(newInstance.id, newInstance);

				// *** EntityTrackerEntry ***
				final EntityTracker tracker = WorldUtil.getTracker(getWorld());
				Object entry = tracker.getEntry(entity);
				if (entry != null) {
					EntityTrackerEntryRef.tracker.set(entry, entity);
				}
				if (hasPassenger()) {
					entry = tracker.getEntry(getPassenger());
					if (entry != null) {
						EntityTrackerEntryRef.vehicle.set(entry, entity);
					}
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
			}
		} catch (Throwable t) {
			throw new RuntimeException("Failed to set controller:", t);
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

	/**
	 * Sets an Entity Controller for this Entity.
	 * This method throws an Exception if this kind of Entity is not supported.
	 * 
	 * @param controller to set to
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void setController(EntityController controller) {
		// Prepare the hook
		this.prepareHook();

		// If null, resolve to the default type
		if (controller == null) {
			controller = new DefaultEntityController();
		}
		getController().bind(null);
		controller.bind(this);
	}

	@Override
	public boolean teleport(Location location, TeleportCause cause) {
		if (isDead()) {
			return false;
		}
		// Preparations prior to teleportation
		final Entity entityHandle = CommonNMS.getNative(entity);
		final CommonEntity<?> passenger = get(getPassenger());
		final World newworld = CommonNMS.getNative(location.getWorld());
		final boolean isWorldChange = entityHandle.world != newworld;
		final EntityNetworkController<?> oldNetworkController = getNetworkController();
		final boolean hasNetworkController = !(oldNetworkController instanceof DefaultEntityNetworkController);
		WorldUtil.loadChunks(location, 3);

		// If in a vehicle, make sure we eject first
		if (isInsideVehicle()) {
			getVehicle().eject();
		}

		// If vehicle, eject the passenger first
		if (hasPassenger()) {
			setPassengerSilent(null);
		}

		// Perform actual teleportation
		if (!isWorldChange || entity instanceof Player) {
			// First: stop tracking the entity
			final EntityTracker tracker = WorldUtil.getTracker(getWorld());
			tracker.stopTracking(entity);

			// Destroy packets are queued: Make sure to send them RIGHT NOW
			for (Player bukkitPlayer : WorldUtil.getPlayers(getWorld())) {
				get(bukkitPlayer).flushEntityRemoveQueue();
			}

			// Teleport
			final boolean succ = entity.teleport(location, cause);

			// Start tracking the entity again
			if (!hasNetworkController && !isWorldChange) {
				tracker.startTracking(entity);
			}

			if (!succ) {
				return false;
			}
		} else {
			// Remove from one world and add to the other
			entityHandle.world.removeEntity(entityHandle);
			entityHandle.dead = false;
			entityHandle.world = newworld;
			entityHandle.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
			entityHandle.world.addEntity(entityHandle);
		}
		if (hasNetworkController) {
			this.setNetworkController(oldNetworkController);
		}

		// If there was a passenger, teleport it and let passenger enter again
		if (passenger != null) {
			// Teleport the passenger, but ignore the chunk send check so vehicle is properly spawned to all players
			EntityRef.ignoreChunkCheck.set(entityHandle, true);
			final boolean passengerTeleported = passenger.teleport(location, cause);
			EntityRef.ignoreChunkCheck.set(entityHandle, false);
			if (passengerTeleported) {
				setPassengerSilent(passenger.getEntity());
				// For players, set checkMovement to True - some odd issue
				if (passenger instanceof CommonPlayer) {
					Object connection = EntityPlayerRef.playerConnection.get(passenger.getHandle());
					if (connection != null) {
						PlayerConnectionRef.checkMovement.set(connection, true);
					}
				}
			}
		}
		return true;
	}

	/**
	 * Spawns this Entity at the Location and using the network controller specified.
	 * 
	 * @param location to spawn at
	 * @param networkController to assign to the Entity after spawning
	 * @return True if spawning occurred, False if not
	 * @see {@link spawn(Location location)}
	 */
	@SuppressWarnings("rawtypes")
	public final boolean spawn(Location location, EntityNetworkController networkController) {
		final boolean spawned = spawn(location);
		this.setNetworkController(networkController);
		return spawned;
	}

	/**
	 * Spawns this Entity at the Location specified.
	 * Note that if important properties have to be set beforehand, this should be done first.
	 * It is recommended to set Entity Controllers before spawning, not after.
	 * This method will trigger Entity spawning events.
	 * The network controller can ONLY be set after spawning.
	 * To be on the safe side, use the Network Controller spawn alternative.
	 * 
	 * @param location to spawn at
	 * @return True if the Entity spawned, False if not (and just teleported)
	 */
	public boolean spawn(Location location) {
		if (this.isSpawned()) {
			teleport(location);
			return false;
		}
		last.set(loc.set(location));
		EntityUtil.addEntity(entity);
		// Perform controller attaching
		getController().onAttached();
		getNetworkController().onAttached();
		return true;
	}

	/**
	 * Obtains a (new) {@link CommonPlayer} instance providing additional methods for the Player specified.
	 * This method never returns null, unless the input Entity is null.
	 * 
	 * @param player to get a CommonPlayer for
	 * @return a (new) CommonPlayer instance for the Player
	 */
	public static CommonPlayer get(Player player) {
		return (CommonPlayer) getByEntity(player);
	}

	/**
	 * Obtains a (new) {@link CommonEntity} instance providing additional methods for the Entity specified.
	 * This method never returns null, unless the input Entity is null.
	 * 
	 * @param entity to get a CommonEntity for
	 * @return a (new) CommonEntity instance for the Entity
	 */
	public static <T extends org.bukkit.entity.Entity> CommonEntity<T> get(T entity) {
		return getByEntity(entity);
	}

	@SuppressWarnings("unchecked")
	private static <T extends org.bukkit.entity.Entity> CommonEntity<T> getByEntity(T entity) {
		if (entity == null) {
			return null;
		}
		final Object handle = Conversion.toEntityHandle.convert(entity);
		if (handle instanceof NMSEntityHook) {
			EntityController<?> controller = ((NMSEntityHook) handle).getController();
			if (controller != null) {
				return (CommonEntity<T>) controller.getEntity();
			}
		}
		return CommonEntityType.byNMSEntity(handle).createCommonEntity(entity);
	}

	/**
	 * Creates (but does not spawn) a new Common Entity backed by a proper Entity.
	 * 
	 * @param entityType to create
	 * @return a new CommonEntity type instance
	 */
	public static CommonEntity<?> create(EntityType entityType) {
		CommonEntityType type = CommonEntityType.byEntityType(entityType);
		if (type == CommonEntityType.UNKNOWN) {
			throw new IllegalArgumentException("The Entity Type '" + entityType + "' is invalid!");
		}
		final CommonEntity<org.bukkit.entity.Entity> entity = type.createCommonEntity(null);
		// Spawn a new NMS Entity
		Entity handle;
		if (type.hasNMSEntity()) {
			handle = (Entity) type.createNMSHookEntity(entity);
		} else {
			throw new RuntimeException("The Entity Type '"  + entityType + "' has no suitable Entity constructor to use!");
		}
		entity.entity = Conversion.toEntity.convert(handle);
		// Create a new CommonEntity and done
		return entity;
	}
}
