package com.bergerkiller.bukkit.common.internal.proxy;

/**
 * Used on Minecraft 1.8.8 and before to represent a dimension
 */
public enum DimensionManager_1_8_8 {
    OVERWORLD(0, ""),
    NETHER(-1, "_nether"),
    THE_END(1, "_end");
    
    private final int id;
    private final String suffix;

    private DimensionManager_1_8_8(int id, String suffix) {
        this.id = id;
        this.suffix = suffix;
    }

    public int getDimensionID() {
        return this.id;
    }

    // getSuffix
    public String c() {
        return this.suffix;
    }

    // fromId
    public static DimensionManager_1_8_8 a(int i) {
        switch (i) {
        case 0:
            return OVERWORLD;
        case -1:
            return NETHER;
        case 1:
            return THE_END;
        default:
            throw new IllegalArgumentException("Invalid dimension id " + i);
        }
    }
}
