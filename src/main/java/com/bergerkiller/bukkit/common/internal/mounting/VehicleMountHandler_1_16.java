package com.bergerkiller.bukkit.common.internal.mounting;

import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.protocol.PacketType;

public class VehicleMountHandler_1_16 extends VehicleMountHandler_1_9_to_1_15_2 {
    public static final PacketType[] LISTENED_PACKETS = {PacketType.OUT_MOUNT};

    public VehicleMountHandler_1_16(Player player) {
        super(player);
    }
}
