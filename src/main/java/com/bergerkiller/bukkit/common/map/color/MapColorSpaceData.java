package com.bergerkiller.bukkit.common.map.color;

import java.awt.Color;
import java.util.Arrays;

/**
 * Stores the raw map color space data, enabling transformation between different storage methods.
 */
public class MapColorSpaceData implements Cloneable {
    private final Color[] colors = new Color[256];
    private final byte[] data = new byte[1 << 24];

    public MapColorSpaceData() {
        Arrays.fill(this.colors, new Color(0, 0, 0, 0));
    }

    /**
     * Gets the total number of colors that exist in this color space.
     * Transparent colors after the first 4 are not included in this count.
     * 
     * @return color count
     */
    public final int getColorCount() {
        int count = 0;
        boolean found_all_transparent = false;
        for (Color color : this.colors) {
            if (color.getAlpha() >= 128) {
                found_all_transparent = true;
                count++;
            } else if (!found_all_transparent) {
                count++;
            }
        }
        return count;
    }

    /**
     * Clears only the RGB data. Equivalent to using {@link #set(int, byte)} on all RGB colors.
     */
    public final void clearRGBData() {
        Arrays.fill(this.data, (byte) 0);
    }

    /**
     * Clears all data, setting all colors to transparent
     */
    public final void clear() {
        Arrays.fill(this.colors, new Color(0, 0, 0, 0));
        Arrays.fill(this.data, (byte) 0);
    }

    /**
     * Sets all color data of this color space data to that from the input color space data
     * 
     * @param data to set
     */
    public void readFrom(MapColorSpaceData data) {
        System.arraycopy(data.data, 0, this.data, 0, this.data.length);
        System.arraycopy(data.colors, 0, this.colors, 0, this.colors.length);
    }

    /**
     * Sets a single map palette color
     * 
     * @param code of the color
     * @param color to set to
     */
    public final void setColor(byte code, Color color) {
        this.colors[code & 0xFF] = color;
    }

    /**
     * Gets a single map palette color
     * 
     * @param index of the color
     * @return map palette color
     */
    public final Color getColor(int index) {
        return this.colors[index & 0xFF];
    }

    /**
     * Gets a single map palette color
     * 
     * @param code of the color
     * @return map palette color
     */
    public final Color getColor(byte code) {
        return this.colors[code & 0xFF];
    }

    /**
     * Sets the map color code value for an rgb value
     * 
     * @param r component
     * @param g component
     * @param b component
     * @param code to set to
     */
    public final void set(int r, int g, int b, byte code) {
        this.data[getDataIndex(r, g, b)] = code;
    }

    /**
     * Gets the map color code value for an rgb value
     * 
     * @param r component
     * @param g component
     * @param b component
     * @return color code
     */
    public final byte get(byte r, byte g, byte b) {
        return this.data[getDataIndex(r, g, b)];
    }

    /**
     * Gets the map color code value for an rgb value
     * 
     * @param r component
     * @param g component
     * @param b component
     * @return color code
     */
    public final byte get(int r, int g, int b) {
        return this.data[getDataIndex(r, g, b)];
    }

    /**
     * Sets the map color code for an rgb value
     * 
     * @param index rgb compound value
     * @param code to set to
     */
    public final void set(int index, byte code) {
        this.data[index] = code;
    }

    /**
     * Gets the map color code for an rgb value
     * 
     * @param index rgb compound value
     * @return color code
     */
    public final byte get(int index) {
        return this.data[index];
    }

    /**
     * Gets all the red/green values for a single Blue color channel
     * 
     * @param b blue channel
     * @return red/green color data
     */
    public final byte[] getRG(int b) {
        byte[] result = new byte[1 << 16];
        getRG(b, result);
        return result;
    }

    /**
     * Gets all the red/green values for a single Blue color channel
     * 
     * @param b blue channel
     * @param data to get
     */
    public final void getRG(int b, byte[] data) {
        System.arraycopy(this.data, b << 16, data, 0, 1 << 16);
    }

    /**
     * Sets all the red/green values for a single Blue color chanel
     * 
     * @param b blue channel
     * @param data to set
     */
    public final void setRG(int b, byte[] data) {
        System.arraycopy(data, 0, this.data, b << 16, 1 << 16);
    }

    @Override
    public MapColorSpaceData clone() {
        MapColorSpaceData clone = new MapColorSpaceData();
        System.arraycopy(this.colors, 0, clone.colors, 0, this.colors.length);
        System.arraycopy(this.data, 0, clone.data, 0, this.data.length);
        return clone;
    }

    /**
     * Gets the mapping index of an rgb value
     * 
     * @param r component
     * @param g component
     * @param b component
     * @return index
     */
    private static final int getDataIndex(byte r, byte g, byte b) {
        return (r & 0xFF) + ((g & 0xFF) << 8) + ((b & 0xFF) << 16);
    }

    /**
     * Gets the mapping index of an rgb value
     * 
     * @param r component
     * @param g component
     * @param b component
     * @return index
     */
    private static final int getDataIndex(int r, int g, int b) {
        return (r & 0xFF) + ((g & 0xFF) << 8) + ((b & 0xFF) << 16);
    }
}
