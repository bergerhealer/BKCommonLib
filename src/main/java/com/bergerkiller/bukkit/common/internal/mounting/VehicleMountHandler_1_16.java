package com.bergerkiller.bukkit.common.internal.mounting;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.DebugUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayInFlyingHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutEntityTeleportHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutEntityVelocityHandle;

/**
 * Used on MC 1.16 and later, when multiple passengers could be mounted inside a vehicle,
 * but a bug exists where players can exit the mount without the server being able to cancel it.
 * This causes a desynchronization issue. This handler re-mounts the player when the player
 * stops sneaking.<br>
 * <br>
 * While the player presses down the sneak button, the player is teleported to where he would
 * be if inside the vehicle every tick. This behavior can also be turned off.<br>
 * <br>
 * The MOUNT packet is used.
 */
public class VehicleMountHandler_1_16 extends VehicleMountHandler_1_9_to_1_15_2 {
    public static final PacketType[] LISTENED_PACKETS = {PacketType.IN_ENTITY_ACTION, PacketType.IN_STEER_VEHICLE,
            PacketType.IN_POSITION, PacketType.IN_POSITION_LOOK, PacketType.OUT_MOUNT,
            PacketType.OUT_ENTITY_TELEPORT, PacketType.OUT_ENTITY_MOVE, PacketType.OUT_ENTITY_MOVE_LOOK};
    private boolean _is_sneaking;
    private Vector in_pos = null;
    private Vector last_pos = null;
    private final Vector curr_pos = new Vector();
    private int last_sync = -1;
    private int remount_sync = -1;

    public VehicleMountHandler_1_16(CommonPlugin plugin, Player player) {
        super(plugin, player);
        this._is_sneaking = false;
    }

    @Override
    protected boolean isPositionTracked() {
        return _plugin.teleportPlayersToSeat();
    }

    @Override
    protected void onPacketReceive(CommonPacket packet) {
        super.onPacketReceive(packet);
        if (packet.getType() == PacketType.IN_STEER_VEHICLE) {
            if (packet.read(PacketType.IN_STEER_VEHICLE.unmount)) {
                // In 3 ticks, re-mount the player in the vehicle
                this.remount_sync = _currentTick + 4;
            }
        } else if (packet.getType() == PacketType.IN_ENTITY_ACTION) {
            String actionId = ((Enum<?>) packet.read(PacketType.IN_ENTITY_ACTION.actionId)).name();
            if (actionId.equals("PRESS_SHIFT_KEY")) {
                if (!this._is_sneaking) {
                    this.remount_sync = _currentTick + 2;
                }
                this._is_sneaking = true;
                //this.remount_sync = -1;
            } else if (actionId.equals("RELEASE_SHIFT_KEY")) {
                if (this._is_sneaking) {
                    this._is_sneaking = false;
                    this.last_pos = null;
                    this.remount_sync = -1;

                    // Resend all active mounts for this player
                    remount();
                }
            }
        } else if (packet.getType() == PacketType.IN_POSITION || packet.getType() == PacketType.IN_POSITION_LOOK) {
            if (isTeleporting()) {
                PacketPlayInFlyingHandle handle = PacketPlayInFlyingHandle.createHandle(packet.getHandle());
                Vector v = new Vector(handle.getX(), handle.getY(), handle.getZ());
                if (in_pos == null || v.distanceSquared(in_pos) > 1.0) {
                    in_pos = v;
                } else {
                    in_pos.setX(in_pos.getX() + 0.05 * (v.getX() - in_pos.getX()));
                    in_pos.setY(in_pos.getY() + 0.05 * (v.getY() - in_pos.getY()));
                    in_pos.setZ(in_pos.getZ() + 0.05 * (v.getZ() - in_pos.getZ()));
                }
            } else {
                in_pos = null;
            }
        }
    }

    @Override
    public void update() {
        synchronizeAndQueuePackets(() -> {
            if (remount_sync == -1 && isTeleporting()) {
                teleport();
            } else {
                last_pos = null;
                in_pos = null;
            }

            if (remount_sync == _currentTick) {
                remount_sync = -1;
                remount();
            }
        });

        super.update();
    }

    private boolean isTeleporting() {
        return _plugin.teleportPlayersToSeat() && _playerSpawnedEntity.vehicleMount != null && _is_sneaking;
    }

    private void teleport() {
        Vector new_position = this._playerSpawnedEntity.position;
        if (new_position == null) {
            return;
        }

        // When a new position update is sent, restart from the current position
        if (this._playerSpawnedEntity.position_sync != this.last_sync) {
            this.last_sync = this._playerSpawnedEntity.position_sync;
            if (this.last_pos != null) {
                MathUtil.setVector(this.last_pos, this.curr_pos);
            }
        }

        if (last_pos == null) {
            last_pos = new_position.clone();
            MathUtil.setVector(curr_pos, last_pos);
        } else {
            // Figure out sync time by looking at what entity is moving
            SpawnedEntity e;
            for (e = this._playerSpawnedEntity; e.vehicleMount != null; e = e.vehicleMount.vehicle);
            final int sync_time = e.type.moveTicks - 1;

            int elapsed = (_currentTick - this._playerSpawnedEntity.position_sync);
            if (elapsed >= sync_time) {
                // Set to final position
                MathUtil.setVector(curr_pos, new_position);
            } else {
                // Interpolate
                double t = (double) (1+elapsed) / (double) (sync_time);
                curr_pos.setX(MathUtil.lerp(last_pos.getX(), new_position.getX(), t));
                curr_pos.setY(MathUtil.lerp(last_pos.getY(), new_position.getY(), t));
                curr_pos.setZ(MathUtil.lerp(last_pos.getZ(), new_position.getZ(), t));
            }
        }

        if (in_pos != null) {
            Vector diff = curr_pos.clone().subtract(in_pos);
            diff.setY(diff.getY() + DebugUtil.getDoubleValue("f", 0.01));
            MathUtil.setVector(in_pos, curr_pos);

            if (diff.lengthSquared() > (2.0*2.0)) {
                Location eye = getPlayer().getEyeLocation();
                queuePacket(PacketPlayOutEntityTeleportHandle.createNew(getPlayer().getEntityId(),
                        curr_pos.getX(), curr_pos.getY(), curr_pos.getZ(),
                        eye.getYaw(), eye.getPitch(), false));
            } else {
                queuePacket(PacketPlayOutEntityVelocityHandle.createNew(getPlayer().getEntityId(),
                        diff.getX(), diff.getY(), diff.getZ()));
            }
        }
    }

    private void remount() {
        if (!this._is_sneaking && this._playerSpawnedEntity.vehicleMount != null) {
            onMountReady(this._playerSpawnedEntity.vehicleMount);
        }
    }
}
