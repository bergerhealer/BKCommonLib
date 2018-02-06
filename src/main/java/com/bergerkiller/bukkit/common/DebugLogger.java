package com.bergerkiller.bukkit.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.block.Block;
import org.bukkit.util.Vector;

/**
 * Enables logging debug data to console in an efficient and readable fashion.
 * Optionally logged lines can be cached until a precondition is met.
 * The logger will guarantee that two columns are kept at least 2 spaces apart.
 * 
 */
public class DebugLogger {
    private static final Map<String, DebugLogger> _loggers = new HashMap<String, DebugLogger>();
    private final String _name;
    private int[] _columnSpacing = new int[0];
    private final StringBuilder _buffer = new StringBuilder();
    private final List<String> _backBuffer = new ArrayList<String>();
    private boolean _backBufferActive = false;
    private int _col = 0;

    private DebugLogger(String name) {
        this._name = name;
        this._buffer.append(this._name);
    }

    /**
     * Resets the buffer and starts buffering messages
     * 
     * @return this logger
     */
    public DebugLogger bufferStart() {
        this._backBuffer.clear();
        this._backBufferActive = true;
        return this;
    }

    /**
     * Logs all messages buffered so far
     * 
     * @return this logger
     */
    public DebugLogger bufferDone() {
        this._backBufferActive = false;
        for (String message : this._backBuffer) {
            System.out.println(message);
        }
        this._backBuffer.clear();
        return this;
    }

    /**
     * Accumulates the world and x/y/z axis of a Block without actually logging it.
     * When all columns have been accumulated, call {@link #log()} to log the line.
     * 
     * @param block
     * @return this logger
     */
    public DebugLogger addBlock(Block block) {
        if (block == null) {
            return add("null", 0, 0, 0);
        } else {
            return add(block.getWorld().getName(),
                    Integer.toString(block.getX()),
                    Integer.toString(block.getY()),
                    Integer.toString(block.getZ()));
        }
    }

    /**
     * Accumulates the x/y/z axis of a 3D vector without actually logging it.
     * The precision at which the values are logged can be specified.
     * When all columns have been accumulated, call {@link #log()} to log the line.
     * 
     * @param decimals
     * @param vec
     * @return this logger
     */
    public DebugLogger addVec(int decimals, Vector vec) {
        return addNum(decimals, vec.getX(), vec.getY(), vec.getZ());
    }

    /**
     * Accumulates additional column double values without actually logging it.
     * The precision at which the values are logged can be specified.
     * When all columns have been accumulated, call {@link #log()} to log the line.
     * 
     * @param decimals
     * @param values
     * @return this logger
     */
    public DebugLogger addNum(int decimals, double... values) {
        double p = Math.pow(10, decimals);
        Object[] output = new Object[values.length];
        for (int i = 0; i < values.length; i++) {
            output[i] = Double.toString(Math.round(values[i] * p) / p);
        }
        return add(output);
    }

    /**
     * Accumulates additional column integer values without actually logging it.
     * When all columns have been accumulated, call {@link #log()} to log the line.
     * 
     * @param values to accumulate to the log buffer
     * @return this logger
     */
    public DebugLogger add(int... values) {
        Object[] output = new Object[values.length];
        for (int i = 0; i < values.length; i++) {
            output[i] = Integer.toString(values[i]);
        }
        return add(output);
    }

    /**
     * Accumulates additional column values without actually logging it.
     * When all columns have been accumulated, call {@link #log()} to log the line.
     * 
     * @param values to accumulate to the log buffer
     * @return this logger
     */
    public DebugLogger add(Object... values) {
        this.cache(values.length);

        // Fill buffer with contents
        for (Object value : values) {
            // Read String value and refresh the spacing for the column
            String s = (value == null) ? "null" : value.toString();
            int spacing = this._columnSpacing[this._col];
            if (s.length() > spacing) {
                spacing = s.length();
                this._columnSpacing[this._col] = spacing;
            }

            // Append to buffer + pad with spaces
            this._buffer.append("  ");
            this._buffer.append(s);
            for (int j = s.length(); j < spacing; j++) {
                this._buffer.append(' ');
            }
            this._col++;
        }
        return this;
    }

    /**
     * Accumulates additional padding between columns without actually logging it.
     * 
     * @param n length of the padding (spaces)
     * @return this logger
     */
    public DebugLogger pad(int n) {
        this.cache(1);

        // Log a line with padding
        int spacing = this._columnSpacing[this._col];
        if (n > spacing) {
            spacing = n;
            this._columnSpacing[this._col] = spacing;
        }
        for (int i = 0; i < spacing; i++) {
            this._buffer.append(' ');
        }
        this._col++;
        return this;
    }

    /**
     * Logs all previously accumulated columns and the additional columns provided here.
     * 
     * @param values to log
     * @return this logger
     */
    public DebugLogger log(Object... values) {
        return this.add(values).log();
    }

    /**
     * Logs all previously accumulated columns
     * 
     * @return this logger
     */
    public DebugLogger log() {
        if (this._backBufferActive) {
            this._backBuffer.add(this._buffer.toString());
        } else {
            System.out.println(this._buffer.toString());
        }
        this._buffer.setLength(this._name.length());
        this._col = 0;
        return this;
    }

    // Resize column spacing information buffer
    private void cache(int n) {
        if ((this._col + n) > this._columnSpacing.length) {
            this._columnSpacing = Arrays.copyOf(this._columnSpacing, (this._col + n));
        }
    }
    
    /**
     * Retrieves or creates a new debug logger by name
     * 
     * @param name
     * @return logger
     */
    public static synchronized DebugLogger get(String name) {
        DebugLogger logger = _loggers.get(name);
        if (logger == null) {
            logger = new DebugLogger(name);
            _loggers.put(name, logger);
        }
        return logger;
    }
}
