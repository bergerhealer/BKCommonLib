package com.bergerkiller.bukkit.common.map;

import java.awt.Color;
import java.util.ArrayList;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.map.color.MapColorSpaceData;
import com.bergerkiller.mountiplex.MountiplexUtil;

/**
 * This class is used to generate the RGB<>Color transformation mapping.
 * It uses some pretty sophisticated and slow maths, but because it's run
 * once and then stored as a binary blob this does not matter.<br>
 * <br>
 * This generator generates the best matching map color for all RGB color values available,
 * with an index table from map color to RGB. It uses the ciede2000 color differencing algorithm.
 */
public class MCSDGenCiede2000 extends MapColorSpaceData {
    private final Entry[] entries;

    /**
     * Initializes a new Map Color Space generator.
     * The Minecraft version is important, as it dictates which
     * color values are available.
     * 
     * @param minecraftVersion to target
     */
    public MCSDGenCiede2000(String minecraftVersion) {
        EntryBuilder builder = new EntryBuilder();
        builder.addBaseColor(127, 178, 56)
               .addBaseColor(247, 233, 163)
               .addBaseColor(199, 199, 199)
               .addBaseColor(255, 0, 0)
               .addBaseColor(160, 160, 255)
               .addBaseColor(167, 167, 167)
               .addBaseColor(0, 124, 0)
               .addBaseColor(255, 255, 255)
               .addBaseColor(164, 168, 184)
               .addBaseColor(151, 109, 77)
               .addBaseColor(112, 112, 112)
               .addBaseColor(64, 64, 255)
               .addBaseColor(143, 119, 72)
               .addBaseColor(255, 252, 245)
               .addBaseColor(216, 127, 51)
               .addBaseColor(178, 76, 216)
               .addBaseColor(102, 153, 216)
               .addBaseColor(229, 229, 51)
               .addBaseColor(127, 204, 25)
               .addBaseColor(242, 127, 165)
               .addBaseColor(76, 76, 76)
               .addBaseColor(153, 153, 153)
               .addBaseColor(76, 127, 153)
               .addBaseColor(127, 63, 178)
               .addBaseColor(51, 76, 178)
               .addBaseColor(102, 76, 51)
               .addBaseColor(102, 127, 51)
               .addBaseColor(153, 51, 51)
               .addBaseColor(25, 25, 25)
               .addBaseColor(250, 238, 77)
               .addBaseColor(92, 219, 213)
               .addBaseColor(74, 128, 255)
               .addBaseColor(0, 217, 58)
               .addBaseColor(129, 86, 49)
               .addBaseColor(112, 2, 0);

        if (MountiplexUtil.evaluateText(minecraftVersion, ">=", "1.12")) {
            builder.addBaseColor(209, 177, 161)
                   .addBaseColor(159, 82, 36)
                   .addBaseColor(149, 87, 108)
                   .addBaseColor(112, 108, 138)
                   .addBaseColor(186, 133, 36)
                   .addBaseColor(103, 117, 53)
                   .addBaseColor(160, 77, 78)
                   .addBaseColor(57, 41, 35)
                   .addBaseColor(135, 107, 98)
                   .addBaseColor(87, 92, 92)
                   .addBaseColor(122, 73, 88)
                   .addBaseColor(76, 62, 92)
                   .addBaseColor(76, 50, 35)
                   .addBaseColor(76, 82, 42)
                   .addBaseColor(142, 60, 46)
                   .addBaseColor(37, 22, 16);
        }

        if (MountiplexUtil.evaluateText(minecraftVersion, ">=", "1.16")) {
            builder.addBaseColor(189, 48, 49)
                   .addBaseColor(148, 63, 97)
                   .addBaseColor(92, 25, 29)
                   .addBaseColor(22, 126, 134)
                   .addBaseColor(58, 142, 140)
                   .addBaseColor(86, 44, 62)
                   .addBaseColor(20, 180, 133);
        }

        this.entries = builder.build();
        if (this.entries.length > 256) {
            throw new IllegalArgumentException("More than 256 colors: " + this.entries.length);
        }
    }

