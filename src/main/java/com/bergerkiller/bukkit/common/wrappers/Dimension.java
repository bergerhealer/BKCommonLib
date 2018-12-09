package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.generated.net.minecraft.server.DimensionManagerHandle;

/**
 * A dimension in the Minecraft universe. As of writing this can be overworld (0), the end (1) and the nether (-1).
 * Dimensions beyond the nether and the end may be added in the future.
 */
public abstract class Dimension {
    public static final Dimension OVERWORLD = fromIdFallback(0);
    public static final Dimension THE_END = fromIdFallback(1);
    public static final Dimension THE_NETHER = fromIdFallback(-1);

    protected Dimension() {
    }

    /**
     * Gets the Id of this Dimension
     * 
     * @return dimension Id
     */
    public abstract int getId();

    /**
     * Gets the dimension manager handle that is used on MC 1.13.2 and later.
     * Returns null on MC 1.13.1 and before.
     * 
     * @return dimension manager handle
     */
    public abstract Object getDimensionManagerHandle();

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
        default: return fromIdFallback(id);
        }
    }

    /**
     * Gets a dimension by its 1.13.2+ DimensionManager handle.
     * 
     * @param dimensionManagerHandle
     * @return Dimension
     */
    public static Dimension fromDimensionManagerHandle(Object dimensionManagerHandle) {
        if (dimensionManagerHandle == null) {
            return null;
        }
        int id = DimensionManagerHandle.T.getId.invoke(dimensionManagerHandle);
        Dimension suggestedDim;
        switch (id) {
        case 0: suggestedDim = OVERWORLD; break;
        case 1: suggestedDim = THE_END; break;
        case -1: suggestedDim = THE_NETHER; break;
        default: suggestedDim = OVERWORLD; break;
        }
        if (suggestedDim.getDimensionManagerHandle() == dimensionManagerHandle) {
            return suggestedDim;
        } else {
            return new DimensionImpl(dimensionManagerHandle, id);
        }
    }

    private static Dimension fromIdFallback(int id) {
        if (CommonCapabilities.HAS_DIMENSION_MANAGER) {
            Object handle = DimensionManagerHandle.T.fromId.invoke(id);
            if (handle == null) {
                throw new IllegalArgumentException("Unknown dimension id: " + id);
            }
            return new DimensionImpl(handle, id);
        } else {
            return new DimensionLegacyImpl(id);
        }
    }

    private static class DimensionImpl extends Dimension {
        private final Object _handle;
        private final int _id;

        public DimensionImpl(Object dimensionManagerHandle, int id) {
            this._handle = dimensionManagerHandle;
            this._id = id;
        }

        @Override
        public int getId() {
            return this._id;
        }

        @Override
        public Object getDimensionManagerHandle() {
            return this._handle;
        }
    }

    private static class DimensionLegacyImpl extends Dimension {
        private final int _id;

        public DimensionLegacyImpl(int id) {
            this._id = id;
        }

        @Override
        public int getId() {
            return this._id;
        }

        @Override
        public Object getDimensionManagerHandle() {
            return null;
        }
    }

}
