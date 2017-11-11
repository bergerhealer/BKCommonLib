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
    private byte _shadowColor = MapColorPalette.COLOR_TRANSPARENT;
    private String _text = "";
    private boolean _autoSize = true;

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
            this.calcAutoSize();
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
     * Sets whether the size of this text widget is automatically calculated.
     * By default this is true.
     * 
     * @param autoSize
     * @return this text widget
     */
    public MapWidgetText setAutoSize(boolean autoSize) {
        if (this._autoSize != autoSize) {
            this._autoSize = autoSize;
            this.calcAutoSize();
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
            this.calcAutoSize();
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

    /**
     * Gets the shadow color of the text. When set non-transparent, a duplicate
     * of the text is drawn at an offset underneath the text with this color
     * 
     * @return shadow color
     */
    public byte getShadowColor() {
        return this._shadowColor;
    }

    /**
     * Sets the shadow color of the text. When set non-transparent, a duplicate
     * of the text is drawn at an offset underneath the text with this color
     * 
     * @param color to set to
     * @return this text widget
     */
    public MapWidgetText setShadowColor(byte color) {
        if (this._shadowColor != color) {
            this._shadowColor = color;
            this.invalidate();
        }
        return this;
    }

    @Override
    public void onAttached() {
        if (this._autoSize) {
            this.calcAutoSize();
        }
    }

    @Override
    public void onDraw() {
        if (this._text != null && !this._text.isEmpty()) {
            MapFont.Alignment oldAlignment = this.view.getAlignment();
            this.view.setAlignment(this._alignment);
            
            int x = 0;
            if (this._alignment == MapFont.Alignment.RIGHT) {
                x = this.getWidth() - 1;
            } else if (this._alignment == MapFont.Alignment.MIDDLE) {
                x = this.getWidth() / 2;
            }

            if (this._shadowColor != MapColorPalette.COLOR_TRANSPARENT) {
                this.view.draw(this._font, x + 1, 1, this._shadowColor, this._text);
            }
            this.view.draw(this._font, x, 0, this._color, this._text);
            this.view.setAlignment(oldAlignment);
        }
    }

    private void calcAutoSize() {
        if (this._autoSize && this.view != null) {
            if (this._text == null || this._text.isEmpty()) {
                this.setSize(0, 0);
            } else {
                this.setSize(this.view.calcFontSize(this._font, this._text));
            }
        }
    }
}
