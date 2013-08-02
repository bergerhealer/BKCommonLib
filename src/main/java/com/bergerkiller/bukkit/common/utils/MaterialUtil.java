package com.bergerkiller.bukkit.common.utils;

import net.minecraft.server.Block;
import net.minecraft.server.Item;

import org.bukkit.Material;
import org.bukkit.entity.Entity;

import com.bergerkiller.bukkit.common.MaterialBooleanProperty;
import com.bergerkiller.bukkit.common.MaterialProperty;
import com.bergerkiller.bukkit.common.MaterialTypeProperty;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.reflection.classes.BlockRef;

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
		for (Material type : types) {
			if (type.getId() == material) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether the material of a block is contained in the types
	 * 
	 * @param block to compare the types with
	 * @param types to look in
	 * @return True if the material is contained
	 */
	public static boolean isType(org.bukkit.block.Block block, Material... types) {
		return isType(block.getTypeId(), types);
	}

	/**
	 * Checks whether the material of a block is contained in the types
	 * 
	 * @param block to compare the types with
	 * @param types to look in
	 * @return True if the material is contained
	 */
	public static boolean isType(org.bukkit.block.Block block, int... types) {
		return isType(block.getTypeId(), types);
	}

	/**
	 * Gets the damage resilience of a block to damage dealt by a certain entity
	 * 
	 * @param blockId of the block
	 * @param source of the damage
	 * @return resilience
	 */
	public static float getDamageResilience(int blockId, Entity source) {
		Block block = (Block) BlockRef.getBlock(blockId);
		return block == null ? 0.0f : block.a(CommonNMS.getNative(source));
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
	public static final MaterialTypeProperty ISREDSTONETORCH = new MaterialTypeProperty(Material.REDSTONE_TORCH_OFF, Material.REDSTONE_TORCH_ON);

	/**
	 * The material is a type of diode (item type excluded)
	 */
	public static final MaterialTypeProperty ISDIODE = new MaterialTypeProperty(Material.DIODE_BLOCK_OFF, Material.DIODE_BLOCK_ON);

	/**
	 * The material is a type of comparator (item type excluded)
	 */
	public static final MaterialTypeProperty ISCOMPARATOR = new MaterialTypeProperty(Material.REDSTONE_COMPARATOR_OFF, Material.REDSTONE_COMPARATOR_ON);

	/**
	 * The material is a type of bucket (milk bucket is excluded)
	 */
	public static final MaterialTypeProperty ISBUCKET = new MaterialTypeProperty(Material.WATER_BUCKET, Material.LAVA_BUCKET, Material.BUCKET);

	/**
	 * The material is a type of rails
	 */
	public static final MaterialTypeProperty ISRAILS = new MaterialTypeProperty(Material.RAILS, Material.POWERED_RAIL, Material.DETECTOR_RAIL, Material.ACTIVATOR_RAIL);

	/**
	 * The material is a type of sign (item type is excluded)
	 */
	public static final MaterialTypeProperty ISSIGN = new MaterialTypeProperty(Material.WALL_SIGN, Material.SIGN_POST);

	/**
	 * The material is a type of pressure plate
	 */
	public static final MaterialTypeProperty ISPRESSUREPLATE = new MaterialTypeProperty(Material.WOOD_PLATE, Material.STONE_PLATE, Material.IRON_PLATE, Material.GOLD_PLATE);

	/**
	 * The material is a type of Minecart item
	 */
	public static final MaterialTypeProperty ISMINECART = new MaterialTypeProperty(Material.MINECART, Material.POWERED_MINECART, 
			Material.STORAGE_MINECART, Material.EXPLOSIVE_MINECART, Material.HOPPER_MINECART);

	/**
	 * The material is a type of wieldable sword
	 */
	public static final MaterialTypeProperty ISSWORD = new MaterialTypeProperty(Material.WOOD_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, 
			Material.GOLD_SWORD, Material.IRON_SWORD, Material.DIAMOND_SWORD);

	/**
	 * The material is a type of wearable boots
	 */
	public static final MaterialTypeProperty ISBOOTS = new MaterialTypeProperty(Material.LEATHER_BOOTS, Material.IRON_BOOTS, 
			Material.GOLD_BOOTS, Material.DIAMOND_BOOTS, Material.CHAINMAIL_BOOTS);

	/**
	 * The material is a type of wearable leggings
	 */
	public static final MaterialTypeProperty ISLEGGINGS = new MaterialTypeProperty(Material.LEATHER_LEGGINGS, Material.IRON_LEGGINGS, 
			Material.GOLD_LEGGINGS, Material.DIAMOND_LEGGINGS, Material.CHAINMAIL_LEGGINGS);

	/**
	 * The material is a type of wearable chestplate
	 */
	public static final MaterialTypeProperty ISCHESTPLATE = new MaterialTypeProperty(Material.LEATHER_CHESTPLATE, Material.IRON_CHESTPLATE, 
			Material.GOLD_CHESTPLATE, Material.DIAMOND_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE);

	/**
	 * The material is a type of wearable helmet
	 */
	public static final MaterialTypeProperty ISHELMET = new MaterialTypeProperty(Material.LEATHER_HELMET, Material.IRON_HELMET, 
			Material.GOLD_HELMET, Material.DIAMOND_HELMET, Material.CHAINMAIL_HELMET);

	/**
	 * The material is a type of armor
	 */
	public static final MaterialTypeProperty ISARMOR = new MaterialTypeProperty(ISBOOTS, ISLEGGINGS, ISCHESTPLATE, ISHELMET);

	/**
	 * The material can be interacted with, such as buttons and levers.
	 * Materials of this type suppress block placement upon interaction.
	 */
	public static final MaterialTypeProperty ISINTERACTABLE = new MaterialTypeProperty(Material.LEVER, Material.WOOD_DOOR, Material.IRON_DOOR, 
			Material.TRAP_DOOR, Material.CHEST, Material.HOPPER, Material.DROPPER, Material.ENDER_CHEST, Material.FURNACE, Material.BURNING_FURNACE,
			Material.DISPENSER, Material.WORKBENCH, Material.DIODE_BLOCK_ON, Material.DIODE_BLOCK_OFF, Material.BED, Material.CAKE, 
			Material.NOTE_BLOCK, Material.JUKEBOX, Material.WOOD_BUTTON, Material.STONE_BUTTON, Material.REDSTONE_COMPARATOR_OFF, 
			Material.REDSTONE_COMPARATOR_ON, Material.ANVIL, Material.FENCE_GATE);

	/**
	 * The material causes suffocation to entities inside
	 */
	public static final MaterialProperty<Boolean> SUFFOCATES = new MaterialBooleanProperty() {
		@Override
		public Boolean get(int typeId) {
			return Block.l(typeId);
		}
	};

	/**
	 * The material is a type of heatable item that can be crafted using a furnace
	 */
	public static final MaterialProperty<Boolean> ISHEATABLE = new MaterialBooleanProperty() {
		@Override
		public Boolean get(int typeId) {
			return RecipeUtil.isHeatableItem(typeId);
		}
	};

	/**
	 * The material is a type of fuel that can be burned in a furnace
	 */
	public static final MaterialProperty<Boolean> ISFUEL = new MaterialBooleanProperty() {
		@Override
		public Boolean get(int typeId) {
			return RecipeUtil.isFuelItem(typeId);
		}
	};

	/**
	 * The material is a solid block that lets no light through and on which other blocks can be placed
	 */
	public static final MaterialProperty<Boolean> ISSOLID = new MaterialBooleanProperty() {
		@Override
		public Boolean get(int typeId) {
			return Block.t[typeId];
		}
	};

	/**
	 * The material can supply redstone power and redstone wire connects to it
	 */
	public static final MaterialProperty<Boolean> ISPOWERSOURCE = new MaterialBooleanProperty() {
		@Override
		public Boolean get(int typeId) {
			final Block block = Block.byId[typeId];
			return block == null ? false : block.isPowerSource();
		}
	};

	/**
	 * The material has a data value that further defines the type of Item or Block
	 */
	public static final MaterialProperty<Boolean> HASDATA = new MaterialBooleanProperty() {
		@Override
		public Boolean get(int typeId) {
			final Item item = LogicUtil.getArray(Item.byId, typeId, null);
			return item == null ? false : item.n();
		}
	};

	/**
	 * Gets the amount of light a block material emits
	 */
	public static final MaterialProperty<Integer> EMISSION = new MaterialProperty<Integer>() {
		@Override
		public Integer get(int typeId) {
			return Block.lightEmission[typeId];
		}
	};

	/**
	 * Gets the opacity of a block material
	 */
	public static final MaterialProperty<Integer> OPACITY = new MaterialProperty<Integer>() {
		@Override
		public Integer get(int typeId) {
			return Block.lightBlock[typeId];
		}
	};
}
