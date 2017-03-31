package com.bergerkiller.bukkit.common.entity;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.bases.ExtendedEntity;
import com.bergerkiller.bukkit.common.controller.*;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.entity.type.CommonItem;
import com.bergerkiller.bukkit.common.entity.type.CommonLivingEntity;
import com.bergerkiller.bukkit.common.entity.type.CommonPlayer;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.internal.hooks.EntityHook;
import com.bergerkiller.bukkit.common.internal.hooks.EntityTrackerHook;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap;
import com.bergerkiller.reflection.net.minecraft.server.NMSDataWatcher;
import com.bergerkiller.reflection.net.minecraft.server.NMSEntity;
import com.bergerkiller.reflection.net.minecraft.server.NMSEntityTracker;
import com.bergerkiller.reflection.net.minecraft.server.NMSEntityTrackerEntry;
import com.bergerkiller.reflection.net.minecraft.server.NMSWorld;
import com.bergerkiller.reflection.net.minecraft.server.NMSWorldServer;
import com.bergerkiller.reflection.org.bukkit.craftbukkit.CBCraftInventory;

import net.minecraft.server.v1_11_R1.Entity;
import net.minecraft.server.v1_11_R1.EntityTrackerEntry;
import net.minecraft.server.v1_11_R1.TileEntity;
import net.minecraft.server.v1_11_R1.World;
import net.minecraft.server.v1_11_R1.IInventory;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftInventory;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

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
        if (NMSEntity.world.get(getHandle()) == null) {
            return null;
        }
        final EntityNetworkController result;
        final Object entityTrackerEntry = WorldUtil.getTrackerEntry(entity);
        if (entityTrackerEntry == null) {
            return null;
        }
        EntityTrackerHook hook = EntityTrackerHook.get(entityTrackerEntry, EntityTrackerHook.class);
        if (hook != null) {
            return CommonUtil.unsafeCast(hook.getController());
        }
        if (EntityTrackerEntry.class.equals(entityTrackerEntry.getClass())) {
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
        EntityTrackerHook hook = EntityTrackerHook.get(storedEntry, EntityTrackerHook.class);
        if (hook != null) {
            final EntityNetworkController<CommonEntity<org.bukkit.entity.Entity>> oldController = (EntityNetworkController<CommonEntity<org.bukkit.entity.Entity>>) hook.getController();
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
            if (NMSEntityTrackerEntry.T.isType(storedEntry)) {
                // Nothing to be done here
                newEntry = storedEntry;
            } else {
                // Create a new unmodified, default server network entry
                newEntry = NMSEntityTracker.createDummyEntry(entity);
                // Transfer data if needed
                if (storedEntry != null) {
                    NMSEntityTrackerEntry.T.transfer(storedEntry, newEntry);
                }
            }
        } else if (controller instanceof ExternalEntityNetworkController) {
            // Use the entry as stored by the external network controller
            newEntry = controller.getHandle();
            // Be sure to refresh stats using the old entry
            if (storedEntry != null && newEntry != null) {
                NMSEntityTrackerEntry.T.transfer(storedEntry, newEntry);
            }
        } else if (hook != null) {
            // Use the previous hooked entry - hotswap the controller
            newEntry = storedEntry;
            NMSEntityTrackerEntry.viewers.get(newEntry).clear();
        } else if (storedEntry != null) {
            // Convert the original entry into a hooked entry
            newEntry = new EntityTrackerHook().hook(storedEntry);
        } else {
            // Create a brand new entry hook from a dummy entry
            newEntry = new EntityTrackerHook().hook(NMSEntityTracker.createDummyEntry(entity));
        }

        // Attach the entry to the controller
        controller.bind(this, newEntry);

        // Attach (new?) entry to the world
        if (storedEntry != newEntry) {
            tracker.setEntry(entity, newEntry);

            // Make sure to update the viewers
            NMSEntityTrackerEntry.scanPlayers(newEntry, getWorld().getPlayers());
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
        EntityHook hook = EntityHook.get(getHandle(), EntityHook.class);
        final EntityController controller;
        if (hook == null) {
            controller = new DefaultEntityController();
            controller.bind(this);
        } else {
            controller = hook.getController();
        }
        return controller;
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

        // if this is a hooked entity, check for an old controller to replace
        if (isHooked()) {
            EntityController old_controller = this.getController();
            if (old_controller != null) {
                old_controller.bind(null);
            }
        }

        controller.bind(this);
    }

    /**
     * Checks whether this particular Entity supports the use of Entity
     * Controllers. If this method returns True,
     * {@link #setController(EntityController)} can be used.<br><br>
     * <p/>
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
        return true;
    }

    /**
     * Checks whether the Entity is a BKCommonLib hook
     *
     * @return True if hooked, False if not
     */
    protected boolean isHooked() {
        return EntityHook.get(getHandle(), EntityHook.class) != null;
    }

    /**
     * Replaces the current entity, if needed, with the BKCommonLib Hook entity
     * type
     */
    protected void prepareHook() {
        if (isHooked()) {
            // Already hooked
            return;
        }

        final Entity oldInstance = getHandle(Entity.class);

        // Check whether conversion is allowed
        final String oldInstanceName = oldInstance.getClass().getName();
        final CommonEntityType type = CommonEntityType.byEntity(entity);
        if (!type.nmsType.isType(oldInstance)) {
            throw new RuntimeException("Can not assign controllers to a custom Entity Type (" + oldInstanceName + ")");
        }

        // Respawn the entity and attach the controller
        try {
            // Create a new entity instance and perform data/property transfer
            replaceEntity((Entity) type.createNMSHookFromEntity(this));
        } catch (Throwable t) {
            t.printStackTrace();
            throw new RuntimeException("Failed to set controller:", t);
        }
    }

    @SuppressWarnings("unchecked")
    private void replaceEntity(final Entity newInstance) {        
        final Entity oldInstance = getHandle(Entity.class);
        if (oldInstance == newInstance) {
            throw new RuntimeException("Can not replace an entity with itself!");
        }

        // Reset entity state
        oldInstance.dead = true;
        newInstance.dead = false;
        oldInstance.valid = false;
        newInstance.valid = true;

        // *** Bukkit Entity ***
        ((CraftEntity) entity).setHandle(newInstance);
        if (entity instanceof InventoryHolder) {
            Inventory inv = ((InventoryHolder) entity).getInventory();
            if (inv instanceof CraftInventory && newInstance instanceof IInventory) {
            	CBCraftInventory.handle.set(inv, newInstance);
            }
        }

        // *** Give the old entity a new Bukkit Entity ***
        NMSEntity.bukkitEntity.set(oldInstance, NMSEntity.createEntity(oldInstance));

        // *** Replace entity in passenger and vehicle fields ***
        Entity vehicle = (Entity) NMSEntity.vehicleField.getInternal(newInstance);
        if (vehicle != null) {
            replaceInList(vehicle.passengers, newInstance);
        }
        if (newInstance.passengers != null) {
            for (Entity passenger : newInstance.passengers) {
                if (NMSEntity.vehicleField.getInternal(passenger) == oldInstance) {
                    NMSEntity.vehicleField.setInternal(passenger, newInstance);
                }
            }
        }

        // *** DataWatcher field of the old Entity ***
        Object dataWatcher = NMSEntity.datawatcher.getInternal(newInstance);
        if (dataWatcher != null) {
            NMSDataWatcher.owner.setInternal(dataWatcher, newInstance);
        }

        // *** Perform further replacement all over the place in the server ***
        replaceEntityInServer(oldInstance, newInstance);

        // *** Repeat the replacement in the server the next tick to make sure nothing lingers ***
        CommonUtil.nextTick(new Runnable() {
            @Override
            public void run() {
                replaceEntityInServer(oldInstance, newInstance);
            }
        });

        // *** Make sure a controller is set when hooked ***
        if (this.isHooked()) {
            DefaultEntityController controller = new DefaultEntityController();
            controller.bind(this);
        }
    }

    /**
     * This should cover the full replacement of an entity in all internal mappings.
     * This includes the chunk, world and network synchronization objects.
     * 
     * @param oldInstance to replace
     * @param newInstance to replace with
     */
    private static void replaceEntityInServer(final Entity oldInstance, final Entity newInstance) {
        // *** Entities By UUID Map ***
        final Map<UUID, Object> entitiesByUUID = NMSWorldServer.entitiesByUUID.get(oldInstance.world);
        entitiesByUUID.put(newInstance.getUniqueID(), newInstance);

        // *** Entities by Id Map ***
        final IntHashMap<Object> entitiesById = NMSWorld.entitiesById.get(oldInstance.world);
        entitiesById.put(newInstance.getId(), newInstance);

        // *** EntityTrackerEntry ***
        replaceInEntityTracker(newInstance.getId(), newInstance);
        if (newInstance.getVehicle() != null) {
            replaceInEntityTracker(newInstance.getVehicle().getId(), newInstance);
        }
        if (newInstance.passengers != null) {
            for (Entity passenger : newInstance.passengers) {
                replaceInEntityTracker(passenger.getId(), newInstance);
            }
        }

        // *** World ***
        replaceInList(newInstance.world.entityList, newInstance);
        // Fixes for PaperSpigot
        // if (!Common.IS_PAPERSPIGOT_SERVER) {
        //     replaceInList(WorldRef.entityRemovalList.get(oldInstance.world), newInstance);
        // }

        // *** Entity Current Chunk ***
        final int chunkX = NMSEntity.chunkX.get(newInstance);
        final int chunkY = NMSEntity.chunkY.get(newInstance);
        final int chunkZ = NMSEntity.chunkZ.get(newInstance);
        final List<Entity>[] entitySlices = newInstance.world.getChunkAt(chunkX, chunkZ).entitySlices;
        if (!replaceInList(entitySlices[chunkY], newInstance)) {
            for (int y = 0; y < entitySlices.length; y++) {
                if (y != chunkY && replaceInList(entitySlices[y], newInstance)) {
                    break;
                }
            }
        }

        // See where the object is still referenced to check we aren't missing any places to replace
        // This is SLOW, do not ever have this enabled on a release version!
        //DebugUtil.logInstances(oldInstance);
    }

    @SuppressWarnings("unchecked")
    private static void replaceInEntityTracker(int entityId, Entity newInstance) {
        final EntityTracker trackerMap = WorldUtil.getTracker(newInstance.world.getWorld());
        Object entry = trackerMap.getEntry(entityId);
        if (entry != null) {
            Entity tracker = (Entity) NMSEntityTrackerEntry.tracker.getInternal(entry);
            if (tracker != null && tracker.getId() == newInstance.getId()) {
                NMSEntityTrackerEntry.tracker.setInternal(entry, newInstance);
            }
            List<Entity> passengers = (List<Entity>) NMSEntityTrackerEntry.passengers.getInternal(entry);
            replaceInList(passengers, newInstance);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static boolean replaceInList(List list, Entity entity) {
        if (list == null) {
            return false;
        }
        ListIterator<Object> iter = list.listIterator();
        while (iter.hasNext()) {
            Object obj = iter.next();
            if (obj instanceof Entity) {
                if (((Entity) obj).getId() == entity.getId()) {
                    iter.set(entity);
                    return true;
                }
            } else if (obj instanceof TileEntity) {
               // CommonPlugin.LOGGER.log(Level.WARNING, "TileEntity is in Entity List!");
            } else {
               // CommonPlugin.LOGGER.log(Level.WARNING, "Invalid Object is in Entity List!");
            }
        }
        return false;
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
            NMSEntity.isLoaded.set(getHandle(), isLoaded);
        }

        // Tick the passenger
        // Automatically regenerate the passenger list if a passenger is found to be unresponsive
        if (isLoaded) {
            List<org.bukkit.entity.Entity> updatedPassengers = null;
            for (org.bukkit.entity.Entity passenger : getPassengers()) {
                if (!passenger.isDead() && passenger.getVehicle() == entity) {
                    if (updatedPassengers != null) {
                        updatedPassengers.add(passenger);
                    }
                    CommonEntity<?> commonPassenger = get(passenger);
                    commonPassenger.getController().onTick();
                    commonPassenger.doPostTick();
                } else {
                    if (updatedPassengers == null) {
                        updatedPassengers = new ArrayList<org.bukkit.entity.Entity>();
                        for (org.bukkit.entity.Entity passedPassenger : getPassengers()) {
                            if (passedPassenger == passenger) {
                                break;
                            }
                            updatedPassengers.add(passedPassenger);
                        }
                    }
                }
            }
            if (updatedPassengers != null) {
                setPassengersSilent(updatedPassengers);
            }
        }
    }

    @Override
    public boolean teleport(Location location, TeleportCause cause) {
        if (isDead()) {
            return false;
        }
        // Preparations prior to teleportation
        final Location oldLocation = entity.getLocation();
        final Entity entityHandle = CommonNMS.getNative(entity);
        final List<org.bukkit.entity.Entity> passengers = getPassengers();
        final World newworld = CommonNMS.getNative(location.getWorld());
        final boolean isWorldChange = entityHandle.world != newworld;
        final EntityNetworkController<?> oldNetworkController = getNetworkController();
        final boolean hasNetworkController = !(oldNetworkController instanceof DefaultEntityNetworkController);
        WorldUtil.loadChunks(location, 3);

        // If in a vehicle, make sure we eject first
        if (isInsideVehicle()) {
            getVehicle().removePassenger(entity);
        }

        // If vehicle, eject the passenger first
        if (hasPassenger()) {
            this.setPassengersSilent(Collections.<org.bukkit.entity.Entity>emptyList());
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
        if (passengers.size() > 0) {
            // Teleport the passenger, but ignore the chunk send check so vehicle is properly spawned to all players
            List<org.bukkit.entity.Entity> teleportedPassengers = new ArrayList<org.bukkit.entity.Entity>();
            NMSEntity.ignoreChunkCheck.set(entityHandle, true);

            float yawChange = location.getYaw() - oldLocation.getYaw();
            float pitchChange = location.getPitch() - oldLocation.getPitch();
            for (org.bukkit.entity.Entity passenger : passengers) {

                // Figure out a suitable location yaw and pitch based on what it was before
                // We must make sure that when players are mounted, they still face the same way relatively
                Location passengerOld = passenger.getLocation();
                Location passengerLoc = location.clone();
                passengerLoc.setYaw(passengerOld.getYaw() + yawChange);
                passengerLoc.setPitch(passengerOld.getPitch() + pitchChange);

                if (get(passenger).teleport(passengerLoc, cause)) {
                    teleportedPassengers.add(passenger);
                }
            };

            NMSEntity.ignoreChunkCheck.set(entityHandle, false);

            if (teleportedPassengers.size() > 0) {
                setPassengersSilent(teleportedPassengers);
            }
        }
        return true;
    }

    /**
     * Creates a new Entity and spawns it at the Location and using the controllers
     * specified.
     *
     * @param entityType to spawn
     * @param location to spawn at
     * @param controller to assign to the Entity after spawning
     * @param networkController to assign to the Entity after spawning
     * @return True if spawning occurred, False if not
     * @see #spawn(Location)
     */
    @SuppressWarnings("rawtypes")
    public static final CommonEntity spawn(EntityType entityType, Location location, EntityController controller, EntityNetworkController networkController) {
        CommonEntityType type = CommonEntityType.byEntityType(entityType);
        if (type == CommonEntityType.UNKNOWN) {
            throw new IllegalArgumentException("The Entity Type '" + entityType + "' is invalid!");
        }

        final CommonEntity<org.bukkit.entity.Entity> entity = type.createNMSHookEntity(location);
        // Set entity position and spawn in the world
        //entity.last.set(entity.loc.set(location));
        //EntityUtil.addEntity(entity.getEntity());

        // Set controllers
        entity.setController(controller);
        entity.setNetworkController(networkController);

        EntityUtil.addEntity(entity.getEntity());

        // Done!
        entity.onSpawn();
        return entity;
    }

    /**
     * Creates a new Entity instance by calling its default World constructor, and initialized position.
     * Unlike {@link #spawn()} this does not spawn the entity in the world, and it will stay detached.
     * 
     * @param entityType to create
     * @param location of the entity
     * @return created entity
     */
    @SuppressWarnings("rawtypes")
    public static final CommonEntity create(EntityType entityType, Location location) {
        CommonEntityType type = CommonEntityType.byEntityType(entityType);
        if (type == CommonEntityType.UNKNOWN) {
            throw new IllegalArgumentException("The Entity Type '" + entityType + "' is invalid!");
        }

        return type.createNMSHookEntity(location);
    }

    /**
     * Creates a new Entity instance without validly constructing it.
     * Please avoid using this as it will cause serious bugs when spawned into a world.
     * 
     * @param entityType type to create
     * @return CommonEntity
     */
    @SuppressWarnings("rawtypes")
    public static final CommonEntity createNull(EntityType entityType) {
        CommonEntityType type = CommonEntityType.byEntityType(entityType);
        if (type == CommonEntityType.UNKNOWN) {
            throw new IllegalArgumentException("The Entity Type '" + entityType + "' is invalid!");
        }
        return type.createCommonEntityNull();
    }

    /**
     * Called after this CommonEntity has spawned
     */
    protected void onSpawn() {
        getController().onAttached();
        getNetworkController().onAttached();
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

        EntityHook hook = EntityHook.get(handle, EntityHook.class);
        if (hook != null) {
            EntityController<?> controller = hook.getController();
            if (controller != null) {
                return (CommonEntity<T>) controller.getEntity();
            }
        }

        return CommonEntityType.byNMSEntity(handle).createCommonEntity(entity);
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
        EntityHook oldHook = EntityHook.get(oldInstance, EntityHook.class);

        // Detach controller and undo hook Entity replacement
        if (oldHook != null) {
            try {
                CommonEntityController<?> controller = oldHook.getController();
                if (controller != null) {
                    controller.onDetached();
                }
            } catch (Throwable t) {
                Logging.LOGGER.log(Level.SEVERE, "Failed to handle controller detachment:");
                t.printStackTrace();
            }
            try {
                // Transfer data and replace
                Object newInstance = EntityHook.unhook(oldInstance);
                commonEntity.replaceEntity((Entity) newInstance);
            } catch (Throwable t) {
                Logging.LOGGER.log(Level.SEVERE, "Failed to unhook Common Entity Controller:");
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
