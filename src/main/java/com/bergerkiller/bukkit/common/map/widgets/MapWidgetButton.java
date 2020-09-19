package com.bergerkiller.bukkit.common.map.widgets;

import java.awt.Dimension;

import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.MapTexture;

/**
 * Shows a filled button with an icon and text next to it.
 * To handle the click, override {@link #onActivate()}.
 */
public class MapWidgetButton extends MapWidget {
    private String _text = "";
    private MapTexture _icon = null;
    private boolean showBorder = true;

    public MapWidgetButton() {
        this.setFocusable(true);
    }

    /**
     * Sets the text displayed in this button
     * 
     * @param text to display, null or empty to show no text
     * @return this menu button widget
     */
    public MapWidgetButton setText(String text) {
        if (!this._text.equals(text)) {
            this._text = text;
            if (this._text == null) {
                this._text = "";
            }
            this.invalidate();
        }
        return this;
    }

    /**
     * Gets the text currently displayed in this button
     * 
     * @return displayed text
     */
    public String getText() {
        return this._text;
    }

    /**
     * Sets the icon displayed to the right in this button
     * 
     * @param icon to set to, null to show no icon
     * @return this menu button widget
     */
    public MapWidgetButton setIcon(MapTexture icon) {
        if (this._icon != icon) {
            this._icon = icon;
            this.invalidate();
        }
        return this;
    }

    /**
     * Sets whether a black border is displayed on the outside of the button
     * 
     * @param showBorder
     * @return this menu button widget
     */
    public MapWidgetButton setShowBorder(boolean showBorder) {
        if (this.showBorder != showBorder) {
            this.showBorder = showBorder;
            this.invalidate();
        }
        return this;
    }

    /**
     * Gets whether a black border is displayed on the outside of the button
     * 
     * @return show border state
     */
    public boolean isShowBorder() {
        return this.showBorder;
    }

    /**
     * Gets the icon displayed to the right in this button
     * 
     * @return displayed icon, null if no icon is set
     */
    public MapTexture getIcon() {
        return this._icon;
    }

    @Override
    public void onDraw() {
        byte textColor, textShadowColor;
        if (!this.isEnabled()) {
            textColor = MapColorPalette.getColor(160, 160, 160);
            textShadowColor = MapColorPalette.COLOR_TRANSPARENT;
        } else if (this.isFocused()) {
            textColor = MapColorPalette.getColor(255, 255, 160);
            textShadowColor = MapColorPalette.getColor(63, 63, 40);
        } else {
            textColor = MapColorPalette.getColor(224, 224, 224);
            textShadowColor = MapColorPalette.getColor(56, 56, 56);
        }

        if (this.showBorder) {
            fillBackground(this.view.getView(1, 1, getWidth() - 2, getHeight() - 2), this.isEnabled(), this.isFocused());
            view.drawRectangle(0, 0, getWidth(), getHeight(), MapColorPalette.COLOR_BLACK);
        } else {
            fillBackground(this.view, this.isEnabled(), this.isFocused());
        }

        // Draw the text inside the button
        if (!this._text.isEmpty()) {
            Dimension textSize = view.calcFontSize(MapFont.MINECRAFT, this._text);
            int textX = (this.getWidth() - textSize.width) / 2;
            int textY = (this.getHeight() - textSize.height) / 2;
            view.setAlignment(MapFont.Alignment.LEFT);
            if (textShadowColor != MapColorPalette.COLOR_TRANSPARENT) {
                view.draw(MapFont.MINECRAFT, textX + 1, textY + 1, textShadowColor, this._text);
            }
            view.draw(MapFont.MINECRAFT, textX, textY, textColor, this._text);
        }

        // If an icon is specified, draw it all the way to the right of the button
        if (this._icon != null) {
            int iconY = (this.getHeight() - this._icon.getHeight()) / 2;
            int iconX = (this.getWidth() - iconY - this._icon.getWidth());
            view.draw(this._icon, iconX, iconY);
        }
    }

    /**
     * Draws the background texture for a button onto a canvas, filling it entirely.
     * For filling only a portion, use a view.
     * 
     * @param canvas to draw on
     * @param enabled whether button is enabled
     * @param focused whether button is focused
     */
    public static void fillBackground(MapCanvas canvas, boolean enabled, boolean focused) {
        byte topEdgeColor, fillColor, btmEdgeColor;
        if (!enabled) {
            topEdgeColor = fillColor = btmEdgeColor = MapColorPalette.getColor(44, 44, 44);
        } else if (focused) {
            topEdgeColor = MapColorPalette.getColor(190, 200, 255);
            fillColor = MapColorPalette.getColor(126, 136, 191);
            btmEdgeColor = MapColorPalette.getColor(92, 102, 157);
        } else {
            topEdgeColor = MapColorPalette.getColor(170, 170, 170);
            fillColor = MapColorPalette.getColor(111, 111, 111);
            btmEdgeColor = MapColorPalette.getColor(86, 86, 86);
        }

        int x1 = 0, y1 = 0;
        int x2 = canvas.getWidth() - 1, y2 = canvas.getHeight() - 1;

        canvas.drawLine(x1, y1, x2 - 1, y1, topEdgeColor);
        canvas.drawLine(x1, y1 + 1, x1, y2 - 2, topEdgeColor);
        canvas.drawLine(x1, y2 - 1, x1, y2, fillColor);
        canvas.drawPixel(x2, y1, fillColor);
        canvas.drawLine(x1 + 1, y2 - 1, x2, y2 - 1, btmEdgeColor);
        canvas.drawLine(x1 + 1, y2, x2, y2, btmEdgeColor);
        canvas.drawLine(x2, 1, x2, y2 - 2, btmEdgeColor);
        canvas.fillRectangle(x1 + 1, y1 + 1, x2 - x1 - 1, y2 - x1 - 2, fillColor);
    }
}
