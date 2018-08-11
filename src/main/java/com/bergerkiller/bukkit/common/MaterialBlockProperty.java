package com.bergerkiller.bukkit.common;

import org.bukkit.Material;

import com.bergerkiller.bukkit.common.wrappers.BlockData;

/**
 * Property for blocks, that can be retrieved from BlockData information
 * 
 * @param <T> property type
 */
public abstract class MaterialBlockProperty<T> extends MaterialProperty<T> {

    @Override
    public T get(Material material) {
        return get(BlockData.fromMaterial(material));
    }

    /**
     * Gets this property for the Block Data specified
     * 
     * @param blockData to get this property of
     * @return The property of the Block Data
     */
    public abstract T get(BlockData blockData);
}
