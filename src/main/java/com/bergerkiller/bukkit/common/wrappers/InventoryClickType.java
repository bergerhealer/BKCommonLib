package com.bergerkiller.bukkit.common.wrappers;

/**
 * Wrapper for net.minecraft.server.InventoryClickType
 */
public enum InventoryClickType {
    /** Left/Right mouse click to take an item from the inventory */
    PICKUP(0),
    /** Shift + Left/Right mouse click to take items from the inventory */
    QUICK_MOVE(1),
    /** Number keys are used to swap items */
    SWAP(2),
    /** Middle-click clone operation */
    CLONE(3),
    /** Q to drop the item */
    THROW(4),
    /** Drag the item over the slots */
    QUICK_CRAFT(5),
    /** Double-click to pick up all items */
    PICKUP_ALL(6);

    private int _id;
    
    private InventoryClickType(int id) {
        this._id = id;
    }

    /**
     * Gets the ID token for the click type action
     * 
     * @return Id
     */
    public int getId() {
        return this._id;
    }

    /**
     * Gets the Inventory Click Type matching a given Id
     * 
     * @param id of the type
     * @return inventory click type, PICKUP by default if not matching any Ids
     */
    public static InventoryClickType byId(int id) {
        for (InventoryClickType value : values()) {
            if (value.getId() == id) {
                return value;
            }
        }
        return PICKUP;
    }
}
