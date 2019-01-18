package com.bergerkiller.bukkit.common.wrappers;

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
     * Gets the dimension manager handle that is used on MC 1.9 and later.
     * Returns null on MC 1.8.8 and before.
     * 
     * @return dimension manager handle
     */
    public abstract Object getDimensionManagerHandle();

    /**
     * Gets whether this Dimension is serializable from/to an internal ID representation.
     * Some dimensions are introduced by Spigot/CraftBukkit code to support flat worlds,
     * causing errors when serialized and restored from/to the ID.
     * 
     * @return True if this dimension is serializable
     */
    public abstract boolean isSerializable();

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
     * Gets a dimension by its 1.9+ DimensionManager handle.
     * 
     * @param dimensionManagerHandle
     * @return Dimension
     */
    public static Dimension fromDimensionManagerHandle(Object dimensionManagerHandle) {
        if (dimensionManagerHandle == null || !DimensionManagerHandle.T.isAvailable()) {
            return null;
        }

        if (dimensionManagerHandle == OVERWORLD.getDimensionManagerHandle()) {
            return OVERWORLD;
        } else if (dimensionManagerHandle == THE_END.getDimensionManagerHandle()) {
            return THE_END;
        } else if (dimensionManagerHandle == THE_NETHER.getDimensionManagerHandle()) {
            return THE_NETHER;
        }

        int id = DimensionManagerHandle.T.getId.invoke(dimensionManagerHandle);
        boolean serializable = false;
        try {
            serializable = (DimensionManagerHandle.T.fromId.invoke(id) != null);
        } catch (IllegalArgumentException ex) {
            // Possible <= MC 1.13
        }
        return new DimensionImpl(dimensionManagerHandle, id, serializable);
    }

    // Uses internal lookup table, if available
    private static Dimension fromIdFallback(int id) {
        if (DimensionManagerHandle.T.isAvailable()) {
            try {
                Object handle = DimensionManagerHandle.T.fromId.invoke(id);
                if (handle != null) {
                    return new DimensionImpl(handle, id, true);
                }
            } catch (IllegalArgumentException ex) {
                // Possible <= MC 1.13
            }

            // Dimension Manager is used but the Id is invalid - fallback
            return new DimensionLegacyImpl(id, false);
        } else {
            return new DimensionLegacyImpl(id, true);
        }
    }

    private static class DimensionImpl extends Dimension {
        private final Object _handle;
        private final int _id;
        private final boolean _serializable;

        public DimensionImpl(Object dimensionManagerHandle, int id, boolean serializable) {
            this._handle = dimensionManagerHandle;
            this._id = id;
            this._serializable = serializable;
        }

        @Override
        public int getId() {
            return this._id;
        }

        @Override
        public Object getDimensionManagerHandle() {
            return this._handle;
        }

        @Override
        public boolean isSerializable() {
            return this._serializable;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Dimension) {
                return ((Dimension) o).getDimensionManagerHandle() == this._handle;
            } else {
                return false;
            }
        }

        @Override
        public String toString() {
            if (this.isSerializable()) {
                try {
                    return this._handle.toString() + "(" + this._id + ")";
                } catch (Throwable t) {
                    // Bug in the server for unmapped types. Ew.
                    // The isSerializable() method should detect it, but just in case.
                }
            }

            return "UNKNOWN(" + this._id + ")";
        }
    }

    private static class DimensionLegacyImpl extends Dimension {
        private final int _id;
        private final boolean _serializable;

        public DimensionLegacyImpl(int id, boolean serializable) {
            this._id = id;
            this._serializable = serializable;
        }

        @Override
        public int getId() {
            return this._id;
        }

        @Override
        public Object getDimensionManagerHandle() {
            return null;
        }

        @Override
        public boolean isSerializable() {
            return this._serializable;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Dimension) {
                return ((Dimension) o).getId() == this._id;
            } else {
                return false;
            }
        }

        @Override
        public String toString() {
            String name;
            switch (this._id) {
            case -1: name = "THE_NETHER"; break;
            case 0: name = "OVERWORLD"; break;
            case 1: name = "THE_END"; break;
            default: name = "UNKNOWN"; break;
            }
            return name + "(" + this._id + ")";
        }
    }

}
