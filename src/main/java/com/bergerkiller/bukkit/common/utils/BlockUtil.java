package com.bergerkiller.bukkit.common.utils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.server.ChunkCoordinates;
import net.minecraft.server.Packet;
import net.minecraft.server.TileEntity;
import net.minecraft.server.TileEntityChest;
import net.minecraft.server.TileEntityDispenser;
import net.minecraft.server.TileEntityFurnace;
import net.minecraft.server.TileEntitySign;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Furnace;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.CraftWorld;
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
	public static MaterialData getData(Block block) {
		return block.getType().getNewData(block.getData());
	}

	/**
	 * Gets the Material data for the block specified and attempts to cast it
	 * 
	 * @param block to get the Material data for
	 * @param type to cast to
	 * @return The cast material data, or null if there was no data or if casting failed
	 */
	public static <T> T getData(Block block, Class<T> type) {
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
	public static int getManhattanDistance(Block b1, Block b2, boolean checkY) {
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
	public static boolean equals(Block block1, Block block2) {
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
	public static Block[] getRelative(Block main, BlockFace... faces) {
		if (main == null)
			return new Block[0];
		Block[] rval = new Block[faces.length];
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
	public static ChunkCoordinates getCoordinates(final Block block) {
		return new ChunkCoordinates(block.getX(), block.getY(), block.getZ());
	}

	/**
	 * Gets the Block at the chunk coordinates specified
	 * 
	 * @param world of the block
	 * @param at coordinates
	 * @return Block a the coordinates in the world
	 */
	public static Block getBlock(World world, ChunkCoordinates at) {
		return world.getBlockAt(at.x, at.y, at.z);
	}

	/**
	 * Gets the face an attachable block is attached to<br>
	 * Returns DOWN if the block is not attachable
	 * 
	 * @param attachable block
	 * @return Attached face
	 */
	public static BlockFace getAttachedFace(Block attachable) {
		Attachable data = getData(attachable, Attachable.class);
		return data == null ? BlockFace.DOWN : data.getAttachedFace();
	}

	/**
	 * Gets the Block an attachable block is attached to
	 * 
	 * @param attachable block
	 * @return Block the attachable is attached to
	 */
	public static Block getAttachedBlock(Block attachable) {
		return attachable.getRelative(getAttachedFace(attachable));
	}

	/**
	 * Gets the facing direction of a Directional block
	 * 
	 * @param directional block
	 * @return facing direction
	 */
	public static BlockFace getFacing(Block directional) {
		Directional data = getData(directional, Directional.class);
		return data == null ? BlockFace.NORTH : data.getFacing();
	}

	/**
	 * Sets the facing direction of a Directional block
	 * 
	 * @param block to set
	 * @param facing direction to set to
	 */
	public static void setFacing(Block block, BlockFace facing) {
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
	public static void setLeversAroundBlock(Block block, boolean down) {
		Block b;
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
	public static boolean isLeverDown(Block lever) {
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
	public static void setLever(Block lever, boolean down) {
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
	 * Performs Physics at the block specified
	 * 
	 * @param block to apply physics to
	 * @param callertype Material, the source of these physics (use Air if there is no caller)
	 */
	public static void applyPhysics(Block block, Material callertype) {
		applyPhysics(block, callertype.getId());
	}

	/**
	 * Performs Physics at the block specified
	 * 
	 * @param block to apply physics to
	 * @param callertypeid of the Material, the source of these physics (use 0 if there is no caller)
	 */
	public static void applyPhysics(Block block, int callertypeid) {
		NativeUtil.getNative(block.getWorld()).applyPhysics(block.getX(), block.getY(), block.getZ(), callertypeid);
	}

	/**
	 * Sets the alignment of Rails
	 * 
	 * @param rails to set the alignment for
	 * @param from direction
	 * @param to direction
	 */
	public static void setRails(Block rails, BlockFace from, BlockFace to) {
		setRails(rails, FaceUtil.combine(from, to).getOppositeFace());
	}

	/**
	 * Sets the alignment of Rails
	 * 
	 * @param rails to set the alignment for
	 * @param alignment
	 */
	public static void setRails(Block rails, BlockFace direction) {
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
	public static <T extends BlockState> T getState(Block block, Class<T> type) {
		return CommonUtil.tryCast(block.getState(), type);
	}

	public static Rails getRails(Block railsblock) {
		return getData(railsblock, Rails.class);
	}

	public static Sign getSign(Block signblock) {
		return getState(signblock, Sign.class);
	}

	public static Chest getChest(Block chestblock) {
		return getState(chestblock, Chest.class);
	}

	public static <T extends TileEntity> T getTile(Block block, Class<T> type) {
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

	public static TileEntitySign getTileSign(Block block) {
		return getTile(block, TileEntitySign.class);
	}

	public static TileEntityChest getTileChest(Block block) {
		return getTile(block, TileEntityChest.class);
	}

	public static TileEntityFurnace getTileFurnace(Block block) {
		return getTile(block, TileEntityFurnace.class);
	}

	public static TileEntityDispenser getTileDispenser(Block block) {
		return getTile(block, TileEntityDispenser.class);
	}

	public static Set<TileEntity> getTileEntities(Block middle) {
		return getTileEntities(middle, 0, 0, 0);
	}

	public static Set<TileEntity> getTileEntities(Block middle, int radius) {
		return getTileEntities(middle, radius, radius, radius);
	}

	public static Set<TileEntity> getTileEntities(Block middle, int radiusX, int radiusY, int radiusZ) {
		return getTileEntities(middle.getWorld(), middle.getX(), middle.getY(), middle.getZ(), radiusX, radiusY, radiusZ);
	}

	public static Set<TileEntity> getTileEntities(World world, int x, int y, int z, int radiusX, int radiusY, int radiusZ) {
		return getTileEntities(NativeUtil.getNative(world), x, y, z, radiusX, radiusY, radiusZ);
	}

	private static LinkedHashSet<TileEntity> tilebuff = new LinkedHashSet<TileEntity>();

	@SuppressWarnings("unchecked")
	public static Set<TileEntity> getTileEntities(net.minecraft.server.World world, int x, int y, int z, int radiusX, int radiusY, int radiusZ) {
		tilebuff.clear();
		if (radiusX == 0 && radiusY == 0 && radiusZ == 0) {
			// simplified coding instead
			TileEntity tile = world.getTileEntity(x, y, z);
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
			for (TileEntity tile : (List<TileEntity>) world.tileEntityList) {
				if (tile.x < xMin || tile.y < yMin || tile.z < zMin || tile.x > xMax || tile.y > yMax || tile.z > zMax) {
					continue;
				}
				tile = world.getTileEntity(tile.x, tile.y, tile.z);
				if (tile != null) {
					offerTile(tile);
				}
			}
		}
		return tilebuff;
	}

	public static World getWorld(TileEntity tile) {
		return TileEntityRef.world.get(tile).getWorld();
	}

	public static Packet getUpdatePacket(TileEntity tile) {
		return tile.getUpdatePacket();
	}

	private static void offerTile(TileEntity tile) {
		if (tile instanceof TileEntityChest) {
			// find a possible double chest as well
			net.minecraft.server.World world = NativeUtil.getNative(getWorld(tile));
			int tmpx, tmpz;
			for (BlockFace sface : FaceUtil.AXIS) {
				tmpx = tile.x + sface.getModX();
				tmpz = tile.z + sface.getModZ();
				if (world.getTypeId(tmpx, tile.y, tmpz) == Material.CHEST.getId()) {
					TileEntity next = world.getTileEntity(tmpx, tile.y, tmpz);
					if (next != null && next instanceof TileEntityChest) {
						if (sface == BlockFace.NORTH || sface == BlockFace.EAST) {
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
	public static void breakBlock(Block block) {
		int x = block.getX();
		int y = block.getY();
		int z = block.getZ();
		net.minecraft.server.World world = ((CraftWorld) block.getWorld()).getHandle();
		net.minecraft.server.Block bb = net.minecraft.server.Block.byId[block.getTypeId()];
		if (bb != null) {
			try {
				bb.dropNaturally(world, x, y, z, block.getData(), 20, 0);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		world.setTypeId(x, y, z, 0);
	}
}
