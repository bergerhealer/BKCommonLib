package com.bergerkiller.bukkit.common.map;

/**
 * A base implementation canvas for performing drawing operations on.
 * Implementations of this canvas should implement the following methods:
 * <ul>
 * <li>{@link #getWidth()} - resolution width</li>
 * <li>{@link #getHeight()} - resolution height</li>
 * <li>{@link #getBuffer()} - read-only buffer for all canvas contents</li>
 * <li>{@link #readPixel(int, int)} - read a single pixel</li>
 * <li>{@link #readPixels(int, int, int, int, byte[])} - read an area of pixels</li>
 * <li>{@link #writePixel(int, int, byte)} - write a single pixel</li>
 * <li>{@link #writePixels(int, int, int, int, byte[])} - write an area of pixels</li>
 * <li>{@link #fillPixels(int, int, int, int, byte)} - fill an area of pixels</li>
 * </ul>
 */
public abstract class MapCanvas {

    /**
     * Gets the width of this canvas
     * 
     * @return canvas width
     */
    public abstract int getWidth();

    /**
     * Gets the height of this canvas
     * 
     * @return canvas height
     */
    public abstract int getHeight();

    /**
     * Gets the backing color data buffer for all the data in this canvas.
     * This buffer is not guaranteed to be immutable. For an immutable buffer,
     * use {@link #readPixels()} instead.
     * This data is formatted in such a way that:
     * <ul>
     * <li>The first color in the array is pixel (0, 0)</li>
     * <li>Satisfies condition <b>index = x + y * width</b></li>
     * <li>The size of the buffer equals (width * height)</li>
     * </ul>
     * 
     * @return raw color byte buffer
     */
    public abstract byte[] getBuffer();

    /**
     * Reads the pixel color value of a single pixel
     * 
     * @param x - coordinate of the pixel
     * @param y - coordinate of the pixel
     * @return color at this pixel, 0 for transparent
     */
    public abstract byte readPixel(int x, int y);

    /**
     * Updates the pixel color of a single pixel
     * 
     * @param x - coordinate of the pixel
     * @param y - coordinate of the pixel
     * @param color to draw
     */
    public abstract void writePixel(int x, int y, byte color);

    /**
     * Reads the raw pixel color data for a particular area in this canvas.
     * This is guaranteed to be a copy of the actual data.
     * This data is formatted in such a way that:
     * <ul>
     * <li>The first color in the array is pixel (0, 0)</li>
     * <li>Satisfies condition <b>index = x + y * width</b></li>
     * <li>The size of the buffer equals (width * height)</li>
     * </ul>
     * 
     * @param x - coordinate of the top-left corner of the area
     * @param y - coordinate of the top-left corner of the area
     * @param w - width of the area
     * @param h - height of the area
     * @param dst_buffer - buffer to write the pixels to
     * @return dst_buffer input parameter
     */
    public byte[] readPixels(int x, int y, int w, int h, byte[] dst_buffer) {
        if (x == 0 && y == 0 && w == this.getWidth() && h == this.getHeight()) {
            return getBuffer().clone();
        }
        byte[] src_buffer = this.getBuffer();
        int src_w = this.getWidth();
        int src_h = this.getHeight();
        int src_y = y;
        for (int dst_y = 0; dst_y < h && src_y < src_h; dst_y++) {
            if (src_y >= 0) {
                int src_offset = (src_y * src_w);
                int dst_offset = (dst_y * w);
                int src_x = x;
                for (int dst_x = 0; dst_x < w && src_x < src_w; dst_x++) {
                    if (src_x >= 0) {
                        dst_buffer[dst_offset + dst_x] = src_buffer[src_offset + src_x];
                    }
                    src_x++;
                }
            }
            src_y++;
        }
        return dst_buffer;
    }

    /**
     * Writes raw pixel color data to a rectangular area in this canvas.
     * No color blending is performed and any original pixels are replaced.
     * This data must be formatted in such a way that:
     * <ul>
     * <li>The first color in the array is pixel (0, 0)</li>
     * <li>Satisfies condition <b>index = x + y * width</b></li>
     * <li>The size of the buffer is at least (width * height)</li>
     * </ul>
     * 
     * @param x - coordinate of the top-left corner of the area
     * @param y - coordinate of the top-left corner of the area
     * @param w - width of the area
     * @param h - height of the area
     * @param colorData to write
     * @param blendMode to use
     */
    public MapCanvas writePixels(int x, int y, int w, int h, byte[] colorData) {
        // Note: this function should be implemented to enhance performance, if possible
        int w_end = (x + w);
        int px = x;
        int py = y;
        for (int i = 0; i < colorData.length; i++) {
            writePixel(px, py, colorData[i]);
            if (++px >= w_end) {
                px = x;
                py++;
            }
        }
        return this;
    }

