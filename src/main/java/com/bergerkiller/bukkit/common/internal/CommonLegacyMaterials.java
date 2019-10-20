package com.bergerkiller.bukkit.common.internal;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;

import com.bergerkiller.bukkit.common.internal.legacy.IBlockDataToMaterialData;
import com.bergerkiller.bukkit.common.internal.legacy.MaterialsByName;
import com.bergerkiller.bukkit.common.utils.LogicUtil;

/**
 * Helper methods for all legacy Material API
 */
public class CommonLegacyMaterials extends MaterialsByName {
    private static final HashMap<Integer, Material> idToMaterial = new HashMap<Integer, Material>();
    private static final EnumMap<Material, Integer> materialToId = new EnumMap<Material, Integer>(Material.class);
    private static final Material[] allLegacyMaterialValues;

    static {
        // Filter by only the legacy Material values
        if (CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            // Material names that start with LEGACY_ are legacy material values
            // We do not use isLegacy() on purpose when under test to avoid initialization of templates
            List<Material> legacyMaterials = new ArrayList<Material>();
            for (Material material : MaterialsByName.getAllMaterials()) {
                if (isLegacy(material)) {
                    legacyMaterials.add(material);
                }
            }
            allLegacyMaterialValues = LogicUtil.toArray(legacyMaterials, Material.class);
        } else {
            // All material values are legacy materials
            allLegacyMaterialValues =  MaterialsByName.getAllMaterials();
        }

        if (CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            // On MC 1.13 can we store anything at all? No.

        } else {
            // Stores all material mapping on 1.12.2 as is
            try {
                java.lang.reflect.Method m = Material.class.getDeclaredMethod("getId");
                for (Material mat : getAllMaterials()) {
                    int id = ((Integer) m.invoke(mat)).intValue();
                    idToMaterial.put(id, mat);
                    materialToId.put(mat, id);
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    /**
     * Gets the legacy material type that is the closest approximation of a non-legacy material type.
     * Legacy materials will resolve to themselves.
     * For example, for blocks of wood that did not exist, a close approximation will be chosen that did.
     * 
     * @param material
     * @return legacy material type
     */
    public static Material toLegacy(Material material) {
        return IBlockDataToMaterialData.toLegacy(material);
    }

    /**
     * Gets the ordinal() of a Material, bypassing Spigot's hooking.
     * 
     * @param material
     * @return ordinal
     */
    public static int getOrdinal(Material material) {
        return ((Enum<?>) material).ordinal();
    }

    /**
     * Gets an array of all Material enum values, only storing legacy Material types.
     * These are Materials that exist on MC 1.12.2 and before.
     * 
     * @return all Legacy Material enum values
     */
    public static Material[] getAllLegacyMaterials() {
        return allLegacyMaterialValues;
    }
    
    /**
     * Converts (old) legacy material ids to the Material types. On MC 1.13 and onwards, this
     * method will fail with a runtime exception. DO NOT USE. Backwards compatibility only!
     * 
     * @param id
     * @return material, null if not matched
     */
    @Deprecated
    public static Material getMaterialFromId(int id) {
        if (CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            throw new UnsupportedOperationException("Material Ids are no longer supported on Minecraft 1.13 and onwards");
        }
        return idToMaterial.get(id);
    }

    /**
     * Converts material enum values to their respective legacy material type Ids. On MC 1.13 and onwards, this
     * method will fail with a runtime exception. DO NOT USE. Backwards compatibility only!
     * 
     * @param type
     * @return material type id, -1 for null
     */
    @Deprecated
    public static int getIdFromMaterial(Material type) {
        if (CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            throw new UnsupportedOperationException("Material Ids are no longer supported on Minecraft 1.13 and onwards");
        }
        return materialToId.get(type);
    }

}
