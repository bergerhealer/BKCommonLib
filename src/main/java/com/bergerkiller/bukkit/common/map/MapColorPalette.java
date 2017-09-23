package com.bergerkiller.bukkit.common.map;

import java.awt.Color;
import java.awt.Image;
import java.util.Arrays;

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

        // Generate 256 lightness values for all colors
        for (int a = 0; a < 256; a++) {
            int index = (a * 256);
            Color color_a = getRealColor((byte) a);
            if (color_a.getAlpha() < 128) {
                // All specular colors for the transparent color are transparent
                Arrays.fill(COLOR_MAP_SPECULAR, index, index + 256, COLOR_TRANSPARENT);
            } else {
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

        // Initialize the color map tables for all possible color values
        for (int c1 = 0; c1 < 256; c1++) {
            for (int c2 = 0; c2 < 256; c2++) {
                initTable((byte) c1, (byte) c2);
            }
        }
    }

    private static void initTable(byte color1, byte color2) {
        int index = getMapIndex(color1, color2);
        if (isTransparent(color1)) {
            initTransparent(index, color2);
        } else if (isTransparent(color2)) {
            initTransparent(index, color1);
        } else {
            Color c1 = getRealColor(color1);
            Color c2 = getRealColor(color2);
            initColor(
                    index,
                    c1.getRed(), c1.getGreen(), c1.getBlue(),
                    c2.getRed(), c2.getGreen(), c2.getBlue()
            );
        }
    }

    private static void initTransparent(int index, byte color) {
        COLOR_MAP_AVERAGE[index] = color;
        COLOR_MAP_ADD[index] = color;
        COLOR_MAP_SUBTRACT[index] = color;
        COLOR_MAP_MULTIPLY[index] = (byte) 0;
    }

    private static void initColor(int index, int r1, int g1, int b1, int r2, int g2, int b2) {
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
        array[index] = getColor(r, g, b);
    }

    public static byte remapColor(byte inputA, byte inputB, byte[] remapArray) {
        return remapArray[getMapIndex(inputA, inputB) & 0xFFFF];
    }

    public static void remapColors(byte input, byte[] output, byte[] remapArray) {
        for (int i = 0; i < output.length; i++) {
            output[i] = remapArray[getMapIndex(input, output[i]) & 0xFFFF];
        }
    }

    public static void remapColors(byte[] input, byte[] output, byte[] remapArray) {
        for (int i = 0; i < output.length; i++) {
            output[i] = remapArray[getMapIndex(input[i], output[i]) & 0xFFFF];
        }
    }

    /**
     * Gets whether a particular color code is a transparent color.
     * There are 4 transparent colors available. Usually value 0 is used.
     * 
     * @param color value
     * @return True if transparent
     */
    public static boolean isTransparent(byte color) {
        return (color & 0xFF) < 0x4;
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
        return (color_a & 0xFF) | ((color_b & 0xFF) << 8);
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
     * @param lightness factor. 0 is darkest (black), 1 is normal, 2 is brightest (white).
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
