package com.bergerkiller.bukkit.common.internal.mounting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.controller.VehicleMountController;
import com.bergerkiller.bukkit.common.entity.CommonEntityType;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.resources.DimensionType;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.utils.PlayerUtil;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutCameraHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutEntityDestroyHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutEntityHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutEntityTeleportHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutMountHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutNamedEntitySpawnHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLivingHandle;

/**
 * Base implementation for vehicle mount handlers
 */
public abstract class VehicleMountHandler_BaseImpl implements VehicleMountController {
    public static boolean SUPPORTS_MULTIPLE_PASSENGERS = PacketPlayOutMountHandle.T.isAvailable();
    private final Player _player;
    protected final CommonPlugin _plugin;
    protected final SpawnedEntity _playerSpawnedEntity;
    private DimensionType _playerDimension;
    protected IntHashMap<SpawnedEntity> _spawnedEntities;
    private final Queue<PacketHandle> _queuedPackets;
    protected int _currentTick = 0;
    private int _vanillaSpectatedEntity;
    private int[] _spectatorStack;

    public VehicleMountHandler_BaseImpl(CommonPlugin plugin, Player player) {
        DimensionType playerDimension = PlayerUtil.getPlayerDimension(player);

        this._plugin = plugin;
        this._player = player;
        this._playerDimension = playerDimension;
        this._playerSpawnedEntity = new SpawnedEntity(player.getEntityId(), CommonEntityType.PLAYER);
        this._playerSpawnedEntity.state = SpawnedEntity.State.SPAWNED;
        this._spawnedEntities = new IntHashMap<>();
        this._spawnedEntities.put(this._playerSpawnedEntity.id, this._playerSpawnedEntity);
        this._queuedPackets = new ConcurrentLinkedQueue<PacketHandle>();
        this._vanillaSpectatedEntity = player.getEntityId();
        this._spectatorStack = new int[0];
    }

    @Override
    public final Player getPlayer() {
        return this._player;
    }

    @Override
    public boolean mount(int vehicleEntityId, int passengerEntityId) {
        return synchronizeAndQueuePackets(() -> {
            SpawnedEntity vehicle = getSpawnedEntity(vehicleEntityId, true);
            SpawnedEntity passenger = getSpawnedEntity(passengerEntityId, true);
            if (vehicle == null || passenger == null) {
                return false;
            }

            // Deal with a previous vehicle
            Mount prevMount = passenger.vehicleMount;
            if (prevMount != null) {
                if (prevMount.vehicle == vehicle) {
                    return true; // Unchanged
                } else {
                    // Remove previous mount
                    if (prevMount.sent) {
                        prevMount.sent = false;
                        onUnmountVehicle(vehicle, Collections.singletonList(prevMount));
                    }
                    prevMount.remove();
                }
            }

            // Create a new mount
            Mount mount;
            if (vehicle.passengerMounts.isEmpty()) {
                mount = new Mount(vehicle, passenger);
                passenger.vehicleMount = mount;
                vehicle.passengerMounts = Collections.singletonList(mount);
            } else if (SUPPORTS_MULTIPLE_PASSENGERS) {
                mount = new Mount(vehicle, passenger);
                passenger.vehicleMount = mount;
                if (vehicle.passengerMounts.size() == 1) {
                    vehicle.passengerMounts = new ArrayList<>(vehicle.passengerMounts);
                }
                vehicle.passengerMounts.add(mount);
            } else {
                return false; // Multiple passengers not supported
            }

            // Send the mount if we can
            if (vehicle.state.isSpawned() && passenger.state.isSpawned()) {
                // Both entities are spawned, so we can synchronize right now
                // Collect all the passenger id's for entities that have spawned and send a mount packet right away
                mount.sent = true;
                onMountReady(mount);
            }
            return true;
        });
    }

