package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import java.util.UUID;

import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;

import com.bergerkiller.bukkit.common.resources.ParticleType;
import com.bergerkiller.bukkit.common.resources.ParticleType.BlockPositionOption;
import com.bergerkiller.bukkit.common.resources.ParticleType.EntityByIdPositionOption;
import com.bergerkiller.bukkit.common.resources.ParticleType.EntityByUUIDPositionOption;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.PacketPlayOutWorldParticlesHandle;

public class ParticleTypeTest {

    @Test
    public void testRegistry() {
        assertTrue(ParticleType.values().size() > 16);
        assertEquals("bubble", ParticleType.BUBBLE.getName());
        assertTrue(ParticleType.BUBBLE.exists());
    }

    @Test
    public void testPacketParticleTypes() {
        PacketPlayOutWorldParticlesHandle packet = PacketPlayOutWorldParticlesHandle.createNew();
        assertEquals(ParticleType.UNKNOWN, packet.getParticleType());

        packet.setParticle(ParticleType.AMBIENT_ENTITY_EFFECT);
        assertEquals(ParticleType.AMBIENT_ENTITY_EFFECT, packet.getParticleType());

        packet.setParticle(ParticleType.BLOCK, BlockData.fromMaterial(MaterialUtil.getFirst("OAK_PLANKS", "LEGACY_WOOD")));
        assertEquals(ParticleType.BLOCK, packet.getParticleType());

        packet.setParticle(ParticleType.ITEM, new ItemStack(MaterialUtil.getFirst("OAK_PLANKS", "LEGACY_WOOD")));
        assertEquals(ParticleType.ITEM, packet.getParticleType());

        packet.setParticle(ParticleType.DUST, ParticleType.DustOptions.create(255, 0, 0, 1.0f));
        assertEquals(ParticleType.DUST, packet.getParticleType());

        if (ParticleType.VIBRATION.exists()) {
            packet.setParticle(ParticleType.VIBRATION, ParticleType.VibrationOptions.create(
                    BlockPositionOption.create(1, 2, 3), BlockPositionOption.create(4, 5, 6), 20));
            packet.setParticle(ParticleType.VIBRATION, ParticleType.VibrationOptions.create(
                    BlockPositionOption.create(1, 2, 3), EntityByIdPositionOption.create(5, 2.0f), 20));
            packet.setParticle(ParticleType.VIBRATION, ParticleType.VibrationOptions.create(
                    BlockPositionOption.create(1, 2, 3), EntityByUUIDPositionOption.create(UUID.randomUUID(), 2.0f), 20));
            assertEquals(ParticleType.VIBRATION, packet.getParticleType());
        }

        if (ParticleType.DUST_COLOR_TRANSITION.exists()) {
            packet.setParticle(ParticleType.DUST_COLOR_TRANSITION, ParticleType.DustColorTransitionOptions.create(
                    Color.RED, Color.GREEN, 1.0f));
            assertEquals(ParticleType.DUST_COLOR_TRANSITION, packet.getParticleType());
        }

        if (ParticleType.SCULK_CHARGE.exists()) {
            packet.setParticle(ParticleType.SCULK_CHARGE, ParticleType.SculkChargeOptions.create(20.0f));
            assertEquals(ParticleType.SCULK_CHARGE, packet.getParticleType());
        }

        if (ParticleType.SHRIEK.exists()) {
            packet.setParticle(ParticleType.SHRIEK, ParticleType.ShriekOptions.create(2));
            assertEquals(ParticleType.SHRIEK, packet.getParticleType());
        }
    }
}
