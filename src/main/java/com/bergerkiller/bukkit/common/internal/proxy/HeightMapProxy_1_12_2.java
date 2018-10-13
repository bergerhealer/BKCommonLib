package com.bergerkiller.bukkit.common.internal.proxy;

import org.bukkit.World;

import com.bergerkiller.generated.net.minecraft.server.ChunkHandle;

/**
 * Acts as the HeightMap class introduced in Minecraft 1.13.
 * Simply looks up values in an internal int[] array.
 */
public class HeightMapProxy_1_12_2 {
    private final ChunkHandle chunkHandle;
    private final int[] heightMap;

    public HeightMapProxy_1_12_2(Object chunkHandle, int[] heightMap) {
        this.chunkHandle = ChunkHandle.createHandle(chunkHandle);
        this.heightMap = heightMap;
    }

    // Initialize()
    public void a() {
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

    // getHeight(x, z)
    public int a(int x, int z) {
        return this.heightMap[z << 4 | x];
    }

    public static enum Type {
        LIGHT_BLOCKING
    }
}
