package com.bergerkiller.bukkit.common.entity;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.bases.ExtendedEntity;
import com.bergerkiller.bukkit.common.controller.*;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.entity.type.CommonItem;
import com.bergerkiller.bukkit.common.entity.type.CommonLivingEntity;
import com.bergerkiller.bukkit.common.entity.type.CommonPlayer;
import com.bergerkiller.bukkit.common.internal.hooks.EntityHook;
import com.bergerkiller.bukkit.common.internal.hooks.EntityTrackerEntryHook;
import com.bergerkiller.bukkit.common.internal.hooks.EntityTrackerHook;
import com.bergerkiller.bukkit.common.internal.logic.EntityAddRemoveHandler;
import com.bergerkiller.bukkit.common.internal.logic.EntityTypingHandler;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.ChunkUtil;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.EntityUtil;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.utils.PlayerUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.generated.net.minecraft.network.syncher.DataWatcherHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryStateHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerHandle;
import com.bergerkiller.generated.net.minecraft.server.level.WorldServerHandle;
import com.bergerkiller.generated.net.minecraft.world.IInventoryHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.level.WorldHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.entity.CraftEntityHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.inventory.CraftInventoryHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template.Handle;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
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
        if (this.handle.getWorld() == null) {
            return null;
        }
        final EntityNetworkController result;
        final Object entityTrackerEntry = Handle.getRaw(WorldUtil.getTrackerEntry(entity));
        if (entityTrackerEntry == null) {
            return null;
        }
        EntityTrackerEntryHook hook = EntityTypingHandler.INSTANCE.getEntityTrackerEntryHook(entityTrackerEntry);
        if (hook != null) {
            return CommonUtil.unsafeCast(hook.getController());
        }
        if (EntityTrackerEntryHandle.T.isType(entityTrackerEntry)) {
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
        final EntityTrackerEntryHandle storedEntry = tracker.getEntry(entity);

        // Take care of null controllers - stop tracking
        if (controller == null) {
            tracker.stopTracking(entity);
            return;
        }

        // Find a previous network controller that may have been set
        EntityNetworkController oldController = null;
        EntityTrackerEntryHook hook = EntityTypingHandler.INSTANCE.getEntityTrackerEntryHook(Handle.getRaw(storedEntry));
        if (hook != null) {
            oldController = (EntityNetworkController<CommonEntity<org.bukkit.entity.Entity>>) hook.getController();
            if (oldController == controller) {
                return; // No Change!
            }
        }

        // Store the previous viewers set for the entry before we swap the controllers
        // This is required to respawn the entity if so required
        List<Player> previousViewers;
        if (storedEntry != null) {
            previousViewers = new ArrayList<Player>(storedEntry.getViewers());
        } else {
            previousViewers = Collections.emptyList();
        }

        // Hide the entity to all viewers. This will use a previously set controller, if set.
        // The vanilla controller will simply send a destroy packet.
        for (Player previousViewer : previousViewers) {
            storedEntry.removeViewer(previousViewer);
        }

        // Remove this entity from all the viewer's "removeNextTick" lists
        // This prevents the spawned entity despawning again the next tick
        // We fire the destroy packet right away to prevent that.
        for (Player previousViewer : previousViewers) {
            if (PlayerUtil.getEntityRemoveQueue(previousViewer).remove((Object) this.entity.getEntityId())) {
                CommonPacket destroyPacket = PacketType.OUT_ENTITY_DESTROY.newInstanceSingle(this.entity.getEntityId());
                PacketUtil.sendPacket(previousViewer, destroyPacket);
            }
        }

        // Handle the onDetached() of the previous controller, if set
        if (oldController != null) {
            oldController.bind(null, storedEntry.getRaw());
        }

        // Purpur: enable or disable legacy tracking logic when controllers are/not used
        if (EntityHandle.T.setLegacyTrackingEntity.isAvailable()) {
            if (controller instanceof DefaultEntityNetworkController) {
                EntityHandle.T.setLegacyTrackingEntity.invoker.invoke(getHandle(), Boolean.FALSE);
            } else {
                EntityHandle.T.setLegacyTrackingEntity.invoker.invoke(getHandle(), Boolean.TRUE);
            }
        }

        final EntityTrackerEntryHandle newEntry;
        if (controller instanceof DefaultEntityNetworkController) {
            // Assign the default Entity Tracker Entry
            if (EntityTrackerEntryHandle.T.isHandleType(storedEntry)) {
                // Nothing to be done here
                newEntry = storedEntry;
            } else {
                // Create a new unmodified, default server network entry
                newEntry = EntityTypingHandler.INSTANCE.createEntityTrackerEntry(tracker, entity);
                // Transfer data if needed
                if (storedEntry != null) {
                    copyEntityTrackerEntry(storedEntry, newEntry);
                }
            }
        } else if (controller instanceof ExternalEntityNetworkController) {
            // Use the entry as stored by the external network controller
            newEntry = EntityTrackerEntryHandle.createHandle(controller.getHandle());
            // Be sure to refresh stats using the old entry
            if (storedEntry != null && newEntry != null) {
                copyEntityTrackerEntry(storedEntry, newEntry);
            }
        } else if (hook != null) {
            // Use the previous hooked entry - hotswap the controller
            newEntry = storedEntry;
            newEntry.clearViewers();
        } else {
            EntityTrackerEntryHandle oldEntry = storedEntry;
            if (oldEntry == null) {
                oldEntry = EntityTypingHandler.INSTANCE.createEntityTrackerEntry(tracker, entity);
            }

            // Convert the original entry into a hooked entry
            newEntry = EntityTrackerEntryHandle.createHandle(EntityTypingHandler.INSTANCE.hookEntityTrackerEntry(oldEntry.getRaw()));
        }

        // Attach the entry to the controller
        controller.bind(this, newEntry.getRaw());

        // Attach (new?) entry to the world
        if (Handle.getRaw(storedEntry) != Handle.getRaw(newEntry)) {
            tracker.setEntry(entity, newEntry);
        }

        // Make the new controller visible to the previous viewers
        // If this is a new entry entirely, perform a scan
        if (storedEntry != null) {
            for (Player previousViewer : previousViewers) {
                newEntry.updatePlayer(previousViewer);
            }
        } else {
            newEntry.scanPlayers(getWorld().getPlayers());
        }
    }

    private static void copyEntityTrackerEntry(EntityTrackerEntryHandle from, EntityTrackerEntryHandle to) {
        if (EntityTrackerEntryHandle.T.getType() == EntityTrackerEntryStateHandle.T.getType()) {
            // Pre-1.14: same class
            EntityTrackerEntryHandle.T.copyHandle(from, to);
        } else {
            // Post-1.14: got to copy state separately
            // Make sure to preserve the state field in the EntityTrackerEntry!
            EntityTrackerEntryStateHandle to_state = to.getState();
            EntityTrackerEntryHandle.T.copyHandle(from, to);
            EntityTrackerEntryHandle.T.setState.invoke(to.getRaw(), to_state);

            // Copy state too. Preserve the 'broadcast()' lambda, as it refers to the entry we want to keep!
            java.util.function.Consumer<?> to_state_broadcastMethod = EntityTrackerEntryStateHandle.T.broadcastMethod.get(to_state.getRaw());
            EntityTrackerEntryStateHandle.T.copyHandle(from.getState(), to_state);
            EntityTrackerEntryStateHandle.T.broadcastMethod.set(to_state.getRaw(), to_state_broadcastMethod);
        }
    }

    /**
     * Gets the Entity Controller currently assigned to this Entity, checking to make sure
     * the controller is of a certain Class type. When no custom controller is set, or the current
     * controller can not be assigned to the type specified, <i>null</i> is returned instead.<br>
     * <br>
     * This method offers performance benefits over {@link #getController()} by not instantiating a new
     * default controller when no controller is set.
     * 
     * @param controllerType to get
     * @return the controller of controllerType, or <i>null</i> if not found
     */
    @SuppressWarnings("unchecked")
    public <C extends EntityController<?>> C getController(Class<? extends C> controllerType) {
        EntityHook hook = EntityHook.get(getHandle(), EntityHook.class);
        if (hook == null || !hook.hasController()) {
            return null;
        }

        EntityController<?> controller = hook.getController();
        if (controllerType.isAssignableFrom(controller.getClass())) {
            return (C) controller;
        } else {
            return null;
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
            controller.bind(this, false);
        } else if (hook.hasController()) {
            controller = hook.getController();
        } else {
            // This should not occur. Return some dummy controller for now.
            controller = new DefaultEntityController();
            controller.bind(this, false);
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
                old_controller.bind(null, true);
            }
        }

        controller.bind(this, true);
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
        if (handle == null || !type.nmsType.isValid() || !type.nmsType.isType(handle)) {
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

        final Object oldInstance = this.getHandle();

        // Check whether conversion is allowed
        final String oldInstanceName = oldInstance.getClass().getName();
        final CommonEntityType type = CommonEntityType.byEntity(entity);
        if (!type.nmsType.isType(oldInstance)) {
            throw new RuntimeException("Can not assign controllers to a custom Entity Type (" + oldInstanceName + ")");
        }

        // Respawn the entity and attach the controller
        try {
            // Create a new entity instance and perform data/property transfer
            replaceEntity(EntityHandle.createHandle(type.createNMSHookFromEntity(this)));
        } catch (Throwable t) {
            t.printStackTrace();
            throw new RuntimeException("Failed to set controller:", t);
        }
    }

    @SuppressWarnings("unchecked")
    private void replaceEntity(final EntityHandle newInstance) {
        final EntityHandle oldInstance = this.handle;
        if (oldInstance.getRaw() == newInstance.getRaw()) {
            throw new RuntimeException("Can not replace an entity with itself!");
        }

        // Reset entity state
        newInstance.setDead(oldInstance.isDead());
        oldInstance.setDead(true);
        oldInstance.setValid(false);
        newInstance.setValid(true);

        // *** Bukkit Entity ***
        CraftEntityHandle craftEntity = CraftEntityHandle.createHandle(this.entity);
        craftEntity.setHandle(newInstance);
        if (entity instanceof InventoryHolder) {
            Inventory inv = ((InventoryHolder) entity).getInventory();
            if (CraftInventoryHandle.T.isAssignableFrom(inv)) {
                CraftInventoryHandle cInv = CraftInventoryHandle.createHandle(inv);
                if (IInventoryHandle.T.isAssignableFrom(newInstance.getRaw())) {
                    IInventoryHandle iinvHandle = IInventoryHandle.createHandle(newInstance.getRaw());
                    cInv.setHandleField(iinvHandle);
                }
            }
        }

        // *** Give the old entity a new Bukkit Entity ***
        oldInstance.setBukkitEntityField(CraftEntityHandle.createCraftEntity(Bukkit.getServer(), oldInstance));
        this.handle = newInstance;

        // *** Replace entity in passenger and vehicle fields ***
        EntityHandle vehicle = newInstance.getVehicle();
        if (vehicle != null) {
            List<EntityHandle> passengers = new ArrayList<EntityHandle>(vehicle.getPassengers());
            replaceInList(passengers, newInstance);
            vehicle.setPassengers(passengers);
        }
        for (EntityHandle passenger : newInstance.getPassengers()) {
            if (oldInstance.getRaw() == passenger.getVehicle().getRaw()) {
                passenger.setVehicle(newInstance);
            }
        }

        // *** DataWatcher field of the old Entity ***
        Object dataWatcher = EntityHandle.T.datawatcherField.raw.get(newInstance.getRaw());
        if (dataWatcher != null) {
            DataWatcherHandle.T.owner.set(dataWatcher, newInstance);
        }

        // *** Perform further replacement all over the place in the server ***
        final World world = this.getWorld();
        EntityAddRemoveHandler.INSTANCE.replace(world, oldInstance, newInstance);

        // *** Repeat the replacement in the server the next tick to make sure nothing lingers ***
        CommonUtil.nextTick(() -> EntityAddRemoveHandler.INSTANCE.replace(world, oldInstance, newInstance));

        // *** Make sure a controller is set when hooked ***
        if (this.isHooked()) {
            DefaultEntityController controller = new DefaultEntityController();
            controller.bind(this, true);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static boolean replaceInList(List list, EntityHandle entity) {
        if (list == null) {
            return false;
        }
        ListIterator<Object> iter = list.listIterator();
        while (iter.hasNext()) {
            Object obj = iter.next();
            if (obj instanceof EntityHandle) {
                EntityHandle obj_e = (EntityHandle) obj;
                if (obj_e.getIdField() == entity.getIdField()) {
                    iter.set(entity);
                }
            } else if (EntityHandle.T.isAssignableFrom(obj)) {
                int obj_id = EntityHandle.T.idField.getInteger(obj);
                if (obj_id == entity.getIdField()) {
                    iter.set(entity.getRaw());
                }
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
                ChunkUtil.removeEntity(chunk, entity);
            }
        }
        // Add to the new chunk
        if (!isLoaded || changedChunks) {
            final org.bukkit.Chunk chunk = WorldUtil.getChunk(world, newcx, newcz);
            if (isLoaded = chunk != null) {
                ChunkUtil.addEntity(chunk, entity);
            }
            this.handle.setIsLoaded(isLoaded);
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
                    EntityHandle.fromBukkit(passenger).onTick();
                    get(passenger).doPostTick();
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
        final List<org.bukkit.entity.Entity> passengers = getPassengers();
        final WorldHandle newworld = WorldHandle.fromBukkit(location.getWorld());
        final boolean isWorldChange = !this.handle.getWorld().equals(newworld);
        final EntityNetworkController<?> oldNetworkController = getNetworkController();
        final boolean hasNetworkController = !(oldNetworkController instanceof DefaultEntityNetworkController);
        WorldUtil.loadChunks(location, 3);

        // If in a vehicle, make sure we eject first
        if (isInsideVehicle()) {
            ExtendedEntity<Entity> extVeh = new ExtendedEntity<Entity>(getVehicle());
            if (!extVeh.removePassenger(entity)) {
                return false;
            }
        }

        // If vehicle, eject the passenger first
        if (hasPassenger()) {
            this.setPassengersSilent(Collections.<org.bukkit.entity.Entity>emptyList());
        }

        // Perform actual teleportation
        final boolean succ;
        if (entity instanceof Player) {
            // No special logic here. Just teleport.
            succ = entity.teleport(location, cause);
        } else if (!isWorldChange) {
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

            // Teleport the non-player entity
            succ = entity.teleport(location, cause);

            // Start tracking the entity again
            if (!hasNetworkController && !isWorldChange) {
                tracker.startTracking(entity);
            }
        } else {
            // Remove from one world and add to the other
            this.handle.getWorldServer().removeEntity(this.handle);
            this.handle.setDead(false);
            this.handle.setWorld(newworld);
            this.handle.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

            if (hasNetworkController) {
                // Hook the entity tracker to cancel track() on this Entity
                Object nmsWorldHandle = HandleConversion.toWorldHandle(location.getWorld());
                EntityTrackerHook hook = hookWorldEntityTracker(nmsWorldHandle);
                hook.ignoredEntities.add(this.handle.getRaw());

                try {
                    // Spawn the entity. This will not create an entity tracker entry.
                    EntityUtil.addEntity(entity);
                } finally {
                    // Remove the entity from the ignore list, restore Entity Tracker entry if last entity
                    hook.ignoredEntities.remove(this.handle.getRaw());
                    unhookWorldEntityTracker(nmsWorldHandle, hook);
                }
            } else {
                // Simply add the entity, which will register a (default) network controller
                EntityUtil.addEntity(entity);
            }
            succ = true;
        }

        // If there was a passenger, teleport it and let passenger enter again
        if (succ && !passengers.isEmpty()) {
            // Teleport the passenger, but ignore the chunk send check so vehicle is properly spawned to all players
            List<org.bukkit.entity.Entity> teleportedPassengers = new ArrayList<org.bukkit.entity.Entity>();
            this.handle.setIgnoreChunkCheck(true);

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
            this.handle.setIgnoreChunkCheck(false);

            if (!teleportedPassengers.isEmpty()) {
                if (hasNetworkController) {
                    // network controller is used; simply set the passengers of the entity handle
                    List<EntityHandle> passengerHandles = new ArrayList<EntityHandle>(teleportedPassengers.size());
                    for (org.bukkit.entity.Entity passenger : teleportedPassengers) {
                        EntityHandle phandle = EntityHandle.fromBukkit(passenger);
                        passengerHandles.add(phandle);
                        phandle.setVehicle(this.handle);
                    }
                    this.handle.setPassengers(passengerHandles);
                } else {
                    setPassengersSilent(teleportedPassengers);
                }
            }
        }

        // Set network controller last, to make sure passenger mounting packets are sent properly
        if (hasNetworkController) {
            setNetworkController(oldNetworkController);
        }

        return succ;
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
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static final CommonEntity spawn(EntityType entityType, Location location, EntityController controller, EntityNetworkController networkController) {
        CommonEntityType type = CommonEntityType.byEntityType(entityType);
        if (type == CommonEntityType.UNKNOWN) {
            throw new IllegalArgumentException("The Entity Type '" + entityType + "' is invalid!");
        }

        // Create the common entity and Entity NMS handle to add to the server
        final CommonEntity<org.bukkit.entity.Entity> entity = type.createNMSHookEntity(location);

        // Set controller before spawning
        controller.bind(entity, false);

        // When setting a custom entity network controller, avoid the creation of a default network controller
        // We do this by temporarily hooking the EntityTracker, cancelling track() for the entity we don't want
        if (networkController != null && !(networkController instanceof DefaultEntityNetworkController)) {
            // Purpur: enable legacy tracking logic when controllers are used
            if (EntityHandle.T.setLegacyTrackingEntity.isAvailable()) {
                EntityHandle.T.setLegacyTrackingEntity.invoker.invoke(entity.getHandle(), Boolean.TRUE);
            }

            // Hook the entity tracker to cancel track() on this Entity
            Object nmsWorldHandle = HandleConversion.toWorldHandle(location.getWorld());
            EntityTrackerHook hook = hookWorldEntityTracker(nmsWorldHandle);
            hook.ignoredEntities.add(entity.getHandle());

            try {
                // Spawn the entity. This will not create an entity tracker entry.
                EntityUtil.addEntity(entity.getEntity());

                // This is why we use bind(entity, false)!
                // Fire onAttached after having added the Entity to the world
                entity.getController().onAttached();

                // Set the network controller
                entity.setNetworkController(networkController);
                // entity.getNetworkController().onAttached(); // Not needed, the setNetworkController() above does this
            } finally {
                // Remove the entity from the ignore list, restore Entity Tracker entry if last entity
                hook.ignoredEntities.remove(entity.getHandle());
                unhookWorldEntityTracker(nmsWorldHandle, hook);
            }


        } else {
            // Simply spawn. No special things to do here.
            EntityUtil.addEntity(entity.getEntity());

            // This is why we use bind(entity, false)!
            // Fire onAttached after having added the Entity to the world
            entity.getController().onAttached();
        }

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
        final Object handle = HandleConversion.toEntityHandle(entity);
        if (handle == null) {
            return null;
        }

        EntityHook hook = EntityHook.get(handle, EntityHook.class);
        if (hook != null && hook.hasController()) {
            return (CommonEntity<T>) hook.getController().getEntity();
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

        // Unhook network controller
        {
            EntityNetworkController<?> controller = commonEntity.getNetworkController();
            if (controller != null && !(controller instanceof DefaultEntityNetworkController)) {
                commonEntity.setNetworkController(new DefaultEntityNetworkController());
            }
        }

        // Detach controller and undo hook Entity replacement
        // Must be done after the network controller, otherwise messy things happen!
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
                commonEntity.replaceEntity(EntityHandle.createHandle(newInstance));
            } catch (Throwable t) {
                Logging.LOGGER.log(Level.SEVERE, "Failed to unhook Common Entity Controller:");
                t.printStackTrace();
            }
        }
    }

    /*
     * Hooks the world's EntityTracker temporarily for the sole purpose of blocking calls to track()
     */
    private static EntityTrackerHook hookWorldEntityTracker(Object nmsWorldHandle) {
        Object nmsEntityTrackerHandle = WorldServerHandle.T.getEntityTrackerHandle.invoke(nmsWorldHandle);
        EntityTrackerHook hook = EntityTrackerHook.get(nmsEntityTrackerHandle, EntityTrackerHook.class);
        if (hook == null) {
            hook = new EntityTrackerHook(nmsEntityTrackerHandle);
            WorldServerHandle.T.setEntityTrackerHandle.invoke(nmsWorldHandle, hook.hook(nmsEntityTrackerHandle));
        }
        return hook;
    }

    /*
     * Unhooks the world's EntityTracker again if no more entity tracks must be ignored
     */
    private static void unhookWorldEntityTracker(Object nmsWorldHandle, EntityTrackerHook hook) {
        if (hook.ignoredEntities.isEmpty()) {
            WorldServerHandle.T.setEntityTrackerHandle.invoke(nmsWorldHandle, hook.original);

            // For Minecraft 1.14 and later:
            // EntityTracker is the PlayerChunkMap on this version of Minecraft.
            // VisibleChunks stores a cloned copy of all updating PlayerChunk instances on the world.
            // If for some reason this EntityTracker is not garbage collected, keeping this around
            // can cause a memory leak of varying severity.
            // To completely prevent that from happening, set this field to equal updatingChunks, which
            // should already equal the same map on the PlayerChunkMap of the world.
            if (EntityTrackerHandle.T.setVisibleChunksToUpdatingChunks.isAvailable()) {
                EntityTrackerHandle.T.setVisibleChunksToUpdatingChunks.invoke(hook.hookedInstance());
            }
        }
    }
}
