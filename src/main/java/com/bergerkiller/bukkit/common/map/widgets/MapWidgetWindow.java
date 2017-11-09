package com.bergerkiller.bukkit.common.map.widgets;

import java.awt.Point;
import java.util.Arrays;

import com.bergerkiller.bukkit.common.map.MapBlendMode;
import com.bergerkiller.bukkit.common.map.MapColorPalette;

/**
 * A widget that looks like a Minecraft-styled window
 */
public class MapWidgetWindow extends MapWidget {
    private byte _outerBorderColor = MapColorPalette.COLOR_BLACK;
    private byte _innerBorderColor1 = MapColorPalette.COLOR_WHITE;
    private byte _innerBorderColor2 = MapColorPalette.getColor(86, 86, 86);
    private byte _backgroundColor = MapColorPalette.getColor(198, 198, 198);
    private MapWidgetText _titleWidget = new MapWidgetText();

    public MapWidgetWindow() {
        this._titleWidget.setBounds(6, 4, 32, 32);
        this._titleWidget.setColor(MapColorPalette.getColor(64, 64, 64));
        this.addWidget(this._titleWidget);
    }

    /**
     * Sets the color of the outer border of the window.
     * By default this is black.
     * 
     * @param color to set to
     * @return this window widget
     */
    public MapWidgetWindow setOuterBorderColor(byte color) {
        if (this._outerBorderColor != color) {
            this._outerBorderColor = color;
            this.invalidate();
        }
        return this;
    }

    /**
     * Sets the color of the background of the window. Other widgets are shown on top.
     * By default this is light-grey.
     * 
     * @param color to set to
     * @return this window widget
     */
    public MapWidgetWindow setBackgroundColor(byte color) {
        if (this._backgroundColor != color) {
            this._backgroundColor = color;
            this.invalidate();
        }
        return this;
    }

    /**
     * Sets the color of the inner border of the window.
     * This consists of two colors for top/left and bottom/right.
     * 
     * @param color1 to set to
     * @param color2 to set to
     * @return this window widget
     */
    public MapWidgetWindow setInnerBorderColors(byte color1, byte color2) {
        if (this._innerBorderColor1 != color1 || this._innerBorderColor2 != color2) {
            this._innerBorderColor1 = color1;
            this._innerBorderColor2 = color2;
            this.invalidate();
        }
        return this;
    }

    /**
     * Gets the title widget of this window, where the title of the window can be displayed
     * 
     * @return title widget
     */
    public MapWidgetText getTitle() {
        return this._titleWidget;
    }

    @Override
    public void onBoundsChanged() {
        this._titleWidget.setSize(this.getWidth() - this._titleWidget.getX() - 4, this._titleWidget.getHeight());
    }

    @Override
    public void onDraw() {
        // Filled inside
        view.fillRectangle(2, 2, this.getWidth() - 4, this.getHeight() - 4, this._backgroundColor);

        // Outer border
        view.drawContour(Arrays.asList(
        /* Top-left */      new Point(0, 3), new Point(3, 0),
        /* Top-right */     new Point(getWidth() - 4, 0), new Point(getWidth() - 1, 3),
        /* Bottom-right */  new Point(getWidth() - 1, getHeight() - 4), new Point(getWidth() - 4, getHeight() - 1),
        /* Bottom-left */   new Point(3, getHeight() - 1), new Point(0, getHeight() - 4)
        ), this._outerBorderColor);

        // Top and left inner border lines
        view.drawLine(3, 1, getWidth() - 4, 1, this._innerBorderColor1);
        view.drawLine(1, 3, 1, getHeight() - 4, this._innerBorderColor1);
        view.drawPixel(2, 2, this._innerBorderColor1);

        // Bottom and right inner border lines
        view.drawLine(3, getHeight() - 2, getWidth() - 4, getHeight() - 2, this._innerBorderColor2);
        view.drawLine(getWidth() - 2, 3, getWidth() - 2, getHeight() - 4, this._innerBorderColor2);
        view.drawPixel(getWidth() - 3, getHeight() - 3, this._innerBorderColor2);

        // Take the average of the two inner border colors and draw a pixel at the intersection
        byte innerColorAvg = MapBlendMode.AVERAGE.process(this._innerBorderColor1, this._innerBorderColor2);
        view.drawPixel(2, getHeight() - 3, innerColorAvg);
        view.drawPixel(getWidth() - 3, 2, innerColorAvg);
    }

}
