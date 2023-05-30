package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import org.bukkit.Material;

import java.util.*;

/**
 * Base class for material properties that return a Boolean when getting.
 * Provides a basic implementation for toString to list all enabled (True)
 * elements
 */
public abstract class MaterialBooleanProperty extends MaterialProperty<Boolean> {

    /**
     * Gets an immutable collection of all Materials this property gets True on
     *
     * @return True material collection
     */
    public Collection<Material> getMaterials() {
        List<Material> mats = new ArrayList<Material>(20);
        for (Material mat : MaterialUtil.getAllMaterials()) {
            // Only include 1.13+ material names on 1.13+, exclude LEGACY_
            if (CommonCapabilities.MATERIAL_ENUM_CHANGES && MaterialUtil.isLegacyType(mat)) {
                continue;
            }

            if (Boolean.TRUE.equals(get(mat))) {
                mats.add(mat);
            }
        }

        return Collections.unmodifiableCollection(mats);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Material material : getMaterials()) {
            if (builder.length() > 0) {
                builder.append(';');
            }
            builder.append(material.toString().toLowerCase(Locale.ENGLISH));
        }
        return builder.toString();
    }
}
