package com.bergerkiller.bukkit.common.map;

import java.awt.Image;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;

import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.common.utils.MathUtil;

public final class MapTexture extends MapCanvas {
    private final byte[] buffer;
    private final int width, height;

    private MapTexture(int width, int height, byte[] buffer) {
        this.width = width;
        this.height = height;
        this.buffer = buffer;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public byte[] getBuffer() {
        return this.buffer;
    }

    @Override
    public byte readPixel(int x, int y) {
        if (x >= 0 && x < width) {
            int index = (x + (y * width));
            if (index >= 0 && index < buffer.length) {
                return buffer[index];
            }
        }
        return (byte) 0;
    }

    @Override
    public void writePixel(int x, int y, byte color) {
        if (x >= 0 && x < width) {
            int index = (x + (y * width));
            if (index >= 0 && index < buffer.length) {
                buffer[index] = color;
            }
        }
    }

    @Override
    public MapCanvas writePixels(int x, int y, int w, int h, byte[] colorData) {
        if (x == 0 && y == 0 && w == width && h == height) {
            System.arraycopy(colorData, 0, this.buffer, 0, (w * h));
            return this;
        } else {
            return super.writePixels(x, y, w, h, colorData);
        }
    }

    public static MapTexture createEmpty(int width, int height) {
        return new MapTexture(width, height, new byte[width * height]);
    }

    public static MapTexture createEmpty() {
        return new MapTexture(0, 0, new byte[0]);
    }

    public static MapTexture loadPluginResource(JavaPlugin plugin, String filename) {
        return fromStream(plugin.getResource(filename));
    }

    public static MapTexture loadResource(Class<?> ownerClass, String filename) {
        return fromStream(ownerClass.getResourceAsStream(filename));
    }

    public static MapTexture loadResource(URL imageResourceURL) {
        try {
            return fromStream(imageResourceURL.openStream());
        } catch (IOException e) {
            throw new RuntimeException("Failed to open image resource stream", e);
        }
    }

    public static MapTexture fromImageFile(String filePath) {
        try {
            FileInputStream stream = new FileInputStream(filePath);
            try {
                return fromStream(stream);
            } finally {
                stream.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to open image file", e);
        }
    }

    public static MapTexture fromStream(InputStream imageStream) {
        try {
            return fromImage(ImageIO.read(imageStream));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load image from stream", e);
        }
    }

    public static MapTexture fromImage(Image image) {
        return new MapTexture(image.getWidth(null), image.getHeight(null), MapColorPalette.convertImage(image));
    }

    public static MapTexture fromRawData(int width, int height, byte[] buffer) {
        return new MapTexture(width, height, buffer);
    }

    public static MapTexture fromBukkitSprite(org.bukkit.map.MapFont.CharacterSprite sprite) {
        if (sprite == null) {
            return createEmpty();
        }
        MapTexture texture = createEmpty(sprite.getWidth() + 1, sprite.getHeight());
        for (int dx = 0; dx < sprite.getWidth(); dx++) {
            for (int dy = 0; dy < sprite.getHeight(); dy++) {
                texture.writePixel(dx, dy, sprite.get(dy, dx) ? 
                        MapColorPalette.COLOR_WHITE : MapColorPalette.COLOR_TRANSPARENT);
            }
        }
        return texture;
    }

    /**
     * Creates a new map texture with the contents of a canvas flipped horizontally
     * 
     * @param input canvas
     * @return flipped texture
     */
    public static MapTexture flipH(MapCanvas input) {
        MapTexture result = MapTexture.createEmpty(input.getWidth(), input.getHeight());
        for (int x = 0; x < result.getWidth(); x++) {
            for (int y = 0; y < result.getHeight(); y++) {
                result.writePixel(x, y, input.readPixel(result.getWidth() - x - 1, y));
            }
        }
        return result;
    }

    /**
     * Creates a new map texture with the contents of a canvas flipped vertically
     * 
     * @param input canvas
     * @return flipped texture
     */
    public static MapTexture flipV(MapCanvas input) {
        MapTexture result = MapTexture.createEmpty(input.getWidth(), input.getHeight());
        for (int x = 0; x < result.getWidth(); x++) {
            for (int y = 0; y < result.getHeight(); y++) {
                result.writePixel(x, y, input.readPixel(x, result.getHeight() - y - 1));
            }
        }
        return result;
    }

    /**
     * Creates a new map texture with the contents rotated 0, 90, 180, or 270 degrees.
     * 
     * @param input canvas
     * @param angle to rotate, must be a multiple of 90
     * @return rotated texture
     */
    public static MapTexture rotate(MapCanvas input, int angle) {
        MapTexture result;
        if (MathUtil.getAngleDifference(angle, 90) <= 45) {
            result = MapTexture.createEmpty(input.getHeight(), input.getWidth());
            for (int x = 0; x < result.getWidth(); x++) {
                for (int y = 0; y < result.getHeight(); y++) {
                    result.writePixel(x, result.getHeight() - y - 1, input.readPixel(y, x));
                }
            }
        } else if (MathUtil.getAngleDifference(angle, 180) <= 45) {
            result = MapTexture.createEmpty(input.getWidth(), input.getHeight());
            for (int x = 0; x < result.getWidth(); x++) {
                for (int y = 0; y < result.getHeight(); y++) {
                    result.writePixel(x, y, input.readPixel(result.getWidth() - x - 1, result.getHeight() - y - 1));
                }
            }
        } else if (MathUtil.getAngleDifference(angle, 270) <= 45) {
            result = MapTexture.createEmpty(input.getHeight(), input.getWidth());
            for (int x = 0; x < result.getWidth(); x++) {
                for (int y = 0; y < result.getHeight(); y++) {
                    result.writePixel(result.getWidth() - x - 1, y, input.readPixel(y, x));
                }
            }
        } else {
            result = input.clone(); // no rotation
        }
        return result;
    }
}
