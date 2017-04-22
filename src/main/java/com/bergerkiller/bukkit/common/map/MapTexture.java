package com.bergerkiller.bukkit.common.map;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;

import org.bukkit.plugin.java.JavaPlugin;

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

    public static MapTexture loadResource(URL imageResourceURL) {
        try {
            return fromStream(imageResourceURL.openStream());
        } catch (IOException e) {
            throw new RuntimeException("Failed to open image resource stream", e);
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

}
