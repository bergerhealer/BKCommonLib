package com.bergerkiller.bukkit.common.map.util;

import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.MapTexture;

/**
 * A tiny font with 3x5 glyphs. Only supports ASCII characters.
 * Sprites are 4x6 in size to account for a whitespace gap between
 * characters.
 */
public class MapTinyFont extends MapFont<Character> {
    private MapTexture _fontMap = null;

    @Override
    protected MapTexture loadSprite(Character key) {
        // Space character is special, can be narrower
        if (key == ' ' || key == '\t') {
            return MapTexture.createEmpty(2, 6);
        }

        int code = (int) key.charValue();
        if (code >= 0 && code < 256) {
            if (this._fontMap == null) {
                this._fontMap = CommonPlugin.getInstance().loadTexture("com/bergerkiller/bukkit/common/internal/resources/textures/tinyfont.png");
            }
            MapCanvas sprite = this._fontMap.getView((code & 0xF) * 4, (code >> 4) * 6, 4, 6);

            // Trim off vertical columns to the left that are empty
            int leftColumn = 0;
            while (leftColumn < 2) {
                boolean columnHasPixel = false;
                for (int y = 0; y < 5; y++ ){
                    if (sprite.readPixel(leftColumn, y) != MapColorPalette.COLOR_TRANSPARENT) {
                        columnHasPixel = true;
                        break;
                    }
                }
                if (columnHasPixel) {
                    break;
                } else {
                    leftColumn++;
                }
            }
            if (leftColumn > 0) {
                sprite = sprite.getView(leftColumn, 0, sprite.getWidth()-leftColumn, sprite.getHeight());
            }

            return sprite.clone();
        } else {
            return this.getSprite(null);
        }
    }

}
