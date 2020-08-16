package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import org.bukkit.block.BlockFace;
import org.junit.Test;

import com.bergerkiller.bukkit.common.collections.BlockFaceSet;
import com.bergerkiller.bukkit.common.utils.FaceUtil;

public class BlockFaceSetTest {

    @Test
    public void testByBlockFace() {
        for (BlockFace face : FaceUtil.BLOCK_SIDES) {
            BlockFaceSet set = BlockFaceSet.of(face);
            assertTrue(set.get(face));
            for (BlockFace face2 : FaceUtil.BLOCK_SIDES) {
                if (face2 != face) {
                    assertFalse(set.get(face2));
                }
            }
        }

        assertEquals(BlockFaceSet.ALL, BlockFaceSet.of(FaceUtil.BLOCK_SIDES));
        assertEquals(BlockFaceSet.NONE, BlockFaceSet.of());
    }

    @Test
    public void testByMask() {
        for (int i = 0; i < 64; i++) {
            BlockFaceSet set = BlockFaceSet.byMask(i);
            assertEquals(i, set.mask());
            assertEquals((i & BlockFaceSet.MASK_NORTH) != 0, set.north());
            assertEquals((i & BlockFaceSet.MASK_EAST) != 0, set.east());
            assertEquals((i & BlockFaceSet.MASK_SOUTH) != 0, set.south());
            assertEquals((i & BlockFaceSet.MASK_WEST) != 0, set.west());
            assertEquals((i & BlockFaceSet.MASK_UP) != 0, set.up());
            assertEquals((i & BlockFaceSet.MASK_DOWN) != 0, set.down());
        }
    }

    @Test
    public void testFaceGettersSetters() {
        boolean[] states = {true, false};
        for (boolean north : states) {
            for (boolean east : states) {
                for (boolean south : states) {
                    for (boolean west : states) {
                        for (boolean up : states) {
                            for (boolean down : states) {
                                testFaceGettersSetters(BlockFaceSet.NONE, north, east, south, west, up, down);
                                testFaceGettersSetters(BlockFaceSet.ALL, north, east, south, west, up, down);
                                testFaceGettersSetClear(BlockFaceSet.NONE, north, east, south, west, up, down);
                                testFaceGettersSetClear(BlockFaceSet.ALL, north, east, south, west, up, down);
                                testFacePropertiesSetClear(BlockFaceSet.NONE, north, east, south, west, up, down);
                                testFacePropertiesSetClear(BlockFaceSet.ALL, north, east, south, west, up, down);
                            }
                        }
                    }
                }
            }
        }
    }

    private void testFaceGettersSetters(BlockFaceSet initial, boolean north, boolean east, boolean south, boolean west, boolean up, boolean down) {
        // Set them all
        BlockFaceSet set = initial;
        set = set.set(BlockFace.NORTH, north);
        set = set.set(BlockFace.EAST, east);
        set = set.set(BlockFace.SOUTH, south);
        set = set.set(BlockFace.WEST, west);
        set = set.set(BlockFace.UP, up);
        set = set.set(BlockFace.DOWN, down);

        // Check get() equals
        assertEquals(north, set.get(BlockFace.NORTH));
        assertEquals(east, set.get(BlockFace.EAST));
        assertEquals(south, set.get(BlockFace.SOUTH));
        assertEquals(west, set.get(BlockFace.WEST));
        assertEquals(up, set.get(BlockFace.UP));
        assertEquals(down, set.get(BlockFace.DOWN));

        // Check face properties equals
        assertEquals(north, set.north());
        assertEquals(east, set.east());
        assertEquals(south, set.south());
        assertEquals(west, set.west());
        assertEquals(up, set.up());
        assertEquals(down, set.down());
    }

    private void testFaceGettersSetClear(BlockFaceSet initial, boolean north, boolean east, boolean south, boolean west, boolean up, boolean down) {
        // Set them all
        BlockFaceSet set = initial;
        set = north ? set.set(BlockFace.NORTH) : set.clear(BlockFace.NORTH);
        set = east ? set.set(BlockFace.EAST) : set.clear(BlockFace.EAST);
        set = south ? set.set(BlockFace.SOUTH) : set.clear(BlockFace.SOUTH);
        set = west ? set.set(BlockFace.WEST) : set.clear(BlockFace.WEST);
        set = up ? set.set(BlockFace.UP) : set.clear(BlockFace.UP);
        set = down ? set.set(BlockFace.DOWN) : set.clear(BlockFace.DOWN);

        // Check get() equals
        assertEquals(north, set.get(BlockFace.NORTH));
        assertEquals(east, set.get(BlockFace.EAST));
        assertEquals(south, set.get(BlockFace.SOUTH));
        assertEquals(west, set.get(BlockFace.WEST));
        assertEquals(up, set.get(BlockFace.UP));
        assertEquals(down, set.get(BlockFace.DOWN));

        // Check face properties equals
        assertEquals(north, set.north());
        assertEquals(east, set.east());
        assertEquals(south, set.south());
        assertEquals(west, set.west());
        assertEquals(up, set.up());
        assertEquals(down, set.down());
    }

    private void testFacePropertiesSetClear(BlockFaceSet initial, boolean north, boolean east, boolean south, boolean west, boolean up, boolean down) {
        // Set them all
        BlockFaceSet set = initial;
        set = set.setNorth(north);
        set = set.setEast(east);
        set = set.setSouth(south);
        set = set.setWest(west);
        set = set.setUp(up);
        set = set.setDown(down);

        // Check get() equals
        assertEquals(north, set.get(BlockFace.NORTH));
        assertEquals(east, set.get(BlockFace.EAST));
        assertEquals(south, set.get(BlockFace.SOUTH));
        assertEquals(west, set.get(BlockFace.WEST));
        assertEquals(up, set.get(BlockFace.UP));
        assertEquals(down, set.get(BlockFace.DOWN));

        // Check face properties equals
        assertEquals(north, set.north());
        assertEquals(east, set.east());
        assertEquals(south, set.south());
        assertEquals(west, set.west());
        assertEquals(up, set.up());
        assertEquals(down, set.down());
    }
}
