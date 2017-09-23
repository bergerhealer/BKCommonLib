package com.bergerkiller.bukkit.common.map;

import java.util.Arrays;

/**
 * A blend mode used when drawing on a canvas
 */
public enum MapBlendMode {
    NONE, OVERLAY, AVERAGE, ADD, SUBTRACT, MULTIPLY;

    public byte process(byte inputA, byte inputB) {
        switch (this) {
        case NONE:
            return inputA;
        case OVERLAY:
            return (inputA == 0) ? inputB : inputA;
        case AVERAGE:
            return MapColorPalette.remapColor(inputA, inputB, MapColorPalette.COLOR_MAP_AVERAGE);
        case ADD:
            return MapColorPalette.remapColor(inputA, inputB, MapColorPalette.COLOR_MAP_ADD);
        case SUBTRACT:
            return MapColorPalette.remapColor(inputA, inputB, MapColorPalette.COLOR_MAP_SUBTRACT);
        case MULTIPLY:
            return MapColorPalette.remapColor(inputA, inputB, MapColorPalette.COLOR_MAP_MULTIPLY);
        }
        return (byte) inputA;
    }

    public void process(byte input, byte[] output) {
        switch (this) {
        case NONE:
            Arrays.fill(output, input);
            break;
        case OVERLAY:
            if (input != 0) {
                Arrays.fill(output, input);
            }
            break;
        case AVERAGE:
            MapColorPalette.remapColors(input, output, MapColorPalette.COLOR_MAP_AVERAGE);
            break;
        case ADD:
            MapColorPalette.remapColors(input, output, MapColorPalette.COLOR_MAP_ADD);
            break;
        case SUBTRACT:
            MapColorPalette.remapColors(input, output, MapColorPalette.COLOR_MAP_SUBTRACT);
            break;
        case MULTIPLY:
            MapColorPalette.remapColors(input, output, MapColorPalette.COLOR_MAP_MULTIPLY);
            break;
        }
    }

    public void process(byte[] input, byte[] output) {
        switch (this) {
        case NONE:
            System.arraycopy(input, 0, output, 0, output.length);
            break;
        case OVERLAY:
            for (int i = 0; i < output.length; i++) {
                if (input[i] != 0) {
                    output[i] = input[i];
                }
            }
            break;
        case AVERAGE:
            MapColorPalette.remapColors(input, output, MapColorPalette.COLOR_MAP_AVERAGE);
            break;
        case ADD:
            MapColorPalette.remapColors(input, output, MapColorPalette.COLOR_MAP_ADD);
            break;
        case SUBTRACT:
            MapColorPalette.remapColors(input, output, MapColorPalette.COLOR_MAP_SUBTRACT);
            break;
        case MULTIPLY:
            MapColorPalette.remapColors(input, output, MapColorPalette.COLOR_MAP_MULTIPLY);
            break;
        }
    }
}
