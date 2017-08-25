package com.bergerkiller.bukkit.common.map;

import java.awt.Color;
import java.awt.Image;

import org.bukkit.map.MapPalette;

import com.bergerkiller.mountiplex.reflection.SafeField;

/**
 * Additional functionality on top of Bukkit's MapPalette
 */
@SuppressWarnings("deprecation")
public class MapColorPalette {
    public static final Color[] COLOR_MAP;
    public static final int COLOR_COUNT;
    public static final byte[] COLOR_MAP_AVERAGE  = new byte[0x10000];
    public static final byte[] COLOR_MAP_ADD      = new byte[0x10000];
    public static final byte[] COLOR_MAP_SUBTRACT = new byte[0x10000];
    public static final byte[] COLOR_MAP_MULTIPLY = new byte[0x10000];
    public static final byte[] COLOR_MAP_SPECULAR = new byte[0x10000];

    // List of colors with their closest matching palette entry
    public static final byte COLOR_TRANSPARENT = 0;
    public static final byte COLOR_BLACK = 119;
    public static final byte COLOR_WHITE = 34;
    public static final byte COLOR_RED = 18;
    public static final byte COLOR_GREEN = 30;
    public static final byte COLOR_BLUE = 50;
    public static final byte COLOR_CYAN = 126;
    public static final byte COLOR_YELLOW = 122;
    public static final byte COLOR_ORANGE = 62;
    public static final byte COLOR_BROWN = 42;
    public static final byte COLOR_PURPLE = 66;
    public static final byte COLOR_PINK = 82;

    static {
        // Ugh.
        COLOR_MAP = SafeField.create(MapPalette.class, "colors", Color[].class).get(null);
        COLOR_COUNT = COLOR_MAP.length;

        // Transparent colors at (a==0)
        for (int b = 1; b < COLOR_COUNT; b++) {
            initTransparent(b, (byte) b, false);
        }

        // All specular colors for the transparent color are transparent
        for (int b = 0; b < 256; b++) {
            COLOR_MAP_SPECULAR[b] = COLOR_TRANSPARENT;
        }

        // Generate the blend map
        for (int a = 1; a < COLOR_COUNT; a++) {
            int index = (a * 256);
            Color color_a = getRealColor((byte) a);
            initTransparent(index++, (byte) a, true);
            for (int b = 0; b < COLOR_COUNT; b++) {
                Color color_b = getRealColor((byte) b);
                initTable(index++,
                        color_a.getRed(), color_a.getGreen(), color_a.getBlue(),
                        color_b.getRed(), color_b.getGreen(), color_b.getBlue());
            }

            // Create 128 darker and 128 lighter specular colors
            index = (a * 256);
            for (int b = 0; b < 256; b++) {
                // 0.0 = black
                // 1.0 = natural color
                // 2.0 = white
                float f = (float) b / 128.0f;
                int sr = (int) ((float) color_a.getRed() * f);
                int sg = (int) ((float) color_a.getGreen() * f);
                int sb = (int) ((float) color_a.getBlue() * f);
                COLOR_MAP_SPECULAR[index++] = getColor(sr, sg, sb);
            }
        }
    }

    private static void initTransparent(int index, byte color, boolean is_second) {
        COLOR_MAP_AVERAGE[index] = color;
        COLOR_MAP_ADD[index] = color;
        COLOR_MAP_SUBTRACT[index] = color;
        COLOR_MAP_MULTIPLY[index] = (byte) 0;
    }

    private static void initTable(int index, int r1, int g1, int b1, int r2, int g2, int b2) {
        initArray(COLOR_MAP_AVERAGE,  index, (r1 + r2) >> 1, (g1 + g2) >> 1, (b1 + b2) >> 1);
        initArray(COLOR_MAP_ADD,      index, (r1 + r2),      (g1 + g2),      (b1 + b2));
        initArray(COLOR_MAP_SUBTRACT, index, (r2 - r1),      (g2 - g1),      (b2 - b1));
        initArray(COLOR_MAP_MULTIPLY, index, (r1 * r2) / 255, (g1 * g2) / 255, (b1 * b2) / 255);
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
     * Gets the Minecraft map color code for an RGB color
     * 
     * @param color input
     * @return minecraft color
     */
    public static byte getColor(Color color) {
        return MapPalette.matchColor(color);
    }

    /**
     * Gets the Minecraft map color code for an RGB color
     * 
     * @param r - red component
     * @param g - green component
     * @param b - blue component
     * @return minecraft color
     */
    public static byte getColor(int r, int g, int b) {
        // This helps prevent dumb exceptions.
        // Nobody likes random exceptions when all you're doing is color calculations
        if (r < 0)
            r = 0;
        else if (r > 255)
            r = 255;
        if (g < 0)
            g = 0;
        else if (g > 255)
            g = 255;
        if (b < 0)
            b = 0;
        else if (b > 255)
            b = 255;

        // Uses Bukkit's API. For so long it works, anyway.
        return MapPalette.matchColor(r, g, b);
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

    /**
     * Gets the real RGB color belonging to a color code
     * 
     * @param color code input
     * @return real RGB color
     */
    public static final Color getRealColor(byte color) {
        return COLOR_MAP[color & 0xFF];
    }

    /**
     * Makes a color darker or brighter based on a specular float value.
     * 
     * @param color to transform
     * @param lightness factor. 0 is no change, -1 is black, 1 is white.
     * @return specular color
     */
    public static byte getSpecular(byte color, float lightness) {
        int index = (int) (128.0f * lightness);
        if (index < 0) {
            return COLOR_BLACK;
        } else if (index >= 256) {
            return COLOR_MAP_SPECULAR[((color & 0xFF) << 8) + 255];
        } else {
            return COLOR_MAP_SPECULAR[((color & 0xFF) << 8) + index];
        }
    }
}
