package com.bergerkiller.bukkit.common.map.util;

import com.bergerkiller.bukkit.common.map.MapTexture;

/**
 * Stores a cache of single-pixel textures of all possible pixel color values.
 * Helps when rendering voxel-based models to keep memory usage down.
 */
public class SinglePixelTexture {
    private static final MapTexture[] _textures = new MapTexture[256];
    static {
        for (int i = 0; i < 256; i++) {
            _textures[i] = MapTexture.createEmpty(1, 1);
            _textures[i].writePixel(0, 0, (byte) i);
        }
    }

    /**
     * Gets a map texture of 1x1 in size, with the given color.
     * The texture is meant to be used read-only, do not change the
     * color of this texture by writing.
     * 
     * @param color
     * @return 1x1 map texture with the color
     */
    public static MapTexture get(byte color) {
        return _textures[color & 0xFF];
    }
}
