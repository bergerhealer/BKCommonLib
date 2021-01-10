package com.bergerkiller.bukkit.common.internal.proxy;

/**
 * Used on Minecraft 1.8.8 and before to represent a dimension
 */
public final class DimensionManager_1_8_8 {
    public static final DimensionManager_1_8_8 OVERWORLD = new DimensionManager_1_8_8(0, "");
    public static final DimensionManager_1_8_8 NETHER = new DimensionManager_1_8_8(-1, "_nether");
    public static final DimensionManager_1_8_8 THE_END = new DimensionManager_1_8_8(1, "_end");

    private final int id;
    private final String suffix;

    private DimensionManager_1_8_8(int id, String suffix) {
        this.id = id;
        this.suffix = suffix;
    }

    public int getDimensionID() {
        return this.id;
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DimensionManager_1_8_8) {
            return ((DimensionManager_1_8_8) o).id == this.id;
        } else {
            return false;
        }
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
            return new DimensionManager_1_8_8(i, "");
        }
    }
}