    @Override
    public void unmount(int vehicleEntityId, int passengerEntityId) {
        synchronizeAndQueuePackets(() -> {
            SpawnedEntity vehicle = getSpawnedEntity(vehicleEntityId, false);
            SpawnedEntity passenger = getSpawnedEntity(passengerEntityId, false);
            if (vehicle == null || passenger == null) {
                return;
            }
            Mount mount = passenger.vehicleMount;
            if (mount == null || mount.vehicle != vehicle) {
                return;
            }

            // Remove the vehicle mount
            mount.remove();

            // If mount was sent, synchronize passengers of vehicle again after the change
            if (mount.sent) {
                mount.sent = false;
                onUnmountVehicle(vehicle, Collections.singletonList(mount));
            }
        });
    }

    @Override
    public void remove(int entityId) {
        synchronizeAndQueuePackets(() -> {
            SpawnedEntity entity = getSpawnedEntity(entityId, false);
            if (entity != null) {
                clear(entity, false);
            }
        });
    }

    @Override
    public void clear(int entityId) {
        synchronizeAndQueuePackets(() -> {
            SpawnedEntity entity = getSpawnedEntity(entityId, false);
            if (entity != null) {
                clear(entity, true);
            } 
        });
    }

    private void clear(SpawnedEntity entity, boolean handleUnmount) {
        // Vehicle of entity
        Mount vehicleMount = entity.vehicleMount;
        if (vehicleMount != null) {
            entity.vehicleMount.remove();
            if (vehicleMount.sent) {
                vehicleMount.sent = false;
                if (handleUnmount) {
                    onUnmountVehicle(vehicleMount.vehicle, Collections.singletonList(vehicleMount));
                }
            }
            tryRemoveFromTracking(vehicleMount.vehicle);
        }

        // Passengers of entity
        List<Mount> passengerMounts = entity.passengerMounts;
        if (!passengerMounts.isEmpty()) {
            entity.passengerMounts = Collections.emptyList();
            for (Mount m : passengerMounts) {
                m.sent = false;
                m.passenger.vehicleMount = null;
                tryRemoveFromTracking(m.passenger);
            }
            if (handleUnmount) {
                onUnmountVehicle(entity, passengerMounts);
            }
        }

        // Remove from tracking
        tryRemoveFromTracking(entity);
    }

    @Override
    public synchronized int getVehicle(int passengerEntityId) {
        SpawnedEntity spawnedEntity = getSpawnedEntity(passengerEntityId, false);
        return (spawnedEntity == null || spawnedEntity.vehicleMount == null || !spawnedEntity.vehicleMount.sent)
                ? -1 : spawnedEntity.vehicleMount.vehicle.id;
    }

    @Override
    public synchronized int[] getPassengers(int vehicleEntityId) {
        SpawnedEntity spawnedEntity = getSpawnedEntity(vehicleEntityId, false);
        return (spawnedEntity == null)
                ? new int[0] : spawnedEntity.collectSentPassengerIds();
    }

    @Override
    public void despawn(int entityId) {
        synchronizeAndQueuePackets(() -> {
            SpawnedEntity entity = getSpawnedEntity(entityId, true);
            if (entity.state == SpawnedEntity.State.SPAWNED) {
                // Currently spawned, despawn it, track while it is spawned
                entity.state = SpawnedEntity.State.SUPPRESSED_INFLIGHT_BLOCKED;
                queuePacket(PacketPlayOutEntityDestroyHandle.createNewSingle(entityId));
            } else if (entity.state == SpawnedEntity.State.DESPAWNED) {
                // Was despawned, let's keep it that way
                entity.state = SpawnedEntity.State.DESPAWNED_BLOCKED;
            }
        });
    }

    @Override
    public void respawn(int entityId, RespawnFunctionWithEntityId respawnFunction) {
        respawn(entityId, () -> respawnFunction.respawn(this._player, entityId));
    }

    @Override
    public <T extends org.bukkit.entity.Entity> void respawn(T entity, RespawnFunctionWithEntity<T> respawnFunction) {
        respawn(entity.getEntityId(), () -> respawnFunction.respawn(this._player, entity));
    }