    /**
     * Fills a rectangular area in this canvas with a single color
     * 
     * @param x - coordinate of the top-left corner of the area
     * @param y - coordinate of the top-left corner of the area
     * @param w - width of the area
     * @param h - height of the area
     * @param color to fill
     * @return this canvas
     */
    public MapCanvas fillPixels(int x, int y, int w, int h, byte color) {
        // Note: this function should be implemented to enhance performance, if possible
        for (int dx = 0; dx < w; dx++) {
            for (int dy = 0; dy < h; dy++) {
                writePixel(x + dx, y + dy, color);
            }
        }
        return this;
    }

    /* ============================================================================ */
    /* ================================= Map Canvas================================ */
    /* ============================================================================ */

    /**
     * Writes raw pixel color data to a rectangular area in this canvas.
     * A blend mode can be specified to perform pixel color blending.
     * This data must be formatted in such a way that:
     * <ul>
     * <li>The first color in the array is pixel (0, 0)</li>
     * <li>Satisfies condition <b>index = x + y * width</b></li>
     * <li>The size of the buffer is at least (width * height)</li>
     * </ul>
     * 
     * @param x - coordinate of the top-left corner of the area
     * @param y - coordinate of the top-left corner of the area
     * @param w - width of the area
     * @param h - height of the area
     * @param colorData to write
     * @param blendMode to use
     */
    public final MapCanvas writePixels(int x, int y, int w, int h, byte[] colorData, MapBlendMode blendMode) {
        if (blendMode == MapBlendMode.NONE) {
            return this.writePixels(x, y, w, h, colorData);
        } else {
            byte[] pixels = this.readPixels(x, y, w, h);
            blendMode.process(colorData, pixels);
            return this.writePixels(x, y, w, h, pixels);
        }
    }

    /**
     * Writes raw pixel color data to this entire canvas.
     * A blend mode can be specified to perform pixel color blending.
     * This data must be formatted in such a way that:
     * <ul>
     * <li>The first color in the array is pixel (0, 0)</li>
     * <li>Satisfies condition <b>index = x + y * width</b></li>
     * <li>The size of the buffer is at least (width * height)</li>
     * </ul>
     * 
     * @param colorData to write
     * @param blendMode to use
     */
    public final MapCanvas writePixels(byte[] colorData, MapBlendMode blendMode) {
        return this.writePixels(0, 0, getWidth(), getHeight(), colorData, blendMode);
    }

    /**
     * Writes raw pixel color data to this entire canvas.
     * No color blending is performed and any original pixels are replaced.
     * This data must be formatted in such a way that:
     * <ul>
     * <li>The first color in the array is pixel (0, 0)</li>
     * <li>Satisfies condition <b>index = x + y * width</b></li>
     * <li>The size of the buffer is at least (width * height)</li>
     * </ul>
     * 
     * @param colorData to write
     */
    public final MapCanvas writePixels(byte[] colorData) {
        return this.writePixels(0, 0, getWidth(), getHeight(), colorData, MapBlendMode.NONE);
    }

    /**
     * Reads all the pixel color data in this canvas.
     * This is guaranteed to be a copy of the actual data.
     * This data is formatted in such a way that:
     * <ul>
     * <li>The first color in the array is pixel (0, 0)</li>
     * <li>Satisfies condition <b>index = x + y * width</b></li>
     * <li>The size of the buffer equals (width * height)</li>
     * </ul>
     * 
     * @return byte buffer containing the pixel color data in this canvas
     */
    public final byte[] readPixels() {
        int w = this.getWidth();
        int h = this.getHeight();
        return this.readPixels(0, 0, w, h, new byte[w * h]);
    }

