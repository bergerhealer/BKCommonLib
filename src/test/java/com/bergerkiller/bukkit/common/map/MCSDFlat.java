package com.bergerkiller.bukkit.common.map;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.map.color.MapColorSpaceData;

/**
 * Map color space data that is read from and written to in a flat GZIP compressed format.
 * Reading and writing is fast, but the produced data is relatively large.
 */
public class MCSDFlat extends MapColorSpaceData {

    public void readFrom(InputStream stream) throws IOException {
        GZIPInputStream zip = new GZIPInputStream(stream);
        try {
            for (int i = 0; i < 256; i++) {
                int r = zip.read();
                int g = zip.read();
                int b = zip.read();
                int a = zip.read();
                this.setColor((byte) i, new Color(r, g, b, a));
            }
            for (int index = 0; index < (1 << 24); index++) {
                this.set(index, (byte) zip.read());
            }
        } finally {
            zip.close();
        }
    }

    public void writeTo(OutputStream stream) throws IOException {
        Logging.LOGGER_MAPDISPLAY.info("Compressing flat map color space data...");
        GZIPOutputStream zip = new GZIPOutputStream(stream);
        try {
            for (int i = 0; i < 256; i++) {
                Color color = this.getColor((byte) i);
                zip.write(color.getRed());
                zip.write(color.getGreen());
                zip.write(color.getBlue());
                zip.write(color.getAlpha());
            }
            for (int index = 0; index < (1 << 24); index++) {
                zip.write(this.get(index) & 0xFF);
            }
            Logging.LOGGER_MAPDISPLAY.info("Finished compressing map color space data");
        } finally {
            zip.close();
        }
    }
}
