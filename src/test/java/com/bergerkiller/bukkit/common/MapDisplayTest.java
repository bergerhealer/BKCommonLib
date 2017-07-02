package com.bergerkiller.bukkit.common;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.FileWriter;

import org.junit.Test;

import com.bergerkiller.bukkit.common.map.MapColorPalette;

public class MapDisplayTest {

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
