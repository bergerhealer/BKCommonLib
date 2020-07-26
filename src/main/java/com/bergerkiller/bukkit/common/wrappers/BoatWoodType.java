package com.bergerkiller.bukkit.common.wrappers;

/**
 * Type of material a boat is made out of
 */
public enum BoatWoodType {
    OAK, SPRUCE, BIRCH, JUNGLE, ACACIA, DARK_OAK;

    private static final BoatWoodType[] VALUES = values();

    /**
     * Gets the Id of the wood type
     * 
     * @return id (internal use)
     */
    public int getId() {
        return this.ordinal();
    }

    /**
     * Gets the wood type of a given id
     * 
     * @param id (internal use)
     * @return wood type
     */
    public static BoatWoodType byId(int id) {
        if (id < 0 || id >= VALUES.length) {
            return OAK;
        } else {
            return VALUES[id];
        }
    }
}
