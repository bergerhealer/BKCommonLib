package com.bergerkiller.bukkit.common.map.color;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.IntPredicate;
import java.util.logging.Level;
import java.util.stream.IntStream;
import java.util.zip.Deflater;
import java.util.zip.InflaterInputStream;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.io.BitInputStream;
import com.bergerkiller.bukkit.common.io.BitOutputStream;
import com.bergerkiller.bukkit.common.io.BitPacket;
import com.bergerkiller.bukkit.common.utils.StreamUtil;

/**
 * Stores all map color space information in a highly compressed bubble format.
 * In this format it is assumed the color data is in cell shapes. It stores the cell
 * borders separate from the colors using the {@link MCSDWebbingCodec}. These cells
 * are then filled with colors to reproduce the original image.
 */
public class MCSDBubbleFormat extends MapColorSpaceData {
    public final boolean[][] strands = new boolean[256][256 * 256];
    public final ArrayList<Bubble> bubbles = new ArrayList<Bubble>();
    private MapColorSpaceData input_colors = null;
    private int max_iterations = 1000;

    /**
     * Sets the maximum number of cycles to perform when encoding the bubble boundary information
     * using the Webbing Codec. Higher numbers improve compression, but take much longer.
     * Values above 100000 have diminishing returns.
     * 
     * @param maxIterations before giving up
     */
    public void setMaxIterations(int maxIterations) {
        this.max_iterations = maxIterations;
    }

    /**
     * Gets whether a bubble boundary is present at a particular position.
     * Positions outside of the color space always return true.
     * 
     * @param x - coordinate
     * @param y - coordinate
     * @param z - coordinate (slice depth)
     * @return True if a boundary exists
     */
    public boolean getStrand(int x, int y, int z) {
        if (x < 0 || y < 0 || z < 0 || x >= 256 || y >= 256 || z >= 256) {
            return true;
        }
        return this.strands[z][x | (y << 8)];
    }

    public void readFrom(InputStream stream) throws IOException {
        BitInputStream bitStream = new BitInputStream(new BufferedInputStream(new InflaterInputStream(stream)));
        try {
            // Read all color RGB values
            for (int i = 0; i < 256; i++) {
                int r = bitStream.read();
                int g = bitStream.read();
                int b = bitStream.read();
                int a = bitStream.read();
                this.setColor((byte) i, new Color(r, g, b, a));
            }

            // Read all bubbles from the stream
            while (true) {
                Bubble bubble = new Bubble();
                bubble.color = (byte) bitStream.read();
                if (bubble.color == 0) {
                    break;
                }
                bubble.x = bitStream.read();
                bubble.y = bitStream.read();
                bubble.z_min = bitStream.read();
                bubble.z_max = bubble.z_min + bitStream.read();
                this.bubbles.add(bubble);
            }

            // Read bubble boundary information from the stream
            MCSDWebbingCodec codec = new MCSDWebbingCodec();
            for (int z = 0; z < 256; z++) {
                Arrays.fill(this.strands[z], false);
                codec.reset(strands[z], false);
                while (codec.readNext(bitStream));
            }

            // Initialize the colors with the bubble colors
            this.initColors();

            // Read color correction data for pixels unset (value = 0)
            for (int i = 0; i < (1 << 24); i++) {
                if (this.get(i) == 0) {
                    if (bitStream.readBits(1) == 0) {
                        this.set(i, this.get(i - 1));
                    } else {
                        int mode = bitStream.readBits(2);
                        if (mode == 0) {
                            this.set(i, this.get(i - 256));
                        } else if (mode == 1) {
                            this.set(i, this.get(i + 1));
                        } else if (mode == 2) {
                            this.set(i, this.get(i + 256));
                        } else {
                            this.set(i, (byte) bitStream.readBits(8));
                        }
                    }
                }
            }
        } finally {
            bitStream.close();
        }
    }

