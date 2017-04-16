package com.bergerkiller.bukkit.common.map;

import java.awt.Color;
import java.awt.Image;

import org.bukkit.map.MapPalette;

import com.bergerkiller.reflection.SafeField;

/**
 * Additional functionality on top of Bukkit's MapPalette
 */
@SuppressWarnings("deprecation")
public class MapColorPalette {
    public static final Color[] COLOR_MAP;
    public static final int COLOR_COUNT = 144; // we want this to be inlined for optimized performance
    public static final byte[] COLOR_MAP_AVERAGE  = new byte[0x10000];
    public static final byte[] COLOR_MAP_ADD      = new byte[0x10000];
    public static final byte[] COLOR_MAP_SUBTRACT = new byte[0x10000];

    static {
        // Ugh.
        COLOR_MAP = SafeField.create(MapPalette.class, "colors", Color[].class).get(null);
        if (COLOR_MAP.length != COLOR_COUNT) {
            throw new RuntimeException("Color count is incorrect! Should be " + COLOR_MAP.length + ", but was " + COLOR_COUNT);
        }

        // Generate the blend map
        for (int a = 1; a < COLOR_COUNT; a++) {
            int index = (a * 256);
            Color color_a = MapPalette.getColor((byte) a);
            COLOR_MAP_AVERAGE[index++] = 0; // transparent = 0
            for (int b = 1; b < COLOR_COUNT; b++) {
                Color color_b = MapPalette.getColor((byte) b);
                initTable(index++,
                        color_a.getRed(), color_a.getGreen(), color_a.getBlue(),
                        color_b.getRed(), color_b.getGreen(), color_b.getBlue());
            }
        }
    }

    private static void initTable(int index, int r1, int g1, int b1, int r2, int g2, int b2) {
        initArray(COLOR_MAP_AVERAGE,  index, (r1 + r2) >> 1, (g1 + g2) >> 1, (b1 + b2) >> 1);
        initArray(COLOR_MAP_ADD,      index, (r1 + r2),      (g1 + g2),      (b1 + b2));
        initArray(COLOR_MAP_SUBTRACT, index, (r2 - r1),      (g2 - g1),      (b2 - b1));
    }

    private static void initArray(byte[] array, int index, int r, int g, int b) {
        if (r < 0x00) r = 0x00;
        if (r > 0xFF) r = 0xFF;
        if (g < 0x00) g = 0x00;
        if (g > 0xFF) g = 0xFF;
        if (b < 0x00) b = 0x00;
        if (b > 0xFF) b = 0xFF;
        array[index] = MapPalette.matchColor(r, g, b);
    }

    public static void remapColors(byte input, byte[] output, byte[] remapArray) {
        for (int i = 0; i < output.length; i++) {
            output[i] = remapArray[((int) input | ((int) output[i] << 8)) & 0xFFFF];
        }
    }

    public static void remapColors(byte[] input, byte[] output, byte[] remapArray) {
        for (int i = 0; i < output.length; i++) {
            output[i] = remapArray[((int) input[i] | ((int) output[i] << 8)) & 0xFFFF];
        }
    }

    /**
     * Converts the pixel data of an image into map color values
     * 
     * @param image to convert
     * @return converted byte data
     */
    public static byte[] convertImage(Image image) {
        return MapPalette.imageToBytes(image);
    }

    /**
     * Gets the index into one of the palette remap arrays
     * 
     * @param color_a first color
     * @param color_b second color
     * @return index
     */
    public static final int getMapIndex(byte color_a, byte color_b) {
        return ((int) color_a | ((int) color_b << 8)) & 0xFFFF;
    }
}
