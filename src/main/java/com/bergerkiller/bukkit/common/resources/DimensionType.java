package com.bergerkiller.bukkit.common.resources;

import com.bergerkiller.bukkit.common.wrappers.BasicWrapper;
import com.bergerkiller.generated.net.minecraft.server.DimensionManagerHandle;

/**
 * A type of dimension in the Minecraft universe. As of writing this can be overworld (0), the end (1) and the nether (-1).
 * Dimensions beyond the nether and the end may be added in the future.
 * It is discouraged to access these dimensions by their id's (-1, 0, 1) since this is legacy behavior
 * and as of Minecraft 1.16 will no longer work for any new dimensions.
 */
public final class DimensionType extends BasicWrapper<DimensionManagerHandle> {
    public static final DimensionType OVERWORLD = fromIdFallback(0);
    public static final DimensionType THE_NETHER = fromIdFallback(-1);
    public static final DimensionType THE_END = fromIdFallback(1);

    /**
     * The resource keys used to refer to different dimension types.
     * Is more efficient than using {@link DimensionType#getKey()}.
     */
    public static final class Key {
        public static final ResourceKey<DimensionType> OVERWORLD = ResourceCategory.dimension_type.createKey("overworld");
        public static final ResourceKey<DimensionType> THE_NETHER = ResourceCategory.dimension_type.createKey("the_nether");
        public static final ResourceKey<DimensionType> THE_END = ResourceCategory.dimension_type.createKey("the_end");
    }

    private DimensionType(DimensionManagerHandle handle) {
        this.setHandle(handle);
    }

    /**
     * Gets the Id of this Dimension
     * 
     * @return dimension Id
     */
    public int getId() {
        return handle.getId();
    }

    /**
     * Gets whether this Dimension type stores sky light information
     * 
     * @return True if this dimension type has sky light
     */
    public boolean hasSkyLight() {
        return handle.hasSkyLight();
    }

    /**
     * Gets the resource key used to refer to this dimension type
     * 
     * @return dimension type key
     */
    public ResourceKey<DimensionType> getKey() {
        return handle.getKey();
    }

    /**
     * Gets the dimension manager handle that is used on MC 1.9 and later.
     * Returns a replacement implementation on MC 1.8.8 and before.
     * 
     * @return dimension manager handle
     */
    public Object getDimensionManagerHandle() {
        return handle.getRaw();
    }

    /**
     * Gets a dimension by its Id
     * 
     * @param id
     * @return dimension
     */
    public static DimensionType fromId(int id) {
        switch (id) {
        case 0: return OVERWORLD;
        case -1: return THE_NETHER;
        case 1: return THE_END;
        default: return fromIdFallback(id);
        }
    }

    /**
     * Gets a dimension by its key. Returns null if not found.
     * 
     * @param dimensionKey
     * @return dimension by this key, null if not found
     */
    public static DimensionType fromKey(ResourceKey<DimensionType> dimensionKey) {
        return fromDimensionManagerHandle(DimensionManagerHandle.T.fromKey.raw.invoke(dimensionKey.getRawHandle()));
    }

    /**
     * Gets a dimension by its 1.9+ DimensionManager handle.
     * 
     * @param dimensionManagerHandle
     * @return Dimension
     */
    public static DimensionType fromDimensionManagerHandle(Object dimensionManagerHandle) {
        if (dimensionManagerHandle == null) {
            return null;
        }

        // Optimization
        if (dimensionManagerHandle == OVERWORLD.getDimensionManagerHandle()) {
            return OVERWORLD;
        } else if (dimensionManagerHandle == THE_END.getDimensionManagerHandle()) {
            return THE_END;
        } else if (dimensionManagerHandle == THE_NETHER.getDimensionManagerHandle()) {
            return THE_NETHER;
        }

        // Return new instance
        return new DimensionType(DimensionManagerHandle.createHandle(dimensionManagerHandle));
    }

    // Uses internal lookup table, if available
    private static DimensionType fromIdFallback(int id) {
        DimensionManagerHandle handle = DimensionManagerHandle.fromId(id);
        if (handle != null) {
            return new DimensionType(handle);
        } else {
            throw new IllegalArgumentException("Invalid dimension id " + id);
        }
    }

    @Override
    public String toString() {
        try {
            return handle.toString();
        } catch (Throwable t) {}

        try {
            return handle.getKey().toString();
        } catch (Throwable t) {}

        return "UNKNOWN[" + handle.getRaw().getClass().getName() + "]";
    }
}
