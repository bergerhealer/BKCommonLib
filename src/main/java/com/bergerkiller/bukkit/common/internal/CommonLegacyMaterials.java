package com.bergerkiller.bukkit.common.internal;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;

import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;

/**
 * Helper methods for all legacy Material API
 */
public class CommonLegacyMaterials {
    private static final HashMap<Integer, Material> idToMaterial = new HashMap<Integer, Material>();
    private static final EnumMap<Material, Integer> materialToId = new EnumMap<Material, Integer>(Material.class);
    private static final Map<String, Material> allMaterialValuesByName = new HashMap<String, Material>();
    private static final Material[] allMaterialValues;
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

        // Filter by only the legacy Material values
        if (CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            // Material names that start with LEGACY_ are legacy material values
            // We do not use isLegacy() on purpose when under test to avoid initialization of templates
            List<Material> legacyMaterials = new ArrayList<Material>();
            for (Material material : allMaterialValues) {
                if (isLegacy(material)) {
                    legacyMaterials.add(material);
                }
            }
            allLegacyMaterialValues = LogicUtil.toArray(legacyMaterials, Material.class);
        } else {
            // All material values are legacy materials
            allLegacyMaterialValues = allMaterialValues;
        }

        // Fill the allMaterialValuesByName map with LEGACY_ names and new names
        // On versions before 1.13, allow for legacy names in the map normally
        try {
            for (Material material : allMaterialValues) {
                allMaterialValuesByName.put(getMaterialName(material), material);
            }
        } catch (Throwable t) {
            t.printStackTrace();
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
        return allMaterialValuesByName.get(name);
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
        return allMaterialValues;
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
