package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;


import java.util.HashMap;
import java.util.Map;

import static com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials.*;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.material.MaterialData;
import org.junit.Test;

import com.bergerkiller.bukkit.common.internal.legacy.MaterialDataToIBlockData;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;

public class BlockDataTest {

    static {
        CommonUtil.bootstrap();
    }

    @Test
    public void testMaterialData() {
        final Map<Material, int[]> legacyDataRanges = new HashMap<Material, int[]>();

        // ================== Data value ranges, empty to skip the Material =================
        legacyDataRanges.put(getLegacyMaterial("STATIONARY_WATER"), new int[0]);
        legacyDataRanges.put(getLegacyMaterial("STATIONARY_LAVA"), new int[0]);
        legacyDataRanges.put(getLegacyMaterial("DEAD_BUSH"), new int[0]);
        legacyDataRanges.put(getLegacyMaterial("SKULL"), new int[0]); // Broken! PLAYER_HEAD?
        legacyDataRanges.put(getLegacyMaterial("WOOL"), createRange(0, 15));
        legacyDataRanges.put(getLegacyMaterial("TORCH"), createRange(1, 5));
        legacyDataRanges.put(getLegacyMaterial("REDSTONE_TORCH_ON"), createRange(1, 5));
        legacyDataRanges.put(getLegacyMaterial("REDSTONE_TORCH_OFF"), createRange(1, 5));
        legacyDataRanges.put(getLegacyMaterial("CHEST"), createRange(2, 5));
        legacyDataRanges.put(getLegacyMaterial("ENDER_CHEST"), createRange(2, 5));
        legacyDataRanges.put(getLegacyMaterial("TRAPPED_CHEST"), createRange(2, 5));
        legacyDataRanges.put(getLegacyMaterial("FURNACE"), createRange(2, 5));
        legacyDataRanges.put(getLegacyMaterial("BURNING_FURNACE"), createRange(2, 5));
        legacyDataRanges.put(getLegacyMaterial("LADDER"), createRange(2, 5));
        legacyDataRanges.put(getLegacyMaterial("WALL_SIGN"), createRange(2, 5));
        legacyDataRanges.put(getLegacyMaterial("SIGN_POST"), createRange(1, 15));
        legacyDataRanges.put(getLegacyMaterial("REDSTONE_COMPARATOR_ON"), createRange(0, 7));
        legacyDataRanges.put(getLegacyMaterial("REDSTONE_COMPARATOR_OFF"), createRange(0, 7));
        legacyDataRanges.put(getLegacyMaterial("STEP"), createRange(0, 15));
        legacyDataRanges.put(getLegacyMaterial("DOUBLE_STEP"), createRange(0, 7));
        legacyDataRanges.put(getLegacyMaterial("WALL_BANNER"), createRange(2, 5));
        legacyDataRanges.put(getLegacyMaterial("PORTAL"), createRange(1, 2));
        // ==================================================================================

        for (Material material : getAllLegacyMaterials()) {
            if (!material.isBlock()) {
                continue;
            }

            int[] dataValues = legacyDataRanges.get(material);
            if (dataValues != null) {
                for (int dataValue : dataValues) {
                    testLegacyMaterial(material, dataValue);
                }
            } else {
                testLegacyMaterial(material, 0);
            }
        }
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testBlockData() {
        for (Material mat : getAllMaterials()) {
            if (!mat.isBlock()) {
                assertType(Material.AIR, BlockData.fromMaterial(mat).getType());
            } else if (MaterialUtil.isLegacyType(mat)) {
                assertType(mat, BlockData.fromMaterial(mat).getLegacyType());
            } else {
                assertType(mat, BlockData.fromMaterial(mat).getType());
            }
        }

        if (Common.evaluateMCVersion(">=", "1.13")) {
            // Since MC 1.13 CraftBukkit deals with legacy conversion, and does some interesting things as a result
            Material[] to_test = getAllByName("LEGACY_AIR", "LEGACY_STONE", "LEGACY_GLASS", "AIR", "STONE", "GLASS");
            for (Material mat : to_test) {
                BlockData from_matdata = BlockData.fromMaterialData(mat, 0);
                BlockData from_mat = BlockData.fromMaterial(mat);
                assertType(from_matdata.getType(),from_mat.getType());
                assertType(from_matdata.getLegacyType(),from_mat.getLegacyType());
            }
        } else {
            // On older versions, the type from fromMaterialData MUST match the type fromMaterial
            for (Material mat : getAllMaterials()) {
                if (mat.isBlock()) {
                    BlockData from_matdata = BlockData.fromMaterialData(mat, 0);
                    BlockData from_mat = BlockData.fromMaterial(mat);
                    assertType(from_matdata.getType(),from_mat.getType());
                    assertType(from_matdata.getLegacyType(),from_mat.getLegacyType());
                }
            }
        }

        assertEquals(0, BlockData.fromMaterial(Material.AIR).getEmission());
        //assertEquals(0, BlockData.fromMaterial(Material.AIR).getOpacity());
        assertEquals(15, BlockData.fromMaterial(Material.GLOWSTONE).getEmission());
        assertEquals(14, BlockData.fromMaterial(Material.TORCH).getEmission());
    }

    @Test
    public void testBlockDataStates() {
        // Get Block Data of stairs
        Material mat = MaterialUtil.getFirst("OAK_STAIRS", "LEGACY_WOOD_STAIRS");
        BlockData data = BlockData.fromMaterial(mat);

        data = data.setState("facing", BlockFace.EAST);
        assertEquals(BlockFace.EAST, data.getState("facing", BlockFace.class));
        assertTrue(data.isType(mat));

        data = data.setState("facing", BlockFace.SOUTH);
        assertEquals(BlockFace.SOUTH, data.getState("facing", BlockFace.class));
        assertTrue(data.isType(mat));
    }

    @SuppressWarnings("deprecation")
    private void assertType(Material expected, Material object) {
        if (expected != object) {
            // There are some exceptions where type information went missing on 1.13
            Map<String, String> exceptions = new HashMap<String, String>();
            exceptions.put("LEGACY_STATIONARY_WATER", "LEGACY_WATER");
            exceptions.put("LEGACY_STATIONARY_LAVA", "LEGACY_LAVA");
            exceptions.put("LEGACY_DEAD_BUSH", "LEGACY_LONG_GRASS");
            exceptions.put("LEGACY_SKULL", "LEGACY_AIR");
            if (object.name().equals(exceptions.get(expected.name()))) {
                return;
            }

            // Failed.
            String msg = "Expected " + expected + " (id=" + expected.getId() + "), but got " + object + " (id=" + object.getId() + ")";
            System.err.println(msg);
            fail(msg);
        }
    }

    private void testLegacyMaterial(Material legacyMaterialType, int dataValue) {
        BlockData blockData = BlockData.fromMaterialData(legacyMaterialType, dataValue);
        MaterialData materialData = blockData.getMaterialData();
        if (materialData.getItemType() != legacyMaterialType) {
            System.err.println("BlockData of " + legacyMaterialType + ":" + dataValue + " = " + blockData);
            System.out.println("TEST " + MaterialDataToIBlockData.getIBlockData(new MaterialData(legacyMaterialType, (byte) dataValue)));
            String msg = "testLegacyMaterial(" + legacyMaterialType + ", " + dataValue + ") failed: " +
                    "Expected legacy type " + legacyMaterialType + ", but was " + materialData.getItemType();
            System.err.println(msg);
            fail(msg);
        }
        if (materialData.getData() != (byte) dataValue) {
            System.err.println("BlockData of " + legacyMaterialType + ":" + dataValue + " = " + blockData);
            String msg = "testLegacyMaterial(" + legacyMaterialType + ", " + dataValue + ") failed: " +
                    "Expected legacy data " + dataValue + ", but was " + ((int) materialData.getData() & 0xF);
            System.err.println(msg);
            fail(msg);
        }
    }

    private int[] createRange(int start, int end) {
        int[] values = new int[end-start+1];
        int idx = 0;
        for (int i = start; i <= end; i++) {
            values[idx++] = i;
        }
        return values;
    }

    private int[] createArray(int... values) {
        return values;
    }
}
