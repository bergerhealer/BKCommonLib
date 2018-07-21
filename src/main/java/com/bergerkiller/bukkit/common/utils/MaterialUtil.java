package com.bergerkiller.bukkit.common.utils;

import com.bergerkiller.bukkit.common.MaterialBooleanProperty;
import com.bergerkiller.bukkit.common.MaterialProperty;
import com.bergerkiller.bukkit.common.MaterialTypeProperty;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.generated.net.minecraft.server.ItemHandle;

import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

/**
 * Contains material properties and helper functions
 */
public class MaterialUtil {

    @Deprecated
    public static int getRawData(TreeSpecies treeSpecies) {
        return treeSpecies.getData();
    }

    @Deprecated
    public static int getRawData(org.bukkit.block.Block block) {
        return block.getData();
    }

    public static int getRawData(ItemStack item) {
        return item.getDurability();
    }

    @Deprecated
    public static int getRawData(MaterialData materialData) {
        return materialData.getData();
    }

    /**
     * Obtains the Material Data using the material type Id and data value
     * specified
     *
     * @param type of the material
     * @param rawData for the material
     * @return new MaterialData instance for this type of material and data
     */
    @Deprecated
    public static MaterialData getData(Material type, int rawData) {
        return BlockData.fromMaterialData(type, rawData).newMaterialData();
    }

