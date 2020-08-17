package com.bergerkiller.bukkit.common.map.color;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.io.BitInputStream;
import com.bergerkiller.bukkit.common.io.BitOutputStream;
import com.bergerkiller.bukkit.common.io.BitPacket;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;

/**
 * Encodes or decodes a 256x256 grid of booleans by walking down the connected lines and encoding them
 * using drawing instructions. For example, a diagonal line in the grid may be encoded as follows:
 * <ul>
 * <li>SET_POSITION(23, 56)</li>
 * <li>SET_DX(-1)</li>
 * <li>SET_DY(1)</li>
 * <li>MOVE DX AND DRAW</li>
 * <li>MOVE DX AND DRAW</li>
 * <li>MOVE DY AND DRAW</li>
 * <li>MOVE DX AND DRAW</li>
 * <li>MOVE DX AND DRAW</li>
 * <li>MOVE DY AND DRAW</li>
 * <li>etc.</li>
 * </ul>
 * 
 * For encoding the data, the follow bits are written out in sequence:
 * <ul>
 * <li>00 -> MOVE DX AND DRAW</li>
 * <li>01 -> MOVE DY AND DRAW</li>
 * <li>10 -> MOVE DX+DY AND DRAW</li>
 * <li>11 100 -> SET DX = -1</li>
 * <li>11 101 -> SET DX = 1</li>
 * <li>11 110 -> SET DY = -1</li>
 * <li>11 111 -> SET DY = 1</li>
 * <li>11 00 [byte_x][byte_y] -> SET POSITION AND DRAW</li>
 * <li>11 01 -> STOP</li>
 * </ul>
 */
public class MCSDWebbingCodec {
    private int written_cells;
    private int last_x, last_y;
    private int last_dx, last_dy;
    public boolean[] strands = new boolean[1 << 16];
    private BitPacket[] packets = new BitPacket[1024];
    private int packets_count = 0;

    public MCSDWebbingCodec() {
        for (int i = 0; i < this.packets.length; i++) {
            this.packets[i] = new BitPacket();
        }
    }

    public void reset(MCSDWebbingCodec codec) {
        this.written_cells = codec.written_cells;
        this.last_x = codec.last_x;
        this.last_y = codec.last_y;
        this.last_dx = codec.last_dx;
        this.last_dy = codec.last_dy;
        System.arraycopy(codec.strands, 0, this.strands, 0, strands.length);

        this.packets_count = 0;
        for (BitPacket packet2 : codec.packets) {
            BitPacket packet = this.addPacket();
            packet.bits = packet2.bits;
            packet.data = packet2.data;
        }
        this.packets_count = codec.packets_count;
    }

    public void reset(boolean[] cells, boolean copyCells) {
        if (copyCells) {
            System.arraycopy(cells, 0, this.strands, 0, cells.length);
        } else {
            this.strands = cells;
        }
        this.written_cells = 0;
        this.last_x = -1000;
        this.last_y = -1000;
        this.last_dx = 1;
        this.last_dy = 1;
        this.packets_count = 0;
    }

    public boolean writeNext(int x, int y) {
        if (x < 0 || y < 0 || x >= 256 || y >= 256) {
            return false;
        }
        int index = x | (y << 8);
        if (!strands[index]) {
            return false;
        }

        strands[index] = false;
        written_cells++;

        int dx = x - last_x;
        int dy = y - last_y;
        last_x = x;
        last_y = y;
        if (dx == 0 && dy == 0) {
            return false;
        }

        if (dx > 1 || dx < -1 || dy > 1 || dy < -1) {
            // Reset coordinates when difference is too big
            BitPacket code = addPacket();
            code.write(0b11, 2);
            code.write(0b0, 1);
            code.write(0b0, 1);
            code.write(x, 8);
            code.write(y, 8);
        } else {
            // Update directions when changed
            if ((dx > 0 && last_dx < 0) || (dx < 0 && last_dx > 0)) {
                BitPacket code = addPacket();
                code.write(0b11, 2);
                code.write(0b1, 1);
                code.write(dx > 0 ? 0b01 : 0b00, 2);
                last_dx = dx;
            }
            if ((dy > 0 && last_dy < 0) || (dy < 0 && last_dy > 0)) {
                BitPacket code = addPacket();
                code.write(0b11, 2);
                code.write(0b1, 1);
                code.write(dy > 0 ? 0b11 : 0b10, 2);
                last_dy = dy;
            }

            // Push pixels
            BitPacket code = addPacket();
            if (dy == 0) {
                // DX
                code.write(0b00, 2);
            } else if (dx == 0) {
                // DY
                code.write(0b01, 2);
            } else {
                // DX AND DY
                code.write(0b10, 2);
            }
        }
        return true;
    }