    /**
     * Performs a slow but highly accurate lookup of the correct map color
     * to approximately display a certain RGB value.
     * 
     * @param r red color component
     * @param g green color component
     * @param b blue color component
     * @param limitDiff whether to use the maxRGBDiff to limit the amount of colors checked each cycle
     * @return approximated map color value
     */
    private byte generateColor(int r, int g, int b, boolean limitDiff) {
        LAB lab = LAB.fromRGB(r, g, b);
        Entry result = null;
        int minRGBDist = Integer.MAX_VALUE;
        double minDist = Double.MAX_VALUE;
        for (Entry entry : this.entries) {
            int rgbDist = entry.dist(r, g, b);
            if (limitDiff && rgbDist > entry.maxRGBDiff) {
                continue;
            }

            double dist = LAB.ciede2000(entry.lab, lab);
            if (dist < minDist) {
                minDist = dist;
                minRGBDist = 1000 + rgbDist + (rgbDist / 4);
                result = entry;
            }
        }

        if (minRGBDist > result.maxRGBDiff) {
            result.maxRGBDiff = minRGBDist;
        }

        return result.code;
    }

    /**
     * Generates the map color space data.
     * This can take some time.
     * 
     * @return map color space data
     */
    public void generate() {
        this.clear();

        for (Entry entry : this.entries) {
            // Store color information
            setColor(entry.code, entry.rgb);

            // Reset maxRGBDiff
            entry.maxRGBDiff = 0;
        }

        // Generate some colors right away that are expected to have minimal differences
        // This quickly initializes the maxRGBDiff value of all entries, speeding up the algorithm later
        final int PREGEN_STEP = 4;
        for (int r = (PREGEN_STEP >> 1); r < 256; r += PREGEN_STEP) {
            Logging.LOGGER_MAPDISPLAY.info("Pregenerating color data " + (r + 1) + "/256");
            for (int g = (PREGEN_STEP >> 1); g < 256; g += PREGEN_STEP) {
                for (int b = (PREGEN_STEP >> 1); b < 256; b += PREGEN_STEP) {
                    set(r, g, b, generateColor(r, g, b, false));
                }
            }
        }

        // All other pixels in-between use maxRGBDiff to avoid checking all colors
        for (int r = 0; r < 256; r++) {
            Logging.LOGGER_MAPDISPLAY.info("Generating color data " + (r + 1) + " / 256");
            for (int g = 0; g < 256; g++) {
                for (int b = 0; b < 256; b++) {
                    if (get(r, g, b) == 0) {
                        set(r, g, b, generateColor(r, g, b, true));
                    }
                }
            }
        }
    }

    private static class EntryBuilder {
        private final ArrayList<Entry> entries = new ArrayList<Entry>();

        public EntryBuilder addBaseColor(int r, int g, int b) {
            return addBaseColor(r, g, b, 255);
        }

        public EntryBuilder addBaseColor(int r, int g, int b, int a) {
            int index = this.entries.size();
            this.entries.add(new Entry(index++, r, g, b, a, 180));
            this.entries.add(new Entry(index++, r, g, b, a, 220));
            this.entries.add(new Entry(index++, r, g, b, a, 255));
            this.entries.add(new Entry(index++, r, g, b, a, 135));
            return this;
        }

        public Entry[] build() {
            return this.entries.toArray(new Entry[this.entries.size()]);
        }
    }

    private static class Entry {
        public final byte code;
        public final Color rgb;
        public final LAB lab;
        public int maxRGBDiff = 0;

        public Entry(int index, int r, int g, int b, int a, int f) {
            this.code = (byte) (index + 4);
            this.rgb = new Color((r*f)/255, (g*f)/255, (b*f)/255, a);
            this.lab = LAB.fromRGB(this.rgb.getRed(), this.rgb.getGreen(), this.rgb.getBlue());
        }
        
        public int dist(int r, int g, int b) {
            int dr = rgb.getRed() - r;
            int dg = rgb.getGreen() - g;
            int db = rgb.getBlue() - b;
            return dr * dr + dg * dg + db * db;
        }
    }

