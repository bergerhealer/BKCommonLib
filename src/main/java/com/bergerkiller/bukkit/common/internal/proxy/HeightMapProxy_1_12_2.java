package com.bergerkiller.bukkit.common.internal.proxy;

/**
 * Acts as the HeightMap class introduced in Minecraft 1.13.
 * Simply looks up values in an internal int[] array.
 */
public class HeightMapProxy_1_12_2 {
    public final Object chunk;
    private final int[] heightMap;

    public HeightMapProxy_1_12_2(Object chunkHandle, int[] heightMap) {
        this.chunk = chunkHandle;
        this.heightMap = heightMap;
    }

    /*
    // a()
    public void initialize() {
        World world = chunkHandle.getBukkitChunk().getWorld();
        int baseX = chunkHandle.getLocX() << 4;
        int baseZ = chunkHandle.getLocZ() << 4;
        int highestY = chunkHandle.getTopSliceY();
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                int y = highestY + 16;
                while (y > 0) {
                    if (chunkHandle.getBlockDataAtCoord(x, y - 1, z).getOpacity(world, baseX+x, y, baseZ+z) == 0) {
                        --y;
                        continue;
                    }

                    this.heightMap[z << 4 | x] = y;
                    break;
                }
            }
        }
    }
    */

    public int getHeight(int x, int z) {
        return this.heightMap[z << 4 | x] - 2;
    }

    public void setHeight(int x, int z, int height) {
        this.heightMap[z << 4 | x] = height + 2;
    }
}
