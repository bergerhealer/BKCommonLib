package com.bergerkiller.bukkit.common.utils;

import net.minecraft.server.v1_8_R1.Item;

import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Attachable;
import org.bukkit.material.MaterialData;

import com.bergerkiller.bukkit.common.MaterialBooleanProperty;
import com.bergerkiller.bukkit.common.MaterialProperty;
import com.bergerkiller.bukkit.common.MaterialTypeProperty;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.wrappers.BlockInfo;

/**
 * Contains material properties and helper functions
 */
public class MaterialUtil {

    /*
     * The below methods may have to be manually re-designed when
     * material IDs/Data officially become 'erased'.
     * But we know how Minecraft is addicted to them, so I bet they
     * still end up somewhere. Since they are valuable for fast equality
     * checks and thus mapping values to item materials/data, these methods
     * will stay, unless the default implementation has an equivalent such as
     * a UUID. Name mapping is NOT an option!
     * 
     * That said, for future compatibility, redirect all method calls to these
     * methods.
     */
    @SuppressWarnings("deprecation")
    public static Material getType(int typeId) {
        return Material.getMaterial(typeId);
    }
    
    public static Material getType(String blockname) {
        return Material.getMaterial(blockname);
    }

    @SuppressWarnings("deprecation")
    public static int getTypeId(ItemStack item) {
        return item.getTypeId();
    }

    @SuppressWarnings("deprecation")
    public static int getTypeId(org.bukkit.block.Block block) {
        return block.getTypeId();
    }

    @SuppressWarnings("deprecation")
    public static int getTypeId(Material material) {
        return material == null ? 0 : material.getId();
    }

    @SuppressWarnings("deprecation")
    public static int getRawData(TreeSpecies treeSpecies) {
        return treeSpecies.getData();
    }

    @SuppressWarnings("deprecation")
    public static int getRawData(org.bukkit.block.Block block) {
        return block.getData();
    }

    public static int getRawData(ItemStack item) {
        return item.getDurability();
    }

    @SuppressWarnings("deprecation")
    public static int getRawData(MaterialData materialData) {
        return materialData.getData();
    }

    /**
     * Obtains the Material Data using the material type Id and data value
     * specified.
     * <b>Please use the int data version instead, as Block data is expected to
     * become more than a byte!</b>
     *
     * @param typeId of the material
     * @param data for the material
     * @return new MaterialData instance for this type of material and data
     */
    public static MaterialData getData(int typeId, byte data) {
        return getData(typeId, (int) data);
    }

    /**
     * Obtains the Material Data using the material type Id and data value
     * specified
     *
     * @param typeId of the material
     * @param rawData for the material
     * @return new MaterialData instance for this type of material and data
     */
    @SuppressWarnings("deprecation")
    public static MaterialData getData(int typeId, int rawData) {
        Material type = Material.getMaterial(typeId);
        return type == null ? new MaterialData(typeId, (byte) rawData) : getData(type, rawData);
    }

