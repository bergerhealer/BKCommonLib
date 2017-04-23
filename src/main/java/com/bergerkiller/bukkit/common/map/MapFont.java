package com.bergerkiller.bukkit.common.map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.logging.Level;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.utils.MathUtil;

/**
 * A generic Map Font type that binds sprite characters to value keys.
 * For text, the bound key would be a Character.
 * This also enables fonts mapped to, for example, materials or other tokens such as player names.
 * 
 * @param <K> key type
 */
public abstract class MapFont<K> {
    private final HashMap<K, MapTexture> sprites = new HashMap<K, MapTexture>();

    // Constants for some default font types
    public static final MapFont<Character> MINECRAFT = fromBukkitFont(org.bukkit.map.MinecraftFont.Font);

    /**
     * Loads the sprite texture for a given key. Called only once upon the first time
     * of use. The key is guaranteed to never be null.
     * 
     * @param key to get the sprite for
     * @return texture
     */
    protected abstract MapTexture loadSprite(K key);

    /**
     * Loads the sprite texture for null keys. Called only once upon the first time
     * of use. Default implementation returns an empty texture.
     * 
     * @return Null texture sprite.
     */
    protected MapTexture loadNullSprite() {
        return MapTexture.createEmpty();
    }

    /**
     * Retrieves the sprite texture mapped to a certain key
     * 
     * @param key texture is bound to
     * @return texture
     */
    public final MapTexture getSprite(K key) {
        MapTexture sprite = sprites.get(key);
        if (sprite == null) {
            try {
                sprite = (key != null) ? this.loadSprite(key) : this.loadNullSprite();
            } catch (Throwable t) {
                Logging.LOGGER.log(Level.SEVERE, "Failed to load font sprite for character '" + key + "'", t);
            }
            if (sprite == null) {
                sprite = MapTexture.createEmpty();
            }
            sprites.put(key, sprite);
        }
        return sprite;
    }

    /**
     * Resets the sprite cache, requiring a reload of all sprites upon first use
     */
    public void reload() {
        sprites.clear();
    }

    /**
     * Creates a new font from a bukkit map font
     * 
     * @param font to convert
     * @return font
     */
    public static MapFont<Character> fromBukkitFont(org.bukkit.map.MapFont font) {
        return new BukkitFont(font);
    }

    /**
     * Creates a new font from a system Java AWT font
     * 
     * @param font to convert
     * @return font
     */
    public static MapFont<Character> fromJavaFont(java.awt.Font font) {
        return new JavaFont(font);
    }

    /**
     * Creates a new font from a system Java AWT font from a family name, font style and size.
     * 
     * @param name of the font
     * @param style of the font
     * @param size of the font
     * @return font
     */
    public static MapFont<Character> fromJavaFont(String name, int style, int size) {
        return new JavaFont(new java.awt.Font(name, style, size));
    }

    /**
     * Creates a new font from a system Java AWT font from a family name and size.
     * Standard font style is used.
     * 
     * @param name of the font
     * @param size of the font
     * @return font
     */
    public static MapFont<Character> fromJavaFont(String name, int size) {
        return new JavaFont(new java.awt.Font(name, 0, size));
    }

    private static class BukkitFont extends MapFont<Character> {
        private final org.bukkit.map.MapFont font;

        public BukkitFont(org.bukkit.map.MapFont font) {
            this.font = font;
        }

        @Override
        protected MapTexture loadSprite(Character key) {
            return MapTexture.fromBukkitSprite(font.getChar(key));
        }

    }

    private static class JavaFont extends MapFont<Character> {
        private final java.awt.Font font;
        private final java.awt.FontMetrics metrics;

        public JavaFont(java.awt.Font font) {
            this.font = font;

            BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = img.createGraphics();
            g2d.setFont(this.font);
            this.metrics = g2d.getFontMetrics();
        }

        @Override
        protected MapTexture loadSprite(Character key) {
            java.awt.font.TextLayout layout = new java.awt.font.TextLayout(key.toString(), metrics.getFont(), metrics.getFontRenderContext());
            int width = MathUtil.ceil(layout.getAdvance());
            int ascent = MathUtil.ceil(layout.getAscent());
            int height = MathUtil.ceil(layout.getDescent()) + ascent;
            if (width <= 0 || height <= 0) {
                return MapTexture.createEmpty();
            }
            BufferedImage image = new BufferedImage(width + 1, height + 1, BufferedImage.TYPE_4BYTE_ABGR);
            Graphics g = image.getGraphics();
            g.setColor(Color.WHITE);
            g.setFont(metrics.getFont());
            g.drawString(key.toString(), 0, ascent);
            return MapTexture.fromImage(image);
        }

    }

    /**
     * The alignment of the characters when drawn on a canvas
     */
    public static enum Alignment {
        LEFT, MIDDLE, RIGHT;
    }

}
