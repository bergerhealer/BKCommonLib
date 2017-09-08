package com.bergerkiller.bukkit.common.map;

import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.utils.ItemUtil;

/**
 * Helper class to make getting and setting Map Item properties easier
 */
public class MapDisplayProperties {
    private final MapDisplay display;

    public MapDisplayProperties(MapDisplay display) {
        this.display = display;
    }

    /**
     * Checks whether the value of a property is contained
     * 
     * @param key of the property
     * @param type of the property
     * @return True if the value of this type is contained
     */
    public boolean containsKey(String key, Class<?> type) {
        return getMetadata().getValue(key, type) != null;
    }

    /**
     * Sets the value of a property
     * 
     * @param key of the property
     * @param value to set to
     */
    public void set(String key, Object value) {
        getMetadata().putValue(key, value);
    }

    /**
     * Gets the value of a property
     * 
     * @param key of the property
     * @param type of value to get
     * @return value at the key, null if not found
     */
    public <T> T get(String key, Class<T> type) {
        return getMetadata().getValue(key, type);
    }

    /**
     * Gets the value of a property
     * 
     * @param key of the property
     * @param defaultValue to return on failure (can not be null)
     * @return value at the key, defaultValue if not found
     */
    public <T> T get(String key, T defaultValue) {
        return getMetadata().getValue(key, defaultValue);
    }

    private final CommonTagCompound getMetadata() {
        CommonTagCompound tag = ItemUtil.getMetaTag(display.getMapItem(), false);
        if (tag == null) {
            throw new IllegalStateException("Map display item does not have metadata");
        }
        return tag;
    }
}
