package com.bergerkiller.bukkit.common.map;

import java.awt.Color;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.Level;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.map.color.MCSDBubbleFormat;
import com.bergerkiller.bukkit.common.map.color.MCSDGenBukkit;
import com.bergerkiller.bukkit.common.map.color.MapColorSpaceData;

/**
 * Additional functionality on top of Bukkit's MapPalette
 */
public class MapColorPalette {
    private static final MapColorSpaceData COLOR_MAP_DATA;
    private static final IndexColorModel COLOR_INDEX_MODEL;
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
        // We NEED java awt or all will just fail to work anyway.
        if (CommonBootstrap.isHeadlessJDK()) {
            Logging.LOGGER_MAPDISPLAY.log(Level.SEVERE, "The Java AWT runtime library is not available");
            Logging.LOGGER_MAPDISPLAY.log(Level.SEVERE, "This is usually because a headless JVM is used for the server");
            Logging.LOGGER_MAPDISPLAY.log(Level.SEVERE, "Please install and configure a non-headless JVM to have Map Displays work");
            throw new UnsupportedOperationException("Map Displays require a non-headless JVM (Uses AWT)");
        }

        // Now we know java.awt exists we can initialize this one
        COLOR_MAP_DATA = new MapColorSpaceData();

        // Load color map data from the Bubble format file bundled with the library
        {
            boolean success = false;
            MCSDBubbleFormat bubbleData = new MCSDBubbleFormat();
            try {
                String bub_path_postfix = "map_1_8_8.bub";
                for (String ver : new String[] { "1.17", "1.16", "1.12" }) {
                    if (CommonBootstrap.evaluateMCVersion(">=", ver)) {
                        bub_path_postfix = "map_" + ver.replace('.', '_') + ".bub";
                        break;
                    }
                }
                String bub_path = "/com/bergerkiller/bukkit/common/internal/resources/map/" + bub_path_postfix;
                InputStream input = MapColorPalette.class.getResourceAsStream(bub_path);
                if (input == null) {
                    Logging.LOGGER_MAPDISPLAY.severe("Bubble data at " + bub_path + " not found!");
                } else {
                    bubbleData.readFrom(input);
                    success = true;
                }
            } catch (Throwable t) {
                Logging.LOGGER_MAPDISPLAY.log(Level.SEVERE, "Unhandled map color format initialization error", t);
            }
            if (success) {
                COLOR_MAP_DATA.readFrom(bubbleData);
            } else {
                Logging.LOGGER_MAPDISPLAY.warning("Bubble colormap data could not be loaded, it will be generated instead");
                MCSDGenBukkit bukkitGen = new MCSDGenBukkit();
                bukkitGen.generate();
                COLOR_MAP_DATA.readFrom(bukkitGen);
            }
        }

