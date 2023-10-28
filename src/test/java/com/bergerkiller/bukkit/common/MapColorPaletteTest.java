package com.bergerkiller.bukkit.common;

import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Ignore;
import org.junit.Test;

import com.bergerkiller.bukkit.common.map.MapBlendMode;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MCSDGenCiede2000;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.color.MapColorSpaceData;
import com.bergerkiller.bukkit.common.map.color.MCSDBubbleFormat;
import com.bergerkiller.bukkit.common.map.color.MCSDFlat;
import com.bergerkiller.bukkit.common.map.util.MapDebugWindow;
import com.bergerkiller.mountiplex.MountiplexUtil;
import com.bergerkiller.mountiplex.reflection.SafeField;

public class MapColorPaletteTest {

    @Ignore
    @Test
    public void generateColorMap() {
        String version = "1.17"; // Minecraft version to generate the data for
        boolean regenerate_bubble_format = false; // Whether to regenerate the highly compressed bubble format file
        boolean debug_display = true; // Whether to display the final results in a debugging window
        int max_iterations = 2000000; // Sets compression versus compression time

        try {
            // First generate a flat format file that can be quickly read again
            // This helps when debugging the slower compression methods later
            // To regenerate it, simply delete the file from the ./misc directory
            String flat_filename = "misc/map_" + version.replace('.', '_') + "_flat.dat";
            if (!new File(flat_filename).exists()) {
                MCSDGenCiede2000 generated = new MCSDGenCiede2000(version);
                generated.generate();

                MCSDFlat flat = new MCSDFlat();
                flat.readFrom(generated);
                flat.writeTo(new FileOutputStream(flat_filename));
            }

            // Read the flat file format
            Logging.LOGGER_MAPDISPLAY.info("Loading flat color space data...");
            MCSDFlat flat = new MCSDFlat();
            flat.readFrom(new FileInputStream(flat_filename));

            // Generate a highly compressed 'bubble format' file that will be compiled with the application
            String bubble_filename = "src/main/resources/com/bergerkiller/bukkit/common/internal/resources/map/" +
                                     "map_" + version.replace('.', '_') + ".bub";
            if (regenerate_bubble_format || !new File(bubble_filename).exists()) {
                MCSDBubbleFormat originalGrid = new MCSDBubbleFormat();
                originalGrid.setMaxIterations(max_iterations);
                originalGrid.readFrom(flat);
                originalGrid.writeTo(new FileOutputStream(bubble_filename));
            }

            // Read the highly compressed 'bubble format'
            Logging.LOGGER_MAPDISPLAY.info("Loading bubble format data...");
            MCSDBubbleFormat bubble = new MCSDBubbleFormat();
            bubble.readFrom(new FileInputStream(bubble_filename));
            Logging.LOGGER_MAPDISPLAY.info("Finished loading all color map data!");

            // Verify the data we read is the same as the original flatfile data
            try {
                assertColorSpaceEqual(flat, bubble);
            } catch (Throwable t) {
                System.out.println(t.getMessage());
            }

            // Debug display the results
            if (debug_display) {
                MapTexture map = MapTexture.createEmpty(256, 256);
                MapDebugWindow window = MapDebugWindow.showMap(map);
                do {
                    int z = window.x(0, 255);
                    byte[] slice = new byte[256 * 256];
                    if (window.y() >= 128) {
                        // Original flat data
                        int i = 0;
                        for (int y = 0; y < 256; y++) {
                            for (int x = 0; x < 256; x++) {
                                slice[i++] = flat.get(x, y, z);
                            }
                        }
                    } else if (window.y() >= 64) {
                        // Generated bubble format data
                        int i = 0;
                        for (int y = 0; y < 256; y++) {
                            for (int x = 0; x < 256; x++) {
                                slice[i++] = bubble.get(x, y, z);
                            }
                        }
                    } else {
                        // Bubble format webbing strands only
                        for (int i = 0; i < slice.length; i++) {
                            slice[i] = bubble.strands[z][i] ? MapColorPalette.COLOR_RED : MapColorPalette.COLOR_BLACK;
                        }
                    }

                    map.writePixels(0, 0, 256, 256, slice);
                } while (window.waitNext());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Ignore
    @Test
    public void testImage() {
        MapColorPalette.getColor(128, 128, 128);
        try {
            Image image = ImageIO.read(new File("misc/map_test_bg.jpg"));
            MapColorPalette.convertImage(image);
            long start = System.currentTimeMillis();
            for (int i = 0; i < 5; i++) {
                MapColorPalette.convertImage(image);
            }
            System.out.println("Loading the image 5x took: " + (System.currentTimeMillis() - start) + " ms");
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Test
    public void testColorBlending() {
        // Verifies that the color blending modes work correctly
        assertEquals((byte) 99, MapBlendMode.MULTIPLY.process((byte) 12, (byte) 64));
        assertEquals((byte) 19, MapBlendMode.MULTIPLY.process((byte) 89, (byte) 18));
        assertEquals((byte) 114, MapBlendMode.AVERAGE.process((byte) 17, (byte) 24));
        assertEquals((byte) 127, MapBlendMode.SUBTRACT.process((byte) 17, (byte) 24));
        assertEquals((byte) 82, MapBlendMode.ADD.process((byte) 17, (byte) 24));
    }

    @Ignore
    @Test
    public void testMapIndices() {
        for (int a = 0; a < 255; a++) {
            for (int b = 0; b < 255; b++) {
                int index = (b << 8) | a;
                assertEquals(index, MapColorPalette.getMapIndex((byte) a, (byte) b));
            }
        }
    }

    @Test
    public void testColorMapDuplex() {
        // Verifies that all colors gotten using getColor, are also retrieved when getting by RGB
        for (int i = 4; i < MapColorPalette.getColorCount(); i++) {
            Color rgb = MapColorPalette.getRealColor(i);
            assertEquals(i, MapColorPalette.getColor(rgb) & 0xFF);
        }
    }

    @Test
    public void testTransparentColors() {
        // Verify the first 4 color indices are transparent colors
        for (int i = 0; i < 4; i++) {
            assertEquals(MapColorPalette.getRealColor(i), new Color(0, 0, 0, 0));
        }
    }

    @Test
    public void testColorPaletteColors() {
        // Verify the constants have the correct colors
        verifyColor("COLOR_WHITE", new Color(255, 255, 255));
        verifyColor("COLOR_BLACK", new Color(13, 13, 13));
    }

    @Ignore
    @Test
    public void createColorPaletteFieldImage() {
        try {
            // Creates a 4096x4096 .png image of all available colors in the palette.
            // This helps to verify correct functioning of color to colormap index conversion
            MCSDFlat flat = new MCSDFlat();
            String version = "1_17";
            flat.readFrom(new FileInputStream("misc/map_" + version + "_flat.dat"));
            BufferedImage img = new BufferedImage(4096, 4096, BufferedImage.TYPE_INT_RGB);

            {
                for (int y = 0; y < 4096; y++) {
                    for (int x = 0; x < 4096; x++) {
                        int b = (x & 0xFF);
                        int g = (y & 0xFF);
                        int b_1 = ((y >> 8) & 0xF);
                        int b_2 = ((x >> 8) & 0xF);
                        if ((b_2 & 0x1) != 0x0) {
                            b = 255 - b;
                        }
                        if ((b_1 & 0x1) != 0x0) {
                            g = 255 - g;
                        }
                        int r = b_2 | (b_1 << 4);

                        byte c = flat.get(r, g, b);
                        Color cc = flat.getColor(c);
                        r = cc.getRed();
                        g = cc.getGreen();
                        b = cc.getBlue();

                        int rgb = (b << 0) | (g << 8) | (r << 16);
                        img.setRGB(x, y, rgb);
                    }
                }
            }
            ImageIO.write(img, "png", new File("misc/map_palette_field_" + version + ".png"));
        } catch (Throwable t) {
            throw MountiplexUtil.uncheckedRethrow(t);
        }
    }
    
    //@Test
    public void exportColorPalette() {
        // Exports all color palette entries to an index table in HTML format
        int colorsPerRow = 8;
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><body><table>");
        html.append("<tr>");
        for (int i = 0; i < colorsPerRow; i++) {
            html.append("<th>Color</th><th>Value</th><th width=50px/>");
        }
        html.append("</tr>\n");
        int colCtr = 0;
        for (int i = 0; i < MapColorPalette.getColorCount(); i++) {
            if (colCtr == 0) {
                html.append("<tr>");
            }
            writeColumns(html, i);
            if (++colCtr == colorsPerRow) {
                colCtr = 0;
                html.append("</tr>\n");
            } else {
                html.append("<td width=50px/>");
            }
        }
        html.append("</table></body></html>");

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter("misc/map_palette.htm"));
            try {
                writer.write(html.toString());
            } finally {
                writer.close();
            }
        } catch (Exception s) {
            s.printStackTrace();
        }
    }

    private void writeColumns(StringBuilder b, int i) {
        Color c = MapColorPalette.getRealColor((byte) i);
        String hex = String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
        String f;
        if ((c.getRed() + c.getGreen() + c.getBlue()) >= (128 * 3)) {
            f = "<font color=\"#000000\">";
        } else {
            f = "<font color=\"#FFFFFF\">";
        }
        String name = hex;
        if (i < 4) {
            name = "transparent";
        }
        b.append("<td bgcolor=\"").append(hex).append("\">").append(f).append(name).append("</font></td>");
        b.append("<td bgcolor=\"").append(hex).append("\">").append(f).append(i).append("</font></td>");
    }

    private void verifyColor(String constantName, Color expected) {
        byte color = SafeField.get(MapColorPalette.class, constantName, byte.class);
        Color c = MapColorPalette.getRealColor(color);
        if (!c.equals(expected)) {
            throw new RuntimeException(
                    "Color constant " + constantName + "[" + (int) color + "] has an invalid color: " +
                    "color=" + c.toString() + ", expected=" + expected.toString());
        }
    }

    private void assertColorSpaceEqual(MapColorSpaceData data1, MapColorSpaceData data2) {
        for (int r = 0; r < 256; r++) {
            for (int g = 0; g < 256; g++) {
                for (int b = 0; b < 256; b++) {
                    byte color1 = data1.get(r, g, b);
                    byte color2 = data2.get(r, g, b);
                    if (color1 != color2) {
                        fail("Color code is different at [" + r + ", " + g + ", " + b + "]: " +
                                (color1 & 0xFF) + " != " + (color2 & 0xFF));
                    }
                }
            }
        }
        for (int i = 0; i < 256; i++) {
            Color color1 = data1.getColor((byte) i);
            Color color2 = data2.getColor((byte) i);
            if (!color1.equals(color2)) {
                fail("Color value is different for c=" + i + ": " + 
                        "[r=" + color1.getRed() + ", g=" + color1.getGreen() + ", b=" + color1.getBlue() + ", a=" + color1.getAlpha() + "] != " +
                        "[r=" + color2.getRed() + ", g=" + color2.getGreen() + ", b=" + color2.getBlue() + ", a=" + color2.getAlpha() + "]");
            }
        }
    }
}
