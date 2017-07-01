package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import org.junit.Test;

import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.CommonUtil;

public class PacketTest {

    static {
        CommonUtil.bootstrap();
    }

    @Test
    public void testPacketDirection() {
        assertFalse(PacketType.IN_CHAT.isOutGoing());
        assertFalse(PacketType.IN_STEER_VEHICLE.isOutGoing());
        assertFalse(PacketType.IN_FLYING.isOutGoing());
        assertFalse(PacketType.IN_ENTITY_ANIMATION.isOutGoing());
        assertTrue(PacketType.OUT_BLOCK_CHANGE.isOutGoing());
        assertTrue(PacketType.OUT_CHAT.isOutGoing());
        assertTrue(PacketType.OUT_ENTITY_MOVE.isOutGoing());
        assertTrue(PacketType.OUT_MAP.isOutGoing());
        assertTrue(PacketType.OUT_MAP_CHUNK.isOutGoing());
    }
}
