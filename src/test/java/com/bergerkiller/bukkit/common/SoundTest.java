package com.bergerkiller.bukkit.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.generated.net.minecraft.resources.MinecraftKeyHandle;
import org.junit.Test;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.generated.net.minecraft.world.level.block.SoundEffectTypeHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.CraftSoundHandle;

/**
 * Tests various sound resource key API's
 */
public class SoundTest {

    @Test
    public void testSoundConstant() {
        assertEquals(MinecraftKeyHandle.DEFAULT_NAMESPACE, SoundEffect.CLICK.getName().getNamespace());
        if (CommonCapabilities.KEYED_EFFECTS) {
            assertEquals("minecraft:ui.button.click", SoundEffect.CLICK.getPath());
            assertEquals("ui.button.click", SoundEffect.CLICK.getName().getName());
        } else {
            assertEquals("minecraft:random.click", SoundEffect.CLICK.getPath());
            assertEquals("random.click", SoundEffect.CLICK.getName().getName());
        }
    }

    @Test
    public void testBlockStepSound() {
        assertTrue(SoundEffectTypeHandle.T.isAvailable());
        ResourceKey<SoundEffect> stepName = BlockData.fromMaterial(MaterialUtil.getFirst("GRASS_BLOCK", "LEGACY_GRASS")).getStepSound();
        if (CommonCapabilities.KEYED_EFFECTS) {
            assertEquals("minecraft:block.grass.step", stepName.getPath());
        } else {
            assertEquals("minecraft:dig.grass", stepName.getPath());
        }
    }

    @Test
    public void testBlockPlaceSound() {
        assertTrue(SoundEffectTypeHandle.T.isAvailable());
        ResourceKey<SoundEffect> stepName = BlockData.fromMaterial(MaterialUtil.getFirst("GRASS_BLOCK", "LEGACY_GRASS")).getPlaceSound();
        if (CommonCapabilities.KEYED_EFFECTS) {
            assertEquals("minecraft:block.grass.place", stepName.getPath());
        } else {
            assertEquals("minecraft:dig.grass", stepName.getPath());
        }
    }

    @Test
    public void testBukkitSoundConversion() {
        CommonBootstrap.initServer();
        ResourceKey<SoundEffect> soundName = CraftSoundHandle.getSoundEffect(org.bukkit.Sound.BLOCK_ANVIL_STEP);
        assertEquals("minecraft:block.anvil.step", soundName.getPath());
    }
}
