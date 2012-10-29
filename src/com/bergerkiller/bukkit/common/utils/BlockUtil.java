package com.bergerkiller.bukkit.common.utils;

import java.util.HashSet;
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
public class BlockUtil {
	private static final Set<Integer> interactableBlocks = new HashSet<Integer>();

	static {
		interactableBlocks.add(Material.LEVER.getId());
		interactableBlocks.add(Material.WOODEN_DOOR.getId());
		interactableBlocks.add(Material.IRON_DOOR.getId());
		interactableBlocks.add(Material.TRAP_DOOR.getId());
		interactableBlocks.add(Material.CHEST.getId());
		interactableBlocks.add(Material.FURNACE.getId());
		interactableBlocks.add(Material.BURNING_FURNACE.getId());
		interactableBlocks.add(Material.DISPENSER.getId());
		interactableBlocks.add(Material.WORKBENCH.getId());
		interactableBlocks.add(Material.DIODE_BLOCK_ON.getId());
		interactableBlocks.add(Material.DIODE_BLOCK_OFF.getId());
		interactableBlocks.add(Material.BED.getId());
		interactableBlocks.add(Material.CAKE.getId());
		interactableBlocks.add(Material.NOTE_BLOCK.getId());
		interactableBlocks.add(Material.JUKEBOX.getId());
	}

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
		for (BlockFace dir : FaceUtil.attachedFaces) {
			if (isType(b = block.getRelative(dir), Material.LEVER)) {
				// attached?
				if (getAttachedFace(b) == dir.getOppositeFace()) {
					setLever(b, down);
				}
			}
		}
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
		WorldUtil.getNative(block.getWorld()).applyPhysics(block.getX(), block.getY(), block.getZ(), callertypeid);
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
			if (direction == BlockFace.NORTH) {
				direction = BlockFace.SOUTH;
			} else if (direction == BlockFace.EAST) {
				direction = BlockFace.WEST;
			}
			byte olddata = rails.getData();
			Rails r = (Rails) type.getNewData(olddata);
			r.setDirection(direction, r.isOnSlope());
			byte newdata = r.getData();
			if (olddata != newdata) {
				rails.setData(newdata);
			}
		}
	}

	public static boolean isType(int material, int... types) {
		return LogicUtil.containsInt(material, types);
	}

	public static boolean isType(Material material, Material... types) {
		return LogicUtil.contains(material, types);
	}

	public static boolean isType(int material, Material... types) {
		return isType(Material.getMaterial(material), types);
	}

	public static boolean isType(Block block, Material... types) {
		return isType(block.getTypeId(), types);
	}

	public static boolean isType(Block block, int... types) {
		return isType(block.getTypeId(), types);
	}

	public static boolean isSign(Material material) {
		return isType(material, Material.WALL_SIGN, Material.SIGN_POST);
	}

	public static boolean isSign(Block b) {
		return b == null ? false : isSign(b.getType());
	}

	public static boolean isRails(Material type) {
		return isType(type, Material.RAILS, Material.POWERED_RAIL, Material.DETECTOR_RAIL);
	}

	public static boolean isRails(int type) {
		return isType(type, Material.RAILS.getId(), Material.POWERED_RAIL.getId(), Material.DETECTOR_RAIL.getId());
	}

	public static boolean isRails(Block b) {
		return b == null ? false : isRails(b.getTypeId());
	}

	public static boolean isPowerSource(Material type) {
		return isPowerSource(type.getId());
	}

	/**
	 * Checks if a given Material type Id is a power source<br>
	 * Redstone wire attaches to power sources
	 * 
	 * @param typeId to check it for
	 * @return True if it is a power source, False if not
	 */
	public static boolean isPowerSource(int typeId) {
		net.minecraft.server.Block block = net.minecraft.server.Block.byId[typeId];
		return block == null ? false : block.isPowerSource();
	}

	/**
	 * Checks if a given Type is a Bucket<br>
	 * Note: The milk bucket is excluded
	 * 
	 * @param type of the Material
	 * @return True if it is a type of Bucket, False if not
	 */
	public static boolean isBucket(Material type) {
		return isType(type, Material.BUCKET, Material.LAVA_BUCKET, Material.WATER_BUCKET);
	}

	/**
	 * Checks if a given Type id is a Bucket<br>
	 * Note: The milk bucket is excluded
	 * 
	 * @param typeId of the Material
	 * @return True if it is a type of Bucket, False if not
	 */
	public static boolean isBucket(int typeId) {
		return isType(typeId, Material.BUCKET, Material.LAVA_BUCKET, Material.WATER_BUCKET);
	}

	/**
	 * Checks if a given material Type can be interacted with
	 * 
	 * @param type of the Material
	 * @return True if it is Interactable, False if not
	 */
	public static boolean isInteractable(Material type) {
		return isInteractable(type.getId());
	}

	/**
	 * Checks if a given material Type id can be interacted with
	 * 
	 * @param typeId of the Material
	 * @return True if it is Interactable, False if not
	 */
	public static boolean isInteractable(int typeId) {
		return interactableBlocks.contains(typeId);
	}

	/**
	 * Checks if a given material Type is solid and can cause player suffocation
	 * 
	 * @param type of the Material
	 * @return True if it is Solid, False if not
	 */
	public static boolean isSolid(Material type) {
		return isSolid(type.getId());
	}

	/**
	 * Checks if a given material Type id is solid and can cause player suffocation
	 * 
	 * @param typeId of the Material
	 * @return True if it is Solid, False if not
	 */
	public static boolean isSolid(int typeId) {
		return net.minecraft.server.Block.i(typeId);
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
			return type.cast(WorldUtil.getNative(block.getWorld()).getTileEntity(block.getX(), block.getY(), block.getZ()));
		} catch (Exception ex) {
			return null;
		}
	}

	public static <T extends TileEntity> T getTile(BlockState block, Class<T> type) {
		try {
			return type.cast(WorldUtil.getNative(block.getWorld()).getTileEntity(block.getX(), block.getY(), block.getZ()));
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
		return getTileEntities(WorldUtil.getNative(world), x, y, z, radiusX, radiusY, radiusZ);
	}

	private static LinkedHashSet<TileEntity> tilebuff = new LinkedHashSet<TileEntity>();

	@SuppressWarnings("unchecked")
	public static Set<TileEntity> getTileEntities(net.minecraft.server.World world, int x, int y, int z, int radiusX, int radiusY, int radiusZ) {
		tilebuff.clear();
		if (radiusX == 0 && radiusY == 0 && radiusZ == 0) {
			// simplified coding instead
			TileEntity tile = world.getTileEntity(x, y, z);
			if (tile != null)
				offerTile(tile);
		} else {
			// loop through tile entity list
			x -= radiusX;
			y -= radiusY;
			z -= radiusZ;
			radiusX = x + radiusX * 2;
			radiusY = y + radiusY * 2;
			radiusZ = z + radiusZ * 2;
			for (TileEntity tile : (List<TileEntity>) world.tileEntityList) {
				if (tile.x < x || tile.y < y || tile.z < z)
					continue;
				if (tile.x > radiusX || tile.y > radiusY || tile.z > radiusZ)
					continue;
				tile = world.getTileEntity(tile.x, tile.y, tile.z);
				if (tile != null) {
					offerTile(tile);
				}
			}
		}
		return tilebuff;
	}

	public static net.minecraft.server.World getWorld(TileEntity tile) {
		return TileEntityRef.world.get(tile);
	}

	public static Packet getUpdatePacket(TileEntity tile) {
		return tile.l();
	}

	private static void offerTile(TileEntity tile) {
		if (tile instanceof TileEntityChest) {
			// find a possible double chest as well
			net.minecraft.server.World world = getWorld(tile);
			int tmpx, tmpz;
			for (BlockFace sface : FaceUtil.axis) {
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
