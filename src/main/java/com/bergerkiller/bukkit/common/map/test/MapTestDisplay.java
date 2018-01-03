package com.bergerkiller.bukkit.common.map.test;

import java.awt.Color;

import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.map.MapFont;

/**
 * A map display that renders colored tiles with text in them to denote
 * the coordinates. Can be used to debug map display tiling performance.
 */
public class MapTestDisplay extends MapDisplay {

    @Override
    public void onAttached() {
        getLayer().setAlignment(MapFont.Alignment.MIDDLE);
        int nxTiles = this.getWidth() / 128;
        int nyTiles = this.getHeight() / 128;
        for (int tx = 0; tx < nxTiles; tx++) {
            for (int ty = 0; ty < nyTiles; ty++) {
                int px = tx * 128;
                int py = ty * 128;

                float hue = (float) (ty * nxTiles + tx) / (float) (nxTiles * nyTiles);
                float sat = 1.0f;
                float bri = 1.0f;
                byte color = MapColorPalette.getColor(Color.getHSBColor(hue, sat, bri));
                byte textColor = MapColorPalette.getSpecular(color, 0.5f);
                String label = "(" + tx + ", " + ty + ")";
                getLayer().fillRectangle(px, py, 128, 128, color);
                getLayer().drawRectangle(px, py, 128, 128, textColor);
                getLayer().draw(MapFont.MINECRAFT, px + 64, py + 64, textColor, label);
            }
        }
    }
}