    /**
     * Reads the raw pixel color data for a particular area in this canvas.
     * This is guaranteed to be a copy of the actual data.
     * This data is formatted in such a way that:
     * <ul>
     * <li>The first color in the array is pixel (0, 0)</li>
     * <li>Satisfies condition <b>index = x + y * width</b></li>
     * <li>The size of the buffer equals (width * height)</li>
     * </ul>
     * 
     * @param x - coordinate of the top-left corner of the area
     * @param y - coordinate of the top-left corner of the area
     * @param w - width of the area
     * @param h - height of the area
     * @return byte buffer containing the pixel color data in the area
     */
    public final byte[] readPixels(int x, int y, int w, int h) {
        return this.readPixels(x, y, w, h, new byte[w * h]);
    }

    /**
     * Draws the contents of another canvas or texture onto this canvas.
     * No color blending is performed and any original pixels are replaced.
     * 
     * @param canvas with the pixel data to draw
     * @param x - position of the top-left corner of the sprite
     * @param y - position of the top-left corner of the sprite
     * @return this canvas
     */
    public final MapCanvas draw(MapCanvas canvas, int x, int y) {
        return writePixels(x, y, canvas.getWidth(), canvas.getHeight(), canvas.getBuffer());
    }

    /**
     * Draws the contents of another canvas or texture onto this canvas.
     * A blend mode can be specified to perform pixel color blending.
     * 
     * @param canvas with the pixel data to draw
     * @param x - position of the top-left corner of the sprite
     * @param y - position of the top-left corner of the sprite
     * @param blendMode to use
     * @return this canvas
     */
    public final MapCanvas draw(MapCanvas canvas, int x, int y, MapBlendMode blendMode) {
        return writePixels(x, y, canvas.getWidth(), canvas.getHeight(), canvas.getBuffer(), blendMode);
    }