    public void writeTo(OutputStream stream) throws IOException {
        try (BitOutputStream bitStream = new BitOutputStream(StreamUtil.createDeflaterOutputStreamWithCompressionLevel(stream, Deflater.BEST_COMPRESSION))) {

            // Input colors will be used to correct errors in the color model
            // These are never written to and serve as a backup while writing
            this.input_colors = new MapColorSpaceData();
            this.input_colors.readFrom(this);

            // Load in all bubble boundary information
            Logging.LOGGER_MAPDISPLAY.info("Loading bubble boundaries...");
            for (int z = 0; z < 256; z++) {
                boolean[] strands = this.strands[z];
                final int index_end = ((z + 1) << 16);
                for (int index = (z << 16); index < index_end; index++) {
                    int x = (index & 0xFF);
                    int y = ((index >> 8) & 0xFF);
                    byte color = get(index);
                    strands[index & (strands.length - 1)] = 
                            (x < 255 && color != get(index + 1)) ||
                            (y < 255 && color != get(index + 256));
                }
            }

            // Find connected blobs of color (restore colors after all bubbles are found)
            Logging.LOGGER_MAPDISPLAY.info("Generating bubble spatial information...");
            for (int z = 0; z < 256; z++) {
                boolean[] strands = this.strands[z];
                int index_offset = (z << 16);
                for (int index = 0; index < (1 << 16); index++) {
                    if (strands[index]) {
                        continue;
                    }
                    byte color = this.get(index_offset + index);
                    if (color == 0) {
                        continue;
                    }
                    this.set(index_offset + index, (byte) 0);

                    Bubble bubble = new Bubble();
                    bubble.x = (index & 0xFF);
                    bubble.y = ((index >> 8) & 0xFF);
                    bubble.z_min = z;
                    bubble.z_max = z;
                    bubble.color = color;
                    bubble.pixels.add(new IntVector2(bubble.x, bubble.y));
                    spread(bubble);
                    this.bubbles.add(bubble);
                }
            }
            this.readFrom(this.input_colors);

            // Merge the blobs with same colors sharing points
            Logging.LOGGER_MAPDISPLAY.info("Connecting bubbles in the z-axis...");
            for (int bubbleidx = 0; bubbleidx < this.bubbles.size(); bubbleidx++) {
                Bubble bubble = this.bubbles.get(bubbleidx);
                Iterator<Bubble> iter = this.bubbles.iterator();
                while (iter.hasNext()) {
                    Bubble otherBubble = iter.next();

                    // Mergable?
                    if (bubble == otherBubble || bubble.color != otherBubble.color) {
                        continue;
                    }

                    // Sharing a Z-border?
                    if (bubble.z_min != (otherBubble.z_max + 1) && bubble.z_max != (otherBubble.z_min - 1)) {
                        continue;
                    }

                    // Sharing any pixel at all?
                    boolean sharesPixels = false;
                    for (IntVector2 p : otherBubble.pixels) {
                        if (bubble.pixels.contains(p)) {
                            sharesPixels = true;
                            break;
                        }
                    }
                    if (!sharesPixels) {
                        continue;
                    }

                    // Remove all pixels of the other cell and update z-bounds to merge the two
                    bubble.pixels.retainAll(otherBubble.pixels);
                    if (otherBubble.z_min < bubble.z_min) {
                        bubble.z_min = otherBubble.z_min;
                    }
                    if (otherBubble.z_max > bubble.z_max) {
                        bubble.z_max = otherBubble.z_max;
                    }
                    iter.remove();
                }
            }

            // Calculate bubble x/y
            Logging.LOGGER_MAPDISPLAY.info("Calculating bubble positions...");
            for (Bubble bubble : this.bubbles) {
                // Adjust x/y to be in the middle of all pixels found
                int avg_x = 0;
                int avg_y = 0;
                for (IntVector2 pixel : bubble.pixels) {
                    avg_x += pixel.x;
                    avg_y += pixel.z;
                }
                avg_x /= bubble.pixels.size();
                avg_y /= bubble.pixels.size();
                IntVector2 closest = null;
                int minDistSq = Integer.MAX_VALUE;
                for (IntVector2 pixel : bubble.pixels) {
                    int dx = (pixel.x - avg_x);
                    int dy = (pixel.z - avg_y);
                    int distSq = (dx * dx) + (dy * dy);
                    if (distSq < minDistSq) {
                        minDistSq = distSq;
                        closest = pixel;
                    }
                }
                bubble.x = closest.x;
                bubble.y = closest.z;
            }

            // Write out all color RGB values
            for(int i = 0; i < 256; i++) {
                Color color = this.getColor((byte) i);
                bitStream.write(color.getRed());
                bitStream.write(color.getGreen());
                bitStream.write(color.getBlue());
                bitStream.write(color.getAlpha());
            }

            // Write out all bubble information
            for (Bubble bubble : bubbles) {
                bitStream.write(bubble.color & 0xFF);
                bitStream.write(bubble.x);
                bitStream.write(bubble.y);
                bitStream.write(bubble.z_min);
                bitStream.write(bubble.z_max - bubble.z_min);
            }
            bitStream.write(0);

            // Initialize the colors based on the known cell colors
            Logging.LOGGER_MAPDISPLAY.info("Initializing color information for " + bubbles.size() + " bubbles...");
            this.initColors();

            // Write cell information
            Logging.LOGGER_MAPDISPLAY.info("Writing bubble boundary information...");
            IntStream.range(0, 256)
                .mapToObj(z -> generateSlice(z))
                .parallel()
                .forEachOrdered(codec -> {
                    try {
                        codec.writePackets(bitStream);
                    } catch (IOException ex) {
                        Logging.LOGGER_MAPDISPLAY.log(Level.SEVERE, "IO Exception while writing packets", ex); // Oh well.
                    }
                });

            // Write missing color information using bit encoding
            Logging.LOGGER_MAPDISPLAY.info("Correcting missing color information...");
            List<BitPacket> colorCodes = new ArrayList<BitPacket>();
            for (int i = 0; i < (1 << 24); i++) {
                if (this.get(i) == 0) {
                    int x = (i & 0xFF);
                    int y = (i >> 8) & 0xFF;
                    BitPacket code = new BitPacket();
                    byte color = this.input_colors.get(i);
                    if (x > 0 && this.get(i - 1) == color) {
                        code.write(0, 1);
                    } else {
                        code.write(1, 1);
                        if (y > 0 && this.get(i - 256) == color) {
                            code.write(0, 2);
                        } else if (x < 255 && this.get(i + 1) == color) {
                            code.write(1, 2);
                        } else if (y < 255 && this.get(i + 256) == color) {
                            code.write(2, 2);
                        } else {
                            code.write(3, 2);
                            code.write(color & 0xFF, 8);
                        }
                    }
                    colorCodes.add(code);
                    this.set(i, color);
                }
            }
            for (BitPacket code : colorCodes) {
                bitStream.writeBits(code.data, code.bits);
            }
        }
    }

