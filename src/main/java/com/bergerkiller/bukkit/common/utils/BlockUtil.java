package com.bergerkiller.bukkit.common.utils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import net.minecraft.server.v1_4_R1.Block;
import net.minecraft.server.v1_4_R1.ChunkCoordinates;
import net.minecraft.server.v1_4_R1.Packet;
import net.minecraft.server.v1_4_R1.TileEntity;
import net.minecraft.server.v1_4_R1.TileEntityChest;
import net.minecraft.server.v1_4_R1.TileEntityDispenser;
import net.minecraft.server.v1_4_R1.TileEntityFurnace;
import net.minecraft.server.v1_4_R1.TileEntitySign;
import net.minecraft.server.v1_4_R1.World;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Furnace;
import org.bukkit.block.Sign;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.material.Attachable;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Rails;
import org.bukkit.material.Directional;
import com.bergerkiller.bukkit.common.reflection.classes.TileEntityRef;

/**
 * Multiple Block utilities you can use to manipulate blocks and get block information
 */
public class BlockUtil extends MaterialUtil {

	/**
	 * Directly obtains the Material Data from the block<br>
	 * This alternative does not create a Block State and is preferred if you only need material data
	 */
	public static MaterialData getData(org.bukkit.block.Block block) {
		return block.getType().getNewData(block.getData());
	}

