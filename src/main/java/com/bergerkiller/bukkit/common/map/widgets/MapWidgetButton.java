package com.bergerkiller.bukkit.common.map.widgets;

import java.awt.Dimension;

import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;

/**
 * Shows a filled button with an icon and text next to it.
 * To handle the click, override {@link #onActivate()}.
 */
public class MapWidgetButton extends MapWidget {
    private String _text = "";
    private MapTexture _icon = null;

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
     * Gets the icon displayed to the right in this button
     * 
     * @return displayed icon, null if no icon is set
     */
    public MapTexture getIcon() {
        return this._icon;
    }

    @Override
    public void onDraw() {
        byte topEdgeColor, fillColor, btmEdgeColor, textColor, textShadowColor;
        if (this.isFocused()) {
            topEdgeColor = MapColorPalette.getColor(190, 200, 255);
            fillColor = MapColorPalette.getColor(126, 136, 191);
            btmEdgeColor = MapColorPalette.getColor(92, 102, 157);
            textColor = MapColorPalette.getColor(255, 255, 160);
            textShadowColor = MapColorPalette.getColor(63, 63, 40);
        } else {
            topEdgeColor = MapColorPalette.getColor(170, 170, 170);
            fillColor = MapColorPalette.getColor(111, 111, 111);
            btmEdgeColor = MapColorPalette.getColor(86, 86, 86);
            textColor = MapColorPalette.getColor(224, 224, 224);
            textShadowColor = MapColorPalette.getColor(56, 56, 56);
        }

        // This draws the button base graphic, supporting dynamic sizes
        view.drawLine(0, 0, getWidth() - 2, 0, topEdgeColor);
        view.drawLine(0, 1, 0, getHeight() - 3, topEdgeColor);
        view.drawLine(0, view.getHeight() - 2, 0, view.getHeight() - 1, fillColor);
        view.drawPixel(view.getWidth() - 1, 0, fillColor);
        view.drawLine(1, view.getHeight() - 2, view.getWidth() - 1, view.getHeight() - 2, btmEdgeColor);
        view.drawLine(1, view.getHeight() - 1, view.getWidth() - 1, view.getHeight() - 1, btmEdgeColor);
        view.drawLine(view.getWidth() - 1, 1, view.getWidth() - 1, view.getHeight() - 3, btmEdgeColor);
        view.fillRectangle(1, 1, getWidth() - 2, getHeight() - 3, fillColor);

        // Draw the text inside the button
        if (!this._text.isEmpty()) {
            Dimension textSize = view.calcFontSize(MapFont.MINECRAFT, this._text);
            int textX = (this.getWidth() - textSize.width) / 2;
            int textY = (this.getHeight() - textSize.height) / 2;
            view.setAlignment(MapFont.Alignment.LEFT);
            view.draw(MapFont.MINECRAFT, textX + 1, textY + 1, textShadowColor, this._text);
            view.draw(MapFont.MINECRAFT, textX, textY, textColor, this._text);
        }

        // If an icon is specified, draw it all the way to the right of the button
        if (this._icon != null) {
            int iconY = (this.getHeight() - this._icon.getHeight()) / 2;
            int iconX = (this.getWidth() - iconY - this._icon.getWidth());
            view.draw(this._icon, iconX, iconY);
        }
     }
}
