package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import org.bukkit.Material;
import org.junit.Test;

import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;

public class BlockDataTest {

    static {
        CommonUtil.bootstrap();
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testBlockData() {
        for (Material mat : Material.values()) {
            if (mat.isBlock()) {
                assertType(mat, BlockData.fromMaterial(mat).getType());
                assertType(BlockData.fromMaterialData(mat, 0).getType(), BlockData.fromMaterial(mat).getType());
            } else {
                assertType(Material.AIR, BlockData.fromMaterial(mat).getType());
            }
        }
        assertEquals(0, BlockData.fromMaterial(Material.AIR).getEmission());
        assertEquals(0, BlockData.fromMaterial(Material.AIR).getOpacity());
        assertEquals(15, BlockData.fromMaterial(Material.GLOWSTONE).getEmission());
        assertEquals(7, BlockData.fromMaterial(Material.REDSTONE_TORCH_ON).getEmission());
        assertEquals(14, BlockData.fromMaterial(Material.TORCH).getEmission());
    }

    @SuppressWarnings("deprecation")
    private void assertType(Material expected, Material object) {
        if (expected != object) {
            fail("Expected " + expected + " (id=" + expected.getId() + "), but got " + object + " (id=" + object.getId() + ")");
        }
    }
}