    /**
     * LAB and ciede2000 color differencing algorithm taken over, with performance improving modifications, from here:
     * https://github.com/StanfordHCI/c3/blob/master/java/src/edu/stanford/vis/color/LAB.java
     * 
     * @author Jeffrey Heer
     * 
     * ================================================================================
     * Copyright (c) 2011, Stanford University
     * All rights reserved.
     * 
     * Redistribution and use in source and binary forms, with or without
     * modification, are permitted provided that the following conditions are met:
     * 
     * * Redistributions of source code must retain the above copyright notice, this
     *   list of conditions and the following disclaimer.
     * 
     * * Redistributions in binary form must reproduce the above copyright notice,
     *   this list of conditions and the following disclaimer in the documentation
     *   and/or other materials provided with the distribution.
     * 
     * * The name Stanford University may not be used to endorse or promote products
     *   derived from this software without specific prior written permission.
     * 
     * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
     * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
     * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
     * DISCLAIMED. IN NO EVENT SHALL STANFORD UNIVERSITY BE LIABLE FOR ANY DIRECT,
     * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
     * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
     * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
     * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
     * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
     * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
     */
    private static class LAB
    {
        private static final CIE_XYZ[] sRGB_TO_CIE_R = new CIE_XYZ[256];
        private static final CIE_XYZ[] sRGB_TO_CIE_G = new CIE_XYZ[256];
        private static final CIE_XYZ[] sRGB_TO_CIE_B = new CIE_XYZ[256];

        static {
            for (int i = 0; i < 256; i++) {
                sRGB_TO_CIE_R[i] = new CIE_XYZ(i, 0.4124564, 0.2126729, 0.0193339, 0.950470);
                sRGB_TO_CIE_G[i] = new CIE_XYZ(i, 0.3575761, 0.7151522, 0.1191920, 1.0);
                sRGB_TO_CIE_B[i] = new CIE_XYZ(i, 0.1804375, 0.0721750, 0.9503041, 1.088830);
            }
        }

        public double L;
        public double a;
        public double b;
        public double ab;

        public LAB(double L, double a, double b) {
            this.L = L;
            this.a = a;
            this.b = b;

            //EDIT: Cache AB vector length
            this.ab = Math.sqrt(a*a + b*b);
        }

        public static LAB fromRGB(int ri, int gi, int bi) {
            // first, normalize RGB values
            /*
            double r = ri / 255.0;
            double g = gi / 255.0;
            double b = bi / 255.0;
            */

            // D65 standard referent
            /*
            double X = 0.950470, Y = 1.0, Z = 1.088830;
            */

            // second, map sRGB to CIE XYZ
            /*
            r = r <= 0.04045 ? r/12.92 : Math.pow((r+0.055)/1.055, 2.4);
            g = g <= 0.04045 ? g/12.92 : Math.pow((g+0.055)/1.055, 2.4);
            b = b <= 0.04045 ? b/12.92 : Math.pow((b+0.055)/1.055, 2.4);
            double x = (0.4124564*r + 0.3575761*g + 0.1804375*b) / X,
                    y = (0.2126729*r + 0.7151522*g + 0.0721750*b) / Y,
                    z = (0.0193339*r + 0.1191920*g + 0.9503041*b) / Z;
            */

            // EDIT: Combines first and second step with a cached result for improved performance
            CIE_XYZ r = sRGB_TO_CIE_R[ri & 0xFF];
            CIE_XYZ g = sRGB_TO_CIE_G[gi & 0xFF];
            CIE_XYZ b = sRGB_TO_CIE_B[bi & 0xFF];
            double x = (r.x + g.x + b.x),
                   y = (r.y + g.y + b.y),
                   z = (r.z + g.z + b.z);

            // third, map CIE XYZ to CIE L*a*b* and return
            x = x > 0.008856 ? Math.pow(x, 1.0/3) : 7.787037*x + 4.0/29;
            y = y > 0.008856 ? Math.pow(y, 1.0/3) : 7.787037*y + 4.0/29;
            z = z > 0.008856 ? Math.pow(z, 1.0/3) : 7.787037*z + 4.0/29;

            double L = 116*y - 16,
                    A = 500*(x-y),
                    B = 200*(y-z);

            return new LAB(L,A,B);
        }

