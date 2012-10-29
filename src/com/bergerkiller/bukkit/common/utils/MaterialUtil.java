package com.bergerkiller.bukkit.common.utils;

import org.bukkit.Material;
import org.bukkit.block.Block;

import com.bergerkiller.bukkit.common.MaterialProperty;
import com.bergerkiller.bukkit.common.MaterialTypeProperty;

/**
 * Contains material properties and helper functions
 */
public class MaterialUtil {
	/**
	 * Checks whether the material Id is contained in the types
	 * 
	 * @param material to check
	 * @param types to look in
	 * @return True if the material is contained
	 */
	public static boolean isType(int material, int... types) {
		return LogicUtil.containsInt(material, types);
	}

	/**
	 * Checks whether the material is contained in the types
	 * 
	 * @param material to check
	 * @param types to look in
	 * @return True if the material is contained
	 */
	public static boolean isType(Material material, Material... types) {
		return LogicUtil.contains(material, types);
	}

	/**
	 * Checks whether the material Id is contained in the types
	 * 
	 * @param material to check
	 * @param types to look in
	 * @return True if the material is contained
	 */
	public static boolean isType(int material, Material... types) {
		return isType(Material.getMaterial(material), types);
	}

	/**
	 * Checks whether the material of a block is contained in the types
	 * 
	 * @param block to compare the types with
	 * @param types to look in
	 * @return True if the material is contained
	 */
	public static boolean isType(Block block, Material... types) {
		return isType(block.getTypeId(), types);
	}

	/**
	 * Checks whether the material of a block is contained in the types
	 * 
	 * @param block to compare the types with
	 * @param types to look in
	 * @return True if the material is contained
	 */
	public static boolean isType(Block block, int... types) {
		return isType(block.getTypeId(), types);
	}

	/**
	 * The material is a type of door (iron or wooden door)
	 */
	public static final MaterialTypeProperty ISDOOR = new MaterialTypeProperty(Material.WOOD_DOOR, Material.IRON_DOOR);

	/**
	 * The material is a type of piston base
	 */
	public static final MaterialTypeProperty ISPISTONBASE = new MaterialTypeProperty(Material.PISTON_BASE, Material.PISTON_STICKY_BASE);

	/**
	 * The material is a type of redstone torch
	 */
	public static final MaterialTypeProperty ISREDSTONETORCH = new MaterialTypeProperty(Material.REDSTONE_TORCH_OFF, Material.REDSTONE_LAMP_ON);

	/**
	 * The material is a type of diode (item type excluded)
	 */
	public static final MaterialTypeProperty ISDIODE = new MaterialTypeProperty(Material.DIODE_BLOCK_OFF, Material.DIODE_BLOCK_ON);
	
	/**
	 * The material is a type of bucket (milk bucket is excluded)
	 */
	public static final MaterialTypeProperty ISBUCKET = new MaterialTypeProperty(Material.WATER_BUCKET, Material.LAVA_BUCKET, Material.BUCKET);

	/**
	 * The material is a type of rails
	 */
	public static final MaterialTypeProperty ISRAILS = new MaterialTypeProperty(Material.RAILS, Material.POWERED_RAIL, Material.DETECTOR_RAIL);

	/**
	 * The material is a type of sign (item type is excluded)
	 */
	public static final MaterialTypeProperty ISSIGN = new MaterialTypeProperty(Material.WALL_SIGN, Material.SIGN_POST);

	/**
	 * The material is a type of pressure plate
	 */
	public static final MaterialTypeProperty ISPRESSUREPLATE = new MaterialTypeProperty(Material.WOOD_PLATE, Material.STONE_PLATE);

	/**
	 * The material can be interacted with, such as buttons and levers
	 */
	public static final MaterialTypeProperty ISINTERACTABLE = new MaterialTypeProperty(Material.LEVER, Material.WOOD_DOOR, Material.IRON_DOOR, 
			Material.TRAP_DOOR, Material.CHEST, Material.ENDER_CHEST, Material.FURNACE, Material.BURNING_FURNACE, Material.DISPENSER, 
			Material.WORKBENCH, Material.DIODE_BLOCK_ON, Material.DIODE_BLOCK_OFF, Material.BED, Material.CAKE, Material.NOTE_BLOCK, Material.JUKEBOX);

	/**
	 * The material causes suffocation to entities inside
	 */
	public static final MaterialProperty<Boolean> SUFFOCATES = new MaterialProperty<Boolean>() {
		@Override
		public Boolean get(int typeId) {
			return net.minecraft.server.Block.i(typeId);
		}
	};

	/**
	 * The material is a solid block that lets no light through and on which other blocks can be placed
	 */
	public static final MaterialProperty<Boolean> ISSOLID = new MaterialProperty<Boolean>() {
		@Override
		public Boolean get(int typeId) {
			return net.minecraft.server.Block.q[typeId];
		}
	};

	/**
	 * The material can supply redstone power and redstone wire connects to it
	 */
	public static final MaterialProperty<Boolean> ISPOWERSOURCE = new MaterialProperty<Boolean>() {
		@Override
		public Boolean get(int typeId) {
			net.minecraft.server.Block block = net.minecraft.server.Block.byId[typeId];
			return block == null ? false : block.isPowerSource();
		}
	};

	/**
	 * Gets the opacity of a block material
	 */
	public static final MaterialProperty<Integer> OPACITY = new MaterialProperty<Integer>() {
		@Override
		public Integer get(int typeId) {
			return net.minecraft.server.Block.lightBlock[typeId];
		}
	};
}
