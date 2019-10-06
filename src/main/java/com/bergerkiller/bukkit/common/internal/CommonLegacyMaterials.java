package com.bergerkiller.bukkit.common.internal;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;

import com.bergerkiller.bukkit.common.internal.legacy.IBlockDataToMaterialData;
import com.bergerkiller.bukkit.common.internal.legacy.MaterialsByName;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.generated.net.minecraft.server.IBlockDataHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.util.CraftMagicNumbersHandle;
import com.bergerkiller.mountiplex.reflection.SafeMethod;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;

/**
 * Helper methods for all legacy Material API
 */
public class CommonLegacyMaterials {
    private static final HashMap<Integer, Material> idToMaterial = new HashMap<Integer, Material>();
    private static final EnumMap<Material, Integer> materialToId = new EnumMap<Material, Integer>(Material.class);
    private static final EnumMap<Material, Material> materialToLegacy = new EnumMap<Material, Material>(Material.class);
    private static final Material[] allLegacyMaterialValues;
    private static final FastMethod<Boolean> isLegacyMethod = new FastMethod<Boolean>();

    static {
        // This method gets whether a material is legacy, or not
        // On 1.12.2 and before, it always returns true
        // We do it this way so we don't have to initialize all templates to get this to work
        {
            String template = "public boolean isLegacy() {\n";
            if (CommonCapabilities.MATERIAL_ENUM_CHANGES) {
                template += "return instance.isLegacy();\n}";
            } else {
                template += "return true;\n}";
            }
            ClassResolver resolver = new ClassResolver();
            resolver.setDeclaredClass(Material.class);
            isLegacyMethod.init(new MethodDeclaration(resolver, template));
        }

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

        // Store a remapping from non-legacy materials to legacy materials
        // First store all materials to themselves
        for (Material material : MaterialsByName.getAllMaterials()) {
            materialToLegacy.put(material, material);
        }
        if (CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            // Use CraftLegacy toLegacy(material) to convert them by default
            // Some we override ourselves, which is done by IBlockDataToMaterialData utility class
            SafeMethod<Material> craftbukkitToLegacy = new SafeMethod<Material>(CommonUtil.getCBClass("util.CraftLegacy"), "toLegacy", Material.class);
            for (Material material :  MaterialsByName.getAllMaterials()) {
                if (isLegacy(material)) {
                    continue;
                }
                Material legacy = IBlockDataToMaterialData.toLegacy(material);
                if (legacy == material) {
                    legacy = craftbukkitToLegacy.invoke(null, material);
                }
                materialToLegacy.put(material, legacy);
            }
        }
    }

    /**
     * Gets whether a given Material enum value is legacy, or not.
     * Always returns true on MC 1.12.2 and before.
     * 
     * @param material
     * @return True if this is a legacy Material
     */
    public static boolean isLegacy(Material material) {
        return isLegacyMethod.invoke(material);
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
        return materialToLegacy.get(material);
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
     * Gets a legacy material by name. Prepends LEGACY_ on MC 1.13 and onwards for lookup.
     * The Material remapping performed by Spigot on MC 1.13 is ignored.
     * 
     * @param name
     * @return
     */
    public static Material getLegacyMaterial(String name) {
        return getMaterial("LEGACY_" + name);
    }

    /**
     * Gets a material by name.
     * The Material remapping performed by Spigot on MC 1.13 is ignored.
     * 
     * @param name
     * @return material, null if not found
     */
    public static Material getMaterial(String name) {
        return MaterialsByName.getMaterial(name);
    }

    /**
     * Gets the name() of a Material.
     * The Material remapping performed by Spigot on MC 1.13 is ignored.
     * 
     * @param type
     * @return type name
     */
    public static String getMaterialName(Material type) {
        return MaterialsByName.getMaterialName(type);
    }

    /**
     * Gets an array of materials from material enum names.
     * Any names missing will cause an exception.
     * The Material remapping performed by Spigot on MC 1.13 is ignored.<br>
     * <b>Not suitable for use by Plugins</b>
     * 
     * @param names
     * @return materials
     */
    public static Material[] getAllByName(String... names) {
        Material[] result = new Material[names.length];
        for (int i = 0; i < names.length; i++) {
            Material m = getMaterial(names[i]);
            if (m == null) {
                throw new RuntimeException("Material not found: " + names[i]);
            }
            result[i] = m;
        }
        return result;
    }

    /**
     * Gets an array of all Material enum values, unaffected by the Spigot Material remapping.<br>
     * <b>Not suitable for use by Plugins</b>
     * 
     * @return all Material enum values
     */
    public static Material[] getAllMaterials() {
        return MaterialsByName.getAllMaterials();
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

    /**
     * Helper method to retrieve IBlockData by Material enum name.
     * Only suitable for non-legacy names, and meant to be used before the BlockData API initializes.
     * 
     * @param name
     * @return IBlockData
     */
    public static IBlockDataHandle getBlockDataFromMaterialName(String name) {
        return CraftMagicNumbersHandle.getBlockDataFromMaterial(getMaterial(name));
    }
}
