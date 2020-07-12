package com.bergerkiller.bukkit.common.internal.mounting;

import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;

/**
 * Used on MC 1.16 and later, when multiple passengers could be mounted inside a vehicle,
 * but a bug exists where players can exit the mount without the server being able to cancel it.
 * This causes a desynchronization issue. This handler re-mounts the player when the player
 * stops sneaking.
 * 
 * The MOUNT packet is used.
 */
public class VehicleMountHandler_1_16 extends VehicleMountHandler_1_9_to_1_15_2 {
    public static final PacketType[] LISTENED_PACKETS = {PacketType.IN_ENTITY_ACTION, PacketType.IN_STEER_VEHICLE, PacketType.OUT_MOUNT};
    private boolean _is_sneaking;
    private final Task _remountTask;

    public VehicleMountHandler_1_16(CommonPlugin plugin, Player player) {
        super(plugin, player);
        this._is_sneaking = false;
        this._remountTask = new Task(plugin) {
            @Override
            public void run() {
                synchronizeAndQueuePackets(() -> {
                    remount();
                });
            }
        };
    }

    @Override
    protected void onRemoved() {
        this._remountTask.stop();
    }

    @Override
    protected void onPacketReceive(CommonPacket packet) {
        super.onPacketReceive(packet);
        if (packet.getType() == PacketType.IN_STEER_VEHICLE) {
            if (packet.read(PacketType.IN_STEER_VEHICLE.unmount)) {
                // In 3 ticks, re-mount the player in the vehicle
                this._remountTask.stop();
                this._remountTask.start(4);
            }
        } else if (packet.getType() == PacketType.IN_ENTITY_ACTION) {
            String actionId = ((Enum<?>) packet.read(PacketType.IN_ENTITY_ACTION.actionId)).name();
            if (actionId.equals("PRESS_SHIFT_KEY")) {
                this._is_sneaking = true;
            } else if (actionId.equals("RELEASE_SHIFT_KEY")) {
                if (this._is_sneaking) {
                    this._is_sneaking = false;

                    // Resend all active mounts for this player
                    remount();
                }
            }
        }
    }

    private void remount() {
        if (!this._is_sneaking && this._playerSpawnedEntity.vehicleMount != null) {
            onMountReady(this._playerSpawnedEntity.vehicleMount);
        }
    }
}
