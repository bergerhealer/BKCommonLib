package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * Can compare a material with an internal whitelist of materials
 */
public class MaterialTypeProperty extends MaterialBooleanProperty {

    private final Collection<Material> allowedTypes;
    private final IntHashMap<Boolean> ordinals = new IntHashMap<Boolean>();
    private final IntHashMap<Boolean> legacyOrdinals = new IntHashMap<Boolean>();

    /**
     * Initializes a new material type property containing all types from the
     * other type properties specified
     *
     * @param properties to set the types of
     */
    public MaterialTypeProperty(MaterialTypeProperty... properties) {
        HashSet<Material> elems = new HashSet<Material>();
        for (MaterialTypeProperty prop : properties) {
            for (Material mat : prop.allowedTypes) {
                elems.add(mat);
            }
        }
        this.allowedTypes = Collections.unmodifiableList(Arrays.asList(elems.toArray(new Material[0])));
        buildOrdinals();
    }

    /**
     * Initializes a new material type property
     *
     * @param allowedMaterials to set
     */
    public MaterialTypeProperty(Material... allowedMaterials) {
        this.allowedTypes = Collections.unmodifiableList(Arrays.asList(allowedMaterials.clone()));
        buildOrdinals();
    }

    /**
     * Initializes a new material type property from an array of material names.<br>
     * <br>
     * This assumes the 1.13 API, which means old legacy materials
     * can be obtained by prefixing LEGACY_. The LEGACY_ prefix is also required on older
     * versions of Minecraft.
     *
     * @param allowedMaterials names to set
     */
    public MaterialTypeProperty(String... allowedMaterials) {
        ArrayList<Material> mats = new ArrayList<Material>(allowedMaterials.length);
        for (String name : allowedMaterials) {
            Material mat = MaterialUtil.getMaterial(name);
            if (mat != null) {
                mats.add(mat);
            }
        }
        this.allowedTypes = Collections.unmodifiableList(Arrays.asList(LogicUtil.toArray(mats, Material.class)));
        buildOrdinals();
    }

    private void buildOrdinals() {
        for (Material type : this.allowedTypes) {
            if (CommonCapabilities.MATERIAL_ENUM_CHANGES && CommonLegacyMaterials.isLegacy(type)) {
                legacyOrdinals.put(CommonLegacyMaterials.getOrdinal(type), Boolean.TRUE);
            } else {
                ordinals.put(CommonLegacyMaterials.getOrdinal(type), Boolean.TRUE);
            }
        }
    }

    @Override
    public Boolean get(Material type) {
        if (CommonCapabilities.MATERIAL_ENUM_CHANGES && CommonLegacyMaterials.isLegacy(type)) {
            return legacyOrdinals.contains(CommonLegacyMaterials.getOrdinal(type));
        } else {
            return ordinals.contains(CommonLegacyMaterials.getOrdinal(type));
        }
    }

    @Override
    public Boolean get(BlockData blockData) {
        return ordinals.contains(CommonLegacyMaterials.getOrdinal(blockData.getType())) ||
                ( CommonCapabilities.MATERIAL_ENUM_CHANGES &&
                  legacyOrdinals.contains(CommonLegacyMaterials.getOrdinal(blockData.getLegacyType())) &&
                  blockData.hasLegacyType() );
    }

    @Override
    public Collection<Material> getMaterials() {
        return allowedTypes;
    }
}
