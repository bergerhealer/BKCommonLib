package com.bergerkiller.bukkit.common.map;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.bergerkiller.bukkit.common.collections.CharacterIterable;
import com.bergerkiller.bukkit.common.map.util.Matrix4f;
import com.bergerkiller.bukkit.common.map.util.Model;
import com.bergerkiller.bukkit.common.map.util.Quad;
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
    private MapBlendMode blendMode = MapBlendMode.OVERLAY;
    private short[] depthBuffer = null;
    private byte[] maskBuffer = null;
    private int mask_w, mask_h;
    private boolean maskRelative = false;
    private short currentDepthZ = 0;
    private boolean hasDepthHoles = false;
    private Matrix4f projMatrix = null;
    private Vector3f directionalLightVec = null;
    private float directionalLightFact = 0.0f;
    private float ambientLightFact = 1.0f;
    public static final int MAX_DEPTH = Short.MAX_VALUE;

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
     * Updates the pixel color of a single pixel. No color blending or
     * depth buffering logic is performed.
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
     * No depth buffering is performed, either.
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
     * No depth buffering is performed, either.
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
     * If a depth value is set for this canvas, depth buffering logic will be performed on the data.
     * A color factor can be specified; all the color data is multiplied with that factor.
     * When <i>null</i> data is specified, the area is filled with the colorFactor color instead.<br>
     * <br>
     * The colorData buffer must be formatted such that:
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
     * @param colorData to write, <i>null</i> to fill with the colorFactor color
     * @param colorFactor to apply, 0 for no factor
     */
    public final MapCanvas drawRawData(int x, int y, int w, int h, byte[] colorData, byte colorFactor) {
        // Shortcut when no special functions are used to fill an area quickly
        if (colorData == null && this.depthBuffer == null && this.blendMode == MapBlendMode.NONE && this.depthBuffer == null && this.maskBuffer == null) {
            return this.writePixelsFill(x, y, w, h, colorFactor);
        }

        if (colorData == null) {
            // No data, fill with the color
            //TODO: Make this more efficient without a new array allocation!
            colorData = new byte[w * h];
            Arrays.fill(colorData, colorFactor);
        } else {
            // Apply color factor to a copy of the data
            if (colorFactor != 0) {
                colorData = colorData.clone();
                MapBlendMode.MULTIPLY.process(colorFactor, colorData);
            }
        }

        if (this.depthBuffer == null) {
            // Simple logic: no depth buffering is required
            if (this.blendMode == MapBlendMode.NONE && this.maskBuffer == null) {
                return this.writePixels(x, y, w, h, colorData);
            } else {
                byte[] pixels = this.readPixels(x, y, w, h);
                if (this.maskBuffer != null) {
                    // Create a temporary buffer to process the pixels
                    byte[] pixels_old = pixels.clone();
                    this.blendMode.process(colorData, pixels);

                    // Restore all pixels that are unmasked
                    int colorIndex = 0;
                    int maskIndex = 0;
                    int maskEndIndex;
                    for (int dy = 0; dy < h; dy++) {
                        // Restore lines outside of the mask range
                        if (dy >= this.mask_h) {
                            System.arraycopy(pixels_old, colorIndex, pixels, colorIndex, w);
                            colorIndex += w;
                            continue;
                        }

                        // Process lines
                        if (this.maskRelative) {
                            maskIndex = dy * this.mask_w;
                            maskEndIndex = maskIndex + this.mask_w;
                        } else {
                            maskIndex = (y + dy) * this.mask_w;
                            maskEndIndex = maskIndex + this.mask_w;
                            maskIndex += x;
                        }
                        for (int dx = 0; dx < w; dx++) {
                            // Restore end of lines out of range of mask
                            if (maskIndex >= maskEndIndex) {
                                System.arraycopy(pixels_old, colorIndex, pixels, colorIndex, w - dx);
                                colorIndex += w - dx;
                                break;
                            }

                            // If mask says no, restore the pixel
                            if (this.maskBuffer[maskIndex] == 0) {
                                pixels[colorIndex] = pixels_old[colorIndex];
                            }
                            maskIndex++;
                            colorIndex++;
                        }
                    }
                } else {
                    this.blendMode.process(colorData, pixels);
                }
                return this.writePixels(x, y, w, h, pixels);
            }
        } else {
            // Complex logic: only draw pixels that are visible according to the depth buffer
            // When pixels are turned from solid to transparent, they will be marked for deeper drawing (hasMoreDepth())
            //byte[] pixels = this.readPixels(x, y, w, h);

            int colorDataIdx = -1;
            for (int dy = 0; dy < h; dy++) {
                for (int dx = 0; dx < w; dx++) {
                    colorDataIdx++;

                    int px = x + dx;
                    int py = y + dy;

                    // Check if not masked out
                    if (this.maskBuffer != null) {
                        if (this.maskRelative) {
                            if (dx >= this.mask_w || dy >= this.mask_h || (this.maskBuffer[dy * this.mask_w + dx] == 0)) {
                                continue;
                            }
                        } else {
                            if (px >= this.mask_w || py >= this.mask_h || (this.maskBuffer[py * this.mask_w + px] == 0)) {
                                continue;
                            }
                        }
                    }

                    if (px < 0 || py < 0 || px >= this.getWidth() || py >= this.getHeight()) {
                        continue;
                    }
                    
                    int depthIndex = px + this.getWidth() * py;
                    short depth = this.depthBuffer[depthIndex];

                    // Only contents on the same or lower depth level are visible
                    if (this.currentDepthZ <= depth) {
                        byte color = colorData[colorDataIdx];
                        if (this.currentDepthZ == depth) {
                            // Re-drawing pixels on the current depth level
                            // If transparent, the contents behind will have to be re-drawn.
                            // If non-transparent, pixel is updated and depth does not change
                            if (color == MapColorPalette.COLOR_TRANSPARENT) {
                                this.writePixel(px, py, MapColorPalette.COLOR_TRANSPARENT);
                                this.depthBuffer[depthIndex] = MAX_DEPTH;
                                this.hasDepthHoles = true;
                            } else {
                                this.writePixel(px, py, color);
                            }
                        } else if (color != MapColorPalette.COLOR_TRANSPARENT) {
                            // Drawing in front of what is already there
                            // If transparent, nothing is drawn and depth is not updated
                            // If non-transparent, depth buffer is updated
                            this.writePixel(px, py, color);
                            this.depthBuffer[depthIndex] = this.currentDepthZ;
                        } else if (depth == MAX_DEPTH) {
                            // If a depth 'hole' existed at a transparent pixel, mark it as such
                            this.hasDepthHoles = true;
                        }
                    }
                }
            }

            return this;
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
     * Moves all pixel information, including depth buffer data, with an offset.
     * This enables quick movement of pixel data without re-drawing all the partss.
     * 
     * @param dx pixel offset
     * @param dy pixel offset
     * @return this canvas
     */
    public final MapCanvas movePixels(int dx, int dy) {
        byte[] oldPixels = this.getBuffer();
        short[] oldDepthBuffer = this.depthBuffer;

        byte[] newPixels = new byte[oldPixels.length];
        short[] newDepthBuffer = null;
        if (this.depthBuffer != null) {
            newDepthBuffer = new short[this.depthBuffer.length];
            Arrays.fill(newDepthBuffer, (short) MAX_DEPTH);
        }

        int colorIndex = 0;
        for (int y = 0; y < this.getHeight(); y++) {
            for (int x = 0; x < this.getWidth(); x++) {
                int sx = x + dx;
                int sy = y + dy;

                // Out of range
                if (sx < 0 || sy < 0 || sx >= this.getWidth() || sy >= this.getHeight()) {
                    colorIndex++;
                    continue;
                }

                // Copy it
                newPixels[sy * this.getWidth() + sx] = oldPixels[colorIndex];
                if (newDepthBuffer != null) {
                    newDepthBuffer[colorIndex] = oldDepthBuffer[colorIndex];
                }
                colorIndex++;
            }
        }

        // Apply it
        this.clearDepthBuffer();
        this.writePixels(0, 0, this.getWidth(), this.getHeight(), newPixels);
        this.depthBuffer = newDepthBuffer;

        return this;
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
        MapBlendMode oldMode = this.blendMode;
        this.blendMode = MapBlendMode.NONE;
        this.fillRectangle(x, y, w, h, MapColorPalette.COLOR_TRANSPARENT);
        this.blendMode = oldMode;
        return this;
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
        return this.drawRawData(x, y, w, h, null, color);
    }

    /**
     * Fills this entire canvas with clear transparency.
     * This means this entire canvas contains no drawable data from this point.
     * 
     * @return this canvas
     */
    public final MapCanvas clear() {
        MapBlendMode oldMode = this.blendMode;
        this.blendMode = MapBlendMode.NONE;
        this.fill(MapColorPalette.COLOR_TRANSPARENT);
        this.blendMode = oldMode;
        return this;
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
     * Sets the ambient and directional light source options:
     * <ul>
     * <li>Ambient light is how lit the model is regardless of its orientation.
     * This light level can be considered a baseline of minimal illumination.
     * It is important to have some ambient light to avoid extremely dark surfaces.</lI>
     * <li>Directional light is light that depends on the orientation of the model surface.
     * When facing into the light direction, the light level is fully applied resulting in a lit surface.
     * When facing away this light level is not applied, resulting in a dark surface.
     * To display realistic-looking models, some directional light helps to distinguish different orientations.
     * </ul>
     * 
     * @param ambientLightFactor - amount of ambient light emitted
     * @param directionalLightFactor - amount of directional light emitted
     * @param directionVector - direction of the directional light
     * @return this canvas
     */
    public final MapCanvas setLightOptions(float ambientLightFactor, float directionalLightFactor, Vector3f directionVector) {
        this.ambientLightFact = ambientLightFactor;
        this.directionalLightFact = (directionVector == null) ? 0.0f : directionalLightFactor;
        this.directionalLightVec = (directionVector == null) ? null : directionVector.normalize();
        return this;
    }

    /**
     * Sets a mask defining the pixels that are drawn. Non-transparent pixels in the mask
     * will be drawn onto this canvas for all drawing operations. Transparent pixels in the mask
     * will not be drawn at all, and also skip depth buffer tests and blend modes. It is recommended
     * that the mask is the same size as this canvas, since the mask is applied relative to (0, 0).
     * 
     * @param mask to apply, <i>null</i> to disable the mask
     * @return this canvas
     */
    public final MapCanvas setBrushMask(MapCanvas mask) {
        if (mask == null) {
            this.maskBuffer = null;
        } else {
            this.maskBuffer = mask.getBuffer();
            this.mask_w = mask.getWidth();
            this.mask_h = mask.getHeight();
            this.maskRelative = false;
        }
        return this;
    }

    /**
     * Sets a mask defining the draw-relative pixels that are drawn. Non-transparent pixels in the mask
     * will be drawn onto this canvas for all drawing operations. Transparent pixels in the mask
     * will not be drawn at all, and also skip depth buffer tests and blend modes.
     * 
     * @param mask to apply, <i>null</i> to disable the mask
     * @return this canvas
     */
    public final MapCanvas setRelativeBrushMask(MapCanvas mask) {
        if (mask == null) {
            this.maskBuffer = null;
        } else {
            this.maskBuffer = mask.getBuffer();
            this.mask_w = mask.getWidth();
            this.mask_h = mask.getHeight();
            this.maskRelative = true;
        }
        return this;
    }

    /**
     * Clears the draw depth buffer, causing any previous contents to be erased
     * when drawing in those areas.
     * 
     * @return this canvas
     */
    public final MapCanvas clearDepthBuffer() {
        if (this.depthBuffer != null) {
            Arrays.fill(this.depthBuffer, (short) MAX_DEPTH);
        }
        return this;
    }

    /**
     * Sets a depth value that will be used when performing the following drawing operations.
     * The depth buffer will allow for different drawn areas to overlap each other correctly
     * in the third dimension (z).<br>
     * <br>
     * Higher depth values correspond to contents 'deeper' into
     * this canvas. In other words, as depth increases, objects are further away.<br>
     * <br>
     * When clearing areas, be aware that the contents behind it have to be re-drawn.
     * Every call to {@link #setDrawDepth(int)} will reset {@link #hasMoreDepth()}, which
     * allows you to check after drawing is completed whether deeper areas need to be re-drawn.
     * 
     * @param depth to set to
     * @return this canvas
     */
    public final MapCanvas setDrawDepth(int depth) {
        this.currentDepthZ = (short) depth;
        this.hasDepthHoles = false;
        if (this.depthBuffer == null) {
            this.depthBuffer = new short[this.getWidth() * this.getHeight()];
            clearDepthBuffer();
        }
        return this;
    }

    /**
     * Gets whether the previous drawing operations resulted in holes in the depth buffer,
     * requiring deeper layers to be re-drawn.
     * 
     * @return True if deeper areas need to be re-drawn as a result of previous drawing operations
     */
    public final boolean hasMoreDepth() {
        if (!this.hasDepthHoles) {
            // Check if any pixels on the display have depth holes
            for (short d : this.depthBuffer) {
                if (d == MAX_DEPTH) {
                    this.hasDepthHoles = true;
                    break;
                }
            }
        }
        return this.hasDepthHoles;
    }

    /**
     * Gets the depth of a pixel drawn on this canvas with the depthbuffer enabled.
     * Returns {@link #MAX_DEPTH} when the pixel has no contents drawn there.
     *  
     * @param x - coordinate of the pixel
     * @param y - coordinate of the pixel
     * @return depth of the pixel
     */
    public final int getDepth(int x, int y) {
        if (this.depthBuffer == null) {
            return MAX_DEPTH;
        } else {
            return this.depthBuffer[y * this.getWidth() + x];
        }
    }

    /**
     * Checks whether a rectangular area on this canvas has depth holes.
     * A depth hole is where the depth is MAX_DEPTH, in other words, nothing is drawn.
     * 
     * @param x - coordinate of the rectangular area to check
     * @param y - coordinate of the rectangular area to check
     * @param width of the rectangular area to check
     * @param height of the rectangular area to check
     * @return True if the area has depth holes
     */
    public final boolean hasMoreDepth(int x, int y, int width, int height) {
        if (this.depthBuffer == null) {
            return true;
        } else if (x >= this.getWidth() || y >= this.getHeight()) {
            return false; // out of bounds
        } else {
            // Bring coordinates to within the canvas
            if (x < 0) {
                width += x;
                x = 0;
            }
            if (y < 0) {
                height += y;
                y = 0;
            }
            if (width <= 0 || height <= 0) {
                return false; // no area
            }

            // Clip width/height extending past the limit
            int limWidth = this.getWidth() - x;
            int limHeight = this.getHeight() - y;
            if (width > limWidth) {
                width = limWidth;
            }
            if (height > limHeight) {
                height = limHeight;
            }

            // Finally actually do the checks
            int depthIndexBase = (y * this.getWidth());
            for (int dy = 0; dy < height; dy++) {
                int depthIndex = depthIndexBase + x;
                for (int dx = 0; dx < width; dx++) {
                    if (this.depthBuffer[depthIndex++] == MAX_DEPTH) {
                        return true;
                    }
                }
                depthIndexBase += this.getWidth();
            }
            return false;
        }
    }

    /**
     * Sets the amount of pixels between each drawn character of a font.
     * By default a spacing of 0 is used. Text fonts will have a default spacing set.
     * Negative values are allowed to make spacing smaller.
     * 
     * @param spacing to use, 0 for none
     * @return this canvas
     */
    public final MapCanvas setSpacing(int spacing) {
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
    public final MapCanvas setAlignment(MapFont.Alignment alignment) {
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
    public final MapCanvas setBlendMode(MapBlendMode blendMode) {
        this.blendMode = blendMode;
        return this;
    }

    /**
     * Gets the color blending mode currently in use by this canvas.
     * See also: {@link #setBlendMode(MapBlendMode)}
     * 
     * @return color blending mode
     */
    public final MapBlendMode getBlendMode() {
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
     * Draws a 3D model onto this canvas, using the position, rotation and scale parameters
     * 
     * @param model to draw
     * @param scale to draw the model at
     * @param x - position of the model origin on the canvas
     * @param y - position of the model origin on the canvas
     * @param yaw rotation
     * @param pitch rotation
     * @return this canvas
     */
    public final MapCanvas drawModel(Model model, float scale, int x, int y, float yaw, float pitch) {
        Matrix4f transform = new Matrix4f();
        transform.translate(x, 0.0f, y);
        transform.scale(scale);
        transform.rotateX(pitch);
        transform.rotateY(yaw);
        return drawModel(model, transform);
    }

    /**
     * Draws a 3D model onto this canvas, using the transform matrix for the positioning
     * of the model on the canvas.
     * 
     * @param model to draw
     * @param transform for drawing the model
     * @return this canvas
     */
    public final MapCanvas drawModel(Model model, Matrix4f transform) {
        if (model == null) {
            throw new IllegalArgumentException("Model is null");
        }
        List<Quad> quads = model.getQuads();
        for (Quad quad : quads) {
            transform.transformQuad(quad);
        }

        Collections.sort(quads);
        for (Quad quad : quads) {
            drawQuad(quad);
        }

        return this;
    }

    /**
     * Draws a pseudo-3D quad onto this canvas, using the 4 2D coordinates of the quad points
     * to define the projection transformation that is applied.
     * 
     * @param quad to draw
     * @return this canvas
     */
    public final MapCanvas drawQuad(Quad quad) {
        return this.drawQuad(quad.texture, quad.p0, quad.p1, quad.p2, quad.p3);
    }

    /**
     * Draws a pseudo-3D quad onto this canvas, using the 4 3D coordinates of the quad points
     * to define the projection transformation that is applied.
     * 
     * @param canvas to draw onto this canvas
     * @param p0 the top-left first point of the quad
     * @param p1 the top-right second point of the quad
     * @param p2 the bottom-right third point of the quad
     * @param p3 the bottom-left fourth point of the quad
     * @return view
     */
    public final MapCanvas drawQuad(MapCanvas canvas, Vector3f p0, Vector3f p1, Vector3f p2, Vector3f p3) {
        Vector3f fp3 = Vector3f.add(p2, Vector3f.subtract(p0, p1));
        if (fp3.distanceSquared(p3) > 0.0001f) {
            // Split into two separate triangle draw calls
            // We can draw the entire Quad in one go
            // Triangle drawing logic? Lol, I'll just draw one half of a quad.
            // Really though, this shit needs to be optimized :(
            this.drawQuad(canvas, p0, p1, p2, fp3, 1);
            Vector3f fp1 = Vector3f.add(p2, Vector3f.subtract(p0, p3));
            this.drawQuad(canvas, p0, fp1, p2, p3, -1);
        } else {
            // We can draw the entire Quad in one go
            this.drawQuad(canvas, p0, p1, p2, p3, 0);
        }
        return this;
    }

    /**
     * Draws a pseudo-3D quad onto this canvas, using the projection matrix to define the
     * projection transformation that is applied.
     * 
     * @param canvas to draw onto this canvas
     * @param projectionMatrix to use for the transformation
     * @return view
     */
    public final MapCanvas drawQuad(MapCanvas canvas, Matrix4f projectionMatrix) {
        return drawQuad(canvas, projectionMatrix, 0);
    }

    private final MapCanvas drawQuad(MapCanvas canvas, Vector3f p0, Vector3f p1, Vector3f p2, Vector3f p3, int half) {
        // This matrix can be cached, saving a precious matrix inversion
        if (canvas.projMatrix == null) {
            Vector3f ip0 = new Vector3f(0, 0, 0);
            Vector3f ip1 = new Vector3f(0, 0, canvas.getHeight());
            Vector3f ip2 = new Vector3f(canvas.getWidth(), 0,  canvas.getHeight());
            Vector3f ip3 = new Vector3f(canvas.getWidth(), 0,  0);

            canvas.projMatrix = Matrix4f.computeProjectionMatrix(new Vector3f[] { ip0, ip1, ip2, ip3 });
            canvas.projMatrix.invert();
        }

        Matrix4f m0 = Matrix4f.computeProjectionMatrix(new Vector3f[] {  p0,  p1,  p2,  p3 });
        if (m0 == null) {
            return this;
        }

        m0.multiply(canvas.projMatrix);

        return drawQuad(canvas, m0, half);
    }

    private final MapCanvas drawQuad(MapCanvas canvas, Matrix4f projectionMatrix, int half) {
        Matrix4f mInv = new Matrix4f(projectionMatrix);
        mInv.invert();

        Vector3f ip0 = new Vector3f(0, 0, 0);
        Vector3f ip1 = new Vector3f(0, 0, canvas.getHeight());
        Vector3f ip2 = new Vector3f(canvas.getWidth(), 0,  canvas.getHeight());
        projectionMatrix.transformPoint(ip0);
        projectionMatrix.transformPoint(ip1);
        projectionMatrix.transformPoint(ip2);

        Vector3f v1 = Vector3f.subtract(ip0, ip1);
        Vector3f v2 = Vector3f.subtract(ip2, ip1);
        Vector3f cross = Vector3f.cross(v1, v2).normalize();

        if (cross.y < 0.0f) {
            cross = cross.negate();
        }

        Vector3f p = new Vector3f();
        MapTexture temp = MapTexture.createEmpty(this.getWidth(), this.getHeight());
        int minX = this.getWidth();
        int minY = this.getHeight();
        int maxX = 0;
        int maxY = 0;

        float light = this.ambientLightFact;
        if (this.directionalLightVec != null) {
            float dot = Vector3f.dot(cross.normalize(), this.directionalLightVec);

            // Change -1.0...1.0 to 0...1
            dot = (dot + 1.0f) / 2.0f;

            light += this.directionalLightFact * dot;
        }

        for (int y = 0; y < getHeight(); y++)
        {
            for (int x = 0; x < getWidth(); x++)
            {
                p.x = x;
                p.z = y;
                p.y = 1.0f;
                mInv.transformPoint(p);

                float ax = p.x;
                float ay = p.z;

                // Half parameter makes it draw only one half (triangle)
                if ((half > 0 && ax > ay) || (half < 0 && ax <= ay)) {
                    continue;
                }

                //float depth = p.y;

                if (ax >= 0.0f && ay >= 0.0f && ax <= (canvas.getWidth()) && ay <= (canvas.getHeight())) {
                    if (y < minY) minY = y;
                    if (y > maxY) maxY = y;
                    if (x < minX) minX = x;
                    if (x > maxX) maxX = x;

                    byte color = canvas.readPixel((int) ax, (int) ay);
                    if (color != MapColorPalette.COLOR_TRANSPARENT) {
                        // Shows specular brightness based on distance from camera (debug)
                        color = MapColorPalette.getSpecular(color, light);

                        temp.writePixel(x, y, color);
                    }
                }
            }
        }
        if (minX != this.getWidth()) {
            this.draw(temp.getView(minX, minY, maxX - minX + 1, maxY - minY + 1), minX, minY);
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
