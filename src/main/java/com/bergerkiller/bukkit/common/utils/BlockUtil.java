package com.bergerkiller.bukkit.common.utils;

import java.util.ArrayList;
import java.util.Collection;

import com.bergerkiller.generated.org.bukkit.block.SignHandle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.material.Directional;
import org.bukkit.material.MaterialData;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.blockstate.BlockStateConversion;
import com.bergerkiller.bukkit.common.conversion.blockstate.ChunkBlockStateConverter;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.generated.net.minecraft.core.BlockPositionHandle;
import com.bergerkiller.generated.net.minecraft.server.level.WorldServerHandle;
import com.bergerkiller.generated.net.minecraft.world.level.WorldHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.entity.TileEntityHandle;
import com.bergerkiller.generated.net.minecraft.world.level.chunk.ChunkHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.AxisAlignedBBHandle;
import com.bergerkiller.generated.org.bukkit.event.block.BlockCanBuildEventHandle;

/**
 * Multiple Block utilities you can use to manipulate blocks and get block
 * information
 */
public class BlockUtil extends MaterialUtil {

    /**
     * Gets the bounding box of a block
     * 
     * @param block
     * @return bounding box, relative to block coordinates
     */
    public static AxisAlignedBBHandle getBoundingBox(Block block) {
        return WorldUtil.getBlockData(block).getBoundingBox(block);
    }

    /**
     * Performs an event asking other plugins whether a block can change to a
     * different Material
     *
     * @param block to check
     * @param type that the block is about to be set to (built)
     * @return True if permitted, False if not
     */
    public static boolean canBuildBlock(org.bukkit.block.Block block, Material type) {
        return canBuildBlock(block, type, true);
    }

    /**
     * Performs an event asking other plugins whether a block can change to a
     * different Material
     *
     * @param block to check
     * @param type that the block is about to be set to (built)
     * @return True if permitted, False if not
     */
    public static boolean canBuildBlock(org.bukkit.block.Block block, BlockData type) {
        return canBuildBlock(block, type, true);
    }

    /**
     * Performs an event asking other plugins whether a block can change to a
     * different Material
     *
     * @param block to check
     * @param type that the block is about to be set to (built)
     * @param isBuildable - Initial allow state
     * @return True if permitted, False if not
     */
    public static boolean canBuildBlock(org.bukkit.block.Block block, Material type, boolean isBuildable) {
        return canBuildBlock(block, BlockData.fromMaterial(type), isBuildable);
    }

    /**
     * Performs an event asking other plugins whether a block can change to a
     * different Material
     *
     * @param block to check
     * @param type that the block is about to be set to (built)
     * @param isBuildable - Initial allow state
     * @return True if permitted, False if not
     */
    public static boolean canBuildBlock(org.bukkit.block.Block block, BlockData type, boolean isBuildable) {
        return CommonUtil.callEvent(BlockCanBuildEventHandle.create(block, type, isBuildable)).isBuildable();
    }

    /**
     * Sets the Block type and data at once, then performs physics
     *
     * @param block to set the type and data of
     * @param type to set to
     * @param data to set to
     */
    public static void setTypeAndData(org.bukkit.block.Block block, Material type, MaterialData data) {
        setTypeAndData(block, type, data, true);
    }

    /**
     * Sets the Block type and data at once
     *
     * @param block to set the type and data of
     * @param type to set to
     * @param data to set to
     * @param update - whether to perform physics afterwards
     */
    @SuppressWarnings("deprecation")
    public static void setTypeAndData(org.bukkit.block.Block block, Material type, MaterialData data, boolean update) {
        BlockData blockData = BlockData.fromMaterialData(type, data);
        if (update) {
            WorldUtil.setBlockData(block, blockData);
        } else {
            WorldUtil.setBlockDataFast(block, blockData);
        }
    }

    /**
     * Sets the Block type and data at once, then performs physics
     *
     * @param block to set the type and data of
     * @param type to set to
     * @param data to set to
     */
    public static void setTypeAndRawData(org.bukkit.block.Block block, Material type, int data) {
        setTypeAndRawData(block, type, data, true);
    }

