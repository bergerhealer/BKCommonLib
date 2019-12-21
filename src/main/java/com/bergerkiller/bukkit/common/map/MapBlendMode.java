package com.bergerkiller.bukkit.common.map;

import java.util.Arrays;

/**
 * A blend mode used when drawing on a canvas
 */
public enum MapBlendMode {
    NONE {
        @Override
        public byte process(byte inputA, byte inputB) {
            return inputA;
        }

        @Override
        public void process(byte input, byte[] output) {
            Arrays.fill(output, input);
        }

        @Override
        public void process(byte[] input, byte[] output) {
            System.arraycopy(input, 0, output, 0, output.length);
        }
    },
    OVERLAY {
        @Override
        public byte process(byte inputA, byte inputB) {
            return (inputA == 0) ? inputB : inputA;
        }

        @Override
        public void process(byte input, byte[] output) {
            if (input != 0) {
                Arrays.fill(output, input);
            }
        }

        @Override
        public void process(byte[] input, byte[] output) {
            for (int i = 0; i < output.length; i++) {
                if (input[i] != 0) {
                    output[i] = input[i];
                }
            }
        }
    },
    AVERAGE {
        @Override
        public byte process(byte inputA, byte inputB) {
            return MapColorPalette.remapColor(inputA, inputB, MapColorPalette.COLOR_MAP_AVERAGE);
        }

        @Override
        public void process(byte input, byte[] output) {
            MapColorPalette.remapColors(input, output, MapColorPalette.COLOR_MAP_AVERAGE);
        }

        @Override
        public void process(byte[] input, byte[] output) {
            MapColorPalette.remapColors(input, output, MapColorPalette.COLOR_MAP_AVERAGE);
        }
    },
    ADD {
        @Override
        public byte process(byte inputA, byte inputB) {
            return MapColorPalette.remapColor(inputA, inputB, MapColorPalette.COLOR_MAP_ADD);
        }

        @Override
        public void process(byte input, byte[] output) {
            MapColorPalette.remapColors(input, output, MapColorPalette.COLOR_MAP_ADD);
        }

        @Override
        public void process(byte[] input, byte[] output) {
            MapColorPalette.remapColors(input, output, MapColorPalette.COLOR_MAP_ADD);
        }
    },
    SUBTRACT {
        @Override
        public byte process(byte inputA, byte inputB) {
            return MapColorPalette.remapColor(inputA, inputB, MapColorPalette.COLOR_MAP_SUBTRACT);
        }

        @Override
        public void process(byte input, byte[] output) {
            MapColorPalette.remapColors(input, output, MapColorPalette.COLOR_MAP_SUBTRACT);
        }

        @Override
        public void process(byte[] input, byte[] output) {
            MapColorPalette.remapColors(input, output, MapColorPalette.COLOR_MAP_SUBTRACT);
        }
    },
    MULTIPLY {
        @Override
        public byte process(byte inputA, byte inputB) {
            return MapColorPalette.remapColor(inputA, inputB, MapColorPalette.COLOR_MAP_MULTIPLY);
        }

        @Override
        public void process(byte input, byte[] output) {
            MapColorPalette.remapColors(input, output, MapColorPalette.COLOR_MAP_MULTIPLY);
        }

        @Override
        public void process(byte[] input, byte[] output) {
            MapColorPalette.remapColors(input, output, MapColorPalette.COLOR_MAP_MULTIPLY);
        }
    };

    public abstract byte process(byte inputA, byte inputB);
    public abstract void process(byte input, byte[] output);
    public abstract void process(byte[] input, byte[] output);
}