    private void spread(Bubble cell) {
        boolean hasChanges;
        do {
            hasChanges = false;
            for (int i = 0; i < cell.pixels.size(); i++) {
                IntVector2 pixel = cell.pixels.get(i);
                for (int dx = -1; dx <= 1; dx += 2) {
                    for (int dy = -1; dy <= 1; dy += 2) {
                        int x = pixel.x + dx;
                        int y = pixel.z + dy;

                        // Bounds check
                        if (x < 0 || y < 0 || x >= 256 || y >= 256) {
                            continue;
                        }

                        // Only spread when it is the same color, and the pixel is not sitting on a cell boundary
                        if (get(x, y, cell.z_min) != cell.color || getStrand(x, y, cell.z_min)) {
                            continue;
                        }

                        set(x, y, cell.z_min, (byte) 0);
                        cell.pixels.add(new IntVector2(x, y));
                        hasChanges = true;
                    }
                }
            }
        } while (hasChanges);
    }

    private void initColors() {
        // Set initial cell colors
        this.clearRGBData();
        for (MCSDBubbleFormat.Bubble cell : bubbles) {
            for (int z = cell.z_min; z <= cell.z_max; z++) {
                this.set(cell.x, cell.y, z, cell.color);
            }
        }
        spreadColors();
    }

    private void spreadColors() {
        // As we'll be processing pretty much every element, allocate the full space (60MB)
        // The range of the buffer we process shrinks as we spread
        StrandBuffer buf;
        {
            final int[] buffer = new int[1 << 24];
            int count = -1;
            for (int z = 0; z < 256; z++) {
                boolean[] layerStrands = this.strands[z];
                int indexOffset = z << 16;
                for (int i = 0; i < (1 << 16); i++) {
                    if (!layerStrands[i]) {
                        buffer[++count] = indexOffset + i;
                    }
                }
            }
            count++;
            buf = new StrandBuffer(buffer, count);
        }

        // Process all until no more changes remain
        buf.process(index -> {
            byte color;

            boolean col = ((index & 0xFF) < 0xFF);
            boolean row = ((index & 0xFF00) < 0xFF00);

            if (col && row) {
                if ((color = this.get(index)) != 0) {
                    this.set(index + 1, color);
                    this.set(index + 256, color);
                    return true;
                } else if ((color = this.get(index + 1)) != 0) {
                    this.set(index, color);
                    this.set(index + 256, color);
                    return true;
                } else if ((color = this.get(index + 256)) != 0) {
                    this.set(index, color);
                    this.set(index + 1, color);
                    return true;
                }
            } else if (col) {
                if ((color = this.get(index)) != 0) {
                    this.set(index + 1, color);
                    return true;
                } else if ((color = this.get(index + 1)) != 0) {
                    this.set(index, color);
                    return true;
                }
            } else if (row) {
                if ((color = this.get(index)) != 0) {
                    this.set(index + 256, color);
                    return true;
                } else if ((color = this.get(index + 256)) != 0) {
                    this.set(index, color);
                    return true;
                }
            }

            return false;
        });
    }