    /**
     * Sets the Block type and data at once
     *
     * @param block to set the type and data of
     * @param type to set to
     * @param data to set to
     * @param update - whether to perform physics afterwards
     */
    @SuppressWarnings("deprecation")
    public static void setTypeAndRawData(org.bukkit.block.Block block, Material type, int data, boolean update) {
        BlockData blockData = BlockData.fromMaterialData(type, data);
        if (update) {
            WorldUtil.setBlockData(block, blockData);
        } else {
            WorldUtil.setBlockDataFast(block, blockData);
        }
    }

    /**
     * Sets the Material Data for a Block
     *
     * @param block to set it for
     * @param materialData to set to
     */
    @SuppressWarnings("deprecation")
    public static void setData(org.bukkit.block.Block block, MaterialData materialData) {
        setData(block, materialData, true);
    }

    /**
     * Sets the Material Data for a Block
     *
     * @param block to set it for
     * @param materialData to set to
     * @param doPhysics - True to perform physics, False for 'silent'
     */
    @SuppressWarnings("deprecation")
    public static void setData(org.bukkit.block.Block block, MaterialData materialData, boolean doPhysics) {
        BlockData data = BlockData.fromMaterialData(materialData);
        if (doPhysics) {
            WorldUtil.setBlockData(block, data);
        } else {
            WorldUtil.setBlockDataFast(block, data);
        }
    }

    /**
     * Directly obtains the Material Data from the block<br>
     * This alternative does not create a Block State and is preferred if you
     * only need material data
     */
    public static MaterialData getData(org.bukkit.block.Block block) {
        return WorldUtil.getBlockData(block).newMaterialData();
    }

    /**
     * Gets the Material data for the block specified and attempts to cast it
     *
     * @param block to get the Material data for
     * @param type to cast to
     * @return The cast material data, or null if there was no data or if
     * casting failed
     */
    public static <T> T getData(org.bukkit.block.Block block, Class<T> type) {
        return LogicUtil.tryCast(getData(block), type);
    }

    /**
     * Calculates the so-called 'Manhatten Distance' between two locations<br>
     * This is the distance between two points without going diagonally
     *
     * @param b1 location
     * @param b2 location
     * @param checkY state, True to include the y distance, False to exclude it
     * @return The manhattan distance
     */
    public static int getManhattanDistance(Location b1, Location b2, boolean checkY) {
        int d = Math.abs(b1.getBlockX() - b2.getBlockX());
        d += Math.abs(b1.getBlockZ() - b2.getBlockZ());
        if (checkY) {
            d += Math.abs(b1.getBlockY() - b2.getBlockY());
        }
        return d;
    }

    /**
     * Calculates the so-called 'Manhatten Distance' between two blocks<br>
     * This is the distance between two points without going diagonally
     *
     * @param b1 block
     * @param b2 block
     * @param checkY state, True to include the y distance, False to exclude it
     * @return The Manhattan distance
     */
    public static int getManhattanDistance(org.bukkit.block.Block b1, org.bukkit.block.Block b2, boolean checkY) {
        int d = Math.abs(b1.getX() - b2.getX());
        d += Math.abs(b1.getZ() - b2.getZ());
        if (checkY) {
            d += Math.abs(b1.getY() - b2.getY());
        }
        return d;
    }

    /**
     * Checks if two Blocks are equal
     *
     * @param block1 to evaluate
     * @param block2 to evaluate
     * @return True if the blocks are the same, False if not
     */
    public static boolean equals(org.bukkit.block.Block block1, org.bukkit.block.Block block2) {
        if (block1 == null || block2 == null) {
            return false;
        }
        if (block1 == block2) {
            return true;
        }
        return block1.getX() == block2.getX() && block1.getZ() == block2.getZ() && block1.getY() == block2.getY() && block1.getWorld() == block2.getWorld();
    }

