package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.server.HeightMapHandle;

/**
 * Heightmap information of a single chunk. There are different possible heightmap
 * configurations since MC 1.13. See {@link Type}
 */
public class HeightMap extends BasicWrapper<HeightMapHandle> {

    public HeightMap(HeightMapHandle handle) {
        setHandle(handle);
    }

    /**
     * Initializes this Heightmap, correcting any errors in height values
     * that may have existed.
     */
    public void initialize() {
        this.handle.initialize();
    }

    /**
     * Gets the height at an x and z coordinate according to this Heightmap.
     * 
     * @param x coordinates [ 0 ... 15 ]
     * @param z coordinates [ 0 ... 15 ]
     * @return height
     */
    public int getHeight(int x, int z) {
        return this.handle.getHeight(x, z) - 1;
    }

    /**
     * Type of Heightmap. On MC 1.12.2 and before, only {@link #LIGHT_BLOCKING}
     * is a valid Heightmap type.
     */
    public static enum Type {
        /**
         * Highest point of a Block that is not Air.
         * Used during world generation only.
         */
        WORLD_SURFACE_WG,
        /**
         * Highest point of a Block that is not Air nor a liquid (Water, Lava).
         * Used during world generation only.
         */
        OCEAN_FLOOR_WG,
        /**
         * Highest point of a Block that is not Air nor a fully transparent Block like Glass.
         * This Block and all blocks above it will have sky light level 15 in the overworld.
         */
        LIGHT_BLOCKING,
        /**
         * Highest point of a Block that is not Air nor any kind of Block a player can not safely
         * be inside without suffocating. So no lava, water or solid blocks.
         */
        MOTION_BLOCKING,
        /**
         * Highest point of a Block that is not Air nor any kind of Block a player can not safely
         * be inside without suffocating, nor Leaves. So no lava, water, leaves or solid blocks.
         */
        MOTION_BLOCKING_NO_LEAVES,
        /**
         * Highest point of a Block that is not Air nor a liquid (Water, Lava).
         * Used during normal world operation.
         */
        OCEAN_FLOOR,
        /**
         * Highest point of a Block that is not Air.
         * Used during normal world operation.
         */
        WORLD_SURFACE;

        private final Object _handle;

        private Type() {
            Enum<?>[] values = (Enum<?>[]) CommonUtil.getNMSClass("HeightMap.Type").getEnumConstants();
            Object handle = null;
            for (Enum<?> value : values) {
                if (value.name().equals("LIGHT_BLOCKING")) {
                    handle = value;
                }
                if (value.name().equals(this.name())) {
                    handle = value;
                    break;
                }
            }
            this._handle = handle;
        }

        /**
         * Gets the net.minecraft.server.Heightmap$Type handle of this Type.
         * If it does not exist (1.12.2 and before), this returns null.
         * 
         * @return handle
         */
        public Object getHandle() {
            return this._handle;
        }

        /**
         * Gets the matching Type from a net.minecraft.server.Heightmap$Type handle.
         * 
         * @param handle
         * @return Heightmap Type
         */
        public static Type fromHandle(Object handle) {
            for (Type type : values()) {
                if (type.getHandle() == handle) {
                    return type;
                }
            }
            return LIGHT_BLOCKING;
        }
    }
}