    @Override
    public void respawn(int entityId, Runnable respawnAction) {
        synchronizeAndQueuePackets(() -> {
            SpawnedEntity entity = getSpawnedEntity(entityId, true);
            switch (entity.state) {
            case DESPAWNED_BLOCKED:
                // Not currently spawned, there is nothing for us to do
                entity.state = SpawnedEntity.State.DESPAWNED;
                tryRemoveFromTracking(entity);
                break;
            case SUPPRESSED_INFLIGHT_BLOCKED:
                // Destroy packet will be sent in a short time, destroying it all
                // Make sure to switch state to 'SPAWNED' to handle this gracefully
                // The destroy packets will be received, switching it back to DESPAWNED.
                // Right after, the packets sent using the respawnFunction are received,
                // switching it back to SPAWNED
                entity.state = SpawnedEntity.State.SPAWNED;
                respawnAction.run();
                break;
            case SUPPRESSED_BLOCKED:
                // Entity is currently not spawned, but we want it to be
                entity.state = SpawnedEntity.State.DESPAWNED;
                respawnAction.run();
                break;
            default:
                // No despawn() is active, do nothing
                break;
            }
        });
    }

    @Override
    public synchronized boolean isSpectating(int entityId) {
        int len = this._spectatorStack.length;
        while (--len >= 0) {
            if (this._spectatorStack[len] == entityId) {
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized void startSpectating(int entityId) {
        // Append to the end of the list
        int len = this._spectatorStack.length;
        if (len == 0) {
            this._spectatorStack = new int[] { entityId };
        } else if (this._spectatorStack[len - 1] == entityId) {
            return; // No change, don't send a new packet
        } else {
            // Remove duplicate existing entity id. Is rare, so don't care about efficiency.
            removeFromSpectatorStack(entityId);
            len = this._spectatorStack.length;

            // Append to the end of the array
            int[] newArr = Arrays.copyOf(this._spectatorStack, len + 1);
            newArr[len] = entityId;
            this._spectatorStack = newArr;
        }

        // Send a packet to start spectating. Ignore listeners - we don't want to track this as
        // it is not a vanilla 'start spectating' packet.
        PacketUtil.sendPacket(this._player, PacketPlayOutCameraHandle.createNew(entityId), false);
    }

    @Override
    public synchronized void stopSpectating(int entityId) {
        // Stop spectating, spectate a previous entity if changed
        int previousSpectatedEntityId = handleStopSpectating(entityId);
        if (previousSpectatedEntityId != -1) {
            PacketUtil.sendPacket(this._player, PacketPlayOutCameraHandle.createNew(previousSpectatedEntityId), false);
        }
    }

    @Override
    public synchronized void swapSpectating(int oldEntityId, int newEntityId) {
        // Try to replace an existing entry
        int idx = this._spectatorStack.length - 1;
        if (idx >= 0) {
            if (this._spectatorStack[idx] == oldEntityId) {
                if (oldEntityId == newEntityId) {
                    return; // Avoid pointless packet
                }
                this._spectatorStack[idx] = newEntityId;
            } else {
                // Update in the middle of the stack, perhaps?
                // No packet is sent then.
                while (--idx >= 0) {
                    if (this._spectatorStack[idx] == oldEntityId) {
                        this._spectatorStack[idx] = newEntityId;
                        return;
                    }
                }

                // Append to stack, send a packet
                int len = this._spectatorStack.length;
                this._spectatorStack = Arrays.copyOf(this._spectatorStack, len + 1);
                this._spectatorStack[len] = newEntityId;
            }
        }

        // Send a packet to refresh
        PacketUtil.sendPacket(this._player, PacketPlayOutCameraHandle.createNew(newEntityId), false);
    }

    // Stops spectating. Returns the next entity id to spectate, or -1 to do nothing.
    private int handleStopSpectating(int entityId) {
        int len = this._spectatorStack.length;
        if (len == 0) {
            return -1; // Nothing is spectated
        } else if (len == 1) {
            // Special case - spectate the vanilla entity if removed
            if (this._spectatorStack[0] != entityId) {
                return -1;
            }
            this._spectatorStack = new int[0];
            return this._vanillaSpectatedEntity;
        } else if (this._spectatorStack[len - 1] == entityId) {
            // Remove from the end, we must send a packet
            this._spectatorStack = Arrays.copyOf(this._spectatorStack, len - 1);
            return this._spectatorStack[len - 2];
        } else {
            // Try to remove from the middle of the array. Silently
            removeFromSpectatorStack(entityId);
            return -1;
        }
    }

    private void removeFromSpectatorStack(int entityId) {
        int len = this._spectatorStack.length;
        for (int n = len - 1; n >= 0; n--) {
            if (this._spectatorStack[n] == entityId) {
                int[] newArr = new int[len - 1];
                System.arraycopy(this._spectatorStack, 0, newArr, 0, n);
                System.arraycopy(this._spectatorStack, n + 1, newArr, n, len - n - 1);
                this._spectatorStack = newArr;
                return;
            }
        }
    }

    /**
     * Called every tick to perform routine updates
     */
    public void update() {
        _currentTick++;
    }

    /**
     * Call this to handle a relevant packet that was sent from the server to the client
     * 
     * @param packet The packet sent
     */
    public final void handlePacketSend(CommonPacket packet) {
        synchronizeAndQueuePackets(() -> {
            // Refresh player dimension if none could be set (temporary player, pre-join)
            if (this._playerDimension == null) {
                this._playerDimension = PlayerUtil.getPlayerDimension(this._player);
            }

            // Event handler for further implementations
            onPacketSend(packet);

            // Handle packets
            PacketType type = packet.getType();
            if (type == PacketType.OUT_ENTITY_DESTROY) {
                PacketPlayOutEntityDestroyHandle dp = PacketPlayOutEntityDestroyHandle.createHandle(packet.getHandle());
                if (dp.hasMultipleEntityIds()) {
                    for (int entityId : dp.getEntityIds()) {
                        handleDespawn(entityId);
                    }
                } else {
                    handleDespawn(dp.getSingleEntityId());
                }
            } else if (type == PacketType.OUT_RESPAWN) {
                DimensionType dimension;
                try {
                    dimension = packet.read(PacketType.OUT_RESPAWN.dimensionType);
                } catch (IllegalArgumentException ex) {
                    //Logging.LOGGER_NETWORK.log(Level.WARNING, "Failed to decide dimension from respawn packet", ex);
                    dimension = null;
                }
                if (dimension != null && !dimension.equals(this._playerDimension)) {
                    this._playerDimension = dimension;
                    handleReset();
                }
            } else if (type == PacketType.OUT_CAMERA) {
                // Called when vanilla/server/Bukkit starts or stops spectating an Entity
                // We remember this choice for later
                this._vanillaSpectatedEntity = packet.read(PacketType.OUT_CAMERA.entityId);

                // If currently spectating something already, override
                int len = this._spectatorStack.length;
                if (len > 0) {
                    packet.write(PacketType.OUT_CAMERA.entityId, this._spectatorStack[len - 1]);
                }
            } else {
                if (this.isPositionTracked()) {
                    // Also decode position
                    if (type == PacketType.OUT_ENTITY_SPAWN) {
                        PacketPlayOutSpawnEntityHandle handle = PacketPlayOutSpawnEntityHandle.createHandle(packet.getHandle());
                        handleSpawn(handle.getEntityId(), handle.getCommonEntityType(), new Vector(handle.getPosX(), handle.getPosY(), handle.getPosZ()));
                    } else if (type == PacketType.OUT_ENTITY_SPAWN_LIVING) {
                        PacketPlayOutSpawnEntityLivingHandle handle = PacketPlayOutSpawnEntityLivingHandle.createHandle(packet.getHandle());
                        handleSpawn(handle.getEntityId(), handle.getCommonEntityType(), new Vector(handle.getPosX(), handle.getPosY(), handle.getPosZ()));
                    } else if (type == PacketType.OUT_ENTITY_SPAWN_NAMED) {
                        PacketPlayOutNamedEntitySpawnHandle handle = PacketPlayOutNamedEntitySpawnHandle.createHandle(packet.getHandle());
                        handleSpawn(handle.getEntityId(), CommonEntityType.PLAYER, new Vector(handle.getPosX(), handle.getPosY(), handle.getPosZ()));
                    } else if (type == PacketType.OUT_ENTITY_TELEPORT) {
                        PacketPlayOutEntityTeleportHandle handle = PacketPlayOutEntityTeleportHandle.createHandle(packet.getHandle());
                        handleMove(handle.getEntityId(), (position) -> {
                            position.setX(handle.getPosX());
                            position.setY(handle.getPosY());
                            position.setZ(handle.getPosZ());
                        });
                    } else if (type == PacketType.OUT_ENTITY_MOVE || type == PacketType.OUT_ENTITY_MOVE_LOOK) {
                        PacketPlayOutEntityHandle handle = PacketPlayOutEntityHandle.createHandle(packet.getHandle());
                        handleMove(handle.getEntityId(), (position) -> {
                            position.setX(position.getX() + handle.getDeltaX());
                            position.setY(position.getY() + handle.getDeltaY());
                            position.setZ(position.getZ() + handle.getDeltaZ());
                        });
                    }
                } else {
                    // No decoding/tracking of position
                    if (type == PacketType.OUT_ENTITY_SPAWN) {
                        PacketPlayOutSpawnEntityHandle handle = PacketPlayOutSpawnEntityHandle.createHandle(packet.getHandle());
                        handleSpawn(handle.getEntityId(), handle.getCommonEntityType(), null);
                    } else if (type == PacketType.OUT_ENTITY_SPAWN_LIVING) {
                        PacketPlayOutSpawnEntityLivingHandle handle = PacketPlayOutSpawnEntityLivingHandle.createHandle(packet.getHandle());
                        handleSpawn(handle.getEntityId(), handle.getCommonEntityType(), null);
                    } else if (type == PacketType.OUT_ENTITY_SPAWN_NAMED) {
                        PacketPlayOutNamedEntitySpawnHandle handle = PacketPlayOutNamedEntitySpawnHandle.createHandle(packet.getHandle());
                        handleSpawn(handle.getEntityId(), CommonEntityType.PLAYER, null);
                    }
                }
            }
        });
    }

    /**
     * Call this to handle a relevant packet that was received by the server from the client
     * 
     * @param packet The packet received
     */
    public final void handlePacketReceive(CommonPacket packet) {
        synchronizeAndQueuePackets(() -> {
            onPacketReceive(packet); 
        });
    }

    /**
     * Call this to tell the handler it has been removed (player is offline)
     */
    public final void handleRemoved() {
        synchronizeAndQueuePackets(() -> {
            onRemoved();
        });
    }

    // Calls a runnable while synchronized, afterwards flushes all queued packets
    protected final void synchronizeAndQueuePackets(Runnable r) {
        try {
            synchronized (this) {
                r.run();
            }
        } catch (Throwable t) {
            Logging.LOGGER_NETWORK.log(Level.SEVERE, "Error handling vehicle mount packets for player " + this._player.getName(), t);
        }

        PacketHandle p;
        while ((p = this._queuedPackets.poll()) != null) {
            PacketUtil.queuePacket(this._player, p);
        }
    }

    // Calls a method while synchronized, afterwards flushes all queued packets and returns the return value
    protected final <T> T synchronizeAndQueuePackets(Supplier<T> s) {
        T result;
        synchronized (this) {
            result = s.get();
        }

        PacketHandle p;
        while ((p = this._queuedPackets.poll()) != null) {
            PacketUtil.queuePacket(this._player, p);
        }

        return result;
    }

    private final void handleSpawn(int entityId, CommonEntityType type, Vector position) {
        SpawnedEntity entity = getSpawnedEntity(entityId, true);
        if (entity == null || entity == this._playerSpawnedEntity) {
            return;
        }
        if (entity.state == SpawnedEntity.State.SPAWNED) {
            entity.state = SpawnedEntity.State.DESPAWNED;
            onDespawned(entity);
        }
        if (entity.state.isBlocked()) {
            // Cancel the spawn
            // We could perhaps cancel the original spawn packet, but because we
            // use a packet monitor we cannot do that. For a split second the Entity
            // might be visible.
            entity.state = SpawnedEntity.State.SUPPRESSED_INFLIGHT_BLOCKED;
            queuePacket(PacketPlayOutEntityDestroyHandle.createNewSingle(entityId));
        } else {
            // Allow the spawn
            entity.state = SpawnedEntity.State.SPAWNED;
            entity.type = type;
            entity.position = position;
            entity.position_sync = _currentTick;
            onSpawned(entity);
        }
    }

    private final void handleDespawn(int entityId) {
        SpawnedEntity entity = getSpawnedEntity(entityId, false);
        if (entity == null || entity == this._playerSpawnedEntity) {
            return;
        }

        switch (entity.state) {
        case SPAWNED:
            entity.state = SpawnedEntity.State.DESPAWNED;
            onDespawned(entity);
            tryRemoveFromTracking(entity);
            break;
        case SUPPRESSED_INFLIGHT_BLOCKED:
            entity.state = SpawnedEntity.State.SUPPRESSED_BLOCKED;
            break;
        case SUPPRESSED_BLOCKED:
            entity.state = SpawnedEntity.State.DESPAWNED_BLOCKED;
            break;
        default:
            break;
        }
    }

    private final void handleMove(int entityId, Consumer<Vector> modify) {
        SpawnedEntity entity = getSpawnedEntity(entityId, false);
        if (entity != null && (entity.vehicleMount == null || !entity.vehicleMount.sent)) {
            if (entity.position == null) {
                entity.position = new Vector();
            }
            modify.accept(entity.position);
            entity.position_sync = _currentTick;
            entity.propagatePosition();
        }
    }

    private synchronized final void handleReset() {
        // Note: values() is a copy and if spawned entities changes, it is not affected
        for (SpawnedEntity entity : this._spawnedEntities.values()) {
            if (entity.vehicleMount != null) {
                entity.vehicleMount.sent = false;
            }
            if (entity != this._playerSpawnedEntity) {
                if (entity.state.isBlocked()) {
                    entity.state = SpawnedEntity.State.DESPAWNED_BLOCKED;
                } else {
                    entity.state = SpawnedEntity.State.DESPAWNED;
                }
                tryRemoveFromTracking(entity);
            }
        }
    }

    /**
     * Called when an Entiy is despawned for the player.
     * This method is already synchronized by {@link #handlePacketSend(packet)}.
     * 
     * @param entity
     */
    private void onDespawned(SpawnedEntity entity) {
        // Note: No need to send any packets, the vehicle or passenger was removed
        //       Clients automatically clean up vehicle/passenger information when this happens
        if (entity.vehicleMount != null) {
            entity.vehicleMount.sent = false;
        }
        for (Mount mount : entity.passengerMounts) {
            mount.sent = false;
        }

        // If we were spectating this entity in Vanilla, destroying the entity
        // causes spectating to stop. Remember this.
        if (this._vanillaSpectatedEntity == entity.id) {
            this._vanillaSpectatedEntity = this._player.getEntityId();
        }

        // If this entity was spectated before, that means stopSpectating() wasn't called.
        // In that case we call it ourselves, but, because this is inside a packet listener/monitor
        // handler, it is not safe to send packets here. We must do so, synchronous, the next
        // tick.
        // This is generally fine because in perfect code, this logic should never even execute.
        // Might result in two camera packets being sent, but it prevents issues at least.
        //
        // Disabled: by nature of packets being asynchronous, it just didn't work.
        /*
        if (this.handleStopSpectating(entity.id) != -1) {
            CommonUtil.nextTick(() -> {
                synchronized (VehicleMountHandler_BaseImpl.this) {
                    int len = this._spectatorStack.length;
                    int currSpectatedEntity;
                    if (len == 0) {
                        currSpectatedEntity = this._vanillaSpectatedEntity;
                    } else {
                        currSpectatedEntity = this._spectatorStack[len - 1];
                    }
                    PacketUtil.sendPacket(_player, PacketPlayOutCameraHandle.createNew(currSpectatedEntity), false);
                }
            });
        }
        */
    }

    private synchronized final void tryRemoveFromTracking(SpawnedEntity entity) {
        if (entity.state == SpawnedEntity.State.DESPAWNED && entity.vehicleMount == null && entity.passengerMounts.isEmpty()) {
            this._spawnedEntities.remove(entity.id);
        }
    }

    /**
     * Gets the state of a spawned entity by entity id. This stores whether
     * the entity is spawned for the player, and what mounts are active.
     * 
     * @param entityId The entity id to query
     * @param create Whether to create an entry when the entity isn't spawned and no mounts are active
     * @return spawned entity
     */
    protected synchronized final SpawnedEntity getSpawnedEntity(int entityId, boolean create) {
        SpawnedEntity spawnedEntity = this._spawnedEntities.get(entityId);
        if (spawnedEntity == null && create && entityId >= 0) {
            spawnedEntity = new SpawnedEntity(entityId);
            this._spawnedEntities.put(entityId, spawnedEntity);
        }
        return spawnedEntity;
    }

    /**
     * Gets whether a particular entity has been spawned for this player
     * 
     * @param entityId
     * @return True if spawned, False if not
     */
    protected synchronized final boolean isSpawned(int entityId) {
        SpawnedEntity spawnedEntity = this._spawnedEntities.get(entityId);
        return spawnedEntity != null && spawnedEntity.state.isSpawned();
    }

    /**
     * Queues a packet for sending after the handler finished processing
     * 
     * @param packet
     */
    protected final void queuePacket(PacketHandle packet) {
        this._queuedPackets.add(packet);
    }

    /**
     * Called when this handler is removed (player has logged off)
     */
    protected void onRemoved() {}

    /**
     * Called when some or all passengers were removed from a vehicle
     * 
     * @param vehicle The vehicle from which mounts were removed
     * @param passengerMounts A list of mounts for every passenger that was removed
     */
    protected abstract void onUnmountVehicle(SpawnedEntity vehicle, List<Mount> passengerMounts);

    /**
     * Called when a new mount has been added and the passenger and vehicle
     * are spawned, making it possible to send it to the player.
     * 
     * @param mount
     */
    protected abstract void onMountReady(Mount mount);

    /**
     * Called when an Entity is spawned for the player.
     * This method is already synchronized by {@link #handlePacketSend(packet)}.
     * 
     * @param entity
     */
    protected abstract void onSpawned(SpawnedEntity entity);

    /**
     * Called when a packet is sent from the server to the player
     * 
     * @param packet
     */
    protected abstract void onPacketSend(CommonPacket packet);

    /**
     * Called when a packet is received from the player by the server
     * 
     * @param packet
     */
    protected abstract void onPacketReceive(CommonPacket packet);

    /**
     * Gets whether the position of spawned entities are tracked for use in
     * this vehicle mount handler.
     * 
     * @return True if tracked, false otherwise
     */
    protected boolean isPositionTracked() {
        return false;
    }

    /**
     * Metadata of a single spawned entity
     */
    protected static final class SpawnedEntity {
        public final int id;
        public CommonEntityType type;
        public State state;
        /**
         * Active mounted passengers, where this entity is a vehicle of.
         */
        public List<Mount> passengerMounts;
        /**
         * Active mount with a vehicle, where this entity is a passenger of.
         * Null if this entity is not mounted inside a vehicle.
         */
        public Mount vehicleMount; // Vehicle of this entity
        /**
         * Last synchronized position of the entity.
         * Null if not tracked by the vehicle mount handler.
         */
        public Vector position;
        /**
         * Last (server) tick time the position was updated
         */
        public int position_sync;

        public SpawnedEntity(int entityId) {
            this(entityId, CommonEntityType.UNKNOWN);
        }

        public SpawnedEntity(int entityId, CommonEntityType type) {
            this.id = entityId;
            this.state = State.DESPAWNED;
            this.passengerMounts = Collections.emptyList();
            this.vehicleMount = null;
            this.position = null;
            this.position_sync = -1;
            this.type = type;
        }

        /**
         * Applies the new position information of this entity to
         * the passengers of this entity (recursively).
         */
        public void propagatePosition() {
            for (Mount mount : this.passengerMounts) {
                if (mount.sent) {
                    //TODO: Relative position offsets?
                    if (mount.passenger.position == null) {
                        mount.passenger.position = this.position.clone();
                    } else {
                        MathUtil.setVector(mount.passenger.position, this.position);
                    }
                    mount.passenger.position_sync = this.position_sync;
                    mount.passenger.propagatePosition();
                }
            }
        }

        /**
         * Checks all stored mounts of passengers of this entity and collects their
         * id's into a single array. Only passengers where the mount sent is true
         * will be included.
         * 
         * @return sent passenger ids
         */
        public int[] collectSentPassengerIds() {
            int size = this.passengerMounts.size();
            if (size == 0) {
                return new int[0];
            } else if (size == 1) {
                Mount m = this.passengerMounts.get(0);
                return m.sent ? new int[] {m.passenger.id} : new int[0];
            } else {
                ArrayList<SpawnedEntity> sentMounts = new ArrayList<SpawnedEntity>(this.passengerMounts.size());
                for (Mount m : this.passengerMounts) {
                    if (m.sent) {
                        sentMounts.add(m.passenger);
                    }
                }
                int[] result = new int[sentMounts.size()];
                for (int i = 0; i < result.length; i++) {
                    result[i] = sentMounts.get(i).id;
                }
                return result;
            }
        }

        @Override
        public String toString() {
            return "{id: " + this.id + "}";
        }

        public static enum State {
            /** Entity is not spawned (yet) */
            DESPAWNED(false, false),
            /** Entity has been spawned */
            SPAWNED(true, false),
            /** Entity is not spawned (yet) and it is also not allowed to */
            DESPAWNED_BLOCKED(false, true),
            /**
             * Entity is supposed to be spawned, but we do not allow it to be.
             * A packet is in-flight to destroy the Entity.
             */
            SUPPRESSED_INFLIGHT_BLOCKED(false, true),
            /**
             * Entity is supposed to be spawned, but we do not allow it to be.
             * The packet to destroy it has already been sent.
             */
            SUPPRESSED_BLOCKED(false, true);

            private final boolean spawned;
            private final boolean blocked;

            private State(boolean spawned, boolean blocked) {
                this.spawned = spawned;
                this.blocked = blocked;
            }

            /**
             * Whether the Entity is currently spawned or not
             * 
             * @return True if spawned
             */
            public boolean isSpawned() {
                return this.spawned;
            }

            /**
             * Whether spawning of the Entity is blocked by the controller
             * 
             * @return True if blocked
             */
            public boolean isBlocked() {
                return this.blocked;
            }
        }
    }

    /**
     * Metadata of a single vehicle-passenger mount pair
     */
    protected static final class Mount {
        public final SpawnedEntity vehicle;
        public final SpawnedEntity passenger;
        public boolean sent;

        public Mount(SpawnedEntity vehicle, SpawnedEntity passenger) {
            this.vehicle = vehicle;
            this.passenger = passenger;
            this.sent = false;
        }

        /**
         * Removes this mount from the vehicle and sets the passenger vehicle mount to null
         */
        public void remove() {
            this.vehicle.passengerMounts = removeFromImmutableList(this.vehicle.passengerMounts, this);
            if (this.passenger.vehicleMount == this) {
                this.passenger.vehicleMount = null;
            }
        }

        private static <T> List<T> removeFromImmutableList(List<T> list, T value) {
            int size = list.size();
            if (size == 2) {
                if (list.get(0) == value) {
                    return Collections.singletonList(list.get(1));
                } else if (list.get(1) == value) {
                    return Collections.singletonList(list.get(0));
                } else {
                    return list;
                }
            } else if (size > 2) {
                list.remove(value);
                return list;
            } else if (size == 1 && list.get(0) == value) {
                return Collections.emptyList();
            } else {
                return list;
            }
        }
    }
}
