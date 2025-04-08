package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import static com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials.*;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.material.MaterialData;
import org.junit.Test;

import com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials;
import com.bergerkiller.bukkit.common.internal.legacy.MaterialDataToIBlockData;
import com.bergerkiller.bukkit.common.internal.legacy.MaterialsByName;
import com.bergerkiller.bukkit.common.internal.logic.BlockDataSerializer;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;

public class BlockDataTest {

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
        legacyDataRanges.put(getLegacyMaterial("STONE_BUTTON"), new int[] {0,1,2,3,4,5, 8,9,10,11,12,13});
        // ==================================================================================

        for (Material material : getAllLegacyMaterials()) {
            if (!MaterialUtil.isBlock(material)) {
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
    public void testLever() {
        BlockData d = BlockData.fromMaterial(MaterialsByName.getMaterial("LEVER"));

        // Attached face
        for (String face : new String[] { "ceiling", "floor", "wall" }) {
            d = d.setState("face", face);

            // Facing (rotation when ceiling/floor)
            for (BlockFace facing : new BlockFace[] { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST }) {
                d = d.setState("facing", facing);

                d = d.setState("powered", false);
                assertFalse("isPowered()==false failed for " + facing,
                        ((org.bukkit.material.Lever) d.getMaterialData()).isPowered());

                d = d.setState("powered", true);
                assertTrue("isPowered()==true failed for " + facing,
                        ((org.bukkit.material.Lever) d.getMaterialData()).isPowered());
            }
        }
    }

    @Test
    public void testButtons() {
        for (String buttonMaterialName : new String[] {
                "STONE_BUTTON", "ACACIA_BUTTON", "BIRCH_BUTTON", "DARK_OAK_BUTTON",
                "JUNGLE_BUTTON", "OAK_BUTTON", "SPRUCE_BUTTON",
                "CRIMSON_BUTTON", "WARPED_BUTTON", "POLISHED_BLACKSTONE_BUTTON", // 1.16
                "POLISHED_BLACKSTONE_BUTTON" // 1.17
        }) {
            Material material = MaterialUtil.getMaterial(buttonMaterialName);
            if (material == null) {
                continue;
            }

            BlockData d = BlockData.fromMaterial(material);

            // Facing
            for (BlockFace facing : FaceUtil.BLOCK_SIDES) {
                if (facing == BlockFace.UP) {
                    d = d.setState("face", "ceiling");
                } else if (facing == BlockFace.DOWN) {
                    d = d.setState("face", "floor");
                } else {
                    d = d.setState("face", "wall");
                    d = d.setState("facing", facing);
                }

                d = d.setState("powered", false);
                assertFalse("isPowered()==false failed for " + facing + " material " + material,
                        ((org.bukkit.material.Button) d.getMaterialData()).isPowered());

                d = d.setState("powered", true);
                assertTrue("isPowered()==true failed for " + facing + " material " + material,
                        ((org.bukkit.material.Button) d.getMaterialData()).isPowered());
            }
        }
    }

    @Test
    public void testWaterloggedRails() {
        if (!Common.evaluateMCVersion(">=", "1.17")) {
            return;
        }

        String[] shapes = new String[] {
                "north_south", "east_west",
                "ascending_east","ascending_west",
                "ascending_north", "ascending_south",
                "south_east", "south_west",
                "north_west" ,"north_east"};
        BlockFace[] directions = new BlockFace[] {
                BlockFace.SOUTH, BlockFace.EAST,
                BlockFace.EAST, BlockFace.WEST,
                BlockFace.NORTH, BlockFace.SOUTH,
                BlockFace.NORTH_WEST, BlockFace.NORTH_EAST,
                BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST};

        Material material;
        BlockData d;
        for (boolean waterlogged : new boolean[] { false, true }) {
            material = MaterialUtil.getMaterial("RAIL");
            d = BlockData.fromMaterial(material);
            d = d.setState("waterlogged", waterlogged);

            for (String railMaterialName : new String[] {
                    "RAIL", "DETECTOR_RAIL", "POWERED_RAIL", "ACTIVATOR_RAIL"
            }) {
                material = MaterialUtil.getMaterial(railMaterialName);
                d = BlockData.fromMaterial(material);
                d = d.setState("waterlogged", waterlogged);

                // Skip curve values for non-curvable rails
                int shapeLim = railMaterialName.equals("RAIL") ? shapes.length : (shapes.length - 4);

                // Check all possible shapes
                for (int i = 0; i < shapeLim; i++) {
                    d = d.setState("shape", shapes[i]);
                    org.bukkit.material.Rails rail = (org.bukkit.material.Rails) d.getMaterialData();
                    assertEquals("BlockData " + d + " expected direction = " + directions[i] +
                            " but was " + rail.getDirection(), directions[i], rail.getDirection());
                    boolean isSlope = shapes[i].contains("ascending");
                    if (isSlope != rail.isOnSlope()) {
                        fail("BlockData " + d + " expected isSlope = " + isSlope + " but was " + rail.isOnSlope());
                    }
                }
            }
        }
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testBlockData() {
        for (Material mat : getAllMaterials()) {
            if (!MaterialUtil.isBlock(mat)) {
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
                if (MaterialUtil.isBlock(mat)) {
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
            String msg = "Expected " + expected + ", but got " + object;
            System.err.println(msg);
            fail(msg);
        }
    }

    private void testLegacyMaterial(Material legacyMaterialType, int dataValue) {
        BlockData blockData = BlockData.fromMaterialData(legacyMaterialType, dataValue);
        MaterialData materialData = blockData.getMaterialData();
        if (CommonLegacyMaterials.toLegacy(materialData.getItemType()) != legacyMaterialType) {
            System.out.println("MaterialData type: " + materialData.getClass().getName());
            System.err.println("BlockData of " + legacyMaterialType + ":" + dataValue + " = " + blockData);
            System.out.println("TEST " + MaterialDataToIBlockData.getIBlockData(new MaterialData(legacyMaterialType, (byte) dataValue)));
            String msg = "testLegacyMaterial(" + legacyMaterialType + ", " + dataValue + ") failed: " +
                    "Expected legacy type " + legacyMaterialType + ", but was " + materialData.getItemType();
            System.err.println(msg);
            fail(msg);
        }
        if (materialData.getData() != (byte) dataValue) {
            // A problem in here indicates that something needs to be fixed in IBlockDataToMaterialData
            System.err.println("BlockData of " + legacyMaterialType + ":" + dataValue + " = " + blockData);
            String msg = "testLegacyMaterial(" + legacyMaterialType + ", " + dataValue + ") failed: " +
                    "Expected legacy data " + dataValue + ", but was " + ((int) materialData.getData() & 0xF);
            System.err.println(msg);
            fail(msg);
        }
    }

    @Test
    public void testSigns() {
        // Test legacy material types
        testSignMaterial(MaterialUtil.getMaterial("LEGACY_SIGN_POST"), false);
        testSignMaterial(MaterialUtil.getMaterial("LEGACY_WALL_SIGN"), true);

        if (Common.evaluateMCVersion(">=", "1.14")) {
            // Test the new sign material since 1.14
            testSignMaterial(MaterialUtil.getMaterial("ACACIA_SIGN"), false);
            testSignMaterial(MaterialUtil.getMaterial("ACACIA_WALL_SIGN"), true);
            testSignMaterial(MaterialUtil.getMaterial("BIRCH_SIGN"), false);
            testSignMaterial(MaterialUtil.getMaterial("BIRCH_WALL_SIGN"), true);
            testSignMaterial(MaterialUtil.getMaterial("DARK_OAK_SIGN"), false);
            testSignMaterial(MaterialUtil.getMaterial("DARK_OAK_WALL_SIGN"), true);
            testSignMaterial(MaterialUtil.getMaterial("JUNGLE_SIGN"), false);
            testSignMaterial(MaterialUtil.getMaterial("JUNGLE_WALL_SIGN"), true);
            testSignMaterial(MaterialUtil.getMaterial("OAK_SIGN"), false);
            testSignMaterial(MaterialUtil.getMaterial("OAK_WALL_SIGN"), true);
            testSignMaterial(MaterialUtil.getMaterial("SPRUCE_SIGN"), false);
            testSignMaterial(MaterialUtil.getMaterial("SPRUCE_WALL_SIGN"), true);
        } else {
            // These material names were used on 1.13 - 1.13.2
            testSignMaterial(MaterialUtil.getMaterial("SIGN"), false);
            testSignMaterial(MaterialUtil.getMaterial("WALL_SIGN"), true);
        }
    }

    @Test
    public void testHangingSigns() {
        if (!Common.evaluateMCVersion(">=", "1.19.3")) {
            return;
        }

        for (String name : new String[] {
                "OAK_HANGING_SIGN", "SPRUCE_HANGING_SIGN",
                "BIRCH_HANGING_SIGN", "JUNGLE_HANGING_SIGN",
                "ACACIA_HANGING_SIGN", "DARK_OAK_HANGING_SIGN",
                "MANGROVE_HANGING_SIGN", "BAMBOO_HANGING_SIGN",
                "CRIMSON_HANGING_SIGN", "WARPED_HANGING_SIGN",

                "OAK_WALL_HANGING_SIGN", "SPRUCE_WALL_HANGING_SIGN",
                "BIRCH_WALL_HANGING_SIGN", "JUNGLE_WALL_HANGING_SIGN",
                "ACACIA_WALL_HANGING_SIGN", "DARK_OAK_WALL_HANGING_SIGN",
                "MANGROVE_WALL_HANGING_SIGN", "BAMBOO_WALL_HANGING_SIGN",
                "CRIMSON_WALL_HANGING_SIGN", "WARPED_WALL_HANGING_SIGN"
        }) {
            testSignMaterial(MaterialUtil.getMaterial(name), false);
        }

        if (!Common.evaluateMCVersion(">=", "1.19.4")) {
            testSignMaterial(MaterialUtil.getMaterial("CHERRY_HANGING_SIGN"), false);
            testSignMaterial(MaterialUtil.getMaterial("CHERRY_WALL_HANGING_SIGN"), false);
        }
    }

    @Test
    public void testSerialize() {
        // Retrieve some complicated BlockData
        BlockData blockData = BlockData.fromMaterial(Material.FURNACE)
                .setState("lit", true)
                .setState("facing", BlockFace.EAST);
        // Serialize
        String output = BlockDataSerializer.INSTANCE.serialize(blockData);
        assertEquals("minecraft:furnace[facing=east,lit=true]", output);
    }

    @Test
    public void testDeserialize() {
        // Uses output of Serialize
        String input = "minecraft:furnace[facing=east,lit=true]";
        // Deserialize
        BlockData blockData = BlockDataSerializer.INSTANCE.deserialize(input);
        assertEquals(Material.FURNACE, blockData.getType());
        assertTrue(blockData.getState("lit", Boolean.class));
        assertEquals(BlockFace.EAST, blockData.getState("facing", BlockFace.class));
    }

    @Test
    public void testBlockModelName() {
        BlockData blockData = BlockData.fromMaterial(Material.FURNACE);
        assertEquals("furnace", blockData.getBlockName());
    }

    @Test
    public void testValidBlockDataFromString() {
        assertEquals(MaterialUtil.getFirst("DIAMOND_BLOCK", "LEGACY_DIAMOND_BLOCK"),
                BlockData.fromString("minecraft:diamond_block").getType());
    }

    @Test
    public void testInvalidBlockDataFromString() {
        assertNull(BlockData.fromString("invalid_block_data"));
    }

    private void testSignMaterial(Material material, boolean isWallSign) {
        assertNotNull(material);
        BlockData signData = BlockData.fromMaterial(material);
        if (signData == null) {
            fail("Material " + material.name() + " produces unexpected null BlockData");
        }
        if (signData == BlockData.AIR) {
            fail("Material " + material.name() + " can not be resolved to valid Sign BlockData");
        }
        if (signData.getLegacyType() != MaterialUtil.getMaterial(isWallSign ? "LEGACY_WALL_SIGN" : "LEGACY_SIGN_POST")) {
            fail("Material " + material.name() + " has an invalid legacy type that is not a sign: " + signData.getLegacyType());
        }
        if (!MaterialUtil.isLegacyType(material) && signData.getType() != material) {
            fail("Material " + material.name() + " has invalid BlockData type: " + signData.getType());
        }

        // BlockData -> MaterialData
        MaterialData legacyMaterialData = signData.getMaterialData();
        if (!(legacyMaterialData instanceof org.bukkit.material.Sign)) {
            fail("Material " + material.name() + " does not have Sign MaterialData");
        }
        if (!MaterialUtil.isLegacyType(material) && legacyMaterialData.getItemType() != material) {
            fail("Material " + material.name() + " results in MaterialData type " + legacyMaterialData.getItemType() + " from block data");
        }
        org.bukkit.material.Sign legacySign = (org.bukkit.material.Sign) legacyMaterialData;
        if (legacySign.isWallSign() != isWallSign) {
            fail("Material " + material.name() + " expected isWallSign() == " + isWallSign + ", but was " + legacySign.isWallSign());
        }

        // MaterialData -> BlockData
        BlockData restoredBlockData = BlockData.fromMaterialData(legacyMaterialData);
        if (restoredBlockData != signData) {
            fail("Block Data was not restored from MaterialData correctly. Expected " + signData + ", but got " + restoredBlockData);
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
