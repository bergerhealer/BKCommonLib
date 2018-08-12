package com.bergerkiller.bukkit.common.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.MaterialBlockProperty;
import com.bergerkiller.bukkit.common.MaterialBooleanProperty;
import com.bergerkiller.bukkit.common.MaterialProperty;
import com.bergerkiller.bukkit.common.MaterialTypeProperty;
import com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.generated.net.minecraft.server.ItemHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.util.CraftMagicNumbersHandle;
import com.bergerkiller.mountiplex.reflection.declarations.SourceDeclaration;

import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

/**
 * Contains material properties and helper functions
 */
public class MaterialUtil {
    private static final Map<String, MaterialTypeProperty> TYPE_PROPERTIES;
    static {
        TYPE_PROPERTIES = new HashMap<String, MaterialTypeProperty>();

        // Load material_categories and send it through the macro pre-parser
        String material_categories_str = "";
        try {
            String mat_cat_path = "/com/bergerkiller/bukkit/common/internal/resources/material_categories.txt";
            try (InputStream input = MaterialUtil.class.getResourceAsStream(mat_cat_path)) {
                try (Scanner scanner = new Scanner(MaterialUtil.class.getResourceAsStream(mat_cat_path), "UTF-8")) {
                    scanner.useDelimiter("\\A");
                    material_categories_str = "#set version " + Common.TEMPLATE_RESOLVER.getVersion() + "\n" +
                                              scanner.next();
                    material_categories_str = SourceDeclaration.preprocess(material_categories_str);
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

        // Read the contents of the post-processed config file. Format:
        // NAME:
        //   VALUE1
        //   VALUE2
        //   etc.
        {
            String key = null;
            List<Material> values = new ArrayList<Material>();
            for (String line : material_categories_str.split("\\r?\\n")) {
                line = line.trim();
                if (line.startsWith("#") || line.isEmpty()) {
                    continue;
                }
                if (line.endsWith(":")) {
                    if (key != null) {
                        TYPE_PROPERTIES.put(key, new MaterialTypeProperty(values.toArray(new Material[values.size()])));
                    }
                    key = line.substring(0, line.length()-1);
                    values.clear();
                } else {
                    Material mat = getMaterial(line);
                    if (mat == null) {
                        throw new RuntimeException("Material type not found: " + line);
                    } else {
                        values.add(mat);
                    }
                }
            }
            if (key != null) {
                TYPE_PROPERTIES.put(key, new MaterialTypeProperty(values.toArray(new Material[values.size()])));
            }
        }
    }

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
        return block != null && WorldUtil.getBlockData(block).isType(types);
    }

    /**
     * Gets whether a particular Material type is legacy. When run on versions of Minecraft
     * before 1.13, this method always returns true.
     * 
     * @param type
     * @return True if the type is legacy
     */
    public static boolean isLegacyType(Material type) {
        return CraftMagicNumbersHandle.isLegacy(type);
    }

    /**
     * Gets the very first material name in the list that matches a valid material.
     * Throws a runtime exception when none of the names could be found.<br>
     * <br>
     * This assumes the 1.13 API, which means old legacy materials
     * can be obtained by prefixing LEGACY_. The LEGACY_ prefix is also required on older
     * versions of Minecraft.
     * 
     * @param names
     * @return first name in the list
     */
    public static Material getFirst(String... names) {
        for (String name : names) {
            Material m = getMaterial(name);
            if (m != null) {
                return m;
            }
        }
        throw new RuntimeException("None of the materials '" + String.join(", ", names) + "' could be found");
    }

    /**
     * Gets a Material by name.<br>
     * <br>
     * This assumes the 1.13 API, which means old legacy materials
     * can be obtained by prefixing LEGACY_. The LEGACY_ prefix is also required on older
     * versions of Minecraft.
     * 
     * @param name
     * @return Material
     */
    public static Material getMaterial(String name) {
        return CommonLegacyMaterials.getMaterial(name);
    }

    /**
     * The material is a type of door block.
     * Materials of this type are guaranteed to have a Door MaterialData.
     */
    public static final MaterialTypeProperty ISDOOR = TYPE_PROPERTIES.get("ISDOOR");

    /**
     * The material is a type of piston base
     */
    public static final MaterialTypeProperty ISPISTONBASE = TYPE_PROPERTIES.get("ISPISTONBASE");

    /**
     * The material is a type of redstone torch
     */
    public static final MaterialTypeProperty ISREDSTONETORCH = TYPE_PROPERTIES.get("ISREDSTONETORCH");

    /**
     * The material is a type of diode (item type excluded)
     */
    public static final MaterialTypeProperty ISDIODE = TYPE_PROPERTIES.get("ISDIODE");

    /**
     * The material is a type of button (item type excluded)
     */
    public static final MaterialTypeProperty ISBUTTON = TYPE_PROPERTIES.get("ISBUTTON");

    /**
     * The material is a type of comparator (item type excluded)
     */
    public static final MaterialTypeProperty ISCOMPARATOR = TYPE_PROPERTIES.get("ISCOMPARATOR");

    /**
     * The material is a type of bucket (milk bucket is excluded)
     */
    public static final MaterialTypeProperty ISBUCKET = TYPE_PROPERTIES.get("ISBUCKET");

    /**
     * The material is a type of rails
     */
    public static final MaterialTypeProperty ISRAILS = TYPE_PROPERTIES.get("ISRAILS");

    /**
     * The material is a type of sign (item type is excluded)
     */
    public static final MaterialTypeProperty ISSIGN = TYPE_PROPERTIES.get("ISSIGN");

    /**
     * The material is a type of pressure plate
     */
    public static final MaterialTypeProperty ISPRESSUREPLATE = TYPE_PROPERTIES.get("ISPRESSUREPLATE");

    /**
     * The material is a type of Minecart item
     */
    public static final MaterialTypeProperty ISMINECART = TYPE_PROPERTIES.get("ISMINECART");

    /**
     * The material is a type of wieldable sword
     */
    public static final MaterialTypeProperty ISSWORD = TYPE_PROPERTIES.get("ISSWORD");

    /**
     * The material is a type of wearable boots
     */
    public static final MaterialTypeProperty ISBOOTS = TYPE_PROPERTIES.get("ISBOOTS");

    /**
     * The material is a type of wearable leggings
     */
    public static final MaterialTypeProperty ISLEGGINGS = TYPE_PROPERTIES.get("ISLEGGINGS");

    /**
     * The material is a type of wearable chestplate
     */
    public static final MaterialTypeProperty ISCHESTPLATE = TYPE_PROPERTIES.get("ISCHESTPLATE");

    /**
     * The material is a type of wearable helmet
     */
    public static final MaterialTypeProperty ISHELMET = TYPE_PROPERTIES.get("ISHELMET");

    /**
     * The material is a type of armor
     */
    public static final MaterialTypeProperty ISARMOR = new MaterialTypeProperty(ISBOOTS, ISLEGGINGS, ISCHESTPLATE, ISHELMET);

    /**
     * The material can be interacted with, such as buttons and levers.
     * Materials of this type suppress block placement upon interaction.
     */
    public static final MaterialTypeProperty ISINTERACTABLE = TYPE_PROPERTIES.get("ISINTERACTABLE");

    /**
     * The material is water
     */
    public static final MaterialTypeProperty ISWATER = TYPE_PROPERTIES.get("ISWATER");

    /**
     * The material is lava
     */
    public static final MaterialTypeProperty ISLAVA = TYPE_PROPERTIES.get("ISLAVA");

    /**
     * The material is a liquid like water or lava
     */
    public static final MaterialTypeProperty ISLIQUID = new MaterialTypeProperty(ISWATER, ISLAVA);

    /**
     * The material is a type of Leaves
     */
    public static final MaterialTypeProperty ISLEAVES = TYPE_PROPERTIES.get("ISLEAVES");

    /**
     * The material causes suffocation to entities inside
     */
    public static final MaterialProperty<Boolean> SUFFOCATES = new MaterialBlockProperty<Boolean>() {
        @Override
        public Boolean get(BlockData blockData) {
            return blockData.isSuffocating();
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
    public static final MaterialProperty<Boolean> ISPOWERSOURCE = new MaterialBlockProperty<Boolean>() {
        @Override
        public Boolean get(BlockData blockData) {
            return blockData.isPowerSource();
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
    public static final MaterialProperty<Integer> EMISSION = new MaterialBlockProperty<Integer>() {
        @Override
        public Integer get(BlockData blockData) {
            return blockData.getEmission();
        }
    };

}
