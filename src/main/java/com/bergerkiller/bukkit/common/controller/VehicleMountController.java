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
}