    private BitPacket addPacket() {
        if (this.packets_count >= packets.length) {
            BitPacket[] new_packets = new BitPacket[packets.length * 2];
            System.arraycopy(this.packets, 0, new_packets, 0, this.packets.length);
            for (int i = this.packets.length; i < new_packets.length; i++) {
                new_packets[i] = new BitPacket();
            }
            this.packets = new_packets;
        }
        BitPacket result = this.packets[this.packets_count++];
        result.bits = 0;
        result.data = 0;
        return result;
    }

    public boolean readNext(BitInputStream stream) throws IOException {
        int op = stream.readBits(2);
        if (op == 0b11) {
            if (stream.readBits(1) == 1) {
                // Set DX/DY increment/decrement
                int sub = stream.readBits(2);
                if (sub == 0b00) {
                    last_dx = -1;
                } else if (sub == 0b01) {
                    last_dx = 1;
                } else if (sub == 0b10) {
                    last_dy = -1;
                } else if (sub == 0b11) {
                    last_dy = 1;
                }
            } else {
                // Command codes
                if (stream.readBits(1) == 1) {
                    // End of slice
                    return false;
                } else {
                    // Reset position
                    last_x = stream.readBits(8);
                    last_y = stream.readBits(8);
                    strands[last_x | (last_y << 8)] = true;
                }
            }
        } else {
            // Write next pixel
            if (op == 0b00) {
                last_x += last_dx;
            } else if (op == 0b01) {
                last_y += last_dy;
            } else if (op == 0b10) {
                last_x += last_dx;
                last_y += last_dy;
            } else if (op == -1) {
                // End of stream
                return false;
            }
            strands[last_x | (last_y << 8)] = true;
        }
        return true;
    }

    public boolean writeFrom(int x, int y) {
        if (!writeNext(x, y)) {
            return false;
        }
        while (true) {
            // These are preferred, because they go in the direction we are already going
            // No additional direction instructions are required then
            if (writeNext(x, y + last_dy)) {
                y += last_dy;
                continue;
            }
            if (writeNext(x + last_dx, y)) {
                x += last_dx;
                continue;
            }
            if (writeNext(x + last_dx, y + last_dy)) {
                x += last_dx;
                y += last_dy;
                continue;
            }
            
            // Change a single direction
            // A single direction change instruction will be fired
            if (writeNext(x - last_dx, y)) {
                x -= last_dx;
                continue;
            }
            if (writeNext(x, y - last_dy)) {
                y -= last_dy;
                continue;
            }
            if (writeNext(x - last_dx, y + last_dy)) {
                x -= last_dx;
                y += last_dy;
                continue;
            }
            if (writeNext(x + last_dx, y - last_dy)) {
                x += last_dx;
                y -= last_dy;
                continue;
            }

            // Change two directions
            // Two direction change instructions will be fired
            if (writeNext(x - last_dx, y - last_dy)) {
                x -= last_dx;
                y -= last_dy;
                continue;
            }

            break;
        }
        return true;
    }

    public int calculateWritten() {
        return this.written_cells;
    }

