package com.bergerkiller.bukkit.common.internal.mounting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.controller.VehicleMountController;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.Dimension;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutMountHandle;

/**
 * Base implementation for vehicle mount handlers
 */
public abstract class VehicleMountHandler_BaseImpl implements VehicleMountController {
    public static boolean SUPPORTS_MULTIPLE_PASSENGERS = PacketPlayOutMountHandle.T.isAvailable();
    private final Player _player;
    private final SpawnedEntity _playerSpawnedEntity;
    private Dimension _playerDimension;
    protected IntHashMap<SpawnedEntity> _spawnedEntities;

    public VehicleMountHandler_BaseImpl(Player player) {
        this._player = player;
        this._playerDimension = WorldUtil.getDimension(player.getWorld());
        this._playerSpawnedEntity = new SpawnedEntity(player.getEntityId());
        this._playerSpawnedEntity.spawned = true;
        this._spawnedEntities = new IntHashMap<>();
        this._spawnedEntities.put(this._playerSpawnedEntity.id, this._playerSpawnedEntity);
    }

    @Override
    public final Player getPlayer() {
        return this._player;
    }

    @Override
    public synchronized boolean mount(int vehicleEntityId, int passengerEntityId) {
        SpawnedEntity vehicle = getSpawnedEntity(vehicleEntityId, true);
        SpawnedEntity passenger = getSpawnedEntity(passengerEntityId, true);
        if (vehicle == null || passenger == null) {
            return false;
        }
        Mount mount = Mount.create(vehicle, passenger);
        if (mount == null) {
            return false;
        }
        if (!mount.sent && vehicle.spawned && passenger.spawned) {
            // Mount was not synchronized, but both entities are spawned, so we can synchronize right now
            // Collect all the passenger id's for entities that have spawned and send a mount packet right away
            mount.sent = true;
            onMountReady(mount);
        }
        return true;
    }

    @Override
    public synchronized void unmount(int vehicleEntityId, int passengerEntityId) {
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
        passenger.vehicleMount = null;
        vehicle.removePassengerMount(mount);

        // If mount was sent, synchronize passengers of vehicle again after the change
        if (mount.sent) {
            mount.sent = false;
            onUnmountVehicle(vehicle, Collections.singletonList(mount));
        }
    }

    @Override
    public synchronized void remove(int entityId) {
        SpawnedEntity entity = getSpawnedEntity(entityId, false);
        if (entity != null) {
            clear(entity, false);
        }
    }

    @Override
    public synchronized void clear(int entityId) {
        SpawnedEntity entity = getSpawnedEntity(entityId, false);
        if (entity != null) {
            clear(entity, true);
        }
    }

