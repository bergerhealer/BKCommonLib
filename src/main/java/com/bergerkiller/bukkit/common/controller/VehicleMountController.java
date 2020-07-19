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
