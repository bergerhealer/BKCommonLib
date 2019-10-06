package com.bergerkiller.bukkit.common.internal.legacy;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;

import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.utils.LogicUtil;

/**
 * Stores a mapping of all available materials by their material's name.
 * Legacy materials are prefixed with LEGACY_, also on older versions of Minecraft.
 */
public class MaterialsByName {
    private static final Material[] allMaterialValues;
    private static final Map<String, Material> allMaterialValuesByName = new HashMap<String, Material>();

    static {
        // Retrieve all Material values through reflection
        {
            Material[] values = null;
            try {
                values = (Material[]) Material.class.getMethod("values").invoke(null);
            } catch (Throwable t) {
                t.printStackTrace();
                values = Material.values();
            }

            // On MC 1.8 there is a LOCKED_CHEST Material that does not actually exist
            // It throws tests off the rails because of the Type Id clash it causes
            // By removing this rogue element from the array we can avoid these problems.
            if (values != null && CommonBootstrap.evaluateMCVersion("==", "1.8")) {
                for (int index = 0; index < values.length; index++) {
                    if (getMaterialName(values[index]).equals("LEGACY_LOCKED_CHEST")) {
                        values = LogicUtil.removeArrayElement(values, index);
                        break;
                    }
                }
            }

            allMaterialValues = values;
        }

        // Fill the allMaterialValuesByName map with LEGACY_ names and new names
        // On versions before 1.13, allow for legacy names in the map normally
        try {
            for (Material material :  MaterialsByName.getAllMaterials()) {
                allMaterialValuesByName.put(getMaterialName(material), material);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * Gets an array of all Material enum values, unaffected by the Spigot Material remapping.<br>
     * <b>Not suitable for use by Plugins</b>
     * 
     * @return all Material enum values
     */
    public static Material[] getAllMaterials() {
        return allMaterialValues;
    }

    /**
     * Gets the name() of a Material.
     * The Material remapping performed by Spigot on MC 1.13 is ignored.
     * 
     * @param type
     * @return type name
     */
    public static String getMaterialName(Material type) {
        if (CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            return ((Enum<?>) type).name();
        } else {
            return "LEGACY_" + ((Enum<?>) type).name();
        }
    }

    /**
     * Gets a material by name.
     * The Material remapping performed by Spigot on MC 1.13 is ignored.
     * 
     * @param name
     * @return material, null if not found
     */
    public static Material getMaterial(String name) {
        return allMaterialValuesByName.get(name);
    }
}
