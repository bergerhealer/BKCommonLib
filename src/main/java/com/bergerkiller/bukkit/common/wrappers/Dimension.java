package com.bergerkiller.bukkit.common.wrappers;

/**
 * A dimension in the Minecraft universe. As of writing this can be overworld (0), the end (1) and the nether (-1).
 * Dimensions beyond the nether and the end may be added in the future.
 */
public class Dimension {
    private final int _id;
    public static final Dimension OVERWORLD = new Dimension(0);
    public static final Dimension THE_END = new Dimension(1);
    public static final Dimension THE_NETHER = new Dimension(-1);

    private Dimension(int id) {
        this._id = id;
    }

    /**
     * Gets the Id of this Dimension
     * 
     * @return dimension Id
     */
    public int getId() {
        return this._id;
    }

    /**
     * Gets a dimension by its Id
     * 
     * @param id
     * @return dimension
     */
    public static Dimension fromId(int id) {
        switch (id) {
        case 0: return OVERWORLD;
        case 1: return THE_END;
        case -1: return THE_NETHER;
        default: return new Dimension(id); //TODO: Improvement needed?
        }
    }
}
