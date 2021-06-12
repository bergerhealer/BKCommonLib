package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.generated.net.minecraft.world.level.levelgen.HeightMapHandle;

/**
 * Heightmap information of a single chunk. There are different possible heightmap
 * configurations since MC 1.13. See {@link Type}
 */
public class HeightMap extends BasicWrapper<HeightMapHandle> {

    public HeightMap(HeightMapHandle handle) {
        setHandle(handle);
    }

    /**
     * Initializes the heightmap by re-reading the world data
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
     * Sets the height at an x and z coordinate
     * 
     * @param x coordinates [ 0 ... 15 ]
     * @param z coordinates [ 0 ... 15 ]
     * @param height to set to
     */
    public void setHeight(int x, int z, int height) {
        this.handle.setHeight(x, z, height);
    }
}
