package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * Can compare a material with an internal whitelist of materials
 */
public class MaterialTypeProperty extends MaterialBooleanProperty {

    private Material[] allowedTypes;

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
        this.allowedTypes = elems.toArray(new Material[0]);
    }

    /**
     * Initializes a new material type property
     *
     * @param allowedMaterials to set
     */
    public MaterialTypeProperty(Material... allowedMaterials) {
        this.allowedTypes = new Material[allowedMaterials.length];
        System.arraycopy(allowedMaterials, 0, this.allowedTypes, 0, allowedMaterials.length);
    }

    @Override
    public Boolean get(Material type) {
        return MaterialUtil.isType(type, this.allowedTypes);
    }

    @Override
    public Boolean get(BlockData blockData) {
        if (CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            return get(blockData.getLegacyType()) || get(blockData.getType());
        } else {
            return super.get(blockData);
        }
    }

    @Override
    public Collection<Material> getMaterials() {
        return Collections.unmodifiableList(Arrays.asList(this.allowedTypes));
    }
}
