package com.bergerkiller.bukkit.common.map;

import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.wrappers.IntHashMap;

public class VirtualMapGlobal extends VirtualMap {
    private static final IntHashMap<VirtualMapGlobal> ALL_MAPS = new IntHashMap<VirtualMapGlobal>();

    /**
     * Called right after the virtual map becomes registered on the server
     */
    public void onAttached() {
    }

    /**
     * Called right before the virtual global map is de-registered from the server
     */
    public void onDetached() {
    }

    /**
     * Registers a new global virtual map, associating it with the map item specified.
     * If another map already exists for this item, it is deregistered.
     * 
     * @param mapItem to register for
     * @param map to register
     */
    public static void registerMap(ItemStack mapItem, VirtualMapGlobal map) {
        int itemId = getMapId(mapItem);
        if (itemId == -1) {
            return;
        }
        VirtualMapGlobal old_map = ALL_MAPS.get(itemId);
        if (old_map != null) {
            old_map.onDetached();
        }
        map.itemId = itemId;
        ALL_MAPS.put(itemId, map);
        map.onAttached();
    }

    /**
     * De-registers any global virtual map that was associated with an item.
     * 
     * @param mapItem to deregister
     */
    public static void deregisterMap(ItemStack mapItem) {
        int itemId = getMapId(mapItem);
        if (itemId == -1) {
            return;
        }
        VirtualMapGlobal map = ALL_MAPS.get(itemId);
        if (map != null) {
            map.onDetached();
            ALL_MAPS.remove(itemId);
        }
    }

    /**
     * Updates all global maps
     */
    public static void updateAll() {
        for (IntHashMap.Entry<VirtualMapGlobal> entry : ALL_MAPS.values()) {
            entry.getValue().update();
        }
    }
}
