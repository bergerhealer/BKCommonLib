package com.bergerkiller.bukkit.common.map;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.bergerkiller.bukkit.common.internal.CommonMapUUIDStore;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.map.util.ItemStackMapDisplayProperties;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.ItemUtil;

/**
 * Helper class to make getting and setting Map Item properties easier.
 * Changes to the properties are automatically synchronized to the map item,
 * which is persistently saved on the server in the background.<br>
 * <br>
 * Information that would normally be passed into a constructor can be passed
 * through these properties, using the item.
 */
public abstract class MapDisplayProperties {

    /**
     * Gets the ItemStack information of the map item.
     * Note that changes to this item will alter these properties.
     * This item can be given to players, or put in item frames,
     * and will then begin displaying contents.
     * 
     * @return map item
     */
    public abstract ItemStack getMapItem();

    /**
     * Gets the name of the plugin owner of the display.
     * This name is also available when the plugin is disabled.
     * 
     * @return name of the owning plugin, null if none is stored
     */
    public String getPluginName() {
        return getMetadata().getValue("mapDisplayPlugin", String.class, null);
    }

    /**
     * Gets the plugin owner of the display. Returns null if the plugin
     * is not enabled currently.
     * 
     * @return owning plugin
     */
    public Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugin(this.getPluginName());
    }

    /**
     * Gets the name of the map display class that is initialized when this
     * map display is viewed.
     * 
     * @return map display class name, null if none is stored
     */
    public String getMapDisplayClassName() {
        return getMetadata().getValue("mapDisplayClass", String.class, null);
    }

    /**
     * Gets the unique ID of the map display
     * 
     * @return unique id
     */
    public UUID getUniqueId() {
        return getMetadata().getUUID("mapDisplay");
    }

    /**
     * Gets the map display class initialized when this map display is viewed.
     * Is null if the Class could not be found, or the plugin is not enabled.
     * 
     * @return map display class, null if the plugin is not enabled or the class is not found
     */
    public Class<? extends MapDisplay> getMapDisplayClass() {
        String name = this.getMapDisplayClassName();
        if (name != null) {
            Plugin plugin = this.getPlugin();
            if (plugin != null && plugin.isEnabled()) {
                try {
                    return plugin.getClass().getClassLoader()
                            .loadClass(name).asSubclass(MapDisplay.class);
                } catch (ClassNotFoundException | ClassCastException e) {}
            }
        }

        return null;
    }

    /**
     * Gets the NBT Tag Compound that stores all the map display properties.
     * This tag can be modified to update properties stored in the underlying map
     * item.
     * 
     * @return map item properties NBT Tag Compound
     */
    public CommonTagCompound getMetadata() {
        CommonTagCompound tag = ItemUtil.getMetaTag(getMapItem(), false);
        if (tag == null) {
            throw new IllegalStateException("Map display item does not have metadata");
        }
        return tag;
    }

    /**
     * Gets or creates an NBT Tag Compound at the key specified.
     * This represents a group of properties all stored in a single block
     * at this key.
     * 
     * @param key Key at which to get or create the compound
     * @return NBT Tag Compound with the group of properties at the key
     */
    public CommonTagCompound compound(String key) {
        return getMetadata().createCompound(key);
    }

    /**
     * Checks whether the value of a property is contained
     * 
     * @param key Key of the property
     * @param type of the property
     * @return True if the value of this type is contained
     */
    public boolean containsKey(String key, Class<?> type) {
        return getMetadata().getValue(key, type) != null;
    }

    /**
     * Sets the value of a property
     * 
     * @param key Key of the property
     * @param value to set to
     */
    public void set(String key, Object value) {
        getMetadata().putValue(key, value);
    }

    /**
     * Gets the value of a property
     * 
     * @param key Key of the property
     * @param type of value to get
     * @return value at the key, null if not found
     */
    public <T> T get(String key, Class<T> type) {
        return getMetadata().getValue(key, type);
    }

    /**
     * Gets the value of a property
     * 
     * @param key Key of the property
     * @param defaultValue to return on failure (can not be null)
     * @return value at the key, defaultValue if not found
     */
    public <T> T get(String key, T defaultValue) {
        return getMetadata().getValue(key, defaultValue);
    }

    /**
     * Creates a map display properties view of a map item.
     * If the input item is invalid, and can't store map display data,
     * then null is returned instead.
     * 
     * @param mapItem The item to create a properties view of
     * @return map display properties, or null if the input item is null or has no metadata
     */
    public static MapDisplayProperties of(ItemStack mapItem) {
        CommonTagCompound metadata = ItemUtil.getMetaTag(mapItem, false);
        return (metadata == null) ? null : new ItemStackMapDisplayProperties(mapItem, metadata);
    }

    /**
     * Creates map display properties for a unique, new display.
     * A unique ID is generated and the plugin and map display class
     * to display the item to players is registered.<br>
     * <br>
     * The class must come from a plugin. If it is not, an
     * {@link IllegalArgumentException} is thrown.<br>
     * <br>
     * To obtain the final map display item, use {@link #getMapItem()}.
     * The map item will automatically initialize the Map Display class
     * when viewed.
     * 
     * @param mapDisplayClass The map display class to initialize when the item is viewed
     * @return new map display properties
     * @throws IllegalArgumentException If the map display class is not from a plugin, or lacks a no-args constructor
     * @throws UnsupportedOperationException If map displays are disabled in BKCommonLib's configuration
     */
    public static MapDisplayProperties createNew(Class<? extends MapDisplay> mapDisplayClass) {
        Plugin plugin = CommonUtil.getPluginByClass(mapDisplayClass);
        if (plugin == null) {
            throw new IllegalArgumentException("The class " + mapDisplayClass.getName() + " does not belong to a Java Plugin");
        }
        return createNew(plugin, mapDisplayClass);
    }

    /**
     * Creates map display properties for a unique, new display.
     * A unique ID is generated and the plugin and map display class
     * to display the item to players is registered.<br>
     * <br>
     * To obtain the final map display item, use {@link #getMapItem()}.
     * The map item will automatically initialize the Map Display class
     * when viewed.
     * 
     * @param plugin The plugin owner of the display
     * @param mapDisplayClass The map display class to initialize when the item is viewed
     * @return new map display properties
     * @throws IllegalArgumentException If the map display class lacks a no-args constructor
     * @throws UnsupportedOperationException If map displays are disabled in BKCommonLib's configuration
     */
    public static MapDisplayProperties createNew(Plugin plugin, Class<? extends MapDisplay> mapDisplayClass) {
        if (!CommonPlugin.getInstance().isMapDisplaysEnabled()) {
            throw new UnsupportedOperationException("Map displays are disabled in BKCommonLib's config.yml!");
        }
        try {
            mapDisplayClass.getDeclaredConstructor();
        } catch (Throwable t) {
            throw new IllegalArgumentException("The class " + mapDisplayClass.getName() + " does not have an empty constructor. Override onAttached() and use properties instead!");
        }

        ItemStack mapItem = ItemUtil.createItem(CommonMapUUIDStore.FILLED_MAP_TYPE, 1);
        CommonMapUUIDStore.setItemMapId(mapItem, 0);
        CommonTagCompound tag = ItemUtil.getMetaTag(mapItem, true);
        tag.putValue("mapDisplayPlugin", plugin.getName());
        tag.putValue("mapDisplayClass", mapDisplayClass.getName());
        tag.putUUID("mapDisplay", CommonMapUUIDStore.generateDynamicMapUUID());
        return of(mapItem);
    }
}
