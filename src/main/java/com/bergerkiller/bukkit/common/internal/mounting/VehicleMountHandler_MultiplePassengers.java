package com.bergerkiller.bukkit.common.internal.mounting;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutMountHandle;

/**
 * Used on MC 1.9 and later, when multiple passengers could be mounted inside a vehicle.
 * The MOUNT packet is used.
 */
public class VehicleMountHandler_MultiplePassengers extends VehicleMountHandler_BaseImpl {

    public VehicleMountHandler_MultiplePassengers(Player player) {
        super(player);
    }

    /**
     * Processes a mount packet before it is sent to the player.
     * Adjustments are made based on the mount rules.
     * 
     * @param packet
     */
    public synchronized void processMountPacket(PacketPlayOutMountHandle packet) {
        SpawnedEntity vehicle = getSpawnedEntity(packet.getEntityId(), false);

        // All passengers specified in the packet, with separate tracking of length/changed
        boolean passsengerIdsChanged = false;
        int[] passengerIds = packet.getMountedEntityIds();
        int passengerIdsLength = passengerIds.length;

        // Remove passenger id values where the passenger has a different vehicle mount set
        for (int i = 0; i < passengerIdsLength;) {
            SpawnedEntity passenger = getSpawnedEntity(passengerIds[i], false);
            if (passenger != null && passenger.vehicleMount != null && passenger.vehicleMount.vehicle != vehicle) {
                passsengerIdsChanged = true;
                passengerIdsLength--;
                for (int j = i; j < passengerIdsLength; j++) {
                    passengerIds[j] = passengerIds[j+1];
                }
            } else {
                i++;
            }
        }

        // Add vehicle mount values that are missing
        if (vehicle != null) {
            mountloop:
            for (Mount mount : vehicle.passengerMounts) {
                if (mount.sent) {
                    int passengerId = mount.passenger.id;
                    for (int j = 0; j < passengerIdsLength; j++) {
                        if (passengerIds[j] == passengerId) {
                            continue mountloop;
                        }
                    }
                    if (passengerIdsLength == passengerIds.length) {
                        passengerIds = Arrays.copyOf(passengerIds, passengerIdsLength+1);
                    }
                    passengerIds[passengerIdsLength++] = passengerId;
                    passsengerIdsChanged = true;
                }
            }
        }

        // Apply
        if (passsengerIdsChanged) {
            if (passengerIds.length == passengerIdsLength) {
                packet.setMountedEntityIds(passengerIds);
            } else {
                packet.setMountedEntityIds(Arrays.copyOf(passengerIds, passengerIdsLength));
            }
        }
    }

    @Override
    protected void onMountReady(Mount mount) {
        sendVehicleMounts(mount.vehicle, true);
    }

    @Override
    protected void onUnmountVehicle(SpawnedEntity vehicle, List<Mount> passengerMounts) {
        sendVehicleMounts(vehicle, true);
    }

    @Override
    protected void onSpawned(SpawnedEntity entity) {
        if (entity.vehicleMount != null && entity.vehicleMount.vehicle.spawned) {
            entity.vehicleMount.sent = true;
            sendVehicleMounts(entity.vehicleMount.vehicle, true);
        }
        for (Mount m : entity.passengerMounts) {
            if (m.passenger.spawned) {
                m.sent = true;
            }
        }
        sendVehicleMounts(entity, false);
    }

    private final void sendVehicleMounts(SpawnedEntity vehicle, boolean sendEmptyList) {
        int[] passengerIds = vehicle.collectSentPassengerIds();
        if (sendEmptyList || passengerIds.length > 0) {
            PacketUtil.queuePacket(getPlayer(), PacketPlayOutMountHandle.createNew(vehicle.id, vehicle.collectSentPassengerIds()));
        }
    }
}
