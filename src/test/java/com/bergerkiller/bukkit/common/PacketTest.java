package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import org.junit.Test;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.resources.BlockStateType;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutEntityTeleportHandle;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutTileEntityDataHandle;

public class PacketTest {

    @Test
    public void testBlockStateType() {
        assertEquals(9, BlockStateType.SIGN.getSerializedId());
        assertEquals("minecraft:sign", BlockStateType.SIGN.getKey().toString());
    }

    @Test
    public void testBlockStateChangePacket() {
        CommonTagCompound metadata = new CommonTagCompound();
        metadata.putValue("Text1", "a");
        metadata.putValue("Text2", "b");
        metadata.putValue("Text3", "c");
        metadata.putValue("Text4", "d");
        PacketPlayOutTileEntityDataHandle packet = PacketPlayOutTileEntityDataHandle.createNew(
                IntVector3.of(1, 2, 3), BlockStateType.SIGN, metadata.clone());
        assertEquals(IntVector3.of(1, 2, 3), packet.getPosition());
        assertEquals(BlockStateType.SIGN, packet.getType());
        assertEquals(metadata, packet.getData());
    }

    @Test
    public void testPacketDirection() {
        assertFalse(PacketType.IN_CHAT.isOutGoing());
        assertFalse(PacketType.IN_STEER_VEHICLE.isOutGoing());
        assertFalse(PacketType.IN_POSITION_LOOK.isOutGoing());
        assertFalse(PacketType.IN_ENTITY_ANIMATION.isOutGoing());
        assertTrue(PacketType.OUT_BLOCK_CHANGE.isOutGoing());
        assertTrue(PacketType.OUT_ENTITY_MOVE.isOutGoing());
        assertTrue(PacketType.OUT_MAP.isOutGoing());
        assertTrue(PacketType.OUT_MAP_CHUNK.isOutGoing());
    }

    @Test
    public void testPacketTeleportCreateNew() {
        assertNotNull(PacketPlayOutEntityTeleportHandle.createNew());
    }
}