    public int calculateCompressedSize() {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        try {
            BitOutputStream s = new BitOutputStream(CommonUtil.setCompressionLevel(new GZIPOutputStream(bs), Deflater.BEST_COMPRESSION));
            for (int i = 0; i < this.packets_count; i++) {
                s.writePacket(this.packets[i]);
            }
            s.close();
            return bs.size();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public int calculateSize() {
        int total_size = 0;
        for (int i = 0; i < this.packets_count; i++) {
            total_size += this.packets[i].bits;
        }
        return total_size;
    }

    public double calculateCost() {
        return (double) calculateSize() / (double) calculateWritten();
    }

    public void writePackets(BitOutputStream stream) throws IOException {
        for (int i = 0; i < this.packets_count; i++) {
            stream.writePacket(this.packets[i]);
        }

        // Closing BitPacket tells the reader this is end of this slice
        BitPacket closingCode = new BitPacket();
        closingCode.write(0b11, 2);
        closingCode.write(0b0, 1);
        closingCode.write(0b1, 1);
        stream.writePacket(closingCode);
    }

    /**
     * Tries out many different orders of drawing the webbing information.
     * The more iterations, the better the results, the slower the process.
     * Using max_iterations higher than 100000 is generally not useful.
     * 
     * @param points to start encoding from
     * @param max_iterations maximum number of iterations until giving up
     */
    public void processBest(List<IntVector2> points, int max_iterations) {
        // Get all starting coordinates
        List<StartPoint> best_coords = new ArrayList<StartPoint>(points.size());
        for (IntVector2 point : points) {
            if (this.strands[point.x | (point.z << 8)]) {
                StartPoint startPoint = new StartPoint();
                startPoint.x = point.x;
                startPoint.y = point.z;
                startPoint.active = true;
                best_coords.add(startPoint);
            }
        }
        if (best_coords.isEmpty()) {
            return; // nothing to do
        }

        // This one is a stored starting point
        MCSDWebbingCodec codec_start = new MCSDWebbingCodec();
        codec_start.reset(this);

        // Try many random iterations
        Random random = new Random();
        MCSDWebbingCodec tempCodec = new MCSDWebbingCodec();
        int start_size = 0;
        int start_count = 0;
        double start_cost = Double.MAX_VALUE;
        int best_size = 0;
        int best_count = 0;
        double best_cost = Double.MAX_VALUE;
        int iterations = 0;
        int index_a, index_b;
        while (++iterations < max_iterations) {
            // Create a new random pair of coordinates that will be swapped
            // If both were inactive last run, skip those coordinates
            // This saves us some cycles that have the same outcome
            do {
                index_a = random.nextInt(best_coords.size());
                index_b = random.nextInt(best_coords.size());
            } while (index_a == index_b || (!best_coords.get(index_a).active && !best_coords.get(index_b).active));

            // Perform another encoding run using a temporary codec
            Collections.swap(best_coords, index_a, index_b);
            tempCodec.reset(codec_start);
            for (StartPoint startPoint : best_coords) {
                startPoint.active = tempCodec.writeFrom(startPoint.x, startPoint.y);
            }

            // Check if the cost per pixel has decreased
            // When that happens, continue with the new coordinates
            // When that does not happen, undo the swap and try again
            double cost = tempCodec.calculateCost();
            if (cost < best_cost) {
                this.reset(tempCodec);
                iterations = 0;
                best_cost = cost;
                best_count = tempCodec.calculateWritten();
                best_size = tempCodec.calculateSize();
                if (start_cost == Double.MAX_VALUE) {
                    start_cost = best_cost;
                    start_count = best_count;
                    start_size = best_size;
                }
            } else {
                Collections.swap(best_coords, index_a, index_b);
            }
        }

        Logging.LOGGER_MAPDISPLAY.info(
                "[" + MathUtil.round(start_cost, 3) + " c/p, " + 
                      start_count + " pixels, " +
                      start_size + " bits] => " +
                "[" + MathUtil.round(best_cost, 3) + " c/p, " +
                      best_count + " pixels, " +
                      best_size + " bits] " +
                "[compressed=" + this.calculateCompressedSize() + "]");
    }

    private static class StartPoint {
        int x, y;
        boolean active;
    }

    public static final Pattern EDGE_PATTERN = new Pattern(new int[][] {
        { 0, 0, 0,
          0, 1, 0,
          0, 1, 0 },
        { 0, 0, 0,
          0, 1, 0,
          0, 1, 1 },
        { 0, 0, 0,
          0, 1, 0,
          0, 0, 1 },
        { 0, 0, 0,
          0, 1, 0,
          0, 0, 0 }
    });

    public static final Pattern LINE_PATTERN = new Pattern(new int[][] {
        { 0, 1, 0,
          0, 1, 0,
          0, 1, 0 },
        { 0, 1, 0,
          0, 1, 0,
          0, 1, 1 },
        { 1, 1, 0,
          0, 1, 0,
          0, 1, 1 },
        { 0, 1, 0,
          0, 1, 0,
          0, 0, 1 },
        { 0, 1, 0,
          0, 1, 1,
          0, 0, 1 },
        { 1, 1, 0,
          0, 1, 1,
          0, 0, 1 },
        { 1, 0, 0,
          0, 1, 0,
          0, 0, 1 }
    });

    /**
     * Utility class to preselect web strand patterns in the data
     */
    public static class Pattern {
        private final boolean[][] patterns;

        public Pattern(int[][] patternData) {
            final int num_transforms = (1 << 3);
            this.patterns = new boolean[patternData.length * num_transforms][3*3];
            int patternIndex = 0;
            for (int[] intPattern : patternData) {
                // Convert to boolean[]
                boolean[] boolPattern = new boolean[intPattern.length];
                for (int i = 0; i < intPattern.length; i++) {
                    boolPattern[i] = (intPattern[i] > 0);
                }

                // Create patterns
                for (int mode = 0; mode < num_transforms; mode++) {
                    boolean[] pattern = this.patterns[patternIndex++];

                    int sx, sy;
                    int idx = 0;
                    for (int y = 0; y < 3; y++) {
                        for (int x = 0; x < 3; x++) {
                            sx = x;
                            sy = y;
                            if ((mode & 0x1) == 0x1) {
                                // Swap x and y
                                sx = y;
                                sy = x;
                            }
                            if ((mode & 0x2) == 0x2) {
                                // Flip horizontal
                                sx = 2 - sx;
                            }
                            if ((mode & 0x4) == 0x4) {
                                // Flip vertical
                                sy = 2 - sy;
                            }
                            pattern[idx++] = boolPattern[sy * 3 + sx];
                        }
                    }
                }
            }
        }

        /**
         * Checks if this Pattern matches the data specified
         * 
         * @param data (length = 9)
         * @return True if the data matches this pattern
         */
        public boolean matches(boolean[] data) {
            for (boolean[] pattern : this.patterns) {
                if (Arrays.equals(data, pattern)) {
                    return true;
                }
            }
            return false;
        }
    }
}