        // Create Java ColorIndexModel
        COLOR_INDEX_MODEL = COLOR_MAP_DATA.toIndexColorModel();

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
        if (isTransparent(color1) || isTransparent(color2)) {
            initTransparent(index, color1, color2);
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

    private static void initTransparent(int index, byte color1, byte color2) {
        COLOR_MAP_AVERAGE[index] = color2;
        COLOR_MAP_ADD[index] = color2;
        COLOR_MAP_SUBTRACT[index] = color2;
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
     * Gets the total number of map colors that exist on the server.
     * The returned value is the exclusive maximum index for {@link #getRealColor(int)}.
     * 
     * @return maximum color count
     */
    public static int getColorCount() {
        return COLOR_MAP_DATA.getColorCount();
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
        if ((color.getAlpha() & 0x80) == 0) {
            return COLOR_TRANSPARENT;
        } else {
            return COLOR_MAP_DATA.get(color.getRed(), color.getGreen(), color.getBlue());
        }
    }

    /**
     * Gets the Minecraft map color code for an RGB color
     * 
     * @param r - red component
     * @param g - green component
     * @param b - blue component
     * @return minecraft color
     */
    public static byte getColor(byte r, byte g, byte b) {
        return COLOR_MAP_DATA.get(r, g, b);
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

        return COLOR_MAP_DATA.get(r, g, b);
    }

    /**
     * Converts the pixel data of an image into map color values
     * 
     * @param image to convert
     * @return converted byte data
     */
    public static byte[] convertImage(Image image) {
        final int width = image.getWidth(null);
        final int height = image.getHeight(null);

        // If the image is not a buffered image, re-render the image onto an ABGR buffered image
        BufferedImage imageBuf;
        if (image instanceof BufferedImage) {
            imageBuf = (BufferedImage) image;
        } else {
            imageBuf = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            java.awt.Graphics2D graphics = imageBuf.createGraphics();
            graphics.drawImage(image, 0, 0, null);
            graphics.dispose();
        }

        // Obtain the raw image data directly, either as an int[] buffer or as a byte[] buffer
        int[] intPixels = null;
        byte[] bytePixels = null;
        java.awt.image.DataBuffer dataBuffer = imageBuf.getRaster().getDataBuffer();
        if (dataBuffer instanceof java.awt.image.DataBufferInt) {
            intPixels = ((java.awt.image.DataBufferInt) dataBuffer).getData();
        } else if (dataBuffer instanceof java.awt.image.DataBufferByte) {
            bytePixels = ((java.awt.image.DataBufferByte) dataBuffer).getData();
        }

        // Obtain the color conversion type that has to be applied (RGB or BGR?)
        int type = imageBuf.getType();
        int byteStep = 4;
        ColorConverterType converterType = null;
        if (type == BufferedImage.TYPE_INT_RGB) {
            converterType = ColorConverterType.RGB;
        } else if (type == BufferedImage.TYPE_INT_ARGB) {
            converterType = ColorConverterType.ARGB;
        } else if (type == BufferedImage.TYPE_INT_BGR) {
            converterType = ColorConverterType.BGR;
        } else if (type == BufferedImage.TYPE_3BYTE_BGR) {
            converterType = ColorConverterType.BGR;
            byteStep = 3;
        } else if (type == BufferedImage.TYPE_4BYTE_ABGR) {
            converterType = ColorConverterType.ABGR;
        }

        // Incompatible backing buffer or color format; simply use int[] ARGB
        if ((intPixels == null && bytePixels == null) || converterType == null) {
            intPixels = new int[width * height];
            bytePixels = null;
            imageBuf.getRGB(0, 0, width, height, intPixels, 0, width);
            converterType = ColorConverterType.ARGB;
        }

        // Perform efficient conversion from RGB int to byte color codes
        byte[] result = new byte[width * height];
        if (bytePixels != null) {
            int index = 0;
            for (int i = 0; i < result.length; i++) {
                result[i] = converterType.convertBytes(bytePixels, index);
                index += byteStep;
            }
        } else {
            for (int i = 0; i < result.length; i++) {
                result[i] = converterType.convert(intPixels[i]);
            }
        }
        return result;
    }

    /**
     * Copies the input byte array of map color pixel data to the output array,
     * performing an image resize operation with bilinear interpolation. For performance
     * it is recommended to scale down an integer resolution amount. For example: 
     * 128x64 -> 64x32.<br>
     * <br>
     * If no faster method is possible, will use a slow method involving Graphics2D.
     *
     * @param input Input byte array of map pixel colors
     * @param inputWidth Input image resolution width
     * @param inputHeight Input image resolution height
     * @param output Output buffer to write the updated colors to
     * @param outputWidth Output image resolution width
     * @param outputHeight Output image resolution height
     */
    public static void resizeCopy(byte[] input, int inputWidth, int inputHeight, byte[] output, int outputWidth, int outputHeight) {
        // Unique case
        if (inputWidth == outputWidth && inputHeight == outputHeight) {
            System.arraycopy(input, 0, output, 0, inputWidth * inputHeight);
            return;
        }

        // Optimized code when down-sizing an integer amount
        int outputLength = outputWidth * outputHeight;
        int pixelStepX = inputWidth / outputWidth;
        int pixelStepY = inputHeight / outputHeight;
        if ((outputWidth * pixelStepX) == inputWidth && (outputHeight * pixelStepY) == inputHeight) {
            int pixelStepBlock = pixelStepX * pixelStepY;
            int pixelStepBlockHalf = pixelStepBlock / 2;
            int inputLineOffset = inputWidth - pixelStepX;
            int inputLineStep = (pixelStepY - 1) * inputWidth;
            int inputStartIndex = 0;
            int inputLineEndIndex = inputWidth;
            for (int i = 0; i < outputLength; i++) {
                // Collect average RGB pixel value
                // Also count how many pixels are transparent
                // If over 50% is transparent, make it a transparent pixel
                int numTransparent = 0;
                int r = 0;
                int g = 0;
                int b = 0;
                int inputIndex = inputStartIndex;
                for (int dy = 0; dy < pixelStepY; dy++) {
                    for (int dx = 0; dx < pixelStepX; dx++) {
                        byte pixel = input[inputIndex++];
                        if (isTransparent(pixel)) {
                            numTransparent++;
                        } else {
                            Color c = MapColorPalette.getRealColor(pixel);
                            r += c.getRed();
                            g += c.getGreen();
                            b += c.getBlue();
                        }
                    }
                    inputIndex += inputLineOffset;
                }
                if (numTransparent >= pixelStepBlockHalf) {
                    output[i] = COLOR_TRANSPARENT;
                } else {
                    int fact = pixelStepBlock - numTransparent;
                    r /= fact;
                    g /= fact;
                    b /= fact;
                    output[i] = COLOR_MAP_DATA.get(r, g, b);
                }

                // Next pixel of the input, and then next line
                inputStartIndex += pixelStepX;
                if (inputStartIndex >= inputLineEndIndex) {
                    inputStartIndex += inputLineStep;
                    inputLineEndIndex = inputStartIndex + inputWidth;
                }
            }
            return;
        }

        // Default fallback code just uses Graphics2D

        // Create an indexed BufferedImage for the input
        BufferedImage inputImage;
        {
            java.awt.image.DataBufferByte data = new java.awt.image.DataBufferByte(input, input.length);
            WritableRaster raster = Raster.createInterleavedRaster(data, inputWidth, inputHeight, inputWidth, 1, new int[] {0}, null);
            inputImage = new BufferedImage(MapColorPalette.getIndexColorModel(), raster, false, null);
        }

        // When scaling down, we must use getScaledInstance because bilinear interpolation looks awful
        // When scaling up, we must use the bilinear interpolation affine operation for best results
        // Sadly there is no solution that covers best of both worlds :(
        BufferedImage outputImage = new BufferedImage(outputWidth, outputHeight, BufferedImage.TYPE_INT_ARGB);
        if (outputLength > (inputWidth * inputHeight)) {
            // Scaling up
            AffineTransform at = new AffineTransform();
            at.scale((double) outputWidth / (double) inputWidth, (double) outputHeight/ (double) inputHeight);
            AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
            outputImage = scaleOp.filter(inputImage, outputImage);
        } else {
            // Scaling down. Note that performance is very poor, but quality is good.
            Image tmp = inputImage.getScaledInstance(outputWidth, outputHeight, Image.SCALE_AREA_AVERAGING);

            // Convert to BufferedImage
            java.awt.Graphics2D graphics2D = outputImage.createGraphics();
            graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            graphics2D.drawImage(tmp, 0, 0, outputWidth, outputHeight, null);
            graphics2D.dispose();
        }

        // Convert back to map colors
        int[] intPixels = ((java.awt.image.DataBufferInt) outputImage.getRaster().getDataBuffer()).getData();
        ColorConverterType converterType = ColorConverterType.ARGB;
        for (int i = 0; i < outputLength; i++) {
            output[i] = converterType.convert(intPixels[i]);
        }
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
        return COLOR_MAP_DATA.getColor(color);
    }

    /**
     * Gets the real RGB color belonging to a color code
     * 
     * @param index of the color
     * @return real RGB color
     */
    public static final Color getRealColor(int index) {
        return COLOR_MAP_DATA.getColor(index);
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

    /**
     * Gets the {@link IndexColorModel} that has the R/G/B values mapped to the byte color map indices.
     * This can be used to encode map data to an indexed image format.
     * 
     * @return Index Color Model
     */
    public static IndexColorModel getIndexColorModel() {
        return COLOR_INDEX_MODEL;
    }

    /**
     * This class is used to convert raw color data into the
     * map color format without using an int[] buffer in between.
     * It is used when reading in images.
     */
    private static abstract class ColorConverterType {
        public abstract byte convert(int color);
        public abstract byte convertBytes(byte[] buffer, int index);

        public static final ColorConverterType RGB = new ColorConverterType() {
            @Override
            public byte convert(int color) {
                return COLOR_MAP_DATA.get(color >> 16, color >> 8, color);
            }

            @Override
            public byte convertBytes(byte[] buffer, int index) {
                return COLOR_MAP_DATA.get(buffer[index], buffer[index + 1], buffer[index + 2]);
            }
        };

        public static final ColorConverterType ARGB = new ColorConverterType() {
            @Override
            public byte convert(int color) {
                if ((color & 0x80000000) == 0) {
                    return COLOR_TRANSPARENT;
                } else {
                    return COLOR_MAP_DATA.get(color >> 16, color >> 8, color);
                }
            }

            @Override
            public byte convertBytes(byte[] buffer, int index) {
                if ((buffer[index] & 0x80) == 0) {
                    return COLOR_TRANSPARENT;
                } else {
                    return COLOR_MAP_DATA.get(buffer[index + 1], buffer[index + 2], buffer[index + 3]);
                }
            }
        };

        public static final ColorConverterType BGR = new ColorConverterType() {
            @Override
            public byte convert(int color) {
                return COLOR_MAP_DATA.get(color, color >> 8, color >> 16);
            }

            @Override
            public byte convertBytes(byte[] buffer, int index) {
                return COLOR_MAP_DATA.get(buffer[index + 2], buffer[index + 1], buffer[index]);
            }
        };

        public static final ColorConverterType ABGR = new ColorConverterType() {
            @Override
            public byte convert(int color) {
                if ((color & 0x80000000) == 0) {
                    return COLOR_TRANSPARENT;
                } else {
                    return COLOR_MAP_DATA.get(color, color >> 8, color >> 16);
                }
            }

            @Override
            public byte convertBytes(byte[] buffer, int index) {
                if ((buffer[index] & 0x80) == 0) {
                    return COLOR_TRANSPARENT;
                } else {
                    return COLOR_MAP_DATA.get(buffer[index + 3], buffer[index + 2], buffer[index + 1]);
                }
            }
        };
    }
}
