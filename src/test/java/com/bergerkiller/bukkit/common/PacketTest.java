package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import org.junit.Test;

import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.generated.net.minecraft.server.EnumProtocolHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayInBlockPlaceHandle;
import com.bergerkiller.generated.net.minecraft.server.PacketPlayOutEntityDestroyHandle;

public class PacketTest {

    @Test
    public void testPacketDirection() {
        assertFalse(PacketType.IN_CHAT.isOutGoing());
        assertFalse(PacketType.IN_STEER_VEHICLE.isOutGoing());
        assertFalse(PacketType.IN_POSITION_LOOK.isOutGoing());
        assertFalse(PacketType.IN_ENTITY_ANIMATION.isOutGoing());
        assertTrue(PacketType.OUT_BLOCK_CHANGE.isOutGoing());
        assertTrue(PacketType.OUT_CHAT.isOutGoing());
        assertTrue(PacketType.OUT_ENTITY_MOVE.isOutGoing());
        assertTrue(PacketType.OUT_MAP.isOutGoing());
        assertTrue(PacketType.OUT_MAP_CHUNK.isOutGoing());
    }

    @Test
    public void testPacketIdRegistry() {
        int id;

        // Test that PacketPlayInBlockPlace has a valid id that can also be converted back
        // The 'out' lookup should not contain this packet
        assertEquals(-1, EnumProtocolHandle.PLAY.getPacketIdOut(PacketPlayInBlockPlaceHandle.T.getType()));
        id = EnumProtocolHandle.PLAY.getPacketIdIn(PacketPlayInBlockPlaceHandle.T.getType());
        assertNotEquals(-1, id);
        assertEquals(PacketPlayInBlockPlaceHandle.T.getType(), EnumProtocolHandle.PLAY.getPacketClassIn(id));

        // Test that PacketPlayOutEntityDestroy has a valid id that can also be converted back
        // The 'in' lookup should not contain this packet
        assertEquals(-1, EnumProtocolHandle.PLAY.getPacketIdIn(PacketPlayOutEntityDestroyHandle.T.getType()));
        id = EnumProtocolHandle.PLAY.getPacketIdOut(PacketPlayOutEntityDestroyHandle.T.getType());
        assertNotEquals(-1, id);
        assertEquals(PacketPlayOutEntityDestroyHandle.T.getType(), EnumProtocolHandle.PLAY.getPacketClassOut(id));
    }
}
