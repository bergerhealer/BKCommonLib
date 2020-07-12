package com.bergerkiller.bukkit.common.internal.mounting;

import java.util.List;

import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutAttachEntityHandle;

/**
 * Used on MC 1.8.8 and before, when only a single passenger per vehicle was possible.
 * The ATTACH packet is used.
 */
public class VehicleMountHandler_1_8_to_1_8_8 extends VehicleMountHandler_BaseImpl {
    public static final PacketType[] LISTENED_PACKETS = {PacketType.OUT_ENTITY_ATTACH};

    public VehicleMountHandler_1_8_to_1_8_8(CommonPlugin plugin, Player player) {
        super(plugin, player);
    }

    /**
     * Processes an attach entity packet before it is sent to the player.
     * Adjustments are made based on the mount rules.
     * Leash update packets should not be used.
     * 
     * @param packet
     */
    private void processAttachEntityPacket(PacketPlayOutAttachEntityHandle packet) {
        SpawnedEntity passenger = getSpawnedEntity(packet.getPassengerId(), false);
        SpawnedEntity vehicle = getSpawnedEntity(packet.getVehicleId(), false);
        if (passenger != null && passenger.vehicleMount != null) {
            // Correct vehicle mount
            if (passenger.vehicleMount.vehicle != vehicle) {
                packet.setVehicleId(passenger.vehicleMount.vehicle.id);
            }
        } else if (vehicle != null && !vehicle.passengerMounts.isEmpty()) {
            // Correct set passengers
            packet.setPassengerId(vehicle.passengerMounts.get(0).passenger.id);
        }
    }

    @Override
    protected void onMountReady(Mount mount) {
        sendAttach(mount.vehicle, mount.passenger);
    }

    @Override
    protected void onUnmountVehicle(SpawnedEntity vehicle, List<Mount> passengerMounts) {
        for (Mount m : passengerMounts) {
            sendAttach(null, m.passenger);
        }
    }

    @Override
    protected void onSpawned(SpawnedEntity entity) {
        if (entity.vehicleMount != null && entity.vehicleMount.vehicle.spawned) {
            entity.vehicleMount.sent = true;
            sendAttach(entity.vehicleMount.vehicle, entity);
        }
        if (!entity.passengerMounts.isEmpty()) {
            Mount m = entity.passengerMounts.get(0);
            if (m.passenger.spawned) {
                m.sent = true;
                sendAttach(entity, m.passenger);
            }
        }
    }

    @Override
    protected void onPacketReceive(CommonPacket packet) {
    }

    @Override
    protected void onPacketSend(CommonPacket packet) {
        if (packet.getType() == PacketType.OUT_ENTITY_ATTACH) {
            PacketPlayOutAttachEntityHandle packet_ae = PacketPlayOutAttachEntityHandle.createHandle(packet.getHandle());
            if (!packet_ae.isLeash()) {
                processAttachEntityPacket(packet_ae);
            }
        }
    }

    private final void sendAttach(SpawnedEntity vehicle, SpawnedEntity passenger) {
        PacketPlayOutAttachEntityHandle attach = PacketPlayOutAttachEntityHandle.T.newHandleNull();
        attach.setVehicleId((vehicle == null) ? -1 : vehicle.id);
        attach.setPassengerId(passenger.id);
        queuePacket(attach);
    }
}