    /**
     * Obtains the Material Data using the material type Id and data value
     * specified
     *
     * @param type of the material
     * @param rawData for the material
     * @return new MaterialData instance for this type of material and data
     */
    @SuppressWarnings("deprecation")
    public static MaterialData getData(Material type, int rawData) {
        if (type == null) {
            return new MaterialData(0, (byte) rawData);
        }
        final MaterialData mdata = type.getNewData((byte) rawData);

        // Fix attachable face returning NULL sometimes
        if (mdata instanceof Attachable) {
            Attachable att = (Attachable) mdata;
            if (att.getAttachedFace() == null) {
                att.setFacingDirection(BlockFace.NORTH);
            }
        }
        return mdata;
    }

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
     * Checks whether the material of the item is contained in the types
     *
     * @param itemStack containing the material type to check
     * @param types to look in
     * @return True if the material is contained
     */
    public static boolean isType(ItemStack itemStack, Material... types) {
        return isType(getTypeId(itemStack), types);
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
            if (getTypeId(type) == material) {
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
        return isType(getTypeId(block), types);
    }

    /**
     * Checks whether the material of a block is contained in the types
     *
     * @param block to compare the types with
     * @param types to look in
     * @return True if the material is contained
     */
    public static boolean isType(org.bukkit.block.Block block, int... types) {
        return isType(getTypeId(block), types);
    }

    /**
     * Gets the damage resilience of a block to damage dealt by a certain entity
     *
     * @param blockId of the block
     * @param source of the damage
     * @return resilience
     */
    @Deprecated
    public static float getDamageResilience(int blockId, Entity source) {
        return BlockInfo.get(blockId).getDamageResilience(source);
    }

    /**
     * The material is a type of door (iron or wooden door)
     */
    public static final MaterialTypeProperty ISDOOR = new MaterialTypeProperty(Material.WOOD_DOOR,
            Material.ACACIA_DOOR, Material.BIRCH_DOOR, Material.DARK_OAK_DOOR,
            Material.JUNGLE_DOOR, Material.SPRUCE_DOOR, Material.IRON_DOOR);

    /**
     * The material is a type of piston base
     */
    public static final MaterialTypeProperty ISPISTONBASE = new MaterialTypeProperty(Material.PISTON_BASE, Material.PISTON_STICKY_BASE);

    /**
     * The material is a type of redstone torch
     */
    public static final MaterialTypeProperty ISREDSTONETORCH = new MaterialTypeProperty(Material.REDSTONE_TORCH_OFF,
            Material.REDSTONE_TORCH_ON);

    /**
     * The material is a type of diode (item type excluded)
     */
    public static final MaterialTypeProperty ISDIODE = new MaterialTypeProperty(Material.DIODE_BLOCK_OFF, Material.DIODE_BLOCK_ON);

    /**
     * The material is a type of comparator (item type excluded)
     */
    public static final MaterialTypeProperty ISCOMPARATOR = new MaterialTypeProperty(Material.REDSTONE_COMPARATOR_OFF,
            Material.REDSTONE_COMPARATOR_ON);

    /**
     * The material is a type of bucket (milk bucket is excluded)
     */
    public static final MaterialTypeProperty ISBUCKET = new MaterialTypeProperty(Material.WATER_BUCKET,
            Material.LAVA_BUCKET, Material.BUCKET);

    /**
     * The material is a type of rails
     */
    public static final MaterialTypeProperty ISRAILS = new MaterialTypeProperty(Material.RAILS,
            Material.POWERED_RAIL, Material.DETECTOR_RAIL, Material.ACTIVATOR_RAIL);

    /**
     * The material is a type of sign (item type is excluded)
     */
    public static final MaterialTypeProperty ISSIGN = new MaterialTypeProperty(Material.WALL_SIGN, Material.SIGN_POST);

    /**
     * The material is a type of pressure plate
     */
    public static final MaterialTypeProperty ISPRESSUREPLATE = new MaterialTypeProperty(Material.WOOD_PLATE,
            Material.STONE_PLATE, Material.IRON_PLATE, Material.GOLD_PLATE);

    /**
     * The material is a type of Minecart item
     */
    public static final MaterialTypeProperty ISMINECART = new MaterialTypeProperty(Material.MINECART, Material.POWERED_MINECART,
            Material.STORAGE_MINECART, Material.EXPLOSIVE_MINECART, Material.HOPPER_MINECART);

    /**
     * The material is a type of wieldable sword
     */
    public static final MaterialTypeProperty ISSWORD = new MaterialTypeProperty(Material.WOOD_SWORD,
            Material.STONE_SWORD, Material.IRON_SWORD,
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
    public static final MaterialTypeProperty ISINTERACTABLE = new MaterialTypeProperty(Material.LEVER,
            Material.WOOD_DOOR, Material.ACACIA_DOOR, Material.BIRCH_DOOR,
            Material.DARK_OAK_DOOR, Material.JUNGLE_DOOR, Material.SPRUCE_DOOR, Material.IRON_DOOR,
            Material.TRAP_DOOR, Material.IRON_TRAPDOOR ,
            Material.CHEST, Material.TRAPPED_CHEST, Material.ENDER_CHEST,
            Material.HOPPER, Material.DROPPER,
            Material.FURNACE, Material.BURNING_FURNACE, Material.WORKBENCH, Material.ANVIL, Material.ENCHANTMENT_TABLE,
            Material.DISPENSER, Material.DIODE_BLOCK_ON, Material.DIODE_BLOCK_OFF,
            Material.BED, Material.CAKE,
            Material.NOTE_BLOCK, Material.JUKEBOX,
            Material.WOOD_BUTTON, Material.STONE_BUTTON,
            Material.REDSTONE_COMPARATOR_OFF, Material.REDSTONE_COMPARATOR_ON,
            Material.FENCE_GATE);

    /**
     * The material causes suffocation to entities inside
     */
    public static final MaterialProperty<Boolean> SUFFOCATES = new MaterialBooleanProperty() {
        @Override
        public Boolean get(Material type) {
            return BlockInfo.get(type).isSuffocating();
        }
    };

    /**
     * The material is a type of heatable item that can be crafted using a
     * furnace
     */
    public static final MaterialProperty<Boolean> ISHEATABLE = new MaterialBooleanProperty() {
        @Override
        public Boolean get(Material type) {
            return RecipeUtil.isHeatableItem(type);
        }
    };

    /**
     * The material is a type of fuel that can be burned in a furnace
     */
    public static final MaterialProperty<Boolean> ISFUEL = new MaterialBooleanProperty() {
        @Override
        public Boolean get(Material type) {
            return RecipeUtil.isFuelItem(type);
        }
    };

    /**
     * The material is a solid block that lets no light through and on which
     * other blocks can be placed
     */
    public static final MaterialProperty<Boolean> ISSOLID = new MaterialBooleanProperty() {
        @Override
        public Boolean get(Material type) {
            return BlockInfo.get(type).isSolid();
        }
    };

    /**
     * The material can supply redstone power and redstone wire connects to it
     */
    public static final MaterialProperty<Boolean> ISPOWERSOURCE = new MaterialBooleanProperty() {
        @Override
        public Boolean get(Material type) {
            return BlockInfo.get(type).isPowerSource();
        }
    };

    /**
     * The material has a data value that further defines the type of Item or
     * Block
     */
    public static final MaterialProperty<Boolean> HASDATA = new MaterialBooleanProperty() {
        @Override
        public Boolean get(Material type) {
            final Item item = CommonNMS.getItem(type);
            return item == null ? false : item.k();
        }
    };

    /**
     * Gets the amount of light a block material emits
     */
    public static final MaterialProperty<Integer> EMISSION = new MaterialProperty<Integer>() {
        @Override
        public Integer get(Material type) {
            return BlockInfo.get(type).getLightEmission();
        }
    };

    /**
     * Gets the opacity of a block material
     */
    public static final MaterialProperty<Integer> OPACITY = new MaterialProperty<Integer>() {
        @Override
        public Integer get(Material type) {
            return BlockInfo.get(type).getOpacity();
        }
    };
}
