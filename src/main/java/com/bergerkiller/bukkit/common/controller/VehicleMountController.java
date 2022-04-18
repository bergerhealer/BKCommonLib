package com.bergerkiller.bukkit.common.controller;

import org.bukkit.entity.Player;

/**
 * Responsible for mounting and unmounting entities inside vehicles.
 * If the entity is not yet spawned, delays the mount logic until
 * it is. Cross-compatible with all versions of Minecraft.
 */
public interface VehicleMountController {
    /**
     * Gets the player for which this mount handler is used.
     * This is the player receiving the mount packets.
     * 
     * @return player
     */
    Player getPlayer();

    /**
     * Instructs to mount a passenger inside a vehicle. On MC 1.8.8 and before,
     * only one passenger could be inside a vehicle, and this method will return False
     * if mounting was not possible.
     * 
     * @param vehicleEntityId
     * @param passengerEntityId
     * @return True if mounting was possible, False if not
     */
    boolean mount(int vehicleEntityId, int passengerEntityId);

    /**
     * Instructs to unmount a passenger from a vehicle.
     * 
     * @param vehicleEntityId
     * @param passengerEntityId
     */
    void unmount(int vehicleEntityId, int passengerEntityId);

    /**
     * Notifies that an Entity has been removed, and that all active mounts
     * for the entity should be removed. Similar as {@link #clear(int)}, except
     * no packets are sent. This assumes the entity was destroyed.
     * 
     * @param entityId
     */
    void remove(int entityId);

    /**
     * Removes all active mounts for an entity. Removes all instructions of mounting
     * passengers inside the entity as a vehicle, and where the entity is mounted
     * as a passenger in another vehicle entity. This assumes the entity is not removed,
     * and sends mount packets to correct for that. If the entity was removed, use
     * {@link #remove(int)} instead.
     * 
     * @param entityId
     */
    void clear(int entityId);

    /**
     * Gets the Entity ID of the vehicle a passenger is mounted inside of. Returns -1
     * if the passenger isn't inside a vehicle.<br>
     * <br>
     * This method is multithread-safe.
     *
     * @param passengerEntityId ID of the passenger Entity
     * @return Vehicle Entity ID, or -1 if not mounted
     */
    int getVehicle(int passengerEntityId);

    /**
     * Gets the Entity ID's of the passengers mounted inside a vehicle. Returns an
     * empty array if the vehicle has no passengers.<br>
     * <br>
     * This method is multithread-safe.
     *
     * @param vehicleEntityId ID of the vehicle Entity
     * @return Array of passenger Entity ID's
     */
    int[] getPassengers(int vehicleEntityId);

    /**
     * Despawns an Entity and prevents the Entity from being spawned by the server until
     * {@link #respawn(int, RespawnFunction)} is called. If the Entity was already spawned,
     * the entity is despawned right away.
     * 
     * @param entityId
     */
    void despawn(int entityId);

    /**
     * Respawns an Entity previous hidden using despawn. Only calls the respawn function
     * if without ever calling despawn the entity would have existed otherwise. If the server
     * already despawned the entity while in the despawned state, nothing happens.
     * 
     * @param entityId The id of the entity to respawn
     * @param respawnFunction Executes the logic for spawning the entity if needed
     */
    void respawn(int entityId, RespawnFunctionWithEntityId respawnFunction);

    /**
     * Starts spectating an entity. Will keep spectating until {@link #stopSpectating(int)}
     * is called with the same entity Id, or until the entity is despawned.<br>
     * <br>
     * An entity can only be spectated once. If spectated a second time, the previous
     * spectator session is cancelled/removed.<br>
     * <br>
     * When stop is called, spectator mode will switch back to the previously spectated entity.
     * Once all spectated entities are exhausted, it switches back to the server-defined entity,
     * or disables spectator mode entirely.
     *
     * @param entityId ID of the entity to spectate
     */
    void startSpectating(int entityId);

    /**
     * Stops spectating an entity that was previously spectated using {@link #startSpectating(int)}.<br>
     * <br>
     * When called, spectator mode will switch back to the previously spectated entity.
     * Once all spectated entities are exhausted, it switches back to the server-defined entity,
     * or disables spectator mode entirely.
     *
     * @param entityId ID of the entity to stop spectating
     */
    void stopSpectating(int entityId);

    /**
     * Starts spectating the new entity while forgetting to spectate the previous. Unless a different entity
     * is being spectated right now, this is equivalent to:
     * <pre>
     * startSpectating(newEntityId);
     * stopSpectating(oldEntityId);
     * </pre>
     * If a different entity is currently spectated, only the underlying history stack is updated.
     * If the old entity is not being spectated at all, then the new entity id is spectated
     * as if {@link #startSpectating(int)} was called.
     *
     * @param oldEntityId The ID of the entity to stop spectating
     * @param newEntityId The ID of the entity to start spectating
     */
    void swapSpectating(int oldEntityId, int newEntityId);

    /**
     * Gets whether this controller is currently spectating an entity. This excludes entities
     * spectated using server/Bukkit mechanics. If true, that means a call to
     * {@link #startSpectating(int)} is still active.
     *
     * @param entityId ID of the entity to check
     * @return True if this entity is being spectated
     */
    boolean isSpectating(int entityId);

    /**
     * Respawns an Entity previous hidden using despawn. Only calls the respawn function
     * if without ever calling despawn the entity would have existed otherwise. If the server
     * already despawned the entity while in the despawned state, nothing happens.
     * 
     * @param entity The entity to respawn
     * @param respawnFunction Executes the logic for spawning the entity if needed
     */
    <T extends org.bukkit.entity.Entity> void respawn(T entity, RespawnFunctionWithEntity<T> respawnFunction);

    /**
     * Respawns an Entity previous hidden using despawn. Only calls the respawn function
     * if without ever calling despawn the entity would have existed otherwise. If the server
     * already despawned the entity while in the despawned state, nothing happens.
     * 
     * @param entityId The id of the entity to respawn
     * @param respawnAction Executes the logic for spawning the entity if needed
     */
    void respawn(int entityId, Runnable respawnAction);

    /**
     * Function parameters for respawning an Entity previously hidden.
     * The input Entity ID is sent as parameter to the respawn function.
     */
    public static interface RespawnFunctionWithEntityId {
        /**
         * Spawns the Entity again
         * 
         * @param viewer Player to which to send the spawn packets
         * @param entityId The ID of the Entity to respawn
         */
        public void respawn(Player viewer, int entityId);
    }

    /**
     * Function parameters for respawning an Entity previously hidden.
     * The input Entity is sent as parameter to the respawn function.
     */
    public static interface RespawnFunctionWithEntity<T extends org.bukkit.entity.Entity> {
        /**
         * Spawns the Entity again
         * 
         * @param viewer Player to which to send the spawn packets
         * @param entityId The ID of the Entity to respawn
         */
        public void respawn(Player viewer, T entity);
    }
}
