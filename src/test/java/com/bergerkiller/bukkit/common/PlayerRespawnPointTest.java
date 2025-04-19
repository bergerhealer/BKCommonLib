package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.resources.ResourceCategory;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.wrappers.PlayerRespawnPoint;
import com.bergerkiller.bukkit.common.wrappers.PlayerRespawnPointNearBlock;
import org.bukkit.World;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests the correct function of the {@link com.bergerkiller.bukkit.common.wrappers.PlayerRespawnPoint}.
 * Specifically the logic of converting between packed handles and NBT and so on.
 */
public class PlayerRespawnPointTest {

    static {
        CommonBootstrap.initCommonServerAssertCompatibility();
    }

    @Test
    public void testCreateNew() {
        ResourceKey<World> dimensionKey = ResourceCategory.dimension.createKey("testworld");
        assertNotNull(dimensionKey);

        PlayerRespawnPoint rawPoint = PlayerRespawnPoint.create(dimensionKey, 1, 2, 3, 30.0f, true);
        assertNotNull(rawPoint);
        assertFalse(rawPoint.isNone());
        assertTrue(rawPoint instanceof PlayerRespawnPointNearBlock);

        PlayerRespawnPointNearBlock nearBlock = (PlayerRespawnPointNearBlock) rawPoint;
        assertEquals(IntVector3.of(1, 2, 3), nearBlock.getBlockPosition());
        assertEquals(1, nearBlock.getBlockX());
        assertEquals(2, nearBlock.getBlockY());
        assertEquals(3, nearBlock.getBlockZ());
        assertEquals(30.0f, nearBlock.getAngle(), 0.0f);
        assertTrue(nearBlock.isForced());
    }

    @Test
    public void testNearBlockNBTSerialization() {
        ResourceKey<World> dimensionKey = ResourceCategory.dimension.createKey("testworld");
        PlayerRespawnPoint rawPoint = PlayerRespawnPoint.create(dimensionKey, 1, 2, 3, 30.0f, true);
        PlayerRespawnPointNearBlock nearBlock = (PlayerRespawnPointNearBlock) rawPoint;

        CommonTagCompound nbt = new CommonTagCompound();
        nearBlock.toNBT(nbt);

        assertFalse(nbt.isEmpty());
        PlayerRespawnPoint parsed = PlayerRespawnPointNearBlock.fromNBT(nbt);
        assertEquals(nearBlock, parsed);
        assertFalse(parsed.isNone());
    }

    @Test
    public void testNoneNBTSerialization() {
        PlayerRespawnPoint none = PlayerRespawnPoint.NONE;

        CommonTagCompound nbt = new CommonTagCompound();
        nbt.putValue("SpawnWorld", "TestWorld");
        nbt.putValue("SpawnX", 22);
        none.toNBT(nbt);

        assertTrue(nbt.isEmpty());
        PlayerRespawnPoint parsed = PlayerRespawnPointNearBlock.fromNBT(nbt);
        assertEquals(none, parsed);
        assertTrue(parsed.isNone());
    }
}