        public static double ciede2000(LAB x, LAB y) {
            // adapted from Sharma et al's MATLAB implementation at
            //  http://www.ece.rochester.edu/~gsharma/ciede2000/

            // parametric factors, use defaults
            double kl = 1, kc = 1, kh = 1;

            // compute terms
            double pi = Math.PI,
                    L1 = x.L, a1 = x.a, b1 = x.b, Cab1 = x.ab, //EDIT: Math.sqrt(a1*a1 + b1*b1),
                    L2 = y.L, a2 = y.a, b2 = y.b, Cab2 = y.ab, //EDIT: Math.sqrt(a2*a2 + b2*b2),

                    Cab = 0.5*(Cab1 + Cab2),
                    Cab_P7 = Math.pow(Cab,7), //EDIT
                    Pow_25_7 = 6103515625.0, //EDIT Math.pow(25,7)
                    G = 0.5*(1 - Math.sqrt(Cab_P7/(Cab_P7+Pow_25_7))),
                    ap1 = (1+G) * a1,
                    ap2 = (1+G) * a2,
                    Cp1 = Math.sqrt(ap1*ap1 + b1*b1),
                    Cp2 = Math.sqrt(ap2*ap2 + b2*b2),
                    Cpp = Cp1 * Cp2;

            // ensure hue is between 0 and 2pi
            double hp1 = Math.atan2(b1, ap1); if (hp1 < 0) hp1 += 2*pi;
            double hp2 = Math.atan2(b2, ap2); if (hp2 < 0) hp2 += 2*pi;

            double dL = L2 - L1,
                    dC = Cp2 - Cp1,
                    dhp = hp2 - hp1;

            if (dhp > +pi) dhp -= 2*pi;
            if (dhp < -pi) dhp += 2*pi;
            if (Cpp == 0) dhp = 0;

            // Note that the defining equations actually need
            // signed Hue and chroma differences which is different
            // from prior color difference formulae
            double dH = 2 * Math.sqrt(Cpp) * Math.sin(dhp/2);

            // Weighting functions
            double Lp = 0.5 * (L1 + L2),
                    Cp = 0.5 * (Cp1 + Cp2);

            // Average Hue Computation
            // This is equivalent to that in the paper but simpler programmatically.
            // Average hue is computed in radians and converted to degrees where needed
            double hp = 0.5 * (hp1 + hp2);
            // Identify positions for which abs hue diff exceeds 180 degrees 
            if (Math.abs(hp1-hp2) > pi) hp -= pi;
            if (hp < 0) hp += 2*pi;

            // Check if one of the chroma values is zero, in which case set 
            // mean hue to the sum which is equivalent to other value
            if (Cpp == 0) hp = hp1 + hp2;

            double Lpm502 = (Lp-50) * (Lp-50),
                    Sl = 1 + 0.015*Lpm502 / Math.sqrt(20+Lpm502),
                    Sc = 1 + 0.045*Cp,
                    T = 1 - 0.17*Math.cos(hp - pi/6)
                    + 0.24*Math.cos(2*hp)
                    + 0.32*Math.cos(3*hp+pi/30)
                    - 0.20*Math.cos(4*hp - 63*pi/180),
                    Sh = 1 + 0.015 * Cp * T,
                    ex = (180/pi*hp-275) / 25,
                    delthetarad = (30*pi/180) * Math.exp(-1 * (ex*ex)),
                    Rc =  2 * Math.sqrt(Math.pow(Cp,7) / (Math.pow(Cp,7) + Pow_25_7)),
                    RT = -1 * Math.sin(2*delthetarad) * Rc;

            dL = dL / (kl*Sl);
            dC = dC / (kc*Sc);
            dH = dH / (kh*Sh);

            // The CIE 00 color difference
            return Math.sqrt(dL*dL + dC*dC + dH*dH + RT*dC*dH);
        }

    }

    private static class CIE_XYZ {
        public final double x, y, z;

        public CIE_XYZ(int value, double fx, double fy, double fz, double referent) {
            double d = (double) value / 255.0;
            d = d <= 0.04045 ? d/12.92 : Math.pow((d+0.055)/1.055, 2.4);
            this.x = (d * fx) / referent;
            this.y = (d * fy) / referent;
            this.z = (d * fz) / referent;
        }
    }
}