    /**
     * Gets all the Blocks relative to a main block using multiple Block Faces
     *
     * @param main block
     * @param faces to get the blocks relative to the main of
     * @return An array of relative blocks to the main based on the input faces
     */
    public static org.bukkit.block.Block[] getRelative(org.bukkit.block.Block main, BlockFace... faces) {
        if (main == null) {
            return new org.bukkit.block.Block[0];
        }
        org.bukkit.block.Block[] rval = new org.bukkit.block.Block[faces.length];
        for (int i = 0; i < rval.length; i++) {
            rval[i] = main.getRelative(faces[i]);
        }
        return rval;
    }

    /**
     * Gets the Block at the coordinates specified
     *
     * @param world of the block
     * @param at coordinates
     * @return Block at the coordinates in the world
     */
    public static org.bukkit.block.Block getBlock(org.bukkit.World world, IntVector3 at) {
        return world.getBlockAt(at.x, at.y, at.z);
    }

    /**
     * Gets the face an attachable block is attached to<br>
     * Returns DOWN if the block is not attachable
     *
     * @param attachable block
     * @return Attached face
     */
    public static BlockFace getAttachedFace(org.bukkit.block.Block attachable) {
        return WorldUtil.getBlockData(attachable).getAttachedFace();
    }

    /**
     * Gets the Block an attachable block is attached to
     *
     * @param attachable block
     * @return Block the attachable is attached to
     */
    public static org.bukkit.block.Block getAttachedBlock(org.bukkit.block.Block attachable) {
        return attachable.getRelative(getAttachedFace(attachable));
    }

    /**
     * Gets the facing direction of a Directional block
     *
     * @param directional block
     * @return facing direction
     */
    public static BlockFace getFacing(org.bukkit.block.Block directional) {
        Directional data = getData(directional, Directional.class);
        return data == null ? BlockFace.NORTH : data.getFacing();
    }

    /**
     * Sets the facing direction of a Directional block
     *
     * @param block to set
     * @param facing direction to set to
     */
    public static void setFacing(org.bukkit.block.Block block, BlockFace facing) {
        MaterialData data = getData(block);
        if (data != null && data instanceof Directional) {
            ((Directional) data).setFacingDirection(facing);
            setData(block, data, true);
        }
    }

    /**
     * Sets the toggled state for all levers attached to a certain block
     *
     * @param block center
     * @param down state to set to
     */
    public static void setLeversAroundBlock(org.bukkit.block.Block block, boolean down) {
        org.bukkit.block.Block b;
        for (BlockFace dir : FaceUtil.BLOCK_SIDES) {
            // Attached lever at this direction?
            if (isType(b = block.getRelative(dir), Material.LEVER) && getAttachedFace(b) == dir.getOppositeFace()) {
                setLever(b, down);
            }
        }
    }

    /**
     * Checks if a given lever block is in the down state<br>
     * The block type is not checked.
     *
     * @param lever block
     * @return True if the lever is down, False if not
     */
    @SuppressWarnings("deprecation")
    public static boolean isLeverDown(org.bukkit.block.Block lever) {
        int dat = getRawData(lever);
        return dat == (dat | 0x8);
    }

    /**
     * Sets the toggled state of a single lever<br>
     * <b>No Lever type check is performed</b>
     *
     * @param lever block
     * @param down state to set to
     */
    @SuppressWarnings("deprecation")
    public static void setLever(org.bukkit.block.Block lever, boolean down) {
        int data = getRawData(lever);
        org.bukkit.material.Lever newMaterialData = (org.bukkit.material.Lever) getData(Material.LEVER, data);
        newMaterialData.setPowered(down);
        if (getRawData(newMaterialData) != data) {
            // CraftBukkit start - Redstone event for lever
            int old = !down ? 1 : 0;
            int current = down ? 1 : 0;
            BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(lever, old, current);
            CommonUtil.callEvent(eventRedstone);
            if ((eventRedstone.getNewCurrent() > 0) != down) {
                return;
            }
            // CraftBukkit end
            setData(lever, newMaterialData, true);
            applyPhysics(getAttachedBlock(lever), Material.LEVER);
        }
    }