	/**
	 * Gets the Material data for the block specified and attempts to cast it
	 * 
	 * @param block to get the Material data for
	 * @param type to cast to
	 * @return The cast material data, or null if there was no data or if casting failed
	 */
	public static <T> T getData(org.bukkit.block.Block block, Class<T> type) {
		try {
			return type.cast(getData(block));
		} catch (Exception ex) {
			return null;
		}
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
		if (checkY)
			d += Math.abs(b1.getBlockY() - b2.getBlockY());
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
		if (checkY)
			d += Math.abs(b1.getY() - b2.getY());
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
		if (block1 == null || block2 == null)
			return false;
		if (block1 == block2)
			return true;
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
	 * Gets the Chunk Coordinates of a block
	 * 
	 * @param block to use
	 * @return Chunk coordinates
	 */
	public static ChunkCoordinates getCoordinates(final org.bukkit.block.Block block) {
		return new ChunkCoordinates(block.getX(), block.getY(), block.getZ());
	}

	/**
	 * Gets the Block at the chunk coordinates specified
	 * 
	 * @param world of the block
	 * @param at coordinates
	 * @return Block a the coordinates in the world
	 */
	public static org.bukkit.block.Block getBlock(org.bukkit.World world, ChunkCoordinates at) {
		return world.getBlockAt(at.x, at.y, at.z);
	}

	/**
	 * Gets the Block of a certain tile entity
	 * 
	 * @param tileEntityto get the block of
	 * @return Tile Entity Block
	 */
	public static org.bukkit.block.Block getBlock(TileEntity tileEntity) {
		return tileEntity.world.getWorld().getBlockAt(tileEntity.x, tileEntity.y, tileEntity.z);
	}

	/**
	 * Gets the face an attachable block is attached to<br>
	 * Returns DOWN if the block is not attachable
	 * 
	 * @param attachable block
	 * @return Attached face
	 */
	public static BlockFace getAttachedFace(org.bukkit.block.Block attachable) {
		Attachable data = getData(attachable, Attachable.class);
		return data == null ? BlockFace.DOWN : data.getAttachedFace();
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
			block.setData(data.getData(), true);
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
		for (BlockFace dir : FaceUtil.ATTACHEDFACES) {
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
	public static boolean isLeverDown(org.bukkit.block.Block lever) {
		byte dat = lever.getData();
		return dat == (dat | 0x8);
	}

	/**
	 * Sets the toggled state of a single lever<br>
	 * <b>No Lever type check is performed</b>
	 * 
	 * @param lever block
	 * @param down state to set to
	 */
	public static void setLever(org.bukkit.block.Block lever, boolean down) {
		byte data = lever.getData();
		int newData;
		if (down) {
			newData = data | 0x8;
		} else {
			newData = data & 0x7;
		}
		if (newData != data) {
			// CraftBukkit start - Redstone event for lever
			int old = !down ? 1 : 0;
			int current = down ? 1 : 0;
			BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(lever, old, current);
			CommonUtil.callEvent(eventRedstone);
			if ((eventRedstone.getNewCurrent() > 0) != down) {
				return;
			}
			// CraftBukkit end
			lever.setData((byte) newData, true);
			applyPhysics(getAttachedBlock(lever), Material.LEVER);
		}
	}

	/**
	 * Causes a block to drop items as if it was broken
	 * 
	 * @param block to spawn the drops for
	 * @param yield of the drop
	 */
	public static void dropNaturally(org.bukkit.block.Block block, float yield) {
		Block b = Block.byId[block.getTypeId()];
		if (b != null) {
			b.dropNaturally(NativeUtil.getNative(block.getWorld()), block.getX(), block.getY(), block.getZ(), block.getData(), yield, 0);
		}
	}

	/**
	 * Performs Physics at the block specified
	 * 
	 * @param block to apply physics to
	 * @param callertype Material, the source of these physics (use Air if there is no caller)
	 */
	public static void applyPhysics(org.bukkit.block.Block block, Material callertype) {
		applyPhysics(block, callertype.getId());
	}

	/**
	 * Performs Physics at the block specified
	 * 
	 * @param block to apply physics to
	 * @param callertypeid of the Material, the source of these physics (use 0 if there is no caller)
	 */
	public static void applyPhysics(org.bukkit.block.Block block, int callertypeid) {
		NativeUtil.getNative(block.getWorld()).applyPhysics(block.getX(), block.getY(), block.getZ(), callertypeid);
	}

	/**
	 * Sets the alignment of Rails
	 * 
	 * @param rails to set the alignment for
	 * @param from direction
	 * @param to direction
	 */
	public static void setRails(org.bukkit.block.Block rails, BlockFace from, BlockFace to) {
		setRails(rails, FaceUtil.combine(from, to).getOppositeFace());
	}

	/**
	 * Sets the alignment of Rails
	 * 
	 * @param rails to set the alignment for
	 * @param alignment
	 */
	public static void setRails(org.bukkit.block.Block rails, BlockFace direction) {
		Material type = rails.getType();
		if (type == Material.RAILS) {
			BlockFace railsDirection;
			if (direction == BlockFace.NORTH) {
				railsDirection = BlockFace.SOUTH;
			} else if (direction == BlockFace.EAST) {
				railsDirection = BlockFace.WEST;
			} else {
				railsDirection = direction;
			}
			byte olddata = rails.getData();
			Rails r = (Rails) type.getNewData(olddata);
			r.setDirection(railsDirection, r.isOnSlope());
			byte newdata = r.getData();
			if (olddata != newdata) {
				rails.setData(newdata);
			}
		}
	}

	/**
	 * Gets the State of a block, cast to a certain type
	 * 
	 * @param block to get the state for
	 * @param type to cast to
	 * @return The block state cast to the type, or null if not possible
	 */
	public static <T extends BlockState> T getState(org.bukkit.block.Block block, Class<T> type) {
		return CommonUtil.tryCast(block.getState(), type);
	}

	public static Rails getRails(org.bukkit.block.Block railsblock) {
		return getData(railsblock, Rails.class);
	}

	public static Sign getSign(org.bukkit.block.Block signblock) {
		return getState(signblock, Sign.class);
	}

	public static Chest getChest(org.bukkit.block.Block chestblock) {
		return getState(chestblock, Chest.class);
	}

	public static <T extends TileEntity> T getTile(org.bukkit.block.Block block, Class<T> type) {
		try {
			return type.cast(NativeUtil.getNative(block.getWorld()).getTileEntity(block.getX(), block.getY(), block.getZ()));
		} catch (Exception ex) {
			return null;
		}
	}

	public static <T extends TileEntity> T getTile(BlockState block, Class<T> type) {
		try {
			return type.cast(NativeUtil.getNative(block.getWorld()).getTileEntity(block.getX(), block.getY(), block.getZ()));
		} catch (Exception ex) {
			return null;
		}
	}

	public static TileEntitySign getTile(Sign sign) {
		return getTile(sign, TileEntitySign.class);
	}

	public static TileEntityFurnace getTile(Furnace furnace) {
		return getTile(furnace, TileEntityFurnace.class);
	}

	public static TileEntityChest getTile(Chest chest) {
		return getTile(chest, TileEntityChest.class);
	}

	public static TileEntityDispenser getTile(Dispenser dispenser) {
		return getTile(dispenser, TileEntityDispenser.class);
	}

	public static TileEntitySign getTileSign(org.bukkit.block.Block block) {
		return getTile(block, TileEntitySign.class);
	}

	public static TileEntityChest getTileChest(org.bukkit.block.Block block) {
		return getTile(block, TileEntityChest.class);
	}

	public static TileEntityFurnace getTileFurnace(org.bukkit.block.Block block) {
		return getTile(block, TileEntityFurnace.class);
	}

	public static TileEntityDispenser getTileDispenser(org.bukkit.block.Block block) {
		return getTile(block, TileEntityDispenser.class);
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

	@SuppressWarnings("unchecked")
	public static Collection<BlockState> getBlockStates(org.bukkit.World world, int x, int y, int z, int radiusX, int radiusY, int radiusZ) {
		World nWorld = NativeUtil.getNative(world);
		if (radiusX == 0 && radiusY == 0 && radiusZ == 0) {
			// simplified coding instead
			TileEntity tile = nWorld.getTileEntity(x, y, z);
			if (tile != null) {
				offerTile(tile);
			}
		} else {
			// loop through tile entity list
			int xMin = x - radiusX;
			int yMin = y - radiusY;
			int zMin = z - radiusZ;
			int xMax = x + radiusX;
			int yMax = y + radiusY;
			int zMax = z + radiusZ;
			for (TileEntity tile : (List<TileEntity>) nWorld.tileEntityList) {
				if (tile.x < xMin || tile.y < yMin || tile.z < zMin || tile.x > xMax || tile.y > yMax || tile.z > zMax) {
					continue;
				}
				tile = nWorld.getTileEntity(tile.x, tile.y, tile.z);
				if (tile != null) {
					offerTile(tile);
				}
			}
		}
		// Convert back to Bukkit types
		ArrayList<BlockState> blocks = new ArrayList<BlockState>(tilebuff.size());
		for (TileEntity tile : tilebuff) {
			BlockState state = getBlock(tile).getState();
			if (state != null) {
				blocks.add(state);
			}
		}
		tilebuff.clear();
		return blocks;
	}

	public static org.bukkit.World getWorld(TileEntity tile) {
		return TileEntityRef.world.get(tile).getWorld();
	}

	public static Packet getUpdatePacket(TileEntity tile) {
		return tile.getUpdatePacket();
	}

	private static final LinkedHashSet<TileEntity> tilebuff = new LinkedHashSet<TileEntity>();
	private static void offerTile(TileEntity tile) {
		if (tile instanceof TileEntityChest) {
			// find a possible double chest as well
			World world = NativeUtil.getNative(getWorld(tile));
			int tmpx, tmpz;
			for (BlockFace sface : FaceUtil.AXIS) {
				tmpx = tile.x + sface.getModX();
				tmpz = tile.z + sface.getModZ();
				if (world.getTypeId(tmpx, tile.y, tmpz) == Material.CHEST.getId()) {
					TileEntity next = world.getTileEntity(tmpx, tile.y, tmpz);
					if (next != null && next instanceof TileEntityChest) {
						if (sface == BlockFace.WEST || sface == BlockFace.SOUTH) {
							tilebuff.add(next);
							tilebuff.add(tile);
						} else {
							tilebuff.add(tile);
							tilebuff.add(next);
						}
						return;
					}
				}
			}
		}
		tilebuff.add(tile);
	}

	/**
	 * Naturally breaks a given block
	 * 
	 * @param block to break
	 */
	public static void breakBlock(org.bukkit.block.Block block) {
		dropNaturally(block, 20.0f);
		block.setTypeId(0);
	}
}
