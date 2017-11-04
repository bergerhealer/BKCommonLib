package com.bergerkiller.bukkit.common.map.widgets;

import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.utils.LogicUtil;

/**
 * Displays text in a custom font and color
 */
public class MapWidgetText extends MapWidget {
    private MapFont<Character> _font = MapFont.MINECRAFT;
    private MapFont.Alignment _alignment = MapFont.Alignment.LEFT;
    private byte _color = MapColorPalette.COLOR_BLACK;
    private String _text = "";

    /**
     * Gets the text
     * 
     * @return text
     */
    public String getText() {
        return this._text;
    }

    /**
     * Sets the text
     * 
     * @param text
     * @return this text widget
     */
    public MapWidgetText setText(String text) {
        if (!LogicUtil.bothNullOrEqual(this._text, text)) {
            this._text = text;
            this.invalidate();
        }
        return this;
    }

    /**
     * Gets the color of the text
     * 
     * @return text color
     */
    public byte getColor() {
        return this._color;
    }

    /**
     * Sets the color of the text
     * 
     * @param color
     * @return this text widget
     */
    public MapWidgetText setColor(byte color) {
        if (this._color != color) {
            this._color = color;
            this.invalidate();
        }
        return this;
    }

    /**
     * Gets the font of hte text
     * 
     * @return text font
     */
    public MapFont<Character> getFont() {
        return this._font;
    }

    /**
     * Sets the font of the text
     * 
     * @param font
     * @return this text widget
     */
    public MapWidgetText setFont(MapFont<Character> font) {
        if (this._font != font) {
            this._font = font;
            this.invalidate();
        }
        return this;
    }

    /**
     * Gets the alignment of the text
     * 
     * @return text alignment
     */
    public MapFont.Alignment getAlignment() {
        return this._alignment;
    }

    /**
     * Sets the alignment of the text
     * 
     * @param alignment
     * @return this text widget
     */
    public MapWidgetText setAlignment(MapFont.Alignment alignment) {
        if (this._alignment != alignment) {
            this._alignment = alignment;
            this.invalidate();
        }
        return this;
    }

    @Override
    public void onDraw() {
        if (this._text != null && !this._text.isEmpty()) {
            MapFont.Alignment oldAlignment = this.view.getAlignment();
            this.view.setAlignment(this._alignment);
            this.view.draw(this._font, 0, 0, this._color, this._text);
            this.view.setAlignment(oldAlignment);
        }
    }

}
