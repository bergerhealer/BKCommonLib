# Map Markers

`MapDisplay` can show vanilla Minecraft map markers (map decorations) at positions on its virtual map. Markers are independent of the canvas pixels: they can show the familiar pointer, cross, or banner-style icons, optionally with a caption.

## Creating and updating markers

Create a marker from the display, choose its icon type, set its position, and optionally set a caption:

```java
public class LocationDisplay extends MapDisplay {
    private MapMarker target;

    @Override
    public void onAttached() {
        target = createMarker("target")
                .setType(MapMarker.Type.RED_MARKER)
                .setPosition(52.0, 68.0)
                .setCaption("Target");
    }

    public void moveTarget(double x, double y) {
        target.setPosition(x, y);
    }
}
```

Coordinates are pixel positions in the full display, not world coordinates or tile-local coordinates. `MapMarker.setPosition()` updates the marker as it moves. Set rotation in degrees with `setRotation()`, hide/show it with `setVisible()`, and remove it with `remove()` or `MapDisplay.removeMarker(id)`. `createMarker()` generates an ID; `createMarker(id)` assigns a known ID that can be used later with `getMarker(id)`. Use `clearMarkers()` to remove all markers from a display.

`MapMarker.Type` contains vanilla marker types such as `WHITE_POINTER`, `GREEN_POINTER`, `RED_MARKER`, `WHITE_CROSS`, and colored banner markers. The available types depend on the Minecraft version; `Type.values()` lists those available on the current server, and `isAvailable()` can be checked for a specific type. Some types may not be displayed on item-frame maps; check `isVisibleOnItemFrames()` if the display will be shown in frames.

## Markers on tiled displays

A display that spans multiple item frames is divided into 128-by-128 map tiles. Marker coordinates still use the full display coordinate space. BKCommonLib assigns each marker to the tile containing its position and sends the marker with that tile's map update. When a marker moves across a tile boundary, it is automatically removed from the old tile and appears on the new tile; plugins do not need to track or update the individual item-frame maps.

For example, on a two-tile-wide display, positions `x = 0..127` belong to the left tile and `x = 128..255` belong to the right tile. The same applies vertically for multi-row displays.