    private static class StrandBuffer {
        private final int[] buf;
        private int start, end;

        public StrandBuffer(int[] buffer, int count) {
            this.buf = buffer;
            this.start = 0;
            this.end = count - 1;
        }

        public void process(IntPredicate strandIndexProc) {
            while (forward(strandIndexProc) && reverse(strandIndexProc)) {
                // Process alternating over and over until there are no more changes
            }
        }

        public boolean forward(IntPredicate strandIndexProc) {
            int[] buf = this.buf;
            int writeIdx = start - 1;
            int endIdx = end;
            boolean changed = false;
            for (int i = start; i <= endIdx; ++i) {
                int strandIndex = buf[i];
                if (strandIndexProc.test(strandIndex)) {
                    changed = true;
                } else {
                    buf[++writeIdx] = strandIndex;
                }
            }
            this.end = writeIdx;
            return changed;
        }

        public boolean reverse(IntPredicate strandIndexProc) {
            int[] buf = this.buf;
            int writeIdx = end + 1;
            int startIdx = start;
            boolean changed = false;
            for (int i = end; i >= startIdx; --i) {
                int strandIndex = buf[i];
                if (strandIndexProc.test(strandIndex)) {
                    changed = true;
                } else {
                    buf[--writeIdx] = strandIndex;
                }
            }
            this.start = writeIdx;
            return changed;
        }
    }

    private MCSDWebbingCodec generateSlice(int z) {
        // Split all found coordinates in edge points, intersections and lines
        // First optimize the order of rendering from the edge points
        // Then try to optimize the order of the remaining intersections
        // Then finally, try to optimize the order in which lines are drawn
        boolean[] cells = this.strands[z];
        List<IntVector2> edges = new ArrayList<IntVector2>();
        List<IntVector2> intersects = new ArrayList<IntVector2>();
        List<IntVector2> lines = new ArrayList<IntVector2>();
        {
            boolean[] data3x3 = new boolean[9];
            int index = 0;
            for (int y = 0; y < 256; y++) {
                for (int x = 0; x < 256; x++) {
                    if (!cells[index++]) {
                        continue;
                    }

                    IntVector2 coord = new IntVector2(x, y);
                    int n = 0;
                    for (int dy = -1; dy <= 1; dy++) {
                        for (int dx = -1; dx <= 1; dx++) {
                            int mx = coord.x + dx;
                            int my = coord.z + dy;
                            if (mx < 0 || my < 0 || mx >= 256 || my >= 256) {
                                data3x3[n++] = false;
                            } else {
                                data3x3[n++] = cells[mx | (my << 8)];
                            }
                        }
                    }
                    if (MCSDWebbingCodec.EDGE_PATTERN.matches(data3x3)) {
                        edges.add(coord);
                    } else if (MCSDWebbingCodec.LINE_PATTERN.matches(data3x3)) {
                        lines.add(coord);
                    } else {
                        intersects.add(coord);
                    }
                }
            }
        }

        Logging.LOGGER_MAPDISPLAY.info("Processing z=" + z + ", " + edges.size() + " edges, " + intersects.size() + " intersects, " + lines.size() + " lines"); 

        MCSDWebbingCodec codec = new MCSDWebbingCodec();
        codec.reset(cells, true);

        codec.processBest(edges, max_iterations);
        codec.processBest(intersects, max_iterations);
        codec.processBest(lines, max_iterations);
        return codec;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof MCSDBubbleFormat) {
            MCSDBubbleFormat other = (MCSDBubbleFormat) o;
            for (int i = 0; i < strands.length; i++) {
                if (other.strands[i] != this.strands[i]) {
                    return false;
                }
            }
            if (bubbles.size() != other.bubbles.size()) {
                return false;
            }
            for (int i = 0; i < bubbles.size(); i++) {
                if (!bubbles.get(i).equals(other.bubbles.get(i))) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public static class Bubble {
        public int x, y;
        public int z_min;
        public int z_max;
        public byte color;
        private final ArrayList<IntVector2> pixels = new ArrayList<IntVector2>();

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (o instanceof Bubble) {
                Bubble other = (Bubble) o;
                return other.x == x && other.y == y &&
                        other.z_min == z_min && other.z_max == z_max &&
                        other.color == color;
            } else {
                return false;
            }
        }

        @Override
        public String toString() {
            return "cell{x="+x+", y="+y+", zmin="+z_min+", zmax="+z_max+", color="+(color & 0xFF)+"}";
        }
    }

}
