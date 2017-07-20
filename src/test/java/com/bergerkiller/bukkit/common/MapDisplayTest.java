package com.bergerkiller.bukkit.common;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

import javax.imageio.ImageIO;

import org.junit.Test;

import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.mountiplex.MountiplexUtil;

public class MapDisplayTest {

    //@Test
    public void createColorPaletteFieldImage() {
        try {
            // Creates a 4096x4096 .png image of all available colors in the palette.
            // This helps to verify correct functioning of color to colormap index conversion
            BufferedImage img = new BufferedImage(4096, 4096, BufferedImage.TYPE_INT_RGB);
            {
                HashSet<Integer> coll = new HashSet<Integer>();
                for (int y = 0; y < 4096; y++) {
                    for (int x = 0; x < 4096; x++) {
                        int r = (x & 0xFF);
                        int g = (y & 0xFF);
                        int b_1 = ((y >> 8) & 0xF);
                        int b_2 = ((x >> 8) & 0xF);
                        if ((b_2 & 0x1) != 0x0) {
                            r = 255 - r;
                        }
                        if ((b_1 & 0x1) != 0x0) {
                            g = 255 - g;
                        }
                        int b = b_2 | (b_1 << 4);
                        
                        byte c = MapColorPalette.getColor(r, g, b);
                        Color cc = MapColorPalette.getRealColor(c);
                        r = cc.getRed();
                        g = cc.getGreen();
                        b = cc.getBlue();
                        
                        int rgb = (r << 0) | (g << 8) | (b << 16);
                        img.setRGB(x, y, rgb);
                    }
                }
            }
            ImageIO.write(img, "png", new File("misc/map_palette_field.png"));
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
        for (int i = 0; i < MapColorPalette.COLOR_COUNT; i++) {
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
}
