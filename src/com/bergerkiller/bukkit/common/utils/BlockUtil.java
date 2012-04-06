package com.bergerkiller.bukkit.common.utils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.server.ChunkCoordinates;
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
import org.bukkit.material.Attachable;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Rails;
import org.bukkit.material.Directional;

public class BlockUtil {
    
    /*
     * Prevents the need to read the lighting when using getState()
     * Can be a little bit faster :)
     */
    public static MaterialData getData(Block block) {
    	return block.getType().getNewData(block.getData());
    }
    public static <T extends MaterialData> T getData(Block block, Class<T> type) {
    	try {
    		return type.cast(getData(block));
    	} catch (Exception ex) {
    		return null;
    	}
    }
    
    public static int getBlockSteps(Location b1, Location b2, boolean checkY) {
    	int d = Math.abs(b1.getBlockX() - b2.getBlockX());
    	d += Math.abs(b1.getBlockZ() - b2.getBlockZ());
    	if (checkY) d +=  Math.abs(b1.getBlockY() - b2.getBlockY());
    	return d;
    }
    public static int getBlockSteps(Block b1, Block b2, boolean checkY) {
    	int d = Math.abs(b1.getX() - b2.getX());
    	d += Math.abs(b1.getZ() - b2.getZ());
    	if (checkY) d +=  Math.abs(b1.getY() - b2.getY());
    	return d;
    }
    
    public static boolean equals(Block block1, Block block2) {
    	if (block1 == null || block2 == null) return false;
    	if (block1 == block2) return true;
    	return block1.getX() == block2.getX() && block1.getZ() == block2.getZ()
    			&& block1.getY() == block2.getY() && block1.getWorld() == block2.getWorld();    	
    }
        
    public static Block[] getRelative(Block main, BlockFace... faces) {
    	if (main == null) return new Block[0];
    	Block[] rval = new Block[faces.length];
    	for (int i = 0; i < rval.length; i++) {
    		rval[i] = main.getRelative(faces[i]);
    	}
    	return rval;
    }
    public static ChunkCoordinates getCoordinates(final Block block) {
    	return new ChunkCoordinates(block.getX(), block.getY(), block.getZ());
    }
    public static Block getBlock(World world, ChunkCoordinates at) {
    	return world.getBlockAt(at.x, at.y, at.z);
    }
    
    public static BlockFace getAttachedFace(Block attachable) {
    	MaterialData data = getData(attachable);
    	if (data != null && data instanceof Attachable) {
    		return ((Attachable) data).getAttachedFace();
    	}
    	return BlockFace.DOWN;
    }
    
	public static BlockFace getFacing(Block b) {
		MaterialData data = getData(b);
		if (data != null && data instanceof Directional) {
			return ((Directional) data).getFacing();
		} else {
			return BlockFace.NORTH;
		}
	}
	public static void setFacing(Block block, BlockFace facing) {
		MaterialData data = getData(block);
		if (data != null && data instanceof Directional) {
			((Directional) data).setFacingDirection(facing);
			block.setData(data.getData(), true);
		}
	}
	
    public static Block getAttachedBlock(Block b) {
    	return b.getRelative(getAttachedFace(b));
    }
    
    public static void setLeversAroundBlock(Block block, boolean down) {
    	Block b;
		for (BlockFace dir : FaceUtil.attachedFaces) {
			if (isType(b = block.getRelative(dir), Material.LEVER)) {
				//attached?
				if (getAttachedFace(b) == dir.getOppositeFace()) {
					setLever(b, down, false);
				}
			}
		}
    }
    
    public static void setLever(Block lever, boolean down) {
    	setLever(lever, down, true);
    }
    public static void setLever(Block lever, boolean down, boolean checktype) {
    	if (!checktype || lever.getTypeId() == Material.LEVER.getId()) {
			byte data = lever.getData();
	        int newData;
	        if (down) {
	        	newData = data | 0x8;
	        } else {
	        	newData = data & 0x7;
	        }
	        if (newData != data) {
	            lever.setData((byte) newData, true);
		        applyPhysics(getAttachedBlock(lever), Material.LEVER);
	        }
    	}
    }
    
    public static void applyPhysics(Block block, Material callertype) {
    	applyPhysics(block, callertype.getId());
    }
    public static void applyPhysics(Block block, int callertypeid) {
    	WorldUtil.getNative(block.getWorld()).applyPhysics(block.getX(), block.getY(), block.getZ(), callertypeid);
    }
    
    public static void setRails(Block rails, BlockFace from, BlockFace to) {
    	setRails(rails, FaceUtil.combine(from, to).getOppositeFace());
    }
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
    	return CommonUtil.contains(material, types);
    }
    public static boolean isType(Material material, Material... types) {
    	return CommonUtil.contains(material, types);
    }
    public static boolean isType(int material, Material... types) {
    	int[] inttypes = new int[types.length];
    	for (int i = 0; i < types.length; i++) inttypes[i] = types[i].getId();
    	return CommonUtil.contains(material, inttypes);
    }
    public static boolean isType(Block block, Material... types) {
    	return isType(block.getTypeId(), types);
    }
    public static boolean isType(Block block, int... types) {
    	return CommonUtil.contains(block.getTypeId(), types);
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
    
	public static <T extends BlockState> T getState(Block block, Class<T> type) {
    	try {
    		return type.cast(block.getState());
    	} catch (Exception ex) {
    		return null;
    	}
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
			//simplified coding instead
			TileEntity tile = world.getTileEntity(x, y, z);
			if (tile != null) offerTile(tile);
		} else {
			//loop through tile entity list
			x -= radiusX;
			y -= radiusY;
			z -= radiusZ;
			radiusX = x + radiusX * 2;
			radiusY = y + radiusY * 2;
			radiusZ = z + radiusZ * 2;
			for (TileEntity tile : (List<TileEntity>) world.tileEntityList) {
				if (tile.x < x || tile.y < y || tile.z < z) continue;
				if (tile.x > radiusX || tile.y > radiusY || tile.z > radiusZ) continue;
				tile = world.getTileEntity(tile.x, tile.y, tile.z);
				if (tile != null) {
					offerTile(tile);
				}
			}
		}
		return tilebuff;
	}
	private static void offerTile(TileEntity tile) {
		if (tile instanceof TileEntityChest) {
			//find a possible double chest as well
			int tmpx, tmpz;
			for (BlockFace sface : FaceUtil.axis) {
				tmpx = tile.x + sface.getModX();
				tmpz = tile.z + sface.getModZ();
				if (tile.world.getTypeId(tmpx, tile.y, tmpz) == Material.CHEST.getId()) {
					TileEntity next = tile.world.getTileEntity(tmpx, tile.y, tmpz);
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
