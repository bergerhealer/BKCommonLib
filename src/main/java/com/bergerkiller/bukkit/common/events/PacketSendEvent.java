package com.bergerkiller.bukkit.common.events;

import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import org.bukkit.entity.Player;

public class PacketSendEvent extends PacketEvent {

    public PacketSendEvent(Player player, CommonPacket packet) {
        super(player, packet);
    }
}
