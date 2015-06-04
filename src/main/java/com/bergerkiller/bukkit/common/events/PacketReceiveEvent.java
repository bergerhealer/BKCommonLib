package com.bergerkiller.bukkit.common.events;

import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import org.bukkit.entity.Player;

public class PacketReceiveEvent extends PacketEvent {

    public PacketReceiveEvent(Player player, CommonPacket packet) {
        super(player, packet);
    }
}