    /**
     * Converts all the pixel color data to remap to a new color format.
     * The remappingColors array should at least have the size of the lowest color pixel value.
     * 
     * @param remappingColors array containing the color remapping table
     * @return this canvas
     */
    public final MapCanvas remap(byte[] remappingColors) {
        byte[] pixels = this.readPixels();
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = remappingColors[(int) pixels[i]];
        }
        return this.writePixels(pixels);
    }

    /**
     * Re-draws the contents of another canvas, clearing the old rectangular area.
     * When sprites gradually move, this method can be used for optimized updates.
     * This function always draws, even when the old and new positions are the same.
     * If that is not desired, do a check up front.
     * 
     * @param canvas with the pixel data to draw
     * @param old_x - old x-coordinate of the sprite
     * @param old_y - old y-coordinate of the sprite
     * @param new_x - new x-coordinate of the sprite
     * @param new_y - new y-coordinate of the sprite
     * @return this canvas
     */
    public final MapCanvas drawMove(MapCanvas canvas, int old_x, int old_y, int new_x, int new_y) {
        this.clearRectangle(old_x, old_y, canvas.getWidth(), canvas.getHeight());
        return this.draw(canvas, new_x, new_y);
    }

    /**
     * Clears a rectangular area with transparent color. Layers below this rectangle
     * will show through.
     * 
     * @param x - coordinate of the top-left corner of the rectangle
     * @param y - coordinate of the top-left corner of the rectangle
     * @param w - width of the rectangle
     * @param h - width of the rectangle
     * @return this canvas
     */
    public final MapCanvas clearRectangle(int x, int y, int w, int h) {
        return this.fillPixels(x, y, w, h, (byte) 0);
    }

    /**
     * Fills a rectangular area with a single color.
     * No color blending is performed and any original pixels are replaced.
     * 
     * @param x - coordinate of the top-left corner of the rectangle
     * @param y - coordinate of the top-left corner of the rectangle
     * @param w - width of the rectangle
     * @param h - height of the rectangle
     * @param color to draw
     * @return this canvas
     */
    public final MapCanvas fillRectangle(int x, int y, int w, int h, byte color) {
        return this.fillPixels(x, y, w, h, color);
    }

    /**
     * Fills a rectangular area with a single color
     * A blend mode can be specified to perform pixel color blending.
     * 
     * @param x - coordinate of the top-left corner of the rectangle
     * @param y - coordinate of the top-left corner of the rectangle
     * @param w - width of the rectangle
     * @param h - height of the rectangle
     * @param color to draw
     * @param blendMode to use
     * @return this canvas
     */
    public final MapCanvas fillRectangle(int x, int y, int w, int h, byte color, MapBlendMode blendMode) {
        if (blendMode == MapBlendMode.NONE) {
            return this.fillPixels(x, y, w, h, color);
        } else {
            byte[] pixels = this.readPixels(x, y, w, h);
            blendMode.process(color, pixels);
            return this.writePixels(x, y, w, h, pixels);
        }
    }

    /**
     * Fills this entire canvas with clear transparency.
     * This means this entire canvas contains no drawable data from this point.
     * 
     * @return this canvas
     */
    public final MapCanvas clear() {
        return this.fillPixels(0, 0, getWidth(), getHeight(), (byte) 0);
    }

    /**
     * Fills this entire canvas area with a single color.
     * No color blending is performed and any original pixels are replaced.
     * 
     * @param color to fill
     * @return this canvas
     * @return this canvas
     */
    public final MapCanvas fill(byte color) {
        return this.fillPixels(0, 0, getWidth(), getHeight(), color);
    }

    /**
     * Fills this entire canvas area with a single color.
     * A blend mode can be specified to perform pixel color blending.
     * 
     * @param color to fill
     * @param blendMode to use
     * @return this canvas
     */
    public final MapCanvas fill(byte color, MapBlendMode blendMode) {
        return this.fillRectangle(0, 0, getWidth(), getHeight(), color, blendMode);
    }

    /**
     * Obtains a view into this canvas for a viewport area
     * 
     * @param x - coordinate of the top-left corner of the viewport
     * @param y - coordinate of the top-left corner of the viewport
     * @param w - width of the viewport
     * @param h - height of the viewport
     * @return view
     */
    public final MapCanvas getView(int x, int y, int w, int h) {
        return new View(this, x, y, w, h);
    }

    /**
     * Obtains a view into this canvas for a viewport area with a certain pixel offset
     * 
     * @param offset_x - offset from the top-left corner
     * @param offset_y - offset from the top-left corner
     * @return view
     */
    public final MapCanvas getView(int offset_x, int offset_y) {
        return new View(this, offset_x, offset_y, getWidth() - offset_x, getHeight() - offset_y);
    }

    /**
     * Creates a new MapTexture with a copy of the pixel data on this canvas.
     * Changes drawn on the texture do not reflect back on this canvas.
     * 
     * @return map texture copy
     */
    @Override
    public final MapTexture clone() {
        return MapTexture.fromRawData(this.getWidth(), this.getHeight(), this.readPixels());
    }

    /**
     * A viewport into the portion of another canvas
     */
    private static final class View extends MapCanvas {
        private final MapCanvas parent;
        private final int x, y, w, h;

        public View(MapCanvas parent, int x, int y, int w, int h) {
            this.parent = parent;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        @Override
        public int getWidth() {
            return this.w;
        }

        @Override
        public int getHeight() {
            return this.h;
        }

        @Override
        public byte[] getBuffer() {
            return this.parent.readPixels(this.x, this.y, this.w, this.h);
        }

        @Override
        public byte[] readPixels(int x, int y, int w, int h, byte[] dst_buffer) {
            if (x >= 0 && y >= 0 && (x + w) <= this.w && (y + h) <= this.h) {
                return this.parent.readPixels(x + this.x, y + this.y, w, h, dst_buffer);
            } else {
                return super.readPixels(x, y, w, h, dst_buffer);
            }
        }

        @Override
        public void writePixel(int x, int y, byte color) {
            if (x >= 0 && y >= 0 && x < this.w && y <= this.h) {
                this.parent.writePixel(x + this.x, y + this.y, color);
            }
        }

        @Override
        public byte readPixel(int x, int y) {
            if (x >= 0 && y >= 0 && x < this.w && y <= this.h) {
                return this.parent.readPixel(x + this.x, y + this.y);
            }
            return (byte) 0;
        }

        @Override
        public MapCanvas writePixels(int x, int y, int w, int h, byte[] colorData) {
            if (x >= 0 && y >= 0 && (x + w) <= this.w && (y + h) <= this.h) {
                return this.parent.writePixels(x + this.x, y + this.y, w, h, colorData);
            } else {
                return super.writePixels(x, y, w, h, colorData);
            }
        }

        @Override
        public MapCanvas fillPixels(int x, int y, int w, int h, byte color) {
            if (x >= 0 && y >= 0 && (x + w) <= this.w && (y + h) <= this.h) {
                return this.parent.fillPixels(x + this.x, y + this.y, w, h, color);
            } else {
                return super.fillPixels(x, y, w, h, color);
            }
        }

    }

}
