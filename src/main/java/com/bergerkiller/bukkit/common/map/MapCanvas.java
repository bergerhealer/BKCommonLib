package com.bergerkiller.bukkit.common.map;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

import com.bergerkiller.bukkit.common.collections.CharacterIterable;
import com.bergerkiller.bukkit.common.map.util.Matrix3f;
import com.bergerkiller.bukkit.common.map.util.Matrix4f;
import com.bergerkiller.bukkit.common.map.util.Quad;
import com.bergerkiller.bukkit.common.map.util.Vector2f;
import com.bergerkiller.bukkit.common.map.util.Vector3f;

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
 * <li>{@link #writePixelsFill(int, int, int, int, byte)} - fill an area of pixels</li>
 * </ul>
 */
public abstract class MapCanvas {
    private int fontSpacing = 0;
    private MapFont.Alignment fontAlignment = MapFont.Alignment.LEFT;
    private MapBlendMode blendMode = MapBlendMode.NONE;

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
     * Fills a rectangular area in this canvas with a single color.
     * No color blending is performed and any original pixels are replaced.
     * 
     * @param x - coordinate of the top-left corner of the area
     * @param y - coordinate of the top-left corner of the area
     * @param w - width of the area
     * @param h - height of the area
     * @param color to fill
     * @return this canvas
     */
    public MapCanvas writePixelsFill(int x, int y, int w, int h, byte color) {
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
     * If a blend mode is set for this canvas, it is used to perform pixel color blending.
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
     */
    public final MapCanvas drawRawData(int x, int y, int w, int h, byte[] colorData) {
        return this.drawRawData(x, y, w, h, colorData, (byte) 0);
    }

    /**
     * Writes raw pixel color data to a rectangular area in this canvas.
     * If a blend mode is set for this canvas, it is used to perform pixel color blending.
     * A color factor can be specified. All the color data is multiplied with that factor.
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
     * @param colorFactor to apply, 0 for no factor
     */
    public final MapCanvas drawRawData(int x, int y, int w, int h, byte[] colorData, byte colorFactor) {
        if (colorFactor != 0) {
            colorData = colorData.clone();
            MapBlendMode.MULTIPLY.process(colorFactor, colorData);
        }
        if (this.blendMode == MapBlendMode.NONE) {
            return this.writePixels(x, y, w, h, colorData);
        } else {
            byte[] pixels = this.readPixels(x, y, w, h);
            this.blendMode.process(colorData, pixels);
            return this.writePixels(x, y, w, h, pixels);
        }
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
    public final MapCanvas drawCopy(MapCanvas canvas, int x, int y) {
        return writePixels(x, y, canvas.getWidth(), canvas.getHeight(), canvas.getBuffer());
    }

    /**
     * Draws the contents of another canvas or texture onto this canvas.
     * If a blend mode is set for this canvas, it is used to perform pixel color blending.
     * 
     * @param canvas with the pixel data to draw
     * @param x - position of the top-left corner of the sprite
     * @param y - position of the top-left corner of the sprite
     * @return this canvas
     */
    public final MapCanvas draw(MapCanvas canvas, int x, int y) {
        return this.drawRawData(x, y, canvas.getWidth(), canvas.getHeight(), canvas.getBuffer());
    }

    /**
     * Draws the contents of another canvas or texture onto this canvas.
     * If a blend mode is set for this canvas, it is used to perform pixel color blending.
     * A color factor can be specified. All color data will be pre-multiplied with that factor.
     * 
     * @param canvas with the pixel data to draw
     * @param x - position of the top-left corner of the sprite
     * @param y - position of the top-left corner of the sprite
     * @param colorFactor to use
     * @return this canvas
     */
    public final MapCanvas draw(MapCanvas canvas, int x, int y, byte colorFactor) {
        return this.drawRawData(x, y, canvas.getWidth(), canvas.getHeight(), canvas.getBuffer(), colorFactor);
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
        return this.writePixels(0, 0, this.getWidth(), this.getHeight(), pixels);
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
        return this.writePixelsFill(x, y, w, h, (byte) 0);
    }

    /**
     * Fills a rectangular area with a single color
     * If a blend mode is set for this canvas, it is used to perform pixel color blending.
     * 
     * @param x - coordinate of the top-left corner of the rectangle
     * @param y - coordinate of the top-left corner of the rectangle
     * @param w - width of the rectangle
     * @param h - height of the rectangle
     * @param color to draw
     * @return this canvas
     */
    public final MapCanvas fillRectangle(int x, int y, int w, int h, byte color) {
        if (this.blendMode == MapBlendMode.NONE) {
            return this.writePixelsFill(x, y, w, h, color);
        } else {
            byte[] pixels = this.readPixels(x, y, w, h);
            this.blendMode.process(color, pixels);
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
        return this.writePixelsFill(0, 0, getWidth(), getHeight(), (byte) 0);
    }

    /**
     * Fills this entire canvas area with a single color.
     * If a blend mode is set for this canvas, it is used to perform pixel color blending.
     * 
     * @param color to fill
     * @return this canvas
     * @return this canvas
     */
    public final MapCanvas fill(byte color) {
        return this.fillRectangle(0, 0, getWidth(), getHeight(), color);
    }

    /**
     * Sets the amount of pixels between each drawn character of a font.
     * By default a spacing of 0 is used. Text fonts will have a default spacing set.
     * Negative values are allowed to make spacing smaller.
     * 
     * @param spacing to use, 0 for none
     * @return this canvas
     */
    public MapCanvas setSpacing(int spacing) {
        this.fontSpacing = spacing;
        return this;
    }

    /**
     * Sets the alignment at which font is drawn on this canvas.
     * By default it is set to LEFT.
     * 
     * <ul>
     * <li>LEFT - Draws from left to right. x/y specify left/middle</li>
     * <li>RIGHT - Draws from right to left. x/y specify right/middle</li>
     * <li>MIDDLE - Draws the contents centered. x/y specify middle/middle</li>
     * </ul>
     * 
     * @param alignment to set
     * @return this canvas
     */
    public MapCanvas setAlignment(MapFont.Alignment alignment) {
        this.fontAlignment = alignment;
        return this;
    }

    /**
     * Sets the color blending mode to use, applied when drawing contents on this canvas.
     * <ul>
     * <li>NONE - overwrites pixels already there, no transparency</li>
     * <li>OVERLAY - overwrites pixels with opaque pixels, transparent pixels do nothing</li>
     * <li>AVERAGE - takes the color average of the current pixels and the pixels drawn</li>
     * <li>ADD - adds the color values of the pixels drawn to the current pixels</li>
     * <li>SUBTRACT - subtracts the color values of the pixels drawn from the current pixels</li>
     * <li>MULTIPLY - multiplies the color values of the pixels drawn with the current pixels</li>
     * </ul>
     * @param blendMode
     * @return this canvas
     */
    public MapCanvas setBlendMode(MapBlendMode blendMode) {
        this.blendMode = blendMode;
        return this;
    }

    /**
     * Gets the color blending mode currently in use by this canvas.
     * See also: {@link #setBlendMode(MapBlendMode)}
     * 
     * @return color blending mode
     */
    public MapBlendMode getBlendMode() {
        return this.blendMode;
    }

    /**
     * Draws a font in its natural colors. If this is a binary font, it will be drawn in white.
     * 
     * @param font to draw
     * @param x - coordinate of the top-left corner of the first character drawn
     * @param y - coordinate of the top-left corner of the first character drawn
     * @param characters to draw
     * @return this canvas
     */
    @SafeVarargs
    public final <T> MapCanvas draw(MapFont<T> font, int x, int y, T... characters) {
        return this.draw(font, x, y, (byte) 0, Arrays.asList(characters));
    }

    /**
     * Draws a text-based Character font using a solid color. If the font is a binary font, the color or
     * transparent is drawn. For multicolor fonts, the color will be used as a multiplier.
     * 
     * @param font to draw
     * @param x - coordinate of the top-left corner of the first character drawn
     * @param y - coordinate of the top-left corner of the first character drawn
     * @param color - color text
     * @param characters to draw
     * @return this canvas
     */
    public final MapCanvas draw(MapFont<Character> font, int x, int y, byte color, CharSequence characters) {
        return this.draw(font, x, y, color, new CharacterIterable(characters));
    }

    /**
     * Draws a series of character sprites using a font and color.
     * A transparent color will result in no color being applied to the font.
     * For text fonts, this will result in white text. For colorful fonts, the colors will be preserved.
     * 
     * @param font to draw
     * @param x - coordinate to start drawing
     * @param y - coordinate to start drawing
     * @param color - color to use when drawing the font
     * @param characters to draw
     * @return this canvas
     */
    public final <T> MapCanvas draw(MapFont<T> font, int x, int y, byte color, Iterable<T> characters) {
        if (fontAlignment == MapFont.Alignment.LEFT) {
            // Left-to-right is easy as it is the natural ordering of the iterable
            for (T character : characters) {
                MapTexture sprite = font.getSprite(character);
                this.draw(sprite, x, y, color);
                x += sprite.getWidth() + fontSpacing;
            }
            return this;
        }

        // Other modes require knowing all the textures to draw up-front
        ArrayList<MapTexture> sprites = new ArrayList<MapTexture>();
        int total_width = 0;
        for (T character : characters) {
            MapTexture sprite = font.getSprite(character);
            sprites.add(sprite);
            total_width += sprite.getWidth();
        }
        if (sprites.isEmpty()) {
            return this; // nothing to draw
        }
        total_width += fontSpacing * (sprites.size() - 1);

        if (fontAlignment == MapFont.Alignment.RIGHT) {
            x -= total_width;
        } else if (fontAlignment == MapFont.Alignment.MIDDLE) {
            x -= total_width / 2;
        }
        for (MapTexture sprite : sprites) {
            this.draw(sprite, x, y, color);
            x += sprite.getWidth() + fontSpacing;
        }
        return this;
    }

    /**
     * Draws a pseudo-3D quad onto this canvas, using the 4 2D coordinates of the quad points
     * to define the projection transformation that is applied.
     * 
     * @param canvas to draw onto this canvas
     * @param quad to draw
     * @return view
     */
    public final MapCanvas drawQuad(MapCanvas canvas, Quad quad) {
        return this.drawQuad(canvas,
                quad.p0.toVector2f(),
                quad.p1.toVector2f(),
                quad.p2.toVector2f(),
                quad.p3.toVector2f());
    }

    /**
     * Draws a pseudo-3D quad onto this canvas, using the 4 2D coordinates of the quad points
     * to define the projection transformation that is applied.
     * 
     * @param canvas to draw onto this canvas
     * @param p0 the top-left first point of the quad
     * @param p1 the top-right second point of the quad
     * @param p2 the bottom-right third point of the quad
     * @param p3 the bottom-left fourth point of the quad
     * @return view
     */
    public final MapCanvas drawQuad(MapCanvas canvas, Vector2f p0, Vector2f p1, Vector2f p2, Vector2f p3) {
        Vector2f ip0 = new Vector2f(0, 0);
        Vector2f ip1 = new Vector2f(0, canvas.getHeight());
        Vector2f ip2 = new Vector2f(canvas.getWidth(), canvas.getHeight());
        Vector2f ip3 = new Vector2f(canvas.getWidth(), 0);

        Matrix3f m = Matrix3f.computeProjectionMatrix(
            new Vector2f[] {  p0,  p1,  p2,  p3 },
            new Vector2f[] { ip0, ip1, ip2, ip3 });
        
        return drawQuad(canvas, m);
    }

    /**
     * Draws a pseudo-3D quad onto this canvas, using the projection matrix to define the
     * projection transformation that is applied.
     * 
     * @param canvas to draw onto this canvas
     * @param projectionMatrix to use for the transformation
     * @return view
     */
    public final MapCanvas drawQuad(MapCanvas canvas, Matrix3f projectionMatrix) {
        Matrix3f mInv = new Matrix3f(projectionMatrix);
        mInv.invert();
        Vector2f p = new Vector2f();
        for (int y = 0; y < getHeight(); y++)
        {
            for (int x = 0; x < getWidth(); x++)
            {
                p.x = x;
                p.y = y;
                mInv.transform(p);
                if (p.x >= 0.0f && p.y >= 0.0f && p.x <= (canvas.getWidth()) && p.y <= (canvas.getHeight())) {
                    byte color = canvas.readPixel((int)p.x, (int)p.y);
                    if (color != MapColorPalette.COLOR_TRANSPARENT) {
                        writePixel(x, y, color);
                    }
                }
            }
        }
        return this;
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
     * Converts all pixel contents of this Map Canvas to a standard RGB Java Buffered Image
     * 
     * @return image
     */
    public final BufferedImage toJavaImage() {
        BufferedImage result = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < this.getWidth(); x++) {
            for (int y = 0; y < this.getHeight(); y++) {
                result.setRGB(x, y, MapColorPalette.getRealColor(this.readPixel(x, y)).getRGB());
            }
        }
        return result;
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
        public MapCanvas writePixelsFill(int x, int y, int w, int h, byte color) {
            if (x >= 0 && y >= 0 && (x + w) <= this.w && (y + h) <= this.h) {
                return this.parent.writePixelsFill(x + this.x, y + this.y, w, h, color);
            } else {
                return super.writePixelsFill(x, y, w, h, color);
            }
        }

    }

}