    /**
     * Performs Physics at the block specified.
     * The block itself is also updated.
     *
     * @param block to apply physics to
     * @param callerType Material, the source of these physics (use Air if there
     * is no caller)
     */
    public static void applyPhysics(org.bukkit.block.Block block, Material callerType) {
        applyPhysics(block, callerType, true);
    }

    /**
     * Performs Physics at the block specified
     *
     * @param block to apply physics to
     * @param callerType Material, the source of these physics (use Air if there is no caller)
     * @param updateSelf whether the block itself is updated. With false, only surrounding blocks are notified.
     */
    public static void applyPhysics(org.bukkit.block.Block block, Material callerType, boolean updateSelf) {
        WorldServerHandle.fromBukkit(block.getWorld()).applyBlockPhysics(new IntVector3(block), BlockData.fromMaterial(callerType), updateSelf);
    }

    /**
     * Obtains a new packet that can be used to update tile entity (block state) information
     * to nearby players<br>
     * Returns null if none are needed or supported by the tile entity
     *
     * @param blockState to get the update packet for
     * @return update packet
     */
    public static CommonPacket getUpdatePacket(BlockState blockState) {
        if (blockState == null) return null;
        Object tileEntity = HandleConversion.toTileEntityHandle(blockState);
        return (tileEntity == null) ? null : TileEntityHandle.T.getUpdatePacket.invoke(tileEntity);
    }

    /**
     * Sets the alignment of Rails Note that this only supports the rails that
     * can curve.
     *
     * @param rails to set the alignment for
     * @param from direction
     * @param to direction
     */
    public static void setRails(org.bukkit.block.Block rails, BlockFace from, BlockFace to) {
        setRails(rails, FaceUtil.combine(from, to).getOppositeFace());
    }

    /**
     * Sets the alignment of Rails. Note that this only supports the rails that
     * can curve.
     *
     * @param rails to set the alignment for
     * @param direction alignment
     */
    @SuppressWarnings("deprecation")
    public static void setRails(org.bukkit.block.Block rails, BlockFace direction) {
        BlockData data = WorldUtil.getBlockData(rails);
        if (MaterialUtil.ISRAILS.get(data)) {
            int olddata = data.getRawData();
            org.bukkit.material.Rails r = data.newMaterialData(org.bukkit.material.Rails.class);
            r.setDirection(FaceUtil.toRailsDirection(direction), r.isOnSlope());

            // If changed, update the data
            if (MaterialUtil.getRawData(r) != olddata) {
                setData(rails, r);
            }
        }
    }

    /**
     * Gets the state of a block, with additional safety measures taken
     *
     * @param block to get the state for
     * @return the Block State
     */
    public static BlockState getState(org.bukkit.block.Block block) {
        return BlockStateConversion.INSTANCE.blockToBlockState(block);
    }

    /**
     * Gets the State of a block, cast to a certain type
     *
     * @param block to get the state for
     * @param type to cast to
     * @return The block state cast to the type, or null if casting is not
     * possible
     */
    public static <T extends BlockState> T getState(org.bukkit.block.Block block, Class<T> type) {
        return LogicUtil.tryCast(getState(block), type);
    }

    public static org.bukkit.material.Rails getRails(org.bukkit.block.Block railsblock) {
        return getData(railsblock, org.bukkit.material.Rails.class);
    }

    public static Sign getSign(org.bukkit.block.Block signblock) {
        return getState(signblock, Sign.class);
    }

    public static Chest getChest(org.bukkit.block.Block chestblock) {
        return getState(chestblock, Chest.class);
    }

    public static Collection<BlockState> getBlockStates(org.bukkit.block.Block middle) {
        return getBlockStates(middle, 0, 0, 0);
    }

    public static Collection<BlockState> getBlockStates(org.bukkit.block.Block middle, int radius) {
        return getBlockStates(middle, radius, radius, radius);
    }

