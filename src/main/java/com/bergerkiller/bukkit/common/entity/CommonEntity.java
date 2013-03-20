package com.bergerkiller.bukkit.common.entity;

import java.util.List;
import java.util.ListIterator;

import org.bukkit.craftbukkit.v1_5_R1.entity.CraftEntity;

import net.minecraft.server.v1_5_R1.Chunk;
import net.minecraft.server.v1_5_R1.Entity;
import net.minecraft.server.v1_5_R1.EntityTrackerEntry;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.ExtendedEntity;
import com.bergerkiller.bukkit.common.controller.DefaultEntityController;
import com.bergerkiller.bukkit.common.controller.DefaultEntityNetworkController;
import com.bergerkiller.bukkit.common.controller.EntityController;
import com.bergerkiller.bukkit.common.controller.EntityNetworkController;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.entity.nms.NMSEntityHook;
import com.bergerkiller.bukkit.common.entity.nms.NMSEntityTrackerEntry;
import com.bergerkiller.bukkit.common.events.EntitySetControllerEvent;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.reflection.classes.EntityTrackerEntryRef;
import com.bergerkiller.bukkit.common.reflection.classes.WorldServerRef;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
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
			result = new DefaultEntityNetworkController<CommonEntity<T>>();
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
	 * {@link com.bergerkiller.bukkit.common.controller.DefaultEntityNetworkController DefaultEntityNetworkController} instance.
	 * 
	 * @param controller to set to
	 */
	public void setNetworkController(EntityNetworkController<CommonEntity<T>> controller) {
		final EntityTracker tracker = WorldUtil.getTracker(getWorld());
		synchronized (tracker.getHandle()) {
			final EntityNetworkController<CommonEntity<T>> oldController = getNetworkController();
			if (oldController == controller) {
				return;
			}
			// Detach previous controller
			if (oldController != null) {
				oldController.bind(null, null);
			}
			if (controller == null) {
				// Stop tracking - nothing special
				tracker.stopTracking(entity);
			} else {
				// Obtain previous and new replacement entry
				final Object oldEntry = tracker.getEntry(entity);
				final Object newEntry;
				if (controller instanceof DefaultEntityNetworkController) {
					if (oldEntry == null) {
						final CommonEntityType type = CommonEntityTypeStore.byEntity(entity);
						newEntry = new EntityTrackerEntry(getHandle(Entity.class), 
								type.networkViewDistance, type.networkUpdateInterval, type.networkIsMobile);
					} else {
						newEntry = oldEntry;
					}
				} else {
					newEntry = new NMSEntityTrackerEntry(entity, controller, oldEntry);
				}
				// Attach (new?) entry to the world
				if (oldEntry != newEntry) {
					tracker.setEntry(entity, newEntry);
				}
				// Attach the data to the controller
				controller.bind(this, newEntry);
			}
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
		if (!isHooked()) {
			// Check whether conversion is allowed
			final String oldInstanceName = oldInstance.getClass().getName();
			if (!oldInstanceName.startsWith(Common.NMS_ROOT)) {
				throw new RuntimeException("Can not assign controllers to a custom Entity Type (" + oldInstanceName + ")");
			}
			final CommonEntityType type = CommonEntityTypeStore.byEntity(entity);
			if (!type.hasHookEntity()) {
				throw new RuntimeException("Entity of type '" + type.entityType + "' has no Controller support!");
			}
			// Respawn the entity and attach the controller
			try {
				// Store the previous Bukkit entity information for later use
				final org.bukkit.entity.Entity oldBukkitEntity = entity;

				// Create a new entity instance and perform data/property transfer
				final Entity newInstance = (Entity) type.createNMSHookEntity();
				type.nmsType.transfer(oldInstance, newInstance);
				oldInstance.dead = true;
				newInstance.dead = false;
				oldInstance.valid = false;
				newInstance.valid = true;

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
					entitiesById.put(newInstance.id, newInstance);
					CommonUtil.nextTick(new Runnable() {
						public void run() {
							entitiesById.put(newInstance.id, newInstance);
						}
					});
				} else {
					entitiesById.put(newInstance.id, newInstance);
				}

				// *** EntityTrackerEntry ***
				final EntityTracker tracker = WorldUtil.getTracker(getWorld());
				Object entry = tracker.getEntry(entity);
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
			} catch (Throwable t) {
				throw new RuntimeException("Failed to set controller:", t);
			}
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
	public void setController(EntityController<CommonEntity<T>> controller) {
		if (!isHooked() && (controller == null || controller instanceof DefaultEntityController)) {
			// No controller is requested - no need to do anything
			return;
		}

		// Prepare the hook
		this.prepareHook();

		// If null, resolve to the default type
		if (controller == null) {
			controller = new DefaultEntityController<CommonEntity<T>>();
		}

		// Event
		CommonUtil.callEvent(new EntitySetControllerEvent(this, controller));

		// Detach the old controller
		final EntityController<CommonEntity<T>> old = getController();
		if (old != null) {
			old.bind(null);
		}

		// Attach the controller
		controller.bind(this);
	}

	/**
	 * Obtains a (new) {@link CommonEntity} instance providing additional methods for the Entity specified.
	 * This method new returns null.
	 * 
	 * @param entity to get a CommonEntity for
	 * @return a (new) CommonEntity instance for the Entity
	 */
	@SuppressWarnings("unchecked")
	public static <T extends org.bukkit.entity.Entity> CommonEntity<T> get(T entity) {
		final Object handle = Conversion.toEntityHandle.convert(entity);
		if (handle instanceof NMSEntityHook) {
			return (CommonEntity<T>) ((NMSEntityHook) handle).getController().getEntity();
		}
		return CommonEntityTypeStore.byNMSEntity(handle).createCommonEntity(entity);
	}
}
