package com.bergerkiller.bukkit.common.entity;

import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;

import net.minecraft.server.v1_8_R1.Chunk;
import net.minecraft.server.v1_8_R1.Entity;
import net.minecraft.server.v1_8_R1.EntityTrackerEntry;
import net.minecraft.server.v1_8_R1.IInventory;
import net.minecraft.server.v1_8_R1.World;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R1.inventory.CraftInventory;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import com.bergerkiller.bukkit.common.bases.ExtendedEntity;
import com.bergerkiller.bukkit.common.controller.DefaultEntityController;
import com.bergerkiller.bukkit.common.controller.DefaultEntityNetworkController;
import com.bergerkiller.bukkit.common.controller.EntityController;
import com.bergerkiller.bukkit.common.controller.EntityNetworkController;
import com.bergerkiller.bukkit.common.controller.ExternalEntityNetworkController;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.entity.nms.NMSEntityHook;
import com.bergerkiller.bukkit.common.entity.nms.NMSEntityTrackerEntry;
import com.bergerkiller.bukkit.common.entity.type.CommonItem;
import com.bergerkiller.bukkit.common.entity.type.CommonLivingEntity;
import com.bergerkiller.bukkit.common.entity.type.CommonPlayer;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.reflection.SafeField;
import com.bergerkiller.bukkit.common.reflection.classes.EntityPlayerRef;
import com.bergerkiller.bukkit.common.reflection.classes.EntityRef;
import com.bergerkiller.bukkit.common.reflection.classes.EntityTrackerEntryRef;
import com.bergerkiller.bukkit.common.reflection.classes.PlayerConnectionRef;
import com.bergerkiller.bukkit.common.reflection.classes.WorldRef;
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
     * Gets the Entity Network Controller currently assigned to this Entity. If
     * none is available, this method returns Null. If no custom network
     * controller is set, this method returns a new
     * {@link DefaultEntityNetworkController} instance.
     *
     * @return Entity Network Controller, or null if not available
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public EntityNetworkController<CommonEntity<T>> getNetworkController() {
        if (EntityRef.world.getInternal(getHandle()) == null) {
            return null;
        }
        final EntityNetworkController result;
        final Object entityTrackerEntry = WorldUtil.getTrackerEntry(entity);
        if (entityTrackerEntry == null) {
            return null;
        } else if (entityTrackerEntry instanceof NMSEntityTrackerEntry) {
            result = ((NMSEntityTrackerEntry) entityTrackerEntry).getController();
        } else if (EntityTrackerEntry.class.equals(entityTrackerEntry.getClass())) {
            result = new DefaultEntityNetworkController();
            result.bind(this, entityTrackerEntry);
        } else {
            result = new ExternalEntityNetworkController();
            result.bind(this, entityTrackerEntry);
        }
        return result;
    }

    /**
     * Sets an Entity Network Controller for this Entity. To stop tracking this
     * minecart, pass in Null. To default back to the net.minecraft.server
     * implementation, pass in a new {@link DefaultEntityNetworkController}
     * instance.<br>
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
        } else if (controller instanceof ExternalEntityNetworkController) {
            // Use the entry as stored by the external network controller
            newEntry = controller.getHandle();
            // Be sure to refresh stats using the old entry
            if (storedEntry != null && newEntry != null) {
                EntityTrackerEntryRef.TEMPLATE.transfer(storedEntry, newEntry);
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
     * Gets the Entity Controller currently assigned to this Entity. If no
     * custom controller is set, this method returns a new
     * {@link DefaultEntityController} instance.
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
     * Checks whether this particular Entity supports the use of Entity
     * Controllers. If this method returns True,
     * {@link #setController(EntityController)} can be used.<br><br>
     *
     * Note that Entity Network Controllers are always supported.
     *
     * @return True if Entity Controllers are supported, False if not
     */
    public boolean hasControllerSupport() {
        // Check whether already hooked
        if (isHooked()) {
            return true;
        }
        final Object handle = getHandle();
        final CommonEntityType type = CommonEntityType.byNMSEntity(handle);
        // Check whether the handle is not of an external-plugin type
        if (handle == null || !type.nmsType.isType(handle)) {
            return false;
        }
        // Check whether the CommonEntityType supports hooking
        return type.hasNMSEntity();
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
     * Replaces the current entity, if needed, with the BKCommonLib Hook entity
     * type
     */
    protected void prepareHook() {
        final Entity oldInstance = getHandle(Entity.class);
        if (oldInstance instanceof NMSEntityHook) {
            // Already hooked
            return;
        }

        // Check whether conversion is allowed
        final String oldInstanceName = oldInstance.getClass().getName();
        final CommonEntityType type = CommonEntityType.byEntity(entity);
        if (!type.nmsType.isType(oldInstance)) {
            throw new RuntimeException("Can not assign controllers to a custom Entity Type (" + oldInstanceName + ")");
        }
        if (!type.hasNMSEntity()) {
            throw new RuntimeException("Entity of type '" + type.entityType + "' has no Controller support!");
        }
        // Respawn the entity and attach the controller
        try {
            // Create a new entity instance and perform data/property transfer
            Object newInstance = type.createNMSHookEntity(this);
            type.nmsType.transfer(oldInstance, newInstance);
            replaceEntity((Entity) newInstance);
        } catch (Throwable t) {
            throw new RuntimeException("Failed to set controller:", t);
        }
    }

    private void replaceEntity(final Entity newInstance) {
        final Entity oldInstance = getHandle(Entity.class);
        oldInstance.dead = true;
        newInstance.dead = false;
        oldInstance.valid = false;
        newInstance.valid = true;

        // *** Bukkit Entity ***
        ((CraftEntity) entity).setHandle(newInstance);
        if (entity instanceof InventoryHolder) {
            Inventory inv = ((InventoryHolder) entity).getInventory();
            if (inv instanceof CraftInventory && newInstance instanceof IInventory) {
                SafeField.set(inv, "inventory", newInstance);
            }
        }

        // *** Give the old entity a new Bukkit Entity ***
        EntityRef.bukkitEntity.set(oldInstance, EntityRef.createEntity(oldInstance));

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
            if (entitiesById.remove(oldInstance.getId()) == null) {
                CommonUtil.nextTick(new Runnable() {
                    public void run() {
                        entitiesById.put(newInstance.getId(), newInstance);
                    }
                });
            }
            entitiesById.put(newInstance.getId(), newInstance);

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
            replaceInList(oldInstance.world.entityList, newInstance);
            replaceInList(WorldRef.entityRemovalList.get(oldInstance.world), newInstance);

            // *** Chunk ***
            final int chunkY = getChunkY();
            if (!replaceInChunk(chunk, chunkY, newInstance)) {
                for (int y = 0; y < chunk.entitySlices.length; y++) {
                    if (y != chunkY && replaceInChunk(chunk, y, newInstance)) {
                        break;
                    }
                }
            }
        }
    }

    private static boolean replaceInChunk(Chunk chunk, int chunkY, Entity entity) {
        if (replaceInList((List) chunk.entitySlices[chunkY], entity)) {
            // Make field p public in CraftBukkit Source code
            //chunk.p = true;
            return true;
        } else {
            return false;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static boolean replaceInList(List list, Entity entity) {
        ListIterator<Entity> iter = list.listIterator();
        while (iter.hasNext()) {
            if (iter.next().getId() == entity.getId()) {
                iter.set(entity);
                return true;
            }
        }
        return false;
    }

    /**
     * Sets an Entity Controller for this Entity. This method throws an
     * Exception if this kind of Entity is not supported.
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

    /**
     * Performs all the logic normally performed after ticking a single Entity.
     * If onTick is managed externally, this method keeps it compatible. Calling
     * this method results in the entity properly moved between chunks, and
     * other logic.
     */
    public void doPostTick() {
        final int oldcx = getChunkX();
        final int oldcy = getChunkY();
        final int oldcz = getChunkZ();
        final int newcx = loc.x.chunk();
        final int newcy = loc.y.chunk();
        final int newcz = loc.z.chunk();
        final org.bukkit.World world = getWorld();
        final boolean changedChunks = oldcx != newcx || oldcy != newcy || oldcz != newcz;
        boolean isLoaded = this.isInLoadedChunk();

		// Handle chunk/slice movement
        // Remove from the previous chunk
        if (isLoaded && changedChunks) {
            final org.bukkit.Chunk chunk = WorldUtil.getChunk(world, oldcx, oldcz);
            if (chunk != null) {
                WorldUtil.removeEntity(chunk, entity);
            }
        }
        // Add to the new chunk
        if (!isLoaded || changedChunks) {
            final org.bukkit.Chunk chunk = WorldUtil.getChunk(world, newcx, newcz);
            if (isLoaded = chunk != null) {
                WorldUtil.addEntity(chunk, entity);
            }
            EntityRef.isLoaded.set(getHandle(), isLoaded);
        }

        // Tick the passenger
        if (isLoaded && hasPassenger()) {
            final org.bukkit.entity.Entity passenger = getPassenger();
            if (!passenger.isDead() && passenger.getVehicle() == entity) {
                CommonEntity<?> commonPassenger = get(passenger);
                commonPassenger.getController().onTick();
                commonPassenger.doPostTick();
            } else {
                setPassengerSilent(null);
            }
        }
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
        final boolean succ;
        if (!isWorldChange || entity instanceof Player) {
            // First: stop tracking the entity
            final EntityTracker tracker = WorldUtil.getTracker(getWorld());
            tracker.stopTracking(entity);

            // Destroy packets are queued: Make sure to send them RIGHT NOW
            for (Player bukkitPlayer : WorldUtil.getPlayers(getWorld())) {
                CommonPlayer player = get(bukkitPlayer);
                if (player != null) {
                    player.flushEntityRemoveQueue();
                }
            }

            // Teleport
            succ = entity.teleport(location, cause);

            // Start tracking the entity again
            if (!hasNetworkController && !isWorldChange) {
                tracker.startTracking(entity);
            }
        } else {
            // Remove from one world and add to the other
            entityHandle.world.removeEntity(entityHandle);
            entityHandle.dead = false;
            entityHandle.world = newworld;
            entityHandle.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            entityHandle.world.addEntity(entityHandle);
            succ = true;
        }
        if (hasNetworkController) {
            this.setNetworkController(oldNetworkController);
        }
        if (!succ) {
            return false;
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
     * Spawns this Entity at the Location and using the network controller
     * specified.
     *
     * @param location to spawn at
     * @param networkController to assign to the Entity after spawning
     * @return True if spawning occurred, False if not
     * @see #spawn(Location)
     */
    @SuppressWarnings("rawtypes")
    public final boolean spawn(Location location, EntityNetworkController networkController) {
        final boolean spawned = spawn(location);
        this.setNetworkController(networkController);
        return spawned;
    }

    /**
     * Spawns this Entity at the Location specified. Note that if important
     * properties have to be set beforehand, this should be done first. It is
     * recommended to set Entity Controllers before spawning, not after. This
     * method will trigger Entity spawning events. The network controller can
     * ONLY be set after spawning. To be on the safe side, use the Network
     * Controller spawn alternative.
     *
     * @param location to spawn at
     * @return True if the Entity spawned, False if not (and just teleported)
     */
    public boolean spawn(Location location) {
        if (this.isSpawned()) {
            teleport(location);
            return false;
        }

        EntityNetworkController<?> controller = getNetworkController();
        if (controller != null) {
            controller.onAttached();
        }

        last.set(loc.set(location));
        EntityUtil.addEntity(entity);
        // Perform controller attaching
        getController().onAttached();
        getNetworkController().onAttached();
        return true;
    }

    /**
     * Obtains a (new) {@link CommonItem} instance providing additional methods
     * for the Item specified. This method never returns null, unless the input
     * Entity is null.
     *
     * @param item to get a CommonItem for
     * @return a (new) CommonItem instance for the Item
     */
    public static CommonItem get(Item item) {
        return get(item, CommonItem.class);
    }

    /**
     * Obtains a (new) {@link CommonPlayer} instance providing additional
     * methods for the Player specified. This method never returns null, unless
     * the input Entity is null.
     *
     * @param player to get a CommonPlayer for
     * @return a (new) CommonPlayer instance for the Player
     */
    public static CommonPlayer get(Player player) {
        return get(player, CommonPlayer.class);
    }

    /**
     * Obtains a (new) {@link CommonLivingEntity} instance providing additional
     * methods for the Living Entity specified. This method never returns null,
     * unless the input Entity is null.
     *
     * @param livingEntity to get a CommonLivingEntity for
     * @return a (new) CommonLivingEntity instance for the Living Entity
     */
    @SuppressWarnings("unchecked")
    public static <T extends LivingEntity, C extends CommonLivingEntity<? extends T>> C get(T livingEntity) {
        return (C) get(livingEntity, CommonLivingEntity.class);
    }

    /**
     * Obtains a (new) {@link CommonEntity} instance providing additional
     * methods for the Entity specified. A specific CommonEntity extension that
     * best fits the input Entity can be requested using this method. This
     * method never returns null, unless the input Entity is null.
     *
     * @param entity to get a CommonEntity for
     * @param type of CommonEntity to get
     * @return a (new) CommonEntity type requested, or null if casting
     * failed/entity is invalid
     */
    public static <T extends org.bukkit.entity.Entity, C extends CommonEntity<? extends T>> C get(T entity, Class<C> type) {
        return CommonUtil.tryCast(get(entity), type);
    }

    /**
     * Obtains a (new) {@link CommonEntity} instance providing additional
     * methods for the Entity specified. This method never returns null, unless
     * the input Entity is null.
     *
     * @param entity to get a CommonEntity for
     * @return a (new) CommonEntity instance for the Entity
     */
    @SuppressWarnings("unchecked")
    public static <T extends org.bukkit.entity.Entity> CommonEntity<T> get(T entity) {
        if (entity == null) {
            return null;
        }
        final Object handle = Conversion.toEntityHandle.convert(entity);
        if (handle == null) {
            return null;
        }
        if (handle instanceof NMSEntityHook) {
            EntityController<?> controller = ((NMSEntityHook) handle).getController();
            if (controller != null) {
                return (CommonEntity<T>) controller.getEntity();
            }
        }
        return CommonEntityType.byNMSEntity(handle).createCommonEntity(entity);
    }

    /**
     * Creates (but does not spawn) a new Common Entity backed by a proper
     * Entity.
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
            throw new RuntimeException("The Entity Type '" + entityType + "' has no suitable Entity constructor to use!");
        }
        entity.entity = Conversion.toEntity.convert(handle);
        // Create a new CommonEntity and done
        return entity;
    }

    /**
     * Clears possible network or Entity controllers from the Entity. This
     * should be called when a specific Entity should default back to all
     * default behaviours.
     *
     * @param entity to clear the controllers of
     */
    public static void clearControllers(org.bukkit.entity.Entity entity) {
        CommonEntity<?> commonEntity = get(entity);
        Object oldInstance = commonEntity.getHandle();

        // Detach controller and undo hook Entity replacement
        if (oldInstance instanceof NMSEntityHook) {
            try {
                CommonEntityController<?> controller = ((NMSEntityHook) oldInstance).getController();
                if (controller != null) {
                    controller.onDetached();
                }
            } catch (Throwable t) {
                CommonPlugin.LOGGER.log(Level.SEVERE, "Failed to handle controller detachment:");
                t.printStackTrace();
            }
            try {
                CommonEntityType type = CommonEntityType.byNMSEntity(oldInstance);
                // Transfer data and replace
                Object newInstance = type.createNMSEntity();
                type.nmsType.transfer(oldInstance, newInstance);
                commonEntity.replaceEntity((Entity) newInstance);
            } catch (Throwable t) {
                CommonPlugin.LOGGER.log(Level.SEVERE, "Failed to unhook Common Entity Controller:");
                t.printStackTrace();
            }
        }
        // Unhook network controller
        EntityNetworkController<?> controller = commonEntity.getNetworkController();
        if (controller != null && !(controller instanceof DefaultEntityNetworkController)) {
            commonEntity.setNetworkController(new DefaultEntityNetworkController());
        }
    }
}