    public static Collection<BlockState> getBlockStates(org.bukkit.block.Block middle, int radiusX, int radiusY, int radiusZ) {
        return getBlockStates(middle.getWorld(), middle.getX(), middle.getY(), middle.getZ(), radiusX, radiusY, radiusZ);
    }

    private static final ArrayList<BlockState> blockStateBuff = new ArrayList<BlockState>();

    public static Collection<BlockState> getBlockStates(org.bukkit.World world, int x, int y, int z, int radiusX, int radiusY, int radiusZ) {
        try {
            if (radiusX == 0 && radiusY == 0 && radiusZ == 0) {
                // find a single BlockState at this position
                Object blockPosition = BlockPositionHandle.T.constr_x_y_z.raw.newInstance(x, y, z);
                Object tile = WorldHandle.T.getTileEntity.raw.invoke(HandleConversion.toWorldHandle(world), blockPosition);
                if (tile != null) {
                    blockStateBuff.add(WrapperConversion.toBlockState(tile));
                }
            } else {
                // loop through tile entity list
                int xMin = x - radiusX;
                int yMin = y - radiusY;
                int zMin = z - radiusZ;
                int xMax = x + radiusX;
                int yMax = y + radiusY;
                int zMax = z + radiusZ;

                // find range of chunk coordinates to look in
                int chunk_xMin = MathUtil.toChunk(xMin);
                int chunk_zMin = MathUtil.toChunk(zMin);
                int chunk_xMax = MathUtil.toChunk(xMax);
                int chunk_zMax = MathUtil.toChunk(zMax);
                for (int cx = chunk_xMin; cx <= chunk_xMax; cx++) {
                    for (int cz = chunk_zMin; cz <= chunk_zMax; cz++) {
                        // Find chunk if loaded
                        org.bukkit.Chunk chunk = WorldUtil.getChunk(world, cx, cz);
                        if (chunk == null) {
                            continue;
                        }

                        // Check tile entities held inside
                        ChunkBlockStateConverter converter = null;
                        Collection<?> rawTiles = ChunkHandle.T.getRawTileEntities.invoke(HandleConversion.toChunkHandle(chunk));
                        for (Object tile : rawTiles) {
                            BlockPositionHandle blockPosition;
                            blockPosition = BlockPositionHandle.createHandle(TileEntityHandle.T.position_field.raw.get(tile));
                            if (blockPosition.isPositionInBox(xMin, yMin, zMin, xMax, yMax, zMax)) {
                                if (converter == null) {
                                    converter = new ChunkBlockStateConverter(chunk);
                                }
                                blockStateBuff.add(converter.convert(tile));
                            }
                        }
                    }
                }
            }
            return new ArrayList<BlockState>(blockStateBuff);
        } finally {
            blockStateBuff.clear();
        }
    }

    /**
     * Gets whether a Block is solid and occludes light from passing through
     * 
     * @param block
     * @return True if solid
     */
    public static boolean isSolid(Block block) {
        return WorldUtil.getBlockData(block).isOccluding(block);
    }

    /**
     * Gets whether a Block is solid and entities inside will suffer suffocation damage
     * 
     * @param block
     * @return True if the block suffocates entities inside
     */
    public static boolean isSuffocating(Block block) {
        return WorldUtil.getBlockData(block).isSuffocating(block);
    }

    /**
     * Gets whether a Block provides a platform for blocks on top, such as torches.
     * 
     * @param block
     * @return True if the block supports blocks on top
     */
    public static boolean canSupportTop(Block block) {
        return WorldUtil.getBlockData(block).canSupportTop(block);
    }

    /**
     * Gets whether the lines that changed of a sign were the back lines, rather than the front lines.
     * Always returns true on MC &lt; 1.20.
     *
     * @param event
     * @return True if the front lines were changed, false if back lines (>= MC 1.20 only)
     */
    public static boolean isChangingFrontLines(SignChangeEvent event) {
        return SignHandle.isChangingFrontLines(event);
    }
}