    private void clear(SpawnedEntity entity, boolean handleUnmount) {
        // Vehicle of entity
        Mount vehicleMount = entity.vehicleMount;
        if (vehicleMount != null) {
            entity.vehicleMount = null;
            vehicleMount.vehicle.removePassengerMount(vehicleMount);
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

    /**
     * Called when a relevant packet is received
     * 
     * @param packet The packet received
     */
    public final synchronized void onPacketReceive(CommonPacket packet) {
        PacketType type = packet.getType();
        if (type == PacketType.OUT_ENTITY_SPAWN) {
            handleSpawn(packet.read(PacketType.OUT_ENTITY_SPAWN.entityId));
        } else if (type == PacketType.OUT_ENTITY_SPAWN_LIVING) {
            handleSpawn(packet.read(PacketType.OUT_ENTITY_SPAWN_LIVING.entityId));
        } else if (type == PacketType.OUT_ENTITY_SPAWN_NAMED) {
            handleSpawn(packet.read(PacketType.OUT_ENTITY_SPAWN_NAMED.entityId));
        } else if (type == PacketType.OUT_ENTITY_DESTROY) {
            for (int entityId : packet.read(PacketType.OUT_ENTITY_DESTROY.entityIds)) {
                handleDespawn(entityId);
            }
        } else if (type == PacketType.OUT_RESPAWN) {
            Dimension dimension = packet.read(PacketType.OUT_RESPAWN.dimension);
            if (dimension != null && !dimension.equals(this._playerDimension)) {
                this._playerDimension = dimension;
                handleReset();
            }
        }  
    }

    private final void handleSpawn(int entityId) {
        SpawnedEntity entity = getSpawnedEntity(entityId, true);
        if (entity == null || entity == this._playerSpawnedEntity) {
            return;
        }
        if (entity.spawned) {
            entity.spawned = false;
            onDespawned(entity);
        }
        entity.spawned = true;
        onSpawned(entity);
    }

    private final void handleDespawn(int entityId) {
        SpawnedEntity entity = getSpawnedEntity(entityId, false);
        if (entity != null && entity.spawned && entity != this._playerSpawnedEntity) {
            entity.spawned = false;
            onDespawned(entity);
            tryRemoveFromTracking(entity);
        }
    }

    private final void handleReset() {
        // Note: values() is a copy and if spawned entities changes, it is not affected
        for (SpawnedEntity entity : this._spawnedEntities.values()) {
            if (entity.vehicleMount != null) {
                entity.vehicleMount.sent = false;
            }
            if (entity != this._playerSpawnedEntity) {
                entity.spawned = false;
                tryRemoveFromTracking(entity);
            }
        }
    }

    /**
     * Called when an Entiy is despawned for the player.
     * This method is already synchronized by {@link #onPacketReceive(packet)}.
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
    }

    private final void tryRemoveFromTracking(SpawnedEntity entity) {
        if (!entity.spawned && entity.vehicleMount == null && entity.passengerMounts.isEmpty()) {
            this._spawnedEntities.remove(entity.id);
        }
    }

    /**
     * Gets the state of a spawned entity by entity id. This stores whether
     * the entity is spawned for the player, and what mounts are active.
     * 
     * @param entityId The entity id to query
     * @param create Whether to create an entry when the entity isn't spawned and no mounts are active
     * @return mounts
     */
    protected final SpawnedEntity getSpawnedEntity(int entityId, boolean create) {
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
    protected final boolean isSpawned(int entityId) {
        SpawnedEntity spawnedEntity = this._spawnedEntities.get(entityId);
        return spawnedEntity != null && spawnedEntity.spawned;
    }

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
     * This method is already synchronized by {@link #onPacketReceive(packet)}.
     * 
     * @param entity
     */
    protected abstract void onSpawned(SpawnedEntity entity);

    /**
     * Metadata of a single spawned entity
     */
    protected static final class SpawnedEntity {
        public final int id;
        public boolean spawned;
        /**
         * Active mounted passengers, where this entity is a vehicle of.
         */
        public List<Mount> passengerMounts;
        /**
         * Active mount with a vehicle, where this entity is a passenger of.
         * Null if this entity is not mounted inside a vehicle.
         */
        public Mount vehicleMount; // Vehicle of this entity

        public SpawnedEntity(int entityId) {
            this.id = entityId;
            this.spawned = false;
            this.passengerMounts = Collections.emptyList();
            this.vehicleMount = null;
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

        public void removePassengerMount(Mount mount) {
            this.passengerMounts = removeFromImmutableList(this.passengerMounts, mount);
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

        @Override
        public String toString() {
            return "{id: " + this.id + "}";
        }
    }

    /**
     * Metadata of a single vehicle-passenger mount pair
     */
    protected static final class Mount {
        public final SpawnedEntity vehicle;
        public final SpawnedEntity passenger;
        public boolean sent;

        private Mount(SpawnedEntity vehicle, SpawnedEntity passenger) {
            this.vehicle = vehicle;
            this.passenger = passenger;
            this.sent = false;
        }

        /**
         * Creates a new mount entry between vehicle and passenger if one does not already exist.
         * If sent is true then the mount was already sent to the player.
         * Returns null if a mount is impossible, because the passenger is already
         * inside a different vehicle, or multiple passengers is not supported.
         * 
         * @param vehicle
         * @param passenger
         * @return Mount
         */
        public static Mount create(SpawnedEntity vehicle, SpawnedEntity passenger) {
            if (passenger.vehicleMount != null) {
                return passenger.vehicleMount.vehicle == vehicle ? passenger.vehicleMount : null;
            } else if (vehicle.passengerMounts.isEmpty()) {
                Mount mount = new Mount(vehicle, passenger);
                passenger.vehicleMount = mount;
                vehicle.passengerMounts = Collections.singletonList(mount);
                return mount;
            } else if (SUPPORTS_MULTIPLE_PASSENGERS) {
                Mount mount = new Mount(vehicle, passenger);
                passenger.vehicleMount = mount;
                if (vehicle.passengerMounts.size() == 1) {
                    vehicle.passengerMounts = new ArrayList<>(2);
                    vehicle.passengerMounts.add(vehicle.passengerMounts.get(0));
                }
                vehicle.passengerMounts.add(mount);
                return mount;
            } else {
                return null; // Multiple passengers supported
            }
        }
    }
}
