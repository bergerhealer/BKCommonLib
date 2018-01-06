package com.bergerkiller.bukkit.common.map.util;

import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.MapTexture;

/**
 * A tiny font with 3x5 glyphs. Only supports ASCII characters.
 */
public class MapTinyFont extends MapFont<Character> {
    private MapTexture _fontMap = null;

    @Override
    protected MapTexture loadSprite(Character key) {
        int code = (int) key.charValue();
        if (code >= 0 && code < 256) {
            if (this._fontMap == null) {
                this._fontMap = CommonPlugin.getInstance().loadTexture("com/bergerkiller/bukkit/common/internal/resources/textures/tinyfont.png");
            }
            return this._fontMap.getView((code & 0xF) * 4, (code >> 4) * 6, 4, 6).clone();
        } else {
            return this.getSprite(null);
        }
    }

}
