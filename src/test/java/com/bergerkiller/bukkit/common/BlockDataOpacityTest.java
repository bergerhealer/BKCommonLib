package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.junit.Test;

import com.bergerkiller.bukkit.common.collections.BlockFaceSet;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;

/**
 * Tests the BlockData opacity and opaque faces properties.
 * Many blocks don't require world access to retrieve these properties,
 * and to test them we can use a null world or block as input.
 */
public class BlockDataOpacityTest {

    @Test
    public void testOpacity() {
        assertOpacity(0, BlockData.fromMaterial(Material.AIR));
        assertOpacity(0, BlockData.fromMaterial(Material.GLASS));
        assertOpacity(15, BlockData.fromMaterial(Material.OAK_WOOD));
        assertOpacity(15, BlockData.fromMaterial(Material.OBSIDIAN));
        assertOpacity(1, BlockData.fromMaterial(Material.OAK_LEAVES));
        assertOpacity(1, BlockData.fromMaterial(Material.WATER));
        assertOpacity(1, BlockData.fromMaterial(Material.ICE));
        assertOpacity(1, BlockData.fromMaterial(Material.COBWEB));
    }

    @Test
    public void testOpaqueFaces() {
        assertOpaqueFaces(BlockFaceSet.NONE, BlockData.fromMaterial(Material.AIR));
        assertOpaqueFaces(BlockFaceSet.NONE, BlockData.fromMaterial(Material.ACACIA_FENCE));
        assertOpaqueFaces(BlockFaceSet.ALL, BlockData.fromMaterial(Material.OAK_WOOD));
        assertOpaqueFaces(BlockFaceSet.ALL, BlockData.fromMaterial(Material.OBSIDIAN));

        // Weird. Says all faces are opaque? Still works since opacity is 0
        //assertOpaqueFaces(BlockFaceSet.NONE, BlockData.fromMaterial(Material.GLASS));

        // Slabs top/bottom half should be opaque
        assertOpaqueFaces(BlockFaceSet.of(BlockFace.UP), BlockData.fromMaterial(Material.OAK_SLAB).setState("type", "top"));
        assertOpaqueFaces(BlockFaceSet.of(BlockFace.DOWN), BlockData.fromMaterial(Material.OAK_SLAB).setState("type", "bottom"));

        // Shapes: straight / inner_left / inner_right / outer_left / outer_right
        // Half: bottom / top

        // Stairs upright straight back facing into a given orientation
        // We should see the bottom and the backside be opaque
        for (BlockFace facing : FaceUtil.AXIS) {
            BlockFaceSet expected = BlockFaceSet.of(BlockFace.DOWN, facing);
            assertOpaqueFaces(expected, BlockData.fromMaterial(Material.OAK_STAIRS)
                    .setState("facing", facing)
                    .setState("half", "bottom")
                    .setState("shape", "straight"));
        }

        // Stairs upside-down straight back facing into a given orientation
        // We should see the top and the backside be opaque
        for (BlockFace facing : FaceUtil.AXIS) {
            BlockFaceSet expected = BlockFaceSet.of(BlockFace.UP, facing);
            assertOpaqueFaces(expected, BlockData.fromMaterial(Material.OAK_STAIRS)
                    .setState("facing", facing)
                    .setState("half", "top")
                    .setState("shape", "straight"));
        }

        // Stairs using inner_left should see two faces be opaque
        {
            BlockFaceSet expected = BlockFaceSet.of(BlockFace.UP, BlockFace.NORTH, BlockFace.WEST);
            assertOpaqueFaces(expected, BlockData.fromMaterial(Material.OAK_STAIRS)
                    .setState("facing", BlockFace.NORTH)
                    .setState("half", "top")
                    .setState("shape", "inner_left"));
        }
    }

    private void assertOpacity(int expected, BlockData blockdata) {
        int actual = blockdata.getOpacity(null);
        if (expected != actual) {
            fail("Opacity of " + blockdata + " was " + actual + ", we expected " + expected);
        }
    }

    private void assertOpaqueFaces(BlockFaceSet expected, BlockData blockdata) {
        BlockFaceSet actual = blockdata.getOpaqueFaces(null);
        if (expected != actual) {
            System.err.println("Incorrect opaque faces for " + blockdata);
            System.err.println("  Actual: " + actual);
            System.err.println("Expected: " + expected);
            fail("Incorrect opaque faces for " + blockdata);
        }
    }
}