    /**
     * Checks whether the material of the item is contained in the types
     *
     * @param itemStack containing the material type to check
     * @param types to look in
     * @return True if the material is contained
     */
    public static boolean isType(ItemStack itemStack, Material... types) {
        return itemStack != null && isType(itemStack.getType(), types);
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
     * Checks whether the material of a block is contained in the types
     *
     * @param block to compare the types with
     * @param types to look in
     * @return True if the material is contained
     */
    public static boolean isType(org.bukkit.block.Block block, Material... types) {
        return block != null && isType(block.getType(), types);
    }

    /**
     * Gets the very first material name in the list that matches a valid material
     * 
     * @param names
     * @return first name in the list
     */
    public static Material getFirst(String... names) {
        for (String name : names) {
            Material m = Material.getMaterial(name);
            if (m != null) {
                return m;
            }
        }
        throw new RuntimeException("None of the materials '" + String.join(", ", names) + "' could be found");
    }

    /**
     * Gets an array of materials from material enum names.
     * Any names missing will cause an exception.
     * 
     * @param names
     * @return materials
     */
    public static Material[] getAllByName(String... names) {
        Material[] result = new Material[names.length];
        for (int i = 0; i < names.length; i++) {
            Material m = Material.getMaterial(names[i]);
            if (m == null) {
                throw new RuntimeException("Material not found: " + names[i]);
            }
            result[i] = m;
        }
        return result;
    }

    /**
     * The material is a type of door block.
     * Materials of this type are guaranteed to have a Door MaterialData.
     */
    public static final MaterialTypeProperty ISDOOR = CommonCapabilities.MATERIAL_ENUM_CHANGES ?
            new MaterialTypeProperty(getAllByName("ACACIA_DOOR", "BIRCH_DOOR", "IRON_DOOR", "JUNGLE_DOOR", "OAK_DOOR", "SPRUCE_DOOR", "DARK_OAK_DOOR")) :
            new MaterialTypeProperty(getAllByName("IRON_DOOR_BLOCK", "WOODEN_DOOR", "SPRUCE_DOOR", "BIRCH_DOOR", "JUNGLE_DOOR", "ACACIA_DOOR", "DARK_OAK_DOOR"));

    /**
     * The material is a type of piston base
     */
    public static final MaterialTypeProperty ISPISTONBASE = new MaterialTypeProperty(
            getFirst("PISTON_BASE", "PISTON"), getFirst("PISTON_STICKY_BASE", "STICKY_PISTON"));

    /**
     * The material is a type of redstone torch
     */
    public static final MaterialTypeProperty ISREDSTONETORCH = CommonCapabilities.MATERIAL_ENUM_CHANGES ?
            new MaterialTypeProperty(Material.getMaterial("REDSTONE_TORCH"), Material.getMaterial("REDSTONE_WALL_TORCH")) :
            new MaterialTypeProperty(Material.getMaterial("REDSTONE_TORCH_OFF"), Material.getMaterial("REDSTONE_TORCH_ON"));

    /**
     * The material is a type of diode (item type excluded)
     */
    public static final MaterialTypeProperty ISDIODE = CommonCapabilities.MATERIAL_ENUM_CHANGES ?
            new MaterialTypeProperty(Material.getMaterial("REPEATER")) :
            new MaterialTypeProperty(Material.getMaterial("DIODE_BLOCK_OFF"), Material.getMaterial("DIODE_BLOCK_ON"));

    /**
     * The material is a type of button (item type excluded)
     */
    public static final MaterialTypeProperty ISBUTTON = CommonCapabilities.MATERIAL_ENUM_CHANGES ?
            new MaterialTypeProperty(getAllByName("STONE_BUTTON", "ACACIA_BUTTON", "BIRCH_BUTTON", "DARK_OAK_BUTTON", "JUNGLE_BUTTON", "OAK_BUTTON", "SPRUCE_BUTTON")) :
            new MaterialTypeProperty(getAllByName("STONE_BUTTON", "WOOD_BUTTON"));

    /**
     * The material is a type of comparator (item type excluded)
     */
    public static final MaterialTypeProperty ISCOMPARATOR = CommonCapabilities.MATERIAL_ENUM_CHANGES ?
            new MaterialTypeProperty(getAllByName("COMPARATOR")) :
            new MaterialTypeProperty(getAllByName("REDSTONE_COMPARATOR_OFF", "REDSTONE_COMPARATOR_ON"));  

    /**
     * The material is a type of bucket (milk bucket is excluded)
     */
    public static final MaterialTypeProperty ISBUCKET = new MaterialTypeProperty(Material.WATER_BUCKET, Material.LAVA_BUCKET, Material.BUCKET);

    /**
     * The material is a type of rails
     */
    public static final MaterialTypeProperty ISRAILS = new MaterialTypeProperty(getFirst("RAILS", "RAIL"), Material.POWERED_RAIL, Material.DETECTOR_RAIL, Material.ACTIVATOR_RAIL);

    /**
     * The material is a type of sign (item type is excluded)
     */
    public static final MaterialTypeProperty ISSIGN = new MaterialTypeProperty(Material.WALL_SIGN, getFirst("SIGN_POST", "SIGN"));

    /**
     * The material is a type of pressure plate
     */
    public static final MaterialTypeProperty ISPRESSUREPLATE = CommonCapabilities.MATERIAL_ENUM_CHANGES ?
            new MaterialTypeProperty(getAllByName("ACACIA_PRESSURE_PLATE", "BIRCH_PRESSURE_PLATE", "DARK_OAK_PRESSURE_PLATE",
                    "HEAVY_WEIGHTED_PRESSURE_PLATE", "JUNGLE_PRESSURE_PLATE", "LIGHT_WEIGHTED_PRESSURE_PLATE",
                    "OAK_PRESSURE_PLATE", "SPRUCE_PRESSURE_PLATE", "SPRUCE_PRESSURE_PLATE", "STONE_PRESSURE_PLATE")) :

            new MaterialTypeProperty(getAllByName("WOOD_PLATE", "STONE_PLATE", "IRON_PLATE", "GOLD_PLATE"));

    /**
     * The material is a type of Minecart item
     */
    public static final MaterialTypeProperty ISMINECART = CommonCapabilities.MATERIAL_ENUM_CHANGES ?
            new MaterialTypeProperty(getAllByName("MINECART", "FURNACE_MINECART", "CHEST_MINECART",
                    "TNT_MINECART", "HOPPER_MINECART", "COMMAND_BLOCK_MINECART")) :

            new MaterialTypeProperty(getAllByName("MINECART", "POWERED_MINECART", "STORAGE_MINECART",
                    "EXPLOSIVE_MINECART", "HOPPER_MINECART", "COMMAND_MINECART"));

    /**
     * The material is a type of wieldable sword
     */
    public static final MaterialTypeProperty ISSWORD = new MaterialTypeProperty(getFirst("WOODEN_SWORD", "WOOD_SWORD"), Material.STONE_SWORD, Material.IRON_SWORD,
            getFirst("GOLDEN_SWORD", "GOLD_SWORD"), Material.IRON_SWORD, Material.DIAMOND_SWORD);

    /**
     * The material is a type of wearable boots
     */
    public static final MaterialTypeProperty ISBOOTS = new MaterialTypeProperty(Material.LEATHER_BOOTS, Material.IRON_BOOTS,
            getFirst("GOLDEN_BOOTS", "GOLD_BOOTS"), Material.DIAMOND_BOOTS, Material.CHAINMAIL_BOOTS);

    /**
     * The material is a type of wearable leggings
     */
    public static final MaterialTypeProperty ISLEGGINGS = new MaterialTypeProperty(Material.LEATHER_LEGGINGS, Material.IRON_LEGGINGS,
            getFirst("GOLDEN_LEGGINGS", "GOLD_LEGGINGS"), Material.DIAMOND_LEGGINGS, Material.CHAINMAIL_LEGGINGS);

    /**
     * The material is a type of wearable chestplate
     */
    public static final MaterialTypeProperty ISCHESTPLATE = new MaterialTypeProperty(Material.LEATHER_CHESTPLATE, Material.IRON_CHESTPLATE,
            getFirst("GOLDEN_CHESTPLATE", "GOLD_CHESTPLATE"), Material.DIAMOND_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE);

    /**
     * The material is a type of wearable helmet
     */
    public static final MaterialTypeProperty ISHELMET = new MaterialTypeProperty(Material.LEATHER_HELMET, Material.IRON_HELMET,
            getFirst("GOLDEN_HELMET", "GOLD_HELMET"), Material.DIAMOND_HELMET, Material.CHAINMAIL_HELMET);

    /**
     * The material is a type of armor
     */
    public static final MaterialTypeProperty ISARMOR = new MaterialTypeProperty(ISBOOTS, ISLEGGINGS, ISCHESTPLATE, ISHELMET);

    /**
     * The material can be interacted with, such as buttons and levers.
     * Materials of this type suppress block placement upon interaction.
     */
    public static final MaterialTypeProperty ISINTERACTABLE = CommonCapabilities.MATERIAL_ENUM_CHANGES ?
            new MaterialTypeProperty(getAllByName("LEVER", "NOTE_BLOCK", "JUKEBOX", "ANVIL",
                    "CHEST", "HOPPER", "DROPPER", "ENDER_CHEST", "FURNACE",
                    "DISPENSER", "CRAFTING_TABLE", "REPEATER", "COMPARATOR", "CAKE",
                    /* Doors */
                    "ACACIA_DOOR", "BIRCH_DOOR", "IRON_DOOR", "JUNGLE_DOOR", "OAK_DOOR", "SPRUCE_DOOR", "DARK_OAK_DOOR",
                    /* Trap doors */
                    "ACACIA_TRAPDOOR", "BIRCH_TRAPDOOR", "DARK_OAK_TRAPDOOR", "IRON_TRAPDOOR",
                    "JUNGLE_TRAPDOOR", "OAK_TRAPDOOR", "SPRUCE_TRAPDOOR",
                    /* Buttons */
                    "ACACIA_BUTTON", "BIRCH_BUTTON", "DARK_OAK_BUTTON", "JUNGLE_BUTTON", "OAK_BUTTON", "SPRUCE_BUTTON",
                    "STONE_BUTTON",
                    /* Fence gates */
                    "ACACIA_FENCE_GATE", "BIRCH_FENCE_GATE", "DARK_OAK_FENCE_GATE", "JUNGLE_FENCE_GATE",
                    "OAK_FENCE_GATE", "SPRUCE_FENCE_GATE",
                    /* Beds */
                    "BLACK_BED", "BLUE_BED", "BROWN_BED", "CYAN_BED", "GRAY_BED", "GREEN_BED", "LIME_BED",
                    "MAGENTA_BED", "ORANGE_BED", "PINK_BED", "PURPLE_BED", "RED_BED", "WHITE_BED", "YELLOW_BED",
                    "LIGHT_BLUE_BED", "LIGHT_GRAY_BED")) :

            new MaterialTypeProperty(getAllByName("LEVER", "WOOD_DOOR", "IRON_DOOR",
                    "TRAP_DOOR", "CHEST", "HOPPER", "DROPPER", "ENDER_CHEST", "FURNACE", "BURNING_FURNACE",
                    "DISPENSER", "WORKBENCH", "DIODE_BLOCK_ON", "DIODE_BLOCK_OFF", "BED", "CAKE",
                    "NOTE_BLOCK", "JUKEBOX", "WOOD_BUTTON", "STONE_BUTTON", "REDSTONE_COMPARATOR_OFF",
                    "REDSTONE_COMPARATOR_ON", "ANVIL", "FENCE_GATE"));

    /**
     * The material causes suffocation to entities inside
     */
    public static final MaterialProperty<Boolean> SUFFOCATES = new MaterialBooleanProperty() {
        @Override
        public Boolean get(Material type) {
            return BlockData.fromMaterial(type).isSuffocating();
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
            return BlockData.fromMaterial(type).isOccluding();
        }
    };

    /**
     * The material can supply redstone power and redstone wire connects to it
     */
    public static final MaterialProperty<Boolean> ISPOWERSOURCE = new MaterialBooleanProperty() {
        @Override
        public Boolean get(Material type) {
            return BlockData.fromMaterial(type).isPowerSource();
        }
    };

    /**
     * The material has a data value that further defines the type of Item or
     * Block
     */
    public static final MaterialProperty<Boolean> HASDATA = new MaterialBooleanProperty() {
        @Override
        public Boolean get(Material type) {
            final ItemHandle item = CommonNMS.getItem(type);
            // return (this.durability > 0) && ((!this.m) || (this.maxStackSize == 1));
            return (item == null) ? false : item.usesDurability();
        }
    };

    /**
     * Gets the amount of light a block material emits
     */
    public static final MaterialProperty<Integer> EMISSION = new MaterialProperty<Integer>() {
        @Override
        public Integer get(Material type) {
            return BlockData.fromMaterial(type).getEmission();
        }
    };

    /**
     * Gets the opacity of a block material
     */
    public static final MaterialProperty<Integer> OPACITY = new MaterialProperty<Integer>() {
        @Override
        public Integer get(Material type) {
            return BlockData.fromMaterial(type).getOpacity();
        }
    };
}
